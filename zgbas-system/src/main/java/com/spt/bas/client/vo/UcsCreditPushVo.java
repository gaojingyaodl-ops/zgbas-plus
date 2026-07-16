package com.spt.bas.client.vo;

import java.math.BigDecimal;

public class UcsCreditPushVo {
	private String optionFlowNo;					//操作流水号
	private String companyName;						//企业客户名称					
	private String appCode;							//系统标识
	private BigDecimal adjustCreditAmount;			//总授信额度
	private BigDecimal beforeHaveusedAmount;		//已用授信-操作前
	private BigDecimal haveusedAmount;				//已用授信-操作后
	private BigDecimal beforeRemainingAmount;		//剩余授信-操作前
	private BigDecimal remainingAmount;				//剩余授信-操作后
	private BigDecimal approveCreditAmount;			//本次操作 金额
	private String type;							//操作类型   A:增加   S:减少
	private String contractNo;						//合同编号
	private String pushType;						//推送类型
	
	public String getOptionFlowNo() {
		return optionFlowNo;
	}
	public void setOptionFlowNo(String optionFlowNo) {
		this.optionFlowNo = optionFlowNo;
	}
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
	public BigDecimal getBeforeHaveusedAmount() {
		return beforeHaveusedAmount;
	}
	public void setBeforeHaveusedAmount(BigDecimal beforeHaveusedAmount) {
		this.beforeHaveusedAmount = beforeHaveusedAmount;
	}
	public BigDecimal getHaveusedAmount() {
		return haveusedAmount;
	}
	public void setHaveusedAmount(BigDecimal haveusedAmount) {
		this.haveusedAmount = haveusedAmount;
	}
	public BigDecimal getBeforeRemainingAmount() {
		return beforeRemainingAmount;
	}
	public void setBeforeRemainingAmount(BigDecimal beforeRemainingAmount) {
		this.beforeRemainingAmount = beforeRemainingAmount;
	}
	public BigDecimal getRemainingAmount() {
		return remainingAmount;
	}
	public void setRemainingAmount(BigDecimal remainingAmount) {
		this.remainingAmount = remainingAmount;
	}
	public BigDecimal getApproveCreditAmount() {
		return approveCreditAmount;
	}
	public void setApproveCreditAmount(BigDecimal approveCreditAmount) {
		this.approveCreditAmount = approveCreditAmount;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getContractNo() {
		return contractNo;
	}
	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}
	public String getPushType() {
		return pushType;
	}
	public void setPushType(String pushType) {
		this.pushType = pushType;
	}
	
}
