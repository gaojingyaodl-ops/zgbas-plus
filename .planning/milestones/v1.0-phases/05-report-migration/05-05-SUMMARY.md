---
phase: 05-report-migration
plan: 05
subsystem: report-api
tags: [controller, report-api, startup-gate, bean-disambiguation, path-prefix, w5-probe]
requirements: [REPORT-02, PERSIST-02]
requires:
  - "05-04: 53/53 report mapper+xml+service baseline in zgbas-system"
  - "05-01: ReportFeignPathConfig + report mybatis wiring + report self-loopback probe"
provides:
  - "54 report api controllers migrated into zgbas-admin under com.spt.bas.report.server.api"
  - "W5 startup proofs added to ZgbasApplicationTest: allReportMappersResolve + reportApiPathPrefixWiring_probe"
  - "Spring startup gate green after minimal controller/service bean disambiguation fixes"
affects:
  - "05-06 acceptance proofs can now validate report HTTP reachability against live admin-embedded report APIs"
  - "report Feign contracts now have concrete admin-side target controllers to self-loop into"
tech-stack:
  added: []
  patterns:
    - "D-P2-07 照搬保包名 — report api package copied verbatim into admin"
    - "startup-gate-first — each newly surfaced Spring wiring blocker fixed with minimal bean-name or injection disambiguation"
    - "W5 proof expansion inside ZgbasApplicationTest before moving to W6 acceptance"
key-files:
  created:
    - "zgbas-admin/src/main/java/com/spt/bas/report/server/api/ (54 files)"
  modified:
    - "zgbas-admin/src/main/java/com/spt/bas/report/server/api/RptBaseCostApi.java"
    - "zgbas-admin/src/main/java/com/spt/bas/report/server/api/RptApplyBusinessPayApi.java"
    - "zgbas-admin/src/test/java/com/spt/ZgbasApplicationTest.java"
decisions:
  - "Keep all 54 report APIs in admin with source package and source text preserved; only apply minimal Spring bean disambiguation where startup proves it is required"
  - "Treat surefire report output as the source of truth for startup failures instead of truncated Maven console tails"
metrics:
  completed: "2026-07-18"
---

# Phase 5 Plan 05: Report API Migration Summary

54 个 report api controller 已迁入 `zgbas-admin`，W5 启动验证探针已补齐，并通过两处最小 Spring 消歧修复把 `ZgbasApplicationTest` 恢复到 23/23 全绿。

## Accomplishments
- 从源 `basReport/reportServer` 将 54 个 `com.spt.bas.report.server.api.Rpt*Api` 原样迁入 `zgbas-admin/src/main/java/com/spt/bas/report/server/api/`。
- 在 `ZgbasApplicationTest` 新增 W5 探针：
  - `allReportMappersResolve`
  - `reportApiPathPrefixWiring_probe`
- 启动门禁恢复为绿色：`ZgbasApplicationTest` 23 tests, 0 failures, 0 errors。

## Verification

| Gate | Command | Result |
|------|---------|--------|
| Startup gate after API migration | `mvn -pl zgbas-admin -am test -Dtest=ZgbasApplicationTest -DfailIfNoTests=false` | 23 tests, 0 failures, 0 errors |
| Report mapper wiring probe | `allReportMappersResolve` | 3 representative MyBatis statements resolve |
| Report path-prefix probe | `reportApiPathPrefixWiring_probe` | `/spt-bas-report/rpt/fundReceivableStatistics/findPage`、`/spt-bas-report/rpt/baseCost/findPage`、`/spt-bas-report/rpt/businessPay/findPageContract` all map to handlers |

## Files Created/Modified
- `zgbas-admin/src/main/java/com/spt/bas/report/server/api/` - 54 个 report api controller 落位。
- `zgbas-admin/src/main/java/com/spt/bas/report/server/api/RptBaseCostApi.java` - 显式命名 report 侧 controller bean，避开与 bas 侧同名 bean 冲突。
- `zgbas-admin/src/main/java/com/spt/bas/report/server/api/RptApplyBusinessPayApi.java` - 将 `@Resource` 名称注入改为按类型注入，避免误命中 bas 侧同名 service bean。
- `zgbas-system/src/main/java/com/spt/bas/report/server/service/impl/RptBaseCostServiceImpl.java` - 去除对 `reportRptBaseCostMapper` 字符串 `@Qualifier` 的依赖，回到按类型注入。
- `zgbas-system/src/main/java/com/spt/bas/report/server/service/impl/RptUserRoiServiceImpl.java` - 去除对 `reportRptBaseCostMapper` 字符串 `@Qualifier` 的依赖，回到按类型注入。
- `zgbas-system/src/main/java/com/spt/bas/report/server/service/impl/RptSummaryRoiServiceImpl.java` - 去除对 `reportRptBaseCostMapper` 字符串 `@Qualifier` 的依赖，回到按类型注入。
- `zgbas-admin/src/test/java/com/spt/ZgbasApplicationTest.java` - 新增并扩展 W5 probe。

## Deviations from Plan

### Auto-fixed Issues

**1. [Rule 1 - Bug] `RptBaseCostApi` controller bean-name collision**
- **Found during:** W5 首次启动门禁
- **Issue:** report 侧 `RptBaseCostApi` 与 bas 侧 `com.spt.bas.server.api.RptBaseCostApi` 共享默认 bean 名 `rptBaseCostApi`，导致 `ConflictingBeanDefinitionException`。
- **Fix:** 在 report 侧 controller 上使用 `@RestController("reportRptBaseCostApi")` 做最小显式命名消歧。
- **Verification:** 后续启动门禁不再报 controller bean-name 冲突。

**2. [Rule 1 - Bug] `RptApplyBusinessPayApi` 名称注入误命中 bas 侧 service bean**
- **Found during:** 第二轮 W5 启动门禁
- **Issue:** `RptApplyBusinessPayApi` 用 `@Resource` 注入 `IRptApplyBusinessPayService applyBusinessPayService`，Spring 按字段名优先解析到 bas 侧同名 bean `applyBusinessPayService`，触发 `BeanNotOfRequiredTypeException`。
- **Fix:** 改为 `@Autowired` 按类型注入，保留字段与接口不变。
- **Verification:** `ZgbasApplicationTest` 恢复到 23/23 全绿。

**3. [Rule 2 - Review hardening] remove string-based mapper qualifier coupling**
- **Found during:** W5 closeout review
- **Issue:** `RptBaseCostServiceImpl`、`RptUserRoiServiceImpl`、`RptSummaryRoiServiceImpl` 通过 `@Qualifier("reportRptBaseCostMapper")` 绑定 mapper，形成不必要的字符串级 Spring 耦合。
- **Fix:** 三处都改回 `@Autowired` 按类型注入；当前容器中 `RptBaseCostMapper` 仅有单一实现/代理，按类型解析无歧义。
- **Verification:** 后续 `ZgbasApplicationTest` 25/0/0/1 与全模块 compile gate 均保持绿色。

## Next Phase Readiness
- report Feign 自回环现在已经有真实 admin-side API 目标，W6 可以直接补 acceptance proof。
- `MyIndexController` / `BusinessOverviewController` 等依赖 report 合同的链路已经具备 report api 目标面。
- 本波无残留启动 blocker。

## Self-Check: PASSED
- 54 个 report api controller 已落位。
- `ZgbasApplicationTest` W5 门禁全绿。
- 两个新暴露的 Spring wiring blocker 都已用最小改动修复。

---
*Phase: 05-report-migration*
*Completed: 2026-07-18*
