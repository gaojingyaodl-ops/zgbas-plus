package com.spt.bas.client.vo;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * @Author: gaojy
 * @create 2022/4/18 16:31
 * @version: 1.0
 * @description:
 */
public class PiccExcelVo {
    /**
     * 限额编号
     */
    private String corpSerialNo;

    /**
     * 保单号
     */
    private String bussinessNo;

    private String piccCode;

    /**
     * 买方名称
     */
    private String companyName;

    /**
     * 买方地址
     */
    private String riskCompAddress;

    /**
     * 申请额度
     */
    private String appliAmount;

    /**
     * 批复额度
     */
    private String approvedQuota;

    /**
     * 期限
     */
    private String paidTerm;

    /**
     * 人保批复日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date piccApproveDate;

    /**
     * 限额生效日
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date bussStartDate;

    /**
     * 限额失效日
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date bussEndDate;

    /**
     * 已用额度
     */
    private String piccHaveusedAmount;

    /**
     * 剩余额度
     */
    private String piccUseAbleaMount;

    /**
     * 赔偿比例（%）
     */
    private String compensationRatio;

    public String getCorpSerialNo() {
        return corpSerialNo;
    }

    public void setCorpSerialNo(String corpSerialNo) {
        this.corpSerialNo = corpSerialNo;
    }

    public String getBussinessNo() {
        return bussinessNo;
    }

    public void setBussinessNo(String bussinessNo) {
        this.bussinessNo = bussinessNo;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getRiskCompAddress() {
        return riskCompAddress;
    }

    public void setRiskCompAddress(String riskCompAddress) {
        this.riskCompAddress = riskCompAddress;
    }

    public String getAppliAmount() {
        return appliAmount;
    }

    public void setAppliAmount(String appliAmount) {
        this.appliAmount = appliAmount;
    }

    public Date getPiccApproveDate() {
        return piccApproveDate;
    }

    public void setPiccApproveDate(Date piccApproveDate) {
        this.piccApproveDate = piccApproveDate;
    }

    public Date getBussStartDate() {
        return bussStartDate;
    }

    public void setBussStartDate(Date bussStartDate) {
        this.bussStartDate = bussStartDate;
    }

    public Date getBussEndDate() {
        return bussEndDate;
    }

    public void setBussEndDate(Date bussEndDate) {
        this.bussEndDate = bussEndDate;
    }

    public String getCompensationRatio() {
        return compensationRatio;
    }

    public void setCompensationRatio(String compensationRatio) {
        this.compensationRatio = compensationRatio;
    }

    public String getPaidTerm() {
        return paidTerm;
    }

    public void setPaidTerm(String paidTerm) {
        this.paidTerm = paidTerm;
    }

    public String getPiccCode() {
        return piccCode;
    }

    public void setPiccCode(String piccCode) {
        this.piccCode = piccCode;
    }

    public String getApprovedQuota() {
        return approvedQuota;
    }

    public void setApprovedQuota(String approvedQuota) {
        this.approvedQuota = approvedQuota;
    }

    public String getPiccHaveusedAmount() {
        return piccHaveusedAmount;
    }

    public void setPiccHaveusedAmount(String piccHaveusedAmount) {
        this.piccHaveusedAmount = piccHaveusedAmount;
    }

    public String getPiccUseAbleaMount() {
        return piccUseAbleaMount;
    }

    public void setPiccUseAbleaMount(String piccUseAbleaMount) {
        this.piccUseAbleaMount = piccUseAbleaMount;
    }

    public PiccExcelVo() {
    }
}
