package com.spt.pm.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.spt.tools.jpa.vo.IdEntity;

/**
 * 流程表单配置表
 * @author zhangyanping
 *
 */
@Entity
@Table(name = "t_pm_apply_set")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class PmApplySet extends IdEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = -467258047263768493L;
	private Long processId;//流程id
	private String fieldName;//属性名
	@JsonSerialize(using = ToStringSerializer.class)
	private Boolean editFlg = true;//是否可编辑  默认可编辑
	@JsonSerialize(using = ToStringSerializer.class)
	private Boolean requireFlg = true;//是否必输 默认必输
	private Long stepId;//步骤id
	private Long enterpriseId;//企业套账ID
	private String remark;//备注
	public Long getProcessId() {
		return processId;
	}
	public void setProcessId(Long processId) {
		this.processId = processId;
	}
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public Boolean getEditFlg() {
		return editFlg;
	}
	public void setEditFlg(Boolean editFlg) {
		this.editFlg = editFlg;
	}
	public Boolean getRequireFlg() {
		return requireFlg;
	}
	public void setRequireFlg(Boolean requireFlg) {
		this.requireFlg = requireFlg;
	}
	public Long getStepId() {
		return stepId;
	}
	public void setStepId(Long stepId) {
		this.stepId = stepId;
	}
	public Long getEnterpriseId() {
		return enterpriseId;
	}
	public void setEnterpriseId(Long enterpriseId) {
		this.enterpriseId = enterpriseId;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	
	

}
