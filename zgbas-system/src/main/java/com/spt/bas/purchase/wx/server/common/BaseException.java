package com.spt.bas.purchase.wx.server.common;

// Phase 4 stub — Phase 5 will overlay with complete source version

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @ClassName BaseException
 * @Author shengong
 * @Date 2020-07-22 23:17
 * @Description 异常基类
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BaseException extends RuntimeException{
    /**
     * 状态码
     */
    private Integer code;

    /**
     * 返回信息
     */
    private String message;

    /**
     * 返回数据
     */
    private Object data;

    public BaseException(Status status) {
        super(status.getMessage());
        this.code = status.getCode();
        this.message = status.getMessage();
    }

    public BaseException(Status status, Object data) {
        this(status);
        this.data = data;
    }

    public BaseException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public BaseException(Integer code, String message, Object data) {
        this(code, message);
        this.data = data;
    }

    public BaseException(Status status, String message) {
        super(message);
        this.code = status.getCode();
        this.message = message;
    }
}
