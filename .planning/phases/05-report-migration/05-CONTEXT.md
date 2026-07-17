# Phase 5: 报表迁移 - Context

**Gathered:** 2026-07-17
**Status:** Ready for planning

<domain>
## Phase Boundary

把源 `basReport`（分支 `feat-系统重构v5.0`）的 **53 套 mybatis 报表**（合同台账/收付款/风控/业绩等）的 **Mapper + XML + Service + HTTP Controller** 迁入单体，查询行为与旧系统 zgbas 等价。本期交付三件事（REPORT-01/02 + PERSIST-02）：

1. **53 报表 Mapper + XML + Service 迁入 `zgbas-system`**（REPORT-01 / PERSIST-02）：源 `reportServer/dao/`（53 `Rpt*Mapper.java`）+ `reportServer/resources/mybatis/mappers/`（53 XML，namespace = `com.spt.bas.report.server.dao.Rpt*Mapper`）+ `reportServer/service/`（54 iface + 54 impl）+ `reportServer/{util,vo,config}`（筛选照搬）迁入。mybatis 复杂报表查询可执行（PERSIST-02 实证）。
2. **55 报表 Controller 迁入 `zgbas-admin`**（REPORT-02）：源 `reportServer/api/`（55 个**前端直连 `@RestController`**，如 `/rpt/fundReceivableStatistics/findPage`，return Spring `Page`，`@Autowired` report service）迁入 admin，保包名 `com.spt.bas.report.server.api`，HTTP 端点 `/rpt/...` 可访问。
3. **reportClient 数据载体 + Feign 契约接通**：源 `reportClient/`（265 java：53 报表实体 + vo + remote `IReport*Client` 契约）**内联源码进 `zgbas-system`**（照搬保包名）；Phase 4 04-05 已放宽 `@EnableFeignClients` 扫 `com.spt.bas.report.client.remote`（自回环→404），本期**接通真实调用**，解除 D-P4-02 对 report 契约的 stub 降级 → 关闭 Phase 3 遗留 MyIndexController 缺口。

**不在本期（明确边界）：**
- **xxl-job `task` handler（reportServer/task 1 个 + command 1 + listener 1）** → **Phase 6**（quartz）。本期 infra 筛选照搬时排除 task/command（listener 评估）。
- **真实业务/报表回归对照 + 浏览器 e2e** → **Phase 7**（ALIGN-01/02）。本期仅抽样查询 proof，非全量对照。
- **报表物理分页性能改造 / 补 createBy-updateBy 审计字段** → **永久 Out-of-Scope**（PROJECT.md，涉及 259 表结构变更）。分页**性能**不优化，但分页**正确性** 1:1 等价（D-P5-06）。
- **basWx 采购业务 + purchase 契约** → **v2**（#14）。purchase 契约维持 D-P4-02 stub 降级。
- **全量实体 schema drift reconcile + 重开 ddl-auto=validate（D-P2-02 原意）** → tech debt 再延（D-P4-05）。本期仅修运行阻塞型（含报表实体若带 `@Entity` 误拾，D-P5-07）。
- **真实轮换已泄漏生产库密码（CR-01）** → 跨阶段安全债，仍 deferred。

</domain>

<decisions>
## Implementation Decisions

### 架构与落位
- **D-P5-01: 报表 Controller 落 `zgbas-admin`（按 ROADMAP 字面）。** 55 个 `reportServer/api` 是**前端直连 edge 控制器**（`@RestController @RequestMapping("/rpt/...")`，return `Page<Entity>` 给前端，`@Autowired` report service），**非 Feign 服务 API** —— 角色上区别于 Phase 4 的 `basServer/api`（后者 `extends BaseApi` 被 web BFF 经 Feign 调用）。落 admin 与前端 report 模板/静态资源（Phase 3/4 已照搬至 `zgbas-admin/.../templates/report/`）同模块，对齐源 ReportServer（8002）作为前端面向报表服务的定位。`admin` 已依赖 `system`（D-08 拓扑），`@Autowired` report service（system）天然可用。弃落 `system`（Phase 4「impl 与契约同位」理由不适用：报表 api 非 Feign 服务 API，且会造成模板在 admin、控制器在 system 的依赖反转）。
- **D-P5-02: reportClient 数据载体（265 java）内联源码进 `zgbas-system`。** 53 报表实体 + vo + remote `IReport*Client` 契约全部 **verbatim 内联**进 `zgbas-system`（保包名 `com.spt.bas.report.client.*`，对齐 Phase 4 basClient Wave 1 + D-P2-07）。消除 Phase 4 暂留的 `report-client` 私服 jar 依赖（types-only defer P5），与已内联的 `basClient`（`com.spt.bas.client.*`）同构。mybatis `type-aliases-package` 指向本地源码包。弃保留 jar（与 Phase 4 basClient 处理不一致 + 留私服依赖）。⚠ 53 实体是否带 `@Entity`（JPA 扫描误拾风险）留 research/planning 显式核实（见 D-P5-07）。

### Feign 契约接线
- **D-P5-03: report Feign 契约本期接通真实调用（解除 D-P4-02 stub 降级）。** Phase 4 04-05 已放宽 `@EnableFeignClients` 扫 `com.spt.bas.report.client.remote`（9 svc refs）→ 自回环 localhost:8080 → 运行期 404（无 impl）。源 `web` BFF（**MyIndexController**[Phase 3 因 report 契约缺口 deferred 到 P5]、BusinessManagerWorkbenchController、BusinessOverviewController 等已迁 admin）`@Autowired IReport*Client` 调报表数据。本期接通 → 这些 BFF 的 report 调用真正可用，关闭 Phase 3 MyIndexController 缺口，完整行为等价。**机制留 research 核实**：55 report api（独立 `@RestController`，不 `implements IReport*Client`）的 `/rpt/...` 路径是否天然满足 `IReport*Client` 契约路径（源 reportServer context-path `/spt-bas-report` 内部转发，前端不见前缀）—— 若满足则 55 api 照搬即满足契约（D-P4-01 方案 A 自回环）；若不满足，planner 作 `checkpoint:human-verify` 上报，不擅自改契约。

### 迁移测序
- **D-P5-04: 按业务域分批 wave + 逐层 compile + 抽样 proof 绿灯。** 对齐 Phase 4 D-P4-03 wave + Phase 1 gotcha 级联教训。53 套报表虽同质（统一 Mapper+XML+service+api 模式，可机械批迁），但**按业务域分批**（合同台账/收付款/风控/业绩等，具体域划分留 planning）控 mybatis namespace / 类型依赖级联风险。每批后 `mvn compile` 全模块零 `[ERROR]`（JDK 1.8.0_482）+ 抽样查询 proof 绿灯再下一批。弃一次性全量批迁（unmask 雪崩风险）。

### 行为保真
- **D-P5-05: Excel 导出全量保行为等价。** 报表含大量 `/rpt/*/exportExcel` 端点（用户高频可见行为）。所有 exportExcel 随 report service 照搬即等价（用 Phase 2 已内联 `spt-tools` Excel util / POI）。行为等价核心价值要求保，逻辑随 service 迁入天然可用，无额外成本。特殊依赖（POI 模板文件、大结果集导出）留 research 确认，运行阻塞才修（D-P4-05 联动）。弃 defer 到 P7（导出缺口 = 用户可见行为缺失）。
- **D-P5-06: 分页正确性 1:1 照搬等价。** `findPage` 的 count + 分页 SQL **verbatim 照搬**，含 count 查询语义。分页**性能**优化 OUT-of-SCOPE（PROJECT.md），但分页**正确性**是行为等价的一部分，必须等价。对极重 count 查询的报表（超时型）仅作 runtime-blocking 逃生阀（对齐 D-P4-05 仅修运行阻塞），可接受近似或 defer —— 默认全量 1:1 等价。

### mybatis 配置 + 风险验证
- **D-P5-07: mybatis 配置合并 + @MapperScan/entity 显式验证。**
  - **配置合并**（research 已核）：源 report `mybatis-plus.mapper-locations=classpath:/mybatis/mappers/*Mapper.xml` **与 Phase 2 `SampleMapper.xml` 同一 classpath 位置** → report XML 落 `zgbas-system/src/main/resources/mybatis/mappers/` 即被现有配置覆盖，无需改扫描路径；`type-aliases-package` 追加 `com.spt.bas.report.client.{entity,vo}`；源 `myBatisConfig.xml` 近空（仅 `logImpl=STDOUT` / `lazyLoadingEnabled=false`，typeAliases 全注释）→ **丢弃**，依赖 Phase 2 mybatis-plus 自动配置（D-P2-09）。
  - **显式验证**（fail-fast）：research/planning 核实 `@MapperScan` basePackages 覆盖 `com.spt.bas.report.server.dao`（Phase 2 设的 SampleMapper 扫描范围，必要时放宽 basePackages）+ 53 report 实体无 `@Entity` 误拾（若有，按 D-P4-05 仅修运行阻塞 —— 报表实体应为 mybatis POJO，非 JPA `@Entity`）。启动期暴露。

### 验收策略
- **D-P5-08: 启动 + 抽样报表查询 proof（PERSIST-02 实证）。** 对齐 Phase 2/3/4 基线（D-P2-03 / D-P3-13 / D-P4-06）：全 Spring context 启动 + 53 Mapper bean 全解析（`@MapperScan` 覆盖 `report.server.dao`，无 `BindingException`）+ report `@RestController` bean 全解析 + **抽样跑几个报表 Mapper 查询返回非空**（证明 PERSIST-02 mybatis 复杂报表查询可用 + XML namespace 正确 + mapper-locations 接通 + type-aliases 解析）。非全 53（成本/边际收益不匹配，真实业务回归留 P7 ALIGN-02）。非 hermetic 同 D-P3-13（需真实库 `sptbasdb_pd`；按 Phase 4 明文密钥决定，dev profile 明文密钥已留，启动测试不再需 export `DB_PASSWORD`/`SPT_APP_SECRET`）。

### Claude's Discretion
- **HTTP 路径**：端点暴露在 `/rpt/...`（报表 `@RequestMapping` 原值，单体根 `/` D-P2-16）。**前端模板实测调 `/rpt/...`，不带 `/spt-bas-report` 前缀**（源 `web` 内部转发，前端不见 context-path）→ 前端模板零改、无需 path 剥离器（区别于 Phase 4 D-P4-01a basServer 前缀剥离）。`/spt-bas-report` context-path 是源服务内部配置，单体里蒸发。
- **报表 service 事务标注**：reports 读为主；沿用 Phase 2 `JpaTransactionManager @Primary` + 单 DataSource，mybatis 查询复用同源 SqlSessionFactory，`@Transactional` 标注随源照搬（不额外设计只读事务）。
- **reportServer infra 筛选**（对齐 D-P4-04）：`util`（3）/`vo`（1）/`config`（6）按需筛选照搬 —— `config` 去重 Phase 2 已接的 mybatis-plus / DataSource（仅保业务必需），`task`/`command` 排除（Phase 6），`listener`（1）评估是否 service 运行依赖。
- **报表 Mapper XML 动态 SQL / `${}`**：照搬即等价，不改写（行为等价优先；安全改造非本期）。

</decisions>

<canonical_refs>
## Canonical References

**Downstream agents MUST read these before planning or implementing.**

### 项目规划（仓库内，必读）
- `.planning/ROADMAP.md` §Phase 5 — 阶段目标（53 套 mybatis 报表迁入，查询行为等价）/ 依赖（Phase 2 + Phase 4）/ 需求映射（REPORT-01/02, PERSIST-02）/ 3 条成功标准
- `.planning/REQUIREMENTS.md` — REPORT-01（53 Mapper+XML 迁入 system 报表包）/ REPORT-02（报表 Controller 迁入 admin，查询行为等价，分页性能另行评估）/ PERSIST-02（mybatis 复杂报表查询可用，迁 53 Mapper+XML）
- `.planning/PROJECT.md` — 持久层体量（259 实体 + 254 Dao + **53 mybatis 报表**）、源架构（ReportServer=8002 报表 mybatis）、Out-of-Scope（报表物理分页性能改造 / 补审计字段 259 表加列 / basWx）
- `.planning/phases/02-infrastructure/02-CONTEXT.md` — **D-P2-07**（照搬保包名）/ **D-P2-09**（mybatis-plus 栈）/ **D-P2-04**（mybatis 范围 = infra + sample Mapper，53 报表留 P5）/ **D-P2-02**（ddl-auto 偏离）/ @Primary 双 ORM 接线 / SampleMapper 占位
- `.planning/phases/03-auth-homepage/03-CONTEXT.md` — **D-P3-10**（stub-port + `@Autowired(required=false)` + null 守卫，本期 D-P5-03 解除 report 契约此降级）/ **MyIndexController deferred 到 P5**（report-contract cascade）/ D-P3-13（启动验证为主 + 非 hermetic）
- `.planning/phases/04-core-business/04-CONTEXT.md` — **D-P4-01**（Feign 自回环方案 A，report 契约机制参照）/ **D-P4-02**（无本地实现契约 stub 降级，report 契约本期解除）/ **D-P4-03**（层次优先 wave 切片，本期 D-P5-04 沿用）/ **D-P4-04**（infra 筛选照搬）/ **D-P4-05**（ddl-auto=none + 仅修运行阻塞）/ **D-P4-06**（启动验证 + 抽样 proof）
- `CLAUDE.md` — 技术栈约束（JDK 1.8 / SpringBoot 2.5.9 / Maven 3.8.6 + zg_settings.xml）、固定 5 模块、双 ORM（#6）、外部 spt-auth 保持外部（#7）

### 源项目（搬运参考，**绝对路径，非本仓库内**）
- `/Users/alan/WorkSpace/IDEA/zgbas`（分支 `feat-系统重构v5.0`）— 源微服务
  - `basReport/reportServer/src/main/java/com/spt/bas/report/server/dao/`（53 `Rpt*Mapper.java`）→ 迁入 `zgbas-system`
  - `basReport/reportServer/src/main/java/com/spt/bas/report/server/service/`（54 iface + `impl/` 54）→ 迁入 `zgbas-system`
  - `basReport/reportServer/src/main/java/com/spt/bas/report/server/api/`（55 前端直连 `@RestController`，如 `RptFundReceivableStatisticsApi`）→ 迁入 **`zgbas-admin`**（D-P5-01）
  - `basReport/reportServer/src/main/resources/mybatis/mappers/`（53 `Rpt*Mapper.xml`，namespace=`com.spt.bas.report.server.dao.Rpt*Mapper`）→ 迁入 `zgbas-system/src/main/resources/mybatis/mappers/`（与 SampleMapper 同位）
  - `basReport/reportServer/src/main/resources/mybatis/myBatisConfig.xml`（近空）→ **丢弃**（D-P5-07）
  - `basReport/reportServer/src/main/resources/application.properties`（`server.servlet.context-path=/spt-bas-report` + `mybatis-plus.mapper-locations` + `mybatis-plus.type-aliases-package=com.spt.bas.report.client.{entity,vo}`）→ 仅取 mybatis-plus 两个键合并进单体 yml
  - `basReport/reportServer/src/main/java/com/spt/bas/report/server/{util,vo,config,task,command,listener}/` → util/vo/config 筛选照搬；task/command 排除（Phase 6）；listener 评估
  - `basReport/reportClient/src/main/java/com/spt/bas/report/client/{entity,vo,remote}/`（265 java）→ **内联源码进 `zgbas-system`**（D-P5-02）
  - `web/src/main/java/com/spt/bas/web/controller/{MyIndexController,BusinessManagerWorkbenchController,BusinessOverviewController}.java` + `web/.../cache/WorkBenchCache.java`（`@Autowired IReport*Client` 调用方，Phase 4 已迁 admin）→ 本期 D-P5-03 接通其 report 契约调用

### 当前单体（已就位的 Phase 2/3/4 资产）
- `zgbas-system/src/main/resources/mybatis/mappers/SampleMapper.xml`（Phase 2 sample）→ 本期 53 report XML 与之**同位**落盘，现有 `mapper-locations` 即覆盖
- `zgbas-system/src/main/java/com/spt/bas/client/vo/{ContractReportVo,ApplyDeliveryReportVo,InternalBuyReportVo,CtrContractAgencyReportVo}.java`（Phase 4 已迁部分 report VO）→ 本期内联 reportClient 时去重
- `zgbas-admin/src/main/resources/templates/report/`、`templates/ctr/contractReport.html`（Phase 3/4 已照搬 report 前端模板）→ 本期 Controller 落 admin 与之同模块
- `zgbas-admin/src/main/java/com/spt/ZgbasApplication.java` — `@EnableFeignClients` 已放宽扫 `com.spt.bas.report.client.remote`（Phase 4 04-05）→ 本期 D-P5-03 接通
- `application.yml` + `application-{dev,prod}.yml`（Phase 2 配置基线，dev 明文密钥）→ 本期追加 mybatis-plus 两键

### 构建 / 工具链（绝对路径）
- `/Users/alan/App/apache-maven-3.8.6` — Maven 可执行
- `/Users/alan/App/apache-maven-3.8.6/zg_settings.xml` — 私服仓库重定向 settings（构建必用）
- `/Users/alan/App/Repository` — 本地仓库

</canonical_refs>

<code_context>
## Existing Code Insights

> zgbas-plus 当前为 **Phase 4 完成态**：骨架 + spt-tools 全量内联 + 双 ORM 单 DataSource（mybatis-plus + SampleMapper）+ 外部 SDK bean + nacos 删除 + Shiro 登录链路 + basServer 核心业务（service/api/BFF）全量迁入。Phase 4 已放宽 Feign 扫 `com.spt.bas.report.client.remote`（自回环→404，无 impl）+ report 前端模板/部分 VO 已就位。Phase 5 补 53 报表 Mapper+XML+service + 55 Controller + reportClient 内联 + report 契约接通。

### Reusable Assets
- **Phase 2 mybatis-plus infra + `SampleMapper.xml`**（`zgbas-system/.../mybatis/mappers/`）—— `mapper-locations=classpath:/mybatis/mappers/*Mapper.xml` 已配，53 report XML 同位落盘即被覆盖；SqlSessionFactory 复用同 DataSource（D-P2-09 双 ORM）。
- **Phase 4 report Feign 扫描放宽**（`@EnableFeignClients` 含 `com.spt.bas.report.client.remote`）—— D-P5-03 接通的真实调用契约侧已就绪，仅缺 impl。
- **Phase 4 已迁 report VO**（`com.spt.bas.client.vo.ContractReportVo` 等）—— 内联 reportClient 时去重，避免重复 bean。
- **Phase 3 stub-port 模式**（`@Autowired(required=false)` + null 守卫）—— D-P5-03 解除 report 契约的此降级（替换为真实调用），purchase 契约仍维持此模式（v2）。
- **Phase 4 wave + 逐层 compile 绿灯纪律**（D-P4-03）—— D-P5-04 按域分批直接复用。
- **前端 report 模板**（`templates/report/*`、`templates/ctr/contractReport.html`）—— 已照搬，调 `/rpt/...`，本期 Controller 落 admin 后天然对接。

### Established Patterns
- **照搬保包名**（D-P2-07）：`com.spt.bas.report.server.*` / `com.spt.bas.report.client.*` verbatim，最小化 import 改动。
- **逐层 compile 绿灯**（Phase 1 gotcha 级联教训）：每 wave 后 `mvn compile` 全模块零 `[ERROR]`（JDK 1.8.0_482）再继续；grep `^\[ERROR\]` locale 无关。
- **framework vs admin 边界**（D-P2-06 + D-08）：报表 Mapper+XML+service+reportClient → `zgbas-system`；55 报表 Controller + 模板 → `zgbas-admin`。
- **mybatis-plus**（D-P2-09）：报表用 mybatis-plus（随 spt-tools-mybatis 内联），与源 ReportServer 栈一致。
- **Feign 自回环**（D-P4-01 方案 A）：report 契约机制参照（D-P5-03 research 核实 55 api 路径是否满足契约）。
- **ddl-auto=none + 仅修运行阻塞**（D-P4-05）：报表实体 drift / `@Entity` 误拾仅修阻塞型。

### Integration Points
- `zgbas-system` ← 53 Mapper + XML（`mybatis/mappers/`）+ 54 service + reportClient 内联（entity/vo/remote）+ infra（util/vo/config 筛选）。
- `zgbas-admin` ← 55 report `api` Controller（保包名 `com.spt.bas.report.server.api`），`@Autowired` system 的 report service。
- `application.yml` ← 追加 `mybatis-plus.type-aliases-package` 的 report 包（`mapper-locations` 不变）。
- `@MapperScan` ← research 核实覆盖 `com.spt.bas.report.server.dao`（必要时放宽 basePackages）。
- `@EnableFeignClients` ← 已放宽（Phase 4），D-P5-03 接通 impl。
- `ZgbasApplication` ComponentScan `com.spt` 已覆盖 `com.spt.bas.report.*`，无需额外扫描声明。

</code_context>

<specifics>
## Specific Ideas

- 用户全程选「行为等价优先 + 最小改动 + 照搬 + 与早期阶段一致」：Controller 落位按 ROADMAP 字面（admin）、reportClient 内联（对齐 basClient）、Feign 契约接通（关闭 P3 缺口）、分页 1:1 等价、Excel 全量保行为 —— 与项目核心价值「搬运而非重造 + 行为对齐旧系统」完全一致。
- 8 个决策中无「偏离核心价值」项；2 个 Claude discretion（路径蒸发、mybatis 配置合并）均经研究消解为「照搬即等价」。
- 用户主动要求「再担一层」（Excel 导出/测序/分页保真/风险验证），体现对执行保真与测序的关注 —— research/planning 应把这些作为显式验收点，非默认假设。

</specifics>

<deferred>
## Deferred Ideas

- **xxl-job reportServer/task（1）+ command（1）+ listener（1，评估）** → Phase 6（QUARTZ-03）
- **真实报表/业务回归对照 + 浏览器 e2e** → Phase 7（ALIGN-01/02）
- **报表物理分页性能改造** → 永久 Out-of-Scope（PROJECT.md，259 表结构变更）
- **basWx 采购业务 + purchase 契约** → v2（#14，purchase 契约维持 D-P4-02 stub 降级）
- **全量实体 schema drift reconcile + 重开 ddl-auto=validate（D-P2-02 原意）** → tech debt（todo `phase4-resolve-entity-schema-drift` 保留 open）
- **CR-01 真实轮换已泄漏生产库密码** → 跨阶段安全债，仍 deferred

### Reviewed Todos (not folded)
- `rotate-leaked-prod-credentials.md`（CR-01 轮换已泄漏生产库密码）→ 跨阶段安全债，弱匹配 Phase 5（通用关键词），review 不折叠。
- `phase4-resolve-entity-schema-drift.md`（D-P2-02 偏离）→ Phase 4 降级保留 open；本期 D-P5-07 仅核 report 实体 `@Entity` 误拾（运行阻塞型），不全量 reconcile。

</deferred>

---

*Phase: 5-报表迁移*
*Context gathered: 2026-07-17*
