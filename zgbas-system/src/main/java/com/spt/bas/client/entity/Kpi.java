package com.spt.bas.client.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
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
@Table(name = "t_kpi")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@DynamicInsert
@DynamicUpdate
@LogEntityName("kpi")
public class Kpi extends IdEntity {
    /**
     * 目标赊销交易额
     */
    private BigDecimal iousTradeAmount;

    /**
     * 目标赊销交易利润
     */
    private BigDecimal iousMarginAmount;

    /**
     * 目标代采交易额
     */
    private BigDecimal matchTradeAmount;

    /**
     * 目标代采交易利润
     */
    private BigDecimal matchMarginAmount;

    /**
     * 目标新增授信
     */
    private BigDecimal totalCreditAmount;

    /**
     * 目标新增有效用户（白名单企业）
     */
    private BigDecimal companyNumber;

    /**
     * 生效与否，默认为不生效
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Boolean effectFlg;

    /**
     * year
     */
    private Integer year;

    /**
     * month
     */
    private Integer month;

    /**
     * 0：集团公司；1：事业部；2：业务部；3：业务员。默认为0
     */
    private String matchType;

    /**
     * matchType
     * 0时，matchUserId不用填；matchType
     * 1时，matchUserId填写事业部Id；matchType
     * 2时，matchUserId填写业务部Id；matchType
     * 3时，matchUserId填写业务员Id
     */
    private Long matchUserId;

    //======
    /**
     * 已实现赊销交易额
     */
    private BigDecimal iousTradeAmountAc;

    /**
     * 已实现赊销交易利润
     */
    private BigDecimal iousMarginAmountAc;

    /**
     * 已实现代采交易额
     */
    private BigDecimal matchTradeAmountAc;

    /**
     * 已实现代采交易利润
     */
    private BigDecimal matchMarginAmountAc;

    /**
     * 已实现新增授信
     */
    private BigDecimal totalCreditAmountAc;

    /**
     * 已实现新增有效用户（白名单企业）
     */
    private BigDecimal companyNumberAc;

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

    public Boolean getEffectFlg() {
        return effectFlg;
    }

    public void setEffectFlg(Boolean effectFlg) {
        this.effectFlg = effectFlg;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public String getMatchType() {
        return matchType;
    }

    public void setMatchType(String matchType) {
        this.matchType = matchType;
    }

    public Long getMatchUserId() {
        return matchUserId;
    }

    public void setMatchUserId(Long matchUserId) {
        this.matchUserId = matchUserId;
    }

    public BigDecimal getIousTradeAmountAc() {
        return iousTradeAmountAc;
    }

    public void setIousTradeAmountAc(BigDecimal iousTradeAmountAc) {
        this.iousTradeAmountAc = iousTradeAmountAc;
    }

    public BigDecimal getIousMarginAmountAc() {
        return iousMarginAmountAc;
    }

    public void setIousMarginAmountAc(BigDecimal iousMarginAmountAc) {
        this.iousMarginAmountAc = iousMarginAmountAc;
    }

    public BigDecimal getMatchTradeAmountAc() {
        return matchTradeAmountAc;
    }

    public void setMatchTradeAmountAc(BigDecimal matchTradeAmountAc) {
        this.matchTradeAmountAc = matchTradeAmountAc;
    }

    public BigDecimal getMatchMarginAmountAc() {
        return matchMarginAmountAc;
    }

    public void setMatchMarginAmountAc(BigDecimal matchMarginAmountAc) {
        this.matchMarginAmountAc = matchMarginAmountAc;
    }

    public BigDecimal getTotalCreditAmountAc() {
        return totalCreditAmountAc;
    }

    public void setTotalCreditAmountAc(BigDecimal totalCreditAmountAc) {
        this.totalCreditAmountAc = totalCreditAmountAc;
    }

    public BigDecimal getCompanyNumberAc() {
        return companyNumberAc;
    }

    public void setCompanyNumberAc(BigDecimal companyNumberAc) {
        this.companyNumberAc = companyNumberAc;
    }
}
