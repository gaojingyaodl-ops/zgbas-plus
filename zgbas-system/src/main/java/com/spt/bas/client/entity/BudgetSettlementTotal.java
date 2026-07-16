package com.spt.bas.client.entity;

import com.spt.tools.jpa.vo.IdEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;


/**
 * 提成明细操作记录表
 */
@Entity
@Table(name = "t_budget_settlement_total")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class BudgetSettlementTotal extends IdEntity {

	private static final long serialVersionUID = 2884405514760693251L;

	/**
	 * 结算表id
	 */
	private String budgetSettlementId;
	/**
	 * 劳务关系
	 */
	private String settleStatus;
	/**
	 * 业务员
	 */
	private Long matchUserId;
	/**
	 * 赊销 销售提成
	 */
	private BigDecimal sellCommissionAmount1;
	/**
	 * 赊销 采购提成
	 */
	private BigDecimal buyCommissionAmount1;
	/**
	 * 赊销 销售负责人提成
	 */
	private BigDecimal sellDirectorCommissionAmount1;
	/**
	 * 赊销 采购负责人提成
	 */
	private BigDecimal buyDirectorCommissionAmount1;
	/**
	 * 代采 销售提成
	 */
	private BigDecimal sellCommissionAmount2;
	/**
	 * 代采 采购提成
	 */
	private BigDecimal buyCommissionAmount2;
	/**
	 * 代采 销售负责人提成
	 */
	private BigDecimal sellDirectorCommissionAmount2;

	/**
	 * 代采 采购负责人提成
	 */
	private BigDecimal buyDirectorCommissionAmount2;
	/**
	 * 合计
	 */
	private BigDecimal totalAmount;


	public String getBudgetSettlementId() {
		return budgetSettlementId;
	}

	public void setBudgetSettlementId(String budgetSettlementId) {
		this.budgetSettlementId = budgetSettlementId;
	}

	public String getSettleStatus() {
		return settleStatus;
	}

	public void setSettleStatus(String settleStatus) {
		this.settleStatus = settleStatus;
	}

	public Long getMatchUserId() {
		return matchUserId;
	}

	public void setMatchUserId(Long matchUserId) {
		this.matchUserId = matchUserId;
	}

	public BigDecimal getSellCommissionAmount1() {
		return defaultNum(sellCommissionAmount1);
	}

	public void setSellCommissionAmount1(BigDecimal sellCommissionAmount1) {
		this.sellCommissionAmount1 = sellCommissionAmount1;
	}

	public BigDecimal getBuyCommissionAmount1() {
		return defaultNum(buyCommissionAmount1);
	}

	public void setBuyCommissionAmount1(BigDecimal buyCommissionAmount1) {
		this.buyCommissionAmount1 = buyCommissionAmount1;
	}

	public BigDecimal getSellDirectorCommissionAmount1() {
		return defaultNum(sellDirectorCommissionAmount1);
	}

	public void setSellDirectorCommissionAmount1(BigDecimal sellDirectorCommissionAmount1) {
		this.sellDirectorCommissionAmount1 = sellDirectorCommissionAmount1;
	}

	public BigDecimal getBuyDirectorCommissionAmount1() {
		return defaultNum(buyDirectorCommissionAmount1);
	}

	public void setBuyDirectorCommissionAmount1(BigDecimal buyDirectorCommissionAmount1) {
		this.buyDirectorCommissionAmount1 = buyDirectorCommissionAmount1;
	}

	public BigDecimal getSellCommissionAmount2() {
		return defaultNum(sellCommissionAmount2);
	}

	public void setSellCommissionAmount2(BigDecimal sellCommissionAmount2) {
		this.sellCommissionAmount2 = sellCommissionAmount2;
	}

	public BigDecimal getBuyCommissionAmount2() {
		return defaultNum(buyCommissionAmount2);
	}

	public void setBuyCommissionAmount2(BigDecimal buyCommissionAmount2) {
		this.buyCommissionAmount2 = buyCommissionAmount2;
	}

	public BigDecimal getSellDirectorCommissionAmount2() {
		return defaultNum(sellDirectorCommissionAmount2);
	}

	public void setSellDirectorCommissionAmount2(BigDecimal sellDirectorCommissionAmount2) {
		this.sellDirectorCommissionAmount2 = sellDirectorCommissionAmount2;
	}

	public BigDecimal getBuyDirectorCommissionAmount2() {
		return defaultNum(buyDirectorCommissionAmount2);
	}

	public void setBuyDirectorCommissionAmount2(BigDecimal buyDirectorCommissionAmount2) {
		this.buyDirectorCommissionAmount2 = buyDirectorCommissionAmount2;
	}

	public BigDecimal getTotalAmount() {
		return defaultNum(totalAmount);
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	private BigDecimal defaultNum(BigDecimal value) {
		return value == null ? BigDecimal.ZERO : value;
	}
}
