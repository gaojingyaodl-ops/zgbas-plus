package com.spt.bas.client.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spt.bas.client.constant.BasConstants;
import com.spt.pm.inter.IPmEntity;
import com.spt.tools.jpa.vo.IdEntity;


@Entity
@Table(name = "t_apply_import_buy")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ApplyImportBuy extends IdEntity implements IPmEntity{
	/**
	 * 自营进口
	 */
	private static final long serialVersionUID = 7734476169444175268L;
	private Long approveId;				//审批ID
	private String approveNo;			//审批编号
	private String contractNo;			//合同编号
	private Long contractId;			//合同ID
	private Long companyId;				//供货商ID
	private String companyName;			//供货商
	private String status;				//状态
	private BigDecimal payBondAmount;	//银行保证金
	private BigDecimal totalAmount;		//合同总价
	private String payType;				//付款方式
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date arrivalTime;			//到货时间
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date payBondTime;			//付银行保证金时间
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date payFullTime;			//付全款日期
	private String contactPhone;		//联系电话
	private String matchUserName;		//业务员
	private Long matchUserId;			//业务员ID
	private String port;				//装运港
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date shippingDate;			//船期
	private String contractAttr;		//合同属性：N-现货，F-期货
	private String objectivePort;		//目的港
	private String foreignContractNo;	//外商合同号
	private String contactAddr;			//联系地址
	private String payCondition;		//付款条款
	private String remark;				//备注
	private String fileId;				//附件ID
	private	Long enterpriseId;			//企业账套ID
	private String ourCompanyName;		//我方抬头
	private String deliveryMode;		//结算方式
	private String deliveryType;		//交货方式
	private String applyType = BasConstants.APPLY_TYPE_B;
	private String businessType;		//业务类型
	private	BigDecimal	transportCost;	//运输费
	private BigDecimal warehouseCost;	//仓储费
	private BigDecimal qingguanFee;		//清关费(元)
	private BigDecimal kaizhengFee;		//开证手续费(元)
	private BigDecimal chengduiFee;		//承兑费(元)
	private BigDecimal dailiFee;		//代理费(元)
	private String qualityStandard;		//质量标准
	public Long getApproveId() {
		return approveId;
	}
	public void setApproveId(Long approveId) {
		this.approveId = approveId;
	}
	public String getApproveNo() {
		return approveNo;
	}
	public void setApproveNo(String approveNo) {
		this.approveNo = approveNo;
	}
	public String getContractNo() {
		return contractNo;
	}
	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
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
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
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
	public String getMatchUserName() {
		return matchUserName;
	}
	public void setMatchUserName(String matchUserName) {
		this.matchUserName = matchUserName;
	}
	public Long getMatchUserId() {
		return matchUserId;
	}
	public void setMatchUserId(Long matchUserId) {
		this.matchUserId = matchUserId;
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
	public String getContractAttr() {
		return contractAttr;
	}
	public void setContractAttr(String contractAttr) {
		this.contractAttr = contractAttr;
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
	public String getContactAddr() {
		return contactAddr;
	}
	public void setContactAddr(String contactAddr) {
		this.contactAddr = contactAddr;
	}
	public String getPayCondition() {
		return payCondition;
	}
	public void setPayCondition(String payCondition) {
		this.payCondition = payCondition;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getFileId() {
		return fileId;
	}
	public void setFileId(String fileId) {
		this.fileId = fileId;
	}
	public Long getCompanyId() {
		return companyId;
	}
	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}
	public BigDecimal getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}
	public String getApplyType() {
		return applyType;
	}
	public void setApplyType(String applyType) {
		this.applyType = applyType;
	}
	public Long getEnterpriseId() {
		return enterpriseId;
	}
	public void setEnterpriseId(Long enterpriseId) {
		this.enterpriseId = enterpriseId;
	}
	public String getOurCompanyName() {
		return ourCompanyName;
	}
	public void setOurCompanyName(String ourCompanyName) {
		this.ourCompanyName = ourCompanyName;
	}
	public String getDeliveryMode() {
		return deliveryMode;
	}
	public void setDeliveryMode(String deliveryMode) {
		this.deliveryMode = deliveryMode;
	}
	public String getBusinessType() {
		return businessType;
	}
	public void setBusinessType(String businessType) {
		this.businessType = businessType;
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
	public String getDeliveryType() {
		return deliveryType;
	}
	public void setDeliveryType(String deliveryType) {
		this.deliveryType = deliveryType;
	}
	public String getQualityStandard() {
		return qualityStandard;
	}
	public void setQualityStandard(String qualityStandard) {
		this.qualityStandard = qualityStandard;
	}
	
}
