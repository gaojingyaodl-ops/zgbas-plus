package com.spt.bas.report.client.entity;

import java.math.BigDecimal;

/**
 * <p>
 *    决算统计合计
 * </p>
 *
 */
public class RptSumFinalAccountReportNew {

    /**
     * 合同数量
     */
    private Integer totalDealNumber;

    /**
     * 采购单价
     */
    private BigDecimal aveBuyPrice;

    /**
     * 销售单价
     */
    private BigDecimal aveSellPrice;

    /**
     * 采购合同金额
     */
    private BigDecimal totalBuyTotalAmount;

    /**
     * 销售合同金额
     */
    private BigDecimal totalSellTotalAmount;

    /**
     * 金融服务费(元)
     */
    private BigDecimal totalFinancialServiceAmount;

    /**
     * 增值税(元)
     */
    private BigDecimal totalVatAmount;

    /**
     * 附加税(元)
     */
    private BigDecimal totalSurchargeAmount;
    
    /**
     * 印花税(元)
     */
    private BigDecimal totalPrintAmount;
    
    /**
     * 毛利
     */
    private BigDecimal totalAfterTaxSpreadAmount;

    public Integer getTotalDealNumber() {
        return totalDealNumber;
    }

    public void setTotalDealNumber(Integer totalDealNumber) {
        this.totalDealNumber = totalDealNumber;
    }

    public BigDecimal getAveBuyPrice() {
        return aveBuyPrice;
    }

    public void setAveBuyPrice(BigDecimal aveBuyPrice) {
        this.aveBuyPrice = aveBuyPrice;
    }

    public BigDecimal getAveSellPrice() {
        return aveSellPrice;
    }

    public void setAveSellPrice(BigDecimal aveSellPrice) {
        this.aveSellPrice = aveSellPrice;
    }

    public BigDecimal getTotalBuyTotalAmount() {
        return totalBuyTotalAmount;
    }

    public void setTotalBuyTotalAmount(BigDecimal totalBuyTotalAmount) {
        this.totalBuyTotalAmount = totalBuyTotalAmount;
    }

    public BigDecimal getTotalSellTotalAmount() {
        return totalSellTotalAmount;
    }

    public void setTotalSellTotalAmount(BigDecimal totalSellTotalAmount) {
        this.totalSellTotalAmount = totalSellTotalAmount;
    }

    public BigDecimal getTotalFinancialServiceAmount() {
        return totalFinancialServiceAmount;
    }

    public void setTotalFinancialServiceAmount(BigDecimal totalFinancialServiceAmount) {
        this.totalFinancialServiceAmount = totalFinancialServiceAmount;
    }

    public BigDecimal getTotalVatAmount() {
        return totalVatAmount;
    }

    public void setTotalVatAmount(BigDecimal totalVatAmount) {
        this.totalVatAmount = totalVatAmount;
    }

    public BigDecimal getTotalSurchargeAmount() {
        return totalSurchargeAmount;
    }

    public void setTotalSurchargeAmount(BigDecimal totalSurchargeAmount) {
        this.totalSurchargeAmount = totalSurchargeAmount;
    }

    public BigDecimal getTotalPrintAmount() {
        return totalPrintAmount;
    }

    public void setTotalPrintAmount(BigDecimal totalPrintAmount) {
        this.totalPrintAmount = totalPrintAmount;
    }

    public BigDecimal getTotalAfterTaxSpreadAmount() {
        return totalAfterTaxSpreadAmount;
    }

    public void setTotalAfterTaxSpreadAmount(BigDecimal totalAfterTaxSpreadAmount) {
        this.totalAfterTaxSpreadAmount = totalAfterTaxSpreadAmount;
    }
}
