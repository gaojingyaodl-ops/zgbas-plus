package com.spt.bas.report.client.entity;

import java.math.BigDecimal;

/**
 * 资金方应收统计
 */
public class RptFundReceivableStatistics {
    /**
     * 企业名称
     */
    private String companyName;
    /**
     * 资金方简称
     */
    private String companyAbbr;
    /**
     * 应收合同数
     */
    private Integer receivableContractNum;
    /**
     * 应收逾期罚息
     */
    private BigDecimal receivableBreachAmount;
    /**
     * 应收余额
     */
    private BigDecimal receivableBalance;

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyAbbr() {
        return companyAbbr;
    }

    public void setCompanyAbbr(String companyAbbr) {
        this.companyAbbr = companyAbbr;
    }

    public Integer getReceivableContractNum() {
        return receivableContractNum;
    }

    public void setReceivableContractNum(Integer receivableContractNum) {
        this.receivableContractNum = receivableContractNum;
    }

    public BigDecimal getReceivableBreachAmount() {
        return receivableBreachAmount;
    }

    public void setReceivableBreachAmount(BigDecimal receivableBreachAmount) {
        this.receivableBreachAmount = receivableBreachAmount;
    }

    public BigDecimal getReceivableBalance() {
        return receivableBalance;
    }

    public void setReceivableBalance(BigDecimal receivableBalance) {
        this.receivableBalance = receivableBalance;
    }
}
