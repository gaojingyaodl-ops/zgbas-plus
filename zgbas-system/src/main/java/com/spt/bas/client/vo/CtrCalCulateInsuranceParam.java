package com.spt.bas.client.vo;

import java.math.BigDecimal;

/**
 * 动态保费费率
 * @Author: gaojy
 * @create 2022/4/2 10:18
 * @version: 1.0
 * @description:
 */
public class CtrCalCulateInsuranceParam {
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
