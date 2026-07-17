package com.spt.bas.report.client.entity;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 改性塑料经营数据分析表
 */
@Data
public class RptFactBisModifiedBusinessVo {

    /**
     * 销售金额
     */
    private BigDecimal totalAmount = BigDecimal.ZERO;

    /**
     * 上月销售金额
     */
    private BigDecimal lastTotalAmount = BigDecimal.ZERO;

    /**
     * 同周期上年销售金额
     */
    private BigDecimal lastYearTotalAmount;

    /**
     * 销售量（吨）
     */
    private BigDecimal totalNumber = BigDecimal.ZERO;

    /**
     * 上月销售量（吨）
     */
    private BigDecimal lastTotalNumber = BigDecimal.ZERO;

    /**
     * 同周期上年销售量（吨）
     */
    private BigDecimal lastYearTotalNumber = BigDecimal.ZERO;

    /**
     * 毛利润
     */
    private BigDecimal grossProfit = BigDecimal.ZERO;

    /**
     * 上月毛利润
     */
    private BigDecimal lastGrossProfit = BigDecimal.ZERO;

    /**
     * 同周期上年毛利润
     */
    private BigDecimal lastYearGrossProfit = BigDecimal.ZERO;

    /**
     * 社保公积金
     */
    private BigDecimal socialSecurity = BigDecimal.ZERO;

    /**
     * 上月社保公积金
     */
    private BigDecimal lastSocialSecurity = BigDecimal.ZERO;

    /**
     * 同周期上年社保公积金
     */
    private BigDecimal lastYearSocialSecurity = BigDecimal.ZERO;

    /**
     * 出差费用
     */
    private BigDecimal evectionAmount = BigDecimal.ZERO;

    /**
     * 上月出差费用
     */
    private BigDecimal lastEvectionAmount = BigDecimal.ZERO;

    /**
     * 同周期上年出差费用
     */
    private BigDecimal lastYearEvectionAmount = BigDecimal.ZERO;


    /**
     * 公关费用
     */
    private BigDecimal ggAmount = BigDecimal.ZERO;

    /**
     * 上月公关费用
     */
    private BigDecimal lastGgAmount = BigDecimal.ZERO;

    /**
     * 同周期上年公关费用
     */
    private BigDecimal lastYearGgAmount = BigDecimal.ZERO;


    /**
     * 资金成本
     */
    private BigDecimal capitalCost = BigDecimal.ZERO;

    /**
     * 上月资金成本
     */
    private BigDecimal lastCapitalCost = BigDecimal.ZERO;
    
    /**
     * 同周期上年资金成本
     */
    private BigDecimal lastYearCapitalCost = BigDecimal.ZERO;


    /**
     * 净利润
     */
    private BigDecimal netMargin = BigDecimal.ZERO;

    /**
     * 上月净利润
     */
    private BigDecimal lastNetMargin = BigDecimal.ZERO;

    /**
     * 同周期上年净利润
     */
    private BigDecimal lastYearNetMargin = BigDecimal.ZERO;

    /**
     * 运营成本
     */
    private BigDecimal operatingCosts = BigDecimal.ZERO;

    /**
     * 上月运营成本
     */
    private BigDecimal lastOperatingCosts = BigDecimal.ZERO;

    /**
     * 同周期上年运营成本
     */
    private BigDecimal lastYearOperatingCosts = BigDecimal.ZERO;


    /**
     * 结余
     */
    private BigDecimal surplus = BigDecimal.ZERO;

    /**
     * 上月结余
     */
    private BigDecimal lastSurplus = BigDecimal.ZERO;

    /**
     * 同周期上年结余
     */
    private BigDecimal lastYearSurplus = BigDecimal.ZERO;

    /**
     * 人员薪酬
     */
    private BigDecimal personnelSalary = BigDecimal.ZERO;

    /**
     * 上月人员薪酬
     */
    private BigDecimal lastPersonnelSalary = BigDecimal.ZERO;

    /**
     * 同周期上年人员薪酬
     */
    private BigDecimal lastYearPersonnelSalary = BigDecimal.ZERO;

    /**
     * 当季销售指标配置
     */
    private BigDecimal sellWorkTarget = BigDecimal.ZERO;
    
    /**
     * 当季销售额
     */
    private BigDecimal quarterlySellAmount = BigDecimal.ZERO;

    /**
     * 当季毛利指标配置
     */
    private BigDecimal grossProfitWorkTarget = BigDecimal.ZERO;
    
    /**
     * 当季毛利润
     */
    private BigDecimal quarterlyGrossProfit = BigDecimal.ZERO; 
    
    
}
