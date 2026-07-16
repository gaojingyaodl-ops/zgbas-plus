/**
 * 
 */
package com.spt.tools.shiro.util;

import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.Filter;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.config.Ini.Section;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.PathMatchingFilter;
import org.apache.shiro.web.filter.mgt.DefaultFilterChainManager;
import org.apache.shiro.web.filter.mgt.PathMatchingFilterChainResolver;
import org.apache.shiro.web.servlet.AbstractShiroFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.spt.tools.core.bean.ShiroUser;
import com.spt.tools.core.reflect.Reflections;
import com.spt.tools.core.util.SpringContextHolder;
import com.spt.tools.shiro.AbstractShiroDbRealm;
import com.spt.tools.shiro.IShiroSection;

/**
 * @author huangjian
 * 
 */
public class ShiroUtil {
	private static Logger logger = LoggerFactory.getLogger(ShiroUtil.class);
	public static volatile String appCd;
	public static volatile boolean filterInited = false;

	/**
	 * 取出Shiro中的当前用户Id.
	 */
	public static Long getCurrentUserId() {
		if (SecurityUtils.getSubject().getPrincipal() != null) {
			ShiroUser shiroUser = (ShiroUser) SecurityUtils.getSubject().getPrincipal();
			return shiroUser.id;
		}
		return 0L;
	}


	protected static <T> T getProp(String key) {
		ShiroUser user = getShiroUser();
		if (user != null) {
			T val =  (T)user.getProp().get(key);
			return val;
		}
		return null;
	}
	protected static String getPropStr(String key) {
		ShiroUser user = getShiroUser();
		if (user != null) {
			String deptAbbr = (String) user.getProp().get(key);
			return deptAbbr;
		}
		return null;
	}

	public static ShiroUser getShiroUser() {
		if (SecurityUtils.getSubject().getPrincipal() != null) {
			ShiroUser shiroUser = (ShiroUser) SecurityUtils.getSubject().getPrincipal();
			return shiroUser;
		}
		return null;
	}

	public static String getCurrentUserName() {
		if (SecurityUtils.getSubject().getPrincipal() != null) {
			ShiroUser shiroUser = (ShiroUser) SecurityUtils.getSubject().getPrincipal();
			return shiroUser.name;
		}
		return "";
	}

	public static boolean isLogin() {
		ShiroUser shiroUser = (ShiroUser) SecurityUtils.getSubject().getPrincipal();
		if (shiroUser != null) {
			return true;
		}
		return false;
	}

	/** 判断角色 */
	public static boolean hasRole(String role) {

		Subject currentUser = SecurityUtils.getSubject();
		return currentUser.hasRole(role);
	}

	/** 判断权限 */
	public static boolean isPermitted(String permCode) {

		Subject currentUser = SecurityUtils.getSubject();
		return currentUser.isPermitted(permCode);
	}

	/**
	 * 更新Shiro中当前用户的用户名.
	 */
	public static void updateCurrentUserName(String userName) {
		ShiroUser shiroUser = (ShiroUser) SecurityUtils.getSubject().getPrincipal();
		shiroUser.name = userName;
	}

	public static void clean() {
		ShiroUtil.reloadShiroFilterChains();
		ShiroUtil.clearAllCachedAuthorizationInfo();
	}

	private static DefaultFilterChainManager getFilterChainManager() {

		return ((DefaultFilterChainManager) ((PathMatchingFilterChainResolver) ((AbstractShiroFilter) SpringContextHolder
				.getBean("shiroFilter")).getFilterChainResolver()).getFilterChainManager());
	}

	public static void reloadShiroFilterChains() {

		logger.info("----reloadShiroFilterChains");
		Section section = SpringContextHolder.getBean("chainDefinitionSectionMetaSource");
		section.clear();
		IShiroSection shiroSection = SpringContextHolder.getBean(IShiroSection.class);
		shiroSection.initSection(section);
		DefaultFilterChainManager chainManager = getFilterChainManager();
		for (Entry<String, Filter> filterEntry : chainManager.getFilters().entrySet()) {
			if (PathMatchingFilter.class.isInstance(filterEntry.getValue())) {
				PathMatchingFilter filter = PathMatchingFilter.class.cast(filterEntry.getValue());
				Map<String, Object> appliedPaths = (Map<String, Object>) Reflections.getFieldValue(filter,
						"appliedPaths");
				synchronized (appliedPaths) {
					appliedPaths.clear();
				}
			}
		}
		synchronized (chainManager.getFilterChains()) {
			chainManager.getFilterChains().clear();
			for (Entry<String, String> chain : section.entrySet()) {
				if (StringUtils.isNoneBlank(chain.getKey())) {
					chainManager.createChain(chain.getKey(), chain.getValue());
				}
			}
		}
	}

	/** 清除所有用户授权信息缓存 */
	public static void clearAllCachedAuthorizationInfo() {

		logger.info("----clearAllCachedAuthorizationInfo");
		AbstractShiroDbRealm realm = SpringContextHolder.getBean(AbstractShiroDbRealm.class);
		realm.clearAllCachedAuthorizationInfo();
	}

}
