package com.spt.bas.client.vo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spt.bas.client.entity.ApplyContractAdjust;
import com.spt.bas.client.entity.ApplyProductDetail;

public class ApplyContractAdjustVo extends ApplyContractAdjust{
	
	private static final long serialVersionUID = 7631585175745581514L;

	private String deptAbbr;	//部门简码
	
	private BigDecimal totalAmount;
	
	private BigDecimal totalNumber;
	
	private BigDecimal bondAmount;
	
	private Long ctrContractId;
	
	private Long oldDetailId;
	
	private Long newDetailId;
	
	private	String	deliveryType;	//	提货方式
	private	String	deliveryMode;	//	交货方式
	private	String	payType;		//	付款方式
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date payFullTime;		//  付全款日期
	
	private List<ApplyProductDetail> lstInsert;
	private List<ApplyProductDetail> lstUpdate;
	private List<ApplyProductDetail> lstDelete;
	
	public List<ApplyProductDetail> getLstInsert() {
		return lstInsert;
	}
	public void setLstInsert(List<ApplyProductDetail> lstInsert) {
		this.lstInsert = lstInsert;
	}
	public List<ApplyProductDetail> getLstUpdate() {
		return lstUpdate;
	}
	public void setLstUpdate(List<ApplyProductDetail> lstUpdate) {
		this.lstUpdate = lstUpdate;
	}
	public List<ApplyProductDetail> getLstDelete() {
		return lstDelete;
	}
	public void setLstDelete(List<ApplyProductDetail> lstDelete) {
		this.lstDelete = lstDelete;
	}
	@Override
	public Class<?> getSubClass() {
		return ApplyProductDetail.class;
	}
	
	@Override
    @SuppressWarnings("unchecked")
	public void setBatchSub(List<?> lstInsert, List<?> lstUpdate, List<?> lstDelete) {
		setLstInsert((List<ApplyProductDetail>)lstInsert);
		setLstUpdate((List<ApplyProductDetail>)lstUpdate);
		setLstDelete((List<ApplyProductDetail>)lstDelete);
	}
	public String getDeptAbbr() {
		return deptAbbr;
	}
	public void setDeptAbbr(String deptAbbr) {
		this.deptAbbr = deptAbbr;
	}
	public BigDecimal getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}
	public BigDecimal getBondAmount() {
		return bondAmount;
	}
	public void setBondAmount(BigDecimal bondAmount) {
		this.bondAmount = bondAmount;
	}
	public Long getCtrContractId() {
		return ctrContractId;
	}
	public void setCtrContractId(Long ctrContractId) {
		this.ctrContractId = ctrContractId;
	}
	public BigDecimal getTotalNumber() {
		return totalNumber;
	}
	public void setTotalNumber(BigDecimal totalNumber) {
		this.totalNumber = totalNumber;
	}
	public Long getOldDetailId() {
		return oldDetailId;
	}
	public void setOldDetailId(Long oldDetailId) {
		this.oldDetailId = oldDetailId;
	}
	public Long getNewDetailId() {
		return newDetailId;
	}
	public void setNewDetailId(Long newDetailId) {
		this.newDetailId = newDetailId;
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
}
