package com.spt.bas.client.vo;

import java.math.BigDecimal;

/**
 * 获取单价vo
 *
 */
public class AcquirePriceVo {
	private String areaCode; // 地区代码
	private String warehouseName;// 仓库名称
	private BigDecimal warehouseUnitCost = new BigDecimal(5);// 单价(未设置默认是5)
	public String getAreaCode() {
		return areaCode;
	}
	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}
	public String getWarehouseName() {
		return warehouseName;
	}
	public void setWarehouseName(String warehouseName) {
		this.warehouseName = warehouseName;
	}
	public BigDecimal getWarehouseUnitCost() {
		return warehouseUnitCost;
	}
	public void setWarehouseUnitCost(BigDecimal warehouseUnitCost) {
		this.warehouseUnitCost = warehouseUnitCost;
	}
	
	
}
