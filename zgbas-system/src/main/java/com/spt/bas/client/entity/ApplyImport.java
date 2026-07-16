package com.spt.bas.client.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spt.pm.inter.IPmEntity;
import com.spt.tools.jpa.vo.IdEntity;

/**
 * 申请单-进口代理申请单
 */
@Entity
@Table(name = "t_apply_import")
public class ApplyImport extends IdEntity implements IPmEntity{

	private static final long serialVersionUID = -8649165423919673273L;
	private	Long	approveId;				//	审批id	bigint
	private	Long	enterpriseId;			//	企业账套ID	bigint
	private	String	approveNo;				//	审批编号	varchar(50)
	private	BigDecimal	grossProfit;		//	毛利润	decimal(16,3)
	private	BigDecimal	differPrice;		//	差价	decimal(16,2)
	private	String	status;					//	审批状态	char(1)
	private	String	remark;					//	备注	varchar(200)
	private	BigDecimal	bondAmount;			//	保证金	decimal(16,3)
	private	BigDecimal	agentAmount;		//	代理费	decimal(16,3)
	private	String	fileId;					//	附件id
	private String ourCompanyName; 			//	我方公司名称
	private BigDecimal buyAmount; 			// 	买方总价
	private BigDecimal sellAmount; 			// 	买方总价
	private String businessType;			//	业务类型
	private String foreignContractNo;	 	//	外商合同号
	private String contractAttr;			//	合同属性
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private	Date arrivalTime;			    //	到货时间
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private	Date bondTime;					//	保证金时间
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private	Date fullTime;					//	付全款时间
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private	Date shippingDate;			    //	船期
	private	String	payCondition;			//	付款条款
	private	String	port;					//	装运港
	
	public Long getApproveId() {
		return approveId;
	}
	public void setApproveId(Long approveId) {
		this.approveId = approveId;
	}
	public Long getEnterpriseId() {
		return enterpriseId;
	}
	public void setEnterpriseId(Long enterpriseId) {
		this.enterpriseId = enterpriseId;
	}
	public String getApproveNo() {
		return approveNo;
	}
	public void setApproveNo(String approveNo) {
		this.approveNo = approveNo;
	}
	public BigDecimal getGrossProfit() {
		return grossProfit;
	}
	public void setGrossProfit(BigDecimal grossProfit) {
		this.grossProfit = grossProfit;
	}
	public BigDecimal getDifferPrice() {
		return differPrice;
	}
	public void setDifferPrice(BigDecimal differPrice) {
		this.differPrice = differPrice;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public BigDecimal getBondAmount() {
		return bondAmount;
	}
	public void setBondAmount(BigDecimal bondAmount) {
		this.bondAmount = bondAmount;
	}
	public BigDecimal getAgentAmount() {
		return agentAmount;
	}
	public void setAgentAmount(BigDecimal agentAmount) {
		this.agentAmount = agentAmount;
	}
	public String getFileId() {
		return fileId;
	}
	public void setFileId(String fileId) {
		this.fileId = fileId;
	}
	public String getOurCompanyName() {
		return ourCompanyName;
	}
	public void setOurCompanyName(String ourCompanyName) {
		this.ourCompanyName = ourCompanyName;
	}
	public BigDecimal getBuyAmount() {
		return buyAmount;
	}
	public void setBuyAmount(BigDecimal buyAmount) {
		this.buyAmount = buyAmount;
	}
	public BigDecimal getSellAmount() {
		return sellAmount;
	}
	public void setSellAmount(BigDecimal sellAmount) {
		this.sellAmount = sellAmount;
	}
	public String getBusinessType() {
		return businessType;
	}
	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}
	public String getForeignContractNo() {
		return foreignContractNo;
	}
	public void setForeignContractNo(String foreignContractNo) {
		this.foreignContractNo = foreignContractNo;
	}
	public String getContractAttr() {
		return contractAttr;
	}
	public void setContractAttr(String contractAttr) {
		this.contractAttr = contractAttr;
	}
	public Date getArrivalTime() {
		return arrivalTime;
	}
	public void setArrivalTime(Date arrivalTime) {
		this.arrivalTime = arrivalTime;
	}
	public Date getBondTime() {
		return bondTime;
	}
	public void setBondTime(Date bondTime) {
		this.bondTime = bondTime;
	}
	public Date getFullTime() {
		return fullTime;
	}
	public void setFullTime(Date fullTime) {
		this.fullTime = fullTime;
	}
	public Date getShippingDate() {
		return shippingDate;
	}
	public void setShippingDate(Date shippingDate) {
		this.shippingDate = shippingDate;
	}
	public String getPayCondition() {
		return payCondition;
	}
	public void setPayCondition(String payCondition) {
		this.payCondition = payCondition;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}

}
