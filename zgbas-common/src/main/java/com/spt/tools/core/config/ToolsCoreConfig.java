package com.spt.tools.core.config;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.spt.tools.core.bean.RandomBean;
import com.spt.tools.core.cmd.CommandExecutor;
import com.spt.tools.core.prop.PropertiesUtil;
import com.spt.tools.core.util.SpringContextHolder;

@Configuration
public class ToolsCoreConfig {

	@Bean
	public SpringContextHolder springContextHolder(ApplicationContext applicationContext) {
		SpringContextHolder contextHolder = new SpringContextHolder();
		contextHolder.setApplicationContext(applicationContext);
		return contextHolder;
	}

	@Bean
	public PropertiesUtil propertiesUtil(Environment environment) {
		PropertiesUtil propertiesUtil = new PropertiesUtil();
		propertiesUtil.setEnvironment(environment);
		return propertiesUtil;
	}

	@Bean("random")
	public RandomBean random() {
		return new RandomBean();
	}

	@Bean
	public CommandExecutor commandExecutor() {
		return new CommandExecutor();
	}
}
