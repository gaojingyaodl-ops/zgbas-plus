package com.spt.bas.report.client.vo;

import java.math.BigDecimal;

/**
 * 合同毛利率报表
 * @Author: gaojy
 * @create 2021/12/23 16:09
 * @version: 1.0
 * @description:
 */
public class RptCtrProfitVo {

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
     * 净毛利率
     */
    private BigDecimal grossProfit = BigDecimal.ZERO;
    

    /**
     * 订单数
     */
    private Integer orderCount;

    /**
     * 吨数
     */
    private BigDecimal tunnage;

    /**
     * 销售额
     */
    private BigDecimal sellMoney;

    /**
     * 毛利
     */
    private BigDecimal gross;

    /**
     * 毛利率
     */
    private BigDecimal grossAvg;

    /**
     * 总投入
     */
    private BigDecimal totalFinancing;

    /**
     * 提成
     */
    private BigDecimal commission;

    /** 出差报销费用 */
    private BigDecimal evectionCost;

    /**
     * ROI
     */
    private BigDecimal roi;
    /**
     * 合计成本
     */
    private BigDecimal totalCost;
    
    
    
    

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

    public BigDecimal getGrossProfit() {
        return grossProfit;
    }

    public void setGrossProfit(BigDecimal grossProfit) {
        this.grossProfit = grossProfit;
    }

    public Integer getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(Integer orderCount) {
        this.orderCount = orderCount;
    }

    public BigDecimal getTunnage() {
        return tunnage;
    }

    public void setTunnage(BigDecimal tunnage) {
        this.tunnage = tunnage;
    }

    public BigDecimal getSellMoney() {
        return sellMoney;
    }

    public void setSellMoney(BigDecimal sellMoney) {
        this.sellMoney = sellMoney;
    }

    public BigDecimal getGross() {
        return gross;
    }

    public void setGross(BigDecimal gross) {
        this.gross = gross;
    }

    public BigDecimal getGrossAvg() {
        return grossAvg;
    }

    public void setGrossAvg(BigDecimal grossAvg) {
        this.grossAvg = grossAvg;
    }

    public BigDecimal getTotalFinancing() {
        return totalFinancing;
    }

    public void setTotalFinancing(BigDecimal totalFinancing) {
        this.totalFinancing = totalFinancing;
    }

    public BigDecimal getCommission() {
        return commission;
    }

    public void setCommission(BigDecimal commission) {
        this.commission = commission;
    }

    public BigDecimal getEvectionCost() {
        return evectionCost;
    }

    public void setEvectionCost(BigDecimal evectionCost) {
        this.evectionCost = evectionCost;
    }

    public BigDecimal getRoi() {
        return roi;
    }

    public void setRoi(BigDecimal roi) {
        this.roi = roi;
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

    public RptCtrProfitVo() {
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    public RptCtrProfitVo(Long deptId, String deptName, Long matchUserId, String matchUserName, String businessCode, String businessName, BigDecimal sellTotalAmount, BigDecimal profit, BigDecimal cost, BigDecimal margin, BigDecimal grossProfit) {
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
