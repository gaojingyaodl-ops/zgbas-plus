package com.spt.bas.report.client.entity;

import java.math.BigDecimal;

public class RptStockContractReportVo{
	private Long id;
	private String wrapSpecs;// 包装规格
	private String warehousePos;// 仓库所在地
	private String productAttr;//现货期货
	private BigDecimal remainNumber;// 剩余数量
	private String ourCompanyName;//我方抬头
	private String contractNo; //采购合同编号
	private String qualityStandard; //质量标准  Y-原厂标准，G-过渡料，F-副牌料
	private BigDecimal warehousePrice;//仓储费单价
	private Long buyContractId; // 采购合同ID
	private Long buyProductId;// 对应采购合同的商品明细id
	private String productName; // 货名
	private String productCd; // 货名CD
	private String brandNumber; // 牌号
	private Long factoryId; // 厂商ID
	private String factoryName; // 厂商名称
	private Long warehouseId; // 仓库Id
	private String warehouseName; // 仓库名称
	private BigDecimal dealPrice; // 采购单价
	private BigDecimal warehouseAmount; // 仓储费
	private BigDecimal buyNumber = BigDecimal.ZERO; // 采购数量
	private BigDecimal sellingNumber = BigDecimal.ZERO; // 销售中数量
	private BigDecimal sellNumber = BigDecimal.ZERO; // 已销售数量
	private BigDecimal presellNumber = BigDecimal.ZERO; // 预售数量
	private BigDecimal deliveryInNumber = BigDecimal.ZERO;// 入库数量
	private BigDecimal deliveryOutNumber = BigDecimal.ZERO;// 出库数量
	private Long enterpriseId; // 企业账套ID
	private Long bizUserId;
	private String bizUserName;
	
	private Integer contractDifTime; 		//合同时长
	
	public String getWrapSpecs() {
		return wrapSpecs;
	}
	public void setWrapSpecs(String wrapSpecs) {
		this.wrapSpecs = wrapSpecs;
	}
	public String getWarehousePos() {
		return warehousePos;
	}
	public void setWarehousePos(String warehousePos) {
		this.warehousePos = warehousePos;
	}
	public String getProductAttr() {
		return productAttr;
	}
	public void setProductAttr(String productAttr) {
		this.productAttr = productAttr;
	}
	public BigDecimal getRemainNumber() {
		return remainNumber;
	}
	public void setRemainNumber(BigDecimal remainNumber) {
		this.remainNumber = remainNumber;
	}
	public String getOurCompanyName() {
		return ourCompanyName;
	}
	public void setOurCompanyName(String ourCompanyName) {
		this.ourCompanyName = ourCompanyName;
	}
	public String getContractNo() {
		return contractNo;
	}
	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}
	public String getQualityStandard() {
		return qualityStandard;
	}
	public void setQualityStandard(String qualityStandard) {
		this.qualityStandard = qualityStandard;
	}
	public BigDecimal getWarehousePrice() {
		return warehousePrice;
	}
	public void setWarehousePrice(BigDecimal warehousePrice) {
		this.warehousePrice = warehousePrice;
	}
	public Long getBuyContractId() {
		return buyContractId;
	}
	public void setBuyContractId(Long buyContractId) {
		this.buyContractId = buyContractId;
	}
	public Long getBuyProductId() {
		return buyProductId;
	}
	public void setBuyProductId(Long buyProductId) {
		this.buyProductId = buyProductId;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getProductCd() {
		return productCd;
	}
	public void setProductCd(String productCd) {
		this.productCd = productCd;
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
	public Long getWarehouseId() {
		return warehouseId;
	}
	public void setWarehouseId(Long warehouseId) {
		this.warehouseId = warehouseId;
	}
	public String getWarehouseName() {
		return warehouseName;
	}
	public void setWarehouseName(String warehouseName) {
		this.warehouseName = warehouseName;
	}
	public BigDecimal getDealPrice() {
		return dealPrice;
	}
	public void setDealPrice(BigDecimal dealPrice) {
		this.dealPrice = dealPrice;
	}
	public BigDecimal getWarehouseAmount() {
		return warehouseAmount;
	}
	public void setWarehouseAmount(BigDecimal warehouseAmount) {
		this.warehouseAmount = warehouseAmount;
	}
	public BigDecimal getBuyNumber() {
		return buyNumber;
	}
	public void setBuyNumber(BigDecimal buyNumber) {
		this.buyNumber = buyNumber;
	}
	public BigDecimal getSellingNumber() {
		return sellingNumber;
	}
	public void setSellingNumber(BigDecimal sellingNumber) {
		this.sellingNumber = sellingNumber;
	}
	public BigDecimal getSellNumber() {
		return sellNumber;
	}
	public void setSellNumber(BigDecimal sellNumber) {
		this.sellNumber = sellNumber;
	}
	public BigDecimal getPresellNumber() {
		return presellNumber;
	}
	public void setPresellNumber(BigDecimal presellNumber) {
		this.presellNumber = presellNumber;
	}
	public BigDecimal getDeliveryInNumber() {
		return deliveryInNumber;
	}
	public void setDeliveryInNumber(BigDecimal deliveryInNumber) {
		this.deliveryInNumber = deliveryInNumber;
	}
	public BigDecimal getDeliveryOutNumber() {
		return deliveryOutNumber;
	}
	public void setDeliveryOutNumber(BigDecimal deliveryOutNumber) {
		this.deliveryOutNumber = deliveryOutNumber;
	}
	public Long getEnterpriseId() {
		return enterpriseId;
	}
	public void setEnterpriseId(Long enterpriseId) {
		this.enterpriseId = enterpriseId;
	}
	public Long getBizUserId() {
		return bizUserId;
	}
	public void setBizUserId(Long bizUserId) {
		this.bizUserId = bizUserId;
	}
	public String getBizUserName() {
		return bizUserName;
	}
	public void setBizUserName(String bizUserName) {
		this.bizUserName = bizUserName;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Integer getContractDifTime() {
		return contractDifTime;
	}
	public void setContractDifTime(Integer contractDifTime) {
		this.contractDifTime = contractDifTime;
	}
	
	
}
