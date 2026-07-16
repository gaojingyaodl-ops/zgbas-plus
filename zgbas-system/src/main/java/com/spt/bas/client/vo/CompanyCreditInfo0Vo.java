package com.spt.bas.client.vo;

import java.io.Serializable;
import java.math.BigDecimal;

public class CompanyCreditInfo0Vo implements Serializable {
    public CompanyCreditInfo0Vo() {
    }
    /**
     * 公司名称
     */
    private String companyName;
    /**
     * 法人
     */
    private String legalRepresent;
    /**
     * 成立时间
     */
    private String startDate;
    /**
     * 注册资本
     */
    private String registerCapital;
    /**
     * 地址
     */
    private String address;
    /**
     * 人保额度
     */
    private BigDecimal creditAmount;
    /**
     * 最近合作日期
     */
    private String contractTime;
    /**
     * 营业执照附件ID
     */
    private String fileId;

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getLegalRepresent() {
        return legalRepresent;
    }

    public void setLegalRepresent(String legalRepresent) {
        this.legalRepresent = legalRepresent;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getRegisterCapital() {
        return registerCapital;
    }

    public void setRegisterCapital(String registerCapital) {
        this.registerCapital = registerCapital;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public BigDecimal getCreditAmount() {
        return creditAmount;
    }

    public void setCreditAmount(BigDecimal creditAmount) {
        this.creditAmount = creditAmount;
    }

    public String getContractTime() {
        return contractTime;
    }

    public void setContractTime(String contractTime) {
        this.contractTime = contractTime;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }
}
