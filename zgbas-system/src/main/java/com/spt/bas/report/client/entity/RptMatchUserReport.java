package com.spt.bas.report.client.entity;

import java.math.BigDecimal;

/**
 * <p>
 *  业务员统计信息
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2021-02-01 14:29
 */
public class RptMatchUserReport {
    private Long matchUserId;

    private String matchUserName;

    /**
     * 企业数
     */
    private Integer companyNumber;

    /**
     * 交易笔数
     */
    private Integer tradeNumber;

    /**
     * 交易累计总额
     */
    private BigDecimal totalTradeAmount;

    /**
     * 逾期笔数
     */
    private Integer overdueNumber;

    /**
     * 逾期累计金额
     */
    private BigDecimal totalOverdueAmount;

    /**
     * 平均逾期天数
     */
    private BigDecimal averageOverdueDay;

    /**
     * 逾期率
     */
    private BigDecimal overdueRate;

    /**
     * 违约率
     */
    private BigDecimal defaultRate;

    /**
     * 应收罚金数额
     */
    private BigDecimal totalInterestAmount;

    /**
     * 实收罚息数额
     */
    private BigDecimal totalReceiveInterestAmount;

    public Long getMatchUserId() {
        return matchUserId;
    }

    public void setMatchUserId(Long matchUserId) {
        this.matchUserId = matchUserId;
    }

    public String getMatchUserName() {
        return matchUserName;
    }

    public void setMatchUserName(String matchUserName) {
        this.matchUserName = matchUserName;
    }

    public Integer getCompanyNumber() {
        return companyNumber;
    }

    public void setCompanyNumber(Integer companyNumber) {
        this.companyNumber = companyNumber;
    }

    public Integer getTradeNumber() {
        return tradeNumber;
    }

    public void setTradeNumber(Integer tradeNumber) {
        this.tradeNumber = tradeNumber;
    }

    public BigDecimal getTotalTradeAmount() {
        return totalTradeAmount;
    }

    public void setTotalTradeAmount(BigDecimal totalTradeAmount) {
        this.totalTradeAmount = totalTradeAmount;
    }

    public Integer getOverdueNumber() {
        return overdueNumber;
    }

    public void setOverdueNumber(Integer overdueNumber) {
        this.overdueNumber = overdueNumber;
    }

    public BigDecimal getTotalOverdueAmount() {
        return totalOverdueAmount;
    }

    public void setTotalOverdueAmount(BigDecimal totalOverdueAmount) {
        this.totalOverdueAmount = totalOverdueAmount;
    }

    public BigDecimal getAverageOverdueDay() {
        return averageOverdueDay;
    }

    public void setAverageOverdueDay(BigDecimal averageOverdueDay) {
        this.averageOverdueDay = averageOverdueDay;
    }

    public BigDecimal getOverdueRate() {
        return overdueRate;
    }

    public void setOverdueRate(BigDecimal overdueRate) {
        this.overdueRate = overdueRate;
    }

    public BigDecimal getDefaultRate() {
        return defaultRate;
    }

    public void setDefaultRate(BigDecimal defaultRate) {
        this.defaultRate = defaultRate;
    }

    public BigDecimal getTotalInterestAmount() {
        return totalInterestAmount;
    }

    public void setTotalInterestAmount(BigDecimal totalInterestAmount) {
        this.totalInterestAmount = totalInterestAmount;
    }

    public BigDecimal getTotalReceiveInterestAmount() {
        return totalReceiveInterestAmount;
    }

    public void setTotalReceiveInterestAmount(BigDecimal totalReceiveInterestAmount) {
        this.totalReceiveInterestAmount = totalReceiveInterestAmount;
    }
}
