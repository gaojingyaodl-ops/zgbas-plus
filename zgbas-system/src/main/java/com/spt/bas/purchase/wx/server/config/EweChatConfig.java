package com.spt.bas.purchase.wx.server.config;

import lombok.Data;

/**
 * @author 杨英承
 * @version 1.0.0
 * @date 2024/1/11 09:52
 */
@Data
public class EweChatConfig {

    /**
     * 企业id
     */
    private String corpid;

    /**
     * 应用的凭证密钥，注意应用需要是启用状态
     */
    private String corpsecret;

    /**
     * 自建应用id
     */
    private Integer agentid;
}
