# Phase 2: quartz-log-ui-and-thymeleaf-support - Context

**Gathered:** 2026-07-21
**Status:** Ready for planning

<domain>
## Phase Boundary

Phase 2 creates the quartz 调度日志前端页面，并补齐 Thymeleaf 模板渲染所需的辅助能力，让 `/monitor/jobLog` 可浏览、检索、删除、清空与导出，同时让 `@dict`、`@permission` 和 `shiro:hasPermission` 在模板中正常工作。

**交付范围：**
- 新增调度日志页面 `templates/monitor/jobLog/jobLog.html`
- 为 quartz 日志页接入字典与权限模板辅助能力
- 添加 `thymeleaf-extras-shiro` 并配置 Shiro dialect
- 验证日志页与模板渲染端到端可用

**不在此阶段：**
- Phase 1 的 job/add/edit 前端对齐问题
- basWx 微信采购小程序迁入
- 生产 handler quartz 路由 gap 之外的新业务任务
</domain>

<decisions>
## Implementation Decisions

### 日志页面
- **D-01:** 调度日志页采用 `monitor/operlog/operlog.html` 的完整监控页风格，保留搜索、批量删除、清空、导出和行内详情按钮。
- **D-02:** 页面结构和 bootstrap-table 配置尽量复用现有 monitor 页面模式，只替换 quartz 日志对应的字段、路由和权限码。

### 模板辅助 Bean
- **D-03:** `@dict` 保持为面向模板的薄封装，直接复用现有字典缓存/服务能力，不另起一套字典体系。
- **D-04:** `@permission` 走独立 Bean，和 `@dict` 分开，专门提供模板里的权限判断入口。

### 权限集成
- **D-05:** 采用双轨支持：既保留 `@permission.hasPermi(...)`，也添加 `thymeleaf-extras-shiro` + `ShiroDialect`，让现有 `shiro:hasPermission` 继续生效。
- **D-06:** 不把现有模板统一改成单一路径；优先兼容当前 monitor 页面写法，减少横向改动。

### Claude's Discretion
- `jobLog` 页面的具体字段顺序和列数可按现有 `operlog` 模板风格落地。
- `@dict` / `@permission` Bean 的具体放置模块由 planner 根据现有配置归位。

</decisions>

<canonical_refs>
## Canonical References

**Downstream agents MUST read these before planning or implementing.**

### Roadmap / Requirements
- `.planning/ROADMAP.md` — Phase 2 goal and required deliverables (QTZ-05..QTZ-08)
- `.planning/REQUIREMENTS.md` — v1.1 quartz 功能完善 requirement list and acceptance criteria

### Quartz controller / frontend
- `zgbas-quartz/src/main/java/com/spt/quartz/controller/SysJobLogController.java` — job log backend routes and permissions
- `zgbas-admin/src/main/resources/templates/monitor/operlog/operlog.html` — monitor page pattern to reuse
- `zgbas-admin/src/main/resources/templates/monitor/logininfor/logininfor.html` — lighter monitor page pattern to compare against

### Existing template infrastructure
- `zgbas-system/src/main/java/com/spt/bas/server/cache/BsDictUtil.java` — existing dict cache/service bridge
- `zgbas-system/src/main/java/com/spt/bas/client/cache/BsDictUtil.java` — client-side dict helper counterpart
- `zgbas-admin/pom.xml` — current admin dependencies; no Thymeleaf Shiro dialect dependency yet

</canonical_refs>

<code_context>
## Existing Code Insights

### Reusable Assets
- `operlog.html`: complete monitor page with dict-backed filters, row detail action, batch delete, clean, and export.
- `logininfor.html`: simpler monitor table pattern if the page needed to be slimmer, but current decision favors `operlog` style.
- `BsDictUtil`: existing dict lookup/cache bridge that can back a template-facing `@dict` facade.

### Established Patterns
- Monitor pages already use `@dict.getType(...)` in Thymeleaf inline expressions.
- Existing templates already mix `@permission.hasPermi(...)` and `shiro:hasPermission`, so dual support is a fit.
- Quartz controller permissions follow the same `monitor:*` namespace as other admin monitor pages.

### Integration Points
- `zgbas-admin/src/main/resources/templates/monitor/jobLog/jobLog.html` will mirror other monitor pages.
- `zgbas-admin` Spring configuration needs template helper beans and a Shiro dialect bean.
- `zgbas-admin/pom.xml` needs the Thymeleaf extras Shiro dependency.

</code_context>

<specifics>
## Specific Ideas

- 调度日志页希望和 `operlog` 一样“完整监控页”风格，而不是极简列表。
- 权限集成希望保留当前模板写法的兼容性，不强迫全仓切换到单一种权限表达式。

</specifics>

<deferred>
## Deferred Ideas

### Reviewed Todos (not folded)
None — discussion stayed within phase scope

### Out of scope for this phase
- Phase 1 的 job/add/edit/job 前端对齐
- basWx 微信采购小程序迁入
- 生产 handler quartz 路由 gap 之外的新业务任务

</deferred>

---

*Phase: 02-quartz-log-ui-and-thymeleaf-support*
*Context gathered: 2026-07-21*
