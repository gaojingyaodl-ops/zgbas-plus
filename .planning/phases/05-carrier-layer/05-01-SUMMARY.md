---
phase: 5
plan: 05-01
subsystem: carrier-layer (承托底座 I)
tags: [migration, wx, pojo, verbatim-port]
requires: []
provides:
  - WX carrier leaf POJO 底座 (payload/vo/enums/common) 就位于 zgbas-system 包飞地
  - 源实测 ApiResult / BaseException / UserInfoVo / Status(消除 P4 stub 形态)
affects:
  - 05-02 util(UserHelper/UserContext/ResponseUtil 引用本批 POJO)
  - 05-03 横切 bean / exception
  - 05-04 cache(BsDictUtil/RedisCache 返回类型)
  - 05-06 承托层编译门
tech-stack:
  added: []
  patterns: [verbatim-port, package-enclave, stub-overlay]
key-files:
  created:
    - "zgbas-system/.../purchase/wx/server/payload/ (22 Request POJO)"
    - "zgbas-system/.../purchase/wx/server/vo/ (18 VO,UserInfoVo 覆盖 stub)"
    - "zgbas-system/.../purchase/wx/server/enums/MessageEnums.java"
    - "zgbas-system/.../purchase/wx/server/common/ (8,BasConstants/CardType/Constant/CustomSetting/InfoStep 新增)"
    - "zgbas-system/.../purchase/wx/client/vo/WarehouseVo.java (deviation: P3 inventory miss)"
  modified:
    - "zgbas-system/.../purchase/wx/server/vo/UserInfoVo.java (stub→source)"
    - "zgbas-system/.../purchase/wx/server/common/ApiResult.java (stub→source,swagger 注释)"
    - "zgbas-system/.../purchase/wx/server/common/BaseException.java (stub→source)"
    - "zgbas-system/.../purchase/wx/server/common/Status.java (去除残留 stub 注释,与源一致)"
key-decisions:
  - "D-P5-14: ApiResult/BaseException/UserInfoVo 以源实测实现替换 P4 stub"
  - "RESEARCH Q2: io.swagger 注释(springfox 不在单体 classpath),纯元数据,字段/行为不变"
  - "Status 处置:enclave 已是源实测实现(仅残留 'Phase 4 stub' 注释),用源覆盖以消除误导注释"
requirements-completed: []
requirements-addressed: [WX-BFF-03]
duration: "~15 min"
completed: 2026-07-24
---

# Phase 5 Plan 01: 承托底座 I — payload + vo + enums + common(含 stub 替换) Summary

承托层第一波:把 basWx 49 个叶节点承托类(payload 22 / vo 18 / enums 1 / common 8)verbatim 落入 zgbas-system `com.spt.bas.purchase.wx.server.*` 包飞地,并以源实测实现替换 4 个 Phase-4 预置形态(ApiResult / BaseException / UserInfoVo stub + Status 残留注释),为 util / 横切 bean / cache 提供自洽的 POJO 与常量底座。

- **Duration:** ~15 min · **Tasks:** 2 · **Files:** 50(49 承托 + 1 deviation WarehouseVo)
- **Start:** 2026-07-24(baseline GREEN) · **End:** 2026-07-24
- **Commits:** `daee202`(Task 1 payload/vo/enums) · `d2efa0e`(Task 2 common)

## Tasks Executed

### Task 1 — verbatim 迁入 payload(22)+ enums(1)+ vo(18,UserInfoVo 覆盖 stub)
- 源 `basWx/purchase-server/.../wx/server/{payload,vo,enums}/` → enclave 同包名 verbatim 复制。
- UserInfoVo 覆盖 P4 stub,与源实测一致(10 个真实字段,diff 仅 swagger 注释差异)。
- 41 类就位:payload=22 / vo=18 / enums=1。

### Task 2 — 迁入 common(8)+ 替换 ApiResult/BaseException stub + Status 处置
- ApiResult / BaseException 以源实测覆盖 P4 stub(D-P5-14)。
- Status:enclave 本就是源实测实现(仅残留 `// Phase 4 stub` 注释头),用源覆盖以消除误导注释,现与源字节一致。
- 8 类就位:ApiResult / BasConstants / BaseException / CardType / Constant / CustomSetting / InfoStep / Status。

## Acceptance Criteria Results

| Criterion | Result |
|---|---|
| payload/ .java = 22 | ✅ 22 |
| vo/ .java ≥ 18(UserInfoVo 非 stub) | ✅ 18,无 stub marker,10 真实字段 |
| enums/ 含 MessageEnums | ✅ |
| 所有文件 `package com.spt.bas.purchase.wx.server.{payload\|vo\|enums\|common}` | ✅ ALL MATCH |
| UserInfoVo / ApiResult / BaseException 非 stub | ✅ 无 stub marker,diff 仅 swagger 注释 |
| 叶节点类合计 ≥ 49 | ✅ 49(+1 deviation) |

## Deviations from Plan

**[Rule 2 — 缺失关键类] 迁入 `client.vo.WarehouseVo`**
- Found during: Task 1 编译验证。
- Issue: `WarehouseRequest`(本 plan)引用 `com.spt.bas.purchase.wx.client.vo.WarehouseVo`,该类在 Phase 3 Feign-client 迁入时漏迁(monolith `wx.client.vo` 仅有 5 个 VO,无 WarehouseVo);legacy 位于 `basWx/purchase-client/.../client/vo/WarehouseVo.java`。
- Fix: verbatim 迁入 WarehouseVo(lombok 叶 DTO,68 行,无 swagger)到 monolith `wx.client.vo/`,与 Phase 3 其余 5 个 client.vo 一致。
- Files modified: `zgbas-system/.../purchase/wx/client/vo/WarehouseVo.java`
- Verification: `diff -q` 与源一致;reactor 编译该引用已解。
- Commit: `daee202`。

**[Rule 1 — 编译阻断] io.swagger 注释处理**
- Issue: 源 11 个文件(payload×7 / vo×3 / common ApiResult)含 active `io.swagger.annotations` import + `@ApiModel/@ApiModelProperty`,而 springfox/swagger 不在单体 classpath(RESEARCH Q2),verbatim 复制会编译失败。
- Fix: 按 plan/RESEARCH 约定 + P4 stub 惯例,注释 swagger import 与注解行(纯元数据,字段与 POJO 形态不变,运行期行为等价)。已在 Task 1/2 内处置。
- Total deviations: 2 auto-fixed(1 missing-critical,1 compile-blocker)。**Impact:** 无行为影响;1 个已知 interim 编译错误(Wave 2 解)。

## Verification

- 叶节点文件数:`ls .../{payload,vo,enums,common}/*.java | wc -l` = **49** ✅(plan 期望 ≥49)
- 残留编译错误:`MessageEnums → util.UserHelper`(Wave 2/05-02 前向引用)—— 符合 plan verification 注记(承托层迁移期跨 plan 引用可残留,05-06 编译门为权威)。
- 3 stub 替换 + Status 处置:全部非 stub,源实测一致。

## Self-Check: PASSED

所有 acceptance criteria 通过;2 个 deviation 已 auto-fix 并记录;唯一残留编译错误为 plan 预期的 Wave 2 前向引用。

## Next

Ready for **05-02**(util 类迁入:ResponseUtil/UserContext stub 替换 + HttpUtils 死码核实)—— 将解除 MessageEnums→UserHelper 残留引用。
