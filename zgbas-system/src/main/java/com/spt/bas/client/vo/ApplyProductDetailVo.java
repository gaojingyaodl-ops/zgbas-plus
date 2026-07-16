package com.spt.bas.client.vo;

import com.spt.bas.client.entity.ApplyProductDetail;

import java.math.BigDecimal;

public class ApplyProductDetailVo extends ApplyProductDetail{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private String contractNo;
	private String companyName;
	private BigDecimal buyDealNumber;
	private BigDecimal buyDealPrice;
	private BigDecimal buyTotalPrice;
	private Long buyCtrContractId;

	private Long oldStockDetailId;
	private String applyMatcher;
	/**
	 * 质量标准   Y-原厂标准 G-过渡料 F-副牌料
	 */
	private String qualityStandard;

	/**
	 * 物流费用
	 */
	private BigDecimal logisticsCosts;

	/**
	 * 出库费用 元/吨
	 */
	private BigDecimal deliveryOutFee;
	/**
	 * 运输费
	 */
	private BigDecimal transportAmount;
	/**
	 * 仓储费
	 */
	private BigDecimal warehouseAmount;

	/**
	 * 仓库/配送地址
	 */
	private String deliveryAddr;
	/**
	 * 车牌号
	 */
	private String plateNumber;
	/**
	 * 联系人
	 */
	private String contactName;
	/**
	 * 联系电话
	 */
	private String contactPhone;
	/**
	 * 司机
	 */
	private String driverName;
	/**
	 * 司机电话
	 */
	private String driverPhone;
	/**
	 * 驾驶员身份证号
	 */
	private String driverCardNo;
	/**
	 * 备注
	 */
	private String remark;

	/**
	 * 出库id
	 */
	private Long deliveryOutId;


	/**
	 * 装卸费
	 */
	private  BigDecimal  stevedorage;

	/**
	 * 其他费用
	 */
	private BigDecimal otherFee;

	/**
	 * 承运商
	 */
	private String carrier;

	public BigDecimal getStevedorage() {
		return stevedorage;
	}

	public void setStevedorage(BigDecimal stevedorage) {
		this.stevedorage = stevedorage;
	}

	public String getContractNo() {
		return contractNo;
	}
	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public BigDecimal getBuyDealNumber() {
		return buyDealNumber;
	}
	public void setBuyDealNumber(BigDecimal buyDealNumber) {
		this.buyDealNumber = buyDealNumber;
	}
	public BigDecimal getBuyDealPrice() {
		return buyDealPrice;
	}
	public void setBuyDealPrice(BigDecimal buyDealPrice) {
		this.buyDealPrice = buyDealPrice;
	}
	public BigDecimal getBuyTotalPrice() {
		return buyTotalPrice;
	}
	public void setBuyTotalPrice(BigDecimal buyTotalPrice) {
		this.buyTotalPrice = buyTotalPrice;
	}
	public Long getBuyCtrContractId() {
		return buyCtrContractId;
	}
	public void setBuyCtrContractId(Long buyCtrContractId) {
		this.buyCtrContractId = buyCtrContractId;
	}
	public Long getOldStockDetailId() {
		return oldStockDetailId;
	}
	public void setOldStockDetailId(Long oldStockDetailId) {
		this.oldStockDetailId = oldStockDetailId;
	}

	public String getApplyMatcher() {
		return applyMatcher;
	}

	public void setApplyMatcher(String applyMatcher) {
		this.applyMatcher = applyMatcher;
	}

	public String getQualityStandard() {
		return qualityStandard;
	}

	public void setQualityStandard(String qualityStandard) {
		this.qualityStandard = qualityStandard;
	}

	public BigDecimal getLogisticsCosts() {
		return logisticsCosts;
	}

	public void setLogisticsCosts(BigDecimal logisticsCosts) {
		this.logisticsCosts = logisticsCosts;
	}

	public BigDecimal getDeliveryOutFee() {
		return deliveryOutFee;
	}

	public void setDeliveryOutFee(BigDecimal deliveryOutFee) {
		this.deliveryOutFee = deliveryOutFee;
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

	public String getDeliveryAddr() {
		return deliveryAddr;
	}

	public void setDeliveryAddr(String deliveryAddr) {
		this.deliveryAddr = deliveryAddr;
	}

	public String getPlateNumber() {
		return plateNumber;
	}

	public void setPlateNumber(String plateNumber) {
		this.plateNumber = plateNumber;
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

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Long getDeliveryOutId() {
		return deliveryOutId;
	}

	public void setDeliveryOutId(Long deliveryOutId) {
		this.deliveryOutId = deliveryOutId;
	}

	public BigDecimal getOtherFee() {
		return otherFee;
	}

	public void setOtherFee(BigDecimal otherFee) {
		this.otherFee = otherFee;
	}

	public String getCarrier() {
		return carrier;
	}

	public void setCarrier(String carrier) {
		this.carrier = carrier;
	}
}
