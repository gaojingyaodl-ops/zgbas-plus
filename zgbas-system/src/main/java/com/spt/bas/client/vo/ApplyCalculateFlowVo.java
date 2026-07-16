package com.spt.bas.client.vo;

import com.spt.bas.client.entity.ApplyCalculate;

public class ApplyCalculateFlowVo extends ApplyCalculate{

	private static final long serialVersionUID = -2923565116465858214L;
	
	private Long approveCurUserId;
	private String approveCurUserName;
	public Long getApproveCurUserId() {
		return approveCurUserId;
	}
	public void setApproveCurUserId(Long approveCurUserId) {
		this.approveCurUserId = approveCurUserId;
	}
	public String getApproveCurUserName() {
		return approveCurUserName;
	}
	public void setApproveCurUserName(String approveCurUserName) {
		this.approveCurUserName = approveCurUserName;
	}
	

}
