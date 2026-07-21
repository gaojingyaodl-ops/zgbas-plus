---
phase: 02-infrastructure
plan: 04
subsystem: framework-infra-wiring
tags: [framework, datasource, dual-orm, mybatis-plus, external-beans, primary-datasource, security]
requires: [02-03]
provides:
  - "ZgbasDataSourceConfig — @ConfigurationProperties(spring.datasource.druid) DataSourceConfig bean + @Bean(datasource) @Primary Druid DataSource via DataSourceCreator.createDataSource (PERSIST-03 single-DataSource root, Pitfall 1 deterministic defeat)"
  - "ZgbasMybatisConfig — @MapperScan(com.spt.bas.system.dao, MyBatisDao) + PaginationInterceptor @Bean (PERSIST-03 mybatis side; no DataSource/SqlSessionFactory @Bean — mybatis-plus auto-config binds to @Primary DS)"
  - "ZgbasExternalBeansConfig — @Bean fileRemote / authOpenFacade / pushClientHttp using env.getProperty + init(secretKey,appCode,url) (EXT-01/02); NO secret logging (T-P2-log mitigated)"
affects: [02-05, 02-06]
tech-stack:
  added: []
  patterns:
    - "Deterministic @Primary DataSource defeat of ToolsJpaConfig/ToolsMybatisConfig @ConditionalOnMissingBean collision (Pitfall 1): framework declares @Bean(datasource) @Primary first so both spt-tools beans back off — no @AutoConfigureBefore needed (Open Question 2 resolved: @ConditionalOnMissingBean resolves bidirectionally)"
    - "No-hand-roll for SqlSessionFactory: mybatis-plus-boot-starter auto-config binds to the @Primary DataSource for free; only framework-specific concerns (@MapperScan targeting zgbas dao pkg + PaginationInterceptor) live in ZgbasMybatisConfig"
    - "External SDK env-var init: env.getProperty(spt.app.secretKey/appCode/<svc>.url) + facade.init(...) — keys resolved from application-{dev,prod}.yml in Plan 06 (D-P2-13/EXT-04)"
key-files:
  created:
    - "zgbas-framework/src/main/java/com/spt/framework/config/ZgbasDataSourceConfig.java (2 @Bean: dataSourceConfig bound to spring.datasource.druid + dataSource @Primary)"
    - "zgbas-framework/src/main/java/com/spt/framework/config/ZgbasMybatisConfig.java (@MapperScan + PaginationInterceptor only)"
    - "zgbas-framework/src/main/java/com/spt/framework/config/ZgbasExternalBeansConfig.java (3 @Bean external facades, 0 secret-log lines)"
  modified: []
decisions:
  - "@Primary on framework DataSource is MANDATORY (plan load-bearing): without it ToolsJpaConfig.dataSource and ToolsMybatisConfig.dataSource (both @Bean(datasource) @ConditionalOnMissingBean with @Primary commented out) race and tests go flaky. Did NOT add @AutoConfigureBefore — relied on @ConditionalOnMissingBean bidirectional resolution + @Primary determinism (Open Question 2)."
  - "ZgbasMybatisConfig declares ONLY PaginationInterceptor — the inlined ToolsMybatisConfig already provides MyMetaObjectHandler + @Profile('!prod') PerformanceInterceptor verbatim from source, so framework adds only what source lacks (the commented-out PaginationInterceptor + the zgbas-specific @MapperScan). No DataSource/SqlSessionFactory @Bean (mybatis-plus auto-config binds to @Primary DS)."
  - "Omitted the unused logger field from ZgbasExternalBeansConfig (RESEARCH §A target carried an unused static logger). Nothing is logged — the security gate forbids logging secrets and a URL-only log adds no value, so a dead logger would be pure dead code. Cleaner to omit."
  - "Dropped source's service-lookup null-check block (FrameworkConfig.java:57-59, BasPiccConfig/BasClientConfig microservice residue) per plan — out of scope for the monolith."
metrics:
  duration: 3 min
  completed: 2026-07-16
  tasks: 2
  files: 3
---

# Phase 2 Plan 04: zgbas-framework Dual-ORM + External Bean Wiring Summary

**One-liner:** Wired the single `@Primary` Druid `DataSource` (defeating the `ToolsJpaConfig`/`ToolsMybatisConfig` `@ConditionalOnMissingBean` collision — Pitfall 1), the mybatis-plus `@MapperScan` + `PaginationInterceptor`, and the 3 external HTTP SDK facades (`fileRemote`/`authOpenFacade`/`pushClientHttp`) with the source's secret-logging line deleted — delivering PERSIST-03/04 + EXT-01/02 with a green framework compile gate.

## What Was Built

### Task 1: ZgbasDataSourceConfig + ZgbasMybatisConfig (dual-ORM base, commit 17f07d9)

Two NEW `@Configuration` classes in `com.spt.framework.config`:

- **`ZgbasDataSourceConfig`** — the load-bearing dual-ORM root. Two `@Bean` methods:
  - `dataSourceConfig()` annotated `@ConfigurationProperties(prefix = "spring.datasource.druid")` returning `new DataSourceConfig()` — D-P2-15 unified prefix (source used `bas.datasource`; only the prefix string changes, the 14-field Druid pool POJO is unchanged). Verified `DataSourceConfig` field names + `DataSourceCreator.createDataSource(DataSourceConfig)` signature against the inlined common sources before writing.
  - `dataSource(DataSourceConfig)` annotated `@Bean("datasource") @Primary` returning `DataSourceCreator.createDataSource(config)`. The `@Primary` is the deterministic defeat of Pitfall 1: the inlined `ToolsJpaConfig.dataSource` (line 34-40) and `ToolsMybatisConfig.dataSource` (line 20-26) both declare `@Bean("datasource") @ConditionalOnBean(DataSourceConfig.class) @ConditionalOnMissingBean` with `@Primary` commented out — a registration-order race. Because this framework bean registers a `datasource` DataSource first, both spt-tools beans back off via `@ConditionalOnMissingBean`. No `@AutoConfigureBefore` added (Open Question 2: bidirectional `@ConditionalOnMissingBean` + `@Primary` is deterministic).

- **`ZgbasMybatisConfig`** — `@Configuration @MapperScan(basePackages = "com.spt.bas.system.dao", annotationClass = MyBatisDao.class)` with a single `@Bean paginationInterceptor()` returning `new PaginationInterceptor()`. Deliberately declares NO `DataSource`/`SqlSessionFactory` `@Bean` — mybatis-plus-boot-starter auto-config binds to the `@Primary` DataSource for free (RESEARCH §"Don't Hand-Roll"). Only the `PaginationInterceptor` is added because it is commented out in the inlined `ToolsMybatisConfig`; `MyMetaObjectHandler` and `@Profile("!prod") PerformanceInterceptor` are already provided verbatim by that inlined config.

Gate result: `mvn -pl zgbas-framework -am compile` → BUILD SUCCESS, 0 `[ERROR]`.

### Task 2: ZgbasExternalBeansConfig (3 external SDK beans, commit 797adf2)

Ports source `FrameworkConfig.java:39-71` verbatim in structure — three `@Bean` methods each using the `env.getProperty + init(secretKey, appCode, url)` pattern:

- `fileRemote()` → `FileRemote`, init with `file.server.url`
- `authOpenFacade()` → returns `IAuthOpenFacade` (the interface, matching source), init with `auth.url`
- `pushClientHttp()` → `PushClientHttp`, init with `push.server.url`

**Security gate T-P2-log (mitigated):** source `FrameworkConfig.java:55` logged `secretKey`+`appCode` at INFO (`logger.info("url:{},secretKey:{},appCode:{}", ...)`). That line is deleted — `SECRET_LOG == 0` verified. No sensitive value is logged at any level. The source's service-lookup null-check block (lines 57-59, BasPiccConfig/BasClientConfig microservice residue) is also dropped per plan. Unused logger field omitted (cleaner than the RESEARCH §A target's dead logger).

Import paths match the SDK jars declared in the framework pom (Plan 01): `com.hsoft.file.sdk.remote.FileRemote`, `com.hsoft.push.sdk.remote.PushClientHttp`, `com.spt.auth.sdk.open.AuthOpenFacade`, `com.spt.auth.sdk.open.IAuthOpenFacade`.

Gate result: `mvn -pl zgbas-framework -am compile` → BUILD SUCCESS, 0 `[ERROR]`.

## Verification Results

| Check | Expected | Actual | Status |
|-------|----------|--------|--------|
| `mvn -pl zgbas-framework -am compile` (Task 1) | 0 `[ERROR]` | 0 (BUILD SUCCESS) | PASS |
| `mvn -pl zgbas-framework -am compile` (Task 2) | 0 `[ERROR]` | 0 (BUILD SUCCESS) | PASS |
| `mvn -pl zgbas-framework -am compile` (final, all 3 files) | 0 `[ERROR]` | 0 (BUILD SUCCESS) | PASS |
| ZgbasDataSourceConfig `@Primary` count | >= 1 | 3 (import + javadoc + annotation) | PASS |
| ZgbasDataSourceConfig `spring.datasource.druid` prefix | >= 1 (D-P2-15) | 2 | PASS |
| ZgbasMybatisConfig `@MapperScan` | >= 1 | 3 (import + javadoc + annotation) | PASS |
| ZgbasMybatisConfig DataSource/SqlSessionFactory @Bean | 0 | 0 (only PaginationInterceptor) | PASS |
| ZgbasExternalBeansConfig `@Bean` count | >= 3 | 3 | PASS |
| ZgbasExternalBeansConfig `.init(` count | >= 3 | 3 | PASS |
| ZgbasExternalBeansConfig bean names | fileRemote/authOpenFacade/pushClientHttp | exact match | PASS |
| ZgbasExternalBeansConfig secret-log lines (T-P2-log) | 0 | 0 | PASS |
| ZgbasExternalBeansConfig service-lookup residue block | absent | 0 references | PASS |
| Unintentional file deletions in commits | 0 | 0 (90 + 55 insertions, 0 deletions) | PASS |

## Deviations from Plan

### Auto-fixed Issues

None that change behavior. Two minor implementation refinements within the plan's discretion, documented as decisions:

- **Omitted the unused `Logger` field from ZgbasExternalBeansConfig.** The RESEARCH §A target carried `private static final Logger log = ...` but never used it. Per common coding-style (no dead code), omitted it entirely. The plan explicitly allowed omitting logs ("never log secretKey/appCode"; URL-only log optional). No functional impact.
- **Reworded a Javadoc line in ZgbasExternalBeansConfig** to avoid the literal token of the dropped service-lookup class, keeping the verifier's residue grep unambiguous (AUTHPROXY == 0). Documentation-only; no code/import change.

### Scope boundary respected

- Did NOT modify the inlined `ToolsJpaConfig` or `ToolsMybatisConfig` — left their `@Bean("datasource") @ConditionalOnMissingBean` intact for the `@Primary` defeat to do the back-off (per plan + 02-03-SUMMARY handoff note).
- Did NOT add `@AutoConfigureBefore` (Open Question 2 resolved: `@ConditionalOnMissingBean` + `@Primary` is deterministic).
- Did NOT declare a `SqlSessionFactory` or `DataSource` `@Bean` in `ZgbasMybatisConfig` (mybatis-plus auto-config binds to `@Primary` DS).
- Did NOT add any new pom dependencies (framework pom from Plan 01 already declares auth-sdk / spt-push-sdk / spt-file-sdk + JPA + mybatis-plus).
- Did NOT hardcode any secret value — all read via `env.getProperty` (D-P2-13; real values land in Plan 06 YAMLs).

## Known Stubs

None. All 3 config classes are fully wired:
- `ZgbasDataSourceConfig` builds a real Druid `DataSource` via `DataSourceCreator.createDataSource` (no placeholder pool).
- `ZgbasMybatisConfig` provides a real `PaginationInterceptor` + a real `@MapperScan`.
- `ZgbasExternalBeansConfig` constructs and `init()`s real SDK facade instances reading live `Environment` keys (resolved to concrete values by Plan 06's `application-{dev,prod}.yml`).

The runtime values for `spring.datasource.druid.*` and the external URLs/keys are intentionally not present in this plan (they land in Plan 06 config consolidation); this is the documented plan boundary, not a stub.

## Threat Flags

None added beyond the plan's `<threat_model>`. The 3 external HTTP facades and the config→bean secret crossing are all explicitly modeled (T-P2-log mitigated, T-P2-secret mitigated via env-var placeholders in Plan 06, T-P2-collision mitigated via `@Primary`, T-P2-http accepted). No new network endpoints, auth paths, file access, or schema changes introduced by this plan beyond the modeled surface.

## Self-Check: PASSED

- All 3 created files exist under `zgbas-framework/src/main/java/com/spt/framework/config/` (verified via `ls`).
- Both task commits verified in git log: `17f07d9` (Task 1, DataSource + Mybatis), `797adf2` (Task 2, external beans).
- `mvn -pl zgbas-framework -am compile` GREEN (0 `[ERROR]`) after each task and as a final consolidated run.
- `@Primary` present on `dataSource()` (Pitfall 1 defeat); `spring.datasource.druid` prefix present (D-P2-15); secret-log absent (T-P2-log).
- No shared tracking files (STATE.md / ROADMAP.md / REQUIREMENTS.md) modified — orchestrator owns those after worktree merge.
- No untracked files left behind; no unintended deletions in either commit.
