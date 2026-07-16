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
 * 保费流水表
 */
@Entity
@Table(name = "t_insurance_amount_flow")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class InsuranceAmountFlow extends IdEntity {
    /**
     * 资金代采方ID
     */
    private Long fundCompanyId;
    /**
     * 资金方名称
     */
    @Transient
    private String fundCompanyName;

    /**
     * 合同id
     */
    private Long contractId;
    /**
     * 流水类型
     */
    private String flowType;
    /**
     * 流水金额
     */
    private BigDecimal flowAmount;
    /**
     * 期初金额
     */
    private BigDecimal initialAmount;
    /**
     * 期末金额
     */
    private BigDecimal ultimateAmount;
    /**
     * 流水摘要
     */
    private String subject;
    /**
     * 关联审批单ID
     */
    private Long linkApproveId;

    public Long getFundCompanyId() {
        return fundCompanyId;
    }

    public void setFundCompanyId(Long fundCompanyId) {
        this.fundCompanyId = fundCompanyId;
    }

    public String getFlowType() {
        return flowType;
    }

    public void setFlowType(String flowType) {
        this.flowType = flowType;
    }

    public BigDecimal getFlowAmount() {
        return flowAmount;
    }

    public void setFlowAmount(BigDecimal flowAmount) {
        this.flowAmount = flowAmount;
    }

    public BigDecimal getInitialAmount() {
        return initialAmount;
    }

    public void setInitialAmount(BigDecimal initialAmount) {
        this.initialAmount = initialAmount;
    }

    public BigDecimal getUltimateAmount() {
        return ultimateAmount;
    }

    public void setUltimateAmount(BigDecimal ultimateAmount) {
        this.ultimateAmount = ultimateAmount;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Long getLinkApproveId() {
        return linkApproveId;
    }

    public void setLinkApproveId(Long linkApproveId) {
        this.linkApproveId = linkApproveId;
    }

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }
    @Transient
    public String getFundCompanyName() {
        return fundCompanyName;
    }

    public void setFundCompanyName(String fundCompanyName) {
        this.fundCompanyName = fundCompanyName;
    }

}
