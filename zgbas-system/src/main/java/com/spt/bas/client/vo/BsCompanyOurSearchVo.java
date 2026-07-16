package com.spt.bas.client.vo;

public class BsCompanyOurSearchVo {
    /**
     * 我方企业名称
     */
    private String companyName;

    /**
     * 我方企业代码
     */
    private String companyCd;

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyCd() {
        return companyCd;
    }

    public void setCompanyCd(String companyCd) {
        this.companyCd = companyCd;
    }

    public BsCompanyOurSearchVo() {
    }

    public BsCompanyOurSearchVo(String companyName) {
        this.companyName = companyName;
    }

    public BsCompanyOurSearchVo(String companyName, String companyCd) {
        this.companyName = companyName;
        this.companyCd = companyCd;
    }
}
