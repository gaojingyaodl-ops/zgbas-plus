package com.spt.bas.client.entity;

import com.spt.tools.jpa.vo.IdEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *  外部系统调用我方系统接口历史流水表
 * @Author: wm
 * @create 2022/3/25 17:33
 * @version: 1.0
 * @description:
 */
@Entity
@Table(name = "t_api_external_his")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ApiExternalHis extends IdEntity {
    private static final long serialVersionUID = -7009066496405326333L;

    /**
     * 应用代码
     */
    private String appCode;

    /**
     * 应用名称
     */
    private String appName;

    /**
     * 第三方接口URL
     */
    private String apiUrl;

    /**
     * 第三方接口参数
     */
    private String apiParam;

    public ApiExternalHis() {
    }

    public ApiExternalHis(String appCode, String appName, String apiUrl, String apiParam) {
        this.appCode = appCode;
        this.appName = appName;
        this.apiUrl = apiUrl;
        this.apiParam = apiParam;
    }


    public String getAppCode() {
        return appCode;
    }

    public void setAppCode(String appCode) {
        this.appCode = appCode;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public String getApiParam() {
        return apiParam;
    }

    public void setApiParam(String apiParam) {
        this.apiParam = apiParam;
    }
}
