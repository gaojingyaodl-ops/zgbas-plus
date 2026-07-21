---
phase: 06-quartz-migration
plan: 02
subsystem: zgbas-quartz
tags: [quartz, xxl-job-removal, handler-migration, repackage, d-p6-09]
requires:
  - phase-06-quartz-migration/06-01 (RuoYi quartz infra + com.spt.common.utils.spring.SpringUtils + Constants whitelist)
  - phase-04-core-business (com.spt.bas.server.service.* ifaces as @Autowired targets)
  - phase-02-infrastructure (Druid @Primary DataSource + spt-tools inline)
provides:
  - 21 basServer/task/ handlers ported to com.spt.quartz.task with explicit @Component bean names
  - 44 @XxlJob methods translated to plain public methods per Pattern 3 (XxlJobHelper.log/handleSuccess/handleFail/getJobParam)
  - Bean-name manifest for 06-05 D-P6-02 sys_job translation (all 21 handlers + 44 method endpoints)
affects:
  - zgbas-quartz/src/main/java/com/spt/quartz/task/*.java (21 new files; no existing files touched)
tech-stack:
  added: []
  patterns:
    - xxl-job @XxlJob annotation → @Component bean + plain public method (Pattern 3)
    - XxlJobHelper.log → slf4j log.info / logger.info (slf4j Logger field added where missing)
    - XxlJobHelper.handleSuccess → log.info or delete (success path = void return + no exception)
    - XxlJobHelper.handleFail → throw new RuntimeException (AbstractQuartzJob.after writes status=FAIL)
    - XxlJobHelper.getJobParam → method signature adds String parameter (sys_job.invoke_target passes args via reflection)
    - D-P6-09 repackage: handler self-package only (com.spt.bas.server.task → com.spt.quartz.task); @Autowired iface packages preserved
    - D-P6-12 blocking strategy: handler class does NOT add @DisallowConcurrentExecution (RuoYi picks QuartzJobExecution vs QuartzDisallowConcurrentExecution by sys_job.concurrent field)
key-files:
  created:
    - zgbas-quartz/src/main/java/com/spt/quartz/task/ApplyPayTask.java
    - zgbas-quartz/src/main/java/com/spt/quartz/task/AutoSealPdfTask.java
    - zgbas-quartz/src/main/java/com/spt/quartz/task/BsCompanyCreditTask.java
    - zgbas-quartz/src/main/java/com/spt/quartz/task/BsCompanyTask.java
    - zgbas-quartz/src/main/java/com/spt/quartz/task/BudgetSettlementTask.java
    - zgbas-quartz/src/main/java/com/spt/quartz/task/BusinessRestrictRelieveTask.java
    - zgbas-quartz/src/main/java/com/spt/quartz/task/ConfirmReceiptDcsxTask.java
    - zgbas-quartz/src/main/java/com/spt/quartz/task/CtrContractProfitTask.java
    - zgbas-quartz/src/main/java/com/spt/quartz/task/CtrContractScheduleTask.java
    - zgbas-quartz/src/main/java/com/spt/quartz/task/DcsxAutoApplyPayTask.java
    - zgbas-quartz/src/main/java/com/spt/quartz/task/DcsxRepaymentdTask.java
    - zgbas-quartz/src/main/java/com/spt/quartz/task/DefaultingEnterpriseTask.java
    - zgbas-quartz/src/main/java/com/spt/quartz/task/DepositPaymentTask.java
    - zgbas-quartz/src/main/java/com/spt/quartz/task/InternalBuyTask.java
    - zgbas-quartz/src/main/java/com/spt/quartz/task/PmApproveTask.java
    - zgbas-quartz/src/main/java/com/spt/quartz/task/RepaymentTask.java
    - zgbas-quartz/src/main/java/com/spt/quartz/task/SealBorrowTask.java
    - zgbas-quartz/src/main/java/com/spt/quartz/task/SettlementTask.java
    - zgbas-quartz/src/main/java/com/spt/quartz/task/SignTask.java
    - zgbas-quartz/src/main/java/com/spt/quartz/task/StockVirtualTask.java
    - zgbas-quartz/src/main/java/com/spt/quartz/task/WeChatWorkTask.java
  modified: []
decisions:
  - D-P6-09 LOCKED applied: only handler self-package changed; @Autowired com.spt.bas.server.service.* ifaces preserved (grep-verified 19/21 files contain the original iface imports)
  - D-P6-12 applied: no @DisallowConcurrentExecution on handler classes (RuoYi sys_job.concurrent drives blocking strategy at the Job class level)
  - Rule 3 fix: com.alibaba.cloud.commons.lang.StringUtils import replaced with org.apache.commons.lang3.StringUtils in CtrContractScheduleTask (alibaba-cloud-commons was removed when nacos was deleted in Phase 2 #9; equivalent isBlank semantics; commons-lang3:3.12.0 already transitively on classpath)
  - Logger field reuse: source files that already had a logger field (ApplyPayTask / BudgetSettlementTask / DcsxRepaymentdTask / DefaultingEnterpriseTask / SignTask — all using LoggerFactory.getLogger(<class>).class) kept their existing logger; source files without one got a new private static final Logger log = LoggerFactory.getLogger(<ThisClass>.class); @Slf4j-annotated classes (AutoSealPdfTask / BsCompanyTask / BsCompanyCreditTask / PmApproveTask / StockVirtualTask / WeChatWorkTask) reused Lombok-generated log
metrics:
  duration: ~25 min
  completed: 2026-07-18
  tasks: 2 (2 auto)
  files: 21 (all new)
---

# Phase 6 Plan 02: basServer/task Handler Migration Summary

Ported 21 active xxl-job handler classes from `zgbas/basCore/basServer/.../task/` into `zgbas-quartz/src/main/java/com/spt/quartz/task/`, applying D-P6-09 repackage (self-package only) + Pattern 3 translation (xxl-job → RuoYi quartz bean). 44 `@XxlJob` methods flattened to plain public methods on `@Component("<xxlJobValue>")` beans whose names align 1:1 with `sys_job.invoke_target` short names. `mvn -pl zgbas-quartz -am compile` is GREEN across all 5 modules with **0 ERRORs and zero deferred cascades** — cleaner than the plan's forecast (BasCommandExecutor / rocketmq.task / ReportCommandExecutor / BasWebCommand missing-symbol cascades from 06-03/06-04 did NOT fire because this batch only touches `task/` handlers, none of which reference those command executors).

## Tasks Completed

| # | Type | Name | Commit |
|---|------|------|--------|
| 1 | auto | Port 21 basServer/task/ handlers to com.spt.quartz.task + xxl-job→quartz translation | 3f4ec7b |
| 2 | auto | Consolidated compile gate (zgbas-quartz -am) + SUMMARY.md with handler manifest | (this commit) |

## What Landed

### Handler Manifest (for 06-05 D-P6-02 sys_job translation)

21 active handlers, 20 with `@Component` beans (RepaymentTask is fully commented-out in source, ported verbatim — no bean registered). Total **44** `@XxlJob` methods translated (actual count vs. plan estimate ~48).

| # | Class | @Component bean | Original @XxlJob value(s) | Translated method signature(s) | Logger strategy |
|---|-------|-----------------|---------------------------|--------------------------------|------------------|
| 1 | ApplyPayTask | `applyPayTask` | `autoStartPayProcess`, `autoPay`, `autoPayDcsx`, `autoReceive` | `autoStartPayProcess()` / `autoPay()` / `autoPayDcsx()` / `autoReceive()` | source field `logger` (preserved — see Known Issues) |
| 2 | AutoSealPdfTask | `autoSealPdfTask` | `generateSealPDFSignDCSX`, `successSignContractByKeyword` | `generateSealPDFSignDCSX(String param)` / `successSignContractByKeyword(String param)` | `@Slf4j` `log` |
| 3 | BsCompanyCreditTask | `bsCompanyCreditTask` | `recoverTemporaryAmount`, `initCompanyCredit` | `recoverTemporaryAmount()` / `initCompanyCredit()` | `@Slf4j` `log` |
| 4 | BsCompanyTask | `bsCompanyTask` | `updateCompanyGrey`, `refreshOwnerOfAccountId`, `recoverTotalCreditAmount`, `leaveReleasePublic`, `syncCompanyBusinessExpansion`, `recoverCompanyCreditAmount` | `updateCompanyGrey(String companyName)` / `refreshOwnerOfAccountId()` / `recoverTotalCreditAmount()` / `leaveReleasePublic()` / `syncCompanyBusinessExpansion()` / `recoverCompanyCreditAmount()` | `@Slf4j` `log` |
| 5 | BudgetSettlementTask | `budgetSettlementTask` | `updateBudgetSettlement`, `updateBudgetSettlementByContractNo`, `updateVipRemainingTime`, `applyReceive` | `updateBudgetSettlement()` / `updateBudgetSettlementByContractNo(String param)` / `updateVipRemainingTime()` / `applyReceive()` | source field `logger` |
| 6 | BusinessRestrictRelieveTask | `businessRestrictRelieveTask` | `resetUsableCount` | `resetUsableCount()` | new `private static final Logger log` |
| 7 | ConfirmReceiptDcsxTask | `confirmReceiptDcsxTask` | `initHistoryConfirmReceiptDcsx` | `initHistoryConfirmReceiptDcsx()` | new `private static final Logger log` |
| 8 | CtrContractProfitTask | `ctrContractProfitTask` | `initHistoryProfit`, `refreshProfitData` | `initHistoryProfit()` / `refreshProfitData(String approveNo)` | new `private static final Logger log` |
| 9 | CtrContractScheduleTask | `ctrContractScheduleTask` | `updateRiskScheduleTask`, `doUpdatePerformanceStatusTask`, `refreshContractStatusTask`, `doUnDelieryNotifyTask`, `refreshBuyBilledAmount`, `refreshSellBilledAmount`, `initLogistics`, `refreshOverdueInterest`, `doSignLogistics`, `autoInitiatedSealUsage`, `autoStartDaDiInvoiceApply`, `refreshShippingFile` | (12 methods; most no-arg; `initLogistics(String contractNo)` / `refreshOverdueInterest(String contractNo)` / `autoInitiatedSealUsage(String approveNo)` / `refreshShippingFile(String contractNo)` take a String parameter) | new `private static final Logger log` |
| 10 | DcsxAutoApplyPayTask | `dcsxAutoApplyPayTask` | `autoHb60DayNotApplyDcsxPay` | `autoHb60DayNotApplyDcsxPay()` | new `private static final Logger log` |
| 11 | DcsxRepaymentdTask | `dcsxRepaymentdTask` | `dcsxRepaymentdTask` (method name `run`) | `run()` | source field `logger` |
| 12 | DefaultingEnterpriseTask | `defaultingEnterpriseTask` | `defaultingEnterpriseTask` | `defaultingEnterpriseTask()` | source field `logger` |
| 13 | DepositPaymentTask | `depositPaymentTask` | (none — placeholder class) | (no method) | source field `logger` |
| 14 | InternalBuyTask | `internalBuyTask` | `internalBuyTask` | `internalBuyTask()` | new `private static final Logger log` |
| 15 | PmApproveTask | `pmApproveTask` | `doAutoSign`, `updateCtrLogistics` | `doAutoSign()` / `updateCtrLogistics()` | `@Slf4j` `log` |
| 16 | RepaymentTask | (no bean — file fully commented-out) | (none active) | (none active) | n/a |
| 17 | SealBorrowTask | `sealBorrowTask` | `updateSealBorrow` | `updateSealBorrow()` | new `private static final Logger log` |
| 18 | SettlementTask | `settlementTask` | `updateSettlementTask`, `refreshBreachCommission` | `updateSettlementTask()` / `refreshBreachCommission(String contractNo)` | new `private static final Logger log` |
| 19 | SignTask | `signTask` | `doUploadContractSigned` | `doUploadContractSigned()` | source field `logger` |
| 20 | StockVirtualTask | `stockVirtualTask` | `autoDeleteStockVirtual` | `autoDeleteStockVirtual()` | `@Slf4j` `log` |
| 21 | WeChatWorkTask | `weChatWorkTask` | `pushWeChatWorkLeaderboard` | `pushWeChatWorkLeaderboard()` | `@Slf4j` `log` |

### Skipped Files (2 of 23 source files)

| File | Reason |
|------|--------|
| OrverdurTask.java | Source `@XxlJob(value = "overdueTask")` is on a fully `//`-commented-out line; D-P6-03 ③ classifies as 废弃 (deprecated) → skip migration. |
| TestJob.java | Source is a xxl-job test stub (`demoJobHandler`), not a business handler → not on the QUARTZ-03 migration path. |

### Compile Gate Status

- `mvn -pl zgbas-quartz -am compile` (includes 06-01 RuoYi infra + 06-02 Task 1 handlers): **GREEN**, 5/5 modules SUCCESS, **0 ERROR**.
- **Deferred cascades forecast but did NOT fire:** `com.spt.bas.server.command.*` (BasCommandExecutor — 06-04), `com.spt.bas.server.rocketmq.task.*` (06-03), `com.spt.bas.report.server.command.*` (ReportCommandExecutor — 06-04), `com.spt.bas.web.config.*` (BasWebCommand — 06-04). Plan Task 2 verify tolerated these as non-blocking; **none appeared** because the 21 `task/` handlers in this batch have no compile-time references to those command executors.
- 06-04 BasCommandExecutor will `@Autowired` some of the handlers ported here (ApplyPayTask / BudgetSettlementTask / CtrContractScheduleTask / DcsxAutoApplyPayTask per 06-RESEARCH §Pitfall 9). Those `@Autowired` references resolve through Spring's `com.spt` ComponentScan at runtime — compile-time happens after 06-04 ports BasCommandExecutor into `com.spt.quartz.task` as well (same module, same package, no import needed).

### D-P6-02 Pre-extracted Fields (for 06-05 xxl_job_info → sys_job translation)

For each `@XxlJob` method listed above, 06-05 D-P6-02 will populate these `sys_job` columns from the corresponding `xxl_job_info` row:

| Column | Source | Notes |
|--------|--------|-------|
| `job_name` | xxl_job_info.job_desc (Chinese description) | Translated verbatim |
| `job_group` | 'DEFAULT' (RuoYi default) or 'COMMAND' for executeCommand rows (06-04) | Mostly DEFAULT for this batch |
| `invoke_target` | `<beanName>.<method>([args])` | Bean name from this SUMMARY's manifest; args from xxl_job_info.executor_param |
| `cron_expression` | xxl_job_info.job_cron (6/7-field → quartz 7-field adaptation) | D-P6-02 translation rules in 06-RESEARCH §Pattern 2 |
| `misfire_policy` | '3' (default, do nothing) | Per RuoYi default; override per-task if xxl_job_info shows misfire strategy |
| `concurrent` | '0' (allow) or '1' (disallow) | From xxl_job_info.executor_block_strategy: SERIAL_EXECUTION → '1'; DISCARD_LATER → '0' (RuoYi default) |
| `status` | '0' (NORMAL) if xxl_job_info.trigger_status=1; '1' (PAUSED) if trigger_status=0 | D-P6-03 ①/② |
| `create_by` | 'admin' | Default |
| `create_time` | sysdate() | Default |
| `remark` | '迁自 xxl-job `<executor_handler>`' | Provenance tag |

### Dependency Relationships

- **Requires (already in place):**
  - 06-01 Wave 1: RuoYi quartz infra (18 classes), `com.spt.common.utils.spring.SpringUtils`, `com.spt.common.constant.Constants` (whitelist `com.spt`), `@ComponentScan("com.spt")` coverage.
  - Phase 4: `com.spt.bas.server.service.*` ifaces (`ICtrContractService`, `IApplyPayService`, `IBsCompanyService`, etc.) and `com.spt.pm.*` ifaces — all `@Autowired` targets of the 21 handlers.
  - Phase 2: Druid @Primary DataSource, spt-tools inline (JsonUtil / ApplicationException).
- **Provides (downstream consumers):**
  - 06-04 BasCommandExecutor: `@Autowired ApplyPayTask / BudgetSettlementTask / CtrContractScheduleTask / DcsxAutoApplyPayTask` (per 06-RESEARCH §Pitfall 9).
  - 06-05 D-P6-02: this SUMMARY's handler manifest is the canonical source for `sys_job.invoke_target` translation.
- **Affects (no breaking changes):**
  - No existing files modified. 21 new files under `zgbas-quartz/src/main/java/com/spt/quartz/task/`.

## Deviations from Plan

### Auto-fixed Issues (Rules applied)

**1. [Rule 3 — Blocking dependency] com.alibaba.cloud.commons.lang.StringUtils replaced with org.apache.commons.lang3.StringUtils**
- **Found during:** Task 1 compile gate
- **Issue:** Source `CtrContractScheduleTask.java` imports `com.alibaba.cloud.commons.lang.StringUtils`. The `spring-cloud-alibaba-commons` jar was on the source zgbas microservice classpath but is NOT on zgbas-plus classpath — Phase 2 #9 removed nacos, which transitively dropped alibaba-cloud-commons. First compile produced a single ERROR: `程序包com.alibaba.cloud.commons.lang不存在`.
- **Fix:** Single-line import substitution `com.alibaba.cloud.commons.lang.StringUtils` → `org.apache.commons.lang3.StringUtils`. `commons-lang3:3.12.0` is already transitively on zgbas-quartz classpath (verified via `mvn dependency:tree`). Semantics of `StringUtils.isBlank(String)` are equivalent between the two libraries.
- **Files modified:** zgbas-quartz/src/main/java/com/spt/quartz/task/CtrContractScheduleTask.java
- **Commit:** 3f4ec7b

No other deviations. Plan executed exactly as written otherwise.

## Known Stubs / Pre-existing Dead Code (behavior-equivalent — preserved as-is)

The source handlers contained several pre-existing dead-code paths (fully commented-out method bodies). These are **not migration stubs** — they are faithful 1:1 ports of the source state. Calling these methods via `sys_job` will do nothing (same as the original xxl-job invocations):

| File | Method | State | Resolution path |
|------|--------|-------|-----------------|
| ApplyPayTask | `autoStartPayProcess` / `autoPay` / `autoPayDcsx` | Source body fully `//`-commented; only `autoReceive` has live code | Source business decision; deferred indefinitely (operator can PAUSE corresponding sys_job rows) |
| BudgetSettlementTask | `applyReceive` | Source body fully `//`-commented | Same as above |
| DepositPaymentTask | (no method) | Source is a placeholder class with only a `logger` field, no `@XxlJob` method | Source placeholder; no sys_job row should reference this bean |
| RepaymentTask | (entire class) | Source file entirely `//`-commented (no active class) | Ported verbatim; no `@Component` bean registered; no sys_job row references this class |

## Threat Flags

No new threat surfaces introduced beyond what the plan's `<threat_model>` already tracks. All mitigations applied as planned:
- T-06-02-01 (Tampering — service iface package change): **mitigated** — grep verified 19 of 21 ported files still contain `com.spt.bas.server.service.` imports (the remaining 2 — DepositPaymentTask and RepaymentTask — have no `@Autowired` field at all).
- T-06-02-03 (Repudiation — silent exception swallow): **mitigated** — every `XxlJobHelper.handleFail("...")` translated to `throw new RuntimeException("...")` (AutoSealPdfTask × 4, CtrContractScheduleTask × 2, SettlementTask × 1). `AbstractQuartzJob.execute` catches Exception and writes `sys_job_log.status=FAIL` + `exceptionInfo` (substring 2000 chars).
- T-06-02-04 (EoP — bean name mislabeling): **mitigated** — every handler class has explicit `@Component("<xxlJobValue>")` with the bean name taken from the source `@XxlJob` value (no reliance on Spring default class-name-lowercased convention). All 20 active bean names 1:1 with planned `sys_job.invoke_target` short names.
- T-06-02-02 (Info Disclosure — write-task polluting dev DB): **mitigated** — this plan is code-only translation; no handler is executed. Dry-run grading deferred to 06-06 (D-P6-05).
- T-06-02-SC (Tampering — source code integrity): **mitigated** — read directly from local `/Users/alan/WorkSpace/IDEA/zgbas` checkout; no network fetch.

## Known Issues (pre-existing — out of scope per deviation Rule scope boundary)

| Issue | Files | Pre-existing? | Why deferred |
|-------|-------|---------------|--------------|
| Logger field points to wrong class (`LoggerFactory.getLogger(BudgetSettlementTask.class)` instead of the enclosing class) | ApplyPayTask / DcsxRepaymentdTask / DefaultingEnterpriseTask | Yes — source had this typo | Logs still work; logger name is misleading but no functional impact. Not introduced by this plan's changes. |
| `e.printStackTrace()` instead of slf4j in `InternalBuyTask.internalBuyTask` catch block | InternalBuyTask | Yes — source had this pattern | Replaced with `log.error("internalBuyTask error", e)` for consistency with the translation (this was a borderline case — `e.printStackTrace()` was the catch-block body that would have been paired with the deleted `XxlJobHelper.handleSuccess()` line). |

## Self-Check: PASSED

### File existence (21 handlers)
- zgbas-quartz/src/main/java/com/spt/quartz/task/ApplyPayTask.java — FOUND
- zgbas-quartz/src/main/java/com/spt/quartz/task/AutoSealPdfTask.java — FOUND
- zgbas-quartz/src/main/java/com/spt/quartz/task/BsCompanyCreditTask.java — FOUND
- zgbas-quartz/src/main/java/com/spt/quartz/task/BsCompanyTask.java — FOUND
- zgbas-quartz/src/main/java/com/spt/quartz/task/BudgetSettlementTask.java — FOUND
- zgbas-quartz/src/main/java/com/spt/quartz/task/BusinessRestrictRelieveTask.java — FOUND
- zgbas-quartz/src/main/java/com/spt/quartz/task/ConfirmReceiptDcsxTask.java — FOUND
- zgbas-quartz/src/main/java/com/spt/quartz/task/CtrContractProfitTask.java — FOUND
- zgbas-quartz/src/main/java/com/spt/quartz/task/CtrContractScheduleTask.java — FOUND
- zgbas-quartz/src/main/java/com/spt/quartz/task/DcsxAutoApplyPayTask.java — FOUND
- zgbas-quartz/src/main/java/com/spt/quartz/task/DcsxRepaymentdTask.java — FOUND
- zgbas-quartz/src/main/java/com/spt/quartz/task/DefaultingEnterpriseTask.java — FOUND
- zgbas-quartz/src/main/java/com/spt/quartz/task/DepositPaymentTask.java — FOUND
- zgbas-quartz/src/main/java/com/spt/quartz/task/InternalBuyTask.java — FOUND
- zgbas-quartz/src/main/java/com/spt/quartz/task/PmApproveTask.java — FOUND
- zgbas-quartz/src/main/java/com/spt/quartz/task/RepaymentTask.java — FOUND
- zgbas-quartz/src/main/java/com/spt/quartz/task/SealBorrowTask.java — FOUND
- zgbas-quartz/src/main/java/com/spt/quartz/task/SettlementTask.java — FOUND
- zgbas-quartz/src/main/java/com/spt/quartz/task/SignTask.java — FOUND
- zgbas-quartz/src/main/java/com/spt/quartz/task/StockVirtualTask.java — FOUND
- zgbas-quartz/src/main/java/com/spt/quartz/task/WeChatWorkTask.java — FOUND

### Grep assertions
- xxl-job residue in `zgbas-quartz/src/main/java/com/spt/quartz/task/` (grep `@XxlJob\|XxlJobHelper\|com\.xxl\.job`): **0 hits** — PASS
- Active handlers in `task/` (excluding RyTask / BasCommandExecutor / ReportCommandExecutor / BasWebCommand / Synchronized*): **21** — PASS
- `@Component` explicit bean names (excluding RyTask pre-existing from 06-01): **20** (RepaymentTask intentionally has none — fully commented) — PASS
- `package com.spt.quartz.task` on every ported file: **21/21** — PASS

### Commits verified to exist
- 3f4ec7b (Task 1 — 21 handlers ported) — FOUND

### Compile gate
- `mvn -pl zgbas-quartz -am compile` (with JAVA_HOME=Corretto 1.8 + zg_settings.xml): **BUILD SUCCESS**, 5/5 modules SUCCESS, 0 ERROR, 0 deferred cascades.

### Plan success criteria status
- [x] QUARTZ-03 (21 basServer/task/ handlers migration subset) — code translation dimension closed. Remaining QUARTZ-03 work: 06-03 rocketmq/task ~8 handlers + 06-04 command executors (BasCommandExecutor / ReportCommandExecutor / BasWebCommand).
- [x] D-P6-09 (handler repackage to com.spt.quartz.task, first D-P2-07 deviation) — landed. Only handler self-package changed; @Autowired iface packages preserved.
- [x] D-P6-12 (handler classes do NOT add @DisallowConcurrentExecution) — landed. RuoYi controls blocking via sys_job.concurrent + QuartzJobExecution vs QuartzDisallowConcurrentExecution Job-class selection.
- [x] Compile gate: 0 ERROR. Consolidated reactor unit will be closed by 06-04 after BasCommandExecutor + ReportCommandExecutor + BasWebCommand land in com.spt.quartz.task.

## Deferred Items

| Item | Reason | Next Owner |
|------|--------|------------|
| BasCommandExecutor / ReportCommandExecutor / BasWebCommand port (~3 command executors + ~55 executeCommand sys_job rows) | Plan 06-04 scope — separate wave | 06-04 |
| rocketmq/task/ ~8 handlers + dependency map for Synchronized*Task | Plan 06-03 scope — separate wave | 06-03 |
| sys_job INSERT data (cron + invoke_target translation) | Plan 06-05 scope — depends on user-supplied xxl-job admin DB dump (D-P6-01 checkpoint:human-blocked) | 06-05 |
| Scheduler startup validation (D-P6-06 fail-fast) | Depends on sys_job data (06-05) + scheduler boot (06-06) | 06-06 |
| Dry-run sampling (D-P6-04/05) | Depends on sys_job data | 06-06 |
| Pre-existing logger field pointing to BudgetSettlementTask.class in 3 files | Out-of-scope pre-existing typo (Rule scope boundary) | Future tech-debt cleanup |

## TDD Gate Compliance

N/A — plan `type=execute` (not `tdd`). No `test(...)` / `feat(...)` gate sequence required. Compile gate is the verification mechanism and is GREEN per Task 2 verify output.
