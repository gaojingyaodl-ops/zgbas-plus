package com.spt.bas.client.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.spt.tools.jpa.vo.IdEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 我方企业信息
 */
@Entity
@Table(name = "t_bs_company_our")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class BsCompanyOur extends IdEntity {
    /**
     * 企业代码
     */
    private String companyCd;

    /**
     * 企业名称
     */
    private String companyName;

    /**
     * 企业简称
     */
    private String companyAbbr;

    /**
     * 法人代表
     */
    private String companyPerson;

    /**
     * 联系人
     */
    private String companyContact;

    /**
     * 电话
     */
    private String companyPhone;

    /**
     * 传真
     */
    private String companyFax;

    /**
     * 税号
     */
    private String companyTaxNo;

    /**
     * 开户行
     */
    private String companyBankName;

    /**
     * 账号
     */
    private String companyCardId;

    /**
     * 开户行
     */
    private String companyBankName2;

    /**
     * 账号
     */
    private String companyCardId2;

    /**
     * 扩展银行表达式
     */
    private String extraBank;

    /**
     * 签订地点
     */
    private String signingAddr;

    /**
     * 是否有效
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Boolean enableFlg = false;

    /**
     * 备注
     */
    private String remark;
    /**
     * 地址
     */
    private String address;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 序号
     */
    private  Long dispOrderNo;

    /**
     * 我司企业（是否）
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Boolean ourCompanyFlag = false;

    /**
     * 授信类别 0-人保，1-大地，9-自主
     */
    private String creditType;

    public Long getDispOrderNo() {
        return dispOrderNo;
    }

    public void setDispOrderNo(Long dispOrderNo) {
        this.dispOrderNo = dispOrderNo;
    }

    public Boolean getOurCompanyFlag() {
        return ourCompanyFlag;
    }

    public void setOurCompanyFlag(Boolean ourCompanyFlag) {
        this.ourCompanyFlag = ourCompanyFlag;
    }

    public String getCompanyCd() {
        return companyCd;
    }

    public void setCompanyCd(String companyCd) {
        this.companyCd = companyCd;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyAbbr() {
        return companyAbbr;
    }

    public void setCompanyAbbr(String companyAbbr) {
        this.companyAbbr = companyAbbr;
    }

    public String getCompanyPerson() {
        return companyPerson;
    }

    public void setCompanyPerson(String companyPerson) {
        this.companyPerson = companyPerson;
    }

    public String getCompanyContact() {
        return companyContact;
    }

    public void setCompanyContact(String companyContact) {
        this.companyContact = companyContact;
    }

    public String getCompanyPhone() {
        return companyPhone;
    }

    public void setCompanyPhone(String companyPhone) {
        this.companyPhone = companyPhone;
    }

    public String getCompanyFax() {
        return companyFax;
    }

    public void setCompanyFax(String companyFax) {
        this.companyFax = companyFax;
    }

    public String getCompanyTaxNo() {
        return companyTaxNo;
    }

    public void setCompanyTaxNo(String companyTaxNo) {
        this.companyTaxNo = companyTaxNo;
    }

    public String getCompanyBankName() {
        return companyBankName;
    }

    public void setCompanyBankName(String companyBankName) {
        this.companyBankName = companyBankName;
    }

    public String getCompanyCardId() {
        return companyCardId;
    }

    public void setCompanyCardId(String companyCardId) {
        this.companyCardId = companyCardId;
    }

    public String getSigningAddr() {
        return signingAddr;
    }

    public void setSigningAddr(String signingAddr) {
        this.signingAddr = signingAddr;
    }

    public Boolean getEnableFlg() {
        return enableFlg;
    }

    public void setEnableFlg(Boolean enableFlg) {
        this.enableFlg = enableFlg;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCompanyBankName2() {
        return companyBankName2;
    }

    public void setCompanyBankName2(String companyBankName2) {
        this.companyBankName2 = companyBankName2;
    }

    public String getCompanyCardId2() {
        return companyCardId2;
    }

    public void setCompanyCardId2(String companyCardId2) {
        this.companyCardId2 = companyCardId2;
    }

    public String getExtraBank() {
        return extraBank;
    }

    public void setExtraBank(String extraBank) {
        this.extraBank = extraBank;
    }

    public String getCreditType() {
        return creditType;
    }

    public void setCreditType(String creditType) {
        this.creditType = creditType;
    }
}
