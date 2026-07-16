package com.spt.bas.client.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spt.tools.jpa.vo.IdEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 企业授信额度表
 */
@Entity
@Table(name = "t_bs_company_credit")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class BsCompanyCredit extends IdEntity {

    private static final long serialVersionUID = 4608215396695183702L;

    /**
     * 企业ID
     */
    private Long companyId;

    /**
     * 企业账套ID
     */
    private Long enterpriseId;

    /**
     * 授信类别
     * 0-人保
     * 1-大地
     * 2-中银
     * 9-自主
     */
    private String creditType;

    /**
     * 授信额度
     */
    private BigDecimal creditAmount;

    /**
     * 风控额度
     */
    private BigDecimal riskAmount;

    /**
     * 已用额度
     */
    private BigDecimal usedCreditAmount;

    /**
     * 可用额度（授信额度-已用额度）
     */
    @Transient
    private BigDecimal availableCreditAmount;

    /**
     * 额度生效日
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date effectiveDate;

    /**
     * 额度失效日
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date expiryDate;

    /**
     * 临时额度
     */
    private BigDecimal temporaryAmount;

    /**
     * 临时额度失效日
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date temporaryExpiryDate;

    /**
     * 赔偿比例（%）
     */
    private BigDecimal compensationRatio;

    /**
     * 是否有效
     */
    private Boolean enableFlg;

    /**
     * 创建人Id
     */
    private Long createdUserId;

    /**
     * 创建人姓名
     */
    private String createdUserName;

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public Long getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(Long enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public String getCreditType() {
        return creditType;
    }

    public void setCreditType(String creditType) {
        this.creditType = creditType;
    }

    private BigDecimal defaultNum(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    public BigDecimal getCreditAmount() {
        return defaultNum(creditAmount);
    }

    public void setCreditAmount(BigDecimal creditAmount) {
        this.creditAmount = creditAmount;
    }

    public BigDecimal getRiskAmount() {
        return riskAmount;
    }

    public void setRiskAmount(BigDecimal riskAmount) {
        this.riskAmount = riskAmount;
    }

    public BigDecimal getUsedCreditAmount() {
        return defaultNum(usedCreditAmount);
    }

    public void setUsedCreditAmount(BigDecimal usedCreditAmount) {
        this.usedCreditAmount = usedCreditAmount;
    }

    @Transient
    public BigDecimal getAvailableCreditAmount() {
        return defaultNum(availableCreditAmount);
    }

    public void setAvailableCreditAmount(BigDecimal availableCreditAmount) {
        this.availableCreditAmount = availableCreditAmount;
    }

    public Date getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public BigDecimal getTemporaryAmount() {
        return defaultNum(temporaryAmount);
    }

    public void setTemporaryAmount(BigDecimal temporaryAmount) {
        this.temporaryAmount = temporaryAmount;
    }

    public Date getTemporaryExpiryDate() {
        return temporaryExpiryDate;
    }

    public void setTemporaryExpiryDate(Date temporaryExpiryDate) {
        this.temporaryExpiryDate = temporaryExpiryDate;
    }

    public Boolean getEnableFlg() {
        return enableFlg;
    }

    public void setEnableFlg(Boolean enableFlg) {
        this.enableFlg = enableFlg;
    }

    public Long getCreatedUserId() {
        return createdUserId;
    }

    public void setCreatedUserId(Long createdUserId) {
        this.createdUserId = createdUserId;
    }

    public String getCreatedUserName() {
        return createdUserName;
    }

    public void setCreatedUserName(String createdUserName) {
        this.createdUserName = createdUserName;
    }

    public BigDecimal getCompensationRatio() {
        return compensationRatio;
    }

    public void setCompensationRatio(BigDecimal compensationRatio) {
        this.compensationRatio = compensationRatio;
    }
}
