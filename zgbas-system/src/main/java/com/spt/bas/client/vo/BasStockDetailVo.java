package com.spt.bas.client.vo;

import com.spt.bas.client.entity.StockDetail;

public class BasStockDetailVo extends StockDetail{
	private static final long serialVersionUID = 1L;
	private String ourCompanyName;		//我方抬头

	public String getOurCompanyName() {
		return ourCompanyName;
	}

	public void setOurCompanyName(String ourCompanyName) {
		this.ourCompanyName = ourCompanyName;
	}
	
}
