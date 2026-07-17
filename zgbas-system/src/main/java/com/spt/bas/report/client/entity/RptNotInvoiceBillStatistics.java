package com.spt.bas.report.client.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spt.tools.jpa.vo.IdEntity;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 未开票明细 VO
 */
@Data
public class RptNotInvoiceBillStatistics extends IdEntity {

    private Long id;

    /**
     * 合同类型（DC：2,SX：1,DCSX：5）
     */
    private String profitType;

    /**
     * 合同编号
     */
    private String sellContractNo;

    /**
     * 我方抬头
     */
    private String ourCompanyName;

    /**
     * 客户名称
     */
    private String sellCompanyName;

    /**
     * 产品
     */
    private String productName;

    /**
     * 合同数量
     */
    private BigDecimal totalNumber;

    /**
     * 合同总价
     */
    private BigDecimal sellTotalAmount;

    /**
     * 签订日
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date contractTime;

    /**
     * 已收款金额
     */
    private BigDecimal receiveAmount;

    /**
     * 收全款日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date sellPayFullDate;

    /**
     * 已开票金额
     */
    private BigDecimal invoiceBillAmount;

    /**
     * 开票日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date invoiceBillDate;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
