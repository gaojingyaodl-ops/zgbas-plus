package com.spt.bas.client.vo;

import java.math.BigDecimal;

import com.spt.bas.client.entity.StockDetailPresell;

public class StockDetailPresellVo extends StockDetailPresell{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1079100125703341316L;
	private String wrapSpecs;//包装规格
	private String warehousePos;//仓库所在地
	private String ourCompanyName;//我方抬头
	private String qualityStandard; //质量标准
	private BigDecimal warehousePrice;//仓储费单价
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
	public String getOurCompanyName() {
		return ourCompanyName;
	}
	public void setOurCompanyName(String ourCompanyName) {
		this.ourCompanyName = ourCompanyName;
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
	
}
