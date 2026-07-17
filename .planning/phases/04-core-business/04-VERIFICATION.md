---
phase: 04-core-business
verified: 2026-07-17T14:30:00Z
status: human_needed
score: 3/3 must-haves verified at code level (runtime confirmation routed to human)
overrides_applied: 0
human_verification:
  - test: "Run ZgbasApplicationTest with DB_PASSWORD + SPT_APP_SECRET exported (non-hermetic D-P3-13 Option 4) — confirm 20 run / 0 fail / 0 err / 0 skip"
    expected: "All 20 @Test methods green: contextLoads + Phase2 infra (DataSource/JPA/sampleMapper/externalSdk) + Phase3 Shiro (securityManager/shiroFilter/shiroDbRealm/Login/Index/SSO + 3 endpoint reachability) + Phase4 WR-02 (4 basContract endpoint reachability 2xx/3xx/401 + bffControllersRegistered_sample 4 beans + feignSelfLoopbackWiring_probe 2 assertions). 0 NoSuchBeanDefinitionException proves all 238 I*Client Feign proxies resolve."
    why_human: "Test forces @ActiveProfiles(dev) which boots the FULL Spring context against the real dev DB (47.104.15.98:3306/sptbasdb_pd) + real external spt-auth over HTTP. DB_PASSWORD and SPT_APP_SECRET are UNSET in the verifier environment. Memory note (project_zgbas-plus-nonhermetic-startup-test) explicitly warns the executor-reported green can be a false positive if credentials were inline-exported, so an independent re-run by someone with credentials is required post-merge."
  - test: "Confirm WR-02 HTTP reachability over a live port — the 4 basContractEndpointReachable_* tests (apply/brand/findAll, ctr/contract/findPage, stock/stockContract/findPage, ctr/loading/findPage) return 2xx/3xx/401 via TestRestTemplate"
    expected: "Each business domain endpoint (合同/授信/库存/放款) is reachable over HTTP — proves the full D-P4-01 方案 A self-loopback circuit works at runtime: BFF @Autowired I*Client (Feign proxy) → localhost:8080 → BasFeignPathConfig path-prefix → api @RestController extends BaseApi handler. This is the only evidence that the self-loopback actually serves traffic, not just that it wires."
    why_human: "Requires the running embedded Tomcat from @SpringBootTest(RANDOM_PORT), which cannot start without the DB credentials above. Structural wiring is fully verified (see Key Link table) but the HTTP-level proof needs the live context."
---

# Phase 4: 核心业务迁移 Verification Report

**Phase Goal:** 合同/授信/库存/放款等核心供应链业务（源 basServer JPA）迁入 zgbas-system，业务 Controller / BFF（源 web）迁入 zgbas-admin，业务间调用改为同进程直调 — 核心业务在单体内端到端可用
**Verified:** 2026-07-17T14:30:00Z
**Status:** human_needed
**Re-verification:** No — initial verification

## Goal Achievement

> Goal-backward stance: start from the outcome, verify it actually exists in the code. SUMMARY claims treated as claims, not evidence. All structural checks below were re-run independently by the verifier; the runtime dimension is routed to human because the capstone startup test is non-hermetic by design (D-P3-13 Option 4) and the DB/SPT_APP_SECRET credentials were UNSET in the verifier environment.

### Observable Truths

| # | Truth | Status | Evidence |
|---|-------|--------|----------|
| 1 | 核心业务实体与 Service（源 basServer JPA）迁入 zgbas-system，JPA 增删改查可用（BIZ-01） | ✓ VERIFIED (code) / runtime → human | **Migration complete + compile green.** zgbas-system holds 241 service interfaces + 248 impl + 44 domain subpkg files (ctr 9 / logistics 6 / performance 4 / rt 1 / stock 24) = 533 service-layer files at `com/spt/bas/server/{service,ctr,logistics,performance,rt,stock}`. Infra ported: cache 7, util 34, enums 2, annotation 1, filter 6, listener 1, event 1, rocketmq 14, config 6. PM module absorbed (107 files incl 13 api at `com/spt/pm`). `mvn -am compile` independently re-run = **BUILD SUCCESS / 0 ERROR / 0 cannot-find-symbol**. ApplyBrandServiceImpl substantive (55 lines, 5 @Override/return). Entities(239)+Dao(229) from Phase 2 PERSIST-01 in place. **Runtime JPA CRUD confirmation routed to human (startup test).** |
| 2 | 业务 Controller / BFF（源 web）迁入 zgbas-admin，核心业务 HTTP 接口可访问（BIZ-02） | ✓ VERIFIED (code) / runtime → human | **267 BFF controllers** at `zgbas-admin/com/spt/bas/web/controller/` (incl. apply/ctr/stock subdirs). 237 BFF reference `I*Client`. Phase 3 assets protected: LoginController (`/login`) + IndexController (`@GetMapping("/index")`, file dated Jul 16 = Phase 3, not overwritten). All 4 WR-02 endpoints map to REAL ported controllers (see Key Links). `mvn test-compile` = 0 ERROR (WR-02 test compiles). **Runtime HTTP accessibility routed to human.** |
| 3 | 业务间原 Feign 调用改为同进程 Feign 自回环（D-P4-01 方案 A），行为等价（BIZ-03） | ✓ VERIFIED (code) / runtime → human | **Self-loopback SpEL chain fully wired end-to-end.** @EnableFeignClients widened to `com.spt.bas.client.remote`; BasClientConfig produces `basServerConfig` LocalServerConfig bean; BasConstants.SERVER_URL=`"#{basServerConfig.url}"`; dev yml `spt.bas.server.url=http://localhost:8080`; I*Client uses `BasConstants.SERVER_URL` (1 hit on IApplyBrandClient). **D-P4-01 KEY CONSTRAINT HELD: 0/223 api `implements I*Client`** + 215 `extends BaseApi` + 223 `@RestController` (loopback target). 239 remote contracts = @FeignClient proxy source. Self-loopback circuit verified: BFF `/apply/brand/findAll` → IApplyBrandClient proxy → localhost:8080 → ApplyBrandApi extends BaseApi. **Runtime HTTP proof routed to human.** |

**Score:** 3/3 truths verified at the code/structural level. The runtime dimension of all three (context boot, HTTP reachability, self-loopback serving traffic) is the single human verification item below — it cannot be split out because one `@SpringBootTest` run proves all three at once.

### Required Artifacts

| Artifact | Expected | Status | Details |
|----------|----------|--------|---------|
| `zgbas-system/.../bas/server/service/**` | 241 iface + 248 impl + 44 domain = 533 | ✓ VERIFIED | Exact counts confirmed; packages verbatim `com.spt.bas.server.*` |
| `zgbas-system/.../bas/server/{ctr,logistics,performance,rt,stock}/**` | 域子包 44 | ✓ VERIFIED | 9+6+4+1+24=44 (top-level server/, not under service/) |
| `zgbas-system/.../bas/server/api/**` | 224 basServer + 13 PM api | ✓ VERIFIED | 223 basServer (1 diff vs RESEARCH 224, within noise) + 13 PM = 236; 0 implements I*Client; 215 extends BaseApi; 223 @RestController |
| `zgbas-system/.../bas/client/remote/**` | ~238 I*Client contracts | ✓ VERIFIED | 239 @FeignClient contracts (proxy source for self-loopback) |
| `zgbas-admin/.../bas/web/controller/**` | 267 BFF | ✓ VERIFIED | 267 .java; Phase3 Login/Index preserved; 237 reference I*Client |
| `zgbas-admin/.../ZgbasApplication.java` | widened @EnableFeignClients | ✓ VERIFIED | L114 `@EnableFeignClients(basePackages={...})` includes com.spt.bas.client.remote |
| `zgbas-system/.../bas/client/config/BasClientConfig.java` | basServerConfig bean | ✓ VERIFIED | @Bean(BasConstants.SERVER_BEAN_NAME) LocalServerConfig, setUrlKey |
| `zgbas-system/.../bas/client/config/BasFeignPathConfig.java` | path-prefix mechanism | ✓ VERIFIED (deviation) | WebMvcConfigurer.addPathPrefix("/spt-bas-server",...) — NOT the PLAN's RequestInterceptor. Documented Wave 5 rewrite (resolved AmbiguousMappingException); Javadoc + 04-REVIEW WR-01/02 updated. Intent (D-P4-01a path handling) achieved. |
| `zgbas-system/pom.xml` | rocketmq starter + xxl-job-core | ✓ VERIFIED | rocketmq-spring-boot-starter:2.2.2 verbatim from source; xxl-job-core kept as compile dep (Phase 6 refactor target, @XxlJob task files deferred) |
| `zgbas-admin/src/test/.../ZgbasApplicationTest.java` | WR-02 substantive test | ✓ VERIFIED | 20 @Test; 4 basContract reachability (合同/授信/库存/放款) + bffControllersRegistered_sample (4 beans) + feignSelfLoopbackWiring_probe (2 assertions); 0 actual @Disabled annotations (3 grep hits are Javadoc comments); test-compile GREEN |

### Key Link Verification

| From | To | Via | Status | Details |
|------|----|-----|--------|---------|
| BFF `ApplyBrandController` | `IApplyBrandClient` Feign proxy | `@Autowired` + @EnableFeignClients(com.spt.bas.client.remote) | ✓ WIRED | controller at apply/ApplyBrandController.java autowires I*Client; IApplyBrandClient is @FeignClient |
| `I*Client.url` SpEL | `basServerConfig.url` → localhost:8080 | BasConstants.SERVER_URL=`"#{basServerConfig.url}"` + spt.bas.server.url | ✓ WIRED | BasConstants L21-23 anchor + BasClientConfig bean + dev yml L38/45/50 http://localhost:8080 |
| Feign proxy → api handler (self-loopback) | `ApplyBrandApi extends BaseApi` | localhost:8080 + BasFeignPathConfig addPathPrefix("/spt-bas-server") | ✓ WIRED (structurally) | ApplyBrandApi @RequestMapping("apply/brand") @RestController; circuit BFF→proxy→8080→api verified at code level; runtime HTTP proof → human |
| WR-02 endpoints → real controllers | TestRestTemplate URLs | @RequestMapping paths | ✓ WIRED | /apply/brand→ApplyBrandController; /ctr/contract→CtrContract*; /stock/stockContract→StockContractController; /ctr/loading→CtrContractLoadingController |

### Data-Flow Trace (Level 4)

| Artifact | Data Variable | Source | Produces Real Data | Status |
|----------|---------------|--------|--------------------|--------|
| BFF controllers (267) | I*Client autowired fields | Feign proxy → localhost:8080 api → BaseApi → Service → Dao → JPA entity | Real (source verbatim port; entities+Dao from P2) | ✓ FLOWING (structurally; runtime boot → human) |

> These are verbatim ports of production source business code (照搬保包名 D-P2-07), not newly-written logic. Data source is the real Druid DataSource + 239 JPA entities (Phase 2 PERSIST-01). No hardcoded empty arrays / static returns introduced by this phase. Runtime CRUD confirmation is the human item.

### Behavioral Spot-Checks

| Behavior | Command | Result | Status |
|----------|---------|--------|--------|
| Full-module compile gate (JDK8) | `mvn -am compile` (independently re-run) | BUILD SUCCESS, 0 ERROR, 0 cannot-find-symbol | ✓ PASS |
| Test compilation (WR-02 test compiles) | `mvn -pl zgbas-admin -am test-compile -q` | 0 ERROR, 0 cannot-find-symbol | ✓ PASS |
| D-P4-01 key constraint: api implements I*Client | `grep -rl 'implements I*Client' api` | 0 | ✓ PASS (proves 方案 A, not the disproven implements pattern) |
| Startup test execution (20 green) | `mvn test -Dtest=ZgbasApplicationTest` | SKIPPED — DB_PASSWORD/SPT_APP_SECRET UNSET | ? SKIP → human |

### Probe Execution

| Probe | Command | Result | Status |
|-------|---------|--------|--------|
| `feignSelfLoopbackWiring_probe` (JUnit, not shell) | requires `@SpringBootTest` context boot | SKIPPED — non-hermetic, no DB creds | ? SKIP → human |

> No `scripts/*/tests/probe-*.sh` conventional probes exist. The Wave 0 fail-fast "probe" is the JUnit `feignSelfLoopbackWiring_probe` method, which boots the full context and therefore requires the same DB credentials. Routed to human verification.

### Requirements Coverage

| Requirement | Source Plan | Description | Status | Evidence |
|-------------|------------|-------------|--------|----------|
| BIZ-01 | 04-03, 04-04, 04-05 | 核心业务实体与 Service 迁入 zgbas-system | ✓ SATISFIED (code) | 533 service+domain + 236 api + infra; compile gate GREEN; PM absorbed under "等核心供应链业务" (process-management). Runtime → human. |
| BIZ-02 | 04-06 | 业务 Controller/BFF 迁入 zgbas-admin | ✓ SATISFIED (code) | 267 BFF ported; endpoints map to real controllers; Phase3 assets protected. Runtime → human. |
| BIZ-03 | 04-01, 04-05, 04-06 | 业务间 Feign → 同进程 Feign 自回环，行为等价 | ✓ SATISFIED (code) | Full SpEL self-loopback chain; 0 implements I*Client; 236 api extends BaseApi; D-P4-01a path-prefix wired. Runtime HTTP proof → human; full 行为等价 regression deferred to Phase 7 ALIGN-02 (by design). |

**Orphaned requirements:** None. REQUIREMENTS.md maps exactly BIZ-01/02/03 to Phase 4. PM module absorption is a scope expansion traceable to BIZ-01 ("等核心供应链业务" — approval/process workflow is core supply-chain business); no separate PM requirement exists to be orphaned.

### Anti-Patterns Found

| File | Line | Pattern | Severity | Impact |
|------|------|---------|----------|--------|
| (wiring files: ZgbasApplication, BasClientConfig, BasFeignPathConfig, ZgbasApplicationTest) | — | TBD/FIXME/XXX/TODO/HACK/PLACEHOLDER | — | ℹ️ None found — zero debt markers in phase-touched wiring files |
| `zgbas-system/.../api/ApplyBrandApi.java` | — | 23-line file, 0 own @RequestMapping | ℹ️ Info | NOT a stub — thin `extends BaseApi<BasBrand>` subclass; CRUD handlers inherited from BaseApi (source design). CtrContractApi=423 lines confirms layer has real fat controllers too. |

### Documented Deviations (NOT gaps)

1. **D-P4-01a path mechanism: RequestInterceptor → WebMvcConfigurer.addPathPrefix.** PLAN 04-01 must_have #5 specified `BasFeignPathConfig.RequestInterceptor as @Bean`. Actual implementation is `WebMvcConfigurer.addPathPrefix("/spt-bas-server", ...)`. This is a documented Wave 5 rewrite: the RequestInterceptor path-stripper caused `AmbiguousMappingException` once BFF + api both registered URLs at root; addPathPrefix restores source topology (api at /spt-bas-server/*, BFF at root /). Javadoc in BasFeignPathConfig + ZgbasApplicationTest + 04-REVIEW WR-01/02 all updated to reflect the rewrite. The D-P4-01a GOAL (path-prefix discrepancy resolved without setting a monolith context-path that would break Phase 3 AUTH-03) is achieved. PLAN must_have wording is stale but the intent is met.
2. **D-P4-02 stub-port: ~5-15 expected → 0 new stubs.** Under 方案 A, `@EnableFeignClients` creates a Feign proxy bean for ALL 238 contracts unconditionally — a proxy points at the URL regardless of whether a server-side @RestController exists. Therefore no `NoSuchBeanDefinitionException` occurs at startup and null-guard stubs are unnecessary. 04-06 SUMMARY documents this as a "better-than-plan outcome" (lazy 404 degradation at runtime replaces null-guard stubs). The 3 `@Autowired(required=false)` in IndexController are Phase 3 insurance stubs, preserved. Architecturally sound and verified.
3. **PM module absorbed (107 files).** Not in original REQUIREMENTS enumeration but forced by basServer infra's existing `com.spt.pm.*` imports. Subsumed under BIZ-01; 13 PM api included in Wave 3. Does not orphan any requirement.

### Deferred Items (addressed in later milestone phases — not actionable gaps)

| # | Item | Addressed In | Evidence |
|---|------|-------------|----------|
| 1 | xxl-job task handler files (23 basServer/task + 8 rocketmq + 1 PM + BasCommandExecutor) | Phase 6 | ROADMAP Phase 6 goal: "xxl-job 删除，64 handler 迁入 RuoYi quartz"; INFRA-03 + QUARTZ-03 mapped to Phase 6. Verified: 0 @XxlJob annotations + no `task` package in zgbas-system; xxl-job-core jar kept as compile dep (scheduling NOT enabled, comment documents this). |
| 2 | Real CRUD behavior comparison / browser e2e | Phase 7 | ROADMAP Phase 7 success criteria: "关键业务流程与旧系统 zgbas 行为等价（回归对照通过）" — ALIGN-02 mapped to Phase 7. D-P4-06 explicitly defers 真实业务 CRUD 对照 to Phase 7. |
| 3 | report-client / purchase-client contracts | Phase 5 / v2 | D-P4-02 stub-degradation; REPORT-01/02 → Phase 5; WX-01/02 → v2. |
| 4 | Full entity schema-drift reconcile + re-enable ddl-auto=validate | tech debt | D-P4-05: ddl-auto=none preserved (matches source implicit behavior); todo `phase4-resolve-entity-schema-drift` kept open. Out of Scope per PROJECT.md. |

### Human Verification Required

The phase is non-hermetic by deliberate design (D-P3-13 Option 4, user-accepted): `ZgbasApplicationTest` forces `@ActiveProfiles("dev")` which boots the full Spring context against the real dev DB + real external spt-auth. The acceptance semantic is "local-export = passing" — but the verifier environment had DB_PASSWORD and SPT_APP_SECRET UNSET, so the runtime proof could not be independently reproduced.

**The memory note (`project_zgbas-plus-nonhermetic-startup-test`) explicitly warns that executor-reported green results can be false positives if credentials were inline-exported at runtime, and mandates an independent re-run post-merge.** This verification therefore routes the single capstone runtime check to a human with credentials.

### 1. Capstone Startup Test (proves all 3 BIZ truths at once)

**Test:** `export DB_PASSWORD=<dev> SPT_APP_SECRET=<dev>` then `JAVA_HOME=<Corretto-1.8> mvn -pl zgbas-admin -am test -Dtest=ZgbasApplicationTest -s <zg_settings.xml>`
**Expected:** 20 run / 0 fail / 0 err / 0 skip. Specifically confirm: (a) `contextLoads` — full context boots with 533 service + 236 api + 267 BFF beans, 0 NoSuchBeanDefinitionException; (b) 4 `basContractEndpointReachable_*` return 2xx/3xx/401 (合同/授信/库存/放款 over HTTP); (c) `bffControllersRegistered_sample` — applyBrandController/ctrContractController/stockContractController/ctrContractLoadingController beans present; (d) `feignSelfLoopbackWiring_probe` — BasFeignPathConfig bean + IBsCompanyOurClient Feign proxy resolve.
**Why human:** Requires live dev DB + external spt-auth credentials (non-hermetic). This single run is the runtime proof for BIZ-01 (JPA context boots), BIZ-02 (BFF endpoints reachable), and BIZ-03 (Feign self-loopback serves HTTP traffic). All structural prerequisites are already VERIFIED; this confirms they function together at runtime.

### Gaps Summary

No structural gaps found. All artifacts exist, are substantive (verbatim source ports, not stubs), and are correctly wired. The D-P4-01 方案 A Feign self-loopback is fully wired at the code level with the key constraint (0 api `implements I*Client`) empirically confirmed. The compile gate is independently green. The two documented deviations (D-P4-01a WebMvcConfigurer rewrite; D-P4-02 zero-stub outcome) are both architecturally sound and better-than-plan, fully documented in SUMMARY/REVIEW.

The only thing standing between this phase and `passed` is the runtime confirmation of the non-hermetic startup test, which requires DB credentials the verifier does not have. Given the memory note's explicit false-positive warning for this exact test, that confirmation must come from a human with credentials rather than from a SUMMARY claim.

---

_Verified: 2026-07-17T14:30:00Z_
_Verifier: Claude (gsd-verifier)_

---

## Runtime Proof Resolution (orchestrator, 2026-07-17)

The single `human_needed` item (run `ZgbasApplicationTest` for runtime BIZ-01/02/03 proof) was **independently re-run post-verification** with the plaintext `application-dev.yml` (user decision 2026-07-17 — no `DB_PASSWORD/SPT_APP_SECRET` export needed) against the real DB (47.104.15.98):

**`mvn -pl zgbas-admin -am test -Dtest=ZgbasApplicationTest` → Tests run: 20, Failures: 0, Errors: 0, Skipped: 0, BUILD SUCCESS.**

This green run is the runtime confirmation for all three BIZ truths: BIZ-01 (full JPA context with 239 entities + service/dao boots), BIZ-02 (267 BFF controllers + 236 api beans register, HTTP reachable via TestRestTemplate), BIZ-03 (D-P4-01 方案 A Feign self-loopback serves real HTTP traffic — WR-02 4 endpoint reachability assertions pass). User approved Phase 4 completion on this basis.
