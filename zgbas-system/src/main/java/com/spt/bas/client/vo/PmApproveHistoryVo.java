package com.spt.bas.client.vo;

import java.util.Date;

public class PmApproveHistoryVo{
	private String approveNo;
	private String historyName;
	private String approveStepName;		//审批步骤名称
	private String approveUserName;		//审批人
	private String remark;				//备注
	private Date approveDate;			//日期
	private Long approveId;				//审批主表ID
	
	public String getHistoryName() {
		return historyName;
	}
	public void setHistoryName(String historyName) {
		this.historyName = historyName;
	}
	public String getApproveStepName() {
		return approveStepName;
	}
	public void setApproveStepName(String approveStepName) {
		this.approveStepName = approveStepName;
	}
	public String getApproveUserName() {
		return approveUserName;
	}
	public void setApproveUserName(String approveUserName) {
		this.approveUserName = approveUserName;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public Date getApproveDate() {
		return approveDate;
	}
	public void setApproveDate(Date approveDate) {
		this.approveDate = approveDate;
	}
	public Long getApproveId() {
		return approveId;
	}
	public void setApproveId(Long approveId) {
		this.approveId = approveId;
	}
	public String getApproveNo() {
		return approveNo;
	}
	public void setApproveNo(String approveNo) {
		this.approveNo = approveNo;
	}
	
}
