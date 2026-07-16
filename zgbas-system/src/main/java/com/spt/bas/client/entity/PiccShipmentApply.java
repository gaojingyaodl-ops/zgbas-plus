package com.spt.bas.client.entity;

import com.spt.tools.jpa.vo.IdEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 人保赊销申请报文
 */
@Entity
@Table(name = "t_picc_shipment_apply")
public class PiccShipmentApply extends IdEntity {
	private static final long serialVersionUID = 6330631828677838478L;

	public String saleid;//赊销id
	public String declarationformNo;//申报单号
	public String policyNo;//保单号
	public String insuredName;//被保险人名称
	public String insuredPiccCode;//被保险人picccode
	public String riskPiccCode;//用户picccode
	public String limitFlag;//1：自行掌握限额赊销，0非自行掌握限额赊销
	public String riskName;//买方名称
	public String riskCompAddress;//买方地址

	public String productCategory;// 商品类别
	public String product;// 商品名称
	public String happenDate;// 发货日期
	public String happen2Date;// 发票日期
	public BigDecimal xamInvoiceAmount;// 赊销金额
	public String startDate;// 信用起始日
	public String accrualDate;// 应回款日
	public Date recoverDate;// 回款日期
	public Long paymentTerms;// 信用期限，赊销天数=应回款日期-（减去）信用期限起始日
	public String contractNo;// 发票号
	public Long contractId;// 合同id
	public String contractPayment;// 合同支付方式
	public String unAcceptReason;// 申请退回/不通过原因

	/**
	 * 批复状态 1-已批复，2-审核中，3-退回客户端，4-退回客户端不予受理
	 */
	public String approveFlag;

	public String state;//状态：1 人保发送成功 2人保发送失败 3 回款正常 4 赊销申请批复成功
	public BigDecimal recoverAmount;// 还款金额
	public BigDecimal totalRecoverAmount = BigDecimal.ZERO;// 还款总金额

	/**
	 * 保险计算状态
	 */
	private String insuranceComputeStatus;

	/**
	 * 保险费用
	 */
	private String insuranceAmount;

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getSaleid() {
		return saleid;
	}

	public void setSaleid(String saleid) {
		this.saleid = saleid;
	}

	public String getDeclarationformNo() {
		return declarationformNo;
	}

	public void setDeclarationformNo(String declarationformNo) {
		this.declarationformNo = declarationformNo;
	}

	public String getPolicyNo() {
		return policyNo;
	}

	public void setPolicyNo(String policyNo) {
		this.policyNo = policyNo;
	}

	public String getInsuredName() {
		return insuredName;
	}

	public void setInsuredName(String insuredName) {
		this.insuredName = insuredName;
	}

	public String getInsuredPiccCode() {
		return insuredPiccCode;
	}

	public void setInsuredPiccCode(String insuredPiccCode) {
		this.insuredPiccCode = insuredPiccCode;
	}

	public String getLimitFlag() {
		return limitFlag;
	}

	public void setLimitFlag(String limitFlag) {
		this.limitFlag = limitFlag;
	}

	public String getRiskName() {
		return riskName;
	}

	public void setRiskName(String riskName) {
		this.riskName = riskName;
	}

	public String getRiskCompAddress() {
		return riskCompAddress;
	}

	public void setRiskCompAddress(String riskCompAddress) {
		this.riskCompAddress = riskCompAddress;
	}

	public String getProductCategory() {
		return productCategory;
	}

	public void setProductCategory(String productCategory) {
		this.productCategory = productCategory;
	}

	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	public String getHappenDate() {
		return happenDate;
	}

	public void setHappenDate(String happenDate) {
		this.happenDate = happenDate;
	}
	@Column(name = "happen2_date")
	public String getHappen2Date() {
		return happen2Date;
	}

	public void setHappen2Date(String happen2Date) {
		this.happen2Date = happen2Date;
	}

	public BigDecimal getXamInvoiceAmount() {
		return xamInvoiceAmount;
	}

	public void setXamInvoiceAmount(BigDecimal xamInvoiceAmount) {
		this.xamInvoiceAmount = xamInvoiceAmount;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getAccrualDate() {
		return accrualDate;
	}

	public void setAccrualDate(String accrualDate) {
		this.accrualDate = accrualDate;
	}

	public Date getRecoverDate() {
		return recoverDate;
	}

	public void setRecoverDate(Date recoverDate) {
		this.recoverDate = recoverDate;
	}

	public Long getPaymentTerms() {
		return paymentTerms;
	}

	public void setPaymentTerms(Long paymentTerms) {
		this.paymentTerms = paymentTerms;
	}

	public String getContractNo() {
		return contractNo;
	}

	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}

	public Long getContractId() {
		return contractId;
	}

	public void setContractId(Long contractId) {
		this.contractId = contractId;
	}

	public String getRiskPiccCode() {
		return riskPiccCode;
	}

	public void setRiskPiccCode(String riskPiccCode) {
		this.riskPiccCode = riskPiccCode;
	}

	public String getContractPayment() {
		return contractPayment;
	}

	public void setContractPayment(String contractPayment) {
		this.contractPayment = contractPayment;
	}

	public String getUnAcceptReason() {
		return unAcceptReason;
	}

	public void setUnAcceptReason(String unAcceptReason) {
		this.unAcceptReason = unAcceptReason;
	}

	public String getApproveFlag() {
		return approveFlag;
	}

	public void setApproveFlag(String approveFlag) {
		this.approveFlag = approveFlag;
	}

	public BigDecimal getRecoverAmount() {
		return recoverAmount;
	}

	public void setRecoverAmount(BigDecimal recoverAmount) {
		this.recoverAmount = recoverAmount;
	}

	public BigDecimal getTotalRecoverAmount() {
		return totalRecoverAmount;
	}

	public void setTotalRecoverAmount(BigDecimal totalRecoverAmount) {
		this.totalRecoverAmount = totalRecoverAmount;
	}

	public String getInsuranceComputeStatus() {
		return insuranceComputeStatus;
	}

	public void setInsuranceComputeStatus(String insuranceComputeStatus) {
		this.insuranceComputeStatus = insuranceComputeStatus;
	}

	public String getInsuranceAmount() {
		return insuranceAmount;
	}

	public void setInsuranceAmount(String insuranceAmount) {
		this.insuranceAmount = insuranceAmount;
	}
}
