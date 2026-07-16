package com.spt.bas.client.vo;

import com.spt.bas.client.entity.ApplyBuy;

public class SysQuickSearchVo extends ApplyBuy {

    private static final long serialVersionUID = 6895345995868005276L;

    /**
     * 模块url，如：/pm/approve
     */
    private String moduleUrl;

    /**
     * 【属性名称-属性值】的JSON数据
     */
    private String propJson;

    /**
     * 快速查询名称
     */
    private String searchName;

    /**
     * 创建人ID
     */
    private Long userId;

    public String getModuleUrl() {
        return moduleUrl;
    }

    public void setModuleUrl(String moduleUrl) {
        this.moduleUrl = moduleUrl;
    }

    public String getPropJson() {
        return propJson;
    }

    public void setPropJson(String propJson) {
        this.propJson = propJson;
    }

    public String getSearchName() {
        return searchName;
    }

    public void setSearchName(String searchName) {
        this.searchName = searchName;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
