package com.spt.tools.http.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * 页面请求拦截器
 * 
 * @author jian
 */
public class PageInterceptor extends HandlerInterceptorAdapter {
	private final static String SITE_MAIN="site.main";
	private static Logger log = LoggerFactory.getLogger(PageInterceptor.class);
	@Autowired
	private Environment env;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		String mainIndex = env.getProperty(SITE_MAIN);
		request.getSession().setAttribute("mainIndex", mainIndex);
		log.debug(request.getRequestURL().toString());
		request.setAttribute("g_filePath", env.getProperty("file.show.url"));
		request.setAttribute("staticPath", env.getProperty("project.static.path"));
		request.setAttribute("g_serverName", request.getServerName());
		request.setAttribute("ctx", request.getContextPath());
		return super.preHandle(request, response, handler);
	}

}
