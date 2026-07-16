package com.spt.bas.client.vo;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.spt.bas.client.entity.ApplyCtrContractFactor;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

public class ApplyCtrContractFactorVo  {

    private  String   status;

    /**
     *合同编号
     */
    private  String   contractNo;

    /**
     *银行放款金额
     */
    private BigDecimal loanAmount;

    /**
     * 实际放款日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date loanDate;

    public String getContractNo() {
        return contractNo;
    }

    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
    }

    public BigDecimal getLoanAmount() {
        return loanAmount;
    }

    public void setLoanAmount(BigDecimal loanAmount) {
        this.loanAmount = loanAmount;
    }

    public Date getLoanDate() {
        return loanDate;
    }

    public void setLoanDate(Date loanDate) {
        this.loanDate = loanDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
