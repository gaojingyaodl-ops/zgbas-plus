package com.spt.bas.client.vo;

import com.spt.bas.client.entity.CtrContract;

public class UcsContractVo extends CtrContract{

	private static final long serialVersionUID = 1L;
	private String optionType;
	private String flowType;
	private String creditFlowNo;
	private String pushType;
	public String getOptionType() {
		return optionType;
	}
	public void setOptionType(String optionType) {
		this.optionType = optionType;
	}
	public String getFlowType() {
		return flowType;
	}
	public void setFlowType(String flowType) {
		this.flowType = flowType;
	}
	public String getCreditFlowNo() {
		return creditFlowNo;
	}
	public void setCreditFlowNo(String creditFlowNo) {
		this.creditFlowNo = creditFlowNo;
	}
	public String getPushType() {
		return pushType;
	}
	public void setPushType(String pushType) {
		this.pushType = pushType;
	}
	
}
