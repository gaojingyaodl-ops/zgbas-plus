package com.spt.bas.client.entity;

import com.spt.tools.jpa.vo.IdEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * 事业部分成
 * @author MoonLight
 * @version 1.0
 * @description
 * @date 2026/2/28 14:12
 */
@Entity
@Table(name = "t_performance_commission_dept")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class PerformanceCommissionDept extends IdEntity {
    private static final long serialVersionUID = -1L;

    /**
     * 提成年月
     */
    private String performanceDate;

    /**
     * 部门负责人ID
     */
    private Long leaderUserId;

    /**
     * 部门负责人
     */
    private String leaderUserName;

    /**
     * 事业部简码
     */
    private String owningRegion;

    /**
     * 事业部净利
     */
    private BigDecimal deptNetProfit;

    /**
     * 事业部提成
     */
    private BigDecimal deptCommission;

    /**
     * 部门负责人人工成本
     */
    private BigDecimal leaderLaborCost;

    /**
     * 部门助理人工成本
     */
    private BigDecimal assistantLaborCost;

    /**
     * 事业部管理成本
     */
    private BigDecimal deptManageCost;

    /**
     * 结存下月成本
     */
    private BigDecimal nextMonthBalance;

    /**
     *  剩余净利
     */
    private BigDecimal residueNetProfit;

    /**
     * 部门负责人分成
     */
    private BigDecimal leaderCommission;

    /**
     * 部门负责人当月结算
     */
    private BigDecimal leaderMonthCommission;

    /**
     * 部门负责人当年余额
     */
    private BigDecimal leaderYearCommission;

    public String getPerformanceDate() {
        return performanceDate;
    }

    public void setPerformanceDate(String performanceDate) {
        this.performanceDate = performanceDate;
    }

    public Long getLeaderUserId() {
        return leaderUserId;
    }

    public void setLeaderUserId(Long leaderUserId) {
        this.leaderUserId = leaderUserId;
    }

    public String getLeaderUserName() {
        return leaderUserName;
    }

    public void setLeaderUserName(String leaderUserName) {
        this.leaderUserName = leaderUserName;
    }

    public String getOwningRegion() {
        return owningRegion;
    }

    public void setOwningRegion(String owningRegion) {
        this.owningRegion = owningRegion;
    }

    public BigDecimal getDeptNetProfit() {
        return deptNetProfit;
    }

    public void setDeptNetProfit(BigDecimal deptNetProfit) {
        this.deptNetProfit = deptNetProfit;
    }

    public BigDecimal getDeptCommission() {
        return deptCommission;
    }

    public void setDeptCommission(BigDecimal deptCommission) {
        this.deptCommission = deptCommission;
    }

    public BigDecimal getLeaderLaborCost() {
        return leaderLaborCost;
    }

    public void setLeaderLaborCost(BigDecimal leaderLaborCost) {
        this.leaderLaborCost = leaderLaborCost;
    }

    public BigDecimal getAssistantLaborCost() {
        return assistantLaborCost;
    }

    public void setAssistantLaborCost(BigDecimal assistantLaborCost) {
        this.assistantLaborCost = assistantLaborCost;
    }

    public BigDecimal getDeptManageCost() {
        return deptManageCost;
    }

    public void setDeptManageCost(BigDecimal deptManageCost) {
        this.deptManageCost = deptManageCost;
    }

    public BigDecimal getNextMonthBalance() {
        return nextMonthBalance;
    }

    public void setNextMonthBalance(BigDecimal nextMonthBalance) {
        this.nextMonthBalance = nextMonthBalance;
    }

    public BigDecimal getResidueNetProfit() {
        return residueNetProfit;
    }

    public void setResidueNetProfit(BigDecimal residueNetProfit) {
        this.residueNetProfit = residueNetProfit;
    }

    public BigDecimal getLeaderCommission() {
        return leaderCommission;
    }

    public void setLeaderCommission(BigDecimal leaderCommission) {
        this.leaderCommission = leaderCommission;
    }

    public BigDecimal getLeaderMonthCommission() {
        return leaderMonthCommission;
    }

    public void setLeaderMonthCommission(BigDecimal leaderMonthCommission) {
        this.leaderMonthCommission = leaderMonthCommission;
    }

    public BigDecimal getLeaderYearCommission() {
        return leaderYearCommission;
    }

    public void setLeaderYearCommission(BigDecimal leaderYearCommission) {
        this.leaderYearCommission = leaderYearCommission;
    }
}
