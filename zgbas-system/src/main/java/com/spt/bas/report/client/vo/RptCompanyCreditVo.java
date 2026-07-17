package com.spt.bas.report.client.vo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;

/**
 * @Author MoonLight
 * @Date 2024/10/14 14:51
 * @Version 1.0
 */
public class RptCompanyCreditVo {
    private Long companyId;

    private String creditType;

    private BigDecimal creditAmount = BigDecimal.ZERO;

    private BigDecimal temporaryAmount = BigDecimal.ZERO;

    private BigDecimal usedCreditAmount = BigDecimal.ZERO;

    private BigDecimal riskAmount;

    private List<Long> companyIdList;

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

    public BigDecimal getCreditAmount() {
        return creditAmount;
    }

    public void setCreditAmount(BigDecimal creditAmount) {
        this.creditAmount = creditAmount;
    }

    public BigDecimal getTemporaryAmount() {
        return temporaryAmount;
    }

    public void setTemporaryAmount(BigDecimal temporaryAmount) {
        this.temporaryAmount = temporaryAmount;
    }

    public BigDecimal getUsedCreditAmount() {
        return usedCreditAmount;
    }

    public void setUsedCreditAmount(BigDecimal usedCreditAmount) {
        this.usedCreditAmount = usedCreditAmount;
    }

    public List<Long> getCompanyIdList() {
        return companyIdList;
    }

    public void setCompanyIdList(List<Long> companyIdList) {
        this.companyIdList = companyIdList;
    }

    public BigDecimal getRiskAmount() {
        return riskAmount;
    }

    public void setRiskAmount(BigDecimal riskAmount) {
        this.riskAmount = riskAmount;
    }

    public RptCompanyCreditVo() {
    }

    public RptCompanyCreditVo(List<Long> companyIdList) {
        this.companyIdList = companyIdList;
    }

    private BigDecimal defaultAmount(BigDecimal v){
        return Objects.isNull(v) ? BigDecimal.ZERO : v;
    }

    public BigDecimal getRemainingAmount(){
        if (Objects.nonNull(riskAmount)){
            return defaultAmount(riskAmount).add(defaultAmount(temporaryAmount)).subtract(defaultAmount(usedCreditAmount)).setScale(2, RoundingMode.HALF_UP);
        }
        return defaultAmount(creditAmount).add(defaultAmount(temporaryAmount)).subtract(defaultAmount(usedCreditAmount)).setScale(2, RoundingMode.HALF_UP);
    }
}
