package com.spt;

import com.spt.tools.jpa.vo.IdEntity;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * zgbas-plus monolith boot class.
 *
 * <p>Keeps {@link SpringBootApplication} with NO auto-config exclusions: we WANT
 * DataSource + JPA + mybatis-plus auto-configuration to engage. Three infra annotations
 * scope the data and feign surface:
 *
 * <ul>
 *   <li>{@link EnableFeignClients} narrowed to {@code com.spt.sign.client.remote} ONLY
 *       (D-P2-12). The 295 internal {@code @FeignClient} interfaces become pure contracts
 *       satisfied by local {@code @RestController} impls in Phase 4; narrowing prevents the
 *       double-bean conflict (Pitfall 5).</li>
 *   <li>{@link EntityScan} with {@code basePackageClasses = IdEntity.class} so
 *       {@code com.spt.tools.jpa.vo} (where {@link IdEntity} lives) is entity-scanned
 *       alongside {@code com.spt.bas.client.entity} (239 entities from Plan 05). Missing
 *       the IdEntity package causes {@code MappingException: Unknown entity} (Pitfall 2).</li>
 *   <li>{@link EnableJpaRepositories} targeting {@code com.spt.bas.server.dao} (240 Dao
 *       from Plan 05, each {@code extends BaseDao}). Phase 4 (Plan 04-05 Rule 3 blocking fix)
 *       added {@code com.spt.pm.dao} — the 14 PM BaseDao interfaces ported in Wave 2b PM
 *       absorption (04-04) were never entity-scanned before because no Wave 2b service had
 *       autowired them; the api layer port surfaced the missing scan via
 *       {@code NoSuchBeanDefinitionException: PmApproveDao} on context load.</li>
 * </ul>
 *
 * <p>The mybatis {@code @MapperScan} for {@code com.spt.bas.system.dao} lives in
 * {@code ZgbasMybatisConfig} (single source of truth — WR-01).
 *
 * <p>{@link ComponentScan} is declared explicitly (overrides the default scan inside
 * {@link SpringBootApplication}) to add an {@code excludeFilters}: two {@code @Configuration}
 * classes share the bean name {@code feignConfig} — {@code com.spt.tools.http.feign.FeignConfig}
 * (inlined common) and {@code com.spt.sign.client.config.FeignConfig} (sign-client jar). Both
 * are per-client Feign configs instantiated inside Feign's child context via
 * {@code @FeignClient(configuration = ...)}; neither should be a top-level singleton. The source
 * {@code BasServer} avoided the conflict via a narrow {@code @ComponentScan(basePackages = {"com.spt.pm",
 * "com.spt.bas.server"})}; the monolith's broad {@code com.spt} scan requires the exclude filter instead
 * (Rule 1 fix — ConflictingBeanDefinitionException on context load).
 *
 * <p>Phase 4 Wave 3 (Plan 04-05) added a third assignable-type exclusion:
 * {@code com.spt.tools.http.interceptor.BasicErrorController}. The source basServer module
 * ships a customised {@code com.spt.bas.server.config.BasicErrorController} (adds errorId/errorMsg
 * integration + specific error pages 400/404/401/500); the spt-tools generic ancestor
 * ({@code com.spt.tools.http.interceptor.BasicErrorController}, inlined Phase 2) is the structural
 * template the basServer one was derived from. Both are {@code @Controller} with the same simple
 * name, so Spring derives the same default bean name {@code basicErrorController} —
 * {@code ConflictingBeanDefinitionException} on context load. Source avoided it via module-isolated
 * scans (basServer boot app scanned only its own package); the monolith's broad {@code com.spt} scan
 * requires excluding the generic ancestor so the basServer customisation wins (Rule 3 blocking
 * auto-fix, Plan 04-05 — same precedent as the FeignConfig exclusion above).
 *
 * <p>Phase 4 Wave 4 (Plan 04-06) added a fourth assignable-type exclusion:
 * {@code com.spt.bas.web.config.BasicErrorController}. The source web module ships its own
 * BasicErrorController (the front-end error controller for BFF routes), nearly identical to the
 * basServer customisation above. BFF controllers reference it for the static
 * {@code getErrorResp(Exception)} utility method. Both {@code @Controller} classes share the
 * simple name {@code BasicErrorController} → same default bean name → conflict. The basServer
 * version remains the active ErrorController (decided in Plan 04-05); the web version stays on
 * classpath for its static method but is not registered as a bean (Rule 3 blocking auto-fix).
 *
 * <p>Phase 7 (Plan 07-04, BFF edge 迁入) added a fifth assignable-type exclusion:
 * {@code com.spt.bas.purchase.wx.server.config.BasicErrorController}. The source basWx module
 * ships its own BasicErrorController (near-clone of the basServer customisation above, kept for
 * its {@code public static getErrorResp(Throwable)} utility + structural fidelity to source D-05
 * config edge). It is {@code @Controller} with the same simple name {@code BasicErrorController}
 * → same default bean name {@code basicErrorController} → would collide with the active
 * {@code com.spt.bas.server.config.BasicErrorController} (the single active ErrorController
 * decided in Plan 04-05). Excluding it keeps classpath presence for the static util while
 * preserving the one-active-ErrorController invariant (Rule 3 blocking, same precedent as the
 * web-version exclusion in Plan 04-06). Global {@code /error} handling is unchanged (behaviour-
 * equivalent to source — source basWx used module-isolated scans; the monolith uses the filter).
 *
 * <p>Phase 3 (AUTH-01..04, D-P3-01): {@code com.spt.tools.shiro.config.ToolsShiroConfig} is now
 * UN-excluded — the previously-dormant Shiro auto-config engages now that a concrete Realm
 * ({@code com.spt.bas.web.shiro.ShiroDbRealm}, @Component, ported in Plan 03-01) satisfies the
 * {@code securityManager(... AbstractShiroDbRealm shiroDbRealm)} parameter. The Phase 2
 * ehcache-name conflict is resolved by the inlined config's own {@code EhCacheManager} bean
 * definition; Shiro session/cache/filter-chain beans now wire alongside JPA + mybatis-plus.
 *
 * <p>Dropped vs source {@code BasServer.java}: {@code @PropertySource} (D-P2-14 native
 * profile), {@code @EnableDiscoveryClient} (nacos removed, D-P2-11), {@code @Import} of
 * microservice residue (sign config auto-registers via the {@code com.spt} scan), the bare
 * {@code @ComponentScan} (superseded by the filtered one above).
 */
@SpringBootApplication
@ComponentScan(
    basePackages = "com.spt",
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = { com.spt.tools.http.feign.FeignConfig.class,
                    com.spt.sign.client.config.FeignConfig.class,
                    com.spt.tools.http.interceptor.BasicErrorController.class,
                    com.spt.bas.web.config.BasicErrorController.class,
                    com.spt.bas.purchase.wx.server.config.BasicErrorController.class }
    )
)
// Phase 4 D-P4-01 方案 A (corrected 2026-07-17): widen @EnableFeignClients to also scan
// com.spt.bas.client.remote. The 238 bas @FeignClient contracts there generate Feign proxies
// whose url = BasConstants.SERVER_URL = "#{basServerConfig.url}" resolves (via the
// BasClientConfig-produced basServerConfig bean) to http://localhost:8080 — i.e. the proxies
// self-loop back to the monolith's own port 8080 where the ported @RestController endpoints
// (Wave 3) live. This is a same-process HTTP hop (no cross-process hop); behaviour is equivalent
// to the source microservice topology where web→basServer was Feign-over-HTTP.
//
// The original D-P2-12 narrowing intent (avoid double-bean conflict) is preserved because the
// ported @RestController endpoints extend BaseApi<Entity> and do NOT implement I*Client
// (verified 0/224 implements in source — see 04-RESEARCH.md §D-P4-01 Critical Finding). There
// is therefore no local bean candidate that could conflict with the Feign proxy.
//
// The excludeFilters above remain UNCHANGED: both FeignConfig singletons stay excluded from
// top-level ComponentScan so they only engage as per-client configurations inside Feign's child
// context (as in Phase 2). The path-prefix discrepancy between the source context-path
// (/spt-bas-server) and the monolith root (/) is resolved by BasFeignPathConfig's
// WebMvcConfigurer.addPathPrefix("/spt-bas-server", ...) applied to the api packages
// (com.spt.bas.server.api / com.spt.pm.api) — the Wave 5 rewrite of D-P4-01a, which replaced
// the earlier path-stripper RequestInterceptor that caused AmbiguousMappingException once the
// BFF controllers landed.
@EnableFeignClients(basePackages = {
    "com.spt.sign.client.remote",        // EXT-03 cfca — Phase 2 D-P2-12 narrowing preserved
    "com.spt.bas.client.remote",         // Phase 4 D-P4-01 — bas 契约自回环
    "com.spt.bas.purchase.wx.client.remote",  // Phase 4 Wave 3 (Plan 04-05 Rule 3): basWx contracts
    // referenced by 16 ported basServer service impls (IWxUserDetailClient + ISaveTempClient).
    // Phase 7 ported the WX BFF controllers (/wx/* /ewechat/* /axq/* + /purchase/* api) into
    // zgbas-admin; the Feign self-loop now reaches real handlers (non-404), superseding the
    // earlier D-P4-02 lazy-degradation note (which held while basWx was v2-deferred). The
    // purchaseWxServerConfig bean + SpEL #{purchaseWxServerConfig.url} resolve at proxy creation;
    // startup succeeds because Feign proxies are lazy (URL resolved on call, not on registration).
    "com.spt.bas.report.client.remote"   // Phase 4 Wave 3 (Plan 04-05 Rule 3): report contracts
    // referenced by 9 ported basServer service impls (IRptCompanyClient + others). Report
    // migration is Phase 5 (REPORT-01/02); report-client jar (04-04 types-only dep) provides
    // contract types. Same lazy-degradation semantics as basWx above — runtime calls 404 until
    // Phase 5 ports ReportServer. ReportClientConfig bean is component-scanned (URL lazy-resolved).
})
@EntityScan(basePackageClasses = IdEntity.class,
            basePackages = {
                "com.spt.bas.client.entity",
                "com.spt.pm.entity",
                "com.spt.bas.purchase.wx.client.entity",   // Phase 3: WX purchase-client entities
                "com.spt.bas.purchase.wx.server.entity"    // Phase 3: WX purchase-server entities
            })
@EnableJpaRepositories(basePackages = {
    "com.spt.bas.server.dao",
    "com.spt.pm.dao",
    "com.spt.bas.purchase.wx.server.dao"   // Phase 3: WX Dao 接口（11个WX专属）
})
public class ZgbasApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZgbasApplication.class, args);
    }
}
