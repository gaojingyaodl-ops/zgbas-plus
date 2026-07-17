package com.spt.bas.report.client.vo;

import java.math.BigDecimal;

/**
 * @author 杨英承
 * @version 1.0.0
 * @date 2023/3/21 15:38
 */

public class RptUserRoiResultVo {

    private Long matchUserId;
    /**
     * 姓名
     */
    private String matchUserName;

    /**
     * 所属区域
     */
    private String branchName;
    /**
     * 所属区域cd
     */
    private String branchCd;

    /**
     * 年月
     */
    private String baseDate;

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

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getBaseDate() {
        return baseDate;
    }

    public void setBaseDate(String baseDate) {
        this.baseDate = baseDate;
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

    public String getBranchCd() {
        return branchCd;
    }

    public void setBranchCd(String branchCd) {
        this.branchCd = branchCd;
    }
}
