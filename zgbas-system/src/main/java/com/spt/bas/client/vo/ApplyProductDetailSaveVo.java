package com.spt.bas.client.vo;

public class ApplyProductDetailSaveVo {
	private	Long	applyId;		//申请单ID
	private	String	applyType;		//申请类型
	private	Long	enterpriseId;		//公司ID
	public Long getApplyId() {
		return applyId;
	}
	public void setApplyId(Long applyId) {
		this.applyId = applyId;
	}
	public String getApplyType() {
		return applyType;
	}
	public void setApplyType(String applyType) {
		this.applyType = applyType;
	}
	public Long getEnterpriseId() {
		return enterpriseId;
	}
	public void setEnterpriseId(Long enterpriseId) {
		this.enterpriseId = enterpriseId;
	}
	

}
