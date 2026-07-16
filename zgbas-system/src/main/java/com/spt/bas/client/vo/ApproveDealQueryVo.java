package com.spt.bas.client.vo;

import com.spt.bas.client.entity.ApproveDeal;

public class ApproveDealQueryVo extends ApproveDeal{
	
	private static final long serialVersionUID = 1L;
	private String processName;
	private  Long processId;
	private String userName;	
	private String dealTypeName;
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getDealTypeName() {
		return dealTypeName;
	}
	public void setDealTypeName(String dealTypeName) {
		this.dealTypeName = dealTypeName;
	}
	public Long getProcessId() {
		return processId;
	}
	public void setProcessId(Long processId) {
		this.processId = processId;
	}
	public String getProcessName() {
		return processName;
	}
	public void setProcessName(String processName) {
		this.processName = processName;
	}
	
	
	
	
	

}
