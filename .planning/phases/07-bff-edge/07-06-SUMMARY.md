---
phase: 07-bff-edge
plan: 06
subsystem: infra
tags: [compile-gate, maven, swagger, jdk8, springboot2]

requires:
  - phase: 07-01
    provides: StockVirtualWxVo + FeignHttpsConfig
  - phase: 07-02
    provides: 11 BFF controller + BaseController
  - phase: 07-03
    provides: 4 api 控制器
  - phase: 07-04
    provides: basWx BasicErrorController + excludeFilter
  - phase: 07-05
    provides: SC#1 路由矩阵(静态面已绿)
provides:
  - zgbas-admin 编译门零 [ERROR](SC#3/4 最终权威)+ 残余缺口回流 inventory
affects: [phase-08]

tech-stack:
  added: [io.swagger:swagger-annotations:1.6.6, com.github.xiaoymin:swagger-bootstrap-ui:1.9.6]
  patterns: [compile-gate 迭代回流:残余缺口(依赖 + 类型签名)最小适配至零 ERROR]

key-files:
  created: []
  modified:
    - zgbas-admin/pom.xml (swagger 注解依赖 ×2)
    - zgbas-admin/src/main/java/com/spt/bas/purchase/wx/server/config/BasicErrorController.java (timestamp LocalDateTime 适配)

key-decisions:
  - "残余缺口类别判定:非源码承托类缺失,而是①swagger 注解依赖缺失(源 basWx 有,单体无)②basWx ErrorResp(Date) 与单体内联 ErrorResp(LocalDateTime) 类型签名漂移"
  - "swagger 依赖 annotations-only:P7 未迁 SwaggerConfig/@EnableSwagger,仅补 swagger-annotations + swagger-bootstrap-ui(注解类),不引 springfox runtime"
  - "BasicErrorController timestamp 适配对标 active bas.server.config 版(LocalDateTime.now()),最小 1 行 + 移除未用 import"

patterns-established:
  - "WX 控制器 swagger 注解经 zgbas-admin pom 显式依赖解析,沿用源 basWx 版本号"

requirements-completed: [WX-BFF-01, WX-BFF-02]

duration: 15min
completed: 2026-07-24
---

# Phase 7 Plan 07-06: 编译门 — zgbas-admin 零 [ERROR](SC#3/4 最终权威)

**3 轮迭代回流达编译门绿:补 swagger 注解依赖(156 errors)+ BasicErrorController timestamp 适配(1 error)→ BUILD SUCCESS,[ERROR]=0**

## Performance

- **Duration:** ~15 min(含 3 轮编译迭代)
- **Tasks:** 1(迭代)
- **Files modified:** 2

## Accomplishments
- SC#3/4 编译门达成:`mvn compile -pl zgbas-admin -am` BUILD SUCCESS,5 模块全绿
- 残余缺口 2 类回流(pom 依赖 + 类型签名),无业务语义变更

## Task Commits

1. **Task 1: 编译门迭代 + 残余缺口回流** — `10c6db5` (fix)

## Files Created/Modified
- `zgbas-admin/pom.xml` — +2 依赖(swagger-annotations 1.6.6 + swagger-bootstrap-ui 1.9.6)
- `zgbas-admin/.../wx/server/config/BasicErrorController.java` — `setTimestamp(new Date())` → `setTimestamp(LocalDateTime.now())` + 移除未用 `java.util.Date` import

## Compile-Gate 迭代记录(inventory)

| 轮 | [ERROR] | symbol errors | 根因 | 回流动作 |
|---|---|---|---|---|
| 1 | 358 | 156 | WX controller/api 用 `io.swagger.annotations.{Api,ApiOperation,ApiOperationSupport,ApiSort}` 但单体无 swagger 依赖(源 basWx purchase-server 有,单体 pom 无) | zgbas-admin/pom.xml 补 swagger-annotations:1.6.6 + swagger-bootstrap-ui:1.9.6(verbatim 源版本) |
| 2 | 1 | 0 | basWx BasicErrorController `err.setTimestamp(new Date())` 与单体内联 `ErrorResp.setTimestamp(LocalDateTime)` 不兼容 | 改 `LocalDateTime.now()`(对标 active bas.server.config 版)+ 移除未用 Date import |
| 3 | 0 | 0 | — | BUILD SUCCESS ✓ |

### 残余缺口 inventory(源 → enclave/动作 → 触发方)
| 缺口 | 源 | 动作 | 触发方 |
|---|---|---|---|
| swagger-annotations | basWx/purchase-server/pom.xml | zgbas-admin pom +1 dep | 8 WX controller/api(@Api/@ApiOperation/@ApiModel) |
| swagger-bootstrap-ui (ApiOperationSupport/ApiSort) | basWx/purchase-server/pom.xml | zgbas-admin pom +1 dep | UserInfoController/BuyEnquiry/UserAttent/Contract/File/WxUser/BuyQuote/WxOpenApi |
| ErrorResp.setTimestamp 签名(Date→LocalDateTime) | 单体内联 ErrorResp(LocalDateTime) vs basWx 源(Date) | BasicErrorController 1 行适配 | basWx BasicErrorController.error() |

> 4 个 zgbas-system 文件(ApplyMatchVo/CommonUtil/RtUtil/ICtrContractSettlementAmountService)在编译日志出现仅为 [WARNING]/[INFO](sun.net 内部 API / deprecation / unchecked),非 [ERROR];P6 编译门绿不变,无需处理。

## Decisions Made

### swagger 依赖 annotations-only
- WX 文件仅用注解(`@Api`/`@ApiOperation`/`@ApiOperationSupport`/`@ApiSort`/`@ApiModel`),无 SwaggerConfig/@EnableSwagger bean 迁入(P7 scope)。
- `ApiOperationSupport`/`@ApiSort` 在 `io.swagger.annotations` 包,由 swagger-bootstrap-ui(xiaoymin,knife4j 前身)提供,非核心 swagger-annotations。
- swagger-bootstrap-ui 1.9.6 的 springfox/spring/servlet 依赖为 `provided`(不传递),仅 javassist(compile)随入 → 不引 springfox runtime auto-config,零启动副作用。
- 本地仓库已含两 jar(1.6.6 / 1.9.6),离线编译可解。

### BasicErrorController timestamp 适配(非 verbatim 偏差)
- 单体内联 `ErrorResp.timestamp: LocalDateTime`(P2 内联时定型);basWx 源 BasicErrorController 用 `new Date()`(源 ErrorResp 为 Date)。
- active `com.spt.bas.server.config.BasicErrorController`(P4 迁入)同处已用 `LocalDateTime.now()` → 本 plan 沿用同一适配,行为等价(均置当前时间戳),对标前例。

## Deviations from Plan

### Auto-fixed Issues

**1. [Rule 3 - Blocking] swagger 注解依赖缺失**
- **Found during:** Task 1 编译门轮 1
- **Issue:** plan D-P7-03 预判「源码承托类」缺口,实测为依赖库缺口(io.swagger.annotations 无 jar)。
- **Fix:** zgbas-admin pom 补 2 依赖(verbatim 源版本 1.6.6 / 1.9.6),annotations-only。
- **Verification:** 轮 2 swagger symbol errors 156→0。
- **Committed in:** 10c6db5

**2. [Rule 3 - Blocking] ErrorResp timestamp 类型签名漂移**
- **Found during:** Task 1 编译门轮 2
- **Issue:** basWx BasicErrorController `setTimestamp(new Date())` 与单体 ErrorResp(LocalDateTime) 不兼容。
- **Fix:** `setTimestamp(LocalDateTime.now())`(对标 active 版)+ 移除未用 import。
- **Verification:** 轮 3 BUILD SUCCESS。
- **Committed in:** 10c6db5

**Total deviations:** 2 auto-fixed (2× Rule 3 blocking)
**Impact on plan:** 均编译必需,无业务语义变更,无 scope creep。

## Issues Encountered
None beyond 编译门迭代回流(plan 预设该机制)。

## Next Phase Readiness
- Phase 7 静态面全绿:SC#1 路由矩阵(07-05)+ SC#3/4 编译门(本 plan)。
- runtime proof(启动 GREEN + /wx 非 404 + 自回环)留 Phase 8(WX-ALIGN-01/02/03),非 hermetic 启动态(D-P7-02)。
- 注意:swagger-bootstrap-ui 入 classpath 后,P8 启动需确认无 swagger 相关 auto-config 副作用(预期无,因无 SwaggerConfig bean)。
