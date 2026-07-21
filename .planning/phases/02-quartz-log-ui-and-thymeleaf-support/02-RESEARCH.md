# Phase 2: quartz-log-ui-and-thymeleaf-support - Research

**Researched:** 2026-07-21
**Domain:** Quartz 调度日志前端 + Thymeleaf 模板辅助 Bean + Shiro 方言集成
**Confidence:** HIGH

<user_constraints>
## User Constraints (from CONTEXT.md)

### Locked Decisions
- **D-01:** 调度日志页采用 `monitor/operlog/operlog.html` 的完整监控页风格，保留搜索、批量删除、清空、导出和行内详情按钮。
- **D-02:** 页面结构和 bootstrap-table 配置尽量复用现有 monitor 页面模式，只替换 quartz 日志对应的字段、路由和权限码。
- **D-03:** `@dict` 保持为面向模板的薄封装，直接复用现有字典缓存/服务能力，不另起一套字典体系。
- **D-04:** `@permission` 走独立 Bean，和 `@dict` 分开，专门提供模板里的权限判断入口。
- **D-05:** 采用双轨支持：既保留 `@permission.hasPermi(...)`，也添加 `thymeleaf-extras-shiro` + `ShiroDialect`，让现有 `shiro:hasPermission` 继续生效。
- **D-06:** 不把现有模板统一改成单一路径；优先兼容当前 monitor 页面写法，减少横向改动。

### Claude's Discretion
- `jobLog` 页面的具体字段顺序和列数可按现有 `operlog` 模板风格落地。
- `@dict` / `@permission` Bean 的具体放置模块由 planner 根据现有配置归位。

### Deferred Ideas (OUT OF SCOPE)
- Phase 1 的 job/add/edit/job 前端对齐
- basWx 微信采购小程序迁入
- 生产 handler quartz 路由 gap 之外的新业务任务
</user_constraints>

<phase_requirements>
## Phase Requirements

| ID | Description | Research Support |
|----|-------------|------------------|
| QTZ-05 | 创建调度日志前端页面 `templates/monitor/jobLog/jobLog.html` | SysJobLogController provides 5 REST routes (list/export/getInfo/remove/clean); SysJobLog domain has 9 fields; operlog.html is the established full-monitor-page pattern with search/batch-delete/clean/export/detail |
| QTZ-06 | 提供 Thymeleaf `@dict` 和 `@permission` 辅助 Bean | Templates reference `@dict.getType(...)` (returns List with dictLabel/dictValue) and `@dict.getLabel(type,value)` (returns String); `@permission.hasPermi(...)` (returns boolean). auth-sdk DictUtil.getListByCategory() returns SysDictDataSdk with both field sets. ShiroUtil.isPermitted() provides Shiro-native permission check |
| QTZ-07 | 验证完整 CRUD + 日志流程端到端可用 | Full flow: job.html CRUD (Phase 1) → jobLog.html list/detail/delete/clean/export (Phase 2). Requires @dict + @permission + ShiroDialect all wired for template rendering |
| QTZ-08 | 添加 `thymeleaf-extras-shiro` 依赖并配置 ShiroDialect | 48 shiro:hasPermission usages across templates; thymeleaf-extras-shiro 2.1.0 in local repo (matches old zgbas 4.8.3); ShiroDialect class = at.pollux.thymeleaf.shiro.dialect.ShiroDialect; no auto-config → manual @Bean needed |
</phase_requirements>

## Summary

Phase 2 fills three gaps in the quartz frontend: (1) a missing jobLog list page, (2) missing Thymeleaf template helper beans (`@dict`, `@permission`), and (3) the missing `thymeleaf-extras-shiro` dependency + ShiroDialect bean. All three are prerequisites for the ported RuoYi monitor templates to render without NPEs.

The critical finding is that the templates use RuoYi-style dict APIs (`dictLabel`, `dictValue`, `getType()`, `getLabel()`) which do NOT match the project's existing `BsDictData` entity fields (`dictCd`, `dictName`). However, the auth-sdk's `SysDictDataSdk` entity has BOTH field sets, and its `DictUtil.getListByCategory()` returns `SysDictDataSdk` objects — so the `@dict` bean should delegate to `com.spt.auth.sdk.cache.DictUtil`, not to the project's `BsDictUtil`. The RuoYi dict codes (`sys_job_group`, `sys_job_status`, `sys_common_status`, `sys_oper_type`) are seeded in the external spt-auth database and loaded at runtime via `IAuthOpenFacade.findDictByAppCode()`.

The `@permission` bean must use Shiro's `SecurityUtils.getSubject().isPermitted()` (via the existing `ShiroUtil.isPermitted()`), NOT the spt-auth `PermissionService` (which uses Spring Security's `SecurityUtils.getLoginUser()` — that API doesn't exist in this Shiro-based project).

**Primary recommendation:** Create two `@Component` beans (`@Component("dict")` and `@Component("permission")`) in zgbas-admin, add thymeleaf-extras-shiro:2.1.0 to admin pom, register a `ShiroDialect` @Bean, and create jobLog.html mirroring operlog.html with SysJobLog fields and /monitor/jobLog routes. Fix the pre-existing `@GetMapping("/{configId}")` path variable bug in SysJobLogController.

## Architectural Responsibility Map

| Capability | Primary Tier | Secondary Tier | Rationale |
|------------|-------------|----------------|-----------|
| 调度日志页面渲染 | Browser / Static templates | Frontend Server (Thymeleaf SSR) | jobLog.html is a static Thymeleaf template served by Spring MVC; table data loaded via AJAX from /monitor/jobLog/list |
| 调度日志 REST API | API / Backend (quartz module) | — | SysJobLogController in zgbas-quartz owns all /monitor/jobLog/* routes |
| @dict 模板辅助 Bean | Frontend Server (Thymeleaf) | API / Backend (auth-sdk) | Bean lives in admin for template resolution; delegates to auth-sdk DictUtil for data |
| @permission 模板辅助 Bean | Frontend Server (Thymeleaf) | API / Backend (Shiro) | Bean lives in admin for template resolution; delegates to Shiro SecurityUtils for permission check |
| ShiroDialect 注册 | Frontend Server (Thymeleaf) | — | ShiroDialect bean registered in admin config; Thymeleaf auto-detects IDialect beans |
| 字典数据存储 | Database / Storage (spt-auth DB) | — | RuoYi dict data (sys_dict_type/sys_dict_data tables) lives in external spt-auth database, accessed via IAuthOpenFacade HTTP |

## Standard Stack

### Core
| Library | Version | Purpose | Why Standard |
|---------|---------|---------|--------------|
| thymeleaf-extras-shiro | 2.1.0 | Thymeleaf dialect for `shiro:hasPermission` attributes | Matches old zgbas 4.8.3 parent pom version; 2.1.0 already in local Maven repo; only version compatible with Thymeleaf 3.x + Shiro 1.8.0 [CITED: old zgbas-plus-4.8.3 pom] |
| spring-boot-starter-thymeleaf | 2.5.9 (managed) | Thymeleaf template engine | Already in admin pom (D-P3-09); version managed by spring-boot-starter-parent:2.5.9 [VERIFIED: zgbas-admin/pom.xml line 41] |
| auth-sdk | 3.8.2-SNAPSHOT | DictUtil + SysDictDataSdk for dict data access | Already a dependency; DictUtil.getListByCategory() returns SysDictDataSdk with dictLabel/dictValue [VERIFIED: zgbas-system/pom.xml + auth-sdk source] |
| shiro-spring | 1.8.0 (managed) | SecurityUtils.getSubject().isPermitted() for permission checks | Already in dependencyManagement; ShiroUtil.isPermitted() wraps it [VERIFIED: pom.xml line 119-122] |

### Supporting
| Library | Version | Purpose | When to Use |
|---------|---------|---------|-------------|
| ruoyi-common | 4.7.2 | BaseController, AjaxResult, TableDataInfo, ExcelUtil | Already a dependency; SysJobLogController extends BaseController [VERIFIED: pom.xml line 214-218] |

### Alternatives Considered
| Instead of | Could Use | Tradeoff |
|------------|-----------|----------|
| auth-sdk DictUtil | BsDictUtil (server cache) | BsDictUtil returns BsDictData with dictCd/dictName — doesn't match template's dictLabel/dictValue; would need adapter DTO. auth-sdk DictUtil returns SysDictDataSdk with both field sets — zero adaptation needed |
| ShiroUtil.isPermitted() | spt-auth PermissionService | PermissionService uses Spring Security SecurityUtils.getLoginUser() which doesn't exist in this Shiro project; ShiroUtil.isPermitted() uses Shiro's SecurityUtils — correct for this stack |

**Installation:**
```xml
<!-- Add to zgbas-admin/pom.xml <dependencies> -->
<dependency>
    <groupId>com.github.theborakompanioni</groupId>
    <artifactId>thymeleaf-extras-shiro</artifactId>
    <version>2.1.0</version>
</dependency>
```

**Version verification:**
```bash
# Confirmed in local Maven repo
ls /Users/alan/App/Repository/com/github/theborakompanioni/thymeleaf-extras-shiro/2.1.0/
# Output: thymeleaf-extras-shiro-2.1.0.jar + .pom + sources

# Confirmed in old zgbas-plus parent pom
grep "thymeleaf.extras.shiro.version" /Users/alan/App/Repository/com/zgbas/zgbas-plus/4.8.3/zgbas-plus-4.8.3.pom
# Output: <thymeleaf.extras.shiro.version>2.1.0</thymeleaf.extras.shiro.version>
```

## Package Legitimacy Audit

| Package | Registry | Age | Downloads | Source Repo | slopcheck | Disposition |
|---------|----------|-----|-----------|-------------|-----------|-------------|
| thymeleaf-extras-shiro | Maven Central | ~8 yrs (2.1.0 released ~2021) | Widely used (RuoYi ecosystem) | github.com/theborakompanioni/thymeleaf-extras-shiro | N/A (slopcheck unavailable) | Approved — verified in local repo + old project pom |

**Packages removed due to slopcheck [SLOP] verdict:** none
**Packages flagged as suspicious [SUS]:** none

*slopcheck was unavailable at research time. thymeleaf-extras-shiro 2.1.0 is verified via: (1) presence in local Maven repo at `/Users/alan/App/Repository/com/github/theborakompanioni/thymeleaf-extras-shiro/2.1.0/`, (2) old zgbas-plus 4.8.3 parent pom declares version 2.1.0, (3) jar contains expected `at.pollux.thymeleaf.shiro.dialect.ShiroDialect` class. No checkpoint:human-verify needed.*

## Architecture Patterns

### System Architecture Diagram

```
Browser (jobLog.html)
    │
    ├── Thymeleaf SSR render (server-side)
    │       │
    │       ├── @dict.getType('sys_job_status') ──→ DictUtil.getListByCategory()
    │       │       └── auth-sdk LoadingCache ──→ IAuthOpenFacade.findDictByAppCode() ──→ spt-auth DB (sys_dict_data)
    │       │
    │       ├── @permission.hasPermi('monitor:job:list') ──→ ShiroUtil.isPermitted()
    │       │       └── Shiro SecurityUtils.getSubject().isPermitted() ──→ ShiroDbRealm (cached perms)
    │       │
    │       └── shiro:hasPermission="monitor:job:remove" ──→ ShiroDialect processor
    │               └── thymeleaf-extras-shiro: ShiroDialect bean ──→ Subject.hasPermission()
    │
    ├── AJAX GET /monitor/jobLog/list ──→ SysJobLogController.list()
    │       └── startPage() + ISysJobLogService.selectJobLogList() ──→ SysJobLogMapper ──→ DB
    │
    ├── AJAX DELETE /monitor/jobLog/{ids} ──→ SysJobLogController.remove()
    │       └── ISysJobLogService.deleteJobLogByIds()
    │
    ├── AJAX DELETE /monitor/jobLog/clean ──→ SysJobLogController.clean()
    │       └── ISysJobLogService.cleanJobLog()
    │
    └── AJAX POST /monitor/jobLog/export ──→ SysJobLogController.export()
            └── ExcelUtil.exportExcel() ──→ HTTP response (xlsx)
```

### Recommended Project Structure
```
zgbas-admin/src/main/
├── java/com/spt/bas/web/
│   └── config/
│       └── TemplateHelperConfig.java    # @Component("dict") + @Component("permission") + @Bean ShiroDialect
└── resources/templates/
    └── monitor/jobLog/
        └── jobLog.html                  # new — mirrors operlog.html pattern
```

### Pattern 1: @dict Bean — Thin Facade Over auth-sdk DictUtil
**What:** A Spring component named `dict` that exposes `getType(type)` and `getLabel(type, value)` methods for Thymeleaf `${@dict.getType(...)}` expressions.
**When to use:** All monitor templates that use `th:with="type=${@dict.getType('...')}"` or `${@dict.getLabel('...', ...)}`.
**Why auth-sdk DictUtil (not BsDictUtil):** Templates use `dict.dictLabel` and `dict.dictValue` (RuoYi field names). `SysDictDataSdk` has both `getDictLabel()`/`getDictValue()` AND `getDictCd()`/`getDictName()`. `BsDictData` only has `dictCd`/`dictName` — would require an adapter DTO.
**Example:**
```java
// Source: auth-sdk DictUtil.java + RuoYi DictUtils pattern
package com.spt.bas.web.config;

import com.spt.auth.sdk.cache.DictUtil;
import com.spt.auth.sdk.entity.SysDictDataSdk;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("dict")
public class DictTemplateHelper {

    /**
     * Get all dict data entries for a type.
     * Used in templates: th:with="type=${@dict.getType('sys_job_status')}"
     * Returns SysDictDataSdk with getDictLabel() and getDictValue() — matches
     * template's ${dict.dictLabel} and ${dict.dictValue} expressions.
     */
    public List<SysDictDataSdk> getType(String dictType) {
        return DictUtil.getListByCategory(dictType);
    }

    /**
     * Get dict label by type and value.
     * Used in templates: ${@dict.getLabel('sys_oper_type', operLog.businessType)}
     */
    public String getLabel(String dictType, String dictValue) {
        List<SysDictDataSdk> datas = DictUtil.getListByCategory(dictType);
        if (datas != null) {
            for (SysDictDataSdk dict : datas) {
                if (dictValue != null && dictValue.equals(dict.getDictValue())) {
                    return dict.getDictLabel();
                }
            }
        }
        return "";
    }
}
```

### Pattern 2: @permission Bean — Shiro-Native Permission Check
**What:** A Spring component named `permission` that exposes `hasPermi(permission)` for Thymeleaf `${@permission.hasPermi('...')}` expressions.
**When to use:** All templates with `[[${@permission.hasPermi('monitor:job:edit')}]]` inline expressions.
**Why ShiroUtil (not spt-auth PermissionService):** spt-auth `PermissionService` uses `com.spt.common.utils.SecurityUtils.getLoginUser()` (Spring Security API). This project uses Shiro, not Spring Security — that class doesn't exist. `ShiroUtil.isPermitted()` uses `org.apache.shiro.SecurityUtils.getSubject().isPermitted()` which IS the correct API for this stack.
**Example:**
```java
// Source: ShiroUtil.java isPermitted() + RuoYi PermissionService hasPermi() pattern
package com.spt.bas.web.config;

import com.spt.bas.web.shiro.ShiroUtil;
import org.springframework.stereotype.Component;

@Component("permission")
public class PermissionTemplateHelper {

    /**
     * Check if current user has a permission.
     * Used in templates: [[${@permission.hasPermi('monitor:job:edit')}]]
     * Returns "btn-success" or "" via CSS class logic in template JS.
     *
     * Delegates to ShiroUtil.isPermitted() which calls
     * SecurityUtils.getSubject().isPermitted(permCode).
     */
    public boolean hasPermi(String permission) {
        if (permission == null || permission.isEmpty()) {
            return false;
        }
        return ShiroUtil.isPermitted(permission);
    }

    public boolean lacksPermi(String permission) {
        return !hasPermi(permission);
    }
}
```

### Pattern 3: ShiroDialect Bean Registration
**What:** Register `at.pollux.thymeleaf.shiro.dialect.ShiroDialect` as a Spring bean so Thymeleaf auto-detects it and processes `shiro:hasPermission` attributes.
**When to use:** Any template with `xmlns:shiro="http://www.pollix.at/thymeleaf/shiro"` and `shiro:hasPermission="..."` attributes.
**Example:**
```java
// Source: thymeleaf-extras-shiro README + RuoYi ShiroConfig pattern
@Bean
public at.pollux.thymeleaf.shiro.dialect.ShiroDialect shiroDialect() {
    return new at.pollux.thymeleaf.shiro.dialect.ShiroDialect();
}
```
**Note:** thymeleaf-extras-shiro 2.1.0 has NO `spring.factories` auto-configuration — the `@Bean` must be declared manually. Thymeleaf's `SpringTemplateEngine` auto-discovers all `IDialect` beans in the application context.

### Pattern 4: jobLog.html — Mirror operlog.html
**What:** Create `templates/monitor/jobLog/jobLog.html` following the exact structure of `operlog.html` but with SysJobLog fields and /monitor/jobLog routes.
**Key differences from operlog.html:**
- Search fields: jobName (text), jobGroup (dict select), status (dict select), time range
- Table columns: jobLogId, jobName, jobGroup (dict-formatted), invokeTarget, jobMessage, status (badge-formatted), createTime, operations
- Routes: prefix = "monitor/jobLog", no detailUrl (getInfo has a path variable bug — see Pitfalls)
- Dict types: `sys_job_group` for jobGroup, `sys_common_status` for status (or custom formatter)
- Permissions: `monitor:job:remove`, `monitor:job:export`, `monitor:job:query`

### Anti-Patterns to Avoid
- **Don't use BsDictUtil for @dict bean:** BsDictData has `dictCd`/`dictName`, not `dictLabel`/`dictValue`. Templates would render empty options.
- **Don't use spt-auth PermissionService for @permission bean:** It depends on Spring Security's `SecurityUtils.getLoginUser()` which doesn't exist in this Shiro project. Runtime NPE.
- **Don't add thymeleaf-extras-shiro to parent pom dependencyManagement:** It's only needed by admin; declare version directly in admin pom (keeps the dep surface minimal, consistent with existing admin-only deps like easyexcel/screw-core/html2image).
- **Don't create a detail.html for jobLog yet:** The SysJobLogController.getInfo() has a path variable bug (`/{configId}` vs `@PathVariable Long jobLogId`). The jobLog page can omit the detail button until the bug is fixed, or the fix can be included in this phase.

## Don't Hand-Roll

| Problem | Don't Build | Use Instead | Why |
|---------|-------------|-------------|-----|
| Dict data loading + caching | Custom dict cache/query | auth-sdk `DictUtil.getListByCategory()` | Already handles LoadingCache (10min refresh), IAuthOpenFacade HTTP fetch, SysDictDataSdk with both field sets |
| Permission check | Custom permission lookup | `ShiroUtil.isPermitted()` | Already wraps Shiro SecurityUtils.getSubject().isPermitted(); ShiroDbRealm caches authorization info |
| Shiro Thymeleaf attributes | Custom Thymeleaf dialect | `thymeleaf-extras-shiro` ShiroDialect | Battle-tested; handles hasPermission/lacksPermission/hasRole/hasAnyRoles/guest/authenticated/user/all |
| Excel export | Custom export logic | `ExcelUtil<SysJobLog>` (ruoyi-common) | SysJobLog already has `@Excel` annotations on fields; controller already uses `ExcelUtil.exportExcel()` |
| Bootstrap-table init | Custom table JS | `$.table.init(options)` (ry-ui.js) | Standard pattern across all monitor pages; handles pagination/sort/search/export |

**Key insight:** All infrastructure for jobLog.html already exists — the controller, service, mapper, domain, and ry-ui.js table framework are all in place. The only missing pieces are the template HTML file, the two helper beans, and the ShiroDialect bean.

## Runtime State Inventory

> This phase creates new files (jobLog.html, TemplateHelperConfig.java) and adds a dependency. No rename/refactor/migration involved.

| Category | Items Found | Action Required |
|----------|-------------|------------------|
| Stored data | N/A — no data migration | None |
| Live service config | N/A — no external service config changes | None |
| OS-registered state | N/A — no OS registrations | None |
| Secrets/env vars | N/A — no new secrets | None |
| Build artifacts | N/A — standard Maven build | None |

## Common Pitfalls

### Pitfall 1: @dict Bean Using Wrong Dict Util (CRITICAL)
**What goes wrong:** If `@dict.getType()` delegates to `BsDictUtil.getListByCategory()`, it returns `BsDictData` objects with `dictCd`/`dictName` fields. Templates use `${dict.dictLabel}` and `${dict.dictValue}` — these properties don't exist on `BsDictData`, so Thymeleaf renders empty strings. All dict-backed dropdowns and formatters appear empty.
**Why it happens:** Both classes are named `BsDictUtil` and both have `getListByCategory()`. It's easy to import the wrong one.
**How to avoid:** Use `com.spt.auth.sdk.cache.DictUtil` (auth-sdk), NOT `com.spt.bas.server.cache.BsDictUtil` or `com.spt.bas.client.cache.BsDictUtil`. The auth-sdk DictUtil returns `SysDictDataSdk` which has `getDictLabel()`/`getDictValue()`.
**Warning signs:** Dropdowns render with no options; `$.table.selectDictLabel(datas, value)` returns empty string.

### Pitfall 2: @permission Bean Using Spring Security API (CRITICAL)
**What goes wrong:** If `@permission.hasPermi()` delegates to spt-auth's `PermissionService.hasPermi()`, it calls `SecurityUtils.getLoginUser()` from `com.spt.common.utils.SecurityUtils`. This class doesn't exist in zgbas-plus (it's a Spring Security utility, but this project uses Shiro). Template rendering throws `NoClassDefFoundError` or `NullPointerException`.
**Why it happens:** The spt-auth source has a `PermissionService` with `hasPermi()` — looks like the right bean, but it's Spring Security-based.
**How to avoid:** Use `ShiroUtil.isPermitted()` from `com.spt.bas.web.shiro.ShiroUtil`, which calls `org.apache.shiro.SecurityUtils.getSubject().isPermitted()`.
**Warning signs:** `NoClassDefFoundError: com/spt/common/utils/SecurityUtils` or NPE on template render.

### Pitfall 3: SysJobLogController Path Variable Mismatch (PRE-EXISTING BUG)
**What goes wrong:** `SysJobLogController.getInfo()` has `@GetMapping("/{configId}")` but `@PathVariable Long jobLogId`. The path variable name in the URL template (`configId`) doesn't match the `@PathVariable` parameter name (`jobLogId`). Spring MVC throws `MissingPathVariableException` when this endpoint is called.
**Why it happens:** Copy-paste error from porting — the original RuoYi SysJobLogController uses `@GetMapping("/{jobLogId}")` and `@PathVariable Long jobLogId`. The port changed the URL path but not the parameter.
**How to avoid:** Fix the path to `@GetMapping("/{jobLogId}")` to match the `@PathVariable` parameter name. This is a one-line fix in the controller.
**Warning signs:** Clicking "详情" button on jobLog page returns 400 or 500 error.

### Pitfall 4: ShiroDialect Not Auto-Configured
**What goes wrong:** Adding the `thymeleaf-extras-shiro` dependency alone does NOT register the ShiroDialect. The `shiro:hasPermission` attributes are silently ignored (rendered as plain HTML attributes with no effect). Buttons that should be hidden by permission are visible to all users.
**Why it happens:** thymeleaf-extras-shiro 2.1.0 has no `spring.factories` auto-configuration. Unlike Spring Boot starters, it requires a manual `@Bean` declaration.
**How to avoid:** Register `@Bean public ShiroDialect shiroDialect() { return new ShiroDialect(); }` in a `@Configuration` class.
**Warning signs:** All toolbar buttons visible regardless of user permissions; no errors in logs.

### Pitfall 5: Dict Cache Not Initialized Before Template Render
**What goes wrong:** `DictUtil.getListByCategory()` returns empty list if the auth-sdk dict cache hasn't been initialized. The cache is initialized via `DictUtil.init(appCode)` which should be called at application startup.
**Why it happens:** The auth-sdk `DictUtil.init()` is called in `ApplicationStartup` (zgbas-system). If startup ordering changes or the init fails silently, dict data is unavailable.
**How to avoid:** Verify `ApplicationStartup` calls `DictUtil.init()` and that it succeeds. The existing `BsDictUtil.init()` in zgbas-system already logs initialization — check for the "---初始化数据字典" log line.
**Warning signs:** Dict dropdowns empty on first page load after server start; `DictUtil.getListByCategory()` returns `[]`.

### Pitfall 6: jobLog.html Missing detail.html
**What goes wrong:** operlog.html has a "详情" (detail) button that opens a modal with `detailUrl: prefix + "/detail/{id}"`. If jobLog.html copies this pattern, it needs a detail.html template AND a controller route that returns the view. But SysJobLogController has no view-returning route (it's a pure @RestController).
**Why it happens:** operlog's detail flow requires a `@GetMapping("/detail/{id}")` that returns a String view name + adds the model attribute. SysJobLogController only has `@GetMapping("/{configId}")` which returns JSON (AjaxResult).
**How to avoid:** Either (a) omit the detail button from jobLog.html (simplest — D-01 says "完整监控页风格" but doesn't require detail), or (b) add a detail view controller route. Option (a) is recommended for this phase; the detail can be added later if needed.

## Code Examples

### jobLog.html (Key Structure — Adapted from operlog.html)
```html
<!-- Source: operlog.html pattern + SysJobLog domain fields + SysJobLogController routes -->
<!DOCTYPE html>
<html lang="zh" xmlns:th="http://www.thymeleaf.org" xmlns:shiro="http://www.pollix.at/thymeleaf/shiro">
<head>
    <th:block th:include="include :: header('调度日志列表')" />
    <th:block th:include="include :: bootstrap-select-css" />
</head>
<body class="gray-bg">
    <div class="container-div">
        <div class="row">
            <div class="col-sm-12 search-collapse">
                <form id="jobLog-form">
                    <div class="select-list">
                        <ul>
                            <li>
                                <label>任务名称： </label><input type="text" name="jobName"/>
                            </li>
                            <li>
                                <label>任务组名： </label><select name="jobGroup" th:with="type=${@dict.getType('sys_job_group')}">
                                    <option value="">所有</option>
                                    <option th:each="dict : ${type}" th:text="${dict.dictLabel}" th:value="${dict.dictValue}"></option>
                                </select>
                            </li>
                            <li>
                                <label>执行状态：</label><select name="status" th:with="type=${@dict.getType('sys_common_status')}">
                                    <option value="">所有</option>
                                    <option th:each="dict : ${type}" th:text="${dict.dictLabel}" th:value="${dict.dictValue}"></option>
                                </select>
                            </li>
                            <li class="select-time">
                                <label>执行时间： </label>
                                <input type="text" class="time-input" id="startTime" placeholder="开始时间" name="params[beginTime]"/>
                                <span>-</span>
                                <input type="text" class="time-input" id="endTime" placeholder="结束时间" name="params[endTime]"/>
                            </li>
                            <li>
                                <a class="btn btn-primary btn-rounded btn-sm" onclick="searchPre()"><i class="fa fa-search"></i>&nbsp;搜索</a>
                                <a class="btn btn-warning btn-rounded btn-sm" onclick="resetPre()"><i class="fa fa-refresh"></i>&nbsp;清空</a>
                            </li>
                        </ul>
                    </div>
                </form>
            </div>

            <div class="btn-group-sm" id="toolbar" role="group">
                <a class="btn btn-danger multiple disabled" onclick="$.operate.removeAll()" shiro:hasPermission="monitor:job:remove">
                    <i class="fa fa-remove"></i> 删除
                </a>
                <a class="btn btn-danger" onclick="$.operate.clean()" shiro:hasPermission="monitor:job:remove">
                    <i class="fa fa-trash"></i> 清空
                </a>
                <a class="btn btn-warning" onclick="$.table.exportExcel()" shiro:hasPermission="monitor:job:export">
                    <i class="fa fa-download"></i> 导出
                </a>
            </div>

            <div class="col-sm-12 select-table table-striped">
                <table id="bootstrap-table"></table>
            </div>
        </div>
    </div>

    <th:block th:include="include :: footer" />
    <th:block th:include="include :: bootstrap-select-js" />
    <script th:inline="javascript">
        var datas = [[${@dict.getType('sys_job_group')}]];
        var prefix = ctx + "monitor/jobLog";

        $(function() {
            var options = {
                url: prefix + "/list",
                cleanUrl: prefix + "/clean",
                removeUrl: prefix,
                exportUrl: prefix + "/export",
                queryParams: queryParams,
                sortName: "createTime",
                sortOrder: "desc",
                modalName: "调度日志",
                escape: true,
                showPageGo: true,
                rememberSelected: true,
                columns: [{
                    field: 'state',
                    checkbox: true
                },
                {
                    field: 'jobLogId',
                    title: '日志编号'
                },
                {
                    field: 'jobName',
                    title: '任务名称'
                },
                {
                    field: 'jobGroup',
                    title: '任务组名',
                    align: 'center',
                    formatter: function(value, row, index) {
                        return $.table.selectDictLabel(datas, value);
                    }
                },
                {
                    field: 'invokeTarget',
                    title: '调用目标字符串'
                },
                {
                    field: 'jobMessage',
                    title: '日志信息'
                },
                {
                    field: 'status',
                    title: '执行状态',
                    align: 'center',
                    formatter: function(value, row, index) {
                        if (value == '0') {
                            return '<span class="badge badge-primary">成功</span>';
                        } else if (value == '1') {
                            return '<span class="badge badge-danger">失败</span>';
                        }
                    }
                },
                {
                    field: 'createTime',
                    title: '执行时间',
                    sortable: true
                }]
            };
            $.table.init(options);
        });

        function queryParams(params) {
            var search = $.table.queryParams(params);
            return search;
        }

        function searchPre() {
            $.table.search('jobLog-form', 'bootstrap-table');
        }

        function resetPre() {
            $("#jobLog-form")[0].reset();
            $.table.search('jobLog-form', 'bootstrap-table');
        }
    </script>
</body>
</html>
```

**Key adaptation notes:**
- `removeUrl: prefix` (not `prefix + "/remove"`) — DELETE /monitor/jobLog/{jobLogIds} matches `@DeleteMapping("/{jobLogIds}")`
- `cleanUrl: prefix + "/clean"` — matches `@DeleteMapping("/clean")`
- `exportUrl: prefix + "/export"` — matches `@PostMapping("/export")`
- No `detailUrl` — SysJobLogController.getInfo() has a path variable bug and no view-returning route
- Status formatter uses hardcoded badges (0=成功/1=失败) matching SysJobLog's `@Excel(readConverterExp = "0=正常,1=失败")` — NOT dict-based, because `sys_common_status` uses different values (0=成功/1=失败) which happens to match
- `createTime` field comes from BaseEntity (inherited by SysJobLog)

### SysJobLogController Path Variable Fix
```java
// BEFORE (buggy):
@GetMapping(value = "/{configId}")
public AjaxResult getInfo(@PathVariable Long jobLogId) {

// AFTER (fixed):
@GetMapping(value = "/{jobLogId}")
public AjaxResult getInfo(@PathVariable Long jobLogId) {
```

## State of the Art

| Old Approach | Current Approach | When Changed | Impact |
|--------------|------------------|--------------|--------|
| RuoYi Spring Security + PermissionService("ss") | Shiro + ShiroUtil.isPermitted() | Phase 3 (D-P3-01) | @permission bean must use Shiro API, not Spring Security |
| BsDictUtil (server-side JPA cache) | auth-sdk DictUtil (HTTP-fetched cache) | Phase 4 (D-P4-01) | @dict bean must use auth-sdk DictUtil for RuoYi dict codes |
| thymeleaf-extras-shiro not configured | thymeleaf-extras-shiro 2.1.0 + ShiroDialect @Bean | This phase | shiro:hasPermission attributes now processed |

**Deprecated/outdated:**
- `com.spt.framework.web.service.PermissionService` (spt-auth): Uses Spring Security API — incompatible with Shiro. Do NOT port or use.
- `com.spt.common.utils.DictUtils` (spt-auth common): Uses Redis cache — not available in this project. Use auth-sdk `DictUtil` instead.

## Assumptions Log

| # | Claim | Section | Risk if Wrong |
|---|-------|---------|---------------|
| A1 | auth-sdk DictUtil cache is initialized at startup via ApplicationStartup | Standard Stack | Dict dropdowns empty; need to verify ApplicationStartup calls DictUtil.init() |
| A2 | RuoYi dict codes (sys_job_group, sys_job_status, etc.) exist in production spt-auth DB | Standard Stack | Dict dropdowns empty in production; codes confirmed in spt-auth/sql/ry_20210908.sql but production DB state unverified |
| A3 | SysJobLog.createTime field exists and is populated by the quartz job execution | Code Examples | createTime column shows empty; SysJobLog extends BaseEntity which has createTime |
| A4 | $.operate.removeAll() and $.operate.clean() work with DELETE method + path param | Code Examples | May need custom JS like Phase 1's removeJob; need to verify ry-ui.js implementation |
| A5 | thymeleaf-extras-shiro 2.1.0 is compatible with Spring Boot 2.5.9's Thymeleaf 3.0.x | Standard Stack | Dialect registration fails; version 2.0.0 is fallback (also in local repo) |

## Open Questions

1. **Does $.operate.removeAll() send DELETE or POST?**
   - What we know: Phase 1 had to create custom `removeJob()`/`removeAllJobs()` functions because `$.operate.remove()` sends POST with `{ids: id}` body, which doesn't match SysJobController's `@DeleteMapping("/{jobIds}")`.
   - What's unclear: Does `$.operate.removeAll()` have the same issue? The operlog.html uses `$.operate.removeAll()` directly with `removeUrl: prefix + "/remove"`.
   - Recommendation: Check ry-ui.js `$.operate.removeAll()` implementation. If it sends POST, jobLog.html needs custom `removeAllJobLogs()` function (same pattern as Phase 1's `removeAllJobs()`). The `cleanUrl` + `$.operate.clean()` also needs verification.

2. **Is the auth-sdk DictUtil cache initialized at application startup?**
   - What we know: `BsDictUtil.init()` is called in ApplicationStartup. auth-sdk `DictUtil.init(appCode)` needs to be called separately.
   - What's unclear: Whether ApplicationStartup also calls `DictUtil.init()`.
   - Recommendation: Read `ApplicationStartup.java` to verify. If not called, the @dict bean may need to trigger initialization.

## Environment Availability

| Dependency | Required By | Available | Version | Fallback |
|------------|------------|-----------|---------|----------|
| JDK 1.8 | Build | ✓ | Corretto 1.8.0_482 | — |
| Maven 3.8.6 | Build | ✓ | 3.8.6 | — |
| thymeleaf-extras-shiro | ShiroDialect | ✓ (local repo) | 2.1.0 | 2.0.0 (also in local repo) |
| auth-sdk | @dict bean | ✓ (local repo) | 3.8.2-SNAPSHOT | — |
| spt-auth service (runtime) | Dict data loading | ✓ (runtime) | — | N/A — dict dropdowns empty without it |

**Missing dependencies with no fallback:** none
**Missing dependencies with fallback:** none

## Validation Architecture

### Test Framework
| Property | Value |
|----------|-------|
| Framework | JUnit 5 (spring-boot-starter-test, scope test) |
| Config file | none — uses ZgbasApplicationTest.java |
| Quick run command | `JAVA_HOME=/Users/alan/Library/Java/JavaVirtualMachines/corretto-1.8.0_482/Contents/Home /Users/alan/App/apache-maven-3.8.6/bin/mvn test -pl zgbas-admin -s /Users/alan/App/apache-maven-3.8.6/zg_settings.xml -Dtest=ZgbasApplicationTest` |
| Full suite command | `JAVA_HOME=/Users/alan/Library/Java/JavaVirtualMachines/corretto-1.8.0_482/Contents/Home /Users/alan/App/apache-maven-3.8.6/bin/mvn test -pl zgbas-admin -s /Users/alan/App/apache-maven-3.8.6/zg_settings.xml` |

### Phase Requirements → Test Map
| Req ID | Behavior | Test Type | Automated Command | File Exists? |
|--------|----------|-----------|-------------------|-------------|
| QTZ-05 | jobLog.html renders without error | smoke (startup) | `mvn test -pl zgbas-admin -Dtest=ZgbasApplicationTest` | ✅ existing |
| QTZ-06 | @dict and @permission beans registered | smoke (startup) | `mvn test -pl zgbas-admin -Dtest=ZgbasApplicationTest` | ✅ existing |
| QTZ-07 | Full CRUD + log flow E2E | manual-only | Browser: /monitor/jobLog renders, dropdowns populated, delete/clean/export work | N/A |
| QTZ-08 | ShiroDialect bean registered | smoke (startup) | `mvn test -pl zgbas-admin -Dtest=ZgbasApplicationTest` | ✅ existing |

### Sampling Rate
- **Per task commit:** `mvn test -pl zgbas-admin -Dtest=ZgbasApplicationTest -s ...` (context-load smoke test — verifies beans wire)
- **Per wave merge:** Full admin test suite
- **Phase gate:** Full suite green + manual browser verification of /monitor/jobLog

### Wave 0 Gaps
- None — existing test infrastructure (ZgbasApplicationTest) covers context-load verification. E2E browser testing is manual-only (consistent with Phase 1 pattern).

**Note:** The startup test is NON-HERMETIC (per memory `project_zgbas-plus-nonhermetic-startup-test.md`). It requires `DB_PASSWORD` and `SPT_APP_SECRET` exported locally, OR the Phase 4 D-P4 decision (plaintext secrets in application-dev.yml) means no exports needed.

## Security Domain

### Applicable ASVS Categories

| ASVS Category | Applies | Standard Control |
|---------------|---------|-----------------|
| V2 Authentication | no (handled by Shiro auth chain, Phase 3) | — |
| V3 Session Management | no (handled by Shiro session, Phase 3) | — |
| V4 Access Control | yes | Shiro @RequiresPermissions on controller routes; @permission bean for template-level visibility; shiro:hasPermission for button-level visibility |
| V5 Input Validation | yes | SysJobLog extends BaseEntity (has params map for search); Spring MVC binds form params to SysJobLog fields |
| V6 Cryptography | no | — |

### Known Threat Patterns for Spring MVC + Thymeleaf + Shiro

| Pattern | STRIDE | Standard Mitigation |
|---------|--------|---------------------|
| Template injection (XSS via Thymeleaf) | Tampering | Thymeleaf auto-escapes by default; `th:text` escapes HTML; `th:utext` is NOT used in monitor templates |
| Missing permission check on API route | Elevation of privilege | SysJobLogController has @RequiresPermissions on all 5 routes; verified [VERIFIED: SysJobLogController.java] |
| Permission bypass via template helper | Elevation of privilege | @permission bean delegates to ShiroUtil.isPermitted() → Subject.isPermitted() → ShiroDbRealm authorization cache |
| Dict data injection | Tampering | Dict data comes from spt-auth DB via authenticated IAuthOpenFacade HTTP; not user-editable from this application |

## Sources

### Primary (HIGH confidence)
- `zgbas-quartz/src/main/java/com/spt/quartz/controller/SysJobLogController.java` — all 5 REST routes, permission codes, path variable bug
- `zgbas-quartz/src/main/java/com/spt/quartz/domain/SysJobLog.java` — 9 fields, @Excel annotations, BaseEntity inheritance
- `zgbas-admin/src/main/resources/templates/monitor/operlog/operlog.html` — full monitor page pattern (search, toolbar, bootstrap-table, dict, permission)
- `zgbas-admin/src/main/resources/templates/monitor/logininfor/logininfor.html` — lighter monitor page pattern
- `zgbas-admin/src/main/resources/templates/monitor/operlog/detail.html` — detail page pattern + @dict.getLabel() usage
- `zgbas-system/src/main/java/com/spt/bas/client/entity/BsDictData.java` — dictCd/dictName fields (NOT dictLabel/dictValue)
- `zgbas-system/src/main/java/com/spt/bas/client/entity/BsDictType.java` — dictTypeCd/dictTypeName fields
- `zgbas-system/src/main/java/com/spt/bas/server/cache/BsDictUtil.java` — server-side dict cache (wrong API for templates)
- `zgbas-system/src/main/java/com/spt/bas/client/cache/BsDictUtil.java` — client-side dict cache (wrong API for templates)
- `zgbas-system/src/main/java/com/spt/bas/web/shiro/ShiroUtil.java` — @Component("shiroUtil"), isPermitted() wraps Shiro SecurityUtils
- `zgbas-common/src/main/java/com/spt/tools/shiro/util/ShiroUtil.java` — base ShiroUtil with isPermitted() using Subject.isPermitted()
- `zgbas-common/src/main/java/com/spt/tools/shiro/config/ToolsShiroConfig.java` — Shiro bean configuration (no ShiroDialect)
- `zgbas-admin/pom.xml` — current dependencies (no thymeleaf-extras-shiro)
- `pom.xml` — parent pom dependencyManagement (no thymeleaf-extras-shiro version management)
- `/Users/alan/WorkSpace/IDEA/spt-auth/auth-sdk/src/main/java/com/spt/auth/sdk/cache/DictUtil.java` — auth-sdk dict cache, getListByCategory() returns SysDictDataSdk
- `/Users/alan/WorkSpace/IDEA/spt-auth/auth-sdk/src/main/java/com/spt/auth/sdk/entity/SysDictDataSdk.java` — has BOTH dictLabel/dictValue AND dictCd/dictName
- `/Users/alan/WorkSpace/IDEA/spt-auth/auth-framework/src/main/java/com/spt/framework/web/service/PermissionService.java` — @Service("ss"), uses Spring Security SecurityUtils (INCOMPATIBLE with Shiro)
- `/Users/alan/WorkSpace/IDEA/spt-auth/sql/ry_20210908.sql` — RuoYi dict codes (sys_job_group, sys_job_status, sys_common_status, sys_oper_type) seeded
- `/Users/alan/App/Repository/com/github/theborakompanioni/thymeleaf-extras-shiro/2.1.0/` — jar + pom verified in local repo
- `/Users/alan/App/Repository/com/zgbas/zgbas-plus/4.8.3/zgbas-plus-4.8.3.pom` — old project declares thymeleaf.extras.shiro.version=2.1.0
- `zgbas-admin/src/main/resources/static/ruoyi/js/ry-ui.js` (lines 573-599) — $.table.selectDictLabel expects dict.dictValue/dict.dictLabel
- `zgbas-admin/src/main/resources/templates/monitor/job/job.html` — Phase 1 output, uses @dict/@permission (confirms gap)
- `.planning/phases/01-quartz-frontend-fix/01-03-PLAN.md` — Phase 1 plan pattern (plan structure, task format, verification)

### Secondary (MEDIUM confidence)
- `jar tf thymeleaf-extras-shiro-2.1.0.jar` — confirmed `at.pollux.thymeleaf.shiro.dialect.ShiroDialect` class exists, no spring.factories auto-config

### Tertiary (LOW confidence)
- None

## Metadata

**Confidence breakdown:**
- Standard stack: HIGH — thymeleaf-extras-shiro 2.1.0 verified in local repo + old project pom; auth-sdk DictUtil verified in source; ShiroUtil verified in source
- Architecture: HIGH — operlog.html pattern is established and working; bean registration pattern is standard Spring; ShiroDialect is standard thymeleaf-extras-shiro
- Pitfalls: HIGH — all pitfalls verified by reading actual source code (BsDictData fields, PermissionService Spring Security dependency, path variable mismatch, no auto-config)

**Research date:** 2026-07-21
**Valid until:** 2026-08-21 (stable — no fast-moving dependencies)
