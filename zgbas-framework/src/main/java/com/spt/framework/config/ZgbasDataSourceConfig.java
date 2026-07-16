package com.spt.framework.config;

import com.spt.tools.data.config.DataSourceConfig;
import com.spt.tools.data.util.DataSourceCreator;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**
 * Dual-ORM persistence root: the single Druid {@link DataSource} that both
 * {@code EntityManagerFactory} (JPA auto-config) and {@code SqlSessionFactory}
 * (mybatis-plus auto-config) bind to (PERSIST-03).
 *
 * <p>Derived from {@code basServer/config/FrameworkConfig.java:33-37} (prefix binding)
 * and {@code spt-tools-jpa/.../ToolsJpaConfig.java} / {@code spt-tools-mybatis/.../ToolsMybatisConfig.java}
 * (the colliding {@code @Bean("datasource")} both of these defeat).
 */
@Configuration
public class ZgbasDataSourceConfig {

    /**
     * D-P2-15: prefix unified from {@code bas.datasource.*} to
     * {@code spring.datasource.druid.*}. The {@link DataSourceConfig} POJO is
     * prefix-agnostic (same 14 Druid pool fields); only the binding prefix changes.
     */
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.druid")
    public DataSourceConfig dataSourceConfig() {
        return new DataSourceConfig();
    }

    /**
     * The load-bearing bean: declares the primary Druid {@link DataSource}.
     *
     * <p>{@code @Primary} makes this DataSource win deterministically over the two
     * spt-tools candidates ({@link com.spt.tools.jpa.config.ToolsJpaConfig#dataSource}
     * and {@link com.spt.tools.mybatis.config.ToolsMybatisConfig#dataSource}), both of
     * which declare {@code @Bean("datasource") @ConditionalOnMissingBean} with
     * {@code @Primary} commented out (Pitfall 1 — the bean-collision race that makes
     * tests flaky). Because this bean registers a DataSource named "datasource" first,
     * both spt-tools beans back off via {@code @ConditionalOnMissingBean}.
     *
     * <p>{@link DataSourceCreator#createDataSource} is prefix-agnostic; it consumes the
     * bound {@link DataSourceConfig} and returns a fully configured {@code DruidDataSource}.
     */
    @Bean("datasource")
    @Primary
    public DataSource dataSource(DataSourceConfig config) {
        return DataSourceCreator.createDataSource(config);
    }
}
