package com.spt.bas.client.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spt.tools.core.date.DateOperator;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

/**
 * @Author MoonLight
 * @Date 2023/10/18 11:03
 * @Version 1.0
 */
public class OverdueInterestVo {
    /**
     * 明细金额
     */
    private BigDecimal targetAmount;

    /**
     * 付款金额
     */
    private BigDecimal payAmount = BigDecimal.ZERO;

    /**
     * 付款日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date payDate;

    /**
     * 收款金额
     */
    private BigDecimal receiveAmount = BigDecimal.ZERO;
    /**
     * 收款日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date receiveDate;

    /**
     * 账期天数
     */
    private Long compareDays = 0L;

    /**
     * 资金成本
     */
    private BigDecimal overdueInterestAmount = BigDecimal.ZERO;

    public OverdueInterestVo() {
    }

    public OverdueInterestVo(BigDecimal payAmount, Date payDate) {
        this.targetAmount = payAmount;
        this.payAmount = payAmount;
        this.payDate = payDate;
        this.receiveDate = new Date();
    }

    public OverdueInterestVo(BigDecimal targetAmount, Date payDate, Date receiveDate) {
        this.targetAmount = targetAmount;
        this.payAmount = targetAmount;
        this.receiveAmount = targetAmount;
        this.payDate = payDate;
        this.receiveDate = receiveDate;
    }

    public OverdueInterestVo(BigDecimal payAmount, Date payDate, BigDecimal receiveAmount, Date receiveDate) {
        this.targetAmount = payAmount;
        this.payAmount = targetAmount;
        this.receiveAmount = receiveAmount;
        this.payDate = payDate;
        this.receiveDate = receiveDate;
    }

    public BigDecimal getTargetAmount() {
        return targetAmount;
    }

    public void setTargetAmount(BigDecimal targetAmount) {
        this.targetAmount = targetAmount;
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

    public BigDecimal getReceiveAmount() {
        return receiveAmount;
    }

    public void setReceiveAmount(BigDecimal receiveAmount) {
        this.receiveAmount = receiveAmount;
    }

    public Date getReceiveDate() {
        return receiveDate;
    }

    public void setReceiveDate(Date receiveDate) {
        this.receiveDate = receiveDate;
    }

    public Long getCompareDays() {
        return compareDays;
    }

    public void setCompareDays(Long compareDays) {
        this.compareDays = compareDays;
    }

    public BigDecimal getOverdueInterestAmount() {
        return overdueInterestAmount;
    }

    public void setOverdueInterestAmount(BigDecimal overdueInterestAmount) {
        this.overdueInterestAmount = overdueInterestAmount;
    }

    public boolean verifyAfterAprilFlg(){
        return Objects.nonNull(payDate) && payDate.after(DateOperator.parse("2024-04-01"));
    }
}
