/**
 * 
 */
package com.spt.tools.core.exception;

import com.spt.tools.core.json.JsonUtil;

/**
 * @author wlddh
 *
 */
public class WebApplicationException extends Exception {

	private static final long serialVersionUID = -8207784148958493754L;
	private int status;// http code
	private ErrorResp resp;

	public WebApplicationException(String msg) {
		super(msg);
	}

	public WebApplicationException(int status, String msg) {
		super(msg);
		this.status = status;
	}
	public WebApplicationException(int status, ErrorResp resp) {
		super(JsonUtil.obj2Json(resp));
		this.status = status;
		this.resp = resp;
	}
	public WebApplicationException(int status, Throwable e) {
		super(e);
		this.status = status;
	}

	public WebApplicationException(int status, String msg, Throwable e) {
		super(msg, e);
		this.status = status;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public ErrorResp getResp() {
		return resp;
	}

	public void setResp(ErrorResp resp) {
		this.resp = resp;
	}

}
