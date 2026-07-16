package com.spt.bas.client.vo;

import java.math.BigDecimal;

public class ContractCfs {

	private BigDecimal dealNumber;
	private String contractId;
	
	public BigDecimal getDealNumber() {
		return dealNumber;
	}
	public void setDealNumber(BigDecimal dealNumber) {
		this.dealNumber = dealNumber;
	}
	public String getContractId() {
		return contractId;
	}
	public void setContractId(String contractId) {
		this.contractId = contractId;
	}
}
