package com.spt.bas.report.client.entity;

import com.spt.tools.jpa.vo.IdEntity;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 利润表统计 
 */

@Data
public class RptProfitStatistics extends IdEntity {


/** ---------------------------------- 主营业务收入 ------------------------------------------ */
    
    /**
     * 主营业务收入（本月）
     */
    private BigDecimal monthBusinessIncomeSum = BigDecimal.ZERO;
    
    /**
     * 主营业务收入（本年）
     */
    private BigDecimal yearBusinessIncomeSum = BigDecimal.ZERO;

    /**
     * 代采收入（本月）
     */
    private BigDecimal monthDcBusinessIncome = BigDecimal.ZERO;

    /**
     * 代采收入（本年）
     */
    private BigDecimal yearDcBusinessIncome = BigDecimal.ZERO;

    /**
     * 代采赊销收入（本月）
     */
    private BigDecimal monthDcsxBusinessIncome = BigDecimal.ZERO;

    /**
     * 代采赊销收入（本年）
     */
    private BigDecimal yearDcsxBusinessIncome = BigDecimal.ZERO;
     
/** ---------------------------------- 主营业务成本 ------------------------------------------ */
    
    /**
     * 主营业务成本（本月）
     */
    private BigDecimal monthBusinessCostsSum = BigDecimal.ZERO;
    
    /**
     * 主营业务成本（本年）
     */
    private BigDecimal yearBusinessCostsSum = BigDecimal.ZERO;

    /**
     * 代采成本（本月）
     */
    private BigDecimal monthDcBusinessCosts = BigDecimal.ZERO;

    /**
     * 代采成本（本年）
     */
    private BigDecimal yearDcBusinessCosts = BigDecimal.ZERO;

    /**
     * 代采赊销成本（本月）
     */
    private BigDecimal monthDcsxBusinessCosts = BigDecimal.ZERO;

    /**
     * 代采赊销成本（本年）
     */
    private BigDecimal yearDcsxBusinessCosts = BigDecimal.ZERO;

/** ---------------------------------- 营业利润 ------------------------------------------ */

    /**
     * 营业利润（本月）
     */
    private BigDecimal monthBusinessProfitSum = BigDecimal.ZERO;

    /**
     * 营业利润（本年）
     */
    private BigDecimal yearBusinessProfitSum = BigDecimal.ZERO;

/** ---------------------------------- 营业费用 ------------------------------------------ */

    /**
     * 营业费用（本月）
     */
    private BigDecimal monthBusinessFeeSum = BigDecimal.ZERO;

    /**
     * 营业费用（本年）
     */
    private BigDecimal yearBusinessFeeSum = BigDecimal.ZERO;

    /**
     * 运输费（本月）
     */
    private BigDecimal monthTransportFee = BigDecimal.ZERO;

    /**
     * 运输费（本年）
     */
    private BigDecimal yearTransportFee = BigDecimal.ZERO;

    /**
     * 仓储费（本月）
     */
    private BigDecimal monthWarehouseAmount = BigDecimal.ZERO;

    /**
     * 仓储费（本年）
     */
    private BigDecimal yearWarehouseAmount = BigDecimal.ZERO;

    /**
     * 金融服务费（本月）
     */
    private BigDecimal monthFinancialServiceAmount = BigDecimal.ZERO;

    /**
     * 金融服务费（本年）
     */
    private BigDecimal yearFinancialServiceAmount = BigDecimal.ZERO;

    /**
     * 资金成本（本月）
     */
    private BigDecimal monthCapitalCost = BigDecimal.ZERO;

    /**
     * 资金成本（本年）
     */
    private BigDecimal yearCapitalCost = BigDecimal.ZERO;

    /**
     * 贴现成本（本月）
     */
    private BigDecimal monthDiscountCost = BigDecimal.ZERO;

    /**
     * 贴现成本（本年）
     */
    private BigDecimal yearDiscountCost = BigDecimal.ZERO;

    /**
     * 保费（本月）
     */
    private BigDecimal monthInsuranceFee = BigDecimal.ZERO;

    /**
     * 保费（本年）
     */
    private BigDecimal yearInsuranceFee = BigDecimal.ZERO;

    /**
     * 税金（本月）
     */
    private BigDecimal monthTaxes = BigDecimal.ZERO;

    /**
     * 税金（本年）
     */
    private BigDecimal yearTaxes = BigDecimal.ZERO;

    /**
     * 人员成本（本月）
     */
    private BigDecimal monthPersonCost = BigDecimal.ZERO;

    /**
     * 人员成本（本年）
     */
    private BigDecimal yearPersonCost = BigDecimal.ZERO;

    /**
     * 差旅招待费（本月）
     */
    private BigDecimal monthTravelExpensesFee = BigDecimal.ZERO;

    /**
     * 差旅招待费（本年）
     */
    private BigDecimal yearTravelExpensesFee = BigDecimal.ZERO;

    /**
     * 其他费用（本月）
     */
    private BigDecimal monthOtherFee = BigDecimal.ZERO;

    /**
     * 其他费用（本年）
     */
    private BigDecimal yearOtherFee = BigDecimal.ZERO;
    


/** ---------------------------------- 净利润 ------------------------------------------ */

    /**
     * 净利润（本月）
     */
    private BigDecimal monthNetProfitSum = BigDecimal.ZERO;

    /**
     * 净利润（本年）
     */
    private BigDecimal yearNetProfitSum = BigDecimal.ZERO;

    
    
    
}
