package com.spt.bas.client.vo;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author MoonLight
 * @Date 2023/10/31 16:47
 * @Version 1.0
 */
public class OverdueInterestDto {
    /**
     * 计算明细
     */
    private List<OverdueInterestVo> interestList;

    /**
     * 中游付款总额
     */
    private BigDecimal payTotalAmount = BigDecimal.ZERO;

    /**
     * 下游收款总额
     */
    private BigDecimal receiveTotalAmount = BigDecimal.ZERO;

    /**
     * 资金成本合计
     */
    private BigDecimal costCapital = BigDecimal.ZERO;

    /**
     * 中游销售利润
     */
    private BigDecimal profit = BigDecimal.ZERO;

    /**
     * 中游应收利息
     */
    private BigDecimal overdueInterest = BigDecimal.ZERO;

    /**
     * 已收下游逾期罚息
     */
    private BigDecimal receiveBreachAmount = BigDecimal.ZERO;

    /**
     * 应付逾期罚息
     */
    private BigDecimal needPayInterestAmount = BigDecimal.ZERO;

    /**
     * 年化收益
     */
    private BigDecimal annualizedRevenueRate;

    public List<OverdueInterestVo> getInterestList() {
        return interestList;
    }

    public void setInterestList(List<OverdueInterestVo> interestList) {
        this.interestList = interestList;
    }

    public BigDecimal getPayTotalAmount() {
        return payTotalAmount;
    }

    public void setPayTotalAmount(BigDecimal payTotalAmount) {
        this.payTotalAmount = payTotalAmount;
    }

    public BigDecimal getReceiveTotalAmount() {
        return receiveTotalAmount;
    }

    public void setReceiveTotalAmount(BigDecimal receiveTotalAmount) {
        this.receiveTotalAmount = receiveTotalAmount;
    }

    public BigDecimal getCostCapital() {
        return costCapital;
    }

    public void setCostCapital(BigDecimal costCapital) {
        this.costCapital = costCapital;
    }

    public BigDecimal getProfit() {
        return profit;
    }

    public void setProfit(BigDecimal profit) {
        this.profit = profit;
    }

    public BigDecimal getOverdueInterest() {
        return overdueInterest;
    }

    public void setOverdueInterest(BigDecimal overdueInterest) {
        this.overdueInterest = overdueInterest;
    }

    public BigDecimal getReceiveBreachAmount() {
        return receiveBreachAmount;
    }

    public void setReceiveBreachAmount(BigDecimal receiveBreachAmount) {
        this.receiveBreachAmount = receiveBreachAmount;
    }

    public BigDecimal getNeedPayInterestAmount() {
        return needPayInterestAmount;
    }

    public void setNeedPayInterestAmount(BigDecimal needPayInterestAmount) {
        this.needPayInterestAmount = needPayInterestAmount;
    }

    public BigDecimal getAnnualizedRevenueRate() {
        return annualizedRevenueRate;
    }

    public void setAnnualizedRevenueRate(BigDecimal annualizedRevenueRate) {
        this.annualizedRevenueRate = annualizedRevenueRate;
    }
}
