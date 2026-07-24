---
phase: 07-bff-edge
plan: 02
subsystem: api
tags: [wx-enclave, bff-controller, verbatim-migration, route-conflict-resolved, jdk8, springboot2]

requires:
  - phase: 07-01
    provides: StockVirtualWxVo(WxStockVirtualController 返回类型)+ FeignHttpsConfig
  - phase: 06-service
    provides: 19 service iface + 20 impl + wx.server 承托(vo/payload/util/common/cache/config)
  - phase: 05-report
    provides: report.client.entity/remote/vo + ReportFeignPathConfig(/spt-bas-report 前缀)
provides:
  - 11 basWx BFF controller(7 /wx/* + 3 /ewechat/* + 1 /axq)+ BaseController 基类,落 admin controller/
affects: [07-05, 07-06, phase-08]

tech-stack:
  added: []
  patterns: [BFF controller 路由 verbatim 零编辑(D-P7-01-RESOLVED 前缀隔离消歧);BaseController 按 extends 判定按需迁入]

key-files:
  created:
    - zgbas-admin/src/main/java/com/spt/bas/purchase/wx/server/controller/ContractController.java
    - zgbas-admin/src/main/java/com/spt/bas/purchase/wx/server/controller/WxUserController.java
    - zgbas-admin/src/main/java/com/spt/bas/purchase/wx/server/controller/UserInfoController.java
    - zgbas-admin/src/main/java/com/spt/bas/purchase/wx/server/controller/UserAttentController.java
    - zgbas-admin/src/main/java/com/spt/bas/purchase/wx/server/controller/FileController.java
    - zgbas-admin/src/main/java/com/spt/bas/purchase/wx/server/controller/WxTextContentController.java
    - zgbas-admin/src/main/java/com/spt/bas/purchase/wx/server/controller/WxStockVirtualController.java
    - zgbas-admin/src/main/java/com/spt/bas/purchase/wx/server/controller/BuyEnquiryController.java
    - zgbas-admin/src/main/java/com/spt/bas/purchase/wx/server/controller/BuyQuoteController.java
    - zgbas-admin/src/main/java/com/spt/bas/purchase/wx/server/controller/BuyMessageController.java
    - zgbas-admin/src/main/java/com/spt/bas/purchase/wx/server/controller/OtherController.java
    - zgbas-admin/src/main/java/com/spt/bas/purchase/wx/server/controller/BaseController.java
  modified: []

key-decisions:
  - "D-P7-01-RESOLVED 实测坐实:ContractController @RequestMapping(\"/wx/contract\") + 16 @PostMapping verbatim 零路由编辑;report 侧 RptCtrContractApi 经 /spt-bas-report 前缀有效路径隔离,无 ambiguous mapping"
  - "G-03 判定:UserAttentController + UserInfoController extends BaseController → BaseController(纯基类,无 @RestController/@RequestMapping)一并 verbatim 迁入;其余 9 controller 不 extends"
  - "Task2 import 盘点:wx.server*/report.client*/wx.client* 共 44 条直接 import 全部 enclave 就位,无新承托缺口;权威编译留 07-06"

patterns-established:
  - "BFF controller 落 admin wx/server/controller/ 包飞地;路由 verbatim 零编辑为默认"

requirements-completed: [WX-BFF-01]

duration: 10min
completed: 2026-07-24
---

# Phase 7 Plan 07-02: 11 BFF controller verbatim 迁入(/wx/* + /ewechat/* + /axq)

**11 BFF controller + BaseController 路由 verbatim 零编辑落 admin 包飞地(D-P7-01-RESOLVED);Task2 import 盘点 wx/report 域 44 条全就位,无新承托缺口**

## Performance

- **Duration:** ~10 min
- **Tasks:** 2
- **Files created:** 12

## Accomplishments
- WX-BFF-01 核心交付:三族 BFF 全量就位
- D-P7-01-RESOLVED 实测坐实:零路由编辑即可(ContractController 16 方法 + resident RptCtrContractApi 经前缀隔离)
- Task2 静态 import 盘点零承托缺口(wx/report 域)

## Task Commits

1. **Task 1: 11 controller + BaseController verbatim 迁入** — `e366d65` (feat)
2. **Task 2: 承托缺口回流捕获(import 盘点)** — 无文件产出(盘点结论见下,权威编译留 07-06)

## Files Created/Modified
- 11 controller(见 frontmatter key-files.created)
- `BaseController.java` — 纯基类(UserAttentController/UserInfoController extends),无路由
- 全部 12 文件 diff 源 vs enclave 零差异

## Decisions Made

### G-03 BaseController 判定
- grep 11 controller:`UserAttentController` + `UserInfoController` extends `BaseController` → 按需一并 verbatim 迁入。
- BaseController 形状:`public class BaseController {}`,无 `@RestController`/`@RequestMapping`,纯辅助基类,**不计路由**。

### D-P7-01-RESOLVED 路由零编辑(实测)
- ContractController 落地后 grep:`@RequestMapping("/wx/contract")` ×1 + `@PostMapping` ×16,与源逐字一致。
- resident `RptCtrContractApi`(report.server.api)经 `ReportFeignPathConfig` `/spt-bas-report` 前缀,有效路径 `/spt-bas-report/wx/contract/*` ≠ 裸 `/wx/contract/*` → 无 ambiguous mapping。
- 10 个字面重叠读方法经前缀隔离,启动不碰撞(runtime proof 留 P8)。

## Task 2: import 盘点(承托缺口回流捕获)
44 条 wx.server*/report.client*/wx.client* 直接 import 逐项 enclave 存在性检查 → **全部 OK,无 MISS**。抽样:
- service:IContractService/IBuyEnquiryService/IBuyQuoteService/IBuyMessageService/IUserService/IUserInfoService/ISuccessContractService/IWxUserTextReadService ✓(P6)
- report.remote:IRptWxCtrContractClient/IRptWxBrandFollowClient/IRptStockVirtualReportClient ✓(P5 内联)
- report.entity/vo:RptCtrContractSearch/RptWxBrandFollow/RptWxBrandUpdate/RptWxStockVirtualSearchVo ✓
- wx.server 承托:ApiResult/BaseException/InfoStep/Status/UserHelper/JwtUtil/OcrUtils/UploadHelper/BsDictUtil/WxConfiguration/CompanyUserDao/WxUserTextRead + vo/payload 族 ✓(P5/P6)
- 07-01 产出:StockVirtualWxVo ✓(WxStockVirtualController)

> wildcard import(`payload.*`/`report.client.entity.*`)包目录存在;包内具体类型以 07-06 编译门为权威。

**结论:无新承托缺口,07-01 两类已覆盖 Wave2 controller 直接编译依赖。**(spt-tools*/Spring/CFCA 等既有依赖留 07-06 编译门确认。)

## Deviations from Plan
None — 按 plan verbatim 执行。Task2 盘点结果(无缺口)符合 plan 预期。

## Issues Encountered
None.

## Next Phase Readiness
- 三族 BFF controller 全量就位 → 07-05 路由矩阵归一化 + 07-06 编译门对象就位。
- ContractController 透传链(IRptWxCtrContractClient → /spt-bas-report/wx/contract)行为不动,runtime proof 留 P8。
