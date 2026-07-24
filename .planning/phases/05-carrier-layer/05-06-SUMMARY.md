---
phase: 5
plan: 05-06
subsystem: carrier-layer (编译门 — SC#3 权威验证)
tags: [verification, compile-gate, sc3]
requires: [05-01, 05-02, 05-03, 05-04, 05-05]
provides:
  - 承托层独立编译零 [ERROR] 权威证据(SC#3)
  - D-P5-14 六 stub 替换确认(SC#1 子项)
  - 孤立 import 清零 + 显式排除类确认(SC#1)
affects:
  - Phase 6(service 迁入的编译底座;承托层零错是其前提)
tech-stack: {}
key-files: {}
key-decisions:
  - "SC#3 权威门 = mvn compile -pl zgbas-system -am 零 [ERROR] + BUILD SUCCESS"
  - "Wave 期残留(MessageEnums→UserHelper[B1→W2]、BsDictUtil→IBsDictService[W4 iface 迁])均已逐波清零,终态零错"
requirements-completed: [WX-BFF-03]
requirements-addressed: [WX-BFF-03]
duration: "~5 min"
completed: 2026-07-24
---

# Phase 5 Plan 06: 编译门 — 承托层零 [ERROR](SC#3) Summary

承托层全量迁入(05-01..05-05)后的权威集成编译门:zgbas-system 零 `[ERROR]` + BUILD SUCCESS,验证 SC#1(逐类可独立解析,无孤立 import)+ SC#3(承托层独立编译通过)。stub 替换 diff + 孤立 import 扫描兜底通过。

- **Duration:** ~5 min · **Tasks:** 2(纯验证,无文件产出除 SUMMARY)
- **Gates:** SC#3 ✅ BUILD SUCCESS / 0 ERROR;SC#1 ✅ 六 stub 非 stub + 无孤立 import

## Tasks Executed

### Task 1 — stub diff + 孤立 import 扫描 + 排除类确认
- **D-P5-14 六 stub 全部非 stub**:
  | stub | vs 源 | 结论 |
  |---|---|---|
  | common/BaseException | IDENTICAL | ✅ 非 stub |
  | exception/SecurityException | IDENTICAL | ✅ |
  | util/ResponseUtil | IDENTICAL | ✅ |
  | util/UserContext | IDENTICAL | ✅ |
  | common/ApiResult | DIFFERS(swagger 注释唯一差异,non-swagger-diff=0) | ✅ 非 stub |
  | vo/UserInfoVo | DIFFERS(swagger 注释唯一差异,non-swagger-diff=0) | ✅ 非 stub |
- **显式排除类正确缺席**:EweChatApi / JinXinApi / PurchaseCommand 均不在 Phase 5 enclave(→ Phase 6)。
- **孤立 import 扫描**:enclave 内仅一处 `service.impl.*` import —— `JwtUtil(P4) → WxAccessTokenService`,该类已于早期(P3/P4)迁入 `.../wx/server/service/impl/WxAccessTokenService.java` 并被 JwtUtil 字段注入,resolve 正常,**非真实悬空**。承托类引用闭环。

### Task 2 — 编译门(SC#3 权威)
```
JAVA_HOME=/Users/alan/Library/Java/JavaVirtualMachines/corretto-1.8.0_482/Contents/Home \
  mvn compile -pl zgbas-system -am -s zg_settings.xml
→ zgbas-system ........ SUCCESS [7.832 s]
→ BUILD SUCCESS
→ ERROR_COUNT = 0
```

## ERROR 清零过程(逐波)

承托层迁移期残留 ERROR 均在产生当波清零,无遗留到本门:

| 波 | 残留 ERROR | 处置 | 清零波 |
|---|---|---|---|
| 05-01 | MessageEnums → util.UserHelper(Wave 2 类未迁) | 前向引用,plan 预期 | 05-02(UserHelper 迁入后自动解) |
| 05-01 | WarehouseRequest → client.vo.WarehouseVo(P3 漏迁) | 迁入 WarehouseVo(Rule 2 deviation) | 05-01(同波) |
| 05-04 | BsDictUtil → service.IBsDictService(P6 接口) | 迁入接口(Rule 2 deviation,impl→P6) | 05-04(同波) |

终态:**0 残留,本门 0 ERROR**。每波 reactor 编译 GREEN,本门为权威确认。

## Acceptance Criteria Results

| Criterion | Result |
|---|---|
| `mvn compile -pl zgbas-system` 零 `[ERROR]` + BUILD SUCCESS(SC#3) | ✅ ERROR_COUNT=0, BUILD SUCCESS |
| 六 stub 与源一致(非 stub) | ✅ 4 IDENTICAL + 2 swagger-only(全非 stub) |
| 无非排除清单孤立 import | ✅(JwtUtil→WxAccessTokenService 已迁,非悬空) |
| EweChatApi/JinXinApi/PurchaseCommand 正确排除 | ✅ 全 absent |
| SUMMARY 记录 ERROR 清零过程 | ✅(上表) |

## Deviations from Plan

None —— 验证 plan,无代码改动。

## Verification

- SC#3 权威门:**BUILD SUCCESS,0 [ERROR]**(locale 无关 grep `^\[ERROR\]` = 0)。
- SC#1:六 stub 非 stub + 孤立 import 清零 + 排除类缺席。

## Self-Check: PASSED

承托层独立编译零错(SC#3);六 stub 替换确认(SC#1);孤立 import 清零;排除类正确缺席。Phase 6 service 迁入的编译底座就绪。

## Phase 5 Conclusion

承托层 6 plan 全部完成,SC#3 编译门 GREEN。承托层(payload/vo/enums/common/util/exception/aop/config/cache/listener)就位,4 P4 stub 形态消除,横切 bean 安全落位,启动接线 + inventory 交付。**Phase 5 ready for verification.**
