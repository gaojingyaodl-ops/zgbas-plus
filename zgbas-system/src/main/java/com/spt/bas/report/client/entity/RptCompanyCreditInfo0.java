package com.spt.bas.report.client.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 企业导出人保对象
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RptCompanyCreditInfo0 {
    /**
     * 公司名称
     */
    private String companyName;
    /**
     * 法人
     */
    private String legalRepresent;
    /**
     * 成立时间
     */
    private String startDate;
    /**
     * 注册资本
     */
    private String registerCapital;
    /**
     * 地址
     */
    private String address;
    /**
     * 人保额度
     */
    private BigDecimal creditAmount;
    /**
     * 最近合作日期
     */
    private String contractTime;
    /**
     * 营业执照附件ID
     */
    private String fileId;
}
