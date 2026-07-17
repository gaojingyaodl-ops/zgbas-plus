package com.spt.bas.report.client.vo;

import com.spt.tools.core.bean.PageSearchVo;

public class RptApplyDeliverySearchVo extends PageSearchVo{
	private String businessType;
	private String productCd;
	private String brandNumber;
	private String contractNo;
	private Long enterpriseId;
	private Long applyDeliveryId;
	private String deliveryOutStatus;				//状态
	private String applyNo;
	private String warehouseOutType;
	private String realBuyContractNo;
	private String buyContractNo;
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
	public String getBrandNumber() {
		return brandNumber;
	}
	public void setBrandNumber(String brandNumber) {
		this.brandNumber = brandNumber;
	}
	public String getContractNo() {
		return contractNo;
	}
	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}
	public Long getEnterpriseId() {
		return enterpriseId;
	}
	public void setEnterpriseId(Long enterpriseId) {
		this.enterpriseId = enterpriseId;
	}
	public Long getApplyDeliveryId() {
		return applyDeliveryId;
	}
	public void setApplyDeliveryId(Long applyDeliveryId) {
		this.applyDeliveryId = applyDeliveryId;
	}
	public String getDeliveryOutStatus() {
		return deliveryOutStatus;
	}
	public void setDeliveryOutStatus(String deliveryOutStatus) {
		this.deliveryOutStatus = deliveryOutStatus;
	}
	public String getApplyNo() {
		return applyNo;
	}
	public void setApplyNo(String applyNo) {
		this.applyNo = applyNo;
	}
	public String getWarehouseOutType() {
		return warehouseOutType;
	}
	public void setWarehouseOutType(String warehouseOutType) {
		this.warehouseOutType = warehouseOutType;
	}
	public String getRealBuyContractNo() {
		return realBuyContractNo;
	}
	public void setRealBuyContractNo(String realBuyContractNo) {
		this.realBuyContractNo = realBuyContractNo;
	}
	public String getBuyContractNo() {
		return buyContractNo;
	}
	public void setBuyContractNo(String buyContractNo) {
		this.buyContractNo = buyContractNo;
	}
	
}
