package com.spt.bas.client.vo;


public class CmsResultVo {
    private String approveStatus;
    private String companyName;

    public String getApproveStatus() {
        return approveStatus;
    }

    public void setApproveStatus(String approveStatus) {
        this.approveStatus = approveStatus;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public CmsResultVo() {
    }

    public CmsResultVo(String approveStatus, String companyName) {
        this.approveStatus = approveStatus;
        this.companyName = companyName;
    }
}
