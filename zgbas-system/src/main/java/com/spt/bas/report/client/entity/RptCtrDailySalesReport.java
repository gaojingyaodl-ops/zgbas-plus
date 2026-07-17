package com.spt.bas.report.client.entity;


import java.math.BigDecimal;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spt.tools.core.bean.PageSearchVo;

/**
 * 日销售明细 实体
 */
public class RptCtrDailySalesReport extends PageSearchVo{
	private Date contractTime;							//销售日期
	private String contractAttr;						//合同属性
	private String sellBusinessNo;						//合同号
	private String productName;							//销售货名
	private BigDecimal sellNumber = BigDecimal.ZERO;	//销售数量
	private BigDecimal sellPrice = BigDecimal.ZERO;		//销售单价
	private BigDecimal totalAmount = BigDecimal.ZERO;	//合同总额
	private String buyBusinessNo;						//采购合同号
	private BigDecimal spreadSell = BigDecimal.ZERO;	//销售差价
	private BigDecimal spreadTotal = BigDecimal.ZERO;	//差价总额
	private String sellMatchName;						//销售业务员
	private Long sellMatchId;							//销售业务员Id
	private String buyMatchName;						//采购业务员
	private Long buyMatchId;							//采购业务员Id
	private Long deptId;								//事业部
	private String team;								//小组
	private Long enterpriseId;							//合同账套Id
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date searchTime;							
	public Date getContractTime() {
		return contractTime;
	}
	public void setContractTime(Date contractTime) {
		this.contractTime = contractTime;
	}
	public String getContractAttr() {
		return contractAttr;
	}
	public void setContractAttr(String contractAttr) {
		this.contractAttr = contractAttr;
	}
	public String getSellBusinessNo() {
		return sellBusinessNo;
	}
	public void setSellBusinessNo(String sellBusinessNo) {
		this.sellBusinessNo = sellBusinessNo;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public BigDecimal getSellNumber() {
		return sellNumber;
	}
	public void setSellNumber(BigDecimal sellNumber) {
		this.sellNumber = sellNumber;
	}
	public BigDecimal getSellPrice() {
		return sellPrice;
	}
	public void setSellPrice(BigDecimal sellPrice) {
		this.sellPrice = sellPrice;
	}
	public BigDecimal getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}
	public String getBuyBusinessNo() {
		return buyBusinessNo;
	}
	public void setBuyBusinessNo(String buyBusinessNo) {
		this.buyBusinessNo = buyBusinessNo;
	}
	public BigDecimal getSpreadSell() {
		return spreadSell;
	}
	public void setSpreadSell(BigDecimal spreadSell) {
		this.spreadSell = spreadSell;
	}
	public BigDecimal getSpreadTotal() {
		return spreadTotal;
	}
	public void setSpreadTotal(BigDecimal spreadTotal) {
		this.spreadTotal = spreadTotal;
	}
	public String getSellMatchName() {
		return sellMatchName;
	}
	public void setSellMatchName(String sellMatchName) {
		this.sellMatchName = sellMatchName;
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
	public String getTeam() {
		return team;
	}
	public void setTeam(String team) {
		this.team = team;
	}
	public Long getEnterpriseId() {
		return enterpriseId;
	}
	public void setEnterpriseId(Long enterpriseId) {
		this.enterpriseId = enterpriseId;
	}
	public Long getSellMatchId() {
		return sellMatchId;
	}
	public void setSellMatchId(Long sellMatchId) {
		this.sellMatchId = sellMatchId;
	}
	public Long getBuyMatchId() {
		return buyMatchId;
	}
	public void setBuyMatchId(Long buyMatchId) {
		this.buyMatchId = buyMatchId;
	}
	public Date getSearchTime() {
		return searchTime;
	}
	public void setSearchTime(Date searchTime) {
		this.searchTime = searchTime;
	}
	
	
}
