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
package com.spt.bas.web.shiro;

import com.ruoyi.common.enums.UserStatus;
import com.ruoyi.common.utils.IpUtils;
import com.spt.auth.sdk.entity.*;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.auth.sdk.vo.UserLoginVo;
import com.spt.bas.client.cache.BsDictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.BsDictData;
import com.spt.bas.web.config.ShiroPropConfig;
import com.spt.bas.web.util.ServletUtils;
import com.spt.tools.core.bean.ShiroUser;
import com.spt.tools.core.collection.CollectionUtil;
import com.spt.tools.core.encrypt.Encodes;
import com.spt.tools.core.encrypt.Md5Encrypt;
import com.spt.tools.shiro.AbstractShiroDbRealm;
import eu.bitwalker.useragentutils.UserAgent;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class ShiroDbRealm extends AbstractShiroDbRealm {

	private static final Logger log = LoggerFactory.getLogger(ShiroDbRealm.class);
	@Autowired
	private IAuthOpenFacade authOpenFacade;
	@Autowired
	private ShiroPropConfig shiroPropConfig;
	private static final String SUPER_PWD = "super:";
	@Value("${zgBas.secret}")
	private String secretKey;

	/**
	 * 设置用户代理信息
	 *
	 * @param loginVo 登录信息
	 */
	public void setUserLoginAgent(UserLoginVo loginVo) {
		UserAgent userAgent = UserAgent.parseUserAgentString(ServletUtils.getRequest().getHeader("User-Agent"));
		String ip = IpUtils.getIpAddr(ServletUtils.getRequest());
        loginVo.setIpaddr(ip);
        loginVo.setBrowser(userAgent.getBrowser().getName());
        loginVo.setOs(userAgent.getOperatingSystem().getName());
	}

	/**
	 * 是否模拟超级账号
	 * @param token
	 * @return
	 */
	protected boolean isMockLogin(UsernamePasswordToken token) {
		boolean isMock = false;
		String pwd = String.valueOf(token.getPassword());
		if (StringUtils.isNotBlank(pwd)) {
			if (pwd.startsWith(SUPER_PWD)) {
				String mockPwd = StringUtils.substringAfter(pwd, SUPER_PWD);
				if (mockPwd.equals(shiroPropConfig.getMockPassword())) {
					isMock = true;
					UserAgent userAgent = UserAgent.parseUserAgentString(ServletUtils.getRequest().getHeader("User-Agent"));
					String ip = IpUtils.getIpAddr(ServletUtils.getRequest());
					log.info(">>>>>isMockLogin<<<<< user:{}, IP:{}, Browser:{}, Os:{}", token.getUsername(), ip, userAgent.getBrowser().getName(), userAgent.getOperatingSystem().getName());
				}
			}
		}
		return isMock;
	}
	/**
	 * 认证回调函数,登录时调用.
	 */
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authcToken) throws AuthenticationException {
		UsernamePasswordToken token = (UsernamePasswordToken) authcToken;
		//支持超级密码登录
		mockLogin(token);

		UserLoginVo userLoginVo = new UserLoginVo(ShiroUtil.appCd, token.getUsername() , String.valueOf(token.getPassword()));
		//userLoginVo.setSso(token.getSsoLogin());
		try {
			setUserLoginAgent(userLoginVo);
			userLoginVo.setMock(isMockLogin(token));
			com.spt.auth.sdk.entity.ShiroUser login = authOpenFacade.login(userLoginVo);
			if (!(Objects.nonNull(login) && StringUtils.isNotBlank(login.loginName))) {
				com.spt.auth.sdk.entity.ShiroUser reLoginSso = reLoginSso(token, userLoginVo);
				if (Objects.isNull(reLoginSso) || StringUtils.isBlank(reLoginSso.loginName)) {
					return null;
				}
			}
		} catch (Exception e) {
			log.error("登录失败：{}",e);
			return null;
		}
		SysUserSdk user = authOpenFacade.findUserByLoginName(userLoginVo);
		if (Objects.nonNull(user)) {
			if(StringUtils.equals(UserStatus.DISABLE.getCode(),user.getStatus()) ||
					StringUtils.equals(UserStatus.DELETED.getCode(),user.getDelFlag())) {
				return null;
			}
			byte[] salt = Encodes.decodeHex(user.getSalt());
			ShiroUser shiroUser = new ShiroUser(user.getUserId(), user.getUserName(), user.getNickName());
			Long appId = getAppId();
			if(Objects.isNull(appId)){
				return null;
			}
			shiroUser.addProp(ShiroUtil.APPID, appId);
			shiroUser.addProp(ShiroUtil.ENTERPRISEID, user.getEnterpriseId());
			if (Objects.nonNull(user.getDept())) {
				shiroUser.addProp(ShiroUtil.DEPTID, user.getDept().getDeptId());
				shiroUser.addProp(ShiroUtil.DEPTABBR, user.getDept().getDeptAbbr());
			}
			SysEnterpriseSdk enterprise = authOpenFacade.findEnterpriseById(user.getEnterpriseId());
			if (enterprise != null) {
				shiroUser.addProp(ShiroUtil.INDUSTRY, enterprise.getIndustry());
			}
			return new SimpleAuthenticationInfo(shiroUser, user.getPassword(), ByteSource.Util.bytes(salt), getName());
		} else {
			return null;
		}
	}

	private com.spt.auth.sdk.entity.ShiroUser reLoginSso(UsernamePasswordToken token, UserLoginVo userLoginVo) {
		LocalDate localDate = LocalDate.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		String timestamp = localDate.format(formatter);
		String ticket = Md5Encrypt.encrypt(secretKey + token.getUsername() + timestamp).toLowerCase();
		if (!StringUtils.equalsIgnoreCase(ticket, String.valueOf(token.getPassword()))) {
			log.error("token验证失败,ticket = {} ,vo.getAccessToken() = {}", ticket, String.valueOf(token.getPassword()));
			throw new AuthenticationException("msg:token验证失败，请联系管理员。");
		} else {
			userLoginVo.setSso(true);
			return authOpenFacade.login(userLoginVo);
		}
	}

	private Long getAppId() {
		Long appId = null;
		// 系统对应的应用
		List<BsDictData> dictDatas = BsDictUtil.getListByCategory(BasConstants.ZG_ENTERPRISE_ID, BasConstants.BS_DICT_TYPE_SYSCONFIG);
		for (BsDictData bsDictData : dictDatas) {
			// 查找appCode
			if(BasConstants.ZG_APP_CODE.equals(bsDictData.getDictCd()) && bsDictData.getEnableFlg()){
				String appCode = bsDictData.getDictName();
				SysAppSdk app = authOpenFacade.findAppByCode(appCode);
				if(Objects.isNull(app)){
					log.error("com.spt.bas.web.shiro.ShiroDbRealm.getAppId() -> 获取应用id失败！");
					return null;
				}
				return app.getId();
			}
		}
		log.error("com.spt.bas.web.shiro.ShiroDbRealm.getAppId() -> 获取应用id失败！");
		return appId;
	}

	@Override
	protected void assertCredentialsMatch(AuthenticationToken authcToken, AuthenticationInfo info)
			throws AuthenticationException {
		// 注意，这个地方不需要再验证密码等的合法性，权限系统那边已经验证过了
		// 也不需要super.assertCredentialsMatch(authcToken, info);这个方法还是会验证密码合法性，单点登录的时候会被拦截下来
		//if (authcToken instanceof SsoUsernamePasswordToken) {
		//
		//	SsoUsernamePasswordToken token = (SsoUsernamePasswordToken) authcToken;
		//	// 若单点登录，则使用单点登录授权方法。
		//	// sso密钥+用户名+日期，进行md5加密，举例： Digests.md5(secretKey+username+20150101)）
		//	UserLoginVo userLoginVo = new UserLoginVo(ShiroUtil.appCd, token.getUsername() , null);
		//	SysUserSdk user = authOpenFacade.findUserByLoginName(userLoginVo);
		//	String secretKey = PropertiesUtil.getProperty("shiro.sso.secretKey");
		//	String plainPwd = secretKey + user.getUserId() + DateOperator.formatDate(new Date(), "yyyyMMdd");
		//	String password = Md5EncryptUtils.encrypt(plainPwd);
		//	if (!StringUtils.equals(String.valueOf(token.getPassword()), password)){
		//		String msg = "Submitted credentials for token [" + token + "] did not match the expected credentials.";
		//		throw new IncorrectCredentialsException(msg);
		//	}
		//} else {
		//	super.assertCredentialsMatch(authcToken, info);
		//}
	}

	/**
	 * 授权查询回调函数, 进行鉴权但缓存中无用户的授权信息时调用.
	 */
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		ShiroUser shiroUser = (ShiroUser) principals.getPrimaryPrincipal();
		UserLoginVo userLoginVo = new UserLoginVo(ShiroUtil.appCd, shiroUser.loginName , null);
		SysUserSdk user = authOpenFacade.findUserByLoginName(userLoginVo);
		SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
		List<SysRoleSdk> roles = authOpenFacade.findRoleByUserId(user.getUserId());
		List<String> roleCds = CollectionUtil.getPropList(roles, "roleKey");
		info.addRoles(roleCds);
		info.addStringPermissions(getAllPerms(roles));
		return info;
	}

	private List<String> getAllPerms(List<SysRoleSdk> roles) {
		List<SysMenuSdk> permissions = new ArrayList<>();
		for (SysRoleSdk role : roles) {
			if (role.getPermissions() != null) {
				permissions.addAll(role.getPermissions());
			}
		}
		return CollectionUtil.getPropList(permissions, "perms");
	}
}
