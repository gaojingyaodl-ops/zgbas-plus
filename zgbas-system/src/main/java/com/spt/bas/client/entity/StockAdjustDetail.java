package com.spt.bas.client.entity;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.spt.tools.jpa.vo.IdEntity;

/**
 * 库存盘点明细
 */
@Entity
@Table(name="t_stock_adjust_detail")
public class StockAdjustDetail extends IdEntity{

	private static final long serialVersionUID = 2200533287000272828L;
	private Long stockAdjustId;				//库存调整id
	private Long stockDetailId;				//库存明细id
	private	String	productName;			//货名
	private	String	productCd;				//货名CD
	private	String	brandNumber;			//牌号
	private	Long	factoryId;				//厂商ID
	private	String	factoryName;			//厂商名称
	private Long warehouseId;				//仓库Id
	private	String	warehouseName;			//仓库名称
	private BigDecimal sysFrozenNumber;		//系统冻结数量
	private BigDecimal sysAvailableNumber;	//系统可用数量
	private BigDecimal realFrozenNumber;	//实际冻结数量
	private BigDecimal realAvailableNumber; //实际可用数量
	private String adjustStatus;			//调整状态
	private String remark;					//备注
	private String businessNo;				//单据号
	private Long enterpriseId;				//企业套账Id
	public Long getStockAdjustId() {
		return stockAdjustId;
	}
	public void setStockAdjustId(Long stockAdjustId) {
		this.stockAdjustId = stockAdjustId;
	}
	public Long getStockDetailId() {
		return stockDetailId;
	}
	public void setStockDetailId(Long stockDetailId) {
		this.stockDetailId = stockDetailId;
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
	public BigDecimal getSysFrozenNumber() {
		return sysFrozenNumber;
	}
	public void setSysFrozenNumber(BigDecimal sysFrozenNumber) {
		this.sysFrozenNumber = sysFrozenNumber;
	}
	public BigDecimal getSysAvailableNumber() {
		return sysAvailableNumber;
	}
	public void setSysAvailableNumber(BigDecimal sysAvailableNumber) {
		this.sysAvailableNumber = sysAvailableNumber;
	}
	public BigDecimal getRealFrozenNumber() {
		return realFrozenNumber;
	}
	public void setRealFrozenNumber(BigDecimal realFrozenNumber) {
		this.realFrozenNumber = realFrozenNumber;
	}
	public BigDecimal getRealAvailableNumber() {
		return realAvailableNumber;
	}
	public void setRealAvailableNumber(BigDecimal realAvailableNumber) {
		this.realAvailableNumber = realAvailableNumber;
	}
	public String getAdjustStatus() {
		return adjustStatus;
	}
	public void setAdjustStatus(String adjustStatus) {
		this.adjustStatus = adjustStatus;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
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
	
	
	

}
