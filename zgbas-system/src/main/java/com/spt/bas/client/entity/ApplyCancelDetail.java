package com.spt.bas.client.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spt.tools.jpa.vo.IdEntity;

/**
 * 申请单-作废申请明细表
 * 
 * @author wlddh
 *
 */
@Entity
@Table(name = "t_apply_cancel_detail")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ApplyCancelDetail extends IdEntity {
	private static final long serialVersionUID = 1L;

	private Long applyCancelId;// 作废申请id
	private Long oldApproveId;// 原审批id
	private String oldApproveNo;// 原审批编号
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date oldApproveDate;// 原审批时间
	private BigDecimal cancelNum;// 作废数量
	private BigDecimal cancelAmount;// 作废金额
	private Long enterpriseId;// 企业账套ID

	public Long getApplyCancelId() {
		return applyCancelId;
	}

	public void setApplyCancelId(Long applyCancelId) {
		this.applyCancelId = applyCancelId;
	}

	public String getOldApproveNo() {
		return oldApproveNo;
	}

	public void setOldApproveNo(String oldApproveNo) {
		this.oldApproveNo = oldApproveNo;
	}

	public Date getOldApproveDate() {
		return oldApproveDate;
	}

	public void setOldApproveDate(Date oldApproveDate) {
		this.oldApproveDate = oldApproveDate;
	}

	public BigDecimal getCancelNum() {
		return cancelNum;
	}

	public void setCancelNum(BigDecimal cancelNum) {
		this.cancelNum = cancelNum;
	}

	public BigDecimal getCancelAmount() {
		return cancelAmount;
	}

	public void setCancelAmount(BigDecimal cancelAmount) {
		this.cancelAmount = cancelAmount;
	}

	public Long getEnterpriseId() {
		return enterpriseId;
	}

	public void setEnterpriseId(Long enterpriseId) {
		this.enterpriseId = enterpriseId;
	}

	public Long getOldApproveId() {
		return oldApproveId;
	}

	public void setOldApproveId(Long oldApproveId) {
		this.oldApproveId = oldApproveId;
	}

}
