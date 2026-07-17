package com.spt.bas.report.client.entity;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 经营数据分析表
 */
@Data
public class RptFactBisBusinessAnalysisVo {

    /**
     * 赊销销售金额
     */
    private BigDecimal sxTotalAmount = BigDecimal.ZERO;

    /**
     * 上月销销销售金额
     */
    private BigDecimal lastSxTotalAmount = BigDecimal.ZERO;
    
    /**
     * 同周期上年销销销售金额
     */
    private BigDecimal lastYearSxTotalAmount = BigDecimal.ZERO;

    /**
     * 代采销售金额
     */
    private BigDecimal dcTotalAmount = BigDecimal.ZERO;

    /**
     * 上月代采销售金额
     */
    private BigDecimal lastDcTotalAmount = BigDecimal.ZERO;
    
    /**
     * 同周期上年代采销售金额 
     */
    private BigDecimal lastYearDcTotalAmount = BigDecimal.ZERO;

    /**
     * 赊销销售量（吨）
     */
    private BigDecimal sxTotalNumber = BigDecimal.ZERO;

    /**
     * 上月销销销售量（吨）
     */
    private BigDecimal lastSxTotalNumber = BigDecimal.ZERO;
    
    /**
     * 同周期上年销销销售量（吨）
     */
    private BigDecimal lastYearSxTotalNumber = BigDecimal.ZERO;

    /**
     * 代采销售量（吨）
     */
    private BigDecimal dcTotalNumber = BigDecimal.ZERO;

    /**
     * 上月代采销售量（吨）
     */
    private BigDecimal lastDcTotalNumber = BigDecimal.ZERO;
    
    /**
     * 同周期上年代采销售量（吨）
     */
    private BigDecimal lastYearDcTotalNumber = BigDecimal.ZERO;

    /**
     * 赊销毛利润
     */
    private BigDecimal sxGrossProfit = BigDecimal.ZERO;
    
    /**
     * 上月销销毛利润
     */
    private BigDecimal lastSxGrossProfit = BigDecimal.ZERO;
    
    /**
     * 同周期上年销销毛利润
     */
    private BigDecimal lastYearSxGrossProfit = BigDecimal.ZERO;

    /**
     * 代采毛利润
     */
    private BigDecimal dcGrossProfit = BigDecimal.ZERO;

    /**
     * 上月代采毛利润
     */
    private BigDecimal lastDcGrossProfit = BigDecimal.ZERO;
    
    /**
     * 同周期上年代采毛利润
     */
    private BigDecimal lastYearDcGrossProfit = BigDecimal.ZERO;

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
     * 资金成本(金融服务费)
     */
    private BigDecimal capitalCost = BigDecimal.ZERO;

    /**
     * 上月资金成本(金融服务费)
     */
    private BigDecimal lastCapitalCost = BigDecimal.ZERO;
    
    /**
     * 同周期上年资金成本(金融服务费)
     */
    private BigDecimal lastYearCapitalCost = BigDecimal.ZERO;

    /**
     * 销售费用
     */
    private BigDecimal saleAmount = BigDecimal.ZERO;

    /**
     * 上月销售费用
     */
    private BigDecimal lastSaleAmount = BigDecimal.ZERO;
    
    /**
     * 同周期上年销售费用
     */
    private BigDecimal lastYearSaleAmount = BigDecimal.ZERO;
    
    /**
     * 管理分摊
     */
    private BigDecimal managementAllocation = BigDecimal.ZERO;

    /**
     * 上月管理分摊
     */
    private BigDecimal lastManagementAllocation = BigDecimal.ZERO;
    
    /**
     * 同周期上年管理分摊
     */
    private BigDecimal lastYearManagementAllocation = BigDecimal.ZERO;

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
