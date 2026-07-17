package com.spt.bas.report.client.entity;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 人力成本分析表
 */
@Data
public class RptFactBisHumanResourceCostsAnalysisVo {

    /**
     * 人工成本
     */
    private BigDecimal humanResourceCosts = BigDecimal.ZERO;
    
    /**
     * 上月人工成本
     */
    private BigDecimal lastHumanResourceCosts = BigDecimal.ZERO;
    
    /**
     * 同周期上年人工成本
     */
    private BigDecimal lastYearHumanResourceCosts = BigDecimal.ZERO;

    /**
     * 招聘成本
     */
    private BigDecimal recruitmentCosts = BigDecimal.ZERO;
    
    /**
     * 上月招聘成本
     */
    private BigDecimal lastRecruitmentCosts = BigDecimal.ZERO;
    
    /**
     * 同周期上年招聘成本
     */
    private BigDecimal lastYearRecruitmentCosts = BigDecimal.ZERO;

    /**
     * 离职成本
     */
    private BigDecimal departureCosts = BigDecimal.ZERO;
    
    /**
     * 上月离职成本
     */
    private BigDecimal lastDepartureCosts = BigDecimal.ZERO;
    
    /**
     * 同周期上年离职成本
     */
    private BigDecimal lastYearDepartureCosts = BigDecimal.ZERO;

    /**
     * 培训成本
     */
    private BigDecimal trainingCosts = BigDecimal.ZERO;
    
    /**
     * 上月培训成本
     */
    private BigDecimal lastTrainingCosts = BigDecimal.ZERO;
    
    /**
     * 同周期上年培训成本
     */
    private BigDecimal lastYearTrainingCosts = BigDecimal.ZERO;

    /**
     * 租金物业成本
     */
    private BigDecimal rentPropertyCosts = BigDecimal.ZERO;
    
    /**
     * 上月租金物业成本
     */
    private BigDecimal lastRentPropertyCosts = BigDecimal.ZERO;
    
    /**
     * 同周期上年租金物业成本
     */
    private BigDecimal lastYearRentPropertyCosts = BigDecimal.ZERO;

    /**
     * 办公费用成本
     */
    private BigDecimal officeCosts = BigDecimal.ZERO;
    
    /**
     * 上月办公费用成本
     */
    private BigDecimal lastOfficeCosts = BigDecimal.ZERO;
    
    /**
     * 同周期上年办公费用成本
     */
    private BigDecimal lastYearOfficeCosts = BigDecimal.ZERO;

    /**
     * 商务招待成本
     */
    private BigDecimal businessEntertainmentCosts = BigDecimal.ZERO;
    
    /**
     * 上月商务招待成本
     */
    private BigDecimal lastBusinessEntertainmentCosts = BigDecimal.ZERO;
    
    /**
     * 同周期上年商务招待成本
     */
    private BigDecimal lastYearBusinessEntertainmentCosts = BigDecimal.ZERO;

    
    /**
     * 在编人数-前台
     */
    private Integer frontPersonCount = 0;

    /**
     * 在编人数-中台
     */
    private Integer middlePersonCount = 0;

    /**
     * 在编人数-后台
     */
    private Integer backPersonCount = 0;

   
    
    
}
