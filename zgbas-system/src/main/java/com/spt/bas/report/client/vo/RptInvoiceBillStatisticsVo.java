package com.spt.bas.report.client.vo;

import lombok.Data;

import java.util.List;

@Data
public class RptInvoiceBillStatisticsVo {
    // 我方
    private String ourCompanyName;
    // 开票日期（开/收）
    private String invoiceDate;
    // 资金方名称集合
    private List<String> ourCompanyNameList;
    // 查看所有资金方权限
    private Boolean viewAllFlg = false;
    // 当前人ID
    private Long userId;

}
