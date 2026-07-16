/**
 * 
 */
package com.spt.tools.data.util;

import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.pool.DruidDataSource;
import com.spt.tools.data.config.DataSourceConfig;

/**
 * @author wlddh
 *
 */
public class DataSourceCreator {
	private static Logger log = LoggerFactory.getLogger(DataSourceCreator.class);

	public static javax.sql.DataSource createDataSource(DataSourceConfig config) {
		DruidDataSource datasource = new DruidDataSource();
		datasource.setUrl(config.getUrl());
		datasource.setUsername(config.getUsername());
		datasource.setPassword(config.getPassword());
		datasource.setDriverClassName(config.getDriver());
		// configuration
		datasource.setInitialSize(config.getInitialSize());
		datasource.setMinIdle(config.getMinIdle());
		datasource.setMaxActive(config.getMaxActive());
		datasource.setMaxWait(config.getMaxWait());
		datasource.setTimeBetweenEvictionRunsMillis(config.getTimeBetweenEvictionRunsMillis());
		datasource.setMinEvictableIdleTimeMillis(config.getMinEvictableIdleTimeMillis());
		datasource.setValidationQuery(config.getValidationQuery());
		datasource.setTestWhileIdle(config.isTestWhileIdle());
		datasource.setTestOnBorrow(config.isTestOnBorrow());
		datasource.setTestOnReturn(config.isTestOnReturn());
		datasource.setPoolPreparedStatements(config.isPoolPreparedStatements());
		datasource.setMaxPoolPreparedStatementPerConnectionSize(config.getMaxPoolPreparedStatementPerConnectionSize());
		// 配置removeAbandoned对性能会有一些影响，建议怀疑存在泄漏之后再打开。在上面的配置中，如果连接超过30分钟未关闭，就会被强行回收，并且日志记录连接申请时的调用堆栈。
		datasource.setRemoveAbandoned(true);
		datasource.setLogAbandoned(true);
		datasource.setRemoveAbandonedTimeout(1800);// 30分钟
		try {
			datasource.setFilters(config.getFilters());
		} catch (SQLException e) {
			log.error("druid configuration initialization filter", e);
		}
		datasource.setConnectionProperties(config.getConnectionProperties());
		return datasource;
	}
}
