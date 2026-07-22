package com.spt.bas.purchase.wx.client.vo;

import com.spt.tools.core.bean.PageSearchVo;

/**
 * @author 杨英承
 * @version 1.0.0
 * @date 2024/1/5 10:08
 */

public class MessageSearchVo extends PageSearchVo {
    /**
     * 消息Id
     */
    private Long id;
    /**
     * 消息类别
     */
    private String messageType;

    /**
     * 微信唯一id
     */
    private String openId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }
}
