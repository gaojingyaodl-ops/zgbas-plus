package com.spt.bas.client.entity;

import com.spt.tools.core.annotation.LogEntityName;
import com.spt.tools.jpa.vo.IdEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * <p>
 *
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2021-05-10 13:28
 */
@Entity
@Table(name = "t_kpi_actual")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@DynamicInsert
@DynamicUpdate
@LogEntityName("kpi_actual")
public class KpiActual extends IdEntity {

    private Long kpiId;
    /**
     * 实现赊销交易额
     */
    private BigDecimal iousTradeAmount;

    /**
     * 实现赊销交易利润
     */
    private BigDecimal iousMarginAmount;

    /**
     * 实现代采交易额
     */
    private BigDecimal matchTradeAmount;

    /**
     * 实现代采交易利润
     */
    private BigDecimal matchMarginAmount;

    /**
     * 实现新增授信
     */
    private BigDecimal totalCreditAmount;

    /**
     * 实现目标新增有效用户（白名单企业）
     */
    private BigDecimal companyNumber;

    public Long getKpiId() {
        return kpiId;
    }

    public void setKpiId(Long kpiId) {
        this.kpiId = kpiId;
    }

    public BigDecimal getIousTradeAmount() {
        return iousTradeAmount;
    }

    public void setIousTradeAmount(BigDecimal iousTradeAmount) {
        this.iousTradeAmount = iousTradeAmount;
    }

    public BigDecimal getIousMarginAmount() {
        return iousMarginAmount;
    }

    public void setIousMarginAmount(BigDecimal iousMarginAmount) {
        this.iousMarginAmount = iousMarginAmount;
    }

    public BigDecimal getMatchTradeAmount() {
        return matchTradeAmount;
    }

    public void setMatchTradeAmount(BigDecimal matchTradeAmount) {
        this.matchTradeAmount = matchTradeAmount;
    }

    public BigDecimal getMatchMarginAmount() {
        return matchMarginAmount;
    }

    public void setMatchMarginAmount(BigDecimal matchMarginAmount) {
        this.matchMarginAmount = matchMarginAmount;
    }

    public BigDecimal getTotalCreditAmount() {
        return totalCreditAmount;
    }

    public void setTotalCreditAmount(BigDecimal totalCreditAmount) {
        this.totalCreditAmount = totalCreditAmount;
    }

    public BigDecimal getCompanyNumber() {
        return companyNumber;
    }

    public void setCompanyNumber(BigDecimal companyNumber) {
        this.companyNumber = companyNumber;
    }
}
