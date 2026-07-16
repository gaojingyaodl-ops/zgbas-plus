/**
 *
 */
package com.spt.pm.vo;

import java.util.List;

/**
 * @author wlddh
 *
 */
public class PmApproveStepFlowVo {
    private List<Long> approveIds;

	/**
	 * 审批ID
	 */
    private Long approveId;

	/**
	 * 审批步骤id
	 */
	private Long approveStepId;

	/**
	 * 审批人id
	 */
    private Long approveUserId;

	/**
	 * 审批人姓名
	 */
    private String approveUserName;

	/**
	 * 审批备注
	 */
    private String approveRemark;

	/**
	 * 审批状态 'A-同意，D-拒绝，R-追回'
	 */
    private String approveOpinion;

	/**
	 * 是否为自动签
	 */
	private Boolean autoSignFlg = false;
    private String ip;
    private String browser;
    private String os;

    public List<Long> getApproveIds() {
        return approveIds;
    }

    public void setApproveIds(List<Long> approveIds) {
        this.approveIds = approveIds;
    }

    /**
     * 是否直接完成
     */
    private boolean complete;

    public Long getApproveId() {
        return approveId;
    }

    public void setApproveId(Long approveId) {
        this.approveId = approveId;
    }

    public Long getApproveStepId() {
        return approveStepId;
    }

    public void setApproveStepId(Long approveStepId) {
        this.approveStepId = approveStepId;
    }

    public Long getApproveUserId() {
        return approveUserId;
    }

    public void setApproveUserId(Long approveUserId) {
        this.approveUserId = approveUserId;
    }

    public String getApproveUserName() {
        return approveUserName;
    }

    public void setApproveUserName(String approvorName) {
        this.approveUserName = approvorName;
    }

    public String getApproveRemark() {
        return approveRemark;
    }

    public void setApproveRemark(String approveRemark) {
        this.approveRemark = approveRemark;
    }

    public String getApproveOpinion() {
        return approveOpinion;
    }

    public void setApproveOpinion(String approveStatus) {
        this.approveOpinion = approveStatus;
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getBrowser() {
        return browser;
    }

    public void setBrowser(String browser) {
        this.browser = browser;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

	public Boolean getAutoSignFlg() {
		return autoSignFlg;
	}

	public void setAutoSignFlg(Boolean autoSignFlg) {
		this.autoSignFlg = autoSignFlg;
	}
}
