---
gsd_version: 2.0
milestone: v1.1
milestone_name: quartz功能完善
created: "2026-07-21T12:00:00.000Z"
status: draft
---

# REQUIREMENTS: quartz 功能完善

## Goal

完善 quartz 定时任务模块的前端管理页面，修复 400 错误，提供完整的定时任务可视化管理能力（CRUD + 日志查看）。

## Background

Phase 6 完成了 quartz 后端迁移（SysJobController → SysJobLogController → Service → Mapper → Quartz 调度器），但前端存在多个 gap：

1. **菜单点击 400 错误** — 外部 spt-auth sys_menu 的 component 路径 `monitor/job/job` 被 Spring 误路由到 `@GetMapping("/{jobId}")`，类型转换失败
2. **前端 JS API 不匹配** — Thymeleaf 模板中的 `$.operate.save/add/edit` 调用路径/方法与 Controller 实际路由不一致
3. **ry-ui.js 缺少 PUT 方法** — `changeStatus`、`run` 端点用 `@PutMapping`，但 `$.operate.put` 不存在
4. **调度日志页面缺失** — SysJobLogController 的 REST API 就绪，但无前端页面
5. **Thymeleaf 辅助 Bean 缺失** — 模板引用了 `@dict.getType()` 和 `@permission.hasPermi()`，但没有对应的 Spring Bean
6. **Shiro Thymeleaf 方言缺失** — 模板使用了 `xmlns:shiro` 和 `shiro:hasPermission`，但没有 `thymeleaf-extras-shiro` 依赖

## Requirements

### Must Have

| ID | Requirement | Rationale |
|----|-------------|-----------|
| QTZ-01 | 修复 `/monitor/job/job` 400 错误 | 菜单入口不可用，阻塞所有操作 |
| QTZ-02 | 修复 add.html 表单提交（路径+方法对齐 Controller） | 新增任务功能不可用 |
| QTZ-03 | 修复 edit.html 表单提交（路径+方法+请求体格式对齐 Controller） | 编辑任务功能不可用 |
| QTZ-04 | 修复 job.html 状态切换/删除/执行操作（JS API 对齐 Controller） | 启停/删除/手动执行不可用 |
| QTZ-05 | 创建调度日志前端页面 `templates/monitor/jobLog/jobLog.html` | 日志查询、删除、清空、导出功能缺失 |
| QTZ-06 | 提供 Thymeleaf `@dict` 和 `@permission` 辅助 Bean | 模板渲染依赖的字典查询和权限检查抛 NPE |
| QTZ-07 | 验证完整 CRUD + 日志流程端到端可用 | 确保修复有效，无回归 |

### Should Have

| ID | Requirement | Rationale |
|----|-------------|-----------|
| QTZ-08 | 添加 `thymeleaf-extras-shiro` 依赖并配置 ShiroDialect | 让 `shiro:hasPermission` 属性正确控制按钮显隐 |
| QTZ-09 | 外部 spt-auth sys_menu UPDATE（component path 需与 Thymeleaf view name 对齐） | 从侧边栏菜单点击能正确导航到任务列表页面 |

### Nice to Have

| ID | Requirement | Rationale |
|----|-------------|-----------|
| QTZ-10 | 讨论 Controller 是否移入 zgbas-admin 模块 | 用户提出的架构问题 |
| QTZ-11 | sys_job_data.sql 88 条数据中的 38 条 "排除"数据 review | Phase 6 遗留的未迁移 handler gap |

## Out of Scope

- basWx 微信采购小程序迁入（v1.2）
- Quartz 集群配置优化
- 新增业务 Task 开发
- `thymeleaf-extras-shiro` 之外的 Shiro 方言自定义扩展

## Controller 位置分析

用户问：controller 是否可以放到 admin 模块中？

**结论：不建议移动。** 原因：
1. Controller 依赖 `ISysJobService` / `ISysJobLogService` → Service → Mapper → Domain，全链路在 quartz 模块内，保持模块内聚
2. `@ComponentScan("com.spt")` 已自动发现 quartz controller（无需额外配置）
3. 其他 monitor 页面（cache / online / operlog / server / logininfor）的 Controller 也不在 admin 模块
4. 移动 controller 需要将它依赖的所有 quartz 类型声明为 admin 的编译期依赖（不增加价值）
5. RuoYi 参考实现也保持 quartz controller 在 quartz 模块内

**如果仍要移动**，需要：
- `zgbas-admin/pom.xml` 添加 `zgbas-quartz` 依赖（已有）
- 将 `SysJobController` + `SysJobLogController` 移到 `zgbas-admin/src/main/java/com/spt/bas/web/controller/monitor/`
- 无需修改 ComponentScan（`com.spt` 包含 `com.spt.bas.web`）

## Verification

- QTZ-01: 浏览器访问 `/monitor/job` 返回 200 + 任务列表页面（非 400）
- QTZ-02/03: 新增/编辑任务表单提交成功，数据库 SQL 验证记录写入
- QTZ-04: 启停/删除/立即执行在页面操作成功
- QTZ-05: 浏览器访问 `/monitor/jobLog` 返回调度日志列表页面
- QTZ-06: 页面渲染不出错（无 NPE），字典下拉框有数据
