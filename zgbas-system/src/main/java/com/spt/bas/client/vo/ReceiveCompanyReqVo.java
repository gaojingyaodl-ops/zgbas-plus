package com.spt.bas.client.vo;

public class ReceiveCompanyReqVo {
	private String  companyName;      	//公司名称
	private Boolean onLineFlg = false;	//线上化标识
	private Boolean openAccountFlg = false;//是否开户
	private Boolean openAdminFlg = false;//是否创建管理员
	private Boolean openCfcaFlg = false;//是否开通安心签
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public Boolean getOnLineFlg() {
		return onLineFlg;
	}
	public void setOnLineFlg(Boolean onLineFlg) {
		this.onLineFlg = onLineFlg;
	}
	public Boolean getOpenAccountFlg() {
		return openAccountFlg;
	}
	public void setOpenAccountFlg(Boolean openAccountFlg) {
		this.openAccountFlg = openAccountFlg;
	}
	public Boolean getOpenAdminFlg() {
		return openAdminFlg;
	}
	public void setOpenAdminFlg(Boolean openAdminFlg) {
		this.openAdminFlg = openAdminFlg;
	}
	public Boolean getOpenCfcaFlg() {
		return openCfcaFlg;
	}
	public void setOpenCfcaFlg(Boolean openCfcaFlg) {
		this.openCfcaFlg = openCfcaFlg;
	}
	
	
}
