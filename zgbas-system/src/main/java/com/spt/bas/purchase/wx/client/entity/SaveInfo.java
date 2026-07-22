package com.spt.bas.purchase.wx.client.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
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
 *  临时保存信息
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-09-18 17:37
 */
@Entity
@Table(name = "t_save_info")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@DynamicInsert
@DynamicUpdate
@LogEntityName("临时保存信息")
public class SaveInfo extends IdEntity {
    private Long userId;

    private Long companyId;

    private Long createUserId;

    private Long updateUserId;

    /**
     * 内容 json字符串
     */
    private String content;

    /**
     * 信息类型：
     * 基本信息类型：0
     * 委托授权：1
     * 入金状态：2
     * 申请白条：3
     * 白条服务费：4
     * cfca平台审核：5
     * cfca费用支付：6
     */
    private String type;

    /**
     * 是否提价flg
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Boolean commitFlg;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getCommitFlg() {
        return commitFlg;
    }

    public void setCommitFlg(Boolean commitFlg) {
        this.commitFlg = commitFlg;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public Long getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Long createUserId) {
        this.createUserId = createUserId;
    }

    public Long getUpdateUserId() {
        return updateUserId;
    }

    public void setUpdateUserId(Long updateUserId) {
        this.updateUserId = updateUserId;
    }
}
