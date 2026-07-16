package com.spt.bas.client.vo;

public class SealBorrowVo {
	private Long sealBorrowId;
	private String opType;
	private Long opUserId;
	private String opUserName;
	private String returnItemType;
	private String remark;
	public Long getSealBorrowId() {
		return sealBorrowId;
	}
	public void setSealBorrowId(Long sealBorrowId) {
		this.sealBorrowId = sealBorrowId;
	}
	public String getOpType() {
		return opType;
	}
	public void setOpType(String opType) {
		this.opType = opType;
	}
	public Long getOpUserId() {
		return opUserId;
	}
	public void setOpUserId(Long opUserId) {
		this.opUserId = opUserId;
	}
	public String getOpUserName() {
		return opUserName;
	}
	public void setOpUserName(String opUserName) {
		this.opUserName = opUserName;
	}
	public String getReturnItemType() {
		return returnItemType;
	}
	public void setReturnItemType(String returnItemType) {
		this.returnItemType = returnItemType;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
}
