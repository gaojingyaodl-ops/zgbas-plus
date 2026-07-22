package com.spt.bas.purchase.wx.server.entity;

import com.spt.tools.core.annotation.LogEntityName;
import com.spt.tools.jpa.vo.IdEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * <p>
 * 微信登录凭证
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-09-15 13:00
 */
@Entity
@Table(name = "t_wx_access_token")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@DynamicInsert
@DynamicUpdate
@LogEntityName("微信登录凭证")
public class WxAccessToken extends IdEntity {
    private static final long serialVersionUID = 3619471792202811223L;

    private Long userId;

    private String accessToken;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
