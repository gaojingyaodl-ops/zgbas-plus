package com.spt.bas.client.entity;

import com.spt.tools.jpa.vo.IdEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * 特户计划企业配置表
 *
 * @Author: gaojy
 * @create 2022/4/2 10:23
 * @version: 1.0
 * @description:
 */
@Entity
@Table(name = "t_bs_company_config")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class BsCompanyConfig extends IdEntity {
    private static final long serialVersionUID = -5646166232622015558L;

    /**
     * 关联企业ID
     */
    private Long bsCompanyId;

    /**
     * 企业名称
     */
    private String bsCompanyName;

    /**
     * 匹配用户ID
     */
    private Long matchUserId;

    /**
     * 匹配用户
     */
    private String matchUserName;

    /**
     * 盈利费率
     */
    private BigDecimal profitRate;

    /**
     * 资金服务费率
     */
    private BigDecimal serviceRate;

    /**
     * 保险费率
     */
    private BigDecimal insuranceRate;

    /**
     * 逾期罚金费率
     */
    private BigDecimal overdueRate;


    /**
     * 销售业务员提成比例
     */
    private BigDecimal sellCommissionRate;

    /**
     * 企业账套ID
     */
    private Long enterpriseId;

    public Long getBsCompanyId() {
        return bsCompanyId;
    }

    public void setBsCompanyId(Long bsCompanyId) {
        this.bsCompanyId = bsCompanyId;
    }

    public String getBsCompanyName() {
        return bsCompanyName;
    }

    public void setBsCompanyName(String bsCompanyName) {
        this.bsCompanyName = bsCompanyName;
    }

    public BigDecimal getServiceRate() {
        return serviceRate;
    }

    public void setServiceRate(BigDecimal serviceRate) {
        this.serviceRate = serviceRate;
    }

    public BigDecimal getInsuranceRate() {
        return insuranceRate;
    }

    public void setInsuranceRate(BigDecimal insuranceRate) {
        this.insuranceRate = insuranceRate;
    }

    public BigDecimal getOverdueRate() {
        return overdueRate;
    }

    public void setOverdueRate(BigDecimal overdueRate) {
        this.overdueRate = overdueRate;
    }

    public Long getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(Long enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public BigDecimal getSellCommissionRate() {
        return sellCommissionRate;
    }

    public void setSellCommissionRate(BigDecimal sellCommissionRate) {
        this.sellCommissionRate = sellCommissionRate;
    }

    public Long getMatchUserId() {
        return matchUserId;
    }

    public void setMatchUserId(Long matchUserId) {
        this.matchUserId = matchUserId;
    }

    public BigDecimal getProfitRate() {
        return profitRate;
    }

    public void setProfitRate(BigDecimal profitRate) {
        this.profitRate = profitRate;
    }

    public String getMatchUserName() {
        return matchUserName;
    }

    public void setMatchUserName(String matchUserName) {
        this.matchUserName = matchUserName;
    }
}
