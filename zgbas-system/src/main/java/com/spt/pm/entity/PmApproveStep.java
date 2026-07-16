package com.spt.pm.entity;

import com.spt.tools.jpa.vo.IdEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;
/**
 * 审批步骤
 */
@Entity
@Table(name = "t_pm_approve_step")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class PmApproveStep extends IdEntity {

	private static final long serialVersionUID = 4350023297841426145L;

	/**
	 * 审批ID
	 */
	private Long approveId;

	/**
	 * 流程id
	 */
	private Long processId;

	/**
	 * 条件id
	 */
	private Long conditionId;

	/**
	 * 节点id
	 */
	private Long nodeId;

	/**
	 * 步骤id
	 */
	private Long stepId;

	/**
	 * 步骤名称
	 */
	private String stepName;

	/**
	 * 分组
	 */
	private String stepGroup;

	/**
	 * 序号
	 */
	private Long dispOrderNo;

	/**
	 * 自动签时限(分钟)
	 */
	private Long autoSignLimit;

	/**
	 * 驳回权限
	 */
	private Boolean backFlg;

	/**
	 * 审批备注
	 */
	private String approveRemark;

	/**
	 * 审批时间
	 */
	private Date approveDate;

	/**
	 * 审批人姓名
	 */
	private String approveUserName;

	/**
	 * 审批人id
	 */
	private Long approveUserId;

	/**
	 * 审批意见 'A-同意，D-拒绝'
	 */
	private String approveOpinion;

	/**
	 * 审批设备与环境 IP-Browser-Os
	 */
	private String approveEnvironment;

	/**
	 * 企业帐套id
	 */
	private Long enterpriseId;
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

	public Long getStepId() {
		return stepId;
	}

	public void setStepId(Long stepId) {
		this.stepId = stepId;
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

	public String getApproveRemark() {
		return approveRemark;
	}

	public void setApproveRemark(String approveRemark) {
		this.approveRemark = approveRemark;
	}

	public Date getApproveDate() {
		return approveDate;
	}

	public void setApproveDate(Date approveDate) {
		this.approveDate = approveDate;
	}

	public String getApproveUserName() {
		return approveUserName;
	}

	public void setApproveUserName(String approvorName) {
		this.approveUserName = approvorName;
	}

	public Long getApproveUserId() {
		return approveUserId;
	}

	public void setApproveUserId(Long approveUserId) {
		this.approveUserId = approveUserId;
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

	public String getApproveEnvironment() {
		return approveEnvironment;
	}

	public void setApproveEnvironment(String approveEnvironment) {
		this.approveEnvironment = approveEnvironment;
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
