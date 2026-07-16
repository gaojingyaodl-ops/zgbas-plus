package com.spt.bas.client.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.spt.pm.inter.IPmEntity;
import com.spt.tools.jpa.vo.IdEntity;

/**
 * 开票信息
 */
@Entity
@Table(name = "t_bas_invoice")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class BasInvoice extends IdEntity  implements IPmEntity{

	private static final long serialVersionUID = 4081578105617372824L;
	private Long contractId; // 合同id
	private String businessNo; // 业务编号
	private String contractNo; // 合同编号
	@DateTimeFormat(pattern="yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date invoiceDate; // 开票日期
	private Long companyId; // 企业id
	private String companyName; // 开票抬头
	private String ourCompanyName; // 我方企业名称
	private String productName; // 商品名称
	private String productCode; // 商品代码
	private BigDecimal dealPrice; // 单价
	private BigDecimal dealNumber; // 数量
	private String numberUnit; // 数量单位
	private BigDecimal taxAmount; // 销项税
	private BigDecimal dealAmountNotax; // 不含税价
	private BigDecimal dealAmount; // 开票金额
	private String bankName; // 开户银行
	private String bankAccount; // 银行账号
	private String taxNo; // 税号
	private String address; // 公司地址
	private String companyPhone; // 公司电话
	private String invoiceNo; // 发票号码
	private String billNo; // 记账凭证号
	@DateTimeFormat(pattern="yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date receiveDate; // 收款日期
	private BigDecimal receiveAmount; // 收款金额
	private String payCompanyName; // 对方抬头
	private String receiveBillNo; // 收款记账凭证号
	private String status; // 状态 'N-新增，A-审批中，B-驳回，D-完成',
	private Long approveId; // 审批id
	private String remark;
	private String fileId; // 附件id
	@JsonSerialize(using = ToStringSerializer.class)
	private Boolean closeFlg;//是否闭口业务
	public Long getContractId() {
		return contractId;
	}

	public void setContractId(Long contractId) {
		this.contractId = contractId;
	}

	public String getContractNo() {
		return contractNo;
	}

	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}

	public Date getInvoiceDate() {
		return invoiceDate;
	}

	public void setInvoiceDate(Date invoiceDate) {
		this.invoiceDate = invoiceDate;
	}

	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public BigDecimal getDealPrice() {
		return dealPrice;
	}

	public void setDealPrice(BigDecimal dealPrice) {
		this.dealPrice = dealPrice;
	}

	public BigDecimal getDealNumber() {
		return dealNumber;
	}

	public void setDealNumber(BigDecimal dealNumber) {
		this.dealNumber = dealNumber;
	}

	public String getNumberUnit() {
		return numberUnit;
	}

	public void setNumberUnit(String numberUnit) {
		this.numberUnit = numberUnit;
	}

	public BigDecimal getTaxAmount() {
		return taxAmount;
	}

	public void setTaxAmount(BigDecimal taxAmount) {
		this.taxAmount = taxAmount;
	}

	public BigDecimal getDealAmountNotax() {
		return dealAmountNotax;
	}

	public void setDealAmountNotax(BigDecimal dealAmountNotax) {
		this.dealAmountNotax = dealAmountNotax;
	}

	public BigDecimal getDealAmount() {
		return dealAmount;
	}

	public void setDealAmount(BigDecimal dealAmount) {
		this.dealAmount = dealAmount;
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

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCompanyPhone() {
		return companyPhone;
	}

	public void setCompanyPhone(String companyPhone) {
		this.companyPhone = companyPhone;
	}

	public String getInvoiceNo() {
		return invoiceNo;
	}

	public void setInvoiceNo(String invoiceNo) {
		this.invoiceNo = invoiceNo;
	}

	public String getBillNo() {
		return billNo;
	}

	public void setBillNo(String billNo) {
		this.billNo = billNo;
	}

	public Date getReceiveDate() {
		return receiveDate;
	}

	public void setReceiveDate(Date receiveDate) {
		this.receiveDate = receiveDate;
	}

	public BigDecimal getReceiveAmount() {
		return receiveAmount;
	}

	public void setReceiveAmount(BigDecimal receiveAmount) {
		this.receiveAmount = receiveAmount;
	}

	public String getPayCompanyName() {
		return payCompanyName;
	}

	public void setPayCompanyName(String payCompanyName) {
		this.payCompanyName = payCompanyName;
	}

	public String getReceiveBillNo() {
		return receiveBillNo;
	}

	public void setReceiveBillNo(String receiveBillNo) {
		this.receiveBillNo = receiveBillNo;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Long getApproveId() {
		return approveId;
	}

	public void setApproveId(Long approveId) {
		this.approveId = approveId;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getFileId() {
		return fileId;
	}

	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

	public String getBusinessNo() {
		return businessNo;
	}

	public void setBusinessNo(String businessNo) {
		this.businessNo = businessNo;
	}

	public String getOurCompanyName() {
		return ourCompanyName;
	}

	public void setOurCompanyName(String ourCompanyName) {
		this.ourCompanyName = ourCompanyName;
	}

	public Boolean getCloseFlg() {
		return closeFlg;
	}

	public void setCloseFlg(Boolean closeFlg) {
		this.closeFlg = closeFlg;
	}
}
