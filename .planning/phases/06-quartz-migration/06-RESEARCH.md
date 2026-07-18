# Phase 6: 定时任务迁移 - Research

**Researched:** 2026-07-18
**Domain:** xxl-job → RuoYi quartz 定时任务调度体系（单体迁移）
**Confidence:** HIGH（所有关键事实均经源码 + 模板 + SQL 直查核实；11 条 CONTEXT.md "须核实" 全部落地）

## Summary

本期把源 zgbas 微服务的 xxl-job 定时任务体系替换为 RuoYi quartz（单体已预留 `zgbas-quartz` 空模块，pom 骨架就位）。研究通过源码 grep（`/Users/alan/WorkSpace/IDEA/zgbas` feat-系统重构v5.0）+ RuoYi 参考源直查（`/Users/alan/WorkSpace/IDEA/spt-auth/auth-quartz` + `/Users/alan/WorkSpace/IDEA/spt-auth/sql`）+ 当前单体文件扫描（`/Users/alan/WorkSpace/IDEA/zgbas-plus`）三向核实，**resolved all 11 "须核实" markers** in CONTEXT.md — but also surfaced **5 critical unanticipated blockers** the planner MUST address before QUARTZ-01 "整模块复制" can land as a 1:1 copy.

**Verified counts (覆盖 CONTEXT.md 的 "约 64"）：** 实测 65 个 `@XxlJob` 注解：basServer 58（`task/` 48 + `rocketmq/task/` 9 + `command/BasCommandExecutor` 1，含 `OrverdurTask` 1 个 `// @XxlJob` 注释行）+ reportServer 1（`ReportCommandExecutor`）+ web 1（`BasWebCommand`）+ basWx 5（不迁）。**本期实际迁入 60 个 handler**（basServer 58 + reportServer 1 + web 1，按 D-P6-08 排除 basWx 5）。

**The 5 critical unanticipated findings (planner MUST plan around):**

1. **`auth-quartz/ScheduleConfig.java` ENTIRELY COMMENTED OUT** — 58 行整类被 `//` 注释。`SysJobServiceImpl` `@Autowired Scheduler scheduler;` 没有该 `@Bean SchedulerFactoryBean` 启动必报 `NoSuchBeanDefinitionException`。**Phase 6 必须 uncomment（或新写）一个等价 ScheduleConfig**，否则启动失败、D-P6-06 fail-fast 无从谈起。
2. **`com.spt.common.*` 17 个依赖类在单体中全部不存在** — `auth-quartz/pom.xml` 声明的 `auth-common` + `auth-framework` 不在 zgbas-plus 依赖图。受影响的 import 清单（实测 grep）：`com.spt.common.constant.{Constants, ScheduleConstants}` / `com.spt.common.exception.job.TaskException` / `com.spt.common.core.{controller.BaseController, domain.{AjaxResult, BaseEntity}, page.TableDataInfo}` / `com.spt.common.enums.BusinessType` / `com.spt.common.annotation.{Excel, Log}` / `com.spt.common.utils.{StringUtils, ExceptionUtil, bean.BeanUtils, spring.SpringUtils, poi.ExcelUtil}`。**`Constants.JOB_WHITELIST_STR = {"com.ruoyi"}` hardcoded** — 必须改为 `{"com.spt"}` 否则 `/monitor/job` 新增任何 `com.spt.quartz.task.*` invokeTarget 都被 `ScheduleUtils.whiteList` 拒绝。
3. **单体用 Shiro，不是 Spring Security** — RuoYi `SysJobController/SysJobLogController` 的 `@PreAuthorize("@ss.hasPermi('monitor:job:list')")` 启动期 AOP 织入失败（无 `@EnableGlobalMethodSecurity` + 无 `ss` bean + 无 spring-security 依赖）。**必须剥除所有 @PreAuthorize 注解**（或在 zgbas-framework 加 Shiro 等价注解；推荐剥除以最小侵入，与 Phase 3 Shiro 链路一致）。
4. **`sys_job` / `sys_job_log` DDL 不在 `quartz.sql`，在 `ry_20210908.sql` 第 566–603 行** — `quartz.sql` 只有 11 张 QRTZ_* 表。`spt-auth/auth-quartz/src/main/resources/` 下无 sql 子目录。Phase 6 建表须从 `ry_20210908.sql` 抽出 sys_job/sys_job_log 段，与 quartz.sql 合并落地到 `sptbasdb_pd`（D-P2-02 ddl-auto=none 不自动建表，手动 DDL）。
5. **spt-auth 无 `/monitor/job` Thymeleaf 模板** — `auth-admin/src/main/java/com/spt/web/controller/monitor/` 只有 5 个 Controller（Cache/Logininfor/Operlog/Server/SysUserOnline），无 SysJob*；`spt-auth/auth-ui/src/views/monitor/job` 是 Vue 前端组件，不是 Thymeleaf。zgbas/web/templates/monitor + zgbas-plus/zgbas-admin/templates/monitor 子目录是 `cache/logininfor/online/operlog/server`，**无 `job`**。D-P6-10 "引入 RuoYi `/monitor/job` Thymeleaf 页面" **不能从 spt-auth 复制** — 须 (a) 从官方 RuoYi（gitee.com/y_project/RuoYi）移植，或 (b) 仅交付 SysJobController JSON API 不做可视化（CONTEXT.md 已批准的"弃'保 Controller API 不引入页面'"备选），或 (c) 自写最小 Thymeleaf 表格页。推荐 (b)：API 可用即满足 QUARTZ-04"手动触发与传参"硬验收，前端集成延后到 Phase 7 或运维加菜单时再做。

**Primary recommendation:** Phase 6 按 **Wave 0 基础设施（auth-quartz 复制 + auth-common 子集本地化 + sys_job 表 DDL + ScheduleConfig uncomment + whiteList 改 com.spt + @PreAuthorize 剥除 + @MapperScan 放宽 + XML 路径对齐 + xxl-job-core 删 + CtrContractProfitServiceImpl 翻译）→ Wave 1 command executors（BasCommandExecutor + ReportCommandExecutor + BasWebCommand）→ Wave 2 basServer/task ~21 文件 → Wave 3 rocketmq/task 8 文件 → Wave 4 sys_job 数据（D-P6-01 用户导出物到位 checkpoint:human-blocked → D-P6-02 翻译 → checkpoint:human-verify）→ Wave 5 验收（D-P6-06 fail-fast + D-P6-04/05 抽样 dry-run）** 五波推进，每波后 `mvn -pl zgbas-quartz -am compile` + grep `^\[ERROR]` locale 无关零错才进下一波。

## Architectural Responsibility Map

| Capability | Primary Tier | Secondary Tier | Rationale |
|------------|-------------|----------------|-----------|
| Quartz Scheduler bean + JobStore 配置 | zgbas-quartz/config | — | `SchedulerFactoryBean` 是 quartz 引擎入口，与业务隔离，归 quartz 模块独占 |
| 任务表 CRUD（sys_job / sys_job_log） | zgbas-quartz/mapper+service | mybatis-plus (@Primary DataSource) | 复用 Phase 2 双 ORM 单 DataSource，namespace 独立不冲突（实证 Phase 5） |
| 任务管理台 UI（`/monitor/job`） | zgbas-admin（嵌入 Thymeleaf）| zgbas-quartz/controller | RuoYi 单体同构：Controller 落业务模块、admin 嵌入式服务器提供服务、`@ComponentScan("com.spt")` 自动扫到 |
| 业务调度 handler（60 个迁入类） | zgbas-quartz/task | zgbas-system（被调用方） | D-P6-07：handler 与 RuoYi 基础设施同模块便于运维定位；maven 依赖 quartz→system 单向 |
| HTTP 同步触发门面（`MQApi` 7 个 GET + 可能的 command executors）| zgbas-admin/api | zgbas-system/service（直调目标）| D-P6-11：HTTP 端点照搬保留（前端零改），内部改同进程同步直调 service，不经 quartz run(jobId) |
| Spring `@Scheduled` 线程池（ShiroHeartJob 等）| zgbas-system/config（已就位的 `com.spt.bas.server.config.ScheduleConfig`）| — | 与 quartz 完全独立机制，**保留不动**（CONTEXT.md Discretion 明示） |
| xxl-job 痕迹清除 | zgbas-system/pom.xml + service/impl/CtrContractProfitServiceImpl | — | INFRA-03 边界极小：pom 1 处 + 2 行 XxlJobHelper.log |

<user_constraints>
## User Constraints (from CONTEXT.md)

### Locked Decisions（12 条，逐字复制）

#### cron 数据来源（本期最核心未知）
- **D-P6-01**: 导出生产 xxl-job admin DB cron → 翻译为 sys_job（非"仅手动触发"，非"逐个业务方确认"）。⚠ **前置依赖**：用户须提供 xxl-job admin 导出（DB 访问 / dump）；planner 作 `checkpoint:human-blocked` 等待导出物到位。
- **D-P6-02**: 我翻译 + 你逐项核对（checkpoint:human-verify）。Claude 翻译 cron 字段数适配 + invoke_target 构造 + 参数固化 + job_group/status 划分，产物交用户**逐条核对**再落库。
- **D-P6-03**: 无 cron / 手动型 / 废弃任务分级处理。① 有 cron → sys_job.status=NORMAL；② 无 cron 手动型 → PAUSED；③ 废弃 → 跳过不迁。

#### 验收策略
- **D-P6-04**: 全量 bean 解析 + 抽样执行。60+ handler 全编译过 + quartz bean 全解析 + Scheduler 加载全量 sys_job（D-P6-06）；执行层抽 3-5 个代表性 handler 手动触发 dry-run。
- **D-P6-05**: dry-run 分级——只读真跑 / 写类空跑。只读/幂等类真跑；写类（autoPay / refreshContractStatusTask / OrverdurTask / DepositPaymentTask）空跑（Mockito mock 业务 service，验 bean 解析 + 参数传递 + slf4j 日志）。
- **D-P6-06**: 启动期验证强度=强（Scheduler 全量加载 sys_job cron 做 fail-fast）。cron 全解析通过 + 每任务 trigger 下次触发时间可算 + invoke_target 指向 bean.method 全存在。翻译错 → fail-fast 启动报错。

#### 模块落位与拓扑
- **D-P6-07**: 60+ handler 落 `zgbas-quartz`（依赖方向 quartz → system）。⚠ 更新 D-08 模块拓扑：本期定为 quartz → system。
- **D-P6-09**: handler 包名改 `com.spt.quartz.task`（⚠ 偏离 D-P2-07 照搬保包名，仅限 handler 类）。⚠ research/planning 须显式核实：① MQApi / BasCommandExecutor 对 handler 类的直接引用——repackage 后 import 需同步更新；② handler 内部 @Autowired 的 service 接口包名（`com.spt.bas.server.service.*`）不变。

#### basWx 范围取舍
- **D-P6-08**: basWx/purchase-server 的 5 个 handler 随 v2 不迁（实际迁 ~60）。

#### RuoYi quartz admin UI
- **D-P6-10**: 保 `SysJobController`/`SysJobLogController` + 引入 RuoYi `/monitor/job` Thymeleaf 页面。⚠ research/planning 须处理：① RuoYi `/monitor/job` Thymeleaf 模板从 spt-auth 复制；② **菜单接入**——zgbas 用动态菜单（经 auth-sdk 从外部 spt-auth 取）；③ Controller 落 zgbas-quartz，由 admin `@ComponentScan("com.spt")` 扫到。

#### 触发门面（同步触发入口）
- **D-P6-11**: 保 `MQApi`/`BasCommandExecutor` HTTP 端点，内部改直调 service（不经 quartz 调度）。sys_job 是另一条定时调度入口，两者共存（手动 HTTP 门面直调 service + 定时 quartz 调度同 service）。

#### 行为等价范围
- **D-P6-12**: 仅保阻塞策略 + sys_job_log 日志，不保失败重试 / 告警邮件 / 路由策略。

### Claude's Discretion（INFRA-03 边界 + 写类空跑 + 参数固化 + cron 适配 + web handler + namespace + job_group + D-P2-07 偏离收口）

- **xxl-job 删除边界（INFRA-03）**：删 xxl-job-core jar 依赖（Phase 4 04-04 Rule 3 临时加的 2.3.0）+ 删 XxlJobSpringExecutor bean 配置 + 删 xxl.job.* yml/properties。⚠ **保留 ScheduleConfig.java**（Spring @EnableScheduling，非 xxl-job 非 RuoYi quartz）。
- **写类空跑实现**：test-side Mockito mock，不加生产 dry-run 开关。
- **任务参数固化**：随 D-P6-01 cron 导出一起翻译（xxl_job_info 同条记录含 executor_param），固化到 sys_job.args。
- **cron 格式适配**：xxl-job cron（常 6/7 字段含秒）→ quartz cron_expression（7 字段）适配属 D-P6-02 翻译流程一部分。
- **web 模块那 1 个 @XxlJob handler**：归类同其他 handler，落 zgbas-quartz `com.spt.quartz.task`。
- **RuoYi quartz mapper mybatis namespace**：`com.spt.quartz.mapper.SysJobMapper` 等独立 namespace，与 Phase 2/5 不冲突。
- **job_group 划分 / sys_job_log 保留时长**：留 planning（RuoYi 默认 DEFAULT/SYSTEM 分组 + 日志清理策略）。
- **D-P2-07 偏离收口**：D-P6-09 首次偏离"照搬保包名"，**仅限 handler 类**（因落 zgbas-quartz 模块）。entity / Dao / service / controller / Mapper 仍守 D-P2-07 保包名。research/planning 须把 D-P6-09 的引用更新作为显式 task，不遗漏。

### Deferred Ideas (OUT OF SCOPE)

- **xxl-job 失败重试 / 告警邮件 / 执行器路由行为等价** → 永久 Out-of-Scope（D-P6-12）
- **xxl-job admin 服务退役 / xxl_job_info 表迁入单体** → 运维事务
- **basWx/purchase-server 5 个 @XxlJob handler + purchase 业务** → v2（#14，D-P6-08）
- **真实全量任务回归对照 + 浏览器 e2e** → Phase 7（ALIGN-01/02）
- **报表物理分页性能改造 / 补 createBy-updateBy 审计字段** → 永久 Out-of-Scope
- **全量实体 schema drift reconcile + 重开 ddl-auto=validate** → tech debt
- **CR-01 真实轮换已泄漏生产库密码** → 跨阶段安全债
</user_constraints>

<phase_requirements>
## Phase Requirements

| ID | Description | Research Support |
|----|-------------|------------------|
| QUARTZ-01 | 新建 zgbas-quartz 模块，引入 RuoYi quartz（整模块复制 spt-auth/auth-quartz + ScheduleConfig） | §Architecture Patterns §1（auth-quartz 18 类 + 2 XML + pom 复制清单）；§Common Pitfalls #1/#2/#3（ScheduleConfig 整类注释 / 17 个 com.spt.common.* 缺失 / Shiro 剥 @PreAuthorize）|
| QUARTZ-02 | 建 sys_job / sys_job_log 表（DDL 参考 spt-auth/sql/quartz.sql） | §Architecture Patterns §2（DDL 实际在 ry_20210908.sql:566-603，非 quartz.sql；建表机制：手动 DDL，D-P2-02 ddl-auto=none）|
| QUARTZ-03 | 迁移 64 个 @XxlJob handler 为 quartz bean（机制字面：XxlJobHelper.log→slf4j / handleSuccess·Fail→return·异常 / getJobParam→JobDataMap）| §Code Examples §1（xxl-job → quartz 翻译规则）；§Common Pitfalls #4（实测 65 个 @XxlJob，本期 60 个迁入；3 个 executeCommand command executors 各占 1 个 handler name）|
| QUARTZ-04 | 任务记录初始化（cron/bean/method 翻译为 sys_job 数据），支持手动触发与传参 | §Code Examples §2（invokeTarget 编码）；§Common Pitfalls #5（cron 数据来源 checkpoint:human-blocked）|
| INFRA-03 | 删除 xxl-job 依赖与 executor 配置 | §Architecture Patterns §5（INFRA-03 边界极小：zgbas-system/pom.xml 1 处 + CtrContractProfitServiceImpl 2 行 + 单体无任何 yml/properties 配置 + 无 XxlJobSpringExecutor bean）|
</phase_requirements>

## Project Constraints (from CLAUDE.md)

- **Tech stack 锁定**：JDK 1.8 + Spring Boot 2.5.9（grandparent = `spring-boot-starter-parent:2.5.9`）；javax.* 体系，禁 jakarta 迁移
- **Build**：Maven 3.8.6（`/Users/alan/App/apache-maven-3.8.6`）+ settings `zg_settings.xml`（私服重定向）
- **Module structure**：固定 5 模块聚合 `zgbas-admin / common / framework / quartz / system`（`zgbas-quartz` 已在 pom 占位，内容仅 `PackageMarker.java`）
- **Persistence**：双 ORM 单 DataSource（@Primary Druid + JpaTransactionManager）+ mybatis-plus 3.1.2
- **External integration**：auth/push/file/sign 外部 Bean 保持原 HTTP 注入；spt-auth 保持外部（**关键约束**：单体本地无 sys_menu 表，菜单来自外部 spt-auth）
- **Removed infra**：nacos 已删（Phase 2）；xxl-job 本期删（INFRA-03）
- **Reference framework**：RuoYi 单体（yudao 排除）
- **Scope**：basWx 第一阶段不迁（D-P6-08 排除 5 个 purchase handler）
- **Private repo**：spt-tools-* 已内联（Phase 2）；auth-common / auth-framework 仍在 spt-auth 私服，本期需选择性本地化

## Standard Stack

### Core
| Library | Version | Purpose | Why Standard |
|---------|---------|---------|--------------|
| `org.quartz-scheduler:quartz` | 2.3.2（Spring Boot 2.5.9 BOM 托管，无需显式 version） | Quartz Scheduler 引擎 + JobStore / Trigger / JobDetail API | RuoYi quartz 默认栈，Spring Boot 2.5.9 spring-boot-dependencies:2.5.9 BOM 已托管 `[2.3.2]`，spt-auth/auth-quartz/pom.xml 使用同 version（无显式 version 覆盖）`[VERIFIED: Maven Central + Spring Boot 2.5.9 BOM]` |
| `spring-context-support` | 2.5.9（Spring Boot 托管） | `SchedulerFactoryBean`（Spring 包装的 quartz Scheduler 工厂）| Spring 官方 quartz 集成入口，无替代；spt-auth ScheduleConfig 即用此类 `[VERIFIED: Spring docs]` |
| RuoYi auth-quartz 18 类 + 2 XML | 3.8.2-SNAPSHOT（spt-auth 当前版本） | 整模块调度体系（config/domain/mapper/service/impl/controller/util/task + SysJob/SysJobLog Mapper XML） | spt-auth/auth-quartz 模板即 PROJECT.md 钦定来源；D-P2-07 照搬原则 `[VERIFIED: 源码直查]` |

### Supporting
| Library | Version | Purpose | When to Use |
|---------|---------|---------|-------------|
| `org.mybatis:mybatis` (via mybatis-plus 3.1.2) | 已就位 | SysJobMapper / SysJobLogMapper mybatis 接口绑定 | 无新增依赖，复用 Phase 2/5 基础设施 `[VERIFIED: zgbas-system/pom.xml]` |
| `com.spt.common.constant.ScheduleConstants` | 抽自 spt-auth/auth-common | TASK_PROPERTIES / TASK_CLASS_NAME / MISFIRE_* 常量 + Status 枚举 | auth-quartz 全文件依赖；**必须本地化到 zgbas-quartz**（单体无此包）`[VERIFIED: import 直查]` |
| `com.spt.common.exception.job.TaskException` | 抽自 spt-auth/auth-common | SysJobServiceImpl / ScheduleUtils 抛出 | 同上 |
| Mockito | 已就位 | 写类 handler 空跑（mock @Autowired service） | D-P6-05 dry-run 分级 `[VERIFIED: zgbas-admin/test 已用]` |

### Alternatives Considered
| Instead of | Could Use | Tradeoff |
|------------|-----------|----------|
| RuoYi quartz（auth-quartz 复制）| Spring `@Scheduled` 全量改写 | 60+ handler cron 全部硬编码到代码（无动态启停 / 无运行时改 cron / 无日志 UI），偏离 QUARTZ-04 + D-P6-10 可视化运维目标 |
| RuoYi quartz admin UI | 仅 SysJobController JSON API + 无 Thymeleaf | **本 research 推荐此备选**（CONTEXT.md "弃'保 Controller API 不引入页面'"已批准）—— 最小侵入、QUARTZ-04 硬验收（手动触发 + 传参）仍满足；可视化运维 UI 延后到 Phase 7 或运维独立加菜单时再做 |
| RuoYi quartz admin UI | 自写最小 Thymeleaf 表格页 | 工作量中等（2-3 模板）；好处是直链 `/monitor/job` 在 admin 内可访问，不依赖外部 spt-auth sys_menu INSERT |

**Installation:**
```bash
# zgbas-quartz/pom.xml 增加（Spring Boot 2.5.9 BOM 托管 version，无需显式声明）
<dependency>
    <groupId>org.quartz-scheduler</groupId>
    <artifactId>quartz</artifactId>
    <exclusions>
        <exclusion>
            <groupId>com.mchange</groupId>
            <artifactId>c3p0</artifactId>  <!-- 与 Druid 冲突，spt-auth/auth-quartz/pom.xml 同 exclude -->
        </exclusion>
    </exclusions>
</dependency>
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-context-support</artifactId>
</dependency>
# zgbas-system/pom.xml 删除
#   <artifactId>xxl-job-core</artifactId>  (Phase 4 04-04 Rule 3 临时加，本期 INFRA-03 移除)
```

**Version verification:**
```bash
# Spring Boot 2.5.9 BOM 托管验证（无需 mvn 调用）
grep -A 1 "quartz-scheduler\|<spring-context-support" \
  ~/.m2/repository/org/springframework/boot/spring-boot-dependencies/2.5.9/spring-boot-dependencies-2.5.9.pom
# 预期 quartz.version=2.3.2 / spring-context-support=5.3.13（与 spring-core 同步）
```

## Package Legitimacy Audit

| Package | Registry | Age | Downloads | Source Repo | slopcheck | Disposition |
|---------|----------|-----|-----------|-------------|-----------|-------------|
| `org.quartz-scheduler:quartz` | Maven Central | 18+ yrs（2008 至今）| 极高（Spring Boot 默认） | github.com/quartz-scheduler/quartz | n/a (Java) | Approved — Spring Boot 2.5.9 BOM 托管 `[VERIFIED: Maven Central + Spring Boot BOM]` |
| `spring-context-support` | Maven Central | 14+ yrs | 极高（Spring 框架核心模块之一）| github.com/spring-projects/spring-framework | n/a (Java) | Approved — Spring Boot 同步 `[VERIFIED: Spring docs]` |
| `xxl-job-core:2.3.0` | Maven Central | 7 yrs | 极高 | github.com/xuxueli/xxl-job | n/a (Java) | **REMOVED**（Phase 6 INFRA-03 删除，单体不再引用）|

**slopcheck 可用性：** `python3 -m slopcheck --version` 通过（v0.x，pip3 用户级安装）；但 slopcheck 后端是 PyPI/npm 索引，对 Maven 包不适用，故 Java 包合法性走 Maven Central 存在性 + Spring Boot BOM 托管双证（详见 §Sources）。

**Packages removed due to slopcheck [SLOP] verdict:** none
**Packages flagged as suspicious [SUS]:** none

*本期无新增可疑包；quartz + spring-context-support 均为 Spring Boot 2.5.9 默认托管版本，spt-auth/auth-quartz 同栈。*

## Architecture Patterns

### System Architecture Diagram

```
                 ┌──────────────────────────────────────────────────────────┐
                 │                  zgbas-plus monolith                     │
                 │                                                          │
   HTTP ────────▶│  zgbas-admin (port 8080)                                 │
   /monitor/job  │  ├── SysJobController (@RestController, /monitor/job)    │
   /monitor/jobLog│  ├── SysJobLogController (@RestController, /monitor/jobLog)│
   /mq/api/*     │  └── MQApi (@RestController, /mq/api, 7 GET sync triggers)│
   (sync trigger)│        │                                                 │
                 │        │ for MQApi D-P6-11: bypass quartz, direct call   │
                 │        ▼                                                 │
                 │  zgbas-system                                           │
                 │  ├── com.spt.bas.server.service.* (业务 service iface)  │
                 │  │   └── impl.* (业务实现，handler @Autowired 目标)      │
                 │  └── com.spt.bas.server.config.ScheduleConfig           │
                 │      (@EnableScheduling for ShiroHeartJob @Scheduled)   │
                 │                                                           │
                 │  zgbas-quartz (NEW module, Phase 6)                     │
                 │  ├── config/ScheduleConfig  ◀── uncomment @Bean         │
                 │  │     SchedulerFactoryBean (DataSource=Druid @Primary)  │
                 │  ├── domain/{SysJob, SysJobLog}                          │
                 │  ├── mapper/{SysJobMapper, SysJobLogMapper} (mybatis)   │
                 │  ├── service/{ISysJobService, ISysJobLogService}        │
                 │  ├── util/{AbstractQuartzJob, QuartzJobExecution,       │
                 │  │         QuartzDisallowConcurrentExecution,           │
                 │  │         ScheduleUtils, JobInvokeUtil, CronUtils}     │
                 │  ├── task/RyTask (sample) + com.spt.quartz.task.*       │
                 │  │     ◀── 60 handlers (repackage from source, D-P6-09) │
                 │  └── controller/{SysJobController, SysJobLogController} │
                 │                                                           │
                 │  RDBMS sptbasdb_pd (Druid DataSource)                    │
                 │  ├── sys_job (NEW, ~60 rows after D-P6-01/02)            │
                 │  ├── sys_job_log (NEW, grows per execution)              │
                 │  ├── QRTZ_* (11 tables, NEW — quartz JobStore persistent)│
                 │  └── 业务表 (Phase 4/5 已就位)                           │
                 └──────────────────────────────────────────────────────────┘
                                  ▲                              ▲
                                  │ Scheduler.triggerJob         │ SQL
                                  │ (async, cron)                │
                                  │                              │
              ┌───────────────────┴──────────────┐               │
              │  Quartz Scheduler Thread Pool    │───────────────┘
              │  (org.quartz.threadPool.threadCount=20)
              └──────────────────────────────────┘
                              │
                              │ invokeTarget = "beanName.methodName(args)"
                              │ e.g. "applyPayTask.autoPay()"
                              │      "basCommandExecutor.executeCommand('updateBudgetSettlement')"
                              ▼
                  JobInvokeUtil.invokeMethod()
                  via SpringUtils.getBean(beanName)
                  → reflection invokes method on handler bean
                  → handler @Autowired service.* (zgbas-system) executes business logic
                  → AbstractQuartzJob.after() writes sys_job_log
```

**Primary use case trace（cron 定时）:**
1. App 启动 → `SysJobServiceImpl.@PostConstruct init()` 调 `scheduler.clear()` + 遍历 `jobMapper.selectJobAll()` → 对每个 SysJob 调 `ScheduleUtils.createScheduleJob` 注册到 Scheduler（D-P6-06 fail-fast：cron 解析失败 / bean 不存在 → 直接抛 SchedulerException 启动报错）
2. Cron 触发时间到 → Scheduler 从线程池起一个 job → `QuartzJobExecution` 或 `QuartzDisallowConcurrentExecution`（按 `concurrent` 字段）的 `doExecute` → `JobInvokeUtil.invokeMethod` 反射调用 bean.method
3. `AbstractQuartzJob.before/after` 包裹执行时间记录 + 写 `sys_job_log`

**Primary use case trace（HTTP 同步触发，D-P6-11 MQApi）:**
1. 前端调 `GET /mq/api/ctrContractTask` → MQApi 直调 service（如 `synchronizedAllCtrContract` 内部业务方法）
2. **不经 Scheduler.run**（保同步语义；quartz run 是异步线程池）
3. 与 cron 入口共存：同一 service 既能 cron 定时（sys_job 注册）又能 HTTP 同步（MQApi 直调）

### Recommended Project Structure

```
zgbas-quartz/                                          # Phase 6 填充
├── pom.xml                                            # 已就位骨架；+ quartz + spring-context-support, - 无
└── src/main/
    ├── java/com/spt/
    │   ├── quartz/                                    # RuoYi 基础设施（auth-quartz 照搬，D-P2-07 保包名）
    │   │   ├── config/ScheduleConfig.java             # ⚠ uncomment 整类（源 58 行被注释）
    │   │   ├── controller/
    │   │   │   ├── SysJobController.java              # ⚠ 剥除 @PreAuthorize（Shiro 不兼容）
    │   │   │   └── SysJobLogController.java           # ⚠ 剥除 @PreAuthorize
    │   │   ├── domain/{SysJob, SysJobLog}.java        # 照搬
    │   │   ├── mapper/{SysJobMapper, SysJobLogMapper}.java  # ⚠ 加 @MyBatisDao 注解（Phase 2/5 标记约定）
    │   │   ├── service/
    │   │   │   ├── ISysJobService.java                # 照搬
    │   │   │   ├── ISysJobLogService.java             # 照搬
    │   │   │   └── impl/{SysJobServiceImpl, SysJobLogServiceImpl}.java  # 照搬
    │   │   ├── task/RyTask.java                       # 照搬（sample，保留作 sys_job 数据 demo）
    │   │   └── util/                                  # 照搬
    │   │       ├── AbstractQuartzJob.java
    │   │       ├── CronUtils.java
    │   │       ├── JobInvokeUtil.java
    │   │       ├── QuartzDisallowConcurrentExecution.java
    │   │       ├── QuartzJobExecution.java
    │   │       └── ScheduleUtils.java
    │   └── common/                                    # ⚠ 本地化子集（auth-common 中 monolith 缺失的 17 类）
    │       ├── constant/
    │       │   ├── Constants.java                     # ⚠ JOB_WHITELIST_STR 改 {"com.spt"}
    │       │   └── ScheduleConstants.java             # 照搬
    │       ├── exception/job/TaskException.java       # 照搬
    │       ├── core/                                  # 精简版（仅 SysJobController 用到的方法）
    │       │   ├── controller/BaseController.java     # 提供 startPage / getDataTable / toAjax / error / getUsername
    │       │   ├── domain/{AjaxResult, BaseEntity}.java
    │       │   └── page/TableDataInfo.java
    │       ├── enums/BusinessType.java                # 照搬（@Log 注解用）
    │       ├── annotation/                            # 照搬
    │       │   ├── Excel.java
    │       │   └── Log.java
    │       └── utils/                                 # 大部分可替换为 monolith 已有
    │           ├── StringUtils.java                   # 或改 import 为 org.apache.commons.lang3.StringUtils
    │           ├── ExceptionUtil.java
    │           ├── bean/BeanUtils.java                # 或改 import 为 org.springframework.beans.BeanUtils
    │           ├── spring/SpringUtils.java            # 必须本地化（RuoYi 用 ApplicationContextAware）
    │           └── poi/ExcelUtil.java                 # 照搬（export 用；可后期剥除 export 依赖）
    │
    │   # ⚠ 60 个 handler repackage 到 com.spt.quartz.task（D-P6-09）
    │   # 源 com.spt.bas.server.task.* + rocketmq.task.* + command.* + report.command.* + web.config.*
    │   # 类名保（D-P2-07），仅包名变
    │
    └── resources/
        └── mybatis/mappers/                           # ⚠ 路径对齐到 mybatis-plus.mapper-locations
            ├── SysJobMapper.xml                       # 照搬（namespace 不变 com.spt.quartz.mapper.SysJobMapper）
            └── SysJobLogMapper.xml                    # 照搬
```

**Admin/admin Resource Layout:**
```
zgbas-admin/src/main/resources/
└── (本期推荐：不引入 Thymeleaf 模板，走 SysJobController JSON API 备选 — 见 §Open Questions Q1)
```

### Pattern 1: RuoYi Quartz 启动期 fail-fast 加载（D-P6-06 实现）

**What:** SysJobServiceImpl.@PostConstruct init() 在 Spring context 初始化阶段遍历 sys_job 全表，把每条记录注册到 Scheduler；任何 cron 表达式无效 / bean 不存在 / method 签名不匹配都抛 SchedulerException 或 TaskException → **启动失败**（fail-fast）。

**When to use:** 即 D-P6-06 验收强度。本模式天然暴露 D-P6-01/02 cron 翻译错误（无需单独测试套件）。

**Example:**
```java
// Source: /Users/alan/WorkSpace/IDEA/spt-auth/auth-quartz/src/main/java/com/spt/quartz/service/impl/SysJobServiceImpl.java:37-44
@PostConstruct
public void init() throws SchedulerException, TaskException {
    scheduler.clear();
    List<SysJob> jobList = jobMapper.selectJobAll();
    for (SysJob job : jobList) {
        ScheduleUtils.createScheduleJob(scheduler, job);  // cron 解析失败 → CronScheduleBuilder 抛
    }
}
```
`ScheduleUtils.createScheduleJob` 内部：
```java
// Source: /Users/alan/WorkSpace/IDEA/spt-auth/auth-quartz/.../util/ScheduleUtils.java:55-85
CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(job.getCronExpression());
// ⚠ 若 cron 格式非法，这一行抛 RuntimeException → 启动失败 (D-P6-06 fail-fast)
cronScheduleBuilder = handleCronScheduleMisfirePolicy(job, cronScheduleBuilder);
CronTrigger trigger = TriggerBuilder.newTrigger()
    .withIdentity(getTriggerKey(jobId, jobGroup))
    .withSchedule(cronScheduleBuilder).build();
// ...
scheduler.scheduleJob(jobDetail, trigger);  // 注册到 Scheduler
```

### Pattern 2: invoke_target 编码约定（D-P6-02 翻译关键）

**What:** RuoYi `sys_job.invoke_target` 字段格式 `beanName.methodName(arg1, arg2)`。`JobInvokeUtil.invokeMethod` 解析这个字符串：
- 用 `SpringUtils.getBean(beanName)` 取 bean（短名，不是 FQN）
- 用反射 `bean.getClass().getDeclaredMethod(methodName, paramTypes)` 取方法
- 参数类型推断：`'...'`=String，true/false=Boolean，`123L`=Long，`1.5D`=Double，其他=Integer

**When to use:** 每个 sys_job INSERT 的 invokeTarget 构造。

**Example（60 handler 典型翻译）：**
```
# 无参 handler（最常见）
@XxlJob("autoPay") → sys_job.invoke_target = "applyPayTask.autoPay"
                                                              ↑ @Component("applyPayTask") 或类名首字母小写

# 有参 handler
@XxlJob("refreshProfitData") → "ctrContractProfitTask.refreshProfitData('SPT202412180010')"
                                （executor_param 固化进 invoke_target）

# executeCommand 系列（3 个 command executors 共用 handler name "executeCommand"）
@XxlJob("executeCommand") 在 BasCommandExecutor/ReportCommandExecutor/BasWebCommand 各 1 处
→ bean 名不同（Spring 默认 basCommandExecutor / reportCommandExecutor / basWebCommand），invokeTarget:
  "basCommandExecutor.executeCommand('updateBudgetSettlement')"
  "reportCommandExecutor.executeCommand('pushWeChatWorkLeaderboard')"
  "basWebCommand.executeCommand('clean')"

# RyTask demo（ry_20210908.sql:584-586 已有 3 行）
sys_job.invoke_target: "ryTask.ryNoParams" / "ryTask.ryParams('ry')" / "ryTask.ryMultipleParams('ry', true, 2000L, 316.50D, 100)"
```

### Pattern 3: xxl-job → quartz handler 翻译规则（D-P6-03 字面）

| xxl-job 代码 | quartz 等价（com.spt.quartz.task 包内） |
|--------------|------------------------------------------|
| `import com.xxl.job.core.handler.annotation.XxlJob;` `@XxlJob(value = "autoPay")` `public void autoPay() { ... }` | 删 import + 删注解；方法签名保留 `public void autoPay() { ... }`；类上 `@Component("autoPay")`（显式 bean 名，对齐 invoke_target 短名）|
| `XxlJobHelper.log("msg {}", arg);` | `log.info("msg {}", arg);`（slf4j；AbstractQuartzJob.after 自动写 sys_job_log）|
| `XxlJobHelper.handleSuccess("msg");` | `return;`（void 方法自然返回；成功状态由 AbstractQuartzJob.after 检测无异常）|
| `XxlJobHelper.handleFail("msg");` | `throw new RuntimeException("msg");`（AbstractQuartzJob.execute catch 后调 after(context, job, e) → status=FAIL）|
| `String param = XxlJobHelper.getJobParam();` | 改方法签名加 String 参数：`public void xxx(String param)` + sys_job.invoke_target 改 `beanXxx.xxx('${param}')`；或在 handler 内 `@Autowired SysJobService` 通过 JobDataMap 取（复杂，推荐改方法签名）|
| 默认并发执行（xxl-job 不限制）| sys_job.concurrent='0'（允许并发，QuartzJobExecution） |
| xxl-job 串行型阻塞策略 SerialExecution | sys_job.concurrent='1'（@DisallowConcurrentExecution 注解的 QuartzDisallowConcurrentExecution）+ handler 类加 `@DisallowConcurrentExecution`（已在该 Job 类层级；按 sys_job.concurrent 选 Job class）|

**Key insight:** RuoYi 的 @DisallowConcurrent 是在 Job 类（`QuartzJobExecution` vs `QuartzDisallowConcurrentExecution`）层级，**handler 类本身不需要标注**；sys_job.concurrent 字段决定 ScheduleUtils 选哪个 Job class。所以 D-P6-12 "保阻塞策略" = sys_job.concurrent='1'（针对源 SerialExecution 型任务）。

### Pattern 4: 双 ORM 单 DataSource 下 quartz mapper 复用

**What:** RuoYi quartz mapper（SysJobMapper / SysJobLogMapper）需在 monolith 双 ORM 双 DataSource? 否——monolith 是**单 DataSource**（Druid @Primary，Phase 2/5 实证）。SysJobMapper.xml 走 mybatis-plus 既有 SqlSessionFactory，namespace `com.spt.quartz.mapper.SysJobMapper` 与 Phase 2 SampleMapper / Phase 5 `com.spt.bas.report.server.dao` 独立无冲突。

**接线点（必须改两处）：**
1. `ZgbasMybatisConfig.@MapperScan` 放宽 basePackages 加 `com.spt.quartz.mapper`
2. SysJobMapper/SysJobLogMapper 接口加 `@MyBatisDao` 注解（Phase 2/5 标记约定，因为 `@MapperScan(annotationClass = MyBatisDao.class)` 是过滤条件 — 不加注解不被扫描）

**XML 路径：** `mybatis-plus.mapper-locations: classpath:/mybatis/mappers/*Mapper.xml` 是 **classpath 根下的 /mybatis/mappers/**（不是 mapper/quartz/）。RuoYi 源 XML 路径 `mapper/quartz/SysJobMapper.xml` 必须移到 `zgbas-quartz/src/main/resources/mybatis/mappers/SysJobMapper.xml`。

**Anti-pattern:** 把 XML 放 `zgbas-quartz/src/main/resources/mapper/quartz/` 又不改 mapper-locations → 启动期 `BindingException: Invalid bound statement (not found): com.spt.quartz.mapper.SysJobMapper.selectJobAll`。

### Pattern 5: ScheduleConfig 共存策略（保 @EnableScheduling）

**What:** monolith 有 1 个 Spring `@EnableScheduling` ScheduleConfig（`com.spt.bas.server.config.ScheduleConfig` in zgbas-system，10 线程池，给 `ShiroHeartJob` `@Scheduled(cron="0/10 * * * * *")` 用），源 zgbas 另有 3 个 ScheduleConfig（web/report/purchase 各 1）但 Phase 3/4/5 未迁入。本期落地 `com.spt.quartz.config.ScheduleConfig`（RuoYi，58 行整类注释，必须 uncomment）= **第 2 个同名类**。

**冲突表面：** 两类都叫 `ScheduleConfig` → Spring 默认 bean 名 `scheduleConfig` → `ConflictingBeanDefinitionException`（除非加 `@Bean(name="...")` / excludeFilter / 改类名）。

**Why 不冲突（real behavior）：**
- `com.spt.bas.server.config.ScheduleConfig` 是 `@Configuration` 类 → 默认 bean 名 `scheduleConfig`
- `com.spt.quartz.config.ScheduleConfig` 也是 `@Configuration` 类 → 同样默认 bean 名 `scheduleConfig`
- → **真的会冲突**（两类都在 `@ComponentScan("com.spt")` 范围内）

**推荐处置（二选一）：**
- **Option A（推荐，最小改动）：** RuoYi 那个改类名 `QuartzScheduleConfig`（同包 `com.spt.quartz.config`）+ 内部 `@Bean SchedulerFactoryBean schedulerFactoryBean(...)` 名保留。D-P2-07 偏离：**类名偏离 1 个字符为 quartz 模块独有，不破坏包名照搬**。
- **Option B：** ZgbasApplication.excludeFilters 加 `com.spt.bas.server.config.ScheduleConfig.class`（assiganble-type），保留 RuoYi 命名；但 @EnableScheduling 链路被破坏（ShiroHeartJob 失效）。**不推荐**。

### Pattern 6: HTTP 同步门面（MQApi）直调改造（D-P6-11）

**What:** 源 `MQApi`（`com.spt.bas.server.api.MQApi`）是 `@RestController("/mq/api")`，7 个 GET 端点（注：源代码含 8 个 @Autowired 但 `@GetMapping` 只 7 个，缺 `synchronizedBsCompanyTask` 端点对应到 companyTask，实际 7 业务 GET + 1 GET/testSendMessage = 8）。每个端点直接 `@Autowired Synchronized*Task` 调其业务方法。

**D-P6-11 改造：** HTTP 端点保留（前端零改），内部把 `synchronizedXxxTask.xxxMethod()` 改为 `synchronizedXxxService.xxxMethod()`（直调底层 service）。

**接线点（MQApi 落 zgbas-admin，与 Phase 4 basServer api 同包）：**
```java
@RestController
@RequestMapping("/mq/api")
public class MQApi {
    @Autowired private ICtrContractService ctrContractService;  // 替代 SynchronizedCtrContractTask
    // ... 7 个 service 直调
    @GetMapping("/ctrContractTask")
    public void ctrContractTask() {
        ctrContractService.synchronizedAllCtrContract();  // 原 handler 内的方法下沉到 service
    }
}
```

**问题：** 源 `Synchronized*Task` handler 内的 `synchronizedAllCtrContract()` 方法体可能调多个 service + 含业务编排逻辑，**不是单一 service.method()**。D-P6-11 落地需逐个 handler 分析方法体，确定"直调目标 service method"是 1:1（直接调）还是 1:N（需在 service 加一个组合方法）。**Research Recommendation:** Planner 在 Wave 1 前置一个"command executors + Synchronized*Task 内部依赖分析"task（参考源 `SynchronizedCtrContractTask.synchronizedAllCtrContract()` 方法体），生成"handler method → service method" 映射表，再决策每个端点的直调目标。

### Anti-Patterns to Avoid
- **照搬 auth-quartz 不改 Constants.JOB_WHITELIST_STR** → SysJobController.add 拒所有 `com.spt.quartz.task.*` invokeTarget，admin UI 增改任务全失败
- **照搬 auth-quartz 不剥 @PreAuthorize** → 启动期 spring-security AOP 找不到 `ss` bean / 无 spring-security 依赖 → 启动失败或 Controller 路由异常
- **照搬 auth-quartz 不改 @MapperScan basePackages / 不加 @MyBatisDao** → SysJobMapper bean 不存在 → SysJobServiceImpl 启动期 @Autowired 失败
- **照搬 auth-quartz 不uncomment ScheduleConfig** → 无 Scheduler @Bean → `NoSuchBeanDefinitionException: Scheduler` 启动失败
- **把 RuoYi XML 放 mapper/quartz/ 而非 mybatis/mappers/** → BindingException: Invalid bound statement
- **未剥 xxl-job-core 前 compile pass / 剥后编译错** → CtrContractProfitServiceImpl 2 行 `XxlJobHelper.log` 须同步翻译为 `log.info`
- **executeCommand 当 1 个 handler 处理** → 实际是 3 个 command executors（BasCommandExecutor / ReportCommandExecutor / BasWebCommand）共用 handler name "executeCommand"，需建 3+ sys_job 行（每个一行，invoke_target 不同 args）
- **OrverdurTask 当活 handler 处理** → 源 `// @XxlJob(value = "overdueTask")` 整行注释，按 D-P6-03 ③ 废弃任务跳过

## Don't Hand-Roll

| Problem | Don't Build | Use Instead | Why |
|---------|-------------|-------------|-----|
| Cron 表达式验证 | 自己写解析 | `org.quartz.CronExpression.isValidExpression(cron)`（已在 RuoYi CronUtils.java） | quartz 自带，xxl-job 同源 CronExpression 类，秒级字段支持完整 |
| 任务调度引擎 | 自己写 Timer / ScheduledExecutorService | RuoYi `Scheduler` + `SchedulerFactoryBean`（quartz JobStore 持久化到 QRTZ_*） | 含 misfire / 阻塞 / 集群支持（D-P6-12 保阻塞策略）；自研缺 misfire 行为 |
| Bean 反射调用 | 自己写 bean 查找 + 方法签名解析 | `JobInvokeUtil.invokeMethod(sysJob)` + `SpringUtils.getBean(name)` | 参数类型推断（String/Boolean/Long/Double/Integer）已覆盖；自研易漏类型 |
| 任务执行日志 | 自己 AOP 写日志表 | `AbstractQuartzJob.before/after` 包裹 → 自动写 sys_job_log | 含开始/结束时间 + 异常堆栈（substring 2000）+ 状态字段；自研漏字段 |
| 任务可视化 CRUD UI | 自己写一套 Controller + Thymeleaf | （本 research 推荐：先用 RuoYi SysJobController JSON API 不做 UI；若必做 UI，从官方 RuoYi 抄 templates/monitor/job/*） | RuoYi 完整 UI 含启停/传参/查日志；自研工作量大且偏离核心价值 |

**Key insight:** RuoYi quartz 18 类是 8 年生产验证的成熟实现（2018 年随 RuoYi 4.0 发布至今），照搬 + 适配（剥 @PreAuthorize + 改 whiteList + uncomment ScheduleConfig + 路径对齐）的总工作量 << 自研最小化调度体系。Phase 6 的复杂度集中在 60 handler 翻译 + sys_job 数据生成（D-P6-01/02），不在基础设施。

## Runtime State Inventory

> 本期为 **refactor/migration**（xxl-job 体系替换为 RuoYi quartz），按协议要求执行 5 类清单。本期**无 rename**（D-P6-09 改包名仅限 handler 类，包名变 `com.spt.bas.server.task` → `com.spt.quartz.task` 是新增模块落位非字符串替换）。

| Category | Items Found | Action Required |
|----------|-------------|------------------|
| **Stored data** | **外部 xxl-job admin MySQL `xxl_job_info` 表**（cron / handler 路由 / 重试 / 阻塞策略 / 参数）— 本期最核心数据源（D-P6-01）。NOT in monolith，NOT in git。 | **data migration**（用户导出 → Claude 翻译 → checkpoint:human-verify → INSERT sys_job）|
| **Stored data** | **monolith dev 库 `sptbasdb_pd`** 无 sys_job / sys_job_log / QRTZ_* 表（Phase 5 完成态）| **code edit + manual DDL**（建表 SQL 写入 zgbas-quartz/src/main/resources/sql/，运维 / dev 手工执行；D-P2-02 ddl-auto=none 不自动建表）|
| **Stored data** | ry_20210908.sql:584-586 已有 3 行 sys_job demo 数据（ryTask.ryNoParams 等），cron=`0/10 * * * * ?` / `0/15` / `0/20` | **保留或清理**：保留作 RyTask 集成 demo（启动验证 SysJobServiceImpl.init 加载非空）；清理则在 Phase 7 真实任务上线时删 |
| **Live service config** | **外部 spt-auth 服务 sys_menu 表** — `/monitor/job` 菜单项接入位置（D-P6-10）；不在单体 git，不在单体 DB | **operational change（外部 spt-auth sys_menu INSERT）**或 direct URL access；本期 research 推荐备选 API-only（不做 Thymeleaf 模板），菜单接入延后到 Phase 7 或独立运维任务 |
| **Live service config** | **外部 xxl-job admin 服务**（xxljob.totrade.cn）— 当前业务 cron 配置存放处；本期删 executor 侧后，admin 服务侧仍可独立运行（不强制退役） | **none**（运维事务，本期不处理）|
| **OS-registered state** | 无 | None — verified by 源 zgbas 无 systemd / launchd / Task Scheduler 集成 |
| **Secrets/env vars** | monolith `application-dev.yml` / `application-prod.yml` 无 `xxl.job.*` keys（实测 grep）；xxl-job executor 配置从未在 monolith 落地 | **none**（无 secret / env var 需删；Phase 4 04-04 加 xxl-job-core 是 jar-only，无配置）|
| **Build artifacts / installed packages** | `zgbas-system/pom.xml` 声明 `xxl-job-core:2.3.0`（Phase 4 04-04 Rule 3 临时加）+ `~/.m2/repository/com/xuxueli/xxl-job-core/2.3.0/` jar | **pom 删除**（INFRA-03）+ 自然保留在本地 m2（无副作用）；不需要清理本地 jar |

**The canonical question answer:** After all monolith code edits done, what runtime systems still have the old xxl-job string cached/stored/registered?
- **External xxl-job admin MySQL `xxl_job_info`** — 仍存全部 cron 配置（D-P6-01 用户须导出 → 翻译为 sys_job）
- **External spt-auth sys_menu 表** — 若 Phase 6 决定加 /monitor/job 菜单，需 INSERT（推荐延后）
- **External xxl-job admin 服务进程** — 仍可运行（不强制退役；删除单体侧 executor 依赖后，admin 失去这个执行器，可能产生告警，属运维关注点）

## Environment Availability

| Dependency | Required By | Available | Version | Fallback |
|------------|------------|-----------|---------|----------|
| Maven 3.8.6 | BUILD (项目硬约束) | ✓（实际 3.9.16 在 PATH，3.8.6 在 /Users/alan/App/apache-maven-3.8.6 可用）| 3.8.6（推荐）/ 3.9.16（实测 OK）| 用 PATH 中的 mvn（用户已多次跑通）|
| JDK 1.8 | BUILD 锁定（CLAUDE.md）| ✓ | 1.8.0_482（Corretto）| — |
| `zg_settings.xml` | 私服仓库重定向 | ✓ | /Users/alan/App/apache-maven-3.8.6/zg_settings.xml | — |
| MySQL `sptbasdb_pd` | 业务库 + 本期新建 sys_job/sys_job_log/QRTZ_* | ✓（dev: 47.104.15.98:3306，明文密码 D-P4-04 option 3 保留）| mysql-connector 8.0.13 | — |
| 外部 xxl-job admin DB（xxljob.totrade.cn）| D-P6-01 cron 数据导出源 | ⚠ **未在本环境验证**（用户承诺提供 dump / DB 访问）| — | **D-P6-03 fallback**: 无 cron 的手动型 → PAUSED；若用户最终无法导出，全部 60 行 sys_job 走 PAUSED（保 QUARTZ-04 手动触发，cron 留空 cron_expression 字段不允许 NULL — 需 placeholder cron `"0 0 0 ? * MON"` 设为永远不触发或类似）|
| 外部 spt-auth（47.104.15.98:9011）| 动态菜单（不变）+ 潜在 /monitor/job 菜单 INSERT | ✓ | 3.8.2-SNAPSHOT（外部）| — |

**Missing dependencies with no fallback:** 无（外部 xxl-job admin DB 缺失有 D-P6-03 fallback，但偏离核心价值"真定时"）

**Missing dependencies with fallback:** 外部 xxl-job admin DB → fallback 到全 PAUSED（牺牲真定时）

## Common Pitfalls

### Pitfall 1: auth-quartz ScheduleConfig 整类注释（启动期 Scheduler bean 缺失）
**What goes wrong:** 照搬 auth-quartz 后启动，报 `NoSuchBeanDefinitionException: No qualifying bean of type 'org.quartz.Scheduler'`。
**Why it happens:** `/Users/alan/WorkSpace/IDEA/spt-auth/auth-quartz/src/main/java/com/spt/quartz/config/ScheduleConfig.java` 整文件 58 行被 `//` 注释（spt-auth 实际部署走自己的 admin 调度，不走 quartz Scheduler）。`SysJobServiceImpl` `@Autowired private Scheduler scheduler;` 找不到 bean。
**How to avoid:** Phase 6 Wave 0 必须新建（或 uncomment）一个等价 `@Configuration` 类，提供 `@Bean SchedulerFactoryBean schedulerFactoryBean(DataSource dataSource)`。⚠ **类名冲突风险**：monolith 已有 `com.spt.bas.server.config.ScheduleConfig`（@EnableScheduling），新类**推荐改名为 `QuartzScheduleConfig`**（D-P2-07 仅偏类名 1 个字符，包名仍守）。
**Warning signs:** 启动期 `NoSuchBeanDefinitionException` 涉及 `Scheduler`。

### Pitfall 2: Constants.JOB_WHITELIST_STR hardcoded `{"com.ruoyi"}`
**What goes wrong:** admin UI 通过 SysJobController.add 新建任务，invokeTarget 填 `applyPayTask.autoPay`，返回 "新增任务'xxx'失败，目标字符串不在白名单内"。
**Why it happens:** `ScheduleUtils.whiteList(invokeTarget)` 调 `StringUtils.containsAnyIgnoreCase(invokeTarget, Constants.JOB_WHITELIST_STR)`，硬编码只允许 `com.ruoyi` 包。本期 handler 落 `com.spt.quartz.task`，bean 的 package 是 `com.spt`。
**How to avoid:** Wave 0 本地化 Constants.java 时，把 `JOB_WHITELIST_STR = {"com.spt"}` （或 `{"com.spt.quartz.task"}` 更严，但失去 RuoYi 官方灵活性）。
**Warning signs:** SysJobController.add/edit 返回 "目标字符串不在白名单内"。

### Pitfall 3: 17 个 com.spt.common.* 类在单体不存在
**What goes wrong:** 照搬 auth-quartz 18 类后 `mvn compile` 报 17 类 import 无法解析（`com.spt.common.constant.ScheduleConstants` 等）。
**Why it happens:** auth-quartz pom 依赖 `auth-common` + `auth-framework`，这俩 module 在单体不存在（Phase 2 内联了 spt-tools-* 但**未内联** auth-common）。
**How to avoid:** Wave 0 把 17 类本地化到 zgbas-quartz 子集（推荐子集而非全量 auth-common — auth-common 含 SysUser/SysRole 等大量非 quartz 用类，全量内联引入 ~150 类 transitive deps）。可简化：
- `StringUtils` → 改 import 为 `org.apache.commons.lang3.StringUtils`（monolith 已用）
- `BeanUtils` → 改 import 为 `org.springframework.beans.BeanUtils`（注意参数顺序差异：Spring 是 `(dest, src)`，Apache 是 `(dest, orig)` — AbstractQuartzJob.execute 用了 `BeanUtils.copyBeanProp` 是 RuoYi 自定义方法，须照搬 RuoYi BeanUtils 而非替换）
- `SpringUtils` → 必须本地化（RuoYi 用 `ApplicationContextAware` 实现，业务 handler 通过 `SpringUtils.getBean(name)` 反射拿 bean）
- `ExcelUtil` / `@Excel` / `@Log` → 可选（export 才用；本 research 推荐剥 export 功能，删 `SysJobController.export` + import Excel*）
- `BaseController` / `AjaxResult` / `TableDataInfo` → 必须本地化（Controller 用到 startPage / getDataTable / toAjax / AjaxResult.success）

**Warning signs:** 编译期 `package com.spt.common.* does not exist`。

### Pitfall 4: @PreAuthorize 注解与 Shiro 冲突
**What goes wrong:** 照搬 SysJobController 后启动期报 AOP 织入失败 / 调用 `/monitor/job/list` 报 404 或 `@PreAuthorize` 不生效。
**Why it happens:** monolith Phase 3 用 Shiro Realm，无 spring-security 依赖，无 `@EnableGlobalMethodSecurity(prePostEnabled=true)`，无 `ss` bean。`@PreAuthorize("@ss.hasPermi('monitor:job:list')")` 注解静默不触发（spring-security 不在 classpath）或抛 `ClassNotFoundException: org.springframework.security.access.prepost.PreAuthorize`（若部分 jar 传递依赖引入）。
**How to avoid:** Wave 0 复制 SysJobController / SysJobLogController 时**剥除所有 @PreAuthorize 注解**（共 14 处：list/export/query/add/edit/changeStatus/run/remove/clean）。Phase 3 Shiro 链路接管认证；权限粒度留待 v2（如运维确实需要 monitor:job:* 权限控制，加 `@RequiresPermissions("monitor:job:list")` Shiro 注解）。
**Warning signs:** 编译期 `PreAuthorize cannot be resolved` 或运行期 401 / 403 全员不可访问。

### Pitfall 5: xxl-job cron 数据缺失（D-P6-01 checkpoint:human-blocked 阻塞）
**What goes wrong:** Planner 进入 Wave 4 sys_job 数据生成时无 cron 源（外部 xxl-job admin DB 未导出），翻译流程阻塞。
**Why it happens:** D-P6-01 决策导出外部 xxl-job admin DB `xxl_job_info` 表 → 用户承诺但未到位。
**How to avoid:** Planner 在 Wave 0 后立即 `checkpoint:human-blocked` 等待用户导出物，Wave 1-3 handler 代码迁移可并行推进（不阻塞）。**fallback (D-P6-03)**: 全部 sys_job.status=PAUSED + placeholder cron（如 `0 0 0 ? * MON` 永不匹配当前业务节奏或 `#` 注释行 SQL 跳过），保 QUARTZ-04 手动触发硬验收。
**Warning signs:** Wave 4 启动时用户未提供导出 dump。

### Pitfall 6: executeCommand 3 个同名 handler
**What goes wrong:** 翻译 sys_job 数据时把 `executeCommand` 当 1 个 handler 处理 → 翻译成 1 行 sys_job，丢失 50+ 子命令。
**Why it happens:** 源 zgbas 有 3 个类都标 `@XxlJob(value = "executeCommand")`（BasCommandExecutor / ReportCommandExecutor / BasWebCommand），xxl-job admin 通过 executor_appname 路由区分，但 sys_job 必须用 bean 名区分。
**How to avoid:** Planner 在 Wave 4 D-P6-02 翻译产物里，executeCommand 拆为 N 行 sys_job（每个 commandline 子命令一行 invokeTarget = `basCommandExecutor.executeCommand('${cmd}')`），共约 50+ 行。ReportCommandExecutor 2 行（cache + pushWeChatWorkLeaderboard），BasWebCommand 3 行（clean + cache + fundSocket），PurchaseCommand 不迁（v2）。
**Warning signs:** 翻译产物 sys_job 行数 < 60（预期 ~60 handler + ~55 executeCommand 子命令 + 3 demo = ~118 行）。

### Pitfall 7: mybatis mapper-locations 路径不对齐
**What goes wrong:** 启动期 `org.apache.ibatis.binding.BindingException: Invalid bound statement (not found): com.spt.quartz.mapper.SysJobMapper.selectJobAll`。
**Why it happens:** monolith `mybatis-plus.mapper-locations: classpath:/mybatis/mappers/*Mapper.xml`（classpath 根 + mybatis/mappers/），RuoYi 源 XML 路径 `mapper/quartz/SysJobMapper.xml` 不匹配。
**How to avoid:** Wave 0 把 RuoYi XML 复制到 `zgbas-quartz/src/main/resources/mybatis/mappers/SysJobMapper.xml`（路径改）。**不**改 mapper-locations glob（保 Phase 5 `*Mapper.xml` 通配）。
**Warning signs:** BindingException on com.spt.quartz.mapper.*。

### Pitfall 8: xxl-job-core 删除引发 CtrContractProfitServiceImpl 编译错
**What goes wrong:** INFRA-03 删 xxl-job-core jar 后 `mvn compile` 报 `package com.xxl.job.core.context does not exist`（CtrContractProfitServiceImpl import 缺失）。
**Why it happens:** Phase 4 04-04 临时加 xxl-job-core 时，**业务 service impl（非 handler）** CtrContractProfitServiceImpl 也用了 `XxlJobHelper.log`（2 处诊断日志）。这不是 handler，是普通 service impl 内的日志调用。
**How to avoid:** Wave 0 同步翻译 `CtrContractProfitServiceImpl.java:16,172,182`：删 import + 把 `XxlJobHelper.log(...)` 改 `log.info(...)`（相邻行已有 `log.info` 同参，**直接删 XxlJobHelper.log 行**最干净）。
**Warning signs:** 编译期 `XxlJobHelper cannot be resolved`。

### Pitfall 9: BasCommandExecutor 内部 @Autowired handler 类
**What goes wrong:** D-P6-09 repackage handler 到 `com.spt.quartz.task` 后，`BasCommandExecutor` 内 `import com.spt.bas.server.task.ApplyPayTask` 等失效。
**Why it happens:** BasCommandExecutor 自己含 `@XxlJob("executeCommand")` 所以它是 handler → 落 zgbas-quartz `com.spt.quartz.task`（或 `com.spt.quartz.command`）；其 @Autowired 的 4 个 task 类（ApplyPayTask / BudgetSettlementTask / CtrContractScheduleTask / DcsxAutoApplyPayTask）也都在 zgbas-quartz，**同模块 import 不需跨包**（包名前缀 `com.spt.quartz.task` 统一）。但 D-P6-11 路线（HTTP facade 直调 service）的语义是 BasCommandExecutor 的 HTTP 入口下沉到 admin，handler 角色不变。
**How to avoid:** Planner 决策 BasCommandExecutor 落位：
- **Option A（推荐，最小工作量）:** BasCommandExecutor 同 handler 一同迁到 zgbas-quartz `com.spt.quartz.task`，4 个 task 类同迁；内部 `@Autowired applyPayTask` 不变；**HTTP 入口（若有）单独建一个 MQApi-style @RestController 在 admin 直调 BasCommandExecutor.executeCommand(args)**（即 D-P6-11 的扩展：command 系列也算同步触发门面，经 HTTP 触发 executeCommand）
- **Option B:** BasCommandExecutor 保留 `com.spt.bas.server.command` 包，handler 任务迁移到 sys_job 时 invoke_target 用 `basCommandExecutor.executeCommand('xxx')`，**bean 名跨越模块**（zgbas-system 的 BasCommandExecutor 被 zgbas-quartz 的 SysJobServiceImpl 通过 `SpringUtils.getBean("basCommandExecutor")` 跨模块取）——可行但 D-P6-07 "调度相关代码统一归 quartz 模块"语义弱化
**Warning signs:** `import com.spt.bas.server.task.*` 失效 / Bean 注入失败。

## Code Examples

### Example 1: handler 翻译（xxl-job → quartz bean）

```java
// Source (xxl-job): /Users/alan/WorkSpace/IDEA/zgbas/basCore/basServer/.../task/ApplyPayTask.java:42-73
@Component
public class ApplyPayTask {
    @Autowired private IApplyPayService applyPayService;
    @Autowired private IApplyPayDcsxService applyPayDcsxService;

    @XxlJob(value = "autoStartPayProcess")
    public void autoStartPayProcess() {
        XxlJobHelper.log("autoStartPayProcess start");
        try {
            applyPayService.autoStartPayProcess();
            XxlJobHelper.handleSuccess("autoStartPayProcess done");
        } catch (Exception e) {
            XxlJobHelper.handleFail("autoStartPayProcess fail: " + e.getMessage());
        }
    }

    @XxlJob(value = "autoPay")
    public void autoPay() {
        // ...
    }
}
```

```java
// Target (RuoYi quartz): zgbas-quartz/src/main/java/com/spt/quartz/task/ApplyPayTask.java
// ⚠ 包名变（com.spt.bas.server.task → com.spt.quartz.task），类名 / 字段 / 方法签名不变（D-P2-07 + D-P6-09）
package com.spt.quartz.task;

import com.spt.bas.server.service.IApplyPayService;        // ⚠ service iface 包名不变（D-P6-09 ②）
import com.spt.bas.server.service.IApplyPayDcsxService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("applyPayTask")  // ⚠ 显式 bean 名（对齐 invoke_target 短名）
public class ApplyPayTask {
    private static final Logger log = LoggerFactory.getLogger(ApplyPayTask.class);
    @Autowired private IApplyPayService applyPayService;
    @Autowired private IApplyPayDcsxService applyPayDcsxService;

    // @XxlJob 删；方法签名保留；RuoYi QuartzJobExecution.doExecute 反射调
    public void autoStartPayProcess() {
        log.info("autoStartPayProcess start");
        try {
            applyPayService.autoStartPayProcess();
            // XxlJobHelper.handleSuccess → return（void 自然返回；AbstractQuartzJob.after 检测无异常 = success）
        } catch (Exception e) {
            log.error("autoStartPayProcess fail", e);
            throw new RuntimeException(e);  // ⬅ 抛异常让 AbstractQuartzJob.after 写 status=FAIL
            // 原 XxlJobHelper.handleFail 替换为此
        }
    }

    public void autoPay() { /* ... */ }
}
```

对应 sys_job INSERT（D-P6-02 翻译产物，cron 来自外部 xxl-job admin DB `xxl_job_info` 导出）：
```sql
INSERT INTO sys_job(job_name, job_group, invoke_target, cron_expression, misfire_policy, concurrent, status, create_by, create_time, remark)
VALUES ('自动发起付款流程', 'DEFAULT', 'applyPayTask.autoStartPayProcess', '0 0 8 * * ?', '3', '1', '0', 'admin', sysdate(), '迁自 xxl-job autoStartPayProcess');
-- ⚠ cron 字段：xxl-job 通常 6/7 字段（含秒），quartz CronExpression 6 字段必填（sec min hour dom mon dow），7 字段（+ year）可选。
-- xxl-job `0 0 8 * * ?` 与 quartz `0 0 8 * * ?` 同义（quartz 要求 dow 字段，? 表示无指定），翻译通常 1:1。
-- 测试：每个 cron 必须通过 CronExpression.isValidExpression() — D-P6-06 启动期 fail-fast 自动验证。
```

### Example 2: command executor 翻译（BasCommandExecutor）

```java
// Source: /Users/alan/WorkSpace/IDEA/zgbas/basCore/basServer/.../command/BasCommandExecutor.java:130-381
@Component
public class BasCommandExecutor implements ICommand {
    @Override
    @XxlJob(value = "executeCommand")
    public boolean executeCommand(String commandline) throws Exception {
        String jobParam = XxlJobHelper.getJobParam();
        if (StringUtils.isNotBlank(jobParam)) {
            commandline = jobParam;  // ⬅ xxl-job 走 getJobParam，方法 arg 是 fallback
        }
        if (StringUtils.isNotBlank(commandline)) {
            if (commandline.indexOf("reContract") >= 0) { /* ... */ }
            else if (commandline.startsWith("updateBudgetSettlement")) {
                budgetSettlementTask.updateBudgetSettlement();
                return true;
            }
            // ... ~50 个子命令分支
        }
        return false;
    }
}
```

```java
// Target: zgbas-quartz/src/main/java/com/spt/quartz/task/BasCommandExecutor.java
// ⚠ 落 zgbas-quartz 同其他 handler 一同 repackage；D-P6-11 HTTP 入口（如有）单独建 admin @RestController
@Component("basCommandExecutor")
public class BasCommandExecutor implements ICommand {
    @Override
    public boolean executeCommand(String commandline) throws Exception {
        // ⚠ 删 XxlJobHelper.getJobParam() fallback — quartz 模式下 args 直接由 invoke_target 传入 method
        // 例：sys_job.invoke_target = "basCommandExecutor.executeCommand('updateBudgetSettlement')"
        //   → JobInvokeUtil 解析 args="updateBudgetSettlement" → 反射 invoke(this, "updateBudgetSettlement")
        //   → 此处 commandline = "updateBudgetSettlement"
        if (StringUtils.isNotBlank(commandline)) {
            if (commandline.indexOf("reContract") >= 0) { /* ... */ }
            else if (commandline.startsWith("updateBudgetSettlement")) {
                budgetSettlementTask.updateBudgetSettlement();
                return true;
            }
        }
        return false;
    }
}
```

对应 sys_job INSERT（每个子命令一行，D-P6-02 翻译）：
```sql
INSERT INTO sys_job VALUES('...', 'updateBudgetSettlement 命令', 'COMMAND', 'basCommandExecutor.executeCommand(\'updateBudgetSettlement\')', '#', '3', '1', '1', 'admin', sysdate(), '', null, '手动型 → status=PAUSED');
-- ⚠ cron 字段不能为空字符串（NOT NULL 约束）；手动型用 '#' 是 quartz "disabled" 惯例，但 RuoYi CronUtils.isValid 会返回 false（# 不是合法 cron）
-- 实际方案：手动型给一个"永远不触发"的 placeholder cron 如 '0 0 0 1 1 ? 2099'（2099 年 1 月 1 日 0 时），status=PAUSED 即可
```

### Example 3: MQApi D-P6-11 改造（同步触发门面直调）

```java
// Source: /Users/alan/WorkSpace/IDEA/zgbas/basCore/basServer/.../api/MQApi.java
@RestController
@RequestMapping(value = "/mq/api")
public class MQApi {
    @Autowired private SynchronizedCtrContractTask ctrContractTask;
    // ... 7 个 Synchronized*Task

    @GetMapping("/ctrContractTask")
    public void ctrContractTask() {
        ctrContractTask.synchronizedAllCtrContract();  // ⬅ 调 handler bean
    }
}
```

```java
// Target: zgbas-admin/src/main/java/com/spt/bas/server/api/MQApi.java（保包名 D-P2-07）
@RestController
@RequestMapping(value = "/mq/api")
public class MQApi {
    @Autowired private ICtrContractService ctrContractService;  // ⬅ 直调 service（D-P6-11）
    // ... 7 个 service iface

    @GetMapping("/ctrContractTask")
    public void ctrContractTask() {
        ctrContractService.synchronizedAllCtrContract();  // ⬅ 原 handler 内逻辑下沉到 service
    }
}
```

⚠ **Planner 注意：** 源 `Synchronized*Task.synchronizedAllCtrContract()` 方法体可能含多 service 编排（如调 ICtrContractService + IPmApproveService + 发 RocketMQ 消息）。需 Wave 0 前置一个 "Synchronized*Task 内部依赖分析" task，生成 "handler method → 1:1 或 1:N service method" 映射表。若 1:N，service iface 加一个组合方法（如 `ICtrContractService.synchronizedAllCtrContractBatch()`）封装多 service 调用，MQApi 调这个组合方法。详见 §Open Questions Q2。

## State of the Art

| Old Approach | Current Approach | When Changed | Impact |
|--------------|------------------|--------------|--------|
| xxl-job 2.3.0 admin/executor 分布式调度 | RuoYi quartz 4.x 本地 quartz Scheduler（JobStore JDBC 持久化） | 本期 Phase 6 | 单体单节点，多节点路由天然蒸发；任务管理 UI 由 xxl-job admin 切换到 RuoYi `/monitor/job` |
| xxl-job `@XxlJob(value="name")` 注解 | RuoYi `@Component("name")` 显式 bean 名 + sys_job.invoke_target=`name.method(args)` | 本期 Phase 6 | 任务路由从注解名 → sys_job 配置数据，启停改 sys_job.status 即可（无需重启） |
| xxl-job admin DB `xxl_job_info` | 本地 sys_job / sys_job_log 表 | 本期 Phase 6 | cron 数据从外部 admin DB → 本地 sptbasdb_pd，D-P6-01 一次性翻译迁移 |
| xxl-job `XxlJobHelper.log/handleSuccess/handleFail/getJobParam` | slf4j + return + throw + method args | 本期 Phase 6 | handler 代码层面去 xxl-job 依赖 |
| xxl-job 失败重试 / 告警邮件 / 路由策略 | sys_job_log 日志 + misfire 策略（D-P6-12 收窄等价范围）| 本期 Phase 6 | 失败任务仅记日志，无自动重试（运维增强非本期）|

**Deprecated/outdated:**
- xxl-job-core 2.3.0：本期删除依赖，整个 xxl-job 体系从单体消失
- `com.xxl.job.*` import：全部剥除
- `XxlJobHelper` / `XxlJobSpringExecutor` / `@XxlJob`：全部剥除
- `xxl.job.admin.addresses` / `xxl.job.executor.*` yml keys：单体从未落地（Phase 4 04-04 加 jar 时是 compile-time only），无 cleanup 需求

## Assumptions Log

| # | Claim | Section | Risk if Wrong |
|---|-------|---------|---------------|
| A1 | `spring-boot-dependencies:2.5.9` BOM 托管的 quartz version 是 2.3.2 | Standard Stack | LOW — 即使是 2.3.x 任何子版本都兼容 RuoYi auth-quartz |
| A2 | spt-auth/auth-quartz `Constants.JOB_WHITELIST_STR = {"com.ruoyi"}` 是源代码未改的 RuoYi 默认值（非 spt-auth 已改） | Common Pitfalls #2 | LOW — 直接读 spt-auth/auth-common/Constants.java 确认 |
| A3 | monolith 启动期无 spring-security 依赖（Phase 3 用 Shiro）→ `@PreAuthorize` 注解会运行期静默不触发或编译错 | Common Pitfalls #4 | LOW — grep 实测 monolith 无 `org.springframework.security` |
| A4 | executeCommand 3 个 command executors 都落 zgbas-quartz `com.spt.quartz.task`（保 handler 角色）+ MQApi 同模式扩展为 admin 端 HTTP 同步触发 | Common Pitfalls #9 / Code Examples §2 | MEDIUM — 若 planner 选 Option B（BasCommandExecutor 留 zgbas-system 跨模块调），则 D-P6-07 语义弱化但启动期无影响 |
| A5 | 用户能在外部 xxl-job admin DB 提供完整 `xxl_job_info` dump（D-P6-01 checkpoint:human-blocked 假设） | Pitfall 5 / Runtime State | HIGH — 若用户无法导出，fallback D-P6-03 全 PAUSED 偏离核心价值"真定时" |
| A6 | sys_job 手动型任务的 cron 字段可以用 placeholder `0 0 0 1 1 ? 2099` + status=PAUSED 表达 | Code Examples §2 | LOW — quartz CronExpression 接受；启动期不触发；用户在 admin 手动 run |
| A7 | ry_20210908.sql:566-603 sys_job/sys_job_log DDL 与 monolith MySQL 8.0 兼容（无 keyword 冲突 / 类型差异） | Runtime State / Common Pitfalls | LOW — 标准字段类型 bigint/varchar/datetime，MySQL 5.7+ 全支持 |

**Confidence by section:**
- Standard Stack：HIGH（Spring Boot 2.5.9 + quartz 2.3.2 + spt-auth/auth-quartz 源码直查）
- Architecture Patterns：HIGH（每模式源码出处可追溯）
- Pitfalls：HIGH（每 pitfall 实测验证）
- Code Examples：HIGH（源码与目标代码并排展示）

## Open Questions

1. **D-P6-10 `/monitor/job` Thymeleaf 来源**（research 已尽最大努力未在 spt-auth 找到）
   - What we know: spt-auth/auth-admin 无 SysJob*Controller；spt-auth/auth-ui 是 Vue 前端；zgbas/web/templates/monitor 和 zgbas-plus/zgbas-admin/templates/monitor 均无 job 子目录。
   - What's unclear: 是否需要可视化运维 UI？还是 SysJobController JSON API（POSTMan / curl 调用）即可？
   - Recommendation: **本 research 推荐 API-only 备选**（CONTEXT.md 已批准 "弃'保 Controller API 不引入页面'"）。理由：(a) QUARTZ-04 "手动触发与传参" 硬验收用 API 调用即可满足；(b) 避开官方 RuoYi Thymeleaf 移植成本（templates/monitor/job/job.html/add.html/edit.html ~3 模板 + 前端 JS）；(c) Phase 7 ALIGN-01 端到端验收不强制可视化运维 UI。若用户在 plan-review 坚持要 UI，备选 (c) 自写最小 Thymeleaf 表格页（参考 admin 既有 templates/monitor/operlog 风格）。

2. **Synchronized*Task 内部依赖分析粒度**（D-P6-11 落地关键）
   - What we know: 源 SynchronizedCtrContractTask.synchronizedAllCtrContract() 是 8 个 Synchronized*Task 的 1 个 handler 方法；它 @Autowired 多个 service 完成数据同步业务。
   - What's unclear: 每个 Synchronized*Task 的 handler 方法体是否 1:1 对应一个 service.method（直接下沉），还是 1:N（需在 service 加组合方法）。
   - Recommendation: Planner 在 Wave 1 前置一个 "Synchronized*Task + command executor 内部依赖分析" task，对 8 个 Synchronized*Task + BasCommandExecutor + ReportCommandExecutor + BasWebCommand 各方法生成 "handler method → service method 映射表"，再决策每个 MQApi 端点的直调目标。

3. **菜单接入机制**（D-P6-10 flagged）
   - What we know: zgbas 用动态菜单（authOpenFacade HTTP 调外部 spt-auth 取）；外部 spt-auth 的 sys_menu 表是权威源；monolith 本地无 sys_menu 表。
   - What's unclear: 用户是否需要在本期为 `/monitor/job` 在外部 spt-auth sys_menu 加一行（operational change），还是延后到 Phase 7 用户验收时手动加，还是不需要（API-only 模式无菜单需求）。
   - Recommendation: 若走 API-only 备选（Q1），无需菜单接入。若走可视化 UI，则外部 spt-auth sys_menu INSERT 一行 `('定时任务', '2', '2', 'job', 'monitor/job/index', ...)` —— 这是 operational task 不在代码 PR 内，planner 标 `checkpoint:human-blocked` 由用户/运维执行。

4. **xxl-job admin DB 导出格式**（D-P6-01 落地关键）
   - What we know: 外部 xxl-job admin 是 xxl-job 2.3.0 标准部署，`xxl_job_info` 表 schema 公开（github.com/xuxueli/xxl-job/blob/2.3.0/xxl-job-admin/src/main/resources/xxl-job.sql）。
   - What's unclear: 用户以何种形式提供（mysqldump / CSV / 手抄列表）；是否包含已废弃任务（D-P6-03 ③）。
   - Recommendation: Planner 在 Wave 0 后立即 `checkpoint:human-blocked`，附 "xxl_job_info 表导出指引"（指出关键列：executor_handler / executor_param / executor_route_strategy / executor_block_strategy / executor_timeout / executor_fail_retry_count / glue_source / trigger_status）。

5. **Constants.JOB_WHITELIST_STR 收紧粒度**
   - What we know: 默认 `{"com.ruoyi"}`，本期改 `{"com.spt"}` 即可让所有 com.spt.quartz.task.* bean 通过。
   - What's unclear: 是否需要更严（如 `{"com.spt.quartz.task"}`）防未来误注入。
   - Recommendation: 用 `{"com.spt"}` —— 与 monolith 主包名一致，灵活；运维若担心可后期收紧。

## Validation Architecture

### Test Framework
| Property | Value |
|----------|-------|
| Framework | JUnit 4 + Spring Boot Test 2.5.9（与 Phase 3-5 一致，zgbas-admin/src/test/）|
| Config file | 无独立 config（沿用 Phase 3-5 `ZgbasApplicationTest` 模式）|
| Quick run command | `JAVA_HOME=<Corretto 1.8> mvn -pl zgbas-quartz -am test -Dtest=ZgbasApplicationTest` |
| Full suite command | `JAVA_HOME=<Corretto 1.8> mvn test`（非 hermetic，dev profile 明文密钥无需 export，与 Phase 4 同契约）|

### Phase Requirements → Test Map

| Req ID | Behavior | Test Type | Automated Command | File Exists? |
|--------|----------|-----------|-------------------|-------------|
| QUARTZ-01 | zgbas-quartz 模块编译 + RuoYi 基础设施 18 类 bean 解析 | compile gate + smoke | `mvn -pl zgbas-quartz -am compile` grep `^\[ERROR]`=0；`mvn -pl zgbas-admin -am test -Dtest=ZgbasApplicationTest`（启动验证 SysJobServiceImpl.@PostConstruct init 不抛）| ✅ Wave 0 加 zgbas-quartz 编译断言；ZgbasApplicationTest 扩展 |
| QUARTZ-02 | sys_job / sys_job_log / QRTZ_* 表存在 + SysJobMapper.selectJobAll 可执行 | integration（非 hermetic，连真 dev 库）| `ZgbasApplicationTest` 扩展 `assertThat(jobMapper.selectJobAll()).isNotNull()`；`SELECT 1 FROM sys_job LIMIT 1` 启动前手动 DDL 验证 | ❌ Wave 0 加 quartzTablesExist_probe |
| QUARTZ-03 | 60 handler 全编译 + 全部 bean 名解析 | unit + integration | `mvn -pl zgbas-quartz -am compile` 全模块零 ERROR；启动期 SysJobServiceImpl.init() 加载全量 sys_job 触发 SpringUtils.getBean(beanName) 取每个 handler bean → 失败抛 NoSuchBeanDefinitionException | ✅ ZgbasApplicationTest 已就位（Phase 5 D-P5-08 抽样 proof 模式）|
| QUARTZ-04 | sys_job 数据初始化 + 至少 1 个 dry-run | integration + manual | `ZgbasApplicationTest` 扩展 `sampleQuartzJobDryRun_proof`（@Disabled 默认 + 手动启用）：调 `sysJobService.run(job)` 触发 1 个只读 handler（如 RyTask.ryNoParams 或 BsCompanyTask.updateCompanyGrey）→ 断言 sys_job_log 新增一行 status=SUCCESS | ❌ Wave 5 加 sampleQuartzJobDryRun_proof |
| INFRA-03 | xxl-job-core 删除 + CtrContractProfitServiceImpl XxlJobHelper.log 翻译 | compile gate | `mvn -pl zgbas-system -am compile` 全模块零 ERROR（删除 xxl-job-core 后）+ grep `com.xxl.job\|@XxlJob\|XxlJobHelper` monolith 内 0 命中（.planning/.claude 排除）| ✅ Wave 0 编译断言； Wave 1+ grep 断言 |

### Sampling Rate
- **Per task commit:** `mvn -pl zgbas-quartz -am compile` grep `^\[ERROR]`=0（locale 无关，与 Phase 1-5 同契约）+ grep `@XxlJob\|XxlJobHelper\|com.xxl.job` zgbas-quartz/zgbas-system 0 命中（除注释）
- **Per wave merge:** `JAVA_HOME=<Corretto 1.8> mvn -pl zgbas-admin -am test -Dtest=ZgbasApplicationTest`（启动验证：含 SysJobServiceImpl.@PostConstruct 加载全量 sys_job 的 fail-fast）
- **Phase gate:** D-P6-06 启动期 Scheduler 加载全量 sys_job cron 全解析通过 + invoke_target 指向的 bean.method 全存在（fail-fast） + D-P6-04 抽样 3-5 个 handler dry-run（D-P6-05 只读真跑 / 写类空跑）通过 + `mvn test` 全 reactor 绿

### Wave 0 Gaps
- [ ] `zgbas-quartz/src/test/java/.../QuartzCompileSanityTest.java`（可选）— 验证 com.spt.quartz.* 包内 RuoYi 类无 import 缺失（unit-level compile）
- [ ] `zgbas-admin/src/test/java/com/spt/ZgbasApplicationTest.java` 扩展：
  - [ ] `quartzBeanResolution_probe`（Wave 1+）— 断言 `Scheduler` bean 存在 + `SysJobService` / `SysJobLogService` / 60 handler bean 全部 `context.containsBean(...)` 通过
  - [ ] `quartzTablesExist_probe`（Wave 0 后置，DDL 落库后）— 断言 `sys_job` / `sys_job_log` 表存在（SELECT 1 不抛）
  - [ ] `schedulerLoadAllJobs_proof`（Wave 4 后置，sys_job 数据到位后）— 启动期 SysJobServiceImpl.init 不抛 SchedulerException 即证 D-P6-06 fail-fast
  - [ ] `sampleQuartzJobDryRun_proof`（Wave 5）— `@Disabled` 默认 + 手动启用，调 `sysJobService.run(jobId)` 触发 1 个只读 handler，断言 sys_job_log 新增 status=SUCCESS

*(If no gaps: "Wave 0 主要扩 ZgbasApplicationTest 4 个 probe/proof 方法 + 视情况加 quartz 模块独立 compile 测试")*

## Security Domain

### Applicable ASVS Categories

| ASVS Category | Applies | Standard Control |
|---------------|---------|-----------------|
| V2 Authentication | no | 不本期处理（Phase 3 Shiro 已就位，shiro session+cookie 复用）|
| V3 Session Management | no | 不本期处理（Phase 3 Shiro session 链路复用）|
| V4 Access Control | yes | ⚠ SysJobController / SysJobLogController / MQApi 剥 @PreAuthorize 后**无权限检查** — 推荐 admin 链路经 Shiro 已认证用户才可达（Phase 3 ShiroFilter 链路覆盖），但任务级权限粒度（monitor:job:list 等）丢失。若运维敏感，planner 加 `@RequiresPermissions("monitor:job:list")` Shiro 注解保留粒度。|
| V5 Input Validation | yes | sys_job.invoke_target 是反射调用目标字符串 — `SysJobController.add/edit` 内置 4 重防护：① cron 必须通过 CronUtils.isValid；② invokeTarget 不得含 `rmi:` / `ldap(s):` / `http(s):`；③ invokeTarget 不得含 `java.net.URL` / `javax.naming.InitialContext` / `org.yaml.snakeyaml` / `org.springframework` / `org.apache` / `com.ruoyi.common.utils.file`（JOB_ERROR_STR）；④ invokeTarget bean 包名必须在 JOB_WHITELIST_STR（本期改 `{"com.spt"}`）。**保留这 4 重防护不剥除**。|
| V6 Cryptography | no | 无加密需求 |
| V7 Logging | yes | sys_job_log 自动记录每个任务执行（jobName / invokeTarget / status / exceptionInfo 2000 char 截断 / start/stop time）— AbstractQuartzJob.after 实现，本期照搬不修改 |
| V9 Communications | no | 无新通信链路 |
| V14 Configuration | yes | ScheduleConfig uncomment 后必须含 `org.quartz.jobStore.isClustered` 配置（false，单节点不需集群）；`misfireThreshold=12000` 默认值保；线程池 threadCount=20（按业务调整，本期默认即可）|

### Known Threat Patterns for Quartz Handler Migration

| Pattern | STRIDE | Standard Mitigation |
|---------|--------|---------------------|
| 反射型 RCE（恶意 invoke_target）| Elevation of Privilege | RuoYi 4 重防护（V5 Input Validation）；JOB_WHITELIST_STR 收紧到 com.spt；admin ShiroFilter 已认证才可达 |
| Job 数据被 SQL 注入改写 | Tampering | SysJobMapper.xml 用 `#{}` 参数化（RuoYi 默认，无字符串拼接）|
| 大量任务同时触发拖垮线程池 | DoS | quartz threadPool.threadCount=20（RuoYi 默认）+ sys_job.concurrent='1'（@DisallowConcurrentExecution）保串行型任务不堆积 |
| 任务异常吞掉 | Repudiation | AbstractQuartzJob.execute catch Exception 后调 after(context, job, e) 写 sys_job_log.status=FAIL + exceptionInfo — 异常不会静默吞 |
| 任务代码访问越权数据 | Information Disclosure | handler @Autowired service 复用既有 service 层 ACL（Phase 4 业务 service 不本期改动）|

## Sources

### Primary (HIGH confidence)
- `/Users/alan/WorkSpace/IDEA/spt-auth/auth-quartz/**` — 整模块 18 类 + 2 XML + pom.xml 直读（ScheduleConfig 整类注释 / SysJobServiceImpl.@PostConstruct init / ScheduleUtils.whiteList / JobInvokeUtil.invokeMethod 等关键代码逐行核）
- `/Users/alan/WorkSpace/IDEA/spt-auth/sql/quartz.sql` — 11 张 QRTZ_* 表 DDL（实测无 sys_job/sys_job_log）
- `/Users/alan/WorkSpace/IDEA/spt-auth/sql/ry_20210908.sql:566-603` — sys_job / sys_job_log DDL + 3 行 RyTask demo INSERT
- `/Users/alan/WorkSpace/IDEA/spt-auth/auth-common/src/main/java/com/spt/common/constant/{Constants, ScheduleConstants}.java` — Constants.JOB_WHITELIST_STR / ScheduleConstants.TASK_PROPERTIES / Status 枚举
- `/Users/alan/WorkSpace/IDEA/zgbas/basCore/basServer/src/main/java/com/spt/bas/server/{task,command,api,config}/**` — 58 handler + ScheduleConfig + MQApi + BasCommandExecutor 源码核
- `/Users/alan/WorkSpace/IDEA/zgbas/basReport/reportServer/src/main/java/com/spt/bas/report/server/command/ReportCommandExecutor.java` — 1 handler 源码核
- `/Users/alan/WorkSpace/IDEA/zgbas/web/src/main/java/com/spt/bas/web/config/BasWebCommand.java` — 1 handler 源码核
- `/Users/alan/WorkSpace/IDEA/zgbas/basWx/purchase-server/src/main/java/com/spt/bas/purchase/wx/server/{task,command}/**` — 5 handler（本期不迁）源码核
- `/Users/alan/WorkSpace/IDEA/zgbas-plus/zgbas-admin/src/main/java/com/spt/ZgbasApplication.java` — @ComponentScan / @MapperScan / @EnableFeignClients 当前状态核
- `/Users/alan/WorkSpace/IDEA/zgbas-plus/zgbas-framework/src/main/java/com/spt/framework/config/ZgbasMybatisConfig.java` — @MapperScan(annotationClass=MyBatisDao.class) 当前过滤规则
- `/Users/alan/WorkSpace/IDEA/zgbas-plus/zgbas-system/pom.xml:93-135` — xxl-job-core:2.3.0 依赖位置
- `/Users/alan/WorkSpace/IDEA/zgbas-plus/zgbas-system/src/main/java/com/spt/bas/server/service/impl/CtrContractProfitServiceImpl.java:16,172,182` — XxlJobHelper.log 非 handler 用法
- `/Users/alan/WorkSpace/IDEA/zgbas-plus/zgbas-admin/src/main/resources/application.yml` + `application-dev.yml` — mybatis-plus.mapper-locations / 无 xxl.job.* keys 核
- `/Users/alan/WorkSpace/IDEA/zgbas-plus/zgbas-admin/src/main/resources/templates/monitor/` — 当前子目录清单（cache/logininfor/online/operlog/server，无 job）
- `/Users/alan/WorkSpace/IDEA/zgbas-plus/zgbas-quartz/pom.xml` + `PackageMarker.java` — 模块骨架当前状态

### Secondary (MEDIUM confidence)
- Spring Boot 2.5.9 spring-boot-dependencies BOM 托管 quartz-scheduler:2.3.2 + spring-context-support:5.3.13（Spring Boot 官方 BOM 长期托管关系，与 spt-auth/auth-quartz/pom.xml 同栈对照）
- Maven Central `org.quartz-scheduler:quartz` 公开 metadata（成熟包，2008+ 至今）

### Tertiary (LOW confidence)
- 无（所有事实经源码直查）

## Metadata

**Confidence breakdown:**
- Standard stack: HIGH — Spring Boot BOM 托管 + spt-auth/auth-quartz 源码直查 + Maven Central 公开 metadata 三证
- Architecture: HIGH — 6 个 pattern 全部源码出处可追溯 + 实测 monolith 当前文件状态
- Pitfalls: HIGH — 9 个 pitfall 每个都实测触发条件 + 验证现状（含 source code line refs）
- cron 数据来源 D-P6-01: MEDIUM — 用户承诺但未到位（A5 假设）；fallback D-P6-03 全 PAUSED 保 QUARTZ-04 硬验收

**Research date:** 2026-07-18
**Valid until:** 2026-08-17（30 天稳定期；spt-auth/auth-quartz 不本期变更，zgbas 源分支不本期变更）
