package com.spt.bas.purchase.wx.client.config;

import com.spt.bas.purchase.wx.client.constant.PurchaseWxConstant;
import com.spt.tools.core.bean.LocalServerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

@Configuration
public class PurchaseWxClientConfig {

	@DependsOn({"propertiesUtil"})
	@Bean(PurchaseWxConstant.SERVER_BEAN_NAME)
	public LocalServerConfig localServerConfig() {
		LocalServerConfig conf = new LocalServerConfig();
		conf.setUrlKey(PurchaseWxConstant.SERVER_URL_KEY);
		return conf;
	}
}
