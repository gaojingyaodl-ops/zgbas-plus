package com.spt.bas.report.client.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spt.tools.jpa.vo.IdEntity;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 开票统计
 */
@Data
public class RptInvoiceBillStatistics extends IdEntity {
    // 我方
    private String ourCompanyName;
    // 开票年月
    private String invoiceDate;
    // 开票总额
    private BigDecimal invoiceAmount;
    // 开票吨数
    private BigDecimal invoiceNumber;
    // 收票总额
    private BigDecimal invoiceReceiveAmount;
    // 收票吨数
    private BigDecimal invoiceReceiveNumber;


}
