package com.spt.bas.client.vo;

import java.math.BigDecimal;

public class ProductDetailVo {
	
	private	String	productName;	//货名
	private	String	productCd;		//货名Cd
	private	String	brandNumber;	//牌号
	private	Long	factoryId;		//厂商ID
	private	String	factoryName;	//厂商名称
	private	BigDecimal	dealNumber;	//入出库数量
	private	BigDecimal	dealPrice;	//单价
	private	BigDecimal	totalPrice;	//总价
	private	Long	enterpriseId;	//企业ID
	private String numberUnit;//数量单位
	private Long warehouseId;//仓库Id
	private String warehouseName;//仓库名称
	
	private BigDecimal curApproveNumber;
	private BigDecimal curNumber;
	private String productAttr;
	private BigDecimal warehouseNumber;
	
	private String contractId;
	
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
	public BigDecimal getDealNumber() {
		return dealNumber;
	}
	public void setDealNumber(BigDecimal dealNumber) {
		this.dealNumber = dealNumber;
	}
	public BigDecimal getDealPrice() {
		return dealPrice;
	}
	public void setDealPrice(BigDecimal dealPrice) {
		this.dealPrice = dealPrice;
	}
	public BigDecimal getTotalPrice() {
		return totalPrice;
	}
	public void setTotalPrice(BigDecimal totalPrice) {
		this.totalPrice = totalPrice;
	}
	public Long getEnterpriseId() {
		return enterpriseId;
	}
	public void setEnterpriseId(Long enterpriseId) {
		this.enterpriseId = enterpriseId;
	}
	public String getNumberUnit() {
		return numberUnit;
	}
	public void setNumberUnit(String numberUnit) {
		this.numberUnit = numberUnit;
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
	public BigDecimal getCurApproveNumber() {
		return curApproveNumber;
	}
	public void setCurApproveNumber(BigDecimal curApproveNumber) {
		this.curApproveNumber = curApproveNumber;
	}
	public BigDecimal getCurNumber() {
		return curNumber;
	}
	public void setCurNumber(BigDecimal curNumber) {
		this.curNumber = curNumber;
	}
	public String getProductAttr() {
		return productAttr;
	}
	public void setProductAttr(String productAttr) {
		this.productAttr = productAttr;
	}
	public BigDecimal getWarehouseNumber() {
		return warehouseNumber;
	}
	public void setWarehouseNumber(BigDecimal warehouseNumber) {
		this.warehouseNumber = warehouseNumber;
	}
	public String getContractId() {
		return contractId;
	}
	public void setContractId(String contractId) {
		this.contractId = contractId;
	}
	
	

}
