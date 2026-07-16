package com.spt.pm.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.spt.tools.jpa.vo.IdEntity;

@Entity
@Table(name = "t_pm_approve_history")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class PmApproveHistory extends IdEntity {

	/**
	 * 审批历史
	 */
	private static final long serialVersionUID = 2518605856830168813L;
	private Long approveId; // 审批id
	private Long processId; // 流程id
	private Long conditionId; // 条件id
	private Long nodeId; // 节点id
	private Long approveStepId; // 步骤id
	private String stepName; // 步骤名称
	private Long approveUserId; // 审批人id
	private String approveUserName; // 审批人姓名
	private String approveRemark; // 审批意见
	private String approveOpinion; // 审批状态 'A-同意，D-拒绝',

	private Long enterpriseId;//企业帐套ID
	public Long getApproveId() {
		return approveId;
	}

	public void setApproveId(Long approveId) {
		this.approveId = approveId;
	}

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

	public Long getApproveStepId() {
		return approveStepId;
	}

	public void setApproveStepId(Long stepId) {
		this.approveStepId = stepId;
	}

	public String getStepName() {
		return stepName;
	}

	public void setStepName(String stepName) {
		this.stepName = stepName;
	}

	public Long getApproveUserId() {
		return approveUserId;
	}

	public void setApproveUserId(Long approveUserId) {
		this.approveUserId = approveUserId;
	}

	public String getApproveUserName() {
		return approveUserName;
	}

	public void setApproveUserName(String approvorName) {
		this.approveUserName = approvorName;
	}

	public String getApproveRemark() {
		return approveRemark;
	}

	public void setApproveRemark(String approveRemark) {
		this.approveRemark = approveRemark;
	}

	public String getApproveOpinion() {
		return approveOpinion;
	}

	public void setApproveOpinion(String approveStatus) {
		this.approveOpinion = approveStatus;
	}

	public Long getEnterpriseId() {
		return enterpriseId;
	}

	public void setEnterpriseId(Long enterpriseId) {
		this.enterpriseId = enterpriseId;
	}

}
