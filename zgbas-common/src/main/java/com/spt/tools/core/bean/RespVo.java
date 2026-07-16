/**
 * 
 */
package com.spt.tools.core.bean;

import com.spt.tools.core.exception.ErrorResp;

/**
 * @author wlddh
 *
 */
public class RespVo<T> {
	private String code = "1"; // 1 代表成功
	private String message = "success";
	private T data;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public void setFail(ErrorResp resp) {
		if (resp != null) {
			this.message = resp.getMessage();
			this.code = resp.getErrorId() + "";
		}
	}

	public void setFail(String msg, String code) {
		this.message = msg;
		this.code = code;
	}
}
