# Phase 6 Deferred Items

Items discovered during Phase 6 execution that are out of scope for the current plan but warrant follow-up.

## From 06-06 (final verification)

### InProcessContractTest pre-existing Phase 2 defect — FIXED INLINE per Rule 3

- **Discovered during:** 06-06 Task 2 full-reactor verify
- **Defect:** `com.spt.proof.InProcessContractTest` uses plain `@SpringBootTest` (no `webEnvironment`), which uses a mock servlet context with no embedded Tomcat. `com.spt.bas.web.ws.WebSocketConfig`'s `ServerEndpointExporter` bean fails `afterPropertiesSet` assertion (`javax.websocket.server.ServerContainer not available`).
- **Pre-existence verified:** Stashed all 06-06 changes; re-ran the test against unchanged sources — identical failure. NOT caused by Phase 6.
- **Phases masked:** Phase 3-5 verifications used `-Dtest=ZgbasApplicationTest` filtering, masking this broken sibling test for 4 phases.
- **Inline fix applied (Rule 3 — blocking Task 2 "全 reactor green" requirement):** Added `webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT` to `@SpringBootTest`. 1-line annotation change. Mirrors `ZgbasApplicationTest`'s working pattern.
- **File:** `zgbas-admin/src/test/java/com/spt/proof/InProcessContractTest.java`
- **Commit:** Task 2 commit (06-06 docs/summary)
- **Why Rule 3 not Rule 4:** Trivial 1-line annotation change; no architectural modification; mirrors existing working pattern.

### Database setup discrepancy — operational, no code change

- **Discovered during:** 06-06 Task 1 first test run
- **Issue:** Orchestrator's `project_critical_context` claimed dev DB had QRTZ_* + sys_job tables applied ("USER HAS APPLIED (confirmed)"). Direct JDBC query showed zero such tables.
- **Resolution:** Applied 3 in-repo SQL files (`quartz.sql`, `sys_job.sql`, `sys_job_data.sql`) to dev DB directly. sptbaspduser has GRANT ALL PRIVILEGES ON *.*.
- **Code change:** None. The SQL files were already in the repo (06-01 created `quartz.sql` + `sys_job.sql`; 06-05 created `sys_job_data.sql`).
- **Follow-up:** Verify with user whether the orchestrator's "USER HAS APPLIED" was based on a different DB / incorrect assumption / stale state.

## From 06-05 (xxl_job_info → sys_job translation)

### 21 active production handlers NOT migrated

- **Source:** 06-05 SUMMARY §"Deferred Items" (ids 5, 15, 16, 17, 18, 19, 20, 31, 34, 35, 40, 56, 57, 62, 66, 70, 71, 72-82, 84)
- **Reason:** No matching @XxlJob in 06-02/03/04 manifests. Includes the 11-row GuTu/工商 batch (job_group=14).
- **Next owner:** Separate Phase 6 gap-closure plan OR v2 tech-debt

### 7 ambiguous executeCommand entries

- **Source:** 06-05 SUMMARY §"Deferred Items" (ids 22, 23, 24, 26, 28, 29, 30)
- **Reason:** Missing `xxl_job_group → executor_appname` mapping in export. All had trigger_status=0 AND trigger_last/next_time=0 (never triggered in prod).
- **Next owner:** User provides mapping OR confirms skip — gap-closure plan

### 15 REVIEW-flagged sys_job rows

- **Source:** 06-05 SUMMARY §"Deferred Items" (ids 37/55/59/63/64/65/86/89/42 etc.)
- **Reason:** Empty args passed to typed-param methods, parameter-name-as-value placeholders, source method bodies commented-out.
- **Next owner:** Operator review — keep / modify args / skip

## From 06-04 (command executors + MQApi refactor)

### Pre-existing source bugs preserved verbatim (scope boundary)

- **BasCommandExecutor.executeCommand duplicate `doAutoSign` branch:** Source lines 321 + 338; second branch unreachable dead code. Ported 1:1.
- **BasWebCommand.fundSocket missing `return true;`:** Falls through to `return false;` despite successful broadcast. Ported 1:1.
- **Dynamic `this.getClass()` logger pattern** in BasCommandExecutor + 06-03 Synchronized*Task handlers: Non-idiomatic vs `private static final Logger log = LoggerFactory.getLogger(<ThisClass>.class)`. Preserved 1:1.
- **MqSyncServiceImpl.testSendMessage hardcoded topic `"yyc-data"` + payload `"这个是测试消息"`:** Dev/test endpoint. 06-03-SUMMARY flagged for future deprecation evaluation.
- **Next owner:** Future tech-debt cleanup

## From 06-02 (basServer/task handler migration)

### Logger field points to wrong class in 3 files

- **Files:** ApplyPayTask / DcsxRepaymentdTask / DefaultingEnterpriseTask (all `LoggerFactory.getLogger(BudgetSettlementTask.class)` instead of enclosing class)
- **Reason:** Pre-existing source typo (cosmetic; logs still work, logger name misleading).
- **Next owner:** Future tech-debt cleanup

## Operational (post-Phase 6)

### xxl-job admin 服务退役

- **Reason:** Source zgbas ran xxl-job admin as a separate service. With xxl-job removed from zgbas-plus (INFRA-03 closed), the admin service has no clients to manage.
- **Next owner:** Operations team — schedule decommission after Phase 7 ALIGN-01/02 signs off on quartz parity

### External spt-auth sys_menu INSERT for /monitor/job

- **Reason:** 06-01 Task 5 prepared the INSERT SQL (`06-01-MENU-INSERT.sql`) but the actual spt-auth DB is external — checkpoint:human-blocked.
- **Next owner:** Operations team — apply INSERT to external spt-auth sys_menu table so the /monitor/job link appears in the admin UI
