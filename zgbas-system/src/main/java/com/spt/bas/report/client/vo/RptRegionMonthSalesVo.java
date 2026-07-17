package com.spt.bas.report.client.vo;

import lombok.Data;

import java.util.List;

@Data
public class RptRegionMonthSalesVo {
    // 月份查询开始日期
    private String monthBegin;
    // 月份查询结束日期
    private String monthEnd;
    // 区域
    private List<String> deptIdList;
}
