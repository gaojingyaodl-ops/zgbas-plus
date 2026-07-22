package com.spt.bas.purchase.wx.client.vo;


/**
 * <p>
 *    基本信息-上传证件步骤
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-09-30 11:30
 */
public class CompanyBaseInfoVo {
    /**
     * 法人代表姓名
     */
    private String legalRepresent;

    /**
     * 法人身份证号
     */
    private String identityCardNumber;

    /**
     * 经办人身份证号
     */
    private String managerIdCardNumber;

    /**
     * 法人邮箱
     */
    private String email;

    /**
     * 经办人
     */
    private String managerEmail;

    /**
     * 营业执照副本加盖公章附件ID
     */
    private String businessLicenseWithSealUrl;

    /**
     * 法人手机号
     */
    private String phone;

    /**
     * 经办人手机号
     */
    private String managerPhone;

    /**
     * 证件正面图片id
     */
    private String legalPersonPicUrl;

    /**
     * 证件反面图片id
     */
    private String legalPersonOppositePicUrl;

    /**
     * 证件类型 0：身份证 1：港澳通行证 2：护照 3：台胞证
     */
    private String cardType;

    private String electronicSignFileId;

    public String getLegalRepresent() {
        return legalRepresent;
    }

    public void setLegalRepresent(String legalRepresent) {
        this.legalRepresent = legalRepresent;
    }

    public String getIdentityCardNumber() {
        return identityCardNumber;
    }

    public void setIdentityCardNumber(String identityCardNumber) {
        this.identityCardNumber = identityCardNumber;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getElectronicSignFileId() {
        return electronicSignFileId;
    }

    public void setElectronicSignFileId(String electronicSignFileId) {
        this.electronicSignFileId = electronicSignFileId;
    }

    public String getManagerIdCardNumber() {
        return managerIdCardNumber;
    }

    public void setManagerIdCardNumber(String managerIdCardNumber) {
        this.managerIdCardNumber = managerIdCardNumber;
    }

    public String getManagerEmail() {
        return managerEmail;
    }

    public void setManagerEmail(String managerEmail) {
        this.managerEmail = managerEmail;
    }

    public String getManagerPhone() {
        return managerPhone;
    }

    public void setManagerPhone(String managerPhone) {
        this.managerPhone = managerPhone;
    }
}
