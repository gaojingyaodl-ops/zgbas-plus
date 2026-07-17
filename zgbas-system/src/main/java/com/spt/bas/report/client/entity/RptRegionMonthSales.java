package com.spt.bas.report.client.entity;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 区域月份销售额度
 */
@Data
public class RptRegionMonthSales {
    // 部门id
    private Long deptId;
    // 部门名称
    private String deptName;
    // 月份
    private String contractTime;
    // 销售额
    private BigDecimal sumTotalAmount;
}
