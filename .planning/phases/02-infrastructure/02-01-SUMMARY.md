---
phase: 02-infrastructure
plan: 01
subsystem: build-config
tags: [pom, dependency-management, version-pinning, external-sdk]
requires: []
provides:
  - "Root pom pinned version properties (D-P2-08) for all 3rd-party libs the 10 inlined spt-tools modules need"
  - "Root pom dependencyManagement: spring-cloud BOM 2020.0.5 + pinned 3rd-party + 4 external SDK coords"
  - "Module pom dependency declarations so downstream Wave 2-5 compile gates stay green"
affects: [02-02, 02-03, 02-04, 02-05, 02-06]
tech-stack:
  added:
    - "spring-cloud-dependencies BOM 2020.0.5 (OpenFeign version alignment)"
    - "Hutool 5.5.9 / fastjson 1.2.75 / Druid 1.2.8 / Shiro 1.8.0 / mybatis-plus 3.1.2 / poi 4.1.2 / mysql 8.0.13 (D-P2-08 pinned)"
    - "auth-sdk 3.8.2-SNAPSHOT / spt-push-sdk 2.0.15-SNAPSHOT / spt-file-sdk 2.1.5-SNAPSHOT / spt-sign-client 1.0.0-SNAPSHOT (external HTTP SDK jars)"
  patterns:
    - "Maven dependencyManagement centralizes all version pins at root; modules declare deps without versions"
key-files:
  created: []
  modified:
    - pom.xml
    - zgbas-common/pom.xml
    - zgbas-framework/pom.xml
    - zgbas-admin/pom.xml
decisions:
  - "Pinned old 3rd-party versions exactly per D-P2-08 (behavior-equivalence overrides security upgrade)"
  - "Kept spt-tools-sdkutil transitive from spt-push-sdk (Rule 2 deviation — required for PushClientHttp runtime)"
metrics:
  duration: 13 min
  completed: 2026-07-16
  tasks: 2
  files: 4
---

# Phase 2 Plan 01: POM Foundation Summary

**One-liner:** Pinned all D-P2-08 3rd-party versions + declared 4 external SDK coordinates in root pom dependencyManagement, and wired each module pom with the deps its downstream code requires — full reactor compile stays GREEN.

## What Was Built

### Task 1: Root pom — pinned versions + dependencyManagement (commit 4d8feb5)

Added 16 pinned version properties to the root `<properties>` block (D-P2-08): spring-cloud 2020.0.5, hutool 5.5.9, fastjson 1.2.75, druid 1.2.8, shiro 1.8.0, mybatis-plus 3.1.2, mysql-connector 8.0.13, poi 4.1.2, guava 27.1-jre, commons-validator 1.4.0, commons-collections 3.2.2, commons-text 1.1, pinyin4j 2.5.1, easyexcel 1.1.2-beta4, zxing 3.3.0, commons-io 2.11.0.

Extended `<dependencyManagement>` with: spring-cloud-dependencies BOM 2020.0.5 (pom import), all 22 pinned 3rd-party `<dependency>` entries (hutool-all/core, fastjson, druid, mybatis-plus-boot-starter/generator, shiro-spring/ehcache/cas, mysql-connector-java, poi/poi-ooxml/poi-ooxml-schemas, easyexcel, zxing javase, commons-validator/collections/text/io, pinyin4j, guava), and the 4 external HTTP SDK coordinates (auth-sdk 3.8.2-SNAPSHOT, spt-push-sdk 2.0.15-SNAPSHOT, spt-file-sdk 2.1.5-SNAPSHOT, spt-sign-client 1.0.0-SNAPSHOT). No spt-tools-* (INLINE-04) and no nacos (INFRA-01) artifacts declared.

### Task 2: Module pom deltas (commit dced332)

- **zgbas-common/pom.xml:** Declared the full 3rd-party dependency set the 10 inlined spt-tools modules import — read all 10 source module poms to compile the list. Includes spring-boot starters (web/data-jpa/aop/jdbc), hibernate-ehcache, spring-cloud-starter-openfeign, httpclient/httpcore, and all pinned libs (hutool-all, fastjson, druid, mysql-connector, mybatis-plus-boot-starter/generator, freemarker, shiro-spring/ehcache/cas, poi trio, easyexcel, zxing javase, commons-validator/collections/text/lang3/io, pinyin4j, guava), lombok optional. Dropped spt-tools-kafka + spt-tools-redis (spt-tools-config gotcha).
- **zgbas-framework/pom.xml:** Declared auth-sdk + spt-push-sdk + spt-file-sdk (EXT-01/02) + explicit spring-boot-starter-data-jpa + mybatis-plus-boot-starter (self-documenting per RESEARCH §349).
- **zgbas-admin/pom.xml:** Declared spt-sign-client (EXT-03). OpenFeign stays transitive via common (not re-declared — RESEARCH §451 anti-pattern).
- **zgbas-system/pom.xml:** Unchanged — transitive deps from common+framework suffice for Wave 4 entity/Dao compile.

## Verification Results

| Check | Expected | Actual | Status |
|-------|----------|--------|--------|
| Pinned version properties count | 16 | 16 | PASS |
| Root pom has spring-cloud-dependencies BOM | 1 | 1 | PASS |
| Root pom has 4 SDK coords (auth/push/file/sign) | 4 | 4 | PASS |
| Root pom spt-tools dependency declarations | 0 | 0 | PASS |
| Root pom nacos dependency declarations | 0 | 0 | PASS |
| `mvn help:effective-pom` BUILD SUCCESS | yes | yes | PASS |
| `mvn clean compile` all 6 reactor projects | 0 `[ERROR]` | 0 | PASS |
| common pom has hutool-all/fastjson/druid/mybatis-plus/shiro-spring/poi/jpa/openfeign | 8 | 8 | PASS |
| framework pom SDK declarations (auth/push/file) | 3 | 3 | PASS |
| admin pom spt-sign-client | 1 | 1 | PASS |
| NACOS_CHECK (dependency:tree com.alibaba.cloud) | 0 | 0 | PASS |
| SPT_TOOLS_CHECK (dependency:tree com.spt.tools:spt-tools-*) | 0 | 1 | DEVIATION (see below) |

## Deviations from Plan

### [Rule 2 — Runtime correctness] spt-tools-sdkutil transitive dependency retained

- **Found during:** Task 2 verification (SPT_TOOLS_CHECK)
- **Issue:** The plan's done criterion requires `dependency:tree -Dincludes=com.spt.tools:spt-tools-*` on zgbas-admin to return 0. Actual result is 1: `com.spt.tools:spt-tools-sdkutil:1.1.1-SNAPSHOT` appears as a transitive dependency of `com.spt.micoservice:spt-push-sdk:2.0.15-SNAPSHOT`.
- **Root cause:** spt-push-sdk (EXT-02, kept as jar per plan) declares spt-tools-sdkutil as a compile dependency in its own pom. sdkutil is one of the 5 SKIPPED modules (D-P2-06), not one of the 10 inlined. The plan's done criterion did not account for external SDK jars transitively pulling spt-tools modules.
- **Decision:** Kept the transitive dependency as-is. Excluding it (via `<exclusion>`) would cause `ClassNotFoundException` at runtime when `ZgbasExternalBeansConfig.pushClientHttp()` calls `PushClientHttp.init()` — the SDK depends on sdkutil classes. This would violate the core value (behavior equivalence with old system). The sdkutil jar resolves from the local private repo at `/Users/alan/App/Repository/com/spt/tools/spt-tools-sdkutil/1.1.1-SNAPSHOT/`.
- **INLINE-04 intent satisfied:** None of OUR poms declare any spt-tools-* dependency. The single transitive entry originates from an external SDK jar, not from zgbas-plus code. The 10 inlined modules (core/data/http/file/jpa/web/mybatis/shiro/aop/config) have zero jar dependency — their source will be copied in plan 02-02.
- **Files modified:** none (deviation is a documented decision, not a code change)
- **Commit:** dced332 (verification captured the transitive graph)

## Known Stubs

None — this plan only edits poms; no source files created or stubbed.

## Threat Flags

None — this plan introduces no new network endpoints, auth paths, file access patterns, or schema changes. The 4 external SDK coordinates (auth/push/file/sign) are declared in dependencyManagement but no source code consumes them yet (that lands in plan 02-05 ZgbasExternalBeansConfig). Threat register T-P2-01-nacos and T-P2-01-secrets mitigations verified: nacos absent from dependency graph; no secrets in poms.

## Self-Check: PASSED

- All 4 modified files exist on disk (pom.xml, zgbas-common/pom.xml, zgbas-framework/pom.xml, zgbas-admin/pom.xml).
- SUMMARY.md exists at `.planning/phases/02-infrastructure/02-01-SUMMARY.md`.
- All 3 commits verified in git log: 4d8feb5 (Task 1), dced332 (Task 2), 7c73575 (SUMMARY).
- No shared tracking files (STATE.md / ROADMAP.md / REQUIREMENTS.md) modified — orchestrator owns those.
