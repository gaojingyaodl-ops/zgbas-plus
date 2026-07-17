package com.spt.bas.report.client.vo;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spt.tools.core.bean.PageSearchVo;

public class RptCtrContractReportSearchVo extends PageSearchVo{
	private Long enterpriseId;
	private String contractNo; //合同编号
	private String companyName;//对方企业名称
	private String matchUserName;//业务员
	private String contractAttr;//合同属性
	private String businessType;//业务类型
	private String type;//类型
	private String productName;//货名
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date contractTimeStart;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date contractTimeEnd;
	private String deliveryMode;
	private String payType;
	private String deliveryType;
	private String billType;
	private String ourCompanyName;
	private String searchType;
	private Long deptId;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date receiveTimeStart;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date receiveTimeEnd;
	public Long getEnterpriseId() {
		return enterpriseId;
	}
	public void setEnterpriseId(Long enterpriseId) {
		this.enterpriseId = enterpriseId;
	}
	public String getContractNo() {
		return contractNo;
	}
	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public String getMatchUserName() {
		return matchUserName;
	}
	public void setMatchUserName(String matchUserName) {
		this.matchUserName = matchUserName;
	}
	public String getContractAttr() {
		return contractAttr;
	}
	public void setContractAttr(String contractAttr) {
		this.contractAttr = contractAttr;
	}
	public String getBusinessType() {
		return businessType;
	}
	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public Date getContractTimeStart() {
		return contractTimeStart;
	}
	public void setContractTimeStart(Date contractTimeStart) {
		this.contractTimeStart = contractTimeStart;
	}
	public Date getContractTimeEnd() {
		return contractTimeEnd;
	}
	public void setContractTimeEnd(Date contractTimeEnd) {
		this.contractTimeEnd = contractTimeEnd;
	}
	public String getDeliveryMode() {
		return deliveryMode;
	}
	public void setDeliveryMode(String deliveryMode) {
		this.deliveryMode = deliveryMode;
	}
	public String getPayType() {
		return payType;
	}
	public void setPayType(String payType) {
		this.payType = payType;
	}
	public String getDeliveryType() {
		return deliveryType;
	}
	public void setDeliveryType(String deliveryType) {
		this.deliveryType = deliveryType;
	}
	public String getBillType() {
		return billType;
	}
	public void setBillType(String billType) {
		this.billType = billType;
	}
	public String getOurCompanyName() {
		return ourCompanyName;
	}
	public void setOurCompanyName(String ourCompanyName) {
		this.ourCompanyName = ourCompanyName;
	}
	public String getSearchType() {
		return searchType;
	}
	public void setSearchType(String searchType) {
		this.searchType = searchType;
	}
	public Long getDeptId() {
		return deptId;
	}
	public void setDeptId(Long deptId) {
		this.deptId = deptId;
	}
	public Date getReceiveTimeStart() {
		return receiveTimeStart;
	}
	public void setReceiveTimeStart(Date receiveTimeStart) {
		this.receiveTimeStart = receiveTimeStart;
	}
	public Date getReceiveTimeEnd() {
		return receiveTimeEnd;
	}
	public void setReceiveTimeEnd(Date receiveTimeEnd) {
		this.receiveTimeEnd = receiveTimeEnd;
	}
	
}
