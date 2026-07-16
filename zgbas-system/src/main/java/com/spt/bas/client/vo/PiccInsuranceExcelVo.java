package com.spt.bas.client.vo;

import lombok.Data;

import java.math.BigDecimal;


/**
 * 人保保费流失导入Excel
 */
@Data
public class PiccInsuranceExcelVo {

    /**
     * 合同号（发票号）
     */
    private String contractNo;

    /**
     * 保险费率
     */
    private String insuranceRate;

    /**
     * 保险费用
     */
    private String insuranceAmount;

    /**
     * 信用期限
     */
    private String creditCycle;

    /**
     * 录入日期
     */
    private String EntryDate;
    
}
