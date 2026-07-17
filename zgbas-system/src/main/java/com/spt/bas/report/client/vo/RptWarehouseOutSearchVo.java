package com.spt.bas.report.client.vo;

import com.spt.tools.core.bean.PageSearchVo;

public class RptWarehouseOutSearchVo extends PageSearchVo {
	private Long enterpriseId;
	private Long ctrProductId;
	private Long stockContractId;
	private String warehouseName;
	private String buyContractNo;// 采购合同号
	private String buyCompanyName;// 采购其他名称
	private String buyBizUserName;
	private String productCd;
	private String productName;
	private String brandNumber;
	private String factoryName;

	public String getWarehouseName() {
		return warehouseName;
	}

	public void setWarehouseName(String warehouseName) {
		this.warehouseName = warehouseName;
	}

	public String getBuyContractNo() {
		return buyContractNo;
	}

	public void setBuyContractNo(String buyContractNo) {
		this.buyContractNo = buyContractNo;
	}

	public String getBuyCompanyName() {
		return buyCompanyName;
	}

	public void setBuyCompanyName(String buyCompanyName) {
		this.buyCompanyName = buyCompanyName;
	}

	public String getBuyBizUserName() {
		return buyBizUserName;
	}

	public void setBuyBizUserName(String buyBizUserName) {
		this.buyBizUserName = buyBizUserName;
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

	public String getProductCd() {
		return productCd;
	}

	public void setProductCd(String productCd) {
		this.productCd = productCd;
	}

	public Long getEnterpriseId() {
		return enterpriseId;
	}

	public void setEnterpriseId(Long enterpriseId) {
		this.enterpriseId = enterpriseId;
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
