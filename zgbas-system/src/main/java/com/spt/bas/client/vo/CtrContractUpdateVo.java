package com.spt.bas.client.vo;


import com.spt.bas.client.entity.CtrContract;

public class CtrContractUpdateVo extends CtrContract{

	private static final long serialVersionUID = 752537067329248861L;
	private String bizUserName;//业务员
	private Long bizUserId;//业务员ID
	public String getBizUserName() {
		return bizUserName;
	}
	public void setBizUserName(String bizUserName) {
		this.bizUserName = bizUserName;
	}
	public Long getBizUserId() {
		return bizUserId;
	}
	public void setBizUserId(Long bizUserId) {
		this.bizUserId = bizUserId;
	}
	
	
}
