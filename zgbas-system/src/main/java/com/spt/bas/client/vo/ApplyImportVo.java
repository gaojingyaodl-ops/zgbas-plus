package com.spt.bas.client.vo;

import java.util.List;

import com.spt.bas.client.entity.ApplyImport;
import com.spt.bas.client.entity.ApplyProductDetail;

public class ApplyImportVo extends ApplyImport{

	private static final long serialVersionUID = -4684343778890078826L;
	
	private String deliveryMode;		//交货方式
	private String deliveryType;		//配送方式
	private String 	deptAbbr;		//部门简码
	private String removeArrStr;		//删除list
	private String contentStr;			//撮合基本信息

	
	private List<ApplyImportDetailVo> lstInsert;
	private List<ApplyImportDetailVo> lstUpdate;
	private List<ApplyImportDetailVo> lstDelete;
	public String getDeliveryMode() {
		return deliveryMode;
	}
	public void setDeliveryMode(String deliveryMode) {
		this.deliveryMode = deliveryMode;
	}
	public String getDeliveryType() {
		return deliveryType;
	}
	public void setDeliveryType(String deliveryType) {
		this.deliveryType = deliveryType;
	}
	public String getRemoveArrStr() {
		return removeArrStr;
	}
	public void setRemoveArrStr(String removeArrStr) {
		this.removeArrStr = removeArrStr;
	}
	public String getContentStr() {
		return contentStr;
	}
	public void setContentStr(String contentStr) {
		this.contentStr = contentStr;
	}
	public List<ApplyImportDetailVo> getLstInsert() {
		return lstInsert;
	}
	public void setLstInsert(List<ApplyImportDetailVo> lstInsert) {
		this.lstInsert = lstInsert;
	}
	public List<ApplyImportDetailVo> getLstUpdate() {
		return lstUpdate;
	}
	public void setLstUpdate(List<ApplyImportDetailVo> lstUpdate) {
		this.lstUpdate = lstUpdate;
	}
	public List<ApplyImportDetailVo> getLstDelete() {
		return lstDelete;
	}
	public void setLstDelete(List<ApplyImportDetailVo> lstDelete) {
		this.lstDelete = lstDelete;
	}
	
	public String getDeptAbbr() {
		return deptAbbr;
	}
	public void setDeptAbbr(String deptAbbr) {
		this.deptAbbr = deptAbbr;
	}
	@Override
	public Class<?> getSubClass() {
		return ApplyProductDetail.class;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void setBatchSub(List<?> lstInsert, List<?> lstUpdate, List<?> lstDelete) {
		setLstInsert((List<ApplyImportDetailVo>)lstInsert);
		setLstUpdate((List<ApplyImportDetailVo>) lstUpdate);
		setLstDelete((List<ApplyImportDetailVo>)lstDelete);
	}
	
	
}
