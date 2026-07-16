package com.spt.bas.client.vo;

import com.spt.bas.client.entity.CtrContract;

import java.math.BigDecimal;
/**
 * 出库明细统计 
 */
public class DeliveryReprtVo extends CtrContract{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private BigDecimal DeliveryOutNumber;	//出库数量
	private BigDecimal DeliveryInNumber;	//入库数量
	private BigDecimal SurplusNumber;		//结余数量
	private BigDecimal totalNumber;			//总数量
	private String warehouse;				//入库仓库
	private String propertyStatus;			//货权状态
	private String warehouseNo;				//入库单号
	private String productName; 			//品名
	private String brandNumber;				//牌号
	private String factoryName;				//厂商
	private Long ctrProductId;				//商品ID
	//private BigDecimal availableNumber;   
	private Long stockDetailId;   			//库存明细Id
	private String deptName;				//部门名称
	
	
	public Long getStockDetailId() {
		return stockDetailId;
	}
	public void setStockDetailId(Long stockDetailId) {
		this.stockDetailId = stockDetailId;
	}
	public BigDecimal getDeliveryInNumber() {
		return DeliveryInNumber;
	}
	public void setDeliveryInNumber(BigDecimal deliveryInNumber) {
		DeliveryInNumber = deliveryInNumber;
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
	public BigDecimal getDeliveryOutNumber() {
		return DeliveryOutNumber;
	}
	public void setDeliveryOutNumber(BigDecimal deliveryOutNumber) {
		DeliveryOutNumber = deliveryOutNumber;
	}
	public BigDecimal getSurplusNumber() {
		return SurplusNumber;
	}
	public void setSurplusNumber(BigDecimal surplusNumber) {
		SurplusNumber = surplusNumber;
	}
	public String getWarehouse() {
		return warehouse;
	}
	public void setWarehouse(String warehouse) {
		this.warehouse = warehouse;
	}
	public String getPropertyStatus() {
		return propertyStatus;
	}
	public void setPropertyStatus(String propertyStatus) {
		this.propertyStatus = propertyStatus;
	}
	public String getWarehouseNo() {
		return warehouseNo;
	}
	public void setWarehouseNo(String warehouseNo) {
		this.warehouseNo = warehouseNo;
	}
	@Override
    public BigDecimal getTotalNumber() {
		return totalNumber;
	}
	@Override
	public void setTotalNumber(BigDecimal totalNumber) {
		this.totalNumber = totalNumber;
	}
	public Long getCtrProductId() {
		return ctrProductId;
	}
	public void setCtrProductId(Long ctrProductId) {
		this.ctrProductId = ctrProductId;
	}
	public String getDeptName() {
		return deptName;
	}
	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}
	
}
