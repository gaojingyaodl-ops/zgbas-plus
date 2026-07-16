package com.spt.bas.client.vo;

import java.util.List;

public class SysInitRequestVo {
	
	private String companyName;
	
	private List<SysUserVo> userList;
	
	private String admin;
	
	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public List<SysUserVo> getUserList() {
		return userList;
	}

	public void setUserList(List<SysUserVo> userList) {
		this.userList = userList;
	}

	public String getAdmin() {
		return admin;
	}

	public void setAdmin(String admin) {
		this.admin = admin;
	}
	

}
