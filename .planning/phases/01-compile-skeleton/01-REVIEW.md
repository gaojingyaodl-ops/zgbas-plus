---
phase: 01-compile-skeleton
reviewed: 2026-07-16T04:58:52Z
depth: standard
files_reviewed: 14
files_reviewed_list:
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
findings:
  critical: 0
  warning: 1
  info: 1
  total: 2
status: issues_found
---

# Phase 1: Code Review Report

**Reviewed:** 2026-07-16T04:58:52Z
**Depth:** standard
**Files Reviewed:** 14
**Status:** issues_found

## Summary

Phase 1 deliverable is a deliberately minimal 5-module Maven aggregator skeleton (`zgbas-admin/common/framework/quartz/system`) whose root parent inherits `spring-boot-starter-parent:2.5.9` as grandparent. Only `zgbas-admin` is a boot module (fat jar); the four library modules each hold a single `PackageMarker` placeholder.

Adversarial review at standard depth found **no blockers and no correctness/security defects**. The skeleton is sound:

- **POM topology is correct and cycle-free.** Dependency edges match the documented D-08 build order: `common -> framework -> system -> quartz -> admin`. `system` has no reverse edge on `quartz` (anti-cycle constraint honored). Root `dependencyManagement` pins all four library modules at `${project.version}`; admin is correctly omitted (nothing depends on it).
- **Plugin placement is correct.** Only `zgbas-admin` declares `spring-boot-maven-plugin`. The repackage execution is inherited from the starter-parent's `pluginManagement` (bound to the `package` phase), so omitting an explicit `<executions>` block is the documented, valid pattern — the fat jar is produced in admin only.
- **Version coherence holds.** `java.version=1.8` (inherited by all children via parent) is compatible with SB 2.5.9; starter-parent manages `spring-boot-starter-web`, `spring-boot-starter-test`, and the boot plugin versions.
- **The boot class reasoning is accurate.** `@SpringBootApplication` with no auto-config exclusion is correct here because no DB/DataSource starter is on the classpath, so `DataSourceAutoConfiguration` is not activated. (When Phase 2/3 adds a persistence starter, this assumption must be revisited.)
- **`application.yml` is clean.** No DataSource, no external SDK, no secrets, no accidental infra wiring — consistent with D-07 empty-context intent.
- **`PackageMarker` classes are well-formed** (`final` class + private constructor = non-instantiable utility idiom).
- **`ZgbasApplicationTest`** uses the canonical JUnit 5 / `@SpringBootTest` smoke-test pattern; the empty body is idiomatic (implicit assertion: context loads without throwing). Tracked `.java-version` (`1.8`) reinforces the BUILD-03 JDK lock — good practice.

Two non-blocking findings follow: one WARNING on `.gitignore` completeness (explicitly in scope for this review) and one INFO forward-looking note on the committed `dev` profile.

## Warnings

### WR-01: `.gitignore` is incomplete for a multi-IDE Maven/Java project

**File:** `.gitignore:1-13`
**Issue:** The ignore file covers `target/`, IntelliJ (`.idea/`, `*.iml`), logs, and `.DS_Store`, but omits several common Java/Maven/IDE artifacts that risk being committed:

- **Eclipse metadata** — `.classpath`, `.project`, `.settings/`, `bin/` (any contributor opening the project in Eclipse will generate these).
- **VS Code metadata** — `.vscode/` (workspace/launch settings).
- **IntelliJ alternate output** — `out/`.
- **JVM crash and heap dumps** — `hs_err_pid*`, `replay_pid*`, `*.hprof`. `*.hprof` in particular can grow to hundreds of MB and capture in-memory state (a data-leak vector once later phases hold secrets/PII in the heap).
- **Maven shade output** — `dependency-reduced-pom.xml` (will appear once a shade/flatten plugin is added in later phases).
- **Standalone compiled classes** — `*.class` (defensive; covers out-of-target compilation).

Completeness here matters because the repo is explicitly a shared multi-module project and the review brief asked for `.gitignore` completeness for a Maven/Java project. The `*.hprof` omission is the most defensible WARNING (size + leak risk), the rest are robustness gaps.

**Fix:** append the standard Java/Maven ignores (keep existing entries):

```gitignore
# Build output
target/

# IDE
.idea/
*.iml
.vscode/
.eclipse/
.classpath
.project
.settings/
bin/
out/

# JVM artifacts
*.class
hs_err_pid*
replay_pid*
*.hprof

# Maven
dependency-reduced-pom.xml

# Logs
logs/
*.log

# OS
.DS_Store
```

## Info

### IN-01: `spring.profiles.active: dev` hardcoded in committed config

**File:** `zgbas-admin/src/main/resources/application.yml:10-11`
**Issue:** The active profile is pinned to `dev` in the checked-in `application.yml`. This is acceptable for the Phase 1 skeleton, but once later phases introduce environment-specific wiring (DataSource, external SDKs, registry) a hardcoded active profile in the base file becomes a footgun — non-dev deployments inherit `dev` unless they remember to override it externally, and `dev` settings can mask missing prod config during local smoke runs.

**Fix:** No change required for Phase 1. Before Phase 2/3, externalize the profile via `SPRING_PROFILES_ACTIVE` env var (or `--spring.profiles.active=`) and split per-environment files (`application-dev.yml`, `application-prod.yml`). If a committed default is still desired, document that the base file is dev-only and that deployments must override it.

---

_Reviewed: 2026-07-16T04:58:52Z_
_Reviewer: Claude (gsd-code-reviewer)_
_Depth: standard_
