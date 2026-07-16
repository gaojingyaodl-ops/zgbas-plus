/**
 * 
 */
package com.spt.tools.shiro.filter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.shiro.web.filter.authc.LogoutFilter;

/**
 * @author huangjian
 * 
 */
public class MyLogoutFilter extends LogoutFilter {
	
//	private Logger log = Logger.getLogger(this.getClass());
	@Override
	protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
		return super.preHandle(request, response);
	}

	@Override
	protected void postHandle(ServletRequest request, ServletResponse response) throws Exception {
		super.postHandle(request, response);
	}
}
