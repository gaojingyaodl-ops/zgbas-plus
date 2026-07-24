---
phase: 07-bff-edge
plan: 05
subsystem: infra
tags: [route-matrix, acceptance-artifact, conflict-resolution, static-proof]

requires:
  - phase: 07-02
    provides: 11 BFF controller(裸 /wx/* + /ewechat/* + /axq)
  - phase: 07-03
    provides: 4 api(purchase/*)
  - phase: 07-04
    provides: BasicErrorController(/error 族)+ excludeFilter
  - phase: 05-report
    provides: ReportFeignPathConfig(/spt-bas-report 前缀)+ resident report /wx/*×2
provides:
  - 07-ROUTE-MATRIX.md(SC#1 强制验收产出物:三族 effective-path 矩阵 + /wx/contract 消歧静态证明 + P8 预案)
affects: [07-06, phase-08]

tech-stack:
  added: []
  patterns: [effective-path 归一化(叠加 PathMatchConfigurer 前缀)为冲突判定权威,非字面注解]

key-files:
  created:
    - .planning/phases/07-bff-edge/07-ROUTE-MATRIX.md
  modified: []

key-decisions:
  - "矩阵以 07-02/03/04 落地后 grep 实测为准(D-P7-02 basis),非假设"
  - "唯一字面重复 /wx/contract(×2)经 /spt-bas-report 前缀隔离 → 零有效路径碰撞 → D-P7-01-RESOLVED 零路由编辑"
  - "runtime 启动 proof 留 P8(非 hermetic 启动态,本阶段静态矩阵为 SC#1/SC#2 权威)"

patterns-established:
  - "路由冲突判定以 effective-path(含 PathMatchConfigurer 前缀)为准,grep 字面注解仅作 inventory"

requirements-completed: [WX-BFF-01]

duration: 8min
completed: 2026-07-24
---

# Phase 7 Plan 07-05: SC#1 三族路由矩阵 + /wx/contract 消歧结论

**grep 实测产出 SC#1 强制验收物:三族 effective-path 矩阵证明唯一字面重复 /wx/contract 经前缀隔离,零 ambiguous mapping;D-P7-01-RESOLVED 零路由编辑记录在案**

## Performance

- **Duration:** ~8 min
- **Tasks:** 2
- **Files created:** 1

## Accomplishments
- SC#1 强制验收产出物 07-ROUTE-MATRIX.md 就位
- 静态证明三族 basWx BFF 与 resident report 有效路径零重叠;/wx/contract 消歧结论固化

## Task Commits

1. **Task 1: grep 实测三族 @RequestMapping** — 无独立文件(数据汇入 Task 2)
2. **Task 2: 写 07-ROUTE-MATRIX.md** — `e1965c7` (docs)

## Files Created/Modified
- `07-ROUTE-MATRIX.md` — SC#1 验收物:§1 前缀机制 + §2 三族归一化表(11 controller + 4 api + /error + resident report /wx/*×2)+ §3 /wx/contract 消歧 + §4 collision sweep + §5 SC 验收声明 + §6 P8 回退预案

## Decisions Made

### 实测结果(grep)
- basWx controller:11 基路径(7 /wx/* + 3 /ewechat/* + 1 /axq),各 count 1
- basWx api:4 基路径(purchase/{open,saveTemp,user,userDetail})
- resident report /wx/*:2(RptCtrContractApi + RptWxBrandFollowApi),均 report.server.api 包 → 经 `/spt-bas-report` 前缀
- collision sweep 唯一重复:`/wx/contract` ×2(basWx 裸 + report),经前缀隔离

### /wx/contract 消歧(D-P7-01-RESOLVED)
- ContractController 有效路径 `/wx/contract/*`(裸)vs RptCtrContractApi 有效路径 `/spt-bas-report/wx/contract/*`(前缀)→ 不同 → 无 AmbiguousMappingException。
- 10 字面重叠读方法经前缀全隔离。
- 零路由编辑:ContractController/RptCtrContractApi/IRptWxCtrContractClient 均不动。

## Deviations from Plan
None — 按 plan 产出。

## Issues Encountered
None.

## Next Phase Readiness
- SC#1/SC#2 静态面绿;剩 SC#3/4(编译门)由 07-06 完成 → Phase 7 静态面全绿。
- runtime proof(/wx 非 404 + 自回环 + 启动 GREEN)留 Phase 8;P8 回退预案就位(理论不应触发)。
