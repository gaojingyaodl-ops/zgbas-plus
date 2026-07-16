package com.spt.bas.client.entity;

import com.spt.tools.jpa.vo.IdEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "t_bas_manual")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class BasManual extends IdEntity {

    private String title;

    private String url;

    private Long parentId;

    private Boolean enableFlg;

    private Integer orderNo;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Boolean getEnableFlg() {
        return enableFlg;
    }

    public void setEnableFlg(Boolean enableFlg) {
        this.enableFlg = enableFlg;
    }

    public Integer getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(Integer orderNo) {
        this.orderNo = orderNo;
    }
}
