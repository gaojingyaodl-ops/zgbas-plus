package com.spt.bas.report.client.vo;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spt.tools.core.bean.PageSearchVo;

public class RptReceiveDetailSearchVo extends PageSearchVo{
	private String isCancel;//是否作废
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date receiveTimeGTED;// 大于收款时间
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date receiveTimeLTD;// 小于收款时间
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date receiveDate; // 收款日期（默认当天）
	private String businessType;//业务类型
	private String contractNo; //合同编号
	private String companyName;//对方企业名称
	private String ourCompanyName;//我方企业名称
	private String source;//业务类型
	private Long enterpriseId;

	
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getIsCancel() {
		return isCancel;
	}
	public void setIsCancel(String isCancel) {
		this.isCancel = isCancel;
	}
	public Date getReceiveDate() {
		return receiveDate;
	}
	public void setReceiveDate(Date receiveDate) {
		this.receiveDate = receiveDate;
	}
	public String getBusinessType() {
		return businessType;
	}
	public void setBusinessType(String businessType) {
		this.businessType = businessType;
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
	public String getOurCompanyName() {
		return ourCompanyName;
	}
	public void setOurCompanyName(String ourCompanyName) {
		this.ourCompanyName = ourCompanyName;
	}
	public Long getEnterpriseId() {
		return enterpriseId;
	}
	public void setEnterpriseId(Long enterpriseId) {
		this.enterpriseId = enterpriseId;
	}
	public Date getReceiveTimeGTED() {
		return receiveTimeGTED;
	}
	public void setReceiveTimeGTED(Date receiveTimeGTED) {
		this.receiveTimeGTED = receiveTimeGTED;
	}
	public Date getReceiveTimeLTD() {
		return receiveTimeLTD;
	}
	public void setReceiveTimeLTD(Date receiveTimeLTD) {
		this.receiveTimeLTD = receiveTimeLTD;
	}
	
	
	
}
