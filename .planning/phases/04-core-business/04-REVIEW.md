---
phase: 04-core-business
reviewed: 2026-07-17T00:00:00Z
depth: standard
files_reviewed: 9
files_reviewed_list:
  - zgbas-admin/src/main/java/com/spt/ZgbasApplication.java
  - zgbas-system/src/main/java/com/spt/bas/client/config/BasClientConfig.java
  - zgbas-system/src/main/java/com/spt/bas/client/config/BasFeignPathConfig.java
  - zgbas-admin/src/test/java/com/spt/ZgbasApplicationTest.java
  - zgbas-system/pom.xml
  - zgbas-common/src/main/java/com/spt/tools/http/util/TokenUtil.java
  - zgbas-system/src/main/java/com/spt/bas/server/config/BasicErrorController.java
  - zgbas-admin/src/main/resources/application-dev.yml
  - zgbas-admin/src/main/resources/application-prod.yml
findings:
  critical: 1
  warning: 3
  info: 2
  total: 6
status: issues_found
---

# Phase 04: Code Review Report

**Reviewed:** 2026-07-17
**Depth:** standard
**Files Reviewed:** 9
**Status:** issues_found

## Summary

Reviewed the 9 hand-touched files for Phase 4 (microservices → monolith migration). The D-P4-01 Feign self-loopback wiring is coherent: `BasClientConfig` → `basServerConfig` bean → SpEL `#{basServerConfig.url}` → `spt.bas.server.url=http://localhost:8080`, and the Wave 5 rewrite of `BasFeignPathConfig` as a `WebMvcConfigurer.addPathPrefix` is sound (api packages get `/spt-bas-server`, BFF packages stay at root). `ZgbasApplicationTest` reachability probes are meaningful (2xx/3xx/401 acceptance is justified by Shiro `user` filter behaviour, not tautological). `BasClientConfig`, `TokenUtil` overload, and `pom.xml` constraint compliance (#9 nacos-common removed, #10 xxl-job-core present as compile-only jar) all check out.

One Critical defect: **`application-prod.yml` omits 7 keys that are `@Value`-injected without defaults by ported service impls** — prod context boot will fail-fast with `IllegalArgumentException: Could not resolve placeholder`. Three Warnings are documentation/import hygiene defects (stale "stripper" references in Javadoc, unused `Date` import after the `LocalDateTime.now()` fix). Two Info items are minor style nits.

Dev-yml plaintext secrets are out of scope per the 2026-07-17 user decision (D-P2-13 override) — not flagged.

## Critical Issues

### CR-01: Prod profile missing 7 `@Value`-resolved keys — prod context boot will fail-fast

**File:** `zgbas-admin/src/main/resources/application-prod.yml` (whole file)
**Affected consumers (verified via grep):**
- `zgbas-system/.../service/impl/ApplyEntrustServiceImpl.java:118` — `@Value("${cms.server.url}")`
- `zgbas-system/.../service/impl/ApplyCompanyLicenseImpl.java:56` — `@Value("${share.key}")`
- `zgbas-system/.../service/impl/ApplyImportServiceImpl.java:64` — `@Value("${credit.contract.switch}")`
- `zgbas-system/.../service/impl/ApplyPresellServiceImpl.java:57` — `@Value("${credit.contract.switch}")`
- `zgbas-system/.../service/impl/ApplySellServiceImpl.java:92` — `@Value("${credit.contract.switch}")`
- `zgbas-system/.../service/impl/BsCompanyDownloadService.java:56` — `@Value("${zip.file.directory}")`
- `zgbas-system/.../service/impl/CtrContractDownloadService.java:79` — `@Value("${zip.file.directory}")`
- `zgbas-system/.../service/impl/PushContractServiceImpl.java:68` — `@Value("${picc.contract.switch}")`
- `zgbas-system/.../service/impl/BaiduMapApiServiceImpl.java:24` — `@Value("${baidu.map.ak}")`
- `zgbas-system/.../service/impl/ApplyChargeSalesServiceImpl.java:154` — `@Value("${basTrade.server.approveCallBackUrl}")`

**Issue:** `application-dev.yml` defines all 7 keys (`baidu.map.ak`, `basTrade.server.approveCallBackUrl`, `cms.server.url`, `credit.contract.switch`, `picc.contract.switch`, `share.key`, `zip.file.directory`), but `application-prod.yml` defines none of them. Every consumer above uses the no-default form `${key}` (not `${key:fallback}`), so under the `prod` profile Spring's `PropertySourcesPlaceholderConfigurer` throws

```
java.lang.IllegalArgumentException:
  Could not resolve placeholder 'cms.server.url' in value "${cms.server.url}"
```

at context refresh. The prod profile therefore cannot start — directly violating PROJECT.md's core value ("single-process boot runs all supply-chain business"). This bug is invisible to `ZgbasApplicationTest` because that test forces `@ActiveProfiles("dev")`, masking the prod gap.

The prod-yml comment at line 82 ("BFF controller @Value placeholders (D-P2-13 prod externalization)") claims the prod side has been externalised, but the externalisation skipped these 7 keys — they were added to dev yml verbatim from source `basServer/application.properties` literals and never promoted to prod.

**Fix:** Add the missing keys to `application-prod.yml`. Externalise env-specific values; inline literals for truly env-invariant ones. Example:

```yaml
# ---- Add to application-prod.yml ----
baidu:
  map:
    ak: ${BAIDU_MAP_AK}            # third-party key, env-specific
basTrade:
  server:
    approveCallBackUrl: ${BASTRADE_APPROVE_CALLBACK_URL}
cms:
  server:
    url: ${CMS_SERVER_URL}
credit:
  contract:
    switch: ${CREDIT_CONTRACT_SWITCH:true}    # feature flag, safe default
picc:
  contract:
    switch: ${PICC_CONTRACT_SWITCH:false}     # feature flag, off in prod
share:
  key: shareUrl                              # literal label, env-invariant (matches dev)
zip:
  file:
    directory: ${ZIP_FILE_DIRECTORY}          # filesystem path, deploy-specific
```

Then verify via `mvn test -Dspring.profiles.active=prod` (or equivalent boot smoke test under prod profile) that no further `Could not resolve placeholder` errors remain.

## Warnings

### WR-01: Stale "basServerPathStripper RequestInterceptor" inline comment in wiring hub

**File:** `zgbas-admin/src/main/java/com/spt/ZgbasApplication.java:109-110`
**Issue:** The inline comment on `@EnableFeignClients` still says:

```
// ... is resolved by BasFeignPathConfig's
// basServerPathStripper RequestInterceptor (D-P4-01a).
```

This contradicts the Wave 5 rewrite documented in `BasFeignPathConfig.java` (now a `WebMvcConfigurer.addPathPrefix`, no longer a `RequestInterceptor` stripper). The class-level Javadoc on the same file (lines 93-104) was updated correctly, but this trailing inline reference was missed. A future maintainer debugging Feign path behaviour will be misled into looking for a `RequestInterceptor` bean that does not exist.

**Fix:**
```java
// ... path-prefix discrepancy between the source context-path
// (/spt-bas-server) and the monolith root (/) is resolved by
// BasFeignPathConfig's WebMvcConfigurer.addPathPrefix (D-P4-01a Wave 5
// rewrite — formerly a RequestInterceptor stripper, replaced after the
// Wave 4 AmbiguousMappingException).
```

### WR-02: Stale Javadoc in `feignSelfLoopbackWiring_probe` describes tests that no longer exist

**File:** `zgbas-admin/src/test/java/com/spt/ZgbasApplicationTest.java:38-48` and `:219-221`
**Issue:** The Javadoc paragraph above `feignSelfLoopbackWiring_probe()` claims the test verifies "three things", specifically:

> (1) the `basServerPathStripper` `RequestInterceptor` bean registers; ... (3) the interceptor strips the literal `"spt-bas-server/"` prefix from a constructed `RequestTemplate` (proves RESEARCH A3 ...).

But the actual test body (lines 222-235) only performs two assertions: (1) `BasFeignPathConfig` bean is present, (2) `IBsCompanyOurClient` Feign proxy resolves. There is no `RequestInterceptor` bean lookup and no `RequestTemplate` strip assertion — those were removed when the Wave 5 rewrite replaced the stripper with `addPathPrefix`. The line-219 inline comment "the path-prefix approach ... replaced the Wave 0 path-stripper" is correct, but the lines 38-48 Javadoc paragraph above the method-level doc still describes the old three-check shape.

This makes the test's documented contract diverge from its actual behaviour, which is misleading for future reviewers and for anyone auditing D-P4-01a coverage.

**Fix:** Rewrite the method-level Javadoc (lines 38-48) to match the actual two assertions:

```java
 * <p>The fifth — {@link #feignSelfLoopbackWiring_probe()} — runs in Wave 0
 * as a fail-fast gate for the D-P4-01 self-loopback wiring and the
 * D-P4-01a path-prefix mechanism. It verifies two things: (1) the
 * {@link com.spt.bas.client.config.BasFeignPathConfig} WebMvcConfigurer
 * bean registers (proves the @Configuration is picked up by the
 * {@code com.spt} ComponentScan and {@code configurePathMatch} engages);
 * (2) the {@link IBsCompanyOurClient} Feign proxy resolves (proves the
 * widened {@code @EnableFeignClients} basePackages + the
 * {@code basServerConfig} bean + SpEL {@code "#{basServerConfig.url}"}
 * all work together).
```

### WR-03: Unused `java.util.Date` import left behind after `LocalDateTime` fix

**File:** `zgbas-system/src/main/java/com/spt/bas/server/config/BasicErrorController.java:4`
**Issue:** Line 111 was correctly changed from `new Date()` to `LocalDateTime.now()`, but the `import java.util.Date;` on line 4 was not removed. A grep of the file confirms `Date` is no longer referenced anywhere in the class body. This is dead code left by an incomplete refactor and will surface as a warning under any IDE/Checkstyle "unused imports" rule.

**Fix:** Delete line 4:
```java
// remove this line:
import java.util.Date;
```

## Info

### IN-01: Inconsistent HTTP-status assertion style in reachability tests

**File:** `zgbas-admin/src/test/java/com/spt/ZgbasApplicationTest.java:172, 180, 189, 200`
**Issue:** The four Phase 4 reachability tests use a mixed style for the 401 check:

```java
assertThat(response.getStatusCode().is2xxSuccessful()
    || response.getStatusCode().is3xxRedirection()
    || response.getStatusCodeValue() == 401).isTrue();
```

This mixes the typed `HttpStatus` predicate methods (`is2xxSuccessful`, `is3xxRedirection`) with a raw int comparison (`getStatusCodeValue() == 401`). On Boot 2.5.9 / Spring 5.x this is functional (and `getStatusCodeValue()` is not yet deprecated), but the inconsistency obscures intent. A reader has to translate `401` back to `HttpStatus.UNAUTHORIZED`.

**Fix:**
```java
HttpStatus status = response.getStatusCode();
assertThat(status.is2xxSuccessful()
    || status.is3xxRedirection()
    || status == HttpStatus.UNAUTHORIZED).isTrue();
```

Consistent enum-only comparisons, single `getStatusCode()` call per assertion.

### IN-02: `BasFeignPathConfig.API_PATH_PREFIX` declared `public` but unused externally

**File:** `zgbas-system/src/main/java/com/spt/bas/client/config/BasFeignPathConfig.java:51`
**Issue:** `public static final String API_PATH_PREFIX = "/spt-bas-server";` is referenced only inside its own class (verified via grep — sole hit is the declaration site and the `addPathPrefix(API_PATH_PREFIX, ...)` call two lines below). No other file in the codebase consumes it. The `public` modifier is therefore unnecessary surface area.

**Fix:** Reduce visibility to `private` (constant is only used inside the config class):

```java
private static final String API_PATH_PREFIX = "/spt-bas-server";
```

If a future caller genuinely needs the value (e.g. a test building expected URLs), it can be re-widened at that point — YAGNI applies now.

---

_Reviewed: 2026-07-17_
_Reviewer: Claude (gsd-code-reviewer)_
_Depth: standard_
