package com.spt.bas.client.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spt.tools.jpa.vo.IdEntity;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "t_ctr_contract_dcsx_apply")
public class CtrContractDcsxApply extends IdEntity {
	private static final long serialVersionUID = 1L;
	/**
	 * 合同ID
	 */
	private Long ctrContractId;
	/**
	 * 企业账套ID
	 */
	private Long enterpriseId;
	/**
	 * 付款
	 */
	private BigDecimal applyPayAmount = BigDecimal.ZERO;
	/**
	 * 收款
	 */
	private  BigDecimal applyReceiveAmount= BigDecimal.ZERO;
	/**
	 * 开/收票
	 */
	private BigDecimal applyBillAmount = BigDecimal.ZERO;
	/**
	 * 出/入库
	 */
	private BigDecimal applyWarehouseNumber = BigDecimal.ZERO;
	/**
	 * 退款金额
	 */
	private BigDecimal applyRefundAmount = BigDecimal.ZERO;

	private BigDecimal applyServiceAmount = BigDecimal.ZERO;


	private String applyCancelApproveNo;// 作废中审批编号（竖线隔开）

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date realWarehoseDate;// 实际出/入库日期
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date realPayDate;// 实际收/付款日期
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date realBillDate;// 实际开/收票日期

	/**
	 * 实际确认收货日期
	 */
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
	private Date realConfirmReceiptDate;

	/**
	 * 确认收货数量
	 */
	private BigDecimal applyConfirmReceiptNumber = BigDecimal.ZERO;

	/**
	 * 抵扣余额
	 */
	private BigDecimal  deductibleAmount;

	public BigDecimal getApplyReceiveAmount() {
		return applyReceiveAmount;
	}

	public void setApplyReceiveAmount(BigDecimal applyReceiveAmount) {
		this.applyReceiveAmount = applyReceiveAmount;
	}

	public BigDecimal getDeductibleAmount() {
		return deductibleAmount;
	}

	public void setDeductibleAmount(BigDecimal deductibleAmount) {
		this.deductibleAmount = deductibleAmount;
	}


	public Date getRealConfirmReceiptDate() {
		return realConfirmReceiptDate;
	}

	public void setRealConfirmReceiptDate(Date realConfirmReceiptDate) {
		this.realConfirmReceiptDate = realConfirmReceiptDate;
	}

	public BigDecimal getApplyConfirmReceiptNumber() {
		return applyConfirmReceiptNumber;
	}

	public void setApplyConfirmReceiptNumber(BigDecimal applyConfirmReceiptNumber) {
		this.applyConfirmReceiptNumber = applyConfirmReceiptNumber;
	}

	public Long getCtrContractId() {
		return ctrContractId;
	}

	public void setCtrContractId(Long ctrContractId) {
		this.ctrContractId = ctrContractId;
	}

	public BigDecimal getApplyPayAmount() {
		return Objects.isNull(applyPayAmount) ? BigDecimal.ZERO : applyPayAmount;
	}

	public void setApplyPayAmount(BigDecimal applyPayAmount) {
		this.applyPayAmount = applyPayAmount;
	}

	public BigDecimal getApplyBillAmount() {
		return Objects.isNull(applyBillAmount) ? BigDecimal.ZERO : applyBillAmount;
	}

	public void setApplyBillAmount(BigDecimal applyBillAmount) {
		this.applyBillAmount = applyBillAmount;
	}

	public BigDecimal getApplyWarehouseNumber() {
		return Objects.isNull(applyWarehouseNumber) ? BigDecimal.ZERO : applyWarehouseNumber;
	}

	public void setApplyWarehouseNumber(BigDecimal applyWarehouseNumber) {
		this.applyWarehouseNumber = applyWarehouseNumber;
	}

	public Long getEnterpriseId() {
		return enterpriseId;
	}

	public void setEnterpriseId(Long enterpriseId) {
		this.enterpriseId = enterpriseId;
	}

	public String getApplyCancelApproveNo() {
		return applyCancelApproveNo;
	}

	public void setApplyCancelApproveNo(String applyCancelApproveNo) {
		this.applyCancelApproveNo = applyCancelApproveNo;
	}

	public Date getRealWarehoseDate() {
		return realWarehoseDate;
	}

	public void setRealWarehoseDate(Date realWarehoseDate) {
		this.realWarehoseDate = realWarehoseDate;
	}

	public Date getRealPayDate() {
		return realPayDate;
	}

	public void setRealPayDate(Date realPayDate) {
		this.realPayDate = realPayDate;
	}

	public Date getRealBillDate() {
		return realBillDate;
	}

	public void setRealBillDate(Date realBillDate) {
		this.realBillDate = realBillDate;
	}

	public BigDecimal getApplyRefundAmount() {
		return applyRefundAmount;
	}

	public void setApplyRefundAmount(BigDecimal applyRefundAmount) {
		this.applyRefundAmount = applyRefundAmount;
	}

	public BigDecimal getApplyServiceAmount() {
		return applyServiceAmount;
	}

	public void setApplyServiceAmount(BigDecimal applyServiceAmount) {
		this.applyServiceAmount = applyServiceAmount;
	}

}
