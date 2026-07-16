package com.spt.bas.client.vo;

import com.spt.bas.client.entity.CtrContract;

import java.math.BigDecimal;

public class CtrContractChooseVo extends CtrContract{
	private static final long serialVersionUID = 3488925737805601852L;
	private BigDecimal applyPayAmount;		//收付款申请金额
	private BigDecimal applyBillAmount;		//收开票申请金额
	private BigDecimal applyWarehouseNumber;//出入库申请数量
	private BigDecimal applyRefundAmount;//退款金额
	private BigDecimal applyServiceAmount;//服务费申请金额
	private String bankName;
	private String bankAccount;
	private String taxNo;
	private Long buyCompanyId;			//对方企业ID
	public BigDecimal getApplyPayAmount() {
		return applyPayAmount;
	}
	public void setApplyPayAmount(BigDecimal applyPayAmount) {
		this.applyPayAmount = applyPayAmount;
	}
	public BigDecimal getApplyBillAmount() {
		return applyBillAmount;
	}
	public void setApplyBillAmount(BigDecimal applyBillAmount) {
		this.applyBillAmount = applyBillAmount;
	}
	public BigDecimal getApplyWarehouseNumber() {
		return applyWarehouseNumber;
	}
	public void setApplyWarehouseNumber(BigDecimal applyWarehouseNumber) {
		this.applyWarehouseNumber = applyWarehouseNumber;
	}
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	public String getBankAccount() {
		return bankAccount;
	}
	public void setBankAccount(String bankAccount) {
		this.bankAccount = bankAccount;
	}
	public String getTaxNo() {
		return taxNo;
	}
	public void setTaxNo(String taxNo) {
		this.taxNo = taxNo;
	}
	public BigDecimal getApplyRefundAmount() {
		return applyRefundAmount;
	}
	public void setApplyRefundAmount(BigDecimal applyRefundAmount) {
		this.applyRefundAmount = applyRefundAmount;
	}
	public Long getBuyCompanyId() {
		return buyCompanyId;
	}
	public void setBuyCompanyId(Long buyCompanyId) {
		this.buyCompanyId = buyCompanyId;
	}

	public BigDecimal getApplyServiceAmount() {
		return applyServiceAmount;
	}

	public void setApplyServiceAmount(BigDecimal applyServiceAmount) {
		this.applyServiceAmount = applyServiceAmount;
	}
}
