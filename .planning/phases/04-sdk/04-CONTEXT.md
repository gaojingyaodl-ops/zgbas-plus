# Phase 4: 基础设施 & SDK 接入 - Context

**Gathered:** 2026-07-22
**Status:** Ready for planning

<domain>
## Phase Boundary

Phase 4 为 WX service 层（Phase 5）准备全部运行时依赖：
- `spring-boot-starter-data-redis` 引入单体，RedisTemplate bean 可注入
- `weixin-java-miniapp:3.8.0` SDK 引入，WxMiniAppConfig + WxConfiguration 就位，WxMaService 通过 static getMaService() 可用
- JwtConfig + JwtUtil + JwtAuthenticationFilter 迁入，FilterRegistrationBean 限定 /wx/* + /ewechat/* + /axq/* 路径，Shiro 已有 session 路径不受干扰
- Redis/JWT/WxMaService 三类 bean 在 Spring context 中均可正常注册，无 BeanCreationException

**不在此阶段：**
- Phase 5（~20 service impl + 11 controller + 辅助组件）
- CFCA / 金信电签保持外部 HTTP 调用
- CustomConfig/IgnoreConfig 不迁（用 FilterRegistrationBean URL pattern 替代 yml ignores 方案）

</domain>

<decisions>
## Implementation Decisions

### Redis 配置
- **D-01:** Redis 连接配置：host=47.104.15.98，port=6379，password=zg123456，写入 `application-dev.yml`（与现有明文密钥策略一致，Phase 4 不引入外置）。
- **D-02:** `RedisConfig`（`@Configuration @EnableCaching`）和 `FastJson2JsonRedisSerializer` 照搬源码迁入 `zgbas-system`，无需改动。
- **D-03:** Redis 依赖在 `zgbas-system/pom.xml` 声明 `spring-boot-starter-data-redis`（无版本，由 Spring Boot 2.5.9 BOM 管理）。

### jjwt 兼容性
- **D-04:** `JwtUtil.parseJWT` 已改为委托 `TokenUtil.parseJWT(jwt, key)`，走 `Jwts.parser().setSigningKey(bytes)` API——正是单体内联的 jjwt 0.7.0（compile-provided）API，无冲突，直接复用，**不需要任何改动**。
- **D-05:** `JwtUtil.createJWT` 同样委托 `TokenUtil.createToken(id, subject, null, key, ttl)`，已验证兼容，照搬迁入即可。

### JwtFilter 注册范围
- **D-06:** `JwtAuthenticationFilter` **不使用 `@Component`**（防止全局生效），改为在一个新建 `WxSecurityConfig`（`@Configuration`）里用 `FilterRegistrationBean` 注册，`urlPatterns = ["/wx/*", "/ewechat/*", "/axq/*"]`。
- **D-07:** `CustomConfig` / `IgnoreConfig` 不迁入（源码 yml ignores 方案被 FilterRegistrationBean 替代）；`JwtAuthenticationFilter.initIgnores()/@PostConstruct` 中对 `CustomConfig` 的 `@Autowired` 需移除，改为 `checkIgnores()` 直接返回 `false`（过滤链本身已限定路径，不再需要二次 ignores 过滤）。
- **D-08:** Shiro `IShiroSection.initDefault` 已将 `/wx/**` 设为 `anon`，JWT filter 与 Shiro filter chain 不冲突，无需修改 Shiro 配置。

### WxMaService 访问模式
- **D-09:** 保留 `WxConfiguration` 源码的 static Map + static `getMaService()` 模式。Phase 5 service 照搬源码直接调用 `WxConfiguration.getMaService()`，不改注入方式，迁移工作量最小。
- **D-10:** `WxConfiguration` 迁入 `zgbas-system`，`WxMiniAppConfig` 小程序 appid/secret 配置写入 `application-dev.yml`（key 前缀 `wx.miniapp.configs[0].*`）。

### weixin-java-miniapp SDK
- **D-11:** `weixin-java-miniapp:3.8.0` 在 `zgbas-system/pom.xml` 声明（无需 version management，非 Spring Boot BOM 管理依赖，显式写 3.8.0）。
- **D-12:** `WxConfiguration` 使用 `@EnableConfigurationProperties(WxMiniAppConfig.class)` 绑定配置，照搬源码，无需额外 `@ConfigurationPropertiesScan`。

</decisions>

<canonical_refs>
## Canonical References

**Downstream agents MUST read these before planning or implementing.**

### 需求 / Roadmap
- `.planning/ROADMAP.md` — Phase 4 goal、Success Criteria（4 条验收标准）、Requirements 映射（WX-SERVICE-02, WX-SERVICE-03）
- `.planning/REQUIREMENTS.md` — v1.2 requirement list，WX-SERVICE-02/03 完整定义

### 源码（basWx purchase-server）
- `/Users/alan/WorkSpace/IDEA/zgbas/basWx/purchase-server/src/main/java/com/spt/bas/purchase/wx/server/config/RedisConfig.java` — Redis bean + FastJson 序列化
- `/Users/alan/WorkSpace/IDEA/zgbas/basWx/purchase-server/src/main/java/com/spt/bas/purchase/wx/server/config/FastJson2JsonRedisSerializer.java` — Redis 序列化器
- `/Users/alan/WorkSpace/IDEA/zgbas/basWx/purchase-server/src/main/java/com/spt/bas/purchase/wx/server/config/WxMiniAppConfig.java` — @ConfigurationProperties `wx.miniapp`
- `/Users/alan/WorkSpace/IDEA/zgbas/basWx/purchase-server/src/main/java/com/spt/bas/purchase/wx/server/config/WxConfiguration.java` — WxMaService 注册 + static getMaService()
- `/Users/alan/WorkSpace/IDEA/zgbas/basWx/purchase-server/src/main/java/com/spt/bas/purchase/wx/server/config/JwtConfig.java` — @ConfigurationProperties `jwt.config`
- `/Users/alan/WorkSpace/IDEA/zgbas/basWx/purchase-server/src/main/java/com/spt/bas/purchase/wx/server/util/JwtUtil.java` — JWT 创建 + 解析（委托 TokenUtil）
- `/Users/alan/WorkSpace/IDEA/zgbas/basWx/purchase-server/src/main/java/com/spt/bas/purchase/wx/server/config/JwtAuthenticationFilter.java` — JWT OncePerRequestFilter（需改造：去掉 @Component + CustomConfig）
- `/Users/alan/WorkSpace/IDEA/zgbas/basWx/purchase-server/src/main/resources/application.properties` — jwt.config.key/ttl/remember 原始值参考

### 关键单体文件（对齐模式参照）
- `zgbas-common/src/main/java/com/spt/tools/http/util/TokenUtil.java` — JwtUtil 委托的 parseJWT/createToken 实现（jjwt 0.7.0 API）
- `zgbas-common/src/main/java/com/spt/tools/shiro/IShiroSection.java` — `initDefault()` 已含 `/wx/**=anon`（确认 Shiro 不干扰 WX 路径）
- `zgbas-admin/src/main/resources/application-dev.yml` — Redis + wx.miniapp + jwt.config 配置落点
- `zgbas-system/pom.xml` — Redis + weixin-java-miniapp 依赖声明落点

### Phase 3 CONTEXT（已完成的上游决策）
- `.planning/phases/03-feign/03-CONTEXT.md` — Feign 自回环、EntityScan/EnableJpaRepositories 扩包决策（D-01~D-09）

</canonical_refs>

<code_context>
## Existing Code Insights

### Reusable Assets
- `TokenUtil.parseJWT(jwt, key)` / `TokenUtil.createToken(id, subject, null, key, ttl)`：JwtUtil 已经委托这两个方法，单体内 jjwt 0.7.0 compile-provided 已支持，照搬即用。
- `IShiroSection.initDefault()`：`/wx/**=anon` 已在过滤链，JwtFilter 注册 /wx/* pattern 后不存在双重拦截问题。
- `BasClientConfig` 模式：Phase 3 已落地，Phase 4 新增 bean 沿用同一 @Configuration 风格。

### Established Patterns
- 明文配置在 `application-dev.yml`：Redis password / wx.miniapp.secret / jwt.config.key 均明文写入 dev yml（与 Phase 4 明文密钥决策 D-P4 一致）。
- `zgbas-system` 承载所有迁入 bean：Redis/JWT/Wx 配置类均落 `zgbas-system`，Admin 模块不放 config 类。
- FilterRegistrationBean 注册 Filter：单体已有 Shiro filter，再注册 JWT filter 须走 `FilterRegistrationBean` 而非 `@Component`，否则 Shiro 全局 filter 之前 JWT filter 就拦截了非 WX 路径。

### Integration Points
- `zgbas-admin/src/main/java/com/spt/ZgbasApplication.java`：`@EnableConfigurationProperties` 如需全局注册 JwtConfig，可在 Admin 启动类或 WxSecurityConfig 上加（推荐在 WxSecurityConfig 局部加 `@EnableConfigurationProperties(JwtConfig.class)`）。
- Phase 5 service 调用 `WxConfiguration.getMaService()`：WxConfiguration 必须在 Phase 5 前完成初始化（@PostConstruct），Phase 4 验收即包含此 bean 可取到。

</code_context>

<specifics>
## Specific Ideas

- Redis 连接：host=47.104.15.98，port=6379，password=zg123456（远程 Redis，非 localhost）。
- JwtFilter 改造要点：删掉 `@Component`，删掉 `@Autowired CustomConfig`，`initIgnores()/@PostConstruct` 方法整体删除，`checkIgnores()` 方法改为直接 `return false`（FilterRegistrationBean pattern 已限定路径，不需要再做路径匹配）。
- WxMaService 保留 static 模式：Phase 5 service 照搬 `WxConfiguration.getMaService()` 调用，不用 @Autowired 注入。

</specifics>

<deferred>
## Deferred Ideas

- `CustomConfig` / `IgnoreConfig` 照搬 yml ignores 方案：被 FilterRegistrationBean 替代，本阶段不迁。
- CFCA 电签（jinxin.*）配置：Phase 5 迁 service 时携带，Phase 4 不处理。
- 企业微信 EweChatApi corpid/corpsecret 配置化：留 Future Requirements，本期不做。

</deferred>

---

*Phase: 4-基础设施 & SDK 接入*
*Context gathered: 2026-07-22*
