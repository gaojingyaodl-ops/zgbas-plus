package com.spt.bas.report.client.entity;

import java.math.BigDecimal;

/**
 * <p>
 *
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2021-02-20 14:07
 */
public class RptSumMarginAmountReport {
    /**
     * 合同数量
     */
    private Integer totalTotalNumber;

    /**
     * 采购合同金额
     */
    private BigDecimal totalBtotalAmount;

    /**
     * 销售合同金额
     */
    private BigDecimal totalStotalAmount;

    /**
     * 利润
     */
    private BigDecimal totalMarginAmount;

    /**
     * 毛利
     */
    private BigDecimal totalCompanyCommissionAmount;

    /**
     * 毛利率，是所有业务的毛利率并非简单的算术平均
     */
    private BigDecimal aveCompanyCommissionRate;

    /**
     * 收款金额
     */
    private BigDecimal totalBdealedAmount;

    /**
     * 付款金额
     */
    private BigDecimal totalSdealedAmount;

    /**
     * 收票金额
     */
    private BigDecimal totalBbilledAmount;

    /**
     * 开票金额
     */
    private BigDecimal totalSbilledAmount;

    /**
     * 应付余额
     */
    private BigDecimal totalBalancePayable;

    /**
     * 应收余额
     */
    private BigDecimal totalBalanceReceivable;

    public Integer getTotalTotalNumber() {
        return totalTotalNumber;
    }

    public void setTotalTotalNumber(Integer totalTotalNumber) {
        this.totalTotalNumber = totalTotalNumber;
    }

    public BigDecimal getTotalBtotalAmount() {
        return totalBtotalAmount;
    }

    public void setTotalBtotalAmount(BigDecimal totalBtotalAmount) {
        this.totalBtotalAmount = totalBtotalAmount;
    }

    public BigDecimal getTotalStotalAmount() {
        return totalStotalAmount;
    }

    public void setTotalStotalAmount(BigDecimal totalStotalAmount) {
        this.totalStotalAmount = totalStotalAmount;
    }

    public BigDecimal getTotalMarginAmount() {
        return totalMarginAmount;
    }

    public void setTotalMarginAmount(BigDecimal totalMarginAmount) {
        this.totalMarginAmount = totalMarginAmount;
    }

    public BigDecimal getTotalCompanyCommissionAmount() {
        return totalCompanyCommissionAmount;
    }

    public void setTotalCompanyCommissionAmount(BigDecimal totalCompanyCommissionAmount) {
        this.totalCompanyCommissionAmount = totalCompanyCommissionAmount;
    }

    public BigDecimal getAveCompanyCommissionRate() {
        return aveCompanyCommissionRate;
    }

    public void setAveCompanyCommissionRate(BigDecimal aveCompanyCommissionRate) {
        this.aveCompanyCommissionRate = aveCompanyCommissionRate;
    }

    public BigDecimal getTotalBdealedAmount() {
        return totalBdealedAmount;
    }

    public void setTotalBdealedAmount(BigDecimal totalBdealedAmount) {
        this.totalBdealedAmount = totalBdealedAmount;
    }

    public BigDecimal getTotalSdealedAmount() {
        return totalSdealedAmount;
    }

    public void setTotalSdealedAmount(BigDecimal totalSdealedAmount) {
        this.totalSdealedAmount = totalSdealedAmount;
    }

    public BigDecimal getTotalBbilledAmount() {
        return totalBbilledAmount;
    }

    public void setTotalBbilledAmount(BigDecimal totalBbilledAmount) {
        this.totalBbilledAmount = totalBbilledAmount;
    }

    public BigDecimal getTotalSbilledAmount() {
        return totalSbilledAmount;
    }

    public void setTotalSbilledAmount(BigDecimal totalSbilledAmount) {
        this.totalSbilledAmount = totalSbilledAmount;
    }

    public BigDecimal getTotalBalancePayable() {
        return totalBalancePayable;
    }

    public void setTotalBalancePayable(BigDecimal totalBalancePayable) {
        this.totalBalancePayable = totalBalancePayable;
    }

    public BigDecimal getTotalBalanceReceivable() {
        return totalBalanceReceivable;
    }

    public void setTotalBalanceReceivable(BigDecimal totalBalanceReceivable) {
        this.totalBalanceReceivable = totalBalanceReceivable;
    }
}
