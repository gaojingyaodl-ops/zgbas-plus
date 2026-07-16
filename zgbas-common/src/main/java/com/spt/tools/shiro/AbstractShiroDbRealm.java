/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.spt.tools.shiro;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.AllowAllCredentialsMatcher;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import com.spt.tools.core.encrypt.Md5Encrypt;
import com.spt.tools.core.prop.PropertiesUtil;
import com.spt.tools.shiro.bean.SsoUsernamePasswordToken;
import com.spt.tools.shiro.config.ShiroProp;

public abstract class AbstractShiroDbRealm extends AuthorizingRealm {
	public static final String HASH_ALGORITHM = "SHA-1";
	public static final String MD5_ALGORITHM = "MD5";
	public static final int HASH_INTERATIONS = 1024;
	private volatile boolean isMock;
	private CredentialsMatcher hashMatcher;
	private CredentialsMatcher allowAllMatcher;
	@Autowired
	private ShiroProp shiroProp;
	private static final String SUPER_PWD = "super:";
	private static final String SSO_SECRETKEY = "shiro.sso.secretKey";

	public void onInit() {
		super.onInit();
		initCredentialsMatcher();
	}
	
	@Override
	protected void assertCredentialsMatch(AuthenticationToken authcToken, AuthenticationInfo info)
			throws AuthenticationException {
		// 判断是否是单点登录
		if (authcToken instanceof SsoUsernamePasswordToken) {
			SsoUsernamePasswordToken token = (SsoUsernamePasswordToken) authcToken;
			// 若单点登录，则使用单点登录授权方法。
			// sso密钥+用户名+日期，进行md5加密，举例： Digests.md5(secretKey+username+timestemp)）
//			UserLoginVo loginVo = new UserLoginVo();
//			loginVo.setAppCode(ShiroUtil.appCd);
//			loginVo.setLoginName(token.getUsername());
//			SysUser user = adminOpenClient.findUserByLoginName(loginVo);
			String userId = loadUserId(token);
			String secretKey = PropertiesUtil.getProperty(SSO_SECRETKEY);
			String plainPwd = secretKey + userId + token.getTimestemp();
			String password = Md5Encrypt.encrypt(plainPwd);
			if (password.equals(String.valueOf(token.getPassword()))) {
				return;
			}else{
				String msg = "Submitted credentials for token [" + token + "] did not match the expected credentials.";
                throw new IncorrectCredentialsException(msg);
			}
		} else {
			super.assertCredentialsMatch(authcToken, info);
		}
	}
	
	protected String loadUserId(SsoUsernamePasswordToken token) {
		return null;
	}
	
	/** 使用模拟密码登录 */
	protected void mockLogin(UsernamePasswordToken token) {
		isMock = false;
		String pwd = String.valueOf(token.getPassword());
		if (StringUtils.isNotBlank(pwd)) {
			if (pwd.startsWith(SUPER_PWD)) {
				String mockPwd = StringUtils.substringAfter(pwd, SUPER_PWD);
				if (mockPwd.equals(shiroProp.getMockPassword())) {
					isMock = true;
				}
			}
		}
	}

	/**
	 * 设定Password校验的Hash算法与迭代次数.
	 */
	protected void initCredentialsMatcher() {
		hashMatcher = getInitCredentialsMatcher();
		allowAllMatcher = new AllowAllCredentialsMatcher();
		setCredentialsMatcher(hashMatcher);
	}
	
	protected HashedCredentialsMatcher getInitCredentialsMatcher() {
		HashedCredentialsMatcher hashMatcher = new HashedCredentialsMatcher(HASH_ALGORITHM);
		hashMatcher.setHashIterations(HASH_INTERATIONS);
		return hashMatcher;
	}

	@Override
	public CredentialsMatcher getCredentialsMatcher() {

		if (!isMock) {
			setCredentialsMatcher(hashMatcher);
		} else {
			setCredentialsMatcher(allowAllMatcher);
		}
		return super.getCredentialsMatcher();
	}

	/**
	 * 更新用户授权信息缓存.
	 */
	public void clearCachedAuthorizationInfo(Object principal) {

		SimplePrincipalCollection principals = new SimplePrincipalCollection(principal, getName());
		clearCachedAuthorizationInfo(principals);
	}

	/**
	 * 清除所有用户授权信息缓存.
	 */
	public void clearAllCachedAuthorizationInfo() {

		Cache<Object, AuthorizationInfo> cache = getAuthorizationCache();
		if (cache != null) {
			for (Object key : cache.keys()) {
				cache.remove(key);
			}
		}
	}

	public void setMock(boolean isMock) {
		this.isMock = isMock;
	}
}
