package com.spt.bas.report.client.entity;

import java.math.BigDecimal;
import java.sql.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

public class RptCtrContractReport {
	private Long id;						// 合同Id
	private String businessType;			// 业务类型
	private String contractType;			// 合同类型
	private String contractAttr;			// 合同属性
	private String contractNo;				// 合同编号
	private String productsName;			// 货名	
	private String fileId;					// 附件
	private String invoiceFileId;			// 收票附件
	private String amountFileId;			// 付款附件
	private String warehouseFileId;			// 收货附件
	private String doubleCheckFileId;		// 双签附件
	private String ourCompanyName;			// 我方抬头
	private String companyName;				// 对方企业名称
	private String deliveryMode;			// 交货方式
	private BigDecimal totalNumber;			// 合同数量
	private BigDecimal totalAmount;			// 合同总价
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date payBondTime;				// 付定金日期
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date payFullTime;				// 付全款日期
	private BigDecimal dealedAmount;		// 已付金额
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date lastPayDate;				// 付款时间
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date deliveryDateTo;			// 交货时间
	private BigDecimal warehouseNumber;		// 入库数量
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date lastDeliveryDate;			// 入库时间	
	private BigDecimal billedAmount;		// 收票金额
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date lastBillDate;				// 收票时间
	private String contractStatus;			// 合同状态
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date contractTime;				// 合同时间
	private Integer contractDifTime;		// 合同时长
	private String matchUserName;			// 业务员
	private BigDecimal expectDeliveryInNum;	// 预计应入库数量/多入库数量
	@JsonSerialize(using = ToStringSerializer.class)
	private Boolean exitContractText;		// 是否有电子合同
	@JsonSerialize(using = ToStringSerializer.class)
	private Boolean moreProduct;			// 是否存在两个及以上商品
	private BigDecimal unDealedAmount;		// 未收金额
	private String deptId;
	private BigDecimal receiveAmount;		// 收款金额
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date receiveDate;				// 收款日期
	@JsonSerialize(using = ToStringSerializer.class)
	private Boolean existDeliveryCancel;	//是否存在入库作废
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getBusinessType() {
		return businessType;
	}
	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}
	public String getContractAttr() {
		return contractAttr;
	}
	public void setContractAttr(String contractAttr) {
		this.contractAttr = contractAttr;
	}
	public String getProductsName() {
		return productsName;
	}
	public void setProductsName(String productsName) {
		this.productsName = productsName;
	}
	public String getFileId() {
		return fileId;
	}
	public void setFileId(String fileId) {
		this.fileId = fileId;
	}
	public String getInvoiceFileId() {
		return invoiceFileId;
	}
	public void setInvoiceFileId(String invoiceFileId) {
		this.invoiceFileId = invoiceFileId;
	}
	public String getAmountFileId() {
		return amountFileId;
	}
	public void setAmountFileId(String amountFileId) {
		this.amountFileId = amountFileId;
	}
	public String getWarehouseFileId() {
		return warehouseFileId;
	}
	public void setWarehouseFileId(String warehouseFileId) {
		this.warehouseFileId = warehouseFileId;
	}
	public String getDoubleCheckFileId() {
		return doubleCheckFileId;
	}
	public void setDoubleCheckFileId(String doubleCheckFileId) {
		this.doubleCheckFileId = doubleCheckFileId;
	}
	public String getOurCompanyName() {
		return ourCompanyName;
	}
	public void setOurCompanyName(String ourCompanyName) {
		this.ourCompanyName = ourCompanyName;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public String getDeliveryMode() {
		return deliveryMode;
	}
	public void setDeliveryMode(String deliveryMode) {
		this.deliveryMode = deliveryMode;
	}
	public BigDecimal getTotalNumber() {
		return totalNumber;
	}
	public void setTotalNumber(BigDecimal totalNumber) {
		this.totalNumber = totalNumber;
	}
	public BigDecimal getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}
	public Date getPayBondTime() {
		return payBondTime;
	}
	public void setPayBondTime(Date payBondTime) {
		this.payBondTime = payBondTime;
	}
	public Date getPayFullTime() {
		return payFullTime;
	}
	public void setPayFullTime(Date payFullTime) {
		this.payFullTime = payFullTime;
	}
	public BigDecimal getDealedAmount() {
		return dealedAmount;
	}
	public void setDealedAmount(BigDecimal dealedAmount) {
		this.dealedAmount = dealedAmount;
	}
	public Date getLastPayDate() {
		return lastPayDate;
	}
	public void setLastPayDate(Date lastPayDate) {
		this.lastPayDate = lastPayDate;
	}
	public Date getDeliveryDateTo() {
		return deliveryDateTo;
	}
	public void setDeliveryDateTo(Date deliveryDateTo) {
		this.deliveryDateTo = deliveryDateTo;
	}
	public BigDecimal getWarehouseNumber() {
		return warehouseNumber;
	}
	public void setWarehouseNumber(BigDecimal warehouseNumber) {
		this.warehouseNumber = warehouseNumber;
	}
	public Date getLastDeliveryDate() {
		return lastDeliveryDate;
	}
	public void setLastDeliveryDate(Date lastDeliveryDate) {
		this.lastDeliveryDate = lastDeliveryDate;
	}
	public BigDecimal getBilledAmount() {
		return billedAmount;
	}
	public void setBilledAmount(BigDecimal billedAmount) {
		this.billedAmount = billedAmount;
	}
	public Date getLastBillDate() {
		return lastBillDate;
	}
	public void setLastBillDate(Date lastBillDate) {
		this.lastBillDate = lastBillDate;
	}
	public String getContractStatus() {
		return contractStatus;
	}
	public void setContractStatus(String contractStatus) {
		this.contractStatus = contractStatus;
	}
	public Date getContractTime() {
		return contractTime;
	}
	public void setContractTime(Date contractTime) {
		this.contractTime = contractTime;
	}
	public Integer getContractDifTime() {
		return contractDifTime;
	}
	public void setContractDifTime(Integer contractDifTime) {
		this.contractDifTime = contractDifTime;
	}
	public String getMatchUserName() {
		return matchUserName;
	}
	public void setMatchUserName(String matchUserName) {
		this.matchUserName = matchUserName;
	}
	public BigDecimal getExpectDeliveryInNum() {
		return expectDeliveryInNum;
	}
	public void setExpectDeliveryInNum(BigDecimal expectDeliveryInNum) {
		this.expectDeliveryInNum = expectDeliveryInNum;
	}
	public String getContractNo() {
		return contractNo;
	}
	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}
	public Boolean getExitContractText() {
		return exitContractText;
	}
	public void setExitContractText(Boolean exitContractText) {
		this.exitContractText = exitContractText;
	}
	public Boolean getMoreProduct() {
		return moreProduct;
	}
	public void setMoreProduct(Boolean moreProduct) {
		this.moreProduct = moreProduct;
	}
	public String getDeptId() {
		return deptId;
	}
	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}
	public BigDecimal getUnDealedAmount() {
		return unDealedAmount;
	}
	public void setUnDealedAmount(BigDecimal unDealedAmount) {
		this.unDealedAmount = unDealedAmount;
	}
	public BigDecimal getReceiveAmount() {
		return receiveAmount;
	}
	public void setReceiveAmount(BigDecimal receiveAmount) {
		this.receiveAmount = receiveAmount;
	}
	public Date getReceiveDate() {
		return receiveDate;
	}
	public void setReceiveDate(Date receiveDate) {
		this.receiveDate = receiveDate;
	}
	public String getContractType() {
		return contractType;
	}
	public void setContractType(String contractType) {
		this.contractType = contractType;
	}
	public Boolean getExistDeliveryCancel() {
		return existDeliveryCancel;
	}
	public void setExistDeliveryCancel(Boolean existDeliveryCancel) {
		this.existDeliveryCancel = existDeliveryCancel;
	}
	
}
