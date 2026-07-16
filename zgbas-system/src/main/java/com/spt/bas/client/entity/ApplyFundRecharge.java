package com.spt.bas.client.entity;

import com.spt.pm.inter.IPmEntity;
import com.spt.tools.jpa.vo.IdEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * 资金方充值申请
 * @Author MoonLight
 * @Date 2024/7/12 16:10
 * @Version 1.0
 */
@Entity
@Table(name = "t_apply_fund_recharge")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ApplyFundRecharge extends IdEntity implements IPmEntity {

    /**
     * 审批单ID
     */
    private Long approveId;

    /**
     * 审批状态
     */
    private String status;

    /**
     * 资金代采方ID
     */
    private Long fundCompanyId;

    /**
     * 资金代采方名称
     */
    private String fundCompanyName;

    /**
     * 我方抬头
     */
    private String ourCompanyName;

    /**
     * 期初金额
     */
    private BigDecimal initialAmount;

    /**
     * 充值金额
     */
    private BigDecimal rechargeAmount;

    /**
     * 期末金额
     */
    private BigDecimal ultimateAmount;

    /**
     * 备注
     */
    private String remark;

    /**
     * 业务员ID
     */
    private Long matchUserId;

    /**
     * 业务员
     */
    private String matchUserName;

    /**
     * 附件ID
     */
    private String fileId;

    public Long getApproveId() {
        return approveId;
    }

    @Override
    public void setApproveId(Long approveId) {
        this.approveId = approveId;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public void setStatus(String status) {
        this.status = status;
    }

    public Long getFundCompanyId() {
        return fundCompanyId;
    }

    public void setFundCompanyId(Long fundCompanyId) {
        this.fundCompanyId = fundCompanyId;
    }

    public String getFundCompanyName() {
        return fundCompanyName;
    }

    public void setFundCompanyName(String fundCompanyName) {
        this.fundCompanyName = fundCompanyName;
    }

    public BigDecimal getInitialAmount() {
        return initialAmount;
    }

    public void setInitialAmount(BigDecimal initialAmount) {
        this.initialAmount = initialAmount;
    }

    public BigDecimal getRechargeAmount() {
        return rechargeAmount;
    }

    public void setRechargeAmount(BigDecimal rechargeAmount) {
        this.rechargeAmount = rechargeAmount;
    }

    public BigDecimal getUltimateAmount() {
        return ultimateAmount;
    }

    public void setUltimateAmount(BigDecimal ultimateAmount) {
        this.ultimateAmount = ultimateAmount;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Long getMatchUserId() {
        return matchUserId;
    }

    public void setMatchUserId(Long matchUserId) {
        this.matchUserId = matchUserId;
    }

    public String getMatchUserName() {
        return matchUserName;
    }

    public void setMatchUserName(String matchUserName) {
        this.matchUserName = matchUserName;
    }

    public String getFileId() {
        return fileId;
    }

    @Override
    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getOurCompanyName() {
        return ourCompanyName;
    }

    public void setOurCompanyName(String ourCompanyName) {
        this.ourCompanyName = ourCompanyName;
    }
}
