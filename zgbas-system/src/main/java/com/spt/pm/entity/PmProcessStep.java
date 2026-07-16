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
 * 流程步骤
 * @author wlddh
 *
 */
@Entity
@Table(name = "t_pm_process_step")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class PmProcessStep extends IdEntity {
	private static final long serialVersionUID = 5955842931609897683L;

	/**
	 * 流程ID
	 */
	private Long processId;

	/**
	 * 条件ID
	 */
	private Long conditionId;

	/**
	 * 节点ID
	 */
	private Long nodeId;

	/**
	 * 步骤名称
	 */
	private String stepName;

	/**
	 * 自动签时限(分钟)
	 */
	private Long autoSignLimit;

	/**
	 * 序号
	 */
	private Long dispOrderNo;

	/**
	 * 驳回权限
	 */
	@JsonSerialize(using = ToStringSerializer.class)
	private Boolean backFlg =false;

	/**
	 * 备注
	 */
	private String remark;

	/**
	 * 是否有效
	 */
	@JsonSerialize(using = ToStringSerializer.class)
	private Boolean enableFlg = true;

	/**
	 * 重复节点是否跳过
	 */
	@JsonSerialize(using = ToStringSerializer.class)
	private Boolean repeatSkipFlg = true;

	/**
	 * 企业帐套ID
	 */
	private Long enterpriseId;

	/**
	 * 提示信息
	 */
	private String tips;

	/**
	 * 分组
	 */
	private String stepGroup;

	public Long getProcessId() { 
		return processId;
	}

	public void setProcessId(Long processId) {
		this.processId = processId;
	}

	public Long getConditionId() {
		return conditionId;
	}

	public void setConditionId(Long conditionId) {
		this.conditionId = conditionId;
	}

	public Long getNodeId() {
		return nodeId;
	}

	public void setNodeId(Long nodeId) {
		this.nodeId = nodeId;
	}

	public String getStepName() {
		return stepName;
	}

	public void setStepName(String stepName) {
		this.stepName = stepName;
	}

	public Long getDispOrderNo() {
		return dispOrderNo;
	}

	public void setDispOrderNo(Long dispOrderNo) {
		this.dispOrderNo = dispOrderNo;
	}

	public Boolean getBackFlg() {
		return backFlg;
	}

	public void setBackFlg(Boolean backFlg) {
		this.backFlg = backFlg;
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

	public Long getEnterpriseId() {
		return enterpriseId;
	}

	public void setEnterpriseId(Long enterpriseId) {
		this.enterpriseId = enterpriseId;
	}

	public String getTips() {
		return tips;
	}

	public void setTips(String tips) {
		this.tips = tips;
	}

	public Boolean getRepeatSkipFlg() {
		return repeatSkipFlg;
	}

	public void setRepeatSkipFlg(Boolean repeatSkipFlg) {
		this.repeatSkipFlg = repeatSkipFlg;
	}

	public String getStepGroup() {
		return stepGroup;
	}

	public void setStepGroup(String stepGroup) {
		this.stepGroup = stepGroup;
	}

	public Long getAutoSignLimit() {
		return autoSignLimit;
	}

	public void setAutoSignLimit(Long autoSignLimit) {
		this.autoSignLimit = autoSignLimit;
	}
}
