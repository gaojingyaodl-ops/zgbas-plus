package com.spt.bas.web.shiro;

import org.apache.shiro.authc.UsernamePasswordToken;

public class SsoUsernamePasswordToken extends UsernamePasswordToken {

	private static final long serialVersionUID = -1988622986945999854L;

	private String appCode;

	private boolean ssoLogin;

	public boolean getSsoLogin() {
		return ssoLogin;
	}

	public void setSsoLogin(boolean ssoLogin) {
		this.ssoLogin = ssoLogin;
	}

	public String getAppCode() {
		return appCode;
	}

	public void setAppCode(String appCode) {
		this.appCode = appCode;
	}


}
