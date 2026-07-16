package com.spt.bas.client.vo;

import java.util.List;

public class BudgetSettlementOphisVo {
	private Long matchUserId;
	private String matchUserName;
	private String settleStatus;
	private String status;
	private List<Long> settlementIds;

	public Long getMatchUserId() {
		return matchUserId;
	}

	public void setMatchUserId(Long matchUserId) {
		this.matchUserId = matchUserId;
	}

	public String getMatchUserName() {
		return matchUserName;
	}

	public void setMatchUserName(String matchUserName) {
		this.matchUserName = matchUserName;
	}

	public String getSettleStatus() {
		return settleStatus;
	}

	public void setSettleStatus(String settleStatus) {
		this.settleStatus = settleStatus;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<Long> getSettlementIds() {
		return settlementIds;
	}

	public void setSettlementIds(List<Long> settlementIds) {
		this.settlementIds = settlementIds;
	}
}
