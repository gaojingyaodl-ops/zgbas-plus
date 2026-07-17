package com.spt.bas.report.client.entity;


import java.math.BigDecimal;

/**
 * <p>
 *
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2021-02-18 09:54
 */
public class RptCountOfRiskOverdueCompany {
    /**
     * 交易笔数
     */
    private Integer totalTradeNumber;

    /**
     * 交易吨数（数量）
     */
    private BigDecimal totalTotalTradeNumber;

    /**
     * "交易累计总额"
     */
    private BigDecimal totalTotalTradeAmount;

    /**
     * 交易累计利润
     */
    private BigDecimal totalTotalMarginAmount;

    /**
     * 平均交易金额
     */
    private BigDecimal aveAverageTradeAmount;

    // ==========================================

    /**
     * 赊销交易笔数
     */
    private Integer totalIousNumber;

    /**
     * 赊销交易吨数
     */
    private BigDecimal totalIousTradeNumber;

    /**
     * 赊销交易累计总额
     */
    private BigDecimal totalIousTradeAmount;

    /**
     * 赊销交易累计利润
     */
    private BigDecimal totalIousMarginAmount;

    /**
     * 赊销平均交易金额
     */
    private BigDecimal aveIousAverageTradeAmount;

    // =============================================

    /**
     * 平均账期
     */
    private BigDecimal aveAverageCreditCycle;

    /**
     * 逾期笔数
     */
    private Integer aveOverdueNumber;

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
     * 应收罚金数额
     */
    private BigDecimal totalTotalInterestAmount;

    /**
     * 实收罚息数额
     */
    private BigDecimal totalTotalReceiveInterestAmount;

    /**
     * 赊销额度（元）
     */
    private BigDecimal totalTotalCreditAmount;

    /**
     * 可用额度（元）
     */
    private BigDecimal availableTotalCreditAmount;

    /**
     * 违约笔数
     */
    private Integer totalBreachNumber;

    /**
     * 累计逾期罚息额
     */
    private BigDecimal totalTotalBreachAmount;

    /**
     * 保险理赔金额
     */
    private BigDecimal totalInsuranceClaimsAmount;

    /**
     * 代采交易笔数
     */
    private Integer totalMatchNumber;

    /**
     * 代采交易吨数
     */
    private BigDecimal totalMatchTradeNumber;

    /**
     * 代采交易累计总额
     */
    private BigDecimal totalMatchTradeAmount;

    /**
     * 代采交易累计利润
     */
    private BigDecimal totalMatchMarginAmount;

    /**
     * 代采平均交易金额
     */
    private BigDecimal aveMatchAverageTradeAmount;

    public Integer getTotalTradeNumber() {
        return totalTradeNumber;
    }

    public void setTotalTradeNumber(Integer totalTradeNumber) {
        this.totalTradeNumber = totalTradeNumber;
    }

    public BigDecimal getTotalTotalTradeNumber() {
        return totalTotalTradeNumber;
    }

    public void setTotalTotalTradeNumber(BigDecimal totalTotalTradeNumber) {
        this.totalTotalTradeNumber = totalTotalTradeNumber;
    }

    public BigDecimal getTotalTotalTradeAmount() {
        return totalTotalTradeAmount;
    }

    public void setTotalTotalTradeAmount(BigDecimal totalTotalTradeAmount) {
        this.totalTotalTradeAmount = totalTotalTradeAmount;
    }

    public BigDecimal getTotalTotalMarginAmount() {
        return totalTotalMarginAmount;
    }

    public void setTotalTotalMarginAmount(BigDecimal totalTotalMarginAmount) {
        this.totalTotalMarginAmount = totalTotalMarginAmount;
    }

    public BigDecimal getAveAverageTradeAmount() {
        return aveAverageTradeAmount;
    }

    public void setAveAverageTradeAmount(BigDecimal aveAverageTradeAmount) {
        this.aveAverageTradeAmount = aveAverageTradeAmount;
    }

    public Integer getTotalIousNumber() {
        return totalIousNumber;
    }

    public void setTotalIousNumber(Integer totalIousNumber) {
        this.totalIousNumber = totalIousNumber;
    }

    public BigDecimal getTotalIousTradeNumber() {
        return totalIousTradeNumber;
    }

    public void setTotalIousTradeNumber(BigDecimal totalIousTradeNumber) {
        this.totalIousTradeNumber = totalIousTradeNumber;
    }

    public BigDecimal getTotalIousTradeAmount() {
        return totalIousTradeAmount;
    }

    public void setTotalIousTradeAmount(BigDecimal totalIousTradeAmount) {
        this.totalIousTradeAmount = totalIousTradeAmount;
    }

    public BigDecimal getTotalIousMarginAmount() {
        return totalIousMarginAmount;
    }

    public void setTotalIousMarginAmount(BigDecimal totalIousMarginAmount) {
        this.totalIousMarginAmount = totalIousMarginAmount;
    }

    public BigDecimal getAveIousAverageTradeAmount() {
        return aveIousAverageTradeAmount;
    }

    public void setAveIousAverageTradeAmount(BigDecimal aveIousAverageTradeAmount) {
        this.aveIousAverageTradeAmount = aveIousAverageTradeAmount;
    }

    public BigDecimal getAveAverageCreditCycle() {
        return aveAverageCreditCycle;
    }

    public void setAveAverageCreditCycle(BigDecimal aveAverageCreditCycle) {
        this.aveAverageCreditCycle = aveAverageCreditCycle;
    }

    public Integer getAveOverdueNumber() {
        return aveOverdueNumber;
    }

    public void setAveOverdueNumber(Integer aveOverdueNumber) {
        this.aveOverdueNumber = aveOverdueNumber;
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

    public BigDecimal getTotalTotalCreditAmount() {
        return totalTotalCreditAmount;
    }

    public void setTotalTotalCreditAmount(BigDecimal totalTotalCreditAmount) {
        this.totalTotalCreditAmount = totalTotalCreditAmount;
    }

    public BigDecimal getAvailableTotalCreditAmount() {
        return availableTotalCreditAmount;
    }

    public void setAvailableTotalCreditAmount(BigDecimal availableTotalCreditAmount) {
        this.availableTotalCreditAmount = availableTotalCreditAmount;
    }

    public Integer getTotalBreachNumber() {
        return totalBreachNumber;
    }

    public void setTotalBreachNumber(Integer totalBreachNumber) {
        this.totalBreachNumber = totalBreachNumber;
    }

    public BigDecimal getTotalTotalBreachAmount() {
        return totalTotalBreachAmount;
    }

    public void setTotalTotalBreachAmount(BigDecimal totalTotalBreachAmount) {
        this.totalTotalBreachAmount = totalTotalBreachAmount;
    }

    public BigDecimal getTotalInsuranceClaimsAmount() {
        return totalInsuranceClaimsAmount;
    }

    public void setTotalInsuranceClaimsAmount(BigDecimal totalInsuranceClaimsAmount) {
        this.totalInsuranceClaimsAmount = totalInsuranceClaimsAmount;
    }

    public Integer getTotalMatchNumber() {
        return totalMatchNumber;
    }

    public void setTotalMatchNumber(Integer totalMatchNumber) {
        this.totalMatchNumber = totalMatchNumber;
    }

    public BigDecimal getTotalMatchTradeNumber() {
        return totalMatchTradeNumber;
    }

    public void setTotalMatchTradeNumber(BigDecimal totalMatchTradeNumber) {
        this.totalMatchTradeNumber = totalMatchTradeNumber;
    }

    public BigDecimal getTotalMatchTradeAmount() {
        return totalMatchTradeAmount;
    }

    public void setTotalMatchTradeAmount(BigDecimal totalMatchTradeAmount) {
        this.totalMatchTradeAmount = totalMatchTradeAmount;
    }

    public BigDecimal getTotalMatchMarginAmount() {
        return totalMatchMarginAmount;
    }

    public void setTotalMatchMarginAmount(BigDecimal totalMatchMarginAmount) {
        this.totalMatchMarginAmount = totalMatchMarginAmount;
    }

    public BigDecimal getAveMatchAverageTradeAmount() {
        return aveMatchAverageTradeAmount;
    }

    public void setAveMatchAverageTradeAmount(BigDecimal aveMatchAverageTradeAmount) {
        this.aveMatchAverageTradeAmount = aveMatchAverageTradeAmount;
    }
}
