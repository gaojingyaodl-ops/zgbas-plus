package com.spt.bas.client.vo;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
/**
 * 接收saas成交合同Vo 
 *
 */
public class SaasContractRequestVo {
	public static final String ORDER_STATUS_HIS_D = "D";// 已成交
	public static final String ORDER_STATUS_HIS_P = "P";// 已付款、收款
	public static final String ORDER_STATUS_HIS_W = "W";// 已发货、收货
	public static final String ORDER_STATUS_HIS_B = "B";// 已开票、收票
	public static final String ORDER_STATUS_HIS_O = "O";// 已完成
	public static final String ORDER_STATUS_HIS_P_C = "PC";// 待收款

	private String content; 				// 电子合同
	private String appCode; 				// 应用Code
	@JsonSerialize(using = ToStringSerializer.class)
	private Boolean deliveryFlg = false;	// 是否是发货状态
	private String buyerId; 				// 买方用户id
	private String buyerCompanyTag; 		// 买方企业tag
	private String buyerCompanyName; 		// 买方企业名
	private String sellerId; 				// 卖方用户id
	private String sellerCompanyTag; 		// 卖方企业tag
	private String sellerCompanyName; 		// 卖方企业名
	private String productCode; 			// 商品代码
	private String productName; 			// 商品名称
	private String brandNumber; 			// 牌号
	private String factoryName; 			// 厂商
	private BigDecimal dealPrice; 			// 成交价
	private BigDecimal dealNumber; 			// 成交数量
	private String productQuality; 			// 质量标准
	private String deliveryPlace; 			// 交货地 发货地
	private String warehouse; 				// 仓库 自提或收货地
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date deliveryDate; 				// 交货期
	private String payMethod; 				// 支付方式 ：CiticTrust 信托转账,unLine 线下,citicTransfer 冻结转账
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date contractTime; 				// 成交时间
	private String useType; 				// 用途
	private String packageSpec; 			// 包装规格
	private String deliveryType; 			// 交货方式
	private String productAttr; 			// 交收方式 N,现货，F远期
	private BigDecimal priceTotal; 			// 总价 与合同模板一致
	private String bankStatus; 				// 资金状态：先款后货、先货后款,默认先货后款
	private String fileId; 					// 附件ID
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date needPayDate; 				// 应付款时间
	private String sellBuyFlag; 			// 买卖标识(S:卖,B:买)
	private Long sellUserId;				// 业务员ID
	private String sellUserName; 			// 销售业务员名称
	private String sellMobile; 				// 销售业务员电话
	@JsonSerialize(using = ToStringSerializer.class)
	private Boolean traderFlg = false; 		// 是否是供应商来的数据
	private BigDecimal acceptDealPrice; 	// 承兑价
	private BigDecimal electricityDealPrice;// 电汇价（承兑之外的价都是电汇价）
	@JsonSerialize(using = ToStringSerializer.class)
	private Boolean sendEndFlag = false; 	// 合同货物是否发货完毕
	private String sellerWarehouseNo; 		// 发货仓库编号
	private String buyerWarehouseNo; 		// 收货仓库编号
	private String orderType = "1"; 		// 订单类型：1,2,3
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date fromPeriodDate; 			// 长约周期
	private BigDecimal settleNumber; 		// 结算数量
	private BigDecimal settleTotalPrice = BigDecimal.ZERO;
	private BigDecimal settleDealPrice; 	// 结算单价
	
	private String contractNo; 				// 合同编号
	private String contractCurType; 		// 合同当前执行的状态
	private BigDecimal dealedAmount;		// 收付款金额
	private BigDecimal warehouseNumber;		// 出入库数量
	private BigDecimal billedAmount;		// 收开票金额

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getAppCode() {
		return appCode;
	}

	public void setAppCode(String appCode) {
		this.appCode = appCode;
	}
	

	public Boolean getDeliveryFlg() {
		return deliveryFlg;
	}

	public void setDeliveryFlg(Boolean deliveryFlg) {
		this.deliveryFlg = deliveryFlg;
	}

	public String getContractNo() {
		return contractNo;
	}

	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}

	public String getBuyerId() {
		return buyerId;
	}

	public void setBuyerId(String buyerId) {
		this.buyerId = buyerId;
	}

	public String getBuyerCompanyTag() {
		return buyerCompanyTag;
	}

	public void setBuyerCompanyTag(String buyerCompanyTag) {
		this.buyerCompanyTag = buyerCompanyTag;
	}

	public String getBuyerCompanyName() {
		return buyerCompanyName;
	}

	public void setBuyerCompanyName(String buyerCompanyName) {
		this.buyerCompanyName = buyerCompanyName;
	}

	public String getSellerId() {
		return sellerId;
	}

	public void setSellerId(String sellerId) {
		this.sellerId = sellerId;
	}

	public String getSellerCompanyTag() {
		return sellerCompanyTag;
	}

	public void setSellerCompanyTag(String sellerCompanyTag) {
		this.sellerCompanyTag = sellerCompanyTag;
	}

	public String getSellerCompanyName() {
		return sellerCompanyName;
	}

	public void setSellerCompanyName(String sellerCompanyName) {
		this.sellerCompanyName = sellerCompanyName;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
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

	public String getProductQuality() {
		return productQuality;
	}

	public void setProductQuality(String productQuality) {
		this.productQuality = productQuality;
	}

	public String getDeliveryPlace() {
		return deliveryPlace;
	}

	public void setDeliveryPlace(String deliveryPlace) {
		this.deliveryPlace = deliveryPlace;
	}

	public String getWarehouse() {
		return warehouse;
	}

	public void setWarehouse(String warehouse) {
		this.warehouse = warehouse;
	}

	public Date getDeliveryDate() {
		return deliveryDate;
	}

	public void setDeliveryDate(Date deliveryDate) {
		this.deliveryDate = deliveryDate;
	}

	public String getPayMethod() {
		return payMethod;
	}

	public void setPayMethod(String payMethod) {
		this.payMethod = payMethod;
	}

	public Date getContractTime() {
		return contractTime;
	}

	public void setContractTime(Date contractTime) {
		this.contractTime = contractTime;
	}

	public String getBrandNumber() {
		return brandNumber;
	}

	public void setBrandNumber(String brandNumber) {
		this.brandNumber = brandNumber;
	}

	public String getUseType() {
		return useType;
	}

	public void setUseType(String useType) {
		this.useType = useType;
	}

	public String getFactoryName() {
		return factoryName;
	}

	public void setFactoryName(String factoryName) {
		this.factoryName = factoryName;
	}

	public String getPackageSpec() {
		return packageSpec;
	}

	public void setPackageSpec(String packageSpec) {
		this.packageSpec = packageSpec;
	}

	public String getDeliveryType() {
		return deliveryType;
	}

	public void setDeliveryType(String deliveryType) {
		this.deliveryType = deliveryType;
	}

	public String getProductAttr() {
		return productAttr;
	}

	public void setProductAttr(String productAttr) {
		this.productAttr = productAttr;
	}

	public BigDecimal getPriceTotal() {
		return priceTotal;
	}

	public void setPriceTotal(BigDecimal priceTotal) {
		this.priceTotal = priceTotal;
	}

	public String getBankStatus() {
		return bankStatus;
	}

	public void setBankStatus(String bankStatus) {
		this.bankStatus = bankStatus;
	}

	public String getFileId() {
		return fileId;
	}

	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

	public Date getNeedPayDate() {
		return needPayDate;
	}

	public void setNeedPayDate(Date needPayDate) {
		this.needPayDate = needPayDate;
	}

	public String getSellBuyFlag() {
		return sellBuyFlag;
	}

	public void setSellBuyFlag(String sellBuyFlag) {
		this.sellBuyFlag = sellBuyFlag;
	}

	public String getSellUserName() {
		return sellUserName;
	}

	public void setSellUserName(String sellUserName) {
		this.sellUserName = sellUserName;
	}

	public String getSellMobile() {
		return sellMobile;
	}

	public void setSellMobile(String sellMobile) {
		this.sellMobile = sellMobile;
	}

	public Boolean getTraderFlg() {
		return traderFlg;
	}

	public void setTraderFlg(Boolean traderFlg) {
		this.traderFlg = traderFlg;
	}

	public BigDecimal getAcceptDealPrice() {
		return acceptDealPrice;
	}

	public void setAcceptDealPrice(BigDecimal acceptDealPrice) {
		this.acceptDealPrice = acceptDealPrice;
	}

	public BigDecimal getElectricityDealPrice() {
		return electricityDealPrice;
	}

	public void setElectricityDealPrice(BigDecimal electricityDealPrice) {
		this.electricityDealPrice = electricityDealPrice;
	}

	public Boolean getSendEndFlag() {
		return sendEndFlag;
	}

	public void setSendEndFlag(Boolean sendEndFlag) {
		this.sendEndFlag = sendEndFlag;
	}

	public String getSellerWarehouseNo() {
		return sellerWarehouseNo;
	}

	public void setSellerWarehouseNo(String sellerWarehouseNo) {
		this.sellerWarehouseNo = sellerWarehouseNo;
	}

	public String getBuyerWarehouseNo() {
		return buyerWarehouseNo;
	}

	public void setBuyerWarehouseNo(String buyerWarehouseNo) {
		this.buyerWarehouseNo = buyerWarehouseNo;
	}

	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	public Date getFromPeriodDate() {
		return fromPeriodDate;
	}

	public void setFromPeriodDate(Date fromPeriodDate) {
		this.fromPeriodDate = fromPeriodDate;
	}

	public BigDecimal getSettleNumber() {
		return settleNumber;
	}

	public void setSettleNumber(BigDecimal settleNumber) {
		this.settleNumber = settleNumber;
	}

	public BigDecimal getSettleTotalPrice() {
		return settleTotalPrice;
	}

	public void setSettleTotalPrice(BigDecimal settleTotalPrice) {
		this.settleTotalPrice = settleTotalPrice;
	}

	public BigDecimal getSettleDealPrice() {
		return settleDealPrice;
	}

	public void setSettleDealPrice(BigDecimal settleDealPrice) {
		this.settleDealPrice = settleDealPrice;
	}

	public String getContractCurType() {
		return contractCurType;
	}

	public void setContractCurType(String contractCurType) {
		this.contractCurType = contractCurType;
	}

	public Long getSellUserId() {
		return sellUserId;
	}

	public void setSellUserId(Long sellUserId) {
		this.sellUserId = sellUserId;
	}

	public BigDecimal getDealedAmount() {
		return dealedAmount;
	}

	public void setDealedAmount(BigDecimal dealedAmount) {
		this.dealedAmount = dealedAmount;
	}


	public BigDecimal getWarehouseNumber() {
		return warehouseNumber;
	}

	public void setWarehouseNumber(BigDecimal warehouseNumber) {
		this.warehouseNumber = warehouseNumber;
	}

	public BigDecimal getBilledAmount() {
		return billedAmount;
	}

	public void setBilledAmount(BigDecimal billedAmount) {
		this.billedAmount = billedAmount;
	}
	
}
