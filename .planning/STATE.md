---
gsd_state_version: 1.0
milestone: v1.0
milestone_name: milestone
status: planning
stopped_at: "Phase 7 context gathered (4 decisions: hybrid verify form, hybrid comparison baseline, tiered coverage, mixed gap handling)"
last_updated: "2026-07-19T12:56:32.315Z"
last_activity: 2026-07-19
progress:
  total_phases: 7
  completed_phases: 6
  total_plans: 29
  completed_plans: 29
  percent: 86
---

# Project State

## Project Reference

See: .planning/PROJECT.md (updated 2026-07-16)

**Core value:** 单进程启动即可跑通全部供应链业务（登录 → 核心业务 → 报表 → 定时任务），行为对齐旧系统 zgbas
**Current focus:** Phase 7 — 行为对齐验证

## Current Position

Phase: 7
Plan: Not started
Status: Ready to plan
Last activity: 2026-07-19

Progress: [██████████] 100%

## Performance Metrics

**Velocity:**

- Total plans completed: 29
- Average duration: — min
- Total execution time: 0 hours

**By Phase:**

| Phase | Plans | Total | Avg/Plan |
|-------|-------|-------|----------|
| 1 | 1 | - | - |
| 02 | 6 | - | - |
| 03 | 4 | - | - |
| 04 | 6 | - | - |
| 05 | 6 | - | - |
| 06 | 6 | - | - |

**Recent Trend:**

- Last 5 plans: —
- Trend: —

*Updated after each plan completion*
| Phase 1 P01 | 7 | 2 tasks | 14 files |
| Phase 03 P01 | 5 | 2 tasks | 16 files |
| Phase 03 P02 | 4 | 2 tasks | 10 files |
| Phase 03 P03 | 2 | 2 tasks | 2709 files |
| Phase 03 P04 | 12 | 2 tasks | 3 files |
| Phase 04 P01 | 10min | 2 tasks | 7 files |
| Phase 04 P02 | 5min | 2 tasks | 253 files |
| Phase 04 P04 | 12 | 3 tasks | 585 files |
| Phase 04 P05 | 20 | 1 tasks | 236 files |
| Phase 04 P05 | 20 | 1 tasks | 236 files |
| Phase 04 P06 | 25 | 3 tasks | 304 files |

## Accumulated Context

### Decisions

Decisions are logged in PROJECT.md Key Decisions table.
Recent decisions affecting current work:

- Foundation-first: RuoYi 单体参考框架（非 yudao，JDK 1.8 / Spring Boot 2.5.9 锁定）
- 认证保持外部 spt-auth（HTTP），不合入单体
- 双 ORM 单 DataSource + JpaTransactionManager @Primary
- spt-tools 内联顺序 core→(data,http,file)→(jpa,web,mybatis,shiro,aop,config)
- [Phase ?]: Phase 1 skeleton complete: 5-module aggregator, spring-boot-starter-parent:2.5.9 grandparent (broke spt-parent chain), zero-error compile + empty-context boot + fat-jar-only-admin verified
- [Phase ?]: Shiro auth chain placed in zgbas-system not framework (D-08 topology: framework can't see system classes)
- [Phase ?]: ruoyi-common 4.7.2 + UserAgentUtils 1.21 jar deps added for ShiroDbRealm compile
- [Phase 03-02]: IndexController stub-port (required=false x3 + null-guards) so /index renders menu via authOpenFacade while Phase-4 business data degrades (D-P3-10)
- [Phase 03-02]: MyIndexController deferred to Phase 5 (report-contract cascade); stub contracts IPmProcessClient/IApproveWaitDealClient in system for Phase 4 replacement
- [Phase ?]: WebSocketServer @OnOpen stub via comment-out (Phase 4 IApproveWaitDealClient) + full frontend 608 templates/742 JS-CSS copied
- [Phase 03-04]: D-P3-13 startup gate — 14-test @SpringBootTest(RANDOM_PORT) proves Shiro beans wire + endpoints reachable, BUT ⚠ NON-HERMETIC: clean `mvn test` ERRORS (Could not resolve placeholder 'SPT_APP_SECRET'); passes only with `DB_PASSWORD`+`SPT_APP_SECRET` exported locally (03-01 un-excluded ToolsShiroConfig → authOpenFacade became eager startup dep; dev placeholders have no default). Accepted per user Option 4 (local-export = passing)
- [Phase ?]: Prod auth config externalized: mock-backdoor OFF in prod (empty placeholder), thymeleaf.cache=true (03-04)
- [Phase ?]: Phase 4 D-P4-01 方案 A 落地: 放宽 @EnableFeignClients 扫 com.spt.bas.client.remote + basServerConfig bean + spt.bas.server.url=localhost:8080 让 238 契约 SpEL 自回环到本进程 (04-01)
- [Phase ?]: Phase 4 D-P4-01a path 前缀: BasFeignPathConfig.basServerPathStripper RequestInterceptor 剥离 spt-bas-server/ 前缀, 不设单体 context-path 保 Phase 3 AUTH-03 Shiro 根路径链 (04-01)
- [Phase ?]: Phase 4 D-P4-04 rocketmq: rocketmq-spring-boot-starter:2.2.2 在 zgbas-system pom 声明 (源 basCore/pom.xml verbatim); 懒连接启动验证不阻塞; dev yml 占位带默认, prod 全占位 D-P2-13 (04-01)
- [Phase ?]: Phase 4 WR-02 占位脚手架: ZgbasApplicationTest 扩 4 @Disabled (Wave 3/4 移除后激活) + 1 feignSelfLoopbackWiring_probe Wave 0 即运行 (fail-fast 验 D-P4-01/01a); 现 19 test 14 旧+5 新全绿 (04-01)
- [Phase 04]: Phase 4 Wave 1 done: 238 @FeignClient contracts + 14 data carriers (dto/util/common/riskScore) ported verbatim to zgbas-system; BIZ-01/03 compile+runtime prerequisite satisfied (04-02)
- [Phase 04]: Phase 4 04-02 commit order reversed: Task 2 (data carriers) before Task 1 (remote contracts) — bidirectional compile coupling (ICtrContractClient needs dto.CtrContractDto, IRiskApplyClient needs common.*) resolved per plan's anticipated reorder
- [Phase 04]: Phase 4 04-02 stub upgrade: IApproveWaitDealClient + IPmProcessClient Phase 3 stubs replaced with source-real @FeignClient extends BaseClient contracts; IndexController unaffected (method signatures preserved: getUserWaitDealNum, findAccess); zgbas-admin sanity compile green
- [Phase 04]: Phase 4 04-02 PM-domain 13 contracts ported verbatim as source-real interfaces; D-P4-02 stub-degradation deferred to Plan 04-06 BFF field layer (@Autowired(required=false)), not at contract interface layer
- [Phase ?]: test
- [Phase ?]: [Phase 04]: Phase 4 04-04 Wave 2b done: basServer service 241 iface + 248 impl + 5 域子包 44 (ctr/logistics/performance/rt/stock) = 533 files ported verbatim; PM absorbed per Decision A (annotation/cache/dao/service/util 51 files); merged compile gate GREEN closes 04-03 + 04-04 (cascade 320->0 via Rule 3 completion + Rule 1 source-bug fixes)
- [Phase ?]: [Phase 04]: Phase 4 04-04 Rule 3 cascade deps verbatim source basServer/basCore pom: pdfbox 2.0.29 + xxl-job-core 2.3.0 (compile-time only, P6 scheduling excluded) + report-client + purchase-client 2.0.1-SNAPSHOT (types-only, P5/v2 defer) + spring-cloud-alibaba-commons + nacos-common (util classes only, Phase 2 #9 nacos-discovery stays in force)
- [Phase ?]: [Phase 04]: Phase 4 04-04 Rule 1 source-bug fixes (latent in feat-系统重构v5.0): TokenUtil.createToken(Map,String) overload added as 1-line delegate (fixes 3 source callers basServer/web/basWx); BasicErrorController Date->LocalDateTime (ErrorResp field type); pmClient constant/PmConstants.java inlined (Phase 2 oversight)
- [Phase ?]: [Phase 04]: Phase 4 04-04 Phase 6 placement correction: command/BasCommandExecutor + package-info removed (has @XxlJob method + imports 4 task/ classes). 04-03 SUMMARY established @XxlJob handler -> P6 rule but Wave 2a command/ had leaked it; Phase 6 re-ports command + task cluster (xxl-job -> RuoYi quartz)
- [Phase ?]: test decision
- [Phase ?]: [Phase 04]: Phase 4 04-05 Wave 3 done: basServer api 223 (MQApi deferred P6 per @XxlJob rule) + PM api 13 ported verbatim; D-P4-01 方案 A 关键约束满足 (0 implements I*Client, 228 extends BaseApi); merged compile + startup gates GREEN (19/0/0/4 skipped, no Phase 2/3 regression)
- [Phase ?]: [Phase 04]: Phase 4 04-05 Rule 3 wiring: ZgbasApplication widen @EnableFeignClients to scan basWx (com.spt.bas.purchase.wx.client.remote, 16 svc refs, v2-defer) + report (com.spt.bas.report.client.remote, 9 svc refs, P5 defer); both self-loop to localhost:8080 where no impl → runtime 404 (D-P4-02 lazy-degradation extended to service layer)
- [Phase ?]: [Phase 04]: Phase 4 04-05 Rule 3 wiring: com.spt.pm.dao added to @EnableJpaRepositories (Phase 2 oversight — entity scan had pm.entity but dao missed pm.dao; 14 PM BaseDao); com.spt.tools.http.interceptor.BasicErrorController added to ComponentScan excludeFilters (bean-name conflict with basServer customisation — same precedent as Phase 2 FeignConfig exclusion)
- [Phase ?]: [Phase 04]: Phase 4 04-05 Phase 6 re-port memory updated: api/MQApi.java joins xxl-job cluster (basServer/task/23 + rocketmq/task/8 + command/BasCommandExecutor + 4 task classes). MQApi is API-layer trigger facade for 8 Synchronized*Task handlers — same Rule 3 defer as BasCommandExecutor 04-04
- [Phase ?]: Phase 4 04-06 capstone: 267 BFF ported + D-P4-01a path-prefix correction + D-P4-02 zero-stub + WR-02 green; Phase 4 COMPLETE 6/6
- [Phase 05]: Phase 5 completed: report mybatis + report service + 54 report api 全部迁入单体路径；`ReportFeignPathConfig` 自回环前缀 `/spt-bas-report` 生效，W5/W6 gate 绿灯（`ZgbasApplicationTest` 25/0/0/1，full reactor compile green）
- [Phase 05]: W5 启动期最小消歧已固化：`RptBaseCostApi` 使用显式 controller bean 名避开 bas/report 同名冲突，`RptApplyBusinessPayApi` 由 `@Resource` 改为按类型注入以避免误命中 bas 侧同名 service bean
- [Phase 05]: W6 proof 收口：`sampleReportQuery_proof` 默认 `@Disabled` 作为真实 DB 手动验收口，`reportHttpReachability_proof` 自动验证 `/spt-bas-report/rpt/fundReceivableStatistics/findPage` 与 `/spt-bas-report/business/overview/api/findBusinessOverviewList` 非 404；精确扫描确认 `EXACT_REPORT_STUB_COUNT=0`
- [Phase 05]: closeout hardening 去掉 `RptBaseCostServiceImpl` / `RptUserRoiServiceImpl` / `RptSummaryRoiServiceImpl` 对 `reportRptBaseCostMapper` 的字符串 `@Qualifier` 耦合；`reportApiPathPrefixWiring_probe` 扩展覆盖 `fundReceivableStatistics` / `baseCost` / `businessPay` 三条 report 路径

### Pending Todos

None yet.

### Blockers/Concerns

- Phase 1 编译止血可能 unmask 下层语义错误（gotcha 级联），需逐层修复至零错
- spt-tools-jpa 引用最广（1226 处），内联时注意 BaseDao/IdEntity 体系完整性
- jdbc.properties 含生产库明文密码，重构时需轮换并外置
- ⚠ Phase 3 启动测试非 hermetic：`application-dev.yml` 的 `${DB_PASSWORD}` / `${SPT_APP_SECRET}` 无默认值，无 test-resource/pom 供给；`mvn test` 需先本地 export（与 Phase 2 的 DB_PASSWORD 前置同契约）。CI/hermetic 化需另立 task（H2 或 test-profile 注入占位值），本期按 Option 4 维持现状
- purchase 侧延迟契约仍保留 1 个 `required=false` 残留，不在 Phase 05 处理范围内

### Quick Tasks Completed

| # | Description | Date | Commit | Directory |
|---|-------------|------|--------|-----------|
| 260718-blh | Phase 5 housekeeping — mark report migration complete + commit W4-W6 | 2026-07-18 | 8fbea05+2955004 | [260718-blh-phase-5-housekeeping-roadmap-phase-5-com](./quick/260718-blh-phase-5-housekeeping-roadmap-phase-5-com/) |
| 260718-g93 | Fix /index NPE — add ConfigUtil.init() to ApplicationStartup | 2026-07-18 | d0a388e | [260718-g93-index-npe-applicationstartup-configutil-](./quick/260718-g93-index-npe-applicationstartup-configutil-/) |
| 260718-hal | Fix /index Thymeleaf NPE — register ShiroUtil bean for @shiroUtil SpEL | 2026-07-18 | 49682cf | [260718-hal-index-thymeleaf-npe-shiroutil-bean-shiro](./quick/260718-hal-index-thymeleaf-npe-shiroutil-bean-shiro/) |

## Deferred Items

Items acknowledged and carried forward from previous milestone close:

| Category | Item | Status | Deferred At |
|----------|------|--------|-------------|
| v2 | basWx 微信采购小程序迁入（WX-01, WX-02） | Deferred to v2 | Project init |

## Session Continuity

Last session: 2026-07-19T12:56:32.309Z
Stopped at: Phase 7 context gathered (4 decisions: hybrid verify form, hybrid comparison baseline, tiered coverage, mixed gap handling)
Resume file: .planning/phases/07-alignment-verification/07-CONTEXT.md
