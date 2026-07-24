# zgbas-plus

## What This Is

zgbas-plus 是供应链核心管理平台 **zgbas** 的单体化重构版。源项目 `zgbas`（分支 `feat-系统重构v5.0`）是 Spring Cloud 微服务，需启动 4 个服务（Web / BasServer / ReportServer / PurchaseWx）才能跑全功能；zgbas-plus 将其收敛为**单进程、单服务启动即可跑通全部业务**的 Maven 聚合单体（5 模块），面向降低部署与运维复杂度，**业务行为与旧系统等价**。

**当前状态：** v1.1 shipped（2026-07-22）— quartz 前端全功能可用，9/9 browser UAT PASS。v1.0 7 阶段 37 plans + v1.1 2 阶段 6 plans，共 43 plans 交付完成。

## Core Value

**一个可独立部署、单进程启动即可跑通全部供应链业务（登录 → 核心业务 → 报表 → 定时任务）的单体应用，行为对齐旧系统。**

当出现取舍时优先保住这条：单体单服务跑全功能 + 行为等价，优先于"技术先进性 / 框架升级"。

## Current Milestone: v1.2 basWx 迁入

**Goal:** 将 PurchaseWxServer 微信采购小程序完整迁入单体，实现全业务单进程覆盖（含采购小程序 BFF）。

**Target features:**
- 11 JPA 实体 + BaseDao（purchase.entity / purchase.dao）迁入 zgbas-system
- purchase-client 契约内联（IBasPurchase* Feign 接口，消除 2.0.1-SNAPSHOT 私服依赖）
- 采购业务 service 层（PurchaseWxServer service impl）全量迁入
- 小程序 BFF controller（WxController 等）迁入，提供采购小程序 API 端点

## Next Milestone Goals

候选方向（优先级供参考，v1.2 后处理）：
- Phase 6 28 生产 handler quartz 路由 gap-closure（QTZ-11：38 条排除数据 review）
- Phase 4 entity schema drift 修复（259 表 ddl-auto=none → validate）
- 生产库明文密码轮换（安全）
- 启动测试 hermetic 化（CI/CD 支持）
- 考虑 JDK 17 / Spring Boot 3 升级路径（javax→jakarta，259 实体影响大）

## Requirements

### Validated

- ✓ 合同 / 授信 / 库存 / 放款等核心供应链业务（源：basServer，JPA）— *v1.0 Phase 4（533 service + 238 contract + 224 api + 267 BFF）*
- ✓ 复杂报表查询：53 套 mybatis 报表全量迁入 — *v1.0 Phase 5*
- ✓ 登录认证与动态菜单首页（Shiro session+cookie，经 auth-sdk 调外部 spt-auth）— *v1.0 Phase 3*
- ✓ 64 个定时任务（xxl-job→RuoYi quartz，32 handler 类迁入，53 sys_job 翻译落库）— *v1.0 Phase 6*
- ✓ 外部服务集成：auth-sdk / spt-push-sdk / spt-file-sdk / spt-sign-client — *v1.0 Phase 2*
- ✓ spt-tools-* 10 模块全量内联（172 类）— *v1.0 Phase 2*
- ✓ 双 ORM 单 DataSource 共存 — *v1.0 Phase 2*
- ✓ nacos 删除 + 295 Feign 进程内化（D-P4-01 方案 A 自回环）— *v1.0 Phase 2/4*
- ✓ 5 模块 Maven 聚合单体 + 单进程启动 — *v1.0 Phase 1*
- ✓ 全链路端到端可用 + 行为对齐旧系统 (ALIGN-01/02) — *v1.0 Phase 7*
- ✓ quartz 前端完整管理页（CRUD + 日志查看），browser 9/9 UAT — *v1.1 Phase 1+2 (QTZ-01~08)*

### Active

- [ ] **[v1.2]** basWx 微信采购小程序迁入（11 实体 + Dao + purchase-client 契约 + service + BFF controller）
- [ ] Phase 6 quartz 路由 gap-closure（28 生产 handler，38 条排除数据 review）
- [ ] Phase 4 entity schema drift 修复（ddl-auto=none → validate，259 表）
- [ ] 启动测试 hermetic 化（CI/CD 支持）
- [ ] 生产库明文密码轮换（application-dev.yml 明文现状，安全）

### Out of Scope

<!-- 显式边界，附理由防回加 -->

- **basWx 微信采购小程序迁入** — ~~第一阶段不迁~~ → **迁入 v1.2**（11 实体 + Dao + purchase-client + service + BFF）
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
| 参考框架 = **RuoYi 单体**（非 yudao） | yudao 需 Spring Boot 3 / JDK 17 / jakarta，与 JDK 1.8 + javax + Shiro + spt-tools 硬冲突；RuoYi 同源（spt-auth 即 RuoYi 系），quartz 可复用 | ✓ Good — quartz 整模块复用成功 |
| 认证**保持外部 spt-auth**（HTTP） | 符合用户 #7"外部 Bean 保持原方式"，改动最小；spt-auth 作共享基础设施 | ✓ Good |
| Spring Boot **2.5.9 / JDK 1.8 锁定不动** | 用户要求"尽量不变"；升级牵动 259 实体 javax→jakarta，风险高 | ✓ Good — Phase 1-7 验证零版本兼容问题 |
| 双 ORM：单 DataSource + `JpaTransactionManager` @Primary | 两微服务本就连同一 schema；单源下 mybatis 自动加入 JPA 事务，无需 JTA | ✓ Good |
| 打包：仅 zgbas-admin 产可执行 **fat jar**，弃旧 layout=ZIP 瘦 jar 策略 | 旧"主 jar + libs/ + config"三件套运维复杂；单体用标准 fat jar 更简单 | ✓ Good |
| spt-tools 内联顺序 core→(data,http,file)→(jpa,web,mybatis,shiro,aop) | core 是依赖根，阻塞其他；jpa 引用最广留后 | ✓ Good — 172 类全量内联完成 |
| xxl-job → RuoYi quartz（整模块复制 spt-auth/auth-quartz） | 用户 #10；现成实现可降工作量 | ✓ Good — 32 handler 类迁入，53 sys_job 落库 |
| D-P4-01 方案 A Feign 自回环 | 238 @FeignClient url=localhost:8080 同进程直调，无路由依赖 | ✓ Good — WR-02 HTTP proof 绿 |
| Phase 3 启动测试非 hermetic（Option 4） | 本地 export DB_PASSWORD + SPT_APP_SECRET 即可通过 | ⚠️ Revisit — CI/CD 需 hermetic 化

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

## Progress

- **Phase 1（编译止血 + 骨架）— COMPLETE 2026-07-16。** 5 模块 Maven 聚合单体骨架就位（`zgbas-admin/common/framework/quartz/system`），根 parent 继承 `spring-boot-starter-parent:2.5.9`（**spt-parent / bas-parent / spt-tools-parent BOM 已消除** —— 新扁平 parent，脱离私服 parent 链），D-08 模块拓扑接线完成（`common←(无) / framework←common / system←common,framework / quartz←common,framework,system / admin←all`），单一 `@SpringBootApplication`（`com.spt.ZgbasApplication`）起空 Spring context，仅 `zgbas-admin` 产 fat jar。`mvn compile` 全模块零 `[ERROR]`（JDK 1.8.0_482，ALIGN-03 编译止血基线达成）。骨架为空 context（无 DB / 外部 SDK / 业务代码）—— 这些在后续阶段引入。
  - **关键决策新增（ discuss 阶段锁定）：** D-01..03 消除 spt-parent，改扁平 parent 继承 spring-boot-starter-parent:2.5.9。
  - **下一阶段：Phase 2（基础设施）** — spt-tools 内联进 common、双 ORM 单 DataSource、外部 Bean 保持 HTTP 注入、删 nacos、295 Feign 进程内化、配置收敛。

- **Phase 2（基础设施）— COMPLETE 2026-07-16。** 6/6 plans，14/14 需求 MET（INLINE-01..04 / PERSIST-01/03/04 / EXT-01..04 / INFRA-01/02/04）。spt-tools 10 模块全量内联进 `zgbas-common`（172 类，零 spt-tools-* jar 声明）；双 ORM 单 `@Primary` Druid DataSource 共存（JPA 239 实体 + 240 Dao 审计链完整 + mybatis-plus sample Mapper 同源可查）；外部 SDK 3 bean 保持原 HTTP 注入（secret log 已删）；nacos 删除；Feign 收窄到 `com.spt.sign.client.remote` + 接口即契约机制 + trivial proof；4 配置收敛为 `application.yml` + dev/prod profile（密钥环境变量外置）。Capstone `@SpringBootTest` 全 context 启动 6/6 GREEN（`DB_PASSWORD`/`SPT_APP_SECRET` 走环境变量）。
  - **延后债务（已记入 todos + 02-LEARNINGS）：** ddl-auto=none（D-P2-02 validate 暴露 239 实体 schema drift，259 表修复留 Phase 4）、WR-02 接口契约 HTTP proof（留 Phase 4）、CR-01 轮换已泄漏的生产库密码（outward-facing）。
  - **下一阶段：Phase 3（认证首页）** — Shiro session+cookie 认证、动态菜单首页、`ToolsShiroConfig` 重新启用 + Realm 接线。

- **Phase 5（承托层迁入，v1.2）— COMPLETE 2026-07-24。** 6/6 plans，SC#3 编译门 `mvn compile -pl zgbas-system` BUILD SUCCESS / 0 `[ERROR]`。承托层(payload 22 / vo 18 / enums 1 / common 8 / util 16 / exception 3 / aop / config 5 / cache 2 / OCR wrapper 2)全量落 `zgbas-system` 的 `com.spt.bas.purchase.wx.server` 包飞地；4 个 P4 stub(UserInfoVo/ApiResult/BaseException/SecurityException/ResponseUtil/UserContext)以源实测替换(D-P5-14)；横切 bean 安全落位(GlobalExceptionHandler 限域 sole `@ControllerAdvice`、CORS 并入单体 WebAppConfig 无重复 multipart、ServiceAop 显式 `@Bean` + pointcut 自限 WX、FrameworkConfig 剥减为 WxCarrierConfig)；启动接线 ApplicationStartup 并入 WX `BsDictUtil.init`(Phase 3 登录缺口同源修复)+ RequestListener ThreadLocal 清理；D-15a/b 五类承托 inventory 交付(SC#4)。WX 外部集成密钥(ewechat/aliyun.ocr/jinxin)明文落 dev yml(D-P5-05)。
  - **延后:** EweChatApi/JinXinApi → Phase 6(承托依赖 EweChatConfig/RedisCache/JinXinConfig 已就位)；IBsDictService 接口已迁(impl → Phase 6)；PurchaseCommand → Phase 6。
  - **🔴 延后债务:** rotate-leaked-prod-credentials 新增 EweChat corpsecret / Aliyun OCR appcode / JinXin RSA 私钥+keystore 密码 三组明文(D-P5-05，待轮换；JinXin RSA 私钥最敏感)。SC#2 与 D-P5-05 冲突由用户锁定明文策略覆盖。
  - **下一阶段：Phase 6（Service 层迁入）** — ~20 service impl + iface + BaseService 适配 → system。

*Last updated: 2026-07-24 — v1.2 Phase 5 (承托层迁入) complete*
