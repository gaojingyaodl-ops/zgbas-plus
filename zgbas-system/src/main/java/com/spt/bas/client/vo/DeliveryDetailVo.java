package com.spt.bas.client.vo;

import java.math.BigDecimal;

import com.spt.bas.client.entity.ApplyDelivery;
/**
 * 出库明细 提货单明细 
 */
public class DeliveryDetailVo extends ApplyDelivery{
	private BigDecimal totalNumber;
	private String contractStatus;
	
	public String getContractStatus() {
		return contractStatus;
	}

	public void setContractStatus(String contractStatus) {
		this.contractStatus = contractStatus;
	}

	public BigDecimal getTotalNumber() {
		return totalNumber;
	}

	public void setTotalNumber(BigDecimal totalNumber) {
		this.totalNumber = totalNumber;
	}
	
}
