---
plan: 07-01
phase: 7-alignment-verification
status: complete
started: 2026-07-19
completed: 2026-07-19
---

# Plan 07-01 Summary: Smoke Proof + 全链路端到端可达验证

## Objective
在 ZgbasApplicationTest 中新增 @Disabled smoke proof 方法，证明单服务启动后登录→首页→核心业务→报表→定时任务全链路端到端可达。维持全 reactor 编译 + 测试绿灯基线。

## What Was Done

### Task 07-01-01: 新增 proof 方法
- **`fullChainSmoke_proof()`** — @Disabled("D-P7-01 smoke 全链路 proof — 手动启用验证 ALIGN-01 端到端可达")
  - 断言 GET /login 返回 2xx/3xx
  - 断言 GET /index 返回 2xx/3xx
  - 断言 4 核心 API findPage 端点非 404: /apply/brand/findAll, /ctr/contract/findPage, /stock/stockContract/findPage, /ctr/loading/findPage
  - 断言 2 报表 API 端点非 404: /spt-bas-report/rpt/fundReceivableStatistics/findPage, /spt-bas-report/rpt/baseCost/findPage
  - 断言 quartz 调度: scheduler.getJobKeys() == 53
  - 断言手动触发 job_id=1 → sys_job_log 新行 + status='0'

- **`coreApiReachabilityExtended_probe()`** — @Disabled("D-P7-01 扩展 API 可达性抽样 — 手动启用")
  - 断言 POST /ctr/contractFee/findPageContractFee 非 404
  - 断言 POST /apply/pay/findPage 非 404
  - 断言 POST /spt-bas-report/rpt/businessPay/findPageContract 非 404
  - 断言 POST /spt-bas-report/business/overview/api/findBusinessOverviewList 非 404
  - 断言 BFF controller bean 存在: ctrContractFeeController, applyPayController

### Task 07-01-02: 全 reactor 编译 + 测试绿灯验证
- `mvn clean compile` → BUILD SUCCESS (13s)
- `mvn test` → BUILD SUCCESS (1m17s)
- 结果: 32 tests, 0 failures, 0 errors, 5 skipped
- @Disabled 总数: 5 (3 原有 + 2 新增) ✅

## Key Files Modified
- `zgbas-admin/src/test/java/com/spt/ZgbasApplicationTest.java` — 新增 2 个 @Disabled proof 方法 (127 行)

## Acceptance Criteria Verification
- [x] ZgbasApplicationTest.java 包含 `void fullChainSmoke_proof()` 方法
- [x] ZgbasApplicationTest.java 包含 `void coreApiReachabilityExtended_probe()` 方法
- [x] 两个方法均标注 `@Disabled`
- [x] 全 reactor `mvn test` BUILD SUCCESS (32/0/0/5 skipped)
- [x] `grep -c '@Disabled'` (actual annotations) = 5 (原 3 + 新增 2)
- [x] `grep 'fullChainSmoke_proof'` 有匹配
- [x] `grep 'coreApiReachabilityExtended_probe'` 有匹配

## Deviations
- None. Plan executed as written.

## Self-Check
- PASSED: compile green, test green, @Disabled count correct, new methods present
