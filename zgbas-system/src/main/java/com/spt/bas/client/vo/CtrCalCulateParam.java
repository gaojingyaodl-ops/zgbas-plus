package com.spt.bas.client.vo;

import java.math.BigDecimal;

/**
 * 提成计算默认参数
 * @Author: gaojy
 * @create 2022/3/10 15:26
 * @version: 1.0
 * @description:
 */
public class CtrCalCulateParam {
    /**
     * 增值税税率-0.13
     */
    private BigDecimal vatRate;

    /**
     * 运费税率-0.09
     */
    private BigDecimal transportationRate;

    /**
     * 仓储费税率-0.06
     */
    private BigDecimal warehouseRate;

    /**
     * 保费税率-0.06
     */
    private BigDecimal premiumRate;

    /**
     * 印花税税率-0.0003
     */
    private BigDecimal stampDutyRate;

    /**
     * 附加税税率-0.1
     */
    private BigDecimal surchargeRate;

    /**
     * 资金服务费率-0.03
     */
    private BigDecimal serveRate;

    /**
     * 采购提成比率-0.08
     */
    private BigDecimal buyCommissionRate;

    /**
     * 代采采购提成比率
     */
    private BigDecimal buyCommissionRateDc;

    /**
     * 销售提成比率-0.29
     */
    private BigDecimal sellCommissionRate;

    /**
     * 销售提成比率代采-0.60
     */
    private BigDecimal sellCommissionRateDc;

    /**
     * 营销留存比率-0.05
     */
    private BigDecimal marketingRate;

    /**
     * 公司净利比率-0.54
     */
    private BigDecimal companyRate;

    /**
     * 公司净利比率代采-0.23
     */
    private BigDecimal companyRateDc;

    /**
     * 采购团队负责人提成比率-0.01
     */
    private BigDecimal buyHeadCommission;

    /**
     * 代采采购团队负责人提成比率
     */
    private BigDecimal buyHeadCommissionDc;


    /**
     * 销售团队负责人提成比率-0.03
     */
    private BigDecimal sellHeadCommission;

    /**
     * 代采销售团队负责人提成比率
     */
    private BigDecimal sellHeadCommissionDc;

    /**
     * 业务员逾期罚息费率
     */
    private BigDecimal matchBreachRate;

    /**
     * 采购提成开关
     */
    private Boolean buyCommissionFlag = true;

    public BigDecimal getVatRate() {
        return vatRate;
    }

    public void setVatRate(BigDecimal vatRate) {
        this.vatRate = vatRate;
    }

    public BigDecimal getTransportationRate() {
        return transportationRate;
    }

    public void setTransportationRate(BigDecimal transportationRate) {
        this.transportationRate = transportationRate;
    }

    public BigDecimal getWarehouseRate() {
        return warehouseRate;
    }

    public void setWarehouseRate(BigDecimal warehouseRate) {
        this.warehouseRate = warehouseRate;
    }

    public BigDecimal getPremiumRate() {
        return premiumRate;
    }

    public void setPremiumRate(BigDecimal premiumRate) {
        this.premiumRate = premiumRate;
    }

    public BigDecimal getStampDutyRate() {
        return stampDutyRate;
    }

    public void setStampDutyRate(BigDecimal stampDutyRate) {
        this.stampDutyRate = stampDutyRate;
    }

    public BigDecimal getSurchargeRate() {
        return surchargeRate;
    }

    public void setSurchargeRate(BigDecimal surchargeRate) {
        this.surchargeRate = surchargeRate;
    }

    public BigDecimal getServeRate() {
        return serveRate;
    }

    public void setServeRate(BigDecimal serveRate) {
        this.serveRate = serveRate;
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

    public BigDecimal getMarketingRate() {
        return marketingRate;
    }

    public void setMarketingRate(BigDecimal marketingRate) {
        this.marketingRate = marketingRate;
    }

    public BigDecimal getCompanyRate() {
        return companyRate;
    }

    public void setCompanyRate(BigDecimal companyRate) {
        this.companyRate = companyRate;
    }

    public BigDecimal getBuyHeadCommission() {
        return buyHeadCommission;
    }

    public void setBuyHeadCommission(BigDecimal buyHeadCommission) {
        this.buyHeadCommission = buyHeadCommission;
    }

    public BigDecimal getSellHeadCommission() {
        return sellHeadCommission;
    }

    public void setSellHeadCommission(BigDecimal sellHeadCommission) {
        this.sellHeadCommission = sellHeadCommission;
    }

    public BigDecimal getSellCommissionRateDc() {
        return sellCommissionRateDc;
    }

    public void setSellCommissionRateDc(BigDecimal sellCommissionRateDc) {
        this.sellCommissionRateDc = sellCommissionRateDc;
    }

    public BigDecimal getCompanyRateDc() {
        return companyRateDc;
    }

    public void setCompanyRateDc(BigDecimal companyRateDc) {
        this.companyRateDc = companyRateDc;
    }

    public BigDecimal getBuyCommissionRateDc() {
        return buyCommissionRateDc;
    }

    public void setBuyCommissionRateDc(BigDecimal buyCommissionRateDc) {
        this.buyCommissionRateDc = buyCommissionRateDc;
    }

    public BigDecimal getBuyHeadCommissionDc() {
        return buyHeadCommissionDc;
    }

    public void setBuyHeadCommissionDc(BigDecimal buyHeadCommissionDc) {
        this.buyHeadCommissionDc = buyHeadCommissionDc;
    }

    public BigDecimal getSellHeadCommissionDc() {
        return sellHeadCommissionDc;
    }

    public void setSellHeadCommissionDc(BigDecimal sellHeadCommissionDc) {
        this.sellHeadCommissionDc = sellHeadCommissionDc;
    }

    public BigDecimal getMatchBreachRate() {
        return matchBreachRate;
    }

    public void setMatchBreachRate(BigDecimal matchBreachRate) {
        this.matchBreachRate = matchBreachRate;
    }

    public Boolean getBuyCommissionFlag() {
        return buyCommissionFlag;
    }

    public void setBuyCommissionFlag(Boolean buyCommissionFlag) {
        this.buyCommissionFlag = buyCommissionFlag;
    }
}

