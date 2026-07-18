# Phase 6 Plan 05: xxl_job_info → sys_job Translation Worksheet

**Created:** 2026-07-18
**Source:** `.planning/phases/06-quartz-migration/06-05-RAW-EXPORT.sql` (88 rows from `zg_prod.xxl_job.xxl_job_info`)
**Output:** `zgbas-quartz/src/main/resources/sql/sys_job_data.sql` (50 INSERT rows, job_id 102–190)
**Decisions:** D-P6-01 (export-driven), D-P6-02 (translate + human-verify), D-P6-03 (3-tier status), D-P6-12 (concurrent from block strategy)
**Status:** Awaiting user line-by-line review (Task 3 `checkpoint:human-verify`)

This worksheet is the **D-P6-02 translation artifact** — every row from the production xxl-job admin export is classified and the translation decision is documented. User reviews each row before SQL apply (06-06 owns startup validation).

---

## §0. Translation Statistics

| Classification | Count | Action |
|----------------|-------|--------|
| **Matched** (direct @XxlJob value match → migrated handler) | 49 | INSERTed into sys_job_data.sql |
| **executeCommand unambiguously matched** (param → known sub-command) | 1 | INSERTed (id=27 clean → basWebCommand) |
| **INSERT rows in sys_job_data.sql total** | **50** | |
| **Unmatched** (excluded — see §3) | 38 | Listed in §3 for user decision |

**Source executor_handler breakdown (88 total):**

| Handler source | Count | Notes |
|----------------|-------|-------|
| 06-02 basServer/task @XxlJob values | 38 matched + 1 deprecated (overdueTask) + 1 test stub (testXxlJob) + 28 non-migrated | See §1 + §3 |
| 06-03 rocketmq/task Synchronized* @XxlJob values | 11 matched | Synchronized*Task beans cover all 9 @XxlJob methods; id=41 and id=46 both call `synchronizedBsCompanyTask.synchronizedAllBsCompany` (different cron/name) |
| 06-04 executeCommand (3 executors) | 1 matched (clean) + 7 ambiguous | See §2 + §3.B |

---

## §1. Matched @XxlJob Handler Translations (49 rows)

Each row below maps 1:1 to an `INSERT INTO sys_job` in `sys_job_data.sql`. The `xxl_job_info.id` → `sys_job.job_id = id + 100` mapping preserves traceability.

**Translation rules applied (per `06-RESEARCH.md` §Pattern 2 + §Pattern 3):**

| xxl_job_info field | sys_job field | Rule |
|--------------------|---------------|------|
| `job_desc` | `job_name` | Verbatim (Chinese preserved) |
| (synthetic) | `job_group` | `'DEFAULT'` for direct @XxlJob handlers |
| `executor_handler` + `executor_param` | `invoke_target` | `beanName.methodName([args])` — bean name from 06-02/03/04 manifests; args typed per Pattern 2 |
| `schedule_conf` (CRON) | `cron_expression` | Verbatim, trailing whitespace trimmed |
| (schedule_type=NONE) | `cron_expression` | Placeholder `0 0 0 1 1 ? 2099` (never fires; field is NOT NULL) |
| `executor_block_strategy` | `concurrent` | All 88 rows = `SERIAL_EXECUTION` → `concurrent='1'` (DisallowConcurrent, D-P6-12) |
| `trigger_status` | `status` | `1` (running) → `'0'` NORMAL; `0` (stopped) → `'1'` PAUSED |
| (fixed) | `misfire_policy` | `'3'` (do nothing — RuoYi default) |
| (fixed) | `create_by` / `create_time` / `update_by` / `update_time` | `'admin'` / `sysdate()` / `''` / `null` |
| (fixed) | `remark` | `'迁自 xxl-job <executor_handler>'` + optional REVIEW notes |

**Ignored xxl_job_info fields** (per D-P6-12 — single-node monolith; RuoYi has no native equivalents):
- `executor_route_strategy` (FIRST in all rows — multi-node routing N/A for single node)
- `executor_timeout` (all rows = 0; RuoYi has no per-job timeout)
- `executor_fail_retry_count` (all rows = 0; RuoYi has no built-in retry — D-P6-12 out-of-scope)
- `misfire_strategy` (all rows = DO_NOTHING; RuoYi uses sys_job.misfire_policy instead)
- `alarm_email`, `glue_*`, `child_jobid`, `trigger_last/next_time` (all empty / N/A)

### §1.A — Matched rows in xxl_job_info.id order

| xxl_id | job_id | job_desc (truncated) | executor_handler | executor_param | schedule_type | cron (trimmed) | trigger | status | concurrent | invoke_target | Bean source | REVIEW notes |
|--------|--------|----------------------|------------------|----------------|---------------|----------------|---------|--------|------------|---------------|-------------|--------------|
| 2 | 102 | 自动发起付款 | `autoStartPayProcess` | (empty) | CRON | `0 5 9 ? * *` | 1 | NORMAL | 1 | `applyPayTask.autoStartPayProcess` | 06-02 ApplyPayTask | — |
| 3 | 103 | 更新私海客户没有成交单则划入公海 | `updateCompanyGrey` | (empty) | CRON | `0 40 23 * * ?` | 1 | NORMAL | 1 | `bsCompanyTask.updateCompanyGrey` | 06-02 BsCompanyTask | — |
| 4 | 104 | 开户人id 刷新历史数据 | `refreshOwnerOfAccountId` | (empty) | CRON | `0 30 23 * * ?` | 1 | NORMAL | 1 | `bsCompanyTask.refreshOwnerOfAccountId` | 06-02 BsCompanyTask | — |
| 6 | 106 | 凌晨零点30分更新结算单 | `updateBudgetSettlement` | (empty) | CRON | `0 30 0 * * ?` | 1 | NORMAL | 1 | `budgetSettlementTask.updateBudgetSettlement` | 06-02 BudgetSettlementTask | — |
| 7 | 107 | 更新vip剩余时长 | `updateVipRemainingTime` | (empty) | CRON | `0 0 0 * * ?` | 1 | NORMAL | 1 | `budgetSettlementTask.updateVipRemainingTime` | 06-02 BudgetSettlementTask | — |
| 8 | 108 | 白条到期日9点自动发起收款审批 | `applyReceive` | (empty) | CRON | `0 0 9 * * ?` | 0 | PAUSED | 1 | `budgetSettlementTask.applyReceive` | 06-02 BudgetSettlementTask | ⚠ Source method body commented-out (06-02 Known Stubs); call is no-op |
| 9 | 109 | 【疑似无效】每日凌晨3点更新风控待办事项 | `updateRiskScheduleTask` | (empty) | CRON | `0 0 3 ? * *` | 0 | PAUSED | 1 | `ctrContractScheduleTask.updateRiskScheduleTask` | 06-02 CtrContractScheduleTask | job_desc prefix 【疑似无效】 carried from source |
| 10 | 110 | 【疑似无效】违约企业重置赊销额度及准入 | `defaultingEnterpriseTask` | (empty) | CRON | `0 0 23 * * ?` | 0 | PAUSED | 1 | `defaultingEnterpriseTask.defaultingEnterpriseTask` | 06-02 DefaultingEnterpriseTask | — |
| 11 | 111 | 【疑似无效】当天内部采购的库存若未销售则原路退回 | `internalBuyTask` | (empty) | CRON | `0 0 23 ? * *` | 0 | PAUSED | 1 | `internalBuyTask.internalBuyTask` | 06-02 InternalBuyTask | — |
| 13 | 113 | 更新逾期印章外借状态 | `updateSealBorrow` | (empty) | CRON | `0 0 2 ? * *` | 1 | NORMAL | 1 | `sealBorrowTask.updateSealBorrow` | 06-02 SealBorrowTask | — |
| 14 | 114 | 更新销售结算表 | `updateSettlementTask` | (empty) | CRON | `0 0 3 ? * *` | 1 | NORMAL | 1 | `settlementTask.updateSettlementTask` | 06-02 SettlementTask | — |
| 21 | 121 | 超保额度到期自动恢复 | `recoverTotalCreditAmount` | (empty) | CRON | `0 0/5 * * * ?` | 1 | NORMAL | 1 | `bsCompanyTask.recoverTotalCreditAmount` | 06-02 BsCompanyTask | — |
| 32 | 132 | 每天更新合同履约状态 | `doUpdatePerformanceStatusTask` | (empty) | CRON | `0 0 7 * * ?` | 1 | NORMAL | 1 | `ctrContractScheduleTask.doUpdatePerformanceStatusTask` | 06-02 CtrContractScheduleTask | — |
| 33 | 133 | 发货预警通知任务 | `doUnDelieryNotifyTask` | (empty) | CRON | `0 0 8 ? * *` | 1 | NORMAL | 1 | `ctrContractScheduleTask.doUnDelieryNotifyTask` | 06-02 CtrContractScheduleTask | — |
| 36 | 136 | 定时清除24小时未被使用的库存 | `autoDeleteStockVirtual` | (empty) | CRON | `0 0 0 * * ?` | 0 | PAUSED | 1 | `stockVirtualTask.autoDeleteStockVirtual` | 06-02 StockVirtualTask | — |
| 37 | 137 | 根据合同编号更新计算违约金 | `updateBudgetSettlementByContractNo` | (empty) | NONE | `0 0 0 1 1 ? 2099` | 0 | PAUSED | 1 | `budgetSettlementTask.updateBudgetSettlementByContractNo('')` | 06-02 BudgetSettlementTask | ⚠ Method signature wants `String param`; empty arg passed (source param empty). Behavior: handler must tolerate empty |
| 38 | 138 | 每天更新合同状态 | `refreshContractStatusTask` | (empty) | CRON | `0 0 5 ? * *` | 1 | NORMAL | 1 | `ctrContractScheduleTask.refreshContractStatusTask` | 06-02 CtrContractScheduleTask | — |
| 39 | 139 | 离职员工名下客户自动释放至公海 | `leaveReleasePublic` | (empty) | CRON | `0 0 22 ? * *` | 1 | NORMAL | 1 | `bsCompanyTask.leaveReleasePublic` | 06-02 BsCompanyTask | — |
| 41 | 141 | 全量同步t_bs_company | `synchronizedAllBsCompany` | (empty) | CRON | `* * 1 ? * 7` | 0 | PAUSED | 1 | `synchronizedBsCompanyTask.synchronizedAllBsCompany` | 06-03 SynchronizedBsCompanyTask | ⚠ Cron `* * 1 ? * 7` = every-second at 1am Sat (extreme frequency); status=PAUSED so never fires. Verify intent |
| 42 | 142 | 刷新风控利润统计数据 | `refreshProfitData` | (empty) | CRON | `0 0 3 * * ?` | 1 | NORMAL | 1 | `ctrContractProfitTask.refreshProfitData('')` | 06-02 CtrContractProfitTask | ⚠ Method wants `String approveNo`; source param empty → empty arg passed. 06-04 BasCommandExecutor.refreshProfitData branch hardcodes `SPT202412180010` — confirm whether direct handler call should also use that value |
| 43 | 143 | zgbas全量同步数据中台t_ctr_contract | `synchronizedAllCtrContract` | (empty) | CRON | `0 0 1 * * ?` | 1 | NORMAL | 1 | `synchronizedCtrContractTask.synchronizedAllCtrContract` | 06-03 SynchronizedCtrContractTask | — |
| 44 | 144 | zgbase全量同步数据中台work_target | `synchronizedAllWorkTarget` | (empty) | CRON | `0 0 1 ? * 1` | 1 | NORMAL | 1 | `synchronizedWorkTargetTask.synchronizedAllWorkTarget` | 06-03 SynchronizedWorkTargetTask | ⚠ Quartz dow=1 means SUNDAY (per `org.quartz.CronExpression`); xxl-job uses same library so semantics unchanged. Confirm "Monday 1am" vs "Sunday 1am" intent |
| 45 | 145 | zgbas全量同步数据中台t_ctr_contract_ophis | `synchronizedAllCtrContractOphis` | (empty) | CRON | `0 0 2 * * ?` | 1 | NORMAL | 1 | `synchronizedCtrContractOphisTask.synchronizedAllCtrContractOphis` | 06-03 SynchronizedCtrContractOphisTask | — |
| 46 | 146 | zgbas全量同步数据中台t_bs_company | `synchronizedAllBsCompany` | (empty) | CRON | `0 0 1 ? * 1` | 1 | NORMAL | 1 | `synchronizedBsCompanyTask.synchronizedAllBsCompany` | 06-03 SynchronizedBsCompanyTask | ⚠ Same invoke_target as id=141; different cron + name — both kept. dow=1=Sunday review same as id=144 |
| 47 | 147 | 生成历史数据中游确认收货审批单 | `initHistoryConfirmReceiptDcsx` | (empty) | NONE | `0 0 0 1 1 ? 2099` | 0 | PAUSED | 1 | `confirmReceiptDcsxTask.initHistoryConfirmReceiptDcsx` | 06-02 ConfirmReceiptDcsxTask | — |
| 48 | 148 | zgbas全量同步数据中台t_ctr_product | `synchronizedAllCtrProduct` | (empty) | CRON | `0 10 1 * * ?` | 1 | NORMAL | 1 | `synchronizedCtrProductTask.synchronizedAllCtrProduct` | 06-03 SynchronizedCtrProductTask | — |
| 49 | 149 | zgbas全量同步数据中台t_pm_approve | `synchronizedAllPmApprove` | (empty) | CRON | `0 15 1 * * ?` | 1 | NORMAL | 1 | `synchronizedPmApproveTask.synchronizedAllPmApprove` | 06-03 SynchronizedPmApproveTask | — |
| 50 | 150 | zgbas全量同步数据中台t_apply_match | `synchronizedAllApplyMatch` | (empty) | CRON | `0 20 1 * * ?` | 1 | NORMAL | 1 | `synchronizedApplyMatchTask.synchronizedAllApplyMatch` | 06-03 SynchronizedApplyMatchTask | — |
| 51 | 151 | zgbas全量同步数据中台t_apply_match_detail | `synchronizedAllApplyMatchDetail` | (empty) | CRON | `0 25 1 * * ?` | 1 | NORMAL | 1 | `synchronizedApplyMatchDetailTask.synchronizedAllApplyMatchDetail` | 06-03 SynchronizedApplyMatchDetailTask | — |
| 52 | 152 | 发起代采赊销预算全款日期/定金日期是当天的收款申请 | `autoReceive` | (empty) | CRON | `0 35 1 * * ?` | 0 | PAUSED | 1 | `applyPayTask.autoReceive` | 06-02 ApplyPayTask | — |
| 53 | 153 | 发起代采赊销预算付款日期/定金日期是当天的付款申请 | `autoPayDcsx` | (empty) | CRON | `0 27 1 * * ?` | 0 | PAUSED | 1 | `applyPayTask.autoPayDcsx` | 06-02 ApplyPayTask | ⚠ Source method body commented-out (06-02 Known Stubs); call is no-op |
| 54 | 154 | 发起普通预算付款日期/定金日期是当天的付款申请 | `autoPay` | (empty) | CRON | `0 30 1 * * ?` | 0 | PAUSED | 1 | `applyPayTask.autoPay` | 06-02 ApplyPayTask | ⚠ Source method body commented-out (06-02 Known Stubs); call is no-op |
| 55 | 155 | 补偿结算单收违约金提成 | `refreshBreachCommission` | (empty) | NONE | `0 0 0 1 1 ? 2099` | 0 | PAUSED | 1 | `settlementTask.refreshBreachCommission('')` | 06-02 SettlementTask | ⚠ Method wants `String contractNo`; empty arg passed. 06-04 BasCommandExecutor.refreshBreachCommission branch takes args[1] — confirm whether direct handler call needs a contractNo |
| 58 | 158 | 定时自动审批 | `doAutoSign` | (empty) | CRON | `10 1/2 * * * ?` | 1 | NORMAL | 1 | `pmApproveTask.doAutoSign` | 06-02 PmApproveTask | — |
| 59 | 159 | 初始化物流单据 | `initLogistics` | (empty) | NONE | `0 0 0 1 1 ? 2099` | 0 | PAUSED | 1 | `ctrContractScheduleTask.initLogistics('')` | 06-02 CtrContractScheduleTask | ⚠ Method wants `String contractNo`; empty arg passed |
| 60 | 160 | 更新物流单据表历史数据 | `updateCtrLogistics` | (empty) | CRON | `0 20 1 * * ?` | 0 | PAUSED | 1 | `pmApproveTask.updateCtrLogistics` | 06-02 PmApproveTask | — |
| 61 | 161 | 企业业务扩展表数据同步 | `syncCompanyBusinessExpansion` | (empty) | CRON | `0 0 1 * * ?` | 1 | NORMAL | 1 | `bsCompanyTask.syncCompanyBusinessExpansion` | 06-02 BsCompanyTask | — |
| 63 | 163 | 代采赊销盖章申请，附件生成异常补偿任务 | `generateSealPDFSignDCSX` | `approveNo,contractNo` | NONE | `0 0 0 1 1 ? 2099` | 0 | PAUSED | 1 | `autoSealPdfTask.generateSealPDFSignDCSX('approveNo,contractNo')` | 06-02 AutoSealPdfTask | ⚠ executor_param looks like parameter NAME list, not actual values. Kept literal — user confirm whether to substitute real approveNo/contractNo |
| 64 | 164 | 代采赊销盖章审批完成后自动执行签署逻辑补偿任务 | `successSignContractByKeyword` | (empty) | NONE | `0 0 0 1 1 ? 2099` | 0 | PAUSED | 1 | `autoSealPdfTask.successSignContractByKeyword('')` | 06-02 AutoSealPdfTask | ⚠ Method wants `String param`; empty arg passed |
| 65 | 165 | 更新中游逾期利息 | `refreshOverdueInterest` | (empty) | CRON | `0 0 23 ? * *` | 1 | NORMAL | 1 | `ctrContractScheduleTask.refreshOverdueInterest('')` | 06-02 CtrContractScheduleTask | ⚠ Method wants `String contractNo`; empty arg passed. 06-04 BasCommandExecutor.refreshOverdueInterest defaults to `""` when args[1] missing — behavior-identical |
| 67 | 167 | 恢复企业授信额度为人保批复额度 | `recoverCompanyCreditAmount` | (empty) | CRON | `0 0 23 ? * *` | 0 | PAUSED | 1 | `bsCompanyTask.recoverCompanyCreditAmount` | 06-02 BsCompanyTask | — |
| 68 | 168 | 物流单据签署补偿任务 | `doSignLogistics` | (empty) | CRON | `0 0/5 * * * ?` | 1 | NORMAL | 1 | `ctrContractScheduleTask.doSignLogistics` | 06-02 CtrContractScheduleTask | — |
| 83 | 183 | 企业微信机器人推送业绩排名 | `pushWeChatWorkLeaderboard` | (empty) | CRON | `0 30 17 * * ?` | 1 | NORMAL | 1 | `weChatWorkTask.pushWeChatWorkLeaderboard` | 06-02 WeChatWorkTask | — |
| 85 | 185 | 临时额度到期自动恢复 | `recoverTemporaryAmount` | (empty) | CRON | `0 0 0 * * ?` | 1 | NORMAL | 1 | `bsCompanyCreditTask.recoverTemporaryAmount` | 06-02 BsCompanyCreditTask | — |
| 86 | 186 | 业务盖章异常生成补偿任务 | `autoInitiatedSealUsage` | (empty) | NONE | `0 0 0 1 1 ? 2099` | 0 | PAUSED | 1 | `ctrContractScheduleTask.autoInitiatedSealUsage('')` | 06-02 CtrContractScheduleTask | ⚠ Method wants `String approveNo`; empty arg passed |
| 87 | 187 | 发货45天后自动发起开票 | `autoStartDaDiInvoiceApply` | (empty) | CRON | `0 0 7 ? * *` | 1 | NORMAL | 1 | `ctrContractScheduleTask.autoStartDaDiInvoiceApply` | 06-02 CtrContractScheduleTask | — |
| 88 | 188 | 业务限制解除次数重置为0 | `resetUsableCount` | (empty) | CRON | `0 0 1 * * ?` | 1 | NORMAL | 1 | `businessRestrictRelieveTask.resetUsableCount` | 06-02 BusinessRestrictRelieveTask | — |
| 89 | 189 | 刷新发货文件 | `refreshShippingFile` | `contractNo` | CRON | `0 0 1 * * ?` | 0 | PAUSED | 1 | `ctrContractScheduleTask.refreshShippingFile('contractNo')` | 06-02 CtrContractScheduleTask | ⚠ executor_param='contractNo' looks like parameter NAME, not value. Kept literal — user confirm whether to substitute a real contractNo or leave as manual-trigger template |
| 90 | 190 | 鸿博业务60后自动发起中游付款申请 | `autoHb60DayNotApplyDcsxPay` | (empty) | CRON | `0 0 1 * * ?` | 1 | NORMAL | 1 | `dcsxAutoApplyPayTask.autoHb60DayNotApplyDcsxPay` | 06-02 DcsxAutoApplyPayTask | — |

**REVIEW-flagged rows (10):** 8, 37, 41, 42, 44, 46, 53, 54, 55, 59, 63, 64, 65, 86, 89 — see ⚠ column notes above. These are translated faithfully from source but have semantics the user should verify (empty args to methods that want values; parameter-name-as-value placeholders; commented-out source bodies; unusual cron frequencies).

### §1.B — Cron expression validation

All cron expressions in §1.A are 6-field format (sec min hour dom mon dow), compatible with both xxl-job (which uses quartz `CronExpression` internally) and RuoYi quartz. Per `06-RESEARCH.md` §Pattern 1, the D-P6-06 fail-fast gate at startup will validate every cron via `org.quartz.CronExpression.isValidExpression()` — any invalid cron aborts startup with a clear error message.

**Trim applied** (3 rows had trailing whitespace in source `schedule_conf`): ids 10, 85, 88, 89, 90.

**No cron rewrites** were necessary — all expressions pass syntax check. Flagged for human review (not validation failure):
- id=41 `* * 1 ? * 7`: every-second frequency (sec=*) — syntactically valid; status=PAUSED so never fires
- id=42 dow=1 (SUNDAY in quartz): same library semantics as xxl-job source, no change needed
- id=144, 146 dow=1: same as above

---

## §2. Matched executeCommand Translation (1 row)

The production xxl-job admin DB had 10 entries with `executor_handler='executeCommand'` (ids 22, 23, 24, 26, 27, 28, 29, 30 + ids 32/33 — wait, 32/33 are `doUpdatePerformanceStatusTask`/`doUnDelieryNotifyTask`, different handlers). Actual executeCommand rows: ids 22, 23, 24, 26, 27, 28, 29, 30 = 8 rows.

**Of these 8:** only id=27 (`executor_param='clean'`) maps unambiguously to a current executor branch (`BasWebCommand.clean` → `ShiroUtil.clean()` per 06-04 SUMMARY). The other 7 are listed in §3.B (Unmatched) with reasons.

| xxl_id | job_id | job_desc | executor_param | schedule | trigger | status | concurrent | invoke_target | Bean source | REVIEW notes |
|--------|--------|----------|----------------|----------|---------|--------|------------|---------------|-------------|--------------|
| 27 | 127 | executeCommand | `clean` | NONE | 0 | PAUSED | 1 | `basWebCommand.executeCommand('clean')` | 06-04 BasWebCommand | Bean disambiguation: 'clean' exists in `BasWebCommand` (→ ShiroUtil.clean) and NOT in `BasCommandExecutor` (no 'clean' branch) or `ReportCommandExecutor`. So bean = `basWebCommand` uniquely. |

---

## §3. Unmatched Handlers (38 rows — EXCLUDED from sys_job_data.sql)

These 38 rows from the production export are **not translated** into sys_job INSERTs. They are surfaced here for user decision (Task 3 `checkpoint:human-verify`). The user can:
- **Accept exclusion** — these are deprecated/test/non-migrated; production will lose no behavior.
- **Identify migration target** — for handlers that should have been migrated but weren't (06-02/03/04 may have missed some).
- **Authorize executeCommand expansion** — for ambiguous executeCommand rows, provide the `xxl_job_group → executor_appname` mapping (not in this export) so the correct bean can be picked.

### §3.A — Source-deprecated / test stubs (D-P6-03 ③ 跳过)

| xxl_id | job_desc | executor_handler | Reason |
|--------|----------|------------------|--------|
| 12 | 【疑似无效】合同添加逾期标示、逾期客户添加逾期记录 | `overdueTask` | 06-02 SUMMARY explicitly skips `OrverdurTask.java`: source `@XxlJob(value = "overdueTask")` is on a fully `//`-commented-out line. D-P6-03 ③ classifies as 废弃 (deprecated). **Action: skip migration.** |
| 69 | 测试任务 | `testXxlJob` | 06-02 SUMMARY explicitly skips `TestJob.java`: source is a xxl-job test stub, not a business handler. Not on the QUARTZ-03 migration path. **Action: skip migration.** |

### §3.B — Ambiguous executeCommand entries (7 rows)

These rows all have `executor_handler='executeCommand'` and `trigger_status=0` (stopped). All have `trigger_last_time=0` AND `trigger_next_time=0` — meaning **they were never scheduled/triggered in production**. They appear to be skeleton/template entries the operator created but never activated.

The 3 command executors (`basCommandExecutor` / `reportCommandExecutor` / `basWebCommand`) all share the source `@XxlJob(value="executeCommand")` annotation. xxl-job admin routed between them via `job_group` → executor `appname` mapping (stored in `xxl_job_group` table — **not included in this export**). Without that mapping, bean disambiguation is only possible via the `executor_param` value matching a unique branch in one executor's source.

| xxl_id | job_group | executor_param | Reason excluded |
|--------|-----------|----------------|-----------------|
| 22 | 7 | (empty) | Empty param + no xxl_job_group mapping → cannot determine bean. Stopped (never triggered). **User decision: skip (recommended — never ran) OR provide xxl_job_group→appname mapping.** |
| 23 | 8 | (empty) | Same as id=22 |
| 24 | 2 | (empty) | Same as id=22 |
| 26 | 4 | `getReceive` | `getReceive` is NOT a current branch in any of the 3 command executors (06-04 SUMMARY BasCommandExecutor 56 branches + ReportCommandExecutor 2 + BasWebCommand 3 — none match). Looks like a stale/deprecated sub-command. **User decision: skip OR confirm migration target.** |
| 28 | 10 | (empty) | Same as id=22 |
| 29 | 5 | (empty) | Same as id=22 |
| 30 | 11 | `clean cache` | Multi-token `clean cache` — no executor branch matches via `equalsIgnoreCase` (BasCommandExecutor.cache, BasWebCommand.cache all require exact `cache`; "clean cache" ≠ "cache"). `indexOf`/`startsWith` branches also fail. Functionally inert if invoked. **User decision: skip OR provide intended parsing.** |

### §3.C — Non-migrated handlers (28 rows)

These 28 `executor_handler` values do NOT match any @XxlJob value from 06-02 (21 task handlers / 44 methods), 06-03 (8 Synchronized*Task / 9 methods), or 06-04 (3 command executors / executeCommand + sub-commands) SUMMARY manifests. They were xxl-job handlers in the source zgbas microservices that **were not migrated to zgbas-plus** — either intentionally (out-of-scope) or by oversight.

**Recommended action:** User reviews each handler name against business needs. If any is still active in production and should be running, identify the source class and add migration (likely a new plan in Phase 6 or a tech-debt item).

| xxl_id | job_desc (truncated) | executor_handler | job_group | schedule | trigger | Notes |
|--------|----------------------|------------------|-----------|----------|---------|-------|
| 5 | 企业根据瑞可获取到的工商信息更新企业基本信息 | `updateCompanyBasicInfo` | 5 | CRON `0 0 1 * * ?` | 1 (running) | Active in prod. Likely lives in a source module not migrated — investigate. |
| 15 | 已完成未签署合同定时任务 | `doSuccessContract` | 2 | CRON `0 */2 * * * ?` | 1 (running) | Active in prod. Group 2 — different from main group 5. |
| 16 | 已完成未签署应收账款债权合同定时任务 | `doSuccessDebtCertificate` | 2 | CRON `0 */2 * * * ?` | 1 (running) | Active in prod. Group 2. |
| 17 | 刷新所有企业瑞克征信信息 | `loadInfoAll` | 3 | CRON `0 30 2 * * ?` | 0 (stopped) | Group 3. Stopped in prod — lower priority. |
| 18 | 计算所有企业得分 | `calcAll` | 4 | CRON `0 30 0 * * ?` | 0 (stopped) | Group 4. Stopped. |
| 19 | 每一小时执行 无效合同标识 | `verifyContract` | 6 | CRON `0 0 0/1 * * ?` | 1 (running) | Active in prod. Group 6. |
| 20 | 刷新企业对接微风企发票信息 | `loadWfqDataInfo` | 3 | CRON `0 0 1 * * ?` | 1 (running) | Active in prod. Group 3. |
| 31 | 已完成未签署确认收货单定时任务 | `doReceiveGood` | 2 | CRON `0 */2 * * * ?` | 1 (running) | Active in prod. Group 2. |
| 34 | 定时更新企业的人保批复额度 | `loadPiccAll` | 4 | CRON `0 30 2 * * ?` | 0 (stopped) | Group 4. Stopped. |
| 35 | 供应商评分计算定时任务 | `supplierCalcAll` | 4 | CRON `0 0 0 * * ?` | 0 (stopped) | Group 4. Stopped. |
| 40 | 初始化风控利润统计 | `initHistoryProfit11111` | 5 | NONE | 0 (stopped) | ⚠ Name suffix `11111` looks like test entry. The real `initHistoryProfit` IS migrated (06-02 CtrContractProfitTask) but no cron row for it in export. **User decision: skip (test entry) OR translate to `ctrContractProfitTask.initHistoryProfit` if real.** |
| 56 | 查询印章-参数[UserID] | `querySeal` | 6 | NONE | 0 (stopped) | Group 6. Stopped. |
| 57 | 删除印章-参数[UserID,SealD] | `deleteSeal` | 6 | NONE | 0 (stopped) | Group 6. Stopped. |
| 62 | 企业微信 > 本地表 > 权限表 | `syncWechatToAdmin` | 12 | CRON `0 0 0 */1 * ?` | 1 (running) | Active in prod. Group 12. Has param `zg_txl` (handled as literal in worksheet — not in any migrated bean). |
| 66 | addEvaHumanCostHandler | `addEvaHumanCostHandler` | 13 | CRON `0 0 1 1 * ?` | 1 (running) | Active in prod (annual Jan 1). Group 13. |
| 70 | 全量同步合同数据 | `syncAllContract` | 15 | CRON `0 0 2 * * ?` | 0 (stopped) | Group 15. Stopped. |
| 71 | 人保申报状态查询 | `piccQueryCreditStatus` | 5 | CRON `0 0 0/1 * * ? ` | 1 (running) | Active in prod (hourly). Group 5. |
| 72 | 获取企业基本信息任务开始 | `getGuTuCompanyBaseInfo` | 14 | CRON `0 0 23 1/2 * ? ` | 1 (running) | Active in prod (every 2 days). Group 14 — appears to be a "GuTu" (工商) external data integration batch (ids 72-82 all group 14, every-2-day cadence). |
| 73 | 获取企业开放股东信息 | `getCompanyHolderListCount` | 14 | CRON `0 30 23 1/2 * ? ` | 1 (running) | Group 14. |
| 74 | 获取企业公开股权出质信息 | `getCompanyEquityCount` | 14 | CRON `0 0 0 1/2 * ? ` | 1 (running) | Group 14. |
| 75 | 获取企业股权冻结信息 | `getCompanyEquityFreeze` | 14 | CRON `0 30 0 1/2 * ? ` | 1 (running) | Group 14. |
| 76 | 获取企业对外投资信息 | `getCompanyInvestment` | 14 | CRON `0 0 1 1/2 * ? ` | 1 (running) | Group 14. |
| 77 | 获取企业公开司法拍卖信息 | `getJudicialSale` | 14 | CRON `0 30 1 1/2 * ? ` | 1 (running) | Group 14. |
| 78 | 获取企业欠税信息 | `getOwnTaxCount` | 14 | CRON `0 0 2 1/2 * ? ` | 1 (running) | Group 14. |
| 79 | 获取企业公开环保处罚信息 | `getEnvironmentalPenaltiesCount` | 14 | CRON `0 30 2 1/2 * ? ` | 1 (running) | Group 14. |
| 80 | 获取企业公开经营异常信息 | `getCompanyAbnormalCount` | 14 | CRON `0 0 3 1/2 * ? ` | 1 (running) | Group 14. |
| 81 | 获取企业行政处罚信息 | `getMergePunishCount` | 14 | CRON `0 30 3 1/2 * ? ` | 1 (running) | Group 14. |
| 82 | 获取企业公开行政许可信息 | `getMergeLicenseCount` | 14 | CRON `0 0 4 1/2 * ? ` | 1 (running) | Group 14. |
| 84 | 全量同步合同结算数据 | `syncAllContractSettlement` | 15 | CRON `0 0 */1 * * ?` | 1 (running) | Group 15. Hourly. |

**Observation:** 11 of the 28 non-migrated handlers are the "GuTu/工商" enterprise-info batch (group 14, ids 72-82). These were likely implemented in a source module (possibly a separate zgbas microservice like `reportServer` or a dedicated GuTu integration service) that was not included in the zgbas-plus monolith migration. **Recommendation for user:** decide whether (a) accept exclusion (GuTu integration deferred to v2), (b) identify source class and add migration as a follow-up Phase 6 plan, or (c) treat as out-of-scope tech-debt.

---

## §4. User Review Instructions (Task 3 `checkpoint:human-verify`)

For each row in §1.A (49 matched) + §2 (1 executeCommand) + §3 (38 unmatched), mark one of:

- **approved** — translation accepted as-is
- **approved with note: <说明>** — accepted with caveat (e.g., confirm empty-arg behavior acceptable)
- **rejected: <理由>** — Claude revises translation and re-submits
- **deferred: <理由>** — defer decision (e.g., for §3.C GuTu batch — defer to v2)

**Particular attention requested on:**

1. **REVIEW-flagged rows in §1.A (15 rows):** empty args passed to methods that want String values; source method bodies commented-out (ids 8, 53, 54); parameter-name-as-value placeholders (ids 63, 89); unusual cron frequencies (id=41); quartz dow=1 semantics (ids 44, 46).
2. **§3.B ambiguous executeCommand (7 rows):** skip vs. provide xxl_job_group→appname mapping.
3. **§3.C non-migrated handlers (28 rows):** especially the 11 GuTu/工商 batch rows (ids 72-82, all group 14, all active in prod) — decide whether to migrate as a follow-up plan or defer to v2.
4. **§3.A source-deprecated rows (2):** confirm skip (already documented in 06-02 SUMMARY).
5. **Plan's ≥60 INSERT expectation:** actual is 50. See SUMMARY "Deviations from Plan" for breakdown. User confirms accept 50-row reality OR authorize expansion (e.g., expand ~55 BasCommandExecutor sub-commands as additional sys_job rows from 06-04 SUMMARY §BasCommandExecutor Sub-Command Complete List).

**After review:** communicate the consolidated result to Claude. If any rejected, Claude revises sys_job_data.sql + worksheet and re-submits. When all rows are approved/approved-with-note/deferred, plan proceeds to 06-06 (startup validation).

---

## §5. Provenance

- **Source export file:** `.planning/phases/06-quartz-migration/06-05-RAW-EXPORT.sql` (88 rows, zg_prod xxl-job admin DB, Navicat export 2026-07-18)
- **Translation artifacts:**
  - `zgbas-quartz/src/main/resources/sql/sys_job_data.sql` (50 INSERT rows)
  - `.planning/phases/06-quartz-migration/06-05-TRANSLATION-WORKSHEET.md` (this file)
- **Handler manifests used for bean disambiguation:**
  - `.planning/phases/06-quartz-migration/06-02-SUMMARY.md` (21 basServer/task handlers + 44 @XxlJob values)
  - `.planning/phases/06-quartz-migration/06-03-SUMMARY.md` (8 Synchronized*Task handlers + 9 @XxlJob values)
  - `.planning/phases/06-quartz-migration/06-04-SUMMARY.md` (3 command executors + 56+2+3 executeCommand sub-commands)
- **Translation rules:** `06-RESEARCH.md` §Pattern 2 (invoke_target encoding), §Pattern 3 (xxl-job→quartz translation table), §Code Examples §1/§2 (sample translations)
- **Decisions applied:** D-P6-01 (export-driven cron source), D-P6-02 (translate + human-verify), D-P6-03 (3-tier status: NORMAL/PAUSED/skip), D-P6-12 (concurrent from block strategy, SERIAL_EXECUTION→'1')
