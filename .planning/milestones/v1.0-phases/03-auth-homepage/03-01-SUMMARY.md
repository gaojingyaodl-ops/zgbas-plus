---
phase: 03-auth-homepage
plan: 01
subsystem: auth
tags: [shiro, framework, realm, security, session, cookie, thymeleaf]

# Dependency graph
requires:
  - phase: 02-infrastructure
    provides: inlined spt-tools-shiro (ToolsShiroConfig/AbstractShiroDbRealm/IShiroService/filters), auth-sdk IAuthOpenFacade HTTP bean, dual ORM single DataSource, dev/prod profile config baseline
provides:
  - ShiroDbRealm (@Component) with mock backdoor + SSO reLogin + assertCredentialsMatch no-op (AUTH-01/03/04)
  - ShiroService (@Component implements IShiroService) wiring the dynamic filter chain via authOpenFacade.findAllMenu HTTP
  - ShiroUtil (app-specific prop constants + getCurrAppId HTTP) + SsoUsernamePasswordToken (SSO token)
  - ShiroPropConfig (@ConfigurationProperties shiro.prop.*) binding mockPassword/appCd/sessionEnable
  - Web utils (ServletUtils/Convert/StringUtils/CharsetKit/StrFormatter) for Realm/controller use
  - ToolsShiroConfig auto-config activated (exclude removed)
  - Behavior-equivalence config keys (static-path-pattern BLOCKER 1 + session WARNING 1 + dev Shiro/thymeleaf keys)
affects: [03-02, 03-03, 03-04, 04-business-logic, 07-alignment]

# Tech tracking
tech-stack:
  added: [ruoyi-common 4.7.2, UserAgentUtils 1.21]
  patterns: [verbatim port preserving package names (D-P2-07), module-placement follows dependency topology (D-08), mock backdoor preserved for behavior equivalence (D-P3-06)]

key-files:
  created:
    - zgbas-system/src/main/java/com/spt/bas/web/shiro/ShiroDbRealm.java
    - zgbas-system/src/main/java/com/spt/bas/web/shiro/ShiroUtil.java
    - zgbas-system/src/main/java/com/spt/bas/web/shiro/ShiroService.java
    - zgbas-system/src/main/java/com/spt/bas/web/shiro/SsoUsernamePasswordToken.java
    - zgbas-system/src/main/java/com/spt/bas/web/config/ShiroPropConfig.java
    - zgbas-framework/src/main/java/com/spt/bas/web/util/ServletUtils.java
    - zgbas-framework/src/main/java/com/spt/bas/web/util/Convert.java
    - zgbas-framework/src/main/java/com/spt/bas/web/util/StringUtils.java
    - zgbas-framework/src/main/java/com/spt/bas/web/util/CharsetKit.java
    - zgbas-framework/src/main/java/com/spt/bas/web/util/StrFormatter.java
  modified:
    - zgbas-admin/src/main/java/com/spt/ZgbasApplication.java
    - zgbas-admin/src/main/resources/application.yml
    - zgbas-admin/src/main/resources/application-dev.yml
    - pom.xml
    - zgbas-framework/pom.xml
    - zgbas-system/pom.xml

key-decisions:
  - "Shiro 5-file unit placed in zgbas-system (not framework as planned) because D-08 topology prevents framework from seeing system classes (BsDictUtil/BasConstants/BsDictData) that ShiroDbRealm/ShiroUtil reference"
  - "ruoyi-common dep kept in framework pom (transitive to system); UserAgentUtils added to system pom directly"
  - "Mock backdoor preserved verbatim (D-P3-06); dev profile keeps plaintext 123456 (D-P3-07)"

patterns-established:
  - "Module placement follows dependency visibility: when a ported class references system-layer types, it must live in system or deeper (not framework)"
  - "Same-package units port together: ShiroDbRealm+ShiroUtil+ShiroService+SsoUsernamePasswordToken share com.spt.bas.web.shiro and rely on same-package constant resolution (no imports)"

requirements-completed: [AUTH-01, AUTH-03, AUTH-04]

# Metrics
duration: 5min
completed: 2026-07-16
---

# Phase 3 Plan 01: Shiro Auth Chain Port Summary

**Shiro session+cookie auth chain (Realm/Service/Util/Token/Config) ported verbatim from source zgbas/web, ToolsShiroConfig un-excluded to activate auto-config, behavior-equivalence config keys (static-path-pattern/session/thymeleaf) applied — compiles zero-error across all 5 modules**

## Performance

- **Duration:** 5 min
- **Started:** 2026-07-16T14:39:03Z
- **Completed:** 2026-07-16T14:44:28Z
- **Tasks:** 2
- **Files modified:** 16 (10 created, 6 modified)

## Accomplishments

- Shiro authentication chain source code ported 1:1 from `zgbas/web` into the monolith: ShiroDbRealm (mock backdoor + SSO reLogin + assertCredentialsMatch no-op), ShiroService (dynamic filter chain via authOpenFacade.findAllMenu HTTP), ShiroUtil (app-specific constants + getCurrAppId), SsoUsernamePasswordToken, ShiroPropConfig
- Web utility classes ported (ServletUtils/Convert/StringUtils/CharsetKit/StrFormatter) — required by ShiroDbRealm and downstream controllers
- ToolsShiroConfig exclude removed from ZgbasApplication — Shiro auto-config (securityManager/ehCacheManager/filter chain) now engages with the ported ShiroDbRealm as the concrete Realm bean
- Behavior-equivalence config keys applied: `spring.mvc.static-path-pattern=/static/**` (BLOCKER 1 — prevents double-static/ 404), `server.servlet.session.tracking-modes=cookie` + `http-only` + `timeout=21600s` (WARNING 1), `spring.thymeleaf.cache=false` (dev hot reload)
- Dev Shiro keys (`shiro.prop.mock-password=123456`, `app-cd=zgbas`, `zgBas.secret`) added to application-dev.yml — prevents `@Value` placeholder resolution failure at startup
- `mvn compile` BUILD SUCCESS with 0 `^\[ERROR\]` across all 5 modules (common/framework/system/quartz/admin)

## Task Commits

Each task was committed atomically:

1. **Task 1: Port Shiro 4-file unit + web utils + ruoyi-common dep** — `1a27abb` (feat)
2. **Task 2: Remove ToolsShiroConfig exclude + config keys + compile green** — `17a2c8c` (feat)

## Files Created/Modified

**Created (zgbas-system — Shiro auth chain):**
- `zgbas-system/.../com/spt/bas/web/shiro/ShiroDbRealm.java` — @Component Realm with mock backdoor + SSO + no-op credential match
- `zgbas-system/.../com/spt/bas/web/shiro/ShiroUtil.java` — extends base ShiroUtil, app constants + getCurrAppId
- `zgbas-system/.../com/spt/bas/web/shiro/ShiroService.java` — @Component implements IShiroService, filter chain init
- `zgbas-system/.../com/spt/bas/web/shiro/SsoUsernamePasswordToken.java` — SSO token with appCode/ssoLogin
- `zgbas-system/.../com/spt/bas/web/config/ShiroPropConfig.java` — @ConfigurationProperties(shiro.prop) binding

**Created (zgbas-framework — web utils):**
- `zgbas-framework/.../com/spt/bas/web/util/ServletUtils.java` — request/response/session helpers
- `zgbas-framework/.../com/spt/bas/web/util/Convert.java` — type conversion utility
- `zgbas-framework/.../com/spt/bas/web/util/StringUtils.java` — extends commons-lang3 StringUtils
- `zgbas-framework/.../com/spt/bas/web/util/CharsetKit.java` — charset constants
- `zgbas-framework/.../com/spt/bas/web/util/StrFormatter.java` — {} placeholder formatting

**Modified:**
- `zgbas-admin/.../ZgbasApplication.java` — ToolsShiroConfig removed from excludeFilters
- `zgbas-admin/.../application.yml` — static-path-pattern + session behavior keys
- `zgbas-admin/.../application-dev.yml` — shiro.prop.* + zgBas.secret + thymeleaf.cache=false
- `pom.xml` — ruoyi-common 4.7.2 + UserAgentUtils 1.21 in dependencyManagement
- `zgbas-framework/pom.xml` — ruoyi-common dep
- `zgbas-system/pom.xml` — UserAgentUtils dep

## Decisions Made

- **Shiro classes placed in zgbas-system, not zgbas-framework:** The plan (D-P2-06) specified framework, but ShiroDbRealm/ShiroUtil reference `com.spt.bas.client.cache.BsDictUtil`, `constant.BasConstants`, `entity.BsDictData` — all in zgbas-system. D-08 topology (`framework <- system`) means framework cannot depend on system. Moving to zgbas-system resolves the compile blocker while preserving Spring DI (admin's `com.spt` component scan finds the @Component beans regardless of module).
- **Web utils stayed in framework:** ServletUtils/Convert/StringUtils/CharsetKit/StrFormatter have no system-layer dependencies (after removing one unused import). ShiroDbRealm (system) references ServletUtils (framework) via the system→framework dependency edge. Keeping utils in framework makes them available to both system and future admin controllers.
- **ruoyi-common kept in framework pom:** Originally added for ShiroDbRealm when it was planned for framework. After the module move, ShiroDbRealm is in system which depends on framework, so ruoyi-common is transitively available. Left in framework because future plans (IndexController in admin) will also need it.

## Deviations from Plan

### Auto-fixed Issues

**1. [Rule 3 - Blocking] Shiro 5-file unit moved from zgbas-framework to zgbas-system**
- **Found during:** Task 2 (compile green step)
- **Issue:** Plan placed ShiroDbRealm/ShiroUtil/ShiroService/SsoUsernamePasswordToken/ShiroPropConfig in zgbas-framework, but these reference `BsDictUtil`/`BasConstants`/`BsDictData` (com.spt.bas.client.*) which live in zgbas-system. D-08 topology prevents framework from depending on system (would create a circular build dependency). Initial compile produced 88 errors.
- **Fix:** `git mv` all 5 files from `zgbas-framework/.../com/spt/bas/web/{shiro,config}/` to `zgbas-system/.../com/spt/bas/web/{shiro,config}/`. Web utils remain in framework (ShiroDbRealm accesses them via system→framework dep). Package names unchanged (`com.spt.bas.web.shiro` / `com.spt.bas.web.config`), so same-package constant resolution and Spring component-scan are unaffected.
- **Files modified:** 5 files moved (ShiroDbRealm/ShiroUtil/ShiroService/SsoUsernamePasswordToken/ShiroPropConfig)
- **Verification:** `mvn compile -pl zgbas-framework,zgbas-admin -am` BUILD SUCCESS, 0 errors
- **Committed in:** 17a2c8c (Task 2 commit)

**2. [Rule 3 - Blocking] Ported StrFormatter.java (same-package dependency not in plan file list)**
- **Found during:** Task 1 (source analysis before copy)
- **Issue:** StringUtils.java line 257 calls `StrFormatter.format(template, params)` — same-package reference (`com.spt.bas.web.util.StrFormatter`), no import. The plan listed only 4 web utils (ServletUtils/Convert/StringUtils/CharsetKit) but missed this 5th same-package dependency.
- **Fix:** Ported StrFormatter.java alongside the other 4 utils to keep `com.spt.bas.web.util` package complete.
- **Files modified:** zgbas-framework/.../util/StrFormatter.java (created)
- **Verification:** Compiles as part of the web util package
- **Committed in:** 1a27abb (Task 1 commit)

**3. [Rule 1 - Bug] Removed unused BasConstants import from StringUtils.java**
- **Found during:** Task 2 (after moving Shiro files, recompile revealed StringUtils still in framework imports a system class)
- **Issue:** StringUtils.java line 3 imported `com.spt.bas.client.constant.BasConstants` but never used it in the method bodies. Since StringUtils stays in framework (which cannot see system), this unused import caused a compile error.
- **Fix:** Removed the single unused import line. No behavioral change.
- **Files modified:** zgbas-framework/.../util/StringUtils.java
- **Verification:** Compiles cleanly; BasConstants not referenced anywhere in the file body
- **Committed in:** 17a2c8c (Task 2 commit)

**4. [Rule 3 - Blocking] Added UserAgentUtils 1.21 dependency**
- **Found during:** Task 2 (compile green step, after Shiro files moved to system)
- **Issue:** ShiroDbRealm imports `eu.bitwalker.useragentutils.UserAgent` (line 36) for login audit logging (browser/OS). The library was not declared as a dependency. Source web pom declares `eu.bitwalker:UserAgentUtils:1.21`; jar exists in local repo.
- **Fix:** Added UserAgentUtils 1.21 to root pom dependencyManagement + zgbas-system pom dependencies.
- **Files modified:** pom.xml, zgbas-system/pom.xml
- **Verification:** `mvn compile` BUILD SUCCESS, 0 errors
- **Committed in:** 17a2c8c (Task 2 commit)

---

**Total deviations:** 4 auto-fixed (2 blocking module/deps, 1 blocking missing file, 1 bug unused import)
**Impact on plan:** All auto-fixes necessary for compilation under the D-08 module topology. No scope creep — every change either resolves a compile blocker or fixes a bug. Behavior equivalence preserved (all source code ported verbatim, no logic changes).

## Issues Encountered

- **Gotcha cascade (D-08 topology):** The plan assumed framework could host the Shiro Realm, but the module dependency graph (`common <- framework <- system`) prevents framework from seeing system-layer types. Resolved by moving the Shiro classes one level deeper (to system). This is a reusable lesson for future plans: any ported class that references `com.spt.bas.client.*` types must live in system or deeper.
- **Same-package hidden dependencies:** The Shiro 4-file unit relies on same-package constant resolution (ShiroUtil.APPID etc. without import). The web utils have a similar pattern (StringUtils → StrFormatter). Both units must port as complete packages.

## User Setup Required

None — no external service configuration required beyond existing Phase 2 env vars (`DB_PASSWORD`, `SPT_APP_SECRET`). The dev profile Shiro keys (`mock-password`, `zgBas.secret`) are committed as plaintext dev defaults per D-P3-07 (照搬旧项目明文).

## Next Phase Readiness

- Shiro Realm/Service/Util/Token/Config source code is in place and compiles
- ToolsShiroConfig auto-config is activated (exclude removed)
- Config keys for startup-time `@Value` resolution are present
- **Plans 03-02/03/04** can proceed: LoginController/IndexController/UserOpenController port, templates/static bulk copy, WebSocket endpoints
- **Potential concern for Plan 03-02:** IndexController references `IPmProcessClient`/`IApproveWaitDealClient` (Phase 4 contracts) — the plan's stub strategy (`@Autowired(required=false)`) must be applied carefully to avoid startup `NoSuchBeanDefinitionException`

## Self-Check: PASSED

All created files verified to exist; both task commits verified in git log.

---
*Phase: 03-auth-homepage*
*Completed: 2026-07-16*
