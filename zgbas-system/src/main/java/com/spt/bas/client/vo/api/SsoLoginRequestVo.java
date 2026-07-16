package com.spt.bas.client.vo.api;

public class SsoLoginRequestVo {
	private Long userId;
	private String token;

	private String accessToken;
	private String redirectUrl;
	private Boolean relogin = false;
	private String appCode;
	private String loginTime;

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getRedirectUrl() {
		return redirectUrl;
	}

	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}

	public Boolean getRelogin() {
		return relogin;
	}

	public void setRelogin(Boolean relogin) {
		this.relogin = relogin;
	}

	public String getAppCode() {
		return appCode;
	}

	public void setAppCode(String appCode) {
		this.appCode = appCode;
	}

	public String getLoginTime() {
		return loginTime;
	}

	public void setLoginTime(String loginTime) {
		this.loginTime = loginTime;
	}


}
