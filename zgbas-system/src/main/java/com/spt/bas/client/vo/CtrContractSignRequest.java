package com.spt.bas.client.vo;

/**
 * 合同签约请求
 * 
 * @author wlddh
 *
 */
public class CtrContractSignRequest {
	private Long ctrContractId;
	private Long userId; // 创建用户ID
	private String userName;// 创建用户名

	public Long getCtrContractId() {
		return ctrContractId;
	}

	public void setCtrContractId(Long ctrContractId) {
		this.ctrContractId = ctrContractId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

}
