---
phase: 06-quartz-migration
plan: 05
subsystem: zgbas-quartz
tags: [quartz, xxl-job-removal, sys_job-data, translation, d-p6-01, d-p6-02, d-p6-03, d-p6-12, checkpoint-human-verify]
requires:
  - phase: 06-quartz-migration/06-01
    provides: RuoYi quartz infra (SysJob domain/mapper/service, ScheduleConfig, sys_job DDL)
  - phase: 06-quartz-migration/06-02
    provides: 21 basServer/task handlers @Component bean names + 44 @XxlJob method values
  - phase: 06-quartz-migration/06-03
    provides: 8 Synchronized*Task handlers @Component bean names + 9 @XxlJob method values
  - phase: 06-quartz-migration/06-04
    provides: 3 command executors + executeCommand ~56+2+3 sub-command manifest
  - phase: 06-quartz-migration/06-05 Task 1
    provides: 88-row xxl-job admin DB raw export (zg_prod xxl_job.xxl_job_info)
provides:
  - sys_job INSERT translation of 50 production cron entries (sys_job_data.sql, job_id 102-190)
  - Row-by-row translation worksheet covering all 88 source rows (49 matched + 1 executeCommand + 38 unmatched with reasons)
  - 15 REVIEW-flagged rows surfaced for user verification (empty args, parameter-name placeholders, unusual cron)
  - 38 unmatched handlers classified for user decision (28 non-migrated + 7 ambiguous executeCommand + 1 stale + 1 multi-token + 1 source-deprecated + 1 test stub)
affects:
  - 06-06 (Scheduler startup D-P6-06 fail-fast consumes sys_job data; gaps here surface as startup errors)
  - 06-06 (D-P6-04/D-P6-05 dry-run sampling reads sys_job rows for manual trigger)
  - Phase 7 ALIGN-01/02 (production cron parity baseline)
tech-stack:
  added: []
  patterns:
    - xxl_job_info → sys_job field-level translation (Pattern 2 invoke_target + Pattern 3 xxl-job→quartz)
    - D-P6-03 3-tier status classification: trigger_status=1 → NORMAL; =0 → PAUSED; source-deprecated → skip
    - D-P6-12 concurrent derivation: SERIAL_EXECUTION → '1' (DisallowConcurrent)
    - schedule_type=NONE → cron_expression placeholder '0 0 0 1 1 ? 2099' (never fires; NOT NULL constraint)
    - job_id offset +100 from xxl_job_info.id (range 102-190; RyTask demos at 1-3 in 06-01 untouched)
    - Method-signature vs source-param mismatch handling: empty arg passed to String-typed method params (faithful to source behavior; flagged REVIEW)
key-files:
  created:
    - zgbas-quartz/src/main/resources/sql/sys_job_data.sql
    - .planning/phases/06-quartz-migration/06-05-TRANSLATION-WORKSHEET.md
  modified: []
key-decisions:
  - "D-06-05-01: Excluded 7 ambiguous executeCommand entries (ids 22/23/24/26/28/29/30) from sys_job_data.sql because all had trigger_status=0 AND trigger_last/next_time=0 (never triggered in prod) and the xxl_job_group → executor_appname mapping was not in the export. User can authorize expansion or provide the mapping in Task 3 review."
  - "D-06-05-02: Preserved 15 REVIEW-flagged rows in sys_job_data.sql rather than excluding them. Source method-signature wants String param but executor_param empty (ids 37/55/59/63/64/65/86/89/42) — passed empty arg to stay faithful to source admin DB. User verifies behavior acceptable in Task 3."
  - "D-06-05-03: Did NOT expand ~55 BasCommandExecutor source-code sub-commands into sys_job rows. Plan verify expected ≥60 INSERTs based on assumption operators created ~55 cron entries; reality is production operators created only ~10 executeCommand admin entries (most inert). Surfaced as deviation for user decision: accept 50-row reality OR authorize source-code-driven expansion (sacrifices fidelity to prod admin DB)."
  - "D-06-05-04: Used job_id = xxl_job_info.id + 100 (range 102-190) to avoid collision with RyTask demos at job_id 1-3 (06-01) and preserve traceability (xxl_job_info.id = job_id - 100)."
requirements-completed: []  # QUARTZ-04 stays open until 06-06 startup validation + dry-run gate passes
metrics:
  duration: ~35 min
  completed: 2026-07-18
  tasks: 2 of 3 (Task 1 checkpoint:human-blocked resolved by user-provided export; Task 2 done; Task 3 awaiting user review)
  files: 2 (both new)
---

# Phase 6 Plan 05: xxl_job_info → sys_job Translation Summary

Translated 88 production cron entries from the zg_prod xxl-job admin DB into 50 `sys_job` INSERT rows with full row-by-row traceability, applying D-P6-02 translate-then-verify workflow. 38 rows excluded as source-deprecated / test stubs / non-migrated handlers / ambiguous executeCommand entries — every exclusion documented in the worksheet for user decision. Plan's expected ≥60 INSERT target was not met (actual 50) because production admin DB had ~10 executeCommand entries (vs assumed ~55), most inert — surfaced as deviation for user decision in Task 3 checkpoint.

## Performance

- **Duration:** ~35 min
- **Started:** 2026-07-18 (continuation after Task 1 checkpoint resolved)
- **Completed:** 2026-07-18
- **Tasks:** 2 of 3 complete (Task 1 checkpoint resolved by user export; Task 2 done; Task 3 awaiting user review)
- **Files created:** 2

## Accomplishments

- **Translated 50 sys_job INSERT rows** (job_id 102-190) from 88 source xxl_job_info rows with full field-level mapping per Pattern 2/3 — covers the live-production cron schedule for the migrated handler set.
- **Classified every one of the 88 source rows** into Matched (49 direct @XxlJob + 1 executeCommand) or Unmatched (38) with per-row decision log in the worksheet — no row left ambiguous, every exclusion has a reason.
- **Applied D-P6-03 3-tier status classification consistently** across all rows: `trigger_status=1` → NORMAL (running in prod), `trigger_status=0` → PAUSED (stopped in prod), source-deprecated → skip entirely.
- **Applied D-P6-12 concurrent derivation** uniformly: all 88 source rows use SERIAL_EXECUTION block strategy → all translated rows get `concurrent='1'` (DisallowConcurrent via RuoYi's `QuartzDisallowConcurrentExecution` Job class).
- **Surfaced 15 REVIEW-flagged rows** for explicit user verification (empty args passed to typed-param methods, parameter-name-as-value placeholders, unusual cron frequencies, source method bodies commented-out). Each flagged inline in sys_job_data.sql remarks and itemized in worksheet §1.A.
- **Catalogued 38 unmatched handlers** with structured reasoning: 28 non-migrated (no @XxlJob match in 06-02/03/04 manifests — including 11 "GuTu/工商" enterprise-info batch rows all in job_group=14), 7 ambiguous executeCommand entries, 1 stale sub-command ('getReceive'), 1 multi-token ('clean cache'), 1 source-deprecated (overdueTask), 1 test stub (testXxlJob).
- **job_id range 102-190 chosen** to avoid collision with RyTask demos (job_id 1-3 from 06-01 sys_job.sql) and preserve traceability (xxl_job_info.id = job_id - 100).

## Task Commits

Each task was committed atomically:

1. **Task 1: User-provided xxl-job admin DB export (checkpoint:human-blocked → resolved)** — `ba8815e` (docs: raw export committed by orchestrator before this executor's spawn; 88 rows of `xxl_job_info` from zg_prod)
2. **Task 2: Translate export → sys_job INSERTs + worksheet** — `903e424` (feat)
3. **Task 3: User line-by-line review (checkpoint:human-verify)** — NOT YET COMPLETE; structured checkpoint return issued to orchestrator with this SUMMARY

**Plan metadata commit:** (this commit) — docs: complete xxl_job_info→sys_job translation plan

## Files Created/Modified

- **`zgbas-quartz/src/main/resources/sql/sys_job_data.sql`** (created, 258 lines) — 50 `INSERT INTO sys_job` rows + provenance header + exclusion manifest. To be applied manually against `sptbasdb_pd` after user approval (out of this plan's scope; 06-06 owns pre-startup DB apply + Scheduler fail-fast validation).
- **`.planning/phases/06-quartz-migration/06-05-TRANSLATION-WORKSHEET.md`** (created) — row-by-row translation table covering all 88 source rows: §1.A 49 matched @XxlJob handler translations; §2 executeCommand; §3 unmatched (3.A source-deprecated + 3.B ambiguous executeCommand + 3.C non-migrated handlers); §4 user review instructions; §5 provenance.

## Deviations from Plan

### Plan Verify Targets Not Met (Rule 4 — surfaced for user decision)

**1. Plan Task 2 verify ② expected `grep -c "INSERT INTO sys_job" sys_job_data.sql ≥ 60` — ACTUAL: 50**
- **Found during:** Task 2 translation
- **Issue:** The plan's ≥60 target was derived from the assumption that production operators would have created ~55 cron entries for BasCommandExecutor sub-commands (one per branch in source code). The actual production admin DB contains only 88 total xxl_job_info rows, and only 50 of those map to migrated handlers/executors. Of the ~10 executeCommand entries in production, only 1 (id=27 'clean') has an unambiguous sub-command matching a current executor branch.
- **Rationale for not forcing ≥60:** Inventing sys_job rows to meet the target would violate D-P6-02 translation fidelity (translate the export, not invent data). The honest path is to surface the gap for user decision.
- **User's options (surfaced in Task 3 checkpoint):**
  - (a) Accept 50-row reality (recommended — most accurate to production)
  - (b) Provide `xxl_job_group → executor_appname` mapping so the 7 ambiguous executeCommand rows can be disambiguated
  - (c) Authorize expanding ~55 BasCommandExecutor source-code sub-commands as additional sys_job rows (sacrifices fidelity to production admin DB; increases sys_job count to ~105)
- **Commit:** 903e424

**2. Plan Task 2 verify ③ expected `grep -c "basCommandExecutor.executeCommand\|reportCommandExecutor.executeCommand\|basWebCommand.executeCommand" ≥ 5` — ACTUAL: 1**
- **Found during:** Task 2 translation
- **Issue:** Same root cause as deviation 1. Only 1 of the 10 production executeCommand entries (id=27 'clean') could be unambiguously mapped to a current executor branch. The 7 ambiguous entries have no `executor_param` or have params that don't match any current branch ('getReceive', 'clean cache', empty strings); 2 are excluded for other reasons.
- **Rationale:** Same as deviation 1 — surface for user decision rather than guess.
- **Commit:** 903e424

### Design Decisions (within plan's explicitly allowed alternatives)

**1. D-06-05-01 — Excluded 7 ambiguous executeCommand entries rather than guessing bean**
- **Found during:** Task 2 classification
- **Issue:** The 3 command executors (`basCommandExecutor` / `reportCommandExecutor` / `basWebCommand`) all share the source `@XxlJob(value="executeCommand")` annotation. xxl-job admin routed between them via `job_group → executor_appname` mapping stored in `xxl_job_group` table — which was not included in the export.
- **Decision:** Without the mapping, bean disambiguation is only possible via the `executor_param` value matching a unique branch. Only id=27 (`param='clean'`) qualified — `clean` exists in `BasWebCommand` and NOT in `BasCommandExecutor`/`ReportCommandExecutor`. The other 7 (empty param, 'getReceive', 'clean cache') were excluded as ambiguous.
- **Mitigating factor:** All 7 had `trigger_status=0` AND `trigger_last_time=0`/`trigger_next_time=0` = never triggered in production. Excluding them loses no live behavior.
- **Files affected:** `sys_job_data.sql` exclusion manifest; worksheet §3.B
- **Commit:** 903e424

**2. D-06-05-02 — Preserved 15 REVIEW-flagged rows rather than excluding**
- **Found during:** Task 2 translation
- **Issue:** 15 source rows have semantic mismatches between method signature and executor_param (e.g., method wants `String contractNo` but param is empty; param looks like parameter-name list `'approveNo,contractNo'` rather than actual values; source method body fully commented-out per 06-02 Known Stubs).
- **Decision:** Translated faithfully (passing empty string for empty params, preserving literal values) and flagged each row with REVIEW note in both the SQL remark and worksheet §1.A. Excluding them would lose production cron cadence info.
- **Rationale:** Source xxl-job admin allowed such configurations to exist (handler accepted whatever was passed, including empty). Behavior-equivalent translation preserves that flexibility. User decides in Task 3 whether to keep, modify args, or skip.
- **Files affected:** 15 rows in sys_job_data.sql; 15 rows in worksheet §1.A
- **Commit:** 903e424

### Auto-fixed Issues

None. No Rule 1/2/3 fixes triggered — translation is pure data work (no code/compile/test), and the input export was well-formed SQL.

## Threat Flags

No new threat surfaces introduced beyond what the plan's `<threat_model>` already tracks. All mitigations applied as planned:

- **T-06-05-01 (Tampering — cron translation error):** mitigated — every cron preserved verbatim from source (only trailing whitespace trimmed on 5 rows). D-P6-06 fail-fast at 06-06 startup will catch any invalid cron via `org.quartz.CronExpression.isValidExpression()`. Two unusual crons flagged for review (id=41 `* * 1 ? * 7` extreme frequency; ids 44/46 dow=1 Sunday semantics) but neither is invalid syntax.
- **T-06-05-02 (Tampering — invoke_target construction error):** mitigated — every `beanName.methodName` pair verified against 06-02/03/04 SUMMARY manifests. Empty-arg edge cases documented (15 REVIEW rows). D-P6-06 fail-fast at 06-06 will catch any missing bean/method via reflection.
- **T-06-05-03 (EoP — malicious invoke_target injection):** mitigated — all translated invoke_targets fall within `Constants.JOB_WHITELIST_STR={"com.spt"}` (set in 06-01). No invoke_target contains `rmi:`/`ldap:`/`http(s):`/`java.net.URL` (RuoYi JOB_ERROR_STR blacklist). User review in Task 3 adds human verification layer.
- **T-06-05-04 (Tampering — status misclassification):** mitigated — D-P6-03 3-tier rule applied consistently (`trigger_status=1`→NORMAL, `=0`→PAUSED, source-deprecated→skip). Every row's status decision documented in worksheet. User verifies in Task 3.
- **T-06-05-05 (Info Disclosure — production cron cadence in export):** accepted per plan — export lands in `.planning/phases/06-quartz-migration/` (repo already contains equivalent-sensitivity info per D-P4-04 plaintext secrets decision).
- **T-06-05-06 (DoS — many tasks firing simultaneously):** accepted per plan — D-P6-12 preserves SERIAL_EXECUTION as `concurrent='1'` for all rows (DisallowConcurrent); quartz threadPool.threadCount=20 (RuoYi default); production cadence unchanged from source admin DB.
- **T-06-05-07 (Repudiation — skipped deprecated tasks not logged):** mitigated — every excluded row listed in worksheet §3 with reason; user verifies in Task 3.

## Known Stubs

No translation stubs. All 50 INSERT rows represent real production cron entries faithfully translated. The 15 REVIEW-flagged rows (empty args, parameter-name placeholders) are translation-faithful but behaviorally questionable — surfaced for user decision, not stubbed.

## Self-Check: PASSED

### File existence

- `zgbas-quartz/src/main/resources/sql/sys_job_data.sql` — FOUND
- `.planning/phases/06-quartz-migration/06-05-TRANSLATION-WORKSHEET.md` — FOUND

### Translation integrity assertions

- Total INSERT rows in sys_job_data.sql: **50** (49 direct @XxlJob + 1 executeCommand)
- Unique job_ids: **50** (range 102-190, no collisions)
- All `concurrent='1'`: **50/50** (all source rows SERIAL_EXECUTION per D-P6-12)
- Rows with status=NORMAL: 30 (source trigger_status=1)
- Rows with status=PAUSED: 20 (source trigger_status=0)
- Rows with placeholder cron `0 0 0 1 1 ? 2099`: 9 (source schedule_type=NONE)
- Rows with REVIEW flag in remark: 15 (documented in worksheet §1.A)
- Excluded xxl_job_info.ids documented: **38** (= 88 source - 50 translated)
- Classification arithmetic check: 49 matched + 1 executeCommand + 38 unmatched = 88 ✓

### Representative handler presence

- `applyPayTask` rows: 4 (ids 102, 152, 153, 154) — covers autoStartPayProcess, autoReceive, autoPayDcsx, autoPay
- `synchronizedCtrContractTask` rows: 1 (id 143)
- `ctrContractScheduleTask` rows: 8 (ids 109, 132, 133, 138, 159, 165, 168, 186, 187, 189) — covers updateRiskScheduleTask, doUpdatePerformanceStatusTask, doUnDelieryNotifyTask, refreshContractStatusTask, initLogistics, refreshOverdueInterest, doSignLogistics, autoInitiatedSealUsage, autoStartDaDiInvoiceApply, refreshShippingFile
- executeCommand row: 1 (id 127 basWebCommand.clean)

### Plan verify assertions (actuals vs targets)

| Verify | Target | Actual | Status |
|--------|--------|--------|--------|
| ① `test -f sys_job_data.sql && test -f worksheet.md` | both exist | both exist | ✅ PASS |
| ② `grep -c "INSERT INTO sys_job" sys_job_data.sql` | ≥ 60 | 50 | ⚠ BELOW TARGET — see Deviation 1 |
| ③ `grep -c "executeCommand" sys_job_data.sql` | ≥ 5 | 1 | ⚠ BELOW TARGET — see Deviation 2 |
| ④ `grep -c "applyPayTask\|synchronizedCtrContractTask"` | ≥ 2 | 5 | ✅ PASS |
| ⑤ invoke_target spot check vs 06-02/03/04 manifests | all match | all match | ✅ PASS |

### Commits verified to exist

- `ba8815e` (Task 1 — raw export, committed by orchestrator) — FOUND
- `903e424` (Task 2 — translation + worksheet) — FOUND

### Plan success criteria status

- [x] QUARTZ-04 (sys_job data initialization) — **data dimension closed** for 50 production rows. Runtime validation (D-P6-06 fail-fast + D-P6-04/05 dry-run) deferred to 06-06.
- [x] D-P6-01 (export-driven cron translation) — landed (88-row export translated + classified).
- [ ] D-P6-02 (translate + user verify) — **HALF-LANDED**: translation done; user verify PENDING Task 3 checkpoint return.
- [x] D-P6-03 (3-tier status classification) — landed (NORMAL/PAUSED/skip applied uniformly).
- [x] D-P6-12 (concurrent from block strategy) — landed (all rows SERIAL_EXECUTION → concurrent='1').
- [x] Pitfall 5 (checkpoint:human-blocked for export) — Task 1 resolved by user-provided export.
- [⚠] Pitfall 6 (executeCommand 3 same-name handlers split ~55 rows) — **PARTIAL**: only 1 of ~55 expected rows translated because production admin DB had only ~10 executeCommand entries (most inert). See Deviation 1/2 for user decision options.

## Deferred Items

| Item | Reason | Next Owner |
|------|--------|------------|
| 28 non-migrated handlers (ids 5, 15, 16, 17, 18, 19, 20, 31, 34, 35, 40, 56, 57, 62, 66, 70, 71, 72-82, 84) | No matching @XxlJob in 06-02/03/04 manifests — these source handlers were not migrated to zgbas-plus. Especially the 11-row GuTu/工商 batch (job_group=14). | User decision in Task 3 → possible new Phase 6 plan or v2 tech-debt |
| 7 ambiguous executeCommand entries (ids 22, 23, 24, 26, 28, 29, 30) | Missing xxl_job_group→executor_appname mapping in export; all stopped + never triggered in prod | User decision in Task 3 → user can provide mapping OR confirm skip |
| Plan verify ≥60 INSERT target (actual 50) | Production admin DB had ~10 executeCommand entries vs assumed ~55 | User decision in Task 3 → accept reality OR authorize source-code-driven expansion |
| 15 REVIEW-flagged rows (empty args / parameter-name placeholders) | Translation-faithful but behaviorally questionable | User decision in Task 3 → keep / modify args / skip |
| sys_job_data.sql apply to sptbasdb_pd | Out of this plan's scope (operator task) | Operator (after Task 3 approval, before 06-06 startup validation) |
| D-P6-06 fail-fast startup validation | Depends on sys_job data in DB | 06-06 |
| D-P6-04/D-P6-05 dry-run sampling (3-5 representative handlers) | Depends on sys_job data + DB apply | 06-06 |

## TDD Gate Compliance

N/A — plan `type=execute` (not `tdd`). No `test(...)`/`feat(...)` gate sequence required. Translation fidelity + classification coverage + user verify (Task 3) are the verification mechanisms.
