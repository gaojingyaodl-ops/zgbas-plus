package com.spt.bas.client.vo;


import java.math.BigDecimal;

public class CreditQuoteVo {
    private Long companyId; //	String	企业id	是
    private String creditType; //	String	额度类型(1-塑融宝；2-浙塑白条;)	是
    private BigDecimal totalCreditAmount; //	BigDecimal	总授信额度	是
    private BigDecimal usedCreditAmount; //	BigDecimal	已使用授信额度	是
    private Long creditDays; //	Long	账期(天)	是

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getCreditType() {
        return creditType;
    }

    public void setCreditType(String creditType) {
        this.creditType = creditType;
    }

    public BigDecimal getTotalCreditAmount() {
        return totalCreditAmount;
    }

    public void setTotalCreditAmount(BigDecimal totalCreditAmount) {
        this.totalCreditAmount = totalCreditAmount;
    }

    public BigDecimal getUsedCreditAmount() {
        return usedCreditAmount;
    }

    public void setUsedCreditAmount(BigDecimal usedCreditAmount) {
        this.usedCreditAmount = usedCreditAmount;
    }

    public Long getCreditDays() {
        return creditDays;
    }

    public void setCreditDays(Long creditDays) {
        this.creditDays = creditDays;
    }
}
