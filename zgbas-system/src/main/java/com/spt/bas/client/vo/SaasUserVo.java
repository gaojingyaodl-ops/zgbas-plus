package com.spt.bas.client.vo;

public class SaasUserVo {

	private String loginName;
	private Long mappingAccountId;
	private String plainPassword;
	public String getLoginName() {
		return loginName;
	}
	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}
	public Long getMappingAccountId() {
		return mappingAccountId;
	}
	public void setMappingAccountId(Long mappingAccountId) {
		this.mappingAccountId = mappingAccountId;
	}
	public String getPlainPassword() {
		return plainPassword;
	}
	public void setPlainPassword(String plainPassword) {
		this.plainPassword = plainPassword;
	}
	
}
