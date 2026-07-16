package com.spt.bas.client.entity;

import com.spt.tools.jpa.vo.IdEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * 提成明细操作记录表
 */
@Entity
@Table(name = "t_budget_settlement_ophis")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class BudgetSettlementOphis extends IdEntity {

	private static final long serialVersionUID = -695449545716305168L;

	/**
	 * 结算表id
	 */
	private Long budgetSettlementId;
	/**
	 * 结算状态：1-已确认，2-已审核
	 */
	private String settleStatus;
	/**
	 * 创建人id
	 */
	private Long createUserId;
	/**
	 * 创建人姓名
	 */
	private String createUserName;
	/**
	 * 备注
	 */
	private String remark;

	public Long getBudgetSettlementId() {
		return budgetSettlementId;
	}

	public void setBudgetSettlementId(Long budgetSettlementId) {
		this.budgetSettlementId = budgetSettlementId;
	}

	public String getSettleStatus() {
		return settleStatus;
	}

	public void setSettleStatus(String settleStatus) {
		this.settleStatus = settleStatus;
	}

	public Long getCreateUserId() {
		return createUserId;
	}

	public void setCreateUserId(Long createUserId) {
		this.createUserId = createUserId;
	}

	public String getCreateUserName() {
		return createUserName;
	}

	public void setCreateUserName(String createUserName) {
		this.createUserName = createUserName;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
}
