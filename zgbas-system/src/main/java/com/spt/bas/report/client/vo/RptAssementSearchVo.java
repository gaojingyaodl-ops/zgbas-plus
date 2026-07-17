package com.spt.bas.report.client.vo;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spt.tools.core.bean.PageSearchVo;
public class RptAssementSearchVo extends PageSearchVo {
	private String sellContractNo;	//销售合同编号
	private String businessNo;		//业务编号
	private String brandNumber;		//牌号
	private String sellCompany;		//销售企业
	private String buyMatchName;	//采购业务员
	private String sellMatchName;
	private Long deptId;			//部门
	private String buyCompanyName;  //供货商
	private String sellCompanyName; //需货商
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date beginTime;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date endTime;
	private Long enterpriseId;
	private Long sellId;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date buyContractSatrtTime;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date buyContractEndTime;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date sellContractSatrtTime;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date sellContractEndTime;
	private String searchType;
	private String businessType;
	private String ourCompanyName;
	private Long matchUserId;
	private Boolean bkbAuditFlg;	//背靠背审核
	private Boolean dlkzAuditFlg;	//代理开证审核
	private Boolean dldcAuditFLg;	//国企代采审核
	private Boolean dlkzApplyFLg;	//代理开证申请
	private Boolean dldcApplyFLg;	//国企代采申请 
	public String getBusinessNo() {
		return businessNo;
	}
	public void setBusinessNo(String businessNo) {
		this.businessNo = businessNo;
	}
	public String getBrandNumber() {
		return brandNumber;
	}
	public void setBrandNumber(String brandNumber) {
		this.brandNumber = brandNumber;
	}
	public String getSellCompany() {
		return sellCompany;
	}
	public void setSellCompany(String sellCompany) {
		this.sellCompany = sellCompany;
	}
	
	public String getBuyMatchName() {
		return buyMatchName;
	}
	public void setBuyMatchName(String buyMatchName) {
		this.buyMatchName = buyMatchName;
	}
	public Long getDeptId() {
		return deptId;
	}
	public void setDeptId(Long deptId) {
		this.deptId = deptId;
	}
	public Date getBeginTime() {
		return beginTime;
	}
	public void setBeginTime(Date beginTime) {
		this.beginTime = beginTime;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	public String getSellMatchName() {
		return sellMatchName;
	}
	public void setSellMatchName(String sellMatchName) {
		this.sellMatchName = sellMatchName;
	}
	public String getBuyCompanyName() {
		return buyCompanyName;
	}
	public void setBuyCompanyName(String buyCompanyName) {
		this.buyCompanyName = buyCompanyName;
	}
	public String getSellCompanyName() {
		return sellCompanyName;
	}
	public void setSellCompanyName(String sellCompanyName) {
		this.sellCompanyName = sellCompanyName;
	}
	public Long getEnterpriseId() {
		return enterpriseId;
	}
	public void setEnterpriseId(Long enterpriseId) {
		this.enterpriseId = enterpriseId;
	}
	public Long getSellId() {
		return sellId;
	}
	public void setSellId(Long sellId) {
		this.sellId = sellId;
	}
	public Date getBuyContractSatrtTime() {
		return buyContractSatrtTime;
	}
	public void setBuyContractSatrtTime(Date buyContractSatrtTime) {
		this.buyContractSatrtTime = buyContractSatrtTime;
	}
	public Date getBuyContractEndTime() {
		return buyContractEndTime;
	}
	public void setBuyContractEndTime(Date buyContractEndTime) {
		this.buyContractEndTime = buyContractEndTime;
	}
	public Date getSellContractSatrtTime() {
		return sellContractSatrtTime;
	}
	public void setSellContractSatrtTime(Date sellContractSatrtTime) {
		this.sellContractSatrtTime = sellContractSatrtTime;
	}
	public Date getSellContractEndTime() {
		return sellContractEndTime;
	}
	public void setSellContractEndTime(Date sellContractEndTime) {
		this.sellContractEndTime = sellContractEndTime;
	}
	public String getSellContractNo() {
		return sellContractNo;
	}
	public void setSellContractNo(String sellContractNo) {
		this.sellContractNo = sellContractNo;
	}
	public String getSearchType() {
		return searchType;
	}
	public void setSearchType(String searchType) {
		this.searchType = searchType;
	}
	public String getBusinessType() {
		return businessType;
	}
	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}
	public String getOurCompanyName() {
		return ourCompanyName;
	}
	public void setOurCompanyName(String ourCompanyName) {
		this.ourCompanyName = ourCompanyName;
	}
	public Long getMatchUserId() {
		return matchUserId;
	}
	public void setMatchUserId(Long matchUserId) {
		this.matchUserId = matchUserId;
	}
	public Boolean getBkbAuditFlg() {
		return bkbAuditFlg;
	}
	public void setBkbAuditFlg(Boolean bkbAuditFlg) {
		this.bkbAuditFlg = bkbAuditFlg;
	}
	public Boolean getDlkzAuditFlg() {
		return dlkzAuditFlg;
	}
	public void setDlkzAuditFlg(Boolean dlkzAuditFlg) {
		this.dlkzAuditFlg = dlkzAuditFlg;
	}
	public Boolean getDldcAuditFLg() {
		return dldcAuditFLg;
	}
	public void setDldcAuditFLg(Boolean dldcAuditFLg) {
		this.dldcAuditFLg = dldcAuditFLg;
	}
	public Boolean getDlkzApplyFLg() {
		return dlkzApplyFLg;
	}
	public void setDlkzApplyFLg(Boolean dlkzApplyFLg) {
		this.dlkzApplyFLg = dlkzApplyFLg;
	}
	public Boolean getDldcApplyFLg() {
		return dldcApplyFLg;
	}
	public void setDldcApplyFLg(Boolean dldcApplyFLg) {
		this.dldcApplyFLg = dldcApplyFLg;
	}
	
}
