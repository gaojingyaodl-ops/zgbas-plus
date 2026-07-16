package com.spt.bas.client.vo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spt.bas.client.entity.ApplyProductDetail;

public class ApplyCalculateDetailVo{
	private Long id;
	private String randomNumber;
	private Long contractId;
	private String contractNo;
	private Long bizUserId;
	private String bizUserName;
	private Long enterpriseId;
	
	/**代理采证修改数据*/
	private String	contractType;		//类型		B-采购，S-销售
	private String	deliveryMode;		//交货方式	款到发货-XKHH、款到发货分批-XKHHFP、货到付款-XHHK
	private BigDecimal payBondAmount;	//银行收保证金
	private BigDecimal	warehouseCost;	//仓储费
	private BigDecimal	transportCost;	//运输费
	private BigDecimal qingguanFee;		//清关费(元)
	private BigDecimal kaizhengFee;		//开证手续费(元)
	private BigDecimal chengduiFee;		//承兑费(元)
	private BigDecimal dailiFee;		//代理费(元)
	private String	payType;			//付款方式	现金cash、信用证credit、承兑-accept
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date arrivalTime;			//到货时间
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date payBondTime;			 //保证金时间
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date payFullTime;			 //付全款日期
	
	private	String	contactPhone;		 //联系电话
	private String objectivePort;		 //目的港
	
	private BigDecimal receiveBondAmount;//银行付保证金金额
	private String	receiveType;		 //收款方式	现金cash、信用证credit、承兑-accept
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date receiveFullTime;		 //收全款时间
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date receiveBondTime;		 //收保证金时间
	/***/
	
	
	private List<ApplyProductDetail> lstInsert;
	private List<ApplyProductDetail> lstUpdate;
	private List<ApplyProductDetail> lstDelete;
	
	//private List<ApplyImportDetailVo> lsImportUpdate;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getRandomNumber() {
		return randomNumber;
	}
	public void setRandomNumber(String randomNumber) {
		this.randomNumber = randomNumber;
	}
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
	public String getDeliveryMode() {
		return deliveryMode;
	}
	public void setDeliveryMode(String deliveryMode) {
		this.deliveryMode = deliveryMode;
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
	public BigDecimal getDailiFee() {
		return dailiFee;
	}
	public void setDailiFee(BigDecimal dailiFee) {
		this.dailiFee = dailiFee;
	}
	public String getPayType() {
		return payType;
	}
	public void setPayType(String payType) {
		this.payType = payType;
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
	public String getContactPhone() {
		return contactPhone;
	}
	public void setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
	}
	public String getObjectivePort() {
		return objectivePort;
	}
	public void setObjectivePort(String objectivePort) {
		this.objectivePort = objectivePort;
	}
	public BigDecimal getReceiveBondAmount() {
		return receiveBondAmount;
	}
	public void setReceiveBondAmount(BigDecimal receiveBondAmount) {
		this.receiveBondAmount = receiveBondAmount;
	}
	public String getReceiveType() {
		return receiveType;
	}
	public void setReceiveType(String receiveType) {
		this.receiveType = receiveType;
	}
	public Date getReceiveFullTime() {
		return receiveFullTime;
	}
	public void setReceiveFullTime(Date receiveFullTime) {
		this.receiveFullTime = receiveFullTime;
	}
	public Date getReceiveBondTime() {
		return receiveBondTime;
	}
	public void setReceiveBondTime(Date receiveBondTime) {
		this.receiveBondTime = receiveBondTime;
	}
	
}
