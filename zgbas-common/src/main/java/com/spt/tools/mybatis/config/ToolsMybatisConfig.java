package com.spt.tools.mybatis.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.baomidou.mybatisplus.extension.plugins.PerformanceInterceptor;
import com.spt.tools.data.config.DataSourceConfig;
import com.spt.tools.data.util.DataSourceCreator;
import com.spt.tools.mybatis.interceptor.MyMetaObjectHandler;

@Configuration
public class ToolsMybatisConfig {
	private static Logger log = LoggerFactory.getLogger(ToolsMybatisConfig.class);

	@Bean("datasource") // 声明其为Bean实例
	@ConditionalOnBean(DataSourceConfig.class)
	@ConditionalOnMissingBean
//	@Primary // 在同样的DataSource中，首先使用被标注的DataSource
	public javax.sql.DataSource dataSource(DataSourceConfig config) {
		return DataSourceCreator.createDataSource(config);
	}

//	/**
//	 * 分页插件
//	 */
//	@Bean
//	public PaginationInterceptor paginationInterceptor() {
//		return new PaginationInterceptor();
//	}
	
	/**
	 * 属性填充拦截器
	 */
	@Bean
	public MyMetaObjectHandler myMetaObjectHandler() {
		return new MyMetaObjectHandler();
	}

	/**
	 * SQL执行效率插件
	 */
	@Bean
	@Profile({ "!prod" }) // 设置 生产环境不开启
	public PerformanceInterceptor performanceInterceptor() {
		return new PerformanceInterceptor();
	}
}
