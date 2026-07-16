package com.spt.bas.client.vo;

import java.math.BigDecimal;

public class PresellMaxBuyVo {
	
	private Long ctrProductId;
	private BigDecimal maxBuyNumber = BigDecimal.ZERO;
	public Long getCtrProductId() {
		return ctrProductId;
	}
	public void setCtrProductId(Long ctrProductId) {
		this.ctrProductId = ctrProductId;
	}
	public BigDecimal getMaxBuyNumber() {
		return maxBuyNumber;
	}
	public void setMaxBuyNumber(BigDecimal maxBuyNumber) {
		this.maxBuyNumber = maxBuyNumber;
	}
	

}
