/**
 * 
 */
package com.spt.tools.core.bean;

import com.spt.tools.core.prop.PropertiesUtil;

/**
 * 本地服务信息配置
 * 
 * @author wlddh
 *
 */
public class LocalServerConfig {
	private String urlKey;

	public String getUrl() {
		String url = PropertiesUtil.getProperty(urlKey);// BasConstants.SERVER_URL_KEY
		return url;
	}

	public String getUrlKey() {
		return urlKey;
	}

	public void setUrlKey(String urlKey) {
		this.urlKey = urlKey;
	}
}
