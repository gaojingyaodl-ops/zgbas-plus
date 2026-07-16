package com.spt.bas.client.vo;

import com.spt.tools.core.bean.PageSearchVo;

public class StockDetailSearchVo extends PageSearchVo{

	private String productCd;
	private String brandNumber;
	private Long factoryId;
	private String sellContractId;
	private Long ctrProductId;
	private String warehouseName;
	private String productAttr;
	
	private Long stockDetailId;// 库存明细id
	private Long stockContractId;// 合同库存id
	
	private String status;//C：当前、A：历史、''：全部

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

	public String getSellContractId() {
		return sellContractId;
	}

	public void setSellContractId(String sellContractId) {
		this.sellContractId = sellContractId;
	}

	public String getWarehouseName() {
		return warehouseName;
	}

	public void setWarehouseName(String warehouseName) {
		this.warehouseName = warehouseName;
	}

	public String getProductAttr() {
		return productAttr;
	}

	public void setProductAttr(String productAttr) {
		this.productAttr = productAttr;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Long getStockDetailId() {
		return stockDetailId;
	}

	public void setStockDetailId(Long stockDetailId) {
		this.stockDetailId = stockDetailId;
	}

	public Long getCtrProductId() {
		return ctrProductId;
	}

	public void setCtrProductId(Long ctrProductId) {
		this.ctrProductId = ctrProductId;
	}

	public Long getStockContractId() {
		return stockContractId;
	}

	public void setStockContractId(Long stockContractId) {
		this.stockContractId = stockContractId;
	}
	
}
