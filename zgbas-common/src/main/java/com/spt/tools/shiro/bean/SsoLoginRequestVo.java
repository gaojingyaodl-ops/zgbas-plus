package com.spt.tools.shiro.bean;

/**
 * 单点登录（如已经登录，则直接跳转）
 * 
 * @param userId    登录用户编码
 * @param token     登录令牌，令牌组成：sso密钥+用户名+时间戳，进行md5加密，举例： String secretKey =
 *                  PropertiesUtil.getProperty("shiro.sso.secretKey"); String
 *                  token = Md5EncryptUtils.encrypt(secretKey + userCode +
 *                  timestemp;
 * @param timestemp 登录时间戳，当前毫秒数
 * @param appCode   应用代码
 * @param url       登录成功后跳转的url地址。URLEncode地址
 * @param relogin   是否重新登录，需要重新登录传递true
 */
public class SsoLoginRequestVo {
	private String userId;
	private String token;
	private String appCode;
	private long timestemp;
	private String redirectUrl;
	private Boolean relogin = false;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
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

	public long getTimestemp() {
		return timestemp;
	}

	public void setTimestemp(long timestemp) {
		this.timestemp = timestemp;
	}

}
