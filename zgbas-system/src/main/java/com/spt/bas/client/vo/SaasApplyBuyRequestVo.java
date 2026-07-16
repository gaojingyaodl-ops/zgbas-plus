package com.spt.bas.client.vo;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

public class SaasApplyBuyRequestVo {
	private String orderId;//订单Id
	
	private Long companyId;			//企业uuid 
	
	private String productCode;		//商品代码
	
	private String productName;		//商品名称
	
	private String custPrice;		//公式价
	
	private String buySell;			//买卖区分： B:买；S：卖
	
	private String productPlace;	//产地
	
	private String productQuality;	//质量标准
	
	private String deliveryPlace;	//发货地
	
	private String warehouseAddr;	//仓库
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date deliveryDate;		//交货期
	
	private String payMethod;		//支付方式

	private Long createUserId;		//创建人id
	
	private String status;		//状态	0:未发布 1:已发布 2 已成交

	private String remark;			//备注
	
	private BigDecimal dealPrice = BigDecimal.ZERO;     //单价
	
	private BigDecimal dealNumber = new BigDecimal(0.00);     //数量
	
	private String deliveryDateStr; //交货期（数据字典） N 现货  F 远期
	
	private String brandNumber;			//牌号

	private String useType;				//用途
	
	private String factoryName;			//厂商
	
	private String packageSpec;			//包装规格
	
	private String deliveryType;		//交货方式
	
	private String deliverySpec;		//运输规格
	
	private String productAttr;			//交收方式
	
	private BigDecimal priceTotal;//总价
	
	private BigDecimal logisticsTotalPrice = BigDecimal.ZERO; //物流总价
	
	private BigDecimal logisticsDealPrice = BigDecimal.ZERO; //物流单价
	
	private String carType;//车型
	
	private String companyName;

	private String appCode;  
	
	private String targetWarehouse;
	
	private String contractNo;
	
	private String retCode;
	
	private String retMessage;
	
	private Long userId;
	
	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
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

	public String getCustPrice() {
		return custPrice;
	}

	public void setCustPrice(String custPrice) {
		this.custPrice = custPrice;
	}

	public String getBuySell() {
		return buySell;
	}

	public void setBuySell(String buySell) {
		this.buySell = buySell;
	}

	public String getProductPlace() {
		return productPlace;
	}

	public void setProductPlace(String productPlace) {
		this.productPlace = productPlace;
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

	public String getWarehouseAddr() {
		return warehouseAddr;
	}

	public void setWarehouseAddr(String warehouseAddr) {
		this.warehouseAddr = warehouseAddr;
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

	public Long getCreateUserId() {
		return createUserId;
	}

	public void setCreateUserId(Long createUserId) {
		this.createUserId = createUserId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
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

	public String getDeliveryDateStr() {
		return deliveryDateStr;
	}

	public void setDeliveryDateStr(String deliveryDateStr) {
		this.deliveryDateStr = deliveryDateStr;
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

	public String getDeliverySpec() {
		return deliverySpec;
	}

	public void setDeliverySpec(String deliverySpec) {
		this.deliverySpec = deliverySpec;
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

	public BigDecimal getLogisticsTotalPrice() {
		return logisticsTotalPrice;
	}

	public void setLogisticsTotalPrice(BigDecimal logisticsTotalPrice) {
		this.logisticsTotalPrice = logisticsTotalPrice;
	}

	public BigDecimal getLogisticsDealPrice() {
		return logisticsDealPrice;
	}

	public void setLogisticsDealPrice(BigDecimal logisticsDealPrice) {
		this.logisticsDealPrice = logisticsDealPrice;
	}

	public String getCarType() {
		return carType;
	}

	public void setCarType(String carType) {
		this.carType = carType;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getAppCode() {
		return appCode;
	}

	public void setAppCode(String appCode) {
		this.appCode = appCode;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getTargetWarehouse() {
		return targetWarehouse;
	}

	public void setTargetWarehouse(String targetWarehouse) {
		this.targetWarehouse = targetWarehouse;
	}

	public String getContractNo() {
		return contractNo;
	}

	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}

	public String getRetCode() {
		return retCode;
	}

	public void setRetCode(String retCode) {
		this.retCode = retCode;
	}

	public String getRetMessage() {
		return retMessage;
	}

	public void setRetMessage(String retMessage) {
		this.retMessage = retMessage;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
	

}
