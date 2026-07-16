package com.spt.bas.client.vo;

import java.math.BigDecimal;

public class StockVo {
	
	private BigDecimal frozenNumber;
	private BigDecimal realNumber;
	public BigDecimal getFrozenNumber() {
		return frozenNumber;
	}
	public void setFrozenNumber(BigDecimal frozenNumber) {
		this.frozenNumber = frozenNumber;
	}
	public BigDecimal getRealNumber() {
		return realNumber;
	}
	public void setRealNumber(BigDecimal realNumber) {
		this.realNumber = realNumber;
	}
	
	
	public StockVo() {
		super();
		// TODO Auto-generated constructor stub
	}
	public StockVo(BigDecimal frozenNumber, BigDecimal realNumber) {
		super();
		this.frozenNumber = frozenNumber;
		this.realNumber = realNumber;
	}
	
	

}
