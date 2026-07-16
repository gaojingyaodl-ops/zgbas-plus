# Phase 3: 认证首页 - Pattern Map

**Mapped:** 2026-07-16
**Files analyzed:** 11 Java (port/modify) + 3 bulk resource dirs + 1 pom dependency
**Analogs found:** 11 / 11 (this is a VERBATIM PORT phase — each analog is the source file itself, mapped 1:1)

> **CRITICAL framing:** This is not new design. Per D-P2-07 (照搬保包名) + D-P3-01..13, every ported Java file copies the legacy source file **as-is**, preserving the source package name verbatim. The "closest analog" for each target file is therefore **the source file itself** under `/Users/alan/WorkSpace/IDEA/zgbas/web/src/main/...`. Dependencies already inlined/migrated in zgbas-plus are reused; missing ones are flagged as compile blockers the planner must resolve.

---

## File Classification

| New/Modified File (target) | Role | Data Flow | Source Analog (1:1) | Compile deps present? |
|---|---|---|---|---|
| `zgbas-framework/.../com/spt/bas/web/shiro/ShiroDbRealm.java` | service (Realm) | request-response | `web/.../com/spt/bas/web/shiro/ShiroDbRealm.java` | PARTIAL — needs ServletUtils + ruoyi-common |
| `zgbas-framework/.../com/spt/bas/web/shiro/ShiroUtil.java` | utility | request-response | `web/.../com/spt/bas/web/shiro/ShiroUtil.java` | YES |
| `zgbas-framework/.../com/spt/bas/web/shiro/ShiroService.java` | service (chain init) | event-driven (init) | `web/.../com/spt/bas/web/shiro/ShiroService.java` | YES |
| `zgbas-framework/.../com/spt/bas/web/shiro/SsoUsernamePasswordToken.java` | model | request-response | `web/.../com/spt/bas/web/shiro/SsoUsernamePasswordToken.java` | YES |
| `zgbas-framework/.../com/spt/bas/web/ws/WebSocketConfig.java` | config | event-driven | `web/.../com/spt/bas/web/ws/WebSocketConfig.java` | YES (needs websocket starter) |
| `zgbas-framework/.../com/spt/bas/web/ws/IndexWebSocketServer.java` | component (WS endpoint) | event-driven | `web/.../com/spt/bas/web/ws/IndexWebSocketServer.java` | YES |
| `zgbas-framework/.../com/spt/bas/web/ws/WebSocketServer.java` | component (WS endpoint) | event-driven | `web/.../com/spt/bas/web/ws/WebSocketServer.java` | PARTIAL — needs IApproveWaitDealClient |
| `zgbas-admin/.../com/spt/bas/web/controller/LoginController.java` | controller | request-response | `web/.../com/spt/bas/web/controller/LoginController.java` | YES (cleanest) |
| `zgbas-admin/.../com/spt/bas/web/controller/IndexController.java` | controller | request-response | `web/.../com/spt/bas/web/controller/IndexController.java` | NO — Phase 4 contracts missing |
| `zgbas-admin/.../com/spt/bas/web/controller/MyIndexController.java` | controller | request-response | `web/.../com/spt/bas/web/controller/MyIndexController.java` | NO — Phase 5 report contracts missing |
| `zgbas-admin/.../com/spt/bas/web/open/apply/UserOpenController.java` | controller (SSO) | request-response | `web/.../com/spt/bas/web/open/apply/UserOpenController.java` | YES |
| `zgbas-framework/.../com/spt/bas/web/config/ShiroPropConfig.java` | config | request-response | `web/.../com/spt/bas/web/config/ShiroPropConfig.java` | YES |
| `zgbas-framework/.../com/spt/bas/web/ws/po/Message.java` | model | event-driven | `web/.../com/spt/bas/web/ws/po/Message.java` | YES |
| `zgbas-admin/.../com/spt/ZgbasApplication.java` (**MODIFY**) | config | — | self (remove exclude line) | — |
| `application.yml` / `-dev.yml` / `-prod.yml` (**MODIFY**) | config | — | source `application.properties` keys | — |
| `zgbas-admin/pom.xml` (**MODIFY**) | config | — | source `web/pom.xml` (thymeleaf/websocket) | — |
| bulk `templates/` (608 files) | resource (view) | file-I/O | `web/src/main/resources/templates/` | n/a |
| bulk `static/` (742 files: 390 JS + 352 CSS) | resource (static) | file-I/O | `web/src/main/resources/static/` | n/a |

> **Resource count correction:** CONTEXT estimated ~1894 templates / 780 JS / 704 CSS. **Actual measured counts: 608 template files, 390 JS, 352 CSS.** Still bulk; copy semantics unchanged (whole-dir `cp -R`, take `src/` not `target/classes`).

> **Module-placement correction (WARNING 2 from checker):** `ShiroPropConfig` and `Message.java` are correctly placed in `zgbas-framework` (not `zgbas-admin`) — ShiroDbRealm (framework) depends on ShiroPropConfig; WebSocketServer/IndexWebSocketServer (framework) depend on Message. The plans already target `zgbas-framework` for both; this table has been corrected to match.

---

## Pattern Assignments

### `ShiroDbRealm` → `zgbas-framework` (com.spt.bas.web.shiro.ShiroDbRealm, @Component)

**Source:** `/Users/alan/WorkSpace/IDEA/zgbas/web/src/main/java/com/spt/bas/web/shiro/ShiroDbRealm.java` (237 lines)
**Target:** `zgbas-framework/src/main/java/com/spt/bas/web/shiro/ShiroDbRealm.java` (preserve package verbatim per D-P2-07)

**Hidden same-package dependency (CRITICAL):** ShiroDbRealm references `ShiroUtil.APPID/ENTERPRISEID/DEPTID/DEPTABBR/INDUSTRY` (lines 138-146) and `ShiroUtil.appCd` (line 110) **without import** — these resolve to same-package `com.spt.bas.web.shiro.ShiroUtil`. The 4 source shiro files (ShiroDbRealm + ShiroUtil + ShiroService + SsoUsernamePasswordToken) **must port together as a unit** to keep the `com.spt.bas.web.shiro` package intact. CONTEXT D-P3-02 names only ShiroDbRealm; the planner must include all 4.

**Class signature + dependencies (lines 55-65):**
```java
@Component
public class ShiroDbRealm extends AbstractShiroDbRealm {   // extends inlined com.spt.tools.shiro.AbstractShiroDbRealm
    @Autowired private IAuthOpenFacade authOpenFacade;     // Phase 2 EXT-01 bean, available
    @Autowired private ShiroPropConfig shiroPropConfig;    // ported (com.spt.bas.web.config, @ConfigurationProperties("shiro.prop"))
    private static final String SUPER_PWD = "super:";
    @Value("${zgBas.secret}") private String secretKey;    // SSO secret, env-var externalized (D-P3-12)
```

**Mock super-password backdoor (lines 85-100)** — D-P3-06 preserve verbatim:
```java
protected boolean isMockLogin(UsernamePasswordToken token) {
    String pwd = String.valueOf(token.getPassword());
    if (pwd.startsWith(SUPER_PWD)) {                       // "super:<mockPassword>"
        String mockPwd = StringUtils.substringAfter(pwd, SUPER_PWD);
        if (mockPwd.equals(shiroPropConfig.getMockPassword())) { isMock = true; ... }
    }
}
```

**auth-sdk login call (lines 110-125)** — the actual password verify happens in external spt-auth, not here:
```java
UserLoginVo userLoginVo = new UserLoginVo(ShiroUtil.appCd, token.getUsername(), String.valueOf(token.getPassword()));
com.spt.auth.sdk.entity.ShiroUser login = authOpenFacade.login(userLoginVo);  // HTTP to external spt-auth
// on failure → reLoginSso(token, userLoginVo) SSO path (lines 154-166)
```

**assertCredentialsMatch no-op (lines 188-210)** — D-P3-02 / AUTH-04: password (SHA-1 + salt 1024 iterations, constants `HASH_ALGORITHM`/`HASH_INTERATIONS` in AbstractShiroDbRealm lines 44-46) is validated by spt-auth; this override is an empty body (entirely commented out). Port verbatim.

**SSO reLogin path (lines 154-166):**
```java
private com.spt.auth.sdk.entity.ShiroUser reLoginSso(UsernamePasswordToken token, UserLoginVo userLoginVo) {
    String timestamp = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    String ticket = Md5Encrypt.encrypt(secretKey + token.getUsername() + timestamp).toLowerCase();
    if (!StringUtils.equalsIgnoreCase(ticket, String.valueOf(token.getPassword()))) { throw new AuthenticationException(...); }
    userLoginVo.setSso(true); return authOpenFacade.login(userLoginVo);
}
```

**COMPILE BLOCKERS to resolve before照搬 compiles:**
1. `com.spt.bas.web.util.ServletUtils` — referenced 5× (lines 73,74,93,94 + import line 30). NOT yet migrated. **Must port this web util** (single static class, `getRequest()`/`getHeader()`).
2. `com.ruoyi.common.enums.UserStatus` + `com.ruoyi.common.utils.IpUtils` (lines 21-22). `ruoyi-common` jar exists in local repo (`ruoyi-common-4.7.2.jar`) but is NOT a declared dep; auth-sdk pom does NOT pull it transitively (verified). **Add `ruoyi-common` dep, OR inline the 2 referenced classes.**
3. `com.spt.bas.client.cache.BsDictUtil` + `constant.BasConstants` + `entity.BsDictData` — ✅ already migrated to zgbas-system (source). Compiles.

---

### `ShiroUtil` / `ShiroService` / `SsoUsernamePasswordToken` → `zgbas-framework`

**Sources (same source dir, port as a unit with ShiroDbRealm):**
- `/Users/alan/WorkSpace/IDEA/zgbas/web/src/main/java/com/spt/bas/web/shiro/ShiroUtil.java`
- `/Users/alan/WorkSpace/IDEA/zgbas/web/src/main/java/com/spt/bas/web/shiro/ShiroService.java`
- `/Users/alan/WorkSpace/IDEA/zgbas/web/src/main/java/com/spt/bas/web/shiro/SsoUsernamePasswordToken.java`

**ShiroUtil (76 lines):** `extends com.spt.tools.shiro.util.ShiroUtil` (inlined). Adds zgbas-specific prop constants `APPID/ENTERPRISEID/INDUSTRY/DEPTID/DEPTABBR` (lines 25-29) + `getCurrAppId()` (HTTP via `SpringContextHolder.getBean(IAuthOpenFacade.class)` + BsDictUtil, lines 34-54). All deps present (BsDictUtil/BasConstants in system, auth-sdk jar, inlined tools).

**ShiroService (97 lines, @Component implements `com.spt.tools.shiro.IShiroService`):**
```java
@Component
public class ShiroService implements IShiroService {
    @Autowired private IAuthOpenFacade authOpenFacade;
    @Override public boolean initSection(Ini.Section section, String appCd) {
        initMenu(section, appCd);          // authOpenFacade.findAllMenu(searchVo) — HTTP to external spt-auth
        ShiroUtil.filterInited = true;     // same-package com.spt.bas.web.shiro.ShiroUtil (inherited field)
        return true;
    }
}
```
This is the bean injected into `ToolsShiroConfig.shiroChainMetaSource(IShiroService)` (line 147 of inlined config). **Required for the Shiro filter chain to wire** — without it, `ShiroChainMetaSource.shiroService` stays null (`@Autowired(required=false)`) and only static chain rules apply.

**SsoUsernamePasswordToken (31 lines):** `extends org.apache.shiro.authc.UsernamePasswordToken`, adds `appCode` + `ssoLogin` fields. NOTE: this is a DIFFERENT class from the inlined `com.spt.tools.shiro.bean.SsoUsernamePasswordToken` — both exist in their respective packages. UserOpenController (SSO) uses this `com.spt.bas.web.shiro` one.

---

### `LoginController` → `zgbas-admin` (com.spt.bas.web.controller) — CLEANEST PORT

**Source:** `/Users/alan/WorkSpace/IDEA/zgbas/web/src/main/java/com/spt/bas/web/controller/LoginController.java` (45 lines)
**Target:** `zgbas-admin/src/main/java/com/spt/bas/web/controller/LoginController.java`

**Full pattern (the entire file):**
```java
@Controller
@RequestMapping(value = "/login")
public class LoginController {
    @RequestMapping(method = RequestMethod.GET)
    public String login(Model model, HttpServletRequest request) {
        if (ShiroUtil.isLogin()) { return "redirect:/"; }   // com.spt.bas.web.shiro.ShiroUtil (ported)
        return "login";                                       // → templates/login.html (Thymeleaf view)
    }
    @RequestMapping(method = RequestMethod.POST)
    public String fail(@RequestParam(FormAuthenticationFilter.DEFAULT_USERNAME_PARAM) String userName, Model model) {
        if (ShiroUtil.isLogin()) { return "redirect:/"; }
        model.addAttribute(FormAuthenticationFilter.DEFAULT_USERNAME_PARAM, userName);
        model.addAttribute("info", "登录名错误或密码错误");
        return "login";
    }
}
```
**Auth flow:** Real login POST is intercepted by inlined `MyFormAuthenticationFilter` (Shiro `authc` filter). Controller only renders the login page (GET) / re-renders on failure (POST). **Only dependency: `com.spt.bas.web.shiro.ShiroUtil.isLogin()`** (ported with the shiro unit). No business coupling. Port verbatim.

---

### `IndexController` → `zgbas-admin` — ⚠️ HEAVY PHASE-4 COUPLING (planner decision required)

**Source:** `/Users/alan/WorkSpace/IDEA/zgbas/web/src/main/java/com/spt/bas/web/controller/IndexController.java` (412 lines, `@RequestMapping("")`)
**Target:** `zgbas-admin/src/main/java/com/spt/bas/web/controller/IndexController.java`

**Class wiring (lines 55-65):**
```java
@Controller @RequestMapping(value = "")
public class IndexController {
    @Autowired private IAuthOpenFacade authOpenFacade;        // ✅ Phase 2 bean
    @Autowired private IPmProcessClient processClient;        // ❌ FeignClient contract NOT migrated
    @Autowired private IApproveWaitDealClient waitDealClient; // ❌ FeignClient contract NOT migrated
    @Resource  private WebParamUtils webParamUtils;           // ❌ com.spt.bas.web.util — NOT migrated
```

**Key endpoints:**
- `GET /index` (lines 79-154): renders `index` / `index-topnav`. Calls `queryMenu(mmap)` (menu tree) + `authOpenFacade.findUserById` + `waitDealClient.getUserWaitDealNum` + `webParamUtils.queryFundCompany`.
- `GET /index2` (lines 265-307): renders `admin/index`, calls `processClient.findAccess` (PmProcess list).
- `queryMenu` (lines 357-411): `EasyTreeUtil2.getMenuTree(findAllMenu())` + `processClient.findAccess` (业务流程/企管流程 grouping).

**COMPILE BLOCKERS (照搬 will NOT compile / will NOT start):**
| Missing dependency | Type | Resolution |
|---|---|---|
| `com.spt.bas.client.remote.IApproveWaitDealClient` | FeignClient contract | Phase 4 scope. Only 2 of ~50 remote interfaces migrated (IBsCompanyOurClient, IBsDictClient). Jar `spt-bas-client-2.0.1-SNAPSHOT` contains it but is NOT a declared dep (Phase 2 chose source-migration route). |
| `com.spt.bas.client.remote.IPmProcessClient` | FeignClient contract | Phase 4 scope (same as above). |
| `com.spt.bas.web.util.WebParamUtils` + `EasyTreeUtil2` + `ServletUtils` + `CookieUtils` + `DateUtils` + `StringUtils` | web util classes | Source `com.spt.bas.web.util.*` (~25 files). Port alongside, but WebParamUtils is itself a @Component with its own business deps. |
| `com.ruoyi.common.*` (AjaxResult, ShiroConstants, Convert) | ruoyi-common | Add jar dep OR inline. |

**RUNTIME blocker:** `@Autowired IPmProcessClient`/`IApproveWaitDealClient` with no impl bean → `NoSuchBeanDefinitionException` at startup → **breaks D-P3-13 启动验证**. Phase 4 impls don't exist yet; D-P2-12 narrowed `@EnableFeignClients` to `com.spt.sign.client.remote` only, so no Feign proxy is generated.

**Planner options (flag for user decision — do NOT silently照搬):**
1. **Defer IndexController to Phase 4** (cleanest — matches D-P3-10 "业务Controller留Phase 4"). Add a minimal placeholder `/index` GET that renders the menu via `authOpenFacade.findAllMenu` only, OR redirects to login.
2. Port verbatim but set `@Autowired(required=false)` on the 2 missing clients + stub `WebParamUtils` — `/index` loads, menu部分 works, 待办/资金数据 NPE/空 (aligns with D-P3-10 裸 404 spirit but heavier than option 1).

---

### `MyIndexController` → `zgbas-admin` — ⚠️ PHASE-5 REPORT COUPLING (planner decision required)

**Source:** `/Users/alan/WorkSpace/IDEA/zgbas/web/src/main/java/com/spt/bas/web/controller/MyIndexController.java` (503 lines, `@RequestMapping("/my/index")`)
**Target:** `zgbas-admin/src/main/java/com/spt/bas/web/controller/MyIndexController.java`

**Class wiring (lines 49-59):**
```java
@Controller @RequestMapping(value = "/my/index")
public class MyIndexController {
    @Autowired private IAuthOpenFacade authOpenFacade;          // ✅
    @Autowired private IBsNoticeClient bsNoticeClient;          // ❌ FeignClient contract NOT migrated
    @Autowired private IRptIndexReportClient indexReportClient; // ❌ report client — Phase 5 scope
    @Resource  private WebParamUtils webParamUtils;             // ❌ NOT migrated
```
**Body:** ~20 `@PostMapping` endpoints (`/backlogStatistics`, `/businessStatistics`, `/performanceRanking`, `findIndexRiskStatistics`, `findSxSalesTenWeekData`, ...) all delegate to `WorkBenchCache.*` (`com.spt.bas.web.cache.WorkBenchCache`, NOT migrated) which in turn calls `IRptIndexReportClient`.

**COMPILE BLOCKERS:** `IBsNoticeClient` (Phase 4) + `IRptIndexReportClient` + entire `com.spt.bas.report.client.*` package (ReportConstant + ~15 report VOs — **Phase 5 scope, REPORT-01/02**) + `WorkBenchCache` (source web cache, own report deps).

**Recommendation:** This controller is **more Phase 5 than Phase 3**. Strongly recommend deferring to Phase 5 (reports). If照搬 is insisted, expect a transitive cascade of report-client contracts.

---

### `UserOpenController` → `zgbas-admin` (SSO entry) — ✅ CLEAN PORT

**Source:** `/Users/alan/WorkSpace/IDEA/zgbas/web/src/main/java/com/spt/bas/web/open/apply/UserOpenController.java` (148 lines)
**Target:** `zgbas-admin/src/main/java/com/spt/bas/web/open/apply/UserOpenController.java`

**Wiring (lines 27-33):**
```java
@Controller @RequestMapping(value = "/open/user")
public class UserOpenController {
    @Autowired private IAuthOpenFacade authOpenFacade;   // ✅
    @Value("${zgBas.secret}") private String secretKey;  // D-P3-12 env-var placeholder
```
**SSO endpoints (D-P3-12):**
- `GET /open/user/ssoLogin` (lines 53-95): builds `com.spt.bas.web.shiro.SsoUsernamePasswordToken` (ported), calls `authOpenFacade.findUserById`, `SecurityUtils.getSubject().login(upt)`.
- `GET /open/user/resSsoLogin` (lines 107-147): accessToken variant, calls `authOpenFacade.findUserByLoginName`.

**Dependencies (all present):** `com.spt.bas.client.vo.api.SsoLoginRequestVo` + `ResSsoLoginRequestVo` ✅ already in zgbas-system; `com.spt.bas.web.shiro.ShiroUtil` + `SsoUsernamePasswordToken` ✅ ported with the shiro unit; auth-sdk ✅. Port verbatim.

---

### `ShiroPropConfig` → `zgbas-framework` (config binding)

**Source:** `/Users/alan/WorkSpace/IDEA/zgbas/web/src/main/java/com/spt/bas/web/config/ShiroPropConfig.java` (37 lines)
**Target:** `zgbas-framework/src/main/java/com/spt/bas/web/config/ShiroPropConfig.java`
```java
@Configuration
@ConfigurationProperties(prefix = "shiro.prop")
public class ShiroPropConfig {
    private String appCd;
    private String mockPassword;        // D-P3-07: dev default明文, prod ${ZGBAS_MOCK_PASSWORD:} placeholder
    private boolean sessionEnable = true;
    // getters/setters
}
```
NOTE: there is a SEPARATE inlined `com.spt.tools.shiro.config.ShiroProp` (bound by `ToolsShiroConfig @EnableConfigurationProperties`). The base `AbstractShiroDbRealm.mockLogin()` reads `shiroProp.getMockPassword()` from the **inlined** `ShiroProp`; ShiroDbRealm.isMockLogin reads `shiroPropConfig.getMockPassword()` from **this** `ShiroPropConfig`. Both bind `mockPassword` — keep config keys consistent. Port verbatim; populate both via `shiro.prop.*` in yml.

> **Module placement (corrected WARNING 2):** `ShiroPropConfig` lives in `zgbas-framework` (NOT admin) — it is `@Autowired` by `ShiroDbRealm` which compiles in framework. Placing it in admin would break framework compilation. The plan 03-01 correctly targets framework.

---

### WebSocket trio → `zgbas-framework`

**Sources:**
- `WebSocketConfig.java` (31 lines): `@Configuration @EnableWebSocket`, registers `IndexWebSocketServer` at `/fundSocket/`, exports `ServerEndpointExporter` bean.
- `IndexWebSocketServer.java` (173 lines): `@Component @ServerEndpoint("/indexWebSocket/{userId}")`, self-contained (only deps: fastjson + `com.spt.bas.web.ws.po.Message`).
- `WebSocketServer.java` (169 lines): `@Component @ServerEndpoint("/webSocket/{username}")`. **Business-coupled in `@OnOpen` (lines 91-115):** injects `IApproveWaitDealClient` (static + `@Autowired` setter) + `IAuthOpenFacade`, calls `iApproveWaitDealClient.findPageWaitDealCount()`.
- `ws/po/Message.java`: simple POJO → port to `zgbas-framework` (same module as the WS servers that depend on it).

**Dependency notes:**
- `IndexWebSocketServer` — ✅ compiles after porting `Message.java`. No business deps.
- `WebSocketServer` — ⚠️ same `IApproveWaitDealClient` Phase-4 blocker as IndexController. Per D-P3-11 ("WS先连上、推送业务发送方留Phase 4"), recommend porting but commenting/stubbing the `@OnOpen` approve-count block, OR `@Autowired(required=false)` on the static setter so the endpoint registers and connects (闲置) without a Phase-4 bean.
- **pom:** add `spring-boot-starter-websocket` (source `web/pom.xml` line 151). If WS classes live in `zgbas-framework`, the starter must be declared on **framework pom** (compile-time); runtime resolved via admin boot.

> **Module placement (corrected WARNING 2):** `Message.java` lives in `zgbas-framework` (NOT admin) — `IndexWebSocketServer` and `WebSocketServer` both compile in framework and directly reference `Message`. Placing it in admin would break framework compilation. The plan 03-03 correctly targets framework.

---

### `ZgbasApplication.java` — MODIFY (remove exclude)

**File:** `/Users/alan/WorkSpace/IDEA/zgbas-plus/zgbas-admin/src/main/java/com/spt/ZgbasApplication.java`
**Edit:** remove `com.spt.tools.shiro.config.ToolsShiroConfig.class` from the `excludeFilters` array (line 63). The other two excludes (`FeignConfig` x2) STAY. D-P3-01. After removal, `ToolsShiroConfig` auto-config engages, wiring `shiroFilter` / `securityManager` / `ehCacheManager` / filter chain — provided a concrete `AbstractShiroDbRealm` bean exists (the ported `ShiroDbRealm` @Component satisfies the `securityManager(... AbstractShiroDbRealm shiroDbRealm)` param at ToolsShiroConfig line 81).

```java
// BEFORE (line 59-64):
excludeFilters = @ComponentScan.Filter(
    type = FilterType.ASSIGNABLE_TYPE,
    classes = { com.spt.tools.http.feign.FeignConfig.class,
                com.spt.sign.client.config.FeignConfig.class,
                com.spt.tools.shiro.config.ToolsShiroConfig.class }   // ← REMOVE this line
)
// AFTER: only the two FeignConfig classes remain.
```

---

## Shared Patterns

### Configuration externalization (D-P2-13 / D-P2-14 / D-P3-07 / D-P3-12)

Apply to `application.yml` + `application-dev.yml` + `application-prod.yml` (in `zgbas-admin/src/main/resources/`). Pattern: dev profile keeps default明文 values (行为等价照搬旧项目); prod profile uses `${ENV:}` placeholders (empty default = feature off).

| Key | dev default (照搬旧明文) | prod placeholder |
|---|---|---|
| `shiro.prop.mockPassword` | 旧项目明文值 | `${ZGBAS_MOCK_PASSWORD:}` |
| `shiro.prop.appCd` | 旧 appCode 值 | `${ZGBAS_APP_CD:}` |
| `zgBas.secret` | 旧 sso secret (or local dev value) | `${ZGBAS_SECRET:}` |
| `auth.url` | local/old auth URL | `${AUTH_URL:}` |
| `spring.mvc.static-path-pattern` | `/static/**` (universal — same in dev+prod) | `/static/**` |
| `spring.thymeleaf.cache` | `false` (dev hot reload) | `true` (prod cache) |

**Existing reference pattern:** `ZgbasExternalBeansConfig.java` (framework) already reads `env.getProperty("spt.app.secretKey")` / `"auth.url"` via injected `Environment` — Phase 3 reuses the same keys; add the new `shiro.prop.*` + `zgBas.secret` keys alongside.

### External auth-sdk bean wiring (already present — reuse)

**Source:** `/Users/alan/WorkSpace/IDEA/zgbas-plus/zgbas-framework/src/main/java/com/spt/framework/config/ZgbasExternalBeansConfig.java` lines 38-45:
```java
@Bean
public IAuthOpenFacade authOpenFacade() {
    AuthOpenFacade http = new AuthOpenFacade();
    http.init(env.getProperty("spt.app.secretKey"),
             env.getProperty("spt.app.appCode"),
             env.getProperty("auth.url"));
    return http;
}
```
**Apply to:** ShiroDbRealm / ShiroService / ShiroUtil / IndexController / MyIndexController / UserOpenController / WebSocketServer all `@Autowired IAuthOpenFacade` → this single bean. No new bean needed. The bean is HTTP-injected (决策 #7, spt-auth stays external).

### Shiro filter-chain mechanism — NO DB TABLE (resolves D-P3-04 open question)

**Finding (cheaply verified by reading inlined config):** The filter chain does **NOT** read from a `sptbasdb_pd` DB table. No DDL is needed. D-P3-04 is **NOT a blocker**. The chain is built from two sources:

1. **Static defaults** — `ToolsShiroConfig.chainDefinitionSectionMetaSource()` (inlined, lines 153-168):
   ```java
   config.append("/login = authc").append(NEW_LINE).append("/logout = logout");
   ```
   + `ShiroChainMetaSource.initSection()` (inlined, line 28): `section.put("/**", "user");` and `IShiroSection.initDefault(section)`.

2. **External spt-auth menu HTTP call** — the ported `ShiroService.initMenu()` calls `authOpenFacade.findAllMenu(searchVo)` (HTTP, not local DB) and adds `perms[...]` entries per menu component.

So: chain = static rules + runtime HTTP to external spt-auth. `ddl-auto=none` (D-P2-02) is irrelevant to the filter chain. The only requirement is that the ported `ShiroService` @Component is present so `ShiroChainMetaSource` picks it up (`@Autowired(required=false) IShiroService`).

### Inlined assets already present (DO NOT re-port)

| Asset | Location | Used by |
|---|---|---|
| `ToolsShiroConfig` (auto-config, un-exclude to engage) | `zgbas-common/.../tools/shiro/config/` | Shiro wiring |
| `AbstractShiroDbRealm` (base Realm) | same | ShiroDbRealm extends it |
| Filters: `MyFormAuthenticationFilter`/`MyLogoutFilter`/`MyPermissionsAuthorizationFilter`/`MyRolesAuthorizationFilter` | `tools/shiro/filter/` | chain |
| `ShiroChainMetaSource` / `ChainDefinitionSectionMetaSource` / `IShiroSection` / `IShiroService` | `tools/shiro/` | chain build |
| `ShiroProp` (inlined, bound by ToolsShiroConfig) | `tools/shiro/config/` | mock password (base) |
| `com.spt.tools.shiro.util.ShiroUtil` (appCd, filterInited, isLogin, getProp) | `tools/shiro/util/` | base util |
| `ehcache-shiro.xml` | `zgbas-common/src/main/resources/` ✅ | EhCacheManager (ToolsShiroConfig line 93) |
| `BsDictUtil` / `BasConstants` / `BsDictData` / `com.spt.pm.*` / `com.spt.bas.client.vo.api.*` (557 files) | `zgbas-system` source | Realm/Controller compile deps |

---

## Bulk Resources (do NOT map individually)

| Bulk item | Source dir | Target dir | Count | Copy semantics |
|---|---|---|---|---|
| Templates | `/Users/alan/WorkSpace/IDEA/zgbas/web/src/main/resources/templates/` | `zgbas-admin/src/main/resources/templates/` | 608 files | Whole-dir recursive copy. Take `src/main/resources`, NOT `target/classes`. Includes `login.html`, `index.html`, `index-topnav.html`, `main.html`, `admin/`, `common/` (layout/fragment), etc. |
| Static JS/CSS | `/Users/alan/WorkSpace/IDEA/zgbas/web/src/main/resources/static/` | `zgbas-admin/src/main/resources/static/` | 390 JS + 352 CSS = 742 | Same. Template-internal `/static/...` references stay valid (no path rewrite). |
| Thymeleaf starter | `web/pom.xml` line 54-58 | `zgbas-admin/pom.xml` | 1 dep | Add `spring-boot-starter-thymeleaf` (D-P3-09). Templates are NOT compile-checked → `mvn compile` baseline unaffected. |
| WebSocket starter | `web/pom.xml` line 151-152 | `zgbas-framework/pom.xml` (if WS classes compile there) | 1 dep | Add `spring-boot-starter-websocket`. |

---

## No Analog / Deferred (planner must decide)

| File | Role | Reason / Recommendation |
|---|---|---|
| `IndexController` | controller | Phase-4 coupling (IPmProcessClient, IApproveWaitDealClient, WebParamUtils, web utils). Recommend **defer to Phase 4** OR port with `required=false` stubs. Do NOT silently照搬 — breaks D-P3-13 startup. |
| `MyIndexController` | controller | Phase-5 coupling (IRptIndexReportClient, WorkBenchCache, report VOs). Recommend **defer to Phase 5** (reports). |
| `WebSocketServer.@OnOpen` business block | component | Couples to `IApproveWaitDealClient` (Phase 4). Per D-P3-11 port the endpoint骨架 + stub the approve-count block (WS先连上闲置). |
| `com.spt.bas.web.util.*` (~25 files) | utility | Referenced by ShiroDbRealm (ServletUtils) + IndexController. `ServletUtils` is a hard ShiroDbRealm dep → must port at least `ServletUtils.java`. The rest ride with IndexController's decision. |
| `com.ruoyi.common.*` | external lib | Used by ShiroDbRealm (UserStatus, IpUtils) + IndexController (AjaxResult, ShiroConstants, Convert). Add `ruoyi-common` jar dep (4.7.2 in local repo) OR inline the few referenced classes. Planner to pick. |
| `WebParamUtils` (@Component) | service | Own business deps; rides with IndexController decision. |

---

## Open Questions for Planner (not blockers for this map)

1. **D-P3-04 resolved:** Shiro filter chain reads NO DB table — static rules + external spt-auth HTTP menu call. No DDL needed. ✅
2. **IndexController/MyIndexController scope:** CONTEXT lists them as照搬 targets, but they carry Phase 4/5 compile + runtime deps that break D-P3-13 startup. **Recommend user confirmation:** defer to Phase 4/5 (cleanest, matches D-P3-10 spirit) vs port-with-stubs (heavier, partial function). This is the single biggest planning decision for Phase 3.
3. **ruoyi-common strategy:** add jar dep vs inline 2-4 classes. Jar is simplest if license/availability OK (4.7.2 present locally).
4. **WebSocket target module:** CONTEXT says "framework/admin" ambiguously. Recommend `zgbas-framework` (infra, matches D-P2-06) + websocket starter on framework pom.

---

## Metadata

**Analog search scope:** legacy source `/Users/alan/WorkSpace/IDEA/zgbas/web/src/main/` (Java + resources); inlined assets `zgbas-common/.../com/spt/tools/**`; migrated source `zgbas-system/.../com/spt/bas/client/**` + `com/spt/pm/**`; framework configs; local repo `/Users/alan/App/Repository` (jar availability checks).
**Files scanned:** 11 source Java + 4 inlined dependency Java + 3 framework configs + 3 poms + bulk dir counts.
**Pattern extraction date:** 2026-07-16
