package com.spt.bas.purchase.wx.client.vo;

import org.apache.commons.lang3.StringUtils;

public class CompanyOnLineApplyVo {
    /**
     * 企业id
     */
    private Long companyId;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 申请Ukey数量
     */
    private Integer ukeyNumber = 0;
    /**
     * 企业名称/签章名称
     */
    private String cfcaName;
    /**
     * 证件类型 0：身份证 1：港澳通行证 2：护照 3：台胞证
     */
    private String cardType;
    /**
     * 法人证件号
     */
    private String identityCardNumber;
    /**
     * 营业执照号
     */
    private String licenseNumber;
    /**
     * 法人代表姓名
     */
    private String legalRepresent;
    /**
     * 法人手机号
     */
    private String phone;
    /**
     * 法人邮箱
     */
    private String email;

    /**
     * 经办人手机号
     */
    private String managerPhone;
    /**
     * 经办人邮箱
     */
    private String managerEmail;
    /**
     * 经办人身份证号
     */
    private String managerIdCardNumber;

    /**
     * 营业执照图片ID
     */
    private String businessLicenseWithSealUrl;
    /**
     * 合同章或公章拍照ID
     */
    private String electronicSignFileId;
    /**
     * 证件正面图片id
     */
    private String legalPersonPicUrl;

    /**
     * 证件反面图片id
     */
    private String legalPersonOppositePicUrl;

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getUkeyNumber() {
        return ukeyNumber;
    }

    public void setUkeyNumber(Integer ukeyNumber) {
        this.ukeyNumber = ukeyNumber;
    }

    public String getCfcaName() {
        return cfcaName;
    }

    public void setCfcaName(String cfcaName) {
        this.cfcaName = cfcaName;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getIdentityCardNumber() {
        return identityCardNumber;
    }

    public void setIdentityCardNumber(String identityCardNumber) {
        this.identityCardNumber = identityCardNumber;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public String getLegalRepresent() {
        return legalRepresent;
    }

    public void setLegalRepresent(String legalRepresent) {
        this.legalRepresent = legalRepresent;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBusinessLicenseWithSealUrl() {
        return businessLicenseWithSealUrl;
    }

    public void setBusinessLicenseWithSealUrl(String businessLicenseWithSealUrl) {
        this.businessLicenseWithSealUrl = businessLicenseWithSealUrl;
    }

    public String getElectronicSignFileId() {
        return electronicSignFileId;
    }

    public void setElectronicSignFileId(String electronicSignFileId) {
        this.electronicSignFileId = electronicSignFileId;
    }

    public String getLegalPersonPicUrl() {
        return legalPersonPicUrl;
    }

    public void setLegalPersonPicUrl(String legalPersonPicUrl) {
        this.legalPersonPicUrl = legalPersonPicUrl;
    }

    public String getLegalPersonOppositePicUrl() {
        return legalPersonOppositePicUrl;
    }

    public void setLegalPersonOppositePicUrl(String legalPersonOppositePicUrl) {
        this.legalPersonOppositePicUrl = legalPersonOppositePicUrl;
    }

    public String parseBusinessLicenseWithSealUrl(){
        return StringUtils.isNotBlank(this.businessLicenseWithSealUrl) ? this.businessLicenseWithSealUrl + "," : "";
    }

    public String parseLegalPersonPicUrl(){
        return StringUtils.isNotBlank(this.legalPersonPicUrl) ? this.legalPersonPicUrl + "," : "";
    }

    public String parseLegalPersonOppositePicUrl(){
        return StringUtils.isNotBlank(this.legalPersonOppositePicUrl) ? this.legalPersonOppositePicUrl + "," : "";
    }

    public String parseElectronicSignFileId(){
        return StringUtils.isNotBlank(this.electronicSignFileId) ? this.electronicSignFileId + "," : "";
    }

    public String getManagerPhone() {
        return managerPhone;
    }

    public void setManagerPhone(String managerPhone) {
        this.managerPhone = managerPhone;
    }

    public String getManagerEmail() {
        return managerEmail;
    }

    public void setManagerEmail(String managerEmail) {
        this.managerEmail = managerEmail;
    }

    public String getManagerIdCardNumber() {
        return managerIdCardNumber;
    }

    public void setManagerIdCardNumber(String managerIdCardNumber) {
        this.managerIdCardNumber = managerIdCardNumber;
    }
}
