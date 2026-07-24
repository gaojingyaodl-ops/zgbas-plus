---
phase: 07-bff-edge
plan: 04
subsystem: infra
tags: [error-controller, component-scan, bean-conflict, wx-enclave, verbatim-migration]

requires:
  - phase: 04-infra
    provides: ZgbasApplication excludeFilters 机制(Plan 04-05/04-06 前例)+ active com.spt.bas.server.config.BasicErrorController
provides:
  - basWx BasicErrorController 在 classpath(包飞地,static getErrorResp 工具)+ 不注册为 bean(excludeFilter)
affects: [07-05, 07-06, phase-08]

tech-stack:
  added: []
  patterns: [同名 @Controller bean 冲突经 @ComponentScan assignable-type excludeFilter 消解(延续 Plan04-05/04-06)]

key-files:
  created:
    - zgbas-admin/src/main/java/com/spt/bas/purchase/wx/server/config/BasicErrorController.java
  modified:
    - zgbas-admin/src/main/java/com/spt/ZgbasApplication.java (excludeFilters + javadoc)

key-decisions:
  - "D-P7-04 deviation(plan 预判分支触发):单体实测已有 4 个 ErrorController implementor,其中 3 个同名 BasicErrorController;basWx 版同 simple name → 同 bean 名 basicErrorController → 与 active bas.server.config 版冲突(ConflictingBeanDefinitionException)"
  - "Resolution(对标 Plan04-05/04-06 前例,Rule-3 blocking):verbatim 落 classpath + 加入 excludeFilters 不注册为 bean;com.spt.bas.server.config.BasicErrorController 保持唯一 active"
  - "行为等价(北极星):全局 /error 处理经 active controller 不变;basWx 版与 source 一样非唯一 active(source 靠 module-isolated scan,monolith 靠 filter)"

patterns-established:
  - "WX config 包飞地 com.spt.bas.purchase.wx.server.config 与 controller/api 同级;同名 bean 冲突一律走 excludeFilter 而非限域注解"

requirements-completed: [WX-BFF-01]

duration: 12min
completed: 2026-07-24
---

# Phase 7 Plan 07-04: BasicErrorController 迁入 + /error 碰撞验证

**Task1 实测推翻 plan「单体无第二个自定义 ErrorController」前提 → deviation:verbatim 落 classpath + excludeFilter 防同名 bean 冲突(延续 Plan04-05/04-06 前例),全局 /error 处理不变**

## Performance

- **Duration:** ~12 min(含碰撞调研)
- **Tasks:** 2
- **Files created:** 1 | **modified:** 1

## Accomplishments
- Task1 grep 坐实单体已有 4 个 ErrorController implementor(3 个同名 BasicErrorController + ServerErrorController)
- 沿用 Plan04-05/04-06 前例正确消解 basWx 版同名 bean 冲突,避免 Phase 8 启动 ConflictingBeanDefinitionException

## Task Commits

1. **Task 1: /error 碰撞预验证(grep)** — 无文件产出(结论驱动 Task 2 决策)
2. **Task 2: verbatim 迁入 + excludeFilter 偏差** — `c9fcca9` (feat)

## Files Created/Modified
- `zgbas-admin/.../wx/server/config/BasicErrorController.java` — verbatim 落 classpath,`public static getErrorResp(Throwable)` 工具保留
- `zgbas-admin/.../ZgbasApplication.java` — `excludeFilters.classes[]` 增 `com.spt.bas.purchase.wx.server.config.BasicErrorController.class` + javadoc Phase 7 条目

## Decisions Made

### Task 1 实测结论(推翻 plan 前提)
grep `implements ErrorController` 命中单体已存在:
| 类 | 包 | 模块 | 角色 |
|---|---|---|---|
| BasicErrorController | com.spt.tools.http.interceptor | common | **excluded**(Plan04-05) |
| BasicErrorController | com.spt.bas.server.config | system | **active**(唯一 ErrorController) |
| BasicErrorController | com.spt.bas.web.config | admin/web | **excluded**(Plan04-06,留 static getErrorResp) |
| ServerErrorController | com.spt.tools.http.interceptor | common | active(json /error) |
| **BasicErrorController** | **com.spt.bas.purchase.wx.server.config** | **admin(本 plan)** | **excluded(本 plan)** |

`error.path`/`server.error` 在 application-dev.yml 无覆盖(默认 /error)。

### 偏差 rationale
- basWx BasicErrorController 同 simple name → Spring 默认 bean 名 `basicErrorController` → 与 active `bas.server.config` 版二选一冲突 → **ConflictingBeanDefinitionException**(Phase 8 启动 blocker)。
- 排除机制 = assignable-type excludeFilter(非 @ConditionalOnMissingBean,因那是针对 Spring Boot 默认 error controller 的退避,不解决多 custom 同名冲突)。
- basWx 版与 source 一样非唯一 active(source 靠 module-isolated scan,monolith 靠 filter),行为等价。
- 保留 classpath:D-05 config edge 结构保真 + `public static getErrorResp(Throwable)` 工具可用(同 web 版前例,BFF 可能引用)。

## Deviations from Plan

### Auto-fixed Issue

**1. [Rule 3 - Blocking] basWx BasicErrorController 同名 bean 冲突 → excludeFilter**
- **Found during:** Task 1(/error 碰撞预验证)
- **Issue:** plan 假设「单体无第二个自定义 ErrorController」→ 期望 verbatim + @ConditionalOnMissingBean 自然退避;实测单体已有 3 个同名 BasicErrorController,active 已锁定 bas.server.config 版,verbatim 不排除会启动崩溃。
- **Fix:** verbatim 落 classpath + ZgbasApplication excludeFilters 增该类(对标 Plan04-06 web 版前例)。
- **Files modified:** ZgbasApplication.java
- **Verification:** excludeFilters 现含 5 类;active bas.server.config 版未被排除。
- **Committed in:** c9fcca9 (Task 2)

**Total deviations:** 1 auto-fixed (Rule 3 blocking)
**Impact on plan:** 必要 — 避免 Phase 8 启动 bean 冲突。无 scope creep(沿用既有前例机制,不改业务语义)。

## Issues Encountered
None(plan 已预判该分支并给出决策框架)。

## Next Phase Readiness
- /error 族静态面就位;07-05 矩阵归一化 /error 族(active controller 映射)。
- 07-06 编译门确认 basWx BasicErrorController + ZgbasApplication 编译通过(excludeFilter 引用类需在 classpath,已满足)。
- Phase 8 runtime:启动不应报 ConflictingBeanDefinitionException(active 唯一)。
