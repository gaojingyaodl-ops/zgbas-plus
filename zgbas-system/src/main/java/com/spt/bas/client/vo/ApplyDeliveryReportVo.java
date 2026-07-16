package com.spt.bas.client.vo;
import java.math.BigDecimal;

import com.spt.bas.client.entity.CtrProductFee;
/**
 * 出库表单详情
 */
public class ApplyDeliveryReportVo extends CtrProductFee{
	private static final long serialVersionUID = -4392675503060354749L;
	private Long applyDeliveryId;	//出库表单ID
	private String warehouseNo;		//出库单号
	private String businessType;	//业务类型
	private String productCd;		//商品代码
	private String productName;		//商品名称
	private String brandNumber;		//牌号
	private String factoryName;		//厂商
	private String contractNo;		//合同编号
	private String ourCompanyName;	//我方抬头
	private String companyName;		//供应商
	private String companyPhone;	//公司电话
	private BigDecimal dealNumber;	//数量
	private String bizUserName;		//业务员
	private String warehouseInNo;	//入库单号
	private String contractName;	//联系人
	private	String driverName;		//提货人
	private	String driverPhone;		//提货人电话
	private	String driverCardNo;	//身份证号
	private	String plateNumber;		//车牌号
	private String warehousePosition;//仓位/货位
	private String warehouseBatchNo;//批号
	private String countersNumber;  //柜数
	private String deliveryAddr;	//提货地址
	private String remark;			//备注
	private String warehouseOutType;//出库方式
	
	public Long getApplyDeliveryId() {
		return applyDeliveryId;
	}
	public void setApplyDeliveryId(Long applyDeliveryId) {
		this.applyDeliveryId = applyDeliveryId;
	}
	public String getWarehouseNo() {
		return warehouseNo;
	}
	public void setWarehouseNo(String warehouseNo) {
		this.warehouseNo = warehouseNo;
	}
	public String getBusinessType() {
		return businessType;
	}
	public void setBusinessType(String businessType) {
		this.businessType = businessType;
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
	public String getBrandNumber() {
		return brandNumber;
	}
	public void setBrandNumber(String brandNumber) {
		this.brandNumber = brandNumber;
	}
	public String getFactoryName() {
		return factoryName;
	}
	public void setFactoryName(String factoryName) {
		this.factoryName = factoryName;
	}
	public String getContractNo() {
		return contractNo;
	}
	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}
	public String getOurCompanyName() {
		return ourCompanyName;
	}
	public void setOurCompanyName(String ourCompanyName) {
		this.ourCompanyName = ourCompanyName;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public String getCompanyPhone() {
		return companyPhone;
	}
	public void setCompanyPhone(String companyPhone) {
		this.companyPhone = companyPhone;
	}
	public BigDecimal getDealNumber() {
		return dealNumber;
	}
	public void setDealNumber(BigDecimal dealNumber) {
		this.dealNumber = dealNumber;
	}
	public String getBizUserName() {
		return bizUserName;
	}
	public void setBizUserName(String bizUserName) {
		this.bizUserName = bizUserName;
	}
	public String getWarehouseInNo() {
		return warehouseInNo;
	}
	public void setWarehouseInNo(String warehouseInNo) {
		this.warehouseInNo = warehouseInNo;
	}
	public String getDriverName() {
		return driverName;
	}
	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}
	public String getDriverPhone() {
		return driverPhone;
	}
	public void setDriverPhone(String driverPhone) {
		this.driverPhone = driverPhone;
	}
	public String getDriverCardNo() {
		return driverCardNo;
	}
	public void setDriverCardNo(String driverCardNo) {
		this.driverCardNo = driverCardNo;
	}
	public String getPlateNumber() {
		return plateNumber;
	}
	public void setPlateNumber(String plateNumber) {
		this.plateNumber = plateNumber;
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
	public String getCountersNumber() {
		return countersNumber;
	}
	public void setCountersNumber(String countersNumber) {
		this.countersNumber = countersNumber;
	}
	public String getDeliveryAddr() {
		return deliveryAddr;
	}
	public void setDeliveryAddr(String deliveryAddr) {
		this.deliveryAddr = deliveryAddr;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getContractName() {
		return contractName;
	}
	public void setContractName(String contractName) {
		this.contractName = contractName;
	}
	public String getWarehouseOutType() {
		return warehouseOutType;
	}
	public void setWarehouseOutType(String warehouseOutType) {
		this.warehouseOutType = warehouseOutType;
	}
	
	
}

