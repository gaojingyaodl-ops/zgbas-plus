package com.spt.pm.vo;

import com.spt.pm.entity.PmApprove;

public class PmApproveCurrVo extends PmApprove{
	private static final long serialVersionUID = -7026858189184359088L;
	private Long currUserId;
	private String currUserName;
	private Boolean completeDismissalFlg = false;		// 是否为合同已完成后的申请单驳回操作
	public Long getCurrUserId() {
		return currUserId;
	}
	public void setCurrUserId(Long currUserId) {
		this.currUserId = currUserId;
	}
	public String getCurrUserName() {
		return currUserName;
	}
	public void setCurrUserName(String currUserName) {
		this.currUserName = currUserName;
	}

	public Boolean getCompleteDismissalFlg() {
		return completeDismissalFlg;
	}

	public void setCompleteDismissalFlg(Boolean completeDismissalFlg) {
		this.completeDismissalFlg = completeDismissalFlg;
	}
}
