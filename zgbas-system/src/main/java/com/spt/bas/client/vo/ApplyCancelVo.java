package com.spt.bas.client.vo;

import com.spt.bas.client.entity.ApplyCancel;
import com.spt.bas.client.entity.ApplyCancelDetail;

import java.util.List;

public class ApplyCancelVo extends ApplyCancel {

	private static final long serialVersionUID = 8421065224360755729L;
	
	private List<ApplyCancelDetail> lstInsert;
	private List<ApplyCancelDetail> lstUpdate;
	private List<ApplyCancelDetail> lstDelete;
	
	public List<ApplyCancelDetail> getLstInsert() {
		return lstInsert;
	}
	public void setLstInsert(List<ApplyCancelDetail> lstInsert) {
		this.lstInsert = lstInsert;
	}
	public List<ApplyCancelDetail> getLstUpdate() {
		return lstUpdate;
	}
	public void setLstUpdate(List<ApplyCancelDetail> lstUpdate) {
		this.lstUpdate = lstUpdate;
	}
	public List<ApplyCancelDetail> getLstDelete() {
		return lstDelete;
	}
	public void setLstDelete(List<ApplyCancelDetail> lstDelete) {
		this.lstDelete = lstDelete;
	}
	@Override
    @SuppressWarnings("unchecked")
	public void setBatchSub(List<?> lstInsert, List<?> lstUpdate, List<?> lstDelete) {
		setLstInsert((List<ApplyCancelDetail>)lstInsert);
		setLstUpdate((List<ApplyCancelDetail>)lstUpdate);
		setLstDelete((List<ApplyCancelDetail>)lstDelete);
	}
	
	@Override
	public Class<?> getSubClass() {
		return ApplyCancelDetail.class;
	}

}
