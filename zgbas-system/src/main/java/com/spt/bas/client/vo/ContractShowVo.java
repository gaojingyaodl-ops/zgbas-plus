package com.spt.bas.client.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spt.bas.client.entity.CtrContract;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.Objects;

public class ContractShowVo extends CtrContract{
	private static final long serialVersionUID = 1L;
	private Boolean canStartBuy = false;
	private String ReplyStatus;//回复状态
	private String contractTimeStr;//合同时间
	private BigDecimal orverdurAmount;//逾期金额
	private String deptName;//事业部
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date lastPayDate;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date lastDeliveryDate;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date lastBillDate;
	private String piccPushFlgStr;
	private String piccReceiveFlgStr;

	private Boolean existServiceText = false;
	private Boolean existServiceTextFileId = false;
	private String serviceFileUrl;

	private Boolean existContractText;
	private Boolean existContractTextFileId = false;
	private String contractFileUrl;

	/**
	 * 补充协议附件url
	 */
	private String protocolFileUrl;

	private Integer contractDifTime;
	private Long pairId;

	private BigDecimal deliveryOutFee;
	
	private Boolean existWarehouse = false;// 存在入库
	
	private Boolean allWarehouse = false;// 全部出库

	private String violateFlg;//标识

	/**
	 * 应收本金
	 */
	private BigDecimal receivablePrincipal;

	private BigDecimal receivableBalance;// 应收余额

	/**
	 * 出入库数量明细
	 */
	private String deliveryNumStr;

	/**
	 * 出入库日期明细
	 */
	private String deliveryDateStr;

	/**
	 * 保费金额
	 */
	private BigDecimal insuranceAmount;

	/**
	 * 是否签连带
	 */
	private String liabilityFlg;

	/**
	 * 是否访厂
	 */
	private String accessReportFlg;

	/**
	 * 已提货款
	 */
	private BigDecimal usedDeliveryAmount;

	/**
	 * 剩余货款
	 */
	private BigDecimal remainingDeliveryAmount;

	private Boolean virtualBillFlag = false;
	
	/**
	 * 发货单url
	 */
	private String shippingFileUrl;

	public BigDecimal getUsedDeliveryAmount() {
		return usedDeliveryAmount;
	}

	public void setUsedDeliveryAmount(BigDecimal usedDeliveryAmount) {
		this.usedDeliveryAmount = usedDeliveryAmount;
	}

	public BigDecimal getRemainingDeliveryAmount() {
		return remainingDeliveryAmount;
	}

	public void setRemainingDeliveryAmount(BigDecimal remainingDeliveryAmount) {
		this.remainingDeliveryAmount = remainingDeliveryAmount;
	}

	public String getViolateFlg() {
		return violateFlg;
	}

	public void setViolateFlg(String violateFlg) {
		this.violateFlg = violateFlg;
	}

	public BigDecimal getReceivableBalance() {
		return receivableBalance;
	}

	public void setReceivableBalance(BigDecimal receivableBalance) {
		this.receivableBalance = receivableBalance;
	}

	/**
	 * 实际确认收货日期
	 */
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
	private Date realConfirmReceiptDate;

	public Date getRealConfirmReceiptDate() {
		return realConfirmReceiptDate;
	}

	public void setRealConfirmReceiptDate(Date realConfirmReceiptDate) {
		this.realConfirmReceiptDate = realConfirmReceiptDate;
	}

	public Boolean getExistWarehouse() {
		return existWarehouse;
	}

	public void setExistWarehouse(Boolean existWarehouse) {
		this.existWarehouse = existWarehouse;
	}

	public Boolean getAllWarehouse() {
		return allWarehouse;
	}

	public void setAllWarehouse(Boolean allWarehouse) {
		this.allWarehouse = allWarehouse;
	}

	public BigDecimal getDeliveryOutFee() {
		return deliveryOutFee;
	}

	public void setDeliveryOutFee(BigDecimal deliveryOutFee) {
		this.deliveryOutFee = deliveryOutFee;
	}

	public Boolean getCanStartBuy() {
		return canStartBuy;
	}

	public void setCanStartBuy(Boolean canStartBuy) {
		this.canStartBuy = canStartBuy;
	}

	public String getReplyStatus() {
		return ReplyStatus;
	}

	public void setReplyStatus(String replyStatus) {
		ReplyStatus = replyStatus;
	}

	public String getContractTimeStr() {
		return contractTimeStr;
	}

	public void setContractTimeStr(String contractTimeStr) {
		this.contractTimeStr = contractTimeStr;
	}

	public Boolean getExistContractText() {
		return existContractText;
	}

	public void setExistContractText(Boolean existContractText) {
		this.existContractText = existContractText;
	}
	public BigDecimal getOrverdurAmount() {
		return orverdurAmount;
	}

	public void setOrverdurAmount(BigDecimal orverdurAmount) {
		this.orverdurAmount = orverdurAmount;
	}

	public String getDeptName() {
		return deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}

	public Date getLastPayDate() {
		return lastPayDate;
	}

	public void setLastPayDate(Date lastPayDate) {
		this.lastPayDate = lastPayDate;
	}

	public Date getLastDeliveryDate() {
		return lastDeliveryDate;
	}

	public void setLastDeliveryDate(Date lastDeliveryDate) {
		this.lastDeliveryDate = lastDeliveryDate;
	}

	public Date getLastBillDate() {
		return lastBillDate;
	}

	public void setLastBillDate(Date lastBillDate) {
		this.lastBillDate = lastBillDate;
	}

	public Integer getContractDifTime() {
		return contractDifTime;
	}

	public void setContractDifTime(Integer contractDifTime) {
		this.contractDifTime = contractDifTime;
	}

	public String getPiccPushFlgStr() {
		return piccPushFlgStr;
	}

	public void setPiccPushFlgStr(String piccPushFlgStr) {
		this.piccPushFlgStr = piccPushFlgStr;
	}

	public String getPiccReceiveFlgStr() {
		return piccReceiveFlgStr;
	}

	public void setPiccReceiveFlgStr(String piccReceiveFlgStr) {
		this.piccReceiveFlgStr = piccReceiveFlgStr;
	}

	public Long getPairId() {
		return pairId;
	}

	public void setPairId(Long pairId) {
		this.pairId = pairId;
	}

	public Boolean getExistServiceText() {
		return existServiceText;
	}

	public void setExistServiceText(Boolean existServiceText) {
		this.existServiceText = existServiceText;
	}

	public Boolean getExistServiceTextFileId() {
		return existServiceTextFileId;
	}

	public void setExistServiceTextFileId(Boolean existServiceTextFileId) {
		this.existServiceTextFileId = existServiceTextFileId;
	}

	public String getServiceFileUrl() {
		return serviceFileUrl;
	}

	public void setServiceFileUrl(String serviceFileUrl) {
		this.serviceFileUrl = serviceFileUrl;
	}

	public Boolean getExistContractTextFileId() {
		return existContractTextFileId;
	}

	public void setExistContractTextFileId(Boolean existContractTextFileId) {
		this.existContractTextFileId = existContractTextFileId;
	}

	public String getContractFileUrl() {
		return contractFileUrl;
	}

	public void setContractFileUrl(String contractFileUrl) {
		this.contractFileUrl = contractFileUrl;
	}

	public String getProtocolFileUrl() {
		return protocolFileUrl;
	}

	public void setProtocolFileUrl(String protocolFileUrl) {
		this.protocolFileUrl = protocolFileUrl;
	}

	public String getDeliveryNumStr() {
		return deliveryNumStr;
	}

	public void setDeliveryNumStr(String deliveryNumStr) {
		this.deliveryNumStr = deliveryNumStr;
	}

	public String getDeliveryDateStr() {
		return deliveryDateStr;
	}

	public void setDeliveryDateStr(String deliveryDateStr) {
		this.deliveryDateStr = deliveryDateStr;
	}

	public BigDecimal getInsuranceAmount() {
		return this.insuranceAmount;
	}

	public void setInsuranceAmount(BigDecimal insuranceAmount) {
		this.insuranceAmount = insuranceAmount;
	}

	public BigDecimal parseInsuranceAmount(){
//		BigDecimal insuranceRate = Objects.nonNull(this.getInsuranceRate()) ? this.getInsuranceRate() : BigDecimal.ZERO;
//		return this.getTotalAmount().multiply(insuranceRate).setScale(2, RoundingMode.HALF_UP);
		return this.insuranceAmount;
	}


	public String getLiabilityFlg() {
		return liabilityFlg;
	}

	public void setLiabilityFlg(String liabilityFlg) {
		this.liabilityFlg = liabilityFlg;
	}

	public String getAccessReportFlg() {
		return accessReportFlg;
	}

	public void setAccessReportFlg(String accessReportFlg) {
		this.accessReportFlg = accessReportFlg;
	}

	public BigDecimal getReceivablePrincipal() {
		return receivablePrincipal;
	}

	public void setReceivablePrincipal(BigDecimal receivablePrincipal) {
		this.receivablePrincipal = receivablePrincipal;
	}

	public Boolean getVirtualBillFlag() {
		return virtualBillFlag;
	}

	public void setVirtualBillFlag(Boolean virtualBillFlag) {
		this.virtualBillFlag = virtualBillFlag;
	}
	
	public String getShippingFileUrl() {
		return shippingFileUrl;
	}

	public void setShippingFileUrl(String shippingFileUrl) {
		this.shippingFileUrl = shippingFileUrl;
	}
}
