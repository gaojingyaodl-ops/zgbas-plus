package com.spt.bas.client.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.spt.pm.inter.IPmEntity;
import com.spt.tools.jpa.vo.IdEntity;

/**
 * 申请单-入库申请单
 */
@Entity
@Table(name = "t_apply_delivery_in_adjust")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ApplyDeliveryInAdjust extends IdEntity implements IPmEntity{

	private static final long serialVersionUID = 1530976683810599809L;
	private	Long	enterpriseId;		//企业ID
	private	Long	contractId;			//合同id
	private	String businessNo;			//业务编号
	private	String	contractNo;			//合同编号
	private	String	deliveryType;		//配送方式		自提ZT、配送PS
	private	boolean	transferFlg;		//是否需转货权
	private	String	companyName;		//供货商名称
	private	String	contactName;		//联系人
	private	String	contactPhone;		//联系电话
	private	String	contactAddr;		//联系地址
	private	String	deliveryPhone;		//提货电话
	private	String	deliveryAddr;		//提货地址
	private	Long	companyId;			//公司id
	private	String	fileId;				//附件id
	private	String	status;				//审批状态		N-新增，A-审批中，B-驳回，D-完成
	private	Long	approveId;			//审批id
	private	String	remark;				//备注
	private	String	applyNo;			//审批编号
	private String warehousePosition;	//仓位/货位
	private String warehouseBatchNo;	//批号
	
	private Long oldApplyId;//原入库申请Id
	
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
	public String getContractNo() {
		return contractNo;
	}
	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}
	public String getDeliveryType() {
		return deliveryType;
	}
	public void setDeliveryType(String deliveryType) {
		this.deliveryType = deliveryType;
	}
	public boolean isTransferFlg() {
		return transferFlg;
	}
	public void setTransferFlg(boolean transferFlg) {
		this.transferFlg = transferFlg;
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
	public Long getCompanyId() {
		return companyId;
	}
	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}
	public String getFileId() {
		return fileId;
	}
	public void setFileId(String fileId) {
		this.fileId = fileId;
	}
	
	public Long getApproveId() {
		return approveId;
	}
	public void setApproveId(Long approveId) {
		this.approveId = approveId;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getApplyNo() {
		return applyNo;
	}
	public void setApplyNo(String approveNo) {
		this.applyNo = approveNo;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getBusinessNo() {
		return businessNo;
	}
	public void setBusinessNo(String businessNo) {
		this.businessNo = businessNo;
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
	public Long getOldApplyId() {
		return oldApplyId;
	}
	public void setOldApplyId(Long oldApplyId) {
		this.oldApplyId = oldApplyId;
	}	
	
}
