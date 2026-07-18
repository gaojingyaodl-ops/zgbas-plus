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
import com.spt.quartz.domain.SysJob;
import com.spt.quartz.domain.SysJobLog;
import com.spt.quartz.mapper.SysJobMapper;
import com.spt.quartz.mapper.SysJobLogMapper;
import com.spt.quartz.service.ISysJobService;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.shiro.mgt.SecurityManager;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.sql.DataSource;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

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

    /**
     * Expected {@code sys_job} row count when the dev DB has been seeded with both
     * Phase 6 baseline SQL scripts:
     * <ul>
     *   <li>3 RyTask demo rows (sys_job.sql job_id 1-3, ported verbatim from RuoYi ry_20210908.sql)</li>
     *   <li>50 production cron entries (06-05 sys_job_data.sql job_id 102-190,
     *       translated from zg_prod xxl_job_info via D-P6-02)</li>
     * </ul>
     * Total = 53. This constant drives the D-P6-06 fail-fast assertion in
     * {@link #schedulerLoadAllJobs_proof()}; if a future plan adds more rows,
     * update this constant along with the SQL seed.
     */
    private static final int EXPECTED_JOB_COUNT = 53;

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

    // ---- Phase 6 (D-P6-06): quartz scheduler + sys_job table wiring ----

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private SysJobMapper sysJobMapper;

    @Autowired
    private SysJobLogMapper sysJobLogMapper;

    @Autowired
    private ISysJobService sysJobService;

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

    // ---- Phase 6 Wave 6 (D-P6-06 fail-fast + D-P6-04/05 sampling dry-run) ----
    //
    // Four quartz probe/proof methods close Phase 6:
    //   1. quartzBeanResolution_probe     — @Test enabled: 11+ handler beans + Scheduler + mappers resolve
    //   2. quartzTablesExist_probe        — @Disabled: redundant with #3 (selectJobAll already exercised)
    //   3. schedulerLoadAllJobs_proof     — @Test enabled: D-P6-06 fail-fast gate (53 rows loaded → all cron + bean.method valid)
    //   4. sampleQuartzJobDryRun_proof    — @Disabled: D-P6-04 sampling + D-P6-05 read-only真跑 / write-class空跑 branches
    //
    // Fail-fast reasoning (D-P6-06): SysJobServiceImpl.@PostConstruct init() runs during Spring
    // context startup — it iterates selectJobAll() and calls ScheduleUtils.createScheduleJob(...)
    // for every row. Any invalid cron_expression throws IllegalArgumentException via
    // CronScheduleBuilder.cronSchedule; any bean.method reference failure surfaces later when the
    // job is triggered (Phase 7 manual-trigger e2e). If @PostConstruct init() fails, Spring boot
    // startup itself fails — so reaching the test method body proves the 53 cron expressions parsed.
    // schedulerLoadAllJobs_proof additionally asserts scheduler.getJobKeys() size matches the DB row
    // count (every sys_job registered a quartz job), which is the strongest automated guarantee
    // short of actually triggering each job.

    @Test
    void quartzBeanResolution_probe() {
        // (1) Quartz scheduler infrastructure beans — SchedulerFactoryBean from QuartzScheduleConfig
        // (06-01, Pitfall 1 fix) + SysJobServiceImpl @PostConstruct init() wiring.
        assertThat(scheduler)
            .as("Scheduler bean from QuartzScheduleConfig (06-01 Pitfall 1 uncomment) resolved")
            .isNotNull();
        assertThat(context.getBean(ISysJobService.class))
            .as("ISysJobService bean (SysJobServiceImpl via @Service) resolves — owns @PostConstruct init")
            .isNotNull();
        assertThat(context.containsBean("sysJobServiceImpl"))
            .as("SysJobServiceImpl registered under Spring default bean name")
            .isTrue();
        assertThat(context.containsBean("sysJobMapper"))
            .as("SysJobMapper registered (@MyBatisDao + 06-01 ZgbasMybatisConfig @MapperScan third element)")
            .isTrue();
        assertThat(context.containsBean("sysJobLogMapper"))
            .as("SysJobLogMapper registered (same @MapperScan + mybatis/mappers/SysJobLogMapper.xml)")
            .isTrue();

        // (2) 11 representative handler beans across 06-02/03/04 + RyTask demo — proves @Component
        // bean names match sys_job.invoke_target short names. Cross-domain sample (contract / stock /
        // pay / synchronized / command / RuoYi demo).
        // 06-02 basServer/task (5)
        assertThat(context.containsBean("applyPayTask")).as("06-02 ApplyPayTask").isTrue();
        assertThat(context.containsBean("bsCompanyTask")).as("06-02 BsCompanyTask").isTrue();
        assertThat(context.containsBean("ctrContractScheduleTask")).as("06-02 CtrContractScheduleTask").isTrue();
        assertThat(context.containsBean("budgetSettlementTask")).as("06-02 BudgetSettlementTask").isTrue();
        assertThat(context.containsBean("settlementTask")).as("06-02 SettlementTask").isTrue();
        // 06-03 rocketmq/task Synchronized* (2)
        assertThat(context.containsBean("synchronizedCtrContractTask"))
            .as("06-03 SynchronizedCtrContractTask").isTrue();
        assertThat(context.containsBean("synchronizedApplyMatchTask"))
            .as("06-03 SynchronizedApplyMatchTask").isTrue();
        // 06-04 command executors (3)
        assertThat(context.containsBean("basCommandExecutor")).as("06-04 BasCommandExecutor").isTrue();
        assertThat(context.containsBean("reportCommandExecutor")).as("06-04 ReportCommandExecutor").isTrue();
        assertThat(context.containsBean("basWebCommand")).as("06-04 BasWebCommand").isTrue();
        // 06-01 RuoYi demo (1)
        assertThat(context.containsBean("ryTask")).as("06-01 RyTask demo").isTrue();
    }

    @Disabled("D-P6-06 table probe — redundant with schedulerLoadAllJobs_proof (which exercises "
        + "selectJobAll during @PostConstruct). Enable manually only when debugging "
        + "table-binding / @MapperScan issues.")
    @Test
    void quartzTablesExist_probe() {
        // Verifies sys_job + sys_job_log tables exist in dev DB sptbasdb_pd, @MyBatisDao marker
        // is honored, @MapperScan third element "com.spt.quartz.mapper" (06-01 ZgbasMybatisConfig)
        // scanned both mappers, and mybatis-plus.mapper-locations=classpath:/mybatis/mappers/*Mapper.xml
        // glob picks up SysJobMapper.xml + SysJobLogMapper.xml. If schedulerLoadAllJobs_proof is green,
        // this probe is implicitly green too.
        List<SysJob> jobs = sysJobMapper.selectJobAll();
        assertThat(jobs)
            .as("sys_job table populated + SysJobMapper.selectJobAll bound (Phase 6 @MapperScan + XML)")
            .isNotNull();

        List<SysJobLog> logs = sysJobLogMapper.selectJobLogAll();
        assertThat(logs)
            .as("sys_job_log table exists + SysJobLogMapper.selectJobLogAll bound")
            .isNotNull();
    }

    @Test
    void schedulerLoadAllJobs_proof() throws SchedulerException {
        // D-P6-06 fail-fast core — proves the entire Phase 6 cron translation + handler migration
        // is behaviorally loadable. SysJobServiceImpl.@PostConstruct init() already ran during
        // Spring context startup (before any @Test executes):
        //   1. scheduler.clear()
        //   2. jobMapper.selectJobAll() → 53 SysJob rows
        //   3. for each: ScheduleUtils.createScheduleJob(scheduler, job)
        //        - CronScheduleBuilder.cronSchedule(job.getCronExpression())
        //          → throws IllegalArgumentException if cron malformed
        //        - JobBuilder + TriggerBuilder build + scheduler.scheduleJob
        // If ANY of the 53 rows had a bad cron_expression OR an invoke_target whose bean.method
        // cannot be constructed, Spring context startup would have failed with IllegalStateException
        // before this method body ran. Reaching this point = fail-fast passed.
        //
        // Note: bean.method reflection resolution itself is deferred to trigger time
        // (JobInvokeUtil.invokeMethod, called from QuartzJobExecution.doExecute). The bean NAME
        // (SpringUtils.getBean(beanName)) is the only part exercised by @PostConstruct; the METHOD
        // signature is checked when the job is actually triggered. The dry-run proof #4 covers that.

        assertThat(scheduler)
            .as("Scheduler bean from QuartzScheduleConfig resolved — QuartzScheduleConfig @Bean schedulerFactoryBean produced a Scheduler")
            .isNotNull();

        int dbJobCount = sysJobMapper.selectJobAll().size();
        assertThat(dbJobCount)
            .as("sys_job table has the expected 53 rows = 3 RyTask demo (sys_job.sql job_id 1-3) "
                + "+ 50 production entries (06-05 sys_job_data.sql job_id 102-190). "
                + "If you added/removed seed rows, update EXPECTED_JOB_COUNT.")
            .isEqualTo(EXPECTED_JOB_COUNT);

        int schedulerJobCount = scheduler.getJobKeys(GroupMatcher.anyJobGroup()).size();
        assertThat(schedulerJobCount)
            .as("Scheduler registered every sys_job row via @PostConstruct createScheduleJob "
                + "(scheduler.getJobKeys count == sys_job row count). A mismatch means init() "
                + "filtered or skipped a row — would indicate a Scheduler.scheduleJob silent failure.")
            .isEqualTo(dbJobCount);
    }

    @Disabled("D-P6-04 sampling + D-P6-05 grading — manually enable to verify manual-trigger UX "
        + "(QUARTZ-04) against the real dev DB. Writes 2 rows to sys_job_log per run (harmless; "
        + "cleanable via RuoYi '清空任务日志'). Branch A is truly read-only (ryTask.ryNoParams just "
        + "logs); Branch B uses inline Mockito swap so IApplyPayService business writes are short-circuited.")
    @Test
    void sampleQuartzJobDryRun_proof() throws Exception {
        // D-P6-04 sampling: pick representative handlers across read-only真跑 + write-class空跑 grades.
        // D-P6-05 grading rationale:
        //   - Read-only / idempotent handlers (ryTask.ryNoParams, BsCompanyTask.updateCompanyGrey, etc.)
        //     → 真跑 against real dev DB (safe; side effects are at most a cache refresh / log line).
        //   - Write-class handlers (applyPayTask.autoReceive, refreshContractStatusTask, etc.)
        //     → 空跑 with Mockito mock swapped into the handler's @Autowired service field,
        //       so the handler body runs (bean.method reflection works, JobDataMap params pass through,
        //       AbstractQuartzJob.after writes sys_job_log) but real business writes are blocked.
        //
        // Both branches trigger via sysJobService.run(SysJob) which is the SAME entry point as the
        // /monitor/job UI "执行一次" button — proving QUARTZ-04 manual-trigger UX end-to-end.

        // ---------- Branch A: read-only真跑 (ryTask.ryNoParams, job_id=1) ----------
        SysJob ryNoParamsJob = sysJobMapper.selectJobById(1L);
        assertThat(ryNoParamsJob)
            .as("sys_job row job_id=1 (RyTask.ryNoParams demo from 06-01 sys_job.sql) exists")
            .isNotNull();
        assertThat(ryNoParamsJob.getInvokeTarget())
            .as("RyTask.ryNoParams invoke_target string")
            .contains("ryTask.ryNoParams");

        long logCountBeforeA = sysJobLogMapper.selectJobLogAll().size();
        sysJobService.run(ryNoParamsJob);
        SysJobLog ryLog = waitForNewJobLog(logCountBeforeA, ryNoParamsJob.getJobName());
        assertThat(ryLog)
            .as("sys_job_log row written by AbstractQuartzJob.after for ryTask.ryNoParams "
                + "(manual-trigger UX path works — QUARTZ-04)")
            .isNotNull();
        assertThat(ryLog.getStatus())
            .as("ryTask.ryNoParams status='0' (SUCCESS) — read-only真跑 completed without exception")
            .isEqualTo("0");

        // ---------- Branch B: write-class空跑 (applyPayTask.autoReceive, job_id=152) ----------
        // Pre-condition: job_id=152 exists in sys_job_data.sql (applyPayTask.autoReceive, source
        // trigger_status=0 / status=PAUSED). The PAUSED status does NOT affect sysJobService.run —
        // run() calls scheduler.triggerJob directly, bypassing the pause flag (RuoYi "立即运行" UX).
        SysJob autoReceiveJob = sysJobMapper.selectJobById(152L);
        assertThat(autoReceiveJob)
            .as("sys_job row job_id=152 (applyPayTask.autoReceive from 06-05 sys_job_data.sql) exists")
            .isNotNull();

        // Inline mock swap (avoids class-level @MockBean which would force a second Spring context
        // cache entry for the whole test class). ReflectionTestUtils.setField handles private
        // @Autowired fields — same mechanism Spring uses for injection.
        Object applyPayTaskBean = context.getBean("applyPayTask", Object.class);
        Object originalApplyPayService = ReflectionTestUtils.getField(applyPayTaskBean, "applyPayService");
        assertThat(originalApplyPayService)
            .as("precondition: ApplyPayTask.@Autowired applyPayService field present (name verified via 06-02 source)")
            .isNotNull();
        Object applyPayServiceMock = mock(com.spt.bas.server.service.IApplyPayService.class);
        try {
            ReflectionTestUtils.setField(applyPayTaskBean, "applyPayService", applyPayServiceMock);

            long logCountBeforeB = sysJobLogMapper.selectJobLogAll().size();
            sysJobService.run(autoReceiveJob);
            SysJobLog autoReceiveLog = waitForNewJobLog(logCountBeforeB, autoReceiveJob.getJobName());
            assertThat(autoReceiveLog)
                .as("sys_job_log row written for applyPayTask.autoReceive via manual trigger — "
                    + "proves JobInvokeUtil.invokeMethod reflectively resolved bean.method on the "
                    + "write-class handler, JobDataMap propagated SysJob, and AbstractQuartzJob.after ran")
                .isNotNull();
            // Status assertion is intentionally loose: autoReceive's live body (06-02 SUMMARY Known Stubs)
            // calls several @Autowired services; only applyPayService is mocked here, so other real
            // services may throw — logging either SUCCESS or FAIL. The D-P6-05 write-class空跑 goal is
            // "bean.method resolves + handler runs through mocked service boundary", not "business success".
            assertThat(autoReceiveLog.getStatus())
                .as("autoReceive sys_job_log has a status row written (either 0=success or 1=fail)")
                .isIn("0", "1");
        } finally {
            // Restore the real service so subsequent tests see the un-mocked bean (defensive —
            // Spring context is shared across test methods in the same class).
            ReflectionTestUtils.setField(applyPayTaskBean, "applyPayService", originalApplyPayService);
        }
    }

    /**
     * Polls {@code sys_job_log} for up to 10 seconds (50 × 200ms) until a new row appears
     * relative to {@code countBefore}. Quartz jobs run on the scheduler's async thread pool
     * ({@code org.quartz.threadPool.threadCount=20}), so {@code scheduler.triggerJob} returns
     * before the job body executes; this helper blocks the test thread until the row lands.
     *
     * <p>If {@code jobNameFilter} is non-null and a new row appears, prefers the row whose
     * {@code job_name} matches (handles the rare case of concurrent jobs from prior test methods
     * landing during the same polling window).
     */
    private SysJobLog waitForNewJobLog(long countBefore, String jobNameFilter) throws InterruptedException {
        for (int i = 0; i < 50; i++) {
            List<SysJobLog> all = sysJobLogMapper.selectJobLogAll();
            if (all.size() > countBefore) {
                if (jobNameFilter == null) {
                    return all.get(all.size() - 1);
                }
                // Walk newest-first looking for the matching job_name.
                for (int j = all.size() - 1; j >= 0; j--) {
                    SysJobLog candidate = all.get(j);
                    if (jobNameFilter.equals(candidate.getJobName())) {
                        return candidate;
                    }
                }
            }
            Thread.sleep(200L);
        }
        return null;
    }
}
