# Phase 2: 基础设施 - Pattern Map

**Mapped:** 2026-07-16
**Files analyzed:** 16 new/modified (across 4 modules + root pom)
**Analogs found:** 16 / 16 (all source-project analogs verified; this repo is a Phase-1 skeleton with no real code)

> **How to read this doc.** Every Phase-2 file is NEW (the repo only has Phase-1 `PackageMarker` placeholders). The real analogs live in 2 source trees: `/Users/alan/WorkSpace/IDEA/tools/spt-tools-*` (verbatim inline source) and `/Users/alan/WorkSpace/IDEA/zgbas/...` (basServer/basClient wiring to port). RESEARCH.md §A–F already holds the full target code for framework/boot/sample-Mapper/proof/config — this map gives the **source analog path + the load-bearing excerpt** so the planner knows exactly which lines to copy/adapt and which to change. Do NOT re-derive versions or wiring; cite RESEARCH.md sections.

## File Classification

Order follows D-08 module topology + RESEARCH.md wave order (common → framework → system → admin).

| New/Modified File (Phase 2) | Module | Role | Data Flow | Source Analog (abs path) | Match |
|-----------------------------|--------|------|-----------|--------------------------|-------|
| `pom.xml` (root delta) | root | config | build | `spt-parent-2.5.3.pom` + `bas-parent/pom.xml` (version pins) | exact (version source) |
| `zgbas-common/pom.xml` (delta) | common | config | build | `tools/spt-tools-*/pom.xml` (10 module poms) | exact |
| `com/spt/tools/core/*` (70 files) | common | utility (inline) | verbatim copy | `tools/spt-tools-core/src/main/java/com/spt/tools/core/**` | exact (copy verbatim) |
| `com/spt/tools/{data,http,file}/*` (40 files) | common | utility (inline) | verbatim copy | `tools/spt-tools-{data,http,file}/src/main/java/com/spt/tools/**` | exact |
| `com/spt/tools/{jpa,web,mybatis,shiro,aop,config}/*` (62 files) | common | utility+infra (inline) | verbatim copy | `tools/spt-tools-{jpa,web,mybatis,shiro,aop,config}/...` | exact |
| `com/spt/framework/config/ZgbasDataSourceConfig.java` (NEW) | framework | infra-wiring | persistence-base | `zgbas/basCore/basServer/.../config/FrameworkConfig.java` + `tools/spt-tools-jpa/.../config/ToolsJpaConfig.java` | role-match (port + add `@Primary`) |
| `com/spt/framework/config/ZgbasMybatisConfig.java` (NEW) | framework | infra-wiring | persistence-base | `tools/spt-tools-mybatis/.../config/ToolsMybatisConfig.java` | role-match (subset) |
| `com/spt/framework/config/ZgbasExternalBeansConfig.java` (NEW) | framework | external-bean | request-response | `zgbas/basCore/basServer/.../config/FrameworkConfig.java:39-71` | exact (port + drop secret log) |
| `com/spt/bas/client/entity/*` (234 files bulk) | system | entity | persistence-base (JPA) | `zgbas/basCore/basClient/.../entity/**` | exact (copy verbatim) |
| `com/spt/bas/server/dao/*` (240 files bulk) | system | dao | CRUD (JPA) | `zgbas/basCore/basServer/.../dao/**` | exact (copy verbatim) |
| `com/spt/bas/system/dao/SampleMapper.java` (NEW) | system | mapper | request-response (mybatis) | `zgbas/basReport/reportServer/.../dao/RptInvoiceBillMapper.java` | role-match (trivial) |
| `resources/mybatis/mappers/SampleMapper.xml` (NEW) | system | config (mapper xml) | file-I/O | (any `zgbas/basReport/.../mapper/*.xml`) | role-match (trivial) |
| `com/spt/ZgbasApplication.java` (MODIFY Phase-1) | admin | boot-class | startup | `zgbas/basCore/basServer/.../BasServer.java:27-37` | role-match (narrow + drop) |
| `com/spt/proof/InProcessContract.java` + `Impl.java` (NEW) | admin | proof | request-response | `zgbas/basCore/basClient/.../remote/I*Client.java` (contract shape) | role-match (trivial) |
| `src/test/.../ZgbasApplicationTest.java` (MODIFY) + `InProcessContractTest.java` (NEW) | admin | test | startup | RESEARCH.md §Validation (lines 1005-1058) | exact (copy from research) |
| `resources/application{,-dev,-prod}.yml` (NEW/MODIFY) | admin | config | startup | `zgbas/basCore/basServer/.../resources/{application,jdbc,config}.properties` | role-match (translate → yml) |

---

## Pattern Assignments

### Wave 0 — root pom version pins + module pom deltas (D-P2-08)

**Analog (version source):** `/Users/alan/App/Repository/com/spt/spt-parent/2.5.3/spt-parent-2.5.3.pom` + `/Users/alan/WorkSpace/IDEA/zgbas/bas-parent/pom.xml`
**Target code:** RESEARCH.md §"Installation (root pom.xml delta)" lines 152-212 — copy that `<properties>` + `<dependencyManagement>` block verbatim.
**Load-bearing rule:** pin old versions (Hutool 5.5.9 / fastjson 1.2.75 / Druid 1.2.8 / Shiro 1.8.0 / mybatis-plus 3.1.2 / poi 4.1.2 / mysql 8.0.13). Do NOT security-upgrade (D-P2-08).
**spt-tools-config gotcha (RESEARCH.md line 424):** drop `spt-tools-kafka` + `spt-tools-redis` `provided` deps from common pom — `EnableToolsWebConfig.java`/`EnableToolsServiceConfig.java` only import core/http/jpa/shiro/aop (verified: `/Users/alan/WorkSpace/IDEA/tools/spt-tools-config/src/main/java/com/spt/tools/config/EnableToolsWebConfig.java:23-26`).

### Wave 1 — spt-tools inline (10 modules → `zgbas-common`, D-P2-06/07)

**Analog (all 10):** `/Users/alan/WorkSpace/IDEA/tools/spt-tools-{core,data,http,file,jpa,web,mybatis,shiro,aop,config}/src/main/java/com/spt/tools/**`
**Pattern:** verbatim copy preserving `com.spt.tools.*` package. Layer-by-layer compile gate (RESEARCH.md §"Pattern 1" lines 383-424). No code excerpts needed — these are copy operations.
**Critical order:** core → (data,http,file) → (jpa,web,mybatis,shiro,aop) → config. `mvn -pl zgbas-common -am compile` GREEN between each layer (Phase-1 gotcha-cascade lesson).

### Wave 2 — `zgbas-framework` infra wiring (3 NEW config classes)

#### `ZgbasDataSourceConfig.java` (the load-bearing one — dual-ORM root)

**Analogs (2 sources merged):**
- `/Users/alan/WorkSpace/IDEA/zgbas/basCore/basServer/src/main/java/com/spt/bas/server/config/FrameworkConfig.java:33-37` — prefix binding pattern
- `/Users/alan/WorkSpace/IDEA/tools/spt-tools-jpa/src/main/java/com/spt/tools/jpa/config/ToolsJpaConfig.java:34-40` — the colliding bean to override

**Source binding (FrameworkConfig.java:33-37)** — the ONE line that changes (D-P2-15 prefix `bas.datasource` → `spring.datasource.druid`):
```java
@Bean
@ConfigurationProperties(prefix = "bas.datasource")   // ← CHANGE to "spring.datasource.druid"
public DataSourceConfig dataSourceConfig() {
    return new DataSourceConfig();
}
```

**The collision to defeat (ToolsJpaConfig.java:34-40 AND ToolsMybatisConfig.java:20-26 — IDENTICAL):**
```java
@Bean("datasource")
@ConditionalOnBean(DataSourceConfig.class)
@ConditionalOnMissingBean
//@Primary // ← COMMENTED OUT in source — the root cause of ambiguity
public javax.sql.DataSource dataSource(DataSourceConfig config) {
    return DataSourceCreator.createDataSource(config);
}
```
Both inline configs declare the same bean with `@ConditionalOnMissingBean`. Strategy: framework declares `@Bean("datasource") @Primary DataSource` FIRST → both spt-tools beans back off via `@ConditionalOnMissingBean`. Target code = RESEARCH.md §A `ZgbasDataSourceConfig` (lines 531-568). Uses `DataSourceCreator.createDataSource(config)` (verified `/Users/alan/WorkSpace/IDEA/tools/spt-tools-data/src/main/java/com/spt/tools/data/util/DataSourceCreator.java:21` — prefix-agnostic, 14 Druid params).

#### `ZgbasMybatisConfig.java`

**Analog:** `/Users/alan/WorkSpace/IDEA/tools/spt-tools-mybatis/src/main/java/com/spt/tools/mybatis/config/ToolsMybatisConfig.java` (lines 36-51)
**Keep from source:** `MyMetaObjectHandler` @Bean (line 40), `PerformanceInterceptor` @Bean `@Profile("!prod")` (line 49). Target code = RESEARCH.md §A second block (lines 571-592). `@MapperScan` + `PaginationInterceptor` go here.
**`MyBatisDao` annotation** (the mapper marker, verified `/Users/alan/WorkSpace/IDEA/tools/spt-tools-mybatis/src/main/java/com/spt/tools/mybatis/annotation/MyBatisDao.java:22`): `@Component`-meta-annotated, used by `annotationClass=MyBatisDao.class` on `@MapperScan`.

#### `ZgbasExternalBeansConfig.java` (EXT-01/02)

**Analog:** `/Users/alan/WorkSpace/IDEA/zgbas/basCore/basServer/src/main/java/com/spt/bas/server/config/FrameworkConfig.java:39-71` — port verbatim, 3 beans.
**Source pattern (lines 39-47, FileRemote):**
```java
@Bean
public FileRemote fileRemote() {
    FileRemote http = new FileRemote();
    http.init(env.getProperty("spt.app.secretKey"),
              env.getProperty("spt.app.appCode"),
              env.getProperty("file.server.url"));
    return http;
}
```
`authOpenFacade()` (lines 49-61) + `pushClientHttp()` (lines 63-71) follow same shape.
**CRITICAL security fix (RESEARCH.md §Security line 1087):** source logs secretKey+appCode at INFO — `FrameworkConfig.java:55`:
```java
logger.info("url:{},secretKey:{},appCode:{}", serverUrl, secretKey, appCode);  // ← DELETE this line
```
Target code = RESEARCH.md §A third block (lines 595-644) — already has the log line removed. Drop the `AuthProxy.getService()` null-check block (lines 57-59) too (BasPiccConfig/BasClientConfig are out-of-scope microservice residue).

### Wave 3 — `zgbas-system` data layer (bulk copy + 1 sample Mapper)

#### Entities + Dao (234 + 240 files, D-P2-01/05)

**Analogs (verbatim bulk copy):**
- Entities: `/Users/alan/WorkSpace/IDEA/zgbas/basCore/basClient/src/main/java/com/spt/bas/client/entity/**` (234 `@Entity`)
- Dao: `/Users/alan/WorkSpace/IDEA/zgbas/basCore/basServer/src/main/java/com/spt/bas/server/dao/**` (240 Dao)

**Sample entity shape (verified `ApiExternalHis.java:17-20`)** — confirms `extends IdEntity` + `@Table(name="t_api_external_his")` for the SampleMapper query target:
```java
@Entity
@Table(name = "t_api_external_his")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ApiExternalHis extends IdEntity {
```
**Sample Dao shape (verified `BsDictTypeDao.java:15-16`)** — `extends BaseDao<T>` + optional `@Query`:
```java
public interface BsDictTypeDao extends BaseDao<BsDictType> {
    @Query(value="select count(*) from BsDictType m where ...")
    public Long existDictTypeCd(...);
}
```
**Inheritance chain (must compile intact):** `BaseDao<T>` (`/Users/alan/WorkSpace/IDEA/tools/spt-tools-jpa/src/main/java/com/spt/tools/jpa/dao/BaseDao.java:16`) = `@NoRepositoryBean extends PagingAndSortingRepository<T,Long>, JpaSpecificationExecutor<T>`. `IdEntity` (`.../jpa/vo/IdEntity.java:27-29`) = `@MappedSuperclass @EntityListeners(EntityListener.class) extends DataEntity`. No rewrites — copy verbatim.

#### Sample Mapper (D-P2-04 trivial proof)

**Analog:** `/Users/alan/WorkSpace/IDEA/zgbas/basReport/reportServer/src/main/java/com/spt/bas/report/server/dao/RptInvoiceBillMapper.java:10-11` — `@MyBatisDao` interface shape.
**Target code:** RESEARCH.md §C (lines 678-703) — `SampleMapper.java` (`countAll()`) + `SampleMapper.xml` (`select count(*) from t_api_external_his`). Table verified to back `ApiExternalHis` @Entity (above).

### Wave 4 — `zgbas-admin` boot + proof + config

#### `ZgbasApplication.java` (MODIFY Phase-1 bare class)

**Analog:** `/Users/alan/WorkSpace/IDEA/zgbas/basCore/basServer/src/main/java/com/spt/bas/server/BasServer.java:27-37`
**Source annotation set (lines 27-36) → what changes:**
```java
@PropertySource(...)              // ← DROP (D-P2-14 native profile)
@SpringBootApplication            // ← KEEP (no auto-config exclusions — WANT DataSource+JPA)
@EnableDiscoveryClient            // ← DROP (nacos removed, D-P2-11)
@Import({ReportClientConfig...})  // ← DROP (microservice residue)
@EnableFeignClients(basePackages = {"com.spt.bas.report.client.remote",
    "com.spt.bas.client.remote", ..., "com.spt.sign.client.remote", ...})  // 6 packages ← NARROW to 1
@ComponentScan(...)               // ← DROP (com.spt base covers all)
@EnableJpaRepositories(...)       // ← KEEP, rebase to com.spt.bas.server.dao
@EntityScan(basePackageClasses = IdEntity.class, basePackages = {...})  // ← KEEP shape (Pitfall 2!)
@EnableToolsServiceConfig         // ← optional (config module inline)
```
**Target code:** RESEARCH.md §B (lines 648-674). Load-bearing narrowing: `@EnableFeignClients(basePackages="com.spt.sign.client.remote")` (D-P2-12 — prevents double-bean when Phase-4 impls land). **Pitfall 2 critical:** keep `basePackageClasses = IdEntity.class` so `com.spt.tools.jpa.vo` (where `IdEntity` lives) is scanned — else `Unknown entity`.

#### In-process Feign proof (D-P2-10/11)

**Analog (contract shape):** any `/Users/alan/WorkSpace/IDEA/zgbas/basCore/basClient/.../remote/I*Client.java` — but Phase 2 uses a TRIVIAL standalone interface (no `@FeignClient`).
**Target code:** RESEARCH.md §D (lines 705-758) — `InProcessContract` (pure interface with `@GetMapping`) + `InProcessContractImpl` (`@RestController implements`) + `InProcessContractTest` (`@Autowired` resolves to local bean). Proves Spring MVC honors interface `@RequestMapping`.

#### Config consolidation (D-P2-13/14/15, EXT-04)

**Analog (source keys):** `/Users/alan/WorkSpace/IDEA/zgbas/basCore/basServer/src/main/resources/{application,jdbc,config}.properties` + 4 profiles.
**Target code:** RESEARCH.md §E (lines 763-896) — full key migration table + 3 target YAMLs.
**Load-bearing deltas:** `bas.datasource.*` → `spring.datasource.druid.*` (D-P2-15); `spring.flyway.enabled=false` + `ddl-auto=validate` (D-P2-02); secrets → `${VAR:}` placeholders in prod, plaintext dev defaults in dev (D-P2-13); drop `xxl.job.*` + `spring.cloud.nacos.*` entirely.

#### Tests (D-P2-03 startup-verification)

**Analog:** RESEARCH.md §Validation "Startup Assertions" (lines 1005-1058) — extend Phase-1 `ZgbasApplicationTest`.
**Assertions to add:** `primaryDataSourceIsPresent` / `jpaTransactionManagerIsPrimary` / `sampleMapperBeanRegistered` / `externalSdkBeansRegistered`. These ARE the PERSIST-03/04 + EXT-01..03 success criteria.

---

## Shared Patterns

### DataSource ownership (applies to: framework wiring + both ORM sides)
**Source collision:** `ToolsJpaConfig.java:34` AND `ToolsMybatisConfig.java:20` declare identical `@Bean("datasource") @ConditionalOnMissingBean` with `@Primary` commented out. Framework MUST declare `@Primary` first — see RESEARCH.md Pitfall 1 (line 471).

### JPA entity/audit chain (applies to: all 234 entities + `@EntityScan`)
**Chain:** `IdEntity` (`@MappedSuperclass @EntityListeners(EntityListener.class)`, line 27-28) → `EntityListener` (`@PrePersist`/`@PreUpdate` + `PropertyUtils.setProperty`, lines 18-51) → every entity `extends IdEntity`. `@EntityScan(basePackageClasses=IdEntity.class)` is MANDATORY or entities fail to map (Pitfall 2, line 477).

### External HTTP SDK init (applies to: 3 framework beans)
**Shape:** `new Facade(); facade.init(env.getProperty("spt.app.secretKey"), env.getProperty("spt.app.appCode"), env.getProperty("<service>.url"));` — identical for FileRemote/AuthOpenFacade/PushClientHttp (FrameworkConfig.java:39-71). Keys resolved from `application-{dev,prod}.yml` (EXT-04).

### Feign narrowing (applies to: boot class + all Phase-4 impls)
**Rule:** `@EnableFeignClients(basePackages="com.spt.sign.client.remote")` ONLY. Never broaden. Internal `I*Client` interfaces become pure contracts satisfied by local `@RestController implements` beans (Pitfall 5, line 495).

---

## Anti-Patterns to Avoid

Point at RESEARCH.md §"Common Pitfalls" (line 469) — do NOT re-derive; planner must read that section. Summary:

| # | Anti-pattern | Where in RESEARCH.md | One-line why |
|---|--------------|----------------------|--------------|
| 1 | Skip framework `@Primary DataSource`, rely on spt-tools implicit bean | Pitfall 1 (line 471) | `ToolsJpaConfig.dataSource` vs `ToolsMybatisConfig.dataSource` race — both have `@Primary` commented out (`:37`, `:23`); flaky tests |
| 2 | `@EntityScan` missing `IdEntity` package | Pitfall 2 (line 477) | `MappingException: Unknown entity` at first query — `IdEntity` lives in `com.spt.tools.jpa.vo`, not `com.spt.bas.client.entity` |
| 3 | Leave `spring.flyway.baseline-on-migrate` dangling | Pitfall 3 (line 483) | Stale `flyway_schema_history` misreports; set `enabled=false` only |
| 4 | mybatis-plus 3.1.2 + Hibernate 5.4 type-alias clash | Pitfall 4 (line 489) | Set `mybatis-plus.type-aliases-package` explicitly or empty |
| 5 | Broaden `@EnableFeignClients` beyond sign client | Pitfall 5 (line 495) | `NoUniqueBeanDefinitionException` when Phase-4 impls land |
| 6 | Locale-dependent grep `cannot find symbol` | Pitfall 6 (line 501) | macOS zh_CN emits "找不到符号"; use `^\[ERROR\]` |
| 7 | Forget `JAVA_HOME` prefix (default JDK 21) | Pitfall 7 (line 507) | `UnsupportedClassVersionError`; prefix every mvn with Corretto 1.8 |
| 8 | Copy 3 `nacos.common.utils` files without import swap | Pitfall 8 (line 513) | Phase-4 rule (files absent in Phase 2 — nothing breaks yet) |
| 9 | Missing `sign.server.url` for cfca SpEL | Pitfall 9 (line 521) | `BeanExpressionException` at startup |
| — | Log secretKey/appCode (source `FrameworkConfig.java:55` does) | §Security (line 1087) | Information disclosure — DELETE the `logger.info` line |
| — | Re-enable Flyway / `ddl-auto=update` | §Anti-Patterns (line 449) | Mutates production schema (D-P2-02 forbids) |
| — | Rename entity/Dao packages | §Anti-Patterns (line 448) | Cascades into 1226+ import edits in Phase 4 |

---

## No Analog Found

None. Every Phase-2 file has a verified source analog — this is a "port + wire" phase, not greenfield. The 3 NEW framework config classes (`ZgbasDataSourceConfig` / `ZgbasMybatisConfig` / `ZgbasExternalBeansConfig`) are the closest to "new" but each ports a concrete source pattern (FrameworkConfig.java + ToolsJpa/MybatisConfig). The 2 trivial proofs (InProcessContract, SampleMapper) have RESEARCH.md §C/§D target code already written.

## Metadata

**Analog search scope:** `/Users/alan/WorkSpace/IDEA/tools/spt-tools-*/` (10 inlined modules) + `/Users/alan/WorkSpace/IDEA/zgbas/{basCore/{basServer,basClient},basReport}/` (wiring/entities/Dao/Mapper) + `/Users/alan/WorkSpace/IDEA/zgbas-plus/` (Phase-1 skeleton baseline)
**Source files read for excerpts:** 14 (FrameworkConfig, BasServer, IdEntity, BaseDao, ToolsJpaConfig, ToolsMybatisConfig, EntityListener, DataSourceCreator, DataSourceConfig, ApiExternalHis, BsDictTypeDao, RptInvoiceBillMapper, MyBatisDao, EnableToolsWebConfig, Phase-1 ZgbasApplication)
**Pattern extraction date:** 2026-07-16
