package com.spt.bas.report.client.vo;

import java.util.Date;

/**
 * @author MoonLight
 * @version 1.0
 * @description
 * @date 2026/2/27 10:06
 */
public class RptOpenCompanyCreditVo {
    private Long companyId;

    private Long matchUserId;

    private String creditType;

    private Date openCreditTime;

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public Long getMatchUserId() {
        return matchUserId;
    }

    public void setMatchUserId(Long matchUserId) {
        this.matchUserId = matchUserId;
    }

    public String getCreditType() {
        return creditType;
    }

    public void setCreditType(String creditType) {
        this.creditType = creditType;
    }

    public Date getOpenCreditTime() {
        return openCreditTime;
    }

    public void setOpenCreditTime(Date openCreditTime) {
        this.openCreditTime = openCreditTime;
    }
}
