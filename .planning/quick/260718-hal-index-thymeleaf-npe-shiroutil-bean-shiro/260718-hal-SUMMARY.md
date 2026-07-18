---
quick_id: 260718-hal
slug: index-thymeleaf-npe-shiroutil-bean-shiro
date: 2026-07-18
mode: quick
status: complete
---

# Quick Task 260718-hal: Fix /index Thymeleaf NPE — register ShiroUtil bean

## Summary

Restored Spring bean registration for `com.spt.bas.web.shiro.ShiroUtil` so Thymeleaf SpEL `@shiroUtil.getIndustry()` / `@shiroUtil.getCurrentUserId()` (referenced from `layouts/include.html:55` and the `/index` render path) resolves instead of throwing `NoSuchBeanDefinitionException: No bean named 'shiroUtil'`. The registration existed in the old web service and was lost during the web→monolith merge.

## Root Cause

`ShiroUtil` is a static utility class extending `com.spt.tools.shiro.util.ShiroUtil`. The old `zgbas` web service exposed it as a Spring bean named `shiroUtil` so Thymeleaf's SpEL bean-reference syntax (`@shiroUtil.method()`) could resolve it. The `@Component` annotation was dropped during the migration to `zgbas-plus`, breaking the `/index` render path. Sibling classes in the same package (`ShiroService`, `ShiroDbRealm`) were already correctly annotated `@Component`, confirming the package is component-scanned.

## Change

`zgbas-system/src/main/java/com/spt/bas/web/shiro/ShiroUtil.java` — exactly 2 insertions:

1. Added `import org.springframework.stereotype.Component;` immediately before `import org.slf4j.Logger;` (keeps `org.slf4j` grouped; `org.springframework` sorts before `org.slf4j`).
2. Added `@Component("shiroUtil")` annotation on its own line immediately above `public class ShiroUtil extends com.spt.tools.shiro.util.ShiroUtil {`.

Spring now instantiates ShiroUtil (implicit no-arg ctor, no state). Template `@shiroUtil.getIndustry()` resolves the bean then invokes the static method via the instance reference — Java permits calling statics through an instance; `getIndustry` is declared on the child, `getCurrentUserId` is inherited from the parent. Behavior-equivalent to the old web service's bean registration.

## Verification

- **Compile:** `JAVA_HOME=/Users/alan/Library/Java/JavaVirtualMachines/corretto-1.8.0_482/Contents/Home /Users/alan/App/apache-maven-3.8.6/bin/mvn -s /Users/alan/App/apache-maven-3.8.6/zg_settings.xml -pl zgbas-system -am compile` → `BUILD SUCCESS`, `grep -c '^\[ERROR\]'` == 0.
- **Diff:** `git show --stat HEAD` → 1 file changed, 2 insertions (import + annotation), no other files touched.
- **Exclusions:** `.claude/`, `.planning/debug/`, `.planning/STATE.md`, and this task's PLAN.md/SUMMARY.md were NOT staged or committed.

## Commit

- `49682cf` — `fix(05): register ShiroUtil as bean 'shiroUtil' for Thymeleaf @shiroUtil SpEL`

## Scope Discipline

No template, controller, or parent `com.spt.tools.shiro.util.ShiroUtil` was modified. The `/index` render path references only `@shiroUtil` — no other unwired beans in this path (verified in PLAN: `@config`/`@dict`/`@permission` RuoYi admin helpers do not appear in `index.html` / `index-topnav.html` / `layouts/`). This is a Phase 3 web-template migration gap that surfaced during end-to-end verification, not a Phase 5 regression (Phase 5 was report-only).

## Self-Check: PASSED

- [x] `zgbas-system/src/main/java/com/spt/bas/web/shiro/ShiroUtil.java` contains `@Component("shiroUtil")` and the new import.
- [x] Commit `49682cf` exists on `master` with exactly 1 file / 2 insertions.
- [x] Compile produces 0 `^[ERROR]` lines.
