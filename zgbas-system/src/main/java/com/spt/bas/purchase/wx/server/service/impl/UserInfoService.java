package com.spt.bas.purchase.wx.server.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.spt.auth.sdk.cache.DictUtil;
import com.spt.auth.sdk.entity.SysDeptSdk;
import com.spt.auth.sdk.entity.SysUserSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.auth.sdk.vo.UserLoginVo;
import com.spt.bas.client.constant.ApplySource;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.*;
import com.spt.bas.client.remote.*;
import com.spt.bas.client.util.RmbUtil;
import com.spt.bas.client.vo.*;
import com.spt.bas.purchase.wx.client.constant.SaveInfoType;
import com.spt.bas.purchase.wx.client.entity.SaveInfo;
import com.spt.bas.purchase.wx.client.entity.UserDetail;
import com.spt.bas.purchase.wx.client.payload.AuthFaceRecognition;
import com.spt.bas.purchase.wx.client.vo.*;
import com.spt.bas.purchase.wx.server.cache.BsDictUtil;
import com.spt.bas.purchase.wx.server.common.*;
import com.spt.bas.purchase.wx.server.dao.*;
import com.spt.bas.purchase.wx.server.payload.UploadBase64Request;
import com.spt.bas.purchase.wx.server.payload.*;
import com.spt.bas.purchase.wx.server.service.*;
import com.spt.bas.purchase.wx.server.util.*;
import com.spt.bas.purchase.wx.server.vo.UploadFileVo;
import com.spt.bas.purchase.wx.server.vo.*;
import com.spt.bas.report.client.entity.RptConfirmReceiptDetail;
import com.spt.bas.report.client.entity.RptConfirmReceiptVo;
import com.spt.pm.constant.PmConstants;
import com.spt.pm.entity.BsKeySequence;
import com.spt.pm.entity.PmApproveContents;
import com.spt.pm.vo.PmApproveStepFlowVo;
import com.spt.sign.client.entity.SignContract;
import com.spt.sign.client.entity.SignInfo;
import com.spt.sign.client.remote.ICfcaSignClient;
import com.spt.sign.client.remote.ISignInfoClient;
import com.spt.sign.client.vo.AxqContractVo;
import com.spt.sign.client.vo.AxqUrlVo;
import com.spt.sign.client.vo.CtrProductVo;
import com.spt.tools.core.date.DateOperator;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.annotation.ServiceTransactional;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 信息补全服务
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-09-20 14:03
 */
@Component
public class UserInfoService implements IUserInfoService {
    private static Logger logger = LoggerFactory.getLogger(UserInfoService.class);
    @Autowired
    private BsCompanyDao bsCompanyDao;
    @Autowired
    private UploadHelper uploadHelper;
    @Autowired
    private OcrHelper ocrHelper;
    @Autowired
    private SaveInfoDao saveInfoDao;
    @Autowired
    private UserDetailDao userDetailDao;
    @Autowired
    private CompanyIndustryDao companyIndustryDao;
    @Autowired
    private IApplyService applyService;
    @Autowired
    private IAuthOpenFacade authOpenFacade;
    @Autowired
    private FeedbackDao feedbackDao;
    @Autowired
    private IBsWarehouseClient bsWarehouseClient;
    @Autowired
    private IBsWarehouseAddrClient bsWarehouseAddrClient;
    @Autowired
    private IBsSupplyInfoClient bsSupplyInfoClient;
    @Autowired
    private IBsEntrustClient bsEntrustClient;
    @Autowired
    private IBsCompanyAccountClient bsCompanyAccountClient;
    @Autowired
    private ICtrContractClient ctrContractClient;
    @Autowired
    private ICtrServiceContractClient serviceContractClient;
    @Autowired
    private ITempSaveInfoService tempSaveInfoService;
    @Autowired
    private ICfcaSignClient cfcaSignClient;
    @Autowired
    private IApplyMatchClient applyMatchClient;
    @Autowired
    private JinXinApi jinXinApi;
    @Resource
    private ISignInfoClient signInfoClient;
    @Resource
    private IBsCompanyClient bsCompanyClient;
    @Resource
    private IPmApproveContentsClient pmApproveContentsClient;
    @Resource
    private IPmApproveClient approveClient;
    @Resource
    private IContractService contractService;
    @Resource
    private ISyncDataClient syncDataClient;
    @Resource
    private IApplyDeliveryOutClient applyDeliveryOutClient;
    @Resource
    private IBsContractTemplateClient bsContractTemplateClient;
    @Resource
    private IBsKeySequenceClient bsKeySequenceClient;
    @Value("${file.show.url}")
    private String fileShowUrl;
    @Autowired
    private ICtrContractClient contractClient;
    @Autowired
    private IApplyConfirmReceiptClient applyConfirmReceiptClient;
    @Autowired
    private IBsCompanyOurClient bsCompanyOurClient;
    @Autowired
    private IApplyChargeSalesClient applyChargeSalesClient;
    @Autowired
    private ISuccessContractService successContractService;
    @Autowired
    private IBsCompanyDcsxClient bsCompanyDcsxClient;
    // Phase 8: WX DeptUtils bean renamed to wxDeptUtils (D-P8-01 disambiguation vs basServer DeptUtils),
    // so this name-based @Resource must point at the renamed bean. Field type is the WX DeptUtils.
    @Resource(name = "wxDeptUtils")
    private DeptUtils deptUtils;
    @Resource
    private BsDictDataDao bsDictDataDao;
    @Resource
    private IMidstreamClient midstreamClient;
    @Resource
    private ISealUsageDCSXClient sealUsageDCSXClient;

    /**
     * 获取用户所选的企业类别
     *
     * @return
     */
    @Override
    @Transactional
    public CustomVo getPmsCompanyType() {
        UserDetail userDetail = userDetailDao.findByUserIdAndEnableFlg(UserHelper.getCurUserId(), true);
        BsDictData bsDictData = BsDictUtil.getBsDictData(CustomSetting.COMPANY_TYPE.getCode(), userDetail.getCustomCompanyType());
        return CustomVo.builder()
                .customCompanyType(bsDictData.getDictCd())
                .customCompanyName(bsDictData.getDictName())
                .infoStep(userDetail.getInfoStep())
                .build();
    }

    /**
     * 获取临时保存信息
     *
     * @return
     */
    @Override
    public String getCompanyInfo() {
        return getAllAppletNotify();
    }

    /**
     * 获取量身定制配置参数
     *
     * @return
     */
    @Override
    public Map<String, Object> getCustomSetting() {
        List<BsDictData> listByCategory = BsDictUtil.getListByCategory(CustomSetting.CUSTOM_SETTING.getCode());
        Map<String, Object> parent = new LinkedHashMap<>(listByCategory.size());
        for (BsDictData bsDictData : listByCategory) {
            if (StrUtil.equals(bsDictData.getDictCd(), CustomSetting.CUSTOM_COMPANY_SOURCE.getCode())) {
                List<BsCompanyIndustry> industries = companyIndustryDao.findByGrand(1);
                Map<String, Object> son = new LinkedHashMap<>(industries.size());
                for (BsCompanyIndustry industry : industries) {
                    son.put(industry.getIndustryCode(), industry.getIndustryName());
                }
                parent.put(bsDictData.getDictCd(), son);
                continue;
            }
            List<BsDictData> bsDictData1 = BsDictUtil.getListByCategory(bsDictData.getDictCd());
            Map<String, Object> son = new LinkedHashMap<>(bsDictData1.size());
            for (BsDictData dictData : bsDictData1) {
                son.put(dictData.getDictCd(), dictData.getDictName());
            }
            parent.put(bsDictData.getDictCd(), son);
        }
        return parent;
    }

    /**
     * 用户服务开通信息
     *
     * @return
     */
    @Override
    public ServiceOpeningInfoVo getSptServieOpeningInfo() {
        UserDetail userDetail = userDetailDao.findByUserIdAndEnableFlg(UserHelper.getCurUserId(), true);
        ServiceOpeningInfoVo serviceOpeningInfoVo = BeanUtil.copyProperties(userDetail, ServiceOpeningInfoVo.class);
        return serviceOpeningInfoVo;
    }

    /**
     * 更新服务开通信息
     */
    @Override
    public void updateUserStatus(ServiceOpeningInfoRequest serviceOpeningInfoRequest) {
        UserDetail userDetail = userDetailDao.findByUserIdAndEnableFlg(UserHelper.getCurUserId(), true);
        handleServiceOpeningInfoWithUserDetail(userDetail, serviceOpeningInfoRequest);
        userDetailDao.save(userDetail);
    }

    /**
     * 获取额度信息
     *
     * @return
     */
    @Override
    public QuotaInfoVo getQuotaInfo() {
        UserDetail userDetail = userDetailDao.findByUserIdAndEnableFlg(UserHelper.getCurUserId(), true);
        return BeanUtil.copyProperties(userDetail, QuotaInfoVo.class);
    }

    /**
     * 处理哪一个服务信息需要更新
     *
     * @param userDetail
     * @return
     */
    private UserDetail handleServiceOpeningInfoWithUserDetail(UserDetail userDetail, ServiceOpeningInfoRequest request) {
        switch (request.getType()) {
            case "0":
                userDetail.setCompanyApplyStatus(request.getStatus());
                break;
            case "1":
                userDetail.setEntrustApplyStatus(request.getStatus());
                break;
            case "2":
                userDetail.setDepositStatus(request.getStatus());
                break;
            case "3":
                userDetail.setApplyIouStatus(request.getStatus());
                break;
            case "4":
                userDetail.setServiceFeeForIouStatus(request.getStatus());
                break;
            case "5":
                userDetail.setCfcaApprovedStatus(request.getStatus());
                break;
            case "6":
                userDetail.setCfcaPayFeeStatus(request.getStatus());
                break;
            case "7":
                userDetail.setPartnerApplyStatus(request.getStatus());
                break;
            case "8":
                userDetail.setQuotaTestStatus(request.getStatus());
                break;
            default:
                throw new BaseException(Status.BAD_REQUEST);
        }
        return userDetail;
    }

    /**
     * '上传证件'步骤 获取临时保存信息
     *
     * @return
     */
    @Override
    public CompanyBaseInfoVo findLatestNotifyBaseInfo() {
        SaveInfo saveInfo = getAppletNotify(SaveInfoType.BASE_INFO.getType());
        if (saveInfo == null) {
            throw new BaseException(Status.NO_SAVE_INFO);
        }
        return JsonUtil.json2Object(CompanyBaseInfoVo.class, saveInfo.getContent());
    }

    /**
     * 上传并识别工商营业执照信息
     *
     * @param fileRequest
     * @param req
     * @return
     */
    @Override
    @Transactional
    public LicenseVo uploadAndOcrLicenseCode(UploadBase64Request fileRequest, HttpServletRequest req) {
        // ocr识别
        LicenseVo licenseVo = ocrHelper.ocrLicenses(fileRequest.getBase64Data());
        logger.info("uploadAndOcrLicenseCode:{}", JsonUtil.obj2Json(licenseVo));
        if (licenseVo == null) {
            throw new BaseException(Status.LICENSE_OCR_FAIL);
        }
        
        if(fileRequest.getLoginFlg()){
            // 企业id
            Long companyId = null;
            // 查询这家企业之前是否已录入,如果已录入
            BsCompany bsCompany = bsCompanyDao.findBsCompanyByCompanyNameAndEnableFlgTrue(licenseVo.getCompanyName());
            if (bsCompany != null) {
                companyId = bsCompany.getId();
            } else {
                bsCompany = new BsCompany();
            }
            bsCompany.setAddress(licenseVo.getAddress());
            bsCompany.setAllowed("Y");
            bsCompany.setEnableFlg(true);
            bsCompany.setCompanyName(licenseVo.getCompanyName());
            bsCompany.setCompanyCreditNo(licenseVo.getLicenseNumber());
            bsCompany.setLegalRepresent(licenseVo.getLegalRepresent());
            bsCompany.setEnterpriseId(BasConstants.ZG_ENTERPRISE_ID);
            BsCompany save = bsCompanyDao.save(bsCompany);
            if (companyId == null) {
                companyId = save.getId();
            }
            // userDetail用户信息绑定公司
            userBindCompany(companyId);
        }

        // 上传
        UploadFileVo fileRespVo = uploadHelper.uploadBase64(fileRequest, req);
        return LicenseVo.builder()
                .address(licenseVo.getAddress())
                .businessLicenseUrl(fileRespVo.getFileId())
                .licenseNumber(licenseVo.getLicenseNumber())
                .companyName(licenseVo.getCompanyName())
                .legalRepresent(licenseVo.getLegalRepresent())
                .build();
    }

    /**
     * userDetail用户信息绑定公司(只是关联 还没有正式绑定)
     *
     * @param companyId
     */
    private void userBindCompany(Long companyId) {
        UserDetail userDetail = userDetailDao.findByUserIdAndEnableFlgTrue(UserHelper.getCurUserId());
        userDetail.setCompanyId(companyId);
//        userDetail.setIsBind(true);
        userDetailDao.save(userDetail);
    }

    /**
     * 如果这家公司已录入
     * 校验是否已绑定用户
     *
     * @param companyId
     */
    private void checkUserIsBind(Long companyId) {
        UserDetail userDetailForCompany = userDetailDao.findByCompanyIdAndIsBindTrue(companyId);
        if (userDetailForCompany != null) {
            throw new BaseException(Status.CHECK_BIND_FAIL);
        }
    }

    /**
     * 上传并识别身份证
     *
     * @param req
     * @return
     */
    @Override
    public IdentityCardVo uploadAndOcrIdentityCard(UploadBase64Request req, HttpServletRequest request) {
        if (!CardType.ID_CARD.getCardType().equals(req.getCardType())) {
            throw new BaseException(Status.OCR_ONLY_ID_CARD);
        }
        // ocr识别
        IdentityCardVo vo = ocrHelper.ocrIdentityCard(req.getBase64Data(), req.getDirection());
        logger.info("uploadAndOcrIdentityCard:{}", JsonUtil.obj2Json(vo));
        // 上传
        UploadFileVo fileRespVo = uploadHelper.uploadBase64(req, request);
        if (Constant.ID_CARD_SIDE_BACK.equals(req.getDirection())) {
            vo.setLegalPersonOppositePicUrl(fileRespVo.getFileId());
        } else {
            vo.setLegalPersonPicUrl(fileRespVo.getFileId());
        }
        return vo;
    }

    /**
     * 临时保存
     *
     * @param request
     */
    @Override
    @Transactional
    public void appletNotifyBaseInfo(CompanyBaseInfoRequest request) {
        SaveInfo before;
        before = findSaveInfo(UserHelper.getCurBindCompanyId(), UserHelper.getCurUserId(), SaveInfoType.BASE_INFO.getType(), false);
        if (before == null) {
            before = new SaveInfo();
        }
        String json = JsonUtil.obj2Json(request);
        before.setContent(json);
        before.setType(SaveInfoType.BASE_INFO.getType());
        before.setUserId(UserHelper.getCurUserId());
        before.setCommitFlg(false);
        saveInfoDao.save(before);

    }

    /**
     * 发起资料审核申请
     *
     * @param request
     */
    @Override
    @Transactional
    public void applyBaseInfo(CompanyBaseInfoRequest request) {
        logger.info("===applyBaseInfo:{}", JsonUtil.obj2Json(request));
        SaveInfo before;
        before = findSaveInfo(UserHelper.getCurBindCompanyId(), UserHelper.getCurUserId(), SaveInfoType.BASE_INFO.getType(), false);
        if (before == null) {
            before = new SaveInfo();
        }
        String json = JsonUtil.obj2Json(request);
        before.setContent(json);
        before.setType(SaveInfoType.BASE_INFO.getType());
        before.setUserId(UserHelper.getCurUserId());
        before.setCommitFlg(false);
        saveInfoDao.save(before);

        // 发起资料申请
        UserDetail userDetail = userDetailDao.findByUserIdAndEnableFlgTrue(UserHelper.getCurUserId());
        if (userDetail.getCompanyId() == null) {
            throw new BaseException(Status.ERROR, "该用户还未绑定公司");
        }
        BsCompany company = bsCompanyDao.findByIdAndEnableFlgTrue(userDetail.getCompanyId());
        if (company == null) {
            throw new BaseException(Status.ERROR, "该企业不存在或已删除");
        }
        if (company.getMatchUserId() == null) {
            throw new BaseException(Status.ERROR, "该企业还未指定业务员,请联系管理员！");
        }
        SysUserSdk userById = authOpenFacade.findUserById(company.getMatchUserId());

        // 判断companyApplyStatus、depositStatus、entrustStatus 如果是0或3 正常发起
        // 先判断companyApplyStatus
        if (StrUtil.equals(userDetail.getCompanyApplyStatus(), Constant.APPLY_STATUS_REJECT)
                || StrUtil.equals(userDetail.getCompanyApplyStatus(), Constant.APPLY_STATUS_NO_START)) {
            // 发起公司信息审批
            ApplyCompanyInfo applyCompanyInfo = new ApplyCompanyInfo();
            applyCompanyInfo.setApplyUserId(company.getMatchUserId());
            applyCompanyInfo.setApplyUserName(userById.getUserName());
            CompanyBaseInfoVo latestNotifyBaseInfo = findLatestNotifyBaseInfo();
            applyCompanyInfo.setFileId(latestNotifyBaseInfo.getBusinessLicenseWithSealUrl());
            applyCompanyInfo.setWxUserId(UserHelper.getCurUserId());
            applyCompanyInfo.setApproveId(0L);
            applyCompanyInfo.setEnterpriseId(BasConstants.ZG_ENTERPRISE_ID);
            applyCompanyInfo.setCompanyName(company.getCompanyName());
            applyCompanyInfo.setCompanyId(company.getId());
            // 发起审批
            applyService.applyCompanyInfo(applyCompanyInfo);

            // 修改状态
            userDetail.setCompanyApplyStatus(Constant.APPLY_STATUS_APPLYING);
            userDetailDao.save(userDetail);
        }

    }

    /**
     * 同步临时保存信息到company表中
     *
     * @param userDetail
     */
    private void syncBaseInfo(UserDetail userDetail) {
        SaveInfo infoByType = tempSaveInfoService.getAppletNotify(userDetail.getUserId(), SaveInfoType.BASE_INFO.getType());
        if (infoByType.getContent() != null) {
            CompanyBaseInfoVo companyBaseInfo = JsonUtil.json2Object(CompanyBaseInfoVo.class, infoByType.getContent());
            BsCompany company = bsCompanyDao.findOne(userDetail.getCompanyId());
            // 法人
            company.setLegalRepresent(companyBaseInfo.getLegalRepresent());
            // 证件类型
            company.setCardType(companyBaseInfo.getCardType());
            // 法人身份证正面
            company.setCardFrontId(companyBaseInfo.getLegalPersonPicUrl());
            // 法人身份证反面
            company.setCardReverseId(companyBaseInfo.getLegalPersonOppositePicUrl());
            // 法人身份证号
            company.setIdentityCardNumber(companyBaseInfo.getIdentityCardNumber());
            // 联系人电话
            company.setContactPhone(companyBaseInfo.getPhone());
            // 联系人邮箱
            company.setContactEmail(companyBaseInfo.getEmail());
            bsCompanyDao.save(company);
        }
    }

    /**
     * 临时保存委托授权
     *
     * @param request
     */
    @Override
    public void appletNotifyEntrust(EntrustRequest request) {
        SaveInfo before;
        // 查询之前保存的临时信息
        before = findSaveInfo(UserHelper.getCurBindCompanyId(), UserHelper.getCurUserId(), SaveInfoType.ENTRUST.getType(), false);
        // 如果没有则初始化
        if (before == null) {
            before = new SaveInfo();
        }
        String json = JsonUtil.obj2Json(request);
        before.setContent(json);
        before.setType(SaveInfoType.ENTRUST.getType());
        before.setUserId(UserHelper.getCurUserId());
        before.setCommitFlg(false);
        // 保存临时信息
        saveInfoDao.save(before);

        Long curBindCompanyId = UserHelper.getCurBindCompanyId();
        // 根据id查询公司的相关信息
        BsCompany company = bsCompanyDao.findByIdAndEnableFlgTrue(curBindCompanyId);
        // 如果没有业务员则抛出异常
        if (company.getMatchUserId() == null) {
            throw new BaseException(Status.BIND_PARTNER_FAIL);
        }
//        UserDetail userDetail = userDetailDao.findByUserIdAndEnableFlg(UserHelper.getCurUserId(), true);
//        SysUser userById = adminOpenFacade.findUserById(company.getMatchUserId());
        // 发起委托授权申请
//        this.startEntrustFlow(company, userDetail, userById);
    }


    @Override
    public void applyEntrust() {
        Long curBindCompanyId = UserHelper.getCurBindCompanyId();
        BsCompany company = bsCompanyDao.findByIdAndEnableFlgTrue(curBindCompanyId);
        if (company.getMatchUserId() == null) {
            throw new BaseException(Status.BIND_PARTNER_FAIL);
        }
        UserDetail userDetail = userDetailDao.findByUserIdAndEnableFlg(UserHelper.getCurUserId(), true);
        SysUserSdk userById = authOpenFacade.findUserById(company.getMatchUserId());
        // 发起委托授权申请
        this.startEntrustFlow(company, userDetail, userById);
    }


    /**
     * 获取委托授权临时保存
     *
     * @return
     */
    @Override
    public EntrustVo findLatestNotifyEntrust() {
        SaveInfo byUserIdAndType = findSaveInfo(UserHelper.getCurBindCompanyId(), UserHelper.getCurUserId(), SaveInfoType.ENTRUST.getType(), false);
        if (byUserIdAndType == null) {
            throw new BaseException(Status.NO_SAVE_INFO);
        }
        EntrustVo entrustVo = JsonUtil.json2Object(EntrustVo.class, byUserIdAndType.getContent());
        return entrustVo;
    }

    /**
     * 生成委托授权书
     *
     * @return
     */
    @Override
    public String generatePowerOfAttorney(HttpServletRequest request) {
        EntrustVo entrustVo = findLatestNotifyEntrust();
        Map<String, String> map = new HashMap<>();
        map.put("clientCompany", entrustVo.getClientCompany());// 委托人
        map.put("legalRepresent", entrustVo.getLegalRepresent());// 法定代表人
        map.put("licenseNumber", entrustVo.getLicenseNumber());// 营业执照
        map.put("relationShipStr", ConvertUtils.convertRelationship(entrustVo.getRelationShip()));// 与受托人关系
        map.put("trusteeName", entrustVo.getTrusteeName());// 受托人姓名
        map.put("trusteeGenderStr", ConvertUtils.convertGender(entrustVo.getTrusteeGender()));// 性别
        map.put("trusteePhone", entrustVo.getTrusteePhone());// 受托人电话号码
        map.put("identityCardNumber", entrustVo.getIdentityCardNumber());// 受托人身份证
        String html;
        try {
            html = FreemarkerUtil.getTemplate("zgEntrust.html", map);
        } catch (Exception e) {
            logger.error("合并模板异常", e);
            throw new BaseException(Status.MERGE_TEMPLATE_FAIL);
        }
        // html转图片
        String imgBase64 = ConvertUtils.html2Img(html, "png");
        // 上传
        UploadBase64Request uploadBase64Request = new UploadBase64Request();
        uploadBase64Request.setBase64Data(imgBase64);
        uploadBase64Request.setFileName("x.png");
        UploadFileVo uploadFileVo = uploadHelper.uploadBase64(uploadBase64Request, request);
        return uploadFileVo.getFileId();
    }

    /**
     * 签署提交
     *
     * @param request
     * @return
     */
    @Override
    @Transactional
    public BaseVo updateEntrustDetail(EntrustRequest request) {
        String json = JsonUtil.obj2Json(request);
        SaveInfo saveInfo = new SaveInfo();
        saveInfo.setContent(json);
        saveInfo.setType(Constant.TEMP_SAVE_TYPE_ENTRUST);
        saveInfo.setUserId(UserHelper.getCurUserId());
        saveInfo.setCommitFlg(true);
        saveInfoDao.save(saveInfo);
        BaseVo baseVo = new BaseVo();
        baseVo.setInfoStep(InfoStep.VERIFICATION_OF_DEPOSIT.getInfoStep());
        return baseVo;
    }

    /**
     * 获取委托授权信息
     *
     * @param request
     * @return
     */
    @Override
    public EntrustVo getEntrustDetail(BaseRequest request) {
        SaveInfo saveInfo = findSaveInfo(UserHelper.getCurBindCompanyId(), Long.valueOf(request.getUserId()), Constant.TEMP_SAVE_TYPE_ENTRUST, true);
        if (saveInfo == null) {
            throw new BaseException(Status.USER_NOT_SAVE);
        }
        EntrustVo entrustVo = JsonUtil.json2Object(EntrustVo.class, saveInfo.getContent());
        return entrustVo;
    }

    /**
     * 获取入金金额和账户信息
     *
     * @return
     */
    @Override
    @Transactional
    public PriceAndAccountVo getDepositInfo() {
        UserDetail userDetail = userDetailDao.findByUserIdAndEnableFlg(UserHelper.getCurUserId(), true);
        if (userDetail.getTotalDepositPrice() == null) {
            // 入金金额随机 存入;
            String s = RandomUtil.randomNumbers(2);
            BigDecimal bigDecimal = new BigDecimal(s);
            BigDecimal depositPrice = bigDecimal.multiply(new BigDecimal("0.01"));
            userDetail.setTotalDepositPrice(depositPrice);
            userDetailDao.save(userDetail);
        }
        // 未支付 = 总金额-已支付
        BigDecimal subtract = userDetail.getTotalDepositPrice().subtract(userDetail.getPaidDepositPrice());
        // 从字典中取出账户信息
        PriceAndAccountVo build = PriceAndAccountVo.builder()
                .price(subtract)
                .accountName(DictUtil.getValue(Constant.DICT_CODE_ACCOUNT_TYPE, Constant.DICT_CODE_ACCOUNT_NAME))
                .accountNumber(DictUtil.getValue(Constant.DICT_CODE_ACCOUNT_TYPE, Constant.DICT_CODE_ACCOUNT_NUMBER))
                .bank(DictUtil.getValue(Constant.DICT_CODE_ACCOUNT_TYPE, Constant.DICT_CODE_BANK))
                .remark(DictUtil.getValue(Constant.DICT_CODE_ACCOUNT_TYPE, Constant.DICT_CODE_REMARK))
                .build();
        return build;
    }

    /**
     * 提交入金申请
     *
     * @param request
     * @return
     */
    @Override
    public BaseVo updateDeposit(BaseRequest request) {
        ApplyDeposit applyDeposit = new ApplyDeposit();
        // todo 发起入金验证审核
        UserDetail userDetail = userDetailDao.findByUserIdAndEnableFlgTrue(UserHelper.getCurUserId());
        applyDeposit.setStatus(BasConstants.APPROVE_STATUS_N);
        applyDeposit.setTargetAmount(userDetail.getTotalDepositPrice());
        applyDeposit.setWxUserId(UserHelper.getCurUserId());
        applyService.applyDeposit(applyDeposit);
        BaseVo baseVo = new BaseVo();
        baseVo.setInfoStep(InfoStep.VERIFICATION_OF_DEPOSIT.getInfoStep());
        return baseVo;
    }

    /**
     * 补充资料 临时保存
     *
     * @param request
     */
    @Override
    @Transactional
    public void appletNotifySupplyInfo(SupplyInfoRequest request) {
        String json = JsonUtil.obj2Json(request);

        // 补充资料临时保存
        appletNotify(json, SaveInfoType.SUPPLY_INFO.getType(), UserHelper.getCurUserId(), true);

        // 同步保存至 bsSupplyInfo表
        syncBsSupplyInfo(json);
    }

    /**
     * 同步保存至 bsSupplyInfo表
     *
     * @param supplyInfoJsonStr
     */
    private void syncBsSupplyInfo(String supplyInfoJsonStr) {
        if (supplyInfoJsonStr.isEmpty()) {
            return;
        }
        BsSupplyInfo bsSupplyInfo = JsonUtil.json2Object(BsSupplyInfo.class, supplyInfoJsonStr);
        bsSupplyInfo.setWxUserId(UserHelper.getCurUserId());
        bsSupplyInfo.setCompanyId(UserHelper.getCurBindCompanyId());
        bsSupplyInfoClient.save(bsSupplyInfo);
    }

    /**
     * cfca提交申请
     * @param request
     * @return
     */
    @Override
    @Transactional
    public void updateCfcaApproved(CfcaRequest request) {
        UserDetail userDetail = userDetailDao.findByUserIdAndEnableFlg(UserHelper.getCurUserId(), true);
        if (!userDetail.getIsBind() || userDetail.getCompanyId() == null) {
            throw new BaseException(Status.CFCA_FAIL);
        }
        SaveInfo saveInfo = new SaveInfo();
        saveInfo.setCommitFlg(false);
        saveInfo.setContent(JsonUtil.obj2Json(request));
        saveInfo.setType(Constant.TEMP_SAVE_TYPE_CFCA_APPROVED);
        saveInfo.setUserId(UserHelper.getCurUserId());
        saveInfoDao.save(saveInfo);
        userDetail.setCfcaApprovedStatus(Constant.APPLY_STATUS_APPLYING);
        userDetailDao.save(userDetail);
        // 发起cfca申请
        ApplyCfca applyCfca = new ApplyCfca();
        applyCfca.setFileId(request.getElectronicSignFileId());
        applyCfca.setUkeyNumber(request.getUkeyNumber());
        applyService.applyCfca(applyCfca);
    }

    /**
     * 获取企业已经开通的CFCA信息
     *
     * @return
     */
    @Override
    public CfcaVo getCfcaInfo() {
        SaveInfo appletNotify = getAppletNotify(Constant.TEMP_SAVE_TYPE_CFCA_APPROVED);
        if (appletNotify == null) {
            throw new BaseException(Status.NO_SAVE_INFO);
        }
        return JsonUtil.json2Object(CfcaVo.class, appletNotify.getContent());
    }


    /**
     * 获取CFCA服务费金额和账户信息
     *
     * @param request
     * @return
     */
    @Override
    public PriceAndAccountVo getCfcaPayFeeInfo(BaseRequest request) {
        UserDetail userDetail = userDetailDao.findByUserIdAndEnableFlg(Long.valueOf(request.getUserId()), true);
        if (userDetail.getTotalCfcaFeePrice() == null) {
            // 初始金额从字典中取
            String totalCfcaFeePrice = DictUtil.getValue(Constant.DICT_CFCA_FEE_PRICE_TYPE, Constant.DICT_CFCA_FEE_PRICE_CODE);
            try {
                BigDecimal oriServiceFee = new BigDecimal(totalCfcaFeePrice);
                userDetail.setTotalCfcaFeePrice(oriServiceFee);
                userDetailDao.save(userDetail);
            } catch (Exception e) {
                throw new BaseException(Status.CFCA_FEE_FAIL);
            }
        }
        // 未支付 = 总金额-已支付
        BigDecimal subtract = userDetail.getTotalCfcaFeePrice().subtract(userDetail.getPaidCfcaFeePrice());
        // 获取金额和账户信息
        return getPriceAndAccountVo(subtract);
    }

    /**
     * cfca支付服务费提交申请
     *
     * @param request
     */
    @Override
    public void updateCfcaPayFee(BaseRequest request) {
        // todo 发起审批
        UserDetail userDetail = userDetailDao.findByUserIdAndEnableFlg(Long.valueOf(request.getUserId()), true);
        userDetail.setCfcaPayFeeStatus(Constant.APPLY_STATUS_APPLYING);
        userDetailDao.save(userDetail);
    }

    /**
     * 新增企业配置的仓库信息
     *
     * @param request
     */
    @Override
    public Long addWarehouse(WarehouseRequest request) {
        BsWarehouse bsWarehouse = new BsWarehouse();
        BsWarehouseAddr bsWarehouseAddr = new BsWarehouseAddr();

        // request字段对应warehouse和warehouseaddr中
        mapWarehouse(bsWarehouse, bsWarehouseAddr, request);
        bsWarehouse.setEnableFlg(true);
        bsWarehouseAddr.setDefaultFlg(true);
        bsWarehouse.setWxUserId(UserHelper.getCurUserId());

        BsWarehouse save = bsWarehouseClient.save(bsWarehouse);
        Long warehouseId = save.getId();
        bsWarehouseAddr.setWarehouseId(warehouseId);
        bsWarehouseAddrClient.save(bsWarehouseAddr);
        return warehouseId;
    }

    /**
     * 获取企业配置的仓库信息列表
     *
     * @return
     */
    @Override
    public List<WarehouseVo> getWarehouseList() {
        List<BsWarehouse> byEnterpriseId = bsWarehouseClient.findByWxUserId(UserHelper.getCurUserId());
        List<WarehouseVo> result = new ArrayList<>(byEnterpriseId.size());
        for (BsWarehouse bsWarehouse : byEnterpriseId) {
            // 遍历 转换数据结构
            result.add(convertWarehouseVo(bsWarehouse));
        }
        return result;
    }

    /**
     * 删除仓库信息
     *
     * @param request
     * @return
     */
    @Override
    public List<WarehouseVo> deleteWarehouse(WarehouseRequest request) {
        BsWarehouse warehouse = bsWarehouseClient.findByWxUserIdAndWarehouseId(UserHelper.getCurUserId(), request.getId());
        if (warehouse != null) {
            warehouse.setEnableFlg(false);
            bsWarehouseClient.save(warehouse);
        } else {
            throw new BaseException(Status.DELETE_WAREHOUSE_FAIL);
        }
        List<WarehouseVo> warehouseList = getWarehouseList();
        return warehouseList;
    }

    /**
     * 获取企业配置的某仓库信息
     *
     * @param request
     * @return
     */
    @Override
    public WarehouseVo getWarehouse(WarehouseRequest request) {
        BsWarehouse warehouse = bsWarehouseClient.findByWxUserIdAndWarehouseId(UserHelper.getCurUserId(), request.getId());
        if (warehouse == null) {
            return null;
        }
        return convertWarehouseVo(warehouse);
    }

    /**
     * bsWarehouse表 数据结构转化
     *
     * @param warehouse
     * @return
     */
    private WarehouseVo convertWarehouseVo(BsWarehouse warehouse) {
        // 将原来设计的仓库表（主表+子表（addr））转化为小程序端需要的接口
        WarehouseVo warehouseVo = new WarehouseVo();
        warehouseVo.setCurAreaName(warehouse.getArea());
        warehouseVo.setCityName(warehouse.getCity());
        warehouseVo.setContactPhone(warehouse.getContactPhone());
        warehouseVo.setContactPerson(warehouse.getContactName());
        warehouseVo.setProvinceName(warehouse.getProvince());
        warehouseVo.setWarehouseName(warehouse.getWarehouseName());
        warehouseVo.setId(warehouse.getId());
        // 小程序端生成的仓库，warehouse和warehouseaddr是一对一的
        if (!warehouse.getAddrs().isEmpty()) {
            warehouseVo.setAreaName(warehouse.getAddrs().get(0).getWarehouseAddr());
            warehouseVo.setWareCompanyName(warehouse.getAddrs().get(0).getWarehouseShortName());
        }
        return warehouseVo;
    }

    /**
     * 仓库数据结构转换
     *
     * @param bsWarehouse
     * @param bsWarehouseAddr
     * @param request
     */
    private void mapWarehouse(BsWarehouse bsWarehouse, BsWarehouseAddr bsWarehouseAddr, WarehouseRequest request) {
        bsWarehouse.setArea(request.getCurAreaName());
        bsWarehouse.setCity(request.getCityName());
        bsWarehouse.setContactName(request.getContactPerson());
        bsWarehouse.setContactPhone(request.getContactPhone());
        bsWarehouse.setProvince(request.getProvinceName());
        bsWarehouse.setEnterpriseId(BasConstants.ZG_ENTERPRISE_ID);
        bsWarehouse.setWarehouseName(request.getWarehouseName());
        if (bsWarehouseAddr != null) {
            bsWarehouseAddr.setWarehouseAddr(request.getAreaName());
            bsWarehouseAddr.setWarehouseShortName(request.getWareCompanyName());
        }
    }

    /**
     * 修改企业配置的仓库信息
     *
     * @param request
     */
    @Override
    public void updateWarehouse(WarehouseRequest request) {
        if (request.getId() == null) {
            throw new BaseException(Status.WAREHOUSE_NOT_ID);
        }
        BsWarehouse oriWarehouse = bsWarehouseClient.findByWxUserIdAndWarehouseId(UserHelper.getCurUserId(), request.getId());
        if (oriWarehouse == null) {
            throw new BaseException(Status.UPDATE_WAREHOUSE_FAIL);
        }

        // request字段映射warehouse和warehouseaddr中
        mapWarehouse(oriWarehouse, oriWarehouse.getAddrs().isEmpty() ? null : oriWarehouse.getAddrs().get(0), request);

        oriWarehouse.setEnableFlg(true);
        oriWarehouse.setWxUserId(UserHelper.getCurUserId());
        bsWarehouseClient.save(oriWarehouse);
    }

    /**
     * 查询企业配置的仓库个数
     *
     * @return
     */
    @Override
    public int queryWarehouseNum() {
        return bsWarehouseClient.countByWxUserId(UserHelper.getCurUserId());
    }

    /**
     * 提交成为合伙人
     */
    @Override
    public void applyPartner() {
        ApplyPartner applyPartner = new ApplyPartner();
        applyPartner.setWxUserId(UserHelper.getCurUserId());
        applyPartner.setEnterpriseId(BasConstants.ZG_ENTERPRISE_ID);
        applyPartner.setStatus(BasConstants.APPROVE_STATUS_N);
        applyPartner.setApproveId(0L);
        applyPartner.setPhone(UserHelper.getUser().getPhone());
        applyService.applyPartner(applyPartner);
        UserDetail userDetail = userDetailDao.findByUserIdAndEnableFlgTrue(UserHelper.getCurUserId());
        userDetail.setPartnerApplyStatus(Constant.APPLY_STATUS_APPLYING);
        userDetailDao.save(userDetail);
    }

    /**
     * 查询推荐码
     *
     * @return
     */
    @Override
    public Long queryPartnerCode() {
        UserDetail userDetail = userDetailDao.findByUserIdAndEnableFlgTrue(UserHelper.getCurUserId());
        return userDetail.getUserId();
    }

    /**
     * 查询企业所属业务员的电话
     *
     * @return
     */
    @Override
    public String queryPartnerPhone() {
        // 查询企业所属业务员的电话
        UserDetail userDetail = userDetailDao.findByUserIdAndEnableFlgTrue(UserHelper.getCurUserId());
        Long companyId = userDetail.getCompanyId();
        BsCompany company = bsCompanyDao.findByIdAndEnableFlgTrue(companyId);
        if (company.getMatchUserId() == null) {
            throw new BaseException(Status.BIND_PARTNER_FAIL);
        }
        SysUserSdk userById = authOpenFacade.findUserById(company.getMatchUserId());
        if (userById == null) {
            throw new BaseException(Status.PARTNER_NO_PHONE);
        }
        return userById.getPhonenumber();
    }

    /**
     * 额度测试提交申请
     */
    @Override
    public void quotaTest(QuotaTestRequest request) {
        // 1.绑定并分配业务员 暂定分配zhaofei
        UserDetail userDetail = userDetailDao.findByUserIdAndEnableFlgTrue(UserHelper.getCurUserId());
        userDetail.setCustomCompanyType(request.getCompanyType());
        userDetail.setCustomCompanySource(request.getCustomCompanySource());
        userDetail.setCustomMyRole(request.getCustomMyRole());
        userDetail.setCustomRepaymentPeriod(request.getCustomRepaymentPeriod());
        userDetail.setCustomQuota(request.getCustomQuota());
        BsCompany bsCompany = bsCompanyDao.findByCompanyNameLikeAndEnableFlgTrue(request.getCompanyName());
        if (bsCompany == null) {
            throw new BaseException(Status.NO_COMPANY);
        }
        bsCompany.setCompanyType(BsDictUtil.getValue(CustomSetting.COMPANY_TYPE.getCode(), userDetail.getCustomCompanyType()));
        bsCompany.setIndustry(companyIndustryDao.findById(Long.valueOf(userDetail.getCustomCompanySource())).get().getId().toString());
        if (UserHelper.getUser().getName() == null) {
            bsCompany.setContactName(BsDictUtil.getValue(CustomSetting.CUSTOM_MY_ROLE.getCode(), userDetail.getCustomMyRole()));
        } else {
            bsCompany.setContactName(UserHelper.getUser().getName());
        }
        bsCompany.setContactPhone(UserHelper.getUser().getPhone());
        if (userDetail.getCompanyId() == null) {
            throw new BaseException(Status.USER_NOT_BIND);
        }
        userDetail.setIsBind(true);
        if (checkCompanyIsBind(bsCompany.getId())) {
            throw new BaseException(Status.USER_HAS_BIND);
        }
        // 更新applyIouStatus和QuotaTest字段为审批中
        userDetail.setApplyIouStatus(Constant.APPLY_STATUS_APPLYING);
        userDetail.setQuotaTestStatus(Constant.APPLY_STATUS_APPLYING);

        //根据quotaType 默认分配额度
        switchQuota(userDetail);

        userDetailDao.save(userDetail);
        bsCompany.setMatchFllowDate(DateUtil.date());
        UserLoginVo userLoginVo = new UserLoginVo();
        userLoginVo.setAppCode(BasConstants.APP_CODE);
        userLoginVo.setLoginName("zgzhaofei");
        SysUserSdk sysUserSdk = authOpenFacade.findUserByLoginName(userLoginVo);
        bsCompany.setMatchUserId(Objects.nonNull(sysUserSdk) ? sysUserSdk.getUserId() : null);
        if(Objects.nonNull(sysUserSdk)){
            SysDeptSdk deptSdk = deptUtils.getDeptByUserIdAndDeptType(sysUserSdk.getUserId(), PmConstants.NODE_TYPE_DEPT);
            bsCompany.setMatchUserDeptId(Objects.nonNull(deptSdk) ? deptSdk.getDeptId() : null);
        }
        bsCompany.setEnterpriseId(BasConstants.ZG_ENTERPRISE_ID);
        bsCompanyDao.save(bsCompany);
        // 2.发起准入审批
        BsCompanyAllowed bsCompanyAllowed = new BsCompanyAllowed();
        bsCompanyAllowed.setApplyUserId(bsCompany.getMatchUserId());
        bsCompanyAllowed.setApplyUserName("zgzhaofei");
        bsCompanyAllowed.setCompanyId(bsCompany.getId());
        bsCompanyAllowed.setCompanyName(bsCompany.getCompanyName());
        bsCompanyAllowed.setEnterpriseId(BasConstants.ZG_ENTERPRISE_ID);
        bsCompanyAllowed.setApproveId(0L);
        bsCompanyAllowed.setAllowed(BasConstants.DICT_TYPE_ALLOWED_Y);
        bsCompanyAllowed.setCreditRating(BasConstants.DICT_TYPE_CREDITRATING_W);
        bsCompanyAllowed.setWxUserId(UserHelper.getCurUserId());
        applyService.applyAdmittance(bsCompanyAllowed);
        // 保存临时信息
        appletNotify(JsonUtil.obj2Json(request), SaveInfoType.QUOTA_TEST.getType(), UserHelper.getCurUserId());
    }

    /**
     * 根据quotaType 默认分配额度
     * <p>
     * 更新：默认预计额度都为0
     *
     * @param userDetail
     * @return
     */
    private UserDetail switchQuota(UserDetail userDetail) {
        BigDecimal customQuota = BigDecimal.ZERO;
        userDetail.setTotalCreditAmount(customQuota);
        userDetail.setTotalFuturesAmount(customQuota);
        userDetail.setTotalSpotAmount(customQuota);
        return userDetail;
    }

    /**
     * 校验企业是否已绑定
     *
     * @param companyId
     * @return
     */
    private boolean checkCompanyIsBind(Long companyId) {
        UserDetail userDetail = userDetailDao.findByCompanyIdAndIsBindTrueAndEnableFlgTrue(companyId);
        if (userDetail == null) {
            return false;
        }
        return true;
    }


    /**
     * 2.3.1营业执照副本加盖公章上传
     *
     * @param req
     * @param request
     * @return
     */
    @Override
    public UploadFileVo uploadAndOcrLicenseCodeWithSeal(UploadBase64Request req, HttpServletRequest request) {
        // ocr识别
        LicenseVo licenseVo = ocrHelper.ocrLicenses(req.getBase64Data());
        if (licenseVo == null) {
            throw new BaseException(Status.LICENSE_OCR_FAIL);
        }
        UserDetail userDetail = userDetailDao.findByUserIdAndEnableFlgTrue(UserHelper.getCurUserId());
        // 当用户已绑定时，校验加盖公章的营业执照信息和公司信息比较
        if (userDetail != null && userDetail.getIsBind() && userDetail.getCompanyId() != null) {
            BsCompany company = bsCompanyDao.findOne(userDetail.getCompanyId());
            if (company == null
                    || !StrUtil.equals(company.getCompanyCreditNo(), licenseVo.getLicenseNumber())
                    || !StrUtil.equals(company.getCompanyName(), licenseVo.getCompanyName())
            ) {
                throw new BaseException(Status.CHECK_LICENSE_FAIL);
            }
        } else {
            // 校验加盖公章的营业执照信息和额度测试时的营业执照信息比较
            SaveInfo before = getAppletNotify(SaveInfoType.QUOTA_TEST.getType());
            QuotaTestVo quotaTestVo = JsonUtil.json2Object(QuotaTestVo.class, before.getContent());
            if (!StrUtil.equals(quotaTestVo.getLicenseNumber(), licenseVo.getLicenseNumber())
                    || !StrUtil.equals(quotaTestVo.getCompanyName(), licenseVo.getCompanyName())
            ) {
                throw new BaseException(Status.CHECK_LICENSE_FAIL);
            }
        }

        // 上传
        return uploadHelper.uploadBase64(req, request);
    }

    /**
     * 获取补充资料临时保存信息
     *
     * @return
     */
    @Override
    public SupplyInfoVo findLatestNotifySupplyInfo() {
        SaveInfo appletNotify = getAppletNotify(SaveInfoType.SUPPLY_INFO.getType());
        if (appletNotify == null) {
            throw new BaseException(Status.ERROR, "用户还没有保存");
        }
        return JsonUtil.json2Object(SupplyInfoVo.class, appletNotify.getContent());
    }

    /**
     * 开通服务提交审核
     */
    @Override
    public void updateCompanyDetail() {

        UserDetail userDetail = userDetailDao.findByUserIdAndEnableFlgTrue(UserHelper.getCurUserId());
        if (userDetail.getCompanyId() == null) {
            throw new BaseException(Status.ERROR, "该用户还未绑定公司");
        }
        BsCompany company = bsCompanyDao.findByIdAndEnableFlgTrue(userDetail.getCompanyId());
        if (company == null) {
            throw new BaseException(Status.ERROR, "该企业不存在或已删除");
        }
        if (company.getMatchUserId() == null) {
            throw new BaseException(Status.ERROR, "该企业还未指定业务员");
        }
        SysUserSdk userById = authOpenFacade.findUserById(company.getMatchUserId());

        // 判断companyApplyStatus、depositStatus、entrustStatus 如果是0或3 正常发起
        // 先判断companyApplyStatus
        if (StrUtil.equals(userDetail.getCompanyApplyStatus(), Constant.APPLY_STATUS_REJECT)
                || StrUtil.equals(userDetail.getCompanyApplyStatus(), Constant.APPLY_STATUS_NO_START)) {
            // 发起公司信息审批
            ApplyCompanyInfo applyCompanyInfo = new ApplyCompanyInfo();
            applyCompanyInfo.setApplyUserId(company.getMatchUserId());
            applyCompanyInfo.setApplyUserName(userById.getUserName());
            CompanyBaseInfoVo latestNotifyBaseInfo = findLatestNotifyBaseInfo();
            applyCompanyInfo.setFileId(latestNotifyBaseInfo.getBusinessLicenseWithSealUrl());
            applyCompanyInfo.setWxUserId(UserHelper.getCurUserId());
            applyCompanyInfo.setApproveId(0L);
            applyCompanyInfo.setEnterpriseId(BasConstants.ZG_ENTERPRISE_ID);
            applyCompanyInfo.setCompanyName(company.getCompanyName());
            applyCompanyInfo.setCompanyId(company.getId());
            applyService.applyCompanyInfo(applyCompanyInfo);

            // 修改状态
            userDetail.setCompanyApplyStatus(Constant.APPLY_STATUS_APPLYING);
            userDetailDao.save(userDetail);
//            return;
        }
//        // 如果资料审批已过 再判断入金和委托的状态
//        if (StrUtil.equals(userDetail.getEntrustApplyStatus(), Constant.APPLY_STATUS_REJECT)
//                || StrUtil.equals(userDetail.getEntrustApplyStatus(), Constant.APPLY_STATUS_NO_START)) {
//            startEntrustFlow(company, userDetail, userById);
//        }
//        if (StrUtil.equals(userDetail.getDepositStatus(), Constant.APPLY_STATUS_REJECT)
//                || StrUtil.equals(userDetail.getDepositStatus(), Constant.APPLY_STATUS_NO_START)) {
//            startDepositFlow(company, userDetail, userById);
//        }
    }

    /**
     * 提交意见反馈
     *
     * @param request
     */
    @Override
    public void saveFeedback(FeedbackRequest request) {
        // 保存
        Feedback feedback = new Feedback();
        BeanUtil.copyProperties(request, feedback);
        feedback.setWxUserId(UserHelper.getCurUserId());
        feedback.setApproveId(0L);
        feedback.setEnterpriseId(BasConstants.ZG_ENTERPRISE_ID);
        Feedback save = feedbackDao.save(feedback);
        // 发起审批
        applyService.applyFeedback(save);

    }

    /**
     * 获取意见反馈类型
     *
     * @return
     */
    @Override
    public List<Map<String, String>> getFeedbackType() {
        List<BsDictData> listByCategory = BsDictUtil.getListByCategory(Constant.FEEDBACK_TYPE_DICT);
        List<Map<String, String>> result = new ArrayList<>(listByCategory.size());
        for (BsDictData bsDictData : listByCategory) {
            HashMap<String, String> t_map = new HashMap<>();
            t_map.put(bsDictData.getDictCd(), bsDictData.getDictName());
            result.add(t_map);
        }
        return result;
    }

    /**
     * 获取付款银行信息
     *
     * @return
     */
    @Override
    public PayBankInfoVo getPaybankInfo(PayBankInfoRequest request) {
        // 暂时解决办法，以后考虑数据库方式
        PayBankInfoVo result = new PayBankInfoVo();
        CtrContract sellContract = ctrContractClient.findByContractNoV2(request.getContractNo());
        PayBankInfoVo vo1 = PayBankInfoVo.builder()
                .accountName(DictUtil.getValue(Constant.DICT_CODE_ACCOUNT_TYPE, Constant.DICT_CODE_ACCOUNT_NAME))
                .accountNumber(DictUtil.getValue(Constant.DICT_CODE_ACCOUNT_TYPE, Constant.DICT_CODE_ACCOUNT_NUMBER))
                .bank(DictUtil.getValue(Constant.DICT_CODE_ACCOUNT_TYPE, Constant.DICT_CODE_BANK))
                .build();
        PayBankInfoVo vo = PayBankInfoVo.builder()
                .accountName(DictUtil.getValue(Constant.DICT_CODE_SERVICE_ACCOUNT_TYPE, Constant.DICT_CODE_ACCOUNT_NAME))
                .accountNumber(DictUtil.getValue(Constant.DICT_CODE_SERVICE_ACCOUNT_TYPE, Constant.DICT_CODE_ACCOUNT_NUMBER))
                .bank(DictUtil.getValue(Constant.DICT_CODE_SERVICE_ACCOUNT_TYPE, Constant.DICT_CODE_BANK))
                .build();
        // 销售合同
        if ("0".equals(request.getType())) {
            if (sellContract.getOurCompanyName().equals(vo1.getAccountName())) {
                result = vo1;
            } else {
                result = vo;
            }
        } else if ("1".equals(request.getType())) {
            CtrServiceContract serviceContract = serviceContractClient.findByCtrContract(sellContract.getId());
            // 服务合同
            if (serviceContract.getOurCompanyName().equals(vo1.getAccountName())) {
                result = vo1;
            } else {
                result = vo;
            }
        }
        return result;
    }

    /**
     * 获取企业配置的发票信息
     *
     * @return
     */
    @Override
    public BillInfoVo getBillsInfo() {
        BillInfoVo billInfoVo = new BillInfoVo();
        Long companyId = UserHelper.getCurBindCompanyId();
        if (companyId != null) {
            BsCompanyVo bsCompanyVo = new BsCompanyVo();
            bsCompanyVo.setEnterpriseId(BasConstants.ZG_ENTERPRISE_ID);
            bsCompanyVo.setId(companyId);
            List<BsCompanyAccount> companyAccountList = bsCompanyAccountClient.findCompanyAccountFlg(bsCompanyVo);
            if (!companyAccountList.isEmpty()
                    && companyAccountList.get(0) != null) {
                BeanUtil.copyProperties(companyAccountList.get(0), billInfoVo);
            }
        }
        return billInfoVo;
    }

    /**
     * 添加企业发票信息
     *
     * @param billInfoRequest
     */
    @Override
    public void addBillsInfo(BillInfoRequest billInfoRequest) {
        Long curBindCompanyId = UserHelper.getCurBindCompanyId();
        if (curBindCompanyId == null) {
            throw new BaseException(Status.ERROR, "该账号还未绑定企业，无法添加企业发票信息");
        }
        billInfoRequest.setCompanyId(UserHelper.getCurBindCompanyId());
        bsCompanyAccountClient.addBillsInfo(billInfoRequest);
    }

    //================================================================================================

    /**
     * 临时保存 update
     *
     * @param json   内容json
     * @param type   类型
     * @param userId
     */
    private void appletNotify(String json, String type, Long userId) {
        SaveInfo saveInfo = getAppletNotify(type);
        // 如果传入为空，就是insert 否则就是update
        if (saveInfo == null) {
            saveInfo = new SaveInfo();
        }
        saveInfo.setContent(json);
        saveInfo.setType(type);
        saveInfo.setUserId(userId);
        saveInfo.setCommitFlg(false);
        saveInfoDao.save(saveInfo);
    }

    /**
     * 临时保存 update
     *
     * @param json
     * @param type
     * @param userId
     * @param commitFlg
     */
    private void appletNotify(String json, String type, Long userId, Boolean commitFlg) {
        SaveInfo saveInfo = getAppletNotify(type);
        // 如果传入为空，就是insert 否则就是update
        if (saveInfo == null) {
            saveInfo = new SaveInfo();
        }
        saveInfo.setContent(json);
        saveInfo.setType(type);
        saveInfo.setUserId(userId);
        saveInfo.setCommitFlg(commitFlg);
        saveInfo.setCompanyId(UserHelper.getCurBindCompanyId());
        saveInfoDao.save(saveInfo);
    }

    /**
     * 获取临时保存信息
     *
     * @param type
     * @return
     */
    private SaveInfo getAppletNotify(String type) {
        return findSaveInfo(UserHelper.getCurBindCompanyId(), UserHelper.getCurUserId(), type, false);
    }

    /**
     * 获取审批完成之后保存的临时信息
     *
     * @return
     */
    private String getAllAppletNotify() {
        Long curUserId = UserHelper.getCurUserId();
        JSONObject r = new JSONObject();

        BsEntrust bsEntrust = bsEntrustClient.findByWxUserId(curUserId);
        UserDetail userDetail = userDetailDao.findByUserIdAndEnableFlgTrue(curUserId);
        BsCompany company = bsCompanyDao.findByIdAndEnableFlgTrue(userDetail.getCompanyId());

        JSONObject jsonObjectEntrust = JSONUtil.parseObj(bsEntrust);
        JSONObject jsonObject = new JSONObject();
        r.putAll(jsonObjectEntrust);
        jsonObject.set("address", company == null ? "" : company.getAddress());
        jsonObject.set("customMyRole", userDetail.getCustomMyRole());
        jsonObject.set("companyType", company == null ? "" : company.getCompanyType());
        // 法人身份证号
        jsonObject.set("legalRepresentIdentityCardNumber", company == null ? "" : company.getIdentityCardNumber());
        jsonObject.set("companyName", company == null ? "" : company.getCompanyName());
        // 营业执照附件id
        jsonObject.set("businessLicenseUrl", company == null ? "" : company.getFileId() == null ? "" : company.getFileId().split(",")[0]);
        // 营业执照加盖公章附件id
        jsonObject.set("businessLicenseWithSealUrl", company == null ? "" : company.getBusinessLicenseWithSealUrl());
        // 证件类型
        jsonObject.set("cardType", company == null ? "" : company.getCardType());
        // 自定义账期类型
        jsonObject.set("customRepaymentPeriod", userDetail.getCustomRepaymentPeriod());
        jsonObject.set("customQuota", userDetail.getCustomQuota());
        // 法人身份证反面
        jsonObject.set("legalPersonOppositePicUrl", company == null ? "" : company.getCardReverseId());
        jsonObject.set("customCompanySource", userDetail.getCustomCompanySource());
        jsonObject.set("licenseNumber", company == null ? "" : company.getCompanyCreditNo());
        // 法人
        jsonObject.set("legalRepresent", company == null ? "" : company.getLegalRepresent());
        // 法人身份证正面
        jsonObject.set("legalPersonPicUrl", company == null ? "" : company.getCardFrontId());
        jsonObject.set("contactPhone", company == null ? "" : company.getContactPhone());
        jsonObject.set("contactEmail", company == null ? "" : company.getContactEmail());
        jsonObject.set("email", company == null ? "" : company.getEmail());
        // 登录人手机号
        jsonObject.set("phone", UserHelper.getUser().getPhone());
        // 相关联的合伙人或业务员的手机号
        String mobile = getMatchUserMobile(userDetail.getCompanyId());
        jsonObject.set("contactPhone", mobile);
        // 联系人手机号
        // todo 相关联的合伙人或业务员的手机号
        jsonObject.set("contactPhone", UserHelper.getUser().getPhone());
        jsonObject.set("companyName", getCompanyName());
        r.putAll(jsonObject);
        r.remove("enableFlg");
        r.remove("updatedDate");
        r.remove("wxUserId");
        r.remove("createdDate");
        return r.toString();
    }

    /**
     * 相关联的合伙人或业务员的手机号
     *
     * @param companyId
     * @return
     */
    private String getMatchUserMobile(Long companyId) {
        String mobile = "";
        BsCompany company = bsCompanyDao.findByIdAndEnableFlgTrue(companyId);
        if (company == null) {
            return mobile;
        }
        Long matchUserId = company.getMatchUserId();
        SysUserSdk userById = authOpenFacade.findUserById(matchUserId);
        if (userById == null) {
            return mobile;
        }
        mobile = userById.getPhonenumber();
        return mobile;
    }

    /**
     * 获取当前用户绑定公司名
     *
     * @return companyName
     */
    private String getCompanyName() {
        UserDetail userDetail = userDetailDao.findByUserIdAndEnableFlgTrue(UserHelper.getCurUserId());
        // 公司名
        String companyName = "";
        if (userDetail.getCompanyId() != null) {
            BsCompany company = bsCompanyDao.findByIdAndEnableFlgTrue(userDetail.getCompanyId());
            if (company != null) {
                companyName = company.getCompanyName();
            }
        }
        return companyName;
    }

    /**
     * 获取实际保存信息
     *
     * @param type
     * @return
     */
    private SaveInfo getAppletNotifyCommit(String type) {
        return findSaveInfo(UserHelper.getCurBindCompanyId(), UserHelper.getCurUserId(), type, true);
    }

    /**
     * 获取金额和账户信息
     *
     * @param price
     * @return priceAndAccountVo
     */
    private PriceAndAccountVo getPriceAndAccountVo(BigDecimal price) {
        // 从字典中取出账户信息
        return PriceAndAccountVo.builder()
                .price(price)
                .accountName(DictUtil.getValue(Constant.DICT_CODE_ACCOUNT_TYPE, Constant.DICT_CODE_ACCOUNT_NAME))
                .accountNumber(DictUtil.getValue(Constant.DICT_CODE_ACCOUNT_TYPE, Constant.DICT_CODE_ACCOUNT_NUMBER))
                .bank(DictUtil.getValue(Constant.DICT_CODE_ACCOUNT_TYPE, Constant.DICT_CODE_BANK))
                .remark(DictUtil.getValue(Constant.DICT_CODE_ACCOUNT_TYPE, Constant.DICT_CODE_REMARK))
                .build();
    }

    /**
     * 发起委托授权审批
     */
    private void startEntrustFlow(BsCompany company, UserDetail userDetail, SysUserSdk userById) {
        ApplyEntrust entrust = new ApplyEntrust();
        entrust.setApplyUserId(userById.getUserId());
        entrust.setApplyUserName(userById.getUserName());
        entrust.setCompanyName(company.getCompanyName());
        entrust.setCompanyId(company.getId());
        entrust.setWxUserId(UserHelper.getCurUserId());
        entrust.setEnterpriseId(BasConstants.ZG_ENTERPRISE_ID);
        SaveInfo entrustInfo = findSaveInfo(UserHelper.getCurBindCompanyId(), UserHelper.getCurUserId(), SaveInfoType.ENTRUST.getType(), false);
        try {
            Map<String, Object> stringObjectMap = JsonUtil.json2Map(entrustInfo.getContent());
            String powerOfAttorneyFileId = (String) stringObjectMap.get("powerOfAttorneyFileId");
            entrust.setFileId(powerOfAttorneyFileId);
        } catch (Exception e) {
            entrust.setFileId(null);
            logger.error("获取委托授权盖章图片错误");
        }
        entrust.setApproveId(0L);
        applyService.applyEntrust(entrust);
        // 修改状态
        userDetail.setEntrustApplyStatus(Constant.APPLY_STATUS_APPLYING);
        userDetailDao.save(userDetail);
    }

    /**
     * 外部网站发起委托授权审批
     */
    @Override
    public void applyEntrustCms(ApplyEntrust entrust) {
        entrust.setApplySource(ApplySource.CMS.getCode());
        List<BsDictData> listByCategory = BsDictUtil.getListByCategory(BasConstants.DICT_CMS_APPLY_USER);
        if(CollectionUtils.isNotEmpty(listByCategory)) {
            BsDictData bsDictData = listByCategory.get(0);
            entrust.setApplyUserId(Long.valueOf(bsDictData.getDictCd()));
            entrust.setApplyUserName(bsDictData.getDictName());
            entrust.setDeptId(Long.valueOf(bsDictData.getRemark()));
        }
        applyService.applyEntrustCms(entrust);
    }

    /**
     * 发起入金验证审批
     *
     * @param company
     * @param userDetail
     * @param userById
     */
    private void startDepositFlow(BsCompany company, UserDetail userDetail, SysUserSdk userById) {
        ApplyDeposit applyDeposit = new ApplyDeposit();
        applyDeposit.setTargetAmount(userDetail.getTotalDepositPrice());
        applyDeposit.setApplyUserId(userById.getUserId());
        applyDeposit.setApplyUserName(userById.getUserName());
        applyDeposit.setWxUserId(UserHelper.getCurUserId());
        applyDeposit.setCompanyName(company.getCompanyName());
        applyDeposit.setApproveId(0L);
        applyService.applyDeposit(applyDeposit);
        // 修改状态
        userDetail.setDepositStatus(Constant.APPLY_STATUS_APPLYING);
        userDetailDao.save(userDetail);
    }

    @Override
    public AxqUrlVo axqContract(ContractNoRequest contractNoRequest) {
        CtrContract contract = ctrContractClient.findByContractNoV2(contractNoRequest.getContractNo());
        Long companyId = contract.getCompanyId();
        BsCompany company = bsCompanyDao.findByIdAndEnableFlgTrue(companyId);
        if (company == null || !company.getOnLineFlg()) {
            throw new BaseException(Status.ERROR, "该企业还未完成线上化,无法进行线上盖章");
        }
        if (contract == null) {
            throw new BaseException(Status.ERROR, "合同不存在");
        }
        if (contract.getSealFlg()) {
            throw new BaseException(Status.ERROR, "已完成盖章申请,不能重复提交");
        }
        AxqContractVo axqContractVo = this.convertContract(contract);
        axqContractVo.setPhoneNumber(contractNoRequest.getOperatorPhone());
        AxqContractVo res = new AxqContractVo();

        if (BasConstants.CONTRACT_STATUS_B.equals(contract.getContractType())) {
            res = cfcaSignClient.createBuyContract(axqContractVo);
        } else if (BasConstants.CONTRACT_STATUS_S.equals(contract.getContractType())) {
            res = cfcaSignClient.createSellContract(axqContractVo);
        }
        logger.info("res:{}", JsonUtil.obj2Json(res));
        AxqUrlVo axqUrlVo = new AxqUrlVo();
        axqUrlVo.setUrl(res.getShortUrl());
        axqUrlVo.setResCode(res.getRetCode());
        axqUrlVo.setResMessage(res.getRetMessage());
        return axqUrlVo;
    }

    @Override
    @Transactional
    public AxqUrlVo axqGoodReceive(DeliveryOutNoRequest deliveryOutNoRequest) {
        String confirmReceiptDate = deliveryOutNoRequest.getConfirmReceiptDate();
        Date receiptDate = DateOperator.parse(confirmReceiptDate);
        logger.info("confirmReceiptDate:{}",confirmReceiptDate);
        logger.info("receiptDate:{}",receiptDate);
        logger.info("axqGoodReceive:{}", JSONUtil.parseObj(deliveryOutNoRequest));
        RptConfirmReceiptVo confirmReceiptVo = new RptConfirmReceiptVo();
        confirmReceiptVo.setContractNo(deliveryOutNoRequest.getContractNo());
        confirmReceiptVo.setDeliveryId(deliveryOutNoRequest.getDeliveryId());
        confirmReceiptVo.setConfirmReceiptDate(receiptDate);
        List<RptConfirmReceiptDetail> confirmReceiptList = new ArrayList<>();
        RptConfirmReceiptDetail confirmReceiptDetail = new RptConfirmReceiptDetail();
        confirmReceiptDetail.setDeliveryId(deliveryOutNoRequest.getDeliveryId());
        confirmReceiptList.add(confirmReceiptDetail);
        confirmReceiptVo.setConfirmReceiptList(confirmReceiptList);
        ApplyConfirmReceiptVo applyConfirmReceiptVo = contractService.confirmReceipt(confirmReceiptVo);
        logger.info("applyConfirmReceipt:{}", JsonUtil.obj2Json(applyConfirmReceiptVo));
        applyConfirmReceiptVo.setDeliveryOutNo(deliveryOutNoRequest.getDeliveryId());
        AxqContractVo axqContractVo = this.convertGoodReceive(applyConfirmReceiptVo);
        axqContractVo.setMatchUserPhone(deliveryOutNoRequest.getMatchUserPhone());
        axqContractVo.setConfirmReceiptDate(receiptDate);
        //获取司机和车辆信息
        if(StringUtils.isNotBlank(deliveryOutNoRequest.getPlateNumber())){
            axqContractVo.setPlateNumber(deliveryOutNoRequest.getPlateNumber());
        }
        if(StringUtils.isNotBlank(deliveryOutNoRequest.getDriverName())){
            axqContractVo.setDriverName(deliveryOutNoRequest.getDriverName());
        }
        if(StringUtils.isNotBlank(deliveryOutNoRequest.getDriverCardNo())){
            axqContractVo.setDriverCardNo(deliveryOutNoRequest.getDriverCardNo());
        }
        logger.info("convertGoodReceive res:{}", JsonUtil.obj2Json(axqContractVo));
        AxqContractVo res = cfcaSignClient.axqGoodReceive(axqContractVo);
        logger.info("convertGoodReceive resp:{}", JsonUtil.obj2Json(res));
        CtrContract ctr = contractClient.findByContractNoV2(confirmReceiptVo.getContractNo());
        if(confirmReceiptVo.getConfirmReceiptDate()==null){
            ctr.setPreselectionConfirmDate(new Date());
        }else{
            ctr.setPreselectionConfirmDate(receiptDate);
        }
        contractClient.save(ctr);
        AxqUrlVo axqUrlVo = new AxqUrlVo();
        axqUrlVo.setUrl(res.getShortUrl());
        axqUrlVo.setResCode(res.getRetCode());
        axqUrlVo.setResMessage(res.getRetMessage());
        return axqUrlVo;
    }

    @Override
    @Transactional
    public void successContract(String contractNo) throws IOException {
        logger.info("successContract,contractNo:{}", contractNo);
        Map map = null;
        try {
            map = cfcaSignClient.successContract(contractNo);
        } catch (ApplicationException e) {
            e.printStackTrace();
        }
        logger.info("successContract,retMap:{}", map);
        String retCode = (String) map.get("retCode");
        String retMessage = (String) map.get("retMessage");
        if (!"60000000".equals(retCode)) {
            throw new BaseException(Integer.valueOf(retCode), retMessage);
        }
        SignInfo signInfo = new SignInfo();
        signInfo.setContractNo(contractNo);
        List<SignInfo> signInfols = signInfoClient.getSignInfols(signInfo);
        String bizContractNo = signInfols.get(0).getBizContractId();
        SignContract signContract = cfcaSignClient.findByContractNo(contractNo);
        // 合同完成
        if ("1".equals(signContract.getContractState())) {
            CtrContract contract = ctrContractClient.findByContractNoV2(bizContractNo);
            if (StringUtils.equals(BasConstants.CONTRACT_TYPE_S, contract.getContractType())) {
                String companyName = contract.getCompanyName();
                String ourCompanyName = contract.getOurCompanyName();
                boolean sgxSpecialFlg = StringUtils.equals("SGX", ourCompanyName) || StringUtils.equals("苏州高新供应链管理有限公司", ourCompanyName);
                Map contractFileId = cfcaSignClient.getContractFileId(contractNo);
                String fileId = (String) contractFileId.get("fileId");
                contract.setSellContentFileId(fileId);
                List<PmApproveContents> pmApproveContents = pmApproveContentsClient.findByRealApproveId(contract.getApproveId());
                logger.info("successContract pmApproveContents.size:{}", pmApproveContents.size());
                for (PmApproveContents pmApproveContent : pmApproveContents) {
                    logger.info("successContract pmApproveContent:{}", JsonUtil.obj2Json(pmApproveContent));
                    if ("SealUsage".equals(pmApproveContent.getApplyName())) {
                        SealUsage sealUsage = JsonUtil.json2Object(SealUsage.class, pmApproveContent.getContents());
                        logger.info("successContract SealUsage:{}", JsonUtil.obj2Json(sealUsage));
                        logger.info("companyName:{}, sgxSpecialFlg:{}", ourCompanyName, sgxSpecialFlg);
                        String businessType = sealUsage.getBusinessType();
                        if (!sgxSpecialFlg && StringUtils.equals(BasConstants.CONTRACT_TYPE_S, businessType)) {
                            pmApproveContent.setFileId(fileId + ",");
                            pmApproveContent.setStatus("D");
                            pmApproveContentsClient.save(pmApproveContent);
                            // 完成盖章审批
                            PmApproveStepFlowVo vo = new PmApproveStepFlowVo();
                            vo.setComplete(true);
                            vo.setApproveId(pmApproveContent.getApproveId());
                            vo.setApproveStepId(0L);
                            vo.setApproveOpinion(BasConstants.APPROVE_OPINION_AGREE);
                            vo.setApproveUserId(999999L);
                            vo.setApproveUserName("anxinsign");
                            vo.setApproveRemark("客户小程序签署");
                            approveClient.doStepFlow(vo);
                        }
                        if (sgxSpecialFlg && StringUtils.equals(BasConstants.CONTRACT_TYPE_S, businessType)){
                            pmApproveContent.setFileId(fileId + ",");
                            pmApproveContentsClient.save(pmApproveContent);

                            SealUsageDcsxVo vo = new SealUsageDcsxVo();
                            vo.setApproveId(pmApproveContent.getApproveId());
                            vo.setFileId(fileId + ",");
                            vo.setContractNo(contractNo);
                            vo.setCompanyName(companyName);
                            vo.setOurCompanyName(ourCompanyName);
                            sealUsageDCSXClient.updateCfcaContractNo(vo);
                        }
                    }
                }
                // 完成盖章申请
                contract.setSealFlg(!sgxSpecialFlg);
                contract.setContractStatus(BasConstants.CONTRACTSTATUS_S);
                if (StrUtil.isEmpty(contract.getSettlementType())) {
                    contract.setContractStatusWx(BasConstants.CONTRACT_STATUS_P);
                }else {
                    contract.setContractStatusWx(BasConstants.CONTRACT_STATUS_W);
                }
                ctrContractClient.save(contract);
            }
        }
    }

    @Override
    public void successGoodReceive(String contractNo) throws ApplicationException, IOException {
        logger.info("successGoodReceive,contractNo:{}", contractNo);
        Map map = null;
        try {

            map = cfcaSignClient.successGoodReceive(contractNo);
        } catch (ApplicationException e) {
            e.printStackTrace();
        }
        logger.info("successContract,retMap:{}", map);
        String retCode = (String) map.get("retCode");
        String retMessage = (String) map.get("retMessage");
        if (!"60000000".equals(retCode)) {
            throw new BaseException(Integer.valueOf(retCode), retMessage);
        }
        SignInfo signInfo = new SignInfo();
        signInfo.setContractNo(contractNo);
        List<SignInfo> signInfols = signInfoClient.getSignInfols(signInfo);
        //
        String bizContractNo = signInfols.get(0).getBizContractId();
        logger.info("bizContractNo :{}", bizContractNo);
        SignContract signContract = cfcaSignClient.findByContractNo(contractNo);
        logger.info("signInfo:{}", JsonUtil.obj2Json(signInfols));
        // 合同完成
        if ("1".equals(signContract.getContractState())) {
            ApplyDeliveryOut deliveryOut = applyDeliveryOutClient.findByApplyNo(bizContractNo);
            RptConfirmReceiptVo confirmReceiptVo = new RptConfirmReceiptVo();
            confirmReceiptVo.setContractNo(deliveryOut.getContractNo());
            confirmReceiptVo.setDeliveryId(bizContractNo);
            List<RptConfirmReceiptDetail> confirmReceiptList = new ArrayList<>();
            RptConfirmReceiptDetail confirmReceiptDetail = new RptConfirmReceiptDetail();
            confirmReceiptDetail.setDeliveryId(bizContractNo);
            Map contractFileId = cfcaSignClient.getContractFileId(contractNo);
            String fileId = (String) contractFileId.get("fileId");
            confirmReceiptDetail.setFileId(fileId);
            confirmReceiptList.add(confirmReceiptDetail);
            confirmReceiptVo.setConfirmReceiptList(confirmReceiptList);
            contractService.confirmReceiptV2(confirmReceiptVo);
        }
    }

    /**
     * 应收账款债权-签署成功回调接口
     * @param contractNo
     * @throws ApplicationException
     * @throws IOException
     */
    @Override
    @ServiceTransactional
    public void successDebtCertificate(String contractNo) throws IOException {
        logger.info("successDebtCertificate,contractNo:{}", contractNo);
        Map map = null;
        try {
            map = cfcaSignClient.successContract(contractNo);
        } catch (ApplicationException e) {
            e.printStackTrace();
        }
        logger.info("successContract,retMap:{}", map);
        String retCode = (String) map.get("retCode");
        String retMessage = (String) map.get("retMessage");
        if (!"60000000".equals(retCode)) {
            throw new BaseException(Integer.valueOf(retCode), retMessage);
        }
        SignContract signContract = cfcaSignClient.findByContractNo(contractNo);
        String bizContractNo = signContract.getAttachmentName();
        // 应收账款债权合同附件赋值
        if ("1".equals(signContract.getContractState())) {
            CtrContract ctrContract = ctrContractClient.findByContractNoV2(bizContractNo);
            if (StringUtils.equals(BasConstants.CONTRACTTYPE_SELL, ctrContract.getContractType())) {
                Map contractFileId = cfcaSignClient.getContractFileId(contractNo);
                String fileId = (String) contractFileId.get("fileId");
                if (StringUtils.isNotBlank(fileId)) {
                    ctrContract.setDebtCertificateFileId(fileId + ",");
                    ctrContractClient.save(ctrContract);
                    ctrContractClient.refreshFactorStatus(ctrContract.getId());

                    // 债权凭证签署完成后自动发起已审批完成的签署保理申请单
                    successContractService.autoStartFactorSign(ctrContract);
                }

                // 自动发起开票申请
//                applyInvoiceClient.autoInitiatedInvoice(ctrContract.getId());
            }
        }
    }


    /**
     *     转换applyConfirmReceiptVo->AxqContractVo
     */
    private AxqContractVo convertGoodReceive(ApplyConfirmReceiptVo applyConfirmReceiptVo) {
        AxqContractVo axqContractVo = new AxqContractVo();
        CtrContract contract = ctrContractClient.findByContractNoV2(applyConfirmReceiptVo.getContractNo());
        if (Objects.isNull(contract)){
            throw new BaseException(Status.ERROR, "合同信息不存在!");
        }
        // 匹配签收单模板
        String ourCompanyName = contract.getOurCompanyName();
        logger.info("签收单抬头:{}",ourCompanyName);
        String templateCd = BsDictUtil.getValue(contract.getEnterpriseId(), BasConstants.GOOD_RECEIVE_TEMPLATE, contract.getOurCompanyName());
        if (StringUtils.isBlank(templateCd)){
            throw new BaseException(Status.ERROR, "签收单模板缺失!");
        }

        axqContractVo.setTemplateId(templateCd);// 必填
        axqContractVo.setBuyerCompanyName(applyConfirmReceiptVo.getCompanyName());
        axqContractVo.setContractNo(applyConfirmReceiptVo.getContractNo());
        axqContractVo.setBizContractId(applyConfirmReceiptVo.getDeliveryOutNo());

        logger.info("applyConfirmReceiptVo:{}", JsonUtil.obj2Json(applyConfirmReceiptVo));

        List<ApplyProductDetail> lstInsert = applyConfirmReceiptVo.getLstInsert();

        if (lstInsert == null || lstInsert.isEmpty()) {
            throw new BaseException(Status.APPLY_FAIL, "商品内容为空");
        }
        ApplyProductDetail applyProductDetail = lstInsert.get(0);

        List<CtrProductVo> ctrProductVos = new ArrayList<>();
        CtrProductVo ctrVo = new CtrProductVo();
        ctrVo.setBrandNumber(applyProductDetail.getBrandNumber());
        ctrVo.setDealNumber(applyProductDetail.getDealNumber() + "");
        ctrVo.setDealPrice(applyProductDetail.getDealPrice() + "");
        ctrVo.setFactoryName(contract.getCompanyName());
        ctrVo.setProductName(applyProductDetail.getProductName());
        ctrVo.setTotalPrice(contract.getTotalAmount() + "");

        axqContractVo.setConfirmReceiptDate(applyConfirmReceiptVo.getConfirmReceiptDate());
        axqContractVo.setConfrimNumber(applyProductDetail.getCurNumber() + "");
        axqContractVo.setProductName(applyProductDetail.getProductName());
        List<ApplyDeliveryOut> applyDeliveryOuts = applyConfirmReceiptVo.getApplyDeliveryOuts();
        ApplyDeliveryOut applyDeliveryOut = applyDeliveryOuts.get(0);

        //产地
        axqContractVo.setProductPlace(applyProductDetail.getFactoryName());
        //规格
        axqContractVo.setPackageSpec(applyProductDetail.getBrandNumber());
        //单位
        axqContractVo.setNumberUnit(applyProductDetail.getNumberUnit());
        //数量
        axqContractVo.setDealNumber(String.valueOf(applyProductDetail.getDealNumber()));
        //送货单号
        axqContractVo.setApplyNo(applyDeliveryOut.getApplyNo());
        //车号
        axqContractVo.setPlateNumber(applyDeliveryOut.getPlateNumber());
        //司机姓名
        axqContractVo.setDriverName(applyDeliveryOut.getDriverName());
        //身份证号
        axqContractVo.setDriverCardNo(applyDeliveryOut.getDriverCardNo());
        //配送地址
         axqContractVo.setDeliveryAddr(applyDeliveryOut.getDeliveryAddr());
        //联系人
         axqContractVo.setContactName(applyDeliveryOut.getContactName());
        //联系人电话
        axqContractVo.setContactPhone(applyDeliveryOut.getContactPhone());
        //到货日
        axqContractVo.setMemo(applyDeliveryOut.getRemark());
        axqContractVo.setPriceTotalAll(String.valueOf(contract.getTotalAmount().setScale(2, BigDecimal.ROUND_HALF_UP)));
        //  s.setAppCode(PropertiesUtil.getProperty(OfferConstants.PROJECT_CODE));
        ctrProductVos.add(ctrVo);
        axqContractVo.setProductList(ctrProductVos);
        axqContractVo.setContractId(axqContractVo.getContractNo());
        mustAxq(axqContractVo);
        return axqContractVo;

    }

    /**
     * 转换ctrContract->AxqContractVo
     *
     * @param contract
     * @return
     */
    private AxqContractVo convertContract(CtrContract contract) {

        AxqContractVo axqContractVo = new AxqContractVo();
        ApplyMatch applyMatch = applyMatchClient.findByApproveId(contract.getApproveId());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
        BeanUtils.copyProperties(applyMatch, axqContractVo);
        List<CtrProductVo> ctrProductVos = new ArrayList<>();
        CtrProductVo ctrVo = new CtrProductVo();
        ctrVo.setBrandNumber(applyMatch.getBrandNumber());
        ctrVo.setDealNumber(contract.getTotalNumber() + "");
        ctrVo.setDealPrice(contract.getDealPrice() + "");
        ctrVo.setFactoryName(contract.getCompanyName());
        ctrVo.setProductName(applyMatch.getProductName());
        ctrVo.setTotalPrice(contract.getTotalAmount() + "");
        String value = DictUtil.getValue(BasConstants.DICT_TYPE_PACKINGSPECIFICATEXT, applyMatch.getWrapSpecs());
        ctrVo.setWrapSpecs(value);
        // 自定义的合同code

        BsContractTemplate template=new BsContractTemplate();
        template.setEnterpriseId(BasConstants.ZG_ENTERPRISE_ID);
        template.setId(contract.getBsTemplateContractId());
        BsContractTemplate byIdAndEnterprise = bsContractTemplateClient.findByIdAndEnterpriseId(template);

        String templateTag = byIdAndEnterprise.getTemplateTag();
        if(StringUtils.equals(BasConstants.TEMPLATETAG_SELL_DCSX_CONTRACT,templateTag)){
            String contractModel = contract.getContractModel();
            BigDecimal bondAmount = contract.getBondAmount();
            // 赊销合同模板特殊处理
            if(bondAmount != null && bondAmount.compareTo(BigDecimal.ZERO) > 0) {
                if(StringUtils.equals(BasConstants.CONTRACT_MODEL_HDFK,contractModel) || StringUtils.equals(BasConstants.CONTRACT_MODEL_DCSXHDFK,contractModel)) {
                    templateTag = BasConstants.TEMPLATETAG_SELL_DCSX_CONTRACT_HDFK;
                } else {
                    templateTag = BasConstants.TEMPLATETAG_SELL_DCSX_CONTRACT;
                }
            } else {
                if(StringUtils.equals(BasConstants.CONTRACT_MODEL_HDFK,contractModel) || StringUtils.equals(BasConstants.CONTRACT_MODEL_DCSXHDFK,contractModel)) {
                    templateTag = BasConstants.TEMPLATETAG_SELL_DCSX_CONTRACT_ALL_HDFK;
                } else {
                    templateTag = BasConstants.TEMPLATETAG_SELL_DCSX_CONTRACT_ALL;
                }
            }
        } else if (StringUtils.equals(BasConstants.TEMPLATETAG_SELL_DCSX_CONTRACT_SUGX,templateTag)) {
            String contractModel = contract.getContractModel();
            BigDecimal bondAmount = contract.getBondAmount();
            // 赊销合同模板特殊处理
            if(bondAmount != null && bondAmount.compareTo(BigDecimal.ZERO) > 0) {
                if(StringUtils.equals(BasConstants.CONTRACT_MODEL_HDFK,contractModel) || StringUtils.equals(BasConstants.CONTRACT_MODEL_DCSXHDFK,contractModel)) {
                    templateTag = BasConstants.TEMPLATETAG_SELL_DCSX_CONTRACT_SUGX_HDFK;
                } else {
                    templateTag = BasConstants.TEMPLATETAG_SELL_DCSX_CONTRACT_SUGX;
                }
            } else {
                if(StringUtils.equals(BasConstants.CONTRACT_MODEL_HDFK,contractModel) || StringUtils.equals(BasConstants.CONTRACT_MODEL_DCSXHDFK,contractModel)) {
                    templateTag = BasConstants.TEMPLATETAG_SELL_DCSX_CONTRACT_SUGX_ALL_HDFK;
                } else {
                    templateTag = BasConstants.TEMPLATETAG_SELL_DCSX_CONTRACT_SUGX_ALL;
                }
            }
        }
        if(StringUtils.equals(BasConstants.TEMPLATETAG_SELL_DC_CONTRACT_TEMPLATE,templateTag)){
            BigDecimal bondAmount = contract.getBondAmount();
            // 代采合同模板特殊处理
            if(bondAmount != null && bondAmount.compareTo(BigDecimal.ZERO) > 0) {
                templateTag = BasConstants.TEMPLATETAG_SELL_DC_CONTRACT_TEMPLATE;
            } else {
                templateTag = BasConstants.TEMPLATETAG_SELL_DC_CONTRACT_TEMPLATE_ALL;
            }
        }
        axqContractVo.setExtraTerm(StringUtils.isBlank(contract.getExtraTerm()) ? "无" : contract.getExtraTerm());
        axqContractVo.setTemplateId(templateTag);// 必填
        axqContractVo.setDeliAddr(applyMatch.getShippingAddr());
        axqContractVo.setQualityStandardStr(DictUtil.getValue(BasConstants.DICT_QUALITYSTANDARDTEXT, applyMatch.getQualityStandard()));
        if (StringUtils.equals(BasConstants.TEMPLATETAG_SELL_DC_TP_CONTRACT_SUGX, templateTag)) {
            axqContractVo.setQualityStandardStr(DictUtil.getValue(BasConstants.DICT_TYPE_QUALITYSTANDDARD, applyMatch.getQualityStandard()));
        }

        axqContractVo.setPriceTotalAll(String.valueOf(contract.getTotalAmount().setScale(2, BigDecimal.ROUND_HALF_UP)));
        //  s.setAppCode(PropertiesUtil.getProperty(OfferConstants.PROJECT_CODE));
        ctrProductVos.add(ctrVo);
        axqContractVo.setProductList(ctrProductVos);
        axqContractVo.setContractId(axqContractVo.getContractNo());

        mustAxq(axqContractVo);

//        String payMode = contract.getPayType();
//        if (StringUtils.isNotBlank(payMode)) {
//            axqContractVo.setPayMode(payMode);
//        } else {
//            axqContractVo.setPayMode("");
//        }
        if (BasConstants.CONTRACT_TYPE_B.equals(contract.getContractType())) {

            axqContractVo.setOurCompanyName(contract.getCompanyName());//必填 签约的企业必须在安心签开户过
        } else {

            axqContractVo.setOurCompanyName(contract.getOurCompanyName());//必填 签约的企业必须在安心签开户过
        }

       // axqContractVo.setUnitDealPrice(contract.getDealPrice().toString());
       // axqContractVo.setPackageSpec(DictUtil.getValue(BasConstants.DICT_TYPE_PACKINGSPECIFICATEXT, applyMatch.getWrapSpecs()));
        axqContractVo.setContractTimeStr(sdf.format(contract.getContractTime()));

        //String toChineseMethod = RmbUtil.number2Chinese(contract.getTotalAmount());
      //  axqContractVo.setTotalAmountCn(toChineseMethod);
        /******************************************************************************************/

        String addres = "";
        String fax = "";
        String person = "";
        String phone = "";
        String companyname = "";
//        List<BsDictData> bsDictList = BsDictUtil.getListByCategory(contract.getEnterpriseId(), BasConstants.DICT_TYPE_OURCOMPANY);
        String taxNo="";
//        String bankName="";
//        String cardId="";
        String ourBankName="";
        String ourBankAccount="";
        String ourCompanyEmail = "";
        String ourCompanyContact = "";
        String ourCompanyPhone = "";
        BsCompanyOurSearchVo companyOurSearchVo = new BsCompanyOurSearchVo();
        companyOurSearchVo.setCompanyName(contract.getOurCompanyName());
        BsCompanyOur companyOur = bsCompanyOurClient.getCompanyOurDetail(companyOurSearchVo);
        if(Objects.nonNull(companyOur)){
            ourBankName = StringUtils.isBlank(companyOur.getCompanyBankName()) ? "" : companyOur.getCompanyBankName();
            ourBankAccount = StringUtils.isBlank(companyOur.getCompanyCardId()) ? "" : companyOur.getCompanyCardId();
            addres = StringUtils.isBlank(companyOur.getAddress()) ? "" : companyOur.getAddress();
            ourCompanyEmail = StringUtils.isBlank(companyOur.getEmail()) ? "" : companyOur.getEmail();
            fax = StringUtils.isBlank(companyOur.getCompanyFax()) ? "" : companyOur.getCompanyFax();
            person = StringUtils.isBlank(companyOur.getCompanyPerson()) ? "" : companyOur.getCompanyPerson();
            phone = StringUtils.isBlank(companyOur.getCompanyPhone()) ? "" : companyOur.getCompanyPhone();
            companyname = StringUtils.isBlank(companyOur.getCompanyName()) ? "" : companyOur.getCompanyName();
            taxNo = StringUtils.isBlank(companyOur.getCompanyTaxNo()) ? "" : companyOur.getCompanyTaxNo();
            axqContractVo.setSignAddress(StringUtils.isBlank(companyOur.getSigningAddr()) ? "" : companyOur.getSigningAddr());
            ourCompanyContact = StringUtils.isBlank(companyOur.getCompanyContact()) ? "" : companyOur.getCompanyContact();
            ourCompanyPhone = StringUtils.isBlank(companyOur.getCompanyPhone()) ? "" : companyOur.getCompanyPhone();
        } else {
            BsCompanyDcsx bsCompanyDcsx = bsCompanyDcsxClient.findByCompanyName(contract.getOurCompanyName());
            if (bsCompanyDcsx!=null) {
                String companyPerson = bsCompanyDcsx.getCompanyPerson();
                String companyContact = bsCompanyDcsx.getCompanyContact();
                String companyFax = bsCompanyDcsx.getCompanyFax();
                String companyPhone =bsCompanyDcsx.getCompanyPhone();
                String ourCompanyAddres =bsCompanyDcsx.getSigningAddr();

                ourBankName = StringUtils.isBlank(bsCompanyDcsx.getCompanyBankName()) ? "" : bsCompanyDcsx.getCompanyBankName();
                ourBankAccount = StringUtils.isBlank(bsCompanyDcsx.getCompanyCardId()) ? "" : bsCompanyDcsx.getCompanyCardId();
                addres = StringUtils.isBlank(bsCompanyDcsx.getAddress()) ? "" : bsCompanyDcsx.getAddress();
                fax = StringUtils.isBlank(companyFax) ? "" : companyFax;
                person = StringUtils.isBlank(companyPerson) ? "" : companyPerson;
                phone = StringUtils.isBlank(companyPhone) ? "" : companyPhone;
                companyname = StringUtils.isBlank(contract.getOurCompanyName()) ? "" : contract.getOurCompanyName();
                taxNo = StringUtils.isBlank(bsCompanyDcsx.getCompanyTaxNo()) ? "" : bsCompanyDcsx.getCompanyTaxNo();
                axqContractVo.setSignAddress(StringUtils.isBlank(bsCompanyDcsx.getSigningAddr()) ? "" : bsCompanyDcsx.getSigningAddr());
                ourCompanyContact = StringUtils.isBlank(companyContact) ? "" : companyContact;
                ourCompanyPhone = StringUtils.isBlank(companyPhone) ? "" : companyPhone;
            }
        }
        
        String  payMode=DictUtil.getValue(BasConstants.PAY_MODE, contract.getPayType());
//        for (BsDictData dict : bsDictList) {
//            if (StringUtils.equals(contract.getOurCompanyName(), dict.getDictName())) {
//                String dictCd = dict.getDictCd();
//                addres = DictUtil.getValue(BasConstants.DICT_TYPE_OURCOMPANY_ADDRES, dictCd);
//                fax = DictUtil.getValue(BasConstants.DICT_TYPE_OURCOMPANY_FAX, dictCd);
//                person = DictUtil.getValue(BasConstants.DICT_TYPE_OURCOMPANY_PERSON, dictCd);
//                phone = DictUtil.getValue(BasConstants.DICT_TYPE_OURCOMPANY_PHONE, dictCd);
//                companyname = DictUtil.getValue(BasConstants.DICT_TYPE_OURCOMPANY, dictCd);
//
//                taxNo = DictUtil.getValue(BasConstants.DICT_TYPE_OURCOMPANY_TAXNO, dictCd);
//                bankName = DictUtil.getValue(BasConstants.DICT_TYPE_OURCOMPANY_BANKNAME, dictCd);
//                cardId = DictUtil.getValue(BasConstants.DICT_TYPE_OURCOMPANY_CARDID, dictCd);
//
//
//            }
//        }

        String rates = BsDictUtil.getValue(BasConstants.ZG_ENTERPRISE_ID, BasConstants.DICT_TYPE_TAX_RATES, BasConstants.DICT_TYPE_TAX_RATES_SL);
        // 1.13
        BigDecimal taxRates = BigDecimal.ONE.add(new BigDecimal(rates));
        // 含税单价
        BigDecimal dealPrice = contract.getDealPrice();
        // 含税总价
        BigDecimal totalAmount = contract.getTotalAmount();
        axqContractVo.setSumDealNumber(contract.getTotalNumber().toString());
        axqContractVo.setSumTaxPriceNoTax(dealPrice.divide(taxRates,2, BigDecimal.ROUND_HALF_UP).toString());
        axqContractVo.setSumTotalPriceNoTax(totalAmount.divide(taxRates,2, BigDecimal.ROUND_HALF_UP).toString());
        axqContractVo.setSumDealPrice(dealPrice.toString());
        axqContractVo.setSumTotalPrice(totalAmount.toString());
        axqContractVo.setTotalAmountStr(RmbUtil.number2Chinese(totalAmount));
        
        axqContractVo.setDealPriceNoTax(dealPrice.divide(taxRates,2, BigDecimal.ROUND_HALF_UP).toString());
        axqContractVo.setTotalPriceNoTax(totalAmount.divide(taxRates,2, BigDecimal.ROUND_HALF_UP).toString());

        DecimalFormat df = new DecimalFormat("0%");
        BigDecimal bondRate =  contract.getBondRate();
        if(bondRate == null) {
            axqContractVo.setBondRateStr("");
        } else {
            axqContractVo.setBondRateStr(df.format(bondRate));
        }
        BigDecimal bondAmount = contract.getBondAmount();
        if(bondRate == null) {
            axqContractVo.setBondRateAmountStr("");
        } else {
            axqContractVo.setBondRateAmountStr(bondAmount.toString());
        }


        BsCompany company = bsCompanyClient.getEntity(contract.getCompanyId());
        List<BsCompanyAccount> accountList = bsCompanyAccountClient.findByCompanyId(company.getId());
        if(CollectionUtils.isNotEmpty(accountList)) {
            BsCompanyAccount account = null;
            for (BsCompanyAccount companyAccount : accountList) {
                if(Boolean.TRUE.equals(companyAccount.getDefaultFlg())){
                    account = companyAccount;
                }
            }
            if(Objects.isNull(account)){
                account = accountList.get(0);
            }
            axqContractVo.setBankName(StringUtils.isBlank(account.getBankName()) ? "" : account.getBankName());
            axqContractVo.setBankAccount(StringUtils.isBlank(account.getBankAccount()) ? "" : account.getBankAccount());
        } else {
            axqContractVo.setBankName(StringUtils.isBlank(company.getBankName()) ? "" : company.getBankName());
            axqContractVo.setBankAccount(StringUtils.isBlank(company.getBankAccount()) ? "" : company.getBankAccount());
        }

        

        // H-票汇，Z-支票，T-电汇，D-信用证
        if(payMode==null||payMode==""){
            axqContractVo.setPayMode("电汇");
        }else{
            axqContractVo.setPayMode(payMode);
        }

//        if("SX".equals(contract.getDeliveryMode())){
//            axqContractVo.setDeliveryMode("赊销");
//            axqContractVo.setPayMode("赊销");
//        }
//        if("XKHH".equals(contract.getDeliveryMode())){
//            axqContractVo.setDeliveryMode("先款后货");
//            axqContractVo.setPayMode("先款后货");
//        }
//        if("XHHK".equals(contract.getDeliveryMode())){
//            axqContractVo.setDeliveryMode("先货后款");
//            axqContractVo.setPayMode("先货后款");
//        }

        axqContractVo.setDealNumber(contract.getTotalNumber().toString());
        BsCompany byCompanyName = bsCompanyClient.findByCompanyName(contract.getCompanyName());
        //----------------左
        axqContractVo.setOurCompanyAddres(addres);
        axqContractVo.setOurCompanyPerson(person);
        axqContractVo.setMatchUserName(companyname);
        axqContractVo.setOurCompanyEmail(ourCompanyEmail);
        axqContractVo.setOurCompanyFax(fax);
        axqContractVo.setMatchUserPhone(phone);
        axqContractVo.setOurBankName(ourBankName);
        axqContractVo.setOurBankAccount(ourBankAccount);
        axqContractVo.setOurCompanyContact(ourCompanyContact);
        axqContractVo.setOurCompanyPhone(ourCompanyPhone);
         //---------------右
        axqContractVo.setCompanyName(byCompanyName.getCompanyName());
        axqContractVo.setContactAddr(byCompanyName.getAddress());
        axqContractVo.setCompanyPerson(byCompanyName.getLegalRepresent());
        axqContractVo.setContactName(byCompanyName.getContactName());
        axqContractVo.setCompanyFax(byCompanyName.getCompanyFax());
        axqContractVo.setEmail(byCompanyName.getEmail());
        axqContractVo.setContactPhone(byCompanyName.getCompanyPhone());
        axqContractVo.setDeliveryAddr(addres);

        // 奥顺宇特殊处理
        boolean specialFlg = ctrContractClient.judgeUseSpecialBankContractId(contract.getId());
        if (Boolean.TRUE.equals(specialFlg)) {
            BsBankVo specialBank = applyChargeSalesClient.getSpecialBank(BasConstants.ZG_ENTERPRISE_ID);
            if (Objects.nonNull(specialBank)) {
                axqContractVo.setOurBankName(StringUtils.isBlank(specialBank.getBankName()) ? "" : specialBank.getBankName());
                axqContractVo.setOurBankAccount(StringUtils.isBlank(specialBank.getBankNum()) ? "" : specialBank.getBankNum());

            }
        }

/*********************************2*************************************/
//        axqContractVo.setSellTaxNo(taxNo);
//        axqContractVo.setBuyerTaxNo(byCompanyName.getTaxNo());
//        axqContractVo.setSellerBankName(bankName);
//        axqContractVo.setBankName(byCompanyName.getBankName());
//
//        axqContractVo.setSellerCardId(cardId);
//        axqContractVo.setCardId(byCompanyName.getBankAccount());
//
//        axqContractVo.setDeliveryPlaceName(contract.getDeliveryAddr());
        // 交货日期
       axqContractVo.setDeliveryDateStr(sdf.format(contract.getDeliveryDateTo()));
//        if(contract.getDeliveryType().equals(BasConstants.DICT_TYPE_BUYDELIVERY_Z)){
//            axqContractVo.setDeliveryTypeName(BsDictUtil.getValue(BasConstants.ZG_ENTERPRISE_ID,BasConstants.DICT_TYPE_BUYDELIVERY, BasConstants.DICT_TYPE_BUYDELIVERY_Z));
//        }else{
//            axqContractVo.setDeliveryTypeName(BsDictUtil.getValue(BasConstants.ZG_ENTERPRISE_ID, BasConstants.DICT_TYPE_BUYDELIVERY, BasConstants.DICT_TYPE_BUYDELIVERY_P));
//        }
/******************************************************************************************/

        axqContractVo.setBizContractId(contract.getContractNo());
        // 质量标准
        axqContractVo.setProductQualityName(
                DictUtil.getValue(BasConstants.DICT_TYPE_QUALITYSTANDDARD, applyMatch.getQualityStandard()));
        //交货方式deliveryTypeStr
        axqContractVo.setDeliveryTypeStr(
                midstreamClient.generateRespStr(contract.getOurCompanyName(), DictUtil.getValue(BasConstants.DICT_TYPE_BUYDELIVERY, contract.getDeliveryType())));


      //  axqContractVo.setContractNo(contract.getContractNo());

        //        axqContractVo.setDealAmountCn(contract.getTotalAmount().toString());
//
//        axqContractVo.setPriceTotal(String.valueOf(contract.getTotalAmount().setScale(2, BigDecimal.ROUND_HALF_UP)));
//
        axqContractVo.setPayRemaindTime(sdf.format(contract.getPayFullTime()));
        if(contract.getPayBondTime() != null) {
            axqContractVo.setPayBondDateStr(sdf.format(contract.getPayBondTime()));
        } else {
            axqContractVo.setPayBondDateStr("");
        }
        
//--------------------------------------------------------------
//        // 发送安心签
//        AxqContractVo resObj = cfcaSignClient.createSellContract(axqContractVo);

//        if (StringUtils.isNotBlank(fileId)) {
//            // 更新合同表合同附件
//            contractDao.updateBuyContentFileId(contract.getId(), fileId);
//        }
//        if(axqContractVo.getOurCompanyName().equals("中光亿云供应链管理有限公司")||axqContractVo.getOurCompanyName().equals("青岛中光亿云供应链管理有限公司")||axqContractVo.getOurCompanyName().equals("上海中光亿云供应链管理有限公司")){
//            axqContractVo.setSignAddress(BsDictUtil.getValue(BasConstants.ZG_ENTERPRISE_ID,BasConstants.DICT_SIGNADDRESS, "zg"));
//        }
//        if(axqContractVo.getOurCompanyName().equals("浙江网塑科技股份有限公司")||axqContractVo.getOurCompanyName().equals("网塑（宁波）化工有限公司")){
//            axqContractVo.setSignAddress(BsDictUtil.getValue(BasConstants.ZG_ENTERPRISE_ID,BasConstants.DICT_SIGNADDRESS, "ws"));
//        }
        if (contract.getBusinessTypeDcsx()!=null && contract.getBusinessTypeDcsx().equals("BL")){
            axqContractVo.setAdditionalAgreement(BsDictUtil.getValue(BasConstants.ZG_ENTERPRISE_ID,BasConstants.DICT_ADDITONALAGEREEMENT, "bctk"));
        }
        axqContractVo.setDeliAddr(contract.getDeliveryAddr());
        return axqContractVo;
    }

    /**
     * 安心签必填字段
     *
     * @param s
     * @return
     */
    private AxqContractVo mustAxq(AxqContractVo s) {

        s.setAppCode(BasConstants.APP_CODE);// 必填
        // 默认
        s.setBuyerSignType(BasConstants.CONTRACT_SEAL_TYPE_UK_SIGN);// 必填 默认
        s.setSellerSignType(BasConstants.CONTRACT_SEAL_TYPE_UK_SIGN);// 必填 默认
        s.setBuyerIsCheckProjectCode(BasConstants.CONTRACT_SEAL_NOT_SEND_PWD);// 必填
        // 默认
        s.setSellerIsCheckProjectCode(BasConstants.CONTRACT_SEAL_NOT_SEND_PWD);// 必填
        // 默认
        s.setSignLocation(BasConstants.CONTRACT_SIGN_LOCATION);// 必填 默认
        s.setBuyerSignLocation(BasConstants.CONTRACT_BUYER_SIGN_LOCATION);// 必填
        // 默认
        s.setSellerSignLocation(BasConstants.CONTRACT_SELLER_SIGN_LOCATION);// 必填
        // 默认
        s.setBuyerLocation("10.2.2.3");
        s.setSellerLocation("11.22.33.66");

        return s;
    }

    /**
     * 获取活体认证人脸识别
     * @param authFaceRecognition
     * @return
     */
    @Override
    @Transactional
    public Map authFaceRecognition(AuthFaceRecognition authFaceRecognition) {
        Map<String, Object> resultMap = new HashMap<>();
        BsDictData bsDictData = bsDictDataDao.loadDictDataByCd(BasConstants.CONFIG_FLG_SWITCH, "authFaceRecognition", BasConstants.ZG_ENTERPRISE_ID);
        logger.info("authFaceRecognition config:{}", bsDictData);
        if (Objects.nonNull(bsDictData) && !StringUtils.equalsIgnoreCase("1", bsDictData.getDictName())) {
            resultMap.put("code", "0");
            resultMap.put("info", "0.8");
        } else {
            JinXinAuthFaceVo authentication = jinXinApi.authenticationNew(authFaceRecognition);
            SyncData syncData = authentication.getSyncData();
            syncDataClient.save(syncData);
            String platformCode = authentication.getPlatformCode();
            resultMap.put("code", StringUtils.equals("0", platformCode) ? "0" : platformCode);
            resultMap.put("info", authentication.getPlatformDesc());
        }
        return resultMap;
    }


    /**
     * cfca用户开户合并接口（绑定企业申请）
     * @param request
     */
    @Override
    public void ApplyWxCfca(CfcaRequest request) {
        logger.info("===onlineAccountOpening:{}", JsonUtil.obj2Json(request));
        SaveInfo before;
        before = findSaveInfo(UserHelper.getCurBindCompanyId(), UserHelper.getCurUserId(), SaveInfoType.BASE_INFO.getType(), false);
        if (before == null) {
            before = new SaveInfo();
        }
        String json = JsonUtil.obj2Json(request);
        before.setContent(json);
        before.setType(SaveInfoType.BASE_INFO.getType());
        before.setUserId(UserHelper.getCurUserId());
        before.setCommitFlg(false);

        // 发起资料申请
        UserDetail userDetail = userDetailDao.findByUserIdAndEnableFlgTrue(UserHelper.getCurUserId());
        if (userDetail.getCompanyId() == null) {
            throw new BaseException(Status.ERROR, "该用户还未绑定公司");
        }
        BsCompany company = bsCompanyDao.findByIdAndEnableFlgTrue(userDetail.getCompanyId());
        if (company == null) {
            throw new BaseException(Status.ERROR, "该企业不存在或已删除");
        }
        if (company.getMatchUserId() == null) {
            throw new BaseException(Status.ERROR, "该企业还未指定业务员,请联系管理员！");
        }
        SysUserSdk userById = authOpenFacade.findUserById(company.getMatchUserId());

        // 判断companyApplyStatus、depositStatus、entrustStatus 如果是0或3 正常发起
        // 先判断companyApplyStatus
        ApplyWxCfca applyWxCfca = new ApplyWxCfca();
        // 发起公司信息审批
        applyWxCfca.setApplyUserId(company.getMatchUserId());
        applyWxCfca.setApplyUserName(userById.getUserName());

        applyWxCfca.setWxUserId(UserHelper.getCurUserId());
        applyWxCfca.setApproveId(0L);
        applyWxCfca.setEnterpriseId(BasConstants.ZG_ENTERPRISE_ID);
        applyWxCfca.setCompanyName(company.getCompanyName());
        applyWxCfca.setCompanyId(company.getId());

        // 修改状态
        userDetail.setCompanyApplyStatus(Constant.APPLY_STATUS_APPLYING);
        userDetailDao.save(userDetail);

        //上传盖章信息
        UserDetail userDetail2 = userDetailDao.findByUserIdAndEnableFlg(UserHelper.getCurUserId(), true);

        before.setUserId(UserHelper.getCurUserId());
        saveInfoDao.save(before);

        userDetail2.setCfcaApprovedStatus(Constant.APPLY_STATUS_APPLYING);
        userDetailDao.save(userDetail2);
        // 发起cfca申请
        CompanyBaseInfoVo latestNotifyBaseInfo = findLatestNotifyBaseInfo();
        applyWxCfca.setBusinessLicenseWithSealUrl(latestNotifyBaseInfo.getBusinessLicenseWithSealUrl());
        applyWxCfca.setElectronicSignFileId(request.getElectronicSignFileId());
        applyWxCfca.setUkeyNumber(request.getUkeyNumber());

        applyWxCfca.setLegalRepresent(request.getLegalRepresent());
        applyWxCfca.setIdentityCardNumber(request.getIdentityCardNumber());
        applyWxCfca.setEmail(request.getEmail());
        applyWxCfca.setPhone(request.getPhone());
        applyWxCfca.setCardType(request.getCardType());
        applyWxCfca.setStatus("N");
        applyService.ApplyWxCfca(applyWxCfca);

    }


    /**
     * 协助线上化申请
     * @param vo
     */
    @Override
    public void saveSaveInfo(CompanyOnLineApplyVo vo) {
        logger.info("===CompanyOnLineApplyVo:{}", JsonUtil.obj2Json(vo));
        SaveInfo before;
        before = findSaveInfo(vo.getCompanyId(), vo.getUserId(), SaveInfoType.BASE_INFO.getType(), false);
        if (before == null) {
            before = new SaveInfo();
        }
        // 营业执照id
        if(StrUtil.isNotEmpty(vo.getBusinessLicenseWithSealUrl())) {
            vo.setBusinessLicenseWithSealUrl(vo.getBusinessLicenseWithSealUrl().replaceAll(",",""));
        }
        // 合同章活公章id
        if(StrUtil.isNotEmpty(vo.getElectronicSignFileId())) {
            vo.setElectronicSignFileId(vo.getElectronicSignFileId().replaceAll(",",""));
        }
        // 证件照正面id
        if(StrUtil.isNotEmpty(vo.getLegalPersonPicUrl())) {
            vo.setLegalPersonPicUrl(vo.getLegalPersonPicUrl().replaceAll(",",""));
        }
        // 证件照反面id
        if(StrUtil.isNotEmpty(vo.getLegalPersonOppositePicUrl())) {
            vo.setLegalPersonOppositePicUrl(vo.getLegalPersonOppositePicUrl().replaceAll(",",""));
        }
        String json = JsonUtil.obj2Json(vo);
        before.setContent(json);
        before.setType(SaveInfoType.BASE_INFO.getType());
        before.setUserId(vo.getUserId());
        before.setCommitFlg(false);
        before.setCompanyId(vo.getCompanyId());
        saveInfoDao.save(before);
    }

    @Override
    public SaveInfo findSaveInfo(Long companyId, Long userId, String type, Boolean commitFlg) {
        SaveInfo saveInfo = null;
        logger.info("1===>findSaveInfoParam companyId:{},userId:{},type:{},commitFlg:{}", companyId, userId, type, commitFlg);
        List<SaveInfo> saveInfoList = saveInfoDao.findByCompanyIdOrUserIdAndTypeAndCommitFlg(companyId, userId, type, commitFlg);
        logger.info("2===>query result:{}", JsonUtil.obj2Json(saveInfoList));
        if (CollectionUtils.isNotEmpty(saveInfoList)) {
            List<SaveInfo> infoList = saveInfoList.stream().filter(info -> info.getUserId().equals(userId)).collect(Collectors.toList());
            // 委托授权
            if(SaveInfoType.ENTRUST.getType().equals(type) && CollectionUtils.isEmpty(infoList)){
                return null;
            }
            logger.info("3===>infoList:{}", JsonUtil.obj2Json(infoList));
            if (CollectionUtils.isNotEmpty(infoList)) {
                saveInfo = infoList.stream().max(Comparator.comparing(SaveInfo::getUpdatedDate)).orElse(null);
            } else {
                saveInfo = saveInfoList.stream().max(Comparator.comparing(SaveInfo::getUpdatedDate)).orElse(null);
            }
        }
        logger.info("4===>result:{}", JsonUtil.obj2Json(saveInfo));
        return saveInfo;
    }

    /**
     * 保理签署
     * @param contractNoRequest
     * @return
     */
    @Override
    @ServiceTransactional
    public AxqUrlVo axqDebtCertificate(ContractNoRequest contractNoRequest){
        CtrContract contract = ctrContractClient.findByContractNoV2(contractNoRequest.getContractNo());
        if (Objects.isNull(contract)) {
            throw new BaseException(Status.ERROR, "合同不存在");
        }
        BsCompany company = bsCompanyDao.findByIdAndEnableFlgTrue(contract.getCompanyId());
        if (Objects.isNull(company) || !company.getOnLineFlg()) {
            throw new BaseException(Status.ERROR, "该企业还未完成线上化,无法进行线上盖章");
        }
        if (StringUtils.isNotBlank(contract.getDebtCertificateFileId())) {
            throw new BaseException(Status.ERROR, "已完成保理签署,不能重复提交");
        }
        AxqContractVo axqContractVo = this.convertContractBL(contract);
        axqContractVo.setPhoneNumber(contractNoRequest.getOperatorPhone());
        AxqContractVo res = cfcaSignClient.axqDebtCertificate(axqContractVo);
        logger.info("res:{}", JsonUtil.obj2Json(res));
        AxqUrlVo axqUrlVo = new AxqUrlVo();
        if (Objects.nonNull(res)){
            axqUrlVo.setUrl(res.getShortUrl());
            axqUrlVo.setResCode(res.getRetCode());
            axqUrlVo.setResMessage(res.getRetMessage());
        }
        return axqUrlVo;
    }





    /**
     * 签署保理转换ctrContract->AxqContractVo
     *
     * @param contract
     * @return
     */
    private AxqContractVo convertContractBL(CtrContract contract) {
        AxqContractVo axqContractVo = new AxqContractVo();
        ApplyMatch applyMatch = applyMatchClient.findByApproveId(contract.getApproveId());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
        BeanUtils.copyProperties(applyMatch, axqContractVo);
        axqContractVo.setTemplateId("BL_Creditor_Template");// 必填
        //BlMustAxq(axqContractVo);
        mustAxq(axqContractVo);
        if (BasConstants.CONTRACT_TYPE_B.equals(contract.getContractType())) {
            axqContractVo.setOurCompanyName(contract.getCompanyName());//必填 签约的企业必须在安心签开户过
        } else {
            axqContractVo.setOurCompanyName(contract.getOurCompanyName());//必填 签约的企业必须在安心签开户过
        }
        axqContractVo.setBuyerCompanyName(contract.getCompanyName());
        axqContractVo.setSellerCompanyName(contract.getOurCompanyName());
        //1.初始登记日期(最近一次收货款日期)
        List<ApplyConfirmReceipt> byContractId = applyConfirmReceiptClient.findByContractId(contract.getId());
        ApplyConfirmReceipt applyConfirmReceipt = byContractId.get(0);
        axqContractVo.setStartDate(sdf.format(applyConfirmReceipt.getConfirmReceiptDate()));
        //2.应收账款大写金额
        String toChineseMethod = RmbUtil.number2Chinese(contract.getTotalAmount());
        axqContractVo.setTotalAmountCn(toChineseMethod);
        //3.债权代码
        BsKeySequence bsKeySequence=new BsKeySequence();
        bsKeySequence.setKeyCategory(BasConstants.CREDITOR_NO);
        bsKeySequence.setEnterpriseId(contract.getEnterpriseId());
        axqContractVo.setCreditorCode(bsKeySequenceClient.getNextKey(bsKeySequence));
        //4.债权付款日期
        axqContractVo.setPaymentDate(sdf.format(contract.getPayFullTime()));
        //5.债权人
        axqContractVo.setCreditorName(contract.getCompanyName());
        //6.债权合同
        axqContractVo.setContractNo(contract.getContractNo());
        axqContractVo.setContractType(contract.getContractType());
        return axqContractVo;
    }

    /**
     * 查询债权凭证
     *
     * @param
     * @return
     */
    @Override
    public String selectCreditor(ContractNoRequest contractNoRequest) {
        CtrContract contract = ctrContractClient.findByContractNoV2(contractNoRequest.getContractNo());
        if ( contract.getDebtCertificateFileId()!=null|| contract.getDebtCertificateFileId()!=""){
            String  url= fileShowUrl+ "/view/show/" + contract.getDebtCertificateFileId();
            return url;
        }
        logger.info(contractNoRequest.getContractNo(),"--找不到附件");
        return "";
    }

}
