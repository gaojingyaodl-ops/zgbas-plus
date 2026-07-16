package com.spt.bas.client.vo;

import java.math.BigDecimal;

public class BsProductConfigVo {
    /**
     * 服务比率
     */
    private BigDecimal serveRate;
    /**
     * 公司提成比率
     */
    private BigDecimal companyCommissionRate;
    /**
     * 业务提成比率
     */
    private BigDecimal businessCommissionRate;
    /**
     * 采购提成比率
     */
    private BigDecimal buyCommissionRate;
    /**
     * 销售提成比率
     */
    private BigDecimal sellCommissionRate;
    /**
     * 管理提成比率
     */
    private BigDecimal manageCommissionRate;
    /**
     * 资金成本比率
     */
    private BigDecimal costRate;
    /**
     * 逾期罚息比率
     */
    private BigDecimal breachRate;
    /**
     * 保费比率
     */
    private BigDecimal insuranceRate;
    /**
     * 赊销比率
     */
    private BigDecimal creditRate;
    /**
     * 日息
     */
    private BigDecimal interest;

    /**
     * 营销留存比例
     */
    private BigDecimal marketingRate;

    public BigDecimal getServeRate() {
        return serveRate;
    }

    public void setServeRate(BigDecimal serveRate) {
        this.serveRate = serveRate;
    }

    public BigDecimal getCompanyCommissionRate() {
        return companyCommissionRate;
    }

    public void setCompanyCommissionRate(BigDecimal companyCommissionRate) {
        this.companyCommissionRate = companyCommissionRate;
    }

    public BigDecimal getBusinessCommissionRate() {
        return businessCommissionRate;
    }

    public void setBusinessCommissionRate(BigDecimal businessCommissionRate) {
        this.businessCommissionRate = businessCommissionRate;
    }

    public BigDecimal getBuyCommissionRate() {
        return buyCommissionRate;
    }

    public void setBuyCommissionRate(BigDecimal buyCommissionRate) {
        this.buyCommissionRate = buyCommissionRate;
    }

    public BigDecimal getSellCommissionRate() {
        return sellCommissionRate;
    }

    public void setSellCommissionRate(BigDecimal sellCommissionRate) {
        this.sellCommissionRate = sellCommissionRate;
    }

    public BigDecimal getManageCommissionRate() {
        return manageCommissionRate;
    }

    public void setManageCommissionRate(BigDecimal manageCommissionRate) {
        this.manageCommissionRate = manageCommissionRate;
    }

    public BigDecimal getCostRate() {
        return costRate;
    }

    public void setCostRate(BigDecimal costRate) {
        this.costRate = costRate;
    }

    public BigDecimal getBreachRate() {
        return breachRate;
    }

    public void setBreachRate(BigDecimal breachRate) {
        this.breachRate = breachRate;
    }

    public BigDecimal getInsuranceRate() {
        return insuranceRate;
    }

    public void setInsuranceRate(BigDecimal insuranceRate) {
        this.insuranceRate = insuranceRate;
    }

    public BigDecimal getCreditRate() {
        return creditRate;
    }

    public void setCreditRate(BigDecimal creditRate) {
        this.creditRate = creditRate;
    }

    public BigDecimal getInterest() {
        return interest;
    }

    public void setInterest(BigDecimal interest) {
        this.interest = interest;
    }

    public BigDecimal getMarketingRate() {
        return marketingRate;
    }

    public void setMarketingRate(BigDecimal marketingRate) {
        this.marketingRate = marketingRate;
    }
}
