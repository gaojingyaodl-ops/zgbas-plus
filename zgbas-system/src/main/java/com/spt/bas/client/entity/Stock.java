package com.spt.bas.client.entity;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.spt.tools.jpa.vo.IdEntity;

/**
 * 库存主表
 */
@Entity
@Table(name = "t_stock")
public class Stock extends IdEntity{

	private static final long serialVersionUID = 924245567597038114L;
	
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
	public String getWarehouseName() {
		return warehouseName;
	}
	public void setWarehouseName(String warehouseName) {
		this.warehouseName = warehouseName;
	}
	public BigDecimal getPresellNumber() {
		return presellNumber;
	}
	public void setPresellNumber(BigDecimal presellNumber) {
		this.presellNumber = presellNumber;
	}

}
