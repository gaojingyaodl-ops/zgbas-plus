package com.spt.bas.client.entity;

import com.spt.tools.jpa.vo.IdEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 第三方接口调用历史流水表
 *
 * @Author: gaojy
 * @create 2022/2/18 14:48
 * @version: 1.0
 * @description:
 */
@Entity
@Table(name = "t_api_request_his")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ApiRequestHis extends IdEntity {
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
     * 第三方接口
     */
    private String apiCode;

    /**
     * 第三方接口名称
     */
    private String apiName;

    /**
     * 第三方接口URL
     */
    private String apiUrl;

    /**
     * 第三方接口参数
     */
    private String apiParam;

    public ApiRequestHis() {
    }

    public ApiRequestHis(String apiCode, String apiName, String apiUrl, String apiParam) {
        this.apiCode = apiCode;
        this.apiName = apiName;
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

    public String getApiCode() {
        return apiCode;
    }

    public void setApiCode(String apiCode) {
        this.apiCode = apiCode;
    }

    public String getApiName() {
        return apiName;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName;
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
