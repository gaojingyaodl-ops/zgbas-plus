package com.spt.bas.report.client.vo;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 *  合同的货款开票的历史详情-分批次
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-11-16 15:29
 */
public class RptCtrBillDetailVo {

    /**
     * 发票号码
     */
    private String billedNumber;

    /**
     * 开票申请id，根据ID可以查询到开票详细
     */
    private String billedId;

    /**
     * 开票时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private Date billedDate;

    /**
     * 开票金额
     */
    private BigDecimal billedAmount;

    /**
     * 开户行
     */
    private String bankName;

    /**
     * 银行账号
     */
    private String bankAccount;

    /**
     * 税号
     */
    private String taxNo;

    /**
     * 联系人
     */
    private String contactPerson;

    /**
     * 联系人的手机号码
     */
    private String contactPhone;

    /**
     * 发票寄送地址
     */
    private String contactAddress;

    /**
     * 备注
     */
    private String remark;


    public String getBilledNumber() {
        return billedNumber;
    }

    public void setBilledNumber(String billedNumber) {
        this.billedNumber = billedNumber;
    }

    public String getBilledId() {
        return billedId;
    }

    public void setBilledId(String billedId) {
        this.billedId = billedId;
    }

    public Date getBilledDate() {
        return billedDate;
    }

    public void setBilledDate(Date billedDate) {
        this.billedDate = billedDate;
    }

    public BigDecimal getBilledAmount() {
        return billedAmount;
    }

    public void setBilledAmount(BigDecimal billedAmount) {
        this.billedAmount = billedAmount;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getTaxNo() {
        return taxNo;
    }

    public void setTaxNo(String taxNo) {
        this.taxNo = taxNo;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getContactAddress() {
        return contactAddress;
    }

    public void setContactAddress(String contactAddress) {
        this.contactAddress = contactAddress;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
