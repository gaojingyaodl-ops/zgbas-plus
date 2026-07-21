# 02-02 SUMMARY — jobLog.html + SysJobLogController path fix

**Phase:** 02-quartz-log-ui-and-thymeleaf-support
**Plan:** 02-02
**Status:** complete
**Commits:** 2

## What Was Built

1. **jobLog.html** — 调度日志列表页（QTZ-05），镜像 operlog.html 完整监控页风格:
   - 搜索栏: jobName 文本、jobGroup 字典下拉 (sys_job_group)、status 字典下拉 (sys_common_status)、时间范围选择器
   - 工具栏: 删除（批量）/清空/导出 — 全部带 shiro:hasPermission
   - bootstrap-table 列: jobLogId, jobName, jobGroup (dict-formatted), invokeTarget, jobMessage, status (badge 0=成功/1=失败), createTime
   - 自定义 DELETE 函数: removeJobLog(单条), removeAllJobLogs(批量), cleanJobLogs(清空) — 使用 `$.ajax DELETE` 而非 `$.operate.removeAll/clean`（后者发 POST，不匹配 @DeleteMapping）
   - 路由: prefix + "/list", "/export", "/clean", "/{id}"

2. **SysJobLogController path variable fix** — line 61 的 `@GetMapping("/{configId}")` 修改为 `@GetMapping("/{jobLogId}")`，与 `@PathVariable Long jobLogId` 参数名一致（修复 RESEARCH Pitfall 3 预存 bug）

## Key Decisions Honored

- D-01: operlog.html 完整监控页风格（搜索+工具栏+bootstrap-table+script）
- D-02: 适配 SysJobLog 字段和 /monitor/jobLog 路由
- 自定义 DELETE 函数模式复用 Phase 1 job.html removeJob/removeAllJobs（发 DELETE 非 POST）
- 不添加 detailUrl/detail 按钮（Pitfall 6 — SysJobLogController 无 view-returning route）
- 不使用 $.operate.removeAll()/$.operate.clean()（发 POST → 不匹配 @DeleteMapping）

## Key Files

| File | Action | Lines |
|------|--------|-------|
| `zgbas-admin/src/main/resources/templates/monitor/jobLog/jobLog.html` | created | 221 |
| `zgbas-quartz/src/main/java/com/spt/quartz/controller/SysJobLogController.java` | modified | 1 line |

## Verification

- [x] jobLog.html 存在于 templates/monitor/jobLog/
- [x] xml namespace: xmlns:th + xmlns:shiro
- [x] 搜索栏: jobName 文本 + jobGroup/status 字典下拉 (th:with @dict.getType)
- [x] 字典 option: th:text="${dict.dictLabel}" / th:value="${dict.dictValue}"
- [x] 工具栏: 删除/清空/导出 三个按钮均有 shiro:hasPermission
- [x] script: var prefix = ctx + "monitor/jobLog"
- [x] url: prefix + "/list", cleanUrl: "/clean", exportUrl: "/export", removeUrl: prefix
- [x] columns: 7 个 SysJobLog 字段 (jobLogId/jobName/jobGroup/invokeTarget/jobMessage/status/createTime)
- [x] jobGroup: formatter → $.table.selectDictLabel(datas, value)
- [x] status: formatter → badge 0=成功/1=失败
- [x] 自定义 DELETE 函数: removeJobLog/removeAllJobLogs/cleanJobLogs
- [x] 不调用 $.operate.removeAll() 或 $.operate.clean()
- [x] 无 detailUrl 配置
- [x] SysJobLogController line 61: @GetMapping("/{jobLogId}")，旧 /{configId} 已消失
- [x] @PathVariable Long jobLogId 未修改
- [x] mvn compile -pl zgbas-quartz -am 退出码 0

## Commits

1. `9fe7be5` feat(02-02): create jobLog.html — scheduler log list page
2. `83754f1` fix(02-02): fix SysJobLogController.getInfo path variable mismatch

## Self-Check: PASSED