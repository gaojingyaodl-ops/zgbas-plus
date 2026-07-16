package com.spt.bas.client.vo;

import java.math.BigDecimal;
public class BsCompanyPiccRequestVo {
    private Long companyId;
    /**
     * 人保已用金额 实际使用额度
     */
    private BigDecimal piccHaveusedAmount = BigDecimal.ZERO;
    /**
     * 人保赊销剩余额度
     */
    private BigDecimal piccUseAbleaMount = BigDecimal.ZERO;
    /**
     * 风控审批额度 
     */
    private BigDecimal applyCreditAmount = BigDecimal.ZERO;
    /**
     * 人保批复额度
     */
    private BigDecimal piccCreditAmount;
    /**
     * 人保批复账期
     */
    private Integer piccApprovalPeriod;
    /**
     * 基础赊销额度
     */
    private BigDecimal baseQuota;
    private Long creditDays;            //账期
    /**
     * 保险申报状态
     */
    private String applyInsuranceStatus;

    /**
     * 是否人保
     */
    private Boolean piccFlg;

    public Boolean getPiccFlg() {
        return piccFlg;
    }

    public void setPiccFlg(Boolean piccFlg) {
        this.piccFlg = piccFlg;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public BigDecimal getPiccHaveusedAmount() {
        return piccHaveusedAmount;
    }

    public void setPiccHaveusedAmount(BigDecimal piccHaveusedAmount) {
        this.piccHaveusedAmount = piccHaveusedAmount;
    }

    public BigDecimal getPiccUseAbleaMount() {
        return piccUseAbleaMount;
    }

    public void setPiccUseAbleaMount(BigDecimal piccUseAbleaMount) {
        this.piccUseAbleaMount = piccUseAbleaMount;
    }

    public BigDecimal getApplyCreditAmount() {
        return applyCreditAmount;
    }

    public void setApplyCreditAmount(BigDecimal applyCreditAmount) {
        this.applyCreditAmount = applyCreditAmount;
    }

    public BigDecimal getPiccCreditAmount() {
        return piccCreditAmount;
    }

    public void setPiccCreditAmount(BigDecimal piccCreditAmount) {
        this.piccCreditAmount = piccCreditAmount;
    }

    public Integer getPiccApprovalPeriod() {
        return piccApprovalPeriod;
    }

    public void setPiccApprovalPeriod(Integer piccApprovalPeriod) {
        this.piccApprovalPeriod = piccApprovalPeriod;
    }

    public BigDecimal getBaseQuota() {
        return baseQuota;
    }

    public void setBaseQuota(BigDecimal baseQuota) {
        this.baseQuota = baseQuota;
    }

    public Long getCreditDays() {
        return creditDays;
    }

    public void setCreditDays(Long creditDays) {
        this.creditDays = creditDays;
    }

    public String getApplyInsuranceStatus() {
        return applyInsuranceStatus;
    }

    public void setApplyInsuranceStatus(String applyInsuranceStatus) {
        this.applyInsuranceStatus = applyInsuranceStatus;
    }
}
