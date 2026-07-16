package com.spt.bas.client.vo.rtVo;

/**
 * @Author: gaojy
 * @create 2022/4/8 9:39
 * @version: 1.0
 * @description:
 */
public class RtResp<T> {
    private int code;
    private String msg;
    private T data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public void setFail(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Boolean isSuccess(){
        return this.code == 200;
    }


}
