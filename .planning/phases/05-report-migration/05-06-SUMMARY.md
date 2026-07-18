---
phase: 05-report-migration
plan: 06
subsystem: report-acceptance
tags: [acceptance-proof, report-http, sample-query, startup-gate, compile-gate, stub-port-upgrade]
requirements: [REPORT-02, PERSIST-02]
requires:
  - "05-05: 54 report APIs present in admin and W5 startup gate green"
provides:
  - "W6 acceptance proofs added to ZgbasApplicationTest: sampleReportQuery_proof + reportHttpReachability_proof"
  - "Phase 5 startup gate expanded to 25 tests with 1 manual-only sample-query skip"
  - "Full-module compile gate green after W6 proof expansion"
affects:
  - "Phase 5 can be marked complete and Phase 6 quartz migration can start from a verified report baseline"
tech-stack:
  added: []
  patterns:
    - "manual-only real-DB proof via @Disabled to avoid non-hermetic failures"
    - "HTTP non-404 reachability proof for report-side self-loopback targets"
    - "exact grep verification before code changes — no-op stub cleanup when real IRpt required=false count is zero"
key-files:
  created: []
  modified:
    - "zgbas-admin/src/test/java/com/spt/ZgbasApplicationTest.java"
decisions:
  - "Do not mass-edit controller/service sources when exact IRpt required=false count is zero; record the verification result instead"
  - "Keep sample query proof disabled by default because it depends on seeded real DB data"
metrics:
  completed: "2026-07-18"
---

# Phase 5 Plan 06: Acceptance Proof Summary

W6 验收证明已经补齐：`ZgbasApplicationTest` 扩展到 25 个测试（1 个手动抽样查询 proof 默认跳过），自动门禁与全模块 compile gate 均已通过，Phase 5 的 report 迁移链路具备收口条件。

## Accomplishments
- 在 `ZgbasApplicationTest` 新增 `sampleReportQuery_proof`：
  - `RptFundReceivableStatisticsMapper.findPage`
  - `RptCtrContractReportMapper.findRptContractPage`
  - `RptBusinessOverviewMapper.findBusinessOverviewList`
  该 proof 默认 `@Disabled`，用于真实 DB 有样本数据时手动激活验证。
- 在 `ZgbasApplicationTest` 新增 `reportHttpReachability_proof`，验证以下路径非 404：
  - `/spt-bas-report/rpt/fundReceivableStatistics/findPage`
  - `/spt-bas-report/business/overview/api/findBusinessOverviewList`
- 先精确核验再决定是否清 stub：`EXACT_REPORT_STUB_COUNT=0`，说明真正落在 `IRpt*Client` 字段上的 `@Autowired(required=false)` 已不存在，因此本波不做无效批量改写。

## Verification

| Gate | Command | Result |
|------|---------|--------|
| Exact report stub scan | python3 exact scan of `@Autowired(required=false)` followed by field type | `EXACT_REPORT_STUB_COUNT=0` |
| Startup gate with W6 proofs | `mvn -pl zgbas-admin -am test -Dtest=ZgbasApplicationTest -DfailIfNoTests=false` | 25 tests, 0 failures, 0 errors, 1 skipped |
| Full compile gate | `mvn -am compile` | BUILD SUCCESS |
| W6 proof presence | grep `sampleReportQuery_proof` / `reportHttpReachability_proof` | both present |

## Notes on Stub Verification
- 计划中的粗 grep 口径会命中“同文件内既有 `required=false` 又用了某个 `IRpt*Client`”的文件，因此得到 `REPORT_STUB_REMAINING=7` 的假阳性结果。
- 用精确口径（`@Autowired(required=false)` 后紧邻字段类型必须为 `IRpt*Client`）复核后，真实结果为 `EXACT_REPORT_STUB_COUNT=0`。
- purchase 侧延迟契约保留 1 个 `required=false` 残留，不在本期修改范围内。

## Files Modified
- `zgbas-admin/src/test/java/com/spt/ZgbasApplicationTest.java`
  - 注入 3 个 representative report mapper
  - 新增 `sampleReportQuery_proof`
  - 新增 `reportHttpReachability_proof`

## Deviations from Plan

### Auto-fixed Issues

**1. [Scope clarification] report stub-port upgrade proved to be a no-op**
- **Found during:** W6 Task 1 exact verification
- **Issue:** 计划假设仍残留 `IRpt*Client` 的 `required=false` 降级点，但精确扫描表明真实残留数为 0。
- **Fix:** 不做无意义源码改写，只记录验证结果，并将 W6 工作重心收敛到 acceptance proof。
- **Verification:** `EXACT_REPORT_STUB_COUNT=0`，且 W6 自动门禁全绿。

**2. [Rule 2 - Review hardening] business overview HTTP proof aligned to real Feign contract path**
- **Found during:** W6 closeout Java review
- **Issue:** `reportHttpReachability_proof` 最初校验了未加前缀的 `/business/overview/api/findBusinessOverviewList`，在 Shiro 302/401 场景下可能形成“非 404 但并未命中 report api 契约路径”的假阳性。
- **Fix:** 改为校验真实 Feign/self-loop 契约路径 `/spt-bas-report/business/overview/api/findBusinessOverviewList`。
- **Verification:** 修正后 `ZgbasApplicationTest` 仍为 25 tests, 0 failures, 0 errors, 1 skipped；full reactor compile 继续 green。

## Next Phase Readiness
- report migration 已具备启动门禁、mapper 解析、HTTP reachability 与手动 sample-query proof 四层验证。
- Phase 5 可视为完成，下一步可以切到 Phase 6 quartz migration。
- 若后续需要真实 DB 实证，只需手动激活 `sampleReportQuery_proof`。

## Self-Check: PASSED
- `sampleReportQuery_proof` 已落位且默认跳过。
- `reportHttpReachability_proof` 自动运行且通过。
- 全模块 compile gate 通过。

---
*Phase: 05-report-migration*
*Completed: 2026-07-18*
