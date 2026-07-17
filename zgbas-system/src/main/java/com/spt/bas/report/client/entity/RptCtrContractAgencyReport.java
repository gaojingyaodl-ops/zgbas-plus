package com.spt.bas.report.client.entity;
import java.math.BigDecimal;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
/**
 * 代采代销明细
 */
public class RptCtrContractAgencyReport {
	private String productName;													
	private String brandNumber;					
	private Long factoryId;						
	private String factoryName;					
	private Long buyCompanyId;					
	private String buyCompanyName;
	private BigDecimal buyPrice=BigDecimal.ZERO;
	private BigDecimal buyTotalNumber=BigDecimal.ZERO;
	private BigDecimal buyTotalAmount=BigDecimal.ZERO;
	private BigDecimal invoiceBillAmount=BigDecimal.ZERO;
	private Long sellCompanyId;
	private String sellCompanyName;
	private BigDecimal sellPrice=BigDecimal.ZERO;
	private BigDecimal sellTotalNumber=BigDecimal.ZERO;
	private BigDecimal sellTotalAmount=BigDecimal.ZERO;
	private BigDecimal receiveBillAmount=BigDecimal.ZERO;
	private String receiveBillNo;
	private String invoiceBillNo;
	private String businessNo;
	private Long buyId;
	private Long sellId;
	private Long enterpriseId;
	private String buyContractNo;			//采购合同号
	private String source;					//业务类型
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date buyContractDate;			//采购合同时间
	private String ourCompanyName;			//我方抬头
	@JsonSerialize(using = ToStringSerializer.class)
	private Boolean billFlg = false;		// 发票状态
	private String billFlgStr;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date lastBillDate;				//最后收票时间
	private String sellContractNo;			//销售合同号
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date sellContractDate;			//销售合同时间
	private BigDecimal warehouseNumber=BigDecimal.ZERO;		//出库数量
	private BigDecimal profit=BigDecimal.ZERO;				//毛利
	private Long relaId;
	private String sellContent;
	private String buyContent;
	private String appCode;
	@JsonSerialize(using = ToStringSerializer.class)
	private Boolean creditFlg;				//是否使用授信
	private String status;					//合同状态   C:作废
	private String buyDeliveryMode;			//采购方式
	private BigDecimal buyTransportAmount;	//采购运输费
	private BigDecimal buyWarehouseAmount;	//采购仓储费
	private String sellDeliveryMode;		//销售方式
	private BigDecimal sellTransportAmount;	//销售运输费
	private BigDecimal sellWarehouseAmount;	//销售仓储费
	private BigDecimal piccTotalNumber;		
	private BigDecimal piccTotalAmount;		
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date piccHappenDate;			//出货日
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date piccAccrualDate;			//应付款日
	private String businessType;
	private String calculateNo;				//二次结算单号
	private Long matchUserId;
	private Long buyMatchUserId;			//采购业务员ID
	private String buyMatchUserName;		//采购业务员
	private Long sellMatchUserId;			//销售业务员ID
	private String sellMatchUserName;		//销售业务员
	private BigDecimal dealNumber;			//数量
	private Long settlementId;				//结算单Id
	private String settlementNo;			//结算单编号
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
	private Date contractTime;				//合同签订日
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date payFullTime;				//预计结算日
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date realPayFullTime;			//实际结算日
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date deliveryTime;				//交货日
	private String settlementStatus;		//结算单状态	I-进行中， B-违约，D-已完成
	private BigDecimal premium;				//加价
	private BigDecimal transportPrice;		//运费
	private BigDecimal warehousePrice;		//仓储费
	private BigDecimal breachAmount;		//逾期罚息
	private BigDecimal marginAmount;		//毛利
	private BigDecimal printAmount;			//印花税
	private BigDecimal vatAmount;			//增值税
	private BigDecimal buyCommissionAmount;	//采购提成
	private BigDecimal sellCommissionAmount;//销售提成
	private BigDecimal manageCommissionAmount;//管理提成
	private BigDecimal serveAmount;			//服务费
	private BigDecimal serveRate;			//服务费率
	private BigDecimal companyCommissionAmount;//公司提成
	private BigDecimal breachRate;			//违约费率
	private BigDecimal insuranceRate;		//保费费率
	private BigDecimal businessCommissionRate;//业务提成比率
	private BigDecimal buyCommissionRate;	//采购提成比率
	private BigDecimal sellCommissionRate;	//销售提成比率
	private BigDecimal manageCommissionRate;//管理提成比率
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
	public Long getBuyCompanyId() {
		return buyCompanyId;
	}
	public void setBuyCompanyId(Long buyCompanyId) {
		this.buyCompanyId = buyCompanyId;
	}
	public String getBuyCompanyName() {
		return buyCompanyName;
	}
	public void setBuyCompanyName(String buyCompanyName) {
		this.buyCompanyName = buyCompanyName;
	}
	public BigDecimal getBuyPrice() {
		return buyPrice;
	}
	public void setBuyPrice(BigDecimal buyPrice) {
		this.buyPrice = buyPrice;
	}
	public BigDecimal getBuyTotalNumber() {
		return buyTotalNumber;
	}
	public void setBuyTotalNumber(BigDecimal buyTotalNumber) {
		this.buyTotalNumber = buyTotalNumber;
	}
	public BigDecimal getBuyTotalAmount() {
		return buyTotalAmount;
	}
	public void setBuyTotalAmount(BigDecimal buyTotalAmount) {
		this.buyTotalAmount = buyTotalAmount;
	}
	public BigDecimal getInvoiceBillAmount() {
		return invoiceBillAmount;
	}
	public void setInvoiceBillAmount(BigDecimal invoiceBillAmount) {
		this.invoiceBillAmount = invoiceBillAmount;
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
	public BigDecimal getSellPrice() {
		return sellPrice;
	}
	public void setSellPrice(BigDecimal sellPrice) {
		this.sellPrice = sellPrice;
	}
	public BigDecimal getSellTotalNumber() {
		return sellTotalNumber;
	}
	public void setSellTotalNumber(BigDecimal sellTotalNumber) {
		this.sellTotalNumber = sellTotalNumber;
	}
	public BigDecimal getSellTotalAmount() {
		return sellTotalAmount;
	}
	public void setSellTotalAmount(BigDecimal sellTotalAmount) {
		this.sellTotalAmount = sellTotalAmount;
	}
	public BigDecimal getReceiveBillAmount() {
		return receiveBillAmount;
	}
	public void setReceiveBillAmount(BigDecimal receiveBillAmount) {
		this.receiveBillAmount = receiveBillAmount;
	}
	public String getReceiveBillNo() {
		return receiveBillNo;
	}
	public void setReceiveBillNo(String receiveBillNo) {
		this.receiveBillNo = receiveBillNo;
	}
	public String getInvoiceBillNo() {
		return invoiceBillNo;
	}
	public void setInvoiceBillNo(String invoiceBillNo) {
		this.invoiceBillNo = invoiceBillNo;
	}
	public Long getBuyId() {
		return buyId;
	}
	public void setBuyId(Long buyId) {
		this.buyId = buyId;
	}
	public Long getSellId() {
		return sellId;
	}
	public void setSellId(Long sellId) {
		this.sellId = sellId;
	}
	public String getBusinessNo() {
		return businessNo;
	}
	public void setBusinessNo(String businessNo) {
		this.businessNo = businessNo;
	}
	public Long getEnterpriseId() {
		return enterpriseId;
	}
	public void setEnterpriseId(Long enterpriseId) {
		this.enterpriseId = enterpriseId;
	}
	public String getBuyContractNo() {
		return buyContractNo;
	}
	public void setBuyContractNo(String buyContractNo) {
		this.buyContractNo = buyContractNo;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public Date getBuyContractDate() {
		return buyContractDate;
	}
	public void setBuyContractDate(Date buyContractDate) {
		this.buyContractDate = buyContractDate;
	}
	public String getOurCompanyName() {
		return ourCompanyName;
	}
	public void setOurCompanyName(String ourCompanyName) {
		this.ourCompanyName = ourCompanyName;
	}
	public Boolean getBillFlg() {
		return billFlg;
	}
	public void setBillFlg(Boolean billFlg) {
		this.billFlg = billFlg;
	}
	public Date getLastBillDate() {
		return lastBillDate;
	}
	public void setLastBillDate(Date lastBillDate) {
		this.lastBillDate = lastBillDate;
	}
	public String getBuyMatchUserName() {
		return buyMatchUserName;
	}
	public void setBuyMatchUserName(String buyMatchUserName) {
		this.buyMatchUserName = buyMatchUserName;
	}
	public String getSellContractNo() {
		return sellContractNo;
	}
	public void setSellContractNo(String sellContractNo) {
		this.sellContractNo = sellContractNo;
	}
	public Date getSellContractDate() {
		return sellContractDate;
	}
	public void setSellContractDate(Date sellContractDate) {
		this.sellContractDate = sellContractDate;
	}
	public String getSellMatchUserName() {
		return sellMatchUserName;
	}
	public void setSellMatchUserName(String sellMatchUserName) {
		this.sellMatchUserName = sellMatchUserName;
	}
	public BigDecimal getWarehouseNumber() {
		return warehouseNumber;
	}
	public void setWarehouseNumber(BigDecimal warehouseNumber) {
		this.warehouseNumber = warehouseNumber;
	}
	public BigDecimal getProfit() {
		return profit;
	}
	public void setProfit(BigDecimal profit) {
		this.profit = profit;
	}
	public String getBillFlgStr() {
		return billFlgStr;
	}
	public void setBillFlgStr(String billFlgStr) {
		this.billFlgStr = billFlgStr;
	}
	public Long getRelaId() {
		return relaId;
	}
	public void setRelaId(Long relaId) {
		this.relaId = relaId;
	}
	public String getSellContent() {
		return sellContent;
	}
	public void setSellContent(String sellContent) {
		this.sellContent = sellContent;
	}
	public String getBuyContent() {
		return buyContent;
	}
	public void setBuyContent(String buyContent) {
		this.buyContent = buyContent;
	}
	public String getAppCode() {
		return appCode;
	}
	public void setAppCode(String appCode) {
		this.appCode = appCode;
	}
	public Boolean getCreditFlg() {
		return creditFlg;
	}
	public void setCreditFlg(Boolean creditFlg) {
		this.creditFlg = creditFlg;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getBuyDeliveryMode() {
		return buyDeliveryMode;
	}
	public void setBuyDeliveryMode(String buyDeliveryMode) {
		this.buyDeliveryMode = buyDeliveryMode;
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
	public String getSellDeliveryMode() {
		return sellDeliveryMode;
	}
	public void setSellDeliveryMode(String sellDeliveryMode) {
		this.sellDeliveryMode = sellDeliveryMode;
	}
	public BigDecimal getSellTransportAmount() {
		return sellTransportAmount;
	}
	public void setSellTransportAmount(BigDecimal sellTransportAmount) {
		this.sellTransportAmount = sellTransportAmount;
	}
	public BigDecimal getSellWarehouseAmount() {
		return sellWarehouseAmount;
	}
	public void setSellWarehouseAmount(BigDecimal sellWarehouseAmount) {
		this.sellWarehouseAmount = sellWarehouseAmount;
	}
	public BigDecimal getPiccTotalNumber() {
		return piccTotalNumber;
	}
	public void setPiccTotalNumber(BigDecimal piccTotalNumber) {
		this.piccTotalNumber = piccTotalNumber;
	}
	public BigDecimal getPiccTotalAmount() {
		return piccTotalAmount;
	}
	public void setPiccTotalAmount(BigDecimal piccTotalAmount) {
		this.piccTotalAmount = piccTotalAmount;
	}
	public Date getPiccHappenDate() {
		return piccHappenDate;
	}
	public void setPiccHappenDate(Date piccHappenDate) {
		this.piccHappenDate = piccHappenDate;
	}
	public Date getPiccAccrualDate() {
		return piccAccrualDate;
	}
	public void setPiccAccrualDate(Date piccAccrualDate) {
		this.piccAccrualDate = piccAccrualDate;
	}
	public String getBusinessType() {
		return businessType;
	}
	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}
	public String getCalculateNo() {
		return calculateNo;
	}
	public void setCalculateNo(String calculateNo) {
		this.calculateNo = calculateNo;
	}
//	public Long getMatchUserId() {
//		return matchUserId;
//	}
//	public void setMatchUserId(Long matchUserId) {
//		this.matchUserId = matchUserId;
//	}
	public Long getSettlementId() {
		return settlementId;
	}
	public void setSettlementId(Long settlementId) {
		this.settlementId = settlementId;
	}
	public Date getContractTime() {
		return contractTime;
	}
	public void setContractTime(Date contractTime) {
		this.contractTime = contractTime;
	}
	public Date getPayFullTime() {
		return payFullTime;
	}
	public void setPayFullTime(Date payFullTime) {
		this.payFullTime = payFullTime;
	}
	public Date getRealPayFullTime() {
		return realPayFullTime;
	}
	public void setRealPayFullTime(Date realPayFullTime) {
		this.realPayFullTime = realPayFullTime;
	}
	public Date getDeliveryTime() {
		return deliveryTime;
	}
	public void setDeliveryTime(Date deliveryTime) {
		this.deliveryTime = deliveryTime;
	}
	public String getSettlementStatus() {
		return settlementStatus;
	}
	public void setSettlementStatus(String settlementStatus) {
		this.settlementStatus = settlementStatus;
	}
	public BigDecimal getPremium() {
		return premium;
	}
	public void setPremium(BigDecimal premium) {
		this.premium = premium;
	}
	public BigDecimal getTransportPrice() {
		return transportPrice;
	}
	public void setTransportPrice(BigDecimal transportPrice) {
		this.transportPrice = transportPrice;
	}
	public BigDecimal getWarehousePrice() {
		return warehousePrice;
	}
	public void setWarehousePrice(BigDecimal warehousePrice) {
		this.warehousePrice = warehousePrice;
	}
	public BigDecimal getBreachAmount() {
		return breachAmount;
	}
	public void setBreachAmount(BigDecimal breachAmount) {
		this.breachAmount = breachAmount;
	}
	public BigDecimal getMarginAmount() {
		return marginAmount;
	}
	public void setMarginAmount(BigDecimal marginAmount) {
		this.marginAmount = marginAmount;
	}
	public BigDecimal getPrintAmount() {
		return printAmount;
	}
	public void setPrintAmount(BigDecimal printAmount) {
		this.printAmount = printAmount;
	}
	public BigDecimal getVatAmount() {
		return vatAmount;
	}
	public void setVatAmount(BigDecimal vatAmount) {
		this.vatAmount = vatAmount;
	}
	public BigDecimal getBuyCommissionAmount() {
		return buyCommissionAmount;
	}
	public void setBuyCommissionAmount(BigDecimal buyCommissionAmount) {
		this.buyCommissionAmount = buyCommissionAmount;
	}
	public BigDecimal getSellCommissionAmount() {
		return sellCommissionAmount;
	}
	public void setSellCommissionAmount(BigDecimal sellCommissionAmount) {
		this.sellCommissionAmount = sellCommissionAmount;
	}
	public BigDecimal getManageCommissionAmount() {
		return manageCommissionAmount;
	}
	public void setManageCommissionAmount(BigDecimal manageCommissionAmount) {
		this.manageCommissionAmount = manageCommissionAmount;
	}
	public BigDecimal getServeAmount() {
		return serveAmount;
	}
	public void setServeAmount(BigDecimal serveAmount) {
		this.serveAmount = serveAmount;
	}
	public BigDecimal getCompanyCommissionAmount() {
		return companyCommissionAmount;
	}
	public void setCompanyCommissionAmount(BigDecimal companyCommissionAmount) {
		this.companyCommissionAmount = companyCommissionAmount;
	}
	public BigDecimal getDealNumber() {
		return dealNumber;
	}
	public void setDealNumber(BigDecimal dealNumber) {
		this.dealNumber = dealNumber;
	}
	public BigDecimal getServeRate() {
		return serveRate;
	}
	public void setServeRate(BigDecimal serveRate) {
		this.serveRate = serveRate;
	}
	public BigDecimal getBreachRate() {
		return breachRate;
	}
	public void setBreachRate(BigDecimal breachRate) {
		this.breachRate = breachRate;
	}
	public BigDecimal getInsuranceRate() {
		return insuranceRate;
	}
	public void setInsuranceRate(BigDecimal insuranceRate) {
		this.insuranceRate = insuranceRate;
	}
	public BigDecimal getBusinessCommissionRate() {
		return businessCommissionRate;
	}
	public void setBusinessCommissionRate(BigDecimal businessCommissionRate) {
		this.businessCommissionRate = businessCommissionRate;
	}
	public BigDecimal getBuyCommissionRate() {
		return buyCommissionRate;
	}
	public void setBuyCommissionRate(BigDecimal buyCommissionRate) {
		this.buyCommissionRate = buyCommissionRate;
	}
	public BigDecimal getSellCommissionRate() {
		return sellCommissionRate;
	}
	public void setSellCommissionRate(BigDecimal sellCommissionRate) {
		this.sellCommissionRate = sellCommissionRate;
	}
	public BigDecimal getManageCommissionRate() {
		return manageCommissionRate;
	}
	public void setManageCommissionRate(BigDecimal manageCommissionRate) {
		this.manageCommissionRate = manageCommissionRate;
	}
	public Long getBuyMatchUserId() {
		return buyMatchUserId;
	}
	public void setBuyMatchUserId(Long buyMatchUserId) {
		this.buyMatchUserId = buyMatchUserId;
	}
	public Long getSellMatchUserId() {
		return sellMatchUserId;
	}
	public void setSellMatchUserId(Long sellMatchUserId) {
		this.sellMatchUserId = sellMatchUserId;
	}
	public String getSettlementNo() {
		return settlementNo;
	}
	public void setSettlementNo(String settlementNo) {
		this.settlementNo = settlementNo;
	}
	public Long getMatchUserId() {
		return matchUserId;
	}
	public void setMatchUserId(Long matchUserId) {
		this.matchUserId = matchUserId;
	}
	
}
