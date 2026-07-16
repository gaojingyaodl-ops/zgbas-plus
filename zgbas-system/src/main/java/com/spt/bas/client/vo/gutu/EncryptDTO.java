package com.spt.bas.client.vo.gutu;


/**
 * valueMap开放接口DTO
 */
public class EncryptDTO {
    /**
     * 产品编号
     */
    private Object name;

    /**
     * appKey
     */
    private Object app_key;

    /**
     * secret
     */
    private Object secret;
    /**
     * 业务参数
     */
    private Object data;

    /**
     * 签名
     */
    private Object sign;
    /**
     * 访问时间
     */
    private Object timestamp;

    /**
     * 版本
     */
    private Object version;

    /**
     * 订单号
     */
    private Object order_no;

    public Object getName() {
        return name;
    }

    public void setName(Object name) {
        this.name = name;
    }

    public Object getApp_key() {
        return app_key;
    }

    public void setApp_key(Object app_key) {
        this.app_key = app_key;
    }

    public Object getSecret() {
        return secret;
    }

    public void setSecret(Object secret) {
        this.secret = secret;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Object getSign() {
        return sign;
    }

    public void setSign(Object sign) {
        this.sign = sign;
    }

    public Object getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Object timestamp) {
        this.timestamp = timestamp;
    }

    public Object getVersion() {
        return version;
    }

    public void setVersion(Object version) {
        this.version = version;
    }

    public Object getOrder_no() {
        return order_no;
    }

    public void setOrder_no(Object order_no) {
        this.order_no = order_no;
    }
}
