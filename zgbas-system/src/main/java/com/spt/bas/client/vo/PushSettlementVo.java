package com.spt.bas.client.vo;

import java.util.List;

import com.spt.bas.client.entity.CtrContractSettlement;

public class PushSettlementVo {
	public List<CtrContractSettlement> settlementList;

	public List<CtrContractSettlement> getSettlementList() {
		return settlementList;
	}

	public void setSettlementList(List<CtrContractSettlement> settlementList) {
		this.settlementList = settlementList;
	}
	
}
