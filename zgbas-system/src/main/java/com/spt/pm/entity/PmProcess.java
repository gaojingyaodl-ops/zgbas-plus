/**
 *
 */
package com.spt.pm.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.spt.tools.jpa.vo.IdEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 流程主表
 *
 * @author wlddh
 *
 */
@Entity
@Table(name = "t_pm_process")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class PmProcess extends IdEntity {

	private static final long serialVersionUID = -985262208661968792L;
	private String processCode;// 流程代码
	private String processName;// 流程名称
	private String remark;// 备注
	@JsonSerialize(using = ToStringSerializer.class)
	private Boolean enableFlg;// 是否有效

	/**
	 * 控制权限
	 */
	@JsonSerialize(using = ToStringSerializer.class)
	private Boolean viewFlg;// 是否有效

	private String entityName;// 实体名称，完整路径
	private String entityService;// 服务名称
	private String listenerService;
	private String contentUrl;// 审批内容地址:/bas/contract/content/
	private Long dispOrderNo;// 序号

	private String applyType;

	private Long enterpriseId;//企业帐套ID

	private String processGroup;//流程分组

	/**
	 * 发起人提示
	 */
	private String sponsorTips;

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

	public String getProcessCode() {
		return processCode;
	}

	public void setProcessCode(String processCode) {
		this.processCode = processCode;
	}

	public String getProcessName() {
		return processName;
	}

	public void setProcessName(String processName) {
		this.processName = processName;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Boolean getEnableFlg() {
		return enableFlg;
	}

	public void setEnableFlg(Boolean enableFlg) {
		this.enableFlg = enableFlg;
	}

	public String getEntityName() {
		return entityName;
	}

	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	public String getEntityService() {
		return entityService;
	}

	public void setEntityService(String entityService) {
		this.entityService = entityService;
	}

	public String getContentUrl() {
		return contentUrl;
	}

	public void setContentUrl(String contentUrl) {
		this.contentUrl = contentUrl;
	}

	public Long getDispOrderNo() {
		return dispOrderNo;
	}

	public void setDispOrderNo(Long dispOrderNo) {
		this.dispOrderNo = dispOrderNo;
	}

	public Long getEnterpriseId() {
		return enterpriseId;
	}

	public void setEnterpriseId(Long enterpriseId) {
		this.enterpriseId = enterpriseId;
	}

	public String getApplyType() {
		return applyType;
	}

	public void setApplyType(String applyType) {
		this.applyType = applyType;
	}

	public String getListenerService() {
		return listenerService;
	}

	public void setListenerService(String listenerService) {
		this.listenerService = listenerService;
	}

	public String getSponsorTips() {
		return sponsorTips;
	}

	public void setSponsorTips(String sponsorTips) {
		this.sponsorTips = sponsorTips;
	}
}
