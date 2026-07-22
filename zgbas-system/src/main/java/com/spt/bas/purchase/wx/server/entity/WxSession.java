package com.spt.bas.purchase.wx.server.entity;

import com.spt.tools.core.annotation.LogEntityName;
import com.spt.tools.jpa.vo.IdEntity;
import lombok.Data;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * <p>
 *  微信登录信息表 保存用户openId和session_key
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-09-18 11:35
 */
@Entity
@Table(name = "t_wx_session")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@DynamicInsert
@DynamicUpdate
@Data
public class WxSession extends IdEntity {
    /**
     * 用户唯一标识
     */
    private String openId;

    /**
     * 会话密钥
     */
    private String sessionKey;

    /**
     * 微信小程序服务接口token
     */
    private String accessToken;

    /**
     * 凭证有效时间，单位：秒。目前是7200秒之内的值
     */
    private Integer expiresIn;

}
