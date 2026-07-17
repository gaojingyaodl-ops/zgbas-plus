package com.spt.bas.report.client.vo;

import com.spt.tools.core.bean.PageSearchVo;

public class RptStockContractSearchReportVo extends PageSearchVo{
	private String contractNo; //采购合同编号
	private String productName; // 品名
	private String brandNumber; // 牌号
	private String factoryName; // 厂商名称
	private String warehouseName; // 仓库名称
	private String bizUserName;
	private Long enterpriseId;
	private String productCd;
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
	public String getBizUserName() {
		return bizUserName;
	}
	public void setBizUserName(String bizUserName) {
		this.bizUserName = bizUserName;
	}
	public Long getEnterpriseId() {
		return enterpriseId;
	}
	public void setEnterpriseId(Long enterpriseId) {
		this.enterpriseId = enterpriseId;
	}
	public String getProductCd() {
		return productCd;
	}
	public void setProductCd(String productCd) {
		this.productCd = productCd;
	}
	
}
