package com.spt.bas.report.client.entity;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spt.tools.core.bean.PageSearchVo;
/**
 * 采购合同库存日明细报表 实体
 */
public class RptCtrDailyStockReport extends PageSearchVo{
	private Date buyContractTime;						//采购日期
	private String contractAttr;						//合同属性
	private String buyBusinessNo;						//采购合同号
	private String productName;							//货名
	private BigDecimal buyNumber = BigDecimal.ZERO;		//采购数量
	private BigDecimal buyPrice = BigDecimal.ZERO;		//采购单价
	private BigDecimal dealNumber = BigDecimal.ZERO;	//销售数量
	private String sellBusinessNo;						//销售合同号
	private Date sellContractTime;						//销售日期
	private BigDecimal remainNumber = BigDecimal.ZERO;	//剩余数量
	private String inWarehouseHours;					//在库时长
	private Long matchId;								//采购业务员Id
	private String matchName;							//采购业务员
	private Long deptId;								//部门
	private Long enterpriseId;							//企业账套Id
	private String team;								//小组
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date searchTime;
	public Date getBuyContractTime() {
		return buyContractTime;
	}
	public void setBuyContractTime(Date buyContractTime) {
		this.buyContractTime = buyContractTime;
	}
	public String getContractAttr() {
		return contractAttr;
	}
	public void setContractAttr(String contractAttr) {
		this.contractAttr = contractAttr;
	}
	public String getBuyBusinessNo() {
		return buyBusinessNo;
	}
	public void setBuyBusinessNo(String buyBusinessNo) {
		this.buyBusinessNo = buyBusinessNo;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public BigDecimal getBuyNumber() {
		return buyNumber;
	}
	public void setBuyNumber(BigDecimal buyNumber) {
		this.buyNumber = buyNumber;
	}
	public BigDecimal getBuyPrice() {
		return buyPrice;
	}
	public void setBuyPrice(BigDecimal buyPrice) {
		this.buyPrice = buyPrice;
	}
	public BigDecimal getDealNumber() {
		return dealNumber;
	}
	public void setDealNumber(BigDecimal dealNumber) {
		this.dealNumber = dealNumber;
	}
	public String getSellBusinessNo() {
		return sellBusinessNo;
	}
	public void setSellBusinessNo(String sellBusinessNo) {
		this.sellBusinessNo = sellBusinessNo;
	}
	public Date getSellContractTime() {
		return sellContractTime;
	}
	public void setSellContractTime(Date sellContractTime) {
		this.sellContractTime = sellContractTime;
	}
	public BigDecimal getRemainNumber() {
		return remainNumber;
	}
	public void setRemainNumber(BigDecimal remainNumber) {
		this.remainNumber = remainNumber;
	}
	public String getInWarehouseHours() {
		return inWarehouseHours;
	}
	public void setInWarehouseHours(String inWarehouseHours) {
		this.inWarehouseHours = inWarehouseHours;
	}
	public Long getMatchId() {
		return matchId;
	}
	public void setMatchId(Long matchId) {
		this.matchId = matchId;
	}
	public String getMatchName() {
		return matchName;
	}
	public void setMatchName(String matchName) {
		this.matchName = matchName;
	}
	public Long getDeptId() {
		return deptId;
	}
	public void setDeptId(Long deptId) {
		this.deptId = deptId;
	}
	public Long getEnterpriseId() {
		return enterpriseId;
	}
	public void setEnterpriseId(Long enterpriseId) {
		this.enterpriseId = enterpriseId;
	}
	public String getTeam() {
		return team;
	}
	public void setTeam(String team) {
		this.team = team;
	}
	public Date getSearchTime() {
		return searchTime;
	}
	public void setSearchTime(Date searchTime) {
		this.searchTime = searchTime;
	}
	
	
}
