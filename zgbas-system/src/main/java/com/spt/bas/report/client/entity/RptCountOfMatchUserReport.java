package com.spt.bas.report.client.entity;

import java.math.BigDecimal;

/**
 * <p>
 *
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2021-02-18 13:43
 */
public class RptCountOfMatchUserReport {
    /**
     * 企业数
     */
    private Integer totalCompanyNumber;

    /**
     * 交易笔数
     */
    private Integer totalTradeNumber;

    /**
     * 交易累计总额
     */
    private BigDecimal totalTotalTradeAmount;

    /**
     * 逾期笔数
     */
    private Integer totalOverdueNumber;

    /**
     * 逾期累计金额
     */
    private BigDecimal totalTotalOverdueAmount;

    /**
     * 平均逾期天数
     */
    private BigDecimal aveAverageOverdueDay;

    /**
     * 逾期率
     */
    private BigDecimal aveOverdueRate;

    /**
     * 违约率
     */
    private BigDecimal aveDefaultRate;

    /**
     * 应收罚金数额
     */
    private BigDecimal totalTotalInterestAmount;

    /**
     * 实收罚息数额
     */
    private BigDecimal totalTotalReceiveInterestAmount;

    public Integer getTotalCompanyNumber() {
        return totalCompanyNumber;
    }

    public void setTotalCompanyNumber(Integer totalCompanyNumber) {
        this.totalCompanyNumber = totalCompanyNumber;
    }

    public Integer getTotalTradeNumber() {
        return totalTradeNumber;
    }

    public void setTotalTradeNumber(Integer totalTradeNumber) {
        this.totalTradeNumber = totalTradeNumber;
    }

    public BigDecimal getTotalTotalTradeAmount() {
        return totalTotalTradeAmount;
    }

    public void setTotalTotalTradeAmount(BigDecimal totalTotalTradeAmount) {
        this.totalTotalTradeAmount = totalTotalTradeAmount;
    }

    public Integer getTotalOverdueNumber() {
        return totalOverdueNumber;
    }

    public void setTotalOverdueNumber(Integer totalOverdueNumber) {
        this.totalOverdueNumber = totalOverdueNumber;
    }

    public BigDecimal getTotalTotalOverdueAmount() {
        return totalTotalOverdueAmount;
    }

    public void setTotalTotalOverdueAmount(BigDecimal totalTotalOverdueAmount) {
        this.totalTotalOverdueAmount = totalTotalOverdueAmount;
    }

    public BigDecimal getAveAverageOverdueDay() {
        return aveAverageOverdueDay;
    }

    public void setAveAverageOverdueDay(BigDecimal aveAverageOverdueDay) {
        this.aveAverageOverdueDay = aveAverageOverdueDay;
    }

    public BigDecimal getAveOverdueRate() {
        return aveOverdueRate;
    }

    public void setAveOverdueRate(BigDecimal aveOverdueRate) {
        this.aveOverdueRate = aveOverdueRate;
    }

    public BigDecimal getAveDefaultRate() {
        return aveDefaultRate;
    }

    public void setAveDefaultRate(BigDecimal aveDefaultRate) {
        this.aveDefaultRate = aveDefaultRate;
    }

    public BigDecimal getTotalTotalInterestAmount() {
        return totalTotalInterestAmount;
    }

    public void setTotalTotalInterestAmount(BigDecimal totalTotalInterestAmount) {
        this.totalTotalInterestAmount = totalTotalInterestAmount;
    }

    public BigDecimal getTotalTotalReceiveInterestAmount() {
        return totalTotalReceiveInterestAmount;
    }

    public void setTotalTotalReceiveInterestAmount(BigDecimal totalTotalReceiveInterestAmount) {
        this.totalTotalReceiveInterestAmount = totalTotalReceiveInterestAmount;
    }
}
