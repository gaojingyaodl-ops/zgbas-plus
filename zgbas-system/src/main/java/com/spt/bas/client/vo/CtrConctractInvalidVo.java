/**
 * 
 */
package com.spt.bas.client.vo;

/**
 * @author wlddh
 *
 */
public class CtrConctractInvalidVo {
	private Long id; // 合同id
	private Long userId; 
	private String userName;
	private Long approveId;

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

	public Long getId() {
		return id;
	}

	public void setId(Long contractId) {
		this.id = contractId;
	}

	public Long getApproveId() {
		return approveId;
	}

	public void setApproveId(Long approveId) {
		this.approveId = approveId;
	}

	public CtrConctractInvalidVo() {
	}

	public CtrConctractInvalidVo(Long id, Long userId, String userName) {
		this.id = id;
		this.userId = userId;
		this.userName = userName;
	}
}
