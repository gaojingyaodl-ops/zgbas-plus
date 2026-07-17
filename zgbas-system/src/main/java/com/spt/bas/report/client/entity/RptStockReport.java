package com.spt.bas.report.client.entity;

import java.math.BigDecimal;

import com.spt.tools.core.bean.PageSearchVo;

public class RptStockReport extends PageSearchVo{
	private Long id;//库存Id
	private Long factoryId;//厂商ID
	private Long enterpriseId;//企业账套ID
	private String productName;//货名
	private String productCd;//货名CD
	private String brandNumber;//牌号
	private String factoryName;//厂商
	private String warehouseName;//仓库
	private String remark;//备注
	private BigDecimal totalNumber = BigDecimal.ZERO;//总数量(当前仓库存在的货物总量)
	private BigDecimal frozenNumber = BigDecimal.ZERO;//冻结数量(已卖出未出库数量)
	private BigDecimal realNumber = BigDecimal.ZERO;;//可用数量(未卖出库存)
	private	BigDecimal	presellNumber = BigDecimal.ZERO;	//预售数量
	private BigDecimal averagePrice = BigDecimal.ZERO;	//平均价
	private BigDecimal totalPrice = BigDecimal.ZERO;		//总货款
	private String productAttr;//属性：现货、期货
	private Long warehouseId;//仓库ID
	private String ourCompanyName;//我方抬头
	private String status;//状态 
	private Long buyContractId;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getFactoryId() {
		return factoryId;
	}
	public void setFactoryId(Long factoryId) {
		this.factoryId = factoryId;
	}
	public Long getEnterpriseId() {
		return enterpriseId;
	}
	public void setEnterpriseId(Long enterpriseId) {
		this.enterpriseId = enterpriseId;
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
	public String getFactoryName() {
		return factoryName;
	}
	public void setFactoryName(String factoryName) {
		this.factoryName = factoryName;
	}
	public String getWarehouseName() {
		return warehouseName;
	}
	public void setWarehouseName(String warehouseName) {
		this.warehouseName = warehouseName;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public BigDecimal getTotalNumber() {
		return totalNumber;
	}
	public void setTotalNumber(BigDecimal totalNumber) {
		this.totalNumber = totalNumber;
	}
	public BigDecimal getFrozenNumber() {
		return frozenNumber;
	}
	public void setFrozenNumber(BigDecimal frozenNumber) {
		this.frozenNumber = frozenNumber;
	}
	public BigDecimal getRealNumber() {
		return realNumber;
	}
	public void setRealNumber(BigDecimal realNumber) {
		this.realNumber = realNumber;
	}
	public BigDecimal getPresellNumber() {
		return presellNumber;
	}
	public void setPresellNumber(BigDecimal presellNumber) {
		this.presellNumber = presellNumber;
	}
	public BigDecimal getAveragePrice() {
		return averagePrice;
	}
	public void setAveragePrice(BigDecimal averagePrice) {
		this.averagePrice = averagePrice;
	}
	public BigDecimal getTotalPrice() {
		return totalPrice;
	}
	public void setTotalPrice(BigDecimal totalPrice) {
		this.totalPrice = totalPrice;
	}
	public String getProductAttr() {
		return productAttr;
	}
	public void setProductAttr(String productAttr) {
		this.productAttr = productAttr;
	}
	public Long getWarehouseId() {
		return warehouseId;
	}
	public void setWarehouseId(Long warehouseId) {
		this.warehouseId = warehouseId;
	}
	public String getOurCompanyName() {
		return ourCompanyName;
	}
	public void setOurCompanyName(String ourCompanyName) {
		this.ourCompanyName = ourCompanyName;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Long getBuyContractId() {
		return buyContractId;
	}
	public void setBuyContractId(Long buyContractId) {
		this.buyContractId = buyContractId;
	}
	
	
}
