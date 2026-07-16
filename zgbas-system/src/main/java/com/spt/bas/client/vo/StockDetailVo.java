package com.spt.bas.client.vo;

import com.spt.bas.client.entity.StockDetail;

public class StockDetailVo extends StockDetail {
	
	private String buyCompanyName;
	private String contractNo; // 合同编号 

	public String getBuyCompanyName() {
		return buyCompanyName;
	}

	public void setBuyCompanyName(String buyCompanyName) {
		this.buyCompanyName = buyCompanyName;
	}

	public String getContractNo() {
		return contractNo;
	}

	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}
}
