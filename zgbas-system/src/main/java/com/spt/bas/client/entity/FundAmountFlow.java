package com.spt.bas.client.entity;

import com.spt.tools.jpa.vo.IdEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * 资金方充值流水记录
 * @Author MoonLight
 * @Date 2024/7/12 16:16
 * @Version 1.0
 */
@Entity
@Table(name = "t_fund_amount_flow")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class FundAmountFlow extends IdEntity {
    /**
     * 资金代采方ID
     */
    private Long fundCompanyId;

    private String ourCompanyName;
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
     * 关联审批单ID
     */
    private Long linkApproveId;

    /**
     * 流水摘要
     */
    private String subject;

    public Long getFundCompanyId() {
        return fundCompanyId;
    }

    public void setFundCompanyId(Long fundCompanyId) {
        this.fundCompanyId = fundCompanyId;
    }

    public String getOurCompanyName() {
        return ourCompanyName;
    }

    public void setOurCompanyName(String ourCompanyName) {
        this.ourCompanyName = ourCompanyName;
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

    public Long getLinkApproveId() {
        return linkApproveId;
    }

    public void setLinkApproveId(Long linkApproveId) {
        this.linkApproveId = linkApproveId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}
