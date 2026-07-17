package com.spt.bas.report.client.vo;

import java.math.BigDecimal;

/**
 * 保费费率
 * @Author: gaojy
 * @create 2022/3/3 9:53
 * @version: 1.0
 * @description:
 */
public class RptCalculateInsuranceRates {

    private BigDecimal insuranceRate;
    private String condition;

    public BigDecimal getInsuranceRate() {
        return insuranceRate;
    }

    public void setInsuranceRate(BigDecimal insuranceRate) {
        this.insuranceRate = insuranceRate;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }
}
