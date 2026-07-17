package com.spt.bas.report.client.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spt.tools.data.vo.DataEntity;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

public class RptCtrContractFinanceVo extends DataEntity {
	private Long id;
	private String contractNo; //合同编号
	private String companyName;//企业名称
	private String ourCompanyName;//我方抬头
	private String productName;//货名
	private BigDecimal totalNumber;//合同数量(吨)
	private BigDecimal totalAmount;//合同总价
	/**
	 * 未收违约金
	 */
	private BigDecimal noReceiveBreachAmount;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date contractTime;//签订日
	private Long matchUserId;//业务员Id
	private String matchUserName;//业务员
	private BigDecimal dealedAmount;//已收/付款金额
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date payFullTime; 	//收/付全款日期
	private BigDecimal billedAmount;// 已开/收票金额
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date realBillDate; //开/收票日期
	private String businessType; // 业务类型
	private Boolean matchCreditFlg;
	private Long approveId;

	/**
	 * 区域
	 */
	private Long deptId;

	/**
	 * 区域名称
	 */
	private String deptName;

	public String getBusinessType() {
		return businessType;
	}

	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}

	public Boolean getMatchCreditFlg() {
		return matchCreditFlg;
	}

	public void setMatchCreditFlg(Boolean matchCreditFlg) {
		this.matchCreditFlg = matchCreditFlg;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getContractNo() {
		return contractNo;
	}

	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getOurCompanyName() {
		return ourCompanyName;
	}

	public void setOurCompanyName(String ourCompanyName) {
		this.ourCompanyName = ourCompanyName;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public BigDecimal getTotalNumber() {
		return totalNumber;
	}

	public void setTotalNumber(BigDecimal totalNumber) {
		this.totalNumber = totalNumber;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public BigDecimal getNoReceiveBreachAmount() {
		return noReceiveBreachAmount;
	}

	public void setNoReceiveBreachAmount(BigDecimal noReceiveBreachAmount) {
		this.noReceiveBreachAmount = noReceiveBreachAmount;
	}

	public Date getContractTime() {
		return contractTime;
	}

	public void setContractTime(Date contractTime) {
		this.contractTime = contractTime;
	}

	public Long getMatchUserId() {
		return matchUserId;
	}

	public void setMatchUserId(Long matchUserId) {
		this.matchUserId = matchUserId;
	}

	public String getMatchUserName() {
		return matchUserName;
	}

	public void setMatchUserName(String matchUserName) {
		this.matchUserName = matchUserName;
	}

	public BigDecimal getDealedAmount() {
		return dealedAmount;
	}

	public void setDealedAmount(BigDecimal dealedAmount) {
		this.dealedAmount = dealedAmount;
	}

	public Date getPayFullTime() {
		return payFullTime;
	}

	public void setPayFullTime(Date payFullTime) {
		this.payFullTime = payFullTime;
	}

	public BigDecimal getBilledAmount() {
		return billedAmount;
	}

	public void setBilledAmount(BigDecimal billedAmount) {
		this.billedAmount = billedAmount;
	}

	public Date getRealBillDate() {
		return realBillDate;
	}

	public void setRealBillDate(Date realBillDate) {
		this.realBillDate = realBillDate;
	}

	public Long getApproveId() {
		return approveId;
	}

	public void setApproveId(Long approveId) {
		this.approveId = approveId;
	}

	public Long getDeptId() {
		return deptId;
	}

	public void setDeptId(Long deptId) {
		this.deptId = deptId;
	}

	public String getDeptName() {
		return deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}
}
