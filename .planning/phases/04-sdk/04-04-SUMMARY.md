---
phase: 4
plan: "04-04"
subsystem: jwt-auth-system
tags: [jwt, filter, wx, authentication, stub, FilterRegistrationBean]
dependency_graph:
  requires: [04-01, 04-03]
  provides: [JwtConfig-bean, JwtAuthenticationFilter-FilterRegistrationBean, WxAccessTokenService-stub]
  affects: [Phase-5-wx-service, basWx-auth-flow]
tech_stack:
  added: []
  patterns: [FilterRegistrationBean-url-pattern, @ConfigurationProperties, ThreadLocal-UserContext, OncePerRequestFilter]
key_files:
  created:
    - zgbas-system/src/main/java/com/spt/bas/purchase/wx/server/exception/SecurityException.java
    - zgbas-system/src/main/java/com/spt/bas/purchase/wx/server/util/ResponseUtil.java
    - zgbas-system/src/main/java/com/spt/bas/purchase/wx/server/util/UserContext.java
    - zgbas-system/src/main/java/com/spt/bas/purchase/wx/server/vo/UserInfoVo.java
    - zgbas-system/src/main/java/com/spt/bas/purchase/wx/server/service/IWxAccessTokenService.java
    - zgbas-system/src/main/java/com/spt/bas/purchase/wx/server/service/impl/WxAccessTokenService.java
    - zgbas-system/src/main/java/com/spt/bas/purchase/wx/server/config/JwtConfig.java
    - zgbas-system/src/main/java/com/spt/bas/purchase/wx/server/util/JwtUtil.java
    - zgbas-system/src/main/java/com/spt/bas/purchase/wx/server/config/JwtAuthenticationFilter.java
    - zgbas-system/src/main/java/com/spt/bas/purchase/wx/server/config/WxSecurityConfig.java
  modified:
    - zgbas-common/src/main/java/com/spt/tools/http/util/TokenUtil.java
decisions:
  - "D-06/D-07 落地: JwtAuthenticationFilter 删除 @Component + CustomConfig @Autowired + initIgnores()/@PostConstruct，checkIgnores() 改为 return false，通过 WxSecurityConfig FilterRegistrationBean 限定路径 /wx/* /ewechat/* /axq/*"
  - "[Rule 1] TokenUtil.createToken 5-arg overload 新增: createToken(String id, String subject, Map claims, String secretKey, int ttlMillis)，源码 basWx/JwtUtil 依赖此签名但 feat-系统重构v5.0 未合回 spt-tools-http（与 2026-07-17 的 2-arg Map overload 同一修复模式）"
  - "[Rule 1] JwtUtil.parseJWT 内层 try-catch 将 TokenUtil.parseJWT 的 checked Exception 转为 SecurityException，避免 Java 编译器 unhandled exception 错误（UnsupportedEncodingException 由 utf-8 编码不存在触发，实际永不发生）"
  - "UserInfoVo swagger 注解注释掉（项目无 springfox/knife4j 依赖），Phase 5 overlay 时恢复"
metrics:
  duration: "15min"
  completed: "2026-07-22"
  tasks: 2
  files: 11
---

# Phase 4 Plan 04: JWT 认证体系迁入 Summary

**One-liner:** JwtConfig + JwtUtil + JwtAuthenticationFilter（去 @Component）+ WxSecurityConfig（FilterRegistrationBean /wx/* 三路径）+ 6 个辅助 stub，zgbas-system compile 零 ERROR，JWT filter 不干扰 Shiro session 路径。

## Tasks Completed

| Task | Name | Commit | Files |
|------|------|--------|-------|
| 1 | 创建辅助 stubs (SecurityException/ResponseUtil/UserContext/UserInfoVo/IWxAccessTokenService/WxAccessTokenService) | c02097e | 6 files created |
| 2 | JWT 体系迁入 (JwtConfig/JwtUtil/JwtAuthenticationFilter/WxSecurityConfig) + TokenUtil 5-arg fix | 1d62462 | 5 files created, 1 file modified |

## Verification Results

```
JAVA_HOME=.../amazon-corretto-8.jdk mvn compile -pl zgbas-system -am
[INFO] BUILD SUCCESS
[INFO] Total time:  8.531 s
```

零 [ERROR] 行，zgbas-system 含上游模块全量编译通过。

追加检查：
```
grep FilterRegistrationBean .../WxSecurityConfig.java
✓ 包含 import/Bean方法/构造调用共 6 行
```

## What Was Built

### Task 1: 辅助 stubs (6 files)

- **SecurityException.java** — `extends BaseException`，4 构造器（Status / Status+data / code+msg / code+msg+data）；package exception
- **ResponseUtil.java** — `renderJson(response, status, data)` + `renderJson(response, exception)` 两个重载，hutool JSONUtil 序列化，通过 `ApiResult.ofStatus/ofException` 构建响应体
- **UserContext.java** — `ThreadLocal<UserInfoVo>`，setUser/getUser/removeUser，移除了源码中对 `CompanyVo` 的 import（该类 vo 不在 Phase 4 迁入范围，方法体中也未实际使用）
- **UserInfoVo.java** — `@Data @Builder`，字段 userId/name/phone/accessToken/infoStep/companyName/companyId/sessionId/openId/informedConsentFlag；swagger 注解全部注释掉（项目无 springfox 依赖）
- **IWxAccessTokenService.java** — `extends IBaseService<WxAccessToken>`，`findByUserid(Long userId)` + `deleteByUserid(String userId)`
- **WxAccessTokenService.java** — `@Component @Transactional(readOnly=true)`，`extends BaseService<WxAccessToken> implements IWxAccessTokenService`，委托 WxAccessTokenDao（Phase 3 已迁入）

### Task 2: JWT 体系核心文件 (4 files) + Rule 1 TokenUtil 修复 (1 file)

- **TokenUtil.java (Rule 1 修复)** — 新增 5-arg overload `createToken(String id, String subject, Map<String,Object> claims, String secretKey, int ttlMillis)`：设 JWT id/subject/expiry/HS512 签名，不抛 checked exception（StandardCharsets.UTF_8）；JwtUtil.createJWT 依赖此签名

- **JwtConfig.java** — `@Data @ConfigurationProperties(prefix="jwt.config")`，字段 key/ttl/remember；无 @Component（由 WxSecurityConfig.@EnableConfigurationProperties 激活）

- **JwtUtil.java** — `@Configuration @EnableConfigurationProperties(JwtConfig.class)`：
  - `createJWT(UserInfoVo)` / `createJWT(Boolean, Long, String)` — 委托新 5-arg TokenUtil.createToken，持久化 WxAccessToken
  - `parseJWT(String jwt)` — 内层 try-catch 将 TokenUtil.parseJWT 的 checked Exception 转为 SecurityException；外层捕获 jjwt 各 RuntimeException 子类（ExpiredJwtException/UnsupportedJwtException/MalformedJwtException/SignatureException/IllegalArgumentException）
  - 其余方法（invildJWT/refreshJWT/getUsernameFromJWT/getUseridFromJWT/getJwtFromRequest/getJwtFromRequestBody/getUseridFromRequest/getUseridFromJwt）照搬源码

- **JwtAuthenticationFilter.java** — 按 D-06/D-07 改造：
  - 删除 `@Component` 注解（不再全局生效）
  - 删除 `private static Set<String> ignores` 字段
  - 删除 `@Autowired private CustomConfig customConfig`
  - 删除整个 `@PostConstruct initIgnores()` 方法
  - `checkIgnores()` 方法体改为 `return false`（FilterRegistrationBean urlPatterns 已限定路径）
  - 保留：`@Slf4j`、`@Autowired JwtUtil`、`@Autowired CompanyUserDao`、`@Autowired UserDetailDao`、完整 `doFilterInternal()` 方法体

- **WxSecurityConfig.java** — 新建 `@Configuration @EnableConfigurationProperties(JwtConfig.class)`：
  - `@Bean JwtAuthenticationFilter jwtAuthenticationFilter()` — Spring 管理 bean，可注入 JwtUtil/UserDetailDao/CompanyUserDao 等字段
  - `@Bean FilterRegistrationBean<JwtAuthenticationFilter> jwtFilterRegistration(JwtAuthenticationFilter filter)` — `addUrlPatterns("/wx/*", "/ewechat/*", "/axq/*")`, `setOrder(1)`;  Shiro `/wx/**=anon` 路径（D-08）不受干扰

## Deviations from Plan

### Auto-fixed Issues

**1. [Rule 1 - Bug] TokenUtil 缺少 5-arg createToken overload**
- **Found during:** Task 2 代码分析
- **Issue:** JwtUtil.createJWT 调用 `TokenUtil.createToken(id.toString(), loginName, null, jwtConfig.getKey(), ttl.intValue())` 但 TokenUtil 无此 5-arg 签名；与 2026-07-17 的 2-arg Map overload 同一模式——源码 feat-系统重构v5.0 mid-refactor 未将扩展方法合回 spt-tools-http
- **Fix:** 在 TokenUtil 新增 `createToken(String id, String subject, Map<String,Object> claims, String secretKey, int ttlMillis)` overload，实现设 JWT id/subject/ttl/HS512 签名；不抛 checked exception（StandardCharsets.UTF_8 替代 "utf-8".getBytes）
- **Files modified:** zgbas-common/src/main/java/com/spt/tools/http/util/TokenUtil.java
- **Commit:** 1d62462

**2. [Rule 1 - Bug] JwtUtil.parseJWT 调用 TokenUtil.parseJWT 未处理 checked Exception**
- **Found during:** Task 2 创建 JwtUtil.java
- **Issue:** `TokenUtil.parseJWT(jwt, key)` 声明 `throws Exception`（因 `"utf-8".getBytes()` 抛 UnsupportedEncodingException），而 JwtUtil.parseJWT 的 catch 链仅捕获具体 JwtException 子类，Java 编译器报 unhandled checked exception
- **Fix:** 在 JwtUtil.parseJWT 内层嵌套 try-catch，先捕获 RuntimeException 直接 rethrow（保持 jjwt 异常语义），再捕获 Exception 转为 SecurityException（实际永不触发，仅满足编译器要求）
- **Files modified:** zgbas-system/.../util/JwtUtil.java
- **Commit:** 1d62462

## Known Stubs

| Stub | File | Reason |
|------|------|--------|
| UserInfoVo swagger 注解注释 | UserInfoVo.java | 项目无 springfox，Phase 5 overlay 时原样恢复 |
| 所有 6 个辅助 stub 文件 | exception/util/vo/service/*.java | Phase 4 stub，Phase 5 将用源码完整版覆盖（文件首行已标注） |
| WxAccessTokenService.java | service/impl/WxAccessTokenService.java | Phase 4 stub，Phase 5 service 迁入后覆盖为完整版 |

所有 stubs 均为真实可运行的 @Component bean，Spring context 可正常创建；WxAccessTokenService 会真实查询 DB（stub 行为即最终行为，Phase 5 仅添加更多 service 实现）。

## Threat Flags

无新增威胁面。计划 threat register 已覆盖：
- T-04-07: JwtAuthenticationFilter JWT 解析用 HS256 签名验证（mitigate）
- T-04-08: FilterRegistrationBean 路径限定 /wx/* /ewechat/* /axq/*（mitigate，Shiro session 路径不受干扰）
- T-04-09: jwt.config.key=sgcoding 明文（accept，Phase 4 明文密钥政策）

## Self-Check: PASSED

- FOUND: SecurityException.java
- FOUND: ResponseUtil.java
- FOUND: UserContext.java
- FOUND: UserInfoVo.java
- FOUND: IWxAccessTokenService.java
- FOUND: WxAccessTokenService.java
- FOUND: JwtConfig.java
- FOUND: JwtUtil.java
- FOUND: JwtAuthenticationFilter.java
- FOUND: WxSecurityConfig.java
- FOUND commit: c02097e (Task 1)
- FOUND commit: 1d62462 (Task 2)
- zgbas-system compile: BUILD SUCCESS, 零 ERROR
