package com.spt.bas.report.client.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spt.tools.core.bean.PageSearchVo;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class RptCompanySearchVo extends PageSearchVo {


	/**
	 * 不参与统计业务员ID
	 */
	private List<Long> notUserIds;

	/**
	 * 客户名称
	 */
	private String companyName;

	/**
	 * 区域
	 */
	private Long deptId;
	
	private List<Long> deptIdList;

	/**
	 * 人保额度
	 */
	private BigDecimal piccCreditAmountStart;
	private BigDecimal piccCreditAmountEnd;

	/** 销售总价 */
	private BigDecimal sellTotalAmountStart;
	private BigDecimal sellTotalAmountEnd;

	/** 毛利率 */
	private BigDecimal grossProfitMarginStart;
	private BigDecimal grossProfitMarginEnd;
	
	private Long  enterpriseId;

	/** 业务员ID  企业领用人 */
	private Long matchUserId;

	private Long ownerOfAccountId;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date firstContractTimeStart;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date firstContractTimeEnd;

	public List<Long> getNotUserIds() {
		return notUserIds;
	}

	public void setNotUserIds(List<Long> notUserIds) {
		this.notUserIds = notUserIds;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public Long getDeptId() {
		return deptId;
	}

	public void setDeptId(Long deptId) {
		this.deptId = deptId;
	}

	public BigDecimal getPiccCreditAmountStart() {
		return piccCreditAmountStart;
	}

	public void setPiccCreditAmountStart(BigDecimal piccCreditAmountStart) {
		this.piccCreditAmountStart = piccCreditAmountStart;
	}

	public BigDecimal getPiccCreditAmountEnd() {
		return piccCreditAmountEnd;
	}

	public void setPiccCreditAmountEnd(BigDecimal piccCreditAmountEnd) {
		this.piccCreditAmountEnd = piccCreditAmountEnd;
	}

	public BigDecimal getSellTotalAmountStart() {
		return sellTotalAmountStart;
	}

	public void setSellTotalAmountStart(BigDecimal sellTotalAmountStart) {
		this.sellTotalAmountStart = sellTotalAmountStart;
	}

	public BigDecimal getSellTotalAmountEnd() {
		return sellTotalAmountEnd;
	}

	public void setSellTotalAmountEnd(BigDecimal sellTotalAmountEnd) {
		this.sellTotalAmountEnd = sellTotalAmountEnd;
	}

	public BigDecimal getGrossProfitMarginStart() {
		return grossProfitMarginStart;
	}

	public void setGrossProfitMarginStart(BigDecimal grossProfitMarginStart) {
		this.grossProfitMarginStart = grossProfitMarginStart;
	}

	public BigDecimal getGrossProfitMarginEnd() {
		return grossProfitMarginEnd;
	}

	public void setGrossProfitMarginEnd(BigDecimal grossProfitMarginEnd) {
		this.grossProfitMarginEnd = grossProfitMarginEnd;
	}

	public Long getEnterpriseId() {
		return enterpriseId;
	}

	public void setEnterpriseId(Long enterpriseId) {
		this.enterpriseId = enterpriseId;
	}

	public Long getMatchUserId() {
		return matchUserId;
	}

	public void setMatchUserId(Long matchUserId) {
		this.matchUserId = matchUserId;
	}

	public Long getOwnerOfAccountId() {
		return ownerOfAccountId;
	}

	public void setOwnerOfAccountId(Long ownerOfAccountId) {
		this.ownerOfAccountId = ownerOfAccountId;
	}

	public List<Long> getDeptIdList() {
		return deptIdList;
	}

	public void setDeptIdList(List<Long> deptIdList) {
		this.deptIdList = deptIdList;
	}

	public Date getFirstContractTimeStart() {
		return firstContractTimeStart;
	}

	public void setFirstContractTimeStart(Date firstContractTimeStart) {
		this.firstContractTimeStart = firstContractTimeStart;
	}

	public Date getFirstContractTimeEnd() {
		return firstContractTimeEnd;
	}

	public void setFirstContractTimeEnd(Date firstContractTimeEnd) {
		this.firstContractTimeEnd = firstContractTimeEnd;
	}
}

