package com.spt.bas.client.vo;

import java.util.List;

public class ApplyCalculateVo{
	private Long entityId;
	private String businessType;
	private String contentStr;
	private List<ApplyCalculateDetailVo> lstInsert;
	private List<ApplyCalculateDetailVo> lstUpdate;
	private List<ApplyCalculateDetailVo> lstDelete;

	public Long getEntityId() {
		return entityId;
	}
	public void setEntityId(Long entityId) {
		this.entityId = entityId;
	}
	public String getBusinessType() {
		return businessType;
	}
	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}
	public List<ApplyCalculateDetailVo> getLstInsert() {
		return lstInsert;
	}
	public void setLstInsert(List<ApplyCalculateDetailVo> lstInsert) {
		this.lstInsert = lstInsert;
	}
	public List<ApplyCalculateDetailVo> getLstUpdate() {
		return lstUpdate;
	}
	public void setLstUpdate(List<ApplyCalculateDetailVo> lstUpdate) {
		this.lstUpdate = lstUpdate;
	}
	public List<ApplyCalculateDetailVo> getLstDelete() {
		return lstDelete;
	}
	public void setLstDelete(List<ApplyCalculateDetailVo> lstDelete) {
		this.lstDelete = lstDelete;
	}
	public String getContentStr() {
		return contentStr;
	}
	public void setContentStr(String contentStr) {
		this.contentStr = contentStr;
	}

}
