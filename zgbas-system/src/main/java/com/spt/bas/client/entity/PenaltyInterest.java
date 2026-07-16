package com.spt.bas.client.entity;

import com.spt.tools.jpa.vo.IdEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * 历史罚息
 */
@Entity
@Table(name = "t_penalty_interest")
public class PenaltyInterest extends IdEntity {

	private static final long serialVersionUID = 865983338547977101L;
	private Long bizId;
	/**
	 * 罚息合同ID(多个合同ID逗号拼接)
	 */
	private String interestContractId;
	/**
	 * 罚息合同号(多个合同号逗号拼接)
	 */
	private String interestContractNo;
	/**
	 * 罚息金额(多个罚息金额逗号拼接)
	 */
	private String interestAmount;
	/**
	 * 总罚息金额
	 */
	private BigDecimal totalInterestAmount;
	/**
	 * 状态(N-新增，A-审批中，B-驳回，D-完成)
	 */
	private String interestStatus;
	/**
	 * 销售合同所选企业Id
	 */
	private String interestCompanyId;

	public Long getBizId() {
		return bizId;
	}

	public void setBizId(Long bizId) {
		this.bizId = bizId;
	}

	public String getInterestContractId() {
		return interestContractId;
	}

	public void setInterestContractId(String interestContractId) {
		this.interestContractId = interestContractId;
	}

	public String getInterestContractNo() {
		return interestContractNo;
	}

	public void setInterestContractNo(String interestContractNo) {
		this.interestContractNo = interestContractNo;
	}

	public String getInterestAmount() {
		return interestAmount;
	}

	public void setInterestAmount(String interestAmount) {
		this.interestAmount = interestAmount;
	}

	public BigDecimal getTotalInterestAmount() {
		return totalInterestAmount;
	}

	public void setTotalInterestAmount(BigDecimal totalInterestAmount) {
		this.totalInterestAmount = totalInterestAmount;
	}

	public String getInterestStatus() {
		return interestStatus;
	}

	public void setInterestStatus(String interestStatus) {
		this.interestStatus = interestStatus;
	}

	public String getInterestCompanyId() {
		return interestCompanyId;
	}

	public void setInterestCompanyId(String interestCompanyId) {
		this.interestCompanyId = interestCompanyId;
	}
}
