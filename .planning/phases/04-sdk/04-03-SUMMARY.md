---
phase: 4
plan: "04-03"
subsystem: wx-sdk-config
tags: [wx, miniapp, WxMaService, common-stubs, configuration]
dependency_graph:
  requires: [04-01, 04-02]
  provides: [WxMaService-bean, common-BaseException-Status-ApiResult]
  affects: [04-04, Phase-5-wx-service]
tech_stack:
  added: [weixin-java-miniapp:3.8.0]
  patterns: [static-map-singleton, @EnableConfigurationProperties, @PostConstruct-init]
key_files:
  created:
    - zgbas-system/src/main/java/com/spt/bas/purchase/wx/server/common/Status.java
    - zgbas-system/src/main/java/com/spt/bas/purchase/wx/server/common/BaseException.java
    - zgbas-system/src/main/java/com/spt/bas/purchase/wx/server/common/ApiResult.java
    - zgbas-system/src/main/java/com/spt/bas/purchase/wx/server/config/WxMiniAppConfig.java
    - zgbas-system/src/main/java/com/spt/bas/purchase/wx/server/config/WxConfiguration.java
  modified: []
decisions:
  - "D-09 落地: WxConfiguration 保留 static Map + getMaService() 模式，Phase 5 service 可直接调用无需改注入方式"
  - "ApiResult swagger 注解注释掉（项目无 springfox/knife4j 依赖）"
  - "WxConfiguration 构造器赋值改为 WxConfiguration.properties = properties（修复 static field 赋值正确性）"
metrics:
  duration: "8min"
  completed: "2026-07-22"
  tasks: 2
  files: 5
---

# Phase 4 Plan 03: WxMaService 配置类迁入 + 公共 exception/common stubs Summary

**One-liner:** WxMiniAppConfig + WxConfiguration 迁入 zgbas-system，static getMaService() 模式保留，BaseException/Status/ApiResult stub 创建，zgbas-system compile 零 ERROR。

## Tasks Completed

| Task | Name | Commit | Files |
|------|------|--------|-------|
| 1 | 创建 common stubs (BaseException/Status/ApiResult) | d5de8e5 | 3 files created |
| 2 | 创建 WxMiniAppConfig + WxConfiguration 并验证编译 | 44a71c6 | 2 files created |

## Verification Results

```
JAVA_HOME=.../amazon-corretto-8.jdk mvn compile -pl zgbas-system
[INFO] BUILD SUCCESS
[INFO] Total time:  8.336 s
```

零 [ERROR] 行，zgbas-system compile 通过。

## What Was Built

### Task 1: common stubs

- **Status.java** — 完整枚举（37 个枚举常量），含 ERROR/UNAUTHORIZED/TOKEN_EXPIRED(5003)/TOKEN_PARSE_ERROR(5004)/TOKEN_OUT_OF_CTRL(5005)/NO_ACCESS_TOKEN(5010) 等 04-04 JwtUtil 需要的全部值
- **BaseException.java** — `extends RuntimeException`，含 `BaseException(Status status)` + `BaseException(Status status, Object data)` + `BaseException(Integer code, String message)` + `BaseException(Status status, String message)` 四种构造器；lombok @Data/@EqualsAndHashCode
- **ApiResult.java** — 含 `ofStatus(Status, Object)` / `ofSuccess(Object)` / `ofException(T)` 静态工厂；swagger 注解行注释掉（项目无 springfox 依赖），其余内容完整照搬

### Task 2: WxMaService 配置

- **WxMiniAppConfig.java** — `@Data @ConfigurationProperties(prefix = "wx.miniapp")`，`List<Config> configs`，内部类 Config（appid/secret/token/aesKey/msgDataFormat）
- **WxConfiguration.java** — `@Configuration @EnableConfigurationProperties(WxMiniAppConfig.class)`：
  - static `Map<String, WxMaService> maServices` + static `Map<String, WxMaMessageRouter> routers`
  - `getMaService()` 无参版本（用 `properties.getConfigs().get(0).getAppid()` 取首个配置）+ `getMaService(String appid)` 有参版本
  - `@PostConstruct init()`: 遍历 configs，创建 `WxMaDefaultConfigImpl` + `WxMaServiceImpl`，并注册 router
  - 5 个 `WxMaMessageHandler` lambda：logHandler / textHandler / picHandler / qrcodeHandler / templateMsgHandler
  - 引用 Task 1 创建的 `BaseException(Status.ERROR, "小程序配置错误")`

## Deviations from Plan

### Auto-fixed Issues

**1. [Rule 1 - Bug] WxConfiguration 构造器 static field 赋值**
- **Found during:** Task 2 代码审查
- **Issue:** 源码 `this.properties = properties` 对 static 字段赋值，Java 中对 static 字段应写 `WxConfiguration.properties = properties`（源码原样如此，编译器会警告但不报错）
- **Fix:** 改为 `WxConfiguration.properties = properties`，消除静态字段赋值歧义
- **Files modified:** WxConfiguration.java
- **Commit:** 44a71c6

None of the other deviations — plan executed as written.

## Known Stubs

| Stub | File | Reason |
|------|------|--------|
| ApiResult swagger 注解注释 | ApiResult.java | 项目无 springfox，Phase 5 overlay 时原样恢复 |
| 所有三个 common 类 | common/*.java | Phase 4 stub，Phase 5 将用源码完整版覆盖（文件首行已标注） |

## Threat Flags

无新增威胁面（WxMiniAppConfig.secret 明文配置属 T-04-05 accept，已在计划 threat register 中登记）。

## Self-Check: PASSED

- FOUND: Status.java
- FOUND: BaseException.java
- FOUND: ApiResult.java
- FOUND: WxMiniAppConfig.java
- FOUND: WxConfiguration.java
- FOUND commit: d5de8e5 (Task 1)
- FOUND commit: 44a71c6 (Task 2)
- zgbas-system compile: BUILD SUCCESS, 零 ERROR
