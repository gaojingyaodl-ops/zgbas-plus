/**
 * 
 */
package com.spt.bas.client.vo;

import com.spt.tools.core.bean.PageSearchVo;

import java.io.Serializable;

/**
 * @author wlddh
 *
 */
public class BsCompanySearchVo extends PageSearchVo implements Serializable {

	private String mode;// M-我的私海 , P-公海数据, MP-我的私海+公海，A-全部
	private Long userId;//用户id
	private Long enterpriseId;//企业帐套id
	private Boolean lookAllCompany; // 查看所有企业权限

	/**
	 * 合伙人权限
	 */
	private Boolean hhrPerm;

	/**
	 * 查看本部门企业权限
	 */
	private Boolean lookCurDeptCompanyPrem;
	
	private Long currentUserId;

	private String requestUrl;
	
	private Boolean accessReportIdExist;

	private Boolean accessReportFlgExist;

	/**
	 * 区域总导出权限（公海数据只导出本区域的数据）
	 */
	private Boolean leaderExportPermitted;

	private Boolean approvePlasticTypeFlag;

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

	public Long getEnterpriseId() {
		return enterpriseId;
	}

	public void setEnterpriseId(Long enterpriseId) {
		this.enterpriseId = enterpriseId;
	}

	public Boolean getLookAllCompany() {
		return lookAllCompany;
	}

	public void setLookAllCompany(Boolean lookAllCompany) {
		this.lookAllCompany = lookAllCompany;
	}

	public Long getCurrentUserId() {
		return currentUserId;
	}

	public void setCurrentUserId(Long currentUserId) {
		this.currentUserId = currentUserId;
	}

	public String getRequestUrl() {
		return requestUrl;
	}

	public void setRequestUrl(String requestUrl) {
		this.requestUrl = requestUrl;
	}

	public Boolean getAccessReportIdExist() {
		return accessReportIdExist;
	}

	public void setAccessReportIdExist(Boolean accessReportIdExist) {
		this.accessReportIdExist = accessReportIdExist;
	}

	public Boolean getHhrPerm() {
		return hhrPerm;
	}

	public void setHhrPerm(Boolean hhrPerm) {
		this.hhrPerm = hhrPerm;
	}

	public Boolean getAccessReportFlgExist() {
		return accessReportFlgExist;
	}

	public void setAccessReportFlgExist(Boolean accessReportFlgExist) {
		this.accessReportFlgExist = accessReportFlgExist;
	}

	public Boolean getLookCurDeptCompanyPrem() {
		return lookCurDeptCompanyPrem;
	}

	public void setLookCurDeptCompanyPrem(Boolean lookCurDeptCompanyPrem) {
		this.lookCurDeptCompanyPrem = lookCurDeptCompanyPrem;
	}

	public Boolean getLeaderExportPermitted() {
		return leaderExportPermitted;
	}

	public void setLeaderExportPermitted(Boolean leaderExportPermitted) {
		this.leaderExportPermitted = leaderExportPermitted;
	}

	public Boolean getApprovePlasticTypeFlag() {
		return approvePlasticTypeFlag;
	}

	public void setApprovePlasticTypeFlag(Boolean approvePlasticTypeFlag) {
		this.approvePlasticTypeFlag = approvePlasticTypeFlag;
	}
}
