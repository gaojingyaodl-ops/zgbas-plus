package com.spt.bas.client.vo;

import java.util.List;

public class SaasUserAccountVo {
	
	private Long accountId;
	private String admin;
	private List<SaasUserVo> saasUserList;
	public Long getAccountId() {
		return accountId;
	}
	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}
	public String getAdmin() {
		return admin;
	}
	public void setAdmin(String admin) {
		this.admin = admin;
	}
	public List<SaasUserVo> getSaasUserList() {
		return saasUserList;
	}
	public void setSaasUserList(List<SaasUserVo> saasUserList) {
		this.saasUserList = saasUserList;
	}
	

}
