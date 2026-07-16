package com.spt.bas.client.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ApplyProtocolDocZnjDetailVo {

    /**
     * 合同编号
     */
    private String contractNo;

    /**
     * 合同金额
     */
    private BigDecimal totalAmount;

    /**
     * 实际收货日期
     */
    private String deliveryDate;

    /**
     * 约定账期
     */
    private String creditDays;

    /**
     * 应付款日期
     */
    private String payDateStr;

    /**
     * 实际付款日期
     */
    private String realPayDateStr;

    /**
     * 逾期滞纳金
     */
    private String overdueLateFees;

    /**
     * 逾期滞纳金合计
     */
    private BigDecimal overdueLateFeeSum;

    private Integer randomNumber = 0;


}
