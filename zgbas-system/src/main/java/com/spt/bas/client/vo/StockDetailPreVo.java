/**
 * 
 */
package com.spt.bas.client.vo;

import java.math.BigDecimal;

/**
 * @author wlddh
 *
 */
public class StockDetailPreVo {
	private BigDecimal preRealNumber;// 上期可用
	private BigDecimal preFrozenNumber;// 上期冻结
	public BigDecimal getPreRealNumber() {
		return preRealNumber;
	}
	public void setPreRealNumber(BigDecimal preRealNumber) {
		this.preRealNumber = preRealNumber;
	}
	public BigDecimal getPreFrozenNumber() {
		return preFrozenNumber;
	}
	public void setPreFrozenNumber(BigDecimal preFrozenNumber) {
		this.preFrozenNumber = preFrozenNumber;
	}
}
