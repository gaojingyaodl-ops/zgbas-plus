package com.spt.bas.report.client.entity;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 经营数据分析表
 */
@Data
public class RptFactBisBusinessAnalysisResultVo {

    /**
     * 销售金额
     */
    private BigDecimal totalAmount = BigDecimal.ZERO;
    
    /**
     * 销售量（吨）
     */
    private BigDecimal totalNumber = BigDecimal.ZERO;

    /**
     * 毛利润
     */
    private BigDecimal grossProfit = BigDecimal.ZERO;
    
    /**
     * 人员薪酬
     */
    private BigDecimal personnelSalary = BigDecimal.ZERO;
    
    /**
     * 资金成本(金融服务费)
     */
    private BigDecimal capitalCost = BigDecimal.ZERO;
    
    /**
     * 销售费用
     */
    private BigDecimal saleAmount = BigDecimal.ZERO;
    
    /**
     * 管理分摊
     */
    private BigDecimal managementAllocation = BigDecimal.ZERO;
    
    
}
