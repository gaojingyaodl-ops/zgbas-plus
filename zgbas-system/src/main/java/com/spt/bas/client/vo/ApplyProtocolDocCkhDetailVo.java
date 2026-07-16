package com.spt.bas.client.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class ApplyProtocolDocCkhDetailVo {

    /**
     * 合同编号
     */
    private String contractNo;

    /**
     * 品种
     */
    private String productCd;
    private String productName;

    /**
     * 牌号
     */
    private String brandNumber;

    /**
     * 合同金额
     */
    private BigDecimal totalAmount;

    /**
     * 合同数量
     */
    private BigDecimal totalNumber;

    /**
     * 约定付款日期
     */
    private String payFullDate;

    /**
     * 已付金额
     */
    private BigDecimal dealedAmount;

    /**
     * 逾期未付金额
     */
    private BigDecimal unPayOverdueAmount;

    /**
     * 逾期罚息
     */
    private BigDecimal breachAmount;

    /**
     * 逾期天数
     */
    private Long breachDays;

    private Integer randomNumber = 0;


}
