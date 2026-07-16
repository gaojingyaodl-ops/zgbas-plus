package com.spt.bas.client.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.spt.tools.jpa.vo.IdEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * <p>
 *     委托人信息表
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-10-28 10:59
 */
@Entity
@Table(name = "t_bs_entrust")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class BsEntrust extends IdEntity {

    private static final long serialVersionUID = 3406203485771300594L;

    private Long companyId;

    /**
     * 受托人姓名
     */
    private String trusteeName;

    /**
     * 性别 0:男 1:女
     */
    private String trusteeGender;

    /**
     * 受托人手机号
     */
    private String trusteePhone;


    /**
     * 受托人身份证号
     */
    private String identityCardNumber;


    /**
     * 与受托人关系 ；0:员工 1:法人 2:其他
     */
    private String relationShip;

    /**
     * 委托授权书附件id
     */
    private String powerOfAttorneyFileId;

    /**
     * 小程序用户id
     */
    private Long wxUserId;

    private Boolean enableFlg;

    public Long getWxUserId() {
        return wxUserId;
    }

    public void setWxUserId(Long wxUserId) {
        this.wxUserId = wxUserId;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getTrusteeName() {
        return trusteeName;
    }

    public void setTrusteeName(String trusteeName) {
        this.trusteeName = trusteeName;
    }

    public String getTrusteeGender() {
        return trusteeGender;
    }

    public void setTrusteeGender(String trusteeGender) {
        this.trusteeGender = trusteeGender;
    }

    public String getTrusteePhone() {
        return trusteePhone;
    }

    public void setTrusteePhone(String trusteePhone) {
        this.trusteePhone = trusteePhone;
    }

    public String getIdentityCardNumber() {
        return identityCardNumber;
    }

    public void setIdentityCardNumber(String identityCardNumber) {
        this.identityCardNumber = identityCardNumber;
    }

    public String getRelationShip() {
        return relationShip;
    }

    public void setRelationShip(String relationShip) {
        this.relationShip = relationShip;
    }

    public String getPowerOfAttorneyFileId() {
        return powerOfAttorneyFileId;
    }

    public void setPowerOfAttorneyFileId(String powerOfAttorneyFileId) {
        this.powerOfAttorneyFileId = powerOfAttorneyFileId;
    }

    @JsonSerialize(using = ToStringSerializer.class)
    public Boolean getEnableFlg() {
        return enableFlg;
    }

    public void setEnableFlg(Boolean enableFlg) {
        this.enableFlg = enableFlg;
    }
}
