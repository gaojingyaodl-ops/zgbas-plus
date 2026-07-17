package com.spt.bas.report.client.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spt.tools.core.bean.PageSearchVo;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @Author MoonLight
 * @Date 2023/10/16 16:16
 * @Version 1.0
 */
public class RptCtrContractWarnSearchVo extends PageSearchVo {

    /**
     * 合同编号
     */
    private String contractNo;

    /**
     * 企业名称
     */
    private String companyName;

    /**
     * 我方企业名称
     */
    private String ourCompanyName;

    /**
     * 资金方
     */
    private String fundCompanyName;

    /**
     * 合同成交日期-查询开始
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date contractStartDate;

    /**
     * 合同成交日期-查询结束
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date contractEndDate;

    /**
     * 最后开票日期-查询开始
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date latestBillstartDate;

    /**
     * 最后开票日期-查询结束
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date latestBillEndDate;

    /**
     * 逾期天数
     * A-逾期14天以下
     * B-逾期14-30天
     * C-逾期30天以上
     */
    private String breachType;

    /**
     * 事业部
     */
    private Long deptId;

    /**
     * 业务员
     */
    private Long matchUserId;

    /**
     * 人保额度-查询开始
     */
    private BigDecimal piccStartCreditAmount;

    /**
     * 人保额度-查询结束
     */
    private BigDecimal piccEndCreditAmount;

    /**
     * 逾期类型
     * H-历史逾期
     * C-当前逾期
     */
    private String overderType;

    /**
     * 确认收货状态
     * Y-全部确认收货
     * H-部分确认收货
     * N-未确认收货
     */
    private String confirmReceiveType;

    /**
     * 履约状态
     *  N-进行中，B-宽限期，D-催告期，S-逾期，P-违约 A-完成
     */
    private String performanceStatus;

    private List<String> performanceStatusList;

    /**
     * 开票状态  未开票：BN 已开票：BY
     */
    private String invoiceBillCondition;

    private List<Long> deptIdList;

    /**
     * 产品类型
     */
    private String productType;

    /**
     * 化工业务员ID集合
     */
    private List<Long> hgMatchUserIdList;

    public Date getContractStartDate() {
        return contractStartDate;
    }

    public void setContractStartDate(Date contractStartDate) {
        this.contractStartDate = contractStartDate;
    }

    public Date getContractEndDate() {
        return contractEndDate;
    }

    public void setContractEndDate(Date contractEndDate) {
        this.contractEndDate = contractEndDate;
    }

    public String getBreachType() {
        return breachType;
    }

    public void setBreachType(String breachType) {
        this.breachType = breachType;
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public Long getMatchUserId() {
        return matchUserId;
    }

    public void setMatchUserId(Long matchUserId) {
        this.matchUserId = matchUserId;
    }

    public BigDecimal getPiccStartCreditAmount() {
        return piccStartCreditAmount;
    }

    public void setPiccStartCreditAmount(BigDecimal piccStartCreditAmount) {
        this.piccStartCreditAmount = piccStartCreditAmount;
    }

    public BigDecimal getPiccEndCreditAmount() {
        return piccEndCreditAmount;
    }

    public void setPiccEndCreditAmount(BigDecimal piccEndCreditAmount) {
        this.piccEndCreditAmount = piccEndCreditAmount;
    }

    public String getOverderType() {
        return overderType;
    }

    public void setOverderType(String overderType) {
        this.overderType = overderType;
    }

    public String getContractNo() {
        return contractNo;
    }

    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getOurCompanyName() {
        return ourCompanyName;
    }

    public void setOurCompanyName(String ourCompanyName) {
        this.ourCompanyName = ourCompanyName;
    }

    public String getConfirmReceiveType() {
        return confirmReceiveType;
    }

    public void setConfirmReceiveType(String confirmReceiveType) {
        this.confirmReceiveType = confirmReceiveType;
    }

    public String getFundCompanyName() {
        return fundCompanyName;
    }

    public void setFundCompanyName(String fundCompanyName) {
        this.fundCompanyName = fundCompanyName;
    }

    public String getPerformanceStatus() {
        return performanceStatus;
    }

    public void setPerformanceStatus(String performanceStatus) {
        this.performanceStatus = performanceStatus;
    }

    public List<String> getPerformanceStatusList() {
        return performanceStatusList;
    }

    public void setPerformanceStatusList(List<String> performanceStatusList) {
        this.performanceStatusList = performanceStatusList;
    }

    public String getInvoiceBillCondition() {
        return invoiceBillCondition;
    }

    public void setInvoiceBillCondition(String invoiceBillCondition) {
        this.invoiceBillCondition = invoiceBillCondition;
    }

    public Date getLatestBillstartDate() {
        return latestBillstartDate;
    }

    public void setLatestBillstartDate(Date latestBillstartDate) {
        this.latestBillstartDate = latestBillstartDate;
    }

    public Date getLatestBillEndDate() {
        return latestBillEndDate;
    }

    public void setLatestBillEndDate(Date latestBillEndDate) {
        this.latestBillEndDate = latestBillEndDate;
    }

    public List<Long> getDeptIdList() {
        return deptIdList;
    }

    public void setDeptIdList(List<Long> deptIdList) {
        this.deptIdList = deptIdList;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public List<Long> getHgMatchUserIdList() {
        return hgMatchUserIdList;
    }

    public void setHgMatchUserIdList(List<Long> hgMatchUserIdList) {
        this.hgMatchUserIdList = hgMatchUserIdList;
    }
}
