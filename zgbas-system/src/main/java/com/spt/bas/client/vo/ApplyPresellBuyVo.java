package com.spt.bas.client.vo;

import java.util.List;

import com.spt.bas.client.entity.ApplyBuy;
import com.spt.bas.client.entity.ApplyProductDetail;

public class ApplyPresellBuyVo extends ApplyBuy {

	private static final long serialVersionUID = -544514712041910440L;
private String deptAbbr;	//部门简码
	
	private List<ApplyProductDetail> lstInsert;
	private List<ApplyProductDetail> lstUpdate;
	private List<ApplyProductDetail> lstDelete;
	
	public List<ApplyProductDetail> getLstInsert() {
		return lstInsert;
	}
	public void setLstInsert(List<ApplyProductDetail> lstInsert) {
		this.lstInsert = lstInsert;
	}
	public List<ApplyProductDetail> getLstUpdate() {
		return lstUpdate;
	}
	public void setLstUpdate(List<ApplyProductDetail> lstUpdate) {
		this.lstUpdate = lstUpdate;
	}
	public List<ApplyProductDetail> getLstDelete() {
		return lstDelete;
	}
	public void setLstDelete(List<ApplyProductDetail> lstDelete) {
		this.lstDelete = lstDelete;
	}
	@Override
	public Class<?> getSubClass() {
		return ApplyProductDetail.class;
	}
	
	@Override
    @SuppressWarnings("unchecked")
	public void setBatchSub(List<?> lstInsert, List<?> lstUpdate, List<?> lstDelete) {
		setLstInsert((List<ApplyProductDetail>)lstInsert);
		setLstUpdate((List<ApplyProductDetail>)lstUpdate);
		setLstDelete((List<ApplyProductDetail>)lstDelete);
	}
	public String getDeptAbbr() {
		return deptAbbr;
	}
	public void setDeptAbbr(String deptAbbr) {
		this.deptAbbr = deptAbbr;
	}
	
}
