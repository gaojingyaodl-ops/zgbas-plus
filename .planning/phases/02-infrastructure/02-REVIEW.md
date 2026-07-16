---
phase: 02-infrastructure
reviewed: 2026-07-16T00:00:00Z
depth: standard
files_reviewed: 11
files_reviewed_list:
  - zgbas-framework/src/main/java/com/spt/framework/config/ZgbasDataSourceConfig.java
  - zgbas-framework/src/main/java/com/spt/framework/config/ZgbasMybatisConfig.java
  - zgbas-framework/src/main/java/com/spt/framework/config/ZgbasExternalBeansConfig.java
  - zgbas-admin/src/main/java/com/spt/ZgbasApplication.java
  - zgbas-admin/src/main/java/com/spt/proof/InProcessContract.java
  - zgbas-admin/src/main/java/com/spt/proof/InProcessContractImpl.java
  - zgbas-admin/src/test/java/com/spt/proof/InProcessContractTest.java
  - zgbas-admin/src/test/java/com/spt/ZgbasApplicationTest.java
  - zgbas-admin/src/main/resources/application.yml
  - zgbas-admin/src/main/resources/application-dev.yml
  - zgbas-admin/src/main/resources/application-prod.yml
findings:
  critical: 1
  warning: 3
  info: 6
  total: 10
status: issues_found
---

# Phase 2: Code Review Report

**Reviewed:** 2026-07-16T00:00:00Z
**Depth:** standard
**Files Reviewed:** 11 (authored wiring/config/test surface only; the ~837 verbatim-copied spt-tools/entity/Dao ports are out of scope by design)
**Status:** issues_found

## Summary

The Phase-2 infrastructure wiring is, on the whole, sound: the dual-ORM `@Primary` DataSource resolution is robust across all bean-registration orderings (it leans correctly on `allow-bean-definition-overriding` + `@Primary`), the JPA/mybatis-plus transaction-manager sharing is correct, the prod YAML externalizes every secret via fail-fast `${VAR}` placeholders, and the T-P2-log security gate **holds** — `ZgbasExternalBeansConfig` contains no `logger.info(...secretKey,appCode...)` line and no logger field at all, so no SDK secret is logged at any level.

That said, the review surfaced one genuine security blocker (plaintext credentials in VCS pointing at a publicly-reachable database), three maintainability/correctness-confidence warnings (a duplicated `@MapperScan` that will drift, a "proof" test that does not actually prove the HTTP route mapping it claims to, and a default profile that silently routes prod misconfigurations to the dev database), and several lower-severity quality notes.

Per phase_context, the following are treated as accepted/intentional and are NOT re-flagged as defects: `ddl-auto: none` (deferred entity/schema drift debt for Phase 4), `allow-bean-definition-overriding: true` (11 sign-client `@FeignClient` name collision), and the `@ComponentScan` excludeFilters for the two colliding `feignConfig` beans.

## Critical Issues

### CR-01: Plaintext dev credentials committed for a publicly-reachable database

**File:** `zgbas-admin/src/main/resources/application-dev.yml:8-10, 27-29`
**Issue:** Real secrets are hardcoded in source-controlled YAML:
- DB password `rjni^xL3Q88p6uC#ZEwK` for `sptbaspduser` against `jdbc:mysql://47.104.15.98:3306/sptbasdb_pd` — a **public IP** MySQL endpoint, with `useSSL=false`, so credentials and query traffic transit in cleartext over the public internet.
- The shared auth-signing secret `spt.app.secretKey: spt_secretkey_171212`, fed into `init(secretKey, appCode, url)` for all three external SDK beans (`FileRemote`, `AuthOpenFacade`, `PushClientHttp`) — i.e. a credential used to authenticate against the auth/push/file services.

This directly violates the project's own security rules ("Never hardcode API keys, tokens, or credentials in source code") and the Java security rule. The inline comment ("already in source git history") acknowledges the prior leak but re-committing the value perpetuates the exposure and broadens the blast radius to the new repo. A dev database at a public IP is still a real target: dev DBs frequently hold scrubbed copies of production data and are routinely scanned. Note `.gitignore` has **no** `application-local*` rule, so a local-override pattern is not currently protected either.

Prod is correctly externalized (`${DB_PASSWORD}`, `${SPT_APP_SECRET}`, fail-fast) — the defect is scoped to the dev profile, but it is a real, exploitable credential leak, not a style issue.

**Fix:** Externalize even dev secrets and rotate the exposed values.
```yaml
# application-dev.yml — no plaintext secrets checked in
spring:
  datasource:
    druid:
      # same URL, but force TLS for any non-localhost target
      url: ${DB_URL_DEV:jdbc:mysql://47.104.15.98:3306/sptbasdb_pd?serverTimezone=GMT%2B8&useUnicode=true&characterEncoding=UTF-8&useSSL=true&verifyServerCertificate=false}
      username: ${DB_USERNAME_DEV:sptbaspduser}
      password: ${DB_PASSWORD_DEV:}            # NO plaintext default; supply via env or application-local.yml
spt:
  app:
    secretKey: ${SPT_APP_SECRET_DEV:}
    appCode: ${SPT_APP_CODE_DEV:zgbas}
```
```gitignore
# .gitignore
**/application-local.yml
**/application-local*.yml
```
Then (1) rotate `rjni^xL3Q88p6uC#ZEwK` and `spt_secretkey_171212` on the dev/auth systems, and (2) purge them from both repos' history (e.g. `git filter-repo`) since they are already exposed. If the team explicitly accepts dev-creds-as-debt, record that as a waived BLOCKER in `02-CONTEXT.md` rather than silently shipping.

## Warnings

### WR-01: Duplicate `@MapperScan` on two classes — drift is inevitable

**File:** `zgbas-admin/src/main/java/com/spt/ZgbasApplication.java:71-72` and `zgbas-framework/src/main/java/com/spt/framework/config/ZgbasMybatisConfig.java:24`
**Issue:** Both the boot class and `ZgbasMybatisConfig` declare the *identical* scan:
```java
@MapperScan(basePackages = "com.spt.bas.system.dao", annotationClass = MyBatisDao.class)
```
Confirmed via grep — two `@MapperScan` annotations resolving to the same package + marker. MyBatis-Plus deduplicates the second registration (logs "already registered"), so this is not a startup failure today. The defect is maintainability: the mapper package is now defined in two places with no link between them. When Plan 05/Phase 5 widens the mapper surface (e.g. report mappers), one site will be updated and the other forgotten, producing a silent scan gap or a stale scan. This is exactly the "configuration drift" class of bug.

**Fix:** Keep the scan in the mybatis-owned config class (cohesion: data config owns mapper wiring) and remove it from the boot class.
```java
// ZgbasApplication.java — remove these two lines; ZgbasMybatisConfig owns mapper scanning
- @MapperScan(basePackages = "com.spt.bas.system.dao",
-             annotationClass = MyBatisDao.class)
```
Leave `ZgbasMybatisConfig.@MapperScan` as the single source of truth.

### WR-02: `InProcessContractTest` does not verify the HTTP route mapping it claims to prove

**File:** `zgbas-admin/src/test/java/com/spt/proof/InProcessContractTest.java:17-26`
**Issue:** The Javadoc claims the test proves "D-P2-10 … Spring MVC honors the interface `@GetMapping`," and `InProcessContract`'s own Javadoc restates that MVC honors the interface-level mapping. But the test only does:
```java
assertThat(contract.echo("hi")).isEqualTo("echo:hi");
```
This is a **direct Java method call on the autowired bean** — it proves the bean wires up and the method returns the right value. It does **not** prove `GET /proof/echo?msg=hi` is actually mapped. grep confirms the entire test tree uses no `MockMvc`, `TestRestTemplate`, or `/proof/echo` request. So the load-bearing question for Phase 4 (does an interface-declared `@GetMapping` get honored when a `@RestController` impl omits it?) is **untested** — and interface-level MVC annotations are a well-known historical gotcha (they work via `AnnotatedElementUtils` traversal, but only a real HTTP request proves the version in play does so). If this regresses, this test still passes and Phase 4 discovers it late, across 295 clients.

**Fix:** Add a real HTTP assertion. With `RANDOM_PORT` + `TestRestTemplate`:
```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class InProcessContractTest {
    @Autowired private InProcessContract contract;
    @Autowired private TestRestTemplate http;

    @Test
    void localImplSatisfiesContract() {
        assertThat(contract.echo("hi")).isEqualTo("echo:hi");
    }

    @Test
    void interfaceGetMappingIsHonoredOverHttp() {   // the actual D-P2-10 proof
        ResponseEntity<String> resp = http.getForEntity("/proof/echo?msg=hi", String.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody()).isEqualTo("echo:hi");
    }
}
```
This converts the proof from asserted-by-fiat to asserted-by-behavior.

### WR-03: Default active profile `dev` baked into shared `application.yml` — silent prod→dev fallback

**File:** `zgbas-admin/src/main/resources/application.yml:13-14`
**Issue:** `spring.profiles.active: dev` is hardcoded in the **shared** config. The monolith's stated core value is "one jar, single process, runs everything." If a production deployment simply forgets `SPRING_PROFILES_ACTIVE=prod` (or an orchestrator drops the env var), the app starts "successfully," loads the `dev` profile, and connects to the dev database at the public IP `47.104.15.98` with the plaintext dev credentials (CR-01). The failure mode is silent and looks green — wrong data, wrong environment, no exception. This directly amplifies CR-01's blast radius.

**Fix:** Do not bake a default profile into the shared YAML; require it explicitly, or default to a safe value.
```yaml
# application.yml — remove the default; force operators to declare the profile
spring:
  application:
    name: zgbas-plus
  # profiles.active omitted — set SPRING_PROFILES_ACTIVE=dev|prod at deploy time.
  # Spring Boot fails fast with "no active profile" guidance if unset in newer versions;
  # if you want a hard guarantee, add an EnvironmentPostProcessor that rejects startup
  # when no profile is active.
```
Alternatively default to `prod` (safe-fail) and require `dev` to be opted into locally. Either way, `dev` should not be the implicit default for a deployable artifact.

## Info

### IN-01: Field injection of `Environment` in `ZgbasExternalBeansConfig`

**File:** `zgbas-framework/src/main/java/com/spt/framework/config/ZgbasExternalBeansConfig.java:26-27`
**Issue:** `@Autowired private Environment env;` is field injection. The Java coding rules mandate constructor injection ("never field injection"). For `Environment` in a `@Configuration` this is low-risk (Environment is always resolved before `@Bean` methods run), but it diverges from the stated convention and makes the class marginally harder to unit-test.
**Fix:** Inject via constructor (`private final Environment env;` + constructor), or implement `EnvironmentAware`.

### IN-02: Triplicated external-SDK bean wiring (DRY)

**File:** `zgbas-framework/src/main/java/com/spt/framework/config/ZgbasExternalBeansConfig.java:29-54`
**Issue:** `fileRemote()`, `authOpenFacade()`, `pushClientHttp()` are near-identical: each constructs an instance and calls `init(env.getProperty("spt.app.secretKey"), env.getProperty("spt.app.appCode"), env.getProperty("<service>.url"))`. The two `env.getProperty("spt.app.*")` lookups are repeated three times. The three SDK types share no common `init` signature, so a generic helper isn't free — but the repeated key lookups are trivially extractable.
**Fix:** Read `secretKey`/`appCode` once into local vars (or a small `record SdkKeys(String secretKey, String appCode)`), and consider a private helper per SDK family. Minor payoff; primarily reduces the chance of one bean drifting (e.g. someone changes the key for one but not the others).

### IN-03: Hibernate statistics + 2nd-level/query cache enabled in shared (prod-applied) config

**File:** `zgbas-admin/src/main/resources/application.yml:27-36`
**Issue:** `generate_statistics: true`, `use_second_level_cache: true`, and `use_query_cache: true` live in the shared YAML and therefore apply to prod. Statistics collection adds overhead to every transaction and can leak operational detail if surfaced; the query cache is a classic source of stale reads/memory pressure when entity `@Cache`/query hints aren't perfectly aligned. The comment marks these "Discretion: copy source behavior," so this is debt-aware — flagging only so the decision is visible.
**Fix:** Consider moving `generate_statistics` (and possibly query-cache) into a dev-only `application-dev.yml` and off by default in prod, unless the source genuinely required it for correctness. No correctness bug — informational.

### IN-04: `PaginationInterceptor` is deprecated in Mybatis-Plus ≥ 3.4

**File:** `zgbas-framework/src/main/java/com/spt/framework/config/ZgbasMybatisConfig.java:3,33`
**Issue:** `com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor` was deprecated in favor of `MybatisPlusInterceptor` (+ `PaginationInnerInterceptor`) in Mybatis-Plus 3.4 and is removed in later 3.x lines. This mirrors the source wiring (copy-source, locked stack), so it compiles against the pinned version today.
**Fix:** None required for Phase 2 (behavior-equivalence + locked version). When the version is ever bumped, replace with `MybatisPlusInterceptor` + `PaginationInnerInterceptor`. Note it here so the migration is on record.

### IN-05: Druid `filters: stat` enabled in prod

**File:** `zgbas-admin/src/main/resources/application-prod.yml:16` (and dev `application-dev.yml:23`)
**Issue:** `filters: stat` activates Druid's stat filter in prod. The in-process filter alone does not expose HTTP, but if a Druid stat-view servlet is registered later (or already by an auto-config), `/druid/*` can leak SQL text and invocation counts. This intersects with Phase 3 (Shiro) auth coverage.
**Fix:** Confirm no `StatViewServlet` is registered in prod, or gate it behind authentication when Phase 3 lands. No action needed for Phase 2 — informational.

### IN-06: EntityScan / Javadoc drift

**File:** `zgbas-admin/src/main/java/com/spt/ZgbasApplication.java:68-70` (and Javadoc lines 26-33), `zgbas-admin/src/test/java/com/spt/ZgbasApplicationTest.java:20-22`
**Issue:** The actual `@EntityScan` is `basePackages = {"com.spt.bas.client.entity", "com.spt.pm.entity"}` (plus `com.spt.tools.jpa.vo` via `basePackageClasses = IdEntity.class`), but the Javadoc only narrates `com.spt.bas.client.entity` and "239 entities." `com.spt.pm.entity` is undocumented. Similarly the test Javadoc says "239 entities validated" while the scan covers three packages. Not a bug — the scan is likely correct — but the comments understate what is scanned and will mislead future readers.
**Fix:** Update the Javadoc to enumerate all three scanned packages and reconcile the entity count, or drop the hard-coded "239" count from the comments so it doesn't rot.

---

_Reviewed: 2026-07-16T00:00:00Z_
_Reviewer: Claude (gsd-code-reviewer)_
_Depth: standard_
