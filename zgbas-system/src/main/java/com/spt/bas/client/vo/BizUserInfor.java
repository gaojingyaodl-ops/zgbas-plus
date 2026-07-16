package com.spt.bas.client.vo;

public class BizUserInfor {
	private Long bizUserId;
	private String bizUserName;
	private Long approveId;
	private String approveNo;
	private Long sellProductId;//预售合同明细Id
	private Long sellContractId;//预售合同Id
	public Long getBizUserId() {
		return bizUserId;
	}
	public void setBizUserId(Long bizUserId) {
		this.bizUserId = bizUserId;
	}
	public String getBizUserName() {
		return bizUserName;
	}
	public void setBizUserName(String bizUserName) {
		this.bizUserName = bizUserName;
	}
	public Long getApproveId() {
		return approveId;
	}
	public void setApproveId(Long approveId) {
		this.approveId = approveId;
	}
	public Long getSellProductId() {
		return sellProductId;
	}
	public void setSellProductId(Long sellProductId) {
		this.sellProductId = sellProductId;
	}
	public Long getSellContractId() {
		return sellContractId;
	}
	public void setSellContractId(Long sellContractId) {
		this.sellContractId = sellContractId;
	}
	public String getApproveNo() {
		return approveNo;
	}
	public void setApproveNo(String approveNo) {
		this.approveNo = approveNo;
	}
	
	

}
