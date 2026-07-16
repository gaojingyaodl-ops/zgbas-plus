package com.spt.bas.client.vo;

import com.spt.bas.client.entity.BasContract;

public class BasContractVo extends BasContract{

	private Long buyOrSellContractId;		//买或者卖合同id
	private String buyOrSellContractNo; //买或者卖合同编号

	public Long getBuyOrSellContractId() {
		return buyOrSellContractId;
	}

	public void setBuyOrSellContractId(Long buyOrSellContractId) {
		this.buyOrSellContractId = buyOrSellContractId;
	}

	public String getBuyOrSellContractNo() {
		return buyOrSellContractNo;
	}

	public void setBuyOrSellContractNo(String buyOrSellContractNo) {
		this.buyOrSellContractNo = buyOrSellContractNo;
	}

	 
	 
	 
	 
}
