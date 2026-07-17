---
phase: 04-core-business
plan: 01
subsystem: feign-wiring
tags: [feign, self-loopback, rocketmq, wr02-scaffold, path-prefix, wave-0]
requires:
  - "Phase 2 D-P2-12 narrowed @EnableFeignClients to com.spt.sign.client.remote"
  - "Phase 2 D-P2-13 yml placeholder convention + Phase 3 D-P3-13 non-hermetic startup gate"
  - "Phase 2 inlined com.spt.tools.core.bean.LocalServerConfig + com.spt.tools.core.prop.PropertiesUtil bean"
  - "Phase 2/3 inlined 4 bas remote contracts (IBsDictClient / IBsCompanyOurClient / IApproveWaitDealClient / IPmProcessClient)"
provides:
  - "BasClientConfig basServerConfig LocalServerConfig bean — SpEL anchor for 238 bas @FeignClient url=#{basServerConfig.url}"
  - "BasFeignPathConfig basServerPathStripper RequestInterceptor — strips literal spt-bas-server/ prefix from outgoing Feign URIs (D-P4-01a)"
  - "ZgbasApplication @EnableFeignClients basePackages widened to include com.spt.bas.client.remote (D-P4-01 方案 A)"
  - "application-dev.yml spt.bas.server.url=http://localhost:8080 (self-loopback target) + rocketmq.* config"
  - "application-prod.yml ${SPT_BAS_SERVER_URL} + rocketmq.* placeholders (D-P2-13 prod externalization)"
  - "zgbas-system pom declares rocketmq-spring-boot-starter:2.2.2 (Wave 2 22-file rocketmq package prerequisite)"
  - "ZgbasApplicationTest WR-02 placeholder shell (4 @Disabled Wave 3/4 tests + feignSelfLoopbackWiring_probe)"
affects:
  - "Wave 1 remote-contract inline (04-02) — Feign proxies now generate for ported I*Client interfaces"
  - "Wave 3 api controller port (04-05) — endpoints exposed at root context, reachable by self-loopback proxies"
  - "Wave 4 BFF port (04-06) — @Autowired I*Client fields resolve via Feign proxy"
tech-stack:
  added:
    - "org.apache.rocketmq:rocketmq-spring-boot-starter:2.2.2 (verbatim from source basCore/pom.xml <rocketmq-version>)"
  patterns:
    - "Feign self-loopback (D-P4-01 方案 A): same-process HTTP hop via localhost:8080, no cross-process hop"
    - "RequestInterceptor URI rewrite (D-P4-01a): mutate RequestTemplate.uri() to strip context-path prefix, preserves target"
    - "SpEL-driven FeignClient url: url = \"#{basServerConfig.url}\" resolves via BasClientConfig bean"
key-files:
  created:
    - path: zgbas-system/src/main/java/com/spt/bas/client/config/BasClientConfig.java
      purpose: Verbatim port of source BasClientConfig — produces basServerConfig LocalServerConfig bean
    - path: zgbas-system/src/main/java/com/spt/bas/client/config/BasFeignPathConfig.java
      purpose: New config — basServerPathStripper RequestInterceptor bean (D-P4-01a path-prefix handling)
  modified:
    - path: zgbas-admin/src/main/java/com/spt/ZgbasApplication.java
      change: "@EnableFeignClients basePackages widened to include com.spt.bas.client.remote (D-P4-01 方案 A)"
    - path: zgbas-system/pom.xml
      change: "Added rocketmq-spring-boot-starter:2.2.2 dependency"
    - path: zgbas-admin/src/main/resources/application-dev.yml
      change: "Added spt.bas.server.url + rocketmq.* (additive; preserved pre-existing plaintext secrets per user decision 2026-07-17)"
    - path: zgbas-admin/src/main/resources/application-prod.yml
      change: "Added spt.bas.server.url + rocketmq.* placeholders (D-P2-13 prod force-externalization)"
    - path: zgbas-admin/src/test/java/com/spt/ZgbasApplicationTest.java
      change: "Extended 14 → 19 tests: 4 @Disabled Wave 3/4 placeholders + 1 active feignSelfLoopbackWiring_probe"
decisions:
  - "D-P4-01 方案 A chosen (Feign self-loopback) — widens @EnableFeignClients + spt.bas.server.url=localhost:8080; preserves 224 api verbatim port (no implements I*Client)"
  - "D-P4-01a path prefix handling via RequestInterceptor (BasFeignPathConfig.basServerPathStripper) — chosen over re-setting context-path to preserve Phase 3 AUTH-03 Shiro /login /index root chain"
  - "RocketMQ starter kept on classpath (lazy-connecting per RESEARCH A2) — startup logs confirm broker contact happens after context boot, does not block D-P4-06 startup verification"
  - "Pre-existing plaintext DB/Redis/spt.app.secretKey in application-dev.yml preserved per user decision 2026-07-17 (overrides D-P2-13 for dev) — additive-only yml edit"
metrics:
  duration: ~30 min
  completed: 2026-07-17
  tasks_completed: 2
  files_created: 2
  files_modified: 5
---

# Phase 4 Plan 01: Wave 0 Feign Self-Loopback Wiring + WR-02 Scaffold Summary

D-P4-01 方案 A 落地：放宽 `ZgbasApplication` 的 `@EnableFeignClients` 同时扫描 `com.spt.sign.client.remote` 与 `com.spt.bas.client.remote`，产出 `basServerConfig` bean 让 238 bas 契约的 SpEL `#{basServerConfig.url}` 解析到 `http://localhost:8080`，并用 `BasFeignPathConfig.RequestInterceptor` 在 Feign 出口处剥离 `spt-bas-server/` 前缀（不设单体 context-path 以保 Phase 3 AUTH-03 Shiro 根路径链）；同时落地 `rocketmq-spring-boot-starter:2.2.2` + rocketmq.* yml 占位、扩展 `ZgbasApplicationTest` 建立 WR-02 占位测试与一个 Wave 0 fail-fast probe（三段断言：bean 注册 + Feign proxy 解析 + path 剥离）。

## What Was Built

### 1. `BasClientConfig` — SpEL anchor bean (verbatim port)

`zgbas-system/src/main/java/com/spt/bas/client/config/BasClientConfig.java` — 19-line `@Configuration`照搬 from source `basCore/basClient/.../config/BasClientConfig.java`. Produces `@Bean(BasConstants.SERVER_BEAN_NAME)` (= `"basServerConfig"`) returning a `LocalServerConfig` whose `getUrl()` reads `PropertiesUtil.getProperty("spt.bas.server.url")`. The `@DependsOn({"propertiesUtil"})` matches the source ordering constraint; the `propertiesUtil` bean was already registered by Phase 2's `ToolsCoreConfig`.

### 2. `BasFeignPathConfig` — D-P4-01a path stripper (new)

`zgbas-system/src/main/java/com/spt/bas/client/config/BasFeignPathConfig.java` — new `@Configuration` exposing `@Bean("basServerPathStripper") public RequestInterceptor`. The interceptor reads `template.path()`, finds the first occurrence of the literal `"spt-bas-server/"`, and re-sets the uri via `template.uri(stripped)` (Feign preserves the target host). No-op when the literal is absent (cfca `com.spt.sign.client.remote` clients are unaffected — their paths never contain this prefix). Chosen over re-enabling `server.servlet.context-path=/spt-bas-server` because that would break Phase 3's AUTH-03 Shiro `/login` / `/index` root filter chain.

### 3. `ZgbasApplication` — `@EnableFeignClients` widened

Changed `@EnableFeignClients(basePackages = "com.spt.sign.client.remote")` to a two-element array including `com.spt.bas.client.remote`. `excludeFilters` (both `FeignConfig` singletons) preserved — no top-level singleton conflict. Added a detailed comment explaining that the original D-P2-12 narrowing intent (avoid double-bean) is preserved because the ported `@RestController` endpoints extend `BaseApi<Entity>` and do NOT `implements I*Client` (RESEARCH §D-P4-01: 0/224 implements).

### 4. `zgbas-system/pom.xml` — rocketmq starter

Added `<dependency>org.apache.rocketmq:rocketmq-spring-boot-starter:2.2.2</dependency>` (version verbatim from source `basCore/pom.xml:25` `<rocketmq-version>`). Required by the Wave 2 22-file `basServer/rocketmq/` package and three compile-time entry points (`BasCommandExecutor` / `MQApi` / `WorkTargetApi`). Pinned to 2.2.2 per D-P2-08 (no 2.3.x bump).

### 5. yml configs

**`application-dev.yml` (additive only):** added `spt.bas.server.url: http://localhost:8080` under existing `spt:` block, plus a new top-level `rocketmq:` block (`name-server: 47.104.66.178:9876` verbatim from source `application-dev.properties:22`, `producer.group: contract_producer_group` from `application.properties:66`, AK/SK with dev defaults via `${ROCKETMQ_AK:zgrocketmq}` / `${ROCKETMQ_SK:zg12345678}`). Pre-existing plaintext DB/Redis/`spt.app.secretKey`/auth/shiro values **preserved unchanged** per user decision 2026-07-17 (overrides D-P2-13 for dev).

**`application-prod.yml`:** added `spt.bas.server.url: ${SPT_BAS_SERVER_URL}` (no default — fail-fast if unset) and `rocketmq:` block with `${ROCKETMQ_NAMESERVER}` / `${ROCKETMQ_AK}` / `${ROCKETMQ_SK}` placeholders (no defaults — D-P2-13 prod force-externalization). `rocketmq.producer.group=contract_producer_group` kept literal (not secret).

### 6. `ZgbasApplicationTest` — WR-02 shell + fail-fast probe

Extended the existing 14-test capstone (Phase 2 D-P2-03 + Phase 3 D-P3-13) with 5 new methods:

| Method | Type | Purpose |
|--------|------|---------|
| `basContractEndpointReachable_applyBrand_findAll` | `@Disabled` Wave 3 | WR-02 合同域 HTTP reachability placeholder |
| `basContractEndpointReachable_ctrContract_findPage` | `@Disabled` Wave 3 | WR-02 授信/合同域 |
| `basContractEndpointReachable_stockDetail_findAll` | `@Disabled` Wave 3 | WR-02 库存域 |
| `bffControllersRegistered_sample` | `@Disabled` Wave 4 | BIZ-02 BFF bean 抽样 (3 representative beans) |
| `feignSelfLoopbackWiring_probe` | **ACTIVE** Wave 0 | D-P4-01/01a fail-fast — 3-step assertion |

The probe asserts:
1. `context.containsBean("basServerPathStripper")` — `BasFeignPathConfig` picked up by ComponentScan.
2. `context.getBean(IBsCompanyOurClient.class) != null` — Feign proxy resolves (proves widened `@EnableFeignClients` + `basServerConfig` bean + SpEL `#{basServerConfig.url}` all wire together). `IBsCompanyOurClient` chosen because Phase 2 already inlined it.
3. Construct `RequestTemplate` with `uri("/spt-bas-server/apply/brand/findAll")`, apply the interceptor, assert `template.path()` no longer contains `"spt-bas-server/"` and starts with `"/apply/brand"`. **This is the Wave 0 verification of RESEARCH assumption A3** — that `RequestTemplate.uri(String)` overwrite is a viable mechanism on feign-core 11.10 (Spring Cloud OpenFeign 3.0.x pulled by Spring Boot 2.5.9).

## Deviations from Plan

### Auto-fixed Issues

**1. [Rule 3 - Blocking] Added `-DfailIfNoTests=false` to ZgbasApplicationTest invocation**
- **Found during:** Task 2 verify step
- **Issue:** `mvn -pl zgbas-admin -am test -Dtest=ZgbasApplicationTest` failed with `No tests were executed!` on the `zgbas-common` module (the first reactor project after the parent pom). Surefire 2.22.2 treats "no test matches `-Dtest=...` filter in this module" as a hard failure by default.
- **Fix:** Added `-DfailIfNoTests=false` to the verify command (documented in Task 2 commit message). Modules without matching tests skip cleanly; zgbas-admin (which owns ZgbasApplicationTest) runs the test as expected.
- **Files modified:** none (command-line flag only)
- **Impact on plan:** none — the verify command semantics are unchanged, just an extra flag.
- **Commit:** `ff5928b` (Task 2 commit message body documents the fix)

### Plan Adherence

Otherwise the plan executed exactly as written — no Rule 1/2/4 deviations. All 6 `done` criteria for Task 1 and all 5 `done` criteria for Task 2 met (see Self-Check below).

## Threat Model Adherence

The plan's `<threat_model>` assigned these dispositions — all landed as specified:

| Threat | Disposition | How landed in this plan |
|--------|-------------|-------------------------|
| T-04-01-S (Spoofing, Feign self-loopback) | accept | Self-loopback is `localhost:8080` only; same-process Shiro auth chain (Phase 3) still gates the actual endpoints. No new attack surface. |
| T-04-01-I (Info Disclosure, rocketmq creds) | mitigate | Prod profile uses `${ROCKETMQ_AK}` / `${ROCKETMQ_SK}` / `${ROCKETMQ_NAMESERVER}` placeholders (D-P2-13); dev has plaintext defaults (acceptable per user decision 2026-07-17 — dev environment only). |
| T-04-02-T (Tampering, URL rewrite) | accept | Interceptor only strips the hardcoded literal `"spt-bas-server/"`; no other URL transformation. Scope limited to this process's outbound Feign calls. |
| T-04-SC (Tampering, Maven install) | mitigate | `rocketmq-spring-boot-starter:2.2.2` is Apache-official (`github.com/apache/rocketmq-spring`); version verbatim from source `basCore/pom.xml:25`. Build succeeded with no anomalous plugin activity. |
| T-04-01-D (DoS, recursive self-loopback) | accept | Spring MVC does not re-enter the FeignClient proxy chain — the controller path goes through Spring MVC bean resolution, the proxy path goes through HTTP. No recursion path exists. |

No new threat surface introduced beyond what the plan's threat model anticipated.

## Verification Evidence

### Task 1 — Compile gate

Command:
```
JAVA_HOME=/Users/alan/Library/Java/JavaVirtualMachines/corretto-1.8.0_482/Contents/Home \
/Users/alan/App/apache-maven-3.8.6/bin/mvn -s /Users/alan/App/apache-maven-3.8.6/zg_settings.xml \
-am compile
```

Result: `BUILD SUCCESS`, `ERROR_COUNT=0`, `CANNOT_FIND=0` (locale-independent grep `^\[ERROR]` / `cannot find symbol|找不到符号`). Log: `/tmp/p4-01-t1.log`.

### Task 2 — ZgbasApplicationTest full run

Command (with Rule 3 fix applied):
```
JAVA_HOME=/Users/alan/Library/Java/JavaVirtualMachines/corretto-1.8.0_482/Contents/Home \
/Users/alan/App/apache-maven-3.8.6/bin/mvn -s /Users/alan/App/apache-maven-3.8.6/zg_settings.xml \
-pl zgbas-admin -am test -Dtest=ZgbasApplicationTest -DfailIfNoTests=false
```

Result: `Tests run: 19, Failures: 0, Errors: 0, Skipped: 4` — 14 Phase 2/3 tests still GREEN (no regression), 4 Wave 3/4 placeholders skipped (expected — `@Disabled`), `feignSelfLoopbackWiring_probe` GREEN (3-step assertion passed). Log: `/tmp/p4-01-t2.log`.

Startup trace confirms RESEARCH A2 (rocketmq starter lazy-connect): broker `47.104.66.178:9876` contacted, then `closeChannel` fires at shutdown — producer init does not block context boot.

### RESEARCH A3 verified in Wave 0

The `feignSelfLoopbackWiring_probe` step (3) directly asserts that `RequestTemplate.uri(String)` overwrite works as documented on feign-core 11.10 (the version pulled by Spring Cloud OpenFeign 3.0.x for Spring Boot 2.5.9). The plan-checker WARNING-2 ("don't defer path-strip first-verification to Wave 4") is satisfied — no Wave 4 surprise possible on this axis.

## Commits

| Hash | Message |
|------|---------|
| `960a1d5` | `feat(04-01): D-P4-01 方案 A Feign self-loopback wiring + D-P4-01a path prefix + rocketmq starter` |
| `ff5928b` | `test(04-01): WR-02 scaffold + D-P4-01/01a fail-fast probe in ZgbasApplicationTest` |

## Known Stubs

None. This plan introduces no business stubs — `BasFeignPathConfig.basServerPathStripper` is real production logic (not a stub), and the 4 `@Disabled` test placeholders are test-shell scaffolds (not production stubs). The `feignSelfLoopbackWiring_probe` test exercises the real interceptor.

The Wave 3/4 placeholder tests will activate by removing `@Disabled` once the corresponding waves port the real `@RestController` endpoints and BFF controllers; this is documented in each test's `@Disabled("reason")` string and in the plan's `tasks > task 2 > action`.

## Threat Flags

None. No new network endpoints, auth paths, file access patterns, or trust-boundary schema changes introduced beyond what the plan's `<threat_model>` anticipated.

## Self-Check: PASSED

**Files created:**
- `FOUND: zgbas-system/src/main/java/com/spt/bas/client/config/BasClientConfig.java`
- `FOUND: zgbas-system/src/main/java/com/spt/bas/client/config/BasFeignPathConfig.java`

**Files modified:**
- `FOUND: zgbas-admin/src/main/java/com/spt/ZgbasApplication.java` (grep `com.spt.bas.client.remote` → 2 hits)
- `FOUND: zgbas-system/pom.xml` (grep `rocketmq-spring-boot-starter` → 2 hits)
- `FOUND: zgbas-admin/src/main/resources/application-dev.yml` (grep `spt.bas.server.url|http://localhost:8080|rocketmq` → 4 hits)
- `FOUND: zgbas-admin/src/main/resources/application-prod.yml` (grep `ROCKETMQ_NAMESERVER|ROCKETMQ_AK|ROCKETMQ_SK|SPT_BAS_SERVER_URL` → 5 hits)
- `FOUND: zgbas-admin/src/test/java/com/spt/ZgbasApplicationTest.java` (grep `void (basContractEndpointReachable|bffControllersRegistered|feignSelfLoopbackWiring_probe)` → 5 hits)

**Commits:**
- `FOUND: 960a1d5` (git log)
- `FOUND: ff5928b` (git log)

**Plan done criteria (Task 1):**
- `com.spt.bas.client.remote` ≥ 1 in ZgbasApplication.java ✓ (2 hits)
- BasClientConfig.java contains `BasConstants.SERVER_BEAN_NAME` ✓
- BasFeignPathConfig.java contains `RequestInterceptor` ✓
- pom contains `rocketmq-spring-boot-starter` version 2.2.2 ✓
- dev yml contains `spt.bas.server.url: http://localhost:8080` + rocketmq.* 4 items ✓
- prod yml contains `${ROCKETMQ_NAMESERVER}` / `${ROCKETMQ_AK}` / `${ROCKETMQ_SK}` + `${SPT_BAS_SERVER_URL}` ✓
- `mvn -am compile` ERROR_COUNT=0, CANNOT_FIND=0 ✓
- D-P4-01a Wave 0 fail-fast via Task 2 probe ✓
- Phase 3 14-test no regression ✓ (Task 2 closure)

**Plan done criteria (Task 2):**
- 5 new test methods (3 endpoint reachability + 1 BFF + 1 probe) ✓
- 4 business placeholders `@Disabled`, probe NOT `@Disabled` ✓
- Probe 3-step assertion passes (bean + Feign proxy + path strip) ✓
- 14 prior tests still green ✓
- `mvn test -Dtest=ZgbasApplicationTest` ERROR_COUNT=0, TEST_FAIL=0 ✓
