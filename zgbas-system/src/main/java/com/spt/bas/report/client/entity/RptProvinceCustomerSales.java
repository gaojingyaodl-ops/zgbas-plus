package com.spt.bas.report.client.entity;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 省份统计客户销售额
 */
@Data
public class RptProvinceCustomerSales {
    // 省份code
    private String provinceCode;
    // 省份名称
    private String provinceName;
    // 销售额
    private BigDecimal totalAmount;
    // 占比
    private BigDecimal proportion;

    public BigDecimal getTotalAmount() {
        return totalAmount == null ? BigDecimal.ZERO : totalAmount;
    }
}
