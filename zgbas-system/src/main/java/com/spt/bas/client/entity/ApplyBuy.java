package com.spt.bas.client.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.spt.bas.client.constant.BasConstants;
import com.spt.pm.inter.IPmEntity;
import com.spt.tools.jpa.vo.IdEntity;

/**
 * 申请单-采购申请单
 */
@Entity
@Table(name = "t_apply_buy")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ApplyBuy extends IdEntity implements IPmEntity{

	private static final long serialVersionUID = -890548234644169533L;
	private	Long	contractId;			//合同id
	private	Long	companyId;			//供货商公司ID
	private	String	companyName;		//供货商名称
	private	String	contactName;		//联系人
	private	String	contactPhone;		//联系电话
	private	String	contactAddr;		//联系地址
	private	String	receiveBank;		//收款银行
	private	String	receiveAccount;		//收款账号
	private	Long	enterpriseId;		//公司ID
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private	Date	arrivalTime;		//到货时间
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private	Date	payBondTime;			//付定金时间
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private	Date	payFullTime;			//付全款时间
	
	private	String	payType;			//付款方式		
	private	BigDecimal	bondAmount = BigDecimal.ZERO;		//定金金额
	private	BigDecimal	bondRate;		//定金比例
	private String deliveryMode;		//结算方式		XHHK-货到付款  XKHH-款到发货
	private	String	deliveryType;		//交货方式		乙方自提ZT、甲方配送PS
	@JsonSerialize(using = ToStringSerializer.class)
	private	Boolean	transferFlg;		//是否需转货权
	private	BigDecimal	transportAmount = BigDecimal.ZERO;	//运输费
	private	BigDecimal	warehouseAmount = BigDecimal.ZERO;	//仓储费
	
	private	String	remark;				//备注
	private	String	status;				//申请状态		N-新增，A-审批中，B-驳回，D-完成
	private	Long	approveId;			//审批ID
	private	String	approveNo;			//审批编号
	private String 	deliveryPhone;		//配送电话
	private String  deliveryAddr;		//配送地址
	private String contractNo;			//合同编号
	private String fileId;				//附件Id
	private BigDecimal totalAmount;//合同总价
	private String ourCompanyName; //我方公司名称
	private String contractAttr;//合同属性：N-现货，F-期货
	private String shippingAddr; //配送地址
	
	private String applyType = BasConstants.APPLY_TYPE_B; //区分普通采购、预售采购
	@JsonSerialize(using = ToStringSerializer.class)
	private Boolean longFlg;//是否长约
	
	private String saasOrderId;//saas采购申请订单Id
	
	private String saasContractNo;
	
	private String businessType;//业务类型
	private String arrivalTimeExt;//到货日期(补充)
	private String payKind;//付款方式
	private String payKindCode;
	private String invoiceDate;//开票日期
	private String extraTerm;//补充条款
	private String qualityStandard;//质量标准
	private	BigDecimal grossProfit;//毛利润
	private Long sellContractId;
	@JsonSerialize(using = ToStringSerializer.class)
	private Boolean riskApproveFlg;//是否需要风控审批
	/**
	 * 承运商
	 */
	private  String  carrier;

	public String getCarrier() {
		return carrier;
	}

	public void setCarrier(String carrier) {
		this.carrier = carrier;
	}

	public Long getContractId() {
		return contractId;
	}
	public void setContractId(Long contractId) {
		this.contractId = contractId;
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
	public String getReceiveBank() {
		return receiveBank;
	}
	public void setReceiveBank(String receiveBank) {
		this.receiveBank = receiveBank;
	}
	public String getReceiveAccount() {
		return receiveAccount;
	}
	public void setReceiveAccount(String receiveAccount) {
		this.receiveAccount = receiveAccount;
	}
	public Long getEnterpriseId() {
		return enterpriseId;
	}
	public void setEnterpriseId(Long enterpriseId) {
		this.enterpriseId = enterpriseId;
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
	
	
	
	public BigDecimal getBondRate() {
		return bondRate;
	}
	public void setBondRate(BigDecimal bondRate) {
		this.bondRate = bondRate;
	}
	public String getDeliveryType() {
		return deliveryType;
	}
	public void setDeliveryType(String deliveryType) {
		this.deliveryType = deliveryType;
	}
	
	public BigDecimal getTransportAmount() {
		return transportAmount;
	}
	public void setTransportAmount(BigDecimal transportAmount) {
		this.transportAmount = transportAmount;
	}
	public BigDecimal getWarehouseAmount() {
		return warehouseAmount;
	}
	public void setWarehouseAmount(BigDecimal warehouseAmount) {
		this.warehouseAmount = warehouseAmount;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
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
	public String getFileId() {
		return fileId;
	}
	public void setFileId(String fileId) {
		this.fileId = fileId;
	}
	public String getDeliveryAddr() {
		return deliveryAddr;
	}
	public void setDeliveryAddr(String deliveryAddr) {
		this.deliveryAddr = deliveryAddr;
	}
	public String getDeliveryPhone() {
		return deliveryPhone;
	}
	public void setDeliveryPhone(String deliveryPhone) {
		this.deliveryPhone = deliveryPhone;
	}
	public BigDecimal getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
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
	public String getContractAttr() {
		return contractAttr;
	}
	public void setContractAttr(String contractAttr) {
		this.contractAttr = contractAttr;
	}
	
	public String getShippingAddr() {
		return shippingAddr;
	}
	public void setShippingAddr(String shippingAddr) {
		this.shippingAddr = shippingAddr;
	}
	public String getApplyType() {
		return applyType;
	}
	public void setApplyType(String applyType) {
		this.applyType = applyType;
	}
	public Boolean getLongFlg() {
		return longFlg;
	}
	public void setLongFlg(Boolean longFlg) {
		this.longFlg = longFlg;
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
	public BigDecimal getBondAmount() {
		return bondAmount;
	}
	public void setBondAmount(BigDecimal bondAmount) {
		this.bondAmount = bondAmount;
	}
	public Boolean getTransferFlg() {
		return transferFlg;
	}
	public void setTransferFlg(Boolean transferFlg) {
		this.transferFlg = transferFlg;
	}
	public String getSaasOrderId() {
		return saasOrderId;
	}
	public void setSaasOrderId(String saasOrderId) {
		this.saasOrderId = saasOrderId;
	}
	public String getSaasContractNo() {
		return saasContractNo;
	}
	public void setSaasContractNo(String saasContractNo) {
		this.saasContractNo = saasContractNo;
	}
	public String getBusinessType() {
		return businessType;
	}
	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}
	public String getArrivalTimeExt() {
		return arrivalTimeExt;
	}
	public void setArrivalTimeExt(String arrivalTimeExt) {
		this.arrivalTimeExt = arrivalTimeExt;
	}
	public String getPayKind() {
		return payKind;
	}
	public void setPayKind(String payKind) {
		this.payKind = payKind;
	}
	public String getInvoiceDate() {
		return invoiceDate;
	}
	public void setInvoiceDate(String invoiceDate) {
		this.invoiceDate = invoiceDate;
	}
	public String getExtraTerm() {
		return extraTerm;
	}
	public void setExtraTerm(String extraTerm) {
		this.extraTerm = extraTerm;
	}
	public String getQualityStandard() {
		return qualityStandard;
	}
	public void setQualityStandard(String qualityStandard) {
		this.qualityStandard = qualityStandard;
	}
	public String getPayKindCode() {
		return payKindCode;
	}
	public void setPayKindCode(String payKindCode) {
		this.payKindCode = payKindCode;
	}
	public BigDecimal getGrossProfit() {
		return grossProfit;
	}
	public void setGrossProfit(BigDecimal grossProfit) {
		this.grossProfit = grossProfit;
	}
	public Long getSellContractId() {
		return sellContractId;
	}
	public void setSellContractId(Long sellContractId) {
		this.sellContractId = sellContractId;
	}
	public Boolean getRiskApproveFlg() {
		return riskApproveFlg;
	}
	public void setRiskApproveFlg(Boolean riskApproveFlg) {
		this.riskApproveFlg = riskApproveFlg;
	}
	
}
