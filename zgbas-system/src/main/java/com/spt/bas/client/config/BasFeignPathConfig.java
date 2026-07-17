package com.spt.bas.client.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerTypePredicate;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * D-P4-01a path-prefix handling for the Feign self-loopback (方案 A) — Wave 4 correction.
 *
 * <p><b>Original Wave 0 approach (path stripper).</b> Plan 04-01 registered a Feign
 * {@code RequestInterceptor} that stripped the literal {@code "spt-bas-server/"} prefix from
 * outgoing Feign request URIs, so that calls like
 * {@code spt-bas-server/apply/brand/findAll} became {@code /apply/brand/findAll} — matching
 * the api controllers' bare {@code @RequestMapping("apply/brand")} at the monolith root.
 *
 * <p><b>Why it was corrected in Wave 4.</b> Once Wave 4 ported the 267 BFF controllers, an
 * {@code AmbiguousMappingException} surfaced: both the api ({@code ApplyAgreementVirtualApi},
 * Wave 3) and the BFF ({@code ApplyAgreementVirtualController}, Wave 4) registered
 * {@code POST /apply/agreementVirtual/updateFileId} at the root. In the source microservice
 * these coexisted because they ran in separate processes (basServer at
 * {@code /spt-bas-server/*} context-path, web at root {@code /}). Stripping the prefix
 * collapsed them onto the same URL space — breaking the source topology within the monolith.
 *
 * <p><b>Corrected approach (path prefix on api layer).</b> Instead of stripping the prefix
 * from Feign requests, add it BACK to the api controllers' URL mappings via
 * {@link PathMatchConfigurer#addPathPrefix(String, java.util.function.Predicate)}. This makes
 * api endpoints accessible at {@code /spt-bas-server/apply/brand/findAll} (matching the source
 * basServer context-path) while BFF endpoints stay at {@code /apply/brand/findAll} (matching
 * the source web root). The Feign contracts' {@code path = BasConstants.SERVER_NAME + "/..."}
 * now resolves directly to the api path — no stripping needed. The path prefix applies only
 * to {@code @RestController} classes in the api packages ({@code com.spt.bas.server.api},
 * {@code com.spt.pm.api}), leaving BFF {@code @Controller} classes untouched.
 *
 * <p><b>Scope.</b> The prefix is added to controllers whose package starts with
 * {@code com.spt.bas.server.api} or {@code com.spt.pm.api} (all 236 ported api controllers
 * match, including subpackages like {@code api.basData}, {@code api.sign}, etc.).
 * cfca sign clients ({@code com.spt.sign.client.remote}) are unaffected — their paths
 * never contained the prefix.
 *
 * <p><b>Phase 3 AUTH-03 preserved.</b> The Shiro {@code /login}/{@code /index} root filter
 * chain is untouched — the prefix applies ONLY to api controllers, not to web controllers.
 */
@Configuration
public class BasFeignPathConfig implements WebMvcConfigurer {

    /**
     * The literal prefix added to api controller URL mappings. Matches source
     * {@code basServer}'s {@code server.servlet.context-path=/spt-bas-server}.
     */
    public static final String API_PATH_PREFIX = "/spt-bas-server";

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.addPathPrefix(API_PATH_PREFIX,
            HandlerTypePredicate.forBasePackage(
                "com.spt.bas.server.api",
                "com.spt.pm.api"));
    }
}
