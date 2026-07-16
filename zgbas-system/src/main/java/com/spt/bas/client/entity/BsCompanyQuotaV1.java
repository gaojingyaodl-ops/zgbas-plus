package com.spt.bas.client.entity;

import com.spt.pm.inter.IPmEntity;
import com.spt.tools.jpa.vo.IdEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * 公司额度申请表
 *
 */
@Entity
@Table(name = "t_bs_company_quota_v1")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class BsCompanyQuotaV1 extends IdEntity implements IPmEntity {
    private static final long serialVersionUID = 8946275027971370707L;
    /**
     * 企业ID
     */
    private Long companyId;
    /**
     * 公司名称
     */
    private String companyName;
    /**
     * 审批ID
     */
    private Long approveId;
    /**
     * 申请的白条额度
     */
    private BigDecimal totalCreditAmount = BigDecimal.ZERO;
    /**
     * 风控审批额度 
     */
    private BigDecimal applyCreditAmount = BigDecimal.ZERO;
    /**
     * 申请的代采现货额度
     */
    private BigDecimal totalSpotAmount = BigDecimal.ZERO;
    /**
     * 申请的代采期货额度
     */
    private BigDecimal totalFuturesAmount = BigDecimal.ZERO;
    /**
     * 企业账套ID
     */
    private Long enterpriseId;
    /**
     * 备注
     */
    private String remark;
    /**
     * 申请人ID
     */
    private Long applyUserId;
    /**
     * 申请人名称
     */
    private String applyUserName;
    /**
     *  微信用户id
     */
    private Long wxUserId;
    /**
     * 发起审批来源
     */
    private String applySource;

    /**
     * 申请回款周期
     */
    private Long creditDays;

    /**
     * 部门Id
     */
    private Long deptId;

    @Override
    public void setApproveId(Long approveId) {
        this.approveId = approveId;
    }

    @Override
    public void setStatus(String status) {

    }

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

    public Long getApproveId() {
        return approveId;
    }

    public BigDecimal getTotalCreditAmount() {
        return totalCreditAmount;
    }

    public void setTotalCreditAmount(BigDecimal totalCreditAmount) {
        this.totalCreditAmount = totalCreditAmount;
    }

    public BigDecimal getApplyCreditAmount() {
        return applyCreditAmount;
    }

    public void setApplyCreditAmount(BigDecimal applyCreditAmount) {
        this.applyCreditAmount = applyCreditAmount;
    }

    public BigDecimal getTotalSpotAmount() {
        return totalSpotAmount;
    }

    public void setTotalSpotAmount(BigDecimal totalSpotAmount) {
        this.totalSpotAmount = totalSpotAmount;
    }

    public BigDecimal getTotalFuturesAmount() {
        return totalFuturesAmount;
    }

    public void setTotalFuturesAmount(BigDecimal totalFuturesAmount) {
        this.totalFuturesAmount = totalFuturesAmount;
    }

    public Long getEnterpriseId() {
        return enterpriseId;
    }

    @Override
    public void setEnterpriseId(Long enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Long getApplyUserId() {
        return applyUserId;
    }

    public void setApplyUserId(Long applyUserId) {
        this.applyUserId = applyUserId;
    }

    public String getApplyUserName() {
        return applyUserName;
    }

    public void setApplyUserName(String applyUserName) {
        this.applyUserName = applyUserName;
    }

    public Long getWxUserId() {
        return wxUserId;
    }

    public void setWxUserId(Long wxUserId) {
        this.wxUserId = wxUserId;
    }

    public String getApplySource() {
        return applySource;
    }

    public void setApplySource(String applySource) {
        this.applySource = applySource;
    }

    public Long getCreditDays() {
        return creditDays;
    }

    public void setCreditDays(Long creditDays) {
        this.creditDays = creditDays;
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }
}
