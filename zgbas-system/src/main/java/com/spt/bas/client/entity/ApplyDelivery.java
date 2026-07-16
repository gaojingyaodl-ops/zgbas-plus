package com.spt.bas.client.entity;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.spt.tools.jpa.vo.IdEntity;

/**
 * 申请单-提货单
 */
@Entity
@Table(name = "t_apply_delivery")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ApplyDelivery extends IdEntity{
	
	private static final long serialVersionUID = -1128913338244515066L;
	private Long deliveryOutApplyId;		//出库申请单id
	private Long stockDetailId;				//库存明细id
	private	Long contractId	;				//合同id
	private Long productId;					//商品Id
	private String warehouseInNo;			//入库单号
	private String countersNumber;			//柜数
	private String warehouseNo;				//出库单号
	private String operation;      			//操作状态
	private Integer printCount;    		 	//打印次数
	private Long enterpriseId;				//企业id	
	private String factoryName;				//厂商
	private String brandNumber;				//牌号
	private String productCd;				//商品代码
	private String productName;				//商品名称
	private	Long approveId	;				//审批id
	private Long warehouseId;				//仓库id
	private String warehouse;				//仓库名称
	private	String driverName;				//提货人
	private	String driverPhone;				//提货人电话
	private	String deliveryAddr;			//提货地址
	private String contactName;				//联系人
	private	String contactPhone;			//联系电话
	private	String contactAddr;				//送柜地址/配送地址
	private	String plateNumber;				//车牌号
	private	String driverCardNo;			//身份证号
	private	Long companyId;					//公司id
	private	String companyName;				//公司名称
	private	String companyPhone;			//公司电话
	private BigDecimal dealNumber;			//数量(吨)
	private String operatorName;			//经办人
	private String applyNo;					//单号
	private String warehousePosition;		//仓位/货位
	private String warehouseBatchNo;		//批号
	private	String remark;					//备注
	
//	private String approveNo;				//审批编号
//	private	String  businessNo;				//业务编号
//	private	String	contractNo	;			//合同编号
//	private	Date	deliveryDate;			//交货日期
//	private	String	deliveryType;			//配送方式
//	private	String	deliveryMode;			//交货方式
//	private	BigDecimal	payAmount;			//已付金额
//	private	String	fileId;					//附件id
//	private	String	status;					//状态
	private String content;	//临时属性	
	@Transient
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
	
	public String getOperation() {
		return operation;
	}
	public void setOperation(String operation) {
		this.operation = operation;
	}
	public Integer getPrintCount() {
		return printCount;
	}
	public void setPrintCount(Integer printCount) {
		this.printCount = printCount;
	}
//	public String getApproveNo() {
//		return approveNo;
//	}
//	public void setApproveNo(String approveNo) {
//		this.approveNo = approveNo;
//	}
	public Long getEnterpriseId() {
		return enterpriseId;
	}
	public void setEnterpriseId(Long enterpriseId) {
		this.enterpriseId = enterpriseId;
	}	
	public Long getDeliveryOutApplyId() {
		return deliveryOutApplyId;
	}
	public void setDeliveryOutApplyId(Long deliveryOutApplyId) {
		this.deliveryOutApplyId = deliveryOutApplyId;
	}
	public Long getContractId() {
		return contractId;
	}
	public void setContractId(Long contractId) {
		this.contractId = contractId;
	}
//	public String getBusinessNo() {
//		return businessNo;
//	}
//	public void setBusinessNo(String businessNo) {
//		this.businessNo = businessNo;
//	}
//	public String getContractNo() {
//		return contractNo;
//	}
//	public void setContractNo(String contractNo) {
//		this.contractNo = contractNo;
//	}
	public Long getApproveId() {
		return approveId;
	}
	public void setApproveId(Long approveId) {
		this.approveId = approveId;
	}
//	public Date getDeliveryDate() {
//		return deliveryDate;
//	}
//	public void setDeliveryDate(Date deliveryDate) {
//		this.deliveryDate = deliveryDate;
//	}
//	public String getDeliveryType() {
//		return deliveryType;
//	}
//	public void setDeliveryType(String deliveryType) {
//		this.deliveryType = deliveryType;
//	}
//	public String getDeliveryMode() {
//		return deliveryMode;
//	}
//	public void setDeliveryMode(String deliveryMode) {
//		this.deliveryMode = deliveryMode;
//	}
//	public String getDeliveryPhone() {
//		return deliveryPhone;
//	}
//	public void setDeliveryPhone(String deliveryPhone) {
//		this.deliveryPhone = deliveryPhone;
//	}
	public String getDeliveryAddr() {
		return deliveryAddr;
	}
	public void setDeliveryAddr(String deliveryAddr) {
		this.deliveryAddr = deliveryAddr;
	}
//	public BigDecimal getPayAmount() {
//		return payAmount;
//	}
//	public void setPayAmount(BigDecimal payAmount) {
//		this.payAmount = payAmount;
//	}
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
//	public String getFileId() {
//		return fileId;
//	}
//	public void setFileId(String fileId) {
//		this.fileId = fileId;
//	}
//	public String getStatus() {
//		return status;
//	}
//	public void setStatus(String status) {
//		this.status = status;
//	}
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
	public Long getWarehouseId() {
		return warehouseId;
	}
	public void setWarehouseId(Long warehouseId) {
		this.warehouseId = warehouseId;
	}
	public String getWarehouse() {
		return warehouse;
	}
	public void setWarehouse(String warehouse) {
		this.warehouse = warehouse;
	}
	public String getFactoryName() {
		return factoryName;
	}
	public void setFactoryName(String factoryName) {
		this.factoryName = factoryName;
	}
	public String getBrandNumber() {
		return brandNumber;
	}
	public void setBrandNumber(String brandNumber) {
		this.brandNumber = brandNumber;
	}
	public String getProductCd() {
		return productCd;
	}
	public void setProductCd(String productCd) {
		this.productCd = productCd;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public BigDecimal getDealNumber() {
		return dealNumber;
	}
	public void setDealNumber(BigDecimal dealNumber) {
		this.dealNumber = dealNumber;
	}
	public String getOperatorName() {
		return operatorName;
	}
	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}
	public String getApplyNo() {
		return applyNo;
	}
	public void setApplyNo(String applyNo) {
		this.applyNo = applyNo;
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
	public Long getStockDetailId() {
		return stockDetailId;
	}
	public void setStockDetailId(Long stockDetailId) {
		this.stockDetailId = stockDetailId;
	}
	public String getWarehouseInNo() {
		return warehouseInNo;
	}
	public void setWarehouseInNo(String warehouseInNo) {
		this.warehouseInNo = warehouseInNo;
	}
	public String getCountersNumber() {
		return countersNumber;
	}
	public void setCountersNumber(String countersNumber) {
		this.countersNumber = countersNumber;
	}
	public String getCompanyPhone() {
		return companyPhone;
	}
	public void setCompanyPhone(String companyPhone) {
		this.companyPhone = companyPhone;
	}
	public String getWarehouseNo() {
		return warehouseNo;
	}
	public void setWarehouseNo(String warehouseNo) {
		this.warehouseNo = warehouseNo;
	}
	public Long getProductId() {
		return productId;
	}
	public void setProductId(Long productId) {
		this.productId = productId;
	}			
	
}
