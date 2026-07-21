---
phase: 05-report-migration
plan: 03
subsystem: report-persistence
tags: [mybatis, mapper, xml, report, dependency-critical, finance, summary-roi]
requirements: [REPORT-01, PERSIST-02]
requires:
  - "05-01: reportClient inline + ReportFeignPathConfig + report MyBatis wiring"
  - "05-02: 21 low-risk domain report mapper/xml/service baseline"
provides:
  - "13 additional Wave 2 report mappers and XML files migrated into zgbas-system"
  - "BaseCost, UserRoi, and mapperless SummaryRoi services now satisfy Wave 3 contract-ledger compile dependencies"
  - "Wave 1 + Wave 2 cumulative report persistence baseline reaches 34 of 53 mapper/xml suites"
affects:
  - "05-04 contract-ledger and Ctr* report families can compile against IRptSummaryRoiService and IRptUserRoiService"
  - "05-05 report API migration can autowire the newly ported finance, ROI, and regional services"
tech-stack:
  added: []
  patterns:
    - "D-P2-07 照搬保包名 — report server dao/service/xml verbatim copy"
    - "Pitfall 4 sequencing held: SummaryRoi and UserRoi landed before Wave 3 Ctr* services"
    - "Wave compile gate discipline — each migration batch validated with zgbas-system -am compile"
key-files:
  created:
    - "zgbas-system/src/main/java/com/spt/bas/report/server/service/IRptSummaryRoiService.java"
    - "zgbas-system/src/main/java/com/spt/bas/report/server/service/impl/RptSummaryRoiServiceImpl.java"
    - "zgbas-system/src/main/java/com/spt/bas/report/server/dao/RptBaseCostMapper.java"
    - "zgbas-system/src/main/java/com/spt/bas/report/server/dao/RptUserRoiMapper.java"
    - "zgbas-system/src/main/resources/mybatis/mappers/RptBaseCostMapper.xml"
    - "zgbas-system/src/main/resources/mybatis/mappers/RptUserRoiMapper.xml"
  modified: []
decisions:
  - "保持 SummaryRoi 的 mapperless 上游真实形态，只迁 service iface + impl，并复用 RptBaseCostMapper"
  - "按计划先 fast-forward 当前 worktree 到已合并的 05-02 master 基线，再执行 05-03，避免在缺失 Wave 1 资产的错误基线上继续迁移"
metrics:
  duration: "36 min"
  completed: "2026-07-17T11:29:35Z"
  tasks: 2
  files_created: 54
  files_modified: 0
---

# Phase 5 Plan 03: Dependency-Critical and Finance Report Mapper Batch Summary

BaseCost/UserRoi/SummaryRoi 依赖链与 Fund/Invoice/Budget/Profit/Sales/Region/Province 报表套件已迁入 zgbas-system，Wave 3 所需的 `IRptSummaryRoiService` 和 `IRptUserRoiService` 编译前置现已就位，累计 report Mapper/XML 达到 34/53。

## Performance

- **Duration:** 36 min
- **Started:** 2026-07-17T10:52:57Z
- **Completed:** 2026-07-17T11:29:35Z
- **Tasks:** 2
- **Files modified:** 54

## Accomplishments
- 迁入 BaseCost、UserRoi、SummaryRoi、Evaluate、Person 依赖关键域，打通 `SummaryRoi -> BaseCostMapper` 与 `UserRoi -> BaseCostMapper` 编译链。
- 迁入 Fund、Invoice、Budget、Profit、CtrDailySales、Region、Province 8 套 finance/region 域 Mapper、XML、service iface、service impl。
- 连续两次 `zgbas-system -am compile` 绿灯，确认 Wave 2 完成后累计 34 套 report Mapper/XML 均可参与编译。

## Task Commits

Each task was committed atomically:

1. **Task 1: dependency-critical 域 BaseCost/UserRoi/SummaryRoi/Evaluate/Person** - `6ef0dce` (feat)
2. **Task 2: Fund/Invoice/Budget/Profit/Sales/Region/Province 域** - `ed64d24` (feat)
3. **Plan metadata: SUMMARY.md** - pending in current worktree commit

## Files Created/Modified
- `zgbas-system/src/main/java/com/spt/bas/report/server/dao/RptBaseCostMapper.java` - SummaryRoi 与 UserRoi 共用的 BaseCost Mapper。
- `zgbas-system/src/main/java/com/spt/bas/report/server/service/IRptSummaryRoiService.java` - Wave 3 Ctr* 依赖的 mapperless service interface。
- `zgbas-system/src/main/java/com/spt/bas/report/server/service/impl/RptSummaryRoiServiceImpl.java` - 复用 `RptBaseCostMapper` 的 SummaryRoi 计算实现。
- `zgbas-system/src/main/java/com/spt/bas/report/server/service/IRptUserRoiService.java` - Wave 3 合同台账家族依赖的 ROI service interface。
- `zgbas-system/src/main/java/com/spt/bas/report/server/service/impl/RptUserRoiServiceImpl.java` - 用户 ROI 汇总实现。
- `zgbas-system/src/main/java/com/spt/bas/report/server/dao/RptFundReceivableStatisticsMapper.java` - 资金应收报表 Mapper。
- `zgbas-system/src/main/java/com/spt/bas/report/server/dao/RptInvoiceBillMapper.java` - 开票明细报表 Mapper。
- `zgbas-system/src/main/java/com/spt/bas/report/server/dao/RptProfitStatisticsMapper.java` - 毛利统计报表 Mapper。
- `zgbas-system/src/main/resources/mybatis/mappers/Rpt*Mapper.xml` - Wave 2 共 13 个新增 XML，namespace 对齐 dao FQN。

## Decisions Made
- 保持 SummaryRoi 无独立 Mapper/XML 的上游真实形态，不人为补造 `RptSummaryRoiMapper`，避免偏离行为等价目标。
- 发现当前 worktree 基线停留在 05-01，缺少已合并到本地 `master` 的 05-02 资产；先 fast-forward 到 `master` 再执行 05-03，属于继续当前任务所必需的阻塞修复。

## Deviations from Plan

### Auto-fixed Issues

**1. [Rule 3 - Blocking] Fast-forward 当前 worktree 到已合并的 05-02 基线**
- **Found during:** Task 1（开始复制前的目标目录核对）
- **Issue:** 当前 `worktree-agent-a39c509737cdee327` 分支还停留在 05-01 头部，缺少已经合并进本地 `master` 的 05-02 低风险域 Mapper/XML/service 资产；若直接执行 05-03，会让累计计数、依赖前置和 compile gate 都落在错误基线。
- **Fix:** 先对当前 worktree-agent 分支执行 `git merge --ff-only master`，把 05-02 已合并成果带入当前 worktree，再按计划继续 05-03 两个 task。
- **Files modified:** 无 05-03 代码文件直接修改；worktree 分支基线前进到 `c7a1af9`
- **Verification:** fast-forward 后 `DAO_CUMULATIVE` 与 `XML_CUMULATIVE` 在 Task 2 验证中正确达到 34，且两次 compile gate 均为 `BUILD SUCCESS`。
- **Committed in:** N/A（基线同步，不产生 05-03 任务提交）

---

**Total deviations:** 1 auto-fixed (1 blocking)
**Impact on plan:** 该修复只恢复了当前 worktree 应有的上游基线，确保 05-03 按计划语义执行；无额外范围扩张。

## Issues Encountered
- 初次批量复制脚本把 Java 目标路径拼成了 `zgbas-system/src/main/com/...`，导致 Java 文件未落盘而 XML 先落盘；发现后立即改为逐文件精确复制，并在 compile gate 前补齐所有 Java 文件。最终不影响 Task 1 结果。

## Verification

| Gate | Command | Result |
|------|---------|--------|
| Task 1 artifact checks | `test -f ...RptBaseCostMapper.java` / `...RptUserRoiMapper.java` / `...IRptSummaryRoiService.java` / `...RptSummaryRoiServiceImpl.java` | BASECOST_MAPPER=1, USERROI_MAPPER=1, SUMMARYROI_IFACE=1, SUMMARYROI_IMPL=1 |
| Task 1 domain count | `find ...dao -name 'Rpt*Mapper.java' \( BaseCost/UserRoi/Evaluate/Person \)` | DAO_5=5 |
| Task 1 compile gate | `mvn -pl zgbas-system -am compile` | BUILD SUCCESS, ERROR_COUNT=0, CANNOT_FIND=0 |
| Task 2 domain count | `find ...dao -name 'Rpt*Mapper.java' \( Fund/Invoice/Budget/Profit/CtrDailySales/Region/Province \)` | DAO_8=8 |
| Wave 1+2 cumulative count | `find .../dao -name 'Rpt*Mapper.java'` / `find .../mappers -name 'Rpt*Mapper.xml'` | DAO_CUMULATIVE=34, XML_CUMULATIVE=34 |
| Final compile gate | `mvn -pl zgbas-system -am compile` | BUILD SUCCESS, ERROR_COUNT=0, CANNOT_FIND=0 |

## User Setup Required

None - no external service configuration required.

## Next Phase Readiness
- Wave 3 可直接开始迁移 Ctr* 合同台账家族，`IRptSummaryRoiService` 与 `IRptUserRoiService` 已先行就位，满足计划中的 Pitfall 4 测序铁律。
- finance、ROI、region 相关 service 已齐备，05-05 报表 API 迁移时可直接进行 `@Autowired` 接线。

## Self-Check: PASSED

- `zgbas-system/src/main/java/com/spt/bas/report/server/service/IRptSummaryRoiService.java` exists.
- `zgbas-system/src/main/java/com/spt/bas/report/server/service/impl/RptSummaryRoiServiceImpl.java` exists.
- `zgbas-system/src/main/java/com/spt/bas/report/server/dao/RptBaseCostMapper.java` exists.
- `zgbas-system/src/main/java/com/spt/bas/report/server/dao/RptUserRoiMapper.java` exists.
- Task commit `6ef0dce` exists in git log.
- Task commit `ed64d24` exists in git log.

---
*Phase: 05-report-migration*
*Completed: 2026-07-17*
