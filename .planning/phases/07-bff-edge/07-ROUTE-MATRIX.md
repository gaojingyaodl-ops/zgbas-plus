---
phase: 07-bff-edge
artifact: ROUTE-MATRIX
title: SC#1 三族 effective-path 路由矩阵 + /wx/contract 冲突消歧静态证明
produced_by: plan 07-05 (Task 1 grep 实测 + Task 2 矩阵固化)
acceptance: SC#1(三族 inventory)+ SC#2(无 ambiguous mapping)静态面
runtime_proof: 留 Phase 8(WX-ALIGN-02)
basis: 07-02/07-03/07-04 落地后实测 @RequestMapping(非假设)
date: 2026-07-24
---

# Phase 7 SC#1 路由矩阵 — 三族 effective-path + /wx/contract 消歧

## 0. 一句话结论

✅ **三族 basWx BFF(/wx/* + /ewechat/* + /axq + /purchase/*)与 resident report 侧(/wx/* 经 `/spt-bas-report` 前缀)有效注册路径无任何重叠 → 启动无 `AmbiguousMappingException`。** 唯一字面重复 `/wx/contract`(×2)经 Phase 5 `ReportFeignPathConfig` 前缀隔离为不同有效路径。**D-P7-01-RESOLVED:零路由编辑。**

---

## 1. 前缀机制(冲突消解的根本)

`zgbas-system/.../client/config/ReportFeignPathConfig.java`(Phase 5 落地):

```java
private static final String API_PATH_PREFIX = "/spt-bas-report";
// ...
configurer.addPathPrefix(API_PATH_PREFIX,
    HandlerTypePredicate.forBasePackage("com.spt.bas.report.server.api"));
```

→ 所有 `com.spt.bas.report.server.api` 包下控制器的 `@RequestMapping` 字面路径,运行时叠加 `/spt-bas-report` 前缀。basWx 控制器在 `com.spt.bas.purchase.wx.server.controller` / `.api` 包,**不在** report api 包 → 不叠加前缀,注册裸路径。

---

## 2. 三族 effective-path 归一化表(07-02/03/04 落地后 grep 实测)

### 2.1 basWx BFF controller 族(裸路径,无前缀)— 11 个

| 族 | Controller | 字面 @RequestMapping | 有效基路径 | 方法级 |
|---|---|---|---|---|
| /wx/* | ContractController | `/wx/contract` | `/wx/contract` | 16 @PostMapping(11 读透传 + 5 写) |
| /wx/* | WxUserController | `/wx/user` | `/wx/user` | 登录/会话 |
| /wx/* | UserInfoController | `/wx/userInfo` | `/wx/userInfo` | |
| /wx/* | UserAttentController | `/wx/userattent` | `/wx/userattent` | |
| /wx/* | FileController | `/wx/file` | `/wx/file` | |
| /wx/* | WxTextContentController | `/wx/content` | `/wx/content` | |
| /wx/* | WxStockVirtualController | `/wx/stock/virtual` | `/wx/stock/virtual` | |
| /ewechat/* | BuyEnquiryController | `/ewechat/buyEnquiry` | `/ewechat/buyEnquiry` | |
| /ewechat/* | BuyQuoteController | `/ewechat/buyQuote` | `/ewechat/buyQuote` | |
| /ewechat/* | BuyMessageController | `/ewechat/message` | `/ewechat/message` | |
| /axq | OtherController | `/axq` | `/axq` | + successDebtCertificate/{contractNo} |

### 2.2 basWx BFF api 族(裸路径,无前导斜杠合法)— 4 个 + BaseApi×7

| Api | 字面 @RequestMapping | 有效基路径 | extends | 自有方法 + BaseApi×7 |
|---|---|---|---|---|
| SaveTempApi | `purchase/saveTemp` | `/purchase/saveTemp` | BaseApi\<SaveInfo\> | 6 自有 + saveBatch/delete/findPage/sumPage/save/findAll/getEntity |
| WxOpenApi | `purchase/open` | `/purchase/open` | BaseApi\<CompanyUser\> | saveApplyOnLineData + ×7 |
| WxUserApi | `purchase/user` | `/purchase/user` | BaseApi\<CompanyUser\> | saveApplyOnLineData + ×7 |
| WxUserDetailApi | `purchase/userDetail` | `/purchase/userDetail` | BaseApi\<UserDetail\> | 5 自有 + ×7 |

### 2.3 /error 族

| Controller | 有效路径 | 角色 |
|---|---|---|
| com.spt.bas.server.config.BasicErrorController | `${…:/error}` = `/error` | **active**(唯一注册的 ErrorController) |
| com.spt.bas.purchase.wx.server.config.BasicErrorController | (classpath,不注册) | **excluded**(Plan 07-04,同名 bean 防冲突) |
| com.spt.bas.web.config.BasicErrorController | (classpath,不注册) | excluded(Plan 04-06) |
| com.spt.tools.http.interceptor.BasicErrorController | (classpath,不注册) | excluded(Plan 04-05) |
| ServerErrorController | `/error` (json) | active(json 协商,与 active BasicErrorController text/html/default 经 produces 区分) |

### 2.4 resident report /wx/* 族(字面 /wx/*,**叠加 `/spt-bas-report` 前缀**)

| 类(report.server.api) | 字面 @RequestMapping | **有效注册基路径** |
|---|---|---|
| RptCtrContractApi | `/wx/contract` | **`/spt-bas-report/wx/contract`** |
| RptWxBrandFollowApi | `/wx/wxBrandFollow` | **`/spt-bas-report/wx/wxBrandFollow`** |

> report api 包仅这 2 个映射 /wx/*,均前缀隔离。其余 52 report 端点基路径 /rpt/*/stat/*,与本阶段无关。

---

## 3. /wx/contract 冲突消歧(SC#2 核心)

### 3.1 两端有效路径对照

| 端 | 包 | 字面路径 | **有效注册路径** | bean 注册 |
|---|---|---|---|---|
| ContractController(basWx) | purchase.wx.server.controller | `/wx/contract` | **`/wx/contract`**(裸) | ✓ active(WX 小程序可见入口) |
| RptCtrContractApi(report) | report.server.api | `/wx/contract` | **`/spt-bas-report/wx/contract`**(前缀) | ✓ active(数据 owner + Feign 目标) |

→ **有效路径不同 → 无 AmbiguousMappingException。**

### 3.2 方法集重叠分析(10 字面重叠读方法)

ContractController 16 方法 vs RptCtrContractApi 11 方法,子路径归并后 10 个字面重叠读方法(getCreditContractDetail/getServiceContractDetail/getContractOperationList/getDeliveryOutDetail/getUndeliveryOutDetail/getPayDetail/getServicePayDetail/getBillDetail/getServiceBillDetail/getConfirmReceiptDetail)。**经前缀,全部有效路径隔离,启动不碰撞。**

### 3.3 D-P7-01-RESOLVED 落地结论(零路由编辑)
1. 不删类、不改 WX 可见路由、不改 report 有效路径 —— 全部满足 D-P7-01 底线(超出预期:连「方法级最小 delta」都无需)。
2. ContractController verbatim 落 admin(裸 /wx/contract);RptCtrContractApi 不动(resident);IRptWxCtrContractClient 不动(已内联,Feign path=`spt-bas-report/wx/contract` 自回环自洽)。
3. ContractController 重叠读方法体 = `search.setUserId(UserHelper.getCurUserId()); return ApiResult.ofSuccess(ctrContractClient.getXxx(...));`(注入 WX 用户上下文后透传),行为关键,路由未改。

---

## 4. collision sweep 结论(SC#1 静态证明)

`grep` 全单体 class-level `@RequestMapping(value = "/wx|/ewechat|/axq|/purchase`:

| 字面路径 | 命中数 | owner | 有效路径碰撞? |
|---|---|---|---|
| /wx/contract | 2 | ContractController + RptCtrContractApi | **否**(前缀隔离) |
| /wx/wxBrandFollow | 1 | RptWxBrandFollowApi(report only) | 否(basWx 无此路径) |
| /wx/{content,file,stock/virtual,user,userattent,userInfo} | 各 1 | basWx only | 否 |
| /ewechat/{buyEnquiry,buyQuote,message} | 各 1 | basWx only | 否 |
| /axq | 1 | OtherController | 否 |
| purchase/{open,saveTemp,user,userDetail} | 各 1 | basWx api only | 否 |

✅ **零 /ewechat、零 /axq、零 /purchase resident 映射;唯一 /wx 字面重复(/wx/contract)经前缀隔离。启动注册无 AmbiguousMappingException。**

---

## 5. SC#1/SC#2 静态面验收声明

| SC | 要求 | 静态证明 | 状态 |
|---|---|---|---|
| SC#1 | 三族路由 inventory | §2 归一化表(11 controller + 4 api + /error + resident report /wx/*×2) | ✅ |
| SC#2 | 无 ambiguous mapping | §3 + §4(唯一字面重复 /wx/contract 经前缀隔离) | ✅ 静态 |
| SC#3/4 | 编译门 | 见 07-06-SUMMARY | (07-06) |

**runtime 启动 proof(/wx 非 404 + 自回环 + 启动 GREEN)留 Phase 8(WX-ALIGN-01/02/03)** —— 启动测试非 hermetic(明文密钥态),本阶段以静态矩阵证明为权威(D-P7-02)。

---

## 6. P8 回退预案(若启动仍报 /wx/contract ambiguous —— 理论不应)

触发条件:Phase 8 启动报 `/wx/contract` AmbiguousMappingException(与前缀机制预期相反)。

回退动作(D-P7-01 原预案,本阶段不执行):
1. 对 report 侧 `RptCtrContractApi` 的 10 个重叠读方法做 **sub-path delta**(如改 @RequestMapping 子路径加 `rpt/` 前缀),使字面路径不再与 basWx 重叠。
2. 同步 `IRptWxCtrContractClient`(`zgbas-system/.../report/client/remote/`)的 Feign `path`,保持透传链自回环命中。
3. 安全性:report 侧重叠方法**无 web 消费者**(WX 小程序走 basWx ContractController),仅作 Feign 透传目标 → sub-path delta 不影响 WX 可见路由,行为等价。

> 该回退为防御性预案;静态矩阵 + ReportFeignPathConfig 前缀机制表明启动应直接 GREEN。

---

*矩阵以 07-02/07-03/07-04 落地后 grep 实测为准(D-P7-02 basis)。*
