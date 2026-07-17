package com.spt.bas.report.client.entity;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
/**
 * 动态业务核算表
 *
 */
public class RptBusinessAccountReport {
	private String businessType;		//业务类型
	private String sellContractNo;		//销售合同号
	private String ourCompanyName;		//我方抬头
	private BigDecimal buyAmount;		//采购额
	private BigDecimal sellAmount;		//销售额
	private BigDecimal profit;			//贸易差价
	private BigDecimal grossMargin;		//毛利
	private BigDecimal margin;			//净毛利
	private BigDecimal realMargin;		//实际净毛利
	private BigDecimal earnings;		//合同收益
	private BigDecimal realEarnings;	//实际收益
	private BigDecimal notReceive;		//未收货款
	private BigDecimal dealedAmount; 	//已收/付金额
	private BigDecimal interestAmount; 	//罚息
	private BigDecimal receiveInterest; //已收罚息（费用登记已收罚息）
	private Integer orverdurDay;		//逾期天数
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone= "GMT+08:00")
	private Date sellContractTime;		//销售合同时间
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date receiveTime;			//收款日期
	private Long deptId;				//部门ID
	private String buyUserName;			//采购业务员
	private String sellUserName;		//销售业务员
	private String contractStatus;		//合同状态
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone= "GMT+08:00")
	private Date lastReceiveTime;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone= "GMT+08:00")
	private Date lastPayTime;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone= "GMT+08:00")
	private Date deliveryInTime;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone= "GMT+08:00")
	private Date deliveryOutTime;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone= "GMT+08:00")
	private Date buyPayFullTime;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone= "GMT+08:00")
	private Date sellPayFullTime;
	private BigDecimal warehousePrice;
	private BigDecimal buyTotalNumber;
	private BigDecimal sellTotalNumber;
	private BigDecimal dealNumber;
	private BigDecimal buyDealPrice;
	private BigDecimal sellDealPrice;
	private BigDecimal feeAmount;
	private BigDecimal buyTransportAmount;// 采购物流费
	private BigDecimal buyWarehouseAmount;// 采购仓储费
	private BigDecimal sellTransportAmount;//销售物流费
	private BigDecimal sellWarehouseAmount;//销售仓储费
	private BigDecimal sellInterestAmount;//销售罚息
	private BigDecimal buyInterestAmount;//采购罚息
	private BigDecimal warehouseNoOutNum;//未出库数量
	private BigDecimal vatAmount;//增值税
	private BigDecimal extraAmount;//附加税
	private BigDecimal printAmount;//印花税
	private BigDecimal taxAmount;//税费
	private BigDecimal costAmount;//资金成本
	private BigDecimal buyOtherWarehouseAmount;//采购仓储运输费
	private BigDecimal sellOtherWarehouseAmount;//销售仓储运输费
	private BigDecimal contractDays;//合同天数
	private BigDecimal realContractDays;//实际合同天数
	private BigDecimal realTranAndWarehouseAmount;//实际不含税仓储运输费
	private Integer creditDays;//账期
	private BigDecimal sellInterest;//销售应付资金利息中间值
	private BigDecimal buyInterest;//采购应收资金利息中间值
	private BigDecimal realCostAmount;//实际资金成本
	
	public String getOurCompanyName() {
		return ourCompanyName;
	}
	public void setOurCompanyName(String ourCompanyName) {
		this.ourCompanyName = ourCompanyName;
	}
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
	public BigDecimal getBuyAmount() {
		return buyAmount;
	}
	public void setBuyAmount(BigDecimal buyAmount) {
		this.buyAmount = buyAmount;
	}
	public BigDecimal getSellAmount() {
		return sellAmount;
	}
	public void setSellAmount(BigDecimal sellAmount) {
		this.sellAmount = sellAmount;
	}
	public BigDecimal getProfit() {
		return profit;
	}
	public void setProfit(BigDecimal profit) {
		this.profit = profit;
	}
	public BigDecimal getEarnings() {
		return earnings;
	}
	public void setEarnings(BigDecimal earnings) {
		this.earnings = earnings;
	}
	public BigDecimal getRealEarnings() {
		return realEarnings;
	}
	public void setRealEarnings(BigDecimal realEarnings) {
		this.realEarnings = realEarnings;
	}
	public BigDecimal getNotReceive() {
		return notReceive;
	}
	public void setNotReceive(BigDecimal notReceive) {
		this.notReceive = notReceive;
	}
	public Integer getOrverdurDay() {
		return orverdurDay;
	}
	public void setOrverdurDay(Integer orverdurDay) {
		this.orverdurDay = orverdurDay;
	}
	public Date getSellContractTime() {
		return sellContractTime;
	}
	public void setSellContractTime(Date sellContractTime) {
		this.sellContractTime = sellContractTime;
	}
	public Date getReceiveTime() {
		return receiveTime;
	}
	public void setReceiveTime(Date receiveTime) {
		this.receiveTime = receiveTime;
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
	public BigDecimal getMargin() {
		return margin;
	}
	public void setMargin(BigDecimal margin) {
		this.margin = margin;
	}
	public BigDecimal getDealedAmount() {
		return dealedAmount;
	}
	public void setDealedAmount(BigDecimal dealedAmount) {
		this.dealedAmount = dealedAmount;
	}
	public BigDecimal getInterestAmount() {
		return interestAmount;
	}
	public void setInterestAmount(BigDecimal interestAmount) {
		this.interestAmount = interestAmount;
	}
	public BigDecimal getReceiveInterest() {
		return receiveInterest;
	}
	public void setReceiveInterest(BigDecimal receiveInterest) {
		this.receiveInterest = receiveInterest;
	}
	public Date getLastReceiveTime() {
		return lastReceiveTime;
	}
	public void setLastReceiveTime(Date lastReceiveTime) {
		this.lastReceiveTime = lastReceiveTime;
	}
	public Date getLastPayTime() {
		return lastPayTime;
	}
	public void setLastPayTime(Date lastPayTime) {
		this.lastPayTime = lastPayTime;
	}
	public Date getDeliveryInTime() {
		return deliveryInTime;
	}
	public void setDeliveryInTime(Date deliveryInTime) {
		this.deliveryInTime = deliveryInTime;
	}
	public Date getDeliveryOutTime() {
		return deliveryOutTime;
	}
	public void setDeliveryOutTime(Date deliveryOutTime) {
		this.deliveryOutTime = deliveryOutTime;
	}
	public Date getBuyPayFullTime() {
		return buyPayFullTime;
	}
	public void setBuyPayFullTime(Date buyPayFullTime) {
		this.buyPayFullTime = buyPayFullTime;
	}
	public Date getSellPayFullTime() {
		return sellPayFullTime;
	}
	public void setSellPayFullTime(Date sellPayFullTime) {
		this.sellPayFullTime = sellPayFullTime;
	}
	public BigDecimal getWarehousePrice() {
		return warehousePrice;
	}
	public void setWarehousePrice(BigDecimal warehousePrice) {
		this.warehousePrice = warehousePrice;
	}
	public BigDecimal getBuyTotalNumber() {
		return buyTotalNumber;
	}
	public void setBuyTotalNumber(BigDecimal buyTotalNumber) {
		this.buyTotalNumber = buyTotalNumber;
	}
	public BigDecimal getSellTotalNumber() {
		return sellTotalNumber;
	}
	public void setSellTotalNumber(BigDecimal sellTotalNumber) {
		this.sellTotalNumber = sellTotalNumber;
	}
	public BigDecimal getDealNumber() {
		return dealNumber;
	}
	public void setDealNumber(BigDecimal dealNumber) {
		this.dealNumber = dealNumber;
	}
	public BigDecimal getBuyDealPrice() {
		return buyDealPrice;
	}
	public void setBuyDealPrice(BigDecimal buyDealPrice) {
		this.buyDealPrice = buyDealPrice;
	}
	public BigDecimal getSellDealPrice() {
		return sellDealPrice;
	}
	public void setSellDealPrice(BigDecimal sellDealPrice) {
		this.sellDealPrice = sellDealPrice;
	}
	public BigDecimal getFeeAmount() {
		return feeAmount;
	}
	public void setFeeAmount(BigDecimal feeAmount) {
		this.feeAmount = feeAmount;
	}
	public BigDecimal getBuyTransportAmount() {
		return buyTransportAmount;
	}
	public void setBuyTransportAmount(BigDecimal buyTransportAmount) {
		this.buyTransportAmount = buyTransportAmount;
	}
	public BigDecimal getBuyWarehouseAmount() {
		return buyWarehouseAmount;
	}
	public void setBuyWarehouseAmount(BigDecimal buyWarehouseAmount) {
		this.buyWarehouseAmount = buyWarehouseAmount;
	}
	public BigDecimal getSellTransportAmount() {
		return sellTransportAmount;
	}
	public void setSellTransportAmount(BigDecimal sellTransportAmount) {
		this.sellTransportAmount = sellTransportAmount;
	}
	public BigDecimal getSellInterestAmount() {
		return sellInterestAmount;
	}
	public void setSellInterestAmount(BigDecimal sellInterestAmount) {
		this.sellInterestAmount = sellInterestAmount;
	}
	public BigDecimal getBuyInterestAmount() {
		return buyInterestAmount;
	}
	public void setBuyInterestAmount(BigDecimal buyInterestAmount) {
		this.buyInterestAmount = buyInterestAmount;
	}
	public BigDecimal getWarehouseNoOutNum() {
		return warehouseNoOutNum;
	}
	public void setWarehouseNoOutNum(BigDecimal warehouseNoOutNum) {
		this.warehouseNoOutNum = warehouseNoOutNum;
	}
	public BigDecimal getVatAmount() {
		return vatAmount;
	}
	public void setVatAmount(BigDecimal vatAmount) {
		this.vatAmount = vatAmount;
	}
	public BigDecimal getExtraAmount() {
		return extraAmount;
	}
	public void setExtraAmount(BigDecimal extraAmount) {
		this.extraAmount = extraAmount;
	}
	public BigDecimal getPrintAmount() {
		return printAmount;
	}
	public void setPrintAmount(BigDecimal printAmount) {
		this.printAmount = printAmount;
	}
	public BigDecimal getTaxAmount() {
		return taxAmount;
	}
	public void setTaxAmount(BigDecimal taxAmount) {
		this.taxAmount = taxAmount;
	}
	public BigDecimal getCostAmount() {
		return costAmount;
	}
	public void setCostAmount(BigDecimal costAmount) {
		this.costAmount = costAmount;
	}
	public BigDecimal getBuyOtherWarehouseAmount() {
		return buyOtherWarehouseAmount;
	}
	public void setBuyOtherWarehouseAmount(BigDecimal buyOtherWarehouseAmount) {
		this.buyOtherWarehouseAmount = buyOtherWarehouseAmount;
	}
	public BigDecimal getSellOtherWarehouseAmount() {
		return sellOtherWarehouseAmount;
	}
	public void setSellOtherWarehouseAmount(BigDecimal sellOtherWarehouseAmount) {
		this.sellOtherWarehouseAmount = sellOtherWarehouseAmount;
	}
	public BigDecimal getSellWarehouseAmount() {
		return sellWarehouseAmount;
	}
	public void setSellWarehouseAmount(BigDecimal sellWarehouseAmount) {
		this.sellWarehouseAmount = sellWarehouseAmount;
	}
	public BigDecimal getContractDays() {
		return contractDays;
	}
	public void setContractDays(BigDecimal contractDays) {
		this.contractDays = contractDays;
	}
	public BigDecimal getRealContractDays() {
		return realContractDays;
	}
	public void setRealContractDays(BigDecimal realContractDays) {
		this.realContractDays = realContractDays;
	}
	public BigDecimal getGrossMargin() {
		return grossMargin;
	}
	public void setGrossMargin(BigDecimal grossMargin) {
		this.grossMargin = grossMargin;
	}
	public BigDecimal getRealMargin() {
		return realMargin;
	}
	public void setRealMargin(BigDecimal realMargin) {
		this.realMargin = realMargin;
	}
	public BigDecimal getRealTranAndWarehouseAmount() {
		return realTranAndWarehouseAmount;
	}
	public void setRealTranAndWarehouseAmount(BigDecimal realTranAndWarehouseAmount) {
		this.realTranAndWarehouseAmount = realTranAndWarehouseAmount;
	}
	public Integer getCreditDays() {
		return creditDays;
	}
	public void setCreditDays(Integer creditDays) {
		this.creditDays = creditDays;
	}
	public BigDecimal getSellInterest() {
		return sellInterest;
	}
	public void setSellInterest(BigDecimal sellInterest) {
		this.sellInterest = sellInterest;
	}
	public BigDecimal getBuyInterest() {
		return buyInterest;
	}
	public void setBuyInterest(BigDecimal buyInterest) {
		this.buyInterest = buyInterest;
	}
	public BigDecimal getRealCostAmount() {
		return realCostAmount;
	}
	public void setRealCostAmount(BigDecimal realCostAmount) {
		this.realCostAmount = realCostAmount;
	}
	
}
