/**
 * 
 */
package com.spt.tools.shiro.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.spt.tools.shiro.util.ShiroUtil;

/**
 * @author wlddh
 *
 */
@ConfigurationProperties(prefix = "shiro.prop")
public class ShiroProp {

	private String appCd;
	private String mockPassword;

	public void setAppCd(String appCd) {
		this.appCd = appCd;
		ShiroUtil.appCd = appCd;
	}

	public String getMockPassword() {
		return mockPassword;
	}

	public void setMockPassword(String mockPassword) {
		this.mockPassword = mockPassword;
	}

	public String getAppCd() {
		return appCd;
	}
}
