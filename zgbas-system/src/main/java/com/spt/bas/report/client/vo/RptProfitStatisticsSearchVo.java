package com.spt.bas.report.client.vo;

import lombok.Data;

import java.util.List;

/**
 * 利润表查询VO
 */
@Data
public class RptProfitStatisticsSearchVo {

    /**
     * 月份
     */
    private String searchMonth;

    /**
     * 年度
     */
    private String searchYear;

    /**
     * 部门ID
     */
    private List<Long> deptIdList;

    /**
     * 类型
     */
    private String type;

}
