package com.spt.bas.report.client.vo;

import com.spt.tools.data.vo.DataEntity;

import java.math.BigDecimal;

/**
 * @author 杨英承
 * @version 1.0.0
 * @date 2022/4/26 09:54
 */

public class RptAssessmentResultVo extends DataEntity {
    /**
     * 业务员名字
     */
    private String userName;
    /**
     * 业务员id
     */
    private Long userId;

    /**
     * 部门
     */
    private String deptName;

    /**
     * 入职日期
     */
    private String entryDate;

    /**
     * 月份
     */
    private String createdDate;

    /**
     * 季度
     */
    private String quarter;

    /**
     * 年度
     */
    private String year;

    /**
     * 赊销金额
     */
    private BigDecimal sellMoney;

    /**
     * 月平均赊销额
     */
    private BigDecimal sellMoneyAverage;

    /**
     * 赊销利润
     */
    private BigDecimal sellMoneyProfit;

    /**
     * 月平均赊销利润
     */
    private BigDecimal sellMoneyProfitAverage;

    /**
     * 代采金额
     */
    private BigDecimal buyMoney;

    /**
     * 月平均代采额
     */
    private BigDecimal buyMoneyAverage;

    /**
     * 代采利润
     */
    private BigDecimal buyMoneyProfit;

    /**
     * 月平均代采利润
     */
    private BigDecimal buyMoneyProfitAverage;

    /**
     * 利润合计
     */
    private BigDecimal sumProfitMoney;

    /**
     * 月平均利润
     */
    private BigDecimal sumProfitMoneyAverage;


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(String entryDate) {
        this.entryDate = entryDate;
    }

    public String getQuarter() {
        return quarter;
    }

    public void setQuarter(String quarter) {
        this.quarter = quarter;
    }

    public BigDecimal getSellMoney() {
        return sellMoney;
    }

    public void setSellMoney(BigDecimal sellMoney) {
        this.sellMoney = sellMoney;
    }

    public BigDecimal getSellMoneyAverage() {
        return sellMoneyAverage;
    }

    public void setSellMoneyAverage(BigDecimal sellMoneyAverage) {
        this.sellMoneyAverage = sellMoneyAverage;
    }

    public BigDecimal getSellMoneyProfit() {
        return sellMoneyProfit;
    }

    public void setSellMoneyProfit(BigDecimal sellMoneyProfit) {
        this.sellMoneyProfit = sellMoneyProfit;
    }

    public BigDecimal getSellMoneyProfitAverage() {
        return sellMoneyProfitAverage;
    }

    public void setSellMoneyProfitAverage(BigDecimal sellMoneyProfitAverage) {
        this.sellMoneyProfitAverage = sellMoneyProfitAverage;
    }

    public BigDecimal getBuyMoney() {
        return buyMoney;
    }

    public void setBuyMoney(BigDecimal buyMoney) {
        this.buyMoney = buyMoney;
    }

    public BigDecimal getBuyMoneyAverage() {
        return buyMoneyAverage;
    }

    public void setBuyMoneyAverage(BigDecimal buyMoneyAverage) {
        this.buyMoneyAverage = buyMoneyAverage;
    }

    public BigDecimal getBuyMoneyProfit() {
        return buyMoneyProfit;
    }

    public void setBuyMoneyProfit(BigDecimal buyMoneyProfit) {
        this.buyMoneyProfit = buyMoneyProfit;
    }

    public BigDecimal getBuyMoneyProfitAverage() {
        return buyMoneyProfitAverage;
    }

    public void setBuyMoneyProfitAverage(BigDecimal buyMoneyProfitAverage) {
        this.buyMoneyProfitAverage = buyMoneyProfitAverage;
    }

    public BigDecimal getSumProfitMoney() {
        return sumProfitMoney;
    }

    public void setSumProfitMoney(BigDecimal sumProfitMoney) {
        this.sumProfitMoney = sumProfitMoney;
    }

    public BigDecimal getSumProfitMoneyAverage() {
        return sumProfitMoneyAverage;
    }

    public void setSumProfitMoneyAverage(BigDecimal sumProfitMoneyAverage) {
        this.sumProfitMoneyAverage = sumProfitMoneyAverage;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
