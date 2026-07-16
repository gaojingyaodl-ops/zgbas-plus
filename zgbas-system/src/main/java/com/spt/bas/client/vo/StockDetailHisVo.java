package com.spt.bas.client.vo;

import com.spt.bas.client.entity.StockDetailHis;

public class StockDetailHisVo extends StockDetailHis{
	private static final long serialVersionUID = 3471768761704385588L;
	private String contractNo;			//合同编号
	private	String	applyNo;			//出入库编号
	public String getContractNo() {
		return contractNo;
	}
	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}
	public String getApplyNo() {
		return applyNo;
	}
	public void setApplyNo(String applyNo) {
		this.applyNo = applyNo;
	}
	
}
