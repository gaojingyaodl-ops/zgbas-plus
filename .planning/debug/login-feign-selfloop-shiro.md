---
slug: login-feign-selfloop-shiro
status: investigating
trigger: "POST /login zgadmin 登录失败,抛 UncheckedExecutionException → WebApplicationException;日志仅显示[登录失败]"
created: 2026-07-17
updated: 2026-07-17
goal: find_and_fix
tdd_mode: false
---

# Symptoms

- **Expected:** 以 zgadmin 登录成功,进入系统。
- **Actual:** 登录失败。日志:`WARN AbstractAuthenticator: Authentication failed... Possible unexpected error?` + `ERROR MyFormAuthenticationFilter: [登录失败] class org.apache.shiro.authc.AuthenticationException`。深层 `WebApplicationException` 来自 `CommErrorDecoder.decode:34`。
- **Error (stack 关键帧):**
  - `ShiroDbRealm.getAppId(ShiroDbRealm.java:171)`
  - `ShiroDbRealm.doGetAuthenticationInfo(ShiroDbRealm.java:134)`
  - `BsDictUtil.getListByCategory(BsDictUtil.java:126)` ← `com.spt.bas.client.cache.BsDictUtil`
  - `BsDictUtil$1.load(BsDictUtil.java:47)` → `IBsDictClient` Feign `$Proxy183.findAll`
  - `CommErrorDecoder.decode(CommErrorDecoder.java:34)` → 抛 `WebApplicationException`
- **Timeline:** 单体合并(zgbas-plus)后才出现。旧 zgbas 微服务下 BasServer 是独立进程、不受 Web Shiro 管,Web→BasServer 的 Feign 跨进程调用在登录期间可正常完成。
- **Reproduction:** 启动应用 → POST /login(username=zgadmin)→ 失败。

# Current Focus

- **hypothesis:** 已确认为 Shiro 循环依赖 + 自回环不带 session(见下"Confirmed Root Cause")。
- **next_action:** 应用方案 A(anon-list `/spt-bas-server/**`),重启复跑验证登录通。
- **planned_fix:** 见下"Planned Fix (Option A, user-approved)"。

# Confirmed Root Cause (verified by code trace)

单体重构引入的行为偏离:**登录期间对"自己"发起 Feign 自回环,且该自回环路径落进了 Shiro `/** = user` 兜底规则**。

链路:
1. `ShiroDbRealm`(import `com.spt.bas.client.cache.BsDictUtil`,文件 `zgbas-system/.../web/shiro/ShiroDbRealm.java:26`)在 `doGetAuthenticationInfo`(此时尚无 session)中调用 `getAppId()`(:134)。
2. client 版 `BsDictUtil.getListByCategory`(:126)触发 Guava cache `load`(:47):`SpringContextHolder.getBean(IBsDictClient.class).findAll()` —— 走 Feign。
3. `IBsDictClient`(`zgbas-system/.../client/remote/IBsDictClient.java:24`,`@FeignClient(name="spt-bas-server", path="spt-bas-server/api/dict", url="#{basServerConfig.url}", configuration=FeignConfig.class)`),`findAll()` 继承自 `BaseClient`(`@PostMapping("findAll")`)。
4. `basServerConfig.url` → `application-dev.yml` `spt.bas.server.url: http://localhost:8080`(自回环锚点);`BasFeignPathConfig` 给 `com.spt.bas.server.api` 包加 `/spt-bas-server` 前缀。故实际请求:`POST http://localhost:8080/spt-bas-server/api/dict/findAll`。
5. 该路径不匹配任何 anon 规则(`IShiroSection.initDefault` 仅 `/open/** /ws/** /wx/** /register/** /static/** /favicon.ico`),落到 `ShiroChainMetaSource:28` 的 `section.put("/**", "user")` 兜底 → authc。
6. 全仓库无 `RequestInterceptor`(已确认 `implements RequestInterceptor` 零命中),自回环请求不携带 Shiro session → 登录期间无 session → 被判未登录 → 非 2xx。
7. `CommErrorDecoder.decode`(`zgbas-common/.../http/feign/CommErrorDecoder.java:27-48`,无 status 守卫,任何非 2xx 都包成 `WebApplicationException`)抛异常。
8. `client.cache.BsDictUtil.getListByCategory:134-137` catch 掉 `ExecutionException` **静默返回空 list** → `getAppId` 查不到 → 返回 null → `doGetAuthenticationInfo` 返回 null → 登录失败。

补充:此问题系统性影响所有 authc 流程中的自回环(不止 dict),因自回环一律不带 session。

# Planned Fix (Option A, user-approved)

在 `IShiroSection.initDefault` 的 anon 组内加入内部 server API 前缀,复刻旧架构"server API 不在登录闸后":

**文件:** `zgbas-common/src/main/java/com/spt/tools/shiro/IShiroSection.java`

**当前(17-27):**
```java
static void initDefault(Ini.Section section) {
    section.put("/open/**", "anon");
    section.put("/ws/**", "anon");
    section.put("/wx/**", "anon");
    section.put("/register/**", "anon");
    section.put("/static/**", "anon");
    section.put("/favicon.ico", "anon");
    section.put("/static/**", "anon");
    section.put("/login", "authc");
    section.put("/logout", "logout");
}
```

**改后:** 在 anon 组内(建议置于 `/open/**` 同组、`/login` 之前)加:
```java
    // 内部 server API(BasServer 合并进单体后的自回环目标):旧微服务架构下为独立进程、不受 Web Shiro 管,
    // 单体内复刻为 anon,避免登录期间 Feign 自回环落到 /**=user 兜底造成循环依赖。
    section.put("/spt-bas-server/**", "anon");
```

一次解掉所有 `spt-bas-server` 自回环(dict/company/... 等)。

**安全权衡(用户已知悉并接受):** 内部 server API 对外变匿名可访问;旧架构靠独立进程隔离,单体内需部署侧网络隔离兜底。符合项目"行为等价优先于安全加固"取向(参考 plaintext-secrets 决策)。

# Verification

1. 编译:JDK1.8,locale 无关 grep `cannot find symbol|找不到符号` 零命中。
2. 启动:明文密钥态(application-dev.yml),无需 export DB_PASSWORD/SPT_APP_SECRET。启动上下文正常(无 bean 缺失)。
3. 功能:以 zgadmin POST /login 登录成功 —— 不再抛 WebApplicationException,getAppId 返回非 null,进入系统。
4. 自回环:启动后(或登录流程中)`/spt-bas-server/api/dict/findAll` 不被 Shiro 重定向/拒。

# Environment Conventions

- **JDK:** 本机默认 JDK21,每条 mvn 必须前缀 Corretto 1.8 的 `JAVA_HOME`(记忆 project_zgbas-plus-jdk8-mvn-prefix)。Maven: `/Users/alan/App/apache-maven-3.8.6`,settings: `/Users/alan/App/apache-maven-3.8.6/zg_settings.xml`。
- **密钥:** 明文态,启动测试无需 export(记忆 project_phase4-plaintext-secrets-decision)。
- **启动测试非 hermetic:** 必须独立复跑确认,勿信自报 GREEN(记忆 project_zgbas-plus-nonhermetic-startup-test)。

# Evidence

- (initial) 根因由 Explore agent 全链路代码追踪确认:ShiroDbRealm import、IBsDictClient 注解、BasFeignPathConfig 前缀、ShiroChainMetaSource `/**=user` 兜底、IShiroSection anon 列表、CommErrorDecoder 无 status 守卫、全仓零 RequestInterceptor。详见对话已确认。

# Eliminated

- (initial) 非"密码/账号错误"——失败发生在 getAppId 阶段(密码校验前的 appId 解析),深层是 HTTP 调用失败而非凭证比对失败。
- (initial) 非 404/path-mismatch——`/spt-bas-server/api/dict/findAll` 路由确实存在(BsDictApi + BaseApi.findAll),只是被 Shiro 拦。
