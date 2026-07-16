/**
 *
 */
package com.spt.pm.vo;

/**
 * @author wlddh
 * 通用混合工具实体类
 */

public class PmApproveSaveVo {
    /**
     * 审批单ID
     */
    private Long approveId;

    /**
     * 流程ID
     */
    private Long processId;

    /**
     * 企业账套ID
     */
    private Long enterpriseId;

    /**
     * 发起人ID
     */
    private Long userId;

    /**
     * 发起人姓名
     */
    private String userName;

    /**
     * 发起人所在部门ID
     */
    private Long deptId;

    /**
     *  N-新增，A-审批中
     */
    private String status;

    /**
     * S-保存，A-发起申请，P-审批中修改
     */
    private String mode;

    /**
     * 对象数据转换json后set入，最后调用此类型的startFlow方法返回
     */
    private String bizEntityJson;

    /**
     * 是否为系统自动发起(线上化)
     */
    private Boolean autoStartFlg = false;

    /**
     * 是否为系统自动发起
     */
    private Boolean autoStartFlgReal = false;

    /**
     * 系统自动发起备注信息
     */
    private String autoStartMessage;

    /**
     * 是否按批次批量发起付款申请
     */
    private boolean batchPayApply = false;

    /**
     * 外部隐藏
     */
    private String hideOut = "0";

    /**
     * 采销中心标识
     */
    private Boolean tradeFlg = false;

    public Long getProcessId() {
        return processId;
    }

    public void setProcessId(Long processId) {
        this.processId = processId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getApproveId() {
        return approveId;
    }

    public void setApproveId(Long approveId) {
        this.approveId = approveId;
    }

    public String getBizEntityJson() {
        return bizEntityJson;
    }

    public void setBizEntityJson(String bizEntityJson) {
        this.bizEntityJson = bizEntityJson;
    }

    public Long getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(Long enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public String getAutoStartMessage() {
        return autoStartMessage;
    }

    public void setAutoStartMessage(String autoStartMessage) {
        this.autoStartMessage = autoStartMessage;
    }

    public Boolean getAutoStartFlg() {
        return autoStartFlg;
    }

    public void setAutoStartFlg(Boolean autoStartFlg) {
        this.autoStartFlg = autoStartFlg;
    }

    public boolean isBatchPayApply() {
        return batchPayApply;
    }

    public void setBatchPayApply(boolean batchPayApply) {
        this.batchPayApply = batchPayApply;
    }

    public Boolean getAutoStartFlgReal() {
        return autoStartFlgReal;
    }

    public void setAutoStartFlgReal(Boolean autoStartFlgReal) {
        this.autoStartFlgReal = autoStartFlgReal;
    }

    public String getHideOut() {
        return hideOut;
    }

    public void setHideOut(String hideOut) {
        this.hideOut = hideOut;
    }

    public Boolean getTradeFlg() {
        return tradeFlg;
    }

    public void setTradeFlg(Boolean tradeFlg) {
        this.tradeFlg = tradeFlg;
    }
}

