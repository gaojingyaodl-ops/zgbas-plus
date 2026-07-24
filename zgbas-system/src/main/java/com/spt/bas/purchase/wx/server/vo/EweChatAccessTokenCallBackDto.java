package com.spt.bas.purchase.wx.server.vo;

import lombok.Data;

/**
 * @author 杨英承
 * @version 1.0.0
 * @date 2024/1/11 09:46
 */
@Data
public class EweChatAccessTokenCallBackDto {

    /**
     * 出错返回码，为0表示成功，非0表示调用失败
     */
    private int errcode;

    /**
     * 返回码提示语
     */
    private String errmsg;

    /**
     * 获取到的凭证，最长为512字节
     */
    private String access_token;

    /**
     * 凭证的有效时间（秒）默认为7200秒=2小时
     */
    private int expires_in;
}
