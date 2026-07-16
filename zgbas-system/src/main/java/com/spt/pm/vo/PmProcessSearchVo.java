/**
 *
 */
package com.spt.pm.vo;

/**
 * @author wlddh
 */
public class PmProcessSearchVo {

	private Long userId;
	private String processCode;

	private Boolean viewFlg;

	private Long enterpriseId;
	// 流程分组：biz是业务流程，mng是企管流程
	private String processGroup;

	public Boolean getViewFlg() {
		return viewFlg;
	}

	public void setViewFlg(Boolean viewFlg) {
		this.viewFlg = viewFlg;
	}

	public String getProcessGroup() {
		return processGroup;
	}

	public void setProcessGroup(String processGroup) {
		this.processGroup = processGroup;
	}

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

	public String getProcessCode() {
		return processCode;
	}

	public void setProcessCode(String processCode) {
		this.processCode = processCode;
	}

	public PmProcessSearchVo() {
	}

	public PmProcessSearchVo(Long enterpriseId) {
		this.enterpriseId = enterpriseId;
	}

	public PmProcessSearchVo(String processCode, Long enterpriseId) {
		this.processCode = processCode;
		this.enterpriseId = enterpriseId;
	}
}
