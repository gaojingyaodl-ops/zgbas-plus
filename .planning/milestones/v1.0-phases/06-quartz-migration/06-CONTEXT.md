# Phase 6: 定时任务迁移 - Context

**Gathered:** 2026-07-18
**Status:** Ready for planning

<domain>
## Phase Boundary

把源 zgbas 的 **xxl-job 定时任务体系**整体替换为 **RuoYi quartz**：新建 `zgbas-quartz` 模块 + 整模块复制 `spt-auth/auth-quartz` + 建 `sys_job`/`sys_job_log` 表；把 ~60 个 `@XxlJob` handler 迁为 quartz bean（机制按 QUARTZ-03 字面：`XxlJobHelper.log→slf4j`、`handleSuccess/Fail→return/异常`、`getJobParam→JobDataMap`）；任务记录初始化（cron/bean/method 翻译为 `sys_job` 数据）；删除 xxl-job 依赖与 executor 配置。本期交付 5 项需求（QUARTZ-01/02/03/04 + INFRA-03）。

**实测口径修正（research 实证，覆盖 roadmap/approximate "64"）：**
- 源 zgbas 实测 **65 个 `@XxlJob` 方法**：basServer 58（`task/` + `rocketmq/task/`）+ reportServer 1 + **basWx/purchase-server 5** + web 1。
- basWx 5 个随业务 v2（#14）**本期不迁**（D-P6-08）→ **实际迁 ~60 个**（basServer 58 + reportServer 1 + web 1）。
- ⚠ **cron 调度数据不在 zgbas 仓库内** —— 源 `xxl.job.admin.addresses=http://xxljob.totrade.cn/xxl-job-admin`，所有 cron/路由/重试/阻塞策略存外部 xxl-job admin 的 MySQL（`xxl_job_info` 表）。仓库内 `@XxlJob(value="autoPay")` 只定义 **handler 名**。→ `sys_job` 的 cron 数据来源是本期最核心未知（D-P6-01）。

**不在本期（明确边界）：**
- **basWx/purchase-server 的 5 个 @XxlJob handler + purchase 业务** → **v2**（#14，本期不迁 handler，单体无 purchase service）。
- **xxl-job 失败重试 / 告警邮件 / 执行器路由行为等价** → 本期**不要求**（D-P6-12，仅保阻塞+日志）。
- **真实全量任务回归对照 + 浏览器 e2e** → **Phase 7**（ALIGN-01/02）。本期仅抽样 dry-run（D-P6-04）。
- **xxl-job admin / xxl_job_info 表本身迁入单体** → 不做（xxl-job admin 是外部独立服务，本期删 executor 侧依赖即可，admin 服务退役是运维事务非本期）。
- **报表物理分页 / 补审计字段 / ddl-auto=validate 重开** → 永久 Out-of-Scope / tech debt（同 Phase 5）。
- **CR-01 真实轮换已泄漏生产库密码** → 跨阶段安全债，仍 deferred。

</domain>

<decisions>
## Implementation Decisions

### cron 数据来源（本期最核心未知）
- **D-P6-01: 导出生产 xxl-job admin DB cron → 翻译为 sys_job（非"仅手动触发"，非"逐个业务方确认"）。** 用户承诺从生产 xxl-job admin（`xxljob.totrade.cn`）的 `xxl_job_info` 表导出 job 全量配置（handler 名 / cron / 参数 / 路由 / 阻塞策略 / 重试次数），翻译为 RuoYi `sys_job` INSERT。迁完后任务是**真定时**，最接近"行为对齐旧系统"核心价值。弃"仅手动触发 cron 留空"（交付物仅为"可触发"非"真定时"，偏离核心价值）/ 弃"逐个业务方确认"（60+ 个成本高，业务方未必记得精确 cron）。⚠ **前置依赖**：用户须提供 xxl-job admin 导出（DB 访问 / dump）；planner 作 `checkpoint:human-blocked` 等待导出物到位。
- **D-P6-02: 我翻译 + 你逐项核对（checkpoint:human-verify）。** 翻译流程：Claude 负责把原始导出翻译为 `sys_job` INSERT（cron 字段数适配 xxl-job→quartz + `invoke_target` 构造 `beanName.methodName()` + 参数固化 + job_group/status 划分），生成翻译产物后作为 `checkpoint:human-verify` 交用户**逐条核对**再落库。平衡自动化与安全。弃"用户给已对齐 sys_job 数据"（用户前置工作量过大）/ 弃"我翻译直接落库不核对"（cron 静默错配会调度错误，风险不可见）。
- **D-P6-03: 无 cron / 手动型 / 废弃任务分级处理。** 导出时遇三类：① 有 cron 的定时任务 → `sys_job.status=NORMAL`；② 无 cron 的手动触发型（源本就不定时，业务方临时点）→ `sys_job.status=PAUSED`（仅手动触发）；③ 已废弃任务 → 跳过不迁。行为最对齐（旧系统手动型本就 PAUSED 等价）。弃"全部 NORMAL + 默认 cron 兜底"（让旧手动型变误定时）/ 弃"只迁有 cron 的无 cron 不迁"（旧手动型丢失）。

### 验收策略
- **D-P6-04: 全量 bean 解析 + 抽样执行（对齐 D-P5-08 / D-P4-06）。** 全量 60+ handler：编译过 + quartz bean 全解析 + Scheduler 加载全量 sys_job（D-P6-06）；执行层只抽 3-5 个代表性 handler 手动触发 dry-run。弃"全量手动触发执行"（成本高 + 写类污染 dev 库）/ 弃"仅 1 个 dry-run"（成功标准 4 字面底线，覆盖过薄）。
- **D-P6-05: dry-run 分级——只读真跑 / 写类空跑。** 抽样优先选只读/幂等类（统计刷新 / 报表类）**真跑**；写类（`autoPay` / `refreshContractStatusTask` / `OrverdurTask` / `DepositPaymentTask` 等）**空跑**（test-side mock 短路业务调用，只验 bean 解析 + 参数传递 + slf4j 日志）。保护 dev 库 `sptbasdb_pd` 不被污染，行为验证部分覆盖（写类真实回归留 P7）。弃"全部真实执行"（污染 dev 库）/ 弃"全部空跑"（行为验证最弱）。
- **D-P6-06: 启动期验证强度=强（Scheduler 全量加载 sys_job cron 做 fail-fast）。** 启动期 quartz `Scheduler` 加载全量 `sys_job`：cron 表达式**全部解析通过** + 每个任务 trigger 可算下次触发时间 + `invoke_target` 指向的 bean.method 全存在。翻译错 → **fail-fast 启动报错**。最大化暴露 D-P6-01/D-P6-02 的 cron 翻译风险（与启动验证为主基线联动）。弃"弱：仅 Spring bean 解析"（cron 错误后移到运行期/首次触发，风险不可见）。

### 模块落位与拓扑
- **D-P6-07: 60+ handler 落 `zgbas-quartz`（依赖方向 quartz → system）。** handler 与 RuoYi quartz 基础设施（ScheduleConfig / SysJob* / util / RyTask）同模块；handler `@Autowired` system 业务 service → `zgbas-quartz` 声明 maven 依赖 `zgbas-system`。⚠ **更新 D-08 模块拓扑**：原拓扑未明确 quartz 位置，本期定为 `quartz → system`（system 作为业务核心不反向依赖调度层，拓扑方向自洽）。弃"handler 落 system，quartz 只放基础设施"（用户决策：调度相关代码统一归 quartz 模块，便于运维定位）/ 弃"落 admin"（handler 非 HTTP 控制器，语义不符）。
- **D-P6-09: handler 包名改 `com.spt.quartz.task`（⚠ 偏离 D-P2-07 照搬保包名）。** 因 handler 落 zgbas-quartz（D-P6-07），包名对齐模块改 `com.spt.quartz.task`（对齐 RuoYi `RyTask` 同包），非保源包名 `com.spt.bas.server.task`。⚠ **research/planning 须显式核实**：① `MQApi` / `BasCommandExecutor`（Phase 4 deferred 到 P6 的 command/4 task 类）对 handler 类的直接引用——repackage 后这些引用的 import 需同步更新（非纯照搬）；② handler 内部 `@Autowired` 的 service 接口包名（`com.spt.bas.server.service.*`）**不变**，仅 handler 自身包名变。弃"保源包名照搬"（handler 在 quartz 模块却用 `com.spt.bas.server.task` 业务域路径，模块/包语义割裂）。

### basWx 范围取舍
- **D-P6-08: basWx/purchase-server 的 5 个 handler 随 v2 不迁（实际迁 ~60）。** basWx 业务整体 defer 到 v2（#14），单体无 purchase service，迁了也运行期空指针。本期实际迁 **~60 个**（basServer 58 + reportServer 1 + web 1），sys_job 不建这 5 条记录。v2 迁 basWx 业务时一并迁这 5 个 handler。弃"迁代码 + stub PAUSED"（无业务语义的空壳任务）/ 弃"迁全部 65 需 stub"（大量 stub service，成本高）。

### RuoYi quartz admin UI
- **D-P6-10: 保 `SysJobController`/`SysJobLogController` + 引入 RuoYi `/monitor/job` Thymeleaf 页面（⚠ 引入 RuoYi 前端，偏离照搬 zgbas 核心）。** 交付完整可视化管理台（增删改查 / 启停 / 手动触发 / 传参 / 查日志），QUARTZ-04"手动触发与传参"验收体验最佳。⚠ **research/planning 须处理**：① RuoYi `/monitor/job` Thymeleaf 模板从 `spt-auth` 复制（zgbas 无此模板，属新增前端）；② **菜单接入**——zgbas 用动态菜单（经 auth-sdk 从外部 spt-auth 取），RuoYi `/monitor/job` 菜单项需接入（直链或动态菜单注册，机制留 research）；③ Controller 落 zgbas-quartz，由 admin 的 `@ComponentScan("com.spt")` 扫到、admin 嵌入式服务器提供服务（RuoYi 单体同构）。弃"保 Controller API 不引入页面"（API 可用但无可视化）/ 弃"不引入 Controller"（QUARTZ-04 手动触发不便，需运维直操 DB）。

### 触发门面（同步触发入口）
- **D-P6-11: 保 `MQApi`/`BasCommandExecutor` HTTP 端点，内部改直调 service（不经 quartz 调度）。** 源前端/外部经 HTTP 调这些门面**同步触发** 8 个 `Synchronized*Task` 任务（不等 cron）。本期 HTTP 端点照搬保留（前端调用路径零改），内部改为**直接调对应 service method**（同进程同步调，不经 quartz `sysJobService.run(jobId)`）。行为等价（同步语义不变）+ 前端零改。sys_job 是另一条**定时调度**入口，两者共存（手动 HTTP 门面直调 service + 定时 quartz 调度同 service）。弃"走 quartz run(jobId)"（quartz run 是异步线程池，破坏同步语义）/ 弃"去掉门面统一 /monitor/job"（前端原调用点 404，需改前端，偏离行为等价）。

### 行为等价范围
- **D-P6-12: 仅保阻塞策略 + sys_job_log 日志，不保失败重试 / 告警邮件 / 路由策略。** xxl-job→RuoYi quartz 能力映射：①**阻塞策略**——源串行型（`SerialExecution`）→ RuoYi `@DisallowConcurrent` 保义；源覆盖/丢弃型 → misfire 默认策略；②**失败重试**——RuoYi 无内置，**不要求等价**（失败仅记 `sys_job_log`）；③**告警邮件**——RuoYi 无，**不要求等价**；④**执行器路由**——单体单节点，**不适用**（xxl-job 多节点路由天然蒸发）。行为等价核心 = 任务能被**调度/触发 + 执行 + 记录**，重试/告警属运维增强非本期。弃"保阻塞+重试"（RuoYi 需自包 retry，成本中）/ 弃"全量等价含告警"（需自接邮件，额外开发）。

### Claude's Discretion
- **xxl-job 删除边界（INFRA-03）**：删 `xxl-job-core` jar 依赖（Phase 4 04-04 Rule 3 为 basServer 编译临时加的 `xxl-job-core:2.3.0`）+ 删 `XxlJobSpringExecutor` bean 配置 + 删 `xxl.job.admin.addresses` / `xxl.job.executor.appname` 等 yml/properties 配置。⚠ **保留** `ScheduleConfig.java`（Spring `@EnableScheduling` + `SchedulingConfigurer`，各模块都有）——这是给 `@Scheduled` 用的线程池配置，**不是 xxl-job**，名称易混但属不同机制，保留不动。源项目是否存在 `@Scheduled` 任务留 research 确认（若有，随 ScheduleConfig 保留行为等价）。
- **写类空跑实现**：走 **test-side mock**（Mockito mock handler 的 service 依赖），**不加生产 dry-run 开关**（对齐最小改动 + 不污染生产代码）。空跑仅验 bean 解析 + 参数 `JobDataMap` 传递 + `XxlJobHelper.log→slf4j` 日志输出。
- **任务参数固化**：随 D-P6-01 的 cron 导出一起翻译（`xxl_job_info` 同条记录含 `executor_param`），固化到 `sys_job.args`，手动触发时可覆盖传参。
- **cron 格式适配**：xxl-job cron（常 6/7 字段含秒）→ quartz `cron_expression`（7 字段）的字段数适配属 D-P6-02 翻译流程一部分，逐条 human-verify 兜底。
- **web 模块那 1 个 @XxlJob handler**：归类同其他 handler，落 `zgbas-quartz` `com.spt.quartz.task`（D-P6-07/D-P6-09）。具体职责留 research 核实。
- **RuoYi quartz mapper mybatis namespace**：`com.spt.quartz.mapper.SysJobMapper` 等独立 namespace，与 Phase 2 `SampleMapper` / Phase 5 `com.spt.bas.report.server.dao` 不冲突（research 核 `@MapperScan` basePackages 覆盖 `com.spt.quartz.mapper`，必要时放宽 + XML 落 `zgbas-quartz/src/main/resources/mapper/`）。
- **job_group 划分 / sys_job_log 保留时长**：留 planning（RuoYi 默认 DEFAULT/SYSTEM 分组 + 日志清理策略，源无强约束）。
- **D-P2-07 偏离收口**：D-P6-09 是本项目首次偏离"照搬保包名"铁律，**仅限 handler 类**（因落 zgbas-quartz 模块）。entity / Dao / service / controller / Mapper 等仍守 D-P2-07 保包名。research/planning 须把 D-P6-09 的引用更新作为显式 task，不遗漏。

### Folded Todos
- 无 todo 折叠进本期（见 Reviewed Todos，2 个弱匹配均不折叠）。

</decisions>

<canonical_refs>
## Canonical References

**Downstream agents MUST read these before planning or implementing.**

### 项目规划（仓库内，必读）
- `.planning/ROADMAP.md` §Phase 6 — 阶段目标（xxl-job 删除 + 64 handler 迁 RuoYi quartz）/ 依赖（Phase 2）/ 需求映射（QUARTZ-01/02/03/04 + INFRA-03）/ 4 条成功标准
- `.planning/REQUIREMENTS.md` — QUARTZ-01（zgbas-quartz 模块 + 整模块复制 spt-auth/auth-quartz + ScheduleConfig）/ QUARTZ-02（sys_job/sys_job_log 表，DDL 参考 spt-auth/sql/quartz.sql）/ QUARTZ-03（64 handler 迁 quartz bean，机制字面：XxlJobHelper.log→slf4j / handleSuccess·Fail→return·异常 / getJobParam→JobDataMap）/ QUARTZ-04（任务记录初始化 cron/bean/method 翻译为 sys_job，手动触发+传参）/ INFRA-03（删 xxl-job 依赖与 executor 配置）
- `.planning/PROJECT.md` — 固定 5 模块（zgbas-quartz 占一席）、xxl-job 删除改 RuoYi quartz（#10）、外部 spt-auth 保持外部（#7）、JDK 1.8 / SpringBoot 2.5.9 / Maven 3.8.6 + zg_settings.xml
- `.planning/phases/02-infrastructure/02-CONTEXT.md` — **D-P2-07**（照搬保包名，D-P6-09 首次偏离仅限 handler）/ **D-P2-09**（mybatis-plus 栈，RuoYi quartz mapper namespace 参照）/ **D-P2-02**（ddl-auto=none）/ @Primary 双 ORM 接线 / D-08 模块拓扑（D-P6-07 补充 quartz→system 方向）
- `.planning/phases/04-core-business/04-CONTEXT.md` — **Phase 4 deferred xxl-job cluster**（command/BasCommandExecutor + 4 task 类 + MQApi + basServer/task 23 + rocketmq/task 8，本期 D-P6-07/D-P6-09/D-P6-11 接手）/ **D-P4-02**（stub 降级，basWx 5 handler 不走此模式直接 v2）/ **D-P4-04** infra 筛选排除 task（本期迁回）/ **D-P4-06**（启动验证为主 + WR HTTP proof，D-P6-04/D-P6-06 沿用）
- `.planning/phases/05-report-migration/05-CONTEXT.md` — **D-P5-08**（启动 + 抽样 proof 非 hermetic，D-P6-04/D-P6-05 沿用；明文密钥不需 export）/ **deferred reportServer/task(1)+command(1)+listener(1)→Phase 6**（本期接手）/ mybatis namespace 不冲突参照
- `CLAUDE.md` — 技术栈约束（JDK 1.8 / SpringBoot 2.5.9 / Maven 3.8.6 + zg_settings.xml）、固定 5 模块、xxl-job 删除改 RuoYi quartz（#10）、RuoYi 单体参考框架

### 源项目与 RuoYi 参考（**绝对路径，非本仓库内**）
- `/Users/alan/WorkSpace/IDEA/zgbas`（分支 `feat-系统重构v5.0`）— 源微服务
  - `basCore/basServer/src/main/java/com/spt/bas/server/task/`（23 @XxlJob handler，如 `ApplyPayTask` / `RepaymentTask` / `OrverdurTask` / `CtrContractScheduleTask` / `BsCompanyTask` ...）+ `.../server/rocketmq/task/`（8 @XxlJob）→ 迁入 zgbas-quartz `com.spt.quartz.task`（D-P6-07/D-P6-09）
  - `basCore/basServer/src/main/java/com/spt/bas/server/config/ScheduleConfig.java`（Spring `@EnableScheduling`，**非 xxl-job 非 RuoYi quartz**，保留不动）+ `basCore/basServer/.../api/MQApi.java`（8 Synchronized*Task 的 HTTP 触发门面，D-P6-11 保留端点改直调）+ command/BasCommandExecutor（D-P6-11）
  - `basReport/reportServer/src/main/java/com/spt/bas/report/server/task/`（1 @XxlJob）+ `.../{command,listener}/`（Phase 5 deferred 到 P6）→ 同迁 zgbas-quartz
  - `web/src/main/java/.../config/ScheduleConfig.java`（1 @XxlJob 在 web 模块，归类同迁）
  - `basWx/purchase-server/src/main/java/com/spt/bas/purchase/wx/server/{task,config}/`（5 @XxlJob）→ **本期不迁**（D-P6-08，随 v2）
  - `**/application*.properties` 的 `xxl.job.admin.addresses` / `xxl.job.executor.{appname,port,logpath...}` → 删除（INFRA-03）
- `/Users/alan/WorkSpace/IDEA/spt-auth/auth-quartz` — **RuoYi quartz 参考源（整模块复制来源，QUARTZ-01 字面）**
  - `src/main/java/com/spt/quartz/` — `config/ScheduleConfig.java`（RuoYi 调度器配置）/ `domain/{SysJob,SysJobLog}.java` / `mapper/{SysJobMapper,SysJobLogMapper}.java` / `service/{ISysJobService,ISysJobLogService}` + `impl/` / `controller/{SysJobController,SysJobLogController}.java`（`/monitor/job` admin UI，D-P6-10）/ `util/{AbstractQuartzJob,QuartzJobExecution,QuartzDisallowConcurrentExecution,ScheduleUtils,JobInvokeUtil,CronUtils}.java` / `task/RyTask.java`（sample）
  - `**/sql/quartz.sql` 或 RuoYi 标准 `quartz.sql`（sys_job/sys_job_log + QRTZ_* 表 DDL，QUARTZ-02）→ research 定位
  - RuoYi `/monitor/job` Thymeleaf 模板（`templates/monitor/job/*`）→ D-P6-10 引入来源
- `/Users/alan/App/apache-maven-3.8.6` — Maven 可执行
- `/Users/alan/App/apache-maven-3.8.6/zg_settings.xml` — 私服仓库 settings（构建必用）
- `/Users/alan/App/Repository` — 本地仓库

### 当前单体（已就位的 Phase 2/3/4/5 资产）
- `zgbas-system/src/main/java/com/spt/bas/server/service/`（Phase 4 已迁业务 service iface + impl）→ handler `@Autowired` 目标，包名不变（D-P6-09 仅 handler 包名变）
- `zgbas-admin/src/main/java/com/spt/ZgbasApplication.java` — `@ComponentScan("com.spt")` 已覆盖 `com.spt.quartz.*`，`@MapperScan` 需 research 放宽覆盖 `com.spt.quartz.mapper`
- `zgbas-system/src/main/resources/mybatis/mappers/`（Phase 2 SampleMapper + Phase 5 53 report XML）→ RuoYi quartz XML 落 `zgbas-quartz/src/main/resources/mapper/`（独立位置，research 核 mapper-locations 覆盖）
- `application.yml` + `application-{dev,prod}.yml`（Phase 2 配置基线，dev 明文密钥）→ 本期删 xxl.job.* 配置 + 加 RuoYi quartz 配置（org.quartz.* / scheduler 配置）
- Phase 2 `pom.xml` 已声明 `zgbas-quartz` 模块占位（5 模块聚合）→ 本期填充模块内容 + 声明 quartz→system 依赖

</canonical_refs>

<code_context>
## Existing Code Insights

> zgbas-plus 当前为 **Phase 5 完成态**：骨架 + spt-tools 全量内联 + 双 ORM 单 DataSource + 外部 SDK bean + nacos 删除 + Shiro 登录 + basServer 核心业务（service/api/BFF）全量迁入 + 53 报表全量迁入 + report 契约接通。`zgbas-quartz` 模块已在聚合 pom 占位但**内容为空**（Phase 1 骨架预留）。Phase 4/5 已把 xxl-job 相关 `task`/`command`/`listener`/`MQApi` 全部 deferred 到本期。Phase 6 填充 zgbas-quartz 模块 + 迁 ~60 handler + 建 sys_job 体系 + 删 xxl-job。

### Reusable Assets
- **Phase 4 已迁业务 service（`com.spt.bas.server.service.*`）**——handler `@Autowired` 的目标已就位（BIZ-01/03 完成），handler 迁入后 service 调用天然可用。
- **Phase 2 `@ComponentScan("com.spt")` + `@MapperScan`**——覆盖 `com.spt.quartz.*` 的组件/mapper 扫描基础已就绪，仅需 research 放宽 `@MapperScan` basePackages 含 `com.spt.quartz.mapper`。
- **Phase 2/5 mybatis-plus 基础设施**——RuoYi quartz mapper 复用同 SqlSessionFactory（双 ORM 单 DataSource），独立 namespace 不冲突。
- **Phase 5 D-P5-08 抽样 proof 模式**——D-P6-04/D-P6-05/D-P6-06 验收策略直接复用（启动验证 + 抽样 + 非 hermetic）。
- **Phase 4 D-P4-02 stub 降级**——purchase 契约同模式（v2），但 basWx handler **不走 stub**，直接不迁（D-P6-08）。
- **RuoYi auth-quartz 整模块**——`/Users/alan/WorkSpace/IDEA/spt-auth/auth-quartz` 完整可复制（18 类 + 模板 + sql），QUARTZ-01 字面"整模块复制"。

### Established Patterns
- **照搬保包名（D-P2-07）**——handler 类**首次偏离**（D-P6-09 改 `com.spt.quartz.task`），其余资产仍守。
- **逐层 compile 绿灯（Phase 1 gotcha 级联教训 + D-P4-03）**——60+ handler 分批迁，每 wave 后 `mvn compile` 全模块零 `[ERROR]`（JDK 1.8.0_482）再继续；grep `^\[ERROR\]` locale 无关。
- **启动验证为主 + 抽样 proof + 非 hermetic（D-P3-13/D-P4-06/D-P5-08）**——明文密钥（Phase 4 决定）不需 export `DB_PASSWORD`/`SPT_APP_SECRET`。
- **checkpoint:human-verify 机制**——D-P6-02 cron 翻译产物、D-P6-01 导出物到位均走此机制（Phase 4 D-P4-01a 同型）。
- **wave + 逐层 compile-gate（D-P4-03/D-P5-04）**——本期 ~60 handler 按域/按模块（basServer task / rocketmq task / report task / web）分批。

### Integration Points
- `zgbas-quartz` ← RuoYi auth-quartz 整模块复制（config/domain/mapper/service/controller/util/task）+ ~60 handler（repackage `com.spt.quartz.task`）+ 声明 maven 依赖 `zgbas-system`（D-P6-07）。
- `zgbas-quartz/src/main/resources/mapper/` ← RuoYi `SysJobMapper.xml` / `SysJobLogMapper.xml`。
- `application.yml` ← 删 `xxl.job.*` + 加 RuoYi quartz 配置（`org.quartz.scheduler.instanceName` / threadPool / `quartz.tablePrefix` 等）。
- `@MapperScan`（ZgbasApplication）← 放宽 basePackages 含 `com.spt.quartz.mapper`。
- `sys_job` / `sys_job_log` / `QRTZ_*` 表 ← DDL（spt-auth/sql/quartz.sql）建进 `sptbasdb_pd`。
- `sys_job` 数据 ← D-P6-01 用户导出 → D-P6-02 翻译 → human-verify → INSERT。
- `MQApi` / `BasCommandExecutor`（admin 层 HTTP 端点）← 内部改直调 system service（D-P6-11）。
- 前端 `/monitor/job` 模板 ← RuoYi 复制 + 菜单接入（D-P6-10，research 机制）。
- pom ← 删 `xxl-job-core:2.3.0`（Phase 4 04-04 Rule 3 临时加）+ zgbas-quartz 填充依赖（quartz / spring-context-support / RuoYi 必需）。

</code_context>

<specifics>
## Specific Ideas

- 用户全程选「行为等价优先 + 真定时 + 可视化运维 + 前端零改」，但在 handler 落位（D-P6-07 落 quartz 而非 system）、包名（D-P6-09 改 `com.spt.quartz.task` 偏离照搬）、admin UI（D-P6-10 引入 RuoYi 前端偏离照搬）三处**主动选择偏离"纯照搬"**，换取【调度相关代码统一归 quartz 模块 + 可视化任务管理台】——体现对**运维可定位性 + 可操作管理界面**的重视，高于"绝对照搬"。research/planning 须把这三处偏离作为显式 task 处理（引用更新 / 模板复制 / 菜单接入），不遗漏。
- cron 数据来源（D-P6-01）用户选"导出生产 + 翻译"而非"仅手动触发"——交付物是**真定时**任务，最贴核心价值；但**强依赖用户提供外部 xxl-job admin 导出**，planner 须作前置 checkpoint:human-blocked。
- 验收策略（D-P6-04/05/06）用户选"全量 bean 解析 + 抽样 + 分级 dry-run + 强 Scheduler fail-fast"——在成本与风险暴露间取平衡，强启动验证专门为兜住 D-P6-01/02 的 cron 翻译风险。
- 行为等价（D-P6-12）用户主动放弃"重试/告警等价"——认同"任务能调度+执行+记录"即核心，重试/告警属运维增强，体现务实的等价边界。

</specifics>

<deferred>
## Deferred Ideas

- **xxl-job 失败重试 / 告警邮件 / 执行器路由行为等价** → 永久 Out-of-Scope（D-P6-12，单体单节点路由不适用；重试/告警属运维增强）
- **xxl-job admin 服务退役 / xxl_job_info 表迁入单体** → 运维事务，非本期（本期仅删单体侧 executor 依赖）
- **basWx/purchase-server 5 个 @XxlJob handler + purchase 业务** → v2（#14，D-P6-08）
- **真实全量任务回归对照 + 浏览器 e2e** → Phase 7（ALIGN-01/02）
- **报表物理分页性能改造 / 补 createBy-updateBy 审计字段** → 永久 Out-of-Scope（PROJECT.md）
- **全量实体 schema drift reconcile + 重开 ddl-auto=validate（D-P2-02 原意）** → tech debt（todo `phase4-resolve-entity-schema-drift` 保留 open）
- **CR-01 真实轮换已泄漏生产库密码** → 跨阶段安全债，仍 deferred

### Reviewed Todos (not folded)
- `phase4-resolve-entity-schema-drift.md`（D-P2-02 偏离）→ Phase 4 降级保留 open；与 quartz 无关，本期不折叠（D-P6 仅核 xxl-job 删除不碰 ddl-auto）。
- `rotate-leaked-prod-credentials.md`（CR-01 轮换已泄漏生产库密码）→ 跨阶段安全债；弱匹配 Phase 6（通用关键词 phase/zgbas），review 不折叠。

</deferred>

---

*Phase: 6-定时任务迁移*
*Context gathered: 2026-07-18*
