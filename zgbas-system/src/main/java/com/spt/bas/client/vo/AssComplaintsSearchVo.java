package com.spt.bas.client.vo;

import com.spt.tools.core.bean.PageSearchVo;

/**
 * @Author: gaojy
 * @create 2022/5/26 15:52
 * @version: 1.0
 * @description:
 */
public class AssComplaintsSearchVo extends PageSearchVo {

    /**
     * 查询类型
     * A-全部权限
     * C-中心权限
     */
    private String searchType;

    /**
     * 查询用户ID
     */
    private Long searchUserId;

    /**
     * 企业账套ID
     */
    private Long enterpriseId;

    public String getSearchType() {
        return searchType;
    }

    public void setSearchType(String searchType) {
        this.searchType = searchType;
    }

    public Long getSearchUserId() {
        return searchUserId;
    }

    public void setSearchUserId(Long searchUserId) {
        this.searchUserId = searchUserId;
    }

    public Long getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(Long enterpriseId) {
        this.enterpriseId = enterpriseId;
    }
}
