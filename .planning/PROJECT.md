# zgbas-plus

## What This Is

zgbas-plus 是供应链核心管理平台 **zgbas** 的单体化重构版。源项目 `zgbas`（分支 `feat-系统重构v5.0`）是 Spring Cloud 微服务，需启动 4 个服务（Web / BasServer / ReportServer / PurchaseWx）才能跑全功能；zgbas-plus 将其收敛为**单进程、单服务启动即可跑通全部业务**的 Maven 聚合单体（5 模块），面向降低部署与运维复杂度，**业务行为与旧系统等价**。

## Core Value

**一个可独立部署、单进程启动即可跑通全部供应链业务（登录 → 核心业务 → 报表 → 定时任务）的单体应用，行为对齐旧系统。**

当出现取舍时优先保住这条：单体单服务跑全功能 + 行为等价，优先于"技术先进性 / 框架升级"。

## Requirements

### Validated

<!-- 旧系统已验证、新单体必须原样保留的业务能力（搬运而非重造） -->

- ✓ 合同 / 授信 / 库存 / 放款等核心供应链业务（源：basServer，JPA）— existing
- ✓ 复杂报表查询：合同台账、收付款/开票/结算、风控、业绩人效、销售区域统计、预算决算等 53 套报表（源：basReport，mybatis）— existing
- ✓ 登录认证与动态菜单首页（Shiro session+cookie，经 auth-sdk 调外部 spt-auth）— existing
- ✓ 64 个定时任务（合同调度、自动盖章、结算、数据同步等，源 xxl-job）— existing
- ✓ 外部服务集成：认证(auth-sdk)、推送(spt-push-sdk)、文件(spt-file-sdk)、电子签(spt-sign-client) — existing
- ✓ 微信采购小程序 basWx（purchase-server）— existing，**第一阶段不迁**

### Active

<!-- 本次重构目标 -->

- [ ] 微服务 4 服务 → **单体 1 服务**，单进程启动跑全功能
- [ ] 打包从 4 可执行 jar → **少数 jar**（仅 zgbas-admin 产可执行 fat jar）
- [ ] 5 模块聚合单体：`zgbas-admin / zgbas-common / zgbas-framework / zgbas-quartz / zgbas-system`
- [ ] **双 ORM 兼容**：JPA 增删改查主力 + mybatis 复杂报表，单数据源共存
- [ ] `spt-tools-*` 代码**内联**进 zgbas-common（摆脱私服 jar 依赖）
- [ ] 外部服务 Bean（AuthOpenFacade / PushClientHttp / FileRemote / CfcaSignClient）**保持原 HTTP 注入方式**
- [ ] **删除 nacos**（服务发现 + 相关配置）
- [ ] **xxl-job → RuoYi quartz**（zgbas-quartz 模块）
- [ ] 登录接口 + 首页**完全照搬**旧项目实现
- [ ] JDK 1.8 + Spring Boot 2.5.9 版本基线不变

### Out of Scope

<!-- 显式边界，附理由防回加 -->

- **basWx 微信采购小程序迁入** — 第一阶段不迁，留待**第二阶段**（独立小程序 BFF + 采购业务，11 实体 + Dao）
- **合并 spt-auth 进单体** — 认证保持外部 HTTP 调用（符合用户决策 #7），spt-auth 作为共享基础设施独立部署
- **Spring Boot 3 / JDK 17 大版本升级** — `javax.* → jakarta.*` 迁移影响 259 实体 + 254 Dao，与 JDK 1.8 硬约束冲突；待单体稳定后另行评估
- **yudao 框架** — 需 Spring Boot 3 / JDK 17 / jakarta，与现有技术栈不兼容，已排除（详见 Key Decisions）
- **报表物理分页改造** — 旧自研 PageHelper 是结果包装器（非 SQL 分页），单体化时仅保持行为等价，性能优化另行评估
- **补 createBy/updateBy/逻辑删除等审计字段** — 旧实体仅有 createdDate/updatedDate，补字段涉及 259 表结构变更，本次不做

## Context

**源项目**：`/Users/alan/WorkSpace/IDEA/zgbas`，分支 `feat-系统重构v5.0`。

**旧架构（4 微服务 + 5 client lib）**：
| 服务 | 端口 | 职责 | ORM |
|------|------|------|-----|
| `Web` | 80 | BFF + UI（Thymeleaf + Shiro + WebSocket），不连 DB | 无 |
| `BasServer` | 8001 | 核心业务（合同/授信/库存/放款）| JPA + Hibernate + Flyway |
| `ReportServer` | 8002 | 报表（53 套）| MyBatis（mybatis-plus）|
| `PurchaseWxServer` | 8013 | 微信采购小程序 | JPA |

**版本基线**（来自 `spt-parent 2.5.3-SNAPSHOT` + `bas-parent`）：JDK 1.8 · Spring Boot 2.5.9 · Spring Cloud 2020.0.5 · nacos 2021.0.1.0 · Shiro 1.8.0 · mybatis-plus（经 spt-tools）· xxl-job 2.3.0 · Druid 1.2.8 · Hutool 5.5.9 · fastjson 1.2.75。**当前无 quartz**（用 Spring `@EnableScheduling`）。

**单体化关键利好**：
1. **295 个 `@FeignClient` 全部带 `url=` 直连**，不走 nacos 路由 → 删 nacos 零业务影响。
2. JPA 服务与报表服务**连同一 MySQL schema**（`sptbasdb_pd`）→ 单体内合并为单一 DataSource，`JpaTransactionManager` 设 `@Primary`，mybatis 复用同源，双 ORM 共存风险低。

**持久层体量**：259 JPA 实体（全继承 `IdEntity`：自增主键 + `createdDate/updatedDate`，无 createBy/逻辑删除/乐观锁，`javax.persistence`）+ 254 Dao（继承 spt-tools `BaseDao` = `JpaRepository + JpaSpecificationExecutor`）+ 53 mybatis 报表 Mapper+XML。

**spt-tools-* 内联**（源码 `/Users/alan/WorkSpace/IDEA/tools`，BOM `1.1.1-SNAPSHOT`）：依赖树 `core`(根,70类) → `data`/`http`/`file` → `jpa`(14类但 1226 处引用,最难)/`web`/`mybatis`/`shiro`/`aop`/`config`。core 是阻塞所有其他的根，须最先内联。

**外部服务 Bean（保持原注入）**：
- `authOpenFacade`(auth-sdk 3.8.2)、`pushClientHttp`(spt-push-sdk)、`fileRemote`(spt-file-sdk)：`@Bean init(secretKey, appCode, url)` 模式，配 `spt.app.secretKey` / `spt.app.appCode` / `*.url`。
- `cfcaSignClient`(spt-sign-client)：OpenFeign，需保留 `@EnableFeignClients(basePackages=..., "com.spt.sign.client.remote")`。

**认证**：Shiro session+cookie（非 JWT），密码 SHA-1+盐 1024 次迭代。zgbas 本身无用户/菜单表，登录与菜单经 auth-sdk HTTP 调外部 spt-auth（RuoYi 改造，`/Users/alan/WorkSpace/IDEA/spt-auth`）。**决策：保持外部**。

**xxl-job → quartz**：64 个 `@XxlJob`（basServer 57 / purchase 5 / report 1 / web 1），约 5–6 人天；spt-auth/auth-quartz 已有现成 RuoYi quartz 实现可整模块复制 + `sys_job`/`sys_job_log` 表 DDL。

**已知风险/技术债**：
- ⚠️ `jdbc.properties` 把**生产库明文密码**提交进了 git 历史（`sptbasdb_pd`）→ 重构时轮换并外置。
- ⚠️ 旧自研报表 `PageHelper` 是结果包装器（内存分页），迁单体仅保行为等价。
- ⚠️ mock-password 后门（`super:<mock-password>` 可模拟任意用户登录）→ 照搬时评估是否保留。

## Constraints

- **Tech stack**: JDK 1.8 + Spring Boot 2.5.9（用户硬要求"Spring 尽量不变"，已锁定）— 大版本升级与 javax→jakarta 迁移阻塞，不在本期。
- **Build**: Maven `/Users/alan/App/apache-maven-3.8.6`，settings `/Users/alan/App/apache-maven-3.8.6/zg_settings.xml`（私服仓库重定向）。
- **Module structure**: 固定 5 模块聚合单体 `zgbas-admin / common / framework / quartz / system`（用户指定）。
- **Persistence**: 必须双 ORM（JPA 主力 + mybatis 报表）共存（用户指定 #6）。
- **External integration**: auth/push/file/sign 外部 Bean 保持原 HTTP 注入（用户指定 #7）；spt-auth 保持外部。
- **Removed infra**: nacos 删除（#9）；xxl-job 删除改 RuoYi quartz（#10）。
- **Reference framework**: RuoYi 单体（yudao 因 JDK/版本冲突排除）。
- **Scope**: basWx 第一阶段不迁（#14）。
- **Private repo**: `spt-tools-*`、`auth-sdk`、`spt-push-sdk`、`spt-file-sdk`、`spt-sign-client`、`spt-parent` 均为公司私服 SNAPSHOT，本地仓库在 `/Users/alan/App/Repository`；内联 spt-tools 可消除其 jar 依赖。

## Key Decisions

| Decision | Rationale | Outcome |
|----------|-----------|---------|
| 参考框架 = **RuoYi 单体**（非 yudao） | yudao 需 Spring Boot 3 / JDK 17 / jakarta，与 JDK 1.8 + javax + Shiro + spt-tools 硬冲突；RuoYi 同源（spt-auth 即 RuoYi 系），quartz 可复用 | — Pending |
| 认证**保持外部 spt-auth**（HTTP） | 符合用户 #7"外部 Bean 保持原方式"，改动最小；spt-auth 作共享基础设施 | — Pending |
| Spring Boot **2.5.9 / JDK 1.8 锁定不动** | 用户要求"尽量不变"；升级牵动 259 实体 javax→jakarta，风险高 | — Pending |
| 双 ORM：单 DataSource + `JpaTransactionManager` @Primary | 两微服务本就连同一 schema；单源下 mybatis 自动加入 JPA 事务，无需 JTA | — Pending |
| 打包：仅 zgbas-admin 产可执行 **fat jar**，弃旧 layout=ZIP 瘦 jar 策略 | 旧"主 jar + libs/ + config"三件套运维复杂；单体用标准 fat jar 更简单 | — Pending |
| spt-tools 内联顺序 core→(data,http,file)→(jpa,web,mybatis,shiro,aop) | core 是依赖根，阻塞其他；jpa 引用最广留后 | — Pending |
| xxl-job → RuoYi quartz（整模块复制 spt-auth/auth-quartz） | 用户 #10；现成实现可降工作量 | — Pending |

## Evolution

This document evolves at phase transitions and milestone boundaries.

**After each phase transition** (via `/gsd-transition`):
1. Requirements invalidated? → Move to Out of Scope with reason
2. Requirements validated? → Move to Validated with phase reference
3. New requirements emerged? → Add to Active
4. Decisions to log? → Add to Key Decisions
5. "What This Is" still accurate? → Update if drifted

**After each milestone** (via `/gsd:complete-milestone`):
1. Full review of all sections
2. Core Value check — still the right priority?
3. Audit Out of Scope — reasons still valid?
4. Update Context with current state

---
*Last updated: 2026-07-16 after initialization*
