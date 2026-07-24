package com.spt.bas.purchase.wx.server.service.impl;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.WxMaUserService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaPhoneNumberInfo;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.BsCompany;
import com.spt.bas.purchase.wx.client.entity.CompanyUser;
import com.spt.bas.purchase.wx.client.entity.UserDetail;
import com.spt.bas.purchase.wx.client.vo.CompanyOnLineApplyVo;
import com.spt.bas.purchase.wx.server.common.BaseException;
import com.spt.bas.purchase.wx.server.common.Constant;
import com.spt.bas.purchase.wx.server.common.InfoStep;
import com.spt.bas.purchase.wx.server.common.Status;
import com.spt.bas.purchase.wx.server.config.WxConfiguration;
import com.spt.bas.purchase.wx.server.dao.*;
import com.spt.bas.purchase.wx.server.entity.WxSession;
import com.spt.bas.purchase.wx.server.entity.WxSmsCheckCode;
import com.spt.bas.purchase.wx.server.exception.UserNameOrPasswordException;
import com.spt.bas.purchase.wx.server.payload.LoginRequest;
import com.spt.bas.purchase.wx.server.payload.WxLoginRequest;
import com.spt.bas.purchase.wx.server.service.IUserInfoService;
import com.spt.bas.purchase.wx.server.service.IUserService;
import com.spt.bas.purchase.wx.server.util.JwtUtil;
import com.spt.bas.purchase.wx.server.util.SMSUtils;
import com.spt.bas.purchase.wx.server.vo.UserChangeVo;
import com.spt.bas.purchase.wx.server.vo.UserInfoVo;
import com.spt.tools.core.bean.RespVo;
import com.spt.tools.core.encrypt.Digests;
import com.spt.tools.core.encrypt.Encodes;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import me.chanjar.weixin.common.error.WxErrorException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>
 *     微信登录注册
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-09-15 11:42
 */
@Component
@Transactional(readOnly = true)
public class UserService extends BaseService<CompanyUser> implements IUserService {
    private static Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private CompanyUserDao userDao;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private WxSmsCheckCodeDao wxSmsCheckCodeDao;

    @Autowired
    private BsCompanyDao bsCompanyDao;

    @Autowired
    private UserDetailDao userDetailDao;
    @Autowired
    private WxSessionDao wxSessionDao;

    @Autowired
    private IUserInfoService userInfoService;

    @Override
    public BaseDao<CompanyUser> getBaseDao() {
        return userDao;
    }

    /**
     * 密码参数
     */
    private static final int SALT_SIZE = 8;
    public static final int HASH_INTERATIONS = 1024;


    /**
     * 注册
     * @param vo
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CompanyUser register(LoginRequest vo) {
        CompanyUser user = userDao.findByLoginPhone(vo.getLoginPhone(),true);
        if (user != null) {
            throw new BaseException(Status.REGISTER_EXIST);
        }
        CompanyUser companyUser = new CompanyUser();
        companyUser.setEnableFlg(true);
        companyUser.setLoginPhone(vo.getLoginPhone());
        byte[] salt = Digests.generateSalt(SALT_SIZE);
        String strSalt = Encodes.encodeHex(salt);
        String password = getEncodePwd(vo.getPassword(), salt);
        companyUser.setPassword(password);
        companyUser.setSalt(strSalt);
        if (StringUtils.isBlank(vo.getOpenId())) {
            companyUser.setOpenId(getOpenId(vo.getCode()));
        } else {
            companyUser.setOpenId(vo.getOpenId());
        }
        user = userDao.save(companyUser);
        // 同时创建详细表user_details表
        UserDetail userDetail = new UserDetail();
        userDetail.setUserId(user.getId());
        userDetail.setInfoStep(InfoStep.QUOTA_TEST.getInfoStep());
        logger.error("register注册时，邀请码{}", vo.getInviteCode());
        userDetail.setInviteCode(vo.getInviteCode());
        // 关联企业 注册的时候只需要通过手机号判断
        BsCompany company = bsCompanyDao.findByContactPhoneAndEnableFlgTrue(vo.getLoginPhone());
        if (checkCompanyIsBind(company)) {
            logger.info("企业已存在,注册时绑定");
            if ("W".equals(company.getCreditRating())) {
                // 企业已准入
                userDetail.setApplyIouStatus(BasConstants.APPLY_STATUS_COMPLETE);
                userDetail.setQuotaTestStatus(BasConstants.APPLY_STATUS_COMPLETE);
            }
            userDetail.setCompanyId(company.getId());
            userDetail.setIsBind(true);
            userDetail.setTotalCreditAmount(company.getTotalCreditAmount());
            userDetail.setUsedCreditAmount(company.getUsedCreditAmount().add(company.getApproveCreditAmount()));
            userDetail.setAvailableCreditAmount(company.getTotalCreditAmount().subtract(userDetail.getUsedCreditAmount()));
        }
        UserDetail save = userDetailDao.save(userDetail);
        user.setUserDetail(save);
        return user;
    }

    /**
     * 获取openId
     *
     * @param code 临时登录凭证
     * @return openId
     */
    private String getOpenId(String code) {
        final WxMaService wxService = WxConfiguration.getMaService();
        final WxMaUserService wxMaUserService = wxService.getUserService();
        WxMaJscode2SessionResult wxSession = null;
        try {
            wxSession = wxMaUserService.getSessionInfo(code);
        } catch (WxErrorException e) {
            logger.error("获取微信回话失败，{}", e.toString());
            throw new RuntimeException(e);
        }
        return wxSession.getOpenid();
    }

    private boolean checkCompanyIsBind(BsCompany company) {
        if (company == null) {
            return false;
        }
        UserDetail userDetail = userDetailDao.findByCompanyIdAndIsBindTrueAndEnableFlgTrue(company.getId());
        if (userDetail != null) {
            return false;
        }
        return true;
    }

    /**
     * 注册
     * @param vo
     * @return
     */
    @Override
    @Transactional(readOnly = false)
    public UserInfoVo register2(LoginRequest vo) {
        CompanyUser register = register(vo);
        UserInfoVo build = UserInfoVo.builder()
                .phone(register.getLoginPhone())
                .infoStep(InfoStep.QUOTA_TEST.getInfoStep())
                .build();
        return build;
    }

    /**
     * 登录
     * @param vo
     * @return
     * @throws ApplicationException
     */
    @Override
    @Transactional(readOnly = false)
    public UserInfoVo login(LoginRequest vo) {
        logger.info("用户登录:{}", JsonUtil.obj2Json(vo));
        CompanyUser user;
        // 登录方式不能为空
        if (StrUtil.isBlank(vo.getLoginType())) {
            throw new BaseException(Status.LOGIN_NO_LOGINTYPE);
        }
        if (StrUtil.isBlank(vo.getCode())) {
            throw new BaseException(Status.LOGIN_NO_LOGINTYPE);
        }
        // 除微信授权登录以外 其他都需要验证手机号
        if (!StrUtil.equals(vo.getLoginType(), Constant.LOGIN_TYPE_WX)
                && StrUtil.isBlank(vo.getLoginPhone())) {
            throw new BaseException(Status.LOGIN_NO_PHONE);
        }
        switch (vo.getLoginType()) {
            case Constant.LOGIN_TYPE_PHONE:
                user =  loginByPhone(vo);
                break;
            case Constant.LOGIN_TYPE_CODE:
                user = loginByCode(vo);
                break;
//            case Constant.LOGIN_TYPE_WX:
//                user = loginByWx(vo);
//                break;
//            case Constant.LOGIN_TYPE_INVITE_CODE:
//                user = loginByInviteCode(vo);
//                break;
            default:
                logger.error("登录时loginType不能为空或其他类型，loginRequest:{}", JSONUtil.toJsonStr(vo));
                throw new BaseException(Status.ERROR,"登录时loginType为空或其他类型");
        }
        user.setUserDetail(userDetailDao.findByUserIdAndEnableFlg(user.getId(), true));
        // 设置jwt并返回
        return setJwt(user);
    }

    @Override
    @Transactional
    public void resetPassword(LoginRequest vo) {
        CompanyUser user = userDao.findByLoginPhone(vo.getLoginPhone(),true);
//        if (user != null) {
//            throw new BaseException(Status.REGISTER_EXIST);
//        }
        byte[] salt = Digests.generateSalt(SALT_SIZE);
        String strSalt = Encodes.encodeHex(salt);
        String password = getEncodePwd(vo.getPassword(), salt);
        user.setPassword(password);
        user.setSalt(strSalt);
        userDao.save(user);
    }

    @Override
    @Transactional
    public UserInfoVo wxLogin(WxLoginRequest loginRequest,  WxMaUserService userService1) throws WxErrorException, ApplicationException {
        logger.info("loginRequest", JSONUtil.toJsonStr(loginRequest));
        WxMaJscode2SessionResult session = userService1.getSessionInfo(loginRequest.getCode());
        logger.info("sessionKey:{}", session.getSessionKey());
        logger.info("openId:{}", session.getOpenid());

        // 保存下sessionKey和OpenId
        WxSession byOpenId1 = wxSessionDao.findByOpenId(session.getOpenid());
        if (byOpenId1 == null) {
            byOpenId1 = new WxSession();
        }
        byOpenId1.setOpenId(session.getOpenid());
        byOpenId1.setSessionKey(session.getSessionKey());
        wxSessionDao.save(byOpenId1);

        CompanyUser byOpenId = userDao.findByOpenIdAndEnableFlgTrue(session.getOpenid());
        if (byOpenId != null) {
            UserDetail userDetail = userDetailDao.findByUserIdAndEnableFlgTrue(byOpenId.getId());
            String jwt = jwtUtil.refreshJWT(byOpenId.getId(), byOpenId.getLoginPhone());
            UserInfoVo build = UserInfoVo.builder()
                    .phone(byOpenId.getLoginPhone())
                    .infoStep(userDetail.getInfoStep())
                    .accessToken(jwt)
                    .userId(byOpenId.getId())
                    .openId(session.getOpenid())
                    .build();
            return build;
        }
        WxMaPhoneNumberInfo phoneNoInfo = userService1.getPhoneNoInfo(session.getSessionKey(), loginRequest.getEncryptedData(), loginRequest.getIv());

        // 注册
        // 注册一个账号
        LoginRequest userLoginVo = new LoginRequest();
        userLoginVo.setOpenId(session.getOpenid());
        userLoginVo.setLoginPhone(phoneNoInfo.getPurePhoneNumber());
        userLoginVo.setInviteCode(loginRequest.getInviteCode()+"");
        Long userId;
        UserDetail userDetail;
        CompanyUser byLoginPhone = userDao.findByLoginPhone(phoneNoInfo.getPurePhoneNumber(), true);
        if (byLoginPhone == null) {
            // 注册
            CompanyUser user = wxRegisterInitPassword(userLoginVo.getLoginPhone(), userLoginVo.getInviteCode(), userLoginVo.getOpenId());
            userId = user.getId();
        }else {
            userId = byLoginPhone.getId();
            // 更新openId
            CompanyUser byUserid = userDao.findByUserid(userId, true);
            byUserid.setOpenId(session.getOpenid());
            userDao.save(byUserid);
        }
        userDetail = userDetailDao.findByUserIdAndEnableFlgTrue(userId);
        String jwt = jwtUtil.refreshJWT(userId, userLoginVo.getLoginPhone());
        UserInfoVo build = UserInfoVo.builder()
                .phone(phoneNoInfo.getPurePhoneNumber())
                .infoStep(userDetail.getInfoStep())
                .accessToken(jwt)
                .userId(userId)
                .openId(session.getOpenid())
                .build();
        return build;
    }

    /**
     * 缓存刷新
     * @param request
     * @return
     */
    @Override
    @Transactional
    public UserInfoVo refreshToken(HttpServletRequest request){
        String userid = jwtUtil.getUseridFromRequest(request);
        CompanyUser user = userDao.findByUserid(Long.valueOf(userid), true);
        UserDetail userDetail = userDetailDao.findByUserIdAndEnableFlg(Long.valueOf(userid), true);
        if (user == null) {
            throw new BaseException(Status.ACCOUNT_NOT_EXIST);
        }
        // 刷新token
        String jwt = jwtUtil.refreshJWT(user.getId(), user.getLoginPhone());
        UserInfoVo build = UserInfoVo.builder()
                .phone(user.getLoginPhone())
                .infoStep(userDetail.getInfoStep())
                .accessToken(jwt)
                .userId(user.getId())
                .openId(user.getOpenId())
                .build();
        return build;
    }

    @Override
    public void wxGetPhone(String phone, WxSession entity) {
        CompanyUser byLoginPhone = userDao.findByLoginPhone(phone, true);
        if (byLoginPhone != null) {
            byLoginPhone.setOpenId(entity.getOpenId());
            userDao.save(byLoginPhone);
            return;
        }
        // 注册一个账号
        LoginRequest userLoginVo = new LoginRequest();
        userLoginVo.setLoginPhone(phone);
        UserInfoVo userInfoVo = register2(userLoginVo);
        UserInfoVo build = UserInfoVo.builder()
                .phone(userLoginVo.getLoginPhone())
                .infoStep(InfoStep.QUOTA_TEST.getInfoStep())
                .build();
//        return build;

    }

    //========================私有方法==================================================

    /**
     * 设置jwt并返回
     * @param user
     * @return UserInfoVo
     */
    private UserInfoVo setJwt(CompanyUser user){
        UserInfoVo build = UserInfoVo.builder().phone(user.getLoginPhone())
                .userId(user.getId())
                .infoStep(user.getUserDetail().getInfoStep())
                .openId(user.getOpenId())
                .build();
        String jwt = jwtUtil.createJWT(build);
        build.setAccessToken(jwt);
        return build;
    }

    /**
     * 通过手机登录
     * @param vo
     * @return
     * @throws ApplicationException
     */
    private CompanyUser loginByPhone(LoginRequest vo) {
        CompanyUser user;
        user = userDao.findByLoginPhone(vo.getLoginPhone(), true);
        // 手机号密码登录 密码不能为空
        if (StrUtil.isBlank(vo.getPassword())) {
            throw new UserNameOrPasswordException(Status.USERNAME_PASSWORD_ERROR);
        }
        // 第一次登录直接注册
        if (user == null || !user.getEnableFlg()) {
            user = register(vo);
        }else {
            if (StringUtils.isBlank(user.getOpenId())) {
                user.setOpenId(getOpenId(vo.getCode()));
                userDao.save(user);
            }
            UserChangeVo newVo = new UserChangeVo();
            newVo.setPassword(vo.getPassword());
            newVo.setUserId(user.getId());
            // 判断密码是否正确
            if (!isPwdEqual(newVo)) {
                throw new UserNameOrPasswordException(Status.USERNAME_PASSWORD_ERROR);
            }
        }
        return user;
    }

    /**
     * 通过验证码登录
     *  1.判断手机号是否合规
     *  2.判断验证码是否正确
     *  3.判断手机是否注册
     *  4.判断是否设置密码，如果有完成登录流程 如果没有 给一个初始密码
     * @param vo
     * @return CompanyUser 用户信息
     * @throws ApplicationException
     */
    private CompanyUser loginByCode(LoginRequest vo) {
        CompanyUser user;
        // 手机号验证码登录 验证码不能为空
        if (StrUtil.isBlank(vo.getCheckCode())) {
            throw new BaseException(Status.CHECKCODE_ERROR);
        }
        // 校验验证码
        if (!checkCode(vo.getLoginPhone(), vo.getCheckCode())) {
            throw new BaseException(Status.CHECKCODE_ERROR);
        }
        // 判断是否注册
        if (checkRegister(vo.getLoginPhone())) {
            user = userDao.findByLoginPhone(vo.getLoginPhone(), Constant.ENABLE_FLG);
            if (StringUtils.isBlank(user.getOpenId())) {
                user.setOpenId(getOpenId(vo.getCode()));
                userDao.save(user);
            }
            // 判断是否设置了密码
            if (!checkPassword(vo.getLoginPhone())) {
                // 设置初始密码
                initPassword(vo.getLoginPhone());
            }
        }else {
            //  注册并给默认初始密码
            user = registerInitPassword(vo.getLoginPhone(), vo.getInviteCode());
        }
        return user;
    }



    /**
     * 通过微信授权登录
     * @param vo
     * @return
     * @throws ApplicationException
     */
//    private CompanyUser loginByWx(LoginRequest vo) {
//        CompanyUser user = null;
//        // todo 微信授权登录
//        // 获取微信登录必要参数
//        String appid = vo.getAppid();
//        String code = vo.getCode();
//        if (StrUtil.isBlank(appid)) {
//            throw new BaseException(Status.WX_LOGIN_NO_APPID);
//        }
//        if (StrUtil.isBlank(code)) {
//            throw new BaseException(Status.WX_LOGIN_NO_CODE);
//        }
//        WxMaService wxService = WxConfiguration.getMaService(appid);
//        try {
//            WxMaJscode2SessionResult session = wxService.getUserService().getSessionInfo(code);
//            logger.info("微信授权登录sessionKey:{}",session.getSessionKey());
//            logger.info("微信授权登录openId:{}",session.getOpenid());
//            //TODO 可以增加自己的逻辑，关联业务相关数据
//            //
//            return user;
//        } catch (WxErrorException e) {
//            logger.error(e.getMessage(), e);
//            throw new BaseException(Status.ERROR);
//        }
//
//    }

    /**
     * 通过邀请码登录
     * @param vo
     * @return
     */
    private CompanyUser loginByInviteCode(LoginRequest vo) {
        CompanyUser user;
        // todo 校验邀请码

        // 判断是否注册
        if (checkRegister(vo.getLoginPhone())) {
            user = userDao.findByLoginPhone(vo.getLoginPhone(), Constant.ENABLE_FLG);
            // 判断是否设置了密码
            if (!checkPassword(vo.getLoginPhone())) {
                // 设置初始密码
                initPassword(vo.getLoginPhone());
            }
        }else {
            //  注册并给默认初始密码
            user = registerInitPassword(vo.getLoginPhone());
        }
        return user;
    }

    /**
     * 验证验证码
     * @param loginPhone
     * @return boolean true:验证通过 false:验证不通过
     */
    private boolean checkCode(String loginPhone,String checkCode) {
        if (StrUtil.isBlank(loginPhone)) {
            return false;
        }
        // 校验手机格式是否合规
        if (!SMSUtils.isMobile(loginPhone)) {
            throw new BaseException(Status.PHONE_WRONGFUL);
        }
        // 所有验证码
        List<WxSmsCheckCode> codes = wxSmsCheckCodeDao.findValidCodes(loginPhone);
        // 有效验证码
        List<String> validCodes = codes.stream()
                .filter((t) -> DateUtil.offsetMinute(t.getCreatedDate(), 10).isAfter(new Date()))
                .map(WxSmsCheckCode::getCheckCode)
                .collect(Collectors.toList());
        if (validCodes.contains(checkCode)) {
            return true;
        }
        return false;
    }

    /**
     * 判断一个手机号是否注册
     * @param phone
     * @return boolean true:已注册 false：未注册
     */
    private boolean checkRegister(String phone) {
        if (StrUtil.isBlank(phone)) {
            return false;
        }
        CompanyUser byLoginPhone = userDao.findByLoginPhone(phone, Constant.ENABLE_FLG);
        if (byLoginPhone == null) {
            return false;
        }
        return true;
    }

    /**
     * 判断一个手机号是否设置了密码
     * @param phone
     * @return boolean true:已设置 false：未设置
     */
    private boolean checkPassword(String phone) {
        if (StrUtil.isBlank(phone)) {
            return false;
        }
        CompanyUser byLoginPhone = userDao.findByLoginPhone(phone, Constant.ENABLE_FLG);
        if (byLoginPhone == null) {
            return false;
        }
        if (StrUtil.isBlank(byLoginPhone.getPassword())) {
            return false;
        }
        return true;
    }



    /**
     * 加密
     * @param plainPwd
     * @param salt
     * @return
     */
    private String getEncodePwd(String plainPwd, byte[] salt) {
        byte[] hashPassword = Digests.sha1(plainPwd.getBytes(), salt, HASH_INTERATIONS);
        return Encodes.encodeHex(hashPassword);
    }

    /** 判断2个密码是否一致 */
    @Override
    public boolean isPwdEqual(UserChangeVo vo) {
        CompanyUser user = userDao.findByUserid(vo.getUserId(), true);
        byte[] salt = Encodes.decodeHex(user.getSalt());
        String encodePwd = getEncodePwd(vo.getPassword(), salt);
        return encodePwd.equals(user.getPassword());
    }

    /**
     * 发送验证码
     * @param phone
     */
    @Override
    @Transactional
    public void sendCheckCode(String phone) {
        // 校验手机格式是否合规
        if (!SMSUtils.isMobile(phone)) {
            throw new BaseException(Status.PHONE_WRONGFUL);
        }
        // 防止一个手机号短时间内重复提交
        if (avoidDuplicate(phone)) {
            String checkCode = RandomUtil.randomNumbers(6);
            logger.info("[sendCheckCode]手机号：{},随机验证码：{}", phone, checkCode);
            wxSmsCheckCodeDao.save(WxSmsCheckCode.builder().phone(phone).checkCode(checkCode).build());
            // 发送短信验证码
            SMSUtils.send(Constant.SEND_SMS_TYPE_CHECK_CODE,phone,checkCode);
        }else{
            throw new BaseException(Status.SMS_SEND_BUSY);
        }
    }



    /**
     * 防止一个手机号重复发送验证码
     * @param phone
     * @return false：重复 true：可以发送
     */
    private boolean avoidDuplicate(String phone) {
        WxSmsCheckCode code = wxSmsCheckCodeDao.findFirstByPhoneOrderByCreatedDateDesc(phone);
        if (code != null) {
            Date now = new Date();
            if (DateUtil.offsetSecond(code.getCreatedDate(), 60).isAfter(now)) {
                return false;
            }
        }
        return true;
    }


    /**
     * 设置初始密码 并发送
     * @param loginPhone
     */
    private void initPassword(String loginPhone) {
        if (StrUtil.isBlank(loginPhone)) {
            logger.error("设置初始化密码失败，loginPhone为空");
            return;
        }
        LoginRequest vo = new LoginRequest();
        vo.setLoginPhone(loginPhone);
        vo.setLoginType(Constant.LOGIN_TYPE_CODE);
        // 密码6位数字随机数
        String initialPassword = RandomUtil.randomNumbers(6);
        vo.setPassword(initialPassword);
        CompanyUser companyUser = userDao.findByLoginPhone(loginPhone, Constant.ENABLE_FLG);
        byte[] salt = Digests.generateSalt(SALT_SIZE);
        String strSalt = Encodes.encodeHex(salt);
        String password = getEncodePwd(vo.getPassword(), salt);
        companyUser.setPassword(password);
        companyUser.setSalt(strSalt);
        userDao.save(companyUser);
        logger.info("[initPassword]手机号：{}，初始密码为：{}",loginPhone, initialPassword);
        //  发送初始密码给用户
        SMSUtils.send(Constant.SEND_SMS_TYPE_INIT_PASSWORD,loginPhone,initialPassword);
    }

    /**
     * 注册 设置初始密码 并发送
     * @param loginPhone
     * @return
     */
    private CompanyUser registerInitPassword(String loginPhone) {
        return registerInitPassword(loginPhone, null);
    }

    /**
     * 注册 设置初始密码 并发送
     * @param loginPhone
     * @return
     */
    private CompanyUser registerInitPassword(String loginPhone,String inviteCode) {
        LoginRequest vo = new LoginRequest();
        vo.setLoginPhone(loginPhone);
        vo.setLoginType(Constant.LOGIN_TYPE_CODE);
        // 密码6位数字随机数
        String initialPassword = RandomUtil.randomNumbers(6);
        vo.setPassword(initialPassword);
        vo.setInviteCode(inviteCode);
        CompanyUser companyUser = register(vo);
        logger.error("[registerInitPassword]手机号：{}，初始密码为：{}",loginPhone, initialPassword);
        //  发送密码给用户
        SMSUtils.send(Constant.SEND_SMS_TYPE_INIT_PASSWORD,loginPhone,initialPassword);
        return companyUser;
    }

    private CompanyUser wxRegisterInitPassword(String loginPhone, String inviteCode, String openId) {
        LoginRequest vo = new LoginRequest();
        vo.setLoginPhone(loginPhone);
        vo.setOpenId(openId);
//        vo.setLoginType(Constant.LOGIN_TYPE_CODE);
        // 密码6位数字随机数
        String initialPassword = RandomUtil.randomNumbers(6);
        vo.setPassword(initialPassword);
        vo.setInviteCode(inviteCode);
        CompanyUser companyUser = register(vo);
        logger.error("[wxRegisterInitPassword]手机号：{}，初始密码为：{}",loginPhone, initialPassword);
        //  发送密码给用户
        SMSUtils.send(Constant.SEND_SMS_TYPE_INIT_PASSWORD,loginPhone,initialPassword);
        return companyUser;
    }

    /**
     * 保存用户信息
     *
     * @param vo
     * @return
     */
    @Override
    @Transactional(readOnly = false)
    public RespVo<CompanyUser> saveApplyOnLineData(CompanyOnLineApplyVo vo) {
        logger.info("协助客户线上化申请:{}", JsonUtil.obj2Json(vo));
        RespVo<CompanyUser> resp = new RespVo<>();

        // 1.查询经办人手机号是否已注册
        CompanyUser companyUser = userDao.findByLoginPhone(vo.getManagerPhone(), true);
        if (Objects.isNull(companyUser)) {
            companyUser = new CompanyUser();
            companyUser.setEnableFlg(true);
            companyUser.setLoginPhone(vo.getManagerPhone());
            companyUser = userDao.save(companyUser);
        }
        if (StringUtils.isBlank(companyUser.getPassword())) {
            // 设置初始密码 并发送短信通知
            initPassword(vo.getManagerPhone());
        }

        // 2.更新/保存 用户信息表数据
        UserDetail userDetail = userDetailDao.findByUserIdAndEnableFlgTrue(companyUser.getId());
        if (Objects.isNull(userDetail)) {
            userDetail = new UserDetail();
            userDetail.setUserId(companyUser.getId());
        }
        userDetail.setInfoStep(InfoStep.DEFAULT.getInfoStep());
        userDetail.setCompanyId(vo.getCompanyId());
        userDetail = userDetailDao.save(userDetail);

        // 3.更新/保存 客户开户信息
        vo.setUserId(companyUser.getId());
        userInfoService.saveSaveInfo(vo);

        companyUser.setUserDetail(userDetail);
        resp.setData(companyUser);
        logger.info("用户信息保存成功 loginPhone:{},companyId:{},openId:{}", companyUser.getLoginPhone(), userDetail.getCompanyId(), companyUser.getOpenId());
        return resp;
    }
}
