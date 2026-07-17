package com.spt.bas.report.client.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import com.spt.bas.report.client.constant.ReportConstant;
import com.spt.tools.core.bean.LocalServerConfig;

@Configuration
public class ReportClientConfig {
	
	@DependsOn({"propertiesUtil"})
	@Bean(ReportConstant.SERVER_BEAN_NAME)
	public LocalServerConfig localServerConfig() {
		LocalServerConfig conf = new LocalServerConfig();
		conf.setUrlKey(ReportConstant.SERVER_URL_KEY);
		return conf;
	}
}
