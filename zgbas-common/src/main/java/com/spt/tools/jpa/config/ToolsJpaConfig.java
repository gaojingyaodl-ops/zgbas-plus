package com.spt.tools.jpa.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.jdbc.support.lob.LobHandler;

import com.spt.tools.data.config.DataSourceConfig;
import com.spt.tools.data.util.DataSourceCreator;
import com.spt.tools.jpa.dao.CommonDao;
import com.spt.tools.jpa.dao.CommonDaoImpl;

@Configuration
public class ToolsJpaConfig {
	private Logger log = LoggerFactory.getLogger(ToolsJpaConfig.class);

	@Bean
	@Lazy
	public LobHandler lobHandler() {
		return new DefaultLobHandler();
	}

	@Bean
	public CommonDao commonDao() {
		CommonDao commonDao = new CommonDaoImpl();
		return commonDao;
	}

	@Bean("datasource") // 声明其为Bean实例
	@ConditionalOnBean(DataSourceConfig.class)
	@ConditionalOnMissingBean
//	@Primary // 在同样的DataSource中，首先使用被标注的DataSource
	public javax.sql.DataSource dataSource(DataSourceConfig config) {
		return DataSourceCreator.createDataSource(config);
	}
	
}
