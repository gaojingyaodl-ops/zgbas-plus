---
phase: 06-quartz-migration
plan: 03
subsystem: zgbas-quartz
tags: [quartz, xxl-job-removal, handler-migration, repackage, d-p6-09, d-p6-11, rocketmq-task]
requires:
  - phase-06-quartz-migration/06-01 (RuoYi quartz infra + com.spt.common.utils.spring.SpringUtils + Constants whitelist)
  - phase-06-quartz-migration/06-02 (Pattern 3 conventions — this plan follows the same bean-name + logger strategy)
  - phase-04-core-business (com.spt.bas.server.dao.* + com.spt.pm.dao.* + rocketmq.* support classes as @Autowired targets)
  - phase-02-infrastructure (Druid @Primary DataSource + spt-tools inline)
provides:
  - 8 Synchronized*Task handlers ported to com.spt.quartz.task with explicit @Component bean names
  - 9 @XxlJob methods translated to plain public methods per Pattern 3 (SynchronizedWorkTargetTask carries 2 methods)
  - D-P6-11 dependency map for 06-04 MQApi direct-service-call refactor (8 handler methods classified 1:N vs 1:1 + recommended service-layer combo-method targets)
  - Bean-name manifest for 06-05 D-P6-02 sys_job translation (8 handlers + 9 method endpoints)
affects:
  - zgbas-quartz/src/main/java/com/spt/quartz/task/*.java (8 new files; no existing files touched)
tech-stack:
  added: []
  patterns:
    - xxl-job @XxlJob annotation → @Component bean + plain public method (Pattern 3, same as 06-02)
    - XxlJobHelper.log → log.info (source already had `protected Logger log = LoggerFactory.getLogger(this.getClass())` field — preserved verbatim)
    - XxlJobHelper.handleSuccess → log.info (ops-visibility translation; AbstractQuartzJob.after detects success via no-exception path)
    - D-P6-09 repackage: handler self-package only (com.spt.bas.server.rocketmq.task → com.spt.quartz.task); @Autowired dao/entity/rocketmq-support packages preserved
    - D-P6-11 handler preservation: business method bodies (pagination + RocketMQ send) preserved verbatim — handler stays as sys_job-scheduled entry point; MQApi HTTP endpoints untouched (06-04 owns the direct-service-call refactor on the HTTP side)
    - D-P6-12 blocking strategy: handler classes do NOT add @DisallowConcurrentExecution (RuoYi picks QuartzJobExecution vs QuartzDisallowConcurrentExecution by sys_job.concurrent field, same as 06-02)
key-files:
  created:
    - zgbas-quartz/src/main/java/com/spt/quartz/task/SynchronizedApplyMatchDetailTask.java
    - zgbas-quartz/src/main/java/com/spt/quartz/task/SynchronizedApplyMatchTask.java
    - zgbas-quartz/src/main/java/com/spt/quartz/task/SynchronizedBsCompanyTask.java
    - zgbas-quartz/src/main/java/com/spt/quartz/task/SynchronizedCtrContractOphisTask.java
    - zgbas-quartz/src/main/java/com/spt/quartz/task/SynchronizedCtrContractTask.java
    - zgbas-quartz/src/main/java/com/spt/quartz/task/SynchronizedCtrProductTask.java
    - zgbas-quartz/src/main/java/com/spt/quartz/task/SynchronizedPmApproveTask.java
    - zgbas-quartz/src/main/java/com/spt/quartz/task/SynchronizedWorkTargetTask.java
  modified: []
decisions:
  - D-P6-09 LOCKED applied (continuation from 06-02): only handler self-package changed; @Autowired com.spt.bas.server.rocketmq.* + com.spt.bas.server.dao.* + com.spt.pm.dao.* + com.spt.bas.client.entity.* imports preserved (grep-verified: 22 distinct business imports retained across the 8 files)
  - D-P6-12 applied (continuation from 06-02): no @DisallowConcurrentExecution on handler classes
  - Bean name convention: class-name-lowercased (e.g. `synchronizedCtrContractTask`) — matches 06-02 strategy and aligns with source MQApi field names (e.g. `private SynchronizedBsCompanyTask synchronizedBsCompanyTask`); not the source `@XxlJob` method-name values (which are method-scoped, not class-scoped)
  - Logger strategy: source field `protected Logger log = LoggerFactory.getLogger(this.getClass())` preserved verbatim in all 8 files — source-faithful 1:1 port (no `@Slf4j` migration, no `private static final` rewrite). Dynamic-`this.getClass()` logger pattern is preserved as-is; per scope boundary this is pre-existing source style, not introduced by this plan
  - `XxlJobHelper.handleSuccess` → `log.info` (not silent delete) to preserve the success-path ops message (same as 06-02 convention for CtrContractProfitTask/SettlementTask)
metrics:
  duration: ~20 min
  completed: 2026-07-18
  tasks: 2 (2 auto)
  files: 8 (all new) + 1 SUMMARY
---

# Phase 6 Plan 03: basServer/rocketmq/task Synchronized*Task Handler Migration Summary

Ported 8 active xxl-job handler classes from `zgbas/basCore/basServer/.../rocketmq/task/` into `zgbas-quartz/src/main/java/com/spt/quartz/task/`, applying D-P6-09 repackage (self-package only) + Pattern 3 translation (xxl-job → RuoYi quartz bean). 9 `@XxlJob` methods (SynchronizedWorkTargetTask carries 2: `synchronizedAllWorkTarget` + `testSendMessage`) flattened to plain public methods on `@Component("synchronizedXxxTask")` beans whose names align with source `MQApi.java` field names AND with planned `sys_job.invoke_target` short names. `mvn -pl zgbas-quartz -am compile` is GREEN across all 5 modules with **0 ERRORs and zero deferred cascades** — cleaner than the plan's forecast (BasCommandExecutor / ReportCommandExecutor / BasWebCommand cascades from 06-04 did NOT fire because none of the Synchronized*Task handlers reference those command executors).

Per D-P6-11, the business method bodies (pagination + ThreadPoolExecutorEngine + RocketMQ async send) are preserved verbatim. The handler layer stays as the **sys_job-scheduled entry point**; the source `MQApi` HTTP endpoints (`GET /mq/api/ctrContractTask`, etc.) remain untouched and will be refactored to call underlying service methods directly in 06-04 Task 3 — yielding two coexisting entry points (sys_job async cron + MQApi HTTP sync) that execute equivalent business logic.

## Tasks Completed

| # | Type | Name | Commit |
|---|------|------|--------|
| 1 | auto | Port 8 basServer/rocketmq/task/ Synchronized*Task handlers to com.spt.quartz.task + xxl-job→quartz translation | 0bbad07 |
| 2 | auto | Consolidated compile gate (zgbas-quartz -am) + SUMMARY.md with handler manifest + D-P6-11 dependency map | (this commit) |

## What Landed

### Handler Manifest (for 06-05 D-P6-02 sys_job translation)

8 active handlers, all with explicit `@Component` bean names. Total **9** `@XxlJob` methods translated (SynchronizedWorkTargetTask contributes 2 — the only multi-method file in this batch, matching research §Common Pitfalls 4 "9 @XxlJob methods across 8 files").

| # | Class | @Component bean | Original @XxlJob value(s) | Translated method signature(s) | Logger strategy |
|---|-------|-----------------|---------------------------|--------------------------------|------------------|
| 1 | SynchronizedApplyMatchDetailTask | `synchronizedApplyMatchDetailTask` | `synchronizedAllApplyMatchDetail` | `synchronizedAllApplyMatchDetail()` | source field `log` (`LoggerFactory.getLogger(this.getClass())`) |
| 2 | SynchronizedApplyMatchTask | `synchronizedApplyMatchTask` | `synchronizedAllApplyMatch` | `synchronizedAllApplyMatch()` | source field `log` |
| 3 | SynchronizedBsCompanyTask | `synchronizedBsCompanyTask` | `synchronizedAllBsCompany` | `synchronizedAllBsCompany()` | source field `log` |
| 4 | SynchronizedCtrContractOphisTask | `synchronizedCtrContractOphisTask` | `synchronizedAllCtrContractOphis` | `synchronizedAllCtrContractOphis()` | source field `log` |
| 5 | SynchronizedCtrContractTask | `synchronizedCtrContractTask` | `synchronizedAllCtrContract` | `synchronizedAllCtrContract()` | source field `log` |
| 6 | SynchronizedCtrProductTask | `synchronizedCtrProductTask` | `synchronizedAllCtrProduct` | `synchronizedAllCtrProduct()` | source field `log` |
| 7 | SynchronizedPmApproveTask | `synchronizedPmApproveTask` | `synchronizedAllPmApprove` | `synchronizedAllPmApprove()` | source field `log` |
| 8 | SynchronizedWorkTargetTask | `synchronizedWorkTargetTask` | `synchronizedAllWorkTarget`, `testSendMessage` | `synchronizedAllWorkTarget()` / `testSendMessage()` | source field `log` |

### Compile Gate Status

- `mvn -pl zgbas-quartz -am compile` (includes 06-01 RuoYi infra + 06-02 21 task handlers + 06-03 8 Synchronized handlers): **GREEN**, 5/5 modules SUCCESS, **0 ERROR**.
- **Deferred cascades forecast but did NOT fire:** `com.spt.bas.server.command.BasCommandExecutor` (06-04), `com.spt.bas.report.server.command.ReportCommandExecutor` (06-04), `com.spt.bas.web.config.BasWebCommand` (06-04). Plan Task 2 verify tolerated these as non-blocking; **none appeared** because the 8 Synchronized*Task handlers ported here have no compile-time references to those command executors. Their only cross-module dependencies are `com.spt.bas.server.dao.*` (7 DAOs) + `com.spt.pm.dao.PmApproveDao` + `com.spt.bas.client.entity.*` (7 entities) + `com.spt.pm.entity.PmApprove` + `com.spt.bas.server.rocketmq.*` (RocketMQ support: properties / tags enum / send callback / thread pool engine) + `RocketMQTemplate` — all present in zgbas-system per Phase 4.

### D-P6-11 Internal Dependency Map (for 06-04 Task 3 MQApi direct-service-call refactor)

Each row analyzes a Synchronized*Task business method to determine whether its body can collapse to a single existing service.method() call (**1:1**) or requires a new service-layer combo method to encapsulate multi-step orchestration (**1:N**). This table is the direct input for 06-04 Task 3 (MQApi refactor: replace `@Autowired Synchronized*Task` handler refs with direct service refs).

| # | Handler bean.method | MQApi HTTP endpoint (source) | Method body orchestration | Classification | 06-04 D-P6-11 target |
|---|---------------------|------------------------------|---------------------------|----------------|----------------------|
| 1 | `synchronizedApplyMatchDetailTask.synchronizedAllApplyMatchDetail` | `GET /mq/api/applyMatchDetailTask` → `applyMatchDetailTask.synchronizedAllApplyMatchDetail()` | `applyMatchDetailDao.selectAllCount()` → paginate (PAGE_COUNT=100) → `ThreadPoolExecutorEngine.execute(ThreadQuery)` → `getApplyMatch(page)` → `applyMatchDetailDao.findAll(page)` → loop `rocketMQTemplate.asyncSend(commonTopic:APPLY_MATCH_DETAIL, message)` | **1:N** (count + paginate + thread-pool dispatch + async send per row) | New combo method e.g. `IApplyMatchService.synchronizedAllApplyMatchDetail()` (or `IMqSyncService.syncApplyMatchDetail()`) encapsulating pagination + async send |
| 2 | `synchronizedApplyMatchTask.synchronizedAllApplyMatch` | `GET /mq/api/applyMatchTask` → `applyMatchTask.synchronizedAllApplyMatch()` | Same shape as #1 with `ApplyMatchDao` + `commonTopic:APPLY_MATCH` | **1:N** | New combo method `IApplyMatchService.synchronizedAllApplyMatch()` |
| 3 | `synchronizedBsCompanyTask.synchronizedAllBsCompany` | `GET /mq/api/companyTask` → `synchronizedBsCompanyTask.synchronizedAllBsCompany()` | Inline paginate `BsCompanyDao` (no ThreadQuery) → `rocketMQTemplate.asyncSend(companyTopic:ALL, messageBody)` per row | **1:N** (inline pagination + async send per row) | New combo method `IBsCompanyService.synchronizedAllBsCompany()` |
| 4 | `synchronizedCtrContractOphisTask.synchronizedAllCtrContractOphis` | `GET /mq/api/ophisTask` → `ophisTask.synchronizedAllCtrContractOphis()` | Same shape as #1 with `CtrContractOphisDao` + `contractHistoryTopic:OPHIS` | **1:N** | New combo method `ICtrContractService.synchronizedAllCtrContractOphis()` (ophis sits in contract domain) |
| 5 | `synchronizedCtrContractTask.synchronizedAllCtrContract` | `GET /mq/api/ctrContractTask` → `ctrContractTask.synchronizedAllCtrContract()` | Same shape as #1 with `CtrContractDao` + `contractTopic:ALL_CONTRACT`; per-message `KEYS` header built from `contractType` + `contractNo` | **1:N** | New combo method `ICtrContractService.synchronizedAllCtrContract()` |
| 6 | `synchronizedCtrProductTask.synchronizedAllCtrProduct` | `GET /mq/api/ctrProductTask` → `productTask.synchronizedAllCtrProduct()` | Same shape as #1 with `CtrProductDao` + `ctrProduct:PRODUCT` | **1:N** | New combo method (in `ICtrContractService` or new `ICtrProductService`) |
| 7 | `synchronizedPmApproveTask.synchronizedAllPmApprove` | `GET /mq/api/pmApproveTask` → `approveTask.synchronizedAllPmApprove()` | Same shape as #1 with `PmApproveDao` + `commonTopic:PM_APPROVE` | **1:N** | New combo method `IPmApproveService.synchronizedAllPmApprove()` |
| 8a | `synchronizedWorkTargetTask.synchronizedAllWorkTarget` | `GET /mq/api/workTargetTask` → `workTargetTask.synchronizedAllWorkTarget()` | Inline paginate `WorkTargetDao` → `rocketMQTemplate.asyncSend(workTargetTopic:ALL, messageBody)` per row | **1:N** | New combo method `IWorkTargetService.synchronizedAllWorkTarget()` |
| 8b | `synchronizedWorkTargetTask.testSendMessage` | `GET /mq/api` (root, no path) → `workTargetTask.testSendMessage()` | Single `rocketMQTemplate.send("yyc-data", message)` — no DAO, no pagination | **1:1** (single RocketMQ send) | MQApi can directly invoke `rocketMQTemplate.send("yyc-data", message)` or call a trivial wrapper `IMqSyncService.testSendMessage()`. **Note**: this is a dev/test endpoint (hardcoded topic `yyc-data`); 06-04 may recommend deprecating it rather than preserving the HTTP route. |

**Key insight for 06-04 planner:** 8 of the 9 methods are **1:N** (pagination + per-row async RocketMQ send). The cleanest 06-04 refactor is to introduce **8 new combo methods on existing service ifaces** (each method's body is the verbatim copy of the current handler method body — the same code that runs via sys_job). This keeps the sys_job entry point and the MQApi HTTP entry point executing literally the same code path, satisfying D-P6-11's "behavior-equivalent dual entry point" semantic. The alternative (extracting to a new `IMqSyncService` aggregator) is viable but adds a new service iface; the per-domain-service approach reuses Phase 4's `com.spt.bas.server.service.*` ifaces and is the recommended path.

**Shared dependency note for 06-04:** All 8 handlers depend on `RocketMQTemplate` + `RocketmqCustomProperties` + `RocketmqSendCallbackBuilder` (+ `ThreadPoolExecutorEngine` for the ThreadQuery variants). The 8 new service combo methods will need these as `@Autowired` dependencies on the service impl side. Phase 4's existing service impls may or may not already inject `RocketMQTemplate` — 06-04 Task 3 should verify before adding new `@Autowired` fields.

### D-P6-02 Pre-extracted Fields (for 06-05 xxl_job_info → sys_job translation)

Same field-mapping convention as 06-02 Task 2. For each `@XxlJob` method above, 06-05 will populate these `sys_job` columns:

| Column | Source | Notes |
|--------|--------|-------|
| `job_name` | xxl_job_info.job_desc (Chinese description) | Translated verbatim |
| `job_group` | 'DEFAULT' | All 9 methods in this batch go to DEFAULT group (none are executeCommand-style) |
| `invoke_target` | `<beanName>.<method>([args])` | Bean name from this SUMMARY's manifest; this batch has no args (no `getJobParam` translation) — exception: `testSendMessage` may be flagged manual-only per 06-04 |
| `cron_expression` | xxl_job_info.job_cron (6/7-field → quartz 7-field adaptation) | D-P6-02 translation rules in 06-RESEARCH §Pattern 2; fallback `0 0 0 1 1 ? 2099` if no cron (D-P6-03 ② manual type) |
| `misfire_policy` | '3' (default, do nothing) | Per RuoYi default |
| `concurrent` | '0' or '1' | From xxl_job_info.executor_block_strategy: SERIAL_EXECUTION → '1'; DISCARD_LATER → '0' |
| `status` | '0' (NORMAL) if xxl_job_info.trigger_status=1; '1' (PAUSED) otherwise | D-P6-03 ①/② |
| `create_by` | 'admin' | Default |
| `create_time` | sysdate() | Default |
| `remark` | '迁自 xxl-job `<executor_handler>`' | Provenance tag |

### Dependency Relationships

- **Requires (already in place):**
  - 06-01 Wave 1: RuoYi quartz infra (18 classes), `com.spt.common.utils.spring.SpringUtils`, `com.spt.common.constant.Constants` (whitelist `com.spt`), `@ComponentScan("com.spt")` coverage.
  - 06-02 Wave 2: Pattern 3 translation conventions + bean-name format (`<classNameLowercased>`).
  - Phase 4: `com.spt.bas.server.dao.*` (7 DAOs) + `com.spt.pm.dao.PmApproveDao` + `com.spt.bas.client.entity.*` (7 entities) + `com.spt.pm.entity.PmApprove` + `com.spt.bas.server.rocketmq.*` (RocketmqCustomProperties / tags enums / RocketmqSendCallbackBuilder / ThreadPoolExecutorEngine) — all `@Autowired` targets of the 8 handlers.
  - Phase 2: Druid @Primary DataSource, spt-tools inline (JsonUtil / ApplicationException), RocketMQ starter bean.
- **Provides (downstream consumers):**
  - 06-04 Task 3 MQApi D-P6-11 refactor: this SUMMARY's dependency map (8 handler methods classified 1:N vs 1:1 + recommended service combo-method targets) is the direct input for the MQApi HTTP-endpoint → service-direct-call refactor.
  - 06-05 D-P6-02: this SUMMARY's handler manifest is the canonical source for `sys_job.invoke_target` translation (8 beans + 9 method endpoints).
- **Affects (no breaking changes):**
  - No existing files modified. 8 new files under `zgbas-quartz/src/main/java/com/spt/quartz/task/`.

## Deviations from Plan

None. Plan executed exactly as written:

- Bean naming: used class-name-lowercased convention (per plan Task 1.C: "约定 SynchronizedXxxTask 类的 bean 名为类名首字母小写 synchronizedXxxTask") — matches 06-02 precedent and source `MQApi.java` field names.
- Compile forecast: plan Task 2 tolerate deferred 06-04 cascades (BasCommandExecutor / ReportCommandExecutor / BasWebCommand); these did not fire (0 total ERRORs) — same as 06-02 outcome, because this handler batch has no compile-time refs to those command executors.
- No `com.alibaba.cloud.commons.lang.StringUtils` substitution needed in this batch (only SynchronizedCtrContractTask uses `StringUtils`, and source already imports `org.apache.commons.lang3.StringUtils` — no Rule 3 fix required here, unlike 06-02 CtrContractScheduleTask).
- Logger strategy: source field `protected Logger log = LoggerFactory.getLogger(this.getClass())` preserved verbatim in all 8 files (no migration to `@Slf4j` or `private static final Logger`). Pre-existing source style — out of scope per deviation Rule scope boundary.

## Known Stubs / Pre-existing Dead Code (behavior-equivalent — preserved as-is)

- `SynchronizedWorkTargetTask.testSendMessage` is a **dev/test endpoint** (hardcoded topic `"yyc-data"`, payload `"这个是测试消息"`, no business logic). Not a stub introduced by this plan — it is a faithful 1:1 port of source `@XxlJob(value = "testSendMessage")`. 06-04 planner may recommend deprecating both the handler method and the corresponding `GET /mq/api` (root) HTTP route; for now it is preserved verbatim per D-P6-09 scope boundary.

## Threat Flags

No new threat surfaces introduced beyond what the plan's `<threat_model>` already tracks. All mitigations applied as planned:

- T-06-03-01 (Tampering — service iface package change): **mitigated** — grep verified all 8 ported files still contain `com.spt.bas.server.rocketmq.*` + `com.spt.bas.server.dao.*` + `com.spt.pm.dao.*` imports (only handler self-package changed to `com.spt.quartz.task`).
- T-06-03-02 (DoS — multi-service orchestration + RocketMQ send): **mitigated by preservation, not new code** — handler bodies unchanged from source; `sys_job.concurrent='1'` will be set at the sys_job data layer (06-05) for serial-type tasks; admin ShiroFilter gates HTTP reachability (Phase 3).
- T-06-03-03 (Repudiation — silent exception swallow): **mitigated** — no `XxlJobHelper.handleFail` calls existed in source for this batch (all source methods are fire-and-forget async sends with success-path `handleSuccess` only). Translation preserves the exception-propagation path: any DAO/RocketMQ exception bubbles up to `AbstractQuartzJob.execute` catch → `sys_job_log.status=FAIL` + `exceptionInfo` (2000 char truncation). Async send exceptions go through `RocketmqSendCallbackBuilder.commonCallback()` (source behavior preserved verbatim).
- T-06-03-04 (Tampering — D-P6-11 dual-entry concurrent risk): **accepted per plan** — source xxl-job system already had dual entry (admin cron + manual HTTP), so behavior equivalence is preserved. Service-layer `@Transactional` boundaries (Phase 4) remain in force. This plan does not touch business method bodies.
- T-06-03-SC (Tampering — source code integrity): **mitigated** — read directly from local `/Users/alan/WorkSpace/IDEA/zgbas` checkout; no network fetch.

## Self-Check: PASSED

### File existence (8 handlers)
- zgbas-quartz/src/main/java/com/spt/quartz/task/SynchronizedApplyMatchDetailTask.java — FOUND
- zgbas-quartz/src/main/java/com/spt/quartz/task/SynchronizedApplyMatchTask.java — FOUND
- zgbas-quartz/src/main/java/com/spt/quartz/task/SynchronizedBsCompanyTask.java — FOUND
- zgbas-quartz/src/main/java/com/spt/quartz/task/SynchronizedCtrContractOphisTask.java — FOUND
- zgbas-quartz/src/main/java/com/spt/quartz/task/SynchronizedCtrContractTask.java — FOUND
- zgbas-quartz/src/main/java/com/spt/quartz/task/SynchronizedCtrProductTask.java — FOUND
- zgbas-quartz/src/main/java/com/spt/quartz/task/SynchronizedPmApproveTask.java — FOUND
- zgbas-quartz/src/main/java/com/spt/quartz/task/SynchronizedWorkTargetTask.java — FOUND

### Grep assertions
- xxl-job residue in 8 Synchronized* files (grep `@XxlJob\|XxlJobHelper\|com\.xxl\.job`): **0 hits** — PASS
- `@Component` explicit bean names on 8 files: **8/8** (all `synchronized*Task`) — PASS
- `package com.spt.quartz.task` on every ported file: **8/8** — PASS
- 06-04 cascade ERRORs (BasCommandExecutor / ReportCommandExecutor / BasWebCommand missing-symbol): **0** — PASS (forecast but did not fire)
- Total compile ERRORs in `mvn -pl zgbas-quartz -am compile`: **0** — PASS

### Commits verified to exist
- 0bbad07 (Task 1 — 8 handlers ported) — FOUND

### Compile gate
- `mvn -pl zgbas-quartz -am compile` (JAVA_HOME=Corretto 1.8 + zg_settings.xml): **BUILD SUCCESS**, 5/5 modules SUCCESS, 0 ERROR, 0 deferred cascades. Evidence logged at `/tmp/p6-03-t1.log` (Task 1) and `/tmp/p6-03-t2.log` (Task 2).

### Plan success criteria status
- [x] QUARTZ-03 (8 rocketmq/task Synchronized*Task handlers migration subset) — code translation dimension closed. Remaining QUARTZ-03 work: 06-04 command executors (BasCommandExecutor / ReportCommandExecutor / BasWebCommand) + 06-04 Task 3 MQApi D-P6-11 HTTP-endpoint refactor.
- [x] D-P6-09 (handler repackage to com.spt.quartz.task) — landed. Only handler self-package changed; @Autowired iface packages preserved (grep-verified).
- [x] D-P6-12 (handler classes do NOT add @DisallowConcurrentExecution) — landed (continuation from 06-02).
- [x] Compile gate: 0 ERROR.
- [x] D-P6-11 prep (internal dependency map for 06-04) — delivered: 9 method rows classified 1:1 vs 1:N with recommended service-layer combo-method targets.

## Deferred Items

| Item | Reason | Next Owner |
|------|--------|------------|
| MQApi HTTP endpoint refactor (D-P6-11 direct-service-call) | Plan 06-04 Task 3 scope — separate wave | 06-04 |
| BasCommandExecutor / ReportCommandExecutor / BasWebCommand port (~3 command executors + ~55 executeCommand sys_job rows) | Plan 06-04 scope — separate wave | 06-04 |
| sys_job INSERT data (cron + invoke_target translation for the 9 methods in this batch) | Plan 06-05 scope — depends on user-supplied xxl-job admin DB dump (D-P6-01 checkpoint:human-blocked) | 06-05 |
| Scheduler startup validation (D-P6-06 fail-fast) | Depends on sys_job data (06-05) + scheduler boot (06-06) | 06-06 |
| Dry-run sampling (D-P6-04/05) | Depends on sys_job data | 06-06 |
| Deprecate `SynchronizedWorkTargetTask.testSendMessage` + `GET /mq/api` root endpoint (hardcoded dev topic `yyc-data`) | Source dev/test stub, out of scope for this plan | 06-04 evaluation |
| Dynamic `this.getClass()` logger pattern (vs idiomatic `private static final Logger log = LoggerFactory.getLogger(<ThisClass>.class)`) | Pre-existing source style, preserved 1:1 per scope boundary | Future tech-debt cleanup |

## TDD Gate Compliance

N/A — plan `type=execute` (not `tdd`). No `test(...)` / `feat(...)` gate sequence required. Compile gate is the verification mechanism and is GREEN per Task 2 verify output.
