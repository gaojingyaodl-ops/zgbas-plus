package com.spt.bas.report.client.entity;

import com.spt.bas.client.entity.BsCompanyCredit;
import com.spt.tools.jpa.vo.IdEntity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 授信额度监控 
 */
public class RptCreditAmountMonitor extends IdEntity {

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
     * 客户分类
     */
    private String creditRating;

    /**
     * 授信类别字符串
     */
    private String piccFlgStr;

    /**
     * 人保批复额度
     */
    private BigDecimal piccCreditAmount; 
    
    /**
     * 大地额度
     */
    private BigDecimal daDiCreditAmount;
    
    /**
     * 已使用额度
     */
    private BigDecimal usedCreditAmount;

    /**
     * 应收金额
     */
    private BigDecimal receiveAmount = BigDecimal.ZERO;

    /**
     * 超额金额
     */
    private BigDecimal excessAmount = BigDecimal.ZERO;

    /**
     * 超保毛利
     */
    private BigDecimal excessGrossProfit = BigDecimal.ZERO;

    /**
     * 超额占比
     */
    private BigDecimal excessRate = BigDecimal.ZERO;
    private String excessRateStr;

    /**
     * 赊销单数
     */
    private Integer creditOrdersNum = 0;

    /**
     * 逾期总额
     */
    private BigDecimal overdueAmount  = BigDecimal.ZERO;

    /**
     * 逾期[7，15)单数
     */
    private Integer overdueDays7 = 0;

    /**
     * 逾期[15，30)单数
     */
    private Integer overdueDays15 = 0;

    /**
     * 逾期[30，)单数
     */
    private Integer overdueDays30 = 0;
    
     /**
     * 开始合作时间
     */
    private Date contractStartDate;
    private Date contractStartDateStr;

    /**
     * 是否签连带
     */
    private String liabilityFlg;

    /**
     * 是否访厂
     */
    private String accessReportFlg;

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

    private List<BsCompanyCredit> companyCreditList;

    /**
     * 授信信息
     */
    private String creditInfo;


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

    public String getPiccFlgStr() {
        return piccFlgStr;
    }

    public void setPiccFlgStr(String piccFlgStr) {
        this.piccFlgStr = piccFlgStr;
    }

    public BigDecimal getPiccCreditAmount() {
        return piccCreditAmount;
    }

    public void setPiccCreditAmount(BigDecimal piccCreditAmount) {
        this.piccCreditAmount = piccCreditAmount;
    }

    public BigDecimal getDaDiCreditAmount() {
        return daDiCreditAmount;
    }

    public void setDaDiCreditAmount(BigDecimal daDiCreditAmount) {
        this.daDiCreditAmount = daDiCreditAmount;
    }

    public BigDecimal getUsedCreditAmount() {
        return usedCreditAmount;
    }

    public void setUsedCreditAmount(BigDecimal usedCreditAmount) {
        this.usedCreditAmount = usedCreditAmount;
    }

    public BigDecimal getReceiveAmount() {
        return receiveAmount;
    }

    public void setReceiveAmount(BigDecimal receiveAmount) {
        this.receiveAmount = receiveAmount;
    }

    public BigDecimal getExcessAmount() {
        return excessAmount;
    }

    public void setExcessAmount(BigDecimal excessAmount) {
        this.excessAmount = excessAmount;
    }

    public BigDecimal getExcessRate() {
        return excessRate;
    }

    public void setExcessRate(BigDecimal excessRate) {
        this.excessRate = excessRate;
    }

    public String getExcessRateStr() {
        return excessRateStr;
    }

    public void setExcessRateStr(String excessRateStr) {
        this.excessRateStr = excessRateStr;
    }

    public Integer getCreditOrdersNum() {
        return creditOrdersNum;
    }

    public void setCreditOrdersNum(Integer creditOrdersNum) {
        this.creditOrdersNum = creditOrdersNum;
    }

    public BigDecimal getOverdueAmount() {
        return overdueAmount;
    }

    public void setOverdueAmount(BigDecimal overdueAmount) {
        this.overdueAmount = overdueAmount;
    }

    public Integer getOverdueDays7() {
        return overdueDays7;
    }

    public void setOverdueDays7(Integer overdueDays7) {
        this.overdueDays7 = overdueDays7;
    }

    public Integer getOverdueDays15() {
        return overdueDays15;
    }

    public void setOverdueDays15(Integer overdueDays15) {
        this.overdueDays15 = overdueDays15;
    }

    public Integer getOverdueDays30() {
        return overdueDays30;
    }

    public void setOverdueDays30(Integer overdueDays30) {
        this.overdueDays30 = overdueDays30;
    }

    public Date getContractStartDate() {
        return contractStartDate;
    }

    public void setContractStartDate(Date contractStartDate) {
        this.contractStartDate = contractStartDate;
    }

    public Date getContractStartDateStr() {
        return contractStartDateStr;
    }

    public void setContractStartDateStr(Date contractStartDateStr) {
        this.contractStartDateStr = contractStartDateStr;
    }

    public String getLiabilityFlg() {
        return liabilityFlg;
    }

    public void setLiabilityFlg(String liabilityFlg) {
        this.liabilityFlg = liabilityFlg;
    }

    public String getAccessReportFlg() {
        return accessReportFlg;
    }

    public void setAccessReportFlg(String accessReportFlg) {
        this.accessReportFlg = accessReportFlg;
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

    public BigDecimal getExcessGrossProfit() {
        return excessGrossProfit;
    }

    public void setExcessGrossProfit(BigDecimal excessGrossProfit) {
        this.excessGrossProfit = excessGrossProfit;
    }

    public List<BsCompanyCredit> getCompanyCreditList() {
        return companyCreditList;
    }

    public void setCompanyCreditList(List<BsCompanyCredit> companyCreditList) {
        this.companyCreditList = companyCreditList;
    }

    public String getCreditInfo() {
        return creditInfo;
    }

    public void setCreditInfo(String creditInfo) {
        this.creditInfo = creditInfo;
    }
}
