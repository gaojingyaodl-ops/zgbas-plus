package com.spt.bas.report.client.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author MoonLight
 * @Date 2024/8/26 15:55
 * @Version 1.0
 */
@Data
@Getter
@Setter
public class RptExportChainVo {
    private String contractType;

    private Long approveId;
    /**
     * 合同编号
     */
    private String contractNo;

    /**
     * 买/卖标识
     */
    private String tag;

    /**
     * 我方
     */
    private String ourCompanyName;

    /**
     * 对方企业
     */
    private String companyName;

    /**
     * 货名
     */
    private String productNames;

    /**
     * 合同数量
     */
    private BigDecimal totalNumber;

    /**
     * 合同单价
     */
    private BigDecimal dealPrice;

    /**
     * 总金额
     */
    private BigDecimal totalAmount;

    /**
     * 回款周期(天)
     */
    private Long creditDays;

    /**
     * 签订日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date contractTime;

    /**
     * 收/付款日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date receiveDate;

    /**
     * 已收付款金额
     */
    private BigDecimal receiveAmount;

    /**
     * 应收付/款金额
     */
    private BigDecimal needReceiveAmount;

    /**
     * 开票日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date billDate;

    /**
     * 开票金额
     */
    private BigDecimal billAmount;

    /**
     * 约定收付款日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date appointPayFullTime;

    /**
     * 出入库日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date deliveryDate;

    /**
     * 确认收货日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date confirmDate;
    /**
     * 人保已用额度
     */
    private BigDecimal usedCreditAmount;
    /**
     * 人保可用额度
     */
    private BigDecimal availableCreditAmount;
    /**
     * 对方企业Id
     */
    private Long companyId;
}
