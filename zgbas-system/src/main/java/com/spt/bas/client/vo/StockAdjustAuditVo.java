package com.spt.bas.client.vo;

import java.math.BigDecimal;

public class StockAdjustAuditVo {
	
	private Long userId;
	private String userName;
	private Long stockAdjustId;
	private String type;//F:冻结、A：可用
	private Long stockDetailId;
	private BigDecimal differentNumber;//可用和实际相差数量
	private BigDecimal realNumber;//实际数量
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public Long getStockAdjustId() {
		return stockAdjustId;
	}
	public void setStockAdjustId(Long stockAdjustId) {
		this.stockAdjustId = stockAdjustId;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Long getStockDetailId() {
		return stockDetailId;
	}
	public void setStockDetailId(Long stockDetailId) {
		this.stockDetailId = stockDetailId;
	}
	public BigDecimal getDifferentNumber() {
		return differentNumber;
	}
	public void setDifferentNumber(BigDecimal differentNumber) {
		this.differentNumber = differentNumber;
	}
	public BigDecimal getRealNumber() {
		return realNumber;
	}
	public void setRealNumber(BigDecimal realNumber) {
		this.realNumber = realNumber;
	}
	
	

}
