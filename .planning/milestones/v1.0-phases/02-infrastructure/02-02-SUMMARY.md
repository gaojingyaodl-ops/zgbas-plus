---
phase: 02-infrastructure
plan: 02
subsystem: spt-tools-inline
tags: [inline, spt-tools, common, compile-gate, verbatim-copy]
requires: [02-01]
provides:
  - "spt-tools-core 70 files inlined verbatim into zgbas-common (com.spt.tools.core.*) — INLINE-01, the dependency root every other module needs"
  - "spt-tools-data/http/file 40 files inlined verbatim (com.spt.tools.{data,http,file}.*) — INLINE-02"
  - "com.spt.tools.data.util.DataSourceCreator.createDataSource(DataSourceConfig) — the prefix-agnostic Druid factory Wave 4 ZgbasDataSourceConfig consumes"
  - "com.spt.tools.http.feign.FeignConfig — Feign configuration bean for the cfca sign client (EXT-03)"
affects: [02-03, 02-04, 02-05, 02-06]
tech-stack:
  added:
    - "org.json:json 20170516 (spt-tools-core JsonUtil imports org.json.JSONObject/JSONArray; pinned from spt-parent D-P2-08)"
    - "io.jsonwebtoken:jjwt 0.7.0 monolithic (spt-tools-http TokenUtil uses old signWith(SignatureAlgorithm, String) API; D-P2-08; SB 2.5.9 does NOT manage jjwt)"
  patterns:
    - "Layered inline compile-gate: core alone → GREEN → data/http/file → GREEN (Phase 1 gotcha-cascade lesson; avoids 1200+ reference avalanche)"
    - "Verbatim package preservation com.spt.tools.* (D-P2-07) — zero renames, zero package rewrites, Phase 4 imports map 1:1"
    - "Pom-only fixes for missing deps; minimal method-body bridges for stale Spring API (source never recompiled vs SB 2.5.9)"
key-files:
  created:
    - "zgbas-common/src/main/java/com/spt/tools/core/** (70 files)"
    - "zgbas-common/src/main/java/com/spt/tools/data/** (17 files)"
    - "zgbas-common/src/main/java/com/spt/tools/http/** (15 files)"
    - "zgbas-common/src/main/java/com/spt/tools/file/** (8 files)"
  modified:
    - "pom.xml (root): +json.version +jjwt.version properties + dependencyManagement entries"
    - "zgbas-common/pom.xml: +org.json +jjwt dependency declarations"
decisions:
  - "Pinned org.json 20170516 + jjwt 0.7.0 to the old spt-parent versions (D-P2-08: behavior-equivalence overrides security upgrade)"
  - "Bridged 4 stale Spring API gaps with minimal method-body edits — the spt-tools source is stale (spt-parent moved to SB 2.5.9 but the inlined source was never recompiled); these are real defects fixed per plan's 'fix the minimal root cause' sanction"
  - "Ran mvn from worktree root ($WT_ROOT), NOT the orchestrator cwd — the plan's verify `cd /Users/.../zgbas-plus` resolves to the main repo, not the worktree (#3099 path-safety)"
metrics:
  duration: 17 min
  completed: 2026-07-16
  tasks: 2
  files: 110
---

# Phase 2 Plan 02: spt-tools Core + Data/HTTP/File Inline Summary

**One-liner:** Inlined spt-tools-core (70 files) then spt-tools-data/http/file (40 files) verbatim into zgbas-common preserving `com.spt.tools.*`, with a green compile gate between each layer and 6 minimal fixes (2 pom deps + 4 Spring API bridges) — delivering INLINE-01/02 and the DataSourceCreator + FeignConfig foundation for Wave 3+.

## What Was Built

### Task 1: spt-tools-core inline (layer 1, commit 9845599)

Copied 70 `.java` files verbatim from `/Users/alan/WorkSpace/IDEA/tools/spt-tools-core/src/main/java/com/spt/tools/core/` into `zgbas-common/src/main/java/com/spt/tools/core/` preserving `com.spt.tools.core.*` (D-P2-07). Removed the Phase 1 `PackageMarker.java` placeholder (superseded by the real core package — source has no PackageMarker).

Compile gate surfaced one missing dependency: `org.json` (spt-tools-core `JsonUtil` imports `org.json.JSONObject/JSONArray`). Per D-P2-08, pinned `org.json:json:20170516` (the version spt-parent declares) — added `json.version` property + dependencyManagement entry to root pom, and the dependency declaration to `zgbas-common/pom.xml`. Pom-only fix, source untouched.

Gate result: `mvn -pl zgbas-common -am compile` → BUILD SUCCESS, 0 `[ERROR]`, 71 class files generated.

### Task 2: spt-tools-data/http/file inline (layer 2, commit 7f221a4)

Copied 40 `.java` files verbatim (data 17, http 15, file 8) into `zgbas-common/src/main/java/com/spt/tools/{data,http,file}/` preserving `com.spt.tools.*` (D-P2-07).

Compile gate surfaced two defect classes:

1. **Missing dependency (pom fix):** `io.jsonwebtoken` — spt-tools-http `TokenUtil` uses the old monolithic jjwt API (`signWith(SignatureAlgorithm.HS512, secretKey)`). Pinned `io.jsonwebtoken:jjwt:0.7.0` (spt-parent's version; the new split jjwt-api/impl/jackson 0.11.2 removed the `signWith(SignatureAlgorithm, String)` overload, so the old monolithic artifact is required for behavior equivalence). Spring Boot 2.5.9 does NOT manage jjwt, so pinned explicitly in root dependencyManagement + declared in common pom.

2. **Stale Spring API (4 minimal source bridges):** The spt-tools source is stale — spt-parent moved to Spring Boot 2.5.9 but the inlined source was never recompiled against it. These are real defects in the copied source; per the plan's `phase2_specifics` ("fix the minimal root cause ... for a real defect in the copied source"), applied minimal method-body bridges (no renames, no package edits, no refactor of structure):
   - `SortImpl`: `super(Order[])` ctor removed in Spring Data 2.5 → `super(java.util.Arrays.asList(orders))` (fully-qualified, no import edit).
   - `PageRequestImpl`: added `Pageable.withPage(int)` override (new abstract method in Spring Data 2.5; returns null matching sibling stubs `next`/`first`/`previousOrFirst`).
   - `BasicErrorController`: `ErrorAttributes.getErrorAttributes(WebRequest, boolean)` → `(WebRequest, ErrorAttributeOptions)` (Spring Boot 2.3+ API change; behavior preserved via `Include.STACK_TRACE` when `includeStackTrace`); dropped stale `@Override` on `getErrorPath()` (removed from `ErrorController` in SB 2.3+).
   - `ServerErrorController`: dropped stale `@Override` on `getErrorPath()` (same `ErrorController` change).

Gate result: `mvn -pl zgbas-common -am compile` → BUILD SUCCESS, 0 `[ERROR]`.

## Verification Results

| Check | Expected | Actual | Status |
|-------|----------|--------|--------|
| Layer 1 (core) `mvn -pl zgbas-common -am compile` | 0 `[ERROR]` | 0 | PASS |
| Layer 2 (data/http/file) `mvn -pl zgbas-common -am compile` | 0 `[ERROR]` | 0 | PASS |
| core .java files under common | 70 (>=69) | 70 | PASS |
| data .java files under common | 17 (±1) | 17 | PASS |
| http .java files under common | 15 (±1) | 15 | PASS |
| file .java files under common | 8 (±1) | 8 | PASS |
| Total common spt-tools .java files | ~110 | 110 | PASS |
| DataSourceCreator.createDataSource present | yes | yes (1 match) | PASS |
| FeignConfig.java present | yes | yes | PASS |
| Package integrity (all `com.spt.tools.*`) | 100% | 100% (0 non-matching) | PASS |
| Full reactor `mvn compile` (no downstream breakage) | 0 `[ERROR]` | 0 | PASS |
| No nacos / no spt-tools jar in common dependency:tree | 0 | 0 | PASS |
| PackageMarker placeholder removed | yes | yes (Task 1) | PASS |

## Deviations from Plan

### [Rule 3 — blocking issue] Compile ran from worktree root, not the orchestrator cwd

- **Found during:** Task 1 compile gate.
- **Issue:** The plan's `<verify>` commands use `cd /Users/alan/WorkSpace/IDEA/zgbas-plus && mvn ...`. That path resolves to the **main repo**, not this worktree (`.../.claude/worktrees/agent-a7ddf378fcc2bc5e8`). The first compile run reported a false GREEN ("Compiling 1 source file" = the main repo's leftover PackageMarker) because the worktree's 70 new files were invisible to it.
- **Fix:** Ran every `mvn` from `$(git rev-parse --show-toplevel)` (worktree root). This is the documented worktree path-safety requirement (#3099 — absolute paths constructed from orchestrator cwd resolve to the main repo).
- **Files modified:** none (execution-path correction; all artifacts correctly landed in the worktree).
- **Commit:** none (process deviation).

### [Rule 1/2 — real defect in copied source] 4 stale Spring API bridges + 2 pinned pom deps

- **Found during:** Task 1 + Task 2 compile gates.
- **Issue A (Task 1):** `org.json` missing (spt-tools-core JsonUtil). Pom fix only.
- **Issue B (Task 2):** `io.jsonwebtoken` jjwt missing (spt-tools-http TokenUtil, old monolithic API). Pom fix only.
- **Issue C (Task 2):** 4 stale Spring API gaps — the spt-tools source predates the Spring Boot 2.5.9 / Spring Data 2021.0.8 API it now compiles against (source never recompiled after spt-parent upgraded to SB 2.5.9). Minimal method-body bridges applied (details in "What Was Built" Task 2). No packages renamed, no classes renamed, no structure refactored — honors D-P2-07's intent (Phase 4 imports map 1:1).
- **Decision basis:** The plan's `phase2_specifics` explicitly sanctions "fix the minimal root cause" for "a real defect in the copied source," distinct from the "missing dependency" pom-only case. The threat model (T-P2-02-deps) anticipated missing-dep errors; these API-bridge fixes extend that mitigation to stale-API defects.
- **Files modified:** pom.xml, zgbas-common/pom.xml (deps); SortImpl.java, PageRequestImpl.java, BasicErrorController.java, ServerErrorController.java (API bridges).
- **Commits:** 9845599 (Task 1, org.json), 7f221a4 (Task 2, jjwt + 4 bridges).

### Scope boundary respected

Did NOT inline any additional spt-tools modules to "fix" errors (jpa/web/mybatis/shiro/aop/config remain for Plan 02-03). Did NOT security-upgrade pinned deps (D-P2-08). Did NOT modify any copied file's package or class name.

## Known Stubs

None introduced by this plan. The copied spt-tools source contains pre-existing stub-style methods (e.g., `PageRequestImpl` returns null/0 for everything — inherited verbatim from source); these are not stubs this plan created and are out of scope (Rule: only auto-fix issues directly caused by the current task). The load-bearing exports (`DataSourceCreator.createDataSource`, `FeignConfig`, `ToolsCoreConfig`) are fully implemented.

## Threat Flags

None. This plan adds no new network endpoints, auth paths, or schema changes. It copies a trusted internal source tree (the migration origin, same codebase branch) and adds two well-known pinned libraries (org.json, jjwt) at the old spt-parent versions. Threat register T-P2-02-copy (verbatim copy integrity) mitigated by green compile gate; T-P2-02-deps (missing dependency) mitigated by layer-by-layer gates surfacing deps immediately.

## Self-Check: PASSED

- All 110 created .java files exist on disk under `zgbas-common/src/main/java/com/spt/tools/{core,data,http,file}/`.
- Root pom.xml + zgbas-common/pom.xml modified (org.json + jjwt).
- Both task commits verified in git log: 9845599 (Task 1), 7f221a4 (Task 2).
- `mvn -pl zgbas-common -am compile` GREEN (0 `[ERROR]`) after both layers.
- No shared tracking files (STATE.md / ROADMAP.md / REQUIREMENTS.md) modified — orchestrator owns those.
- No untracked files left behind.
