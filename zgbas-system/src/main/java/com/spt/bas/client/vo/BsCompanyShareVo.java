/**
 * 
 */
package com.spt.bas.client.vo;

/**
 * 企业共享Vo
 * @author wlddh
 *
 */
public class BsCompanyShareVo {

	private Long userId;//用户id
	private String userName;
	private Long SharedUserId;//被共享人id
	private String sharedUserName;
	private Long enterpriseId;//企业帐套id
	

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}


	public Long getEnterpriseId() {
		return enterpriseId;
	}

	public void setEnterpriseId(Long enterpriseId) {
		this.enterpriseId = enterpriseId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Long getSharedUserId() {
		return SharedUserId;
	}

	public void setSharedUserId(Long sharedUserId) {
		SharedUserId = sharedUserId;
	}

	public String getSharedUserName() {
		return sharedUserName;
	}

	public void setSharedUserName(String sharedUserName) {
		this.sharedUserName = sharedUserName;
	}
}
