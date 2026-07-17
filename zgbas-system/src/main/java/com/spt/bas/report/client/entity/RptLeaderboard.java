package com.spt.bas.report.client.entity;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 排行榜vo
 */
@Data
public class RptLeaderboard {

    private Long matchUserId;
    private String matchUserName;
    private Long deptId;
    private String deptName;
    private BigDecimal grossProfitAmount;
    private BigDecimal totalNum;
    private BigDecimal totalAmount;
}
