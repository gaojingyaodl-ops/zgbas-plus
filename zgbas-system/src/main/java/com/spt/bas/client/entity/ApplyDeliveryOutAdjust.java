package com.spt.bas.client.entity;


import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.spt.pm.inter.IPmEntity;
import com.spt.tools.jpa.vo.IdEntity;
/**
 * 申请单-新出库申请单
 */
@Entity
@Table(name = "t_apply_delivery_out_Adjust")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ApplyDeliveryOutAdjust extends IdEntity implements IPmEntity{


	private static final long serialVersionUID = 8435560825962509054L;
	private	Long	contractId	;			//合同id
	private	String  businessNo;			//业务编号
	private	String	contractNo	;			//合同编号
	private	Long	approveId	;			//审批id
	private	String	applyNo	;			//审批编号
	private	Date	deliveryDate;			//交货日期
	private	String	deliveryType;			//配送方式
	private	String	deliveryMode;			//交货方式
	private	String	deliveryPhone;			//仓库/配送电话
	private	String	deliveryAddr;			//仓库/配送地址
	private	BigDecimal	payAmount;			//已付金额

	private	String	plateNumber;			//车牌号
	private	String	driverCardNo;			//驾驶员身份证号
	private	String	driverName;				//司机
	private String driverPhone;				//司机电话
	private	String	fileId;					//附件id
	private	String	status;					//状态
	private	String	remark;					//备注
	private	Long	enterpriseId;			//企业账套ID
	private	Long	companyId;				//公司id
	private	String	companyName;			//公司名称
	private String contactName;				//联系人
	private	String	contactPhone;			//联系电话
	private	String	contactAddr;			//联系地址
	private String warehousePosition;	//仓位/货位
	private String warehouseBatchNo;	//批号
	
	private Long oldApplyId;
		
	public Long getContractId() {
		return contractId;
	}
	public void setContractId(Long contractId) {
		this.contractId = contractId;
	}
	public String getContractNo() {
		return contractNo;
	}
	public String getContactName() {
		return contactName;
	}
	public void setContactName(String contactName) {
		this.contactName = contactName;
	}
	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}
	public Long getApproveId() {
		return approveId;
	}
	public void setApproveId(Long approveId) {
		this.approveId = approveId;
	}
	public String getApplyNo() {
		return applyNo;
	}
	public void setApplyNo(String approveNo) {
		this.applyNo = approveNo;
	}
	public Date getDeliveryDate() {
		return deliveryDate;
	}
	public void setDeliveryDate(Date deliveryDate) {
		this.deliveryDate = deliveryDate;
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
	public String getDeliveryPhone() {
		return deliveryPhone;
	}
	public void setDeliveryPhone(String deliveryPhone) {
		this.deliveryPhone = deliveryPhone;
	}
	public String getDeliveryAddr() {
		return deliveryAddr;
	}
	public void setDeliveryAddr(String deliveryAddr) {
		this.deliveryAddr = deliveryAddr;
	}
	public BigDecimal getPayAmount() {
		return payAmount;
	}
	public void setPayAmount(BigDecimal payAmount) {
		this.payAmount = payAmount;
	}
	
	public String getPlateNumber() {
		return plateNumber;
	}
	public void setPlateNumber(String plateNumber) {
		this.plateNumber = plateNumber;
	}
	public String getDriverCardNo() {
		return driverCardNo;
	}
	public void setDriverCardNo(String driverCardNo) {
		this.driverCardNo = driverCardNo;
	}
	public String getDriverName() {
		return driverName;
	}
	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}
	public String getFileId() {
		return fileId;
	}
	public void setFileId(String fileId) {
		this.fileId = fileId;
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
	public Long getEnterpriseId() {
		return enterpriseId;
	}
	public void setEnterpriseId(Long enterpriseId) {
		this.enterpriseId = enterpriseId;
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
	public String getBusinessNo() {
		return businessNo;
	}
	public void setBusinessNo(String businessNo) {
		this.businessNo = businessNo;
	}
	public String getWarehousePosition() {
		return warehousePosition;
	}
	public void setWarehousePosition(String warehousePosition) {
		this.warehousePosition = warehousePosition;
	}
	public String getWarehouseBatchNo() {
		return warehouseBatchNo;
	}
	public void setWarehouseBatchNo(String warehouseBatchNo) {
		this.warehouseBatchNo = warehouseBatchNo;
	}
	public String getDriverPhone() {
		return driverPhone;
	}
	public void setDriverPhone(String driverPhone) {
		this.driverPhone = driverPhone;
	}
	public Long getOldApplyId() {
		return oldApplyId;
	}
	public void setOldApplyId(Long oldApplyId) {
		this.oldApplyId = oldApplyId;
	}
	
	
	
}
