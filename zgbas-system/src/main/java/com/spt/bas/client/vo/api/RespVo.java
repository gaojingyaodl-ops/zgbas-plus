/**
 * 
 */
package com.spt.bas.client.vo.api;

import com.spt.tools.core.exception.ErrorResp;

/**
 * @author wlddh
 *
 */
public class RespVo<T> {
	private int code = ApiCode.BASE_SUCCESS; // 200 代表成功
	private String message ="success";
	private T data;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
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

	public void setFail(String message) {
		this.setCode(201);
		this.setMessage(message);
	}

	public void setFail(ErrorResp resp) {
		if (resp != null) {
			this.message = resp.getMessage();
			this.code = resp.getErrorId();
		}
	}
}
