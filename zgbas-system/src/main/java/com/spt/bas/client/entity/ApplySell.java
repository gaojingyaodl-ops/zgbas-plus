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
 * 申请单-销售申请单
 */
@Entity
@Table(name = "t_apply_sell")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ApplySell extends IdEntity implements IPmEntity{

	private static final long serialVersionUID = -739111804784127743L;
	private	Long	contractId;			//合同id
	private	Long	approveId;			//审批ID
	private	String	approveNo;			//审批编号
	private	Long	enterpriseId;		//企业ID
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private	Date	deliveryTime;		//交货时间
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private	Date	receiveBondTime;	//收定金时间
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private	Date	receiveFullTime;	//收全款时间
	private	String	receiveType;		//收款方式		现金cash、信用证credit
	private	BigDecimal	bondAmount = BigDecimal.ZERO;		//定金金额
	private	BigDecimal	receiveRate;	//定金比例
	private	String	receiveRemark;		//收款备注
	private	String	deliveryMode;		//结算方式  SX-赊销  XHHK-货到付款  XKHH-款到发货
	private	String	deliveryType;		//提货方式		自提ZT、配送PS
	private	BigDecimal	transportCost = BigDecimal.ZERO;	//运输费
	private	BigDecimal	warehouseCost = BigDecimal.ZERO;	//仓储费
	private	String	remark;				//备注
	private	Long	companyId;			//采购商公司ID
	private	String	companyName;		//采购商名称
	private	String	contactName;		//联系人
	private	String	contactPhone;		//联系电话
	private	String	contactAddr;		//联系地址
	private	String	status;				//申请状态		N-新增，A-审批中，B-驳回，D-完成
	private String  contractNo;			//合同编号
	private String fileId;				//附件id
	private BigDecimal totalAmount;		//合同总价
	private String ourCompanyName; 		//我方公司名称
	private String realOurCompanyName;	//回购抬头
	private String contractAttr;		//合同属性：N-现货，F-期货
	private String shippingAddr;   		//交货地点
	private String qualityStandard; 	//质量标准  Y-原厂标准，G-过渡料，F-副牌料
	private String payMode;				// 付款方式
	private String payModeCode;
	private String attachDeliveryTime; 	// 附加交货时间   
	private	BigDecimal grossProfit;		//毛利润
	private	BigDecimal differPrice;		//差价
	private String businessType;		//业务类型
	private String extraTerm;			//补充条款
	private Integer creditDays;			//账期
	private String ourCompanyBank;		//我方开户行(赊销申请)
	private String applyType = BasConstants.APPLY_TYPE_S; //区分普通销售和预售
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
	public Long getContractId() {
		return contractId;
	}
	public void setContractId(Long contractId) {
		this.contractId = contractId;
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
	public Long getEnterpriseId() {
		return enterpriseId;
	}
	public void setEnterpriseId(Long enterpriseId) {
		this.enterpriseId = enterpriseId;
	}
	public Date getDeliveryTime() {
		return deliveryTime;
	}
	public void setDeliveryTime(Date deliverylTime) {
		this.deliveryTime = deliverylTime;
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
	public BigDecimal getTransportCost() {
		return transportCost;
	}
	public void setTransportCost(BigDecimal transportCost) {
		this.transportCost = transportCost;
	}
	public BigDecimal getWarehouseCost() {
		return warehouseCost;
	}
	public void setWarehouseCost(BigDecimal warehouseCost) {
		this.warehouseCost = warehouseCost;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
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
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
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
	public BigDecimal getBondAmount() {
		return bondAmount;
	}
	public void setBondAmount(BigDecimal bondAmount) {
		this.bondAmount = bondAmount;
	}
	public String getQualityStandard() {
		return qualityStandard;
	}
	public void setQualityStandard(String qualityStandard) {
		this.qualityStandard = qualityStandard;
	}
	public String getPayMode() {
		return payMode;
	}
	public void setPayMode(String payMode) {
		this.payMode = payMode;
	}
	public String getAttachDeliveryTime() {
		return attachDeliveryTime;
	}
	public void setAttachDeliveryTime(String attachDeliveryTime) {
		this.attachDeliveryTime = attachDeliveryTime;
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
	public String getBusinessType() {
		return businessType;
	}
	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}
	public String getExtraTerm() {
		return extraTerm;
	}
	public void setExtraTerm(String extraTerm) {
		this.extraTerm = extraTerm;
	}
	public String getPayModeCode() {
		return payModeCode;
	}
	public void setPayModeCode(String payModeCode) {
		this.payModeCode = payModeCode;
	}
	public Integer getCreditDays() {
		return creditDays;
	}
	public void setCreditDays(Integer creditDays) {
		this.creditDays = creditDays;
	}
	public String getRealOurCompanyName() {
		return realOurCompanyName;
	}
	public void setRealOurCompanyName(String realOurCompanyName) {
		this.realOurCompanyName = realOurCompanyName;
	}
	public String getOurCompanyBank() {
		return ourCompanyBank;
	}
	public void setOurCompanyBank(String ourCompanyBank) {
		this.ourCompanyBank = ourCompanyBank;
	}
	public String getApplyType() {
		return applyType;
	}
	public void setApplyType(String applyType) {
		this.applyType = applyType;
	}
	public Boolean getRiskApproveFlg() {
		return riskApproveFlg;
	}
	public void setRiskApproveFlg(Boolean riskApproveFlg) {
		this.riskApproveFlg = riskApproveFlg;
	}
	
}
