package com.spt.bas.purchase.wx.server.payload;

//import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * <p>
 *
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-10-12 12:04
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WxLoginRequest {

    private String signature;

    /**
     * 不包括敏感信息的原始数据字符串，用于计算签名
     */
    private String rawData;

    private String encryptedData;

    private String iv;

    /**
     * 小程序临时登录凭证code
     */
    @NotBlank(message = "code不能为空")
    //@ApiModelProperty(value = "临时登录凭证code", dataType = "String", required = true)
    private String code;

    private String appId;

    private String cloudID;

    /**
     * 邀请码
     */
    private Long inviteCode;

}
