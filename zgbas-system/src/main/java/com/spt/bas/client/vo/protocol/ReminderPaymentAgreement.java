package com.spt.bas.client.vo.protocol;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author 田起立
 * @Date 2024/6/6 13:56
 * @Description: 付款提示函
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ReminderPaymentAgreement {

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
     * 合同数量(吨)
     */
    private BigDecimal totalNumber;

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
     * 已付款金额
     */
    private BigDecimal dealedAmount;

    /**
     * 应付款金额
     */
    private BigDecimal shouldPayAmount;




    private Integer randomNumber = 0;
}
