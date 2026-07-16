/**
 * 
 */
package com.spt.pm.vo;

/**
 * @author wlddh
 *
 */
public class PmApproveSaveOpenVo<T> {
	private String thirdOrderNo;
	private String processCode; // 流程代码
	private Long enterpriseId;// 企业ID
	private Long userId; // 发起人id
	private String userName; // 发起人姓名

	private T pmEntity;

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

	public Long getEnterpriseId() {
		return enterpriseId;
	}

	public void setEnterpriseId(Long enterpriseId) {
		this.enterpriseId = enterpriseId;
	}

	public T getPmEntity() {
		return pmEntity;
	}

	public void setPmEntity(T pmEntity) {
		this.pmEntity = pmEntity;
	}

	public String getThirdOrderNo() {
		return thirdOrderNo;
	}

	public void setThirdOrderNo(String thirdOrderNo) {
		this.thirdOrderNo = thirdOrderNo;
	}

	public String getProcessCode() {
		return processCode;
	}

	public void setProcessCode(String processCode) {
		this.processCode = processCode;
	}

}
