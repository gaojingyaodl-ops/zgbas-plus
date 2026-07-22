package com.spt.bas.purchase.wx.client.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.spt.bas.purchase.wx.client.entity.UserDetail;
import com.spt.tools.core.annotation.LogEntityName;
import com.spt.tools.jpa.vo.IdEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

/**
 * <p>
 * 用户表账号密码等信息
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-09-15 11:18
 */
@Entity
@Table(name = "t_wx_company_user")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@DynamicInsert
@DynamicUpdate
@LogEntityName("用户")
public class CompanyUser extends IdEntity {
    private static final long serialVersionUID = 3619471799202811223L;

    /**
     * 登录手机号
     */
    private String loginPhone;

    /**
     * 姓名
     */
    private String name;

    /**
     * 密码
     */
    @JsonIgnore
    private String password;

    /**
     * 微信openId
     */
    @JsonIgnore
    private String openId;

    @JsonIgnore
    @JsonSerialize(using = ToStringSerializer.class)
    private Boolean enableFlg;

    @JsonIgnore
    private String salt;

    private UserDetail userDetail;

    @Transient
    public UserDetail getUserDetail() {
        return userDetail;
    }

    public void setUserDetail(UserDetail userDetail) {
        this.userDetail = userDetail;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getLoginPhone() {
        return loginPhone;
    }

    public void setLoginPhone(String loginPhone) {
        this.loginPhone = loginPhone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    @JsonSerialize(using = ToStringSerializer.class)
    public Boolean getEnableFlg() {
        return enableFlg;
    }

    public void setEnableFlg(Boolean enableFlg) {
        this.enableFlg = enableFlg;
    }



}
