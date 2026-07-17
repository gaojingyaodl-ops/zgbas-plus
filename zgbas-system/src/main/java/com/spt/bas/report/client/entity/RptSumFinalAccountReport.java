package com.spt.bas.report.client.entity;

import java.math.BigDecimal;

/**
 * <p>
 *
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2021-02-20 14:15
 */
public class RptSumFinalAccountReport {

    /**
     * 合同数量
     */
    private Integer totalTotalNumber;

    /**
     * 采购单价
     */
    private BigDecimal aveBdealPrice;

    /**
     * 销售单价
     */
    private BigDecimal aveSdealPrice;

    /**
     * 采购合同金额
     */
    private BigDecimal totalBtotalAmount;

    /**
     * 销售合同金额
     */
    private BigDecimal totalStotalAmount;

    /**
     * 回款周期(天)
     */
    private BigDecimal aveCreditDays;

    /**
     * 加价*吨数.差价收入(元)
     */
    private BigDecimal totalPremiumAmount;

    /**
     * 资金服务费(元)
     */
    private BigDecimal totalServiceAmount;

    /**
     * 增值税(元)
     */
    private BigDecimal totalValueAddedTax;

    /**
     * 服务附加税(元)
     */
    private BigDecimal totalServiceSurtax;

    /**
     * 销售附加税(元)
     */
    private BigDecimal totalSaleSurtax;

    /**
     * 印花税(元)
     */
    private BigDecimal totalStampTax;

    /**
     * 资金成本(元)
     */
    private BigDecimal totalCapitalCost;

    /**
     * 保险成本(元)
     */
    private BigDecimal totalInsuranceCost;

    /**
     * 经营性利润
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
     * 营销费用
     */
    private BigDecimal totalMarketingExpenses;

    /**
     * 采购提成(元)
     */
    private BigDecimal totalBuyCommission;

    /**
     * 销售提成(元)
     */
    private BigDecimal totalSellCommission;

    /**
     * 营销留存(元)
     */
    private BigDecimal totalMarketingRetention;

    /**
     * 公司留存(元)
     */
    private BigDecimal totalCompanyCommission;

    /**
     * 净利(元)
     */
    private BigDecimal totalNetProfit;

    public Integer getTotalTotalNumber() {
        return totalTotalNumber;
    }

    public void setTotalTotalNumber(Integer totalTotalNumber) {
        this.totalTotalNumber = totalTotalNumber;
    }

    public BigDecimal getAveBdealPrice() {
        return aveBdealPrice;
    }

    public void setAveBdealPrice(BigDecimal aveBdealPrice) {
        this.aveBdealPrice = aveBdealPrice;
    }

    public BigDecimal getAveSdealPrice() {
        return aveSdealPrice;
    }

    public void setAveSdealPrice(BigDecimal aveSdealPrice) {
        this.aveSdealPrice = aveSdealPrice;
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

    public BigDecimal getAveCreditDays() {
        return aveCreditDays;
    }

    public void setAveCreditDays(BigDecimal aveCreditDays) {
        this.aveCreditDays = aveCreditDays;
    }

    public BigDecimal getTotalPremiumAmount() {
        return totalPremiumAmount;
    }

    public void setTotalPremiumAmount(BigDecimal totalPremiumAmount) {
        this.totalPremiumAmount = totalPremiumAmount;
    }

    public BigDecimal getTotalServiceAmount() {
        return totalServiceAmount;
    }

    public void setTotalServiceAmount(BigDecimal totalServiceAmount) {
        this.totalServiceAmount = totalServiceAmount;
    }

    public BigDecimal getTotalValueAddedTax() {
        return totalValueAddedTax;
    }

    public void setTotalValueAddedTax(BigDecimal totalValueAddedTax) {
        this.totalValueAddedTax = totalValueAddedTax;
    }

    public BigDecimal getTotalServiceSurtax() {
        return totalServiceSurtax;
    }

    public void setTotalServiceSurtax(BigDecimal totalServiceSurtax) {
        this.totalServiceSurtax = totalServiceSurtax;
    }

    public BigDecimal getTotalSaleSurtax() {
        return totalSaleSurtax;
    }

    public void setTotalSaleSurtax(BigDecimal totalSaleSurtax) {
        this.totalSaleSurtax = totalSaleSurtax;
    }

    public BigDecimal getTotalStampTax() {
        return totalStampTax;
    }

    public void setTotalStampTax(BigDecimal totalStampTax) {
        this.totalStampTax = totalStampTax;
    }

    public BigDecimal getTotalCapitalCost() {
        return totalCapitalCost;
    }

    public void setTotalCapitalCost(BigDecimal totalCapitalCost) {
        this.totalCapitalCost = totalCapitalCost;
    }

    public BigDecimal getTotalInsuranceCost() {
        return totalInsuranceCost;
    }

    public void setTotalInsuranceCost(BigDecimal totalInsuranceCost) {
        this.totalInsuranceCost = totalInsuranceCost;
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

    public BigDecimal getTotalMarketingExpenses() {
        return totalMarketingExpenses;
    }

    public void setTotalMarketingExpenses(BigDecimal totalMarketingExpenses) {
        this.totalMarketingExpenses = totalMarketingExpenses;
    }

    public BigDecimal getTotalBuyCommission() {
        return totalBuyCommission;
    }

    public void setTotalBuyCommission(BigDecimal totalBuyCommission) {
        this.totalBuyCommission = totalBuyCommission;
    }

    public BigDecimal getTotalSellCommission() {
        return totalSellCommission;
    }

    public void setTotalSellCommission(BigDecimal totalSellCommission) {
        this.totalSellCommission = totalSellCommission;
    }

    public BigDecimal getTotalMarketingRetention() {
        return totalMarketingRetention;
    }

    public void setTotalMarketingRetention(BigDecimal totalMarketingRetention) {
        this.totalMarketingRetention = totalMarketingRetention;
    }

    public BigDecimal getTotalCompanyCommission() {
        return totalCompanyCommission;
    }

    public void setTotalCompanyCommission(BigDecimal totalCompanyCommission) {
        this.totalCompanyCommission = totalCompanyCommission;
    }

    public BigDecimal getTotalNetProfit() {
        return totalNetProfit;
    }

    public void setTotalNetProfit(BigDecimal totalNetProfit) {
        this.totalNetProfit = totalNetProfit;
    }
}
