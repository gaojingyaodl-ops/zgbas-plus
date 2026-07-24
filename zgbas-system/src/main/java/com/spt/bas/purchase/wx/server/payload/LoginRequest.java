package com.spt.bas.purchase.wx.server.payload;

import com.fasterxml.jackson.annotation.JsonIgnore;
//import io.swagger.annotations.ApiModel;
//import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
//@ApiModel(value = "登录接口参数")
public class LoginRequest {

    //@ApiModelProperty(value = "登录手机号", required = true)
    private String loginPhone;

    //@ApiModelProperty(value = "密码", required = true)
    private String password;

    /**
     * 登录方式
     * 0：手机号
     * 1：验证码
     * 2：微信授权
     * 3：邀请码
     */
    //@ApiModelProperty(value = "登录方式", notes = "0:手机号，1：验证码，2：微信授权 3：邀请码", dataType = "String", required = true)
    private String loginType;

    /**
     * 验证码
     */
    //@ApiModelProperty(value = "验证码", dataType = "String", required = true)
    private String checkCode;

    /**
     * 邀请码
     */
    //@ApiModelProperty(value = "邀请码", dataType = "String", required = true)
    private String inviteCode;

    /**
     * 微信 code
     */
    private String code;

    /**
     * 微信小程序appid
     */
    //@ApiModelProperty(value = "微信小程序appid", dataType = "String", required = true)
    private String appId;

    @JsonIgnore
    private String openId;

    /**
     * 记住我
     */
    @JsonIgnore
    private boolean rememberMe = true;
}
