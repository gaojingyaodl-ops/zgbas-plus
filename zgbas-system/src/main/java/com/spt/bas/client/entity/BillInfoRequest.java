package com.spt.bas.client.entity;


/**
 * <p>
 *  for addBillsInfo
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-11-20 12:59
 */
public class BillInfoRequest {
    /**
     * 开户行
     */
    private String bankName;

    /**
     * 开户名
     */
    private String accountName;

    /**
     * 银行账户
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
     * 联系人手机号
     */
    private String contactPhone;

    /**
     * 发票寄送地址
     */
    private String contactAddress;

    private Long companyId;

    /**
     *
     */
    private Long accountId;

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

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }
}
