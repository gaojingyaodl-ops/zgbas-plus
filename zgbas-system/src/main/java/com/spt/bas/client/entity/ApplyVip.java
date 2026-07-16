package com.spt.bas.client.entity;


import com.spt.pm.inter.IPmEntity;
import com.spt.tools.jpa.vo.IdEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * vip审批
 */
@Entity
@Table(name = "t_apply_vip")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ApplyVip extends IdEntity implements IPmEntity {


    /**
     * 剩余天数
     */
    private    Integer daysRemaining;

    public Integer getDaysRemaining() {
        return daysRemaining;
    }

    public void setDaysRemaining(Integer daysRemaining) {
        this.daysRemaining = daysRemaining;
    }

    /**
     * 申请人ID
     */
    private Long applyUserId;
    /**
     * b备注
     */
    private String remark;
    /**
     * 申请人
     */
    private String applyUserName;
    /**
     *  公司名称
     */
    private String companyName;
    /**
     * 公司ID
     */
    private Long companyId;
    /**
     *  附件ID
     */
    private String fileId;

    private Long approveId;
    /**
     * 发起系统
     */
    private String applySource;
    /**
     * vip等级
     */
    private Integer vipLevel;


    /**
     * 开始日期
     */
    private Date  startDate;

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    /**
     * 截止日期
     */
    private Date  endDate;


    /**
     * vip服务费
     */
    private BigDecimal vipAmount;
    /**
     * 服务费率
     */
    private BigDecimal rate;
    /**
     * 超期服务费率
     */
    private BigDecimal interestRate;

    /**
     * 业务员名字
     */
    private  String matchUserName;

    public String getMatchUserName() {
        return matchUserName;
    }

    public void setMatchUserName(String matchUserName) {
        this.matchUserName = matchUserName;
    }

    /**
     * vip合同id
     */
    private String vipContractId;
    /**
     * VIP的业务提成归属
     */
    private Long matchUserId;
    /**
     * 状态
     */

    private String status;

    public BigDecimal getVipAmount() {
        return vipAmount;
    }

    public void setVipAmount(BigDecimal vipAmount) {
        this.vipAmount = vipAmount;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    public String getVipContractId() {
        return vipContractId;
    }

    public void setVipContractId(String vipContractId) {
        this.vipContractId = vipContractId;
    }

    public Long getMatchUserId() {
        return matchUserId;
    }

    public void setMatchUserId(Long matchUserId) {
        this.matchUserId = matchUserId;
    }

    public String getApplySource() {
        return applySource;
    }

    public void setApplySource(String applySource) {
        this.applySource = applySource;
    }


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


    public Long getApplyUserId() {
        return applyUserId;
    }

    public void setApplyUserId(Long applyUserId) {
        this.applyUserId = applyUserId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getApplyUserName() {
        return applyUserName;
    }

    public void setApplyUserName(String applyUserName) {
        this.applyUserName = applyUserName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getFileId() {
        return fileId;
    }

    @Override
    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public Integer getVipLevel() {
        return vipLevel;
    }

    public void setVipLevel(Integer vipLevel) {
        this.vipLevel = vipLevel;
    }


}
