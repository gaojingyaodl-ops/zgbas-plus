package com.spt.bas.client.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.spt.tools.jpa.vo.IdEntity;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * 合伙人企业信息表
 * @Author: wm
 * @Date: Created in 2022-05-06 13:28
 */
@Entity
@Table(name = "t_partner_company")
public class PartnerCompany extends IdEntity {
    private static final long serialVersionUID = 4225927722446946894L;
    /**
     * 企业名称
     */
    private String companyName;
    /**
     * 地址
     */
    private String address;
    /**
     * 法人
     */
    private String legalRepresent;

    /**
     * 联系人
     */
    private String contactName;
    /**
     * 联系电话
     */
    private String contactPhone;
    /**
     * 联系邮箱
     */
    private String contactEmail;
    /**
     * 营业执照号
     */
    private String licenseNumber;
    /**
     * 银行账号
     */
    private String bankAccount;
    /**
     * 开户行
     */
    private String bankName;
    /**
     * 是否有效
     * 0-有效，1-无效
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Boolean enableFlg ;

    /**
     * 注册日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date registerDate;

    /**
     * 备注
     */
    private String remark;


    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLegalRepresent() {
        return legalRepresent;
    }

    public void setLegalRepresent(String legalRepresent) {
        this.legalRepresent = legalRepresent;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public Boolean getEnableFlg() {
        return enableFlg;
    }

    public void setEnableFlg(Boolean enableFlg) {
        this.enableFlg = enableFlg;
    }

    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
