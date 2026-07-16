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
 * 二次结算申请单
 *
 */
@Entity
@Table(name = "t_apply_calculate")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ApplyCalculate extends IdEntity{
	private static final long serialVersionUID = -4711636924377591604L;
	private String calculateNo;			//二次结算单号
	private Long contractId;
	private String contractNo;		
	private Long oldProductDetailId;	//旧货物明细Id
	private Long newProductDetailId;	//新货物明细Id
	private BigDecimal dealNumber;		//修改-数量差值
	private BigDecimal dealPrice;		//修改-单价差值alcu
	private Long bizUserId;
	private String bizUserName;
	private Long enterpriseId;
	private String status;
	private Long importDetailId;
	
	private BigDecimal payBondAmount;   //银行保证金
	private BigDecimal warehouseCost;	//仓储费
	private BigDecimal transportCost;	//运输费
	private BigDecimal qingguanFee;		//清关费
	private BigDecimal kaizhengFee;		//开证手续费
	private BigDecimal chengduiFee;		//承兑费
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date arrivalTime;			//到货时间
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date payBondTime;			//保证金时间
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date payFullTime;			//付全款时间
	private BigDecimal dailiFee;		//代理费
	
	public Long getContractId() {
		return contractId;
	}
	public void setContractId(Long contractId) {
		this.contractId = contractId;
	}
	public String getContractNo() {
		return contractNo;
	}
	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}
	public Long getOldProductDetailId() {
		return oldProductDetailId;
	}
	public void setOldProductDetailId(Long oldProductDetailId) {
		this.oldProductDetailId = oldProductDetailId;
	}
	public Long getNewProductDetailId() {
		return newProductDetailId;
	}
	public void setNewProductDetailId(Long newProductDetailId) {
		this.newProductDetailId = newProductDetailId;
	}
	public BigDecimal getDealNumber() {
		return dealNumber;
	}
	public void setDealNumber(BigDecimal dealNumber) {
		this.dealNumber = dealNumber;
	}
	public BigDecimal getDealPrice() {
		return dealPrice;
	}
	public void setDealPrice(BigDecimal dealPrice) {
		this.dealPrice = dealPrice;
	}
	public Long getEnterpriseId() {
		return enterpriseId;
	}
	public void setEnterpriseId(Long enterpriseId) {
		this.enterpriseId = enterpriseId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Long getBizUserId() {
		return bizUserId;
	}
	public void setBizUserId(Long bizUserId) {
		this.bizUserId = bizUserId;
	}
	public String getBizUserName() {
		return bizUserName;
	}
	public void setBizUserName(String bizUserName) {
		this.bizUserName = bizUserName;
	}
	public String getCalculateNo() {
		return calculateNo;
	}
	public void setCalculateNo(String calculateNo) {
		this.calculateNo = calculateNo;
	}
	public Long getImportDetailId() {
		return importDetailId;
	}
	public void setImportDetailId(Long importDetailId) {
		this.importDetailId = importDetailId;
	}
	public BigDecimal getPayBondAmount() {
		return payBondAmount;
	}
	public void setPayBondAmount(BigDecimal payBondAmount) {
		this.payBondAmount = payBondAmount;
	}
	public BigDecimal getWarehouseCost() {
		return warehouseCost;
	}
	public void setWarehouseCost(BigDecimal warehouseCost) {
		this.warehouseCost = warehouseCost;
	}
	public BigDecimal getTransportCost() {
		return transportCost;
	}
	public void setTransportCost(BigDecimal transportCost) {
		this.transportCost = transportCost;
	}
	public BigDecimal getQingguanFee() {
		return qingguanFee;
	}
	public void setQingguanFee(BigDecimal qingguanFee) {
		this.qingguanFee = qingguanFee;
	}
	public BigDecimal getKaizhengFee() {
		return kaizhengFee;
	}
	public void setKaizhengFee(BigDecimal kaizhengFee) {
		this.kaizhengFee = kaizhengFee;
	}
	public BigDecimal getChengduiFee() {
		return chengduiFee;
	}
	public void setChengduiFee(BigDecimal chengduiFee) {
		this.chengduiFee = chengduiFee;
	}
	public Date getArrivalTime() {
		return arrivalTime;
	}
	public void setArrivalTime(Date arrivalTime) {
		this.arrivalTime = arrivalTime;
	}
	public Date getPayBondTime() {
		return payBondTime;
	}
	public void setPayBondTime(Date payBondTime) {
		this.payBondTime = payBondTime;
	}
	public Date getPayFullTime() {
		return payFullTime;
	}
	public void setPayFullTime(Date payFullTime) {
		this.payFullTime = payFullTime;
	}
	public BigDecimal getDailiFee() {
		return dailiFee;
	}
	public void setDailiFee(BigDecimal dailiFee) {
		this.dailiFee = dailiFee;
	}
	
}
