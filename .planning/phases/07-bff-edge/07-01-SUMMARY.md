---
phase: 07-bff-edge
plan: 01
subsystem: infra
tags: [feign, wx-enclave, verbatim-migration, jdk8, springboot2]

requires:
  - phase: 06-service
    provides: 已迁 service 层 + wx.client.entity 包飞地(StockVirtualWxVo/FeignHttpsConfig 同侧落点参照)
provides:
  - StockVirtualWxVo(zgbas-system wx/client/vo/,WxStockVirtualController 返回类型依赖)
  - FeignHttpsConfig(zgbas-system wx/client/config/,WX Feign HTTPS wiring 占位类)
affects: [07-02, 07-06, phase-08]

tech-stack:
  added: []
  patterns: [D-P7-03 承托缺口 inline verbatim 迁入(保包名,不 stub/不回流 todo)]

key-files:
  created:
    - zgbas-system/src/main/java/com/spt/bas/purchase/wx/client/vo/StockVirtualWxVo.java
    - zgbas-system/src/main/java/com/spt/bas/purchase/wx/client/config/FeignHttpsConfig.java
  modified: []

key-decisions:
  - "StockVirtualWxVo verbatim 落地:extends com.spt.tools.core.bean.PageSearchVo(已在 zgbas-common),无新承托缺口"
  - "FeignHttpsConfig @Bean 查重结论 = N/A:源文件整体为块注释(/* */),零活动 @Bean/@Configuration,Feign Client TLS trust-all 逻辑被源侧禁用;与单体既有 FeignConfig/ReportFeignPathConfig/BasFeignPathConfig/PurchaseWxClientConfig 无重复注册"
  - "D-P7-03 遵守:不 stub、不回流 todo;两类均为 Wave2 controller 直接编译依赖"

patterns-established:
  - "承托缺口 inline 迁入:purchase-client 侧类 → zgbas-system wx/client/* 同侧飞地,包名逐字保留"

requirements-completed: [WX-BFF-01, WX-BFF-02]

duration: 8min
completed: 2026-07-24
---

# Phase 7 Plan 07-01: 承托缺口 inline 迁入 — StockVirtualWxVo + FeignHttpsConfig

**Wave2 11 controller 的两个 purchase-client 侧承托缺口 verbatim 落 zgbas-system 包飞地,@Bean 查重 N/A(FeignHttpsConfig 源侧整体注释)**

## Performance

- **Duration:** ~8 min
- **Tasks:** 2
- **Files created:** 2

## Accomplishments
- StockVirtualWxVo 就位 zgbas-system wx/client/vo/,解锁 WxStockVirtualController 编译
- FeignHttpsConfig 就位 zgbas-system wx/client/config/,@Bean 重复注册风险经查证为零(源整体注释)

## Task Commits

1. **Task 1: StockVirtualWxVo verbatim 迁入** — `88a3807` (feat)
2. **Task 2: FeignHttpsConfig verbatim 迁入 + @Bean 查重** — `8160096` (feat)

## Files Created/Modified
- `zgbas-system/.../wx/client/vo/StockVirtualWxVo.java` — 虚拟库存 WX VO(extends PageSearchVo),WxStockVirtualController 返回类型
- `zgbas-system/.../wx/client/config/FeignHttpsConfig.java` — WX Feign HTTPS wiring 占位类(源侧整体块注释,零活动 @Bean)

## Decisions Made

### @Bean 查重结论(FeignHttpsConfig)
- **结论:N/A,verbatim 落地零适配。**
- 证据:`perl` 剥离块注释后,文件无任何活动 `@Bean`/`@Configuration`/`class FeignHttpsConfig` —— Feign Client(LoadBalancerFeignClient + TLSv1.2 trust-all)逻辑被源侧用 `/* … */` 整体禁用。
- 单体既有 Feign 配置(`ReportFeignPathConfig`/`BasFeignPathConfig`/`PurchaseWxClientConfig`/`FeignConfig`)均无 `feign.Client`/`LoadBalancerFeignClient`/`RequestInterceptor` @Bean 注册(grep 零命中),即便该类被激活也无重复。
- 故无需 `@ConditionalOnMissingBean`/重命名,T-07-01b 自然消解。

### StockVirtualWxVo 依赖确认
- `extends com.spt.tools.core.bean.PageSearchVo` → `zgbas-common/src/main/java/com/spt/tools/core/bean/PageSearchVo.java` 已就位(find 命中),无级联缺口。

## Inventory(承托缺口回流)
| 缺口类 | 源 | enclave 落点 | 消费方 | diff |
|---|---|---|---|---|
| StockVirtualWxVo | basWx/purchase-client/.../wx/client/vo/ | zgbas-system/.../wx/client/vo/ | WxStockVirtualController(07-02) | 零差异 |
| FeignHttpsConfig | basWx/purchase-client/.../wx/client/config/ | zgbas-system/.../wx/client/config/ | WX Feign HTTPS wiring | 零差异 |

(无额外回流缺口;Wave2 编译期若再暴露,沿用 D-P7-03 在 07-02/07-06 处理。)

## Deviations from Plan
None — 按 plan verbatim 执行。@Bean 查重结果(N/A)与 plan 预期分支一致(无重复 → verbatim 落地)。

## Issues Encountered
None.

## Next Phase Readiness
- 07-02(Wave2 11 controller)的编译前提就位(StockVirtualWxVo 已补)。
- 残余承托缺口以 07-06 编译门为权威迭代回流。
