---
status: passed
phase: 6-Service 层迁入
verified: 2026-07-24
gate: D-P6-04 (compile gate only)
requirements: [WX-SERVICE-01]
---

# Phase 6 Verification — SC#4 Compile Gate

**Verdict: PASSED** — `zgbas-system` compiles with zero `[ERROR]`.

## SC#4 — Compile Gate (D-P6-04, the sole acceptance gate)

**Command:**
```
JAVA_HOME=/Library/Java/JavaVirtualMachines/amazon-corretto-8.jdk/Contents/Home \
mvn compile -pl zgbas-system -am \
  --settings /Users/alan/App/apache-maven-3.8.6/zg_settings.xml
```

**Result:** `BUILD SUCCESS` — reactor zgbas-common ✓ / zgbas-framework ✓ / zgbas-system ✓.
**Locale-agnostic error grep** (`^\[ERROR\]` | `cannot find symbol` | `找不到符号` | `找不到合适的方法` | `不存在`): **empty (0 matches).**

> **Build-invocation note:** the literal plan command (`-pl zgbas-system`, no `-am`) fails on a stale inter-module dependency — `.m2` holds an old `zgbas-common-4.8.3` release jar that predates the Phase 4-04 `TokenUtil` 5-arg `createToken` overload (added in commit `1d62462`). `-am` rebuilds `zgbas-common` from source in the reactor, picking up the overload. This is a local `.m2` staleness artifact, NOT a Phase 6 source defect — `-am` is the correct way to compile a single module against its reactor dependencies. Runtime/bean/startup verification deferred to Phase 8 (D-P6-04).

## Cascade resolution (103 → 1 → 0 errors)

The first compile run surfaced the **Phase-3 inline-completeness gaps** that Phase 6's service migrations forced into scope (gotcha cascade, per memory `project_v13-compilefix-gotchas`). Resolved across two fix rounds:

| Round | Gap | Resolution |
|-------|-----|------------|
| Pre-06-04 | `AuthFaceRecognition` payload (JinXinApi/UserInfoService) | inlined verbatim (purchase-client/payload) |
| Pre-06-05 | WX-package `BsCompanyDao` (4 finders) needed by 3 services | inlined verbatim (research §3 had wrongly claimed "reuse main-domain") |
| Round 1 (06-06) | 6 wx.client types (WebApplicationMsg + 5 VOs) + 4 WX Daos (CompanyIndustry/Feedback/BsDict{Data,Type}) + CFCA deps | inlined verbatim + added `cfca.etl.common:{etl-common,etl-uaclient}:3.19.4.4` (research R2 "compile 无虞" was wrong) |
| Round 2 | `JwtUtil`/`TokenUtil` stale `.m2` | resolved via `-am` (rebuild zgbas-common from source) |

## Static Assertions

| Assertion | Result |
|-----------|--------|
| ① xxl-job residue in `com.spt.bas.purchase.wx.*` | **0 hits** (PurchaseCommand scrubbed clean, D-P6-02) |
| ② `purchase-client` private-jar residual import in `service/` | **0** (`com.spt.bas.purchase.wx.client.*` is in-source embedded; regex excludes via `.bas.` prefix) |
| ③ Structure: `service/impl/` ≥ 19 | **20** (19 migrated + WxAccessToken[P5]) |
| ④ Structure: `service/I*Service` ≥ 18 | **19** (18 migrated + IWxAccessToken[P5]) |
| ⑤ `command/PurchaseCommand` + `ewechat/EweChatApi` present | **1 + 1 ✓** |

## What this verifies

- All 19 migrated service impls + 18 interfaces + EweChatApi + PurchaseCommand(scrubbed) compile and all dependencies resolve in the monolith.
- 14 `extends BaseService<T>` entity services + 5 plain `implements IXxxService` + JinXinApi(`extends CommonUtil`) + EweChatApi(`@Component`) + PurchaseCommand(`implements ICommand`) all type-check.
- Feign self-loop (方案1) preserved — all main-domain `I*Client` + 电签 `ISign*Client` resolve (28 + 3, zero gap).
- BaseService/IBaseService/IDataService untouched (D-P6-01); main-domain Daos untouched.

## Explicitly NOT verified this phase (deferred → Phase 8, D-P6-04)

Bean wiring / startup GREEN / `/wx/*` non-404 / WX Feign self-loop runtime proof / ServiceAop hit / CFCA & 金信 HTTP runtime / scheduled-trigger (PurchaseCommand/SignContractTask → v1.3 quartz gap-closure).
