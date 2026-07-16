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
 * 合伙人业务员表
 * @Author: wm
 * @Date: Created in 2022-05-06 13:29
 */
@Entity
@Table(name = "t_partner_user")
public class PartnerUser extends IdEntity {
    private static final long serialVersionUID = -7576907307042977504L;
    /**
     * 合伙人企业id
     */
    private Long partnerCompanyId;
    /**
     * 姓名
     */
    private String name;
    /**
     * 登录名
     */
    private String loginName;

    /**
     * 电话
     */
    private String mobile;
    /**
     * 系统账号id
     */
    private Long sysUserId;
    /**
     * 是否有效
     * 0-无效，1-有效
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Boolean enableFlg = true;
    @JsonSerialize(using = ToStringSerializer.class)
    private Boolean delFlg = false;

    public Boolean getDelFlg() {
        return delFlg;
    }

    public void setDelFlg(Boolean delFlg) {
        this.delFlg = delFlg;
    }

    public Long getPartnerCompanyId() {
        return partnerCompanyId;
    }

    public void setPartnerCompanyId(Long partnerCompanyId) {
        this.partnerCompanyId = partnerCompanyId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Long getSysUserId() {
        return sysUserId;
    }

    public void setSysUserId(Long sysUserId) {
        this.sysUserId = sysUserId;
    }

    public Boolean getEnableFlg() {
        return enableFlg;
    }

    public void setEnableFlg(Boolean enableFlg) {
        this.enableFlg = enableFlg;
    }
}
