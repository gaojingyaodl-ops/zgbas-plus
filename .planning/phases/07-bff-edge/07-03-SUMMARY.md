---
phase: 07-bff-edge
plan: 03
subsystem: api
tags: [wx-enclave, verbatim-migration, baseapi-crud, jdk8, springboot2]

requires:
  - phase: 06-service
    provides: service iface(ITempSaveInfoService 等)+ dao + wx.client.entity 泛型实参(SaveInfo/CompanyUser/UserDetail)
  - phase: 03-feign
    provides: wx.client.entity 包飞地 + BaseApi/DataEntity in zgbas-common
provides:
  - 4 个 purchase/* CRUD BFF(SaveTempApi/WxOpenApi/WxUserApi/WxUserDetailApi,extends BaseApi<T>)
affects: [07-05, 07-06, phase-08]

tech-stack:
  added: []
  patterns: [BaseApi<T> 继承自动注册 7 CRUD 端点;实控制器判定(D-P7-04,无 @FeignClient)]

key-files:
  created:
    - zgbas-admin/src/main/java/com/spt/bas/purchase/wx/server/api/SaveTempApi.java
    - zgbas-admin/src/main/java/com/spt/bas/purchase/wx/server/api/WxOpenApi.java
    - zgbas-admin/src/main/java/com/spt/bas/purchase/wx/server/api/WxUserApi.java
    - zgbas-admin/src/main/java/com/spt/bas/purchase/wx/server/api/WxUserDetailApi.java
  modified: []

key-decisions:
  - "D-P7-04-RESOLVED 实测确认:4 类 grep @FeignClient 全 0,均 @RestController extends BaseApi<T> → 实控制器落 admin"
  - "verbatim 落地零路由编辑;基路径无前导斜杠(purchase/*)是 BaseApi CRUD 风格,Spring 合法解析"

patterns-established:
  - "api/ 控制器落 admin wx/server/api/ 包飞地,与 controller/ 同级"

requirements-completed: [WX-BFF-02]

duration: 6min
completed: 2026-07-24
---

# Phase 7 Plan 07-03: 4 api 控制器 verbatim 迁入(purchase/* CRUD,extends BaseApi)

**4 个 @RestController extends BaseApi<T> 实控制器(零 @FeignClient)verbatim 落 zgbas-admin wx/server/api/,CRUD 端点由继承自动注册**

## Performance

- **Duration:** ~6 min
- **Tasks:** 1
- **Files created:** 4

## Accomplishments
- WX-BFF-02 交付:purchase/* CRUD BFF 四类就位
- D-P7-04-RESOLVED 实测坐实:4 类均为实控制器(非 Feign 契约),无 @FeignClient 双注册风险

## Task Commits

1. **Task 1: 4 api verbatim 迁入 + BaseApi/泛型实参确认** — `46ca88a` (feat)

## Files Created/Modified
- `SaveTempApi.java` — `purchase/saveTemp`,extends BaseApi<SaveInfo>,6 自有方法(commitTempInfo/getEntrustInfo/getInfoByType/getInfosByCompanyId/getInfoByCompanyId/getInfoByCompanyId2)+ BaseApi×7
- `WxOpenApi.java` — `purchase/open`,extends BaseApi<CompanyUser>,saveApplyOnLineData + BaseApi×7
- `WxUserApi.java` — `purchase/user`,extends BaseApi<CompanyUser>,saveApplyOnLineData + BaseApi×7
- `WxUserDetailApi.java` — `purchase/userDetail`,extends BaseApi<UserDetail>,5 自有方法(findByCompanyIdAndIsBindTrue/findByCompanyIdAndEnableFlgTrue/findByCompanyIdAndIsBindTrueAndEnableFlgTrue/findByUserId/getUserPhone)+ BaseApi×7

## Decisions Made
- **实控制器判定(D-P7-04):** grep 4 文件 @FeignClient 计数全 0;均为 `@RestController extends BaseApi<T>` → 实控制器,落 admin(D-05),无 @FeignClient+@RestController 双注册风险。
- **BaseApi/泛型实参就位:** `BaseApi`/`DataEntity` in zgbas-common(find 命中);`SaveInfo`/`CompanyUser`/`UserDetail` in zgbas-system wx/client/entity(P3)。

## Inventory(import → enclave 就位预检)
| 类 | 关键 import | enclave 状态 |
|---|---|---|
| SaveTempApi | SaveInfoType(wx.client.constant), SaveInfo(wx.client.entity), SaveInfoDao(wx.server.dao), ITempSaveInfoService(wx.server.service) | SaveInfo✓;SaveInfoType/SaveInfoDao/ITempSaveInfoService 待 07-06 编译门确认(P6 迁入范畴) |
| WxOpenApi/WxUserApi | CompanyUser(wx.client.entity), service | CompanyUser✓ |
| WxUserDetailApi | UserDetail(wx.client.entity), UserDetailDao | UserDetail✓ |

> 自有方法注入的 dao/service(ITempSaveInfoService/UserDetailDao 等)是否全就位,以 07-06 编译门为权威;若缺属 P5/P6 漏迁范畴,07-06 回流或记待办。

## Deviations from Plan
None — 按 plan verbatim 执行。注意 WxUserDetailApi 实际含 5 自有方法(RESEARCH §1.2 列 3),verbatim 与源一致,非偏差。

## Issues Encountered
None.

## Next Phase Readiness
- purchase/* 族 BFF 就位,供 07-05 路由矩阵归一化 /purchase/* 族。
- 07-06 编译门将暴露 4 类注入的 dao/service 缺口(若有),按 D-P7-03 回流。
