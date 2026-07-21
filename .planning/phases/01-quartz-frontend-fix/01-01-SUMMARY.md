---
phase: 01-quartz-frontend-fix
plan: 01
subsystem: quartz-frontend
tags: [quartz, frontend, routing, spring-mvc]
depends_on: []
tech-stack:
  added: []
  patterns: [Spring MVC literal path precedence, @GetMapping view handler]
key-files:
  created: []
  modified:
    - zgbas-quartz/src/main/java/com/spt/quartz/controller/SysJobController.java
decisions:
  - "新增 @GetMapping(\"/job\") 显式字面路径处理器，让 Spring MVC 优先匹配字面 /job 而非路径变量 /{jobId}"
  - "jobView() 方法复用 view() 的 Thymeleaf 视图名 prefix + '/job'，返回同一个 job.html 列表页"
  - "不加 @RequiresPermissions — 与现有 view() 一致，权限由 Shiro 链处理"
metrics:
  duration: 3 min
  completed_date: 2026-07-21
---

# Phase 01 Plan 01: 400 错误修复 — @GetMapping("/job")

修复 spt-auth 菜单 `monitor/job/job` 导航时 Spring MVC 将 `/job` 误路由到 `@GetMapping("/{jobId}")` 导致 TypeMismatchException 的 400 错误。

## What Was Done

### Task 1: 添加 @GetMapping("/job") 显式路由处理器 (QTZ-01)

**修改文件:** `zgbas-quartz/src/main/java/com/spt/quartz/controller/SysJobController.java`

**变更内容:**
- 在 `view()` 方法（line 64）和 `add()` 方法（line 72）之间新增 `jobView()` 方法
- 注解: `@GetMapping("/job")`
- 返回: `prefix + "/job"`（即 Thymeleaf 模板 `templates/monitor/job/job.html`）
- Spring MVC 字面路径 `/job` 优先于路径变量 `/{jobId}` 匹配，不再出现 400

## Deviations from Plan

None — plan executed exactly as written.

## Verification Results

- PASS: `@GetMapping("/job")` 存在于 line 74
- PASS: `jobView()` 返回 `prefix + "/job"` (line 75-76)
- PASS: 编译零错 — `mvn compile -pl zgbas-quartz` 无 ERROR 输出

## Commits

| # | Hash | Message |
|---|------|---------|
| 1 | *pending* | fix(01-01): add @GetMapping("/job") to fix /monitor/job/job 400 error |
