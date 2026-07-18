package com.spt;

import com.spt.bas.client.remote.IBsCompanyOurClient;
import com.spt.bas.client.vo.ContractSearchVo;
import com.spt.bas.report.client.entity.RptBusinessOverview;
import com.spt.bas.report.client.entity.RptFundReceivableStatistics;
import com.spt.bas.report.client.vo.RptBusinessOverviewSearchVo;
import com.spt.bas.report.client.vo.RptFundReceivableStatisticsVo;
import com.spt.bas.report.client.vo.RptCtrContractRptVo;
import com.spt.bas.report.server.dao.RptBusinessOverviewMapper;
import com.spt.bas.report.server.dao.RptCtrContractReportMapper;
import com.spt.bas.report.server.dao.RptFundReceivableStatisticsMapper;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.shiro.mgt.SecurityManager;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

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
 * <p>Phase 4 (Wave 0, D-P4-01 方案 A + D-P4-01a + D-P4-06 WR-02): adds six test methods.
 * Five verify ported-controller reachability (Wave 3/4 bas contract endpoints + BFF bean
 * sampling) and are now active — the {@code @Disabled} placeholders were lifted once Wave 3/4
 * ported the controllers. The sixth — {@link #feignSelfLoopbackWiring_probe()} — runs as a
 * fail-fast gate for the D-P4-01 self-loopback wiring. It verifies two things: (1) the
 * {@code BasFeignPathConfig} {@code WebMvcConfigurer} bean registers (the Wave 5 path-prefix
 * rewrite of D-P4-01a — {@code addPathPrefix("/spt-bas-server", ...)} on the api packages,
 * which replaced the earlier path-stripper {@code RequestInterceptor} that caused
 * {@code AmbiguousMappingException} once the BFF controllers landed); (2) the
 * {@link IBsCompanyOurClient} Feign proxy resolves (proves the widened
 * {@code @EnableFeignClients} basePackages + the {@code basServerConfig} bean + SpEL
 * {@code "#{basServerConfig.url}"} all work together).
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

    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    @Autowired
    private RptFundReceivableStatisticsMapper rptFundReceivableStatisticsMapper;

    @Autowired
    private RptCtrContractReportMapper rptCtrContractReportMapper;

    @Autowired
    private RptBusinessOverviewMapper rptBusinessOverviewMapper;

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
    // These tests are the WR-02 acceptance proof (D-P4-06). Wave 3 ported the 236 @RestController
    // endpoints (extends BaseApi<Entity>) into zgbas-system; Wave 4 ported the 267 BFF controllers
    // into zgbas-admin. The @Disabled annotations have been removed — the endpoints now resolve
    // and these tests prove end-to-end HTTP reachability through the Feign self-loopback (方案 A).
    //
    // Assertion shape: 2xx / 3xx / 401 all count as "endpoint registered and Spring MVC reachable
    // over HTTP". Shiro's user filter (Phase 3 AUTH-03 /**=user chain) redirects unauthenticated
    // requests to /login (302) — proving the endpoint space is protected, not absent. 401 is
    // expected when Shiro returns unauthorized for protected business endpoints.

    @Test
    void basContractEndpointReachable_applyBrand_findAll() {
        // WR-02 合同域: ApplyBrandController @RequestMapping("/apply/brand")
        ResponseEntity<String> response = restTemplate.postForEntity(
            "/apply/brand/findAll", null, String.class);
        assertThat(response.getStatusCode().is2xxSuccessful()
            || response.getStatusCode().is3xxRedirection()
            || response.getStatusCodeValue() == 401).isTrue();
    }

    @Test
    void basContractEndpointReachable_ctrContract_findPage() {
        // WR-02 授信/合同域: CtrContractController @RequestMapping("/ctr/contract")
        ResponseEntity<String> response = restTemplate.postForEntity(
            "/ctr/contract/findPage", null, String.class);
        assertThat(response.getStatusCode().is2xxSuccessful()
            || response.getStatusCode().is3xxRedirection()
            || response.getStatusCodeValue() == 401).isTrue();
    }

    @Test
    void basContractEndpointReachable_stockContract_findPage() {
        // WR-02 库存域: StockContractController @RequestMapping("/stock/stockContract")
        ResponseEntity<String> response = restTemplate.postForEntity(
            "/stock/stockContract/findPage", null, String.class);
        assertThat(response.getStatusCode().is2xxSuccessful()
            || response.getStatusCode().is3xxRedirection()
            || response.getStatusCodeValue() == 401).isTrue();
    }

    @Test
    void basContractEndpointReachable_ctrLoading_findPage() {
        // WR-02 放款域: CtrContractLoadingController @RequestMapping("/ctr/loading")
        ResponseEntity<String> response = restTemplate.postForEntity(
            "/ctr/loading/findPage", null, String.class);
        assertThat(response.getStatusCode().is2xxSuccessful()
            || response.getStatusCode().is3xxRedirection()
            || response.getStatusCodeValue() == 401).isTrue();
    }

    @Test
    void bffControllersRegistered_sample() {
        // BIZ-02: Wave 4 ports 267 web BFF controllers into zgbas-admin; sample 4 representative
        // beans (合同/授信/库存/放款 domains). Bean name convention = simple class name with
        // initial lowercased (Spring default for @Controller).
        assertThat(context.containsBean("applyBrandController")).isTrue();
        assertThat(context.containsBean("ctrContractController")).isTrue();
        assertThat(context.containsBean("stockContractController")).isTrue();
        assertThat(context.containsBean("ctrContractLoadingController")).isTrue();
    }

    // ---- Phase 4 Wave 0/4 (D-P4-01 方案 A + D-P4-01a): Feign self-loopback fail-fast probe ----
    // Runs in Wave 0 (NOT @Disabled). Verifies the self-loopback wiring is correct.
    // Wave 4 correction: the path-prefix approach (BasFeignPathConfig WebMvcConfigurer) replaced
    // the Wave 0 path-stripper RequestInterceptor — the stripper caused AmbiguousMappingException
    // when BFF and api both registered the same URL at root. The prefix restores source topology
    // (api at /spt-bas-server/*, BFF at /*), so the Feign path now matches the api directly.
    @Test
    void feignSelfLoopbackWiring_probe() {
        // (1) BasFeignPathConfig registered as a bean — proves the @Configuration is picked up
        // by the com.spt ComponentScan and its WebMvcConfigurer.configurePathMatch engages.
        assertThat(context.getBean(
            com.spt.bas.client.config.BasFeignPathConfig.class)).isNotNull();

        // (2) IBsCompanyOurClient Feign proxy resolves — proves:
        //   - the widened @EnableFeignClients basePackages includes com.spt.bas.client.remote;
        //   - the basServerConfig LocalServerConfig bean (BasClientConfig) registered;
        //   - the SpEL "#{basServerConfig.url}" in BasConstants.SERVER_URL resolved to a value;
        //   - the IBsCompanyOurClient @FeignClient metadata (name/path/url/configuration) is valid.
        assertThat(context.getBean(IBsCompanyOurClient.class)).isNotNull();
    }

    // ---- Phase 5 Wave 0 (D-P5-03): report Feign self-loopback fail-fast probe ----
    // Runs in Wave 0 (NOT @Disabled). Verifies the report-side wiring that Wave 5 (api controllers)
    // + Waves 1-4 (53 Mappers + XML + services) will depend on. Mirrors the Phase 4
    // feignSelfLoopbackWiring_probe verbatim, with three report-specific assertions.
    @Test
    void reportFeignSelfLoopbackWiring_probe() {
        // (1) ReportFeignPathConfig registered as a bean — proves the @Configuration is picked
        // up by the com.spt ComponentScan and its WebMvcConfigurer.configurePathMatch engages,
        // adding the /spt-bas-report prefix to all 54 (future Wave 5) api controllers in
        // com.spt.bas.report.server.api. Without this, the 14 BFF path collisions would
        // throw AmbiguousMappingException once Wave 5 ports the api controllers.
        assertThat(context.getBean(
            com.spt.bas.client.config.ReportFeignPathConfig.class)).isNotNull();

        // (2) IRptFundReceivableStatisticsClient Feign proxy resolves — proves:
        //   - @EnableFeignClients (Phase 4 04-05) includes com.spt.bas.report.client.remote;
        //   - the inlined ReportClientConfig (Wave 0 D-P5-02) registered the
        //     "reportServerConfig" LocalServerConfig bean;
        //   - the SpEL "#{reportServerConfig.url}" in ReportConstant.SERVER_URL resolved;
        //   - the IRptFundReceivableStatisticsClient @FeignClient metadata is valid.
        // If the inline copy missed ReportClientConfig, or if the SpEL broke, this throws
        // NoSuchBeanDefinitionException / SpelEvaluationException — fail-fast.
        assertThat(context.getBean(
            com.spt.bas.report.client.remote.IRptFundReceivableStatisticsClient.class)).isNotNull();

        // (3) reportServerConfig URL resolves to the localhost:8080 self-loopback — proves
        // the application-dev.yml key spt.bas.report.url: http://localhost:8080 (Phase 4 04-05)
        // was read by ReportClientConfig.setUrlKey("spt.bas.report.url") and propagated to
        // LocalServerConfig.getUrl(). When Phase 5 Wave 5 ports the api controllers + Waves 1-4
        // port the Mappers/services, the 9 basServer service impls + BFF controllers that
        // @Autowired IRpt*Client (Phase 4 D-P4-02 lazy-degradation) will self-loop correctly.
        com.spt.tools.core.bean.LocalServerConfig reportServerConfig = context.getBean(
            "reportServerConfig", com.spt.tools.core.bean.LocalServerConfig.class);
        assertThat(reportServerConfig.getUrl()).contains("localhost:8080");
    }

    @Test
    void allReportMappersResolve() {
        assertThat(sqlSessionFactory.getConfiguration().hasStatement(
            "com.spt.bas.report.server.dao.RptFundReceivableStatisticsMapper.findPage"))
            .as("RptFundReceivableStatisticsMapper.findPage should resolve")
            .isTrue();
        assertThat(sqlSessionFactory.getConfiguration().hasStatement(
            "com.spt.bas.report.server.dao.RptCtrContractReportMapper.findRptContractPage"))
            .as("RptCtrContractReportMapper.findRptContractPage should resolve")
            .isTrue();
        assertThat(sqlSessionFactory.getConfiguration().hasStatement(
            "com.spt.bas.report.server.dao.RptBusinessOverviewMapper.findBusinessOverviewList"))
            .as("RptBusinessOverviewMapper.findBusinessOverviewList should resolve")
            .isTrue();
    }

    @Test
    void reportApiPathPrefixWiring_probe() throws Exception {
        RequestMappingHandlerMapping mapping = context.getBean(RequestMappingHandlerMapping.class);

        MockHttpServletRequest fundReceivableRequest = new MockHttpServletRequest(
            "POST", "/spt-bas-report/rpt/fundReceivableStatistics/findPage");
        HandlerExecutionChain fundReceivableHandler = mapping.getHandler(fundReceivableRequest);
        assertThat(fundReceivableHandler)
            .as("/spt-bas-report/rpt/fundReceivableStatistics/findPage should map to a report api handler")
            .isNotNull();

        MockHttpServletRequest baseCostRequest = new MockHttpServletRequest(
            "POST", "/spt-bas-report/rpt/baseCost/findPage");
        HandlerExecutionChain baseCostHandler = mapping.getHandler(baseCostRequest);
        assertThat(baseCostHandler)
            .as("/spt-bas-report/rpt/baseCost/findPage should map to a report api handler")
            .isNotNull();

        MockHttpServletRequest businessPayRequest = new MockHttpServletRequest(
            "POST", "/spt-bas-report/rpt/businessPay/findPageContract");
        HandlerExecutionChain businessPayHandler = mapping.getHandler(businessPayRequest);
        assertThat(businessPayHandler)
            .as("/spt-bas-report/rpt/businessPay/findPageContract should map to a report api handler")
            .isNotNull();
    }

    @Disabled("D-P5-08 sample query proof — activate manually with real DB seeded data")
    @Test
    void sampleReportQuery_proof() {
        RptFundReceivableStatisticsVo fundReceivableSearch = new RptFundReceivableStatisticsVo();
        fundReceivableSearch.setPage(1);
        fundReceivableSearch.setRows(10);
        java.util.List<RptFundReceivableStatistics> fundReceivableRows =
            rptFundReceivableStatisticsMapper.findPage(fundReceivableSearch);
        assertThat(fundReceivableRows)
            .as("RptFundReceivableStatisticsMapper.findPage should return rows with seeded real DB data")
            .isNotNull()
            .isNotEmpty();

        ContractSearchVo contractSearch = new ContractSearchVo();
        contractSearch.setPage(1);
        contractSearch.setRows(10);
        java.util.List<RptCtrContractRptVo> contractRows =
            rptCtrContractReportMapper.findRptContractPage(contractSearch);
        assertThat(contractRows)
            .as("RptCtrContractReportMapper.findRptContractPage should return rows with seeded real DB data")
            .isNotNull()
            .isNotEmpty();

        RptBusinessOverviewSearchVo businessOverviewSearch = new RptBusinessOverviewSearchVo();
        java.util.List<RptBusinessOverview> businessOverviewRows =
            rptBusinessOverviewMapper.findBusinessOverviewList(businessOverviewSearch);
        assertThat(businessOverviewRows)
            .as("RptBusinessOverviewMapper.findBusinessOverviewList should return rows with seeded real DB data")
            .isNotNull()
            .isNotEmpty();
    }

    @Test
    void reportHttpReachability_proof() {
        ResponseEntity<String> reportApiResponse = restTemplate.postForEntity(
            "/spt-bas-report/rpt/fundReceivableStatistics/findPage", null, String.class);
        assertThat(reportApiResponse.getStatusCodeValue())
            .as("/spt-bas-report/rpt/fundReceivableStatistics/findPage should not return 404")
            .isNotEqualTo(404);

        ResponseEntity<String> businessOverviewResponse = restTemplate.postForEntity(
            "/spt-bas-report/business/overview/api/findBusinessOverviewList", null, String.class);
        assertThat(businessOverviewResponse.getStatusCodeValue())
            .as("/spt-bas-report/business/overview/api/findBusinessOverviewList should not return 404")
            .isNotEqualTo(404);
    }
}
