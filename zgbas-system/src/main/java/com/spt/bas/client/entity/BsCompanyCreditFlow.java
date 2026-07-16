package com.spt.bas.client.entity;

import com.spt.tools.jpa.vo.IdEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * 授信流水表
 */
@Entity
@Table(name = "t_bs_company_credit_flow")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class BsCompanyCreditFlow extends IdEntity {

    private static final long serialVersionUID = 4596964844471288697L;

    /**
     * 企业ID
     */
    private Long companyId;

    /**
     * 企业名称
     */
    private String companyName;

    /**
     * 企业授信ID
     */
    private Long companyCreditId;

    /**
     * 额度类型
     * 0-人保 1-大地 9-自主
     */
    private String creditType;

    /**
     * 流水类型
     */
    private String flowType;

    /**
     * 流水名称
     */
    private String flowName;

    /**
     * 审批单ID
     */
    private Long approveId;

    /**
     * 审批编号
     */
    private String approveNo;

    /**
     * 更新前-已使用授信额度
     */
    private BigDecimal beforeUsedCreditAmount;

    /**
     * 更新后-已使用授信额度
     */
    private BigDecimal afterUsedCreditAmount;

    /**
     * 更新前-审批占用额度
     */
    private BigDecimal beforeApproveCreditAmount;

    /**
     * 更新后-审批占用额度
     */
    private BigDecimal afterApproveCreditAmount;

    /**
     * 使用流水金额
     */
    private BigDecimal usedFlowAmount = BigDecimal.ZERO;

    /**
     * 审批流水金额
     */
    private BigDecimal approveFlowAmount = BigDecimal.ZERO;

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getFlowType() {
        return flowType;
    }

    public void setFlowType(String flowType) {
        this.flowType = flowType;
    }

    public String getApproveNo() {
        return approveNo;
    }

    public void setApproveNo(String approveNo) {
        this.approveNo = approveNo;
    }

    public BigDecimal getBeforeUsedCreditAmount() {
        return beforeUsedCreditAmount;
    }

    public void setBeforeUsedCreditAmount(BigDecimal beforeUsedCreditAmount) {
        this.beforeUsedCreditAmount = beforeUsedCreditAmount;
    }

    public BigDecimal getAfterUsedCreditAmount() {
        return afterUsedCreditAmount;
    }

    public void setAfterUsedCreditAmount(BigDecimal afterUsedCreditAmount) {
        this.afterUsedCreditAmount = afterUsedCreditAmount;
    }

    public BigDecimal getBeforeApproveCreditAmount() {
        return beforeApproveCreditAmount;
    }

    public void setBeforeApproveCreditAmount(BigDecimal beforeApproveCreditAmount) {
        this.beforeApproveCreditAmount = beforeApproveCreditAmount;
    }

    public BigDecimal getAfterApproveCreditAmount() {
        return afterApproveCreditAmount;
    }

    public void setAfterApproveCreditAmount(BigDecimal afterApproveCreditAmount) {
        this.afterApproveCreditAmount = afterApproveCreditAmount;
    }

    public BigDecimal getUsedFlowAmount() {
        return usedFlowAmount;
    }

    public void setUsedFlowAmount(BigDecimal usedFlowAmount) {
        this.usedFlowAmount = usedFlowAmount;
    }

    public BigDecimal getApproveFlowAmount() {
        return approveFlowAmount;
    }

    public void setApproveFlowAmount(BigDecimal approveFlowAmount) {
        this.approveFlowAmount = approveFlowAmount;
    }

    public String getFlowName() {
        return flowName;
    }

    public void setFlowName(String flowName) {
        this.flowName = flowName;
    }

    public Long getCompanyCreditId() {
        return companyCreditId;
    }

    public void setCompanyCreditId(Long companyCreditId) {
        this.companyCreditId = companyCreditId;
    }

    public String getCreditType() {
        return creditType;
    }

    public void setCreditType(String creditType) {
        this.creditType = creditType;
    }

    public Long getApproveId() {
        return approveId;
    }

    public void setApproveId(Long approveId) {
        this.approveId = approveId;
    }

    public BsCompanyCreditFlow() {
    }


}
