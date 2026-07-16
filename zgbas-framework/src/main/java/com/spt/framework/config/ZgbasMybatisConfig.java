package com.spt.framework.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.spt.tools.mybatis.annotation.MyBatisDao;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * mybatis-plus side of the dual-ORM wiring (PERSIST-03 / D-P2-04).
 *
 * <p>This class owns the framework-specific concerns that cannot live in the inlined
 * {@code com.spt.tools.mybatis.config.ToolsMybatisConfig} (which already provides the
 * {@code MyMetaObjectHandler} and {@code @Profile("!prod") PerformanceInterceptor} beans
 * verbatim from source): the {@code @MapperScan} targeting zgbas's own mapper package
 * using the inlined {@link MyBatisDao} marker, plus the {@link PaginationInterceptor}
 * that is commented out in the source ToolsMybatisConfig.
 *
 * <p>No {@code DataSource} or {@code SqlSessionFactory} {@code @Bean} is declared here —
 * mybatis-plus-boot-starter auto-config binds to the {@code @Primary} DataSource for free
 * (RESEARCH §"Don't Hand-Roll"). The {@code PaginationInterceptor} is the only addition.
 */
@Configuration
@MapperScan(basePackages = "com.spt.bas.system.dao", annotationClass = MyBatisDao.class)
public class ZgbasMybatisConfig {

    /**
     * Page helper for the sample mapper (D-P2-04). Not strictly required for a
     * {@code select count(*)} query, but mirrors the source reportServer wiring so the
     * interceptor chain is in place before Phase 5 report mappers arrive.
     */
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        return new PaginationInterceptor();
    }
}
