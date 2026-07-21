---
phase: 01-compile-skeleton
plan: 01
subsystem: build-skeleton
tags: [maven, spring-boot, skeleton, jdk8, monolith]
requires: []
provides:
  - "5-module Maven aggregator skeleton (compiles clean + boots empty context)"
  - "ALIGN-03 compile-stop-bleeding baseline (zero [ERROR] across all modules)"
  - "BUILD-04 single-process boot baseline (ZgbasApplication empty context)"
affects:
  - "Phase 2 (infrastructure) builds on this module topology + parent chain"
  - "Phase 4/5/6 land business/report/quartz code into the reserved module packages"
tech-stack:
  added:
    - "spring-boot-starter-parent 2.5.9 (grandparent, breaks old spt-parent chain)"
    - "spring-boot-starter-web (admin, embedded Tomcat)"
    - "spring-boot-starter-test (admin, context-load smoke)"
  patterns:
    - "Grandparent inheritance (module -> zgbas-plus root -> spring-boot-starter-parent:2.5.9)"
    - "dependencyManagement ${project.version} unifies internal module versions"
    - "Fat-jar-only-on-admin (spring-boot-maven-plugin declared only in zgbas-admin)"
key-files:
  created:
    - pom.xml
    - .gitignore
    - zgbas-common/pom.xml
    - zgbas-common/src/main/java/com/spt/tools/core/PackageMarker.java
    - zgbas-framework/pom.xml
    - zgbas-framework/src/main/java/com/spt/framework/PackageMarker.java
    - zgbas-system/pom.xml
    - zgbas-system/src/main/java/com/spt/system/PackageMarker.java
    - zgbas-quartz/pom.xml
    - zgbas-quartz/src/main/java/com/spt/quartz/PackageMarker.java
    - zgbas-admin/pom.xml
    - zgbas-admin/src/main/java/com/spt/ZgbasApplication.java
    - zgbas-admin/src/main/resources/application.yml
    - zgbas-admin/src/test/java/com/spt/ZgbasApplicationTest.java
decisions:
  - "Grandparent inheritance: root pom -> spring-boot-starter-parent:2.5.9 (D-02), no spt-parent/bas-parent/spt-tools-parent anywhere (D-01/D-03)"
  - "Module topology D-08: common<-none, framework<-common, system<-{common,framework}, quartz<-{common,framework,system}, admin<-all"
  - "Only zgbas-admin declares the boot repackage plugin (D-11); 4 library modules are plain jars"
  - "Common placeholder package com.spt.tools.core aligns to the Phase 2 spt-tools inline target"
metrics:
  duration: 7 min
  tasks_completed: 2
  files_created: 14
  completed: 2026-07-16
---

# Phase 1 Plan 01: Compile-Stop-Bleeding + Skeleton Summary

5-module Maven aggregator monolith skeleton (zgbas-admin/common/framework/quartz/system) inheriting spring-boot-starter-parent:2.5.9 as grandparent, compiling zero-error on JDK 1.8 and booting an empty Spring context (single @SpringBootApplication, no DataSource/SDK/business code).

## What Was Built

A greenfield 5-module Maven aggregator on the previously-empty zgbas-plus repo:

- **Root pom.xml** (`com.spt:zgbas-plus:1.0.0-SNAPSHOT`, packaging=pom) inherits `spring-boot-starter-parent:2.5.9` as grandparent. This breaks the old private parent chain (no `spt-parent`, no `bas-parent`, no `spt-tools-parent` BOM anywhere). Internal module versions are unified via `dependencyManagement` with `${project.version}` for the 4 library modules (admin is never depended upon, so it is excluded).
- **4 library modules** (common/framework/system/quartz), each a plain jar with a `PackageMarker` placeholder class. Dependencies wire the D-08 topology: framework<-common; system<-{common,framework}; quartz<-{common,framework,system}. `system` has NO reverse dependency on `quartz` (anti-cycle, D-09).
- **zgbas-admin** is the sole boot module: depends on all 4 internal modules + `spring-boot-starter-web` + `spring-boot-starter-test` (test scope), and is the only module declaring `spring-boot-maven-plugin` (repackage, D-11). No `layout=ZIP`, no `excludeGroupIds`, no dependency-copy plugin.
- **ZgbasApplication** (`com.spt`, bare `@SpringBootApplication` — no exclude, since classpath has no db starter) + minimal **application.yml** (port 8080, profile dev, no datasource/secrets) + **ZgbasApplicationTest** (`@SpringBootTest contextLoads()`).

The `common` placeholder package `com.spt.tools.core` is intentionally aligned to the Phase 2 spt-tools inline target.

## Verification Results

All Phase 1 acceptance gates pass (JDK8 = Corretto 1.8.0_482, settings = zg_settings.xml, locale = zh_CN so locale-independent `[ERROR]` prefix assertions used):

| Gate | Requirement | Result |
|------|-------------|--------|
| ALIGN-03 / BUILD-02 | `mvn clean compile` zero `[ERROR]` + BUILD SUCCESS | 0 `[ERROR]` lines, BUILD SUCCESS across all 6 reactor projects |
| BUILD-01 | Topology correct (D-08) | `dependency:tree` for admin shows common/framework/system/quartz; reactor builds in order common->framework->system->quartz->admin; system has no quartz edge |
| BUILD-03 | JDK 1.8 + SB 2.5.9 locked | root parent=2.5.9, java.version=1.8, mvn runs on Java 1.8.0_482 |
| BUILD-04 | Single-process empty-context boot | `contextLoads()` passes (Tests run: 1, Failures: 0, Errors: 0); `spring-boot:run` prints `Started ZgbasApplication in 0.501 seconds` with no `APPLICATION FAILED TO START` |
| BUILD-05 / D-11 | Fat jar only in admin | `find *.jar.original` matches only `zgbas-admin/target/`; 4 library modules are plain jars |
| D-01/D-02/D-03 | Parent chain broken | grep for spt-parent/bas-parent/spt-tools-parent in root pom = 0 |

Boot log confirms startup on `Java 1.8.0_482` with `dev` profile active. The repackaged admin artifact is `zgbas-admin-1.0.0-SNAPSHOT.jar` (with `.jar.original`).

## Deviations from Plan

### Auto-fixed Issues

**1. [Rule 3 - Blocking] Removed banned grep-token collisions from XML/YAML comments**
- **Found during:** Task 1 and Task 2 acceptance verification
- **Issue:** The plan's acceptance criteria do literal `grep` for forbidden tokens (e.g. `spt-parent`, `spring-boot-maven-plugin`, `excludeGroupIds`, `layout>ZIP`, `exclude`, `datasource`, `password`, `url`, `redis`, `nacos`). My XML/YAML/Javadoc *comments* mentioned several of these tokens descriptively (e.g. "No spring-boot-maven-plugin: library jar"), causing the strict grep checks to fail even though the actual POM/code configuration was correct.
- **Fix:** Reworded all comments to avoid the banned tokens — e.g. library-module comment changed to "Library jar: plain jar only, not a boot module, no fat-jar packaging."; root-pom comment changed "spt-parent chain" to "private parent chain"; admin-pom comment reworded to "no custom layout and no dependency-copy step"; ZgbasApplication Javadoc changed "No exclude" to "Bare annotation (no auto-config removal)"; application.yml header changed to "no db/cache/registry/config-store wiring".
- **Files modified:** pom.xml, zgbas-common/pom.xml, zgbas-framework/pom.xml, zgbas-system/pom.xml, zgbas-quartz/pom.xml, zgbas-admin/pom.xml, zgbas-admin/src/main/java/com/spt/ZgbasApplication.java, zgbas-admin/src/main/resources/application.yml
- **Commit:** folded into 91be8d7 (Task 1) and c5979fd (Task 2)

No functional deviation from the plan. All artifacts match RESEARCH.md § Code Examples (POC-verified source of truth) exactly in structure and configuration.

## Auth Gates

None — no authentication involved in Phase 1 (empty context, no endpoints/auth).

## Known Stubs

The 4 `PackageMarker` classes (common/framework/system/quartz) are intentional placeholders to avoid empty-source-directory edge cases in Maven/IDE. They are explicitly in scope for Phase 1 (D-06 "medium skeleton") and will be superseded by real infrastructure/business code in Phases 2-6. They do NOT flow to UI rendering and are not behavioral stubs.

No data-source-wired components, no empty/mock-data UI — the skeleton is an empty Spring context by design.

## Threat Flags

None. The threat model's two mitigate/accept dispositions (T-01-01: application.yml has no secrets — verified 0 banned tokens; T-01-02/T-01-SC: nexus HTTP / official Spring starters — accepted infra constraint) are honored. No new security surface beyond the plan's threat_model.

## Self-Check: PASSED

- All 14 created source files exist on disk (pom.xml, .gitignore, 5 module poms, 4 PackageMarkers, ZgbasApplication, application.yml, ZgbasApplicationTest).
- Both task commits exist in git history: 91be8d7 (skeleton), c5979fd (boot module).
- SUMMARY.md itself present.
