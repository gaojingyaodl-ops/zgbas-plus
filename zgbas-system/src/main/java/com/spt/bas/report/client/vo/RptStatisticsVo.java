package com.spt.bas.report.client.vo;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spt.tools.core.bean.PageSearchVo;
/**
 * 统计查询条件Vo
 * @author zhouzihang
 */
public class RptStatisticsVo extends PageSearchVo {
	private Long enterpriseId;// 企业账套Id
	private String contractType;//合同类型
	private String statisticsType;// 统计类型
	private String productName;// 商品名称
	private String brandNumber;// 牌号
	private String companyName;// 对方企业
	private String matchUserName;// 业务员名称
	private Long deptId;// 部门ID
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date contractTimeGTED;// 大于采购时间
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date contractTimeLTD;// 小于采购时间
	public Long getEnterpriseId() {
		return enterpriseId;
	}
	public void setEnterpriseId(Long enterpriseId) {
		this.enterpriseId = enterpriseId;
	}
	public String getContractType() {
		return contractType;
	}
	public void setContractType(String contractType) {
		this.contractType = contractType;
	}
	public String getStatisticsType() {
		return statisticsType;
	}
	public void setStatisticsType(String statisticsType) {
		this.statisticsType = statisticsType;
	}
	public String getBrandNumber() {
		return brandNumber;
	}
	public void setBrandNumber(String brandNumber) {
		this.brandNumber = brandNumber;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public String getMatchUserName() {
		return matchUserName;
	}
	public void setMatchUserName(String matchUserName) {
		this.matchUserName = matchUserName;
	}
	public Long getDeptId() {
		return deptId;
	}
	public void setDeptId(Long deptId) {
		this.deptId = deptId;
	}
	public Date getContractTimeGTED() {
		return contractTimeGTED;
	}
	public void setContractTimeGTED(Date contractTimeGTED) {
		this.contractTimeGTED = contractTimeGTED;
	}
	public Date getContractTimeLTD() {
		return contractTimeLTD;
	}
	public void setContractTimeLTD(Date contractTimeLTD) {
		this.contractTimeLTD = contractTimeLTD;
	}
}
