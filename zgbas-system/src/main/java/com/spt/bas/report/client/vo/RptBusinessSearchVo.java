package com.spt.bas.report.client.vo;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spt.tools.core.bean.PageSearchVo;

public class RptBusinessSearchVo extends PageSearchVo{
	private String businessType;		//业务类型-小类
	private String business;			//业务类型-大类
	private String sellContractNo;		//销售合同号
	private Long deptId;				//部门ID
	private String buyUserName;			//采购业务员
	private String sellUserName;		//销售业务员
	private String contractStatus;		//合同状态
	private Long enterpriseId;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date sellContractStartTime;	//销售合同开始时间
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone= "GMT+08:00")
	private Date sellContractEndTime;	//销售合同结束时间
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date receiveStartTime;		//收款开始时间
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date receiveEndTime;		//收款结束时间
	public String getBusinessType() {
		return businessType;
	}
	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}
	public String getSellContractNo() {
		return sellContractNo;
	}
	public void setSellContractNo(String sellContractNo) {
		this.sellContractNo = sellContractNo;
	}
	public Long getDeptId() {
		return deptId;
	}
	public void setDeptId(Long deptId) {
		this.deptId = deptId;
	}
	public String getBuyUserName() {
		return buyUserName;
	}
	public void setBuyUserName(String buyUserName) {
		this.buyUserName = buyUserName;
	}
	public String getSellUserName() {
		return sellUserName;
	}
	public void setSellUserName(String sellUserName) {
		this.sellUserName = sellUserName;
	}
	public String getContractStatus() {
		return contractStatus;
	}
	public void setContractStatus(String contractStatus) {
		this.contractStatus = contractStatus;
	}
	public Long getEnterpriseId() {
		return enterpriseId;
	}
	public void setEnterpriseId(Long enterpriseId) {
		this.enterpriseId = enterpriseId;
	}
	public String getBusiness() {
		return business;
	}
	public void setBusiness(String business) {
		this.business = business;
	}
	public Date getSellContractStartTime() {
		return sellContractStartTime;
	}
	public void setSellContractStartTime(Date sellContractStartTime) {
		this.sellContractStartTime = sellContractStartTime;
	}
	public Date getSellContractEndTime() {
		return sellContractEndTime;
	}
	public void setSellContractEndTime(Date sellContractEndTime) {
		this.sellContractEndTime = sellContractEndTime;
	}
	public Date getReceiveStartTime() {
		return receiveStartTime;
	}
	public void setReceiveStartTime(Date receiveStartTime) {
		this.receiveStartTime = receiveStartTime;
	}
	public Date getReceiveEndTime() {
		return receiveEndTime;
	}
	public void setReceiveEndTime(Date receiveEndTime) {
		this.receiveEndTime = receiveEndTime;
	}
	
}
