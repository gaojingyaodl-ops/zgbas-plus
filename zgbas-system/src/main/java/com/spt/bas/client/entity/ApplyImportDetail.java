package com.spt.bas.client.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spt.tools.jpa.vo.IdEntity;

/**
 * 进口代理详情
 * @author unknow1
 *
 */
@Entity
@Table(name = "t_apply_import_detail")
public class ApplyImportDetail extends IdEntity{
	private static final long serialVersionUID = 1L;
	private	Long	applyImportId;		//进口代理ID
	private	String	contractType;		//类型		B-采购，S-销售
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private	Date	arrivalTime;		//到货时间
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	//private	Date	payTime;			//付款时间
	//private	BigDecimal	payAmount;		//付款金额
	private	String	payType;			//付款方式		现金cash、信用证credit、承兑-accept
	private	BigDecimal	payRate;		//付款比例
	private	String	payRemark;			//付款备注
	private	String	receiveType;		//收款方式		现金cash、信用证credit、承兑-accept
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	//private	Date	receiveTime;		//收款时间
	//private	BigDecimal	contractAmount;	//收款金额
	private	BigDecimal	receiveRate;	//收款比例
	private	String	receiveRemark;		//收款备注
	private	String	deliveryMode;		//交货方式		款到发货-XKHH、款到发货分批-XKHHFP、货到付款-XHHK
	private	String	deliveryType;		//配送方式		自提-ZT、配送-PS
	private	BigDecimal	warehouseCost;	//仓储费
	private	BigDecimal	transportCost;	//运输费
	private	String	status;		//申请状态		状态 'N-新增，A-审批中，B-驳回，D-完成'
	private	Long	enterpriseId;		//企业账套ID
	private	Long	contractId;			//合同ID
	private	String	companyName;		//对方公司名称
	private	String	contactName;		//供货商联系人
	private	String	contactPhone;		//联系电话
	private	String	contactAddr;		//联系地址
	private	String	companyBank;		//银行
	private	String	companyAccount;		//公司账号
	private String taxNumber;			//需货商 税号
	private Long companyId;         	 //公司ID
	private	String	payCondition;		//	付款条款
	private	String	port;				//	装运港
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private	Date	shippingDate;		//	船期
	private String contractNo;			//合同编号
	private String contractAttr;	//合同属性：N-现货，F-期货
	
	private BigDecimal payBondAmount;	 //银行保证金
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date payBondTime;			 //付银行保证金时间
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date payFullTime;			 //付全款日期
	private BigDecimal receiveBondAmount;//付款金额
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date receiveBondTime;		 //收银行保证金时间
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date receiveFullTime;		 //收全款时间
	private String objectivePort;		 //目的港
	private String foreignContractNo;	 //外商合同号
	
	private String businessType;//业务类型
	private BigDecimal qingguanFee;//清关费(元)
	private BigDecimal kaizhengFee;//开证手续费(元)
	private BigDecimal chengduiFee;//承兑费(元)
	private BigDecimal dailiFee;//代理费(元)
	
	
	public Long getApplyImportId() {
		return applyImportId;
	}
	public void setApplyImportId(Long applyImportId) {
		this.applyImportId = applyImportId;
	}
	public String getContractType() {
		return contractType;
	}
	public void setContractType(String contractType) {
		this.contractType = contractType;
	}
	public Date getArrivalTime() {
		return arrivalTime;
	}
	public void setArrivalTime(Date arrivalTime) {
		this.arrivalTime = arrivalTime;
	}
	public String getPayType() {
		return payType;
	}
	public void setPayType(String payType) {
		this.payType = payType;
	}
	public BigDecimal getPayRate() {
		return payRate;
	}
	public void setPayRate(BigDecimal payRate) {
		this.payRate = payRate;
	}
	public String getPayRemark() {
		return payRemark;
	}
	public void setPayRemark(String payRemark) {
		this.payRemark = payRemark;
	}
	public String getReceiveType() {
		return receiveType;
	}
	public void setReceiveType(String receiveType) {
		this.receiveType = receiveType;
	}
	public BigDecimal getReceiveRate() {
		return receiveRate;
	}
	public void setReceiveRate(BigDecimal receiveRate) {
		this.receiveRate = receiveRate;
	}
	public String getReceiveRemark() {
		return receiveRemark;
	}
	public void setReceiveRemark(String receiveRemark) {
		this.receiveRemark = receiveRemark;
	}
	public String getDeliveryMode() {
		return deliveryMode;
	}
	public void setDeliveryMode(String deliveryMode) {
		this.deliveryMode = deliveryMode;
	}
	public String getDeliveryType() {
		return deliveryType;
	}
	public void setDeliveryType(String deliveryType) {
		this.deliveryType = deliveryType;
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
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Long getEnterpriseId() {
		return enterpriseId;
	}
	public void setEnterpriseId(Long enterpriseId) {
		this.enterpriseId = enterpriseId;
	}
	public Long getContractId() {
		return contractId;
	}
	public void setContractId(Long contractId) {
		this.contractId = contractId;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public String getContactName() {
		return contactName;
	}
	public void setContactName(String contactName) {
		this.contactName = contactName;
	}
	public String getContactPhone() {
		return contactPhone;
	}
	public void setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
	}
	public String getContactAddr() {
		return contactAddr;
	}
	public void setContactAddr(String contactAddr) {
		this.contactAddr = contactAddr;
	}
	public String getCompanyBank() {
		return companyBank;
	}
	public void setCompanyBank(String companyBank) {
		this.companyBank = companyBank;
	}
	public String getCompanyAccount() {
		return companyAccount;
	}
	public void setCompanyAccount(String companyAccount) {
		this.companyAccount = companyAccount;
	}
	public String getTaxNumber() {
		return taxNumber;
	}
	public void setTaxNumber(String taxNumber) {
		this.taxNumber = taxNumber;
	}
	public Long getCompanyId() {
		return companyId;
	}
	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
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
	public Date getShippingDate() {
		return shippingDate;
	}
	public void setShippingDate(Date shippingDate) {
		this.shippingDate = shippingDate;
	}
	public String getContractNo() {
		return contractNo;
	}
	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}
	public String getContractAttr() {
		return contractAttr;
	}
	public void setContractAttr(String contractAttr) {
		this.contractAttr = contractAttr;
	}
	public BigDecimal getPayBondAmount() {
		return payBondAmount;
	}
	public void setPayBondAmount(BigDecimal payBondAmount) {
		this.payBondAmount = payBondAmount;
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
	public BigDecimal getReceiveBondAmount() {
		return receiveBondAmount;
	}
	public void setReceiveBondAmount(BigDecimal receiveBondAmount) {
		this.receiveBondAmount = receiveBondAmount;
	}
	public Date getReceiveBondTime() {
		return receiveBondTime;
	}
	public void setReceiveBondTime(Date receiveBondTime) {
		this.receiveBondTime = receiveBondTime;
	}
	public Date getReceiveFullTime() {
		return receiveFullTime;
	}
	public void setReceiveFullTime(Date receiveFullTime) {
		this.receiveFullTime = receiveFullTime;
	}
	public String getObjectivePort() {
		return objectivePort;
	}
	public void setObjectivePort(String objectivePort) {
		this.objectivePort = objectivePort;
	}
	public String getForeignContractNo() {
		return foreignContractNo;
	}
	public void setForeignContractNo(String foreignContractNo) {
		this.foreignContractNo = foreignContractNo;
	}
	public String getBusinessType() {
		return businessType;
	}
	public void setBusinessType(String businessType) {
		this.businessType = businessType;
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
	
	
}
