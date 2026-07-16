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
 * 预售申请单
 */
@Entity
@Table(name = "t_apply_presell")
public class ApplyPresell extends IdEntity implements IPmEntity{

	private static final long serialVersionUID = 2055918437685869821L;
	private	Long	contractId;			//	合同id	
	private	Long	approveId;			//	审批ID	
	private	String	approveNo;			//	审批编号	
	private	Long	enterpriseId;		//	企业ID	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private	Date	deliveryTime;		//	交货时间	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private	Date	receiveTime;		//	收全款日期	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private	Date	receiveBondTime;	//  收定金时间
	private	String	receiveType;		//	收款方式	
	private	BigDecimal	contractAmount = BigDecimal.ZERO;	//	保证金	
	private	BigDecimal	receiveRate;	//	收款比例	
	private	String	receiveRemark;		//	收款备注	
	private	String	deliveryMode;		//	交货方式	
	private	String	deliveryType;		//	提货方式	
	private	BigDecimal	transportCost = BigDecimal.ZERO;	//	运输费	
	private	BigDecimal	warehouseCost = BigDecimal.ZERO;	//	仓储费	
	private	Long	warehouseId;		//	仓库ID	
	private	String	warehousePhone;		//	仓库配送电话	
	private	String	warehouseAddr;		//	仓库配送地址	
	private	String	remark;				//	备注	
	private	Long	companyId;			//	采购商公司ID	
	private	String	companyName;		//	采购商名称	
	private	String	contactName;		//	联系人	
	private	String	contactPhone;		//	联系电话	
	private	String	contactAddr;		//	联系地址	
	private	String	status;				//	申请状态	
	private	String	contractNo;			//	合同编号	
	private	String	fileId;				//	附件ID	
	private	BigDecimal	totalAmount;	//	合同总价	
	private	String	ourCompanyName;		//	我方企业名称	
	private	String	contractAttr;		//	合同属性	
	private	String	shippingAddr;		//	交货地点
	
	private String saasOrderId;//saas采购申请订单Id
	
	private String saasContractNo;
	
	private String qualityStandard; //质量标准  Y-原厂标准，G-过渡料，F-副牌料
	private String businessType;//业务类型
	private String arrivalTimeExt;//到货日期(补充)
	private String payKind;//付款方式
	private String payKindCode;
	private String extraTerm;//补充条款
	private Integer creditDays;			//账期
	private String ourCompanyBank;		//我方开户行
	
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
	public void setDeliveryTime(Date deliveryTime) {
		this.deliveryTime = deliveryTime;
	}
	public Date getReceiveTime() {
		return receiveTime;
	}
	public void setReceiveTime(Date receiveTime) {
		this.receiveTime = receiveTime;
	}
	public String getReceiveType() {
		return receiveType;
	}
	public void setReceiveType(String receiveType) {
		this.receiveType = receiveType;
	}
	public BigDecimal getContractAmount() {
		return contractAmount;
	}
	public void setContractAmount(BigDecimal contractAmount) {
		this.contractAmount = contractAmount;
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
	public Long getWarehouseId() {
		return warehouseId;
	}
	public void setWarehouseId(Long warehouseId) {
		this.warehouseId = warehouseId;
	}
	public String getWarehousePhone() {
		return warehousePhone;
	}
	public void setWarehousePhone(String warehousePhone) {
		this.warehousePhone = warehousePhone;
	}
	public String getWarehouseAddr() {
		return warehouseAddr;
	}
	public void setWarehouseAddr(String warehouseAddr) {
		this.warehouseAddr = warehouseAddr;
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
	public Date getReceiveBondTime() {
		return receiveBondTime;
	}
	public void setReceiveBondTime(Date receiveBondTime) {
		this.receiveBondTime = receiveBondTime;
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
	public Integer getCreditDays() {
		return creditDays;
	}
	public void setCreditDays(Integer creditDays) {
		this.creditDays = creditDays;
	}
	public String getOurCompanyBank() {
		return ourCompanyBank;
	}
	public void setOurCompanyBank(String ourCompanyBank) {
		this.ourCompanyBank = ourCompanyBank;
	}
	
}
