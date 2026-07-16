# Phase 2: 基础设施 - Context

**Gathered:** 2026-07-16
**Status:** Ready for planning

<domain>
## Phase Boundary

把 zgbas-plus 从 Phase 1 的"空 context 骨架"升级为**可用的基础设施层**，为 Phase 3+ 业务迁移提供地基。本阶段交付四件事：

1. **spt-tools 源码内联**（INLINE-01..04）：10 个被引用模块照搬进 `zgbas-common`，消除私服 jar 依赖。
2. **双 ORM 持久层**（PERSIST-01/03/04）：单 DataSource + `JpaTransactionManager @Primary`，JPA（259/~250 basServer 实体 + 254 Dao 全量迁入 `zgbas-system`）与 mybatis-plus（trivial sample Mapper 证明同源可执行）共存；审计字段行为保留。
3. **外部服务 Bean**（EXT-01..04）：`AuthOpenFacade / PushClientHttp / FileRemote / CfcaSignClient` 保持原 HTTP/OpenFeign 注入，配置项迁移到位。
4. **基础设施收敛**（INFRA-01/02/04）：删 nacos + Feign 服务发现（295 `@FeignClient` 进程内化机制建立 + trivial proof，bulk 转换推 Phase 4/5）；4 套配置收敛为单 `application.yml` + dev/prod profile，密钥环境变量外置 + 轮换。

**不在本期（明确边界）：**
- Shiro 认证 / 首页 → **Phase 3**
- 业务 Service / Controller / BFF 逻辑搬运 → **Phase 4**（Phase 2 只搬数据层：实体 + Dao，无业务逻辑）
- 53 套报表 Mapper + XML → **Phase 5**（Phase 2 只搭 mybatis-plus infra + 1 个 sample Mapper）
- 64 个 xxl-job handler 迁 RuoYi quartz → **Phase 6**
- basWx/purchase 实体（~11，v2 不迁）→ **不在本期任何阶段**
- 295 FeignClient 的 bulk 真实转换 → **Phase 4/5**（impl 就位后随业务落地，Phase 2 只建机制）

</domain>

<decisions>
## Implementation Decisions

### 持久层范围 (PERSIST-01/03/04)
- **D-P2-01:** Phase 2 **全量迁入** basServer ~250 实体 + 254 Dao 进 `zgbas-system`（纯数据层，无业务 Service/Controller 逻辑）。Phase 4 只在已有实体/Dao 上加业务逻辑。实体/Dao 迁移是机械活可批量。
- **D-P2-02:** **禁用 Flyway**（`spring.flyway.enabled=false`），`ddl-auto=validate`，单体指向现有 `sptbasdb_pd` schema（只校验不建表）。最小风险，不碰生产 schema 结构；实体/表偏差靠 validate 启动期暴露。
- **D-P2-03:** Phase 2 infra **启动验证为主** —— 单进程启动成功（DataSource 注入、`JpaTransactionManager @Primary`、mybatis-plus SqlSessionFactory 均起来、`validate` 不报错）即视为可用。不写查询正确性测试（留 Phase 4 业务接入时验）。
- **D-P2-04:** mybatis 范围 = **infra + trivial sample Mapper** —— 搭 mybatis-plus（starter + 复用同 DataSource 的 SqlSessionFactory + `@MapperScan`）+ 一个 trivial sample Mapper（如 `select count(*)`）证明 dual-ORM 同源可执行查询，满足 PERSIST-03 成功标准。53 套报表 Mapper 留 Phase 5。
- **D-P2-05:** 实体迁移来源**仅 basServer**（~250 实体 + Dao），**排除 basWx/purchase**（~11，v2 不迁，#14）。报表（mybatis 无 `@Entity`）与 web（不连 DB）不涉及。

### spt-tools 内联 (INLINE-01..04)
- **D-P2-06:** 10 个被引用 spt-tools 模块**全部内联进 `zgbas-common`**（core/data/http/file/jpa/web/mybatis/shiro/aop/config）；5 个零引用模块（elastic/redis/kafka/sdkutil/wechat4j）**跳过不内联**。`framework` 模块留给 **zgbas 自有 infra 接线**（DataSource `@Bean` / `JpaTransactionManager @Primary` / SqlSessionFactory / 数据源前缀绑定等 Phase 2 写，Shiro Realm Phase 3 写）。对齐 INLINE 字面 + 保持内联单元完整。
- **D-P2-07:** spt-tools **源码照搬**进 common，**保留源包结构 `com.spt.tools.*` verbatim**（core 75 类 + jpa 14 类 + 其余）。旧业务代码 835/1226 处引用在 Phase 4 搬运时 1:1 映射、零 import 改动。行为等价最稳。
- **D-P2-08:** spt-tools 三方依赖**pin 旧项目原版本**（根 pom `dependencyManagement`：Hutool 5.5.9 / fastjson 1.2.75 / Druid 1.2.8 / Shiro 1.8.0 等），Spring Boot 管理的栈顺 2.5.9 grandparent。行为等价优先于安全升级。
- **D-P2-09:** 双 ORM 的 mybatis 侧用 **mybatis-plus**（随 spt-tools-mybatis 内联），Phase 2 sample Mapper + Phase 5 报表均用 mybatis-plus。与旧 ReportServer 栈一致。

### Feign 进程内化 (INFRA-02)
- **D-P2-10:** 进程内化机制 = **接口即契约 + 目标 impl 满足接口**。保留 `@FeignClient` 接口作契约（去 Feign 运行时），让目标 Controller/Service 实现该接口注册为本地 bean；调用方继续 `@Autowired` 接口 → 进程内直调。调用方零改动、行为等价；bulk 295 转换随 Phase 4/5 impl 落地天然完成（RuoYi 风格）。
- **D-P2-11:** Phase 2 Feign 交付深度 = **机制/约定建立 + 删 nacos**（discovery 配置 + 19 文件引用 + 3 个 `nacos.common.utils` 工具类引用改 commons）**+ 删 Feign 服务发现 + 一个 trivial 端到端 proof**（假接口 + impl 验证 pattern 可编译可进程内调用）。**保留 OpenFeign starter** 供 `cfcaSignClient`（EXT-03）。295 真实转换推 Phase 4/5（impl 耦合）。
- **D-P2-12:** `@EnableFeignClients(basePackages=...)` **收窄到仅 `com.spt.sign.client.remote`**；295 内部接口不再被 Feign 扫描 → 作纯契约接口由本地 impl bean 满足，无 double-bean 冲突。复用 EXT-03 basePackages 配置。

### 配置收敛 + 密钥外置 (INFRA-04)
- **D-P2-13:** 密钥**环境变量占位 + 轮换** —— 敏感项（DB password / xxl accessToken / `spt.app.secretKey` / appCode）改为 `${DB_PASSWORD:}` 等占位，真实值不入 git；`application.yml` 只留占位 + 本地 dev 默认值。**轮换生产库密码**（技术债：jdbc.properties 明文密码已进 git 历史）。
- **D-P2-14:** profile = **dev + prod**：`application.yml`（公共 + 占位）+ `application-dev.yml`（本地默认值，不依赖环境变量）+ `application-prod.yml`（全占位，靠环境变量注入）。uat/test 弃用。
- **D-P2-15:** 数据源前缀**统一到 `spring.datasource.*`**（Druid 保留）。绑定属 zgbas infra → 放 `framework`（旧 `bas.datasource` 绑定在 basServer `FrameworkConfig`，**非 spt-tools**，故统一不违反 D-P2-07 照搬）。spt-tools-data `DataSourceCreator` 前缀无关，照搬不受影响。
- **D-P2-16:** 单体端口 **8080 + 根 `/`**，无 context-path（Phase 1 骨架默认，单体标准）。旧 web=80/basServer=8001/report=8002 三合一。

### Claude's Discretion
- **实体/Dao 落位 + 包结构**：实体/Dao 落 `zgbas-system`，包名对齐源 `com.spt.bas.*` verbatim（最小化搬运 import 改动，对齐 D-08 + "搬运而非重造"）。
- **spt-tools 内联分层节奏**：按已锁顺序 core→(data,http,file)→(jpa,web,mybatis,shiro,aop,config) 逐层内联，每层 `mvn compile` 绿灯再下一层（复用 Phase 1 "gotcha 级联" 教训，避免一次性 1200+ 引用 unmask 雪崩）。
- **JPA 二级缓存 / show-sql / Druid 池参数**：照搬旧 `application.properties` 配置（ehcache 二级缓存 `ENABLE_SELECTIVE`、show-sql=false、Druid 池参数），行为等价优先。
- **外部 URL 配置迁移**（`spt.qxb.server.url` / `auth.url` / `push.server.url` / `file.server.url`）：迁入 `application.yml` 作占位，EXT-04 落地。
- **xxl-job 配置键**（`xxl.job.*`）：Phase 2 配置收敛时一并从 application.yml 移除（handler 迁移见 Phase 6）。
- **actuator/health 端点**：Phase 2 仍不加（延用 Phase 1 最小启动）；Phase 3+ 需启动信号时再加。

</decisions>

<canonical_refs>
## Canonical References

**Downstream agents MUST read these before planning or implementing.**

### 项目规划（仓库内，必读）
- `.planning/ROADMAP.md` §Phase 2 — 阶段目标 / 依赖（Phase 1）/ 需求映射 / 5 条成功标准
- `.planning/REQUIREMENTS.md` — INLINE-01..04 / PERSIST-01/03/04 / EXT-01..04 / INFRA-01/02/04（本期 14 需求）
- `.planning/PROJECT.md` — 源架构（4 微服务）、版本基线、spt-tools 内联依赖树、外部 Bean 注入模式、已知技术债（jdbc.properties 明文密码）
- `.planning/phases/01-compile-skeleton/01-CONTEXT.md` — **D-01..D-11**（Phase 1 锁定决策：扁平 parent / 模块拓扑 D-08 / 防环 D-09 / fat-jar-only-admin），Phase 2 在其上构建
- `.planning/phases/01-compile-skeleton/01-01-SUMMARY.md` — Phase 1 骨架成品（5 模块 / PackageMarker 占位 / 空启动），Phase 2 起点
- `CLAUDE.md` — 技术栈约束（JDK 1.8 / SpringBoot 2.5.9 / Maven 3.8.6 / zg_settings.xml）、固定 5 模块、私服与本地仓库位置

### 源项目（搬运参考，**绝对路径，非本仓库内**）
- `/Users/alan/WorkSpace/IDEA/tools` — **spt-tools 源码**（Phase 2 内联来源；只内联 10 个被引用模块：core/data/http/file/jpa/web/mybatis/shiro/aop/config，跳过 elastic/redis/kafka/sdkutil/wechat4j）
  - `spt-tools-core`（75 类，依赖根，最先内联）
  - `spt-tools-jpa`（14 类，1226 处引用，最难，含 `IdEntity`/`BaseDao`/审计 `@EntityListeners`）
  - `spt-tools-data/src/main/java/com/spt/tools/data/util/DataSourceCreator.java`（前缀无关，吃 bound config）
  - `spt-tools-mybatis`（mybatis-plus 栈）
- `/Users/alan/WorkSpace/IDEA/zgbas`（分支 `feat-系统重构v5.0`）— 源微服务
  - `basCore/basServer/src/main/resources/` — `jdbc.properties`（`bas.datasource.*` + 明文密码技术债）/ `application.properties`（Flyway/nacos/JPA/ehcache/xxl-job 配置）/ `application-{dev,uat,prod,test}.properties`
  - `basCore/basServer/src/main/java/com/spt/bas/server/config/FrameworkConfig.java` — `@ConfigurationProperties(prefix="bas.datasource")`（前缀绑定在此，**非 spt-tools**，Phase 2 迁 framework 并改前缀）
  - `basCore/basClient/`（238 FeignClient 契约）/ `basReport/reportClient/`（54 FeignClient 契约）
- `/Users/alan/WorkSpace/IDEA/spt-auth` — RuoYi 改造参考（`auth-quartz` + `ScheduleConfig` 供 Phase 6；单体参考框架）

### 构建 / 工具链（绝对路径）
- `/Users/alan/App/apache-maven-3.8.6` — Maven 可执行
- `/Users/alan/App/apache-maven-3.8.6/zg_settings.xml` — 私服仓库重定向 settings（构建必用）
- `/Users/alan/App/Repository` — 本地仓库（外部 HTTP SDK jar：auth-sdk / spt-push-sdk / spt-file-sdk / spt-sign-client）

</canonical_refs>

<code_context>
## Existing Code Insights

> zgbas-plus 当前为 **Phase 1 骨架**（5 模块 Maven 聚合 + `PackageMarker` 占位 + `ZgbasApplication` 空 context + 最小 `application.yml`），无真实业务/infra 代码。下列洞察指向**源项目 + spt-tools 源码**结构，指导本期从骨架搭建 infra。

### Reusable Assets
- **Phase 1 骨架**（本仓库）：5 模块拓扑（D-08）+ 扁平根 parent（继承 `spring-boot-starter-parent:2.5.9`，已断 spt-parent 链）+ `common` 占位包 `com.spt.tools.core`（**正好对齐 Phase 2 spt-tools 内联目标**）+ `ZgbasApplication`（`com.spt`，`@SpringBootApplication`）+ `application.yml`（8080 / dev / 无 datasource）。
- **spt-tools 源码**（`/Users/alan/WorkSpace/IDEA/tools`）：10 模块直接照搬，含 `IdEntity`/`BaseDao`（JPA 体系根）、`DataSourceCreator`（Druid 创建器，前缀无关）、mybatis-plus 栈、Shiro/web/aop 工具。

### Established Patterns
- **逐层内联 + compile 绿灯**（Phase 1 gotcha 级联教训）：core 先 → compile → data/http/file → compile → jpa/web/mybatis/shiro/aop/config，避免 1200+ 引用一次性 unmask 雪崩。
- **接口即契约**（D-P2-10）：FeignClient 接口保留作契约，目标 Controller/Service 实现接口注册本地 bean —— Spring MVC 认可接口上的 `@RequestMapping`，impl 只需 `implements`。
- **zgbas infra vs spt-tools 内联的边界**：spt-tools 源码 → `common`（照搬）；zgbas 自有 infra 接线（DataSource bean / 事务管理 / 前缀绑定）→ `framework`。两者不混淆。
- **私服 jar 保留范围**：外部 HTTP SDK（auth/push/file/sign）保留 jar 依赖（D-05，EXT-01..04），仅 spt-tools 内联消除。

### Integration Points
- `zgbas-common` ← spt-tools 10 模块照搬落位（`com.spt.tools.*`），为 Phase 4 业务实体（`IdEntity`/`BaseDao` 体系）与 1226 处引用提供基础。
- `zgbas-framework` ← zgbas infra 接线（DataSource `@Bean` / `JpaTransactionManager @Primary` / mybatis-plus SqlSessionFactory / 数据源前缀绑定 `spring.datasource.*`），Phase 3 再加 Shiro。
- `zgbas-system` ← basServer ~250 实体 + 254 Dao + mybatis-plus sample Mapper 落位。
- `zgbas-admin` ← 启动模块，`@EnableFeignClients(basePackages="com.spt.sign.client.remote")` 收窄声明 + `application.yml`/`application-dev.yml`/`application-prod.yml` 配置收敛归宿。
- `ZgbasApplication` ← ComponentScan `com.spt` 已占位，覆盖 `com.spt.tools.*`（内联）/ `com.spt.bas.*`（Phase 4 业务）/ `com.spt.sign.client.remote`（cfca Feign）。

</code_context>

<specifics>
## Specific Ideas

- 用户对私服依赖的态度是**主动消除**：内联 spt-tools + 去 spt-parent；但**接受**外部 HTTP SDK 暂留 jar（因它们是外部 HTTP 集成 Bean，非内联目标）。
- 用户全程选"行为等价优先"（全量迁入 / pin 旧版本 / mybatis-plus / 照搬保包名），与项目核心价值"搬运而非重造 + 行为对齐旧系统"一致。
- 安全技术债（jdbc.properties 明文密码 + xxl accessToken 进 git）用户确认**环境变量外置 + 轮换**处理，不掩盖。
- Phase 2 是最大阶段（14 需求），用户接受其体量，未要求拆分 —— 持久层全量迁入、spt-tools 全量内联均在本期一次到位，为 Phase 3+ 铺完整地基。

</specifics>

<deferred>
## Deferred Ideas

- **Shiro 认证 / 首页 / 动态菜单** → Phase 3（AUTH-01..04）
- **业务 Service / Controller / BFF 逻辑** → Phase 4（BIZ-01..03；Phase 2 只搬数据层）
- **53 套报表 Mapper + XML** → Phase 5（REPORT-01..02 / PERSIST-02）
- **295 FeignClient bulk 真实转换** → Phase 4/5（随业务 impl 落地；Phase 2 只建机制 + trivial proof）
- **64 xxl-job handler 迁 RuoYi quartz + 删 xxl-job 依赖** → Phase 6（QUARTZ-01..04 / INFRA-03；Phase 2 仅从 application.yml 移除 xxl.job 配置键）
- **basWx/purchase 实体 + Dao（~11）** → v2（#14，第一阶段任何阶段都不迁）
- **actuator/health 端点** → Phase 3+ 需启动信号时加
- **报表物理分页性能改造 / 补 createBy-updateBy 审计字段** → 不在本期（PROJECT.md Out of Scope，涉及 259 表结构变更）

</deferred>

---

*Phase: 2-基础设施*
*Context gathered: 2026-07-16*
