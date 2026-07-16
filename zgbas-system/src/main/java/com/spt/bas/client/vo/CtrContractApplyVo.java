package com.spt.bas.client.vo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

public class CtrContractApplyVo {
	
	private Long contractId;
	private String applyType;
	private BigDecimal dealAmount;
	private BigDecimal dealNumber;
	private Date realDate;
	
	/**
	 * 运输费
	 */
	private BigDecimal transportAmount = BigDecimal.ZERO;

	/**
	 * 仓储费
	 */
	private BigDecimal warehouseAmount = BigDecimal.ZERO;

	/**
	 * 装卸费
	 */
	private   BigDecimal stevedorage = BigDecimal.ZERO;
	
	public Long getContractId() {
		return contractId;
	}
	public void setContractId(Long contractId) {
		this.contractId = contractId;
	}
	public String getApplyType() {
		return applyType;
	}
	public void setApplyType(String applyType) {
		this.applyType = applyType;
	}
	public BigDecimal getDealAmount() {
		return dealAmount;
	}
	public void setDealAmount(BigDecimal dealAmount) {
		this.dealAmount = dealAmount;
	}

	public BigDecimal getDealNumber() {
		return dealNumber;
	}

	public void setDealNumber(BigDecimal dealNumber) {
		this.dealNumber = dealNumber;
	}

	public Date getRealDate() {
		return realDate;
	}
	public void setRealDate(Date realDate) {
		this.realDate = realDate;
	}

	public BigDecimal getTransportAmount() {
		return Objects.isNull(transportAmount) ? BigDecimal.ZERO : transportAmount;
	}

	public void setTransportAmount(BigDecimal transportAmount) {
		this.transportAmount = transportAmount;
	}

	public BigDecimal getWarehouseAmount() {
		return Objects.isNull(warehouseAmount) ? BigDecimal.ZERO : warehouseAmount;
	}

	public void setWarehouseAmount(BigDecimal warehouseAmount) {
		this.warehouseAmount = warehouseAmount;
	}

	public BigDecimal getStevedorage() {
		return Objects.isNull(stevedorage) ? BigDecimal.ZERO : stevedorage;
	}

	public void setStevedorage(BigDecimal stevedorage) {
		this.stevedorage = stevedorage;
	}
}
