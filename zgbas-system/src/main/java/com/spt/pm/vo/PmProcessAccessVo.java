package com.spt.pm.vo;

public class PmProcessAccessVo {
	
	private Long userId;
	private String userName;
	private String processIds;
	private Long enterpriseId;
	
	private String processCode;
	
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
	public String getProcessIds() {
		return processIds;
	}
	public void setProcessIds(String processIds) {
		this.processIds = processIds;
	}
	public Long getEnterpriseId() {
		return enterpriseId;
	}
	public void setEnterpriseId(Long enterpriseId) {
		this.enterpriseId = enterpriseId;
	}
	public String getProcessCode() {
		return processCode;
	}
	public void setProcessCode(String processCode) {
		this.processCode = processCode;
	}

	public PmProcessAccessVo() {
	}

	public PmProcessAccessVo(Long userId, String processCode) {
		this.userId = userId;
		this.processCode = processCode;
	}
}
