---
gsd_state_version: 1.0
milestone: v1.0
milestone_name: milestone
status: planning
stopped_at: Phase 4 context gathered
last_updated: "2026-07-16T16:19:55.632Z"
last_activity: 2026-07-16
progress:
  total_phases: 7
  completed_phases: 3
  total_plans: 11
  completed_plans: 11
  percent: 43
---

# Project State

## Project Reference

See: .planning/PROJECT.md (updated 2026-07-16)

**Core value:** 单进程启动即可跑通全部供应链业务（登录 → 核心业务 → 报表 → 定时任务），行为对齐旧系统 zgbas
**Current focus:** Phase 4 — 核心业务迁移

## Current Position

Phase: 4
Plan: Not started
Status: Ready to plan
Last activity: 2026-07-16

Progress: [██████████] 100%

## Performance Metrics

**Velocity:**

- Total plans completed: 13
- Average duration: — min
- Total execution time: 0 hours

**By Phase:**

| Phase | Plans | Total | Avg/Plan |
|-------|-------|-------|----------|
| 1 | 1 | - | - |
| 02 | 6 | - | - |
| 03 | 4 | - | - |

**Recent Trend:**

- Last 5 plans: —
- Trend: —

*Updated after each plan completion*
| Phase 1 P01 | 7 | 2 tasks | 14 files |
| Phase 03 P01 | 5 | 2 tasks | 16 files |
| Phase 03 P02 | 4 | 2 tasks | 10 files |
| Phase 03 P03 | 2 | 2 tasks | 2709 files |
| Phase 03 P04 | 12 | 2 tasks | 3 files |

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

### Pending Todos

None yet.

### Blockers/Concerns

- Phase 1 编译止血可能 unmask 下层语义错误（gotcha 级联），需逐层修复至零错
- spt-tools-jpa 引用最广（1226 处），内联时注意 BaseDao/IdEntity 体系完整性
- jdbc.properties 含生产库明文密码，重构时需轮换并外置
- ⚠ Phase 3 启动测试非 hermetic：`application-dev.yml` 的 `${DB_PASSWORD}` / `${SPT_APP_SECRET}` 无默认值，无 test-resource/pom 供给；`mvn test` 需先本地 export（与 Phase 2 的 DB_PASSWORD 前置同契约）。CI/hermetic 化需另立 task（H2 或 test-profile 注入占位值），本期按 Option 4 维持现状

## Deferred Items

Items acknowledged and carried forward from previous milestone close:

| Category | Item | Status | Deferred At |
|----------|------|--------|-------------|
| v2 | basWx 微信采购小程序迁入（WX-01, WX-02） | Deferred to v2 | Project init |

## Session Continuity

Last session: 2026-07-16T16:19:55.627Z
Stopped at: Phase 4 context gathered
Resume file: .planning/phases/04-core-business/04-CONTEXT.md
