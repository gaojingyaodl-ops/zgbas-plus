package com.spt.tools.http.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import com.spt.tools.http.interceptor.PageInterceptor;
import com.spt.tools.http.remote.IRemoteService;
import com.spt.tools.http.remote.RemoteServiceImpl;

@Configuration
public class ToolsHttpConfig {

	@Bean("restTemplateUrl")
	public RestTemplate restTemplateUrl() {
		RestTemplate template = new RestTemplate();
		SimpleClientHttpRequestFactory factory = (SimpleClientHttpRequestFactory) template.getRequestFactory();
		factory.setConnectTimeout(5000);
		factory.setReadTimeout(30000);
		return template;
	}

	@Bean
	public IRemoteService remoteService() {
		IRemoteService remoteService = new RemoteServiceImpl();
		remoteService.setRestTemplate(restTemplateUrl());
		return remoteService;
	}
	
	@Bean
	public PageInterceptor pageInterceptor() {
		PageInterceptor interceptor = new PageInterceptor();
		return interceptor;
	}

}
