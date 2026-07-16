package com.spt.bas.client.vo;

import com.spt.tools.core.bean.PageSearchVo;
/**
 * 考核统计 
 */
public class ContractReportVo extends PageSearchVo{
	private String brandNumber;
	private String businessNo;
	private String companyName;
	private String matchUserName;
	private String procurementName;
	private String payMoneyTimeGted;
	private String payMoneyTimeLtd;
	public String getBrandNumber() {
		return brandNumber;
	}
	public void setBrandNumber(String brandNumber) {
		this.brandNumber = brandNumber;
	}
	public String getBusinessNo() {
		return businessNo;
	}
	public void setBusinessNo(String businessNo) {
		this.businessNo = businessNo;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public String getMatchUserName() {
		return matchUserName;
	}
	public void setMatchUserName(String matchUserName) {
		this.matchUserName = matchUserName;
	}
	public String getProcurementName() {
		return procurementName;
	}
	public void setProcurementName(String procurementName) {
		this.procurementName = procurementName;
	}
	public String getPayMoneyTimeGted() {
		return payMoneyTimeGted;
	}
	public void setPayMoneyTimeGted(String payMoneyTimeGted) {
		this.payMoneyTimeGted = payMoneyTimeGted;
	}
	public String getPayMoneyTimeLtd() {
		return payMoneyTimeLtd;
	}
	public void setPayMoneyTimeLtd(String payMoneyTimeLtd) {
		this.payMoneyTimeLtd = payMoneyTimeLtd;
	}
	
	
}
