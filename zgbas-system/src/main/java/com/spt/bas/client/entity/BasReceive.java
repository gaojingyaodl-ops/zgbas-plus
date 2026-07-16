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
import com.spt.tools.jpa.vo.IdEntity;

@Entity
@Table(name = "t_bas_receive")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class BasReceive extends IdEntity{

	/**
	 * 收款信息
	 */
	private static final long serialVersionUID = -3700421591076472685L;
	private Long contractId;			//合同Id
	private String businessNo;			//业务编号
	private String contractNo; 			//合同编号
	@DateTimeFormat(pattern="yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date receiveDate;			//收款日期(应收日期)
	private Long companyId;				//公司Id
	private String companyName;			//公司名称
	private String productName; 		//商品名称
	private String productCode; 		//商品代码
	private BigDecimal receiveAmount;	//收款金额
	private String inInvoiceNo; 		//发票号码
	@DateTimeFormat(pattern="yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date inInvoiceDate; 		//开票日期
	private String inBillNo;    		//记账凭证号
	private String status;      		//状态		N-新增，D-完成
	private String receiveType; 		//收款类型		B-定金，R-余款，A-全款，Z-逐笔
	@JsonSerialize(using = ToStringSerializer.class)
	private Boolean closeFlg;			//是否闭口业务
	private String remark;				//备注
	public Long getContractId() {
		return contractId;
	}
	public void setContractId(Long contractId) {
		this.contractId = contractId;
	}
	public String getBusinessNo() {
		return businessNo;
	}
	public void setBusinessNo(String businessNo) {
		this.businessNo = businessNo;
	}
	public String getContractNo() {
		return contractNo;
	}
	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}
	
	public Date getReceiveDate() {
		return receiveDate;
	}
	public void setReceiveDate(Date receiveDate) {
		this.receiveDate = receiveDate;
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
	public BigDecimal getReceiveAmount() {
		return receiveAmount;
	}
	public void setReceiveAmount(BigDecimal receiveAmount) {
		this.receiveAmount = receiveAmount;
	}
	public String getInInvoiceNo() {
		return inInvoiceNo;
	}
	public void setInInvoiceNo(String inInvoiceNo) {
		this.inInvoiceNo = inInvoiceNo;
	}
	public Date getInInvoiceDate() {
		return inInvoiceDate;
	}
	public void setInInvoiceDate(Date inInvoiceDate) {
		this.inInvoiceDate = inInvoiceDate;
	}
	public String getInBillNo() {
		return inBillNo;
	}
	public void setInBillNo(String inBillNo) {
		this.inBillNo = inBillNo;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getReceiveType() {
		return receiveType;
	}
	public void setReceiveType(String receiveType) {
		this.receiveType = receiveType;
	}
	public Boolean getCloseFlg() {
		return closeFlg;
	}
	public void setCloseFlg(Boolean closeFlg) {
		this.closeFlg = closeFlg;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}

}
