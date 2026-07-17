package com.spt.bas.report.client.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 主页毛利率vo
 *
 * @author 杨英承
 * @version 1.0.0
 * @date 2022/6/23 18:06
 */

@Data
public class RptGrossProfitMarginVo {
    /**
     * 净毛利率
     */
    private BigDecimal netRate;
    private BigDecimal rate;

    /**
     * 月份
     */
    private String month;

    /**
     * 净毛利额
     */
    private BigDecimal netGrossProfit;
    private BigDecimal grossProfit;
    private BigDecimal buyTotalAmount = BigDecimal.ZERO;

    /**
     * 未收违约金
     */
    private BigDecimal noReceiveBreachAmount = BigDecimal.ZERO;

    /**
     * 逾期罚息
     */
    private BigDecimal overdueInterestSum = BigDecimal.ZERO;

    /**
     * 代采赊销资金成本
     */
    private BigDecimal dcsxCapitalCost = BigDecimal.ZERO;

    /**
     * 普通赊销资金成本
     */
    private BigDecimal capitalCost = BigDecimal.ZERO;

    /**
     * 普通赊销 代采赊销 资金成本合计
     */
    private BigDecimal totalCapitalCost = BigDecimal.ZERO;

}
