/**
 * 
 */
package com.spt.pm.vo;

/**
 * 审批追回
 * 
 * @author wlddh
 *
 */
public class PmApproveRetrieveVo {
	private Long approveId; // 审批id
	private Long bizId;
	private Long userId; // 审批人id
	private String userName; // 审批人姓名

	public Long getApproveId() {
		return approveId;
	}

	public void setApproveId(Long approveId) {
		this.approveId = approveId;
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

	public Long getBizId() {
		return bizId;
	}

	public void setBizId(Long bizId) {
		this.bizId = bizId;
	}
	
}
