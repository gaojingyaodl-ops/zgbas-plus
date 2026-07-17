package com.spt.bas.client.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerTypePredicate;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * D-P5-03 path-prefix handling for the report Feign self-loopback — Phase 5 Wave 0 (05-01).
 *
 * <p>Verbatim clone of {@link BasFeignPathConfig} (Phase 4 Wave 4 D-P4-01a), with two constants
 * changed: prefix {@code "/spt-bas-report"} and basePackage {@code "com.spt.bas.report.server.api"}
 * (single package, not the dual basServer/pm scan of the bas variant).
 *
 * <p><b>Why mandatory.</b> The source ReportServer(8002) ran with
 * {@code server.servlet.context-path=/spt-bas-report}. In the monolith, the root must stay
 * un-prefixed (Phase 3 AUTH-03 Shiro root filter chain at {@code /}). Two problems arise if the
 * 54 report api {@code @RestController}s land without a prefix:
 *
 * <ol>
 *   <li><b>14 BFF path collisions</b> — Phase 4 already ported 14 BFF controllers whose
 *       {@code @RequestMapping} top-level paths literally overlap with report api paths
 *       (e.g. {@code /rpt/fundReceivableStatistics}, {@code /business/manager/workbench},
 *       {@code /bs/company}, {@code /evaluate/total}, {@code /rpt/business}, etc.). Same-process
 *       same-URL → {@code AmbiguousMappingException} at startup. (Same failure mode as Phase 4
 *       D-P4-01a Wave 4, which forced the original path-stripper rewrite.)
 *   <li><b>Feign contract path diverge</b> — every {@code IRpt*Client} uses
 *       {@code path = ReportConstant.SERVER_NAME + "/rpt/..."} (= {@code "spt-bas-report/rpt/..."}).
 *       Without the prefix, the proxy lands on {@code /spt-bas-report/rpt/foo} but the api is
 *       mapped at bare {@code /rpt/foo} → 404.
 * </ol>
 *
 * <p><b>Fix.</b> Add {@code /spt-bas-report} back to the api controllers' URL mappings via
 * {@link PathMatchConfigurer#addPathPrefix(String, java.util.function.Predicate)}. The 54 report
 * api endpoints become accessible at {@code /spt-bas-report/rpt/foo} (matching source ReportServer
 * context-path), while BFF endpoints stay at root (matching source web root). The Feign contracts'
 * {@code path} now resolves directly to the api path — no stripping needed. The 9 basServer
 * service impls + multiple BFF controllers that {@code @Autowired IRpt*Client} (Phase 4 D-P4-02
 * lazy-degradation) become real calls once Phase 5 Wave 5 ports the api controllers.
 *
 * <p><b>Scope.</b> The prefix applies only to {@code @RestController} classes whose package starts
 * with {@code com.spt.bas.report.server.api}. {@link BasFeignPathConfig} retains its separate
 * {@code /spt-bas-server} prefix on {@code com.spt.bas.server.api} / {@code com.spt.pm.api}.
 *
 * <p><b>Phase 3 AUTH-03 preserved.</b> The Shiro {@code /login}/{@code /index} root filter chain
 * is untouched — the prefix applies ONLY to report api {@code @RestController}s, not to web
 * {@code @Controller}s.
 */
@Configuration
public class ReportFeignPathConfig implements WebMvcConfigurer {

    /**
     * The literal prefix added to report api controller URL mappings. Matches source
     * {@code ReportServer}'s {@code server.servlet.context-path=/spt-bas-report}.
     */
    private static final String API_PATH_PREFIX = "/spt-bas-report";

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.addPathPrefix(API_PATH_PREFIX,
            HandlerTypePredicate.forBasePackage("com.spt.bas.report.server.api"));
    }
}
