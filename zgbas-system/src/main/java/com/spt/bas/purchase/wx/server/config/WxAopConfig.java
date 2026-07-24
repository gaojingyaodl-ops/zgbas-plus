package com.spt.bas.purchase.wx.server.config;

import com.spt.bas.purchase.wx.server.aop.ServiceAop;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Phase 5 Plan 05-03 (D-P5-03): explicit @Bean registration of the WX
 * {@link ServiceAop} aspect.
 *
 * <p>The aspect class carries only {@code @Aspect} (no {@code @Component}), so
 * Spring component scanning will not register it — it must be declared as a bean
 * here, mirroring the spt-tools {@code ToolsAopConfig} pattern.
 *
 * <p>The pointcut {@code execution(* com.*.*.*.*.*.service..*.*(..))} uses five
 * single-segment wildcards, so it matches only the seven-segment path
 * {@code com.spt.bas.purchase.wx.server.service..} and does NOT advise the main
 * domain ({@code com.spt.bas.server.service}, five segments) nor overlap the
 * spt-tools annotation-gated ServiceAop (three wildcards + {@code @ServiceLogAop}
 * / {@code @ServiceExceptionAop}). No double-advice.
 */
@Configuration
public class WxAopConfig {

    @Bean
    public ServiceAop wxServiceAop() {
        return new ServiceAop();
    }
}
