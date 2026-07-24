---
phase: 05
phase_name: carrier-layer (承托层迁入)
status: passed
verified: 2026-07-24
requirement: WX-BFF-03
verifier: inline orchestrator (gsd-verifier subagent unavailable — GLM gateway rejects Claude model codes)
method: goal-backward analysis against must_haves + requirement traceability
---

# Phase 5 Verification — 承托层迁入 (carrier-layer)

> Goal-backward verification. Note: the `gsd-verifier` subagent could not be spawned — this runtime (GLM gateway) rejects Claude model codes (`模型不存在`), so all subagent-based GSD agents fail at launch. Verification was performed inline by the orchestrator following the same goal-backward method.

## Phase Goal

> payload/VO/util/common/config/cache/AOP/ewechat 全量落 zgbas-system,为 service 与 BFF 提供稳定编译底座。

**Verdict: ACHIEVED.** Carrier layer fully landed in the `com.spt.bas.purchase.wx.server` enclave; reactor compiles clean (SC#3); Phase 6 has a stable compile base.

## Must-Haves / Success Criteria

| SC | Criterion | Evidence | Result |
|---|---|---|---|
| SC#1 | 逐类可独立解析,无孤立 import | 6 stubs non-stub (05-06 Task 1); orphan scan clean (only JwtUtil→WxAccessTokenService, which exists); EweChatApi/JinXinApi/PurchaseCommand correctly excluded to P6 | ✅ PASS |
| SC#3 | `mvn compile -pl zgbas-system` 零 `[ERROR]` + BUILD SUCCESS | `05-06`: BUILD SUCCESS, ERROR_COUNT=0 (locale-agnostic grep `^\[ERROR\]`) | ✅ PASS |
| SC#4 | D-15a/b 五类承托 inventory 交付 | `05-INVENTORY-CHECKLIST.md` — 5 classes, all must-port=Y landed | ✅ PASS |
| (implicit) | 4 P4 stub 形态消除(D-P5-14) | UserInfoVo/ApiResult/BaseException/SecurityException/ResponseUtil/UserContext 全部源实测(05-01/02/03) | ✅ PASS |
| (implicit) | 横切 bean 安全落位(不改写主域/不重复/不双跑) | GlobalExceptionHandler sole scoped @ControllerAdvice; CORS merged no dup multipart; ServiceAop self-limiting pointcut + explicit @Bean; FrameworkConfig dropped | ✅ PASS |
| (implicit) | 启动接线 + ThreadLocal 清理 | ApplicationStartup wires WX BsDictUtil.init (Phase-3 login-gap same-root fix); RequestListener→UserContext.removeUser | ✅ PASS |

## Requirement Traceability

**WX-BFF-03** — 辅助组件(payload/VO/util/AOP/cache/EweChatApi)全量迁入

| Sub-scope | Status | Note |
|---|---|---|
| payload (22) | ✅ migrated (05-01) | + swagger commented (off-classpath) |
| VO (18) | ✅ migrated (05-01) | UserInfoVo stub→source |
| enums (1) | ✅ migrated (05-01) | MessageEnums |
| common (8) | ✅ migrated (05-01) | ApiResult/BaseException stub→source; Status de-stubbed |
| util (16) | ✅ migrated (05-02) | ResponseUtil/UserContext stub→source; HttpUtils non-dead |
| AOP (ServiceAop) | ✅ migrated (05-03) | explicit @Bean, self-limiting pointcut |
| exception (3) | ✅ migrated (05-03) | GlobalExceptionHandler scoped |
| config (5 new) | ✅ migrated (05-03) | + WxCarrierConfig; FrameworkConfig dropped |
| cache (2) | ✅ migrated (05-04) | BsDictUtil/RedisCache |
| OCR wrapper (2) | ✅ migrated (05-04) | OcrUtils/OcrHelper, HTTP boundary |
| startup wiring | ✅ (05-05) | ApplicationStartup + RequestListener |
| **EweChatApi** | ⏭ Phase 6 (D-P5-18) | carrier deps (EweChatConfig/RedisCache/TemplateCardMessage) migrated; impl needs P6 IBuyMessageService |
| **JinXinApi** | ⏭ Phase 6 (D-P5-08) | JinXinConfig migrated; impl needs P6 UserInfoService + CFCA |

**WX-BFF-03 at carrier-layer scope: SATISFIED.** EweChatApi/JinXinApi deferrals are documented decisions (not gaps) — their carrier dependencies are in place, paving Phase 6.

## Deviations Resolved During Phase (all auto-fixed, Rule 1-3)

1. **WarehouseVo** (05-01) — P3 client.vo inventory miss; migrated verbatim.
2. **gui.ava:html2image** (05-02) — added to zgbas-system pom (ConvertUtils.html2Img used by P6 UserInfoService).
3. **IBsDictService interface** (05-04) — migrated iface only (BsDictUtil.getBean needs type); impl→Phase 6.
4. **swagger annotations** (05-01) — commented (off-classpath RESEARCH Q2), behavior-neutral.
5. **Status de-stub** (05-01) — was real body under stale stub comment; overwritten to source.

## Risks / Follow-ups (non-blocking)

- 🔴 **rotate-leaked-prod-credentials** (high/critical): 3 new WX secret sets (EweChat corpsecret, Aliyun OCR appcode, JinXin RSA private key + store passwords) committed plaintext per D-P5-05; registered in todo for rotation. JinXin RSA key = most sensitive.
- **Startup GREEN deferred to Phase 8** (SC: 启动 GREEN): BsDictUtil.getBean(IBsDictService.class) needs the P6 impl bean at runtime; compile is green, runtime startup verified in Phase 8.
- **IBsDictService impl** (Phase 6): interface in place; Phase 6 must not re-migrate it, only add impl + bean.
- **`/wx/contract` route conflict** (Phase 7): RptCtrContractApi vs basWx ContractController — must disambiguate before Phase 7 startup (carried from STATE blockers).

## Gates Run

| Gate | Result |
|---|---|
| Post-wave compile (each of 6 waves) | ✅ GREEN (final: 0 ERROR) |
| SC#3 authoritative compile (05-06) | ✅ BUILD SUCCESS, 0 ERROR |
| Schema-drift | ✅ no drift (carrier layer = POJOs/config, no entities) |
| Code review (gsd-code-review) | ⏭ SKIPPED — subagent outage (gsd-code-reviewer spawn fails on GLM gateway). Recommend `/gsd:code-review 05` when subagents are restored. |
| Regression tests | ⏭ N/A — no Maven test runner auto-detected; carrier layer added no tests; project tests are non-hermetic (need DB_PASSWORD/SPT_APP_SECRET). Compile gate is the carrier-layer quality bar. |

## Verdict

**Phase 5 PASSED.** Carrier layer migrated, SC#3 compile gate green, SC#4 inventory delivered, 4 P4 stubs eliminated, cross-cutting beans safely placed, startup wiring done. Phase 6 has a stable compile base. The two code-quality gates that depend on subagents (code-review, gsd-verifier) could not run due to the runtime subagent outage and are flagged for manual follow-up; the goal-backward verification above confirms the phase goal is achieved.
