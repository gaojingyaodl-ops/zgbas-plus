package com.spt.bas.report.client.vo;

import java.math.BigDecimal;

/**
 * 业务成本统计表
 */
public class RptBaseCostVo {

    private Long id;
    
    /** 业务员ID */
    private Long matchUserId;

    /** 业务员 */
    private String matchUserName;

    /** 所属区域CD */
    private String branchCd;

    /** 所属区域名称 */
    private String branchName;

    /** 业务成本年月 */
    private String baseDate;

    /** 工资 */
    private BigDecimal wages;

    /** 提成绩效 */
    private BigDecimal commission;

    /** 其它费用 */
    private BigDecimal otherCost;

    /** 社保 */
    private BigDecimal socialSecurity;

    /** 公积金 */
    private BigDecimal providentFund;

    /** 出差报销费用 */
    private BigDecimal evectionCost;

    /** 合计成本 */
    private BigDecimal totalCost;

    /** 备注 */
    private String remark;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getBranchCd() {
        return branchCd;
    }

    public void setBranchCd(String branchCd) {
        this.branchCd = branchCd;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getBaseDate() {
        return baseDate;
    }

    public void setBaseDate(String baseDate) {
        this.baseDate = baseDate;
    }

    public BigDecimal getWages() {
        return wages;
    }

    public void setWages(BigDecimal wages) {
        this.wages = wages;
    }

    public BigDecimal getCommission() {
        return commission;
    }

    public void setCommission(BigDecimal commission) {
        this.commission = commission;
    }

    public BigDecimal getOtherCost() {
        return otherCost;
    }

    public void setOtherCost(BigDecimal otherCost) {
        this.otherCost = otherCost;
    }

    public BigDecimal getSocialSecurity() {
        return socialSecurity;
    }

    public void setSocialSecurity(BigDecimal socialSecurity) {
        this.socialSecurity = socialSecurity;
    }

    public BigDecimal getProvidentFund() {
        return providentFund;
    }

    public void setProvidentFund(BigDecimal providentFund) {
        this.providentFund = providentFund;
    }

    public BigDecimal getEvectionCost() {
        return evectionCost;
    }

    public void setEvectionCost(BigDecimal evectionCost) {
        this.evectionCost = evectionCost;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "RptBaseCostVo{" +
                "id=" + id +
                ", matchUserId=" + matchUserId +
                ", matchUserName='" + matchUserName + '\'' +
                ", branchCd='" + branchCd + '\'' +
                ", branchName='" + branchName + '\'' +
                ", baseDate='" + baseDate + '\'' +
                ", wages=" + wages +
                ", commission=" + commission +
                ", otherCost=" + otherCost +
                ", socialSecurity=" + socialSecurity +
                ", providentFund=" + providentFund +
                ", evectionCost=" + evectionCost +
                ", totalCost=" + totalCost +
                ", remark='" + remark + '\'' +
                '}';
    }
}
