package com.spt.bas.client.vo.api;

public class ResSsoLoginRequestVo {

	/**
	 * 用户的登录名
	 */
	private String userId;

	/**
	 * token
	 */
	private String accessToken;

	/**
	 * 时间戳
	 */
	private String timestamp;

	/**
	 * 重定向地址
	 */
	private String redirectUrl;

	public String getRedirectUrl() {
		return redirectUrl;
	}

	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public String toString() {
		return "ResSsoLoginRequestVo{" +
				"userId='" + userId + '\'' +
				", accessToken='" + accessToken + '\'' +
				", timestamp='" + timestamp + '\'' +
				", redirectUrl='" + redirectUrl + '\'' +
				'}';
	}
}
