package com.spt;

import com.spt.bas.client.remote.IBsCompanyOurClient;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.apache.shiro.mgt.SecurityManager;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Capstone startup-verification test for Phase 2 (D-P2-03) + Phase 3 (D-P3-13) + Phase 4 (D-P4-01/06).
 *
 * <p>Uses {@code WebEnvironment.RANDOM_PORT} so the embedded Tomcat starts with a real
 * servlet container — this is required for {@code ServerEndpointExporter} (WebSocket) and
 * lets us verify endpoint reachability via {@link TestRestTemplate}.
 *
 * <p>The {@link SpringBootTest} context load brings up the full monolith context: Druid
 * {@link DataSource}, JPA (239 entities, ddl-auto=none), mybatis-plus (sample Mapper),
 * 3 external SDK beans, cfca sign Feign client, Shiro securityManager/shiroFilter/shiroDbRealm
 * (Phase 3 un-excluded ToolsShiroConfig), ported Login/Index/SSO controllers, and WebSocket
 * endpoints. If this context starts and all assertions pass, D-P3-13 is satisfied.
 *
 * <p>Phase 3 endpoint assertions verify /login, /index, /open/user/ssoLogin return 2xx or 3xx
 * — matching D-P3-13 acceptance ("200 或 302"). Not-logged-in /index is expected to redirect
 * (302) to /login via Shiro {@code user} filter — correct behavior.
 *
 * <p>Phase 4 (Wave 0, D-P4-01 方案 A + D-P4-01a + D-P4-06 WR-02): adds five new test methods.
 * Four are {@link Disabled} placeholders (Wave 3/4 endpoint + BFF bean sampling — activated
 * once the ported controllers land). The fifth — {@link #feignSelfLoopbackWiring_probe()} —
 * runs in Wave 0 as a fail-fast gate for the D-P4-01 self-loopback wiring and the D-P4-01a
 * path-prefix stripper. It verifies three things: (1) the {@code basServerPathStripper}
 * {@link RequestInterceptor} bean registers; (2) the {@link IBsCompanyOurClient} Feign proxy
 * resolves (proves the widened {@code @EnableFeignClients} basePackages + the
 * {@code basServerConfig} bean + SpEL {@code "#{basServerConfig.url}"} all work together);
 * (3) the interceptor strips the literal {@code "spt-bas-server/"} prefix from a constructed
 * {@link RequestTemplate} (proves RESEARCH A3 — Feign path rewrite via
 * {@link RequestTemplate#uri(String)} is viable, no Wave 4 surprise).
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dev")
class ZgbasApplicationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private ApplicationContext context;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void contextLoads() {
        // Primary gate: the full context starts without exceptions.
        // Covers PERSIST-03/04, EXT-01..04, INFRA-04, AUTH-01..04.
    }

    @Test
    void primaryDataSourceIsPresent() {                // PERSIST-03
        assertThat(context.getBean(DataSource.class)).isNotNull();
    }

    @Test
    void jpaTransactionManagerIsPrimary() {            // PERSIST-03 D-P2-03
        assertThat(context.containsBean("transactionManager")).isTrue();
        assertThat(context.getBean(PlatformTransactionManager.class))
            .isInstanceOf(JpaTransactionManager.class);
    }

    @Test
    void sampleMapperBeanRegistered() {                // PERSIST-03 sample Mapper
        assertThat(context.containsBean("sampleMapper")).isTrue();
    }

    @Test
    void externalSdkBeansRegistered() {                // EXT-01..03
        assertThat(context.containsBean("authOpenFacade")).isTrue();
        assertThat(context.containsBean("pushClientHttp")).isTrue();
        assertThat(context.containsBean("fileRemote")).isTrue();
    }

    // ---- Phase 3 (D-P3-13): Shiro auth chain bean wiring ----

    @Test
    void shiroSecurityManagerBeanPresent() {           // AUTH-03 D-P3-01
        assertThat(context.containsBean("securityManager")).isTrue();
        assertThat(context.getBean("securityManager"))
            .isInstanceOf(SecurityManager.class);
    }

    @Test
    void shiroFilterBeanPresent() {                    // AUTH-03 D-P3-01
        assertThat(context.containsBean("shiroFilter")).isTrue();
    }

    @Test
    void shiroDbRealmBeanPresent() {                   // AUTH-01/04 D-P3-02
        assertThat(context.containsBean("shiroDbRealm")).isTrue();
    }

    // ---- Phase 3 (D-P3-13): Ported controller endpoints registered ----

    @Test
    void loginControllerRegistered() {                 // AUTH-01 D-P3-03
        assertThat(context.containsBean("loginController")).isTrue();
    }

    @Test
    void indexControllerRegistered() {                 // AUTH-02 D-P3-10
        assertThat(context.containsBean("indexController")).isTrue();
    }

    @Test
    void userOpenControllerRegistered() {              // AUTH-02 D-P3-12 SSO
        assertThat(context.containsBean("userOpenController")).isTrue();
    }

    // ---- Phase 3 (D-P3-13): Endpoint reachability ----

    @Test
    void loginEndpointReachable() {                    // AUTH-01 D-P3-13
        ResponseEntity<String> response = restTemplate.getForEntity("/login", String.class);
        assertThat(response.getStatusCode().is2xxSuccessful()
            || response.getStatusCode().is3xxRedirection()).isTrue();
    }

    @Test
    void indexEndpointReachable() {                    // AUTH-02 D-P3-13
        // Not logged in → Shiro user filter redirects to /login (302) — correct behavior.
        ResponseEntity<String> response = restTemplate.getForEntity("/index", String.class);
        assertThat(response.getStatusCode().is2xxSuccessful()
            || response.getStatusCode().is3xxRedirection()).isTrue();
    }

    @Test
    void ssoLoginEndpointReachable() {                 // AUTH-02 D-P3-12/13 SSO
        // Not logged in → Shiro user filter redirects to /login (302) — correct behavior.
        ResponseEntity<String> response =
            restTemplate.getForEntity("/open/user/ssoLogin", String.class);
        assertThat(response.getStatusCode().is2xxSuccessful()
            || response.getStatusCode().is3xxRedirection()).isTrue();
    }

    // ---- Phase 4 Wave 0 (D-P4-06 WR-02): business contract HTTP reachability placeholders ----
    // These four tests build the WR-02 shell. Wave 3 ports the 224 @RestController endpoints
    // (extends BaseApi<Entity>) into zgbas-system; Wave 4 ports the 267 BFF controllers into
    // zgbas-admin. Once those land, remove @Disabled — the endpoints will resolve and these
    // tests naturally activate as the WR-02 acceptance proof.
    //
    // Assertion shape matches the existing loginEndpointReachable pattern: 2xx / 3xx / 401 all
    // count as "endpoint registered and Spring MVC reachable over HTTP". 401 is expected when
    // Shiro's user filter rejects an unauthenticated request to a protected business endpoint.

    @Test
    @Disabled("Wave 3: ApplyBrandApi落位后启用 (D-P4-06 / WR-02 合同域)")
    void basContractEndpointReachable_applyBrand_findAll() {
        ResponseEntity<String> response = restTemplate.postForEntity(
            "/apply/brand/findAll", null, String.class);
        assertThat(response.getStatusCode().is2xxSuccessful()
            || response.getStatusCode().is3xxRedirection()
            || response.getStatusCodeValue() == 401).isTrue();
    }

    @Test
    @Disabled("Wave 3: CtrContractApi落位后启用 (D-P4-06 / WR-02 授信/合同域)")
    void basContractEndpointReachable_ctrContract_findPage() {
        ResponseEntity<String> response = restTemplate.postForEntity(
            "/ctr/contract/findPage", null, String.class);
        assertThat(response.getStatusCode().is2xxSuccessful()
            || response.getStatusCode().is3xxRedirection()
            || response.getStatusCodeValue() == 401).isTrue();
    }

    @Test
    @Disabled("Wave 3: StockDetailApi落位后启用 (D-P4-06 / WR-02 库存域)")
    void basContractEndpointReachable_stockDetail_findAll() {
        ResponseEntity<String> response = restTemplate.postForEntity(
            "/stock/detail/findAll", null, String.class);
        assertThat(response.getStatusCode().is2xxSuccessful()
            || response.getStatusCode().is3xxRedirection()
            || response.getStatusCodeValue() == 401).isTrue();
    }

    @Test
    @Disabled("Wave 4: BFF controller 落位后启用 (D-P4-06 / BIZ-02)")
    void bffControllersRegistered_sample() {
        // Wave 4 ports 267 web BFF controllers into zgbas-admin; sample 3 representative beans
        // (合同/授信/库存 domains) once they land. Bean name convention = simple class name
        // with initial lowercased (Spring default for @Controller).
        assertThat(context.containsBean("applyBrandController")).isTrue();
        assertThat(context.containsBean("ctrContractController")).isTrue();
        assertThat(context.containsBean("stockDetailController")).isTrue();
    }

    // ---- Phase 4 Wave 0 (D-P4-01 方案 A + D-P4-01a): Feign self-loopback fail-fast probe ----
    // Runs in Wave 0 (NOT @Disabled). Three-step assertion that the self-loopback wiring is
    // correct, before Waves 1-3 copy ~1356 files. A failure here means the entire D-P4-01
    // 方案 A approach must be revisited before more files are ported — much cheaper than
    // discovering it at Wave 4 acceptance.
    @Test
    void feignSelfLoopbackWiring_probe() {
        // (1) basServerPathStripper bean registered — proves BasFeignPathConfig @Configuration
        // is picked up by the com.spt ComponentScan and the RequestInterceptor @Bean wires.
        assertThat(context.containsBean("basServerPathStripper")).isTrue();
        Object interceptor = context.getBean("basServerPathStripper");
        assertThat(interceptor).isInstanceOf(RequestInterceptor.class);

        // (2) IBsCompanyOurClient Feign proxy resolves — proves:
        //   - the widened @EnableFeignClients basePackages includes com.spt.bas.client.remote;
        //   - the basServerConfig LocalServerConfig bean (BasClientConfig) registered;
        //   - the SpEL "#{basServerConfig.url}" in BasConstants.SERVER_URL resolved to a value;
        //   - the IBsCompanyOurClient @FeignClient metadata (name/path/url/configuration) is valid.
        // A failure here surfaces as NoSuchBeanDefinitionException or a SpelEvaluationException.
        assertThat(context.getBean(IBsCompanyOurClient.class)).isNotNull();

        // (3) Path-prefix stripping logic — proves RESEARCH A3 (the RequestTemplate.uri()
        // overwrite approach is viable for D-P4-01a). Constructs a template carrying the
        // "spt-bas-server/" prefix, applies the interceptor, asserts the prefix is gone and
        // the remainder starts with "/". This is a pure unit assertion: no HTTP request leaves
        // the JVM. It guards against a Wave 4 surprise where the interceptor compiles but
        // doesn't actually mutate the URI (e.g. wrong API used on this feign-core version).
        RequestTemplate template = new RequestTemplate();
        template.uri("/spt-bas-server/apply/brand/findAll");
        assertThat(template.path()).contains("spt-bas-server/");   // precondition sanity check

        ((RequestInterceptor) interceptor).apply(template);

        assertThat(template.path())
            .doesNotContain("spt-bas-server/")
            .startsWith("/apply/brand");
    }
}
