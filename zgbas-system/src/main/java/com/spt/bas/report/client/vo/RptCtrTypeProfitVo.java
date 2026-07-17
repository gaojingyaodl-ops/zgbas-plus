package com.spt.bas.report.client.vo;

import java.math.BigDecimal;

/**
 * 合同业务类型毛利率报表
 * @Author: gaojy
 * @create 2021/12/23 16:09
 * @version: 1.0
 * @description:
 */
public class RptCtrTypeProfitVo {

    /**
     * 部门ID
     */
    private Long deptId;

    /**
     * 部门
     */
    private String deptName;

    /**
     * 业务员ID
     */
    private Long matchUserId;

    /**
     * 业务员
     */
    private String matchUserName;

    /**
     * 业务类型Code businessType + businessTypeDcsx
     */
    private String businessCode;

    /**
     * 业务类型
     */
    private String businessName;

    /**
     * 销售额
     */
    private BigDecimal sellTotalAmount = BigDecimal.ZERO;

    /**
     * 采购额
     */
    private BigDecimal buyTotalAmount = BigDecimal.ZERO;

    /**
     * 毛利
     */
    private BigDecimal profit = BigDecimal.ZERO;

    /**
     * 毛利率
     */
    private BigDecimal profitRate = BigDecimal.ZERO;

    /**
     * 费用
     */
    private BigDecimal cost = BigDecimal.ZERO;

    /**
     * 净毛利
     */
    private BigDecimal margin = BigDecimal.ZERO;

    /**
     * 代采赊销资金成本
     */
    private BigDecimal dcsxCapitalCost = BigDecimal.ZERO;

    /**
     * 普通赊销资金成本
     */
    private BigDecimal capitalCost = BigDecimal.ZERO;

    /**
     * 净毛利率
     */
    private BigDecimal grossProfit = BigDecimal.ZERO;

    /**
     * 业务人数
     */
    private Integer businessUserCount;

    /**
     * 订单数
     */
    private Integer orderCount;

    /**
     * 吨数
     */
    private BigDecimal tonnes;

    /**
     * 销售额
     */
    private BigDecimal sellMoney;

    /**
     * 销售额人效
     */
    private BigDecimal sellLabor;

    /**
     * 毛利
     */
    private BigDecimal gross;

    /**
     * 毛利人效
     */
    private BigDecimal grossLabor;

    /**
     * 毛利率
     */
    private BigDecimal grossAvg;
    
    
    
    

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public Long getMatchUserId() {
        return matchUserId;
    }

    public void setMatchUserId(Long matchUserId) {
        this.matchUserId = matchUserId;
    }

    public String getMatchUserName() {
        return matchUserName;
    }

    public void setMatchUserName(String matchUserName) {
        this.matchUserName = matchUserName;
    }

    public String getBusinessCode() {
        return businessCode;
    }

    public void setBusinessCode(String businessCode) {
        this.businessCode = businessCode;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public BigDecimal getSellTotalAmount() {
        return sellTotalAmount;
    }

    public void setSellTotalAmount(BigDecimal sellTotalAmount) {
        this.sellTotalAmount = sellTotalAmount;
    }

    public BigDecimal getProfit() {
        return profit;
    }

    public void setProfit(BigDecimal profit) {
        this.profit = profit;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public BigDecimal getMargin() {
        return margin;
    }

    public void setMargin(BigDecimal margin) {
        this.margin = margin;
    }

    public BigDecimal getDcsxCapitalCost() {
        return dcsxCapitalCost;
    }

    public void setDcsxCapitalCost(BigDecimal dcsxCapitalCost) {
        this.dcsxCapitalCost = dcsxCapitalCost;
    }

    public BigDecimal getCapitalCost() {
        return capitalCost;
    }

    public void setCapitalCost(BigDecimal capitalCost) {
        this.capitalCost = capitalCost;
    }

    public BigDecimal getGrossProfit() {
        return grossProfit;
    }

    public void setGrossProfit(BigDecimal grossProfit) {
        this.grossProfit = grossProfit;
    }

    public Integer getBusinessUserCount() {
        return businessUserCount;
    }

    public void setBusinessUserCount(Integer businessUserCount) {
        this.businessUserCount = businessUserCount;
    }

    public Integer getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(Integer orderCount) {
        this.orderCount = orderCount;
    }

    public BigDecimal getTonnes() {
        return tonnes;
    }

    public void setTonnes(BigDecimal tonnes) {
        this.tonnes = tonnes;
    }

    public BigDecimal getSellMoney() {
        return sellMoney;
    }

    public void setSellMoney(BigDecimal sellMoney) {
        this.sellMoney = sellMoney;
    }

    public BigDecimal getSellLabor() {
        return sellLabor;
    }

    public void setSellLabor(BigDecimal sellLabor) {
        this.sellLabor = sellLabor;
    }

    public BigDecimal getGross() {
        return gross;
    }

    public void setGross(BigDecimal gross) {
        this.gross = gross;
    }

    public BigDecimal getGrossLabor() {
        return grossLabor;
    }

    public void setGrossLabor(BigDecimal grossLabor) {
        this.grossLabor = grossLabor;
    }

    public BigDecimal getGrossAvg() {
        return grossAvg;
    }

    public void setGrossAvg(BigDecimal grossAvg) {
        this.grossAvg = grossAvg;
    }

    public BigDecimal getBuyTotalAmount() {
        return buyTotalAmount;
    }

    public void setBuyTotalAmount(BigDecimal buyTotalAmount) {
        this.buyTotalAmount = buyTotalAmount;
    }

    public BigDecimal getProfitRate() {
        return profitRate;
    }

    public void setProfitRate(BigDecimal profitRate) {
        this.profitRate = profitRate;
    }

    public RptCtrTypeProfitVo() {
    }

    public RptCtrTypeProfitVo(Long deptId, String deptName, Long matchUserId, String matchUserName, String businessCode, String businessName, BigDecimal sellTotalAmount, BigDecimal profit, BigDecimal cost, BigDecimal margin, BigDecimal grossProfit) {
        this.deptId = deptId;
        this.deptName = deptName;
        this.matchUserId = matchUserId;
        this.matchUserName = matchUserName;
        this.businessCode = businessCode;
        this.businessName = businessName;
        this.sellTotalAmount = sellTotalAmount;
        this.profit = profit;
        this.cost = cost;
        this.margin = margin;
        this.grossProfit = grossProfit;
    }

    @Override
    public String toString() {
        return "CtrProfitVo{" +
                "deptId=" + deptId +
                ", deptName='" + deptName + '\'' +
                ", matchUserId=" + matchUserId +
                ", matchUserName='" + matchUserName + '\'' +
                ", businessCode='" + businessCode + '\'' +
                ", businessName='" + businessName + '\'' +
                ", sellTotalAmount=" + sellTotalAmount +
                ", profit=" + profit +
                ", cost=" + cost +
                ", margin=" + margin +
                ", grossProfit=" + grossProfit +
                '}';
    }
}
