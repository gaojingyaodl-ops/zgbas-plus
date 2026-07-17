package com.spt.bas.report.client.vo;


import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 *
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-11-10 10:11
 */
public class RptCtrServiceContractVo {
    /**
     * 服务合同合同号
     */
    private String serviceContractNo;

    /**
     * 是服务费合同总价
     */
    private BigDecimal totalAmount;

    /**
     * 中光亿云供应链管理有限公司
     */
    private String ourCompanyName;

    /**
     * 服务费合同开始执行的时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private Date contractStartTime;

    /**
     * 合同结束日期（赊销结束时间）
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private Date contractEndTime;

    /**
     * 赊销天数
     */
    private Integer creditCycle;

    /**
     * 销售合同服务费的费率
     */
    private BigDecimal rate;

    /**
     * 服务费已付款金额
     */
    private BigDecimal dealedAmount;

    /**
     * 逾期罚金的费率
     */
    private BigDecimal interestRate;

    /**
     *罚金
     */
    private BigDecimal interestAmount;

    /**
     * 逾期天数
     */
    private Integer interestDays;

    /**
     * 已收罚息
     */
    private BigDecimal receiveInterestAmount;

    /**
     * 服务费合同附件ID
     */
    private String fileId;

    /**
     * 合同状态，执行中、逾期、违约
     */
    private String contractStatus;

    /**
     * 是否完成支付服务费，0未完成，1完成
     */
    private Boolean dealedFlg;

    /**
     * 已开服务费发票金额
     */
    private BigDecimal billedAmount;

    /**
     * 是否完成开服务费发票，0未完成，1完成
     */
    private Boolean billedFlg;

    /**
     * 合同逾期标识，0未逾期，1逾期
     */
    private Boolean orverdurFlg;

    public String getServiceContractNo() {
        return serviceContractNo;
    }

    public void setServiceContractNo(String serviceContractNo) {
        this.serviceContractNo = serviceContractNo;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getOurCompanyName() {
        return ourCompanyName;
    }

    public void setOurCompanyName(String ourCompanyName) {
        this.ourCompanyName = ourCompanyName;
    }

    public Date getContractStartTime() {
        return contractStartTime;
    }

    public void setContractStartTime(Date contractStartTime) {
        this.contractStartTime = contractStartTime;
    }

    public Date getContractEndTime() {
        return contractEndTime;
    }

    public void setContractEndTime(Date contractEndTime) {
        this.contractEndTime = contractEndTime;
    }

    public Integer getCreditCycle() {
        return creditCycle;
    }

    public void setCreditCycle(Integer creditCycle) {
        this.creditCycle = creditCycle;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public BigDecimal getDealedAmount() {
        return dealedAmount;
    }

    public void setDealedAmount(BigDecimal dealedAmount) {
        this.dealedAmount = dealedAmount;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    public BigDecimal getInterestAmount() {
        return interestAmount;
    }

    public void setInterestAmount(BigDecimal interestAmount) {
        this.interestAmount = interestAmount;
    }

    public Integer getInterestDays() {
        return interestDays;
    }

    public void setInterestDays(Integer interestDays) {
        this.interestDays = interestDays;
    }

    public BigDecimal getReceiveInterestAmount() {
        return receiveInterestAmount;
    }

    public void setReceiveInterestAmount(BigDecimal receiveInterestAmount) {
        this.receiveInterestAmount = receiveInterestAmount;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getContractStatus() {
        return contractStatus;
    }

    public void setContractStatus(String contractStatus) {
        this.contractStatus = contractStatus;
    }

    public Boolean getDealedFlg() {
        return dealedFlg;
    }

    public void setDealedFlg(Boolean dealedFlg) {
        this.dealedFlg = dealedFlg;
    }

    public BigDecimal getBilledAmount() {
        return billedAmount;
    }

    public void setBilledAmount(BigDecimal billedAmount) {
        this.billedAmount = billedAmount;
    }

    public Boolean getBilledFlg() {
        return billedFlg;
    }

    public void setBilledFlg(Boolean billedFlg) {
        this.billedFlg = billedFlg;
    }

    public Boolean getOrverdurFlg() {
        return orverdurFlg;
    }

    public void setOrverdurFlg(Boolean orverdurFlg) {
        this.orverdurFlg = orverdurFlg;
    }
}
