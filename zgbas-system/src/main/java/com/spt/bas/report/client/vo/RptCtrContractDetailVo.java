package com.spt.bas.report.client.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-11-09 18:37
 */
public class RptCtrContractDetailVo {
    /**
     * 1:白条（一票制）
     * 2:白条（两票制）
     * 3:白条
     * 4:代采
     * 0:所有
     */
    private String serviceType;

    /**
     * 合同编号
     */
    private String contractNo;

    /**
     * 品名列表
     */
    private List<RptCtrProductDetailVo> products;

    /**
     * Z：自提；P：配送
     */
    private String deliveryType;

    /**
     * 仓库地址信息
     */
    private String deliveryAddr;

    /**
     * 仓库地址id
     */
    private Long warehouseId;

    /**
     * 合同总价
     */
    private BigDecimal totalAmount;

    /**
     * 合同数量
     */
    private BigDecimal totalNumber;

    /**
     * 合同订立时间，若有服务合同，与contractStartTime时间相同
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private Date contractTime;

    /**
     * 我方公司抬头
     */
    private String ourCompanyName;

    /**
     * 发货日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private Date deliveryDateFrom;

    /**
     * 收货日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private Date deliveryDateTo;

    /**
     * 合同结束日期（赊销结束时间）
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private Date contractEndTime;

    /**
     * 赊销天数
     */
    private Long creditCycle;

    /**
     * 合同id
     */
    @JsonIgnore
    private Long ctrContractId;

    private String contractStatus;

    private Boolean sealFlg;
    /**
     * 是否完成出库（发货），0未发货，1发货
     */
    private Boolean warehouseFlg;
    /**
     * 是否完成支付货款，0未完成，1完成
     */
    private Boolean dealedFlg;

    /**
     * 是否完成支付服务费，0未完成，1完成
     */
    private Boolean interestDealFlg;
    /**
     * 是否完成开货款发票，0未完成，1完成
     */
    private Boolean billedFlg;
    /**
     * 是否完成开服务费发票，0未完成，1完成
     */
    private Boolean interestBilledFlg;
    /**
     * 是否完成确认收货，0未完成，1完成
     */
    private Boolean confirmReceiptFlg;

    /**
     * 合同逾期标识，0未逾期，1逾期
     */
    private Boolean orverdurFlg;

    // =====
    /**
     * 发货地
     */
    private String deliveryAddrSell;

    /**
     * 实际已入\出库金额
     */
    private BigDecimal warehouseAmount;

    /**
     * 实际已入\出库数量，实际发货的数量
     */
    private BigDecimal warehouseNumber;

    /**
     * 合同实际结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private Date contractRealEndTime;

    /**
     * 已付款金额
     */
    private BigDecimal dealedAmount;

    /**
     * 已付款的数量
     */
    private BigDecimal dealedNumber;

    /**
     * 确认收货的金额
     */
    private BigDecimal confirmReceiptAmount;

    /**
     * 确认收货的数量
     */
    private BigDecimal confirmReceiptNumber;

    /**
     * 已开票已收票金额
     */
    private BigDecimal billedAmount;

    /**
     * 已开票已收票数量
     */
    private BigDecimal billedNumber;

    /**
     * 合同附件ID
     */
    private String fileId;
    /**
     * 收开票附件ID
     */
    private String invoiceFileId;
    /**
     * 收付款附件ID
     */
    private String amountFileId;
    /**
     * 出入库附件ID
     */
    private String warehouseFileId;
    /**
     * 确认收货附件ID
     */
    private String confirmReceiptFileId;

    /**
     * 收付全款日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private Date payFullTime;

    /**
     * 逾期罚息
     */
    private BigDecimal interestAmount;

    /**
     * 逾期天数
     */
    private Long interestDays;

    /**
     * 已收逾期罚息
     */
    private BigDecimal receiveInterestAmount;

    public Date getPayFullTime() {
        return payFullTime;
    }

    public void setPayFullTime(Date payFullTime) {
        this.payFullTime = payFullTime;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getContractNo() {
        return contractNo;
    }

    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
    }

    public List<RptCtrProductDetailVo> getProducts() {
        return products;
    }

    public void setProducts(List<RptCtrProductDetailVo> products) {
        this.products = products;
    }

    public String getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(String deliveryType) {
        this.deliveryType = deliveryType;
    }

    public String getDeliveryAddr() {
        return deliveryAddr;
    }

    public void setDeliveryAddr(String deliveryAddr) {
        this.deliveryAddr = deliveryAddr;
    }

    public Long getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Long warehouseId) {
        this.warehouseId = warehouseId;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getTotalNumber() {
        return totalNumber;
    }

    public void setTotalNumber(BigDecimal totalNumber) {
        this.totalNumber = totalNumber;
    }

    public Date getContractTime() {
        return contractTime;
    }

    public void setContractTime(Date contractTime) {
        this.contractTime = contractTime;
    }

    public String getOurCompanyName() {
        return ourCompanyName;
    }

    public void setOurCompanyName(String ourCompanyName) {
        this.ourCompanyName = ourCompanyName;
    }

    public Date getDeliveryDateFrom() {
        return deliveryDateFrom;
    }

    public void setDeliveryDateFrom(Date deliveryDateFrom) {
        this.deliveryDateFrom = deliveryDateFrom;
    }

    public Date getDeliveryDateTo() {
        return deliveryDateTo;
    }

    public void setDeliveryDateTo(Date deliveryDateTo) {
        this.deliveryDateTo = deliveryDateTo;
    }

    public Date getContractEndTime() {
        return contractEndTime;
    }

    public void setContractEndTime(Date contractEndTime) {
        this.contractEndTime = contractEndTime;
    }

    public Long getCreditCycle() {
        return creditCycle;
    }

    public void setCreditCycle(Long creditCycle) {
        this.creditCycle = creditCycle;
    }

    public Long getCtrContractId() {
        return ctrContractId;
    }

    public void setCtrContractId(Long ctrContractId) {
        this.ctrContractId = ctrContractId;
    }

    public String getContractStatus() {
        return contractStatus;
    }

    public void setContractStatus(String contractStatus) {
        this.contractStatus = contractStatus;
    }

    public Boolean getSealFlg() {
        return sealFlg;
    }

    public void setSealFlg(Boolean sealFlg) {
        this.sealFlg = sealFlg;
    }

    public Boolean getWarehouseFlg() {
        return warehouseFlg;
    }

    public void setWarehouseFlg(Boolean warehouseFlg) {
        this.warehouseFlg = warehouseFlg;
    }

    public Boolean getDealedFlg() {
        return dealedFlg;
    }

    public void setDealedFlg(Boolean dealedFlg) {
        this.dealedFlg = dealedFlg;
    }

    public Boolean getInterestDealFlg() {
        return interestDealFlg;
    }

    public void setInterestDealFlg(Boolean interestDealFlg) {
        this.interestDealFlg = interestDealFlg;
    }

    public Boolean getBilledFlg() {
        return billedFlg;
    }

    public void setBilledFlg(Boolean billedFlg) {
        this.billedFlg = billedFlg;
    }

    public Boolean getInterestBilledFlg() {
        return interestBilledFlg;
    }

    public void setInterestBilledFlg(Boolean interestBilledFlg) {
        this.interestBilledFlg = interestBilledFlg;
    }

    public Boolean getConfirmReceiptFlg() {
        return confirmReceiptFlg;
    }

    public void setConfirmReceiptFlg(Boolean confirmReceiptFlg) {
        this.confirmReceiptFlg = confirmReceiptFlg;
    }

    public Boolean getOrverdurFlg() {
        return orverdurFlg;
    }

    public void setOrverdurFlg(Boolean orverdurFlg) {
        this.orverdurFlg = orverdurFlg;
    }

    public String getDeliveryAddrSell() {
        return deliveryAddrSell;
    }

    public void setDeliveryAddrSell(String deliveryAddrSell) {
        this.deliveryAddrSell = deliveryAddrSell;
    }

    public BigDecimal getWarehouseAmount() {
        return warehouseAmount;
    }

    public void setWarehouseAmount(BigDecimal warehouseAmount) {
        this.warehouseAmount = warehouseAmount;
    }

    public BigDecimal getWarehouseNumber() {
        return warehouseNumber;
    }

    public void setWarehouseNumber(BigDecimal warehouseNumber) {
        this.warehouseNumber = warehouseNumber;
    }

    public Date getContractRealEndTime() {
        return contractRealEndTime;
    }

    public void setContractRealEndTime(Date contractRealEndTime) {
        this.contractRealEndTime = contractRealEndTime;
    }

    public BigDecimal getDealedAmount() {
        return dealedAmount;
    }

    public void setDealedAmount(BigDecimal dealedAmount) {
        this.dealedAmount = dealedAmount;
    }

    public BigDecimal getDealedNumber() {
        return dealedNumber;
    }

    public void setDealedNumber(BigDecimal dealedNumber) {
        this.dealedNumber = dealedNumber;
    }

    public BigDecimal getConfirmReceiptAmount() {
        return confirmReceiptAmount;
    }

    public void setConfirmReceiptAmount(BigDecimal confirmReceiptAmount) {
        this.confirmReceiptAmount = confirmReceiptAmount;
    }

    public BigDecimal getConfirmReceiptNumber() {
        return confirmReceiptNumber;
    }

    public void setConfirmReceiptNumber(BigDecimal confirmReceiptNumber) {
        this.confirmReceiptNumber = confirmReceiptNumber;
    }

    public BigDecimal getBilledAmount() {
        return billedAmount;
    }

    public void setBilledAmount(BigDecimal billedAmount) {
        this.billedAmount = billedAmount;
    }

    public BigDecimal getBilledNumber() {
        return billedNumber;
    }

    public void setBilledNumber(BigDecimal billedNumber) {
        this.billedNumber = billedNumber;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getInvoiceFileId() {
        return invoiceFileId;
    }

    public void setInvoiceFileId(String invoiceFileId) {
        this.invoiceFileId = invoiceFileId;
    }

    public String getAmountFileId() {
        return amountFileId;
    }

    public void setAmountFileId(String amountFileId) {
        this.amountFileId = amountFileId;
    }

    public String getWarehouseFileId() {
        return warehouseFileId;
    }

    public void setWarehouseFileId(String warehouseFileId) {
        this.warehouseFileId = warehouseFileId;
    }

    public String getConfirmReceiptFileId() {
        return confirmReceiptFileId;
    }

    public void setConfirmReceiptFileId(String confirmReceiptFileId) {
        this.confirmReceiptFileId = confirmReceiptFileId;
    }

    public BigDecimal getInterestAmount() {
        return interestAmount;
    }

    public void setInterestAmount(BigDecimal interestAmount) {
        this.interestAmount = interestAmount;
    }

    public Long getInterestDays() {
        return interestDays;
    }

    public void setInterestDays(Long interestDays) {
        this.interestDays = interestDays;
    }

    public BigDecimal getReceiveInterestAmount() {
        return receiveInterestAmount;
    }

    public void setReceiveInterestAmount(BigDecimal receiveInterestAmount) {
        this.receiveInterestAmount = receiveInterestAmount;
    }
}
