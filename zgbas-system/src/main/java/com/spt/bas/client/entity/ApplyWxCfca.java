package com.spt.bas.client.entity;


import com.spt.pm.inter.IPmEntity;
import com.spt.tools.jpa.vo.IdEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * <p>
 *cfca开户
 * </p>
 *
 * @Author: quzhihao
 */
@Entity
@Table(name = "t_wx_apply_cfca")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ApplyWxCfca extends IdEntity implements IPmEntity {

    private String approveNo;

    private String userId;

    /**
     * 申请Ukey数量
     */
    private Integer ukeyNumber;

    /**
     * 电子签章名称
     */
    private String cfcaName;

    /**
     * 电子签名
     */
    private String electronicSign;

    /**
     * 电子签章图片ID
     */
    private String electronicSignFileId;
    /**
     * 法人代表姓名
     */
    private String legalRepresent;

    /**
     * 身份证号
     */
    private String identityCardNumber;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 营业执照副本加盖公章附件ID
     */
    private String businessLicenseWithSealUrl;

    /**
     * 手机号
     */
    private String phone;

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

    private Long companyId;

    /**
     * 申请状态		N-新增，A-审批中，B-驳回，D-完成
     */
    private String status;

    private Long applyUserId;

    private String applyUserName;

    private Long wxUserId;

    private Long approveId;

    private Long enterpriseId;

    private String companyName;

    private String applySource;

    private Long deptId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
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

    public String getElectronicSign() {
        return electronicSign;
    }

    public void setElectronicSign(String electronicSign) {
        this.electronicSign = electronicSign;
    }

    public String getElectronicSignFileId() {
        return electronicSignFileId;
    }

    public void setElectronicSignFileId(String electronicSignFileId) {
        this.electronicSignFileId = electronicSignFileId;
    }

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

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public void setStatus(String status) {
        this.status = status;
    }

    public Long getApplyUserId() {
        return applyUserId;
    }

    public void setApplyUserId(Long applyUserId) {
        this.applyUserId = applyUserId;
    }

    public String getApplyUserName() {
        return applyUserName;
    }

    public void setApplyUserName(String applyUserName) {
        this.applyUserName = applyUserName;
    }

    public Long getWxUserId() {
        return wxUserId;
    }

    public void setWxUserId(Long wxUserId) {
        this.wxUserId = wxUserId;
    }

    public Long getApproveId() {
        return approveId;
    }

    @Override
    public void setApproveId(Long approveId) {
        this.approveId = approveId;
    }

    public Long getEnterpriseId() {
        return enterpriseId;
    }

    @Override
    public void setEnterpriseId(Long enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getApplySource() {
        return applySource;
    }

    public void setApplySource(String applySource) {
        this.applySource = applySource;
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public String getApproveNo() {
        return approveNo;
    }

    public void setApproveNo(String approveNo) {
        this.approveNo = approveNo;
    }
}
