package com.spt.bas.client.vo.performance;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * @author MoonLight
 * @version 1.0
 * @description
 * @date 2026/1/26 11:26
 */
public class UserCommissionSummary {
    /**
     * 业务员ID
     */
    private Long userId;

    /**
     * 采购业务员提成
     */
    private BigDecimal buyMatchCommission = BigDecimal.ZERO;

    /**
     * 销售业务员提成
     */
    private BigDecimal sellMatchCommission = BigDecimal.ZERO;

    /**
     * 采购主管提成
     */
    private BigDecimal buyHeadCommission = BigDecimal.ZERO;

    /**
     * 销售主管提成
     */
    private BigDecimal sellHeadCommission = BigDecimal.ZERO;

    /**
     * 供应商资源负责人提成
     */
    private BigDecimal supplierManagerAmount = BigDecimal.ZERO;

    /**
     * 总提成
     */
    private BigDecimal totalCommission = BigDecimal.ZERO;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public BigDecimal getBuyMatchCommission() {
        return defaultNum(buyMatchCommission);
    }

    public void setBuyMatchCommission(BigDecimal buyMatchCommission) {
        this.buyMatchCommission = buyMatchCommission;
    }

    public BigDecimal getSellMatchCommission() {
        return defaultNum(sellMatchCommission);
    }

    public void setSellMatchCommission(BigDecimal sellMatchCommission) {
        this.sellMatchCommission = sellMatchCommission;
    }

    public BigDecimal getBuyHeadCommission() {
        return defaultNum(buyHeadCommission);
    }

    public void setBuyHeadCommission(BigDecimal buyHeadCommission) {
        this.buyHeadCommission = buyHeadCommission;
    }

    public BigDecimal getSellHeadCommission() {
        return defaultNum(sellHeadCommission);
    }

    public void setSellHeadCommission(BigDecimal sellHeadCommission) {
        this.sellHeadCommission = sellHeadCommission;
    }

    public BigDecimal getSupplierManagerAmount() {
        return defaultNum(supplierManagerAmount);
    }

    public void setSupplierManagerAmount(BigDecimal supplierManagerAmount) {
        this.supplierManagerAmount = supplierManagerAmount;
    }

    public BigDecimal getTotalCommission() {
        return defaultNum(totalCommission);
    }

    public void setTotalCommission(BigDecimal totalCommission) {
        this.totalCommission = totalCommission;
    }

    public UserCommissionSummary() {
    }

    private BigDecimal defaultNum(BigDecimal value) {
        return Objects.isNull(value) ? BigDecimal.ZERO : value;
    }

    public void calcTotal() {
        totalCommission = defaultNum(buyMatchCommission)
                .add(defaultNum(sellMatchCommission))
                .add(defaultNum(buyHeadCommission))
                .add(defaultNum(sellHeadCommission))
                .add(defaultNum(supplierManagerAmount));
    }
}
