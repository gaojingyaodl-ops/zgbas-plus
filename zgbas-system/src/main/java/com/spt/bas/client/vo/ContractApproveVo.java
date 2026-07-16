package com.spt.bas.client.vo;

import java.math.BigDecimal;
import java.util.Date;

public class ContractApproveVo {

	private String contractType;
	private String contractNo;
	private String sellCompanyName;
	private String buyCompanyName;
	private BigDecimal price;
	private BigDecimal amount;
	private BigDecimal deposit;
	private Date settleDate;
	private BigDecimal finalPayment;
	private BigDecimal deliveryType;
	private String warehouse;
	
	public String getContractType() {
		return contractType;
	}
	public void setContractType(String contractType) {
		this.contractType = contractType;
	}
	public String getContractNo() {
		return contractNo;
	}
	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}
	public String getSellCompanyName() {
		return sellCompanyName;
	}
	public void setSellCompanyName(String sellCompanyName) {
		this.sellCompanyName = sellCompanyName;
	}
	public String getBuyCompanyName() {
		return buyCompanyName;
	}
	public void setBuyCompanyName(String buyCompanyName) {
		this.buyCompanyName = buyCompanyName;
	}
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public BigDecimal getDeposit() {
		return deposit;
	}
	public void setDeposit(BigDecimal deposit) {
		this.deposit = deposit;
	}
	public Date getSettleDate() {
		return settleDate;
	}
	public void setSettleDate(Date settleDate) {
		this.settleDate = settleDate;
	}
	public BigDecimal getFinalPayment() {
		return finalPayment;
	}
	public void setFinalPayment(BigDecimal finalPayment) {
		this.finalPayment = finalPayment;
	}
	public BigDecimal getDeliveryType() {
		return deliveryType;
	}
	public void setDeliveryType(BigDecimal deliveryType) {
		this.deliveryType = deliveryType;
	}
	public String getWarehouse() {
		return warehouse;
	}
	public void setWarehouse(String warehouse) {
		this.warehouse = warehouse;
	}
}
