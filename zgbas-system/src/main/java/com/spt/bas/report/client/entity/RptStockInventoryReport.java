package com.spt.bas.report.client.entity;
import java.math.BigDecimal;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spt.tools.core.bean.PageSearchVo;
/**
 * 库存统计报表 
 */
public class RptStockInventoryReport extends PageSearchVo{
	private String contractNo;			//合同编号
	private String companyName;			//供货商
	private String productName;			//品名
	private String brandNumber;			//牌号
	private String factoryName;			//厂商
	private String warehouseName;		//仓库
	private String productAttr;			//货物类型 现/期货
	private String spotType;			//货权
	private String longFlg;				//是否长约
	private BigDecimal totalNumber = BigDecimal.ZERO;		//合同数量
	private BigDecimal soldNumber = BigDecimal.ZERO;		//已售数量
	private BigDecimal outSoldNumber = BigDecimal.ZERO;		//未售数量
	private BigDecimal dealedAmount = BigDecimal.ZERO;		//付款金额
	private BigDecimal occupation = BigDecimal.ZERO;		//占用资金
	private Long matchUserId;			//业务员Id
	private String matchUserName;		//业务员
	private String theirTeam;			//所属团队
	private Long deptId;				//部门
	private String deptName;			//部门名称
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date createDate;			//创建时间
	private Long enterpriseId;			//企业账套ID
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date beginTime;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date endTime;
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
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getBrandNumber() {
		return brandNumber;
	}
	public void setBrandNumber(String brandNumber) {
		this.brandNumber = brandNumber;
	}
	public String getFactoryName() {
		return factoryName;
	}
	public void setFactoryName(String factoryName) {
		this.factoryName = factoryName;
	}
	public String getWarehouseName() {
		return warehouseName;
	}
	public void setWarehouseName(String warehouseName) {
		this.warehouseName = warehouseName;
	}
	public String getProductAttr() {
		return productAttr;
	}
	public void setProductAttr(String productAttr) {
		this.productAttr = productAttr;
	}
	
	public String getSpotType() {
		return spotType;
	}
	public void setSpotType(String spotType) {
		this.spotType = spotType;
	}
	public String getLongFlg() {
		return longFlg;
	}
	public void setLongFlg(String longFlg) {
		this.longFlg = longFlg;
	}
	public BigDecimal getTotalNumber() {
		return totalNumber;
	}
	public void setTotalNumber(BigDecimal totalNumber) {
		this.totalNumber = totalNumber;
	}
	public BigDecimal getOutSoldNumber() {
		return outSoldNumber;
	}
	public void setOutSoldNumber(BigDecimal outSoldNumber) {
		this.outSoldNumber = outSoldNumber;
	}
	public BigDecimal getDealedAmount() {
		return dealedAmount;
	}
	public void setDealedAmount(BigDecimal dealedAmount) {
		this.dealedAmount = dealedAmount;
	}
	public BigDecimal getOccupation() {
		return occupation;
	}
	public void setOccupation(BigDecimal occupation) {
		this.occupation = occupation;
	}
	public String getMatchUserName() {
		return matchUserName;
	}
	public void setMatchUserName(String matchUserName) {
		this.matchUserName = matchUserName;
	}
	public Long getDeptId() {
		return deptId;
	}
	public void setDeptId(Long deptId) {
		this.deptId = deptId;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public Long getEnterpriseId() {
		return enterpriseId;
	}
	public void setEnterpriseId(Long enterpriseId) {
		this.enterpriseId = enterpriseId;
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
	public String getTheirTeam() {
		return theirTeam;
	}
	public void setTheirTeam(String theirTeam) {
		this.theirTeam = theirTeam;
	}
	public BigDecimal getSoldNumber() {
		return soldNumber;
	}
	public void setSoldNumber(BigDecimal soldNumber) {
		this.soldNumber = soldNumber;
	}
	public Long getMatchUserId() {
		return matchUserId;
	}
	public void setMatchUserId(Long matchUserId) {
		this.matchUserId = matchUserId;
	}
	public String getDeptName() {
		return deptName;
	}
	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}
	
}
