---
phase: 01-quartz-frontend-fix
plan: 02
subsystem: quartz-frontend
tags: [quartz, frontend, js-api, form-submit]
depends_on: []
tech-stack:
  added: []
  patterns: [jQuery serializeArray, JSON body submission, $.operate.successCallback]
key-files:
  created: []
  modified:
    - zgbas-admin/src/main/resources/templates/monitor/job/add.html
    - zgbas-admin/src/main/resources/templates/monitor/job/edit.html
decisions:
  - "使用 $.ajax 直调替代 $.operate.save，实现 JSON body + 正确 HTTP method + 正确 URL"
  - "表单序列化使用 jQuery.serializeArray() 而非 .serialize()，生成 JSON 对象以匹配 @RequestBody"
  - "成功回调复用 $.operate.successCallback() 保持模态框关闭 + 父窗口表格刷新的标准行为"
metrics:
  duration: 2 min
  completed_date: 2026-07-21
---

# Phase 01 Plan 02: add/edit 表单提交 JS API 修复

修复 quartz 定时任务模块 add.html 和 edit.html 的表单提交 JS API，使其与 SysJobController 的 RESTful 路由匹配。

## What Was Done

### Task 1: 修复 add.html 表单提交 (QTZ-02)

**修改文件:** `zgbas-admin/src/main/resources/templates/monitor/job/add.html`

**变更内容:**
- `$.operate.save(prefix + "/add", $('#form-job-add').serialize())` 替换为 `$.ajax` 直调
- URL: `prefix + "/add"` -> `prefix`（即 `/monitor/job`）
- HTTP method: POST（保持不变）
- Body: form-encoded 字符串 -> JSON 对象（`JSON.stringify(serializeArray()` 构建的 plain object）
- Content-Type: 默认 -> `application/json;charset=utf-8`
- 成功回调: 委托 `$.operate.successCallback(result)` 保持模态框自动关闭 + 父窗口表格刷新

### Task 2: 修复 edit.html 表单提交 (QTZ-03)

**修改文件:** `zgbas-admin/src/main/resources/templates/monitor/job/edit.html`

**变更内容:**
- `$.operate.save(prefix + "/edit", $('#form-job-edit').serialize())` 替换为 `$.ajax` 直调
- URL: `prefix + "/edit"` -> `prefix`（即 `/monitor/job`）
- HTTP method: POST -> PUT（匹配 `@PutMapping`）
- Body: form-encoded 字符串 -> JSON 对象
- Content-Type: `application/json;charset=utf-8`
- 隐藏字段 `jobId` 通过 `serializeArray()` 自动包含在 JSON body 中

## Deviations from Plan

None - plan executed exactly as written.

## Verification Results

### Task 1 (add.html) - Source assertions all PASSED
- PASS: `$.operate.save` removed
- PASS: `JSON.stringify` present
- PASS: `application/json` content type present
- PASS: Uses `prefix` (not `prefix + "/add"`)
- PASS: `serializeArray()` present

### Task 2 (edit.html) - Source assertions all PASSED
- PASS: `$.operate.save` removed
- PASS: PUT method (`"put"`)
- PASS: `JSON.stringify` present
- PASS: `application/json` content type present
- PASS: Uses `prefix` (not `prefix + "/edit"`)

## Known Stubs

None. Form submissions are fully wired with JSON body serialization and success callback delegation.

## Commits

| # | Hash | Message |
|---|------|---------|
| 1 | 46e48aa | fix(01-02): Task 1 — add.html submitHandler POST /monitor/job with JSON body |
| 2 | 680e781 | fix(01-02): Task 2 — edit.html submitHandler PUT /monitor/job with JSON body |
