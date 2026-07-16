package com.spt.bas.client.vo;

/**
 * @Author: gaojy
 * @create 2022/12/9 10:48
 * @version: 1.0
 * @description:
 */
public class BsBankVo {
    /**
     * 户名
     */
    private String companyName;

    /**
     * 账号
     */
    private String taxNo;

    /**
     * 开户行
     */
    private String bankName;

    /**
     * 行号
     */
    private String bankNum;

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getTaxNo() {
        return taxNo;
    }

    public void setTaxNo(String taxNo) {
        this.taxNo = taxNo;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankNum() {
        return bankNum;
    }

    public void setBankNum(String bankNum) {
        this.bankNum = bankNum;
    }
}
