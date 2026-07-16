package com.spt.bas.client.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spt.bas.client.entity.CtrContract;
import com.spt.tools.core.bean.PageSearchVo;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class ContractOrderVo extends PageSearchVo {
	/**
	 * 合同编号
	 */
	private String contractNo;
	/**
	 * 对方企业名称
	 */
	private String companyName;

	/**
	 * 我方企业名称
	 */
	private String ourCompanyName;
	private Long userId;//用户ID
	private Long deptLeaderId;	  //中心负责人ID
	private boolean admin;

	/**
	 * 履约状态
	 */
	private String performanceStatus;

	/**
	 * 业务类型
	 */
	private String businessType;

	/**
	 * 业务员Id
	 */
	private Long matchUserId;

	/**
	 * 产品类型
	 */
	private String productType;

	/**
	 * 化工业务员ID集合
	 */
	private List<Long> hgMatchUserIdList;

	public Long getMatchUserId() {
		return matchUserId;
	}

	public void setMatchUserId(Long matchUserId) {
		this.matchUserId = matchUserId;
	}

	public String getBusinessType() {
		return businessType;
	}

	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}

	public boolean isAdmin() {
		return admin;
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getDeptLeaderId() {
		return deptLeaderId;
	}

	public void setDeptLeaderId(Long deptLeaderId) {
		this.deptLeaderId = deptLeaderId;
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

	public String getPerformanceStatus() {
		return performanceStatus;
	}

	public void setPerformanceStatus(String performanceStatus) {
		this.performanceStatus = performanceStatus;
	}

	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}

	public List<Long> getHgMatchUserIdList() {
		return hgMatchUserIdList;
	}

	public void setHgMatchUserIdList(List<Long> hgMatchUserIdList) {
		this.hgMatchUserIdList = hgMatchUserIdList;
	}
}
