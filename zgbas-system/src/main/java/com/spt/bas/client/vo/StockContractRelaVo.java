package com.spt.bas.client.vo;

import com.spt.bas.client.entity.StockContractRela;

public class StockContractRelaVo extends StockContractRela{
	private static final long serialVersionUID = 1871479921676895809L;
	private String contractNo;
	private String productName;
	private String brandNumber;
	private String factoryName;
	private String warehouseName;

	public String getContractNo() {
		return contractNo;
	}

	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
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

	public String getWarehouseName() {
		return warehouseName;
	}

	public void setWarehouseName(String warehouseName) {
		this.warehouseName = warehouseName;
	}
	 
}
