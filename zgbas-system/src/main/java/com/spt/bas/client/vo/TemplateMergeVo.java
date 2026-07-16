package com.spt.bas.client.vo;

import com.spt.bas.client.entity.BasContract;

public class TemplateMergeVo extends BasContract{

	private static final long serialVersionUID = 8714657114358684090L;
	private String dealAmountCn;
	private String contractTimeStr;
	private String payTimeStr;
	
	private String deliveryDateFromStr;
	private String deliveryDateToStr;
	
	public String getDealAmountCn() {
		return dealAmountCn;
	}
	public void setDealAmountCn(String dealAmountCn) {
		this.dealAmountCn = dealAmountCn;
	}
	public String getContractTimeStr() {
		return contractTimeStr;
	}
	public void setContractTimeStr(String contractTimeStr) {
		this.contractTimeStr = contractTimeStr;
	}
	public String getDeliveryDateFromStr() {
		return deliveryDateFromStr;
	}
	public void setDeliveryDateFromStr(String deliveryDateFromStr) {
		this.deliveryDateFromStr = deliveryDateFromStr;
	}
	public String getDeliveryDateToStr() {
		return deliveryDateToStr;
	}
	public void setDeliveryDateToStr(String deliveryDateToStr) {
		this.deliveryDateToStr = deliveryDateToStr;
	}
	public String getPayTimeStr() {
		return payTimeStr;
	}
	public void setPayTimeStr(String payTimeStr) {
		this.payTimeStr = payTimeStr;
	}
	

}
