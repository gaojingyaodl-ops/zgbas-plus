package com.spt.bas.report.client.entity;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 个人成就
 */
@Data
public class RptPersonalAchievement {

    /**
     * 业务员
     */
    private Long matchUserId;
    private String matchUserName;
    /**
     * 所属区域
     */
    private Long deptId;
    private String deptName;
    /**
     * 公司业绩排名
     */
    private String ranking;
    /**
     * 月份
     */
    private String month;
    /**
     * 毛利润
     */
    private BigDecimal grossProfitAmount;
    /**
     * 销售额
     */
    private BigDecimal sellTotalAmount;
    /**
     * 销售量
     */
    private BigDecimal sellTotalNumber;


}
