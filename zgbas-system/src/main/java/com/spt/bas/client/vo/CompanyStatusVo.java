package com.spt.bas.client.vo;

public class CompanyStatusVo {

	private Long id;
	private String status;
	private Long matchUserId;//领用人
	private String matchUserName;
	private Long createUserId;
	private String createUserName;
	private String remark;
	private String assignedUserName;//指派人
	private Long assignedUserId;//指派人ID
	private Long ownerOfAccountId;//开户人ID

	/**
	 * 人保申请状态
	 */
	private String piccApplyStatus;
	
	private String piccApplyCreditAmountFlg;

	/**
	 * 释放给区域总次数
	 */
	private Integer freedToDeptLeaderCount;

	/**
	 * 部门ID
	 */
	private Long deptId;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Long getMatchUserId() {
		return matchUserId;
	}
	public void setMatchUserId(Long matchUserId) {
		this.matchUserId = matchUserId;
	}
	public Long getCreateUserId() {
		return createUserId;
	}
	public void setCreateUserId(Long createUserId) {
		this.createUserId = createUserId;
	}
	public String getCreateUserName() {
		return createUserName;
	}
	public void setCreateUserName(String createUserName) {
		this.createUserName = createUserName;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getAssignedUserName() {
		return assignedUserName;
	}
	public void setAssignedUserName(String assignedUserName) {
		this.assignedUserName = assignedUserName;
	}
	public Long getAssignedUserId() {
		return assignedUserId;
	}
	public void setAssignedUserId(Long assignedUserId) {
		this.assignedUserId = assignedUserId;
	}
	public String getMatchUserName() {
		return matchUserName;
	}
	public void setMatchUserName(String matchUserName) {
		this.matchUserName = matchUserName;
	}

	public Long getOwnerOfAccountId() {
		return ownerOfAccountId;
	}

	public void setOwnerOfAccountId(Long ownerOfAccountId) {
		this.ownerOfAccountId = ownerOfAccountId;
	}

	public String getPiccApplyStatus() {
		return piccApplyStatus;
	}

	public void setPiccApplyStatus(String piccApplyStatus) {
		this.piccApplyStatus = piccApplyStatus;
	}

	public String getPiccApplyCreditAmountFlg() {
		return piccApplyCreditAmountFlg;
	}

	public void setPiccApplyCreditAmountFlg(String piccApplyCreditAmountFlg) {
		this.piccApplyCreditAmountFlg = piccApplyCreditAmountFlg;
	}

	public Integer getFreedToDeptLeaderCount() {
		return freedToDeptLeaderCount;
	}

	public void setFreedToDeptLeaderCount(Integer freedToDeptLeaderCount) {
		this.freedToDeptLeaderCount = freedToDeptLeaderCount;
	}

	public Long getDeptId() {
		return deptId;
	}

	public void setDeptId(Long deptId) {
		this.deptId = deptId;
	}
}
