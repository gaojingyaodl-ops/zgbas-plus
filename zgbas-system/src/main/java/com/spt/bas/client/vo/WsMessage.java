package com.spt.bas.client.vo;

/**
 * @author moonLight
 */
public class WsMessage {
    // 资金方余额更新消息通知
    public static final String MESSAGE_TYPE_F = "F";

    // 待办事项数量更新消息通知
    public static final String MESSAGE_TYPE_W = "W";

    private Long targetUserId;
    private String messageType;
    private Long waitDealNum;
    private Long fundCompanyId;
    private String fundCompanyName;
    private String fundAmount;
    private String fundAmountQg;
    private String fundAmountWs;

    public Long getTargetUserId() {
        return targetUserId;
    }

    public void setTargetUserId(Long targetUserId) {
        this.targetUserId = targetUserId;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public Long getWaitDealNum() {
        return waitDealNum;
    }

    public void setWaitDealNum(Long waitDealNum) {
        this.waitDealNum = waitDealNum;
    }

    public Long getFundCompanyId() {
        return fundCompanyId;
    }

    public void setFundCompanyId(Long fundCompanyId) {
        this.fundCompanyId = fundCompanyId;
    }

    public String getFundCompanyName() {
        return fundCompanyName;
    }

    public void setFundCompanyName(String fundCompanyName) {
        this.fundCompanyName = fundCompanyName;
    }

    public String getFundAmount() {
        return fundAmount;
    }

    public void setFundAmount(String fundAmount) {
        this.fundAmount = fundAmount;
    }

    public String getFundAmountQg() {
        return fundAmountQg;
    }

    public void setFundAmountQg(String fundAmountQg) {
        this.fundAmountQg = fundAmountQg;
    }

    public String getFundAmountWs() {
        return fundAmountWs;
    }

    public void setFundAmountWs(String fundAmountWs) {
        this.fundAmountWs = fundAmountWs;
    }

    public WsMessage() {
    }

    public WsMessage(Long targetUserId, String messageType, Long waitDealNum) {
        this.targetUserId = targetUserId;
        this.messageType = messageType;
        this.waitDealNum = waitDealNum;
    }

    public WsMessage(Long fundCompanyId, String messageType, String fundCompanyName, String fundAmount) {
        this.fundCompanyId = fundCompanyId;
        this.messageType = messageType;
        this.fundCompanyName = fundCompanyName;
        this.fundAmount = fundAmount;
    }

    public WsMessage(Long fundCompanyId, String messageType, String fundCompanyName, String fundAmount, String fundAmountQg, String fundAmountWs) {
        this.fundCompanyId = fundCompanyId;
        this.messageType = messageType;
        this.fundCompanyName = fundCompanyName;
        this.fundAmount = fundAmount;
        this.fundAmountQg = fundAmountQg;
        this.fundAmountWs = fundAmountWs;
    }

}
