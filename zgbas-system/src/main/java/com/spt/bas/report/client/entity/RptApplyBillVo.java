package com.spt.bas.report.client.entity;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

/**
 * <p>
 *  for applyBill
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-11-19 15:52
 */
public class RptApplyBillVo {
    /**
     * 销售合同编号
     */
    @NotBlank(message = "合同编号不能为空")
    private String contractNo;

    /**
     * 开票金额
     */
    private BigDecimal billedAmount;

    /**
     * 0是开货款发票，1是开服务费发票
     */
    private String type;

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
     * 联系方式
     */
    private String contactPhone;

    /**
     * 寄送地址
     */
    private String contactAddress;

    public String getContractNo() {
        return contractNo;
    }

    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
    }

    public BigDecimal getBilledAmount() {
        return billedAmount;
    }

    public void setBilledAmount(BigDecimal billedAmount) {
        this.billedAmount = billedAmount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
}
