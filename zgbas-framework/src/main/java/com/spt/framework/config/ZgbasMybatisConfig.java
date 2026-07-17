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
 *
 * <p><b>Phase 5 Wave 0 (D-P5-07 part 2)</b> — broadened {@code @MapperScan} from a single
 * package string to a two-element array so the 53 ported {@code com.spt.bas.report.server.dao}
 * mappers (all {@code @MyBatisDao} annotated) get registered alongside the Phase 2
 * {@code SampleMapper}. Without this, startup hits
 * {@code org.apache.ibatis.binding.BindingException: Invalid bound statement (not found)}
 * on the first report query.
 */
@Configuration
@MapperScan(
    basePackages = {
        "com.spt.bas.system.dao",            // Phase 2 (SampleMapper)
        "com.spt.bas.report.server.dao"      // Phase 5 (53 Rpt*Mapper)
    },
    annotationClass = MyBatisDao.class
)
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
