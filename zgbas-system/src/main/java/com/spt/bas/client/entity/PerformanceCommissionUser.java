package com.spt.bas.client.entity;

import com.spt.tools.jpa.vo.IdEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * 业务员业绩提成表
 * @author MoonLight
 * @version 1.0
 * @description
 * @date 2026/1/16 15:12
 */
@Entity
@Table(name = "t_performance_commission_user")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class PerformanceCommissionUser extends IdEntity {
    private static final long serialVersionUID = -1L;

    /**
     * 提成年月
     */
    private String performanceDate;

    /**
     * 业务员ID
     */
    private Long userId;


    /**
     * 业务员名称
     */
    private String userName;

    /**
     * 部门负责人ID
     */
    private Long leaderUserId;

    /**
     * 部门负责人
     */
    private String leaderUserName;

    /**
     * 事业部ID
     */
    private Long deptId;

    /**
     * 事业部简码
     */
    private String owningRegion;

    /**
     * 毛利
     */
    private BigDecimal grossMargin;

    /**
     * 人工成本
     */
    private BigDecimal laborCost;

    /**
     * 差旅成本
     */
    private BigDecimal travelCost;

    /**
     * 管理成本
     */
    private BigDecimal manageCost = BigDecimal.valueOf(5000L);

    /**
     * 保险成本
     */
    private BigDecimal insuranceCost;

    /**
     * 净利润
     */
    private BigDecimal netProfit;

    /**
     * 提成
     */
    private BigDecimal commission;

    /**
     * 月度提成
     */
    private BigDecimal monthCommission;

    /**
     * 年度提成
     */
    private BigDecimal yearCommission;

    public String getPerformanceDate() {
        return performanceDate;
    }

    public void setPerformanceDate(String performanceData) {
        this.performanceDate = performanceData;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public String getOwningRegion() {
        return owningRegion;
    }

    public void setOwningRegion(String owningRegion) {
        this.owningRegion = owningRegion;
    }

    public BigDecimal getGrossMargin() {
        return grossMargin;
    }

    public void setGrossMargin(BigDecimal grossMargin) {
        this.grossMargin = grossMargin;
    }

    public BigDecimal getLaborCost() {
        return laborCost;
    }

    public void setLaborCost(BigDecimal laborCost) {
        this.laborCost = laborCost;
    }

    public BigDecimal getTravelCost() {
        return travelCost;
    }

    public void setTravelCost(BigDecimal travelCost) {
        this.travelCost = travelCost;
    }

    public BigDecimal getManageCost() {
        return manageCost;
    }

    public void setManageCost(BigDecimal manageCost) {
        this.manageCost = manageCost;
    }

    public BigDecimal getInsuranceCost() {
        return insuranceCost;
    }

    public void setInsuranceCost(BigDecimal insuranceCost) {
        this.insuranceCost = insuranceCost;
    }

    public BigDecimal getNetProfit() {
        return netProfit;
    }

    public void setNetProfit(BigDecimal netProfit) {
        this.netProfit = netProfit;
    }

    public BigDecimal getCommission() {
        return commission;
    }

    public void setCommission(BigDecimal commission) {
        this.commission = commission;
    }

    public BigDecimal getMonthCommission() {
        return monthCommission;
    }

    public void setMonthCommission(BigDecimal monthCommission) {
        this.monthCommission = monthCommission;
    }

    public BigDecimal getYearCommission() {
        return yearCommission;
    }

    public void setYearCommission(BigDecimal yearCommission) {
        this.yearCommission = yearCommission;
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
}
