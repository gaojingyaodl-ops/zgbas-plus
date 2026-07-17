package com.spt.bas.report.client.vo;

import com.spt.tools.core.bean.PageSearchVo;

import java.math.BigDecimal;

/**
 * 授信额度监控 VO
 */
public class RptCreditAmountMonitorSearchVo extends PageSearchVo {

    /**
     * 企业ID
     */
    private Long companyId;
    /**
     * 客户名称
     */
    private String companyName;

    /**
     * 授信类别
     */
    private Boolean piccFlg;

    /**
     * 授信类别(0：人保，1：大地，9：自主)
     */
    private String creditCategory;

    /**
     * 客户分类
     */
    private String creditRating;

    /**
     * 超额金额
     */
    private BigDecimal excessAmountStart;
    private BigDecimal excessAmountEnd; 
    
    /**
     * 应收本金
     */
    private BigDecimal receiveAmountStart;
    private BigDecimal receiveAmountEnd;
    
    /**
     * 超保比率
     */
    private BigDecimal excessRateStart;
    private BigDecimal excessRateEnd;
    
    /**
     * 业务员ID
     */
    private Long matchUserId;

    /**
     * 业务员名称
     */
    private String matchUserName;

    /**
     * 区域ID
     */
    private Long deptId;

    /**
     * 区域名称
     */
    private String deptName;

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

    public Boolean getPiccFlg() {
        return piccFlg;
    }

    public void setPiccFlg(Boolean piccFlg) {
        this.piccFlg = piccFlg;
    }

    public BigDecimal getExcessAmountStart() {
        return excessAmountStart;
    }

    public void setExcessAmountStart(BigDecimal excessAmountStart) {
        this.excessAmountStart = excessAmountStart;
    }

    public BigDecimal getExcessAmountEnd() {
        return excessAmountEnd;
    }

    public void setExcessAmountEnd(BigDecimal excessAmountEnd) {
        this.excessAmountEnd = excessAmountEnd;
    }

    public BigDecimal getExcessRateStart() {
        return excessRateStart;
    }

    public void setExcessRateStart(BigDecimal excessRateStart) {
        this.excessRateStart = excessRateStart;
    }

    public BigDecimal getExcessRateEnd() {
        return excessRateEnd;
    }

    public void setExcessRateEnd(BigDecimal excessRateEnd) {
        this.excessRateEnd = excessRateEnd;
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

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getCreditRating() {
        return creditRating;
    }

    public void setCreditRating(String creditRating) {
        this.creditRating = creditRating;
    }

    public BigDecimal getReceiveAmountStart() {
        return receiveAmountStart;
    }

    public void setReceiveAmountStart(BigDecimal receiveAmountStart) {
        this.receiveAmountStart = receiveAmountStart;
    }

    public BigDecimal getReceiveAmountEnd() {
        return receiveAmountEnd;
    }

    public void setReceiveAmountEnd(BigDecimal receiveAmountEnd) {
        this.receiveAmountEnd = receiveAmountEnd;
    }

    public String getCreditCategory() {
        return creditCategory;
    }

    public void setCreditCategory(String creditCategory) {
        this.creditCategory = creditCategory;
    }
}
