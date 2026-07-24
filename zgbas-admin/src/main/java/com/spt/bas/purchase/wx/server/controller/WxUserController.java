package com.spt.bas.purchase.wx.server.controller;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.WxMaUserService;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.purchase.wx.server.cache.BsDictUtil;
import com.spt.bas.purchase.wx.server.common.ApiResult;
import com.spt.bas.purchase.wx.server.common.BaseException;
import com.spt.bas.purchase.wx.server.common.Status;
import com.spt.bas.purchase.wx.server.config.WxConfiguration;
import com.spt.bas.purchase.wx.server.entity.WxUserTextRead;
import com.spt.bas.purchase.wx.server.payload.LoginRequest;
import com.spt.bas.purchase.wx.server.payload.WxLoginRequest;
import com.spt.bas.purchase.wx.server.service.IUserService;
import com.spt.bas.purchase.wx.server.service.IWxUserTextReadService;
import com.spt.bas.purchase.wx.server.util.UserHelper;
import com.spt.bas.purchase.wx.server.vo.UserInfoVo;
import com.spt.tools.core.exception.ApplicationException;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * <p>
 *  wx登录
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-09-15 09:43
 */
@RestController
@RequestMapping(value = "/wx/user")
@Api(tags = "微信小程序注册登录接口")
@ApiSort(value = 1)
@Slf4j
public class WxUserController {

    @Autowired
    private IUserService userService;

    @Autowired
    private IWxUserTextReadService wxUserTextReadService;

    @ApiOperation(value = "刷新token")
    @PostMapping("/refreshToken")
    public ApiResult refreshToken(HttpServletRequest request) {
        UserInfoVo infoVo = userService.refreshToken(request);
        ApiResult apiResponse = ApiResult.ofSuccess(infoVo);
        return apiResponse;
    }

    /**
     * 登陆接口
     */
    @PostMapping("/login")
    @ApiOperation(value = "登录接口")
    @ApiResponses({
            @ApiResponse(code=400,message="请求参数没填好"),
            @ApiResponse(code=404,message="请求路径没有或页面跳转路径不对")
    })
    @ApiOperationSupport(order = 1)
    public ApiResult login(@Valid @RequestBody LoginRequest loginRequest) {
        UserInfoVo infoVo = userService.login(loginRequest);
        return ApiResult.ofSuccess(infoVo);
    }

    /**
     * 注册接口
     */
    @PostMapping("/register")
    @ApiOperation(value = "注册接口")
    @ApiOperationSupport(order = 2)
    public ApiResult register(@Valid @RequestBody LoginRequest loginRequest) {
        UserInfoVo register = userService.register2(loginRequest);
        return ApiResult.ofSuccess(register);
    }

    /**
     * 验证码发送接口
     */
    @PostMapping("/smsValidateCode")
    @ApiOperation(value = "验证码发送接口")
    @ApiOperationSupport(order = 3)
    public ApiResult smsValidateCode(@Valid @RequestBody LoginRequest loginRequest) {
        userService.sendCheckCode(loginRequest.getLoginPhone());
        return ApiResult.ofSuccess("发送成功");
    }

    /**
     * 微信登录接口
     * @return
     */
    @PostMapping("/wxLogin")
    @ApiOperation(value = "微信登录接口")
    @ApiOperationSupport(order = 4)
    public ApiResult wxLogin(@RequestBody WxLoginRequest wxLoginRequest) {
        if (wxLoginRequest.getCode().isEmpty()) {
            throw new BaseException(Status.ERROR, "enpty jscode");
        }
        final WxMaService wxService = WxConfiguration.getMaService();
        final WxMaUserService userService1 = wxService.getUserService();
        try {
            log.info("微信登录邀请码：{}", wxLoginRequest.getInviteCode());
            UserInfoVo userInfoVo = userService.wxLogin(wxLoginRequest, userService1);
            WxUserTextRead wxUserTextRead = wxUserTextReadService.findByUserIdAndTextType(userInfoVo.getUserId(), "noticeInterest");
            if (Objects.nonNull(wxUserTextRead)) {
                Date now = new Date();
                int a = (int) ((now.getTime() - wxUserTextRead.getReadTime().getTime()) / (1000*3600*24));
                if (a > 30) {
                    userInfoVo.setInformedConsentFlag(0);
                } else {
                    userInfoVo.setInformedConsentFlag(1);
                }
            } else {
                userInfoVo.setInformedConsentFlag(0);
            }
            return ApiResult.ofSuccess(userInfoVo);
        } catch (WxErrorException | ApplicationException e) {
            log.error(e.getMessage());
            return ApiResult.ofStatus(Status.ERROR);
        }
    }

    /**
     * 微信登录接口
     * @return
     */
    @PostMapping("/resetPassword")
    @ApiOperation(value = "密码重置")
    @ApiOperationSupport(order = 5)
    public ApiResult resetPassword(@RequestBody LoginRequest loginRequest) {
        userService.resetPassword(loginRequest);
        return ApiResult.ofSuccess();
    }

    /**
     * 安心签配置信息改成后台数据库配置
     * @return
     */
    @PostMapping("/axqinfo")
    @ApiOperation(value = "安心签配置信息改成后台数据库配置")
    @ApiOperationSupport(order = 6)
    public ApiResult axqinfo( HttpServletResponse response) {
        String appid = BsDictUtil.getValue(BasConstants.ZG_ENTERPRISE_ID, BasConstants.WX_PURCHASE_INFO, "appid");
        String path = BsDictUtil.getValue(BasConstants.ZG_ENTERPRISE_ID, BasConstants.WX_PURCHASE_INFO, "path");
        Map<String,String> map=new HashMap<>();
        map.put("appid",appid);
        map.put("path",path);
        return ApiResult.ofSuccess(map);
    };

}
