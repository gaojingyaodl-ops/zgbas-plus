---
phase: 6
slug: quartz-migration
status: draft
nyquist_compliant: true
wave_0_complete: false
created: 2026-07-18
---

# Phase 6 — Validation Strategy

> Per-phase validation contract for feedback sampling during execution.
> Derived from `06-RESEARCH.md` §Validation Architecture (HIGH confidence).

---

## Test Infrastructure

| Property | Value |
|----------|-------|
| **Framework** | JUnit 4 + Spring Boot Test 2.5.9（与 Phase 3-5 一致，`zgbas-admin/src/test/`）|
| **Config file** | 无独立 config（沿用 Phase 3-5 `ZgbasApplicationTest` 非 hermetic 模式）|
| **Quick run command** | `JAVA_HOME=/Library/Java/JavaVirtualMachines/corretto-1.8.0_482/Contents/Home mvn -pl zgbas-quartz -am compile`（grep `^\[ERROR]` = 0 lines）|
| **Full suite command** | `JAVA_HOME=/Library/Java/JavaVirtualMachines/corretto-1.8.0_482/Contents/Home mvn -pl zgbas-admin -am test -Dtest=ZgbasApplicationTest`（启动验证：含 SysJobServiceImpl `@PostConstruct` 加载全量 sys_job 的 fail-fast）|
| **Estimated runtime** | ~60-90 seconds（compile gate ~30s + 启动验证 ~40-60s）|

> 非 hermetic：dev profile 明文密钥（Phase 4 D-P4-13 决定），**无需** `export DB_PASSWORD` / `SPT_APP_SECRET`。需本地 MySQL `sptbasdb_pd` 可达 + sys_job/sys_job_log/QRTZ_* 表已手工 apply DDL。

---

## Sampling Rate

- **After every task commit:** `mvn -pl zgbas-quartz -am compile` grep `^\[ERROR]` = 0（locale 无关，与 Phase 1-5 同契约）+ grep `@XxlJob\|XxlJobHelper\|com.xxl.job` 在 zgbas-quartz / zgbas-system 内 0 命中（排除 `.planning/` / `.claude/`）
- **After every plan wave:** `mvn -pl zgbas-admin -am test -Dtest=ZgbasApplicationTest`（启动验证 SysJobServiceImpl.init 不抛 SchedulerException）
- **Before `/gsd:verify-work`:** Full reactor `mvn test` green + D-P6-06 Scheduler 全量 sys_job cron 解析通过 + D-P6-04 抽样 dry-run（D-P6-05 只读真跑 / 写类空跑）通过
- **Max feedback latency:** ~90 seconds（compile + 启动验证）

---

## Requirement → Verification Map

> Per-task `<verify><automated>` blocks live in each `06-*-PLAN.md`. This table is the requirement-level contract.

| Req ID | Behavior | Test Type | Automated Command | Probe Location |
|--------|----------|-----------|-------------------|----------------|
| QUARTZ-01 | zgbas-quartz 模块编译 + RuoYi 基础设施 18 类 bean 解析（含 QuartzScheduleConfig / ScheduleUtils / SysJobServiceImpl）| compile gate + smoke | `mvn -pl zgbas-quartz -am compile` grep `^\[ERROR]`=0；`ZgbasApplicationTest#quartzBeanResolution_probe` 断言 `Scheduler` / `SysJobService` bean 存在 | ZgbasApplicationTest 扩展 |
| QUARTZ-02 | sys_job / sys_job_log / QRTZ_* 表存在 + SysJobMapper.selectJobList 可执行 | integration（非 hermetic，连真 dev 库）| `ZgbasApplicationTest#quartzTablesExist_probe` 断言 `SELECT 1 FROM sys_job LIMIT 1` 不抛；DDL 落库（`ry_20210908.sql:566-603` + `quartz.sql` 11 张 QRTZ_*）手工 apply 前置 | ZgbasApplicationTest 扩展 |
| QUARTZ-03 | 60 handler 全编译 + 全部 bean 名解析（启动期 SpringUtils.getBean 取每个 handler）| unit + integration | `mvn -pl zgbas-quartz -am compile` 全模块零 ERROR；`ZgbasApplicationTest#quartzBeanResolution_probe` 对 60 handler `context.containsBean(beanName)` 全通过 | ZgbasApplicationTest 扩展 |
| QUARTZ-04 | sys_job 数据初始化 + 至少 1 个 dry-run 可手动触发 + 传参 | integration + manual | `ZgbasApplicationTest#sampleQuartzJobDryRun_proof`（`@Disabled` 默认 + 手动启用）：`sysJobService.run(job)` 触发 1 个只读 handler，断言 sys_job_log 新增 status=SUCCESS | ZgbasApplicationTest 扩展 |
| INFRA-03 | xxl-job-core 删除 + CtrContractProfitServiceImpl `XxlJobHelper.log` 翻译为 `log.info` | compile gate + grep | `mvn -pl zgbas-system -am compile` 零 ERROR；grep `com.xxl.job\|@XxlJob\|XxlJobHelper` monolith 内 0 命中（排除 `.planning/` / `.claude/`）| grep 断言 |

---

## Wave 0 Requirements

> Wave 0 = 06-01 基础设施 plan 落地后、后续 handler 迁移前的测试基础。

- [ ] DDL apply（人工）：`zgbas-quartz/src/main/resources/sql/` 下 `ry_20210908.sql:566-603`（sys_job/sys_job_log）+ `quartz.sql`（11 张 QRTZ_*）apply 到本地 `sptbasdb_pd` —— 非 hermetic 前置，D-P2-02 ddl-auto=none 不自动建表
- [ ] `zgbas-admin/src/test/java/com/spt/ZgbasApplicationTest.java` 扩展 4 个 probe/proof 方法：
  - [ ] `quartzBeanResolution_probe`（Wave 1+）— `Scheduler` bean 存在 + `SysJobService`/`SysJobLogService` + 60 handler bean `context.containsBean` 全通过
  - [ ] `quartzTablesExist_probe`（Wave 0 后置，DDL 落库后）— `sys_job`/`sys_job_log` 表存在（`SELECT 1` 不抛）
  - [ ] `schedulerLoadAllJobs_proof`（Wave 5 后置，sys_job 数据到位后）— SysJobServiceImpl.`@PostConstruct` init 不抛 SchedulerException，即证 D-P6-06 fail-fast（cron 全解析 + invoke_target bean.method 全存在）
  - [ ] `sampleQuartzJobDryRun_proof`（Wave 5）— `@Disabled` 默认 + 手动启用，调 `sysJobService.run(jobId)` 触发 1 个只读 handler，断言 sys_job_log 新增 status=SUCCESS
- [ ] （可选）`zgbas-quartz/src/test/java/.../QuartzCompileSanityTest.java` — 验证 `com.spt.quartz.*` 包内 RuoYi 类无 import 缺失

---

## Manual-Only Verifications

| Behavior | Requirement | Why Manual | Test Instructions |
|----------|-------------|------------|-------------------|
| sys_job 数据落库 | QUARTZ-04 | D-P6-01 `checkpoint:human-blocked` 等待用户导出 xxl-job admin DB → D-P6-02 `checkpoint:human-verify` 逐条核对 sys_job INSERT | 用户提供 xxl_job_info 导出 → planner 翻译 → 用户核对 → 手工 apply `sys_job_data.sql` 到 `sptbasdb_pd` |
| 写类 handler dry-run | QUARTZ-04 | D-P6-05 写类（autoPay / refreshContractStatusTask 等）空跑，保护 dev 库不被污染 | test-side mock handler 的 service 依赖，只验 bean 解析 + JobDataMap 参数传递 + slf4j 日志（真实写类回归留 Phase 7 ALIGN-01）|
| （ contingent）`/monitor/job` 菜单接入 | QUARTZ-04（D-P6-10）| 外部 spt-auth sys_menu INSERT 属 operational change，不在代码 PR 内 | 仅当 D-P6-10 走可视化 UI 路径时：外部 spt-auth `sys_menu` INSERT 一行 `('定时任务','2','2','job','monitor/job/index',...)` —— 待 Escalation Gate 决策（见 plan-checker blocker 2）|

---

## Validation Sign-Off

- [x] All tasks have `<verify><automated>` or Wave 0 dependencies（per `06-*-PLAN.md`）
- [x] Sampling continuity: no 3 consecutive tasks without automated verify（每 wave 末 compile gate）
- [x] Wave 0 covers all MISSING references（DDL apply + 4 probe/proof 方法）
- [x] No watch-mode flags
- [x] Feedback latency < 90s
- [x] `nyquist_compliant: true` set in frontmatter

**Approval:** pending（planner revision pass 关闭 plan-checker warnings 后转 approved）
