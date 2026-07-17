package com.spt.bas.report.client.vo;

import com.spt.tools.core.bean.PageSearchVo;

import java.util.List;

public class RptBusinessManagerWorkbenchSearchVo extends PageSearchVo {

    /**
     * 业务员ID
     */
    private Long matchUserId;
    private List<Long> matchUserIdList;
    
    /**
     * 化工业务员 ids
     */
    private List<Long> hgMatchUserIdList;
    
    /**
     * 本月查询标识
     */
    private Boolean monthFlag;
    
    /**
     * 月份 2024-12
     */
    private String month;
    
    /**
     * 本年查询标识
     */
    private Boolean yearFlag;
    
    /**
     * 年度 2024
     */
    private String year;

    /**
     * 过去五个月月份list
     */
    private List<String> fiveMonthList;

    /**
     * 订单-执行类型查询参数【待出库：NCK；待收款：NSK；待开票：NKP；代收票：NSP；】
     * 订单-应收类型查询参数【即将到期：N；宽期限：B；催告期：D；渔区：S；诉讼：P；】
     * 
     */
    private String labelCode;

    /**
     * 合同类型 
     */
    private String contractType;

    /**
     * 赊销|代采
     */
    private Boolean matchCreditFlg;

    /**
     * 预算类型：DCSX,DC,SX
     */
    private String budgetType;
    
    private List<Long> approveIdList;
    
    private List<Long> contractIdList;
    private List<String> contractNoList;

    /**
     * 企业类型  I-工业客户  T-贸易商
     */
    private String companyType;

    /**
     * 企业IDs
     */
    private List<Long> companyIdList;
    
    private String beginDate;


    public Long getMatchUserId() {
        return matchUserId;
    }

    public void setMatchUserId(Long matchUserId) {
        this.matchUserId = matchUserId;
    }

    public List<Long> getMatchUserIdList() {
        return matchUserIdList;
    }

    public void setMatchUserIdList(List<Long> matchUserIdList) {
        this.matchUserIdList = matchUserIdList;
    }

    public List<Long> getHgMatchUserIdList() {
        return hgMatchUserIdList;
    }

    public void setHgMatchUserIdList(List<Long> hgMatchUserIdList) {
        this.hgMatchUserIdList = hgMatchUserIdList;
    }

    public Boolean getMonthFlag() {
        return monthFlag;
    }

    public void setMonthFlag(Boolean monthFlag) {
        this.monthFlag = monthFlag;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public Boolean getYearFlag() {
        return yearFlag;
    }

    public void setYearFlag(Boolean yearFlag) {
        this.yearFlag = yearFlag;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public List<String> getFiveMonthList() {
        return fiveMonthList;
    }

    public void setFiveMonthList(List<String> fiveMonthList) {
        this.fiveMonthList = fiveMonthList;
    }

    public String getLabelCode() {
        return labelCode;
    }

    public void setLabelCode(String labelCode) {
        this.labelCode = labelCode;
    }

    public String getContractType() {
        return contractType;
    }

    public void setContractType(String contractType) {
        this.contractType = contractType;
    }

    public Boolean getMatchCreditFlg() {
        return matchCreditFlg;
    }

    public void setMatchCreditFlg(Boolean matchCreditFlg) {
        this.matchCreditFlg = matchCreditFlg;
    }

    public String getBudgetType() {
        return budgetType;
    }

    public void setBudgetType(String budgetType) {
        this.budgetType = budgetType;
    }

    public List<Long> getApproveIdList() {
        return approveIdList;
    }

    public void setApproveIdList(List<Long> approveIdList) {
        this.approveIdList = approveIdList;
    }

    public List<Long> getContractIdList() {
        return contractIdList;
    }

    public void setContractIdList(List<Long> contractIdList) {
        this.contractIdList = contractIdList;
    }

    public List<String> getContractNoList() {
        return contractNoList;
    }

    public void setContractNoList(List<String> contractNoList) {
        this.contractNoList = contractNoList;
    }

    public String getCompanyType() {
        return companyType;
    }

    public void setCompanyType(String companyType) {
        this.companyType = companyType;
    }

    public List<Long> getCompanyIdList() {
        return companyIdList;
    }

    public void setCompanyIdList(List<Long> companyIdList) {
        this.companyIdList = companyIdList;
    }

    public String getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(String beginDate) {
        this.beginDate = beginDate;
    }
}
