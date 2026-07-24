---
phase: 5
plan: 05-03
subsystem: carrier-layer (灰区 A — 横切 bean 收口)
tags: [migration, wx, cross-cutting, gray-area, aop, cors, exception-handler]
requires: [05-01, 05-02]
provides:
  - WX 横切 bean 安全就位(CORS 合并 / 限域 @ControllerAdvice / 显式注册 @Aspect / WX-only 配置)
  - exception/ 3 类(GlobalExceptionHandler 限域 + SecurityException 源实测 + UserNameOrPasswordException)
affects:
  - 05-04(OcrConfig POJO 就位供 OcrUtils;EweChatConfig @Bean 供 ewechat)
  - 05-05(启动接线;WxCarrierConfig 注入)
  - Phase 7(WX controller 错误信封经限域 GlobalExceptionHandler → ApiResult)
tech-stack:
  added: []
  patterns: [scoped-controller-advice, explicit-aspect-bean-registration, config-slimming]
key-files:
  created:
    - "zgbas-system/.../wx/server/exception/{GlobalExceptionHandler,SecurityException,UserNameOrPasswordException}.java"
    - "zgbas-system/.../wx/server/aop/ServiceAop.java"
    - "zgbas-system/.../wx/server/config/{WxAopConfig,WxCarrierConfig,JinXinConfig,EweChatConfig,OcrConfig}.java"
  modified:
    - "zgbas-system/.../bas/server/config/WebAppConfig.java (+addCorsMappings, D-P5-01)"
key-decisions:
  - "D-P5-01:WX CORS 并入单体 WebAppConfig(唯一独有),不整体迁 WX WebAppConfig"
  - "D-P5-02:GlobalExceptionHandler @ControllerAdvice 限 basePackages,单体唯一 @ControllerAdvice"
  - "D-P5-03:ServiceAop pointcut 5 通配 = 7 段 wx.server.service,自限无需收窄;@Aspect 无 @Component → WxAopConfig 显式 @Bean"
  - "D-P5-15:FrameworkConfig 6 个 @Bean 全 DROP(单体已自有/冲突);WxCarrierConfig 仅 eweChatConfig() @Bean"
  - "D-P5-14:SecurityException 源实测替换 P4 stub"
requirements-completed: []
requirements-addressed: [WX-BFF-03]
duration: "~20 min"
completed: 2026-07-24
---

# Phase 5 Plan 03: 横切 bean 收口(灰区 A) Summary

灰区 A 逐 bean 最小消歧:4 个全局横切 bean(CORS / GlobalExceptionHandler / ServiceAop / FrameworkConfig)按用户锁定决策安全落位 —— 行为等价优先于 verbatim 字面,避免启动崩溃与主域行为改写。同时 exception/ 3 类落位(SecurityException 替换 stub)。

- **Duration:** ~20 min · **Tasks:** 4 · **Files:** 11(10 新 + 1 改)
- **Commits:** e238800(T1 CORS)/ 87bdde7(T2 exception)/ b607324(T3 ServiceAop)/ c115334(T4 config)

## Tasks Executed

### Task 1 — WebAppConfig CORS 合并(D-P5-01,非 verbatim)
- WX WebAppConfig 与单体 bas.server.config.WebAppConfig 唯一差异 = CORS `/**`。
- 落地:**CORS verbatim 并入单体 WebAppConfig#addCorsMappings**(未整体迁 WX WebAppConfig)。
- 不重复:multipartConfigElement @Bean 仍只 1 个(单体原有)、PageInterceptor、转换器。
- 合并方式:**并入单体 WebAppConfig**(非新建 WxCorsConfig)。

### Task 2 — exception/ 3 类 + GlobalExceptionHandler 限域(D-P5-02)
- GlobalExceptionHandler verbatim,**但** `@ControllerAdvice(basePackages = "com.spt.bas.purchase.wx.server")`。
- 单体全量 @ControllerAdvice 计数 = **1**(仅此限域处),不改写主域 bas 错误响应。
- SecurityException 源实测替换 P4 stub(D-P5-14);UserNameOrPasswordException verbatim。

### Task 3 — ServiceAop + WxAopConfig(D-P5-03)
- ServiceAop verbatim(`@Aspect`,**无** `@Component`),pointcut 原文:`execution(* com.*.*.*.*.*.service..*.*(..))`。
- **pointcut 收窄决策:不收窄**。5 段单段通配 = 7 段路径 `com.spt.bas.purchase.wx.server.service..`,不命中主域 `com.spt.bas.server.service`(5 段,需 3 通配),亦不与 spt-tools `com.*.*.*.service..`(3 通配 + 注解驱动)重叠 → 无双增强。
- 新建 WxAopConfig(@Configuration),`@Bean public ServiceAop wxServiceAop()` 显式注册(参照 ToolsAopConfig)。

### Task 4 — config POJO + WxCarrierConfig(D-P5-15)
- verbatim 迁入:JinXinConfig(`@ConfigurationProperties(jinxin)`)、EweChatConfig(`@Data` POJO)、OcrConfig(`@ConfigurationProperties(aliyun.ocr)`)。
- **FrameworkConfig 未 verbatim 迁**(enclave 无该文件)。DROP 6 个 @Bean:
  | @Bean | 处置理由 |
  |---|---|
  | threadPoolTaskExecutor | 单体 ScheduleConfig 已供调度执行器 |
  | dataSourceConfig | 单体自有 datasource |
  | pushClientHttp | 外部 push SDK,单体已保留(#7) |
  | authOpenFacade | 外部 auth SDK,单体已保留(#7) |
  | fileRemote | 外部 file SDK,单体已保留(#7) |
  | customConfig | FilterRegistrationBean wiring 已替代 |
- 新建 WxCarrierConfig(@Configuration),唯一 `@Bean @ConfigurationProperties(prefix=ewechat.config) EweChatConfig eweChatConfig()`。

## Acceptance Criteria Results

| Criterion | Result |
|---|---|
| 单体 WebAppConfig 含 WX CORS(allowedOriginPatterns 等) | ✅ |
| multipartConfigElement @Bean 仅 1(无重复) | ✅(方法定义 1 个) |
| GlobalExceptionHandler 含 `@ControllerAdvice(basePackages=...)` | ✅ line 29 |
| exception/ 含 3 类;SecurityException 非 stub | ✅ 3 / ✓ real |
| 全单体 @ControllerAdvice 仅 1(限域) | ✅ count=1 |
| ServiceAop @Aspect 且无 @Component | ✅ 1 / 0 |
| WxAopConfig 含 `@Bean new ServiceAop()` | ✅ |
| JinXinConfig/EweChatConfig/OcrConfig 三 POJO 就位 | ✅ |
| WxCarrierConfig 含 eweChatConfig()(prefix ewechat.config) | ✅ |
| enclave 无 FrameworkConfig.java | ✅ absent |
| 静态自检:taskExecutor @Bean 全单体仅 1 | ✅ count=1 |

## Deviations from Plan

None —— 4 个 task 全部按 plan + 锁定决策(D-P5-01/02/03/15)执行。pointcut 经核实已自限,无需收窄(plan 允许的两种结论之一)。无新增 deviation。

## Verification

- reactor 编译:**EXIT=0,0 ERROR** —— GREEN。
- 静态自检:`@ControllerAdvice` 全单体 = 1(限域);`taskExecutor` @Bean 全单体 = 1(无重复)。

## Self-Check: PASSED

所有 acceptance criteria 通过;横切 bean 全部按锁定决策安全落位,无主域行为改写、无重复 @Bean、无双增强;reactor 全 GREEN。

## Next

Ready for **05-04**(灰区 B:cache 工具 BsDictUtil/RedisCache + OCR 外部 wrapper OcrUtils/OcrHelper;WX 外部集成密钥按明文策略进 dev yml;OcrConfig POJO 已就位供绑定)。
