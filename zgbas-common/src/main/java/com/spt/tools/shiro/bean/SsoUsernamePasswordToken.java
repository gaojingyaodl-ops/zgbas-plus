package com.spt.tools.shiro.bean;

import org.apache.shiro.authc.UsernamePasswordToken;

public class SsoUsernamePasswordToken extends UsernamePasswordToken {

	private static final long serialVersionUID = -1988622986945999854L;
	
	private String appCode;
	private long timestemp;

	public String getAppCode() {
		return appCode;
	}

	public void setAppCode(String appCode) {
		this.appCode = appCode;
	}

	public long getTimestemp() {
		return timestemp;
	}

	public void setTimestemp(long timestemp) {
		this.timestemp = timestemp;
	}
}
