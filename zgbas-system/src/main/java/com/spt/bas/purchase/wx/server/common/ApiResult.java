package com.spt.bas.purchase.wx.server.common;

// Phase 4 stub — Phase 5 will overlay with complete source version

//import io.swagger.annotations.ApiModel;
//import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * 统一返回结果
 */
//@ApiModel(value = "统一返回结果")
public class ApiResult implements Serializable {

    private static final long serialVersionUID = 8958089064948717205L;

    /**
     * 状态码
     */
    //@ApiModelProperty(value = "状态码", dataType = "Integer", required = true)
    private Integer resultCode;

    /**
     * 返回信息
     */
    //@ApiModelProperty(value = "返回信息", dataType = "String", required = true)
    private String message;

    /**
     * 返回数据
     */
    //@ApiModelProperty(value = "返回数据", dataType = "Object", required = true)
    private Object data;

    /**
     * 全参构造函数
     *
     * @param resultCode 状态码
     * @param message    返回信息
     * @param data       返回数据
     */
    public ApiResult(Integer resultCode, String message, Object data) {
        this.resultCode = resultCode;
        this.message = message;
        this.data = data;
    }

    /**
     * 构造一个自定义的返回结果集
     *
     * @param code
     * @param message
     * @param data
     * @return
     */
    public static ApiResult of(Integer code, String message, Object data) {
        return new ApiResult(code, message, data);
    }

    /**
     * 构造一个有状态的带数据的结果集
     *
     * @param status 状态
     * @param data   数据
     * @return BaseResult
     */
    public static ApiResult ofStatus(Status status, Object data) {
        return of(status.getCode(), status.getMessage(), data);
    }


    /**
     * 构造一个有状态不带数据的结果集
     *
     * @param status 状态
     * @return BaseResult
     */
    public static ApiResult ofStatus(Status status) {
        return ofStatus(status, "");
    }

    /**
     * 构造一个成功带数据的结果集
     *
     * @param data 数据
     * @return BaseResult
     */
    public static ApiResult ofSuccess(Object data) {
        return ofStatus(Status.SUCCESS, data);
    }

    /**
     * 构造一个成功且带数据的结果集
     *
     * @return BaseResult
     */
    public static ApiResult ofSuccess() {
        return ofSuccess("");
    }


    /**
     * 构造一个成功带自定义消息和数据的结果集
     *
     * @param message 返回信息
     * @param data    数据
     * @return BaseResult
     */
    public static ApiResult ofMessage(String message, Object data) {
        return of(Status.SUCCESS.getCode(), message, data);
    }

    /**
     * 构造一个异常的API返回
     *
     * @param t   异常
     * @param <T> {@link BaseException} 的子类
     * @return QxbApiResult
     */
    public static <T extends BaseException> ApiResult ofException(T t) {
        return of(t.getCode(), t.getMessage(), t.getData());
    }

    /**
     * 构造一个成功带自定义消息的结果集
     *
     * @param message 返回信息
     * @return BaseResult
     */
    public static ApiResult ofMessage(String message) {
        return of(Status.SUCCESS.getCode(), message, null);
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Integer getResultCode() {
        return resultCode;
    }

    public void setResultCode(Integer resultCode) {
        this.resultCode = resultCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
