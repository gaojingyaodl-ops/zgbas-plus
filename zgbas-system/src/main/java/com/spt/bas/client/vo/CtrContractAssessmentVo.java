package com.spt.bas.client.vo;

import java.math.BigDecimal;
import java.util.Date;
/**
 *	自营考核
 */
public class CtrContractAssessmentVo{
	private String businessNo;//业务编号
	private String productName;//商品名称
	private String brandNumber;//牌号
	private String factoryName;//厂商
	private BigDecimal salesNumber;//销售数量
	private String proCompanyName;//采购企业
	private BigDecimal dealPrice;//采购单价
	private Date payMoneyTime;//付款日期            -->payTime采购
	private String companyName;//销售企业
	private BigDecimal salesPrice;//销售单价
	private Date collectionTime;//收款日期       -->payTime销售
	private BigDecimal differenceMoney;//差额
	private BigDecimal transportAmount;//运输费
	private BigDecimal matchingProfits;//毛利
	private BigDecimal rewardAmount;//奖励金额
	private BigDecimal reward;//奖励
	private String procurementName;//采购员
	private String salesName;//销售员
	private Date contractTime;//合同时间  采购日期	
	private BigDecimal dealNumber;//采购额
	private BigDecimal procurementMoney;//销售员毛利
	private BigDecimal totPrice;//销售额
	private Long deptId;//部门ID
	private Date deliveryDateFrom;//收货日期
	private BigDecimal salesMoney;//采购员毛利
	
	
	public BigDecimal getDifferenceMoney() {
		return differenceMoney;
	}
	public void setDifferenceMoney(BigDecimal differenceMoney) {
		this.differenceMoney = differenceMoney;
	}
	public BigDecimal getMatchingProfits() {
		return matchingProfits;
	}
	public void setMatchingProfits(BigDecimal matchingProfits) {
		this.matchingProfits = matchingProfits;
	}
	public BigDecimal getRewardAmount() {
		return rewardAmount;
	}
	public void setRewardAmount(BigDecimal rewardAmount) {
		this.rewardAmount = rewardAmount;
	}
	public BigDecimal getReward() {
		return reward;
	}
	public void setReward(BigDecimal reward) {
		this.reward = reward;
	}
	public Date getCollectionTime() {
		return collectionTime;
	}
	public void setCollectionTime(Date collectionTime) {
		this.collectionTime = collectionTime;
	}
	public BigDecimal getTransportAmount() {
		return transportAmount;
	}
	public void setTransportAmount(BigDecimal transportAmount) {
		this.transportAmount = transportAmount;
	}
	public Date getDeliveryDateFrom() {
		return deliveryDateFrom;
	}
	public void setDeliveryDateFrom(Date deliveryDateFrom) {
		this.deliveryDateFrom = deliveryDateFrom;
	}
	public Date getPayMoneyTime() {
		return payMoneyTime;
	}
	public void setPayMoneyTime(Date payMoneyTime) {
		this.payMoneyTime = payMoneyTime;
	}
	public String getSalesName() {
		return salesName;
	}
	public void setSalesName(String salesName) {
		this.salesName = salesName;
	}
	public Long getDeptId() {
		return deptId;
	}
	public void setDeptId(Long deptId) {
		this.deptId = deptId;
	}
	public String getBusinessNo() {
		return businessNo;
	}
	public void setBusinessNo(String businessNo) {
		this.businessNo = businessNo;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public String getProcurementName() {
		return procurementName;
	}
	public void setProcurementName(String procurementName) {
		this.procurementName = procurementName;
	}
	public BigDecimal getTotPrice() {
		return totPrice;
	}
	public void setTotPrice(BigDecimal totPrice) {
		this.totPrice = totPrice;
	}
	public BigDecimal getSalesPrice() {
		return salesPrice;
	}
	public void setSalesPrice(BigDecimal salesPrice) {
		this.salesPrice = salesPrice;
	}
	public String getProCompanyName() {
		return proCompanyName;
	}
	public void setProCompanyName(String proCompanyName) {
		this.proCompanyName = proCompanyName;
	}
	
	public Date getContractTime() {
		return contractTime;
	}
	public void setContractTime(Date contractTime) {
		this.contractTime = contractTime;
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
	
	public BigDecimal getSalesNumber() {
		return salesNumber;
	}
	public void setSalesNumber(BigDecimal salesNumber) {
		this.salesNumber = salesNumber;
	}
	public BigDecimal getDealPrice() {
		return dealPrice;
	}
	public void setDealPrice(BigDecimal dealPrice) {
		this.dealPrice = dealPrice;
	}
	public BigDecimal getDealNumber() {
		return dealNumber;
	}
	public void setDealNumber(BigDecimal dealNumber) {
		this.dealNumber = dealNumber;
	}
	public BigDecimal getSalesMoney() {
		return salesMoney;
	}
	public void setSalesMoney(BigDecimal salesMoney) {
		this.salesMoney = salesMoney;
	}
	public BigDecimal getProcurementMoney() {
		return procurementMoney;
	}
	public void setProcurementMoney(BigDecimal procurementMoney) {
		this.procurementMoney = procurementMoney;
	}
	
	
}
