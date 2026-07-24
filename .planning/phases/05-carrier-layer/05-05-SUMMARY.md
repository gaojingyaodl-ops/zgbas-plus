---
phase: 5
plan: 05-05
subsystem: carrier-layer (灰区 C — 启动接线 + inventory)
tags: [migration, wx, startup-wiring, listener, inventory, gray-area]
requires: [05-03, 05-04]
provides:
  - 启动期 WX 字典缓存初始化(ApplicationStartup 并入 WX BsDictUtil.init)
  - 请求期 UserContext ThreadLocal 清理(RequestListener)
  - D-15a/b 五类承托 inventory checklist(SC#4)
affects:
  - Phase 8(启动 GREEN:WX /wx/* 字典缓存就位,登录缺口同源修复)
tech-stack:
  added: []
  patterns: [startup-wiring-merge, threadlocal-cleanup, fully-qualified-disambiguation]
key-files:
  created:
    - "zgbas-system/.../wx/server/listener/RequestListener.java"
    - ".planning/phases/05-carrier-layer/05-INVENTORY-CHECKLIST.md"
  modified:
    - "zgbas-system/.../bas/server/listener/ApplicationStartup.java (+WX BsDictUtil.init, D-P5-09)"
key-decisions:
  - "D-P5-09:ApplicationStartup 并入 WX BsDictUtil.init(全限定消歧),不 verbatim 迁 WX ApplicationStartup(防 2nd ApplicationReadyEvent 监听器双跑)"
  - "D-P5-10:RequestListener verbatim 迁,requestDestroyed→UserContext.removeUser 清理 ThreadLocal"
  - "ApplicationStartup 合并是 Phase 3 登录缺口(memory authsdk-static-cache-init-gap)同源修复"
requirements-completed: []
requirements-addressed: [WX-BFF-03]
duration: "~12 min"
completed: 2026-07-24
---

# Phase 5 Plan 05: 启动接线 + D-15a/b inventory checklist(灰区 C) Summary

灰区 C 收尾:启动接线(ApplicationStartup 并入 WX 字典缓存初始化[Phase 3 登录缺口同源修复]+ RequestListener ThreadLocal 清理)+ D-15a/b 五类承托 inventory 交付(SC#4)。

- **Duration:** ~12 min · **Tasks:** 3 · **Files:** 2 新 + 1 改
- **Commits:** cf10fc6(T1 ApplicationStartup)/ 7b9b297(T2 RequestListener)/ 05aa01e(T3 inventory)

## Tasks Executed

### Task 1 — ApplicationStartup 并入 WX BsDictUtil.init(D-P5-09,非 verbatim)
- 在单体 `onApplicationEvent` init 序列(WarehouseCache.init 后、new Thread(executor) 前)追加一行:`com.spt.bas.purchase.wx.server.cache.BsDictUtil.init();`(全限定,避免与既有 `com.spt.bas.server.cache.BsDictUtil` import 同名冲突)。
- **不**重复:`DictUtil.init(appCode)` 实测 1 次、`new Thread(executor).start()` 实测 1 次。
- **不** verbatim 迁 WX ApplicationStartup(单体 ApplicationReadyEvent 监听器仍只 1 个,无双跑竞态)。
- **同源修复**:WX `/wx/*` 字典缓存未初始化是 Phase 3 登录缺口(memory `authsdk-static-cache-init-gap`)根因之一,本 plan 直击。

### Task 2 — RequestListener verbatim 迁入(D-P5-10)
- `@Component ServletRequestListener`,verbatim。`requestDestroyed → UserContext.removeUser()` 清理 ThreadLocal,防内存泄漏 + 身份串线程。
- 依赖 UserContext(05-02 已迁)。单体零既有 ServletRequestListener,无重名。

### Task 3 — D-15a/b 五类 inventory checklist(SC#4)
- 五类(return envelope / exception advice / user-context / auth helper / serialization-upload-wrapper)逐项 source→consumer→must-port→落点 plan→status 盘点。
- 全部 must-port=Y 且已落位(05-01..05-05);附 deferred/out-of-scope 清单(EweChatApi/JinXinApi/PurchaseCommand→P6;FrameworkConfig dropped;TransactionConfig/WxJobConfig skipped)。

## Acceptance Criteria Results

| Criterion | Result |
|---|---|
| ApplicationStartup 含全限定 `purchase.wx.server.cache.BsDictUtil.init()` | ✅ line 62 |
| `DictUtil.init` 实测调用 1 次(无重复) | ✅ 1(line 44) |
| `new Thread(executor)` 实测调用 1 次 | ✅ 1(line 63) |
| 单体 ApplicationReadyEvent 监听器仅 1(WX ApplicationStartup 未 verbatim 迁) | ✅ 1 |
| RequestListener @Component + ServletRequestListener | ✅ |
| requestDestroyed 调 UserContext.removeUser | ✅ line 30 |
| INVENTORY-CHECKLIST 含全部 5 类 | ✅ count=11 |
| 五类结论与 05-01..05-04 实际一致 | ✅ |

## Deviations from Plan

None —— 3 个 task 全部按 plan + 锁定决策(D-P5-09/D-P5-10)执行。无新增 deviation。

## Verification

- reactor 编译:**EXIT=0,0 ERROR** —— GREEN。
- 启动 GREEN 留 Phase 8(本 plan 只做接线 + 静态断言)。

## Self-Check: PASSED

所有 acceptance criteria 通过;启动接线完成(WX 字典 init 并入,无双跑);RequestListener ThreadLocal 清理就位;SC#4 五类 inventory 交付;reactor GREEN。

## Next

Ready for **05-06**(承托层编译门:集成编译验证 + 兜底静态扫描,SC#3 权威门)。承托层 05-01..05-05 全部就位,本 plan 零残留编译错误预期。
