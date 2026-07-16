package com.spt.bas.client.vo;

import java.math.BigDecimal;
import java.util.Date;

public class InternalBuyReportVo {
	
	private Long id;
	private String productName;
	private String brandNumber;
	private String factoryName;
	private String warehouseName;
	private BigDecimal buyNumber;
	private Long matchUserId;
	private String matchUserName;
	private Long shipperUserId;
	private String shipperUserName;
	private Date createdDate;
	
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
	public BigDecimal getBuyNumber() {
		return buyNumber;
	}
	public void setBuyNumber(BigDecimal buyNumber) {
		this.buyNumber = buyNumber;
	}
	public String getMatchUserName() {
		return matchUserName;
	}
	public void setMatchUserName(String matchUserName) {
		this.matchUserName = matchUserName;
	}
	public String getShipperUserName() {
		return shipperUserName;
	}
	public void setShipperUserName(String shipperUserName) {
		this.shipperUserName = shipperUserName;
	}
	public Date getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	public Long getMatchUserId() {
		return matchUserId;
	}
	public void setMatchUserId(Long matchUserId) {
		this.matchUserId = matchUserId;
	}
	public Long getShipperUserId() {
		return shipperUserId;
	}
	public void setShipperUserId(Long shipperUserId) {
		this.shipperUserId = shipperUserId;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	

}
