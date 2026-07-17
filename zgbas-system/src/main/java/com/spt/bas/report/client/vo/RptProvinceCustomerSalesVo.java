package com.spt.bas.report.client.vo;

import lombok.Data;

@Data
public class RptProvinceCustomerSalesVo {
    // 月份查询开始日期
    private String monthBegin;
    // 月份查询结束日期
    private String monthEnd;
    // 区域
    private String deptId;
    // 业务类型
    private Boolean matchCreditFlg;
}
