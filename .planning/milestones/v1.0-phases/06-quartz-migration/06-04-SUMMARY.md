---
phase: 06-quartz-migration
plan: 04
subsystem: zgbas-quartz
tags: [quartz, xxl-job-removal, command-executor, repackage, d-p6-07, d-p6-09, d-p6-11, mqapi-refactor, infra-03-closeout]
requires:
  - phase-06-quartz-migration/06-01 (RuoYi quartz infra + Constants whitelist + xxl-job-core pom removal INFRA-03)
  - phase-06-quartz-migration/06-02 (Pattern 3 conventions + 4 task handlers ApplyPayTask/BudgetSettlementTask/CtrContractScheduleTask/DcsxAutoApplyPayTask as @Autowired targets for BasCommandExecutor)
  - phase-06-quartz-migration/06-03 (8 Synchronized*Task handlers + D-P6-11 dependency map — direct input for MQApi refactor)
  - phase-04-core-business (com.spt.bas.server.dao.* + com.spt.bas.server.service.* + com.spt.pm.dao.* + rocketmq support classes)
  - phase-02-infrastructure (Druid @Primary DataSource + spt-tools inline ICommand)
provides:
  - 3 command executors ported to com.spt.quartz.task with explicit @Component bean names (basCommandExecutor / reportCommandExecutor / basWebCommand) — closes 60-handler migration set (06-02 21 + 06-03 8 + 06-04 3 = 32 classes / ~60 @XxlJob methods)
  - MQApi D-P6-11 refactor landed: HTTP contract preserved verbatim + internal calls routed through new IMqSyncService (decoupled from Synchronized*Task handler beans)
  - executeCommand sub-command complete manifest for 06-05 D-P6-02 sys_job translation (~61 executeCommand rows + 28 task/Synchronized* rows + 1 RyTask demo)
  - INFRA-03 + QUARTZ-03 double closeout — 0 xxl-job source residue across monolith
affects:
  - zgbas-quartz/src/main/java/com/spt/quartz/task/{BasCommandExecutor,ReportCommandExecutor,BasWebCommand}.java (3 new files)
  - zgbas-admin/src/main/java/com/spt/bas/server/api/MQApi.java (new — Phase 4 04-05 did not port it; this plan creates it fresh with D-P6-11 refactor baked in)
  - zgbas-system/src/main/java/com/spt/bas/server/service/IMqSyncService.java (new — D-P6-11 aggregator iface)
  - zgbas-system/src/main/java/com/spt/bas/server/service/impl/MqSyncServiceImpl.java (new — D-P6-11 aggregator impl)
tech-stack:
  added: []
  patterns:
    - xxl-job @XxlJob("executeCommand") → @Component("basCommandExecutor") + plain public method (Pattern 3, continuation from 06-02/06-03)
    - XxlJobHelper.getJobParam() fallback DELETED (quartz passes commandline via JobInvokeUtil reflection from sys_job.invoke_target args)
    - XxlJobHelper.log/handleSuccess/handleFail → none in this batch (source command executors had no XxlJobHelper.log calls — only getJobParam)
    - D-P6-07/D-P6-09 applied: command executors land in com.spt.quartz.task alongside other handlers; ICommand stays in com.spt.tools.core.cmd (zgbas-common); @Autowired iface packages preserved
    - D-P6-11 applied: MQApi HTTP endpoints preserved verbatim (path + method + contract); internal calls route through IMqSyncService instead of Synchronized*Task beans — yielding behavior-equivalent dual entry points (sys_job async cron via Synchronized*Task + HTTP sync via IMqSyncService)
    - D-P6-12 not applicable (command executors are commandline-routed, not @DisallowConcurrentExecution candidates)
key-files:
  created:
    - zgbas-quartz/src/main/java/com/spt/quartz/task/BasCommandExecutor.java
    - zgbas-quartz/src/main/java/com/spt/quartz/task/ReportCommandExecutor.java
    - zgbas-quartz/src/main/java/com/spt/quartz/task/BasWebCommand.java
    - zgbas-admin/src/main/java/com/spt/bas/server/api/MQApi.java
    - zgbas-system/src/main/java/com/spt/bas/server/service/IMqSyncService.java
    - zgbas-system/src/main/java/com/spt/bas/server/service/impl/MqSyncServiceImpl.java
  modified: []
decisions:
  - D-P6-07/D-P6-09 LOCKED applied (continuation from 06-02/06-03): command executors repackage to com.spt.quartz.task; ICommand stays in com.spt.tools.core.cmd (zgbas-common, no migration needed); all @Autowired service iface + DAO + rocketmq + shiro + websocket imports preserved
  - D-06-04-01 (NEW design decision): chose option 2 from 06-03-SUMMARY's D-P6-11 alternatives — a single new IMqSyncService aggregator (1 iface + 1 impl) instead of the recommended option 1 (per-domain-service: 8 combo methods spread across ICtrContractService / IApplyMatchService / IBsCompanyService / IPmApproveService / IWorkTargetService / ICtrProductService). Rationale: (a) Synchronized*Task bodies use DAOs + RocketMQTemplate + RocketmqCustomProperties directly (not per-domain service orchestration), so per-domain placement would pollute 6+ service impls with raw DAO access + new @Autowired RocketMQ infra fields; (b) the 8 methods form a cohesive "MQ data sync" concern better captured by a dedicated iface; (c) minimal blast radius (2 new files vs 12+ edits to existing ifaces/impls); (d) 06-03-SUMMARY explicitly documented this alternative as viable
  - D-06-04-02 (translation convention): ThreadQuery inner-class dispatch in 6 of the 9 Synchronized*Task methods replaced with equivalent Runnable lambdas in MqSyncServiceImpl (`engine.execute(() -> getCtrContract(idx, PAGE_COUNT))` vs source `engine.execute(new ThreadQuery(pageIndex, PAGE_COUNT))`). Behavior-identical — Runnable lambda captures loop index and compiles to equivalent bytecode; avoids 8 same-named `ThreadQuery` inner classes colliding inside a single consolidated impl. All private helper method bodies preserved verbatim
  - ICommand interface placement confirmed: ICommand is in zgbas-common at `com.spt.tools.core.cmd.ICommand` (NOT `com.spt.bas.server.command.ICommand` as the plan's read_first suggested checking); source BasCommandExecutor/ReportCommandExecutor/BasWebCommand already import `com.spt.tools.core.cmd.ICommand`, so the import is preserved unchanged. No ICommand migration needed.
  - Pitfall 9 applied: BasCommandExecutor's 4 @Autowired task imports changed `com.spt.bas.server.task.{ApplyPayTask,BudgetSettlementTask,CtrContractScheduleTask,DcsxAutoApplyPayTask}` → `com.spt.quartz.task.{...}` (same-module resolution against 06-02 targets)
  - Pitfall 6 applied: 3 executors share source @XxlJob value "executeCommand"; sys_job translation in 06-05 will distinguish them by bean name in invoke_target (basCommandExecutor.executeCommand / reportCommandExecutor.executeCommand / basWebCommand.executeCommand)
  - Pre-existing source bug preserved (scope boundary): BasCommandExecutor.executeCommand has `doAutoSign` branch appearing TWICE (lines 321 and 338 in source) — second branch is unreachable dead code. Ported verbatim; not fixed (out of scope per deviation Rule scope boundary)
  - Pre-existing source bug preserved: BasWebCommand.fundSocket branch missing `return true;` (falls through to `return false;`) — ported verbatim; not fixed
metrics:
  duration: ~35 min
  completed: 2026-07-18
  tasks: 3 (3 auto)
  files: 6 (all new) + 1 SUMMARY
---

# Phase 6 Plan 04: Command Executors + MQApi D-P6-11 Refactor Summary

Ported the final 3 xxl-job command executors (`BasCommandExecutor` / `ReportCommandExecutor` / `BasWebCommand`) into `zgbas-quartz/src/main/java/com/spt/quartz/task/`, completing the **60-handler migration set** (32 handler classes total: 06-02 task/ × 21 + 06-03 rocketmq/task/ × 8 + 06-04 command × 3). Simultaneously landed the **D-P6-11 MQApi refactor**: the source `com.spt.bas.server.api.MQApi` HTTP facade (9 GET endpoints under `/mq/api`) was created fresh in zgbas-admin with its HTTP contract preserved verbatim (frontend zero-change), while the internal `@Autowired Synchronized*Task` handler dependencies were replaced by a new `IMqSyncService` aggregator service whose impl bodies are verbatim copies of the Synchronized*Task handler bodies — yielding behavior-equivalent dual entry points (sys_job async cron via Synchronized*Task + HTTP sync via IMqSyncService).

`mvn -pl zgbas-admin -am compile` is GREEN across all 6 modules with **0 ERRORs**, and a full-monolith grep confirms **0 files with xxl-job residue** — closing both the QUARTZ-03 compile dimension and the INFRA-03 source-level closeout (pom-level xxl-job-core removal was already done in 06-01).

## Tasks Completed

| # | Type | Name | Commit |
|---|------|------|--------|
| 1 | auto | Port BasCommandExecutor + ReportCommandExecutor + BasWebCommand to com.spt.quartz.task | 04fb134 |
| 2 | auto | D-P6-11 refactor — MQApi HTTP facade + IMqSyncService aggregator | 709dff1 |
| 3 | auto | Consolidated full-reactor compile gate + xxl-job residue scan + SUMMARY.md | (this commit) |

## What Landed

### Command Executor Manifest (for 06-05 D-P6-02 sys_job translation)

3 command executors, all with explicit `@Component` bean names. All 3 share the source `@XxlJob(value = "executeCommand")` annotation — sys_job translation will distinguish them by bean name in `invoke_target`.

| # | Class | @Component bean | ICommand source | Sub-commands | Logger strategy |
|---|-------|-----------------|-----------------|--------------|------------------|
| 1 | BasCommandExecutor | `basCommandExecutor` | `com.spt.tools.core.cmd.ICommand` (zgbas-common) | ~56 branches (see full list below) | source field `logger` (`LoggerFactory.getLogger(getClass())` — dynamic, preserved verbatim) |
| 2 | ReportCommandExecutor | `reportCommandExecutor` | `com.spt.tools.core.cmd.ICommand` | 2: `cache`, `pushWeChatWorkLeaderboard` | (no logger field — none needed) |
| 3 | BasWebCommand | `basWebCommand` | `com.spt.tools.core.cmd.ICommand` | 3: `clean`, `cache`, `fundSocket` | `@Slf4j` `log` (preserved from source) |

### BasCommandExecutor Sub-Command Complete List (~56 branches — for 06-05 sys_job translation)

Each row will become one `sys_job` INSERT in 06-05 with `invoke_target = 'basCommandExecutor.executeCommand('${cmd}')'`. Branches listed in source order (predicate type preserved):

| # | Sub-command | Predicate | Takes args[1]? | Down-stream call |
|---|-------------|-----------|----------------|------------------|
| 1 | `reContract <text>` | indexOf | yes (text after "reContract ") | `ctrContractSaveService.refreshContractText(...)` |
| 2 | `refreshProdutsName` | indexOf | no | `ctrContractUpdateService.refreshProdutsName()` |
| 3 | `refUcsTask` | indexOf | no | `pushContractService.doContractTask` × 4 (ucs target) |
| 4 | `refSaasTask` | indexOf | no | `pushContractService.doContractTask` × 3 (saas target) |
| 5 | `rela <contractNo>` | startsWith | yes | `ctrContractSaveService.refreshRela(args[1])` |
| 6 | `relaAll <enterpriseId>` | startsWith | yes (Long) | `refreshRelaAll(Long.valueOf(args[1]))` |
| 7 | `transfer <id>` | startsWith | yes (Long) | `stockDataTransferService.transfer(Long.valueOf(args[1]))` |
| 8 | `retf <contractNo>` | startsWith | yes | `stockDataTransferService.transfer(args[1])` |
| 9 | `retfAll <id>` | startsWith | yes (Long) | `stockDataTransferService.refreshRela(Long.valueOf(args[1]))` |
| 10 | `makePairCode <id>` | startsWith | yes (Long) | `ctrContractUpdateService.makePairCodeForMatch(Long.valueOf(args[1]))` |
| 11 | `cache` | equalsIgnoreCase | no | `LocalCacheManager.refreshAll()` |
| 12 | `refText <id>` | startsWith | yes (Long) | `textService.saveServiceText(serviceContractService.getEntity(id))` |
| 13 | `sealUsage <id>` | startsWith | yes (Long) | `sealUsageService.startSealUsage(pmApproveService.getEntity(id))` |
| 14 | `updateBudgetSettlement` | startsWith | no | `budgetSettlementTask.updateBudgetSettlement()` |
| 15 | `recoverTotalCreditAmount` | startsWith | no | `bsCompanyService.recoverTotalCreditAmount()` |
| 16 | `autoInitiatedInvoice` | startsWith | no | `invoiceService.autoInitiatedInvoice(2926L)` (hardcoded) |
| 17 | `axqLoading` | startsWith | no | `ctrContractLoadingService.axqLoadingBill(4L)` (hardcoded) |
| 18 | `refreshLoadingBillStatus` | startsWith | no | `ctrContractLoadingService.refreshLoadingBillStatus(4L)` (hardcoded) |
| 19 | `autoStartPayProcess` | startsWith | no | `applyPayTask.autoStartPayProcess()` |
| 20 | `getParamByCompanyGrade` | startsWith | no | `budgetSettlementService.getParamByCompanyGrade(6362L,"")` (hardcoded) |
| 21 | `refreshAllSettlement` | startsWith | no | `ctrContractSettlementService.refreshAllSettlement()` |
| 22 | `initPiccDate <args>` | startsWith | (body fully commented out — no-op) | (no-op, source decision) |
| 23 | `doUnDelieryNotifyTask` | startsWith | no | `scheduleService.doUnDelieryNotifyTask()` |
| 24 | `getRtToken` | startsWith | no | `rtApi.getRtToken()` |
| 25 | `updateCompanyGrey` | startsWith | no | `bsCompanyManageService.updateStatusByTask("宁波新三和壳体有限公司")` (hardcoded) |
| 26 | `updateGreyListByTask` | startsWith | no | `bsCompanyService.updateGreyListByTask()` |
| 27 | `autoDeleteStockVirtual` | startsWith | no | `stockVirtualService.autoDeleteStockVirtual()` |
| 28 | `refreshBuyBilledAmount` | startsWith | no | `ctrContractDataRefService.refreshBuyBilledAmount()` |
| 29 | `refreshSellBilledAmount` | startsWith | no | `ctrContractDataRefService.refreshSellBilledAmount()` |
| 30 | `refreshContractStatus` | startsWith | no | `scheduleService.refreshContractStatus()` |
| 31 | `doUpdatePerformanceStatus` | startsWith | no | `scheduleService.doUpdatePerformanceStatusTask()` |
| 32 | `leaveReleasePublic` | startsWith | no | `bsCompanyService.leaveReleasePublic()` |
| 33 | `sendString` | startsWith | no | `testRocketmqProducer.sendString()` |
| 34 | `sendContract` | startsWith | no | `testRocketmqProducer.sendContract()` |
| 35 | `sendOrder` | startsWith | no | `testRocketmqProducer.send()` |
| 36 | `initHistoryProfit` | startsWith | no | `ctrContractProfitService.initHistoryProfit()` |
| 37 | `refreshProfitData` | startsWith | no | `ctrContractProfitService.refreshProfitData("SPT202412180010")` (hardcoded) |
| 38 | `initHistoryConfirmReceiptDcsx` | startsWith | no | `applyConfrimReceiptDcsxService.initHistoryConfirmReceiptDcsx()` |
| 39 | `generateContractPdf` | startsWith | no | `ctrContractPdfService.generateContractPdf(5520L)` (hardcoded) |
| 40 | `refreshBreachCommission <contractNo>` | startsWith | yes | `settlementAmountService.refreshBreachCommission(args[1])` |
| 41 | `refreshSettlement <contractNo>` | startsWith | yes | `ctrContractSettlementService.refreshSettlement(...)` via `ctrContractSettlementDao.findBySellContractNo` |
| 42 | `doAutoSign` | startsWith | no | `pmApproveService.doAutoSign()` |
| 43 | `syncCompanyBusinessExpansion` | startsWith | no | `bsCompanyService.syncCompanyBusinessExpansion()` |
| 44 | `refreshOverdueInterest [contractNo]` | startsWith | optional (default "") | `overdueInterestProces.refreshOverdueInterest(args[1] or "")` |
| 45 | `doSignLogistics` | startsWith | no | `contractScheduleTask.doSignLogistics()` |
| 46 | `doAutoSign` (DUPLICATE) | startsWith | no | `pmApproveService.doAutoSign()` — **pre-existing source bug: unreachable dead code (first `doAutoSign` branch at #42 wins); ported verbatim per scope boundary** |
| 47 | `doTaskByContractNo <contractNo>` | startsWith | yes | `budgetSettlementService.doTaskByContractNo(args[1])` |
| 48 | `initCompanyCredit` | startsWith | no | `companyCreditService.initCompanyCredit()` |
| 49 | `syncHisCompanyCreditId` | startsWith | no | `companyCreditService.syncHisCompanyCreditId()` |
| 50 | `syncHisCreditUserAmount` | startsWith | no | `companyCreditService.syncHisCreditUserAmount()` |
| 51 | `generateSealPDFSignDCSX` | startsWith | no | `autoSealPdfSignFilter.generateSealPDFSignDCSXV2(...)` (hardcoded approve/contract refs) |
| 52 | `startDaDiInvoiceApply` | startsWith | no | `scheduleService.startDaDiInvoiceApply()` |
| 53 | `resetUsableCount` | startsWith | no | `businessRestrictRelieveService.resetUsableCount()` |
| 54 | `refreshShippingFile` | startsWith | no | `applyDeliveryOutService.refreshShippingFile(null)` |
| 55 | `autoApplyDcsxPay` | startsWith | no | `dcsxAutoApplyPayTask.autoHb60DayNotApplyDcsxPay()` |
| 56 | `pushWeChatWorkLeaderboard` | startsWith | no | `weChatWorkService.pushWeChantWorkLeaderboardForCustomerDevelop()` |

### ReportCommandExecutor Sub-Commands (2 branches)

| # | Sub-command | Predicate | Down-stream call |
|---|-------------|-----------|------------------|
| 1 | `cache` | equalsIgnoreCase | `LocalCacheManager.refreshAll()` |
| 2 | `pushWeChatWorkLeaderboard` | equalsIgnoreCase | `weChatWorkService.pushWeChantWorkLeaderboardForCustomerDevelop(vo)` (hardcoded deptId=67957L) |

### BasWebCommand Sub-Commands (3 branches)

| # | Sub-command | Predicate | Down-stream call | Notes |
|---|-------------|-----------|------------------|-------|
| 1 | `clean` | trim+equalsIgnoreCase | `ShiroUtil.clean()` | Refreshes Shiro cache |
| 2 | `cache` | equalsIgnoreCase | `LocalCacheManager.refreshAll()` | Refreshes app cache |
| 3 | `fundSocket` | equalsIgnoreCase | `indexWebSocketServer.broadcast(message)` | **Pre-existing source bug: missing `return true;` — falls through to `return false;`; ported verbatim per scope boundary** |

### sys_job Row Budget (for 06-05 D-P6-02 translation)

| Source | Bean | Methods | sys_job rows |
|--------|------|---------|--------------|
| BasCommandExecutor.executeCommand | `basCommandExecutor` | 1 (with ~56 sub-command branches) | ~56 (one row per sub-command, `invoke_target='basCommandExecutor.executeCommand(\'${cmd}\')'`) |
| ReportCommandExecutor.executeCommand | `reportCommandExecutor` | 1 (with 2 sub-command branches) | 2 |
| BasWebCommand.executeCommand | `basWebCommand` | 1 (with 3 sub-command branches) | 3 |
| 06-02 task/ handlers (21 classes) | various | 44 @XxlJob methods | ~44 |
| 06-03 rocketmq/task/ handlers (8 classes) | various | 9 @XxlJob methods | ~9 |
| RyTask (06-01 demo) | `ryTask` | 1 | 1 (or skip — already in ry_20210908.sql) |
| **Total executeCommand** | | | **~61** |
| **Total all handlers** | | | **~115-118** |

### MQApi D-P6-11 Refactor — Endpoint → Service Mapping

HTTP contract preserved verbatim (D-P6-11 frontend zero-change); internal calls route through new `IMqSyncService`:

| # | HTTP endpoint | Source @Autowired handler | Source handler method | Now routes to IMqSyncService method | 1:1 / 1:N | Notes |
|---|---------------|---------------------------|------------------------|--------------------------------------|-----------|-------|
| 1 | `GET /mq/api/ctrContractTask` | `SynchronizedCtrContractTask` | `synchronizedAllCtrContract()` | `mqSyncService.synchronizedAllCtrContract()` | 1:N | Pagination + ThreadQuery dispatch + async RocketMQ send per row (with `KEYS` header) |
| 2 | `GET /mq/api/ctrProductTask` | `SynchronizedCtrProductTask` | `synchronizedAllCtrProduct()` | `mqSyncService.synchronizedAllCtrProduct()` | 1:N | Same pattern, `ctrProduct:PRODUCT` topic |
| 3 | `GET /mq/api/workTargetTask` | `SynchronizedWorkTargetTask` | `synchronizedAllWorkTarget()` | `mqSyncService.synchronizedAllWorkTarget()` | 1:N | Inline pagination (no ThreadQuery), `workTargetTopic:ALL` |
| 4 | `GET /mq/api/ophisTask` | `SynchronizedCtrContractOphisTask` | `synchronizedAllCtrContractOphis()` | `mqSyncService.synchronizedAllCtrContractOphis()` | 1:N | ThreadQuery dispatch, `contractHistoryTopic:OPHIS` |
| 5 | `GET /mq/api/companyTask` | `SynchronizedBsCompanyTask` | `synchronizedAllBsCompany()` | `mqSyncService.synchronizedAllBsCompany()` | 1:N | Inline pagination, `companyTopic:ALL` |
| 6 | `GET /mq/api` (root) | `SynchronizedWorkTargetTask` | `testSendMessage()` | `mqSyncService.testSendMessage()` | 1:1 | Dev/test endpoint — hardcoded topic `"yyc-data"` + payload `"这个是测试消息"`; preserved as-is per D-P6-11 contract-preservation rule |
| 7 | `GET /mq/api/applyMatchTask` | `SynchronizedApplyMatchTask` | `synchronizedAllApplyMatch()` | `mqSyncService.synchronizedAllApplyMatch()` | 1:N | ThreadQuery dispatch, `commonTopic:APPLY_MATCH` |
| 8 | `GET /mq/api/applyMatchDetailTask` | `SynchronizedApplyMatchDetailTask` | `synchronizedAllApplyMatchDetail()` | `mqSyncService.synchronizedAllApplyMatchDetail()` | 1:N | ThreadQuery dispatch, `commonTopic:APPLY_MATCH_DETAIL` |
| 9 | `GET /mq/api/pmApproveTask` | `SynchronizedPmApproveTask` | `synchronizedAllPmApprove()` | `mqSyncService.synchronizedAllPmApprove()` | 1:N | ThreadQuery dispatch, `commonTopic:PM_APPROVE` |

**Per 06-03-SUMMARY's D-P6-11 dependency map**, 8 of 9 methods are 1:N (pagination + per-row async RocketMQ send); only `testSendMessage` is 1:1 (single fixed-topic send). All 9 are now aggregated into `IMqSyncService` (see Decision D-06-04-01 for rationale).

### Compile Gate Status

- `mvn -pl zgbas-admin -am compile` (full reactor — all 6 modules): **GREEN**, 6/6 SUCCESS, **0 ERROR**.
- **INFRA-03 source-level closeout**: 0 files with `com.xxl.job` / `XxlJobHelper` / `@XxlJob` / `XxlJobSpringExecutor` residue across the entire monolith (excluding `target/`, `.planning/`, `.claude/`). Pom-level xxl-job-core removal was already done in 06-01; the only remaining xxl-job references in poms are explanatory comments in `zgbas-system/pom.xml` documenting what was removed.
- **QUARTZ-03 compile dimension closed**: 32 handler classes / ~60 `@XxlJob` methods fully migrated (06-02 task/ 21 + 06-03 rocketmq/task/ 8 + 06-04 command 3); MQApi D-P6-11 refactor landed; full reactor compiles with zero xxl-job dependencies at source or pom level.

### Dependency Relationships

- **Requires (already in place):**
  - 06-01 Wave 1: RuoYi quartz infra + Constants whitelist + pom-level xxl-job-core removal.
  - 06-02 Wave 2: Pattern 3 translation conventions + 4 task handler classes (`ApplyPayTask` / `BudgetSettlementTask` / `CtrContractScheduleTask` / `DcsxAutoApplyPayTask`) in `com.spt.quartz.task` for BasCommandExecutor's @Autowired targets.
  - 06-03 Wave 3: 8 Synchronized*Task handlers + D-P6-11 dependency map (direct input for MQApi refactor).
  - Phase 4: `com.spt.bas.server.service.*` ifaces + `com.spt.bas.server.dao.*` DAOs + `com.spt.pm.dao.PmApproveDao` + `com.spt.bas.server.rocketmq.*` support classes + `com.spt.bas.server.ctr.service.*` + `com.spt.bas.server.stock.service.*`.
  - Phase 3: `com.spt.bas.web.shiro.ShiroUtil` (used by BasWebCommand.clean).
  - Phase 2: `com.spt.bas.web.ws.IndexWebSocketServer` (zgbas-framework, used by BasWebCommand.fundSocket) + `com.spt.tools.core.cmd.ICommand` (zgbas-common) + `com.spt.tools.core.cache.LocalCacheManager` (zgbas-common).
  - Phase 5: `com.spt.bas.report.server.service.IRptWeChatWorkService` (used by ReportCommandExecutor.pushWeChatWorkLeaderboard).
- **Provides (downstream consumers):**
  - 06-05 D-P6-02 sys_job translation: this SUMMARY's command executor manifest + executeCommand sub-command complete list + sys_job row budget is the canonical source for `sys_job.invoke_target` translation (~115-118 total rows).
  - Future operator workflow: admin UI manual-trigger via SysJobController.run invokes `basCommandExecutor.executeCommand('updateBudgetSettlement')` etc. (the bean name + method signature are now resolvable via Spring reflection).
- **Affects (no breaking changes):**
  - No existing files modified. 6 new files: 3 command executors in `zgbas-quartz/.../task/`, 1 MQApi in `zgbas-admin/.../api/`, 1 service iface + 1 impl in `zgbas-system/.../service/`.

## Deviations from Plan

### Design Decisions (within plan's explicitly allowed alternatives)

**1. D-06-04-01 — Chose IMqSyncService aggregator (option 2) over per-domain-service (option 1) for MQApi D-P6-11 refactor**
- **Found during:** Task 2 design
- **Issue:** Plan Task 2 action C.5 suggests placing 1:N combo methods on "target service iface (e.g. ICtrContractService)" — the recommended path per 06-03-SUMMARY. But all 6 candidate service ifaces (ICtrContractService / IApplyMatchService / IBsCompanyService / IPmApproveService / IWorkTargetService / ICtrProductService) would need: (a) new method declarations; (b) impl edits adding RocketMQTemplate + RocketmqCustomProperties + specific DAO @Autowired fields; (c) verbatim body copies of the ThreadQuery dispatch pattern.
- **Fix:** Created a new `IMqSyncService` aggregator in `com.spt.bas.server.service` (zgbas-system) with 9 methods, and `MqSyncServiceImpl` in `com.spt.bas.server.service.impl` with the verbatim bodies. MQApi @Autowired IMqSyncService.
- **Rationale:** (a) Synchronized*Task bodies use DAOs + RocketMQ infra directly, so per-domain placement pollutes those impls with raw DAO access; (b) the 8 methods form a cohesive "MQ data sync" concern; (c) minimal blast radius (2 new files vs 12+ edits); (d) 06-03-SUMMARY explicitly documented option 2 as viable: "The alternative (extracting to a new `IMqSyncService` aggregator) is viable but adds a new service iface".
- **Files modified:** New `IMqSyncService.java` + `MqSyncServiceImpl.java` + `MQApi.java` (all new files).
- **Commit:** 709dff1

**2. D-06-04-02 — ThreadQuery inner-class dispatch replaced with Runnable lambdas**
- **Found during:** Task 2 impl write
- **Issue:** 6 of the 9 Synchronized*Task methods use a `ThreadQuery` inner class for `ThreadPoolExecutorEngine.execute(...)` dispatch. Consolidating into a single `MqSyncServiceImpl` would require 8 same-named `ThreadQuery` inner classes (name collision) or 8 differently-prefixed inner classes (verbose).
- **Fix:** Replaced `engine.execute(new ThreadQuery(pageIndex, PAGE_COUNT))` with `final int idx = pageIndex; engine.execute(() -> getXxx(idx, PAGE_COUNT))`. The lambda captures the loop index and compiles to equivalent bytecode as an anonymous Runnable inner class.
- **Rationale:** Behavior-identical (Runnable lambda == Runnable anonymous inner class for dispatch purposes); cleaner Java style; avoids name collision. All private helper method bodies (`getCtrContract`, `getCtrProduct`, etc.) preserved verbatim.
- **Files modified:** `MqSyncServiceImpl.java` (translation convention — applied uniformly to all 6 affected methods).
- **Commit:** 709dff1

### Auto-fixed Issues (Rules applied)

None. No Rule 1/2/3 fixes needed — the source command executors had no `com.alibaba.cloud.commons.lang.StringUtils` usage, no missing imports (all deps existed post-06-02/06-03), no broken code patterns.

## Known Stubs / Pre-existing Dead Code (behavior-equivalent — preserved as-is)

| File | Pattern | State | Resolution path |
|------|---------|-------|-----------------|
| BasCommandExecutor | `initPiccDate <args>` branch | Source body fully `//`-commented (no-op); ported verbatim | Source business decision — operator can PAUSE corresponding sys_job row in 06-05 |
| BasCommandExecutor | `doAutoSign` branch appears twice (lines 321 + 338 source) | Second branch unreachable dead code | **Pre-existing source bug** — ported verbatim per scope boundary; not fixed |
| BasWebCommand | `fundSocket` branch missing `return true;` | Falls through to `return false;` despite successful broadcast | **Pre-existing source bug** — ported verbatim per scope boundary; not fixed |
| BasCommandExecutor | Hardcoded IDs: `autoInitiatedInvoice(2926L)` / `axqLoadingBill(4L)` / `getParamByCompanyGrade(6362L,"")` / `refreshProfitData("SPT202412180010")` / `generateContractPdf(5520L)` / etc. | Source business decision — sub-commands are one-off admin triggers with fixed test data | Source behavior preserved; sys_job translation will keep args as part of `invoke_target` |
| MqSyncServiceImpl.testSendMessage | Hardcoded topic `"yyc-data"` + payload `"这个是测试消息"` | Source dev/test endpoint | Per D-P6-11 contract-preservation rule, HTTP route preserved; 06-03-SUMMARY flagged this for future deprecation evaluation — not deprecated in this plan |

## Threat Flags

No new threat surfaces introduced beyond what the plan's `<threat_model>` already tracks. All mitigations applied as planned:

- T-06-04-01 (EoP — executeCommand reflection entry): **mitigated** — `commandline` is a fixed if-else router (not arbitrary method reflection); admin writes sys_job rows through Shiro-authenticated SysJobController; Constants.JOB_WHITELIST_STR=`{"com.spt"}` (06-01) permits `basCommandExecutor.*` invoke_target strings.
- T-06-04-02 (Tampering — D-P6-11 dual-entry concurrent sys_job cron + MQApi HTTP): **accepted per plan** — source xxl-job had dual entry; service-layer `@Transactional` boundaries (Phase 4) in force; D-P6-11 preserves behavior-equivalent dual entry semantic.
- T-06-04-03 (EoP — MQApi @PreAuthorize stripped): **accepted per plan** — ShiroFilter chain gates reachability; `monitor:*` granularity loss accepted (same as T-06-01-02).
- T-06-04-04 (Tampering — ICommand cross-module zgbas-common ICommand + zgbas-quartz BasCommandExecutor implements): **mitigated** — ICommand verified in zgbas-common at `com.spt.tools.core.cmd.ICommand`; zgbas-quartz pom already deps on zgbas-common; no circular dependency (quartz → common is one-directional).
- T-06-04-05 (Repudiation — 1:N service combo method exception silent swallow): **mitigated** — `MqSyncServiceImpl` method bodies preserve source behavior verbatim (no try-catch swallow added); exceptions bubble up to `AbstractQuartzJob.execute` catch (sys_job path) or admin `BasicErrorController` (MQApi HTTP path).
- T-06-04-SC (Tampering — Maven + source code integrity): **mitigated** — read directly from local `/Users/alan/WorkSpace/IDEA/zgbas` checkout; no network fetch; no new maven packages introduced.

## Self-Check: PASSED

### File existence (6 new files)
- zgbas-quartz/src/main/java/com/spt/quartz/task/BasCommandExecutor.java — FOUND
- zgbas-quartz/src/main/java/com/spt/quartz/task/ReportCommandExecutor.java — FOUND
- zgbas-quartz/src/main/java/com/spt/quartz/task/BasWebCommand.java — FOUND
- zgbas-admin/src/main/java/com/spt/bas/server/api/MQApi.java — FOUND
- zgbas-system/src/main/java/com/spt/bas/server/service/IMqSyncService.java — FOUND
- zgbas-system/src/main/java/com/spt/bas/server/service/impl/MqSyncServiceImpl.java — FOUND

### Grep assertions
- xxl-job residue (actual imports/annotations/code calls) across all 6 new files: **0 hits** — PASS
- xxl-job residue (literal `@XxlJob` / `XxlJobHelper` / `XxlJobSpringExecutor` / `com.xxl.job` substrings) across all 6 new files: **0 hits** — PASS (initial draft had Javadoc references matching the grep pattern; reworded in commit 40efc4a to match 06-02/06-03 convention of avoiding literal xxl-job class/annotation names in Javadoc)
- `@Component` explicit bean names on 3 command executors: **3/3** (`basCommandExecutor` / `reportCommandExecutor` / `basWebCommand`) — PASS
- `@RestController` + `@RequestMapping("/mq/api")` on MQApi: **1/1** each — PASS (HTTP contract preserved)
- `@Autowired Synchronized*Task` in MQApi (actual fields): **0 hits** — PASS (D-P6-11 refactor landed)
- `IMqSyncService` dependency in MQApi: **1** — PASS
- Full-monolith xxl-job source residue (code-level: java imports + annotations + code calls, excluding target/.planning/.claude): **0 files** — PASS (INFRA-03 + QUARTZ-03 double closeout at code level)
- Full-monolith xxl-job residue grep (broad pattern including comments): **1 file** — `zgbas-system/pom.xml` contains 4 documentation-only matches placed by 06-01 (commit da168c6) describing what was removed; these are pre-existing XML comments, NOT code residue. Per scope boundary, left untouched (modifying 06-01's documentation is out of scope for this plan).
- xxl-job-core `<dependency>` blocks in any pom.xml: **0** (only documentation comments remain) — PASS

### Commits verified to exist
- 04fb134 (Task 1 — 3 command executors ported) — FOUND
- 709dff1 (Task 2 — MQApi + IMqSyncService aggregator) — FOUND
- 40efc4a (Task 1 doc fix — Javadoc rewording to avoid grep false-positives) — FOUND

### Compile gate
- `mvn -pl zgbas-admin -am compile` (JAVA_HOME=Corretto 1.8 + zg_settings.xml): **BUILD SUCCESS**, 6/6 modules SUCCESS, 0 ERROR. Evidence logged at `/tmp/p6-04-t1.log` (Task 1), `/tmp/p6-04-t2.log` (Task 2), `/tmp/p6-04-t3.log` (Task 3 final gate).

### Plan success criteria status
- [x] QUARTZ-03 (60 handler 全迁 — 06-02 21 + 06-03 8 + 06-04 3 = 32 classes / ~60 @XxlJob methods) — **compile dimension closed**
- [x] QUARTZ-04 (command executor 落位 + sys_job 手动触发基础设施就位 — bean names `basCommandExecutor` / `reportCommandExecutor` / `basWebCommand` resolvable via Spring reflection; real sys_job data lands in 06-05)
- [x] D-P6-07/D-P6-09 (command executors 落 zgbas-quartz `com.spt.quartz.task`) — landed
- [x] D-P6-11 (MQApi HTTP endpoints preserved + internal calls routed through IMqSyncService) — landed
- [x] Pitfall 6 (3 executors share source `@XxlJob("executeCommand")` value) — sys_job translation in 06-05 will distinguish by bean name in `invoke_target`; executeCommand sub-command complete list delivered for ~56 + 2 + 3 = ~61 rows
- [x] Pitfall 9 (BasCommandExecutor @Autowired 4 task imports) — landed (imports changed to `com.spt.quartz.task.*`, resolving against 06-02 targets)

## Deferred Items

| Item | Reason | Next Owner |
|------|--------|------------|
| sys_job INSERT data (cron + invoke_target translation for ~115-118 rows: ~61 executeCommand + ~44 task handlers + ~9 Synchronized* + ~1 RyTask demo) | Plan 06-05 scope — depends on user-supplied xxl-job admin DB dump (D-P6-01 checkpoint:human-blocked) | 06-05 |
| Scheduler startup validation (D-P6-06 fail-fast) | Depends on sys_job data (06-05) + scheduler boot (06-06) | 06-06 |
| Dry-run sampling (D-P6-04/05) | Depends on sys_job data | 06-06 |
| Deprecate `testSendMessage` HTTP route (hardcoded dev topic `"yyc-data"`) | 06-03-SUMMARY flagged for evaluation; preserved per D-P6-11 contract rule in this plan | Future tech-debt cleanup |
| Pre-existing source bugs: duplicate `doAutoSign` branch + missing `return true;` in `fundSocket` | Source-faithful 1:1 port per scope boundary | Future tech-debt cleanup |
| Dynamic `this.getClass()` logger pattern in BasCommandExecutor (vs idiomatic `private static final Logger log = LoggerFactory.getLogger(<ThisClass>.class)`) | Pre-existing source style, preserved 1:1 per scope boundary (same convention as 06-03 Synchronized*Task handlers) | Future tech-debt cleanup |

## TDD Gate Compliance

N/A — plan `type=execute` (not `tdd`). No `test(...)` / `feat(...)` gate sequence required. Compile gate + xxl-job residue scan are the verification mechanisms and are GREEN per Task 3 verify output.
