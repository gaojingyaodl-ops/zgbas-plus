package com.spt.bas.report.client.entity;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 业务指标配置查询结果表
 */
@Data
public class RptFactBisBusinessWorkTargetResultVo {

    /**
     * 目标总价
     */
    private BigDecimal targetTotalAmount = BigDecimal.ZERO;

    /**
     * 业务类型
     */
    private String businessType;

    /**
     * 指标类型
     */
    private String targetType;
    
    
    
}
