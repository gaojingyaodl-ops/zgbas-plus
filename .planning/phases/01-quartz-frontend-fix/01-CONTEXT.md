# Phase 1: 前端接口对齐 + 400 修复 — Context

**Gathered:** 2026-07-21
**Status:** Ready for planning
**Source:** ROADMAP.md + REQUIREMENTS.md (v1.1: quartz 功能完善)

<domain>
## Phase Boundary

Phase 1 修复 quartz 定时任务模块的前端 JS API 与后端 Controller 路由不匹配问题，使定时任务 CRUD + 启停 + 立即执行全部前端可用。

**交付范围：**
- 修复 `/monitor/job/job` 400 错误（外部 spt-auth 菜单入口）
- 修复 add.html / edit.html / job.html 三个 Thymeleaf 页面的 JS API 调用
- 使新增/编辑/删除/启停/立即执行 全部功能在前端可用

**不在此阶段：**
- 调度日志页面（Phase 2）
- Thymeleaf 辅助 Bean (@dict / @permission)（Phase 2）
- thymeleaf-extras-shiro 集成（Phase 2）
- sys_job_data.sql 数据 review
</domain>

<decisions>
## Implementation Decisions

### QTZ-01: 400 错误修复
- **Root cause**: 外部 spt-auth sys_menu 的 component 路径 `monitor/job/job` 被 Spring MVC 误路由到 `@GetMapping("/{jobId}")` 上，`"job"` 字符串 → Long 类型转换失败
- **Fix**: 在 SysJobController 中添加 `@GetMapping("/job")` 显式处理器（或重排 `@GetMapping` 方法顺序），让 Spring 优先匹配字面路径
- **注意**: 当前 Controller 已有 `@GetMapping()` 返回 `job` 模板页，和 `@GetMapping("/{jobId}")` 获取详情。问题在于 spt-auth 菜单 component 路径是 `monitor/job/job`，会匹配 `/{jobId}` 而非 `/` 的空路径

### QTZ-02: add.html 表单提交修复
- **Current state (broken)**:
  - `$.operate.save(prefix + "/add", $('#form-job-add').serialize())` → POST `/monitor/job/add` with form-encoded body
- **Controller expects**:
  - `@PostMapping` (路径 `/monitor/job`) + `@RequestBody SysJob` (JSON body)
- **Fix**: URL 改为 `prefix`（非 `prefix + "/add"`），data 从 `.serialize()` 改为 JSON.stringify 构造

### QTZ-03: edit.html 表单提交修复
- **Current state (broken)**:
  - `$.operate.save(prefix + "/edit", $('#form-job-edit').serialize())` → POST `/monitor/job/edit` with form-encoded body
- **Controller expects**:
  - `@PutMapping` (路径 `/monitor/job`) + `@RequestBody SysJob` (JSON body)
- **Fix**: URL 改为 `prefix`，HTTP method 改为 PUT，data 改为 JSON body

### QTZ-04: job.html 操作按钮修复
- **删除 (removeUrl)**:
  - Current: `removeUrl: prefix + "/remove"` → POST `/monitor/job/remove` with `{ids: id}`
  - Controller: `@DeleteMapping("/{jobIds}")` → DELETE `/monitor/job/{jobIds}`
  - Fix: `removeUrl` 改为 `prefix + "/{id}"`，`$.operate.remove()` 改为发送 DELETE + 路径参数

- **立即执行 (runJob)**:
  - Current: `$.operate.post(prefix + "/run", {"jobId": jobId})` → POST `/monitor/job/run`
  - Controller: `@PutMapping("/run")` + `@RequestBody SysJob`
  - Fix: 改为 PUT 请求 + JSON body

- **状态切换 (startJob/pauseJob)**:
  - Current: `$.operate.put(prefix + "/changeStatus", {...})` — BUT `$.operate.put` DOES NOT EXIST in ry-ui.js
  - Controller: `@PutMapping("/changeStatus")` + `@RequestBody SysJob`
  - Fix: 使用 `$.operate.save` 或 `$.ajax` 直接发 PUT 请求 + JSON body

### Claude's Discretion
- job.html 的 bootstrap-table options（createUrl/updateUrl/removeUrl）是否需要同步修改以匹配 Controller RESTful 路由
- `$.operate.put` 不存在时的具体替代方案（在 ry-ui.js 中添加 `.put()` 方法 vs 使用 `$.ajax` 直接调用）
- 是否需要考虑 spt-auth 的 sys_menu 表更新（component path 对齐）— 这是 QTZ-09 (should have)，Phase 1 先放 defer
</decisions>

<canonical_refs>
## Canonical References

**Downstream agents MUST read these before planning or implementing.**

### Controller
- `zgbas-quartz/src/main/java/com/spt/quartz/controller/SysJobController.java` — 所有后端路由定义（@GetMapping/@PostMapping/@PutMapping/@DeleteMapping）

### Thymeleaf 模板 (前端)
- `zgbas-admin/src/main/resources/templates/monitor/job/job.html` — 列表页（bootstrap-table + 操作按钮）
- `zgbas-admin/src/main/resources/templates/monitor/job/add.html` — 新增表单
- `zgbas-admin/src/main/resources/templates/monitor/job/edit.html` — 编辑表单

### JS 基础设施
- `zgbas-admin/src/main/resources/static/ruoyi/js/ry-ui.js` — `$.operate` 工具方法（save/post/get/remove/add/edit，**无 `.put()` 方法**）

### RuoYi 参考
- RuoYi 原版 SysJobController SPI 路由约定：RESTful 风格，`@PostMapping` (create)、`@PutMapping` (update)、`@DeleteMapping("/{ids}")` (delete)
- RuoYi 原版 ry-ui.js `$.operate` 约定：`save` 发 POST、`remove` 发 POST with `{ids}`
</canonical_refs>

<specifics>
## Specific Ideas

### 问题模式
所有问题都源于同一个根因：Phase 6 把 RuoYi 风格的 `@RestController` + `@PreAuthorize` 移植过来后，做了三处关键适配（`@RestController→@Controller`、spring-security→Shiro、添加 view-returning handlers），但前端 Thymeleaf 模板从 RuoYi 原版直接搬过来后，JS 层的 URL 和 HTTP 方法没有同步适配。

### Controller 路由总结
| 功能 | HTTP | 路径 | Body | 说明 |
|------|------|------|------|------|
| 列表页 | GET | `/monitor/job` | — | 返回 job.html |
| 新增页 | GET | `/monitor/job/add` | — | 返回 add.html |
| 编辑页 | GET | `/monitor/job/edit/{jobId}` | — | 返回 edit.html |
| 查询列表 | POST | `/monitor/job/list` | form | `$.table.init` 自动处理 |
| 新增提交 | POST | `/monitor/job` | JSON @RequestBody | `$.operate.save` 适配 |
| 编辑提交 | PUT | `/monitor/job` | JSON @RequestBody | 需手动 PUT |
| 删除 | DELETE | `/monitor/job/{jobIds}` | path | RESTful 风格 |
| 状态切换 | PUT | `/monitor/job/changeStatus` | JSON @RequestBody | `$.operate.put` 不存在 |
| 立即执行 | PUT | `/monitor/job/run` | JSON @RequestBody | `$.operate.put` 不存在 |
| 导出 | POST | `/monitor/job/export` | form | `$.table.exportExcel` 自动处理 |
</specifics>

<deferred>
## Deferred Ideas

- QTZ-09: spt-auth sys_menu UPDATE（component path 对齐）→ Phase 2 或手动操作
- QTZ-05/06/07/08: 调度日志页面 + 辅助 Bean + 端到端验证 → Phase 2
- Controller 是否移入 zgbas-admin → 结论：不建议移动（保持 quartz 模块内聚）
- sys_job_data.sql 数据 review → Phase 2 或后续
</deferred>

---

*Phase: 01-quartz-frontend-fix*
*Context gathered: 2026-07-21 from ROADMAP.md + REQUIREMENTS.md + 代码审查*
