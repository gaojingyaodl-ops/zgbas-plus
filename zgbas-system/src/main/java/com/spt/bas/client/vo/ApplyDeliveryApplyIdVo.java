package com.spt.bas.client.vo;


import com.spt.bas.client.entity.ApplyDelivery;

public class ApplyDeliveryApplyIdVo extends ApplyDelivery{
		
	private static final long serialVersionUID = 2367683108247320867L;
	private Long id;
	private Long  applyId;
	private String applyType;
	private String strDate;
	private String CurNumberCn;
	private String warehouseNo;		//仓库单号
	private String warehouseAddr;//仓库地址
	private String warehousePhone;//交货仓库电话
	private String ourCompanyName;
	private String contractNo;
	public Long getApplyId() {
		return applyId;
	}
	public void setApplyId(Long applyId) {
		this.applyId = applyId;
	}
	public String getApplyType() {
		return applyType;
	}
	public void setApplyType(String applyType) {
		this.applyType = applyType;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getStrDate() {
		return strDate;
	}
	public void setStrDate(String strDate) {
		this.strDate = strDate;
	}
	public String getCurNumberCn() {
		return CurNumberCn;
	}
	public void setCurNumberCn(String curNumberCn) {
		CurNumberCn = curNumberCn;
	}
	public String getWarehouseNo() {
		return warehouseNo;
	}
	public void setWarehouseNo(String warehouseNo) {
		this.warehouseNo = warehouseNo;
	}
	public String getWarehouseAddr() {
		return warehouseAddr;
	}
	public void setWarehouseAddr(String warehouseAddr) {
		this.warehouseAddr = warehouseAddr;
	}
	public String getOurCompanyName() {
		return ourCompanyName;
	}
	public void setOurCompanyName(String ourCompanyName) {
		this.ourCompanyName = ourCompanyName;
	}
	public String getContractNo() {
		return contractNo;
	}
	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}
	public String getWarehousePhone() {
		return warehousePhone;
	}
	public void setWarehousePhone(String warehousePhone) {
		this.warehousePhone = warehousePhone;
	}
		
}
