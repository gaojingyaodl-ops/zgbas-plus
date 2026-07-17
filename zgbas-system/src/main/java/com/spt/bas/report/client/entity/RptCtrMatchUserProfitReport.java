package com.spt.bas.report.client.entity;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spt.tools.core.bean.PageSearchVo;
/**
 * 业务员毛利明细月报表 实体
 */
public class RptCtrMatchUserProfitReport extends PageSearchVo{
	private Long deptId;							//事业部Id
	private Long sellMatchId;						//销售业务员Id
	private String sellMatchName;					//销售业务员
	private String sellBusinessNo;					//销售合同号
	private String productName;						//货名
	private BigDecimal dealNumber = BigDecimal.ZERO;//数量
	private BigDecimal sellPrice = BigDecimal.ZERO;	//销售单价
	private String buyBusinessNo;					//采购合同号
	private BigDecimal buyPrice;					//采购单价
	private Long buyMatchId;						//采购业务员Id
	private String buyMatchName;					//采购业务员
	private Long enterpriseId;						//企业账套Id
	private String team;							//小组
	@DateTimeFormat(pattern = "yyyy-MM")
	@JsonFormat(pattern = "yyyy-MM", timezone = "GMT+08:00")
	private Date searchTime;
	public Long getDeptId() {
		return deptId;
	}
	public void setDeptId(Long deptId) {
		this.deptId = deptId;
	}
	public Long getSellMatchId() {
		return sellMatchId;
	}
	public void setSellMatchId(Long sellMatchId) {
		this.sellMatchId = sellMatchId;
	}
	public String getSellMatchName() {
		return sellMatchName;
	}
	public void setSellMatchName(String sellMatchName) {
		this.sellMatchName = sellMatchName;
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
	public BigDecimal getDealNumber() {
		return dealNumber;
	}
	public void setDealNumber(BigDecimal dealNumber) {
		this.dealNumber = dealNumber;
	}
	public BigDecimal getSellPrice() {
		return sellPrice;
	}
	public void setSellPrice(BigDecimal sellPrice) {
		this.sellPrice = sellPrice;
	}
	public String getBuyBusinessNo() {
		return buyBusinessNo;
	}
	public void setBuyBusinessNo(String buyBusinessNo) {
		this.buyBusinessNo = buyBusinessNo;
	}
	public BigDecimal getBuyPrice() {
		return buyPrice;
	}
	public void setBuyPrice(BigDecimal buyPrice) {
		this.buyPrice = buyPrice;
	}
	public Long getBuyMatchId() {
		return buyMatchId;
	}
	public void setBuyMatchId(Long buyMatchId) {
		this.buyMatchId = buyMatchId;
	}
	public String getBuyMatchName() {
		return buyMatchName;
	}
	public void setBuyMatchName(String buyMatchName) {
		this.buyMatchName = buyMatchName;
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
