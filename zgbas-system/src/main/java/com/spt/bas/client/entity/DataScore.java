package com.spt.bas.client.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spt.tools.jpa.vo.IdEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 *
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2021-01-20 09:44
 */
@Entity
@Table(name = "t_data_score")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@DynamicUpdate
@DynamicInsert
public class DataScore extends IdEntity {
    private Long companyId;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date basicAbilityCreateDate;

    private BigDecimal basicAbilityScore;

    private BigDecimal riskScore;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date riskCreateDate;

    private BigDecimal investigateScore;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date investigateCreateDate;

    private BigDecimal historyTradeScore;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date historyTradeCreateDate;

    private BigDecimal activeTradeScore;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date activeTradeCreateDate;

    private BigDecimal increaseScore;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date increaseCreateDate;

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public Date getBasicAbilityCreateDate() {
        return basicAbilityCreateDate;
    }

    public void setBasicAbilityCreateDate(Date basicAbilityCreateDate) {
        this.basicAbilityCreateDate = basicAbilityCreateDate;
    }

    public BigDecimal getBasicAbilityScore() {
        return basicAbilityScore;
    }

    public void setBasicAbilityScore(BigDecimal basicAbilityScore) {
        this.basicAbilityScore = basicAbilityScore;
    }

    public BigDecimal getRiskScore() {
        return riskScore;
    }

    public void setRiskScore(BigDecimal riskScore) {
        this.riskScore = riskScore;
    }

    public Date getRiskCreateDate() {
        return riskCreateDate;
    }

    public void setRiskCreateDate(Date riskCreateDate) {
        this.riskCreateDate = riskCreateDate;
    }

    public BigDecimal getInvestigateScore() {
        return investigateScore;
    }

    public void setInvestigateScore(BigDecimal investigateScore) {
        this.investigateScore = investigateScore;
    }

    public Date getInvestigateCreateDate() {
        return investigateCreateDate;
    }

    public void setInvestigateCreateDate(Date investigateCreateDate) {
        this.investigateCreateDate = investigateCreateDate;
    }

    public BigDecimal getHistoryTradeScore() {
        return historyTradeScore;
    }

    public void setHistoryTradeScore(BigDecimal historyTradeScore) {
        this.historyTradeScore = historyTradeScore;
    }

    public Date getHistoryTradeCreateDate() {
        return historyTradeCreateDate;
    }

    public void setHistoryTradeCreateDate(Date historyTradeCreateDate) {
        this.historyTradeCreateDate = historyTradeCreateDate;
    }

    public BigDecimal getActiveTradeScore() {
        return activeTradeScore;
    }

    public void setActiveTradeScore(BigDecimal activeTradeScore) {
        this.activeTradeScore = activeTradeScore;
    }

    public Date getActiveTradeCreateDate() {
        return activeTradeCreateDate;
    }

    public void setActiveTradeCreateDate(Date activeTradeCreateDate) {
        this.activeTradeCreateDate = activeTradeCreateDate;
    }

    public BigDecimal getIncreaseScore() {
        return increaseScore;
    }

    public void setIncreaseScore(BigDecimal increaseScore) {
        this.increaseScore = increaseScore;
    }

    public Date getIncreaseCreateDate() {
        return increaseCreateDate;
    }

    public void setIncreaseCreateDate(Date increaseCreateDate) {
        this.increaseCreateDate = increaseCreateDate;
    }
}
