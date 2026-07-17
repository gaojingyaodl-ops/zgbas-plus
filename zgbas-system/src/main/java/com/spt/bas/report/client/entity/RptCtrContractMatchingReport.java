package com.spt.bas.report.client.entity;
/**
 * 撮合考核
 */

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

public class RptCtrContractMatchingReport {
	private Long sellId;
	private Long buyId;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date sellPayTime;
	private String productName;
	private String brandNumber;
	private Long factoryId;
	private String factoryName;
	private BigDecimal sellNumber=BigDecimal.ZERO;
	private BigDecimal sellPrice;
	private Long sellMatchId;
	private String sellMatchName;
	private String buyMatchName;
	private String buyCompanyName;
	private Long buyCompanyId;
	private BigDecimal buyPrice;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date buyPayTime;
	private Long sellCompanyId;
	private String sellCompanyName;
	private Long deptId;
	private BigDecimal buyNumber;
	private BigDecimal sellTransportAmount;
	private BigDecimal buyTransportAmount;
	private BigDecimal transportAmount=BigDecimal.ZERO;//运费
	private BigDecimal balance=BigDecimal.ZERO; //差额
	private BigDecimal profit=BigDecimal.ZERO; //毛利
	private BigDecimal bountyAmoun=BigDecimal.ZERO; //奖励金
	private BigDecimal bounty=BigDecimal.ZERO; //奖励
	private Long enterpriseId;
	public Long getSellId() {
		return sellId;
	}
	public void setSellId(Long sellId) {
		this.sellId = sellId;
	}
	public Long getBuyId() {
		return buyId;
	}
	public void setBuyId(Long buyId) {
		this.buyId = buyId;
	}
	public Date getSellPayTime() {
		return sellPayTime;
	}
	public void setSellPayTime(Date sellPayTime) {
		this.sellPayTime = sellPayTime;
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
	public Long getFactoryId() {
		return factoryId;
	}
	public void setFactoryId(Long factoryId) {
		this.factoryId = factoryId;
	}
	public String getFactoryName() {
		return factoryName;
	}
	public void setFactoryName(String factoryName) {
		this.factoryName = factoryName;
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
	public String getBuyMatchName() {
		return buyMatchName;
	}
	public void setBuyMatchName(String buyMatchName) {
		this.buyMatchName = buyMatchName;
	}
	public String getBuyCompanyName() {
		return buyCompanyName;
	}
	public void setBuyCompanyName(String buyCompanyName) {
		this.buyCompanyName = buyCompanyName;
	}
	public Long getBuyCompanyId() {
		return buyCompanyId;
	}
	public void setBuyCompanyId(Long buyCompanyId) {
		this.buyCompanyId = buyCompanyId;
	}
	public BigDecimal getBuyPrice() {
		return buyPrice;
	}
	public void setBuyPrice(BigDecimal buyPrice) {
		this.buyPrice = buyPrice;
	}
	public Date getBuyPayTime() {
		return buyPayTime;
	}
	public void setBuyPayTime(Date buyPayTime) {
		this.buyPayTime = buyPayTime;
	}
	public Long getSellCompanyId() {
		return sellCompanyId;
	}
	public void setSellCompanyId(Long sellCompanyId) {
		this.sellCompanyId = sellCompanyId;
	}
	public String getSellCompanyName() {
		return sellCompanyName;
	}
	public void setSellCompanyName(String sellCompanyName) {
		this.sellCompanyName = sellCompanyName;
	}
	public Long getDeptId() {
		return deptId;
	}
	public void setDeptId(Long deptId) {
		this.deptId = deptId;
	}
	public BigDecimal getBuyNumber() {
		return buyNumber;
	}
	public void setBuyNumber(BigDecimal buyNumber) {
		this.buyNumber = buyNumber;
	}
	public BigDecimal getSellTransportAmount() {
		return sellTransportAmount;
	}
	public void setSellTransportAmount(BigDecimal sellTransportAmount) {
		this.sellTransportAmount = sellTransportAmount;
	}
	public BigDecimal getBuyTransportAmount() {
		return buyTransportAmount;
	}
	public void setBuyTransportAmount(BigDecimal buyTransportAmount) {
		this.buyTransportAmount = buyTransportAmount;
	}
	public BigDecimal getTransportAmount() {
		return transportAmount;
	}
	public void setTransportAmount(BigDecimal transportAmount) {
		this.transportAmount = transportAmount;
	}
	public BigDecimal getBalance() {
		return balance;
	}
	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}
	public BigDecimal getProfit() {
		return profit;
	}
	public void setProfit(BigDecimal profit) {
		this.profit = profit;
	}
	public BigDecimal getBountyAmoun() {
		return bountyAmoun;
	}
	public void setBountyAmoun(BigDecimal bountyAmoun) {
		this.bountyAmoun = bountyAmoun;
	}
	public BigDecimal getBounty() {
		return bounty;
	}
	public void setBounty(BigDecimal bounty) {
		this.bounty = bounty;
	}
	public Long getEnterpriseId() {
		return enterpriseId;
	}
	public void setEnterpriseId(Long enterpriseId) {
		this.enterpriseId = enterpriseId;
	}
	
}
