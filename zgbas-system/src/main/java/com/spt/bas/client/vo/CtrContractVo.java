package com.spt.bas.client.vo;

import java.math.BigDecimal;
import java.util.Date;

public class CtrContractVo {
	
	private	Long	enterpriseId;		//公司ID
	private String contractNo;			//合同编号
	private	String	productType;		//产品类型		现货N、期货F
	private	BigDecimal	payAmount;		//付款金额
	private	Long	companyId;			//供货商公司ID
	private	String	companyName;		//供货商名称
	private	String	contactName;		//联系人
	private	String	contactPhone;		//联系电话
	private	String	contactAddr;		//联系地址
	private	String	deliveryType;		//提货方式		自提ZT、配送PS
	private String fileId;				//附件Id
	private	String	remark;				//备注
	private	BigDecimal	bondRate;		//付款比例
	private	Date	payTime;			//付款时间
	private String  warehouse;			//仓库
	private String  deliveryAddr;		//配送地址
	private String 	deliveryPhone;		//配送电话
	private	Boolean	transferFlg;		//是否需转货权
	private String payType;				//付款方式
	public Long getEnterpriseId() {
		return enterpriseId;
	}
	public void setEnterpriseId(Long enterpriseId) {
		this.enterpriseId = enterpriseId;
	}
	public String getContractNo() {
		return contractNo;
	}
	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}
	public String getProductType() {
		return productType;
	}
	public void setProductType(String productType) {
		this.productType = productType;
	}
	public BigDecimal getPayAmount() {
		return payAmount;
	}
	public void setPayAmount(BigDecimal payAmount) {
		this.payAmount = payAmount;
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
	public String getDeliveryType() {
		return deliveryType;
	}
	public void setDeliveryType(String deliveryType) {
		this.deliveryType = deliveryType;
	}
	public String getFileId() {
		return fileId;
	}
	public void setFileId(String fileId) {
		this.fileId = fileId;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public BigDecimal getBondRate() {
		return bondRate;
	}
	public void setBondRate(BigDecimal bondRate) {
		this.bondRate = bondRate;
	}
	public Date getPayTime() {
		return payTime;
	}
	public void setPayTime(Date payTime) {
		this.payTime = payTime;
	}
	public String getWarehouse() {
		return warehouse;
	}
	public void setWarehouse(String warehouse) {
		this.warehouse = warehouse;
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
	public Boolean getTransferFlg() {
		return transferFlg;
	}
	public void setTransferFlg(Boolean transferFlg) {
		this.transferFlg = transferFlg;
	}
	public String getPayType() {
		return payType;
	}
	public void setPayType(String payType) {
		this.payType = payType;
	}
	
	

}
