---
phase: 03-auth-homepage
plan: 02
subsystem: auth
tags: [controller, login, index, sso, thymeleaf, stub]

# Dependency graph
requires:
  - phase: 03-auth-homepage
    plan: 01
    provides: ShiroUtil.isLogin()/getCurrentUserId()/getEnterpriseId()/getCurrAppId()/appCd/isPermitted(), SsoUsernamePasswordToken (framework/system), IAuthOpenFacade HTTP bean (Phase 2), ServletUtils/Convert/StringUtils/CharsetKit/StrFormatter (framework), ruoyi-common 4.7.2 transitive dep, dev zgBas.secret config key
provides:
  - LoginController (GET/POST /login — renders Thymeleaf login view, real POST intercepted by MyFormAuthenticationFilter)
  - UserOpenController (/open/user/ssoLogin + resSsoLogin SSO entry — Shiro SecurityUtils.getSubject().login)
  - IndexController (/index homepage + dynamic menu via authOpenFacade.findAllMenu; /index2 admin index; changePwd/updateFileId/lockscreen/unlockscreen/switchSkin/menuStyle)
  - Web utils for admin layer (CookieUtils/DateUtils/EasyTreeUtil2 — same-package StringUtils resolved cross-JAR via framework)
  - Stub contracts IPmProcessClient/IApproveWaitDealClient (Phase 4 replaces) + WebParamUtils stub (Phase 4 replaces)
  - Thymeleaf starter on admin pom (template resolution for login/index views)
affects: [03-03, 03-04, 04-business-logic, 07-alignment]

# Tech tracking
tech-stack:
  added: [spring-boot-starter-thymeleaf]
  patterns: [verbatim port preserving package names (D-P2-07), stub-port with required=false + null-guard for Phase-4 contracts (D-P3-10), cross-JAR same-package resolution (admin+framework share com.spt.bas.web.util)]

key-files:
  created:
    - zgbas-admin/src/main/java/com/spt/bas/web/controller/LoginController.java
    - zgbas-admin/src/main/java/com/spt/bas/web/controller/IndexController.java
    - zgbas-admin/src/main/java/com/spt/bas/web/open/apply/UserOpenController.java
    - zgbas-admin/src/main/java/com/spt/bas/web/util/CookieUtils.java
    - zgbas-admin/src/main/java/com/spt/bas/web/util/DateUtils.java
    - zgbas-admin/src/main/java/com/spt/bas/web/util/EasyTreeUtil2.java
    - zgbas-admin/src/main/java/com/spt/bas/web/util/WebParamUtils.java
    - zgbas-system/src/main/java/com/spt/bas/client/remote/IPmProcessClient.java
    - zgbas-system/src/main/java/com/spt/bas/client/remote/IApproveWaitDealClient.java
  modified:
    - zgbas-admin/pom.xml

key-decisions:
  - "IndexController stub-port (required=false x3 + null-guards) chosen over deferral so /index renders the menu via authOpenFacade while business-data calls degrade to empty/zero (D-P3-10)"
  - "MyIndexController deferred to Phase 5 — its report-contract cascade (IRptIndexReportClient + WorkBenchCache + ~15 report VOs) would break D-P3-13 startup"
  - "Stub contracts placed in zgbas-system com.spt.bas.client.remote alongside existing IBsCompanyOurClient/IBsDictClient; plain interfaces (no @FeignClient) since D-P2-12 narrowed @EnableFeignClients and no runtime bean is intended"

patterns-established:
  - "stub-port pattern for Phase-N-coupled controllers: @Autowired(required=false) on missing-contract fields + ternary null-guards returning Collections.emptyList()/default values, keeping the rendering path (authOpenFacade) intact"
  - "Web utils split across modules by dependency visibility: framework holds ServletUtils/StringUtils (no system deps); admin holds CookieUtils/DateUtils/EasyTreeUtil2 (EasyTreeUtil2 needs system entities); same package com.spt.bas.web.util resolves cross-JAR at runtime"

requirements-completed: [AUTH-01, AUTH-02]

# Metrics
duration: 4min
completed: 2026-07-16
---

# Phase 3 Plan 02: Login/Index/SSO Controllers + Thymeleaf Summary

**Login/Index/SSO controllers ported verbatim from source zgbas/web into zgbas-admin (LoginController + UserOpenController clean ports, IndexController stub-port with required=false + null-guards on 3 Phase-4 contracts), 2 stub contract interfaces + WebParamUtils stub created, Thymeleaf starter added — compiles zero-error across all 5 modules on first attempt**

## Performance

- **Duration:** 4 min
- **Started:** 2026-07-16T14:48:18Z
- **Completed:** 2026-07-16T14:52:08Z
- **Tasks:** 2
- **Files modified:** 10 (9 created, 1 modified)

## Accomplishments

- **LoginController** (`/login` GET renders Thymeleaf `login` view; POST re-renders on failure) ported 1:1 — cleanest port, only dependency is `ShiroUtil.isLogin()` (Phase 03-01)
- **UserOpenController** (`/open/user/ssoLogin` + `resSsoLogin` SSO entry) ported 1:1 — builds `SsoUsernamePasswordToken`, calls `authOpenFacade.findUserById/findUserByLoginName`, `SecurityUtils.getSubject().login()`. `@Value("${zgBas.secret}")` resolves from dev config (03-01 Task 2)
- **IndexController** stub-ported — `/index` (homepage + dynamic menu), `/index2` (admin index), `changePwd`, `updateFileId`, `lockscreen`, `unlockscreen`, `switchSkin`, `menuStyle`. The 3 Phase-4-coupled fields (`IPmProcessClient`, `IApproveWaitDealClient`, `WebParamUtils`) use `@Autowired(required = false)`; all their call sites have null-guards (ternary `Collections.emptyList()` for process lists, `"0"` for waitDealNum, `fundCompanyFlg=false` when webParamUtils absent). Menu tree still builds from `authOpenFacade.findAllMenu` — only biz/mng process sub-groupings are skipped
- **Web utils** (CookieUtils/DateUtils/EasyTreeUtil2) ported to admin `com.spt.bas.web.util` — EasyTreeUtil2's same-package `StringUtils` reference resolves cross-JAR via framework at runtime (admin→framework classpath merge)
- **2 stub contract interfaces** (`IPmProcessClient`, `IApproveWaitDealClient`) created in zgbas-system `com.spt.bas.client.remote` — plain interfaces (no @FeignClient), only the methods IndexController calls; Phase 4 overwrites with real spt-bas-client interfaces
- **WebParamUtils stub** (2 methods: `formatterWaitDealNum`, `queryFundCompany`) — no @Component, so `@Autowired(required=false)` leaves it null at runtime; null-guards skip its calls
- **Thymeleaf** `spring-boot-starter-thymeleaf` added to admin pom (D-P3-09) — templates not compile-checked, mvn baseline unaffected
- **MyIndexController** confirmed NOT ported (deferred to Phase 5 per user decision — report-contract cascade would break startup)
- `mvn compile -pl zgbas-admin -am` BUILD SUCCESS, 0 `^\[ERROR\]` on first attempt (no gotcha cascade)

## Task Commits

Each task was committed atomically:

1. **Task 1: LoginController + UserOpenController + 3 web utils + Thymeleaf** — `c6de7d2` (feat)
2. **Task 2: IndexController stub-port + stub contracts + WebParamUtils stub + compile green** — `a9eb179` (feat)

## Files Created/Modified

**Created (zgbas-admin — controllers):**
- `zgbas-admin/.../controller/LoginController.java` — `/login` GET/POST, Thymeleaf view resolution
- `zgbas-admin/.../controller/IndexController.java` — `/index` homepage+menu (stub-port), `/index2`, changePwd/updateFileId/lockscreen/unlockscreen/switchSkin/menuStyle
- `zgbas-admin/.../open/apply/UserOpenController.java` — SSO entry (`/open/user/ssoLogin` + `resSsoLogin`)

**Created (zgbas-admin — web utils):**
- `zgbas-admin/.../util/CookieUtils.java` — cookie get/set (javax.servlet only)
- `zgbas-admin/.../util/DateUtils.java` — date format/parse (commons-lang3)
- `zgbas-admin/.../util/EasyTreeUtil2.java` — menu/dept/process tree builder (auth-sdk + system entities)
- `zgbas-admin/.../util/WebParamUtils.java` — STUB (2 methods, no @Component)

**Created (zgbas-system — stub contracts):**
- `zgbas-system/.../client/remote/IPmProcessClient.java` — STUB (`findAccess`)
- `zgbas-system/.../client/remote/IApproveWaitDealClient.java` — STUB (`getUserWaitDealNum`)

**Modified:**
- `zgbas-admin/pom.xml` — added `spring-boot-starter-thymeleaf`

## Decisions Made

- **IndexController stub-port over deferral:** The plan offered two options (defer to Phase 4, or port with required=false stubs). User decision (2026-07-16) chose stub-port so `/index` renders the menu via `authOpenFacade.findAllMenu` immediately. The 3 Phase-4-coupled fields are `@Autowired(required=false)` with null-guards at every call site — business data (waitDealNum, fund company, process lists) degrades to `"0"`/`false`/empty, but the menu rendering chain (authOpenFacade, independent of Phase 4) stays fully functional. Aligns with D-P3-10 (accept degraded business data).
- **MyIndexController deferred to Phase 5:** Its hard dependencies (`IRptIndexReportClient` + `WorkBenchCache` + ~15 report VOs) are Phase 5 scope (REPORT-01/02). Porting it now would cause startup `NoSuchBeanDefinitionException`, breaking D-P3-13. Confirmed not ported.
- **Stub contracts as plain interfaces (no @FeignClient):** D-P2-12 narrowed `@EnableFeignClients` to `com.spt.sign.client.remote` only, so no Feign proxy would be generated regardless. With `@Autowired(required=false)` and no bean, the fields are null at runtime — exactly the intended degradation. Phase 4 replaces these with the real annotated interfaces.
- **WebParamUtils stub without @Component:** The real WebParamUtils is a @Component with ~15 unmigrated remote-client deps. The stub exposes only the 2 methods IndexController calls and deliberately omits @Component — so it never becomes a bean, the optional injection stays null, and null-guards skip its calls entirely. This avoids pulling in the transitive dependency cascade.

## Deviations from Plan

None — plan executed exactly as written. Compile passed on the first attempt (no gotcha cascade, no missing same-package dependencies, no module-topology surprises). The cross-module same-package pattern (EasyTreeUtil2 in admin referencing StringUtils in framework, both `com.spt.bas.web.util`) resolved cleanly at compile time via the admin→framework dependency edge, exactly as the plan's topology note predicted.

## Issues Encountered

- **Cross-JAR same-package resolution (pre-validated, no issue):** EasyTreeUtil2 (admin) uses `StringUtils.isBlank`/`equals` without import — same-package reference to `com.spt.bas.web.util.StringUtils` (framework). This was called out in the plan's topology note and confirmed working: javac resolves same-package types across module dependencies at compile time, and the runtime classpath merges both JARs. No action needed.
- **Stub method behavior divergence (intentional):** The WebParamUtils stub's `formatterWaitDealNum` returns `"0"` for null (per plan spec) whereas the original returns `""`. This is moot in Phase 3 — WebParamUtils has no @Component, so the method is never invoked (null-guard skips it). Phase 4 ports the real @Component with faithful behavior.

## Known Stubs

| Stub | File | Reason | Phase 4 Resolution |
|------|------|--------|--------------------|
| `IPmProcessClient` | zgbas-system/.../client/remote/IPmProcessClient.java | Phase-4 FeignClient contract not migrated; plain interface with `findAccess` signature only | Replace with real `spt-bas-client` @FeignClient interface |
| `IApproveWaitDealClient` | zgbas-system/.../client/remote/IApproveWaitDealClient.java | Phase-4 FeignClient contract not migrated; plain interface with `getUserWaitDealNum` signature only | Replace with real `spt-bas-client` @FeignClient interface |
| `WebParamUtils` | zgbas-admin/.../util/WebParamUtils.java | Real impl has ~15 unmigrated remote-client deps; stub has 2 methods, no @Component | Port real @Component WebParamUtils (satisfies the existing `@Autowired(required=false)`) |

All 3 stubs are inert at runtime (no bean → null → null-guard skips). They exist solely to satisfy IndexController compilation. The `/index` menu rendering path (`authOpenFacade.findAllMenu` → `EasyTreeUtil2.getMenuTree`) is fully wired and independent of these stubs.

## User Setup Required

None — no external configuration beyond existing Phase 2 env vars and 03-01 dev config keys (`zgBas.secret` already in application-dev.yml). Thymeleaf resolves views from `templates/` (copied in Plan 03-03).

## Next Phase Readiness

- Login/Index/SSO controllers source code is in place and compiles
- Thymeleaf starter present on admin pom — template resolution ready (templates arrive in Plan 03-03)
- IndexController `/index` will render the menu once templates + static resources are in place (Plan 03-03) and the app starts (Plan 03-04 WebSocket endpoints)
- **For Plan 03-03:** bulk-copy `templates/` + `static/` from source `web/src/main/resources/` into `zgbas-admin/src/main/resources/` — `login.html`, `index.html`, `index-topnav.html`, `main.html`, `admin/index.html` + shared layout/fragment/static referenced by these controllers
- **For Plan 03-04:** WebSocket trio (WebSocketConfig/IndexWebSocketServer/WebSocketServer + Message POJO) — note WebSocketServer has the same `IApproveWaitDealClient` Phase-4 coupling; apply the same required=false stub pattern
- **For Phase 4:** replace the 3 stubs with real implementations; the `@Autowired(required=false)` fields will then resolve to real beans with no IndexController changes needed

## Self-Check: PASSED

All created files verified to exist; both task commits verified in git log; BUILD SUCCESS confirmed with 0 errors.

---
*Phase: 03-auth-homepage*
*Completed: 2026-07-16*
