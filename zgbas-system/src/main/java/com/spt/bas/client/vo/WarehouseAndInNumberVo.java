package com.spt.bas.client.vo;

import java.math.BigDecimal;

/**
 * 仓库和该仓库存在的某个销售合同的可出库数量
 * */
public class WarehouseAndInNumberVo {
	
	private String text;//仓库名称（warehouseName），因要初始化下拉框，该字段名改为text
	private String warehouseName;
	private String buyContractId;
	private BigDecimal frozenNumber;//最大出库数量=已入库数量-已出库数量
	private BigDecimal availableNumber;
	private Long stockDetailId;
	private Long ctrProductId;
	private Long stockContractId;
	private BigDecimal curApproveNumber = BigDecimal.ZERO;//当前审批中的数量
	
	private String buyContractNo;//采购合同号
	private String buyCompanyName;//采购其他名称
	private String buyBizUserName;
	private String productName;
	private String brandNumber;
	private String factoryName;
	
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getBuyContractId() {
		return buyContractId;
	}
	public void setBuyContractId(String buyContractId) {
		this.buyContractId = buyContractId;
	}
	public BigDecimal getFrozenNumber() {
		return frozenNumber;
	}
	public void setFrozenNumber(BigDecimal frozenNumber) {
		this.frozenNumber = frozenNumber;
	}
	public BigDecimal getAvailableNumber() {
		return availableNumber;
	}
	public void setAvailableNumber(BigDecimal availableNumber) {
		this.availableNumber = availableNumber;
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
	public BigDecimal getCurApproveNumber() {
		return curApproveNumber;
	}
	public void setCurApproveNumber(BigDecimal curApproveNumber) {
		this.curApproveNumber = curApproveNumber;
	}
	public String getWarehouseName() {
		return warehouseName;
	}
	public void setWarehouseName(String warehouseName) {
		this.warehouseName = warehouseName;
	}
	public Long getStockContractId() {
		return stockContractId;
	}
	public void setStockContractId(Long stockContractId) {
		this.stockContractId = stockContractId;
	}
	public String getBuyContractNo() {
		return buyContractNo;
	}
	public void setBuyContractNo(String buyContractNo) {
		this.buyContractNo = buyContractNo;
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
	public String getBuyCompanyName() {
		return buyCompanyName;
	}
	public void setBuyCompanyName(String buyCompanyName) {
		this.buyCompanyName = buyCompanyName;
	}
	

}
