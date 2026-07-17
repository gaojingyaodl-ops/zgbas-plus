package com.spt.bas.report.client.vo;

import java.math.BigDecimal;

/**
 * @author 杨英承
 * @version 1.0.0
 * @date 2023/3/21 15:38
 */

public class RptSummaryRoiResultVo {

    /**
     * 业务类型名称
     */
    private String businessTypeName;

    /**
     * 所属区域
     */
    private String branchName;

    /**
     * 所属区域CD
     */
    private String branchCd;

    /**
     * 年月
     */
    private String baseDate;

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

    public BigDecimal getTonnes() {
        return tonnes;
    }

    public void setTonnes(BigDecimal tonnes) {
        this.tonnes = tonnes;
    }

    public String getBusinessTypeName() {
        return businessTypeName;
    }

    public void setBusinessTypeName(String businessTypeName) {
        this.businessTypeName = businessTypeName;
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

    public String getBranchCd() {
        return branchCd;
    }

    public void setBranchCd(String branchCd) {
        this.branchCd = branchCd;
    }
}
