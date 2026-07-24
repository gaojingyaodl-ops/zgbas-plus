package com.spt.bas.purchase.wx.client.entity;

/**
 * <p>
 *    捕捉webApplicationException异常消息实体
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-11-19 14:38
 */
public class WebApplicationMsg {
    private Integer errorId;

    private String message;

    private Integer status;

    public Integer getErrorId() {
        return errorId;
    }

    public void setErrorId(Integer errorId) {
        this.errorId = errorId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
