package com.spt.bas.client.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

public class CtrContractDetailVo {
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date realPayTime;
	private String warehouseName;
	private String warehouseAddrs;
	private String doubleCheckFileId;
	private BigDecimal interestAmount;
	private BigDecimal receiveInterestAmount;
	private BigDecimal transportAmount = BigDecimal.ZERO;
	private BigDecimal warehouseAmount = BigDecimal.ZERO;
	private BigDecimal deliveryFee = BigDecimal.ZERO;
	private String ourCompanyName;
	private String deliveryMode;
	private String businessType;
	private Boolean matchCreditFlg;


	public String getBusinessType() {
		return businessType;
	}

	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}

	public Boolean getMatchCreditFlg() {
		return matchCreditFlg;
	}

	public void setMatchCreditFlg(Boolean matchCreditFlg) {
		this.matchCreditFlg = matchCreditFlg;
	}

	public Date getRealPayTime() {
		return realPayTime;
	}
	public void setRealPayTime(Date realPayTime) {
		this.realPayTime = realPayTime;
	}
	public String getWarehouseName() {
		return warehouseName;
	}
	public void setWarehouseName(String warehouseName) {
		this.warehouseName = warehouseName;
	}
	public String getWarehouseAddrs() {
		return warehouseAddrs;
	}
	public void setWarehouseAddrs(String warehouseAddrs) {
		this.warehouseAddrs = warehouseAddrs;
	}
	public String getDoubleCheckFileId() {
		return doubleCheckFileId;
	}
	public void setDoubleCheckFileId(String doubleCheckFileId) {
		this.doubleCheckFileId = doubleCheckFileId;
	}
	public BigDecimal getInterestAmount() {
		return interestAmount;
	}
	public void setInterestAmount(BigDecimal interestAmount) {
		this.interestAmount = interestAmount;
	}
	public BigDecimal getReceiveInterestAmount() {
		return receiveInterestAmount;
	}
	public void setReceiveInterestAmount(BigDecimal receiveInterestAmount) {
		this.receiveInterestAmount = receiveInterestAmount;
	}
	public BigDecimal getTransportAmount() {
		return transportAmount;
	}
	public void setTransportAmount(BigDecimal transportAmount) {
		this.transportAmount = transportAmount;
	}
	public BigDecimal getWarehouseAmount() {
		return warehouseAmount;
	}
	public void setWarehouseAmount(BigDecimal warehouseAmount) {
		this.warehouseAmount = warehouseAmount;
	}
	public String getOurCompanyName() {
		return ourCompanyName;
	}
	public void setOurCompanyName(String ourCompanyName) {
		this.ourCompanyName = ourCompanyName;
	}
	public String getDeliveryMode() {
		return deliveryMode;
	}
	public void setDeliveryMode(String deliveryMode) {
		this.deliveryMode = deliveryMode;
	}

	public BigDecimal getDeliveryFee() {
		return deliveryFee;
	}

	public void setDeliveryFee(BigDecimal deliveryFee) {
		this.deliveryFee = deliveryFee;
	}
}
