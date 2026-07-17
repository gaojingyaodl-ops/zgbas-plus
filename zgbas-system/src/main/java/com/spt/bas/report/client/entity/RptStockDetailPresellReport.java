package com.spt.bas.report.client.entity;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;



public class RptStockDetailPresellReport{
	private Long id;
	private String wrapSpecs;//包装规格
	private String warehousePos;//仓库所在地
	private String ourCompanyName;//我方抬头
	private String qualityStandard; //质量标准
	private BigDecimal warehousePrice;//仓储费单价
	private Long stockId;					//库存Id
	private	String	productName;			//货名
	private	String	productCd;				//货名CD
	private	String	brandNumber;			//牌号
	private	Long	factoryId;				//厂商ID
	private	String	factoryName;			//厂商名称
	private Long warehouseId;				//仓库Id
	private	String	warehouseName;			//仓库名称
	private	BigDecimal	dealPrice;			//单价
	private BigDecimal presellNumber = BigDecimal.ZERO;		//预售数量
	private BigDecimal buyedNumber = BigDecimal.ZERO;			//已购数量
	private BigDecimal approveBuyNumber = BigDecimal.ZERO;//当前审批中的采购数量
	private Long contractId;
	private Long ctrProductId;
	private Long enterpriseId; //企业账套Id
	private String remark;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date createdDate;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date updatedDate;
	
	
	public Date getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	public Date getUpdatedDate() {
		return updatedDate;
	}
	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
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
	public Long getStockId() {
		return stockId;
	}
	public void setStockId(Long stockId) {
		this.stockId = stockId;
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
	public BigDecimal getDealPrice() {
		return dealPrice;
	}
	public void setDealPrice(BigDecimal dealPrice) {
		this.dealPrice = dealPrice;
	}
	public BigDecimal getPresellNumber() {
		return presellNumber;
	}
	public void setPresellNumber(BigDecimal presellNumber) {
		this.presellNumber = presellNumber;
	}
	public BigDecimal getBuyedNumber() {
		return buyedNumber;
	}
	public void setBuyedNumber(BigDecimal buyedNumber) {
		this.buyedNumber = buyedNumber;
	}
	public BigDecimal getApproveBuyNumber() {
		return approveBuyNumber;
	}
	public void setApproveBuyNumber(BigDecimal approveBuyNumber) {
		this.approveBuyNumber = approveBuyNumber;
	}
	public Long getContractId() {
		return contractId;
	}
	public void setContractId(Long contractId) {
		this.contractId = contractId;
	}
	public Long getCtrProductId() {
		return ctrProductId;
	}
	public void setCtrProductId(Long ctrProductId) {
		this.ctrProductId = ctrProductId;
	}
	public Long getEnterpriseId() {
		return enterpriseId;
	}
	public void setEnterpriseId(Long enterpriseId) {
		this.enterpriseId = enterpriseId;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	
}

