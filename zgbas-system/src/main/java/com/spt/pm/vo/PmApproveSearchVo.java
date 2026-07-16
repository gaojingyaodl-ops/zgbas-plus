/**
 *
 */
package com.spt.pm.vo;

import com.spt.tools.core.bean.PageSearchVo;

/**
 * @author wlddh
 *
 */
public class PmApproveSearchVo extends PageSearchVo {

	private String searchKey;
	private Long enterpriseId;
	private String mode;// H-我的历史，C-我的审批，S-我发起， A-全部，A1-与我有关的所有数据
	private Long userId;
	private Long deptLeaderId;	//中心负责人ID

	private Integer batchSelectDel;	// 是否查询已删除审批单

	private String loginName;

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public Long getDeptLeaderId() {
		return deptLeaderId;
	}

	public void setDeptLeaderId(Long deptLeaderId) {
		this.deptLeaderId = deptLeaderId;
	}

	public String getSearchKey() {
		return searchKey;
	}

	public void setSearchKey(String searchKey) {
		this.searchKey = searchKey;
	}

	public Long getEnterpriseId() {
		return enterpriseId;
	}

	public void setEnterpriseId(Long enterpriseId) {
		this.enterpriseId = enterpriseId;
	}

	public Integer getBatchSelectDel() {
		return batchSelectDel;
	}

	public void setBatchSelectDel(Integer batchSelectDel) {
		this.batchSelectDel = batchSelectDel;
	}

	public PmApproveSearchVo() {
	}

	public PmApproveSearchVo(String searchKey, Long enterpriseId) {
		this.searchKey = searchKey;
		this.enterpriseId = enterpriseId;
	}
}
