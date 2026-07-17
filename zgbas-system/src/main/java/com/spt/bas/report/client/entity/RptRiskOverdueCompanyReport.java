package com.spt.bas.report.client.entity;

import java.math.BigDecimal;

/**
 * <p>
 *  企业统计信息
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2021-02-01 14:29
 */
public class RptRiskOverdueCompanyReport {

    private Long companyId;


    private String companyName;

    //================================================

    /**
     * 事业部id
     */
    private Long serviceDepartmentId;

    /**
     * 事业部名称
     */
    private String serviceDepartmentName;

    /**
     * 业务部id
     */
    private Long businessDepartmentId;

    /**
     * 业务部名称
     */
    private String businessDepartmentName;

    private Long matchUserId;

    private String matchUserName;

    //=========================================


    /**
     * 交易笔数
     */
    private Integer tradeNumber;

    /**
     * 交易吨数（数量）
     */
    private BigDecimal totalTradeNumber;

    /**
     * 累计交易总额
     */
    private BigDecimal totalTradeAmount;

    /**
     * 平均交易金额
     */
    private BigDecimal averageTradeAmount;

    /**
     * 累计交易利润
     */
    private BigDecimal totalMarginAmount;

    // ====================================================

    /**
     * 赊销交易笔数
     */
    private Integer iousNumber;

    /**
     * 赊销交易吨数
     */
    private BigDecimal iousTradeNumber;

    /**
     * 赊销交易累计总额
     */
    private BigDecimal iousTradeAmount;

    /**
     * 赊销平均交易金额
     */
    private BigDecimal iousAverageTradeAmount;

    /**
     * 赊销交易累计利润
     */
    private BigDecimal iousMarginAmount;

    // ====================================================

    /**
     * 平均账期
     */
    private BigDecimal averageCreditCycle;

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
     *  逾期率
     */
    private BigDecimal overdueRate;

    /**
     * 赊销额度（元）
     */
    private BigDecimal totalCreditAmount;

    /**
     * 可用额度（元）
     */
    private BigDecimal availableCreditAmount;

    /**
     * 应收罚金数额
     */
    private BigDecimal totalInterestAmount;

    /**
     * 实收罚息数额
     */
    private BigDecimal totalReceiveInterestAmount;

    /**
     * 违约笔数
     */
    private Integer breachNumber;

    /**
     * 累计逾期罚息金额
     */
    private BigDecimal totalBreachAmount;

    /**
     * 保险理赔金额
     */
    private BigDecimal insuranceClaimsAmount;

    // ====================================================

    /**
     * 代采交易笔数
     */
    private Integer matchNumber;

    /**
     * 代采交易吨数
     */
    private BigDecimal matchTradeNumber;

    /**
     *  代采交易累计总额
     */
    private BigDecimal matchTradeAmount;

    /**
     * 赊销平均交易金额
     */
    private BigDecimal matchAverageTradeAmount;

    /**
     * 代采交易累计利润
     */
    private BigDecimal matchMarginAmount;

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Long getServiceDepartmentId() {
        return serviceDepartmentId;
    }

    public void setServiceDepartmentId(Long serviceDepartmentId) {
        this.serviceDepartmentId = serviceDepartmentId;
    }

    public String getServiceDepartmentName() {
        return serviceDepartmentName;
    }

    public void setServiceDepartmentName(String serviceDepartmentName) {
        this.serviceDepartmentName = serviceDepartmentName;
    }

    public Long getBusinessDepartmentId() {
        return businessDepartmentId;
    }

    public void setBusinessDepartmentId(Long businessDepartmentId) {
        this.businessDepartmentId = businessDepartmentId;
    }

    public String getBusinessDepartmentName() {
        return businessDepartmentName;
    }

    public void setBusinessDepartmentName(String businessDepartmentName) {
        this.businessDepartmentName = businessDepartmentName;
    }

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

    public Integer getTradeNumber() {
        return tradeNumber;
    }

    public void setTradeNumber(Integer tradeNumber) {
        this.tradeNumber = tradeNumber;
    }

    public BigDecimal getTotalTradeNumber() {
        return totalTradeNumber;
    }

    public void setTotalTradeNumber(BigDecimal totalTradeNumber) {
        this.totalTradeNumber = totalTradeNumber;
    }

    public BigDecimal getTotalTradeAmount() {
        return totalTradeAmount;
    }

    public void setTotalTradeAmount(BigDecimal totalTradeAmount) {
        this.totalTradeAmount = totalTradeAmount;
    }

    public BigDecimal getAverageTradeAmount() {
        return averageTradeAmount;
    }

    public void setAverageTradeAmount(BigDecimal averageTradeAmount) {
        this.averageTradeAmount = averageTradeAmount;
    }

    public BigDecimal getTotalMarginAmount() {
        return totalMarginAmount;
    }

    public void setTotalMarginAmount(BigDecimal totalMarginAmount) {
        this.totalMarginAmount = totalMarginAmount;
    }

    public Integer getIousNumber() {
        return iousNumber;
    }

    public void setIousNumber(Integer iousNumber) {
        this.iousNumber = iousNumber;
    }

    public BigDecimal getIousTradeNumber() {
        return iousTradeNumber;
    }

    public void setIousTradeNumber(BigDecimal iousTradeNumber) {
        this.iousTradeNumber = iousTradeNumber;
    }

    public BigDecimal getIousTradeAmount() {
        return iousTradeAmount;
    }

    public void setIousTradeAmount(BigDecimal iousTradeAmount) {
        this.iousTradeAmount = iousTradeAmount;
    }

    public BigDecimal getIousAverageTradeAmount() {
        return iousAverageTradeAmount;
    }

    public void setIousAverageTradeAmount(BigDecimal iousAverageTradeAmount) {
        this.iousAverageTradeAmount = iousAverageTradeAmount;
    }

    public BigDecimal getIousMarginAmount() {
        return iousMarginAmount;
    }

    public void setIousMarginAmount(BigDecimal iousMarginAmount) {
        this.iousMarginAmount = iousMarginAmount;
    }

    public BigDecimal getAverageCreditCycle() {
        return averageCreditCycle;
    }

    public void setAverageCreditCycle(BigDecimal averageCreditCycle) {
        this.averageCreditCycle = averageCreditCycle;
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

    public BigDecimal getTotalCreditAmount() {
        return totalCreditAmount;
    }

    public void setTotalCreditAmount(BigDecimal totalCreditAmount) {
        this.totalCreditAmount = totalCreditAmount;
    }

    public BigDecimal getAvailableCreditAmount() {
        return availableCreditAmount;
    }

    public void setAvailableCreditAmount(BigDecimal availableCreditAmount) {
        this.availableCreditAmount = availableCreditAmount;
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

    public Integer getBreachNumber() {
        return breachNumber;
    }

    public void setBreachNumber(Integer breachNumber) {
        this.breachNumber = breachNumber;
    }

    public BigDecimal getTotalBreachAmount() {
        return totalBreachAmount;
    }

    public void setTotalBreachAmount(BigDecimal totalBreachAmount) {
        this.totalBreachAmount = totalBreachAmount;
    }

    public BigDecimal getInsuranceClaimsAmount() {
        return insuranceClaimsAmount;
    }

    public void setInsuranceClaimsAmount(BigDecimal insuranceClaimsAmount) {
        this.insuranceClaimsAmount = insuranceClaimsAmount;
    }

    public Integer getMatchNumber() {
        return matchNumber;
    }

    public void setMatchNumber(Integer matchNumber) {
        this.matchNumber = matchNumber;
    }

    public BigDecimal getMatchTradeNumber() {
        return matchTradeNumber;
    }

    public void setMatchTradeNumber(BigDecimal matchTradeNumber) {
        this.matchTradeNumber = matchTradeNumber;
    }

    public BigDecimal getMatchTradeAmount() {
        return matchTradeAmount;
    }

    public void setMatchTradeAmount(BigDecimal matchTradeAmount) {
        this.matchTradeAmount = matchTradeAmount;
    }

    public BigDecimal getMatchAverageTradeAmount() {
        return matchAverageTradeAmount;
    }

    public void setMatchAverageTradeAmount(BigDecimal matchAverageTradeAmount) {
        this.matchAverageTradeAmount = matchAverageTradeAmount;
    }

    public BigDecimal getMatchMarginAmount() {
        return matchMarginAmount;
    }

    public void setMatchMarginAmount(BigDecimal matchMarginAmount) {
        this.matchMarginAmount = matchMarginAmount;
    }
}
