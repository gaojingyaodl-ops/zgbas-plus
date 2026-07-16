package com.spt.bas.client.vo;

import java.math.BigDecimal;

public class StockDetailDeleveryInResp {

	private boolean isBack;// 是否作废操作

	private Long stockDetailId;
	private BigDecimal realNumber = BigDecimal.ZERO; // 可用数量
	private BigDecimal frozenNumber = BigDecimal.ZERO;// 冻结数量

	public boolean isBack() {
		return isBack;
	}

	public void setBack(boolean isBack) {
		this.isBack = isBack;
	}

	public Long getStockDetailId() {
		return stockDetailId;
	}

	public void setStockDetailId(Long stockDetailId) {
		this.stockDetailId = stockDetailId;
	}

	public BigDecimal getRealNumber() {
		return realNumber;
	}

	public void setRealNumber(BigDecimal realNumber) {
		this.realNumber = realNumber;
	}

	public BigDecimal getFrozenNumber() {
		return frozenNumber;
	}

	public void setFrozenNumber(BigDecimal frozenNumber) {
		this.frozenNumber = frozenNumber;
	}

}
