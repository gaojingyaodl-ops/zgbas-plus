package com.spt.tools.shiro.config;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;

import com.spt.tools.shiro.util.GenerateCipherKey;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.config.Ini.Section;
import org.apache.shiro.mgt.RememberMeManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.MethodInvokingFactoryBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.spt.tools.shiro.AbstractShiroDbRealm;
import com.spt.tools.shiro.ChainDefinitionSectionMetaSource;
import com.spt.tools.shiro.IShiroSection;
import com.spt.tools.shiro.IShiroService;
import com.spt.tools.shiro.ShiroChainMetaSource;
import com.spt.tools.shiro.filter.MyFormAuthenticationFilter;
import com.spt.tools.shiro.filter.MyLogoutFilter;
import com.spt.tools.shiro.filter.MyPermissionsAuthorizationFilter;
import com.spt.tools.shiro.filter.MyRolesAuthorizationFilter;

@Configuration
@EnableConfigurationProperties({ ShiroProp.class })
public class ToolsShiroConfig {
	private Logger logger = LoggerFactory.getLogger(getClass());
	private static final String NEW_LINE = "\n\r";
	private static final String base64Encoded_key = Arrays.toString(GenerateCipherKey.generateNewKey());
	private static final int MAX_AGE = 60 * 60 * 6;// 6小时， 单位s

	/**
	 * ShiroFilterFactoryBean 处理拦截资源文件问题。 注意：单独一个ShiroFilterFactoryBean配置是或报错的，以为在
	 * 初始化ShiroFilterFactoryBean的时候需要注入：SecurityManager
	 *
	 * Filter Chain定义说明 1、一个URL可以配置多个Filter，使用逗号分隔 2、当设置多个过滤器时，全部验证通过，才视为通过
	 * 3、部分过滤器可指定参数，如perms，roles
	 *
	 */
	@ConditionalOnMissingBean
	@Bean("shiroFilter")
	public ShiroFilterFactoryBean shirFilter(org.apache.shiro.mgt.SecurityManager securityManager, Section section) {
		ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();

		// 必须设置 SecurityManager
		shiroFilterFactoryBean.setSecurityManager(securityManager);
		// 如果不设置默认会自动寻找Web工程根目录下的"/login.jsp"页面
		shiroFilterFactoryBean.setLoginUrl("/login");
		// 登录成功后要跳转的链接
		shiroFilterFactoryBean.setSuccessUrl("/index");
		// 未授权界面;
		shiroFilterFactoryBean.setUnauthorizedUrl("/403");

		// 拦截器.
		shiroFilterFactoryBean.setFilterChainDefinitionMap(section);
		Map<String, Filter> filters = new HashMap<>();
		filters.put("perms", new MyPermissionsAuthorizationFilter());
		filters.put("authc", new MyFormAuthenticationFilter());
		filters.put("logout", new MyLogoutFilter());
		filters.put("roleOr", new MyRolesAuthorizationFilter());
		shiroFilterFactoryBean.setFilters(filters);
		logger.info("Shiro拦截器工厂类注入成功");
		return shiroFilterFactoryBean;
	}

	@Bean
	public org.apache.shiro.mgt.SecurityManager securityManager(EhCacheManager cacheManager,
			RememberMeManager rememberMeManager, AbstractShiroDbRealm shiroDbRealm) {
		DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
		// 设置realm.
		securityManager.setRealm(shiroDbRealm);
		securityManager.setCacheManager(cacheManager);
		securityManager.setRememberMeManager(rememberMeManager);
		return securityManager;
	}

	@Bean
	public EhCacheManager ehCacheManager() {
		EhCacheManager cacheManager = new EhCacheManager();
		cacheManager.setCacheManagerConfigFile("classpath:ehcache-shiro.xml");
		return cacheManager;
	}

	/**
	 * 身份认证realm; (这个需要自己写，账号密码校验；权限等)
	 * 
	 * @return
	 */
	/*
	 * @Bean public AbstractShiroDbRealm shiroDbRealm() { AbstractShiroDbRealm
	 * myShiroRealm = new AbstractShiroDbRealm(); return myShiroRealm; }
	 */

	@Bean
	public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
		LifecycleBeanPostProcessor lifecycleBeanPostProcessor = new LifecycleBeanPostProcessor();
		return lifecycleBeanPostProcessor;
	}

	@Bean
	public MethodInvokingFactoryBean methodInvokingFactoryBean(org.apache.shiro.mgt.SecurityManager securityManager) {
		MethodInvokingFactoryBean bean = new MethodInvokingFactoryBean();
		bean.setStaticMethod("org.apache.shiro.SecurityUtils.setSecurityManager");
		bean.setArguments(new Object[] { securityManager });
		return bean;
	}

	@Bean
	public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(
			org.apache.shiro.mgt.SecurityManager securityManager) {
		AuthorizationAttributeSourceAdvisor bean = new AuthorizationAttributeSourceAdvisor();
		bean.setSecurityManager(securityManager);
		return bean;
	}

	@Bean
	public SimpleCookie rememberMeCookie() {
		SimpleCookie cookie = new SimpleCookie("rememberMe");
		cookie.setHttpOnly(true);
		cookie.setMaxAge(MAX_AGE);//
		return cookie;
	}

	@Bean
	public CookieRememberMeManager rememberMeManager(SimpleCookie cookie) {
		CookieRememberMeManager manager = new CookieRememberMeManager();
		manager.setCookie(cookie);
		manager.setCipherKey(org.apache.shiro.codec.Base64.decode(base64Encoded_key));
		return manager;
	}

	@ConditionalOnMissingBean(value = IShiroSection.class)
	@Bean
	public IShiroSection shiroChainMetaSource(IShiroService shiroService) {
		ShiroChainMetaSource chainMetaSource = new ShiroChainMetaSource();
		chainMetaSource.setShiroService(shiroService);
		return chainMetaSource;
	}

	@Bean
	public Section chainDefinitionSectionMetaSource(IShiroSection shiroSection) {
		ChainDefinitionSectionMetaSource metaSource = new ChainDefinitionSectionMetaSource();
		metaSource.setShiroSection(shiroSection);
		// 默认授权配置
		StringBuffer config = new StringBuffer();
		config.append("/login = authc").append(NEW_LINE);
		config.append("/logout = logout");
		metaSource.setFilterChainDefinitions(config.toString());
		try {
			return metaSource.getObject();
		} catch (Exception e) {
			logger.error("chainDefinitionSectionMetaSource", e);
		}
		return null;
	}

}
