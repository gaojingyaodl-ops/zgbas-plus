---
phase: 02-infrastructure
verified: 2026-07-16T18:58:00Z
status: passed
score: 6/7 must-have truths verified (1 UNCERTAIN — resolved by human decision 2026-07-16, see Resolution)
resolution_date: 2026-07-16
overrides_applied: 0
human_verification:
  - test: "Re-run capstone gate with the real DB password exported"
    expected: "mvn -pl zgbas-admin -am test -Dtest=ZgbasApplicationTest,InProcessContractTest exits BUILD SUCCESS, Tests run: 6, Failures: 0, Errors: 0"
    why_human: "CR-01 fix correctly externalized DB_PASSWORD with NO default. The capstone context-load cannot reach MySQL at 47.104.15.98 without the real credential, which is not (and must not be) in the repo. Verifier confirmed wiring is sound (dummy password yields 'Access denied for user sptbaspduser', NOT a wiring exception), but cannot independently reproduce the SUMMARY's 6/6 GREEN claim."
  - test: "Decide on WR-02: accept the in-process contract proof as-is, or require a MockMvc/TestRestTemplate HTTP assertion"
    expected: "Either (a) accept that InProcessContractTest proves bean resolution + method result only (HTTP route mapping unproven), or (b) add a GET /proof/echo assertion via TestRestTemplate to prove Spring MVC honors the interface-level @GetMapping"
    why_human: "The D-P2-10 proof test is a plain Java method call on an @Autowired bean. It does NOT issue an HTTP request, so the load-bearing claim 'Spring MVC honors interface @GetMapping on a @RestController impl' is untested. grep confirms no MockMvc/TestRestTemplate/RANDOM_PORT anywhere in the test tree. Phase 4 reuses this exact pattern across 295 clients."
  - test: "Decide on ddl-auto=none deviation from locked D-P2-02 (validate)"
    expected: "Confirm the deferral is acceptable: validate surfaced real pre-existing entity/schema drift across 239 entities (e.g. api_param varchar(255) vs DB mediumtext); source ran implicit none; fixing @Column annotations is a 259-table change explicitly out of Phase 2 scope"
    why_human: "Trade-off between locked decision (validate surfaces drift at startup) and phase boundary (drift fix is Phase 4+ scope). Current code matches source runtime behavior (none) so infra boots; drift becomes Phase 4 debt."
---

# Phase 2: 基础设施 Verification Report

**Phase Goal:** spt-tools 源码内联进 zgbas-common，双 ORM 单 DataSource 共存，外部服务 Bean 保持原 HTTP 注入，nacos 移除，295 个 FeignClient 进程内化，配置文件收敛 — 为业务迁移提供可用的基础设施层
**Verified:** 2026-07-16T18:58:00Z
**Status:** passed (human decisions resolved 2026-07-16 — see Resolution)
**Re-verification:** No — initial verification

## Goal Achievement

The infrastructure codebase delivers a bootable infrastructure layer. Every code-level truth is VERIFIED: spt-tools is fully inlined (172 files), the dual-ORM `@Primary` DataSource is wired correctly, the 3 external SDK beans are constructed with no secret logging, nacos is absent from the dependency graph and config, the Feign mechanism (narrowed `@EnableFeignClients` + interface-as-contract) compiles and resolves, and 3 consolidated YAML profiles replace the 4 microservice configs. The single behavioral gate — the D-P2-03 capstone `@SpringBootTest` context-load — cannot be reproduced GREEN in a clean environment because CR-01 correctly externalized `DB_PASSWORD` with no default; a dummy-password run reaches `Access denied` (auth), proving the wiring is sound and isolating the only missing piece to the credential itself. Three items warrant a human decision (capstone re-run with real password, WR-02 HTTP-mapping proof, ddl-auto deferral).

### Observable Truths

| # | Truth | Status | Evidence |
| --- | --- | --- | --- |
| 1 | spt-tools-\* 10 modules inlined into zgbas-common, runtime classpath no longer depends on spt-tools private-repo jar | ✓ VERIFIED | core 70 / data 17 / http 15 / file 8 / jpa 14 / web 6 / mybatis 13 / shiro 18 / aop 9 / config 2 = 172 .java files under `zgbas-common/src/main/java/com/spt/tools/`; 0 spt-tools-\* declared in any pom; dep:tree shows only transitive `spt-tools-sdkutil:1.1.1-SNAPSHOT` via spt-push-sdk (accepted EXT-02 exception) |
| 2 | Dual ORM single DataSource coexists: @Primary JpaTransactionManager + mybatis, audit fields preserved | ✓ VERIFIED | `ZgbasDataSourceConfig.@Bean("datasource") @Primary` + `DataSourceCreator.createDataSource`; `ZgbasMybatisConfig.@MapperScan`; SampleMapper/XML present; IdEntity `@MappedSuperclass @EntityListeners(EntityListener.class)` + EntityListener `@PrePersist/@PreUpdate` for createdDate/updatedDate byte-for-byte intact |
| 3 | External service beans keep original HTTP/Feign injection, config keys migrated | ✓ VERIFIED | `ZgbasExternalBeansConfig` declares fileRemote/authOpenFacade/pushClientHttp with `init(secretKey,appCode,url)`; 0 secret-log lines; `@EnableFeignClients(com.spt.sign.client.remote)`; spt.app.secretKey/appCode + auth/push/file/sign URLs present in dev+prod yml |
| 4 | nacos removed; 295 FeignClient in-process mechanism established | ✓ VERIFIED (mechanism) | nacos: 0 in dep graph, 0 in yml, 0 declared (pom comment only); `@EnableFeignClients` narrowed so 295 internal interfaces are pure contracts; InProcessContract+Impl compiles and bean resolves. NOTE: HTTP-mapping sub-proof untested (WR-02) — see Human Verification |
| 5 | 4 config files consolidated to single application.yml + profile, datasource prefix unified | ✓ VERIFIED | application.yml + application-dev.yml + application-prod.yml present; `spring.datasource.druid` prefix (D-P2-15); 0 xxl keys, 0 nacos keys; prod all `${VAR}` fail-fast; dev secrets externalized (CR-01 resolved) |
| 6 | @SpringBootTest contextLoads brings up full monolith context (D-P2-03 capstone gate) | ? UNCERTAIN | FAILS in clean env: `DB_PASSWORD` unset → Druid connects to 47.104.15.98 but auth fails → Hibernate `DialectResolutionInfo cannot be null`. Dummy-password run yields `Access denied for user 'sptbaspduser'` (auth), proving wiring is correct and isolating the failure to the missing credential. SUMMARY's 6/6 GREEN is reproducible only with the real DB password exported. |
| 7 | Audit chain compiles intact across all entities (PERSIST-04) | ✓ VERIFIED | 234/239 entities `extends IdEntity`; 239/240 Dao `extends BaseDao`; compile green across reactor |

**Score:** 6/7 truths verified (1 UNCERTAIN → human)

### Required Artifacts

| Artifact | Expected | Status | Details |
| --- | --- | --- | --- |
| `zgbas-common/src/main/java/com/spt/tools/{core,data,http,file,jpa,web,mybatis,shiro,aop,config}/**` | 10 spt-tools modules inlined verbatim | ✓ VERIFIED | 172 files; packages `com.spt.tools.*` preserved; compile green |
| `zgbas-framework/.../ZgbasDataSourceConfig.java` | @Primary Druid DataSource, spring.datasource.druid prefix | ✓ VERIFIED | @Primary (3 occurrences), `@ConfigurationProperties(spring.datasource.druid)`, calls `DataSourceCreator.createDataSource` |
| `zgbas-framework/.../ZgbasMybatisConfig.java` | @MapperScan + PaginationInterceptor only | ✓ VERIFIED | @MapperScan(com.spt.bas.system.dao, MyBatisDao); PaginationInterceptor @Bean; no DataSource/SqlSessionFactory @Bean |
| `zgbas-framework/.../ZgbasExternalBeansConfig.java` | 3 external SDK beans, no secret logging | ✓ VERIFIED | 3 @Bean (fileRemote/authOpenFacade/pushClientHttp), 3 init() calls, 0 secret-log lines |
| `zgbas-system/.../com/spt/bas/client/entity/**` | 239 @Entity extending IdEntity | ✓ VERIFIED | 239 files, 234 extends IdEntity; ApiExternalHis @Table(name="t_api_external_his") |
| `zgbas-system/.../com/spt/bas/server/dao/**` | 240 Dao extending BaseDao | ✓ VERIFIED | 240 files, 239 extends BaseDao |
| `zgbas-system/.../SampleMapper.java` + XML | trivial dual-ORM proof | ✓ VERIFIED | @MyBatisDao, countAll(); XML namespace + `select count(*) from t_api_external_his` |
| `zgbas-admin/.../ZgbasApplication.java` | 4 infra annotations + narrowed Feign | ✓ VERIFIED | @EnableFeignClients(sign.client.remote) + @EntityScan(IdEntity + bas.client.entity + pm.entity) + @EnableJpaRepositories + @ComponentScan excludeFilters (FeignConfig x2 + ToolsShiroConfig). NOTE: @MapperScan is in ZgbasMybatisConfig (WR-01 resolved — single source of truth) |
| `zgbas-admin/src/main/resources/application{,-dev,-prod}.yml` | 3 consolidated profiles | ✓ VERIFIED | all 3 exist; flyway disabled; ddl-auto=none; no xxl/nacos; prod `${VAR}` fail-fast; dev secrets externalized |
| `zgbas-admin/.../InProcessContract.java` + Impl + Test | D-P2-10 in-process proof | ⚠️ PARTIAL (WR-02) | Interface + @RestController impl + @SpringBootTest compile and bean resolves; test is a plain Java call (asserts echo result), NOT a MockMvc/HTTP request — see Human Verification |
| `zgbas-admin/.../ZgbasApplicationTest.java` | 4 startup assertions | ✓ VERIFIED (code) | primaryDataSourceIsPresent + jpaTransactionManagerIsPrimary + sampleMapperBeanRegistered + externalSdkBeansRegistered present; EXECUTION gated on real DB_PASSWORD (truth #6) |

### Key Link Verification

| From | To | Via | Status | Details |
| --- | --- | --- | --- | --- |
| ZgbasDataSourceConfig.dataSource() | DataSourceCreator.createDataSource | @Bean @Primary Druid factory | ✓ WIRED | confirmed in source; dummy-password run proves the DataSource is constructed and attempts connection |
| ZgbasExternalBeansConfig 3 beans | Environment.getProperty(spt.app.*/svc.url) | env.getProperty + init() | ✓ WIRED | 3 init() calls reading live Environment keys resolved by dev/prod yml |
| ZgbasApplication @EntityScan | com.spt.bas.client.entity + IdEntity | basePackageClasses=IdEntity + basePackages | ✓ WIRED | covers 239 entities + pm.entity (14); Pitfall 2 defense present |
| application-dev/prod.yml spring.datasource.druid | ZgbasDataSourceConfig @ConfigurationProperties | prefix binding (D-P2-15) | ✓ WIRED | prefix matches |
| InProcessContractTest @Autowired | InProcessContractImpl @RestController | Spring MVC resolves interface @GetMapping | ⚠️ PARTIAL (WR-02) | bean resolves + method returns correct value; HTTP route mapping NOT asserted (no MockMvc) |

### Data-Flow Trace (Level 4)

| Artifact | Data Variable | Source | Produces Real Data | Status |
| --- | --- | --- | --- | --- |
| ZgbasDataSourceConfig | Druid DataSource | DataSourceCreator.createDataSource(DataSourceConfig bound from spring.datasource.druid) | Yes (constructs real DruidDataSource; connects to 47.104.15.98) | ✓ FLOWING |
| ZgbasExternalBeansConfig | 3 SDK facades | env.getProperty → init(secretKey,appCode,url) | Yes (reads live Environment; keys present in dev/prod yml) | ✓ FLOWING |
| SampleMapper.countAll | mybatis query | SqlSessionFactory bound to @Primary DataSource, XML `select count(*) from t_api_external_his` | Yes (real table backing ApiExternalHis @Entity) | ✓ FLOWING (execution gated on DB credential) |
| ZgbasApplicationTest assertions | context beans | @SpringBootTest full context | Cannot reproduce GREEN without DB_PASSWORD | ⚠️ HOLLOW_ENV (wiring proven sound; credential-locked) |

### Behavioral Spot-Checks

| Behavior | Command | Result | Status |
| --- | --- | --- | --- |
| Full reactor compiles | `mvn clean compile` (JDK8) | BUILD SUCCESS, 0 [ERROR] | ✓ PASS |
| Capstone context-load (clean env) | `mvn -pl zgbas-admin -am test -Dtest=ZgbasApplicationTest,InProcessContractTest` (no DB_PASSWORD) | Tests run: 6, Errors: 6 — `DialectResolutionInfo cannot be null` | ✗ FAIL (env-secret) |
| Capstone context-load (dummy password) | same + `DB_PASSWORD=dummy` | `Access denied for user 'sptbaspduser'@117.143.46.216 (using password: YES)` | ✓ PASS (wiring proven — only auth fails) |
| nacos absent from dep graph | `dependency:tree -Dincludes=com.alibaba.cloud:*-nacos-*` | 0 matches | ✓ PASS |
| spt-tools jar status | `dependency:tree -Dincludes=com.spt.tools:spt-tools-*` | 1 match: transitive spt-tools-sdkutil via spt-push-sdk | ✓ PASS (accepted exception) |
| InProcessContract HTTP proof | grep MockMvc/TestRestTemplate/RANDOM_PORT in tests | 0 matches | ✗ FAIL (WR-02 — no HTTP-level assertion) |

### Probe Execution

Step 7c SKIPPED — Phase 2 has no `scripts/*/tests/probe-*.sh` probes; the validation contract (02-VALIDATION.md) uses the capstone `@SpringBootTest` as the single probe, whose result is captured under Behavioral Spot-Checks above.

### Requirements Coverage

| Requirement | Source Plan | Description | Status | Evidence |
| --- | --- | --- | --- | --- |
| INLINE-01 | 02-02 | spt-tools-core (70 类) inlined into zgbas-common first | MET | 70 files under com.spt.tools.core; compile green |
| INLINE-02 | 02-02 | spt-tools-data/http/file inlined | MET | data 17 / http 15 / file 8 files; compile green |
| INLINE-03 | 02-03 | spt-tools-jpa/web/mybatis/shiro/aop/config inlined | MET | 62 files; audit chain intact |
| INLINE-04 | 02-01/02-03 | inline eliminates spt-tools private-repo jar runtime dep | MET | 0 spt-tools-* declared in poms; only transitive sdkutil via spt-push-sdk (EXT-02 accepted) |
| PERSIST-01 | 02-05 | JPA entity+Dao migration (239+240) | MET | 239 entities (234 extends IdEntity), 240 Dao (239 extends BaseDao); compile green |
| PERSIST-03 | 02-04/02-05/02-06 | dual ORM single DataSource @Primary coexists | MET (code) | @Primary DataSource + mybatis @MapperScan + SampleMapper; context-load GREEN gated on real DB_PASSWORD (human) |
| PERSIST-04 | 02-03/02-05 | audit fields createdDate/updatedDate + @EntityListeners preserved | MET | IdEntity @MappedSuperclass @EntityListeners(EntityListener.class) + @PrePersist/@PreUpdate byte-for-byte |
| EXT-01 | 02-01/02-04 | AuthOpenFacade @Bean init(secretKey,appCode,url) HTTP injection | MET | authOpenFacade() bean present with init + auth.url |
| EXT-02 | 02-01/02-04 | PushClientHttp/FileRemote keep HTTP injection | MET | pushClientHttp() + fileRemote() beans with init |
| EXT-03 | 02-01/02-06 | CfcaSignClient OpenFeign kept, @EnableFeignClients narrowed | MET | @EnableFeignClients(com.spt.sign.client.remote); sign.server.url set (Pitfall 9) |
| EXT-04 | 02-06 | external config keys migrated | MET | spt.app.secretKey/appCode + auth/push/file/sign URLs in dev+prod yml |
| INFRA-01 | 02-01/02-06 | nacos deps + config + utils removed | MET | 0 nacos in dep graph, 0 in yml, 0 declared (comment only) |
| INFRA-02 | 02-06 | 295 FeignClient in-process mechanism | PARTIAL | Mechanism established (narrowed scan + interface-as-contract compiles + bean resolves). HTTP-mapping proof incomplete (WR-02). Bulk 295 conversion deferred to Phase 4/5 per D-P2-11 (in-phase scope = mechanism+proof only). |
| INFRA-04 | 02-06 | 4 configs → single application.yml + profile, unified prefix | MET | 3 YAML profiles; spring.datasource.druid prefix; no xxl/nacos; prod env-var |

No orphaned requirements: all 14 IDs mapped to Phase 2 in REQUIREMENTS.md appear in plan frontmatter and are accounted for above.

### Anti-Patterns Found

| File | Line | Pattern | Severity | Impact |
| --- | --- | --- | --- | --- |
| application.yml | 22 | `ddl-auto: none` (deviation from locked D-P2-02 `validate`) | ℹ️ Info (deferred debt) | Documented: validate surfaced real entity/schema drift across 239 entities; source ran implicit none; drift fix is Phase 4+ scope. Does not block infra boot. |
| application.yml | 10 | `allow-bean-definition-overriding: true` | ℹ️ Info (accepted) | Resolves 11 sign-client @FeignClient sharing name="sign" (FeignClientSpecification collision). Standard Spring Cloud OpenFeign fix. |
| ZgbasApplication.java | 57-65 | @ComponentScan excludeFilters (2 FeignConfig + ToolsShiroConfig) | ℹ️ Info (accepted) | Resolves dual FeignConfig bean-name collision + keeps Phase-3 Shiro dormant (ehcache conflict). Documented. |
| ZgbasExternalBeansConfig.java | 26-27 | `@Autowired private Environment env` (field injection) | ℹ️ Info (IN-01) | Diverges from constructor-injection convention; low-risk for Environment in @Configuration. Not a blocker. |
| InProcessContractTest.java | 26-27 | proof test is plain Java call, no HTTP assertion | ⚠️ Warning (WR-02) | D-P2-10 HTTP-mapping claim untested — see Human Verification |

No `TBD`/`FIXME`/`XXX` debt markers in Phase-2 authored files. No stub implementations in authored wiring (the inlined spt-tools source carries pre-existing stub-style utilities copied verbatim, out of scope).

### Human Verification Required

### 1. Capstone re-run with real DB password

**Test:** Export the real `DB_PASSWORD` (and `SPT_APP_SECRET`) for sptbaspduser@sptbasdb_pd, then run `JAVA_HOME=<corretto-1.8> mvn -s zg_settings.xml -pl zgbas-admin -am test -Dtest=ZgbasApplicationTest,InProcessContractTest`.
**Expected:** BUILD SUCCESS, Tests run: 6, Failures: 0, Errors: 0 — full context loads (Druid @Primary DataSource, JpaTransactionManager, sampleMapper, 3 external SDK beans, cfca sign Feign, 253 entities).
**Why human:** CR-01 correctly externalized `DB_PASSWORD` with no default. Verifier confirmed the wiring is sound (dummy-password run yields `Access denied`, not a wiring exception), but cannot reproduce the SUMMARY's 6/6 GREEN without the credential, which must not live in the repo. The leaked value still needs rotation (outward-facing user action).

### 2. WR-02 — accept or strengthen the in-process contract proof

**Test:** Decide whether `InProcessContractTest` (plain `contract.echo("hi")` assertion) is sufficient proof for D-P2-10, or whether a `TestRestTemplate`/`MockMvc` GET `/proof/echo?msg=hi` assertion is required.
**Expected:** Either (a) accept bean-resolution proof as the Phase-2 mechanism demonstration, or (b) add an HTTP-level assertion so the claim "Spring MVC honors interface @GetMapping" is behaviorally proven before Phase 4 reuses the pattern across 295 clients.
**Why human:** Interface-level MVC annotation honoring is a well-known historical gotcha; the current test does not exercise it. grep confirms no MockMvc/TestRestTemplate/RANDOM_PORT anywhere in the test tree.

### 3. ddl-auto=none deviation acceptance

**Test:** Confirm the deviation from locked D-P2-02 (`validate`) is acceptable as deferred debt.
**Expected:** Acknowledge that `validate` surfaced real pre-existing entity/schema drift (e.g. api_param varchar(255) vs DB mediumtext) across 239 entities; the source project ran implicit `none` against the same DB; fixing @Column annotations is a 259-table change explicitly out of Phase 2 scope (CONTEXT.md deferred). Drift becomes Phase 4+ debt.
**Why human:** Trade-off between the locked startup-verification decision and the phase boundary. Current behavior matches source runtime so infra boots.

### Gaps Summary

No code-level gaps. All 14 requirements are MET at the compile/wiring/artifact level (INFRA-02 is PARTIAL only in its HTTP-proof sub-claim, which is a human-decision item, not missing implementation). The phase goal — a bootable infrastructure layer — is delivered in the code: spt-tools inlined, dual-ORM @Primary DataSource wired, external beans injected, nacos gone, Feign mechanism established, config consolidated.

The single behavioral gate (D-P2-03 capstone context-load) is credential-locked after the CR-01 security fix: it fails in a clean environment only because the real `DB_PASSWORD` is correctly absent from the repo. A dummy-password run isolates the failure to authentication (`Access denied`), proving the wiring itself is correct. Final behavioral sign-off therefore requires the human to re-run the capstone with the real credential, plus decide on WR-02 (HTTP-mapping proof) and the ddl-auto deferral. None of these are implementation gaps; they are verification-environment and scope-boundary decisions.

---

_Verified: 2026-07-16T18:58:00Z_
_Verifier: Claude (gsd-verifier)_

## Resolution (2026-07-16 — orchestrator, post human decision)

All three `human_verification` items resolved; status flipped `human_needed → passed`.

| Item | Resolution | Tracking |
|------|-----------|----------|
| 1. Re-run capstone with real DB password | **DONE** — orchestrator ran `mvn -pl zgbas-admin -am test -Dtest=ZgbasApplicationTest,InProcessContractTest -DfailIfNoTests=false` with `DB_PASSWORD`/`SPT_APP_SECRET` from env → **BUILD SUCCESS, Tests run: 6, Failures: 0, Errors: 0**. The 6/6 GREEN claim is reproduced. | — |
| 2. WR-02 (HTTP-mapping proof) | **Accepted as-is; deferred to Phase 4.** User chose to add MockMvc HTTP coverage when real Phase-4 `@RestController implements I*Client` impls land (more meaningful then). | `.planning/todos/pending/phase4-inprocess-contract-http-proof.md` |
| 3. ddl-auto=none deviation | **Accepted; drift deferred to Phase 4.** User confirmed the ~259-table schema fix is out of Phase-2 scope; `none` matches source behavior; re-enable `validate` after drift reconciliation. | `.planning/todos/pending/phase4-resolve-entity-schema-drift.md` |

Additionally, **CR-01 (plaintext prod secrets)** was fixed before verification: `application-dev.yml` now uses `${DB_PASSWORD}`/`${SPT_APP_SECRET}` (no secret in git as of HEAD), `.gitignore` covers `application-local*`. The leaked **values still require rotation** — an outward-facing deployment action tracked in `.planning/todos/pending/rotate-leaked-prod-credentials.md`.

**Verdict:** Phase 2 delivers a bootable infrastructure layer (14/14 requirement IDs MET). The capstone test is GREEN with credentials present. No code-level gaps. Phase marked **passed**.
