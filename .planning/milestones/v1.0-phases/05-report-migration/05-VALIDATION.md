---
phase: 5
slug: report-migration
status: draft
nyquist_compliant: false
wave_0_complete: false
created: 2026-07-17
---

# Phase 5 — Validation Strategy

> Per-phase validation contract for feedback sampling during execution.
> Derived from `05-RESEARCH.md` §Validation Architecture (HIGH confidence).

---

## Test Infrastructure

| Property | Value |
|----------|-------|
| **Framework** | JUnit 4 + Spring Boot Test 2.5.9 (Phase 2/3/4 baseline) |
| **Config file** | `zgbas-admin/src/test/java/com/spt/ZgbasApplicationTest.java` (Phase 4 WR-02 scaffold, has `@Disabled` placeholder + `contextLoads`) |
| **Quick run command** | `JAVA_HOME=/Users/alan/Library/Java/JavaVirtualMachines/corretto-1.8.0_482/Contents/Home /Users/alan/App/apache-maven-3.8.6/bin/mvn -s /Users/alan/App/apache-maven-3.8.6/zg_settings.xml -pl zgbas-admin -am test -Dtest=ZgbasApplicationTest` |
| **Full suite command** | same (single test class) |
| **Estimated runtime** | ~60–90 seconds (Spring context boot) |

> Non-hermetic (D-P5-08 / D-P3-13): real DB `sptbasdb_pd`. Per D-P4-plaintext-secrets, dev profile has plaintext secrets → no `export DB_PASSWORD`/`SPT_APP_SECRET` needed.

---

## Sampling Rate

- **After every task commit:** `JAVA_HOME=<Corretto 1.8> /Users/alan/App/apache-maven-3.8.6/bin/mvn -s <zg_settings> -pl <module> -am compile -q` then grep `^\[ERROR\]` (locale-irrelevant) = 0
- **After every plan wave:** full multi-module `mvn compile` + `ZgbasApplicationTest` (incl. Wave 0 `reportFeignSelfLoopbackWiring_probe`) GREEN
- **Before `/gsd:verify-work`:** Wave 6 sampling proof — startup → 2–3 `findPage` queries assert non-empty → 1 BFF HTTP proof (triggers `IRpt*Client` chain) → all green
- **Max feedback latency:** ~90 seconds (context boot)

---

## Per-Task Verification Map

> Task IDs finalized by planner; requirement → test mapping fixed here (from RESEARCH §Validation Architecture).

| Task ID | Plan | Wave | Requirement | Threat Ref | Secure Behavior | Test Type | Automated Command | File Exists | Status |
|---------|------|------|-------------|------------|-----------------|-----------|-------------------|-------------|--------|
| 05-00-xx | 00 | 0 | REPORT-02 (D-P5-03) | — | report Feign self-loopback wired (9 IRpt*Client beans + url→localhost:8080) | smoke (startup) | `ZgbasApplicationTest.reportFeignSelfLoopbackWiring_probe` | ❌ W0 new | ⬜ pending |
| 05-0N-xx | 01–04 | 1–4 | REPORT-01 + PERSIST-02 | — | 53 Mapper beans resolve, no BindingException, namespace correct | smoke (startup) | `mvn compile` grep `^\[ERROR\]`=0 (per wave) | ✅ infra | ⬜ pending |
| 05-05-xx | 05 | 5 | REPORT-01 | — | 53 report mapper namespaces registered in SqlSessionFactory | smoke | `ZgbasApplicationTest.allReportMappersResolve` | ❌ W5 new | ⬜ pending |
| 05-05-xx | 05 | 5 | REPORT-02 (D-P5-03) | T-5-01 | path-prefix applied: `/spt-bas-report/rpt/...` maps to report api | smoke | `ZgbasApplicationTest.reportApiPathPrefixWiring_probe` | ❌ W5 new | ⬜ pending |
| 05-06-xx | 06 | 6 | PERSIST-02 | — | sample report `findPage` returns non-empty + correct `Page` shape | integration (real DB) | `ZgbasApplicationTest.sampleReportQuery_proof` (`@Disabled` default) | ❌ W6 new | ⬜ pending |
| 05-06-xx | 06 | 6 | REPORT-02 (D-P5-03) | — | BFF→Feign self-loopback→report api full chain 200 | integration (HTTP proof) | `POST /business/overview/api/findBusinessOverviewList` assert 200 | ❌ W6 new | ⬜ pending |

*Status: ⬜ pending · ✅ green · ❌ red · ⚠️ flaky*

---

## Wave 0 Requirements

- [ ] `zgbas-admin/src/test/java/com/spt/ZgbasApplicationTest.java` — add `reportFeignSelfLoopbackWiring_probe` (D-P5-03 fail-fast: 9 `IRpt*Client` beans registered + url resolves to localhost:8080; mirrors Phase 4 WR-02 same-name probe)

*Existing test infrastructure (Phase 2/3/4) covers the runtime stack; only report-specific assertions are appended in W5/W6.*

---

## Sampling Strategy (D-P5-08 sampling selection)

2–3 **representative** reports for `findPage` proof, covering different mapper families + resultType paths:

1. **`RptFundReceivableStatisticsMapper.findPage`** — `resultType=...report.client.entity.RptFundReceivableStatistics` (simple entity, single-table) → proves typeAliases + namespace + DataSource wiring
2. **`RptCtrContractReportMapper.findRptContractPage`** — cross-service dep (`IRptSummaryRoiService` + `IRptUserRoiService`), complex multi-join → proves wave sequencing + complex report SQL executable
3. **`RptBusinessOverviewMapper`** (any method) — result feeds BFF `BusinessOverviewController` (triggers `IRptBusinessOverviewClient`) → proves D-P5-03 self-loopback closure

---

## Manual-Only Verifications

| Behavior | Requirement | Why Manual | Test Instructions |
|----------|-------------|------------|-------------------|
| Sample report `findPage` returns non-empty + Page shape | PERSIST-02 | non-hermetic, needs real DB `sptbasdb_pd` + seeded report data | Activate `@Disabled` `sampleReportQuery_proof`, run against dev DB, assert `Page<>.getResult()` non-empty for the 3 sample mappers above |
| BFF→Feign→report api HTTP 200 | REPORT-02 (D-P5-03) | needs running server + real DB | `POST /spt-bas-report/rpt/fundReceivableStatistics/findPage` assert 200 + Page shape; `POST /business/overview/api/findBusinessOverviewList` assert 200 (full self-loopback chain) |

---

## Validation Sign-Off

- [ ] All tasks have `<automated>` verify or Wave 0 dependencies
- [ ] Sampling continuity: no 3 consecutive tasks without automated verify
- [ ] Wave 0 covers all MISSING references (`reportFeignSelfLoopbackWiring_probe`)
- [ ] No watch-mode flags
- [ ] Feedback latency < 90s
- [ ] `nyquist_compliant: true` set in frontmatter

**Approval:** pending
