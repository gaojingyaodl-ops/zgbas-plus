# Phase 2: 基础设施 - Research

**Researched:** 2026-07-16
**Domain:** Maven multi-module infra wiring — spt-tools 源码内联 / 双 ORM 单 DataSource / 外部 Bean HTTP 注入 / Feign 进程内化机制 / 配置收敛
**Confidence:** HIGH (verified by reading source projects at absolute paths and the zgbas-plus skeleton)

## Summary

Phase 2 turns the empty Phase 1 skeleton into a usable infrastructure layer. Four mechanical-but-large deliverables: (1) inline 10 spt-tools modules (~172 main java classes) verbatim into `zgbas-common`; (2) dual ORM (JPA + mybatis-plus) sharing one Druid DataSource, wired in `zgbas-framework`, with ~239 basClient entities + 240 basServer Dao bulk-copied into `zgbas-system`; (3) the 3 external HTTP SDK beans (`AuthOpenFacade` / `PushClientHttp` / `FileRemote`) re-declared in `framework` via the `env.getProperty + init(secretKey,appCode,url)` pattern; (4) nacos + Feign service-discovery deleted, 295 FeignClient "interface-as-contract" mechanism established with one trivial proof, OpenFeign retained only for `com.spt.sign.client.remote`.

**Primary recommendation:** Treat Phase 2 as 5 mechanical waves — Wave 0 (root pom version pins + external SDK deps + framework infra @Beans), Wave 1 (spt-tools inline, layered compile gates), Wave 2 (DataSource / JPA / mybatis-plus wiring in framework), Wave 3 (entity + Dao bulk copy into system), Wave 4 (sample Mapper + admin boot annotations + config consolidation + startup verification). Every wave ends with `mvn compile` green; the phase ends with a single `@SpringBootTest contextLoads` that brings up the full context (DataSource + JPA + mybatis + sample Mapper query + sign Feign + 3 external beans).

**Critical correction to CONTEXT.md / PROJECT.md:** the ~239 `@Entity`-annotated entities live in `com.spt.bas.client.entity.*` inside the **basClient** module (NOT basServer). The 240 Dao live in `com.spt.bas.server.dao.*` inside basServer and `import com.spt.bas.client.entity.*` to reference entities. Phase 2 must copy BOTH packages into `zgbas-system` (entity + Dao, both verbatim). D-P2-01 says "basServer ~250 entities + 254 Dao" — the spirit is correct (data-layer migration from the basCore aggregator), the literal module attribution is off by one (entities are in basClient.entity). Verified: `grep -rl "@Entity" basServer/...` returns 0 files; `grep -rl "@Entity" basClient/...` returns 234 files. `[VERIFIED: codebase grep at /Users/alan/WorkSpace/IDEA/zgbas/basCore/{basClient,basServer}/src/main/java]`

<user_constraints>
## User Constraints (from CONTEXT.md)

### Locked Decisions (research honors, does NOT re-litigate)

**持久层范围 (PERSIST-01/03/04)**
- **D-P2-01:** Phase 2 全量迁入 basServer ~250 实体 + 254 Dao 进 `zgbas-system`（纯数据层，无业务 Service/Controller 逻辑）。Phase 4 只在已有实体/Dao 上加业务逻辑。实体/Dao 迁移是机械活可批量。
- **D-P2-02:** 禁用 Flyway（`spring.flyway.enabled=false`），`ddl-auto=validate`，单体指向现有 `sptbasdb_pd` schema（只校验不建表）。最小风险，不碰生产 schema 结构；实体/表偏差靠 validate 启动期暴露。
- **D-P2-03:** Phase 2 infra 启动验证为主 —— 单进程启动成功（DataSource 注入、`JpaTransactionManager @Primary`、mybatis-plus SqlSessionFactory 均起来、`validate` 不报错）即视为可用。不写查询正确性测试（留 Phase 4 业务接入时验）。
- **D-P2-04:** mybatis 范围 = infra + trivial sample Mapper —— 搭 mybatis-plus（starter + 复用同 DataSource 的 SqlSessionFactory + `@MapperScan`）+ 一个 trivial sample Mapper（如 `select count(*)`）证明 dual-ORM 同源可执行查询，满足 PERSIST-03 成功标准。53 套报表 Mapper 留 Phase 5。
- **D-P2-05:** 实体迁移来源仅 basServer（~250 实体 + Dao），排除 basWx/purchase（~11，v2 不迁，#14）。报表与 web 不涉及。

**spt-tools 内联 (INLINE-01..04)**
- **D-P2-06:** 10 个被引用 spt-tools 模块全部内联进 `zgbas-common`（core/data/http/file/jpa/web/mybatis/shiro/aop/config）；5 个零引用模块（elastic/redis/kafka/sdkutil/wechat4j）跳过不内联。`framework` 模块留给 zgbas 自有 infra 接线（DataSource `@Bean` / `JpaTransactionManager @Primary` / SqlSessionFactory / 数据源前缀绑定等 Phase 2 写，Shiro Realm Phase 3 写）。
- **D-P2-07:** spt-tools 源码照搬进 common，保留源包结构 `com.spt.tools.*` verbatim。旧业务代码 835/1226 处引用在 Phase 4 搬运时 1:1 映射、零 import 改动。
- **D-P2-08:** spt-tools 三方依赖 pin 旧项目原版本（根 pom `dependencyManagement`：Hutool 5.5.9 / fastjson 1.2.75 / Druid 1.2.8 / Shiro 1.8.0 等），Spring Boot 管理的栈顺 2.5.9 grandparent。行为等价优先于安全升级。
- **D-P2-09:** 双 ORM 的 mybatis 侧用 mybatis-plus（随 spt-tools-mybatis 内联），Phase 2 sample Mapper + Phase 5 报表均用 mybatis-plus。与旧 ReportServer 栈一致。

**Feign 进程内化 (INFRA-02)**
- **D-P2-10:** 进程内化机制 = 接口即契约 + 目标 impl 满足接口。保留 `@FeignClient` 接口作契约（去 Feign 运行时），让目标 Controller/Service 实现该接口注册为本地 bean；调用方继续 `@Autowired` 接口 → 进程内直调。
- **D-P2-11:** Phase 2 Feign 交付深度 = 机制/约定建立 + 删 nacos + 删 Feign 服务发现 + 一个 trivial 端到端 proof（假接口 + impl 验证 pattern 可编译可进程内调用）。保留 OpenFeign starter 供 `cfcaSignClient`（EXT-03）。295 真实转换推 Phase 4/5。
- **D-P2-12:** `@EnableFeignClients(basePackages=...)` 收窄到仅 `com.spt.sign.client.remote`；295 内部接口不再被 Feign 扫描 → 作纯契约接口由本地 impl bean 满足，无 double-bean 冲突。

**配置收敛 + 密钥外置 (INFRA-04)**
- **D-P2-13:** 密钥环境变量占位 + 轮换 —— 敏感项（DB password / xxl accessToken / `spt.app.secretKey` / appCode）改为 `${DB_PASSWORD:}` 等占位，真实值不入 git；`application.yml` 只留占位 + 本地 dev 默认值。轮换生产库密码（jdbc.properties 明文密码已进 git 历史）。
- **D-P2-14:** profile = dev + prod：`application.yml`（公共 + 占位）+ `application-dev.yml`（本地默认值）+ `application-prod.yml`（全占位）。uat/test 弃用。
- **D-P2-15:** 数据源前缀统一到 `spring.datasource.*`（Druid 保留）。绑定属 zgbas infra → 放 `framework`（旧 `bas.datasource` 绑定在 basServer `FrameworkConfig`，非 spt-tools，故统一不违反 D-P2-07 照搬）。spt-tools-data `DataSourceCreator` 前缀无关，照搬不受影响。
- **D-P2-16:** 单体端口 8080 + 根 `/`，无 context-path（Phase 1 骨架默认）。

### Claude's Discretion
- 实体/Dao 落位 + 包结构：实体/Dao 落 `zgbas-system`，包名对齐源 `com.spt.bas.*` verbatim。
- spt-tools 内联分层节奏：core → (data,http,file) → (jpa,web,mybatis,shiro,aop,config) 逐层内联，每层 `mvn compile` 绿灯再下一层。
- JPA 二级缓存 / show-sql / Druid 池参数：照搬旧 `application.properties`（ehcache 二级缓存 `ENABLE_SELECTIVE`、show-sql=false、Druid 池参数）。
- 外部 URL 配置迁移（`spt.qxb.server.url` / `auth.url` / `push.server.url` / `file.server.url`）：迁入 `application.yml` 作占位，EXT-04 落地。
- xxl-job 配置键（`xxl.job.*`）：Phase 2 配置收敛时一并从 application.yml 移除（handler 迁移见 Phase 6）。
- actuator/health 端点：Phase 2 仍不加（延用 Phase 1 最小启动）。

### Deferred Ideas (OUT OF SCOPE)
- Shiro 认证 / 首页 / 动态菜单 → Phase 3（AUTH-01..04）
- 业务 Service / Controller / BFF 逻辑 → Phase 4（BIZ-01..03；Phase 2 只搬数据层）
- 53 套报表 Mapper + XML → Phase 5（REPORT-01..02 / PERSIST-02）
- 295 FeignClient bulk 真实转换 → Phase 4/5（随业务 impl 落地）
- 64 xxl-job handler 迁 RuoYi quartz + 删 xxl-job 依赖 → Phase 6
- basWx/purchase 实体 + Dao（~11） → v2（#14）
- actuator/health 端点 → Phase 3+
- 报表物理分页性能改造 / 补 createBy-updateBy 审计字段 → 不在本期
</user_constraints>

<phase_requirements>
## Phase Requirements

| ID | Description | Research Support |
|----|-------------|------------------|
| INLINE-01 | `spt-tools-core` (依赖根，70 类) 内联进 `zgbas-common`，最先完成 | Confirmed 70 main classes at `/Users/alan/WorkSpace/IDEA/tools/spt-tools-core/src/main/java`; first wave in compile-gate order; brings spring-boot-starter / hutool-core 5.5.9 / commons-lang3 / guava / pinyin4j / lombok deps. |
| INLINE-02 | `spt-tools-data/http/file` 内联（数据源配置 / FeignConfig / Excel 工具） | Confirmed 17/15/8 main classes; data brings Druid/MySQL/spring-jdbc, http brings openfeign starter + httpclient + jjwt, file brings poi 4.1.2 + easyexcel 1.1.2-beta4 + fastjson + zxing. |
| INLINE-03 | `spt-tools-jpa/web/mybatis/shiro/aop/config` 内联（jpa 引用最广留后） | Confirmed 14/6/13/18/9/2 main classes; jpa brings spring-boot-starter-data-jpa + hibernate-ehcache; mybatis brings mybatis-plus-boot-starter 3.1.2 + generator + freemarker; shiro brings shiro-spring/ehcache/cas 1.8.0; aop brings spring-boot-starter-aop + jdbc. |
| INLINE-04 | 内联后消除对 `spt-tools-*` 私服 jar 的运行时依赖 | Drop `spt-tools-parent` BOM import + `<dependency>com.spt.tools:spt-tools-*</dependency>` deps everywhere; root pom `dependencyManagement` pins all transitive versions (see Standard Stack). |
| PERSIST-01 | JPA 主力增删改查可用，迁移 259 实体 + 254 Dao（继承 `BaseDao`/`IdEntity`） | Actual count: 234 `@Entity` files (239 entity files total) in basClient.entity + 240 Dao files in basServer.dao. Bulk copy into zgbas-system preserving `com.spt.bas.client.entity.*` and `com.spt.bas.server.dao.*` packages verbatim. |
| PERSIST-03 | 双 ORM 单 DataSource 共存，`JpaTransactionManager` 设 `@Primary`，mybatis 复用同源 | Wiring pattern in Code Examples §A — framework's `DataSourceConfig` `@Bean` + JPA auto-config + mybatis-plus SqlSessionFactory bean share the same `DataSource` parameter; Spring Boot's JPA autoconfig already names the JPA TM `transactionManager` and there is only one, mybatis-plus boot starter does not declare its own TM. |
| PERSIST-04 | 审计字段（`createdDate/updatedDate` + `@EntityListeners`）行为保留，`javax.persistence` 体系不变 | Source `IdEntity` (vo/IdEntity.java) declares `@EntityListeners(EntityListener.class)` + `@MappedSuperclass`; `EntityListener` (listener/EntityListener.java) uses `@PrePersist`/`@PreUpdate` with `PropertyUtils.setProperty` — verbatim copy preserves behavior; Hibernate 5.4 (SB 2.5.9 default) honors javax.persistence callbacks. |
| EXT-01 | `AuthOpenFacade`(auth-sdk) 保持 `@Bean init(secretKey,appCode,url)` HTTP 注入 | Source pattern at FrameworkConfig.java:49-61; coords `com.spt:auth-sdk:3.8.2-SNAPSHOT` (jar present at `/Users/alan/App/Repository/com/spt/auth-sdk/`); key reads `spt.app.secretKey` / `spt.app.appCode` / `auth.url`. |
| EXT-02 | `PushClientHttp`(spt-push-sdk)、`FileRemote`(spt-file-sdk) 保持原 HTTP 注入方式 | Source pattern FrameworkConfig.java:39-47 + 63-71; coords `com.spt.micoservice:spt-push-sdk:2.0.15-SNAPSHOT` (contains `com.hsoft.push.sdk.remote.PushClientHttp`) + `com.spt.micoservice:spt-file-sdk:2.1.5-SNAPSHOT` (contains `com.hsoft.file.sdk.remote.FileRemote`); keys `push.server.url` / `file.server.url`. |
| EXT-03 | `CfcaSignClient`(spt-sign-client) OpenFeign 保持，`@EnableFeignClients(basePackages="com.spt.sign.client.remote")` 保留 | Coords `com.spt:spt-sign-client:1.0.0-SNAPSHOT`; jar exposes 9 `I*Client` interfaces under `com.spt.sign.client.remote.*` with `@FeignClient(name=SignConstants.SERVER_NAME, url=SignConstants.SERVER_URL, configuration=FeignConfig.class)`. Keep `spring-cloud-starter-openfeign` on classpath via spt-tools-http inline. |
| EXT-04 | 外部连接配置项（`spt.app.secretKey/appCode`、`auth.url/push.server.url/file.server.url`）迁移到位 | Source values in `basServer/src/main/resources/config.properties`; target: `application-dev.yml` (plaintext dev defaults) + `application-prod.yml` (env-var placeholders). Sample migration table in Code Examples §E. |
| INFRA-01 | 删除 nacos（discovery 配置 + 依赖 + 3 处 `nacos.common.utils` 工具类引用改 commons） | POM deps: `com.alibaba.cloud:spring-cloud-starter-alibaba-nacos-discovery` in 4 poms (basServer/web/reportServer/basWx). Config keys: `spring.cloud.nacos.discovery.*` (drop). 3 source files import `com.alibaba.nacos.common.utils.{StringUtils,CollectionUtils}` — but all 3 are Phase 4 scope (basServer service/impl + web cache), NOT present in Phase 2; planner notes swap-to-commons rule for Phase 4 in `## Open Questions`. |
| INFRA-02 | 295 个服务间 `@FeignClient` 改为进程内 bean 直调 | Phase 2 deliverable = mechanism + 1 trivial proof (Code Examples §F). Bulk 295 conversion deferred to Phase 4/5 — achieved implicitly when impls (Controllers/Services) start `implements I*Client` and the narrowed `@EnableFeignClients` (D-P2-12) prevents double-bean conflict. |
| INFRA-04 | 配置文件收敛（4 套 application/config/jdbc.properties → 单 `application.yml` + profile），数据源前缀统一 | Source files: basServer (application.properties + jdbc.properties + config.properties + bootstrap.yml + 4 profiles), reportServer (mirror), web (smaller), basWx (out-of-scope). Target: 3 files in `zgbas-admin/src/main/resources/` (application.yml + application-dev.yml + application-prod.yml). Drop `@PropertySource` annotations on ZgbasApplication. Prefix `bas.datasource.*` → `spring.datasource.druid.*` bound via framework `@ConfigurationProperties`. |
</phase_requirements>

## Architectural Responsibility Map

| Capability | Primary Tier | Secondary Tier | Rationale |
|------------|-------------|----------------|-----------|
| spt-tools utility code (IdEntity / BaseDao / DataSourceCreator / FeignConfig / ShiroUtil / ExcelUtil etc.) | `zgbas-common` (library jar) | — | D-P2-06/07: verbatim source copy preserving `com.spt.tools.*`; no internal module deps; foundation for every other module. |
| DataSource / TransactionManager / SqlSessionFactory / external HTTP SDK beans wiring | `zgbas-framework` (library jar, depends on common) | — | D-P2-06 + Claude's Discretion: zgbas-owned infra (NOT spt-tools verbatim); lives above common so it can consume `DataSourceConfig`, `DataSourceCreator`, `IdEntity` etc. |
| JPA entities (`@Entity`) + JPA Daos (`extends BaseDao<T>`) | `zgbas-system` (library jar, depends on common+framework) | — | D-P2-01: data layer only; verbatim `com.spt.bas.client.entity.*` + `com.spt.bas.server.dao.*`. |
| mybatis-plus sample Mapper + XML | `zgbas-system` | — | D-P2-04: trivial `select count(*)` Mapper proves dual-ORM query path; report Mappers join in Phase 5. |
| Boot class with `@EnableFeignClients`/`@EntityScan`/`@EnableJpaRepositories`/`@MapperScan` | `zgbas-admin` (fat-jar boot module) | — | Phase 1 already owns `ZgbasApplication`; Phase 2 adds the four annotations + drops `@PropertySource`. |
| Profile-specific config (datasource, secrets, URLs) | `zgbas-admin/src/main/resources` | — | D-P2-14: 3 files (application.yml + application-dev.yml + application-prod.yml). |
| OpenFeign retention for cfca sign client | `zgbas-admin` (classpath) | — | D-P2-11/12: OpenFeign starter pulled transitively via `spt-tools-http` inline; narrowed `@EnableFeignClients(basePackages="com.spt.sign.client.remote")`. |
| `spt-sign-client` jar (cfca contracts + FeignConfig + SignClientConfig) | `zgbas-admin` (declared dep) | — | EXT-03: keep jar from `/Users/alan/App/Repository/com/spt/spt-sign-client/1.0.0-SNAPSHOT/`; `SignClientConfig` registers `LocalServerConfig signServerConfig` that reads `sign.server.url`. |
| `auth-sdk` / `spt-push-sdk` / `spt-file-sdk` jars | `zgbas-framework` (declared dep) | — | EXT-01/02: framework's ZgbasExternalBeansConfig @Bean-declares the 3 HTTP facades; coords in Standard Stack. |

## Standard Stack

### Core (Phase 2 invariants — pinned in root pom `<properties>` + `<dependencyManagement>`)

| Library | Version | Purpose | Why Standard |
|---------|---------|---------|--------------|
| `spring-boot-starter-parent` | 2.5.9 | grandparent BOM | `[VERIFIED: pom.xml:9]` Phase 1 D-02 locked; brings Hibernate 5.4 + spring-data-jpa 2.5.9 + spring-boot-autoconfigure 2.5.9. |
| `spring-cloud-dependencies` BOM | 2020.0.5 | OpenFeign + cloud BOM | `[CITED: spt-parent-2.5.3.pom]` source uses this BOM; SB 2.5.x ↔ 2020.0.x (Ilford) compatibility matrix. Import in root `<dependencyManagement>` so spt-tools-http's `spring-cloud-starter-openfeign` resolves consistently. |
| `org.springframework.boot:spring-boot-starter-web` | (SB-managed) | MVC + Tomcat embedded | `[VERIFIED: zgbas-admin/pom.xml:35]` Phase 1 already present. |
| `org.springframework.boot:spring-boot-starter-data-jpa` | (SB-managed) | JPA + Hibernate 5.4 | `[VERIFIED: spt-tools-jpa/pom.xml:13]` brought transitively once spt-tools-jpa inlined. |
| `org.springframework.boot:spring-boot-starter-aop` | (SB-managed) | AOP for `@ServiceLogAop` etc. | `[VERIFIED: spt-tools-aop/pom.xml:13]` brought by spt-tools-aop inline. |
| `org.springframework.boot:spring-boot-starter-jdbc` | (SB-managed) | spring-jdbc | `[VERIFIED: spt-tools-data/pom.xml:13 + spt-tools-aop/pom.xml:17]` brought by data + aop inline. |
| `org.hibernate:hibernate-ehcache` | (SB-managed) | Hibernate 2nd-level cache | `[VERIFIED: spt-tools-jpa/pom.xml:18]` brought by jpa inline; ehcache 2.x region factory used by source. |
| `com.alibaba:druid` | 1.2.8 | JDBC connection pool | `[CITED: spt-parent-2.5.3.pom druid.version=1.2.8]` D-P2-08 pin; required by `DataSourceCreator`. |
| `mysql:mysql-connector-java` | 8.0.13 | JDBC driver | `[CITED: spt-tools-parent-1.0.24-SNAPSHOT.pom mysql-connector-java=8.0.13]` D-P2-08 pin. |
| `com.baomidou:mybatis-plus-boot-starter` | 3.1.2 | mybatis-plus auto-config (dual ORM side B) | `[VERIFIED: spt-tools-mybatis/pom.xml:24-27 hardcoded]` D-P2-09 pin. Brings mybatis 3.5.1 + mybatis-spring 2.0.1 + MybatisPlusAutoConfiguration. |
| `com.baomidou:mybatis-plus-generator` | 3.1.2 | code generator (referenced by `MybatisPlusCodeGenerator` util) | `[VERIFIED: spt-tools-mybatis/pom.xml:29-32]` pin to match; only needed at compile time for inlined util class. |
| `org.freemarker:freemarker` | (SB-managed) | templating for mybatis-plus generator | `[VERIFIED: spt-tools-mybatis/pom.xml:45]` brought by mybatis inline. |
| `cn.hutool:hutool-core` / `hutool-all` | 5.5.9 | utility lib used across spt-tools-core | `[CITED: bas-parent pom hutool.version=5.5.9]` D-P2-08 pin. |
| `com.alibaba:fastjson` | 1.2.75 | JSON lib used by spt-tools-core/json + spt-tools-file | `[CITED: bas-parent pom fastjson.version=1.2.75]` D-P2-08 pin (bas-parent overrides spt-parent's 1.2.79 down). |
| `org.apache.shiro:shiro-spring` + `shiro-ehcache` + `shiro-cas` | 1.8.0 | Shiro framework (Phase 3 activates; Phase 2 just compiles) | `[CITED: spt-parent-2.5.3.pom shiro.version=1.8.0]` D-P2-08 pin. |
| `com.google.guava:guava` | 27.1-jre | utility lib (spt-tools-core/http/aop) | `[CITED: spt-parent-2.5.3.pom guava.version=27.1-jre]` pin. |
| `org.apache.commons:commons-lang3` | (SB-managed) | core utils (spt-tools-core) | `[VERIFIED: spt-tools-core/pom.xml:22]` SB-managed. **Replaces** `nacos.common.utils.StringUtils` for Phase 4 swaps. |
| `commons-validator:commons-validator` | 1.4.0 | validation util (spt-tools-core) | `[CITED: bas-parent pom version=1.4.0]` pin. |
| `commons-collections:commons-collections` | 3.2.2 | collection util (spt-tools-core) | `[CITED: bas-parent pom version=3.2.2]` pin. |
| `org.apache.commons:commons-text` | 1.1 | text util (spt-tools-core) | `[CITED: spt-tools-parent + bas-parent version=1.1]` pin. |
| `com.belerweb:pinyin4j` | 2.5.1 | pinyin (spt-tools-core) | `[CITED: bas-parent pom version=2.5.1]` pin. |
| `org.apache.poi:poi` / `poi-ooxml` / `poi-ooxml-schemas` | 4.1.2 | Excel (spt-tools-file) | `[CITED: spt-parent-2.5.3.pom poi.version=4.1.2]` D-P2-08 pin. |
| `com.alibaba:easyexcel` | 1.1.2-beta4 | EasyExcel (spt-tools-file) | `[VERIFIED: spt-tools-file/pom.xml:38 hardcoded]` pin. |
| `com.google.zxing:javase` | 3.3.0 | QR code (spt-tools-file) | `[CITED: bas-parent pom version=3.3.0]` pin. |
| `org.apache.httpcomponents:httpclient` + `httpcore` | (SB-managed) | HTTP client (spt-tools-http) | `[VERIFIED: spt-tools-http/pom.xml:20-25]` SB-managed. |
| `io.jsonwebtoken:jjwt` | (spt-parent-managed) | JWT (spt-tools-http `TokenUtil`) | `[ASSUMED]` version inherited from spt-parent; check at install time. |
| `org.projectlombok:lombok` | (SB-managed) | boilerplate reduction | `[VERIFIED: all spt-tools module poms]` optional=true, SB-managed. |

### Supporting (private repo — kept as jar deps)

| Library | Version | Purpose | When to Use |
|---------|---------|---------|-------------|
| `com.spt:auth-sdk` | 3.8.2-SNAPSHOT | `AuthOpenFacade` external HTTP facade | `[VERIFIED: /Users/alan/App/Repository/com/spt/auth-sdk/3.8.2-SNAPSHOT/]` framework `@Bean authOpenFacade()`. |
| `com.spt.micoservice:spt-push-sdk` | 2.0.15-SNAPSHOT | `PushClientHttp` external HTTP facade | `[VERIFIED: /Users/alan/App/Repository/com/spt/micoservice/spt-push-sdk/2.1.5-SNAPSHOT/]` (jar contains `com/hsoft/push/sdk/remote/PushClientHttp.class`). |
| `com.spt.micoservice:spt-file-sdk` | 2.1.5-SNAPSHOT | `FileRemote` external HTTP facade | `[VERIFIED: /Users/alan/App/Repository/com/spt/micoservice/spt-file-sdk/2.1.5-SNAPSHOT/]` (jar contains `com/hsoft/file/sdk/remote/FileRemote.class`). |
| `com.spt:spt-sign-client` | 1.0.0-SNAPSHOT | cfca sign FeignClient contracts + `SignClientConfig` | `[VERIFIED: /Users/alan/App/Repository/com/spt/spt-sign-client/1.0.0-SNAPSHOT/]` EXT-03; admin module dep + narrowed `@EnableFeignClients`. |

### Alternatives Considered

| Instead of | Could Use | Tradeoff |
|------------|-----------|----------|
| mybatis-plus 3.1.2 | mybatis-plus 3.5.x | D-P2-08 forbids: pin old to keep behavior parity; 3.5.x brings breaking API + Spring Boot 2.5 compatibility risk. |
| Druid 1.2.8 | HikariCP (SB default) | D-P2-08 forbids: source uses Druid-specific config keys (`filters=stat`, `poolPreparedStatements`, etc.); switching pools breaks behavior parity. |
| spring-cloud 2020.0.5 | drop entirely + replace FeignClient with manual RestTemplate | D-P2-11/12 explicitly keep OpenFeign for cfca. Drop is larger rewrite, deferred. |
| Inline spt-tools source | Keep private-repo jar dep | D-01..04 (Phase 1) explicitly broke spt-parent chain; re-importing breaks D-04. |

### Installation (root pom.xml delta)

```xml
<properties>
    <java.version>1.8</java.version>
    <!-- D-P2-08 pinned versions -->
    <spring-cloud.version>2020.0.5</spring-cloud.version>
    <hutool.version>5.5.9</hutool.version>
    <fastjson.version>1.2.75</fastjson.version>
    <druid.version>1.2.8</druid.version>
    <shiro.version>1.8.0</shiro.version>
    <mybatis-plus.version>3.1.2</mybatis-plus.version>
    <mysql-connector.version>8.0.13</mysql-connector.version>
    <poi.version>4.1.2</poi.version>
    <guava.version>27.1-jre</guava.version>
    <commons-validator.version>1.4.0</commons-validator.version>
    <commons-collections.version>3.2.2</commons-collections.version>
    <commons-text.version>1.1</commons-text.version>
    <pinyin4j.version>2.5.1</pinyin4j.version>
    <easyexcel.version>1.1.2-beta4</easyexcel.version>
    <zxing.version>3.3.0</zxing.version>
    <commons-io.version>2.11.0</commons-io.version>
</properties>

<dependencyManagement>
    <dependencies>
        <!-- ... existing zgbas-* internal modules ... -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-dependencies</artifactId>
            <version>${spring-cloud.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
        <!-- All pinned 3rd-party deps as D-P2-08 -->
        <dependency><groupId>cn.hutool</groupId><artifactId>hutool-all</artifactId><version>${hutool.version}</version></dependency>
        <dependency><groupId>cn.hutool</groupId><artifactId>hutool-core</artifactId><version>${hutool.version}</version></dependency>
        <dependency><groupId>com.alibaba</groupId><artifactId>fastjson</artifactId><version>${fastjson.version}</version></dependency>
        <dependency><groupId>com.alibaba</groupId><artifactId>druid</artifactId><version>${druid.version}</version></dependency>
        <dependency><groupId>com.baomidou</groupId><artifactId>mybatis-plus-boot-starter</artifactId><version>${mybatis-plus.version}</version></dependency>
        <dependency><groupId>com.baomidou</groupId><artifactId>mybatis-plus-generator</artifactId><version>${mybatis-plus.version}</version></dependency>
        <dependency><groupId>org.apache.shiro</groupId><artifactId>shiro-spring</artifactId><version>${shiro.version}</version></dependency>
        <dependency><groupId>org.apache.shiro</groupId><artifactId>shiro-ehcache</artifactId><version>${shiro.version}</version></dependency>
        <dependency><groupId>org.apache.shiro</groupId><artifactId>shiro-cas</artifactId><version>${shiro.version}</version></dependency>
        <dependency><groupId>mysql</groupId><artifactId>mysql-connector-java</artifactId><version>${mysql-connector.version}</version></dependency>
        <dependency><groupId>org.apache.poi</groupId><artifactId>poi</artifactId><version>${poi.version}</version></dependency>
        <dependency><groupId>org.apache.poi</groupId><artifactId>poi-ooxml</artifactId><version>${poi.version}</version></dependency>
        <dependency><groupId>org.apache.poi</groupId><artifactId>poi-ooxml-schemas</artifactId><version>${poi.version}</version></dependency>
        <dependency><groupId>com.alibaba</groupId><artifactId>easyexcel</artifactId><version>${easyexcel.version}</version></dependency>
        <dependency><groupId>com.google.zxing</groupId><artifactId>javase</artifactId><version>${zxing.version}</version></dependency>
        <dependency><groupId>commons-validator</groupId><artifactId>commons-validator</artifactId><version>${commons-validator.version}</version></dependency>
        <dependency><groupId>commons-collections</groupId><artifactId>commons-collections</artifactId><version>${commons-collections.version}</version></dependency>
        <dependency><groupId>org.apache.commons</groupId><artifactId>commons-text</artifactId><version>${commons-text.version}</version></dependency>
        <dependency><groupId>com.belerweb</groupId><artifactId>pinyin4j</artifactId><version>${pinyin4j.version}</version></dependency>
        <dependency><groupId>com.google.guava</groupId><artifactId>guava</artifactId><version>${guava.version}</version></dependency>
        <!-- External SDKs (private repo, NOT pinned - SNAPSHOT) -->
        <dependency><groupId>com.spt</groupId><artifactId>auth-sdk</artifactId><version>3.8.2-SNAPSHOT</version></dependency>
        <dependency><groupId>com.spt.micoservice</groupId><artifactId>spt-push-sdk</artifactId><version>2.0.15-SNAPSHOT</version></dependency>
        <dependency><groupId>com.spt.micoservice</groupId><artifactId>spt-file-sdk</artifactId><version>2.1.5-SNAPSHOT</version></dependency>
        <dependency><groupId>com.spt</groupId><artifactId>spt-sign-client</artifactId><version>1.0.0-SNAPSHOT</version></dependency>
    </dependencies>
</dependencyManagement>
```

**Version verification:** Versions above are sourced directly from `spt-parent-2.5.3.pom` and `bas-parent-2.0.1-SNAPSHOT.pom` (read at `/Users/alan/App/Repository/com/spt/spt-parent/2.5.3/spt-parent-2.5.3.pom` and `/Users/alan/WorkSpace/IDEA/zgbas/bas-parent/pom.xml`). The jars already exist in `/Users/alan/App/Repository` (proven by `find` for auth-sdk / spt-push-sdk / spt-file-sdk / spt-sign-client / shiro / druid / hutool). Maven Central existence of public packages is implied by the spt-tools source poms compiling today in the source project. `[VERIFIED: local repo + source poms]`

## Package Legitimacy Audit

> slopcheck 0.6.1 was installed via pip3 and run against the 6 most critical Maven packages. **Caveat: slopcheck's Maven Central index is shallow** (it reported "37 downloads" for mybatis-plus-generator — an established library with millions of real downloads). The `[SUS]` verdicts below are false positives caused by incomplete slopcheck data, NOT real hallucination signals. Each flagged package is independently corroborated by: (1) source-code presence in the actual zgbas/spt-tools repos we are migrating from, (2) jar presence in the local Maven repo, (3) explicit version declarations in spt-parent-2.5.3.pom / bas-parent pom. Planner should NOT add `checkpoint:human-verify` for these — they are battle-tested dependencies already on disk.

| Package | Registry | Age | Downloads (per slopcheck) | Source Repo | slopcheck | Disposition |
|---------|----------|-----|-----------|-------------|-----------|-------------|
| `cn.hutool:hutool-all` | maven | ~6 yrs | 203 (undercounted) | github.com/dromara/hutool | `[OK]` | Approved |
| `com.alibaba:fastjson` | maven | ~12 yrs | 349 (undercounted) | github.com/alibaba/fastjson | `[OK]` | Approved |
| `com.alibaba:druid` | maven | ~10 yrs | 121 (undercounted) | github.com/alibaba/druid | `[OK]` | Approved |
| `org.apache.shiro:shiro-spring` | maven | ~15 yrs | 45 (undercounted) | github.com/apache/shiro | `[SUS]` | False positive — established Apache project, jar present locally; approved. |
| `com.baomidou:mybatis-plus-boot-starter` | maven | ~7 yrs | 49 (undercounted) | github.com/baomidou/mybatis-plus | `[SUS]` | False positive — dominant MyBatis enhancer in CN ecosystem, jar present locally; approved. |
| `com.baomidou:mybatis-plus-generator` | maven | ~7 yrs | 37 (undercounted) | github.com/baomidou/mybatis-plus | `[SUS]` | False positive — pulled by spt-tools-mybatis source pom; approved. |
| `org.springframework.cloud:spring-cloud-starter-openfeign` | maven | ~8 yrs | n/a | github.com/spring-cloud/spring-cloud-openfeign | n/a | Approved — BOM-managed, brings feign-core. |
| `org.springframework.boot:spring-boot-starter-data-jpa` | maven | ~10 yrs | n/a | github.com/spring-projects/spring-boot | n/a | Approved — SB-managed. |
| `org.hibernate:hibernate-ehcache` | maven | ~10 yrs | n/a | hibernate.org | n/a | Approved — SB-managed. |
| `com.spt:auth-sdk` | private nexus | n/a | n/a | private | n/a | Approved — jar at `/Users/alan/App/Repository/com/spt/auth-sdk/3.8.2-SNAPSHOT/`. |
| `com.spt.micoservice:spt-push-sdk` | private nexus | n/a | n/a | private | n/a | Approved — jar contains `com/hsoft/push/sdk/remote/PushClientHttp.class`. |
| `com.spt.micoservice:spt-file-sdk` | private nexus | n/a | n/a | private | n/a | Approved — jar contains `com/hsoft/file/sdk/remote/FileRemote.class`. |
| `com.spt:spt-sign-client` | private nexus | n/a | n/a | private | n/a | Approved — source at `/Users/alan/WorkSpace/IDEA/sign/signClient/`; jar at local repo. |

**Packages removed due to slopcheck `[SLOP]` verdict:** none.
**Packages flagged as suspicious `[SUS]`:** 3 (`shiro-spring`, `mybatis-plus-boot-starter`, `mybatis-plus-generator`) — all false positives (slopcheck Maven data is incomplete; all three are established libraries with local jars on disk). **Planner should NOT insert `checkpoint:human-verify` for these — would create noise without adding safety.**

## Architecture Patterns

### System Architecture Diagram (Phase 2 end state)

```
┌──────────────────────────────────────────────────────────────────────┐
│                         zgbas-admin (boot, fat-jar)                  │
│  ┌──────────────────────────────────────────────────────────────┐   │
│  │ ZgbasApplication (@SpringBootApplication + 4 infra annots)   │   │
│  │  • @EntityScan(basePackages={"com.spt.bas.client.entity"})   │   │
│  │    + basePackageClasses=IdEntity.class  (covers .tools.jpa)  │   │
│  │  • @EnableJpaRepositories(basePackages={"com.spt.bas.server.dao"})│
│  │  • @MapperScan(basePackages="com.spt.bas.system.dao.sample", │   │
│  │      annotationClass=MyBatisDao.class)                       │   │
│  │  • @EnableFeignClients(basePackages="com.spt.sign.client.remote")│
│  │  • NO @PropertySource (D-P2-14 native profile)               │   │
│  └──────────────────────────┬───────────────────────────────────┘   │
│                             │ loads                                   │
│  ┌─────────────────────────▼────────────────────────────────────┐   │
│  │ application.yml + application-{dev,prod}.yml                 │   │
│  │  spring.datasource.druid.* (D-P2-15 unified prefix)          │   │
│  │  spring.jpa.* + spring.flyway.enabled=false + ddl-auto=validate│  │
│  │  mybatis-plus.mapper-locations=classpath:/mybatis/mappers/*.xml│  │
│  │  ${DB_PASSWORD} / ${XXL_TOKEN} placeholders (D-P2-13)        │   │
│  │  auth.url / push.server.url / file.server.url (D-P2-13/EXT-04)│  │
│  └──────────────────────────────────────────────────────────────┘   │
└─────────────┬────────────────────┬───────────────────┬───────────────┘
              │ depends on          │ depends on         │ depends on
              ▼                     ▼                    ▼
┌─────────────────────┐  ┌──────────────────────┐  ┌──────────────────────┐
│  zgbas-framework    │  │  zgbas-system        │  │  zgbas-quartz        │
│  (library jar)      │  │  (library jar)       │  │  (library jar)       │
│                     │  │                      │  │ Phase 6 — empty now  │
│ ZgbasDataSourceCfg  │  │ com/spt/bas/client/  │  └──────────────────────┘
│  • DataSourceConfig │  │   entity/* (234)     │
│    @ConfigurationPro│  │   ← verbatim from    │
│    perties("spring. │  │   basClient.entity   │
│    datasource.druid")│  │                      │
│  • Druid DataSource │  │ com/spt/bas/server/  │
│    @Bean @Primary   │  │   dao/* (240)        │
│  • JpaTransactionMgr│  │   ← verbatim from    │
│    auto (@Primary)  │  │   basServer.dao      │
│  • SqlSessionFactory│  │                      │
│    @Bean (same DS)  │  │ com/spt/bas/system/  │
│  • (3) external SDK │  │   dao/sample/        │
│    @Beans           │  │   SampleMapper.java  │
│    (AuthOpenFacade/ │  │   + @MyBatisDao      │
│     PushClientHttp/ │  │ res/mybatis/mappers/ │
│     FileRemote)     │  │   SampleMapper.xml   │
│                     │  │   (select count(*))  │
│ depends on common   │  │ depends on common+   │
└─────────┬───────────┘  │   framework          │
          │              └──────────┬───────────┘
          ▼                         │
┌──────────────────────────────────────────────────────────┐
│                  zgbas-common (library jar)              │
│ com/spt/tools/core/*        (70) ← verbatim spt-tools-core│
│ com/spt/tools/data/*        (17) ← verbatim spt-tools-data│
│ com/spt/tools/http/*        (15) ← verbatim spt-tools-http│
│ com/spt/tools/file/*         (8) ← verbatim spt-tools-file│
│ com/spt/tools/jpa/*         (14) ← verbatim spt-tools-jpa │
│ com/spt/tools/web/*          (6) ← verbatim spt-tools-web │
│ com/spt/tools/mybatis/*     (13) ← verbatim spt-tools-myb │
│ com/spt/tools/shiro/*       (18) ← verbatim spt-tools-shi │
│ com/spt/tools/aop/*          (9) ← verbatim spt-tools-aop │
│ com/spt/tools/config/*       (2) ← verbatim spt-tools-cfg │
│                                                          │
│ KEY: IdEntity, BaseDao, EntityListener, DataSourceCreator│
│      DataSourceConfig, ToolsJpaConfig, ToolsMybatisConfig│
│      FeignConfig (spt-tools-http), MyBatisDao annotation │
└──────────────────────────────────────────────────────────┘
                             │
                             │ External HTTP calls (keep jar deps)
                             ▼
        ┌────────────────────────────────────────────┐
        │ spt-auth (HTTP) ◀── AuthOpenFacade.init()  │
        │ ys-push-server  ◀── PushClientHttp.init()  │
        │ ys-file-server  ◀── FileRemote.init()      │
        │ sign-server     ◀── cfca OpenFeign (D-P2-12)│
        └────────────────────────────────────────────┘
                             │
                             │ DB (existing schema)
                             ▼
                   ┌──────────────────────┐
                   │ MySQL sptbasdb_pd    │
                   │ (read-only validate) │
                   └──────────────────────┘
```

A reader can trace the primary use case from top to bottom: boot class scans → loads YAML → framework builds Druid `DataSource` → JPA + mybatis-plus both bind to it → system module holds entities+Dao+sample Mapper → external SDKs make HTTP calls to 4 services.

### Recommended Project Structure (Phase 2 end state — additions to Phase 1 skeleton)

```
zgbas-plus/
├── pom.xml                                  # +spring-cloud BOM, +pinned <properties>, +external SDKs in dependencyManagement
├── zgbas-common/
│   ├── pom.xml                              # +declares every pinned dep the 10 modules need (hutool/druid/shiro/mybatis-plus/poi/etc.)
│   └── src/main/java/com/spt/tools/
│       ├── core/    ...(70 files, verbatim)
│       ├── data/    ...(17 files)
│       ├── http/    ...(15 files)
│       ├── file/    ...(8 files)
│       ├── jpa/     ...(14 files)
│       ├── web/     ...(6 files)
│       ├── mybatis/ ...(13 files)
│       ├── shiro/   ...(18 files)
│       ├── aop/     ...(9 files)
│       └── config/  ...(2 files)
├── zgbas-framework/
│   ├── pom.xml                              # +auth-sdk, +spt-push-sdk, +spt-file-sdk, +spring-boot-starter-data-jpa (optional), +mybatis-plus-boot-starter
│   └── src/main/java/com/spt/framework/
│       ├── config/
│       │   ├── ZgbasDataSourceConfig.java   # @ConfigurationProperties("spring.datasource.druid") → DataSourceConfig bean + Druid DataSource @Bean @Primary
│       │   ├── ZgbasMybatisConfig.java      # SqlSessionFactory @Bean sharing same DataSource, mapper-locations, type-aliases
│       │   └── ZgbasExternalBeansConfig.java# @Bean authOpenFacade / pushClientHttp / fileRemote using env.getProperty + init()
│       └── PackageMarker.java               # (Phase 1 keep)
├── zgbas-system/
│   ├── pom.xml                              # unchanged (already depends on common+framework)
│   └── src/main/
│       ├── java/com/spt/
│       │   ├── bas/client/entity/           # ← bulk-copied from basClient.entity (234 @Entity files, verbatim)
│       │   ├── bas/server/dao/             # ← bulk-copied from basServer.dao (240 Dao files + 4 subpkgs, verbatim)
│       │   └── system/PackageMarker.java    # (Phase 1 keep)
│       └── resources/
│           └── mybatis/mappers/
│               └── SampleMapper.xml         # trivial proof: <select id="countAll" resultType="long">select count(*) from t_api_external_his</select>
├── zgbas-quartz/                            # Phase 6 — unchanged
└── zgbas-admin/
    ├── pom.xml                              # +spt-sign-client dep, +spring-cloud-starter-openfeign (transitive via common is fine but explicit is safer)
    └── src/main/
        ├── java/com/spt/
        │   ├── ZgbasApplication.java        # +4 infra annotations (EntityScan/JpaRepositories/MapperScan/EnableFeignClients narrowed)
        │   └── proof/                       # trivial in-process Feign-contract proof (D-P2-10/D-P2-11)
        │       ├── InProcessContract.java   # @PostMapping("/proof/echo") interface (NO @FeignClient — pure contract)
        │       └── InProcessContractImpl.java # @RestController implements InProcessContract
        ├── test/java/com/spt/
        │   └── ZgbasApplicationTest.java    # @SpringBootTest contextLoads (Phase 1 keep) + optional InProcessContractTest
        └── resources/
            ├── application.yml              # shared: server.port=8080, spring.jpa.*, mybatis-plus.mapper-locations, auth.url placeholders
            ├── application-dev.yml          # local defaults (no env-var dependency)
            └── application-prod.yml         # all env-var placeholders
```

### Pattern 1: Layered Inline Compile-Gate (D-P2-06 ordering)

**What:** Copy spt-tools modules into common in 4 layers, run `mvn -pl zgbas-common compile -am` between layers.

**When to use:** Always in Phase 2 — avoids gotcha cascade documented in Phase 1 lessons.

**Layers (cross-deps verified from source poms):**
```
Layer 1 (core alone):                  spt-tools-core (70)
                                       ↓ mvn compile
Layer 2 (core's direct children):      + spt-tools-data (depends core)
                                       + spt-tools-http (depends core)
                                       + spt-tools-file (depends core)
                                       ↓ mvn compile
Layer 3 (core + data children):        + spt-tools-jpa    (depends core, data)
                                       + spt-tools-web    (depends core, data)
                                       + spt-tools-mybatis(depends core, data)
                                       + spt-tools-shiro  (depends core only)
                                       + spt-tools-aop    (depends core only)
                                       ↓ mvn compile
Layer 4 (composition only):            + spt-tools-config (imports aop, http, jpa, shiro — no kafka/redis needed despite pom)
                                       ↓ mvn compile
```

**Cross-module dependency map (verified from pom.xml of each module):**

| Module | Depends on (internal) |
|--------|----------------------|
| `spt-tools-core` | (none) |
| `spt-tools-data` | core |
| `spt-tools-http` | core |
| `spt-tools-file` | core |
| `spt-tools-jpa` | core, data |
| `spt-tools-web` | core, data |
| `spt-tools-mybatis` | core, data |
| `spt-tools-shiro` | core |
| `spt-tools-aop` | core |
| `spt-tools-config` | aop, http, jpa, shiro (source imports only these `EnableTools*Config` annotations — does NOT import kafka/redis despite pom provided-scope deps) |

`[VERIFIED: grep spt-tools- /Users/alan/WorkSpace/IDEA/tools/spt-tools-*/pom.xml]`

**Gotcha:** `spt-tools-config/pom.xml` declares `spt-tools-kafka` + `spt-tools-redis` as `provided`. When inlining, **drop these 2 deps from common's pom** — the source files `EnableToolsWebConfig.java` / `EnableToolsServiceConfig.java` only import `EnableToolsCoreConfig`/`Http`/`Jpa`/`Shiro`/`Aop` (verified by reading both files). `[VERIFIED: read both files at /Users/alan/WorkSpace/IDEA/tools/spt-tools-config/src/main/java/com/spt/tools/config/]`

### Pattern 2: Single-DataSource Dual-ORM Wiring (D-P2-03)

**What:** One Druid `DataSource` bean, consumed by both `EntityManagerFactory` (JPA auto-config) and `SqlSessionFactory` (mybatis-plus). JPA's auto-configured `JpaTransactionManager` becomes `@Primary` by virtue of being the only `PlatformTransactionManager` on the classpath.

**When to use:** Always in Phase 2 — the entire PERSIST-03 success criterion.

**Why this works without JTA:** Spring Boot 2.5.9's `JpaBaseConfiguration` creates a `JpaTransactionManager` bean named `transactionManager` whenever `spring-boot-starter-data-jpa` is on the classpath. mybatis-plus 3.1.2's `MybatisPlusAutoConfiguration` registers `SqlSessionFactory` + `SqlSessionTemplate` but does NOT register its own `PlatformTransactionManager` — it relies on Spring Boot's `DataSourceTransactionManagerAutoConfiguration` which backs off when a JPA TM exists. Result: one TM, both ORMs share it, both bind to the same DataSource. `[CITED: Spring Boot 2.5.9 reference — "If you use spring-boot-starter-data-jpa, a JpaTransactionManager is automatically registered."]`

**But:** spt-tools-jpa's `ToolsJpaConfig.java:34` and spt-tools-mybatis's `ToolsMybatisConfig.java:20` BOTH declare `@Bean("datasource") @ConditionalOnBean(DataSourceConfig.class) @ConditionalOnMissingBean`. This collides: whoever loads first wins; the other backs off. Source project tolerates this because both produce equivalent Druid DataSources from the same `DataSourceConfig` bean. **Phase 2 strategy:** define our own `@Bean @Primary DataSource dataSource(...)` in framework's `ZgbasDataSourceConfig`, which will be registered FIRST (via `@Import` ordering or `@AutoConfigureBefore`) and the two spt-tools beans will back off via `@ConditionalOnMissingBean`. Simpler alternative: leave the spt-tools beans as-is and let Spring pick one — but then `@Primary` is implicit/ambiguous. **Recommend the explicit framework `@Bean @Primary DataSource`** to make D-P2-03's "@Primary TM" guarantee deterministic.

### Pattern 3: Interface-as-Contract Feign Mechanism (D-P2-10/D-P2-11/D-P2-12)

**What:** `@FeignClient` interfaces from `basClient/remote/*` (238) and `reportClient/remote/*` (54) become plain Java interfaces. Spring MVC honors `@RequestMapping`/`@PostMapping` declared on an interface when the implementing `@RestController` registers as a bean; callers `@Autowired` the interface and get the local impl bean.

**When to use:** Bulk conversion in Phase 4/5. **Phase 2 only:** prove the pattern works (1 trivial interface + impl) + narrow `@EnableFeignClients` so the 238+54 internal interfaces are NOT scanned as Feign proxies (which would cause double-bean conflicts once impls arrive).

**Why narrowing is required:** Without narrowing, `@EnableFeignClients` would create a Feign proxy bean AND the impl `@RestController` would create a local bean for the same interface — Spring fails with `NoUniqueBeanDefinitionException`. D-P2-12 narrows scan to `com.spt.sign.client.remote` only, so internal interfaces are pure contracts until Phase 4 impls arrive.

### Anti-Patterns to Avoid

- **Inline all 10 modules at once then compile:** will unmask 1200+ references in one cascade (Phase 1 lesson). Honor layer-by-layer compile gates.
- **Skip the framework `@Primary DataSource` and rely on spt-tools' implicit bean:** ambiguous — `ToolsJpaConfig.dataSource` vs `ToolsMybatisConfig.dataSource` race; tests become flaky depending on bean registration order.
- **Copy entities with new package names:** D-P2-01+Discretion says verbatim `com.spt.bas.client.entity.*` — any rename cascades into 1226+ import edits in Phase 4.
- **Re-enable Flyway / ddl-auto=update:** D-P2-02 explicitly forbids — would mutate production schema. Use `validate` only.
- **Leave `bas.datasource.*` prefix:** D-P2-15 unifies to `spring.datasource.druid.*` — old prefix breaks if any code reads it directly (DataSourceCreator is prefix-agnostic, but the binding annotation must change).
- **Pull OpenFeign starter explicitly in admin pom:** already transitively present via spt-tools-http inline into common → framework → admin. Declaring it explicitly is harmless but redundant.
- **Add `@EnableDiscoveryClient`:** nacos removal (D-P2-11) means dropping this annotation from `ZgbasApplication`. Spring Cloud 2020.0.5 no longer requires it for non-discovery apps.

## Don't Hand-Roll

| Problem | Don't Build | Use Instead | Why |
|---------|-------------|-------------|-----|
| Druid DataSource creation | manual `DruidDataSource` setup | inlined `com.spt.tools.data.util.DataSourceCreator.createDataSource(config)` | Source already handles 14 pool params + abandoned cleanup + filters; verbatim copy preserves behavior. |
| JPA audit (createdDate / updatedDate) | custom `@PrePersist` advice | inlined `com.spt.tools.jpa.listener.EntityListener` + `@EntityListeners` on `IdEntity` | Source uses `PropertyUtils.setProperty` for reflective field write; works for all `IdEntity` subclasses without per-entity code. |
| TransactionManager wiring | manual `@Bean PlatformTransactionManager` | Spring Boot's JPA auto-config | Already names it `transactionManager` and is unique on classpath (mybatis-plus starter does not declare its own TM). |
| mybatis-plus SqlSessionFactory | manual `SqlSessionFactoryBean` | mybatis-plus-boot-starter auto-config + `mybatis-plus.mapper-locations` property | Auto-config binds to the primary DataSource for free; overriding the bean risks losing PaginationInterceptor etc. |
| External HTTP SDK init | custom HTTP boilerplate | source's `@Bean init(secretKey,appCode,url)` pattern | Jars already encapsulate HTTP + auth; we just configure them with env-var-driven values. |
| Feign client invocation (in-process) | manual `@Service` adapter per interface | D-P2-10 "target impl satisfies interface" pattern | Spring MVC resolves interface @RequestMapping; zero-touch conversion for 295 clients in Phase 4/5. |
| Env-var placeholder syntax | custom `PropertyResolver` | Spring Boot `${VAR:default}` placeholder | Native support in `@PropertySource`/YAML; works in both `.yml` and `.properties`. |
| DataSource prefix migration | sed-replace every reference | bind via `@ConfigurationProperties(prefix="spring.datasource.druid")` on `DataSourceConfig` | Old code reads `DataSourceConfig` getters, not raw keys — only the prefix-binding annotation changes. |

**Key insight:** spt-tools already contains battle-tested solutions for pool creation, JPA audit, and external HTTP SDK init. The Phase 2 task is *wiring* those solutions into zgbas-framework, not *rebuilding* them.

## Common Pitfalls

### Pitfall 1: `ToolsJpaConfig.dataSource` vs `ToolsMybatisConfig.dataSource` Bean Collision
**What goes wrong:** Both inlined `@Configuration` classes declare `@Bean("datasource") @ConditionalOnBean(DataSourceConfig.class) @ConditionalOnMissingBean`. In a multi-config scenario, registration order is non-deterministic.
**Why it happens:** Source project tolerated it because both produce equivalent DataSources; the monolith has both `ToolsJpaConfig` AND `ToolsMybatisConfig` on the same scan path simultaneously (previously they were in separate microservices).
**How to avoid:** Define `@Bean @Primary DataSource` in `framework/ZgbasDataSourceConfig` and let `@ConditionalOnMissingBean` force spt-tools' beans to back off. Add `@AutoConfigureBefore(ToolsJpaConfig.class)` if needed.
**Warning signs:** `BeanDefinitionStoreException` or intermittent `NoSuchBeanDefinitionException` for `dataSource` on test runs.

### Pitfall 2: `@EntityScan` missing IdEntity's package → `MappingException: Unknown entity`
**What goes wrong:** JPA sees basClient entities but cannot resolve the `@MappedSuperclass IdEntity` parent → `IllegalArgumentException: Not an entity` at first query.
**Why it happens:** `@EntityScan(basePackages="com.spt.bas.client.entity")` does NOT include `com.spt.tools.jpa.vo` (where `IdEntity` lives). Source project handled this via `@EntityScan(basePackageClasses = IdEntity.class, basePackages = {...})` — the `basePackageClasses` adds `IdEntity`'s package automatically.
**How to avoid:** Mirror source: `@EntityScan(basePackageClasses = IdEntity.class, basePackages = {"com.spt.bas.client.entity"})`.
**Warning signs:** Startup succeeds but first DAO call throws `IllegalArgumentException: Unknown entity bean`.

### Pitfall 3: Flyway Baseline Confusion
**What goes wrong:** After setting `spring.flyway.enabled=false`, a stale `flyway_schema_history` row in `sptbasdb_pd` causes validate-on-migrate to silently misreport.
**Why it happens:** Source `application.properties` had `spring.flyway.baseline-on-migrate=true` + `spring.flyway.validate-on-migrate=true`. Both keys must be neutralized when Flyway is disabled.
**How to avoid:** Set `spring.flyway.enabled=false` (sufficient — Flyway auto-config backs off entirely). Do NOT leave baseline/validate keys dangling.
**Warning signs:** Log line `Flyway is disabled` — if absent, Flyway is still active.

### Pitfall 4: mybatis-plus 3.1.2 + Hibernate 5.4 Auto-Config Conflict
**What goes wrong:** `SqlSessionFactoryBean` post-processing fails because Hibernate's `JpaMetamodelMappingContext` already claims type aliases.
**Why it happens:** mybatis-plus 3.1.2 (from 2019) was not tested against Hibernate 5.4 + Spring Boot 2.5 (post-3.1.2).
**How to avoid:** Explicitly set `mybatis-plus.type-aliases-package=com.spt.bas.client.entity` (or empty) — source project set it to `com.spt.bas.report.client.entity,com.spt.bas.report.client.vo` (report entities); Phase 2's sample Mapper doesn't strictly need it.
**Warning signs:** `IllegalArgumentException: Property 'typeAliasesPackage' is required` or class-not-found on Mapper return types at startup.

### Pitfall 5: Feign Proxy + Local Impl Double Bean (Phase 4 leak-through)
**What goes wrong:** When the first internal `@FeignClient` impl is added in Phase 4, Spring complains about two beans of the same interface type.
**Why it happens:** D-P2-12 narrowing must be in place BEFORE any impl lands; otherwise the old `@EnableFeignClients(basePackages={...6 packages...})` from source would still scan the internal interfaces.
**How to avoid:** Phase 2 puts narrowed `@EnableFeignClients(basePackages="com.spt.sign.client.remote")` on `ZgbasApplication` and never broadens it. Document this in a Phase 4 prerequisite note.
**Warning signs:** `NoUniqueBeanDefinitionException` for `IApplyCreditCycleClient` etc. when Phase 4 work begins.

### Pitfall 6: Locale-Dependent Compile Output
**What goes wrong:** `mvn compile` errors are emitted as "找不到符号" (zh_CN locale) instead of "cannot find symbol"; CI grep `cannot find symbol` misses all errors.
**Why it happens:** macOS `user.language=zh`. Documented in MEMORY.md.
**How to avoid:** Use locale-independent grep pattern `^\[ERROR\]` (matches Maven's `[ERROR]` prefix regardless of locale).
**Warning signs:** A grep that returns 0 hits but the build clearly failed.

### Pitfall 7: JAVA_HOME Mis-set (Default is JDK 21, not 1.8)
**What goes wrong:** Build fails immediately with `Unsupported class file major version 65` or similar JDK 21 artifacts.
**Why it happens:** Machine default is JDK 21; project requires JDK 1.8.
**How to avoid:** Prefix every mvn invocation: `JAVA_HOME=/Users/alan/Library/Java/JavaVirtualMachines/corretto-1.8.0_482/Contents/Home`.
**Warning signs:** Build errors referencing `java.lang.UnsupportedClassVersionError`.

### Pitfall 8: `nacos.common.utils` Imports in Phase 4 Files
**What goes wrong:** If Phase 4 bulk-copies `WorkBenchCache.java` / `ApplyReceiveRefundDcsxServiceImpl.java` / `ApplyLossServiceImpl.java` without import swap, compilation fails (nacos jar removed).
**Why it happens:** These 3 files import `com.alibaba.nacos.common.utils.{StringUtils,CollectionUtils}`.
**How to avoid:** Phase 2 establishes the rule; Phase 4 bulk copy must mechanically replace:
  - `com.alibaba.nacos.common.utils.StringUtils` → `org.apache.commons.lang3.StringUtils`
  - `com.alibaba.nacos.common.utils.CollectionUtils` → `org.springframework.util.CollectionUtils` or `org.apache.commons.collections4.CollectionUtils`
**Warning signs:** Phase 4 compile errors `package com.alibaba.nacos.common.utils does not exist`.

### Pitfall 9: Sign Client Feign URL SpEL Resolution
**What goes wrong:** `ICfcaSignClient` declares `url = SignConstants.SERVER_URL` where `SERVER_URL = "#{" + SERVER_BEAN_NAME + ".url}"` — a SpEL expression referencing a `LocalServerConfig` bean named `signServerConfig`.
**Why it happens:** Source uses late-binding via `SignClientConfig.localServerConfig()` which reads `sign.server.url` from Environment. If this `@Bean` isn't loaded, Feign client creation fails with `BeanExpressionException`.
**How to avoid:** `spt-sign-client` jar includes `SignClientConfig` (verified via class listing). Spring will pick it up via ComponentScan of `com.spt.sign.*` (covered by `ZgbasApplication`'s base package `com.spt`). Verify `sign.server.url` is set in `application-{dev,prod}.yml`.
**Warning signs:** `BeanExpressionException` or `SpelEvaluationException` at startup.

## Code Examples

### A. Framework DataSource / JPA / mybatis-plus Wiring

```java
// Source: derived from spt-tools-jpa/config/ToolsJpaConfig.java +
//         spt-tools-mybatis/config/ToolsMybatisConfig.java +
//         basServer/config/FrameworkConfig.java (adapted for monolith)
// File: zgbas-framework/src/main/java/com/spt/framework/config/ZgbasDataSourceConfig.java
package com.spt.framework.config;

import com.spt.tools.data.config.DataSourceConfig;
import com.spt.tools.data.util.DataSourceCreator;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class ZgbasDataSourceConfig {

    /** D-P2-15: prefix unified from bas.datasource.* to spring.datasource.druid.* */
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.druid")
    public DataSourceConfig dataSourceConfig() {
        return new DataSourceConfig();
    }

    /**
     * Primary Druid DataSource. Declared BEFORE spt-tools' ToolsJpaConfig/ToolsMybatisConfig
     * (both have @ConditionalOnMissingBean on their own "datasource" @Bean), so they back off.
     * D-P2-03: this single DS feeds both EntityManagerFactory (JPA) and SqlSessionFactory (mybatis-plus).
     */
    @Bean("datasource")
    @Primary
    public DataSource dataSource(DataSourceConfig config) {
        return DataSourceCreator.createDataSource(config);
    }
}
```

```java
// File: zgbas-framework/src/main/java/com/spt/framework/config/ZgbasMybatisConfig.java
package com.spt.framework.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.spt.tools.mybatis.annotation.MyBatisDao;

@Configuration
@MapperScan(basePackages = "com.spt.bas.system.dao", annotationClass = MyBatisDao.class)
public class ZgbasMybatisConfig {

    /** Page helper for sample Mapper (D-P2-04). Pagination not strictly needed for count(*)
     *  but mirrors source's reportServer wiring. */
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        return new PaginationInterceptor();
    }
}
```

```java
// File: zgbas-framework/src/main/java/com/spt/framework/config/ZgbasExternalBeansConfig.java
// Source: basServer/config/FrameworkConfig.java lines 39-71 — verbatim pattern, env-var placeholders
package com.spt.framework.config;

import com.hsoft.file.sdk.remote.FileRemote;
import com.hsoft.push.sdk.remote.PushClientHttp;
import com.spt.auth.sdk.open.AuthOpenFacade;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class ZgbasExternalBeansConfig {
    private static final Logger log = LoggerFactory.getLogger(ZgbasExternalBeansConfig.class);

    @Autowired
    private Environment env;

    @Bean
    public FileRemote fileRemote() {
        FileRemote http = new FileRemote();
        http.init(env.getProperty("spt.app.secretKey"),
                  env.getProperty("spt.app.appCode"),
                  env.getProperty("file.server.url"));
        return http;
    }

    @Bean
    public IAuthOpenFacade authOpenFacade() {
        AuthOpenFacade http = new AuthOpenFacade();
        http.init(env.getProperty("spt.app.secretKey"),
                  env.getProperty("spt.app.appCode"),
                  env.getProperty("auth.url"));
        return http;
    }

    @Bean
    public PushClientHttp pushClientHttp() {
        PushClientHttp http = new PushClientHttp();
        http.init(env.getProperty("spt.app.secretKey"),
                  env.getProperty("spt.app.appCode"),
                  env.getProperty("push.server.url"));
        return http;
    }
}
```

### B. Boot Class Annotations

```java
// Source: derived from basServer/BasServer.java (lines 27-37) adapted for monolith
// File: zgbas-admin/src/main/java/com/spt/ZgbasApplication.java
package com.spt;

import com.spt.tools.jpa.vo.IdEntity;
import com.spt.tools.mybatis.annotation.MyBatisDao;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication                                     // NO exclusions — we WANT DataSource + JPA auto-config
@EnableFeignClients(basePackages = "com.spt.sign.client.remote")  // D-P2-12 narrowed
@EntityScan(basePackageClasses = IdEntity.class,                  // adds com.spt.tools.jpa.vo
            basePackages = {"com.spt.bas.client.entity"})         // entities live in basClient.entity (NOT basServer)
@EnableJpaRepositories(basePackages = {"com.spt.bas.server.dao"}) // 240 Dao (extends BaseDao<T>)
@MapperScan(basePackages = "com.spt.bas.system.dao",              // sample Mapper only in Phase 2
            annotationClass = MyBatisDao.class)
public class ZgbasApplication {
    public static void main(String[] args) {
        SpringApplication.run(ZgbasApplication.class, args);
    }
}
```

### C. Trivial Sample Mapper (D-P2-04 proof)

```java
// File: zgbas-system/src/main/java/com/spt/bas/system/dao/SampleMapper.java
package com.spt.bas.system.dao;

import com.spt.tools.mybatis.annotation.MyBatisDao;

@MyBatisDao
public interface SampleMapper {
    /** Trivial proof: mybatis-plus can execute a query through the same DataSource as JPA.
     *  Targets t_api_external_his — table backs ApiExternalHis @Entity (verified present
     *  in basClient.entity.ApiExternalHis). */
    long countAll();
}
```

```xml
<!-- File: zgbas-system/src/main/resources/mybatis/mappers/SampleMapper.xml -->
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.spt.bas.system.dao.SampleMapper">
    <select id="countAll" resultType="long">
        select count(*) from t_api_external_his
    </select>
</mapper>
```

### D. Trivial Feign In-Process Proof (D-P2-10/D-P2-11)

```java
// File: zgbas-admin/src/main/java/com/spt/proof/InProcessContract.java
package com.spt.proof;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/** Pure contract interface — NO @FeignClient. Proves D-P2-10: target impl satisfies interface.
 *  Caller @Autowired this interface and gets the local @RestController bean. */
public interface InProcessContract {
    @GetMapping("/proof/echo")
    String echo(@RequestParam("msg") String msg);
}
```

```java
// File: zgbas-admin/src/main/java/com/spt/proof/InProcessContractImpl.java
package com.spt.proof;

import org.springframework.web.bind.annotation.RestController;

/** Local bean registered as @RestController. Spring MVC honors @GetMapping on the interface.
 *  This is the exact pattern Phase 4 will use for 295 internal @FeignClient interfaces. */
@RestController
public class InProcessContractImpl implements InProcessContract {
    @Override
    public String echo(String msg) {
        return "echo:" + msg;
    }
}
```

```java
// File: zgbas-admin/src/test/java/com/spt/proof/InProcessContractTest.java
package com.spt.proof;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class InProcessContractTest {
    @Autowired
    private InProcessContract contract;   // resolves to local impl bean — proves D-P2-10

    @Test
    void localImplSatisfiesContract() {
        assertThat(contract.echo("hi")).isEqualTo("echo:hi");
    }
}
```

### E. Config Consolidation Map

| Source (basServer) | Source key | Target (zgbas-admin) | Target key | Notes |
|--------------------|-----------|----------------------|------------|-------|
| `jdbc.properties:1` | `bas.datasource.url` | `application-dev.yml` + `application-prod.yml` | `spring.datasource.druid.url` | D-P2-15 prefix change; prod uses `${DB_URL:}` |
| `jdbc.properties:3` | `bas.datasource.password` (plaintext!) | `application-prod.yml` | `${DB_PASSWORD:}` | D-P2-13 secret externalization; ROTATE |
| `application.properties:6` | `spring.cloud.nacos.discovery.fail-fast` | (drop) | — | INFRA-01 nacos removal |
| `application.properties:11-17` | `spring.jpa.properties.hibernate.cache.*` | `application.yml` | (same) | ehcache 2nd-level preserved (Discretion) |
| `application.properties:29` | `spring.flyway.enabled=true` | `application.yml` | `spring.flyway.enabled=false` + `spring.jpa.hibernate.ddl-auto=validate` | D-P2-02 |
| `application.properties:45-52` | `xxl.job.*` | (drop entirely) | — | D-P2-11 + xxl-job config removal |
| `config.properties:6` | `file.server.url` | `application-dev.yml` + `-prod.yml` | `file.server.url` (prod: `${FILE_SERVER_URL:}`) | EXT-04 |
| `config.properties:11` | `push.server.url` | same | `push.server.url` | EXT-04 |
| `config.properties:13` | `auth.url` | same | `auth.url` | EXT-04 |
| `config.properties:15` | `spt.app.secretKey` | same | `spt.app.secretKey` (prod: `${SPT_APP_SECRET:}`) | D-P2-13 secret |
| `config.properties:16` | `spt.app.appCode` | same | `spt.app.appCode` (dev: `zgbas`, prod: `${SPT_APP_CODE:}`) | EXT-04 |
| `application-dev.properties:11` | `sign.server.url` (sign client uses) | `application-dev.yml` + `-prod.yml` | `sign.server.url` | EXT-03 required for cfca Feign SpEL resolution |
| `bootstrap.yml` | (empty) | (drop) | — | No nacos config bootstrap |

```yaml
# File: zgbas-admin/src/main/resources/application.yml (shared — public + placeholders)
server:
  port: 8080
  servlet:
    context-path: /

spring:
  application:
    name: zgbas-plus
  profiles:
    active: dev
  flyway:
    enabled: false                                     # D-P2-02
  jpa:
    hibernate:
      ddl-auto: validate                               # D-P2-02
    show-sql: false                                    # Discretion: copy source behavior
    properties:
      hibernate:
        generate_statistics: true
        cache:
          use_second_level_cache: true
          use_query_cache: true
          region:
            factory_class: org.hibernate.cache.ehcache.SingletonEhCacheRegionFactory
      javax.persistence:
        sharedCache:
          mode: ENABLE_SELECTIVE
  aop:
    auto: true
    proxyTargetClass: true

mybatis-plus:
  mapper-locations: classpath:/mybatis/mappers/*Mapper.xml
  type-aliases-package: com.spt.bas.client.entity

logging:
  level:
    com.spt: info
    org.springframework: warn
```

```yaml
# File: zgbas-admin/src/main/resources/application-dev.yml (no env-var dependency)
spring:
  datasource:
    druid:
      driver-class-name: com.mysql.cj.jdbc.Driver
      url: jdbc:mysql://47.104.15.98:3306/sptbasdb_pd?serverTimezone=GMT%2B8&useUnicode=true&characterEncoding=UTF-8&useSSL=false
      username: sptbaspduser
      password: DEV_PLACEHOLDER_ROTATE_BEFORE_COMMIT   # NEVER commit prod password; rotate
      initial-size: 1
      min-idle: 1
      max-active: 20
      max-wait: 60000
      time-between-eviction-runs-millis: 60000
      min-evictable-idle-time-millis: 300000
      validation-query: select 'x'
      test-while-idle: true
      test-on-borrow: false
      test-on-return: false
      pool-prepared-statements: true
      max-pool-prepared-statement-per-connection-size: 20
      filters: stat

spt:
  app:
    secretKey: spt_secretkey_171212
    appCode: zgbas

auth:
  url: http://47.104.15.98:9011
push:
  server:
    url: http://push.tosupply.cn/ys-push-server
file:
  server:
    url: http://file.tosupply.cn/ys-file-server
sign:
  server:
    url: http://47.104.15.98:9002
```

```yaml
# File: zgbas-admin/src/main/resources/application-prod.yml (ALL secrets via env-var)
spring:
  datasource:
    druid:
      driver-class-name: com.mysql.cj.jdbc.Driver
      url: ${DB_URL:}
      username: ${DB_USERNAME:}
      password: ${DB_PASSWORD:}                        # D-P2-13 rotate prod DB password
      initial-size: 1
      min-idle: 1
      max-active: 20
      max-wait: 60000
      validation-query: select 'x'
      test-while-idle: true
      filters: stat

spt:
  app:
    secretKey: ${SPT_APP_SECRET:}                      # D-P2-13 rotate
    appCode: ${SPT_APP_CODE:}

auth:
  url: ${AUTH_URL:}
push:
  server:
    url: ${PUSH_SERVER_URL:}
file:
  server:
    url: ${FILE_SERVER_URL:}
sign:
  server:
    url: ${SIGN_SERVER_URL:}
```

## State of the Art

| Old Approach | Current Approach | When Changed | Impact |
|--------------|------------------|--------------|--------|
| nacos discovery (spring-cloud-alibaba) | monolith (no service discovery needed) | Phase 2 D-P2-11 | Drop `spring-cloud-starter-alibaba-nacos-discovery` dep; drop `@EnableDiscoveryClient`; remove 3 `nacos.common.utils` imports in Phase 4. |
| xxl-job executor (autrade) | RuoYi quartz (Phase 6) | Phase 6 QUARTZ-01..04 | Phase 2 only removes `xxl.job.*` config keys; jar dep + handlers removed in Phase 6. |
| `bas.datasource.*` prefix | `spring.datasource.druid.*` prefix | Phase 2 D-P2-15 | Only the `@ConfigurationProperties` prefix string changes; DataSourceConfig POJO unchanged. |
| 4 microservice `@PropertySource("classpath:/jdbc.properties")` | Spring Boot native `application-{profile}.yml` | Phase 2 D-P2-14 | Drop `@PropertySource`; rename `jdbc.properties` content into `application-*.yml`. |
| spt-tools jar from private nexus | inline source into zgbas-common | Phase 2 D-P2-06 | Drop `spt-tools-parent` BOM import; drop all `spt-tools-*` deps from poms. |
| `spring.flyway.enabled=true` + baseline-on-migrate | `spring.flyway.enabled=false` + `ddl-auto=validate` | Phase 2 D-P2-02 | Existing `flyway_schema_history` rows ignored; schema drift surfaces at startup. |

**Deprecated/outdated:**
- `org.springframework.cloud.openfeign.EnableFeignClients` 6-package scan (source `BasServer.java:32`): replaced by single-package scan `com.spt.sign.client.remote` (D-P2-12).
- `@EnableDiscoveryClient`: not needed in Spring Cloud ≥ 2020.0.x for non-discovery apps; removing is safe.
- `mybatis-plus-boot-starter:3.1.2` (2019): pinned for behavior parity (D-P2-08/D-P2-09); do not upgrade.

## Assumptions Log

| # | Claim | Section | Risk if Wrong |
|---|-------|---------|---------------|
| A1 | mybatis-plus 3.1.2 + Hibernate 5.4 (Spring Boot 2.5.9) coexist without auto-config conflict beyond `type-aliases-package`. | Standard Stack / Pitfall 4 | HIGH — if conflict, planner must define an explicit `SqlSessionFactory` @Bean overriding auto-config. Mitigation: Phase 2 ends with `@SpringBootTest contextLoads` proving startup; if it fails, escalate before Phase 4. |
| A2 | `@ConditionalOnMissingBean` on spt-tools' two `dataSource` @Beans causes them to back off when framework's `@Primary DataSource` exists. | Pattern 2 / Pitfall 1 | MEDIUM — if it does not back off, planner must `<exclusion>` the inlined @Bean or use `@Primary` only. Mitigation: framework `@Bean @Primary` is the safer pattern; verified Spring semantics. |
| A3 | Source entities (234 `@Entity` files in basClient.entity) compile under Hibernate 5.4 without modification. | Standard Stack | LOW — source already runs on Hibernate 5.4 (Spring Boot 2.5.9 baseline); no version change. |
| A4 | `org.springframework.cloud:spring-cloud-starter-openfeign` resolves to a working version under SB 2.5.9 when imported via `spring-cloud-dependencies:2020.0.5` BOM. | Standard Stack | LOW — source project uses the exact same combination today. |
| A5 | `spring-cloud-starter-alibaba-nacos-discovery` removal does not break `spring-cloud-context` transitive deps needed by OpenFeign. | Pitfall 1 / State of the Art | LOW — OpenFeign depends on `spring-cloud-context` directly, not via nacos starter; verified by source `web/pom.xml` which has openfeign + nacos as siblings. |
| A6 | jjwt version (used by spt-tools-http `TokenUtil`) resolves via Spring Boot's dependency management without an explicit pin. | Standard Stack | LOW — if it does not, planner can pin explicitly. Mitigation: Wave 1 compile gate will surface any unresolved version. |
| A7 | Phase 4 bulk-copy of 3 files importing `nacos.common.utils` will be handled by the mechanical swap-to-commons rule (Pitfall 8). | Pitfall 8 | LOW — explicit rule documented; Phase 4 planner inherits this research. |
| A8 | The `SampleMapper.xml` table `t_api_external_his` exists in `sptbasdb_pd` (so count(*) returns a row count, not error). | Validation Architecture | MEDIUM — if table missing, sample Mapper query throws at startup with `validate`. Mitigation: choose a table verified to back a migrated entity (ApiExternalHis @Table(name="t_api_external_his")); validate-mode startup itself will catch missing tables first. |
| A9 | spt-sign-client jar's `SignClientConfig` auto-registers `LocalServerConfig signServerConfig` via ComponentScan of `com.spt.sign.*`. | Pitfall 9 / EXT-03 | LOW — jar contents listing shows `com/spt/sign/client/config/SignClientConfig.class`; `@Configuration` is meta-annotated; `ZgbasApplication` scans `com.spt` so covered. |
| A10 | The 239 entity count + 240 Dao count is the complete set (no additional entities/Dao in basWx/purchase that sneak in). | PERSIST-01 / D-P2-05 | LOW — verified via `find ... | wc -l` in basClient.entity (239) and basServer.dao (240); basWx explicitly out-of-scope. |

## Open Questions (RESOLVED)

> All 4 questions resolved during planning (plans 02-01..06 adopt every recommendation below). Markers added per plan-checker W1.

1. **Do the 3 `nacos.common.utils` source files belong in Phase 2's deliverable or Phase 4's?**
   - What we know: They are `WorkBenchCache.java` (web), `ApplyReceiveRefundDcsxServiceImpl.java` (basServer service/impl), `ApplyLossServiceImpl.java` (basServer service/impl).
   - What's unclear: D-P2-11 says "delete nacos + 3 util-class refs" — literally these 3 files. But they are business code (Phase 4 scope).
   - RESOLVED — Recommendation: **Phase 2 removes the nacos POM deps + config keys only; the 3 file imports stay broken-but-absent** (files don't exist yet in zgbas-plus, so nothing breaks). Phase 4 planner applies the swap rule (Pitfall 8) when those files are bulk-copied. Document as Phase 4 prerequisite.

2. **Should framework's `@Bean @Primary DataSource` use `@AutoConfigureBefore(ToolsJpaConfig.class)`?**
   - What we know: Both `ToolsJpaConfig` and `ToolsMybatisConfig` declare `@Bean("datasource") @ConditionalOnMissingBean`. `@ConditionalOnMissingBean` evaluates at config-class processing time.
   - What's unclear: Whether `ZgbasDataSourceConfig` is guaranteed to process before the spt-tools configs.
   - RESOLVED — Recommendation: Use `@AutoConfigureBefore({ToolsJpaConfig.class, ToolsMybatisConfig.class})` on `ZgbasDataSourceConfig` OR define `ZgbasDataSourceConfig` in a `META-INF/spring.factories` Auto-configuration with lower order. Simpler: rely on `@ConditionalOnMissingBean` working bidirectionally (it does — whichever @Bean is registered first wins). Phase 2 ends with context-load test that proves non-ambiguity.

3. **Should `application-prod.yml` ship with empty defaults `${VAR:}` (graceful) or `${VAR}` (fail-fast if missing)?**
   - What we know: D-P2-13 says "占位 + 本地 dev 默认值"; the syntax `${VAR:default}` provides a default (empty after colon) while `${VAR}` fails startup if unset.
   - RESOLVED — Recommendation: Use `${VAR}` for secrets in prod (fail-fast if rotation incomplete) and `${VAR:dev-default}` in dev (always provide defaults).

4. **Phase 2 Sample Mapper — use `t_api_external_his` (ApiExternalHis entity) or a custom sample table?**
   - What we know: Sample Mapper needs a real table to query (count(*)).
   - RESOLVED — Recommendation: Use `t_api_external_his` (backs `ApiExternalHis` @Entity, verified in basClient.entity). If table missing in dev schema, fall back to a system table like `information_schema.tables` (no entity coupling).

## Environment Availability

| Dependency | Required By | Available | Version | Fallback |
|------------|------------|-----------|---------|----------|
| Maven | All builds | ✓ | apache-maven-3.8.6 at `/Users/alan/App/apache-maven-3.8.6` | — |
| Maven `zg_settings.xml` | Private nexus resolution | ✓ | `/Users/alan/App/apache-maven-3.8.6/zg_settings.xml` | — |
| JDK 1.8 (Corretto) | Compile | ✓ | corretto-1.8.0_482 at `/Users/alan/Library/Java/JavaVirtualMachines/corretto-1.8.0_482/Contents/Home` | — (must override system default JDK 21 via JAVA_HOME prefix) |
| Local repo | External SDK jar resolution | ✓ | `/Users/alan/App/Repository` (auth-sdk 3.8.2, spt-push-sdk 2.0.15, spt-file-sdk 2.1.5, spt-sign-client 1.0.0 all present) | — |
| MySQL sptbasdb_pd | validate-mode startup | ✓ (per source `jdbc.properties`) | 8.x (driver `com.mysql.cj.jdbc.Driver`) | dev profile may use any MySQL 5.7+ with same schema |
| Maven Central | Hutool/Druid/mybatis-plus/etc. | ✓ | n/a (already cached locally per Phase 1 zero-error build) | — |
| slopcheck | Package legitimacy gate | ✓ | 0.6.1 at `/Users/alan/Library/Python/3.9/bin/slopcheck` | Maven data shallow — see Package Legitimacy Audit caveat |

**Missing dependencies with no fallback:** None.
**Missing dependencies with fallback:** None.

**Build verification done during research:** Phase 1 baseline still compiles cleanly:
```
$ JAVA_HOME=/Users/alan/Library/Java/JavaVirtualMachines/corretto-1.8.0_482/Contents/Home \
  /Users/alan/App/apache-maven-3.8.6/bin/mvn -s /Users/alan/App/apache-maven-3.8.6/zg_settings.xml compile
[INFO] BUILD SUCCESS
[INFO] Total time:  0.429 s
```

## Validation Architecture

### Test Framework
| Property | Value |
|----------|-------|
| Framework | JUnit 5 (Jupiter) via `spring-boot-starter-test` (Phase 1 D-06) |
| Config file | none — inherits `spring-boot-starter-test` defaults |
| Quick run command | `JAVA_HOME=...corretto-1.8.0_482... /Users/alan/App/apache-maven-3.8.6/bin/mvn -s .../zg_settings.xml -pl zgbas-admin -am test -Dtest=ZgbasApplicationTest` |
| Full suite command | `JAVA_HOME=... /Users/alan/App/apache-maven-3.8.6/bin/mvn -s .../zg_settings.xml test` |

### Phase Requirements → Test Map

Phase 2's verification strategy is **startup-verification-primary** (D-P2-03). One `@SpringBootTest` context-load test + one trivial in-process contract test = full coverage. No query-correctness tests in Phase 2.

| Req ID | Behavior | Test Type | Automated Command | File Exists? |
|--------|----------|-----------|-------------------|-------------|
| INLINE-01..04 | spt-tools 10 modules inline & compile | compile (wave-gated) | `mvn -pl zgbas-common -am compile` after each layer | ✅ Wave 0 (Phase 1 baseline) |
| PERSIST-01 | 239 entities + 240 Dao migrated & compile | compile | `mvn -pl zgbas-system -am compile` | ❌ Wave 0 (entities/Dao not yet copied) |
| PERSIST-03 | Single DataSource + JPA TM @Primary + mybatis SqlSessionFactory coexist | startup (context-load) | `mvn -pl zgbas-admin -am test -Dtest=ZgbasApplicationTest` | ✅ Wave 0 (Phase 1 stub — extend assertions) |
| PERSIST-04 | `@EntityListeners` audit chain preserved | startup (validate-mode catches entity/mapping drift) | same as PERSIST-03 | ✅ Wave 0 |
| EXT-01..03 | 3 external SDK @Beans + cfca OpenFeign registered | startup (context-load) | same as PERSIST-03 | ✅ Wave 0 |
| EXT-04 | External URL keys resolvable from env | startup | same as PERSIST-03 | ✅ Wave 0 |
| INFRA-01 | nacos absent from classpath | startup (context-load + dep:tree) | `mvn -pl zgbas-admin dependency:tree -Dincludes=com.alibaba.cloud:spring-cloud-starter-alibaba-nacos-discovery` returns empty | ❌ Wave 0 (new check) |
| INFRA-02 | Interface-as-contract pattern works in-process | unit (Spring) | `mvn -pl zgbas-admin -am test -Dtest=InProcessContractTest` | ❌ Wave 0 (new test) |
| INFRA-04 | 4 service configs consolidated → 3 YAML files | file existence | `ls zgbas-admin/src/main/resources/application{,-dev,-prod}.yml` | ❌ Wave 0 (Phase 1 has only application.yml) |

### Sampling Rate
- **Per task commit:** `mvn -pl <affected-module> -am compile` (wave-gated; < 30s after Wave 0)
- **Per wave merge:** `mvn -pl zgbas-admin -am test -Dtest=ZgbasApplicationTest` (full context-load; ~30-60s)
- **Phase gate:** `mvn clean test` from root + manual verify: (a) startup log contains "Flyway is disabled", (b) startup log contains "HikariCP"→NO (must be Druid), (c) `dependency:tree` shows no `nacos-*`, (d) `mvn -pl zgbas-admin -am test -Dtest=InProcessContractTest` passes.

### Startup Assertions (extending `ZgbasApplicationTest`)

```java
// File: zgbas-admin/src/test/java/com/spt/ZgbasApplicationTest.java (Phase 2 extension)
package com.spt;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ZgbasApplicationTest {

    @Autowired
    private ApplicationContext context;

    @Test
    void contextLoads() {
        // Primary gate: context starts without exceptions — covers PERSIST-03/04, EXT-01..04, INFRA-04
    }

    @Test
    void primaryDataSourceIsPresent() {           // PERSIST-03
        assertThat(context.getBean(DataSource.class)).isNotNull();
    }

    @Test
    void jpaTransactionManagerIsPrimary() {        // PERSIST-03 D-P2-03
        // Spring Boot registers JPA TM as "transactionManager"; @Primary by virtue of uniqueness
        assertThat(context.containsBean("transactionManager")).isTrue();
        assertThat(context.getBean(org.springframework.transaction.PlatformTransactionManager.class))
            .isInstanceOf(org.springframework.orm.jpa.JpaTransactionManager.class);
    }

    @Test
    void sampleMapperBeanRegistered() {            // PERSIST-03 sample Mapper
        assertThat(context.containsBean("sampleMapper")).isTrue();
    }

    @Test
    void externalSdkBeansRegistered() {            // EXT-01..03
        assertThat(context.containsBean("authOpenFacade")).isTrue();
        assertThat(context.containsBean("pushClientHttp")).isTrue();
        assertThat(context.containsBean("fileRemote")).isTrue();
    }
}
```

### Wave 0 Gaps
- [ ] `zgbas-admin/src/test/java/com/spt/proof/InProcessContractTest.java` — covers INFRA-02
- [ ] Extend `ZgbasApplicationTest` with the 4 new assertions above — covers PERSIST-03/04 + EXT-01..03
- [ ] `zgbas-common/pom.xml` — declare every pinned 3rd-party dep
- [ ] `zgbas-framework/pom.xml` — declare auth-sdk / spt-push-sdk / spt-file-sdk + JPA + mybatis-plus
- [ ] `zgbas-admin/pom.xml` — declare spt-sign-client
- [ ] `zgbas-system/src/main/java/com/spt/bas/system/dao/SampleMapper.java` + `resources/mybatis/mappers/SampleMapper.xml` — sample Mapper
- [ ] `zgbas-admin/src/main/resources/application-dev.yml` + `application-prod.yml` — new config files

## Security Domain

### Applicable ASVS Categories

| ASVS Category | Applies | Standard Control |
|---------------|---------|-----------------|
| V2 Authentication | no (Phase 3) | — (Shiro deferred) |
| V3 Session Management | no (Phase 3) | — (Shiro deferred) |
| V4 Access Control | no (Phase 3) | — |
| V5 Input Validation | yes (config) | Spring Boot `${VAR:default}` placeholder validation; fail-fast on missing required env vars |
| V6 Cryptography | no | — (no new crypto; SHA-1 + salt for passwords is Phase 3 AUTH-04 deferred) |
| V7 Error Handling | yes (logging) | Log redaction — FrameworkConfig source logs `secretKey/appCode` at INFO level (line 55); **remove sensitive values from log lines in ZgbasExternalBeansConfig** |
| V8 Data Protection | yes (secrets) | D-P2-13 secret externalization; rotate prod DB password; never commit plaintext |
| V9 Communications | yes (external HTTP) | External SDKs handle their own HTTP; production must terminate TLS at gateway (out of scope) |
| V14 Configuration | yes | D-P2-14 profile = dev + prod; D-P2-02 disable Flyway; D-P2-11 drop nacos; production deploy must inject env vars |

### Known Threat Patterns for Spring Boot 2.5.9 monolith + external HTTP SDK stack

| Pattern | STRIDE | Standard Mitigation |
|---------|--------|---------------------|
| Plaintext DB password in git history (tech debt) | Information Disclosure | D-P2-13 rotate prod DB password; use `${DB_PASSWORD}` placeholder; document rotation in commit message. |
| xxl-job accessToken leaked in `application.properties` | Information Disclosure | D-P2-11 drop `xxl.job.*` keys entirely; if Phase 6 needs them, use env-var placeholder. |
| Source `FrameworkConfig` logs secretKey + appCode at INFO | Information Disclosure | `ZgbasExternalBeansConfig` (Phase 2 rewrite) MUST NOT log sensitive values — drop the `logger.info("url:{},secretKey:{},appCode:{}", ...)` line. |
| Druid stat filter exposes SQL stats via HTTP | Information Disclosure | Phase 2 sets `filters: stat` to mirror source; production should firewall the Druid stat endpoint (no `/druid/*` is auto-exposed without `druid.stat-view-servlet.enabled=true` — safe by default). |
| External HTTP SDKs use plaintext HTTP URLs | Spoofing / Tampering | Production URLs in `application-prod.yml` must be HTTPS (currently source shows HTTP — out-of-scope tech debt). |
| cfca sign client SpEL evaluates `#{signServerConfig.url}` | Injection | SpEL expression is hardcoded in source jar; user input never reaches SpEL — safe. |

## Sources

### Primary (HIGH confidence)
- `/Users/alan/WorkSpace/IDEA/tools/spt-tools-*/` — read every module's `pom.xml` + key source files (core/config/ToolsCoreConfig, jpa/vo/IdEntity, jpa/dao/BaseDao, jpa/listener/EntityListener, jpa/config/ToolsJpaConfig, data/util/DataSourceCreator, data/config/DataSourceConfig, data/vo/DataEntity, mybatis/config/ToolsMybatisConfig, mybatis/annotation/MyBatisDao, http/config/ToolsHttpConfig, http/feign/FeignConfig, config/EnableToolsWebConfig + EnableToolsServiceConfig)
- `/Users/alan/WorkSpace/IDEA/zgbas/basCore/basServer/src/main/java/com/spt/bas/server/` — read BasServer boot class + FrameworkConfig + BasPiccConfig; counted 240 Dao + 0 @Entity files
- `/Users/alan/WorkSpace/IDEA/zgbas/basCore/basClient/src/main/java/com/spt/bas/client/entity/` — counted 239 entity files, 234 with `@Entity`; read sample ApiExternalHis
- `/Users/alan/WorkSpace/IDEA/zgbas/basCore/basClient/src/main/java/com/spt/bas/client/remote/IApplyCreditCycleClient.java` + `constant/BasConstants.java` — confirmed 238 @FeignClient contracts
- `/Users/alan/WorkSpace/IDEA/zgbas/basReport/reportServer/src/main/java/com/spt/bas/report/server/` — read ReportServer boot class + sample RptInvoiceBillMapper; counted 54 @FeignClient in reportClient + 53 Mapper XML files
- `/Users/alan/WorkSpace/IDEA/zgbas/basCore/basServer/src/main/resources/{application.properties,application-dev.properties,application-prod.properties,jdbc.properties,config.properties,bootstrap.yml}` — full config inventory
- `/Users/alan/App/Repository/com/spt/spt-parent/2.5.3/spt-parent-2.5.3.pom` — extracted shiro.version=1.8.0, druid.version=1.2.8, poi.version=4.1.2, spring-cloud.version=2020.0.5, guava.version=27.1-jre, etc.
- `/Users/alan/WorkSpace/IDEA/zgbas/bas-parent/pom.xml` — extracted hutool.version=5.5.9, fastjson.version=1.2.75, spt-tool.version=1.1.1-SNAPSHOT, spt-push-sdk.version=2.0.15-SNAPSHOT, spt-file-sdk.version=2.1.5-SNAPSHOT
- `/Users/alan/WorkSpace/IDEA/tools/spt-tools-parent/pom.xml` — extracted mysql-connector-java=8.0.13, commons-text=1.1
- `/Users/alan/WorkSpace/IDEA/sign/signClient/src/main/java/com/spt/sign/client/` — read ICfcaSignClient + SignClientConfig + SignConstants
- `/Users/alan/App/Repository/com/spt/spt-sign-client/1.0.0-SNAPSHOT/spt-sign-client-1.0.0-SNAPSHOT.jar` — unzip -l verified classes including `com/spt/sign/client/remote/ICfcaSignClient.class` + `SignClientConfig.class`
- `/Users/alan/App/Repository/com/spt/micoservice/spt-push-sdk/2.0.15-SNAPSHOT/` + `spt-file-sdk/2.1.5-SNAPSHOT/` — unzip -l verified `PushClientHttp.class` + `FileRemote.class`
- `/Users/alan/WorkSpace/IDEA/zgbas-plus/` — Phase 1 skeleton baseline; `mvn compile` zero-error verified during research (BUILD SUCCESS 0.429s)

### Secondary (MEDIUM confidence)
- Spring Boot 2.5.9 reference documentation on JPA auto-configuration (JpaTransactionManager naming) — `[CITED]`
- spring-cloud-openfeign behavior on `@EnableFeignClients(basePackages=...)` — `[CITED]`

### Tertiary (LOW confidence — flagged for validation)
- `org.springframework.cloud:spring-cloud-starter-openfeign` exact resolved version (relies on BOM 2020.0.5 resolution; will surface at first compile)
- jjwt exact version (relies on Spring Boot 2.5.9 dependency management; will surface at Wave 1 compile gate)

## Metadata

**Confidence breakdown:**
- Standard stack: HIGH — every version read from source poms + jars verified on disk
- Architecture: HIGH — patterns traced through source code line-by-line; Phase 1 skeleton confirms baseline
- Pitfalls: HIGH — each pitfall grounded in a specific source file observation or MEMORY.md lesson
- Cross-module dependency map: HIGH — read each module's pom.xml directly

**Research date:** 2026-07-16
**Valid until:** 2026-08-15 (30 days — versions pinned from source poms are stable; Spring Boot 2.5.9 is end-of-life so no upstream movement)
