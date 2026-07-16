package com.spt.tools.aop.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.spt.tools.aop.dao.TaskMonitorDao;
import com.spt.tools.aop.interceptor.ServiceAop;
import com.spt.tools.aop.interceptor.TaskInterceptor;
import com.spt.tools.core.prop.PropertiesUtil;

@Configuration
@ConditionalOnBean(PropertiesUtil.class)
public class ToolsAopConfig {

	@Bean
	public ServiceAop serviceAop() {
		return new ServiceAop();
	}
	@Bean
	public TaskInterceptor taskInterceptor() {
		return new TaskInterceptor();
	}
	@Bean
	public TaskMonitorDao taskMonitorDao() {
		return new TaskMonitorDao();
	}
	

}
