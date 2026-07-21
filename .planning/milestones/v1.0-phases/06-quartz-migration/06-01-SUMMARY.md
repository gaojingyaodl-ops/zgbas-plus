---
phase: 06-quartz-migration
plan: 01
subsystem: zgbas-quartz
tags: [quartz, ruoyi, xxl-job-removal, scheduler, thymeleaf, monitor-ui]
requires:
  - phase-02-infrastructure (dual ORM @Primary DataSource, spt-tools inline)
  - phase-03-auth (Shiro chain for @RequiresPermissions)
  - phase-04-core-business (basServer service layer as handler @Autowired targets)
provides:
  - zgbas-quartz module populated (RuoYi auth-quartz 18 classes verbatim)
  - RuoYi Scheduler bean wired via @Primary Druid DataSource
  - /monitor/job Thymeleaf visualization console (D-P6-10)
  - sys_job / sys_job_log / 11× QRTZ_* DDL files (QUARTZ-02 file dimension)
  - external spt-auth sys_menu INSERT SQL (D-P6-10 menu wiring)
affects:
  - zgbas-quartz/pom.xml (quartz + spring-context-support deps)
  - zgbas-framework ZgbasMybatisConfig (@MapperScan +com.spt.quartz.mapper)
  - zgbas-system/pom.xml (xxl-job-core dep removed — INFRA-03)
  - zgbas-system CtrContractProfitServiceImpl (XxlJobHelper.log → log.info)
  - external spt-auth sys_menu table (operational INSERT, checkpoint)
tech-stack:
  added:
    - org.quartz-scheduler:quartz 2.3.2 (Spring Boot 2.5.9 BOM managed)
    - org.springframework:spring-context-support 5.3.13 (BOM managed)
  patterns:
    - RuoYi quartz Scheduler factory bean (LocalDataSourceJobStore + Druid)
    - Shiro @RequiresPermissions as compensation for stripped @PreAuthorize (W5)
    - PageHelper.startPage() for mybatis pagination in BaseController
    - Thymeleaf th:value auto-escape for XSS defense-in-depth on invoke_target
key-files:
  created:
    - zgbas-quartz/src/main/java/com/spt/quartz/config/QuartzScheduleConfig.java
    - zgbas-quartz/src/main/java/com/spt/quartz/controller/SysJobController.java
    - zgbas-quartz/src/main/java/com/spt/quartz/controller/SysJobLogController.java
    - zgbas-quartz/src/main/java/com/spt/quartz/domain/{SysJob,SysJobLog}.java
    - zgbas-quartz/src/main/java/com/spt/quartz/mapper/{SysJobMapper,SysJobLogMapper}.java
    - zgbas-quartz/src/main/java/com/spt/quartz/service/ISysJobService.java
    - zgbas-quartz/src/main/java/com/spt/quartz/service/ISysJobLogService.java
    - zgbas-quartz/src/main/java/com/spt/quartz/service/impl/SysJobServiceImpl.java
    - zgbas-quartz/src/main/java/com/spt/quartz/service/impl/SysJobLogServiceImpl.java
    - zgbas-quartz/src/main/java/com/spt/quartz/task/RyTask.java
    - zgbas-quartz/src/main/java/com/spt/quartz/util/{AbstractQuartzJob,CronUtils,JobInvokeUtil,QuartzDisallowConcurrentExecution,QuartzJobExecution,ScheduleUtils}.java
    - zgbas-quartz/src/main/java/com/spt/common/{constant,core,enums,annotation,exception,utils}/*.java (18 classes)
    - zgbas-quartz/src/main/resources/mybatis/mappers/{SysJobMapper,SysJobLogMapper}.xml
    - zgbas-quartz/src/main/resources/sql/{quartz.sql,sys_job.sql}
    - zgbas-admin/src/main/resources/templates/monitor/job/{job,add,edit}.html
    - .planning/phases/06-quartz-migration/06-01-MENU-INSERT.sql
  modified:
    - zgbas-quartz/pom.xml
    - zgbas-framework/src/main/java/com/spt/framework/config/ZgbasMybatisConfig.java
    - zgbas-system/pom.xml
    - zgbas-system/src/main/java/com/spt/bas/server/service/impl/CtrContractProfitServiceImpl.java
decisions:
  - D-P6-10 LOCKED (option a): visualization UI kept (Thymeleaf + external sys_menu)
  - Pitfall 1: ScheduleConfig uncommented + renamed QuartzScheduleConfig (bean name collision avoided)
  - Pitfall 3: Constants.JOB_WHITELIST_STR = {"com.spt"} (not {"com.ruoyi"})
  - Pitfall 4/W5: @PreAuthorize stripped + Shiro @RequiresPermissions compensation
  - Pitfall 7: XML path mybatis/mappers/ aligned with mapper-locations glob
  - Rule 3: ExcelUtil stubbed (full port would cascade 10+ classes; export not on QUARTZ-04 path)
  - Rule 3: RyTask flushUserRedisCache dropped (RedisUserCache not in monolith)
  - Rule 2: view methods added to SysJobController so /monitor/job is URL-reachable
metrics:
  duration: ~30 min
  completed: 2026-07-18
  tasks: 5 (4 auto + 1 checkpoint:human-blocked)
  files: 47 (23 Task 1 + 21 Task 2 + 2 Task 3 + 3 Task 4 + 1 Task 5 - 3 line-edited re-modifications)
---

# Phase 6 Plan 01: Quartz Foundation Summary

Ported RuoYi auth-quartz 18 classes + 2 XML + 2 SQL into a self-contained zgbas-quartz module; removed xxl-job-core from the reactor; wired the @Primary Druid DataSource through a quartz SchedulerFactoryBean; built the /monitor/job Thymeleaf console (list / add / edit / manual-trigger / start-stop) with Shiro @RequiresPermissions compensation and XSS defense-in-depth; laid sys_job / sys_job_log / QRTZ_* DDL files plus a ready-to-apply external spt-auth sys_menu INSERT. Full reactor `mvn -pl zgbas-admin -am compile` is GREEN; xxl-job residue across the monolith is 0.

## Tasks Completed

| # | Type | Name | Commit |
|---|------|------|--------|
| 1 | auto | Port RuoYi auth-quartz 18 classes + 2 XML + 2 SQL + 5 pitfall adaptations | 492eb26 |
| 2 | auto | Localize 18 com.spt.common.* classes + @MyBatisDao + MapperScan broaden + whitelist fix | 777f70a |
| 3 | auto | INFRA-03 — remove xxl-job-core + translate XxlJobHelper.log (reactor GREEN) | da168c6 |
| 4 | auto | D-P6-10 /monitor/job Thymeleaf UI (3 pages) | ef70191 |
| 5 | checkpoint:human-blocked | External spt-auth sys_menu INSERT SQL | 3e2f07c |

## What Landed

### Compile + Module Self-Containment (QUARTZ-01)
- zgbas-quartz is now a self-contained RuoYi quartz module: 18 RuoYi classes (config / domain / mapper / service / impl / controller / task / util) + 18 com.spt.common.* localized classes (BaseController / AjaxResult / TableDataInfo / BaseEntity / Constants / ScheduleConstants / HttpStatus / BusinessType / OperatorType / TaskException / @Excel / @Log / StringUtils / ExceptionUtil / BeanUtils / SpringUtils / ExcelUtil[stub] / ExcelHandlerAdapter).
- `QuartzScheduleConfig` (renamed from source `ScheduleConfig`) provides `@Bean SchedulerFactoryBean schedulerFactoryBean(DataSource)` — DataSource auto-wired to Phase 2's `@Primary` Druid.
- pom declares `quartz` + `spring-context-support` (BOM-managed versions); `c3p0` excluded to avoid Druid conflict (mirrors `spt-auth/auth-quartz/pom.xml` verbatim).
- SysJobServiceImpl.@PostConstruct init() + ScheduleUtils.createScheduleJob will fail-fast at startup if cron translation is bad — sets up D-P6-06 (Phase 6 plan 06).

### 5 Pitfall Adaptations (research §Common Pitfalls 1-7)
1. **Pitfall 1** — `QuartzScheduleConfig.java` generated from the source's 58 commented-out lines, uncommented, class name shifted to avoid bean-name collision with `com.spt.bas.server.config.ScheduleConfig`.
2. **Pitfall 2** — 17 source `com.spt.common.*` classes localized as a zgbas-quartz internal subset (not pulling auth-common jar). No transitive dep cascade.
3. **Pitfall 3** — `Constants.JOB_WHITELIST_STR = {"com.spt"}` so `com.spt.quartz.task.*` beans pass `ScheduleUtils.whiteList` validation.
4. **Pitfall 4** — Every `@PreAuthorize("@ss.hasPermi(...)")` stripped from both controllers; **Shiro `@RequiresPermissions` added on every write method** as W5 compensation control (research §Security Domain V4 / threat_model T-06-01-02 accept → mitigated). SysJobController switched from `@RestController` to `@Controller` + `@ResponseBody` per endpoint so Thymeleaf view methods resolve.
5. **Pitfall 7** — RuoYi XML path `mapper/quartz/` rewritten to `mybatis/mappers/` to match `mybatis-plus.mapper-locations=classpath:/mybatis/mappers/*Mapper.xml` glob. `@MapperScan` basePackages extended with `com.spt.quartz.mapper`; both mappers annotated `@MyBatisDao` (Phase 2/5 marker convention).

### xxl-job Fully Removed (INFRA-03)
- `zgbas-system/pom.xml` `<dependency>com.xuxueli:xxl-job-core:2.3.0</dependency>` deleted.
- `CtrContractProfitServiceImpl` — 2 `XxlJobHelper.log(...)` lines deleted (CONTEXT.md Discretion: adjacent `log.info(...)` already logs identical content; "直接删 XxlJobHelper.log 行最干净"). Import line also removed.
- Monolith-wide grep `com.xxl.job|XxlJobHelper|@XxlJob|XxlJobSpringExecutor` in `*.java/*.yml/*.properties/*.xml` = 0 (excluding `.planning` / `.claude` / `target`).
- Reactor compile `mvn -pl zgbas-admin -am compile` GREEN across all 5 modules.

### D-P6-10 Visualization UI (QUARTZ-04 code dimension)
- `templates/monitor/job/job.html` — list page with start/stop / manual-trigger / edit / delete buttons, AJAX-wired to SysJobController @PostMapping `/list`, PUT `/run`, PUT `/changeStatus`.
- `templates/monitor/job/add.html` + `edit.html` — form pages with invoke_target field rendered via `th:value` (Thymeleaf HTML auto-escape, threat_model T-06-01-06 mitigation). `grep "th:utext" = 0` across all 3 files (XSS defense-in-depth).
- Shell reuses operlog/operlog.html layout (`th:include="include :: header"` / `footer` / `bootstrap-select-*` / `shiro:hasPermission` dialect) — no full RuoYi template port.

### SysJobController — View Methods Added (Rule 2)
- Added `@GetMapping()` view (returns `monitor/job/job`), `@GetMapping("/add")`, `@GetMapping("/edit/{jobId}")` to SysJobController (Task 1) so `/monitor/job` is URL-reachable for the Task 5 checkpoint fallback path (direct link without menu).

### SQL DDL Files (QUARTZ-02 file dimension)
- `zgbas-quartz/src/main/resources/sql/quartz.sql` — verbatim copy of `spt-auth/sql/quartz.sql` (11 QRTZ_* tables).
- `zgbas-quartz/src/main/resources/sql/sys_job.sql` — sys_job + sys_job_log DDL + 3 RyTask demo INSERTs, extracted from `spt-auth/sql/ry_20210908.sql:567-603` (research §Pitfall 4: source DDL was NOT in quartz.sql).
- Files not applied automatically (D-P2-02 `ddl-auto=none`); operator/DBA applies manually to `sptbasdb_pd` before D-P6-06 startup validation (plan 06-06).

### External Menu Wiring (Task 5 checkpoint)
- `.planning/phases/06-quartz-migration/06-01-MENU-INSERT.sql` generated: 1 parent menu row (menu_id=110, component=`monitor/job/job`) + 6 button permission rows (1049-1055, all monitor:job:* perms).
- `INSERT IGNORE` for idempotency; component path corrected from RuoYi default `monitor/job/index` to `monitor/job/job` (Task 4 template name).
- **Checkpoint:human-blocked** — user/operator applies SQL to external spt-auth DB, refreshes auth-sdk cache, verifies menu visible.

## Deviations from Plan

### Auto-fixed Issues (Rules applied)

**1. [Rule 3 — Blocking transitive dep] ExcelUtil stubbed instead of full port**
- **Found during:** Task 2
- **Issue:** Source `spt-auth/auth-common/.../poi/ExcelUtil.java` is 1158 lines + 35 imports, pulling in Excels/SptAuthConfig/Convert/UtilException/DateUtils/DictUtils/FileTypeUtils/FileUtils/ImageUtils/ReflectUtils (~10 cascade classes). Full port would consume the whole Task 2 budget.
- **Fix:** Wrote a 30-line stub at `com.spt.common.utils.poi.ExcelUtil` that satisfies the SysJobController/SysJobLogController `export` endpoint signatures (constructor + `exportExcel(HttpServletResponse, List<T>, String)`) and throws `UnsupportedOperationException` at runtime. Plan Task 2 action D explicitly allows this ("不剥 ExcelUtil / @Excel / @Log 先保 SysJobController.export 编译通过，export 功能可后期剥除").
- **Files modified:** zgbas-quartz/src/main/java/com/spt/common/utils/poi/ExcelUtil.java
- **Commit:** 777f70a

**2. [Rule 3 — Blocking transitive dep] BaseController minimal version (5 methods only)**
- **Found during:** Task 2
- **Issue:** Source `BaseController.java` pulls in mybatis-plus Page / pagehelper PageInfo / LoginUser / PageDomain / TableSupport / DateUtils / PageUtils / SecurityUtils (RuoYi) / SqlUtil — 13 cascade classes, most cascading further. Quartz subsystem uses only 5 methods: startPage / getDataTable / toAjax / error / getUsername.
- **Fix:** Wrote a minimal BaseController using monolith stack directly: `com.github.pagehelper.PageHelper` 5.3.0 (transitively on classpath per dependency:tree) + Apache Shiro `SecurityUtils.getSubject().getPrincipal()` for username. getUsername falls back to `"admin"` when no Shiro session is bound (e.g. dry-run via scheduler on fresh boot).
- **Files modified:** zgbas-quartz/src/main/java/com/spt/common/core/controller/BaseController.java
- **Commit:** 777f70a

**3. [Rule 3 — Cascade avoidance] StringUtils format() inlined**
- **Found during:** Task 2
- **Issue:** Source StringUtils.format() delegates to StrFormatter which calls Convert.utf8Str() — pulling StrFormatter + Convert (2 more classes). RuoYi quartz uses format() only in RyTask.ryMultipleParams diagnostic logging.
- **Fix:** Inlined a simple `{}`-substitution format() implementation directly inside StringUtils (handles non-escaped case — RuoYi StrFormatter's escape `\\{}` path is unused for quartz).
- **Files modified:** zgbas-quartz/src/main/java/com/spt/common/utils/StringUtils.java
- **Commit:** 777f70a

**4. [Rule 3 — Missing class] RyTask flushUserRedisCache dropped**
- **Found during:** Task 1
- **Issue:** Source RyTask has a `flushUserRedisCache()` method that calls `com.spt.framework.cache.RedisUserCache.init()` — a type that does not exist in the monolith (grep-verified). Porting would require either localizing RedisUserCache (cascading into more framework classes) or stubbing.
- **Fix:** Dropped the method entirely. The 3 sys_job demo INSERTs only reference `ryNoParams / ryParams / ryMultipleParams`; flushUserRedisCache is not referenced by any sys_job data. SysJobLogServiceImpl will simply never invoke it.
- **Files modified:** zgbas-quartz/src/main/java/com/spt/quartz/task/RyTask.java
- **Commit:** 492eb26

**5. [Rule 2 — Critical functionality] View methods added to SysJobController**
- **Found during:** Task 1
- **Issue:** Task 5 checkpoint fallback requires `GET /monitor/job` to render `job.html` even without sys_menu applied. Source `SysJobController` only has REST endpoints (no view handlers). Without view methods, direct-URL fallback fails (404 on GET /monitor/job).
- **Fix:** Added 3 view methods to SysJobController: `@GetMapping()` view (returns `"monitor/job/job"`), `@GetMapping("/add")`, `@GetMapping("/edit/{jobId}")` (prefills ModelMap). Switched class from `@RestController` to `@Controller` + `@ResponseBody` per endpoint so Thymeleaf views resolve.
- **Files modified:** zgbas-quartz/src/main/java/com/spt/quartz/controller/SysJobController.java
- **Commit:** 492eb26

**6. [Task 5 Component path correction] monitor/job/index → monitor/job/job**
- **Found during:** Task 5
- **Issue:** Plan Task 5 action A specifies url='monitor/job' (not 'monitor/job/index') to match zgbas-plus's `job.html` template. RuoYi standard `sys_menu.component` for menu_id=110 is `'monitor/job/index'` (RuoYi template name).
- **Fix:** SQL INSERT uses `component='monitor/job/job'` to match the Task 4 template at `templates/monitor/job/job.html`. Added an `UPDATE` comment block to retrofit the component path if menu_id=110 pre-exists from a RuoYi seed.
- **Files modified:** .planning/phases/06-quartz-migration/06-01-MENU-INSERT.sql
- **Commit:** 3e2f07c

**7. [Task 1 ExcelUtil support] HttpStatus / OperatorType / ExcelHandlerAdapter localized**
- **Found during:** Task 2
- **Issue:** Plan Task 2 lists 15 classes to localize but AjaxResult imports `com.spt.common.constant.HttpStatus`, `@Log` references `com.spt.common.enums.OperatorType`, `@Excel` references `com.spt.common.utils.poi.ExcelHandlerAdapter`. Without these 3 supporting types, the 15-class localization set does not compile.
- **Fix:** Added 3 supporting types (HttpStatus minimal — SUCCESS/ERROR only; OperatorType verbatim 3-value enum; ExcelHandlerAdapter verbatim interface). Plan's "15 classes" was the user-facing API surface count; these 3 are part of the same localization batch.
- **Files modified:** zgbas-quartz/src/main/java/com/spt/common/{constant/HttpStatus.java, enums/OperatorType.java, utils/poi/ExcelHandlerAdapter.java}
- **Commit:** 777f70a

## Checkpoint Status

### Task 5 (checkpoint:human-blocked) — Awaiting user signal

**What is automated (done):**
- `.planning/phases/06-quartz-migration/06-01-MENU-INSERT.sql` generated with 1 parent row + 6 button rows, idempotent INSERT IGNORE pattern.
- SysJobController has `@GetMapping` view handler so `/monitor/job` is reachable via direct URL even before menu SQL is applied.

**What is blocked (awaits user/operator):**
Apply SQL to external spt-auth MySQL DB, then verify one of:
1. `menu-applied` — login zgbas-plus → "系统监控" → "定时任务" entry visible.
2. `direct-link-verified` — open `http://<zgbas-plus-host>/monitor/job` in authenticated session, Task 4 `job.html` renders.
3. `menu-deferred-to-p7` — defer to Phase 7 UAT (caveat: D-P6-10 code-complete but operational menu apply is Phase 7 work).

## Known Stubs

| Stub | File | Reason | Resolution Path |
|------|------|--------|-----------------|
| ExcelUtil.exportExcel throws UnsupportedOperationException | zgbas-quartz/src/main/java/com/spt/common/utils/poi/ExcelUtil.java | Source class is 1158 lines + 35 cascade imports. Not on QUARTZ-04 critical path (admin export convenience only). Plan Task 2 action D explicitly defers. | Future enhancement: port full ExcelUtil + Excels + ReflectUtils + supporting file utils, OR swap export endpoint to a simpler Apache POI direct implementation. |
| BaseController.initBinder Date parsing | zgbas-quartz/.../BaseController.java | Source used RuoYi DateUtils.parseDate which pulls DictUtils + Convert cascade. | SysJobController endpoints don't bind Date query params so this is never hit. If needed: add a real Date parser using java.text.SimpleDateFormat chain. |
| @Log AOP aspect | (none — annotation only) | Monolith has no equivalent of RuoYi LogAspect. @Log annotations compile but don't trigger audit logging at runtime. | sys_job_log already captures the critical audit trail (jobName / invokeTarget / status / exceptionInfo / timing). Phase 7 UAT to decide if per-endpoint admin audit is needed. |

## Threat Flags

No new threat surfaces introduced beyond what the plan's `<threat_model>` already tracks (T-06-01-01 through T-06-01-07 + T-06-01-SC + T-06-06-SC). All mitigations applied as planned:
- T-06-01-02 (EoP on controller writes): **mitigated** (Shiro @RequiresPermissions added — flips source `accept` disposition to `mitigated`).
- T-06-01-06 (XSS on invoke_target): **mitigated** (th:value auto-escape + grep-verified `th:utext = 0`).
- T-06-01-01 (RCE via invoke_target): **mitigated** (whitelist = {"com.spt"} + RuoYi 4-layer defense retained).
- T-06-01-03 (unaudited DDL apply): **mitigated** (DDL files only, not executed; apply deferred to 06-05 human-verify).

## Self-Check: PASSED

Files verified to exist:
- zgbas-quartz/src/main/java/com/spt/quartz/config/QuartzScheduleConfig.java — FOUND
- zgbas-quartz/src/main/java/com/spt/quartz/controller/SysJobController.java — FOUND
- zgbas-quartz/src/main/java/com/spt/quartz/controller/SysJobLogController.java — FOUND
- zgbas-quartz/src/main/java/com/spt/quartz/domain/SysJob.java — FOUND
- zgbas-quartz/src/main/java/com/spt/quartz/domain/SysJobLog.java — FOUND
- zgbas-quartz/src/main/java/com/spt/quartz/mapper/SysJobMapper.java — FOUND
- zgbas-quartz/src/main/java/com/spt/quartz/mapper/SysJobLogMapper.java — FOUND
- zgbas-quartz/src/main/java/com/spt/quartz/service/ISysJobService.java — FOUND
- zgbas-quartz/src/main/java/com/spt/quartz/service/ISysJobLogService.java — FOUND
- zgbas-quartz/src/main/java/com/spt/quartz/service/impl/SysJobServiceImpl.java — FOUND
- zgbas-quartz/src/main/java/com/spt/quartz/service/impl/SysJobLogServiceImpl.java — FOUND
- zgbas-quartz/src/main/java/com/spt/quartz/task/RyTask.java — FOUND
- zgbas-quartz/src/main/java/com/spt/quartz/util/{AbstractQuartzJob,CronUtils,JobInvokeUtil,QuartzDisallowConcurrentExecution,QuartzJobExecution,ScheduleUtils}.java — FOUND (6/6)
- zgbas-quartz/src/main/resources/mybatis/mappers/{SysJobMapper,SysJobLogMapper}.xml — FOUND
- zgbas-quartz/src/main/resources/sql/{quartz,sys_job}.sql — FOUND
- zgbas-admin/src/main/resources/templates/monitor/job/{job,add,edit}.html — FOUND
- .planning/phases/06-quartz-migration/06-01-MENU-INSERT.sql — FOUND
- 18 com.spt.common.* localized classes under zgbas-quartz/src/main/java/com/spt/common/ — FOUND

Commits verified to exist:
- 492eb26 (Task 1) — FOUND
- 777f70a (Task 2) — FOUND
- da168c6 (Task 3) — FOUND
- ef70191 (Task 4) — FOUND
- 3e2f07c (Task 5) — FOUND

Reactor compile gate: `mvn -pl zgbas-admin -am compile` GREEN (5/5 modules SUCCESS, 0 ERROR).

Monolith xxl-job residue: grep across `*.java/*.yml/*.properties/*.xml` = 0 (excluding `.planning` / `.claude` / `target`).

Plan success criteria status:
- [x] QUARTZ-01 compile dimension closed (reactor green; Scheduler bean wiring validated in 06-06)
- [x] QUARTZ-02 file dimension closed (sys_job.sql + quartz.sql ready; DB apply is 06-05 + operational)
- [x] QUARTZ-04 code dimension closed (Thymeleaf UI + Task 1 endpoints + Task 5 menu SQL)
- [x] INFRA-03 fully closed (xxl-job-core removed, XxlJobHelper.log translated, residue = 0)
- [x] 5 research-flagged pitfalls all handled (Pitfall 1/2/3/4/7 in code; Pitfall 5 via D-P6-10 LOCKED option a)
- [x] W5 compensation: @RequiresPermissions replaces stripped @PreAuthorize (T-06-01-02 accept → mitigated)

## Deferred Items

| Item | Reason | Next Owner |
|------|--------|------------|
| External spt-auth sys_menu INSERT apply | Task 5 checkpoint:human-blocked | User/operator |
| Excel export implementation (ExcelUtil stub) | Plan Task 2 action D defers; not on QUARTZ-04 critical path | Future enhancement |
| sys_job / sys_job_log / QRTZ_* DDL apply to sptbasdb_pd | D-P2-02 ddl-auto=none; plan 06-05 human-verify | User/operator (06-05) |
| Scheduler startup validation (D-P6-06 fail-fast) | Depends on sys_job data (06-05) + scheduler boot (06-06) | Plan 06-06 |
| Cron translation (D-P6-01/02) | Requires external xxl-job admin DB dump | Plan 06-05 |
| Handler port (~60 @XxlJob handlers) | Out of 06-01 scope | Plans 06-02/06-03 |
| MQApi/BasCommandExecutor D-P6-11 refactor | Out of 06-01 scope | Plan 06-04 |

## TDD Gate Compliance

N/A — plan `type=execute` (not `tdd`). No test(...) / feat(...) gate sequence required. Compile gates are GREEN per Task 2 and Task 3 verify output.
