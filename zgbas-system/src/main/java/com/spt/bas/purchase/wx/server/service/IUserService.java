package com.spt.bas.purchase.wx.server.service;

import cn.binarywang.wx.miniapp.api.WxMaUserService;
import com.spt.bas.purchase.wx.client.vo.CompanyOnLineApplyVo;
import com.spt.bas.purchase.wx.client.entity.CompanyUser;
import com.spt.bas.purchase.wx.server.entity.WxSession;
import com.spt.bas.purchase.wx.server.payload.LoginRequest;
import com.spt.bas.purchase.wx.server.payload.WxLoginRequest;
import com.spt.bas.purchase.wx.server.vo.UserChangeVo;
import com.spt.bas.purchase.wx.server.vo.UserInfoVo;
import com.spt.tools.core.bean.RespVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.jpa.service.IBaseService;
import me.chanjar.weixin.common.error.WxErrorException;

import javax.servlet.http.HttpServletRequest;

public interface IUserService extends IBaseService<CompanyUser> {
    /**
     * 用户注册
     * @param vo
     * @return
     * @throws ApplicationException
     */
    CompanyUser register(LoginRequest vo) throws WxErrorException;

    /**
     * 用户注册返回
     * @param vo
     * @return
     * @throws ApplicationException
     */
    UserInfoVo register2(LoginRequest vo);

    /**
     * 用户登录
     * @param vo
     * @return
     * @throws ApplicationException
     */
    UserInfoVo login(LoginRequest vo);

    void resetPassword(LoginRequest vo);

    /**
     * 微信登录
     * @param loginRequest
     * @param userService1
     * @return
     * @throws WxErrorException
     * @throws ApplicationException
     */
    UserInfoVo wxLogin(WxLoginRequest loginRequest, WxMaUserService userService1) throws WxErrorException, ApplicationException;


    /**
     * 判断密码是否一致
     * @param vo
     * @return
     */
    boolean isPwdEqual(UserChangeVo vo);

    /**
     * 发送验证码
     * @param phone
     */
    void sendCheckCode(String phone);

    /**
     *  刷新token
     * @param request
     * @return
     * @throws ApplicationException
     */
    UserInfoVo refreshToken(HttpServletRequest request);

//    UserInfoVo wxRegister(LoginRequest vo);

    void wxGetPhone(String phone, WxSession wxSession);

    /**
     * 保存用户信息
     * @param vo
     * @return
     */
    RespVo<CompanyUser> saveApplyOnLineData(CompanyOnLineApplyVo vo);

}
