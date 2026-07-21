---
phase: 05-report-migration
plan: 02
subsystem: report-persistence
tags: [mybatis, mapper, xml, report, low-risk-domains, bulk-copy]
requirements: [REPORT-01, PERSIST-02]
requires:
  - "05-01: reportClient inline + ReportFeignPathConfig + report MyBatis wiring"
provides:
  - "21 low-risk domain report Mapper interfaces and XML files migrated into zgbas-system"
  - "Low-risk report service layer ported in source-real shape, including merged Risk/Stock service forms from upstream"
  - "MyBigDecimalUtils and ReportCalculateUtil inlined for later Wave 3 report calculations"
  - "Wave 1 compile-clean baseline proving report mapper scan, XML discovery, and FQN type resolution all work"
affects:
  - "05-03 medium-risk report domains can build on the verified mapper/xml/service pattern"
  - "05-04 contract-ledger report family can reuse ReportCalculateUtil and the proven XML FQN convention"
tech-stack:
  added: []
  patterns:
    - "D-P2-07 照搬保包名 — report server dao/service/xml verbatim copy"
    - "Wave compile gate discipline — each migration batch validated with zgbas-system -am compile"
    - "XML resultType/parameterType use FQN to avoid alias ambiguity"
key-files:
  created:
    - "zgbas-system/src/main/java/com/spt/bas/report/server/dao/Rpt*Mapper.java (21 files in low-risk domains)"
    - "zgbas-system/src/main/java/com/spt/bas/report/server/service/IRpt*.java"
    - "zgbas-system/src/main/java/com/spt/bas/report/server/service/impl/Rpt*.java"
    - "zgbas-system/src/main/java/com/spt/bas/report/server/util/MyBigDecimalUtils.java"
    - "zgbas-system/src/main/java/com/spt/bas/report/server/util/ReportCalculateUtil.java"
    - "zgbas-system/src/main/resources/mybatis/mappers/Rpt*Mapper.xml (21 files in low-risk domains)"
  modified: []
decisions:
  - "沿用上游源码真实形态而非强行凑齐 21 份独立 service：Risk 合并为 IRptRiskReportService/RptRiskReportServiceImpl，Stock 合同/虚拟域保留上游命名差异"
  - "继续执行 D-P5-04 分批 compile 策略，先低风险域建立 report mybatis 迁移基线"
metrics:
  duration: "6 min"
  completed: "2026-07-17T09:51:07Z"
  tasks: 2
  files_created: 82
  files_modified: 0
---

# Phase 5 Plan 02: Low-risk Report Mapper Batch Summary

21 套低风险域报表 Mapper/XML 已在 zgbas-system 落位，并带上上游真实 service 形态与 2 个计算 util，验证了 report MyBatis 接线、XML 发现和 FQN 类型解析在首批业务域上全部可用。

## Performance

- **Duration:** 6 min
- **Started:** 2026-07-17T17:29:38+08:00
- **Completed:** 2026-07-17T09:51:07Z
- **Tasks:** 2
- **Files modified:** 82

## Accomplishments
- 迁入 Apply/Wx/WeChat/Stock/Company/Supplier/Business/NotBill/NotInvoice/Risk 21 套低风险报表 Mapper 与 XML。
- 迁入对应 report service 层与 `MyBigDecimalUtils`、`ReportCalculateUtil`，为后续 Wave 3 的复杂计算域提前铺好依赖。
- 两批迁移后都复验 `zgbas-system -am compile` 绿灯，确认 `@MapperScan`、`mapper-locations`、reportClient FQN 类型解析协同正常。

## Task Commits

Each task was committed atomically:

1. **Task 1: Apply/Wx/WeChat/Stock/Company/Supplier/Util 域迁移** - `5786f6f` (feat)
2. **Task 2: Business/NotBill/NotInvoice/Risk 域迁移** - `95d9573` (feat)
3. **Plan metadata: SUMMARY.md** - pending in current worktree commit

## Files Created/Modified
- `zgbas-system/src/main/java/com/spt/bas/report/server/dao/` - 21 个低风险域 `@MyBatisDao` Mapper 接口。
- `zgbas-system/src/main/resources/mybatis/mappers/` - 21 个对应 XML，namespace 对齐 dao FQN。
- `zgbas-system/src/main/java/com/spt/bas/report/server/service/` - 上游真实 service interface 形态。
- `zgbas-system/src/main/java/com/spt/bas/report/server/service/impl/` - 上游真实 service impl 形态。
- `zgbas-system/src/main/java/com/spt/bas/report/server/util/MyBigDecimalUtils.java` - BigDecimal 工具。
- `zgbas-system/src/main/java/com/spt/bas/report/server/util/ReportCalculateUtil.java` - 报表计算工具。

## Decisions Made
- 没有为追求“21 套 = 21 份 service iface + 21 份 impl”去偏离上游源码；按源仓库真实组织保留合并接口/实现，更符合行为等价目标。
- 继续要求所有 XML 的 `resultType` / `parameterType` 使用 FQN，避免 report entity/vo 同名 alias 歧义。

## Deviations from Plan

### Auto-fixed Issues

**1. [Source-shape clarification] service 文件数少于 21 并非漏迁**
- **Found during:** 收尾复核
- **Issue:** 计划文本按“每套 4 件”估算，但上游源码实际存在合并 service 形态：Risk 三个 mapper 共用 `IRptRiskReportService`/`RptRiskReportServiceImpl`，Stock 域也有命名不完全一一对应的实现类。
- **Fix:** 保持上游真实文件组织，不为满足表面计数做额外拆分或重命名；在本 SUMMARY 中显式记录该差异。
- **Files modified:** `.planning/phases/05-report-migration/05-02-SUMMARY.md`
- **Verification:** compile gate 通过，dao/xml 全量 21，XML namespace/FQN 校验通过。
- **Committed in:** SUMMARY commit

---

**Total deviations:** 1 auto-documented clarification
**Impact on plan:** 无行为风险；只是把“计划估算数量”与“上游真实文件形态”对齐，避免后续误判为漏迁。

## Issues Encountered
- 无阻塞问题。执行代理超时退出，但实际两个 task commit 已在 worktree 中完成；本次仅补做 SUMMARY 与复核。

## Verification

| Gate | Command | Result |
|------|---------|--------|
| Artifact count | `find .../dao -name 'Rpt*Mapper.java'` / `find .../mappers -name 'Rpt*Mapper.xml'` | DAO_21=21, XML_21=21 |
| Util count | `find .../util -name '*.java'` | UTIL_COUNT=2 |
| FQN type check | XML `resultType` / `parameterType` grep + simple-name scan | BAD_COUNT=0 |
| Compile gate | `mvn -pl zgbas-system -am compile` | BUILD SUCCESS, ERROR_COUNT=0, CANNOT_FIND=0 |

## User Setup Required

None - no external service configuration required.

## Next Phase Readiness
- Wave 1 的 low-risk report 模式已经跑通，05-03 可以直接复用同一 dao/xml/service 迁移套路进入中风险域。
- `ReportCalculateUtil` 已就位，为 05-04 的合同台账复杂报表实现消除了前置依赖。

## Self-Check: PASSED

- 21 个低风险域 Mapper 与 XML 均已就位。
- compile gate 复验通过，无新增 `cannot find symbol`。
- XML 未发现 simple-name report alias。

---
*Phase: 05-report-migration*
*Completed: 2026-07-17*
