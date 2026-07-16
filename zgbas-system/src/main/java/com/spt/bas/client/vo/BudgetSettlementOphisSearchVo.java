package com.spt.bas.client.vo;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.spt.tools.core.bean.PageSearchVo;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class BudgetSettlementOphisSearchVo extends PageSearchVo{
	
	private String sellContractNo; //合同编号

	private String businessType; //业务状态
	private String settleStatus; //结算状态
	private Long matchUserId;//业务员


	public String getSellContractNo() {
		return sellContractNo;
	}

	public void setSellContractNo(String sellContractNo) {
		this.sellContractNo = sellContractNo;
	}

	public String getBusinessType() {
		return businessType;
	}

	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}

	public String getSettleStatus() {
		return settleStatus;
	}

	public void setSettleStatus(String settleStatus) {
		this.settleStatus = settleStatus;
	}

	public Long getMatchUserId() {
		return matchUserId;
	}

	public void setMatchUserId(Long matchUserId) {
		this.matchUserId = matchUserId;
	}
}
