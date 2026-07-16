package com.spt.bas.client.entity;


import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.jpa.vo.IdEntity;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "t_sync_data")
public class SyncData extends IdEntity {

    private String url;

    private String dataType;

    private String serviceName;

    private String orderId;

    private Long dataCount;

    private String request;

    private String response;

    private String status;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Long getDataCount() {
        return dataCount;
    }

    public void setDataCount(Long dataCount) {
        this.dataCount = dataCount;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
