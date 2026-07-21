---
phase: 01-quartz-frontend-fix
plan: 03
subsystem: quartz
tags: [frontend, quartz, js-api, http-method, delete, put, job, ry-ui]
depends_on: [01-01, 01-02]
provides: [QTZ-04-fix]
affects:
  - job.html script
  - ry-ui.js $.operate
tech-stack:
  added:
    - $.operate.put (ry-ui.js utility)
  patterns:
    - RESTful DELETE with path parameters
    - JSON PUT with application/json content type
key-files:
  created: []
  modified:
    - zgbas-admin/src/main/resources/static/ruoyi/js/ry-ui.js
    - zgbas-admin/src/main/resources/templates/monitor/job/job.html
decisions:
  - Add $.operate.put() to ry-ui.js rather than inline $.ajax at every call site
  - Custom removeJob/removeAllJobs functions rather than reusing $.operate.remove/removeAll (which hardcode POST + form body)
metrics:
  duration: 352
  completed_date: 2026-07-21
---

# Phase 01 Plan 03: job.html Operation Button Fix Summary

修复定时任务列表页全部操作按钮的 JS API 对齐后端 Controller：删除改用 DELETE + 路径参数，立即执行改用 PUT + JSON body，启停通过新增的 $.operate.put 自动生效。

## Completed Tasks

### Task 1: Add $.operate.put Method to ry-ui.js

**Commit:** `4057f29` — feat(01-quartz-frontend-fix): add $.operate.put method to ry-ui.js

在 ry-ui.js 的 `$.operate` 对象中，`$.operate.post` 和 `$.operate.get` 之间新增了 `$.operate.put(url, data, callback)` 方法。该方法直接通过 `$.ajax` 发送 PUT 请求，设置 `contentType: "application/json;charset=utf-8"` 和 `data: JSON.stringify(data)`，确保 Controller 的 `@RequestBody SysJob` 能正确反序列化 JSON body。

**文件修改:** `zgbas-admin/src/main/resources/static/ruoyi/js/ry-ui.js` (+19 行)

**关键实现细节:**
- `type: "put"` — HTTP PUT 请求
- `contentType: "application/json;charset=utf-8"` — JSON 请求体类型
- `data: JSON.stringify(data)` — JS 对象转 JSON 字符串
- `success` 回调调用 `$.operate.ajaxSuccess(result)` 保持统一的行为（表格刷新、弹窗关闭、loading 关闭）

### Task 2: Fix job.html Delete Flow + runJob Uses $.operate.put

**Commit:** `f81ceb3` — fix(01-quartz-frontend-fix): fix job.html delete flow and runJob to use $.operate.put

修复了 job.html 的全部四项操作:

1. **单条删除 (removeJob):** 自定义函数发送 `DELETE /monitor/job/{jobId}`，路径参数匹配 `@DeleteMapping("/{jobIds}")`
2. **批量删除 (removeAllJobs):** 自定义函数发送 `DELETE /monitor/job/{id1,id2,...}`，逗号分隔多 ID 路径参数
3. **立即执行 (runJob):** `$.operate.post` 改为 `$.operate.put`，对齐 `@PutMapping("/run")`
4. **启停 (startJob/pauseJob):** 无需修改 — 已调用 `$.operate.put`，Task 1 新增方法后自动生效

**文件修改:** `zgbas-admin/src/main/resources/templates/monitor/job/job.html` (+56 行 / -4 行)

**关键变更:**
- `removeUrl: prefix + "/remove"` → `removeUrl: prefix`
- `onclick="$.operate.remove(...)"` → `onclick="removeJob(...)"`
- `onclick="$.operate.removeAll()"` → `onclick="removeAllJobs()"`
- `$.operate.post(prefix + "/run", ...)` → `$.operate.put(prefix + "/run", ...)`

## Verification Results

### Source Assertions (Task 1)

| Check | Status |
|-------|--------|
| `put: function` exists in ry-ui.js | PASS (line 1067) |
| `application/json` content type set | PASS (line 1072) |
| `"put"` request type | PASS (line 1070) |
| Method order: post → put → get | PASS (1063, 1067, 1086) |

### Source Assertions (Task 2)

| Check | Status |
|-------|--------|
| `removeUrl: prefix,` | PASS |
| `function removeJob` exists | PASS |
| `function removeAllJobs` exists | PASS |
| `"delete"` appears (2x: removeJob + removeAllJobs) | PASS (2 occurrences) |
| runJob uses `$.operate.put` not `$.operate.post` | PASS |
| `$.operate.remove()` zero calls in job.html | PASS (0 occurrences) |
| `$.operate.put()` 3 calls in job.html (runJob + startJob + pauseJob) | PASS (lines 188, 203, 209) |

## Deviations from Plan

None — plan executed exactly as written.

## Known Stubs

None — all data flows are wired to real Controller endpoints.

## Threat Flags

None — this plan modifies only frontend JS files, no new network endpoints or auth paths introduced.

## Self-Check: PASSED

- [x] ry-ui.js modified with $.operate.put method
- [x] job.html modified with removeJob, removeAllJobs, runJob fix
- [x] Commit 4057f29 verified in git log
- [x] Commit f81ceb3 verified in git log
- [x] No deletions, no untracked generated files
