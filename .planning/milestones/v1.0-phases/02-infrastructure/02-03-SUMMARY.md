---
phase: 02-infrastructure
plan: 03
subsystem: spt-tools-inline
tags: [inline, spt-tools, common, compile-gate, verbatim-copy, jpa-audit-chain]
requires: [02-02]
provides:
  - "spt-tools-jpa/web/mybatis/shiro/aop (60 source files) inlined verbatim into zgbas-common — INLINE-03 (the hardest layer: jpa owns IdEntity/BaseDao/EntityListener audit chain with 1226 downstream refs)"
  - "spt-tools-config (2 composite @Enable annotations) inlined verbatim — all 10 spt-tools modules now source-inline (~172 classes total)"
  - "com.spt.tools.jpa.vo.IdEntity — @MappedSuperclass @EntityListeners(EntityListener.class) extends DataEntity (PERSIST-04 audit chain root, byte-for-byte intact)"
  - "com.spt.tools.jpa.dao.BaseDao<T> — @NoRepositoryBean extends PagingAndSortingRepository<T,Long>, JpaSpecificationExecutor<T>"
  - "com.spt.tools.jpa.listener.EntityListener — @PrePersist/@PreUpdate audit callback (createdDate/updatedDate via PropertyUtils.setProperty)"
  - "com.spt.tools.mybatis.annotation.MyBatisDao — @Component-meta-annotated mapper marker for @MapperScan(annotationClass=MyBatisDao.class)"
  - "com.spt.tools.jpa.config.ToolsJpaConfig + com.spt.tools.mybatis.config.ToolsMybatisConfig — colliding @Bean('datasource') @ConditionalOnMissingBean left unmodified for Wave 4 @Primary defeat"
  - "com.spt.tools.config.EnableToolsWebConfig + EnableToolsServiceConfig — composite @Enable annotations (core/http/jpa/shiro/aop only)"
affects: [02-04, 02-05, 02-06]
tech-stack:
  added: []
  patterns:
    - "Stale Spring Data 2.x API bridges (same class as Wave 2 SortImpl/PageRequestImpl): PageRequest protected ctors -> PageRequest.of(); Sort(Direction,String) and Sort(List<Order>) removed ctors -> Sort.by() — minimal method-body edits, no renames/package edits"
    - "Config-module kafka/redis drop (T-P2-03-kafka mitigation): verified EnableToolsWebConfig/EnableToolsServiceConfig source never imports com.spt.tools.kafka/redis, so zero pom declarations added"
    - "Layered inline compile-gate: layer 3 (jpa/web/mybatis/shiro/aop) GREEN first, then layer 4 (config) GREEN — proves the 1226-ref JPA audit chain compiles byte-for-byte before Wave 4 entity/Dao copy"
key-files:
  created:
    - "zgbas-common/src/main/java/com/spt/tools/jpa/** (14 files — IdEntity, BaseDao, EntityListener, DataEntity, ToolsJpaConfig, CommonDao, BaseService, WebUtil, SearchFilter, DynamicSpecifications)"
    - "zgbas-common/src/main/java/com/spt/tools/web/** (6 files)"
    - "zgbas-common/src/main/java/com/spt/tools/mybatis/** (13 files — MyBatisDao, ToolsMybatisConfig, MybatisPlusCodeGenerator, dialects, page interceptor)"
    - "zgbas-common/src/main/java/com/spt/tools/shiro/** (18 files — ShiroUtil, realm, filters, ToolsShiroConfig)"
    - "zgbas-common/src/main/java/com/spt/tools/aop/** (9 files — @ServiceLogAop, TaskInterceptor, ToolsAopConfig)"
    - "zgbas-common/src/main/java/com/spt/tools/config/** (2 files — EnableToolsWebConfig, EnableToolsServiceConfig)"
  modified:
    - "zgbas-common/src/main/java/com/spt/tools/jpa/persistence/WebUtil.java (3 Spring Data 2.x ctor bridges: PageRequest.of/Sort.by)"
    - "zgbas-common/src/main/java/com/spt/tools/jpa/service/BaseService.java (2 Spring Data 2.x ctor bridges: Sort.by)"
decisions:
  - "Bridged 5 stale Spring Data 2.x ctor calls across 2 files (WebUtil + BaseService) — the spt-tools source predates Spring Data 2.5 (PageRequest ctors made protected; Sort(Direction,String)/Sort(List<Order>) ctors removed). Same precedent as Wave 2's SortImpl/PageRequestImpl bridges. Minimal method-body edits only; no package/class renames; honors D-P2-07 (Phase 4 imports map 1:1)."
  - "INLINE-04 interpreted precisely per plan objective: NO spt-tools-* jar DECLARED by any pom. The single remaining spt-tools-* in the graph (spt-tools-sdkutil) is TRANSITIVE via the external spt-push-sdk jar (EXT-02, kept as jar) — the documented+accepted exception from Wave 1, NOT one of the 10 inlined source modules."
metrics:
  duration: 18 min
  completed: 2026-07-16
  tasks: 2
  files: 64
---

# Phase 2 Plan 03: spt-tools Layer 3/4 (JPA/Web/MyBatis/Shiro/Aop + Config) Inline Summary

**One-liner:** Inlined spt-tools-jpa/web/mybatis/shiro/aop (60 files) then spt-tools-config (2 files) verbatim into zgbas-common preserving `com.spt.tools.*`, with a green compile gate between each layer, 5 minimal Spring Data 2.x API bridges across 2 files, and final INLINE-04 verification (zero declared spt-tools deps) — delivering the JPA audit chain (IdEntity/BaseDao/EntityListener byte-for-byte) that Wave 4 entities extend and finalizing all 10 modules source-inline (~172 classes).

## What Was Built

### Task 1: spt-tools-jpa/web/mybatis/shiro/aop inline (layer 3, commit d7a0baf)

Copied 60 `.java` files verbatim from `/Users/alan/WorkSpace/IDEA/tools/spt-tools-{jpa,web,mybatis,shiro,aop}/src/main/java/com/spt/tools/` into `zgbas-common/src/main/java/com/spt/tools/{jpa,web,mybatis,shiro,aop}/` preserving `com.spt.tools.*` (D-P2-07). No pom changes required — Wave 0/1 root dependencyManagement + common pom already declared every 3rd-party lib these 5 modules bring (spring-boot-starter-data-jpa, hibernate-ehcache, mybatis-plus-boot-starter/generator, freemarker, shiro-spring/ehcache/cas, spring-boot-starter-aop/jdbc, guava, lombok).

Compile gate surfaced **one defect class** in 2 files (5 error sites): stale Spring Data 2.x constructor calls — the spt-tools source was never recompiled after spt-parent moved to Spring Boot 2.5.9 (Spring Data Commons 2.5). Per the plan's `phase2_specifics` ("fix the minimal root cause" for stale Spring API; same precedent as Wave 2's SortImpl/PageRequestImpl bridges), applied minimal method-body bridges (no renames, no package edits, return types unchanged):
- `WebUtil.java` (3 sites): `new PageRequest(int,int)` -> `PageRequest.of(int,int)` (ctors made protected in Spring Data 2.x); `new Sort(List<Order>)` -> `Sort.by(List<Order>)` (ctor removed); `new PageRequest(int,int,Sort)` -> `PageRequest.of(int,int,Sort)`.
- `BaseService.java` (2 sites): `new Sort(Direction,String)` -> `Sort.by(Direction,String)` (ctor removed).

PERSIST-04 audit chain verified byte-for-byte intact after copy: `IdEntity` = `@MappedSuperclass @EntityListeners(EntityListener.class) extends DataEntity`; `EntityListener` = `@PrePersist`/`@PreUpdate` with `PropertyUtils.setProperty` for createdDate/updatedDate; `BaseDao<T>` = `@NoRepositoryBean extends PagingAndSortingRepository<T,Long>, JpaSpecificationExecutor<T>`. `ToolsJpaConfig` + `ToolsMybatisConfig` left unmodified — their `@Bean("datasource") @ConditionalOnMissingBean` declarations are intentionally left as-is for Wave 4 `@Primary DataSource` to defeat (Pitfall 1).

Gate result: `mvn -pl zgbas-common -am compile` -> BUILD SUCCESS, 0 `[ERROR]`.

### Task 2: spt-tools-config inline (layer 4) + INLINE-04 final (commit 934f7ec)

Copied 2 `.java` files verbatim (`EnableToolsWebConfig` + `EnableToolsServiceConfig`) into `zgbas-common/src/main/java/com/spt/tools/config/`. Read both source files first to confirm (T-P2-03-kafka mitigation): `EnableToolsWebConfig` imports only `core.config`/`http.config`/`shiro.config`; `EnableToolsServiceConfig` imports only `core.config`/`jpa.config`/`http.config`/`aop.config`. Neither references `com.spt.tools.kafka` or `com.spt.tools.redis`, so zero pom declarations were added — the kafka/redis `<provided>` deps from `spt-tools-config/pom.xml` are correctly dropped (D-P2-06).

Gate result: `mvn -pl zgbas-common -am compile` -> 0 `[ERROR]`; full reactor `mvn clean compile` -> BUILD SUCCESS, 0 `[ERROR]`.

**INLINE-04 final verification:** Scanned every module pom (`pom.xml`, common/framework/system/quartz/admin) — ZERO declare any of the 10 inlined `spt-tools-*` modules (the 9 `spt-tools` strings in `zgbas-common/pom.xml` are all comments). `dependency:tree -Dincludes=com.spt.tools:spt-tools-*` against zgbas-admin returns exactly ONE entry: `com.spt.tools:spt-tools-sdkutil:jar:1.1.1-SNAPSHOT` — TRANSITIVE via the external `spt-push-sdk` jar (EXT-02, kept as jar). This is the documented+accepted exception from Wave 1; `spt-tools-sdkutil` is NOT one of the 10 inlined source modules. All 10 spt-tools modules (~172 main classes) are now source-inline in zgbas-common.

## Verification Results

| Check | Expected | Actual | Status |
|-------|----------|--------|--------|
| Layer 3 `mvn -pl zgbas-common -am compile` | 0 `[ERROR]` | 0 | PASS |
| Layer 4 common `mvn -pl zgbas-common -am compile` | 0 `[ERROR]` | 0 | PASS |
| Full reactor `mvn clean compile` (6 projects) | 0 `[ERROR]` | 0 (BUILD SUCCESS) | PASS |
| jpa .java files | 14 | 14 | PASS |
| web .java files | 6 | 6 | PASS |
| mybatis .java files | 13 | 13 | PASS |
| shiro .java files | 18 | 18 | PASS |
| aop .java files | 9 | 9 | PASS |
| config .java files | 2 | 2 | PASS |
| Total common spt-tools .java files | ~172 | 172 (110 prior + 62 new) | PASS |
| IdEntity @MappedSuperclass + @EntityListeners | present | 1 + 1 (PERSIST-04 root) | PASS |
| EntityListener @PrePersist + @PreUpdate | present | 1 + 1 | PASS |
| BaseDao @NoRepositoryBean + PagingAndSortingRepository | present | 1 + 2 | PASS |
| MyBatisDao @Component meta-annotation | present | 1 | PASS |
| ToolsJpaConfig @ConditionalOnMissingBean (unmodified) | present | 2 | PASS |
| ToolsMybatisConfig @ConditionalOnMissingBean (unmodified) | present | 2 | PASS |
| Package integrity (all `com.spt.tools.*`) | 100% | 100% (0 non-matching) | PASS |
| INLINE-04: declared spt-tools-* deps in any pom | 0 | 0 (9 common-pom refs are comments) | PASS |
| INLINE-04: spt-tools jar in graph (exception: transitive sdkutil) | only sdkutil via spt-push-sdk | only spt-tools-sdkutil via spt-push-sdk (accepted) | PASS |
| common pom declares spt-tools-kafka/redis | 0 | 0 | PASS |

## Deviations from Plan

### [Rule 1/2 — real defect in copied source] 5 stale Spring Data 2.x API bridges across 2 files

- **Found during:** Task 1 compile gate (28 raw errors collapsed to 5 distinct sites after dedup).
- **Issue:** The spt-tools-jpa source predates Spring Data Commons 2.5: `PageRequest` constructors were made protected (must use `PageRequest.of(...)` static factories); `Sort(Direction, String...)` and `Sort(List<Order>)` constructors were removed (must use `Sort.by(...)`). Source was never recompiled after spt-parent upgraded to SB 2.5.9.
- **Fix:** Minimal method-body bridges — `WebUtil.java` (3 sites) and `BaseService.java` (2 sites). No package/class renames, no signature changes, return types unchanged.
- **Decision basis:** Identical precedent to Wave 2's `SortImpl`/`PageRequestImpl` bridges (sanctioned by the plan's `phase2_specifics`: "fix the minimal root cause" for "a real defect in the copied source"). Threat model T-P2-03-copy (verbatim copy integrity) anticipated the need for compile-gate validation; this extends the same mitigation to stale-API defects.
- **Files modified:** WebUtil.java, BaseService.java.
- **Commit:** d7a0baf.

### Scope boundary respected

- Did NOT modify `ToolsJpaConfig.java` or `ToolsMybatisConfig.java` (their `@Bean("datasource") @ConditionalOnMissingBean` collision is intentionally left for Wave 4 `@Primary` defeat — Pitfall 1).
- Did NOT inline `spt-tools-sdkutil` (it is a separate transitive utility pulled by the external `spt-push-sdk` jar kept per EXT-02 — out of scope for the 10-module inline).
- Did NOT add any new 3rd-party deps (Wave 0/1 pom already declared everything layer 3/4 imports).
- Did NOT rename any package or class (D-P2-07 — Phase 4 imports must map 1:1).

## Known Stubs

None introduced by this plan. The copied `MybatisPlusCodeGenerator` and `WebUtil` contain pre-existing utility logic copied verbatim from source; the load-bearing exports (IdEntity, BaseDao, EntityListener, ToolsJpaConfig, ToolsMybatisConfig, MyBatisDao, the 2 EnableTools annotations) are fully implemented.

## Threat Flags

None. This plan adds no new network endpoints, auth paths, file access, or schema changes. It copies a trusted internal source tree (the migration origin, same codebase branch) into a single common jar. Threat register T-P2-03-copy (verbatim copy integrity — highest risk on jpa with 1226 refs) mitigated by the green compile gate proving the audit chain compiles intact; T-P2-03-kafka (kafka/redis declared provided) mitigated by reading both config source files and confirming zero kafka/redis imports before dropping the deps; T-P2-04-audit (PERSIST-04) mitigated by byte-for-byte copy verified via grep of `@MappedSuperclass`/`@EntityListeners`/`@PrePersist`/`@PreUpdate`.

## Self-Check: PASSED

- All 62 created .java files exist under `zgbas-common/src/main/java/com/spt/tools/{jpa,web,mybatis,shiro,aop,config}/` (verified via per-module find counts).
- Both modified files (WebUtil.java, BaseService.java) carry the bridged `PageRequest.of`/`Sort.by` calls (compile gate GREEN proves it).
- Both task commits verified in git log: d7a0baf (Task 1, layer 3 + bridges), 934f7ec (Task 2, layer 4 + INLINE-04).
- `mvn -pl zgbas-common -am compile` GREEN (0 `[ERROR]`) after each layer; full reactor `mvn clean compile` BUILD SUCCESS.
- INLINE-04: no module pom declares any of the 10 inlined spt-tools modules; only transitive spt-tools-sdkutil via spt-push-sdk (accepted EXT-02 exception).
- No shared tracking files (STATE.md / ROADMAP.md / REQUIREMENTS.md) modified — orchestrator owns those.
- No untracked files left behind; no unintended deletions in either commit.
