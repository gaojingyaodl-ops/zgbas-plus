---
title: Phase 5 承托层五类 inventory checklist (D-15a/b, SC#4 deliverable)
phase: 5
plan: 05-05
status: complete
created: 2026-07-24
---

# Phase 5 承托层 D-15a/b 五类 Inventory Checklist

> SC#4 硬交付物。逐类盘点 `source → consumer → must-port? → 落点 plan → 状态`,结论与 05-01..05-04 实际迁入对账。

## 1. return envelope(统一返回信封)

| 项 | 值 |
|---|---|
| source | `com.spt.bas.purchase.wx.server.common.ApiResult` |
| consumer | WX controller(Phase 7)+ `GlobalExceptionHandler`(05-03)catch → ApiResult |
| must-port? | **Y** |
| 落点 plan | 05-01(源实测替换 P4 stub,D-P5-14) |
| 状态 | ✅ 就位(05-01),含 `of/ofStatus/ofException` 方法,swagger 注释 |

## 2. exception advice(全局异常处理)

| 项 | 值 |
|---|---|
| source | `com.spt.bas.purchase.wx.server.exception.GlobalExceptionHandler`(+ SecurityException / UserNameOrPasswordException) |
| consumer | WX controller(Phase 7)错误响应 |
| must-port? | **Y** |
| 落点 plan | 05-03(`@ControllerAdvice(basePackages=...)` 限域,D-P5-02) |
| 状态 | ✅ 就位(05-03)。单体全量 @ControllerAdvice = 1(仅此限域),不改写主域 bas 错误响应 |

## 3. user-context(用户上下文 + 请求清理)

| 项 | 值 |
|---|---|
| source | `util/UserContext` + `vo/UserInfoVo` + `listener/RequestListener` |
| consumer | `JwtAuthenticationFilter`(P4 写入)+ `RequestListener`(05-05 清理)+ WX 认证链路 |
| must-port? | **Y** |
| 落点 plan | UserInfoVo 05-01(替换 stub);UserContext 05-02(替换 stub);RequestListener 05-05 |
| 状态 | ✅ 就位。RequestListener.requestDestroyed → `UserContext.removeUser()` 清理 ThreadLocal(D-P5-10),防内存泄漏 + 身份串线程 |

## 4. auth helper(认证辅助)

| 项 | 值 |
|---|---|
| source | `util/UserHelper` + `util/JwtUtil` |
| consumer | WX 认证(JwtAuthenticationFilter P4、登录服务 Phase 6) |
| must-port? | **Y** |
| 落点 plan | UserHelper 05-02;JwtUtil P4 已就位(288L 实测,未覆盖) |
| 状态 | ✅ 就位。无其他 pending auth helper。UserHelper 解除 MessageEnums 前向引用 |

## 5. serialization-upload-wrapper(序列化 + 上传 wrapper)

| 项 | 值 |
|---|---|
| source | `config/FastJson2JsonRedisSerializer` + `util/UploadHelper` + 单体 `WebAppConfig` multipartConfigElement/converters + `util/ConvertUtils`(html2Img) |
| consumer | Redis 序列化(P4 RedisConfig)+ WX 文件上传(Phase 6/7)+ 合同 HTML→图(Phase 6 UserInfoService) |
| must-port? | **Y** |
| 落点 plan | FastJson2JsonRedisSerializer P4;UploadHelper 05-02;ConvertUtils 05-02(+ gui.ava dep);multipart/converters 单体 WebAppConfig 已有(CORS 05-03 并入) |
| 状态 | ✅ 就位。OCR wrapper(OcrUtils/OcrHelper 05-04)归此类延伸,HTTP 边界维持 |

---

## 汇总

| # | 类别 | must-port | 落点 | 状态 |
|---|---|---|---|---|
| 1 | return envelope | Y | 05-01 | ✅ |
| 2 | exception advice | Y | 05-03 | ✅ |
| 3 | user-context | Y | 05-01/02/05 | ✅ |
| 4 | auth helper | Y | 05-02/P4 | ✅ |
| 5 | serialization-upload-wrapper | Y | P4/05-02/05-03 | ✅ |

**结论:** 五类承托全部 must-port=Y,均已落位(05-01..05-05)。无遗漏、无虚报。

## 不迁项(deferred / out-of-scope)

- **EweChatApi / JinXinApi** → Phase 6(D-P5-18/D-P5-08;依赖 service 层)
- **PurchaseCommand** → Phase 6(D-P5-13 修正;依赖 P6 service + xxl-job scrub)
- **IBsDictService impl + bean** → Phase 6(05-04 已迁接口供 BsDictUtil 编译;runtime getBean 是 Phase 8 启动关注点)
- **FrameworkConfig**(6 个 @Bean)→ 永不迁(D-P5-15,单体已自有/冲突)
- **TransactionConfig / WxJobConfig** → 跳过(D-P5-04/D-P5-06,死码/被替代)
