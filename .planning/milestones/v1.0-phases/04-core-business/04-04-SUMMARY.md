---
phase: 04-core-business
plan: 04
subsystem: service+PM (merged compile unit)
tags: [bulk-copy, wave-2b, service, impl, domain-subpkg, pm-absorption, cascade-pom, merged-compile-unit]

requires:
  - "04-01 BasClientConfig basServerConfig bean + widened @EnableFeignClients + BasFeignPathConfig + rocketmq starter (Wave 0)"
  - "04-02 238 basClient @FeignClient contracts + 14 data carriers (Wave 1) — service @Autowired I*Client type resolution"
  - "04-03 Wave 2a infra (cache/util/enums/annotation/filter/listener/command/event/rocketmq/config, commit 3c7cc52) — structural gate deferred to this plan per Decision A"
  - "Phase 2 inlined entity(239)/dao(229)/vo(293)/constant(20)/cache(3) + pmClient(pm.entity/pm.vo/pm.inter) + spt-tools BaseDataService/BaseApi/BaseClient/TokenUtil"
provides:
  - "basServer service layer: 241 top-level I*Service interfaces + 248 service/impl + 44 domain subpkg files (ctr 9 / logistics 6 / performance 4 / rt 1 / stock 24) = 533 files, package com.spt.bas.server.{service[.impl],ctr.service,logistics.service,performance.service,rt,stock.service} verbatim"
  - "PM module absorbed (Decision A): pm/annotation 1 + pm/cache 2 + pm/dao 14 + pm/service 30 (15 iface + 15 impl) + pm/util 4 = 51 files, package com.spt.pm.* verbatim"
  - "pm/constant/PmConstants.java inlined (Phase 2 pmClient oversight completion)"
  - "Cascade pom deps verbatim from source basServer/basCore pom: pdfbox 2.0.29, xxl-job-core 2.3.0, report-client 2.0.1-SNAPSHOT, purchase-client 2.0.1-SNAPSHOT, spring-cloud-alibaba-commons 2021.0.1.0, nacos-common 1.4.2 (plus pre-existing QLExpress/httpmime/ik-expression from 04-03)"
  - "TokenUtil.createToken(Map, String) overload added (Rule 1 bug fix — fixes 3 source-side callers across basServer + web + basWx)"
  - "BIZ-01 service-layer compile complete: mvn -pl zgbas-system -am compile BUILD SUCCESS, 0 errors"
  - "MERGED COMPILE GATE GREEN (closes both 04-04 AND 04-03 deferred structural gate)"
affects:
  - "04-05 (Wave 3 api) — service beans available for @RestController extends BaseApi endpoints to delegate to"
  - "04-06 (Wave 4 BFF) — service layer in place; BFF will still go through Feign self-loopback per D-P4-01 方案 A"
  - "Phase 6 (quartz) — command/BasCommandExecutor (has @XxlJob) + 4 referenced task classes deferred here alongside basServer/task/23"
  - "Phase 5 (report) — report-client jar dep provides types; ReportServer migration will inline source"
  - "v2 (basWx) — purchase-client jar dep provides types; basWx migration will inline source"
tech-stack:
  added:
    - "org.apache.pdfbox:pdfbox:2.0.29 (basServer/pom.xml verbatim)"
    - "com.xuxueli:xxl-job-core:2.3.0 (basServer/pom.xml verbatim — compile-time jar only, scheduling infra excluded)"
    - "com.spt.bas:report-client:2.0.1-SNAPSHOT (types-only, Phase 5 defer)"
    - "com.spt.bas:purchase-client:2.0.1-SNAPSHOT (types-only, v2 defer)"
    - "com.alibaba.cloud:spring-cloud-alibaba-commons:2021.0.1.0 (util classes only, NO discovery autoconfig)"
    - "com.alibaba.nacos:nacos-common:1.4.2 (util classes only, NO discovery)"
  patterns:
    - "Merged compile unit (Decision A): infra+service+PM as single unit, gate runs once at unit completion"
    - "Gotcha cascade patience (Phase 1/2 precedent): 320 → 268 → 20 → 1 → 0 via iterative completion not source-logic edit"
    - "@XxlJob handler rule (04-03 established): command/BasCommandExecutor has @XxlJob → correctly deferred to Phase 6"
    - "Source-bug Rule 1 auto-fix (照搬保真 preserved): add missing TokenUtil overload as 1-line delegate; LocalDateTime.now() for ErrorResp.setTimestamp"
key-files:
  created:
    - path: zgbas-system/src/main/java/com/spt/bas/server/service/**
      purpose: 241 top-level I*Service interfaces + 6 concrete services (BsCompanyBalanceHistoryService, ApplyChargeSalesService, LogisticsCompanyConfigService, BsCompanyFeeConfigService, LogisticsCompanyDetailService) + package-info
    - path: zgbas-system/src/main/java/com/spt/bas/server/service/impl/**
      purpose: 248 @Service implementations (CRUD + business logic)
    - path: zgbas-system/src/main/java/com/spt/bas/server/{ctr,logistics,performance,rt,stock}/**
      purpose: 44 domain subpkg files (合同 ctr 9 + 物流 logistics 6 + 业绩 performance 4 + 融拓 rt 1 + 库存 stock 24)
    - path: zgbas-system/src/main/java/com/spt/pm/{annotation,cache,dao,service,util,constant}/**
      purpose: 52 PM files (annotation 1 + cache 2 + dao 14 + service 30 + util 4 + constant 1)
  modified:
    - path: zgbas-common/src/main/java/com/spt/tools/http/util/TokenUtil.java
      change: "Added createToken(Map<String,Object>, String secretKey) overload as 1-line delegate to byte[] 3-arg form via StandardCharsets.UTF_8 (no checked exception). Phase 4 Rule 1 bug fix — 3 source callers expected this overload that was missing from spt-tools master."
    - path: zgbas-system/src/main/java/com/spt/bas/server/config/BasicErrorController.java
      change: "Line 111: new Date() → LocalDateTime.now() (ErrorResp.timestamp field is LocalDateTime — source-side type mismatch in feat-系统重构v5.0)."
    - path: zgbas-system/pom.xml
      change: "Added 6 cascade deps (pdfbox, xxl-job-core, report-client, purchase-client, spring-cloud-alibaba-commons, nacos-common) + 3 pre-cascade deps already added earlier in plan (QLExpress, httpmime, ik-expression)"
  deleted:
    - path: zgbas-system/src/main/java/com/spt/bas/server/command/BasCommandExecutor.java
      reason: "Phase 6 defer — has @XxlJob method (line 130 executeCommand) + imports 4 task/ classes (ApplyPayTask/BudgetSettlementTask/CtrContractScheduleTask/DcsxAutoApplyPayTask). Per 04-03 established rule ('@XxlJob handler files → Phase 6'). Wave 2a misclassification corrected."
    - path: zgbas-system/src/main/java/com/spt/bas/server/command/package-info.java
      reason: "Package-level doc; command/ pkg now empty, returns with BasCommandExecutor in Phase 6."
decisions:
  - "Decision A (用户 2026-07-17) honored: infra+service+PM merged compile unit; single merged gate runs at unit completion (closed both 04-03 deferred gate AND 04-04 plan gate together)"
  - "Rule 3 cascade resolution: 6 cascade jar deps added verbatim from source basServer/basCore pom. xxl-job-core kept as compile-time dep only (CtrContractProfitServiceImpl uses XxlJobHelper.log) — does NOT enable scheduling; BasJobConfig @Bean XxlJobSpringExecutor correctly stays excluded to Phase 6."
  - "Rule 3 nacos utility classes: spring-cloud-alibaba-commons + nacos-common added as inert util jars (NO @EnableDiscoveryClient, NO autoconfig). Phase 2 #9 nacos-discovery deletion stays in force — utility classes are not discovery."
  - "Rule 3 Phase 6 defer of BasCommandExecutor: 04-03 SUMMARY established '@XxlJob handler files → Phase 6' rule when deferring rocketmq/task/8. BasCommandExecutor (committed in 04-03 Wave 2a command/) has @XxlJob method → rule applies; corrected placement, deferred alongside task/ handlers + command package-info."
  - "Rule 1 source-bug auto-fix: TokenUtil.createToken(Map, String) overload missing from spt-tools master but expected by 3 source callers (mid-refactor feat-系统重构v5.0 — overload exists in spt-auth/auth-sdk/SdkTokenUtil). Added as 1-line delegate (SUBJECT_PP + byte[] 3-arg) — zero new logic, zero source-side call-site edits,照搬保真 preserved."
  - "Rule 1 source-bug auto-fix: BasicErrorController.java:111 new Date() → LocalDateTime.now() (ErrorResp.timestamp field type is LocalDateTime in both source and inlined). Source-side latent type mismatch corrected."
  - "Phase 2 oversight completion: pmClient/.../constant/PmConstants.java was missed in Phase 2 pmClient inline (only entity/vo/inter were inlined). Single-file fix here."
  - "PM api (13 files) + PM task (1 package-info) correctly excluded — api joins basServer api in Wave 4 (04-05); task to Phase 6 (xxl-job)."
patterns-established:
  - "Gotcha cascade patience under Decision A: when source module topology crosses wave boundaries, complete the merged unit before judging compile state (Phase 1/2 precedent: 320→268→20→1→0 via completion not source-logic edit)"
  - "Source-bug Rule 1 fix under 照搬保真: when source is mid-refactor and references missing API surface, prefer adding the missing overload/fix at the callee (zgbas-common) over editing multiple ported source callers — preserves source-side byte-identity"
metrics:
  duration: ~12 min
  completed: 2026-07-17
  tasks_completed: 3
  files_created: 585
  files_modified: 3
  files_deleted: 2
  compile_errors_baseline: 320
  compile_errors_final: 0
---

# Phase 4 Plan 04: Wave 2b service + impl + domain subpkgs + PM absorption (Merged Compile Unit) Summary

Wave 2b 落地 basServer 业务 Service 层 533 文件（241 顶-level I*Service 接口 + 248 impl + 5 域子包 ctr/logistics/performance/rt/stock = 44）保包名 `com.spt.bas.server.*` verbatim，并按 Decision A（用户 2026-07-17）吸收 PM 模块 51 文件（annotation/cache/dao/service/util + 补 Phase 2 遗漏的 constant），合并编译单元一次性跑绿灯——**关闭 04-04 自身 gate 与 04-03 延迟结构性 gate**。Gotcha 级联按 Phase 1/2 先例从 320→268→20→1→0 通过补齐（非源码逻辑修改）解卷。

## What Was Built

### Task 1 (commit `4c3074d`): basServer service + impl + 域子包 (533 files)

| 目标包 | 文件数 | 包名 |
|----|--------|------|
| `service/` 顶-level | 241 (235 I*Service + 6 concrete) | `com.spt.bas.server.service` |
| `service/impl/` | 248 | `com.spt.bas.server.service.impl` |
| `ctr/` (合同业务) | 9 | `com.spt.bas.server.ctr.service[.impl]` |
| `logistics/` (物流) | 6 | `com.spt.bas.server.logistics.service[.impl]` |
| `performance/` (业绩) | 4 | `com.spt.bas.server.performance.service[.impl]` |
| `rt/` (融拓对接) | 1 | `com.spt.bas.server.rt` |
| `stock/` (库存) | 24 | `com.spt.bas.server.stock.service[.impl]` |
| **合计** | **533** | — |

零源码改动（D-P2-07 verbatim）。`cp -r` 单型机械搬运，包名与源 basServer 完全一致。此 commit 单独不绿（依赖 PM + pom 补齐），按 Decision A 合并单元延迟 gate。

### Task 2 (commit `719eb9f`): PM 吸收 (51 files)

| PM 子包 | 文件数 | 处置 |
|----|--------|------|
| `pm/annotation/` | 1 (ServerTransactional) | COPY |
| `pm/cache/` | 2 (DeptCache, PmNodeCache) | COPY |
| `pm/dao/` | 14 | COPY |
| `pm/service/` | 30 (15 iface + 15 impl) | COPY |
| `pm/util/` | 4 | COPY |
| `pm/vo/` `pm/entity/` `pm/inter/` | (19+14+3 已在位) | DEDUP against Phase 2 pmClient |
| `pm/api/` | (13) | **DEFER Wave 4 (04-05)** |
| `pm/task/` | (1 package-info) | **DEFER Phase 6** |

51 net-new 文件，保包名 `com.spt.pm.*` verbatim。orchestrator 已独立验证 PM 纯下行依赖（PM → basServer {api, task, rocketmq.task, dao} 引用全 0），故吸收安全。

### Task 3 (commit `1bf0893`): Cascade resolution + 合并编译门 GREEN

按 gotcha 级联迭代，从 320 (04-03 baseline) → 268 → 20 → 1 → 0。具体修复见下 Deviations。

**Final gate:**
- `mvn -pl zgbas-system -am compile` → **BUILD SUCCESS, `^\[ERROR\]` = 0, `cannot find symbol|找不到符号` = 0**
- `mvn -pl zgbas-admin -am compile` → **BUILD SUCCESS** (no IndexController regression)
- 合并门同时关闭 04-04 plan gate 与 04-03 结构性延迟 gate

## Deviations from Plan

### Auto-fixed Issues

**1. [Rule 3 — Phase 6 placement correction] Remove command/BasCommandExecutor + package-info**
- **Found during:** Task 3 cascade gate iteration (268 errors → 20 errors)
- **Issue:** BasCommandExecutor.java (committed in 04-03 Wave 2a command/) has `@XxlJob(value = "executeCommand")` annotation (line 130) on its `executeCommand` method + imports 4 task/ classes (ApplyPayTask, BudgetSettlementTask, CtrContractScheduleTask, DcsxAutoApplyPayTask). Per the rule established in 04-03 SUMMARY ("8 个 rocketmq/task/Synchronized*Task 是纯 @XxlJob handler，与排除的 basServer/task/23 同性质，正确推迟至 Phase 6"), `@XxlJob` handler files belong in Phase 6, not Wave 2a infra.
- **Fix:** `git rm` BasCommandExecutor.java + package-info.java (command/ pkg now empty). Phase 6 will re-port the whole command + task cluster together (xxl-job → RuoYi quartz refactor). The 4 referenced task classes remain excluded as before.
- **Files deleted:** zgbas-system/.../command/BasCommandExecutor.java, zgbas-system/.../command/package-info.java
- **Commit:** `1bf0893`
- **Impact on plan:** Wave 2a command/ pkg (2 files committed in `3c7cc52`) now correctly deferred — 04-03 SUMMARY's claim "command 2 COPY" should have applied the same @XxlJob rule it established for rocketmq/task/. Memory note for Phase 6: re-port command/BasCommandExecutor + 4 referenced task classes (ApplyPayTask, BudgetSettlementTask, CtrContractScheduleTask, DcsxAutoApplyPayTask) as part of xxl-job cluster.

**2. [Rule 3 — Phase 2 inline oversight] Inline pmClient constant/PmConstants.java**
- **Found during:** Task 3 cascade gate (24 errors / 12 files missing `com.spt.pm.constant`)
- **Issue:** Phase 2 inlined pmClient entity(14)/vo(19)/inter(3) but missed `pmClient/.../constant/PmConstants.java`. Source PM service layer + 12 basServer service impls reference `com.spt.pm.constant.PmConstants`.
- **Fix:** Single-file copy `pmClient/.../constant/PmConstants.java` → `zgbas-system/.../com/spt/pm/constant/PmConstants.java` (completes Phase 2 pmClient inline).
- **Files created:** zgbas-system/.../pm/constant/PmConstants.java
- **Commit:** `1bf0893`

**3. [Rule 3 — Cascade pom deps verbatim from source] 6 jar deps added**
- **Found during:** Task 3 cascade gate (268 errors → 20 errors)
- **Issue:** Source basServer/basCore pom declared deps that were not yet in zgbas-system pom: pdfbox (PDF generation), xxl-job-core (XxlJobHelper.log contextual logging), report-client (Phase 5 report types), purchase-client (v2 basWx types), spring-cloud-alibaba-commons + nacos-common (utility classes — NOT discovery).
- **Fix:** Added 6 deps to zgbas-system/pom.xml with versions verbatim from source basServer/basCore pom (pdfbox 2.0.29, xxl-job-core 2.3.0, report-client/purchase-client 2.0.1-SNAPSHOT via `${snapshot.version}`, spring-cloud-alibaba-commons 2021.0.1.0, nacos-common 1.4.2). All jars verified present in /Users/alan/App/Repository.
- **Key constraint honored:** xxl-job-core is a compile-time jar only — does NOT enable xxl-job scheduling. Scheduling requires `@Bean XxlJobSpringExecutor` (BasJobConfig), which stays correctly excluded to Phase 6 per INFRA-03. nacos-common + spring-cloud-alibaba-commons are inert util jars — NO @EnableDiscoveryClient, NO autoconfig wiring; Phase 2 #9 nacos-discovery deletion stays in force.
- **Files modified:** zgbas-system/pom.xml
- **Commit:** `1bf0893`

**4. [Rule 1 — Source-side latent bug] TokenUtil.createToken(Map, String) overload added**
- **Found during:** Task 3 final gate (1 error: ApplyCompanyLicenseImpl line 101)
- **Issue:** 3 source files (basServer/ApplyCompanyLicenseImpl, web/FactBisController, basWx/JwtUtil) call `TokenUtil.createToken(Map<String,Object>, String)` — a 2-arg overload that doesn't exist in spt-tools master nor in the inlined zgbas-common TokenUtil. Source branch `feat-系统重构v5.0` is mid-refactor; the matching overload exists only in `spt-auth/auth-sdk/SdkTokenUtil`. Source-side latent compile bug.
- **Fix:** Added the missing overload to zgbas-common TokenUtil as a 1-line delegate to the existing 3-arg `createToken(SUBJECT_PP, map, bytes)` via `secretKey.getBytes(StandardCharsets.UTF_8)` (Charset overload → no checked UnsupportedEncodingException → no `throws` needed, matching source caller expectations). Zero new logic, zero source-side call-site edits.
- **Files modified:** zgbas-common/.../tools/http/util/TokenUtil.java
- **Commit:** `1bf0893`
- **Impact on plan:** Fixes root cause — prevents future cascade when web (Wave 4) and basWx (v2) port their respective callers. 照搬保真 preserved (no ported source files edited).

**5. [Rule 1 — Source-side latent bug] BasicErrorController Date → LocalDateTime**
- **Found during:** Task 3 cascade gate (1 error: BasicErrorController line 111)
- **Issue:** Source `err.setTimestamp(new Date())` against `ErrorResp.timestamp` field which is `LocalDateTime` in both source spt-tools and our inlined version. Source-side type mismatch.
- **Fix:** Single-token change `new Date()` → `LocalDateTime.now()` (LocalDateTime already imported at line 3). Semantically equivalent (current timestamp).
- **Files modified:** zgbas-system/.../config/BasicErrorController.java
- **Commit:** `1bf0893`

### Plan Adherence

Otherwise the plan executed exactly as written for the core service+domain copy (Task 1) and PM absorption (Task 2 — Decision A extension). No new stubs introduced (Decision A forbids). No source-side call-site edits to ported files (only TokenUtil overload addition at the callee + 1 token change in BasicErrorController for type-match). Package names preserved verbatim per D-P2-07.

## Cascade Resolution Detail (Gotcha Unravel)

Per Phase 1/2 gotcha-cascade precedent ("一处错掩一处，级联从 40→30→20→0"), the merged gate was iterated to green by completion, not source-logic rewrite:

| Stage | Errors | What unmasked |
|-------|--------|---------------|
| 04-03 baseline (infra only) | 320 | infra↔service↔PM interdependencies |
| After Task 1 + Task 2 (service + PM copy) | 268 | service-side refs resolved; cascade pom gaps surface |
| After cascade pom deps (Task 3 part C) | 20 | All missing packages resolved; method/signature mismatches surface |
| After Rule 1 fixes (Task 3 part D) | 1 | Unreported checked exception from throws-declaration mismatch |
| After throws-declaration refinement | **0** | Merged compile unit green |

This validates Decision A: complete the compile unit, then judge. Wave-by-wave structural gates are not achievable when source topology crosses wave boundaries.

## Threat Model Adherence

The plan's `<threat_model>` assigned `accept` dispositions to all 3 threats — no mitigations required of this plan:

| Threat | Disposition | How landed in this plan |
|--------|-------------|-------------------------|
| T-04-04-T (Tampering, @Transactional boundary) | accept | Service impls照搬 source @Transactional verbatim; Phase 2 JpaTransactionManager @Primary (single Druid DataSource) handles all boundaries. No new transaction design. |
| T-04-04-R (Repudiation, audit) | accept | Source audit field behavior preserved (createdDate/updatedDate via @EntityListeners); no new audit logic. |
| T-04-04-I (Info Disclosure, SQL injection) | accept | All JPA queries照搬 source (Spring Data JPA parameterized); no new query surface. |

No new threat surface introduced. The TokenUtil overload addition is a 1-line delegate using existing signing logic (HS512 + jjwt) — no new cryptographic surface. The xxl-job-core dep is compile-time only (no @Bean XxlJobSpringExecutor wired). The nacos-common + spring-cloud-alibaba-commons jars do not auto-configure discovery.

## Verification Evidence

### Merged compile gate (closes both 04-04 and 04-03 deferred gates)

Command:
```
JAVA_HOME=/Users/alan/Library/Java/JavaVirtualMachines/corretto-1.8.0_482/Contents/Home \
/Users/alan/App/apache-maven-3.8.6/bin/mvn -s /Users/alan/App/apache-maven-3.8.6/zg_settings.xml \
-pl zgbas-system -am compile
```

Result: `BUILD SUCCESS`, `ERROR_COUNT=0`, `CANNOT_FIND=0` (locale-independent grep `^\[ERROR]` / `cannot find symbol|找不到符号`). Log: `/tmp/p4-04-cascade4.log`.

### Downstream sanity (zgbas-admin — IndexController contract compatibility)

Command:
```
JAVA_HOME=<Corretto-1.8> mvn -pl zgbas-admin -am compile
```
Result: `BUILD SUCCESS`, `ADMIN_ERROR_COUNT=0`. Confirms Wave 1 contract upgrade (04-02) + Wave 2a infra + Wave 2b service + PM absorption introduce no downstream breakage. Log: `/tmp/p4-04-admin.log`.

### Done-criteria metrics

| Criterion | Target | Actual |
|-----------|--------|--------|
| SVC top-level (I* + concrete) | ≥ 241 | **241** (235 I*Service + 6 concrete) |
| SVC_IMPL | ≥ 248 | **248** |
| CTR / LOGISTICS / PERFORMANCE / RT / STOCK | 9 / 6 / 4 / 1 / 24 | **9 / 6 / 4 / 1 / 24** ✓ |
| Service+domain total | ~533 ±10 | **533** ✓ |
| PM absorbed (annotation/cache/dao/service/util) | ~50 | **51** ✓ |
| PM constant (Phase 2 oversight) | 1 | **1** ✓ |
| PM api deferred (Wave 4) | 0 copied | **0** ✓ |
| PM task deferred (P6) | 0 copied | **0** ✓ |
| PM dedup vs Phase 2 (vo/entity/inter) | untouched | **untouched** ✓ |
| command/ @XxlJob file deferred (P6) | removed | **BasCommandExecutor + package-info removed** ✓ |
| Cascade pom deps added | 9 total (3 pre + 6 new) | **9** ✓ |
| Merged zgbas-system ERROR_COUNT | 0 | **0** ✓ |
| Merged zgbas-system CANNOT_FIND | 0 | **0** ✓ |
| zgbas-admin sanity (no regression) | 0 errors | **0** ✓ |
| Package names verbatim (D-P2-07) | com.spt.bas.server.* + com.spt.pm.* | **confirmed (sample-checked)** |

## Commits

| Hash | Message | Files |
|------|---------|-------|
| `4c3074d` | `feat(04-04): port basServer service + impl + domain subpkgs (533 files)` | 533 created |
| `719eb9f` | `feat(04-04): absorb PM module (annotation/cache/dao/service/util, 51 files)` | 51 created |
| `1bf0893` | `fix(04-04): cascade resolution — merged compile gate GREEN (closes 04-03 + 04-04)` | 1 created (PmConstants) + 3 modified (TokenUtil, BasicErrorController, pom) + 2 deleted (BasCommandExecutor, package-info) |

Sequential order matches Decision A's compile-unit merge: copy first (Task 1), absorb PM (Task 2), resolve cascade + close gate (Task 3).

## Hand-off to Wave 3 (04-05)

The merged compile unit (infra + service + PM) is GREEN. Wave 3 (04-05) can now port `basServer/api/**` (224 `@RestController extends BaseApi`) + `pm/api/**` (13 @RestController) — these endpoints will autowire the now-available service beans. Path prefix handling per D-P4-01a (BasFeignPathConfig from 04-01) remains in force.

Phase 6 memory: re-port `command/BasCommandExecutor` + 4 referenced task classes (ApplyPayTask, BudgetSettlementTask, CtrContractScheduleTask, DcsxAutoApplyPayTask) alongside basServer/task/23 + rocketmq/task/8 as part of xxl-job → RuoYi quartz refactor.

Phase 5 memory: when ReportServer ports, inline report-client source (replacing the types-only jar dep added here).

v2 memory: when basWx ports, inline purchase-client source (replacing the types-only jar dep added here).

## Known Stubs

None introduced by this plan. Decision A forbids stubs — all missing compile surface was resolved by:
- Source copy completion (pmClient constant)
- Cascade jar deps (verbatim from source pom)
- 2 Rule 1 source-bug fixes (1-line overload delegate + 1-token type-match)

The D-P4-02 stub-degradation for ~5-15 no-impl contracts remains deferred to Plan 04-06 (Wave 4 BFF field layer, `@Autowired(required=false)` + null guards) per 04-02-SUMMARY.

## Threat Flags

None. No new network endpoints, auth paths, file access patterns, or trust-boundary schema changes introduced. The xxl-job-core jar dep is compile-time only (no `@Bean XxlJobSpringExecutor`). The nacos-common + spring-cloud-alibaba-commons jars are inert util jars (no autoconfig). Runtime endpoint exposure is a Wave 3 (api `@RestController`) concern, and runtime access control remains covered by Phase 3 Shiro chain.

## Self-Check: PASSED

**Files created (sample):**
- `FOUND: zgbas-system/src/main/java/com/spt/bas/server/service/IApplyBrandService.java`
- `FOUND: zgbas-system/src/main/java/com/spt/bas/server/service/impl/ApplyBrandServiceImpl.java`
- `FOUND: zgbas-system/src/main/java/com/spt/bas/server/ctr/service/ICtrContractSaveService.java`
- `FOUND: zgbas-system/src/main/java/com/spt/bas/server/logistics/service/ICtrLogisticsDeliveryService.java`
- `FOUND: zgbas-system/src/main/java/com/spt/bas/server/performance/service/IPerformanceCommissionDeptService.java`
- `FOUND: zgbas-system/src/main/java/com/spt/bas/server/rt/RtApi.java`
- `FOUND: zgbas-system/src/main/java/com/spt/bas/server/stock/service/IStockVirtualService.java`
- `FOUND: zgbas-system/src/main/java/com/spt/pm/annotation/ServerTransactional.java`
- `FOUND: zgbas-system/src/main/java/com/spt/pm/cache/DeptCache.java`
- `FOUND: zgbas-system/src/main/java/com/spt/pm/dao/PmProcessDao.java`
- `FOUND: zgbas-system/src/main/java/com/spt/pm/service/IPmProcessService.java`
- `FOUND: zgbas-system/src/main/java/com/spt/pm/service/impl/PmProcessServiceImpl.java`
- `FOUND: zgbas-system/src/main/java/com/spt/pm/util/RuleUtils.java`
- `FOUND: zgbas-system/src/main/java/com/spt/pm/constant/PmConstants.java`

**Files modified:**
- `FOUND: zgbas-common/src/main/java/com/spt/tools/http/util/TokenUtil.java` — `createToken(Map, String)` overload present (grep confirmed)
- `FOUND: zgbas-system/src/main/java/com/spt/bas/server/config/BasicErrorController.java` — `LocalDateTime.now()` at line 111 (replaces `new Date()`)
- `FOUND: zgbas-system/pom.xml` — 9 cascade deps total

**Files deleted (intentional Phase 6 defers):**
- `FOUND: BasCommandExecutor.java` removed from working tree (preserved in commit `3c7cc52` for Phase 6 re-port)
- `FOUND: command/package-info.java` removed

**Excludes verified:**
- `FOUND: pm/api/` absent (deferred to Wave 4 / 04-05)
- `FOUND: pm/task/` absent (deferred to Phase 6)
- `FOUND: command/` pkg absent (BasCommandExecutor deferred to Phase 6)
- `FOUND: pm/{vo,entity,inter}` untouched (Phase 2 pmClient dedup honored)

**Commits:**
- `FOUND: 4c3074d` (git log)
- `FOUND: 719eb9f` (git log)
- `FOUND: 1bf0893` (git log)

**Plan done criteria (all tasks):** all met (see Done-criteria metrics table). Merged compile gate GREEN closes both 04-04 and 04-03 deferred structural gate.
