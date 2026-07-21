# ROADMAP: quartz 功能完善

## Milestones

- ✅ **v1.0 单体化重构交付** — Phases 1–7 (shipped 2026-07-20)
- 📋 **v1.1 quartz 功能完善** — Phases 1–2 (draft)

## v1.1 Phases

### Phase 1: 前端接口对齐 + 400 修复

**Goal**: 修复 `/monitor/job/job` 400 错误；修复 add/edit/job 三个 Thymeleaf 页面的 JS API 与 Controller 路由不匹配；使定时任务 CRUD + 启停 + 立即执行全部前端可用

**Depends on**: v1.0 Phase 6 (quartz 后端已就绪)
**Requirements**: QTZ-01, QTZ-02, QTZ-03, QTZ-04
**Plans**: 3 plans

Plans:
**Wave 1**

- [x] 01-01-PLAN.md — 修复 /monitor/job/job 400 错误 (QTZ-01)
- [x] 01-02-PLAN.md — 修复 add.html + edit.html 表单提交 API 对齐 (QTZ-02, QTZ-03)

**Wave 2** *(blocked on Wave 1 completion)*

- [x] 01-03-PLAN.md — 修复 job.html 删除/执行/启停 + 添加 $.operate.put (QTZ-04)

### Phase 2: 调度日志页面 + Thymeleaf 辅助 Bean

**Goal**: 创建调度日志前端页面；提供 `@dict` 和 `@permission` Thymeleaf 辅助 Bean；添加 `thymeleaf-extras-shiro` 依赖

**Depends on**: Phase 1
**Requirements**: QTZ-05, QTZ-06, QTZ-07, QTZ-08
**Estimated Plans**: 2–3

**关键交付物**：

1. **QTZ-05**: `templates/monitor/jobLog/jobLog.html` — 日志列表页（bootstrap-table，含搜索/删除/清空/导出）
2. **QTZ-06**: `DictService` Bean（`@dict.getType(...)`）+ `PermissionService` Bean（`@permission.hasPermi(...)`）→ 或在 zgbas-system 中找已有实现
3. **QTZ-07**: 端到端验证 + 回归测试
4. **QTZ-08**: `thymeleaf-extras-shiro` 依赖 + `ShiroDialect` Bean（`shiro:hasPermission` 标签生效）

## Progress

| Phase | Plan | Status |
|-------|------|--------|
| 1. 前端接口对齐 + 400 修复 | 01-01: 400 修复 | Planned |
| 1. 前端接口对齐 + 400 修复 | 01-02: add/edit 表单修复 | Planned |
| 1. 前端接口对齐 + 400 修复 | 01-03: job 操作修复 + $.operate.put | Planned |
| 2. 调度日志页面 + 辅助 Bean | — | Planned |

---
*Created: 2026-07-21*
*Updated: 2026-07-21 — Phase 1 plans created (3 plans)*
