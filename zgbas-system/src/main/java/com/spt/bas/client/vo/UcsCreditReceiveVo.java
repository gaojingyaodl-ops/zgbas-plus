package com.spt.bas.client.vo;
/**
 * 企业授信额度接收Vo 
 *
 */

import java.math.BigDecimal;

public class UcsCreditReceiveVo {
	private String companyName;										//企业名称
	private String appCode;						
	private BigDecimal adjustCreditAmount = BigDecimal.ZERO;		//总授信额度
	private BigDecimal haveusedAmount = BigDecimal.ZERO;			//已用授信额度
	private BigDecimal remainingAmount = BigDecimal.ZERO;			//剩余授信额度
	private String zhangPeriod;									    //账期
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public String getAppCode() {
		return appCode;
	}
	public void setAppCode(String appCode) {
		this.appCode = appCode;
	}
	public BigDecimal getAdjustCreditAmount() {
		return adjustCreditAmount;
	}
	public void setAdjustCreditAmount(BigDecimal adjustCreditAmount) {
		this.adjustCreditAmount = adjustCreditAmount;
	}
	public BigDecimal getHaveusedAmount() {
		return haveusedAmount;
	}
	public void setHaveusedAmount(BigDecimal haveusedAmount) {
		this.haveusedAmount = haveusedAmount;
	}
	public BigDecimal getRemainingAmount() {
		return remainingAmount;
	}
	public void setRemainingAmount(BigDecimal remainingAmount) {
		this.remainingAmount = remainingAmount;
	}
	public String getZhangPeriod() {
		return zhangPeriod;
	}
	public void setZhangPeriod(String zhangPeriod) {
		this.zhangPeriod = zhangPeriod;
	}
	
}
