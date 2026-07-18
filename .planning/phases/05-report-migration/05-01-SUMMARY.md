---
phase: 05-report-migration
plan: 01
subsystem: report-wiring
tags: [wiring, reportClient-inline, path-prefix, mapper-scan, feign-self-loopback, wr05-scaffold]
requirements: [REPORT-02, PERSIST-02]
requires:
  - "Phase 4 04-05: @EnableFeignClients widened to com.spt.bas.report.client.remote"
  - "Phase 4 04-05: application-dev.yml + application-prod.yml carried spt.bas.report.url placeholder (dev value corrected in this plan)"
  - "Phase 2 D-P2-04: ZgbasMybatisConfig @MapperScan for SampleMapper (broadened here)"
  - "Phase 4 D-P4-01a: BasFeignPathConfig verbatim template for ReportFeignPathConfig"
provides:
  - "265 reportClient java inlined under zgbas-system/src/main/java/com/spt/bas/report/client/ (entity 83 + vo 119 + remote 54 + config 1 + constant 2 + payload 3 + utils 2 + pkg-info) — Wave 1-4 Mapper/XML/service type refs now resolve at compile"
  - "ReportFeignPathConfig WebMvcConfigurer adds /spt-bas-report prefix to com.spt.bas.report.server.api @RestController paths — 14 BFF path collisions pre-empted, Feign contract path diverge resolved"
  - "ZgbasMybatisConfig @MapperScan broadened to cover com.spt.bas.report.server.dao — Wave 1-4 Rpt*Mapper bean registration unblocked"
  - "mybatis-plus.type-aliases-package includes com.spt.bas.report.client.{entity,vo} — Wave 1-4 XML resultType / parameterType FQN resolution"
  - "report-client:2.0.1-SNAPSHOT jar dep removed from zgbas-system pom — classpath double-definition closed"
  - "reportFeignSelfLoopbackWiring_probe fail-fast gate in ZgbasApplicationTest"
affects:
  - "zgbas-system/pom.xml (removed report-client, added easyexcel+cglib cascade deps)"
  - "zgbas-framework/.../ZgbasMybatisConfig.java"
  - "zgbas-admin/.../application.yml"
  - "zgbas-admin/.../application-dev.yml + application-prod.yml (Rule 1 fix: report url key path)"
  - "zgbas-admin/.../ZgbasApplicationTest.java"
tech-stack:
  added:
    - "com.alibaba:easyexcel:3.1.0 (Rule 3 cascade from source reportClient/pom.xml; 3 files import com.alibaba.excel.*)"
    - "cglib:cglib:3.1 (Rule 3 cascade; was transitive via removed report-client jar's spt-bas-client dep; Phase 4 ApplyContractAdjustServiceImpl imports net.sf.cglib.beans.BeanMap)"
  patterns:
    - "verbatim clone of Phase 4 BasFeignPathConfig (D-P4-01a) — path-prefix WebMvcConfigurer pattern reused for report api package"
    - "D-P2-07 照搬保包名 — 265 java recursive cp from source reportClient tree"
    - "Rule 3 cascade pom — same pattern as Phase 4 04-04 verbatim source pom deps"
key-files:
  created:
    - "zgbas-system/src/main/java/com/spt/bas/report/client/ (265 files)"
    - "zgbas-system/src/main/java/com/spt/bas/client/config/ReportFeignPathConfig.java"
  modified:
    - "zgbas-system/pom.xml"
    - "zgbas-framework/src/main/java/com/spt/framework/config/ZgbasMybatisConfig.java"
    - "zgbas-admin/src/main/resources/application.yml"
    - "zgbas-admin/src/main/resources/application-dev.yml"
    - "zgbas-admin/src/main/resources/application-prod.yml"
    - "zgbas-admin/src/test/java/com/spt/ZgbasApplicationTest.java"
decisions:
  - "D-P5-02 closed: report-client jar replaced by 265 inlined java (对齐 Phase 4 basClient pattern)"
  - "D-P5-03 path-prefix established: ReportFeignPathConfig clone of BasFeignPathConfig, single basePackage com.spt.bas.report.server.api"
  - "D-P5-07 mybatis config merged: @MapperScan broadened to 2-element array, type-aliases-package appended with 2 report packages"
metrics:
  duration: "11 min"
  completed: "2026-07-17T08:49:35Z"
  tasks: 2
  files_created: 266
  files_modified: 6
---

# Phase 5 Plan 01: Report Wiring (Wave 0) Summary

Wave 0 scaffolding for the 6-wave Phase 5 report migration — 265 reportClient java inlined into zgbas-system, ReportFeignPathConfig cloned verbatim from Phase 4 BasFeignPathConfig, @MapperScan broadened for the 53 incoming report Mappers, mybatis type-aliases appended, report-client jar removed (classpath double-definition closed), and a fail-fast probe proving the report Feign self-loopback wiring works end-to-end. Plus two Rule 3 cascade pom deps (easyexcel, cglib) and a Rule 1 fix for a latent Phase 4 YAML bug exposed by the probe.

## What Was Built

### Task 1 — Five atomic wiring operations

1. **reportClient 265 java inline (D-P2-07 照搬保包名)** — recursive copy of `/Users/alan/WorkSpace/IDEA/zgbas/basReport/reportClient/src/main/java/com/spt/bas/report/client/` into `zgbas-system/src/main/java/com/spt/bas/report/client/`, zero rename, zero import edit. Breakdown: entity 83 + vo 119 + remote 54 (`@FeignClient` contracts, including 1 `package-info.java`) + config 1 (`ReportClientConfig` producing the `reportServerConfig` bean) + constant 2 + payload 3 + utils 2.
2. **pom.xml report-client dep removed (D-P5-02 closeout, Pitfall 7)** — the types-only `com.spt.bas:report-client:2.0.1-SNAPSHOT` jar block deleted. `mvn dependency:tree` confirms zero report-client residue — classpath no longer has two copies of `com.spt.bas.report.client.*`.
3. **ReportFeignPathConfig (D-P5-03, Pitfall 1 + 2 defense)** — new `@Configuration implements WebMvcConfigurer` at `zgbas-system/src/main/java/com/spt/bas/client/config/ReportFeignPathConfig.java`. Verbatim clone of Phase 4 `BasFeignPathConfig` with two constants changed: `API_PATH_PREFIX = "/spt-bas-report"` and `HandlerTypePredicate.forBasePackage("com.spt.bas.report.server.api")` (single package, not the dual basServer/pm scan of the bas variant). Adds the `/spt-bas-report` prefix to all 54 (future Wave 5) api controllers so that (a) the 14 BFF path collisions are pre-empted and (b) the `IRpt*Client` Feign contracts' `path = "spt-bas-report" + "/rpt/..."` resolves directly to the ported api (no path-stripper needed).
4. **@MapperScan broadened (D-P5-07 part 2, Pitfall 3 defense)** — `ZgbasMybatisConfig.java` `@MapperScan` changed from single string `basePackages = "com.spt.bas.system.dao"` to two-element array `{ "com.spt.bas.system.dao", "com.spt.bas.report.server.dao" }`. annotationClass `MyBatisDao.class` unchanged (53 report Mappers all carry this marker, verified in research). Wave 1-4 Rpt*Mapper bean registration unblocked.
5. **type-aliases-package appended (D-P5-07 part 1)** — `application.yml` mybatis-plus `type-aliases-package` changed from single string to YAML folded block scalar with three entries: `com.spt.bas.client.entity, com.spt.bas.report.client.entity, com.spt.bas.report.client.vo`. mapper-locations unchanged (53 report XML will drop alongside SampleMapper.xml in Waves 1-4 and be picked up by the existing glob).

### Task 2 — W0 fail-fast probe + Rule 1 yml bug fix

- **reportFeignSelfLoopbackWiring_probe** added to `ZgbasApplicationTest` (NOT `@Disabled` — runs Wave 0). Three assertions: (1) `ReportFeignPathConfig` bean registered; (2) `IRptFundReceivableStatisticsClient` Feign proxy resolves; (3) `reportServerConfig.getUrl()` contains `"localhost:8080"`.
- **Rule 1 latent Phase 4 bug fixed** — first run of the probe surfaced that `reportServerConfig.getUrl()` returned `null`. Root cause: Phase 4 04-05 placed the `report:` block at the same YAML indent as `bas:` (sibling under `spt:`), so Spring flattened to `spt.report.url` — but `ReportConstant.SERVER_URL_KEY` is `"spt.bas.report.url"`. Phase 4 D-P4-02 lazy-degradation meant nothing actually called `reportServerConfig.getUrl()` at runtime, so the mismatch stayed latent. Moved `report:` UNDER `bas:` in both `application-dev.yml` and `application-prod.yml` so the flattened key now matches the constant.

## Deviations from Plan

### Auto-fixed Issues

**1. [Rule 3 - Blocking] Added easyexcel 3.1.0 cascade dep**
- **Found during:** Task 1 compile gate (79 errors → 15 after first pass)
- **Issue:** 3 inlined reportClient files (`entity/RptInvoiceDetailExcel`, `entity/RptInvoiceInfoExcel`, `utils/DateUtils`) import `com.alibaba.excel.{annotation.write.style, enums.poi}.*`. The source `basReport/reportClient/pom.xml` declared `com.alibaba:easyexcel:3.1.0`; the types-only `report-client` jar previously brought it in transitively. After removing report-client, the dep went missing.
- **Fix:** Added `com.alibaba:easyexcel:3.1.0` to `zgbas-system/pom.xml` verbatim from source reportClient/pom.xml (Phase 4 04-04 Rule 3 cascade pom pattern).
- **Files modified:** `zgbas-system/pom.xml`
- **Commit:** 619e3f6

**2. [Rule 3 - Blocking] Added cglib 3.1 cascade dep**
- **Found during:** Task 1 compile gate (15 errors after easyexcel fix)
- **Issue:** Phase 4 basServer file `ApplyContractAdjustServiceImpl.java:30` imports `net.sf.cglib.beans.BeanMap`. cglib was previously transitive via the removed `report-client` jar's `spt-bas-client` dependency. Source zgbas has no explicit cglib declaration anywhere (verified via `grep -rn 'cglib' /Users/alan/WorkSpace/IDEA/zgbas --include='pom.xml'`).
- **Fix:** Added `cglib:cglib:3.1` to `zgbas-system/pom.xml` (only version available in local repo, matches the legacy non-Spring-repackaged cglib API the source code uses).
- **Files modified:** `zgbas-system/pom.xml`
- **Commit:** 619e3f6

**3. [Rule 1 - Bug] Fixed Phase 4 latent YAML key path for spt.bas.report.url**
- **Found during:** Task 2 first test run (probe failed at assertion 3 — `getUrl()` returned null)
- **Issue:** Phase 4 04-05 placed the `report:` block at sibling indent of `bas:` under `spt:`, so Spring flattened to `spt.report.url`. But `ReportConstant.SERVER_URL_KEY = "spt.bas.report.url"` (the constant ReportClientConfig passes to `setUrlKey`). D-P4-02 lazy-degradation meant no runtime caller ever invoked `reportServerConfig.getUrl()`, so the mismatch stayed latent through Phase 4 completion.
- **Fix:** Moved `report:` UNDER `bas:` in both `application-dev.yml` and `application-prod.yml` so the flattened key matches the constant. The new `reportFeignSelfLoopbackWiring_probe` assertion (3) forces this code path, exposing the bug.
- **Files modified:** `zgbas-admin/src/main/resources/application-dev.yml`, `zgbas-admin/src/main/resources/application-prod.yml`
- **Commit:** 9d7cf1d

**4. [Rule 3 - Blocking] Added -DfailIfNoTests=false to test command**
- **Found during:** Task 2 first test run (`No tests were executed!` on zgbas-common)
- **Issue:** `mvn test -Dtest=ZgbasApplicationTest` applies the test filter to ALL upstream reactor modules. zgbas-common/framework/system have no test matching that name → surefire fails the build before reaching zgbas-admin.
- **Fix:** Added `-DfailIfNoTests=false` to the test command. Same pattern Phase 4 used.
- **Files modified:** none (invocation-only)

## Verification

| Gate | Command | Result |
|------|---------|--------|
| Multi-module compile | `mvn -am compile` (JDK 1.8.0_482) | BUILD SUCCESS, 0 `[ERROR]`, 0 cannot-find-symbol |
| Test gate | `mvn -pl zgbas-admin -am test -Dtest=ZgbasApplicationTest -DfailIfNoTests=false` | 21 tests, 0 failures, 0 errors, 0 skipped |
| dependency:tree | `mvn -pl zgbas-system -o dependency:tree` | 0 report-client residue; easyexcel 3.1.0 + cglib 3.1 present |
| reportClient inline count | `find zgbas-system/src/main/java/com/spt/bas/report/client -name '*.java' \| wc -l` | 265 (matches plan target) |
| @FeignClient count | `grep -rl '@FeignClient' zgbas-system/src/main/java/com/spt/bas/report/client/remote \| wc -l` | 54 (matches plan target; 55th file is package-info.java) |
| ReportFeignPathConfig bean | `context.getBean(ReportFeignPathConfig.class)` in probe | not null |
| IRptFundReceivableStatisticsClient proxy | `context.getBean(IRptFundReceivableStatisticsClient.class)` in probe | not null |
| reportServerConfig URL | `reportServerConfig.getUrl()` in probe | contains `"localhost:8080"` |

### Non-hermetic test note (D-P5-08)

The probe runs against the real dev DB (`sptbasdb_pd` at 47.104.15.98:3306) per the Phase 4 plaintext-secrets decision — `application-dev.yml` keeps plaintext secrets so no `DB_PASSWORD` / `SPT_APP_SECRET` exports are needed for `mvn test`. Same contract as Phase 3 / Phase 4.

## Decisions Made

1. **Easyexcel + cglib cascade deps added verbatim (not substituted)** — per Rule 3 cascade pom pattern established in Phase 4 04-04. Source reportClient/pom.xml verbatim for easyexcel (3.1.0); cglib 3.1 is the only version in local repo and matches the legacy API surface Phase 4 depends on.
2. **Phase 4 latent yml bug fixed by nesting, not by changing the constant** — `ReportConstant.SERVER_URL_KEY` is source code (`"spt.bas.report.url"`), immutable per D-P2-07 照搬保包名. Restructuring the yml is the only correct fix.
3. **Probe runs Wave 0 (no `@Disabled`)** — per plan; fail-fast is the entire point of the probe. Latent bugs caught now (Rule 1 yml fix) vs. discovered late in Wave 5/6 when the api controllers land.

## Known Stubs

None. This plan is pure wiring — no business logic, no data flow to UI. The 9 basServer service impls + multiple BFF controllers that `@Autowired IRpt*Client` (Phase 4 D-P4-02 lazy-degradation) will become real calls once Phase 5 Wave 5 ports the api controllers; today they still 404 at runtime (no api impl exists yet). That deferred behavior is tracked as the Wave 6 stub-port upgrade (D-P5-03 closure), not a stub introduced by this plan.

## Threat Flags

None. The threat register in `05-01-PLAN.md <threat_model>` covers all 4 entries (T-05-01-S spoofing, T-05-01-T tampering, T-05-01-I info disclosure, T-05-05-SC Maven installs) — all `accept` disposition. No new threat surface introduced beyond what the plan anticipated. The easyexcel + cglib cascade deps are well-known artifacts (Alibaba's Excel library, the legacy CGLIB code generation library) and were already on the runtime classpath transitively via the removed report-client jar — adding them explicitly changes the dependency declaration, not the runtime surface.

## Self-Check: PENDING

(Self-check will be appended after SUMMARY write completes — see below.)
