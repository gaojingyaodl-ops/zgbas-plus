package com.spt.bas.report.client.vo;

import java.math.BigDecimal;

/**
 * 业务员利润计算配置表
 * @Author: gaojy
 * @create 2022/2/25 11:25
 * @version: 1.0
 * @description:
 */
public class RptMatchProfitConfig {
    /**
     * 业务员ID
     */
    private Long matchUserId;

    /**
     * 业务员
     */
    private String matchUserName;

    /**
     * 采购提成比率
     */
    private BigDecimal buyCommissionRate;

    /**
     * 销售提成比率
     */
    private BigDecimal sellCommissionRate;

    /**
     * 营销留存比率
     */
    private BigDecimal marketingRate;

    /**
     * 公司净利比率
     */
    private BigDecimal companyRate;

    /**
     * 采购团队负责人提成比率
     */
    private BigDecimal buyHeadCommissionRate;

    /**
     * 销售团队负责人提成比率
     */
    private BigDecimal sellHeadCommissionRate;

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

    public BigDecimal getBuyHeadCommissionRate() {
        return buyHeadCommissionRate;
    }

    public void setBuyHeadCommissionRate(BigDecimal buyHeadCommissionRate) {
        this.buyHeadCommissionRate = buyHeadCommissionRate;
    }

    public BigDecimal getSellHeadCommissionRate() {
        return sellHeadCommissionRate;
    }

    public void setSellHeadCommissionRate(BigDecimal sellHeadCommissionRate) {
        this.sellHeadCommissionRate = sellHeadCommissionRate;
    }
}
