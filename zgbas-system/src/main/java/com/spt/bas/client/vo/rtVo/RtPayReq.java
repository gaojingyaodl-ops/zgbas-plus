package com.spt.bas.client.vo.rtVo;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 融拓推送付款信息Vo
 *
 * @Author: gaojy
 * @create 2022/4/8 16:00
 * @version: 1.0
 * @description:
 */
public class RtPayReq extends RtBaseReq{
    /**
     * 审批编号
     */
    private String applyNo;

    /**
     * 银行名称
     */
    private String bankName;

    /**
     * 银行账号
     */
    private String bankAccount;

    /**
     * 业务类型
     */
    private String businessType;

    /**
     * 客户名称
     */
    private String companyName;

    /**
     * 合同编号
     */
    private String contractNo;

    /**
     * 保证金金额
     */
    private BigDecimal factorAmount;

    /**
     * 我方抬头
     */
    private String ourCompanyName;

    /**
     * 付款金额
     */
    private BigDecimal payAmount;

    /**
     * 付款日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date payDate;

    /**
     * 已付金额
     */
    private BigDecimal payedAmount;

    /**
     * 支付方式
     */
    private String payMode;

    /**
     * 支付类型
     */
    private String payType;

    /**
     * 状态
     */
    private String status;

    /**
     * 票证期限
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date ticketDueTime;

    /**
     * 总金额
     */
    private BigDecimal totalAmount;

    /**
     * 未付款金额
     */
    private BigDecimal unpayedAmount;

    public String getApplyNo() {
        return applyNo;
    }

    public void setApplyNo(String applyNo) {
        this.applyNo = applyNo;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getContractNo() {
        return contractNo;
    }

    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
    }

    public BigDecimal getFactorAmount() {
        return factorAmount;
    }

    public void setFactorAmount(BigDecimal factorAmount) {
        this.factorAmount = factorAmount;
    }

    public String getOurCompanyName() {
        return ourCompanyName;
    }

    public void setOurCompanyName(String ourCompanyName) {
        this.ourCompanyName = ourCompanyName;
    }

    public BigDecimal getPayAmount() {
        return payAmount;
    }

    public void setPayAmount(BigDecimal payAmount) {
        this.payAmount = payAmount;
    }

    public Date getPayDate() {
        return payDate;
    }

    public void setPayDate(Date payDate) {
        this.payDate = payDate;
    }

    public BigDecimal getPayedAmount() {
        return payedAmount;
    }

    public void setPayedAmount(BigDecimal payedAmount) {
        this.payedAmount = payedAmount;
    }

    public String getPayMode() {
        return payMode;
    }

    public void setPayMode(String payMode) {
        this.payMode = payMode;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getTicketDueTime() {
        return ticketDueTime;
    }

    public void setTicketDueTime(Date ticketDueTime) {
        this.ticketDueTime = ticketDueTime;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getUnpayedAmount() {
        return unpayedAmount;
    }

    public void setUnpayedAmount(BigDecimal unpayedAmount) {
        this.unpayedAmount = unpayedAmount;
    }
}
