---
quick_id: 260718-hal
slug: index-thymeleaf-npe-shiroutil-bean-shiro
date: 2026-07-18
mode: quick
status: planned
---

# Quick Task 260718-hal: Fix /index Thymeleaf NPE — register ShiroUtil bean

<objective>
Post-login GET `/index` renders template `index` → `layouts/include.html:55` evaluates SpEL `@shiroUtil.getIndustry()` → `NoSuchBeanDefinitionException: No bean named 'shiroUtil'`. Root cause: `com.spt.bas.web.shiro.ShiroUtil` is a static utility with no `@Component`; the old web service registered it as a Spring bean named `shiroUtil` so Thymeleaf `@shiroUtil` SpEL resolves; that registration was lost in the web→monolith migration. Fix: annotate the class `@Component("shiroUtil")`. Confirmed the `/index` render path (index.html, index-topnav.html, layouts/) references ONLY `@shiroUtil` — no other unwired beans in this path.
</objective>

<root_cause_evidence>
- Stack: `TemplateInputException` ... `Exception evaluating SpringEL expression: "@shiroUtil.getIndustry()" (template: "layouts/include" - line 55, col 48)` → `NoSuchBeanDefinitionException: No bean named 'shiroUtil'`.
- `com.spt.bas.web.shiro.ShiroUtil` (zgbas-system): `public class ShiroUtil extends com.spt.tools.shiro.util.ShiroUtil` — NO `@Component`. Static methods only (`getIndustry()` line 62, inherits `getCurrentUserId()` from parent).
- Sibling classes in the same package ARE component-scanned: `ShiroService.java:26` and `ShiroDbRealm.java:55` both have `@Component` (ZgbasApplication.java:72 comment confirms `com.spt.bas.web.shiro` is scanned).
- Templates use `@shiroUtil.getCurrentUserId()` + `@shiroUtil.getIndustry()` (SpEL bean-reference `@beanName` → requires a bean). layouts/include.html:55 and one other line.
- `/index` render path templates reference ONLY `@shiroUtil` — verified `@config`/`@dict`/`@permission` (RuoYi admin helpers) do NOT appear in index.html / index-topnav.html / layouts/.
- Not a Phase 5 regression (Phase 5 was report-only); a Phase 3 web-template migration gap surfaced during end-to-end verification.
</root_cause_evidence>

<tasks>

## Task 1: Register ShiroUtil as bean "shiroUtil"

**file:** `zgbas-system/src/main/java/com/spt/bas/web/shiro/ShiroUtil.java`

**action:** Two surgical edits:
1. Add import immediately BEFORE the existing `import org.slf4j.Logger;` line (keeps org.slf4j grouped; alphabetical `org.springframework` < `org.slf4j`):
   ```java
   import org.springframework.stereotype.Component;
   import org.slf4j.Logger;
   ```
2. Add the annotation on its own line immediately ABOVE `public class ShiroUtil extends com.spt.tools.shiro.util.ShiroUtil {`:
   ```java
   @Component("shiroUtil")
   public class ShiroUtil extends com.spt.tools.shiro.util.ShiroUtil {
   ```

**rationale:** Spring instantiates ShiroUtil (implicit no-arg ctor, no state). Template `@shiroUtil.getIndustry()` / `@shiroUtil.getCurrentUserId()` resolve the bean then call the static methods via the instance reference (Java permits calling statics via instance; `getIndustry` is declared on the child, `getCurrentUserId` is inherited from parent). Behavior-equivalent to the old web service's bean registration.

**do NOT:** edit any template, IndexController, or any other file. Do NOT touch the parent `com.spt.tools.shiro.util.ShiroUtil`.

**done:** exactly 2 lines added to ShiroUtil.java (1 import + 1 annotation), nothing else.

## Task 2: Compile verification

**action:**
```bash
JAVA_HOME=/Users/alan/Library/Java/JavaVirtualMachines/corretto-1.8.0_482/Contents/Home \
/Users/alan/App/apache-maven-3.8.6/bin/mvn \
  -s /Users/alan/App/apache-maven-3.8.6/zg_settings.xml \
  -pl zgbas-system -am compile
```
Capture output; confirm `grep -c '^\[ERROR\]'` == 0 AND exit code 0. (`org.springframework.stereotype.Component` is already on the classpath via spring-context; ShiroDbRealm/ShiroService in the same package already use @Component, so compile risk is near-zero — insurance only.)

**done:** compile green; if ANY error, stop and report (do NOT commit broken code).

## Task 3: Atomic commit

**action:** Stage ONLY `zgbas-system/src/main/java/com/spt/bas/web/shiro/ShiroUtil.java` (explicit path, no `git add -A`/`.`). Commit (HEREDOC, no Co-Authored-By, no --no-verify):
```
fix(05): register ShiroUtil as bean 'shiroUtil' for Thymeleaf @shiroUtil SpEL

layouts/include.html:55 @shiroUtil.getIndustry() threw
NoSuchBeanDefinitionException — ShiroUtil is a static utility the old
web service exposed as a Spring bean named 'shiroUtil' for Thymeleaf
SpEL resolution; the registration was lost in the web->monolith merge.
@Component("shiroUtil") restores it (sibling ShiroService/ShiroDbRealm
in the same package are already @Component). /index render path uses
only @shiroUtil — no other unwired beans in this path.
```

**verify:** `git show --stat HEAD` shows exactly 1 file (ShiroUtil.java), 2 insertions.

**done:** single fix commit landed; `.claude/` and `.planning/debug/` untouched.

</tasks>

<exclusions>
Do NOT stage or commit:
- `.claude/`, `.planning/debug/`
- `.planning/STATE.md` (orchestrator commits in Step 8)
- this task's PLAN.md / SUMMARY.md (orchestrator commits in Step 8)
</exclusions>

<must_haves>
truths:
  - "ShiroUtil is annotated @Component(\"shiroUtil\") and will be registered as a bean"
  - "No template or controller is modified"
  - "zgbas-system -am compile produces 0 ^[ERROR] lines"
  - "Exactly one commit lands, touching only ShiroUtil.java"
artifacts:
  - 260718-hal-SUMMARY.md
key_links:
  - zgbas-system/src/main/java/com/spt/bas/web/shiro/ShiroUtil.java
</must_haves>
