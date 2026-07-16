package com.spt.bas.client.vo;

import java.math.BigDecimal;

/**
 * 根据企业等级获取服务费率、违约费率
 * @Author: gaojy
 * @create 2022/4/2 18:37
 * @version: 1.0
 * @description:
 */
public class ParamByCompanyGrade {
    private BigDecimal breachRate;
    private BigDecimal serveRate;
    private String condition;

    public BigDecimal getBreachRate() {
        return breachRate;
    }

    public void setBreachRate(BigDecimal breachRate) {
        this.breachRate = breachRate;
    }

    public BigDecimal getServeRate() {
        return serveRate;
    }

    public void setServeRate(BigDecimal serveRate) {
        this.serveRate = serveRate;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }
}
