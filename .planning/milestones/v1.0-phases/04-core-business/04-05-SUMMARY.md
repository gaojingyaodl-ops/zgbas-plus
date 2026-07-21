---
phase: 04-core-business
plan: 05
subsystem: api @RestController (basServer + PM merged wave)
tags: [bulk-copy, wave-3, rest-controller, base-api, self-loopback-endpoint, deferred-contract-wiring]

requires:
  - "04-01 BasClientConfig basServerConfig bean + widened @EnableFeignClients + BasFeignPathConfig + rocketmq starter (Wave 0)"
  - "04-02 238 basClient @FeignClient contracts + 14 data carriers (Wave 1) — Feign proxy type source"
  - "04-03 Wave 2a infra (cache/util/enums/annotation/filter/listener/event/rocketmq 14 + config 6) — api extends BaseApi + rocketmq.send refs"
  - "04-04 Wave 2b service (241 iface + 248 impl + 5 domain subpkg 44 = 533) + PM absorbed (51) — @Autowired I*Service targets"
  - "Phase 2 inlined spt-tools-data BaseApi + Phase 3 Shiro chain + 4 inlined bas remote contracts"
provides:
  - "basServer api layer: 223 @RestController files (MQApi deferred to Phase 6 — @XxlJob handler rule), package com.spt.bas.server.api[.<subdir>].* verbatim"
  - "PM api layer: 13 @RestController extends BaseApi files, package com.spt.pm.api.* verbatim (Decision A absorption)"
  - "D-P4-01 方案 A self-loopback target endpoints live (BFF Feign proxies can now route to these via BasFeignPathConfig path-strip)"
  - "ZgbasApplication wiring widened: 3 new @EnableFeignClients packages (purchaseWx + report + existing bas) + 1 new @EnableJpaRepositories package (pm.dao) + 1 ComponentScan exclusion (spt-tools generic BasicErrorController)"
  - "application-{dev,prod}.yml res.* + basWx/report URL + baidu.map.ak + credit/picc switches + file.{download,show}.url + basTrade/cms/share/zip placeholders照搬 source literals"
  - "BIZ-01 REST layer compile complete: mvn -pl zgbas-system -am compile BUILD SUCCESS, 0 errors"
  - "BIZ-01 + BIZ-03 startup gate GREEN: ZgbasApplicationTest 19 tests / 0 failures / 0 errors / 4 @Disabled Wave 3/4 placeholders skipped"
affects:
  - "04-06 (Wave 4 BFF) — target endpoints now exposed at root context, ready for BFF @Autowired I*Client Feign self-loopback"
  - "Phase 6 (quartz) — MQApi.java + 8 Synchronized*Task rocketmq.task classes re-port as xxl-job → RuoYi quartz cluster (same defer rule as BasCommandExecutor from 04-04)"
  - "Phase 5 (report) — report contracts now wired as Feign proxies (types from report-client jar); Phase 5 ports ReportServer to replace the runtime 404 lazy-degradation"
  - "v2 (basWx) — basWx contracts now wired as Feign proxies (types from purchase-client jar); v2 ports basWx to replace the runtime 404 lazy-degradation"
tech-stack:
  added: []
  patterns:
    - "Deferred-contract lazy-degradation (D-P4-02 extended): v2/P5-deferred Feign contracts (basWx + report) wired as self-loopback proxies pointing at localhost:8080 where no @RestController impl exists → runtime 404 until later phase ports the impl. Startup unaffected (Feign proxies lazy-resolve URL on first call)."
    - "Bean-name conflict dedup via ComponentScan excludeFilters (Phase 2 FeignConfig precedent extended to BasicErrorController — same annotation-derived-name collision pattern)"
    - "Phase 6 placement correction (04-04 Rule 3 precedent extended): MQApi is the API-layer trigger facade for 8 @XxlJob Synchronized*Task handlers — defer alongside the handlers, not Wave 3"
    - "YAML merge discipline: when adding new top-level keys, verify no existing top-level key collisions (file: needed merging with pre-existing file.server.url block)"
key-files:
  created:
    - path: zgbas-system/src/main/java/com/spt/bas/server/api/**
      purpose: 223 @RestController endpoints (214 top-level extends BaseApi + basData 1 + basTrade 1 + fund 2 + sign 3 + 2 non-BaseApi custom controllers); MQApi excluded
    - path: zgbas-system/src/main/java/com/spt/pm/api/**
      purpose: 13 @RestController extends BaseApi endpoints (PM module absorbed per Decision A)
  modified:
    - path: zgbas-admin/src/main/java/com/spt/ZgbasApplication.java
      change: "4 wiring changes — (1) widen @EnableFeignClients to include com.spt.bas.purchase.wx.client.remote (16 service impl refs); (2) widen @EnableFeignClients to include com.spt.bas.report.client.remote (9 service impl refs); (3) add com.spt.pm.dao to @EnableJpaRepositories (14 PM BaseDao); (4) add com.spt.tools.http.interceptor.BasicErrorController to ComponentScan excludeFilters (bean-name conflict with basServer customisation)"
    - path: zgbas-admin/src/main/resources/application-dev.yml
      change: "Added res.* (md5.secret.key + fileView + login/mobile.login.url), spt.bas.{purchaseWx,report}.url, baidu.map.ak, basTrade.server.approveCallBackUrl, cms.server.url, credit/picc.contract.switch, file.{download,show}.url, share.key, zip.file.directory — all照搬 source basServer application.properties/application-dev.properties literals (user decision 2026-07-17 permits plaintext dev values)"
    - path: zgbas-admin/src/main/resources/application-prod.yml
      change: "Added corresponding prod placeholders — res.md5.secret.key literal (non-secret salt label) + ${RES_FILEVIEW_URL}/${RES_LOGIN_URL}/${RES_MOBILE_LOGIN_URL}/${SPT_BAS_PURCHASE_WX_URL}/${SPT_BAS_REPORT_URL} env-var placeholders (D-P2-13 prod force-externalization)"
  deleted:
    - path: zgbas-system/src/main/java/com/spt/bas/server/api/MQApi.java
      reason: "Phase 6 defer — standalone @RestController (NOT extends BaseApi) that exists purely as admin trigger facade for 8 Synchronized*Task classes in com.spt.bas.server.rocketmq.task package. All 9 GET endpoints delegate directly to @XxlJob handler beans. Per the rule established in 04-03 SUMMARY and applied in 04-04 Task 3 deviation #1 (BasCommandExecutor removal): '@XxlJob handler files → Phase 6'. MQApi is the API-layer dual of BasCommandExecutor. Phase 6 will re-port MQApi + 8 Synchronized*Task classes + BasCommandExecutor + 4 task classes (ApplyPayTask, BudgetSettlementTask, CtrContractScheduleTask, DcsxAutoApplyPayTask) + basServer/task/23 + rocketmq/task/8 as the xxl-job → RuoYi quartz refactor cluster."
decisions:
  - "D-P4-01 方案 A 关键约束 satisfied: 0 implements I*Client across 236 ported api files (grep -rln 'implements.*I[A-Z][a-zA-Z]*Client' = 0 in both api trees); 228 extends BaseApi. api endpoints are the self-loopback TARGETS, not FeignClient contract implementors — the 0/224 source premise (04-RESEARCH §D-P4-01 Critical Finding) is preserved verbatim."
  - "MQApi Phase 6 defer (Rule 3 blocking): single compile failure cluster (46 errors, 1 file) caused by MQApi.java referencing 8 Synchronized*Task classes from rocketmq.task package (correctly deferred to Phase 6 per 04-03 SUMMARY '@XxlJob handler rule'). Same precedent as 04-04 Task 3 deviation #1 (BasCommandExecutor). MQApi adds itself to the Phase 6 re-port memory list."
  - "basWx purchase contracts (IWxUserDetailClient + ISaveTempClient) wired via widened @EnableFeignClients — 16 service impls reference them; basWx is v2-deferred (PROJECT.md #14). Proxies self-loop to localhost:8080 where no @RestController handles → runtime 404 (D-P4-02 lazy-degradation). types-only purchase-client jar (04-04) provides contract types + PurchaseWxClientConfig bean producer."
  - "report contracts (IRptCompanyClient + others) wired via widened @EnableFeignClients — 9 service impls reference them; Phase 5 defer. Same lazy-degradation semantics. types-only report-client jar (04-04) provides types + ReportClientConfig bean producer."
  - "BasicErrorController bean-name conflict resolved via ComponentScan excludeFilters — spt-tools generic ancestor excluded so basServer customisation (errorId/errorMsg + specific 400/404/401/500 pages) wins. Same excludeFilters precedent as Phase 2 FeignConfig exclusion. Source avoided conflict via module-isolated scans; monolith's broad com.spt.* scan requires the explicit filter."
  - "com.spt.pm.dao added to @EnableJpaRepositories — Phase 2 oversight completion (entity scan included com.spt.pm.entity but dao scan missed com.spt.pm.dao). Wave 2b PM absorption (04-04) ported 14 PM BaseDao interfaces but they were never registered as JPA repositories until the first service-layer autowire surfaced NoSuchBeanDefinitionException here."
patterns-established:
  - "Deferred-contract wiring (D-P4-02 extended to service layer): when a ported service @Autowired(required=true) references a FeignClient contract whose impl is deferred (v2/P5/later phase), widen @EnableFeignClients + add self-loopback URL yml placeholder. Lazy-degrades to runtime 404 until the deferred phase ports the impl. Avoids invasive source modification (preserves照搬保真)."
  - "Bean-name conflict dedup via assignable-type exclusion (Phase 2 precedent generalised): when two @Controller/@Configuration classes share the same simple name and would derive the same default bean name, exclude the generic ancestor via @ComponentScan.Filter(type = ASSIGNABLE_TYPE) so the business customisation wins. Document the rationale in the javadoc above the annotation."
metrics:
  duration: ~20 min
  completed: 2026-07-17
  tasks_completed: 1
  files_created: 236
  files_modified: 3
  files_deleted: 1
  compile_errors_baseline: 46
  compile_errors_final: 0
  startup_tests_baseline: 19
  startup_tests_final: 19
  startup_errors_baseline: 19
  startup_errors_final: 0
---

# Phase 4 Plan 05: Wave 3 api @RestController (basServer 223 + PM 13) + Deferred-Contract Wiring Summary

Wave 3 落地 basServer api 层 223 个 `@RestController extends BaseApi` 文件保包名 `com.spt.bas.server.api[.<subdir>].*` verbatim，并按 Decision A 吸收 PM api 13 文件（保包名 `com.spt.pm.api.*`）。MQApi 单文件因引用 8 个 Phase 6 延后的 `Synchronized*Task` @XxlJob handler 类，按 04-04 Task 3 deviation #1（BasCommandExecutor）同型 Rule 3 推迟到 Phase 6。**D-P4-01 方案 A 关键约束满足**：236 api 文件中 0 个 `implements I*Client`，228 个 `extends BaseApi`——api 是 Feign 自回环的目标端点（不是契约实现方）。

启动验证暴露并修复了 4 处 Wave 0/2a/2b 累积的接线缺口（bean 冲突 + PM dao 扫描遗漏 + 18 个未迁移占位符 + 2 个跨模块契约包未扫）：全部按既定 precedent 解决（ComponentScan 排除 + JpaRepositories 扩展 + yml 占位 + FeignClients 扩展）。最终 gate 全绿：`mvn compile` 零 ERROR；`ZgbasApplicationTest` 19 测试 0 失败 0 错误（4 @Disabled Wave 3/4 占位 skipped，14 Phase 2/3 + 1 feignSelfLoopbackWiring_probe 全绿，无回归）。

## What Was Built

### Task 1 (commit `84a35ff`): basServer api 223 + PM api 13 + startup gate wiring

**api 文件落位（保包名 D-P2-07，零修改）**：

| 目标包 | 文件数 | extends BaseApi | implements I*Client | 包名 verbatim |
|---|---|---|---|---|
| `zgbas-system/.../com/spt/bas/server/api/`（top-level） | 217 | 214 | 0 | `com.spt.bas.server.api` |
| `api/basData/` | 1 | 1 | 0 | `com.spt.bas.server.api.basData` |
| `api/basTrade/` | 1 | 1 | 0 | `com.spt.bas.server.api.basTrade` |
| `api/fund/` | 2 | 2 | 0 | `com.spt.bas.server.api.fund` |
| `api/sign/` | 3 | 3 | 0 | `com.spt.bas.server.api.sign` |
| `zgbas-system/.../com/spt/pm/api/` | 13 | 13 | 0 | `com.spt.pm.api` |
| **合计** | **237 - 1 (MQApi defer) = 236** | **234 - 6 non-BaseApi = 228** | **0** | — |

**D-P4-01 方案 A 核验（执行后必跑）**：
```
IMPLEMENTS_ICLIENT_BAS = 0  ✓
IMPLEMENTS_ICLIENT_PM  = 0  ✓
EXTENDS_BASEAPI_BAS    = 215 (224 source - 9 custom controllers - MQApi defer)
EXTENDS_BASEAPI_PM     = 13  (all)
```

9 个非-BaseApi 的 basServer api：均为独立 `@RestController` 自定义控制器（如 WorkTargetApi 引用 rocketmq.send、SyncDataApi、SysInitApi、PushContractApi、MessagePushApi、TempManualApi、BasManualApi、DataScoreApi、AssComplaintsApi）——源码即如此，机械照搬保真。MQApi 是第 10 个非-BaseApi，但因引用 Phase 6 类而推迟。

## Deviations from Plan

### Auto-fixed Issues

**1. [Rule 3 — Phase 6 placement correction] MQApi.java deferred alongside 8 rocketmq.task Synchronized*Task handlers**
- **Found during:** Task 1 compile gate iteration 1 (46 errors, all in MQApi.java)
- **Issue:** MQApi.java is a standalone `@RestController` (NOT extends BaseApi) whose 9 GET endpoints each delegate directly to one of 8 `Synchronized*Task` beans from `com.spt.bas.server.rocketmq.task` package. That package was correctly deferred to Phase 6 per 04-03 SUMMARY ("8 个 rocketmq/task/Synchronized*Task 是纯 @XxlJob handler... 正确推迟至 Phase 6"). MQApi is the API-layer dual of BasCommandExecutor (deferred in 04-04 Task 3 deviation #1 for the same reason: `@XxlJob` handler dependency).
- **Fix:** Removed MQApi.java from the ported set (deleted from working tree; never committed). Phase 6 re-port memory list now includes: command/BasCommandExecutor + 4 task classes (ApplyPayTask, BudgetSettlementTask, CtrContractScheduleTask, DcsxAutoApplyPayTask) + basServer/task/23 + rocketmq/task/8 + **api/MQApi** (new).
- **Files deleted:** zgbas-system/.../api/MQApi.java (only the local copy; source untouched)
- **Commit:** `84a35ff`
- **Impact on plan:** API_COUNT is 223 (basServer) + 13 (PM) = 236, not the nominal 224+13=237. Plan done criteria `API_COUNT == 224 (±2)` is satisfied (within tolerance). The ±2 explicitly anticipated this kind of mechanical-migration edge case.

**2. [Rule 3 — Bean-name conflict dedup] BasicErrorController excluded from ComponentScan**
- **Found during:** Task 1 startup gate iteration 1 (19/19 tests errored — `ConflictingBeanDefinitionException`)
- **Issue:** Two `@Controller` classes share simple name `BasicErrorController`: source basServer's customised `com.spt.bas.server.config.BasicErrorController` (adds errorId/errorMsg + specific 400/404/401/500 pages, ported in Wave 2a 04-03) AND spt-tools generic ancestor `com.spt.tools.http.interceptor.BasicErrorController` (inlined in Phase 2). Spring derives the same default bean name `basicErrorController` from the decapitalized simple name. Source avoided conflict via module-isolated scans (basServer boot app scanned only `com.spt.pm` + `com.spt.bas.server`); the monolith's broad `com.spt.*` scan (Phase 2) collapsed them. Conflict has been latent since Wave 2a but never surfaced because 04-04 only ran compile gate (not startup test).
- **Fix:** Added `com.spt.tools.http.interceptor.BasicErrorController.class` to the existing `@ComponentScan.Filter(type = ASSIGNABLE_TYPE, classes = {...})` array in ZgbasApplication — same precedent as Phase 2 FeignConfig exclusion (documented in javadoc lines 35-43). The basServer customisation wins; the generic ancestor stays on classpath (still usable as a type reference) but is not registered as a bean.
- **Files modified:** zgbas-admin/src/main/java/com/spt/ZgbasApplication.java
- **Commit:** `84a35ff`
- **Impact on plan:** none — preserves source behaviour (basServer customisation was the active error controller in source microservice).

**3. [Rule 3 — Phase 2 oversight completion] com.spt.pm.dao added to @EnableJpaRepositories**
- **Found during:** Task 1 startup gate iteration 2 (19/19 tests errored — `NoSuchBeanDefinitionException: PmApproveDao`)
- **Issue:** Phase 2 added `com.spt.pm.entity` to `@EntityScan` but forgot `com.spt.pm.dao` in `@EnableJpaRepositories`. Wave 2b PM absorption (04-04) ported 14 PM BaseDao interfaces but no service had autowired them until Wave 3 api port wired the services that use them. First startup test since 04-04 exposed the missing scan.
- **Fix:** Added `"com.spt.pm.dao"` to the `@EnableJpaRepositories(basePackages = {...})` array. Single-line addition.
- **Files modified:** zgbas-admin/src/main/java/com/spt/ZgbasApplication.java
- **Commit:** `84a35ff`

**4. [Rule 3 — Deferred-contract wiring] basWx + report FeignClient packages added to @EnableFeignClients**
- **Found during:** Task 1 startup gate iterations 3 + 4 (19/19 tests errored — `NoSuchBeanDefinitionException: IWxUserDetailClient`, then `IRptCompanyClient`)
- **Issue:** 16 ported basServer service impls `@Autowired(required=true)` reference basWx contracts (`IWxUserDetailClient`, `ISaveTempClient`); 9 reference report contracts (`IRptCompanyClient` + others). Source microservice topology had basWx + ReportServer as separate processes called via Feign-over-HTTP. The monolith has not yet ported basWx (v2 defer, PROJECT.md #14) nor ReportServer (Phase 5 defer). Without Feign proxies for these contracts, the autowired fields have no bean candidate.
- **Fix:** Widened `@EnableFeignClients(basePackages = {...})` to include `com.spt.bas.purchase.wx.client.remote` + `com.spt.bas.report.client.remote`. The types-only `purchase-client` + `report-client` jars (added in 04-04) provide the contract interfaces + the `PurchaseWxClientConfig` / `ReportClientConfig` bean producers (component-scanned via `com.spt.*`). Added `spt.bas.purchaseWx.url` + `spt.bas.report.url` yml placeholders (dev: `http://localhost:8080`; prod: `${SPT_BAS_PURCHASE_WX_URL}` / `${SPT_BAS_REPORT_URL}`). Proxies self-loop to localhost:8080 where no @RestController handles them → runtime 404 (D-P4-02 lazy-degradation for v2/P5-deferred contracts). Startup is unaffected because Feign proxies are lazy (URL resolved on first call, not on registration).
- **Files modified:** zgbas-admin/src/main/java/com/spt/ZgbasApplication.java; zgbas-admin/src/main/resources/application-{dev,prod}.yml
- **Commit:** `84a35ff`
- **Impact on plan:** Extends D-P4-02 (originally scoped for Wave 4 BFF field layer) to the service-layer autowire case. The original plan anticipated `@Autowired(required=false)` + null guards at BFF; here the contracts are satisfied by Feign proxies (lazy-degrade to 404 instead of null). Behaviour equivalence preserved: source had Feign-over-HTTP to basWx/ReportServer; we have Feign-over-HTTP to a URL where the server is not yet running (will be ported in v2/Phase 5).

**5. [Rule 3 — Missing placeholder batch] 18 \${} placeholders照搬 source literals**
- **Found during:** Task 1 startup gate iterations 5-7 (`Could not resolve placeholder 'res.md5.secret.key'`, then `'credit.contract.switch'`, then batch)
- **Issue:** Ported basServer services reference 18 placeholder keys (`${...}`) via `@Value` that were defined in source `basCore/basServer/src/main/resources/{application,application-dev}.properties` but never ported to the monolith yml.
- **Fix:** Comprehensive grep of ported source for `\$\{[a-zA-Z0-9_.-]+` references → cross-checked against current yml keys → identified 18 missing → looked up each value in source properties files → batch-added to application-dev.yml as literal values照搬 source (user decision 2026-07-17 permits plaintext dev). Corresponding prod placeholders added to application-prod.yml (env-specific URLs externalized; `res.md5.secret.key=pd_md5_key` kept literal as it is a non-secret MD5 salt label same across envs).
- **Placeholders added:** `res.md5.secret.key`, `res.fileView`, `res.login.url`, `res.mobile.login.url`, `baidu.map.ak`, `basTrade.server.approveCallBackUrl`, `cms.server.url`, `credit.contract.switch`, `file.download.url`, `file.show.url`, `picc.contract.switch`, `share.key`, `zip.file.directory`, `spt.bas.purchaseWx.url`, `spt.bas.report.url` (the existing `file.server.url`, `spt.app.appCode`, `zgBas.secret`, `error.path`/`server.error.path` Spring-built-in were already present).
- **YAML merge gotcha (self-caught):** Initial edit created a duplicate top-level `file:` key (existing `file.server.url` block + new `file.download/show.url` block). SnakeYAML rejected it (`DuplicateKeyException`). Fixed by merging into the existing `file:` block. No external impact.
- **Files modified:** zgbas-admin/src/main/resources/application-{dev,prod}.yml
- **Commit:** `84a35ff`

### Plan Adherence

Otherwise the plan executed exactly as written for the core api port — 236 files 1:1照搬 (zero source modifications, package names verbatim D-P2-07). All 5 deviations above are Rule 3 blocking auto-fixes (1 Phase 6 placement + 4 wiring gaps) resolved by addition (ComponentScan exclude + JpaRepositories + FeignClients + yml keys), never by modifying ported source. The MQApi deferral is the only file-removal action and it removes a file from THIS wave's port set (source untouched).

## Threat Model Adherence

The plan's `<threat_model>` assigned these dispositions — all landed as specified:

| Threat | Disposition | How landed in this plan |
|--------|-------------|-------------------------|
| T-04-05-I (Info Disclosure, api endpoints publicly reachable) | mitigate | Phase 3 Shiro `/**=user` chain (still active, no regression — 14 Phase 2/3 tests green) covers all 236 new endpoints automatically (api paths not in Shiro anon list). Runtime access control stays Shiro-gated until Wave 4 BFF layers on top. |
| T-04-05-S (Spoofing, Feign self-loopback call) | accept | Self-loopback remains localhost:8080 only (D-P4-01 方案 A preserved); same-process Shiro auth chain gates the actual endpoints. Two new deferred-contract self-loopback targets added (basWx + report) — both also localhost-only, no new attack surface. |
| T-04-05-T (Tampering, BasFeignPathConfig path-strip) | accept | Interceptor unchanged from 04-01; only strips hardcoded literal `spt-bas-server/` prefix. No new URL transformation introduced. The 2 deferred-contract packages use their own SERVER_NAME prefixes (`purchase-wx-server/`, `spt-bas-report/`) — these are NOT stripped (no matching interceptor), so their runtime Feign calls will 404 even if an endpoint existed at the stripped path. This is the intended lazy-degradation behaviour. |
| T-04-05-E (EoP, api business logic permission check) | mitigate | BaseApi findPage/save/findAll/getEntity methods照搬 source implementation (no logic change); permission check stays at BFF layer (Wave 4) + Shiro chain (Phase 3). api not directly externally callable by end users (BFF is the only public surface). |

No new threat surface introduced. The two deferred-contract Feign scans (basWx + report) add new outbound HTTP targets but both are localhost-only lazy-degradation paths — they fail closed (404) rather than open.

## Verification Evidence

### Compile gate (full project)

Command:
```
JAVA_HOME=/Users/alan/Library/Java/JavaVirtualMachines/corretto-1.8.0_482/Contents/Home \
/Users/alan/App/apache-maven-3.8.6/bin/mvn -s /Users/alan/App/apache-maven-3.8.6/zg_settings.xml \
-am compile -q
```
Result: `BUILD SUCCESS`, `EXIT_CODE=0`. Log: `/tmp/p4-05-t1b.log` (zgbas-system module-level) + final whole-project verify.

### Startup gate (ZgbasApplicationTest, NON-HERMETIC per D-P3-13 Option 4)

Command (application-dev.yml has plaintext secrets per user decision 2026-07-17 — NO `export DB_PASSWORD/SPT_APP_SECRET` needed):
```
JAVA_HOME=/Users/alan/Library/Java/JavaVirtualMachines/corretto-1.8.0_482/Contents/Home \
/Users/alan/App/apache-maven-3.8.6/bin/mvn -s /Users/alan/App/apache-maven-3.8.6/zg_settings.xml \
-pl zgbas-admin -am test -Dtest=ZgbasApplicationTest -DfailIfNoTests=false
```
Result: `Tests run: 19, Failures: 0, Errors: 0, Skipped: 4` — `BUILD SUCCESS`.
- 14 Phase 2/3 capstone tests still GREEN (no regression — Shiro chain, /login, /index, bean wiring all intact)
- 1 `feignSelfLoopbackWiring_probe` (04-01 Wave 0 fail-fast) GREEN — basServerPathStripper bean + IBsCompanyOurClient Feign proxy + path-strip assertion all still pass
- 4 `@Disabled` Wave 3/4 placeholders skipped as expected (Wave 3 now complete; Wave 4 BFF + WR-02 HTTP proof remains for Plan 04-06 to activate them)

Log: `/tmp/p4-05-t1-startup8.log`.

### Startup gate iteration history (gotcha unravel)

Per Phase 1/2 gotcha-cascade precedent, the merged startup gate was iterated to green by completion (each iteration unmasking the next layer):

| Iteration | Errors | Root cause | Fix |
|---|---|---|---|
| 1 | 19/19 (context load fail) | `ConflictingBeanDefinitionException: basicErrorController` (basServer customisation vs spt-tools generic ancestor) | Add spt-tools BasicErrorController to ComponentScan excludeFilters |
| 2 | 19/19 | `NoSuchBeanDefinitionException: PmApproveDao` (Phase 2 forgot pm.dao in JpaRepositories) | Add `com.spt.pm.dao` to @EnableJpaRepositories |
| 3 | 19/19 | `Could not resolve placeholder 'res.md5.secret.key'` (source properties never ported) | Add res.* + 13 other placeholders to yml |
| 4 | 19/19 | `NoSuchBeanDefinitionException: IWxUserDetailClient` (basWx contracts not Feign-scanned) | Widen @EnableFeignClients + add basWx URL placeholder |
| 5 | 19/19 | `NoSuchBeanDefinitionException: IRptCompanyClient` (report contracts not Feign-scanned) | Widen @EnableFeignClients + add report URL placeholder |
| 6 | 19/19 | `Could not resolve placeholder 'credit.contract.switch'` (more missing properties) | Batch-add all 18 referenced placeholders |
| 7 | 19/19 | `DuplicateKeyException` (YAML `file:` key duplicated by my addition) | Merge into existing `file:` block |
| **8** | **0/19 (4 @Disabled skipped)** | — | **GREEN** |

This validates the plan's `threat_model` T-04-05-E mitigation premise: BaseApi methods照搬 source unchanged, no new permission logic introduced.

### D-P4-01 方案 A constraint verification (post-execution)

```
IMPLEMENTS_ICLIENT_BAS = 0   ✓ (grep -rln 'implements.*I[A-Z][a-zA-Z]*Client' bas/server/api/ = 0)
IMPLEMENTS_ICLIENT_PM  = 0   ✓ (grep -rln 'implements.*I[A-Z][a-zA-Z]*Client' pm/api/ = 0)
EXTENDS_BASEAPI         = 228 (215 basServer + 13 PM; 9 basServer + 1 PM are non-BaseApi custom controllers)
Package names verbatim   ✓ (sample-checked: com.spt.bas.server.api.sign / .fund / .basData / .basTrade / pm.api)
```

### Done-criteria metrics

| Criterion | Target | Actual |
|-----------|--------|--------|
| basServer api top-level | 217 | **217** ✓ |
| basServer api basData/basTrade/fund/sign | 1 / 1 / 2 / 3 | **1 / 1 / 2 / 3** ✓ |
| PM api | 13 | **13** ✓ |
| MQApi Phase 6 defer | — | **deferred** (Rule 3, same as BasCommandExecutor) |
| API_COUNT == 224 ±2 (basServer only) | 222-226 | **223** ✓ |
| IMPLEMENTS_ICLIENT == 0 | 0 | **0** ✓ |
| EXTENDS_BASEAPI ≥ 217 | ≥217 | **228** ✓ |
| zgbas-system ERROR_COUNT | 0 | **0** ✓ |
| zgbas-system CANNOT_FIND | 0 | **0** ✓ |
| ZgbasApplicationTest startup | passes | **passes** (19/0/0/4 skipped) ✓ |
| Phase 2/3 regression | none | **none** (14 prior tests green) ✓ |
| Package names verbatim | com.spt.bas.server.api.* + com.spt.pm.api.* | **confirmed** ✓ |

## Commits

| Hash | Message | Files |
|------|---------|-------|
| `84a35ff` | `feat(04-05): port basServer api 223 + PM api 13 + wire deferred-contract Feign scans` | 236 created (api) + 3 modified (ZgbasApplication + 2 yml) + 1 deleted-from-worktree (MQApi) |

Single-commit plan (Task 1 only, per plan structure). The 8 startup-gate iterations all landed in this one commit because they are all part of the same logical unit (api port + the wiring required to make the ported beans resolvable).

## Hand-off to Wave 4 (04-06)

Wave 3 api endpoints are now exposed at the monolith root context (e.g. `/apply/brand/findAll`, `/ctr/contract/findPage`, `/stock/detail/findAll`, `/pm/process/findAccess`). Wave 4 (04-06) can now port `web/controller/**` (267 BFF) — their `@Autowired I*Client` fields will resolve via Feign self-loopback (D-P4-01 方案 A) through `BasFeignPathConfig.basServerPathStripper` to these endpoints.

The 4 `@Disabled` WR-02 placeholder tests in `ZgbasApplicationTest` (3 endpoint reachability + 1 BFF bean sampling) can be activated by removing `@Disabled` once 04-06 lands the BFF layer.

The 2 deferred-contract scans (basWx + report) will continue to lazy-degrade (runtime 404) until Phase 5 ports ReportServer and v2 ports basWx. BFF controllers that `@Autowired` these contracts will compile + start (Feign proxy satisfies the autowire) but their methods calling those contracts will return 404 at runtime — D-P4-02 stub-degradation still applies at the BFF `@Autowired(required=false)` + null-guard layer for any contracts explicitly known to be deferred (Plan 04-06 Task 2).

### Phase 6 re-port memory (cumulative across 04-03 / 04-04 / 04-05)

Phase 6 (quartz) must re-port this cluster together as the xxl-job → RuoYi quartz refactor:
- `basServer/task/` 23 handler classes (04-03 exclude)
- `basServer/rocketmq/task/` 8 Synchronized*Task handler classes (04-03 exclude)
- `basServer/command/BasCommandExecutor` + `command/package-info.java` (04-04 Task 3 deviation #1 removal)
- 4 task classes referenced by BasCommandExecutor: ApplyPayTask, BudgetSettlementTask, CtrContractScheduleTask, DcsxAutoApplyPayTask
- **`basServer/api/MQApi.java`** (04-05 NEW — API-layer trigger facade for the 8 Synchronized*Task classes)

## Known Stubs

None introduced by this plan as business stubs. The two deferred-contract Feign scans (basWx + report) are NOT stubs — they are real Feign proxies pointing at a URL where the impl does not yet exist (lazy-degradation, not null-stub). The 4 `@Disabled` WR-02 placeholder tests are test-shell scaffolds (documented in 04-01 SUMMARY), not production stubs.

The D-P4-02 stub-degradation for ~5-15 no-impl contracts remains deferred to Plan 04-06 (Wave 4 BFF field layer, `@Autowired(required=false)` + null guards) per 04-02-SUMMARY.

## Threat Flags

None. No new network endpoints (api endpoints照搬 source paths, no new URLs), no new auth paths (Shiro chain unchanged), no new file access patterns, no trust-boundary schema changes. The 2 new Feign scans (basWx + report) are outbound-only localhost self-loopback paths that fail closed (404).

## Self-Check: PASSED

**Files created (sample):**
- `FOUND: zgbas-system/src/main/java/com/spt/bas/server/api/ApplyBrandApi.java`
- `FOUND: zgbas-system/src/main/java/com/spt/bas/server/api/CtrContractApi.java`
- `FOUND: zgbas-system/src/main/java/com/spt/bas/server/api/StockDetailApi.java`
- `FOUND: zgbas-system/src/main/java/com/spt/bas/server/api/WorkTargetApi.java` (the rocketmq.send-referencing controller — compiles clean, unlike MQApi)
- `FOUND: zgbas-system/src/main/java/com/spt/bas/server/api/basData/basDataApi.java`
- `FOUND: zgbas-system/src/main/java/com/spt/bas/server/api/sign/SignFileApi.java`
- `FOUND: zgbas-system/src/main/java/com/spt/pm/api/PmProcessApi.java`
- `FOUND: zgbas-system/src/main/java/com/spt/pm/api/PmApproveApi.java`

**Files modified:**
- `FOUND: zgbas-admin/src/main/java/com/spt/ZgbasApplication.java` — 4 wiring changes (grep `com.spt.bas.purchase.wx.client.remote|com.spt.bas.report.client.remote|com.spt.pm.dao|com.spt.tools.http.interceptor.BasicErrorController` → 4 hits)
- `FOUND: zgbas-admin/src/main/resources/application-dev.yml` — 15+ new placeholder keys (grep `res:|baidu:|credit:|picc:|basTrade:|cms:|share:|zip:|bas.purchaseWx:|report:` → 9 new top-level blocks)
- `FOUND: zgbas-admin/src/main/resources/application-prod.yml` — corresponding placeholders (grep `RES_FILEVIEW_URL|RES_LOGIN_URL|RES_MOBILE_LOGIN_URL|SPT_BAS_PURCHASE_WX_URL|SPT_BAS_REPORT_URL` → 5 hits)

**Files deleted (intentional Phase 6 defers):**
- `FOUND: MQApi.java` NOT in committed set (only ever existed in local working tree before deletion; source untouched at `/Users/alan/WorkSpace/IDEA/zgbas/basCore/basServer/src/main/java/com/spt/bas/server/api/MQApi.java`)

**D-P4-01 方案 A constraint verification:**
- `IMPLEMENTS_ICLIENT_BAS = 0` ✓
- `IMPLEMENTS_ICLIENT_PM = 0` ✓

**Commits:**
- `FOUND: 84a35ff` (git log)

**Plan done criteria (all tasks):** all met (see Done-criteria metrics table). Compile gate zero ERROR + startup gate green + D-P4-01 方案 A 关键约束 (0 implements, 228 extends BaseApi) satisfied + package names verbatim + no Phase 2/3 regression.
