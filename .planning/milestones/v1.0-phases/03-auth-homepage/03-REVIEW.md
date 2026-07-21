---
phase: 03-auth-homepage
reviewed: 2026-07-16T16:30:00Z
depth: standard
files_reviewed: 28
files_reviewed_list:
  - zgbas-admin/src/main/java/com/spt/ZgbasApplication.java
  - zgbas-admin/src/main/java/com/spt/bas/web/controller/IndexController.java
  - zgbas-admin/src/main/java/com/spt/bas/web/controller/LoginController.java
  - zgbas-admin/src/main/java/com/spt/bas/web/open/apply/UserOpenController.java
  - zgbas-admin/src/main/java/com/spt/bas/web/util/CookieUtils.java
  - zgbas-admin/src/main/java/com/spt/bas/web/util/DateUtils.java
  - zgbas-admin/src/main/java/com/spt/bas/web/util/EasyTreeUtil2.java
  - zgbas-admin/src/main/java/com/spt/bas/web/util/WebParamUtils.java
  - zgbas-admin/src/main/resources/application.yml
  - zgbas-admin/src/main/resources/application-dev.yml
  - zgbas-admin/src/main/resources/application-prod.yml
  - zgbas-admin/src/test/java/com/spt/ZgbasApplicationTest.java
  - zgbas-framework/src/main/java/com/spt/bas/web/util/CharsetKit.java
  - zgbas-framework/src/main/java/com/spt/bas/web/util/Convert.java
  - zgbas-framework/src/main/java/com/spt/bas/web/util/ServletUtils.java
  - zgbas-framework/src/main/java/com/spt/bas/web/util/StrFormatter.java
  - zgbas-framework/src/main/java/com/spt/bas/web/util/StringUtils.java
  - zgbas-framework/src/main/java/com/spt/bas/web/ws/IndexWebSocketServer.java
  - zgbas-framework/src/main/java/com/spt/bas/web/ws/WebSocketConfig.java
  - zgbas-framework/src/main/java/com/spt/bas/web/ws/WebSocketServer.java
  - zgbas-framework/src/main/java/com/spt/bas/web/ws/po/Message.java
  - zgbas-system/src/main/java/com/spt/bas/web/config/ShiroPropConfig.java
  - zgbas-system/src/main/java/com/spt/bas/web/shiro/ShiroDbRealm.java
  - zgbas-system/src/main/java/com/spt/bas/web/shiro/ShiroService.java
  - zgbas-system/src/main/java/com/spt/bas/web/shiro/ShiroUtil.java
  - zgbas-system/src/main/java/com/spt/bas/web/shiro/SsoUsernamePasswordToken.java
  - zgbas-system/src/main/java/com/spt/bas/client/remote/IApproveWaitDealClient.java
  - zgbas-system/src/main/java/com/spt/bas/client/remote/IPmProcessClient.java
findings:
  critical: 0
  warning: 7
  info: 4
  total: 11
status: issues_found
---

# Phase 3: Code Review Report

**Reviewed:** 2026-07-16T16:30:00Z
**Depth:** standard
**Files Reviewed:** 28 (source files only; ~2700 bulk-copied vendor static assets excluded)
**Status:** issues_found

## Summary

Phase 3 ports the Shiro login/auth chain + dynamic-menu homepage verbatim (照搬) from source `zgbas/web` into the monolith. The port is faithful: all Java source files match the source project byte-for-byte (verified via side-by-side comparison), and the port-introduced changes (stub null-guards, WebSocket comment-out, module placement, config keys) are correctly implemented.

**Verdict:** No BLOCKER-level defects introduced by the port itself. The IndexController stub-port pattern (`@Autowired(required=false)` + ternary null-guards) is complete and correct — every Phase-4 stub field is null-guarded at every call site, and the menu rendering path (`authOpenFacade.findAllMenu`) is fully independent of the stubs. The WebSocketServer comment-out approach correctly eliminates all references to `IApproveWaitDealClient`/`ApproveWaitDeal`/`ApproveWaitSearchVo` without altering the endpoint registration.

All 7 WARNING findings are **pre-existing source bugs carried verbatim** — the project's core value ("behavior equivalence") requires these to be ported as-is. They are flagged here for awareness and potential hardening in a future security pass, not as port fidelity failures. The 4 INFO findings are quality issues inherited from the RuoYi-derived source code.

**Known/accepted (per `<already_known_do_not_re_flag>`, not re-flagged):**
- D-P3-13 startup test non-hermeticity (`DB_PASSWORD` / `SPT_APP_SECRET` no default) — accepted per Option 4
- Mock-password backdoor (`super:<mock-password>`) — deliberate verbatim port, OFF in prod
- Historical dev DB password leak in git history (CR-01) — deferred cross-phase debt

## Warnings

### WR-01: Open redirect in UserOpenController SSO endpoints

**File:** `zgbas-admin/src/main/java/com/spt/bas/web/open/apply/UserOpenController.java:66,86,115,138`
**Issue:** The `redirectUrl` parameter is user-controlled and directly concatenated into a Spring redirect without validation. An attacker can craft `redirectUrl=https://evil.com` to redirect authenticated users to malicious sites after SSO login. This pattern appears 4 times across `ssoLogin()` and `resSsoLogin()`. This is a verbatim port from source (confirmed identical at source line 66/86/115/138) — it is a genuine OWASP Open Redirect vulnerability that the source project also carries.
**Fix:** Validate that `redirectUrl` is a relative path before redirecting. Example:
```java
String decoded = URLDecoder.decode(vo.getRedirectUrl(), "utf8");
if (!decoded.startsWith("/")) {
    decoded = "/index"; // reject absolute URLs
}
return "redirect:" + decoded;
```
Note: applying this fix deviates from strict "behavior equivalence" — coordinate with the user if this should be a behavior-equivalence exception.

### WR-02: NPE in doGetAuthorizationInfo when user lookup returns null

**File:** `zgbas-system/src/main/java/com/spt/bas/web/shiro/ShiroDbRealm.java:219-221`
**Issue:** In the authorization callback, `authOpenFacade.findUserByLoginName(userLoginVo)` can return null (e.g., user deleted between auth and authz, or external spt-auth returns empty). Line 221 immediately dereferences `user.getUserId()` without a null check, causing a `NullPointerException`. This NPE propagates through Shiro's authorization layer and can cause 500 errors on permission checks. Verbatim from source (source line 219-221 identical).
**Fix:**
```java
SysUserSdk user = authOpenFacade.findUserByLoginName(userLoginVo);
if (user == null) {
    return new SimpleAuthorizationInfo(); // empty perms = no access
}
List<SysRoleSdk> roles = authOpenFacade.findRoleByUserId(user.getUserId());
```

### WR-03: NPE in IndexController when no cookies are present

**File:** `zgbas-admin/src/main/java/com/spt/bas/web/controller/IndexController.java:113-114`
**Issue:** `ServletUtils.getRequest().getCookies()` returns `null` when the client sends no cookies (e.g., first visit, clean session, API client). The for-each loop `for (Cookie cookie : cookies)` on line 114 throws `NullPointerException`. This is the `/index` homepage — the primary landing page after login. A fresh browser session or any client that doesn't send cookies on the first request to `/index` will get a 500 error. Verbatim from source (source line 112-113 identical).
**Fix:**
```java
Cookie[] cookies = ServletUtils.getRequest().getCookies();
if (cookies != null) {
    for (Cookie cookie : cookies) {
        // ...
    }
}
```

### WR-04: NPE risk in UserOpenController exception handling

**File:** `zgbas-admin/src/main/java/com/spt/bas/web/open/apply/UserOpenController.java:79,88,130,140`
**Issue:** `ex.getMessage().startsWith("msg:")` is called on caught exceptions. `Throwable.getMessage()` returns `null` for exceptions constructed without a message (common for NPEs, some RuntimeException subclasses). Calling `.startsWith()` on null throws a secondary `NullPointerException` that masks the original error. This pattern appears 4 times. Verbatim from source (identical lines).
**Fix:** Use null-safe check:
```java
String msg = ex.getMessage();
if (msg == null || !msg.startsWith("msg:")) {
    ex = new AuthenticationException("msg:授权令牌错误，请联系管理员。");
}
```

### WR-05: Plaintext SSO shared secret committed in application-dev.yml

**File:** `zgbas-admin/src/main/resources/application-dev.yml:55`
**Issue:** `zgBas.secret: DUHZOTTIUTWNDJFRGLUKSHBGVBKWJRUY` — a 32-character SSO shared secret is committed in plaintext. This secret is used by `ShiroDbRealm.reLoginSso()` for SSO ticket generation/verification: `Md5Encrypt.encrypt(secretKey + username + date)`. Anyone with repo access can forge SSO login tickets for any user (the ticket algorithm is MD5(secret + username + yyyy-MM-dd), giving a 24-hour replay window). The value matches the source project's default `application.properties` (verified). The prod profile correctly externalizes via `${ZGBAS_SECRET:}` with a different prod secret. This is NOT covered by the `already_known` block (which only covers mock-password and DB password). While it's a correct verbatim port, committing a live shared secret in plaintext violates security best practices.
**Fix:** Externalize even in dev:
```yaml
zgBas:
  secret: ${ZGBAS_SECRET:dev-only-placeholder-rotate-me}
```

### WR-06: Plaintext password logging in IndexController.changePwd

**File:** `zgbas-admin/src/main/java/com/spt/bas/web/controller/IndexController.java:343`
**Issue:** `logger.info("修改密码：{},{},{}", userId, oldPwd, newPwd)` — logs both old and new plaintext passwords at INFO level. This violates the project's security rules ("Never log passwords, tokens, or PII"). Passwords in log files are a credential exposure vector. Verbatim from source (source line 343 identical).
**Fix:**
```java
logger.info("修改密码：userId={}", userId);
```

### WR-07: NPE in ServletUtils.checkAgentIsMobile when User-Agent header is null

**File:** `zgbas-framework/src/main/java/com/spt/bas/web/util/ServletUtils.java:163`
**Issue:** `ua.contains("Windows NT")` is called without null-checking `ua`. When the `User-Agent` header is absent (curl, custom clients, some proxies), `getHeader("User-Agent")` returns null. Called from IndexController lines 96 and 110 on every `/index` request. Verbatim from source.
**Fix:**
```java
public static boolean checkAgentIsMobile(String ua) {
    if (ua == null || ua.isEmpty()) {
        return false;
    }
    // ...
}
```

## Info

### IN-01: IndexWebSocketServer mixes incompatible WebSocket APIs

**File:** `zgbas-framework/src/main/java/com/spt/bas/web/ws/IndexWebSocketServer.java:27`
**Issue:** The class extends `TextWebSocketHandler` (Spring WebSocket framework API) AND is annotated `@ServerEndpoint` (JSR-356 API). These are two different WebSocket integration approaches. The `@ServerEndpoint` annotations take effect via `ServerEndpointExporter`; the `extends TextWebSocketHandler` is dead code — none of its methods (`afterConnectionEstablished`, `handleTextMessage`, etc.) are overridden or invoked. The class also uses `@OnOpen`/`@OnClose`/`@OnMessage`/`@OnError` (JSR-356 lifecycle), not the Spring WebSocket lifecycle. Verbatim from source — the source author likely copy-pasted from a Spring WebSocket tutorial and added `@ServerEndpoint` on top.
**Fix:** Remove `extends TextWebSocketHandler` and the unused import `org.springframework.web.socket.handler.TextWebSocketHandler`. No behavioral change.

### IN-02: System.out.println debug artifacts in IndexWebSocketServer

**File:** `zgbas-framework/src/main/java/com/spt/bas/web/ws/IndexWebSocketServer.java:98,117`
**Issue:** Two `System.out.println` statements in `@OnOpen` and `@OnClose` write connection logs to stdout instead of using the SLF4J logger (which is already declared as a field on line 28). These produce unstructured console output that bypasses log aggregation and level controls. Verbatim from source — the sibling `WebSocketServer` class uses commented-out `System.out.println` (source chose to comment them out but left these active in `IndexWebSocketServer`).
**Fix:** Replace with `logger.info(...)` or remove if the connection count is already tracked by `AtomicInteger`.

### IN-03: e.printStackTrace() used instead of proper logging across multiple files

**Files:** `WebSocketServer.java:66,74,156`; `IndexWebSocketServer.java:155`; `CookieUtils.java:73,126`; `ServletUtils.java:119`
**Issue:** Multiple `e.printStackTrace()` calls write stack traces to stderr, bypassing the logging framework. In production, these may be lost (no stderr capture), clutter container logs unpredictably, and expose stack traces in accessible log output. The project standard is SLF4J (`LoggerFactory`).
**Fix:** Replace with `logger.error("description", e)`.

### IN-04: ShiroDbRealm exception logging may suppress stack trace

**File:** `zgbas-system/src/main/java/com/spt/bas/web/shiro/ShiroDbRealm.java:123`
**Issue:** `log.error("登录失败：{}", e)` passes the exception as a format argument to `{}`. In SLF4J, a `Throwable` passed as the last argument WITHOUT a matching `{}` placeholder triggers stack trace logging. Here, the exception IS matched to `{}`, so SLF4J calls `e.toString()` (class + message only) and the full stack trace is suppressed. This makes diagnosing authentication failures more difficult.
**Fix:** Separate the exception from the format string:
```java
log.error("登录失败", e);
```

---

_Reviewed: 2026-07-16T16:30:00Z_
_Reviewer: Claude (gsd-code-reviewer)_
_Depth: standard_
