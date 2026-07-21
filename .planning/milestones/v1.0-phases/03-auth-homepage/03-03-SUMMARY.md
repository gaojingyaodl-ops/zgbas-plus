---
phase: 03-auth-homepage
plan: 03
subsystem: framework-web
tags: [websocket, templates, static-resources, bulk-copy, stub]

# Dependency graph
requires:
  - phase: 03-auth-homepage
    plan: 01
    provides: Shiro auth chain (ShiroUtil/ShiroDbRealm), framework web utils, compile baseline
provides:
  - WebSocket endpoint trio in zgbas-framework (WebSocketConfig @EnableWebSocket + IndexWebSocketServer + WebSocketServer stub + Message POJO)
  - spring-boot-starter-websocket on framework pom
  - Full frontend UI surface: 608 Thymeleaf templates + 390 JS + 352 CSS in zgbas-admin/resources
affects: [03-04, 04-business-logic, 07-alignment]

# Tech tracking
tech-stack:
  added: [spring-boot-starter-websocket]
  patterns: [Phase-4 stub via comment-out with Phase 4 stub markers (D-P3-11), bulk resource verbatim copy from src/ not target/ (D-P3-08)]

key-files:
  created:
    - zgbas-framework/src/main/java/com/spt/bas/web/ws/WebSocketConfig.java
    - zgbas-framework/src/main/java/com/spt/bas/web/ws/IndexWebSocketServer.java
    - zgbas-framework/src/main/java/com/spt/bas/web/ws/WebSocketServer.java
    - zgbas-framework/src/main/java/com/spt/bas/web/ws/po/Message.java
    - zgbas-admin/src/main/resources/templates/ (608 files)
    - zgbas-admin/src/main/resources/static/ (2097 files: 390 JS + 352 CSS + images/fonts)
  modified:
    - zgbas-framework/pom.xml

key-decisions:
  - "WebSocketServer @OnOpen approve-count block stubbed by full comment-out (5 Phase 4 stub markers) — same IApproveWaitDealClient dependency as IndexController, same D-P3-10 degradation strategy"
  - "authOpenFacade field + setter retained in WebSocketServer despite unused @OnOpen — compiles cleanly, Phase 4 uncomments the block to re-engage"
  - "Bulk copy from src/main/resources confirmed exact counts: 608 templates / 390 JS / 352 CSS (matching plan expectations)"

patterns-established:
  - "Phase-4 stub via comment-out: when a @OnOpen/@PostConstruct block references unavailable Phase-N contracts, comment the entire block with '// Phase 4 stub: uncomment when X impl is ported' markers — the endpoint still registers and accepts connections (idle)"

requirements-completed: [AUTH-02]

# Metrics
duration: 2min
completed: 2026-07-16
---

# Phase 3 Plan 03: WebSocket Endpoints + Full Frontend Resources Summary

**WebSocket endpoint trio (Config + 2 Server + Message POJO) ported verbatim into zgbas-framework with WebSocketServer @OnOpen approve-count block stubbed (Phase 4 IApproveWaitDealClient), spring-boot-starter-websocket added — plus all 608 HTML templates + 742 JS/CSS bulk-copied from source web/src/main/resources into zgbas-admin — compiles zero-error on first attempt**

## Performance

- **Duration:** 2 min
- **Started:** 2026-07-16T14:55:26Z
- **Completed:** 2026-07-16T14:57:16Z
- **Tasks:** 2
- **Files created:** 4 Java + 2705 resources (2709 total)
- **Files modified:** 1 (framework pom.xml)

## Accomplishments

- **WebSocket trio ported** into `zgbas-framework/com.spt.bas.web.ws`:
  - `WebSocketConfig` (@Configuration @EnableWebSocket, registers IndexWebSocketServer at `/fundSocket/`, exports ServerEndpointExporter bean) — clean verbatim copy
  - `IndexWebSocketServer` (@Component @ServerEndpoint `/indexWebSocket/{userId}`) — clean verbatim copy, self-contained (fastjson + Message POJO only)
  - `Message` POJO (from/to/text/date/countReadFlg/countCompleteFlg + @JSONField) — clean verbatim copy
  - `WebSocketServer` (@Component @ServerEndpoint `/webSocket/{username}`) — verbatim copy with @OnOpen approve-count block fully commented out (Phase 4 stub: IApproveWaitDealClient + ApproveWaitDeal + ApproveWaitSearchVo imports, static field, setter, and the entire approve-count logic block from `searchVo` through `msg.setCountCompleteFlg()`). The WS endpoint registers, accepts connections, and sends the base onOpen message — the approve-count push stays idle until Phase 4
- **spring-boot-starter-websocket** added to framework pom (D-P3-11) — WS classes compile in framework, runtime resolved via admin boot
- **Full frontend UI surface copied** into `zgbas-admin/src/main/resources/`:
  - Templates: 608 files (login.html, index.html, index-topnav.html, main.html, admin/index.html, common/layout+fragment, all business-page templates)
  - Static: 390 JS + 352 CSS + images/fonts = 2097 total files
  - Source: `/Users/alan/WorkSpace/IDEA/zgbas/web/src/main/resources/` (src/, not target/classes — D-P3-08)
  - Template-internal `/static/...` references unchanged (no path rewrite needed)
- **authOpenFacade field + setter retained** in WebSocketServer despite @OnOpen no longer calling it — compiles cleanly (IAuthOpenFacade + SysDeptSdk are auth-sdk types visible to framework); Phase 4 uncomments the block to re-engage the full approve-count logic
- `mvn compile -pl zgbas-framework,zgbas-admin -am` BUILD SUCCESS, 0 `^\[ERROR\]` on first attempt

## Task Commits

Each task was committed atomically:

1. **Task 1: WebSocket trio + Message POJO + websocket starter** — `456c1cb` (feat)
2. **Task 2: Bulk copy 608 templates + 742 JS/CSS** — `5aad2ff` (feat)

## Files Created/Modified

**Created (zgbas-framework — WebSocket endpoints):**
- `zgbas-framework/.../com/spt/bas/web/ws/WebSocketConfig.java` — @Configuration @EnableWebSocket, ServerEndpointExporter bean + IndexWebSocketServer handler registration
- `zgbas-framework/.../com/spt/bas/web/ws/IndexWebSocketServer.java` — @ServerEndpoint `/indexWebSocket/{userId}`, self-contained WS endpoint
- `zgbas-framework/.../com/spt/bas/web/ws/WebSocketServer.java` — @ServerEndpoint `/webSocket/{username}`, @OnOpen approve-count stubbed (Phase 4)
- `zgbas-framework/.../com/spt/bas/web/ws/po/Message.java` — WS message POJO (fastjson @JSONField)

**Created (zgbas-admin — frontend resources):**
- `zgbas-admin/src/main/resources/templates/` — 608 HTML templates (login/index/admin/common/business pages)
- `zgbas-admin/src/main/resources/static/` — 2097 static files (390 JS + 352 CSS + images/fonts/icons)

**Modified:**
- `zgbas-framework/pom.xml` — added `spring-boot-starter-websocket`

## Decisions Made

- **WebSocketServer @OnOpen stub via full comment-out (not required=false):** Unlike IndexController (which used `@Autowired(required=false)` on fields), WebSocketServer uses a static field + `@Autowired` setter for `iApproveWaitDealClient`. The setter runs at startup and would fail if no bean exists (no `required=false`). The plan's approach: comment out the field, setter, and entire @OnOpen approve-count block so the WS class has zero references to `IApproveWaitDealClient`/`ApproveWaitDeal`/`ApproveWaitSearchVo`. The endpoint still registers and accepts connections — it just sends the base onOpen message without approve counts. Phase 4 uncomments the block.
- **authOpenFacade retained despite unused:** The `authOpenFacade` static field + `setAdminOpenFacade` setter are kept (not commented) because `IAuthOpenFacade` and `SysDeptSdk` are auth-sdk types visible to framework. Keeping them means Phase 4 only needs to uncomment the @OnOpen logic block — no field/setter changes needed. The unused field generates no compile error.

## Deviations from Plan

None — plan executed exactly as written. Both tasks compiled clean on first attempt. No gotcha cascade, no missing dependencies, no module-topology surprises. The stub pattern (comment-out with Phase 4 markers) worked exactly as the plan specified.

## Known Stubs

| Stub | File | Reason | Phase 4 Resolution |
|------|------|--------|--------------------|
| WebSocketServer @OnOpen approve-count block | zgbas-framework/.../ws/WebSocketServer.java | IApproveWaitDealClient + ApproveWaitDeal + ApproveWaitSearchVo are system-layer types invisible to framework; the approve-count push logic is Phase-4 scope | Uncomment the block (field, setter, imports, @OnOpen logic) — all 5 Phase 4 stub markers guide the uncomment locations |

The WebSocket endpoint is **not** inert — it registers, accepts connections, and sends the base onOpen message (date/to/text). Only the approve-count enrichment is stubbed. This matches D-P3-11 ("WS 先连上、闲置" — WS connects first, push business sender deferred to Phase 4).

## User Setup Required

None — no external configuration beyond existing Phase 2/3 env vars. The WebSocket starter resolves at runtime via admin boot. Templates resolve from classpath `templates/` via Thymeleaf (configured in Plan 03-02).

## Next Phase Readiness

- WebSocket endpoint skeleton is in place (idle connection, push business Phase 4)
- Full frontend UI surface (608 templates + 742 JS/CSS) is in zgbas-admin/resources
- **For Plan 03-04:** startup verification — Shiro chain wires, /login renders, /index renders with menu, WebSocket endpoints register
- **For Phase 4:** uncomment the WebSocketServer @OnOpen approve-count block (replace stub markers with real `IApproveWaitDealClient` bean) — alongside replacing the 3 IndexController stubs from Plan 03-02

## Self-Check: PASSED

All created files verified to exist; both task commits verified in git log; BUILD SUCCESS confirmed with 0 errors.

---
*Phase: 03-auth-homepage*
*Completed: 2026-07-16*
