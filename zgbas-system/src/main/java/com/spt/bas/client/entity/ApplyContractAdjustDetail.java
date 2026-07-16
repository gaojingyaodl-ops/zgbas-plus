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
 * 合同调整明细
 */
@Entity
@Table(name = "t_apply_contract_adjust_detail")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ApplyContractAdjustDetail extends IdEntity {

	private static final long serialVersionUID = 2138230401322035037L;
	private	Long contractAdjustId;	//	合同调整id
	private	BigDecimal	totalAmount;//	合同总价
	private	String	remark;			//	备注
	private	BigDecimal	bondAmount;	//	定金金额
	private	BigDecimal	totalNumber;//	总数量
	private	String	contractAttr;	//	合同属性
	private	Date	createdDate;	//	创建时间
	private	Date	updatedDate;	//	更新时间
	private	Long	enterpriseId;	//	企业账套ID
	private	String	deliveryType;	//	提货方式
	private	String	deliveryMode;	//	交货方式
	private	String	payType;		//	付款方式
	private String detailType;		//  明细类型	O:原合同  N:新合同
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date payFullTime;		//	付全款日期
	private Long companyId;			//  企业ID
	private String companyName;		//	企业名称
	public Long getContractAdjustId() {
		return contractAdjustId;
	}
	public void setContractAdjustId(Long contractAdjustId) {
		this.contractAdjustId = contractAdjustId;
	}
	public BigDecimal getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
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
	public BigDecimal getTotalNumber() {
		return totalNumber;
	}
	public void setTotalNumber(BigDecimal totalNumber) {
		this.totalNumber = totalNumber;
	}
	
	public String getContractAttr() {
		return contractAttr;
	}
	public void setContractAttr(String contractAttr) {
		this.contractAttr = contractAttr;
	}
	public Date getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	public Date getUpdatedDate() {
		return updatedDate;
	}
	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}
	public Long getEnterpriseId() {
		return enterpriseId;
	}
	public void setEnterpriseId(Long enterpriseId) {
		this.enterpriseId = enterpriseId;
	}
	public String getDetailType() {
		return detailType;
	}
	public void setDetailType(String detailType) {
		this.detailType = detailType;
	}
	public String getDeliveryType() {
		return deliveryType;
	}
	public void setDeliveryType(String deliveryType) {
		this.deliveryType = deliveryType;
	}
	public String getDeliveryMode() {
		return deliveryMode;
	}
	public void setDeliveryMode(String deliveryMode) {
		this.deliveryMode = deliveryMode;
	}
	public String getPayType() {
		return payType;
	}
	public void setPayType(String payType) {
		this.payType = payType;
	}
	public Date getPayFullTime() {
		return payFullTime;
	}
	public void setPayFullTime(Date payFullTime) {
		this.payFullTime = payFullTime;
	}
	public Long getCompanyId() {
		return companyId;
	}
	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	
}
