---
phase: 02
slug: infrastructure
status: approved
nyquist_compliant: true
wave_0_complete: false
created: 2026-07-16
---

# Phase 2 — Validation Strategy

> Per-phase validation contract for feedback sampling during execution.
> **Strategy: startup-verification-primary (D-P2-03).** No query-correctness tests in Phase 2 — one `@SpringBootTest` context-load test + one trivial in-process contract test = full coverage. Query correctness deferred to Phase 4.

> **JDK8 build invariant:** every `mvn` command runs on Corretto 1.8.0_482 (machine default is JDK 21). Prefix all commands with `JAVA_HOME=<local Corretto 1.8 path>` and `-s /Users/alan/App/apache-maven-3.8.6/zg_settings.xml`. Locale is zh_CN → compile-error grep must be locale-independent (`^\[ERROR\]`).

---

## Test Infrastructure

| Property | Value |
|----------|-------|
| **Framework** | JUnit 5 (Jupiter) via `spring-boot-starter-test` (Phase 1 D-06, already in zgbas-admin) |
| **Config file** | none — inherits `spring-boot-starter-test` defaults |
| **Quick run command** | `JAVA_HOME=<corretto-1.8> mvn -s zg_settings.xml -pl zgbas-admin -am test -Dtest=ZgbasApplicationTest` |
| **Full suite command** | `JAVA_HOME=<corretto-1.8> mvn -s zg_settings.xml test` |
| **Estimated runtime** | ~30–60s (context-load on JDK8 + 250 entities validate) |

---

## Sampling Rate

- **After every task commit:** `mvn -pl <affected-module> -am compile` (wave-gated; <30s after Wave 0)
- **After every plan wave:** `mvn -pl zgbas-admin -am test -Dtest=ZgbasApplicationTest` (full context-load; ~30–60s)
- **Before `/gsd:verify-work`:** `mvn clean test` from root green + manual startup-log checks below
- **Max feedback latency:** 60s

---

## Per-Task Verification Map

| Task ID | Plan | Wave | Requirement | Threat Ref | Secure Behavior | Test Type | Automated Command | File Exists | Status |
|---------|------|------|-------------|------------|-----------------|-----------|-------------------|-------------|--------|
| spt-tools layer inline | 01 | 1 | INLINE-01..04 | — | N/A | compile (wave-gated) | `mvn -pl zgbas-common -am compile` after each layer | ✅ W0 (Phase 1 baseline) | ⬜ pending |
| entities+Dao bulk-copy | 02 | 2 | PERSIST-01 | — | N/A | compile | `mvn -pl zgbas-system -am compile` | ❌ W0 (not yet copied) | ⬜ pending |
| dual-ORM DataSource wiring | 03 | 2 | PERSIST-03 | — | single Druid DataSource, no JTA | startup (context-load) | `mvn -pl zgbas-admin -am test -Dtest=ZgbasApplicationTest` | ✅ W0 (stub — extend) | ⬜ pending |
| audit @EntityListeners | 03 | 2 | PERSIST-04 | — | createdDate/updatedDate preserved | startup (validate catches drift) | same as PERSIST-03 | ✅ W0 | ⬜ pending |
| external SDK @Beans + cfca | 04 | 3 | EXT-01..03 | T-P2-log / T-P2-secret | beans registered, no secret logging | startup (context-load) | same as PERSIST-03 | ✅ W0 | ⬜ pending |
| external URL keys resolvable | 04 | 3 | EXT-04 | T-P2-secret | env-var placeholders resolve | startup | same as PERSIST-03 | ✅ W0 | ⬜ pending |
| nacos removed | 05 | 3 | INFRA-01 | — | nacos absent from classpath | dep:tree | `mvn -pl zgbas-admin dependency:tree -Dincludes=com.alibaba.cloud:spring-cloud-starter-alibaba-nacos-discovery` → empty | ❌ W0 (new check) | ⬜ pending |
| Feign in-process proof | 05 | 3 | INFRA-02 | — | interface-as-contract works in-process | unit (Spring) | `mvn -pl zgbas-admin -am test -Dtest=InProcessContractTest` | ❌ W0 (new test) | ⬜ pending |
| config consolidation | 05 | 3 | INFRA-04 | T-P2-secret | secrets as env-var placeholders | file existence | `ls zgbas-admin/src/main/resources/application{,-dev,-prod}.yml` | ❌ W0 (Phase 1 has only application.yml) | ⬜ pending |

*Status: ⬜ pending · ✅ green · ❌ red · ⚠️ flaky*

---

## Wave 0 Requirements

- [ ] `zgbas-admin/src/test/java/com/spt/proof/InProcessContractTest.java` — covers INFRA-02 (interface-as-contract in-process call)
- [ ] Extend `zgbas-admin/src/test/java/com/spt/ZgbasApplicationTest.java` with assertions: `primaryDataSourceIsPresent`, `jpaTransactionManagerIsPrimary`, `sampleMapperBeanRegistered`, `externalSdkBeansRegistered` — covers PERSIST-03/04 + EXT-01..03
- [ ] `zgbas-common/pom.xml` — declare every pinned 3rd-party dep (Hutool 5.5.9 / fastjson 1.2.75 / Druid 1.2.8 / Shiro 1.8.0 / POI 4.1.2 / mybatis-plus 3.1.2 / mysql-connector 8.0.13 / commons-text 1.1)
- [ ] `zgbas-framework/pom.xml` — declare auth-sdk / spt-push-sdk / spt-file-sdk + spring-data-jpa + mybatis-plus
- [ ] `zgbas-admin/pom.xml` — declare spt-sign-client
- [ ] `zgbas-system/src/main/java/.../SampleMapper.java` + `resources/mybatis/mappers/SampleMapper.xml` — trivial sample Mapper (query `t_api_external_his` count; fallback `information_schema.tables`)
- [ ] `zgbas-admin/src/main/resources/application-dev.yml` + `application-prod.yml` — new profile configs

*Phase 1 baseline (spring-boot-starter-test + ZgbasApplicationTest.contextLoads) already covers the Wave-0 test infra. The above are Phase 2 additions.*

---

## Manual-Only Verifications

| Behavior | Requirement | Why Manual | Test Instructions |
|----------|-------------|------------|-------------------|
| Startup log shows Flyway disabled + Druid (not HikariCP) + no nacos | PERSIST-03 / INFRA-01 | log-grep on a real boot, not a unit assertion | `mvn spring-boot:run`, grep log: contains "Flyway is disabled" / "Druid"; NO "HikariCP"; NO "nacos" |
| dependency:tree clean of nacos + xxl-job | INFRA-01 / INFRA-03(partial) | reactive dependency audit | `mvn -pl zgbas-admin dependency:tree` — no `nacos-*`, no `xxl-job-*` |

---

## Validation Sign-Off

- [x] All tasks have `<automated>` verify or Wave 0 dependencies
- [x] Sampling continuity: no 3 consecutive tasks without automated verify
- [x] Wave 0 covers all MISSING references (Wave-0 artifacts created within plans 02-01/05/06)
- [x] No watch-mode flags
- [x] Feedback latency < 60s
- [x] `nyquist_compliant: true` set in frontmatter

> Strategy-level sign-off confirmed by gsd-plan-checker (0 blockers). `wave_0_complete` stays false until execute-phase runs the Wave-0 tasks.

**Approval:** approved 2026-07-16
