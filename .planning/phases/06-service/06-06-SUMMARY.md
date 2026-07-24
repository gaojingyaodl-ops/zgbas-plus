---
plan_id: 06-06
phase: 6
wave: 5
title: 编译门 — mvn compile -pl zgbas-system 零 [ERROR] (SC#4 / D-P6-04)
status: complete
commits: [fix-06-06-cascade-r1, 06-VERIFICATION]
requirements: [WX-SERVICE-01]
---

# 06-06 Summary

**What was built:** Phase 6 acceptance gate (D-P6-04: compile-only). Verified `zgbas-system` compiles with zero `[ERROR]`, plus 3 static assertion groups. **PASSED.** No source edits in this plan beyond the cascade-fix rounds required to reach green.

## Tasks Completed

| Task | Result |
|------|--------|
| Task 1 — compile `zgbas-system` zero ERROR | ✓ BUILD SUCCESS (0 errors, locale-agnostic grep empty) via `mvn compile -pl zgbas-system -am` |
| Task 2 — static assertions | ✓ xxl-job 0 hits / no private-jar residual import / 20 impl + 19 iface + PurchaseCommand + EweChatApi |

## Cascade resolution (the real work of this plan)

First run: **103 errors** → Phase-3 inline-completeness gaps forced into scope by P6 service migrations.

- **Pre-gate fixes** (discovered during 06-04/06-05 prep): inlined `AuthFaceRecognition` payload + WX-package `BsCompanyDao` (4 finders) — research §3 had wrongly claimed these services "reuse main-domain Dao".
- **Round 1** (`fix(06-06)`): inlined 6 wx.client types (WebApplicationMsg + 5 VOs) + 4 WX Daos (CompanyIndustry/Feedback/BsDict{Data,Type}) verbatim; added 2 CFCA deps (`cfca.etl.common:{etl-common,etl-uaclient}:3.19.4.4`) — research R2 "compile 无虞" was wrong. → 103 errors → 1.
- **Round 2**: the remaining error was `JwtUtil`/`TokenUtil.createToken` 5-arg — a stale `.m2` `zgbas-common-4.8.3` jar predating the Phase 4-04 overload fix. Resolved by `-am` (rebuild zgbas-common from source). → 1 error → **0**.

## Build-invocation note

The literal plan command (`-pl zgbas-system`, no `-am`) fails ONLY because the project's `zgbas-common` SNAPSHOT is not installed in `.m2` (a stale `4.8.3` release lingers). `-am` rebuilds reactor deps from source and is the correct single-module-compile invocation. Not a Phase 6 source defect.

## Output

- `.planning/phases/06-service/06-VERIFICATION.md` (status: passed) — serves as Phase 6 phase-level verification (D-P6-04: compile gate is the sole acceptance).

## Self-Check: PASSED — SC#4 / WX-SERVICE-01 achieved.
