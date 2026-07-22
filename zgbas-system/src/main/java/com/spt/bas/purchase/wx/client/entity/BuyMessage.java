package com.spt.bas.purchase.wx.client.entity;

import com.spt.tools.jpa.vo.IdEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 采购管家消息表
 */
@Entity
@Table(name = "t_buy_message")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class BuyMessage extends IdEntity {

    /**
     * 微信用户唯一标识
     */
    private String openId;

    /**
     * 已读标记(0-未读，1-已读)
     */
    private String readFlag;

    /**
     * 消息(json格式)
     */
    private String messageContent;

    /**
     * 消息类型(B-报价消息,C-成交消息,S-系统消息)
     */
    private String messageType;

    /**
     * 来源Z-老审批，B-企业微信
     */
    private String sourceBy;

    /**
     * 创建人
     */
    private Long createdBy;

    /**
     * 修改人
     */
    private Long updatedBy;

    public String getSourceBy() {
        return sourceBy;
    }

    public void setSourceBy(String sourceBy) {
        this.sourceBy = sourceBy;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getReadFlag() {
        return readFlag;
    }

    public void setReadFlag(String readFlag) {
        this.readFlag = readFlag;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public Long getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Long updatedBy) {
        this.updatedBy = updatedBy;
    }
}
