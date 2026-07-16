package com.spt.bas.client.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spt.tools.core.bean.PageSearchVo;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

/**
 * @author 杨英承
 * @version 1.0.0
 * @date 2023/4/11 13:59
 */

public class RptBaseCostVo extends PageSearchVo {
    /** 业务员ID */
    private Long matchUserId;
    private List<Long> userList;

    /** 业务员 */
    private String matchUserName;

    /** 所属区域CD */
    private String branchCd;
    private List<String> branchCdList;

    /** 所属区域名称 */
    private String branchName;

    /** 业务成本年月 */
    private String baseDate;
    private String baseStartDate;
    private String baseEndDate;

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

    /** 创建日期 */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date createdDate;

    /** 更新日期 */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date updatedDate;

    public Long getMatchUserId() {
        return matchUserId;
    }

    public void setMatchUserId(Long matchUserId) {
        this.matchUserId = matchUserId;
    }

    public List<Long> getUserList() {
        return userList;
    }

    public void setUserList(List<Long> userList) {
        this.userList = userList;
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

    public List<String> getBranchCdList() {
        return branchCdList;
    }

    public void setBranchCdList(List<String> branchCdList) {
        this.branchCdList = branchCdList;
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

    public String getBaseStartDate() {
        return baseStartDate;
    }

    public void setBaseStartDate(String baseStartDate) {
        this.baseStartDate = baseStartDate;
    }

    public String getBaseEndDate() {
        return baseEndDate;
    }

    public void setBaseEndDate(String baseEndDate) {
        this.baseEndDate = baseEndDate;
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

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }
}
