# Phase 3: 认证首页 - Context

**Gathered:** 2026-07-16
**Status:** Ready for planning

<domain>
## Phase Boundary

让用户能登录单体应用，并看到与旧系统 zgbas 等价的首页动态菜单。本阶段交付三件事：

1. **Shiro 登录认证链路移植**（AUTH-01/03/04）：取消 `ZgbasApplication` 对 `ToolsShiroConfig` 的 exclude，把源 `web` 的 `ShiroDbRealm` / `LoginController` / Shiro 过滤器链 / `ShiroUtil` 照搬进单体 —— Realm/Service/Util/ShiroFilter 配置落 `zgbas-framework`，登录 Controller（web 层）落 `zgbas-admin`。Shiro session+cookie（非 JWT）；密码校验行为等价（SHA-1 + 盐 1024 次迭代，**实际由外部 spt-auth 侧完成**，Realm 的 `assertCredentialsMatch` 为 no-op）。
2. **首页 + 动态菜单 + 全量前端**（AUTH-02 / UI hint=yes）：照搬源 `web` 的**全部静态资源 + 全部 HTML 模板**（1894 模板 / 780 JS / 704 CSS）进 `zgbas-admin/resources`，配套 Thymeleaf。登录成功后首页 + 动态菜单经 auth-sdk HTTP 调外部 spt-auth 取菜单/用户数据。
3. **配套 auth 面**：Mock 超级密码后门、SSO 单点登录入口、WebSocket 端点 —— 全部照搬（见 decisions）。

**不在本期（明确边界）：**
- 业务 Service / Controller / BFF 逻辑搬运 → **Phase 4**（BIZ-01..03）。本期搬全部前端模板，但业务页的后端 Controller 未迁 → 点业务菜单时页面 HTML 能开、数据接口裸 404（预期、Phase 4 接通自好）。
- 真实浏览器登录 e2e → **Phase 7**（行为对齐验证）。本期验收为"启动验证为主"。
- 真实轮换已泄漏生产库密码（CR-01）→ 跨阶段安全债，仍 deferred。

</domain>

<decisions>
## Implementation Decisions

### Shiro 认证链路移植 (AUTH-01/03/04)
- **D-P3-01:** **取消 `ToolsShiroConfig` exclude** —— 当前 `ZgbasApplication.java:63` 把 `com.spt.tools.shiro.config.ToolsShiroConfig.class` 列在 `@SpringBootApplication(exclude=...)` 里（Phase 2 启动时为避免空 Realm 报错而 exclude）。Phase 3 首步 = 移除该 exclude，让已内联的 spt-tools Shiro 自动配置生效。
- **D-P3-02:** **照搬源 `ShiroDbRealm`** 进 `zgbas-framework`（`com.spt.bas.web.shiro.ShiroDbRealm`，`@Component`，extends 已内联的 `AbstractShiroDbRealm`）。保留全部逻辑：`authOpenFacade.login()` 调外部 spt-auth、`findUserByLoginName` 构建 ShiroUser principal、`doGetAuthorizationInfo` 取角色/权限、SSO `reLoginSso` 路径、`assertCredentialsMatch` no-op（密码由 spt-auth 侧校验）。
- **D-P3-03:** **照搬源 `LoginController`** 进 `zgbas-admin`（`/login` GET 渲染 login 页 / POST 失败重渲染）。真正的登录 POST 由 Shiro `MyFormAuthenticationFilter`（已内联）拦截完成，Controller 仅负责页面。
- **D-P3-04:** **过滤器链照搬 DB 动态链** —— 源用 `ChainDefinitionSectionMetaSource` / `ShiroChainMetaSource`（已内联）从 DB 读过滤规则，照搬该机制（行为等价、与 spt-auth 同源）。⚠️ **留 research/planning 确认**：链表是否在 `sptbasdb_pd` schema 存在（Phase 2 `ddl-auto=none` 不建表）；不存在则记为 blocker，需补 DDL 或改静态规则。
- **D-P3-05:** **会话存储 = 内存** —— 源 web + 内联 spt-tools **无任何 Redis/SessionDAO**，Shiro 用默认内存会话；单体单进程，照搬内存即可，无需 Redis（已侦察确认）。

### Mock 超级密码后门
- **D-P3-06:** **保留照搬** —— 源 `ShiroDbRealm.isMockLogin()` 的 `super:<mockPassword>` 后门（模拟任意用户登录）代码 + `mockPassword` 配置值**全部照搬**，后门默认可用。行为等价优先于安全（与项目核心价值一致；PROJECT.md 已列为已知技术债）。
- **D-P3-07:** **`mockPassword` 值存放** —— 写死在 `application-dev.yml` 默认值（明文照搬旧项目）；prod profile 走 `${ZGBAS_MOCK_PASSWORD:}` 占位（沿用 Phase 2 D-P2-13/D-P2-14 的 prod 占位模式，默认空 = 后门关闭）。

### 前端落地 + 首页菜单 (AUTH-02 / UI hint)
- **D-P3-08:** **搬源 `web` 全部静态资源 + 全部 HTML 模板** —— 来源 `/Users/alan/WorkSpace/IDEA/zgbas/web/src/main/resources/{templates,static}`（**取 `src/`，不取 `target/classes` 编译副本**），落 `zgbas-admin/src/main/resources/{templates,static}`（admin 是 web/BFF 层，对齐 D-08 拓扑 + Phase 4"web→admin"模式）。含 login.html / index.html / main.html / admin/index.html + 共享 layout/fragment/static。
- **D-P3-09:** **配套 Thymeleaf** —— 引入 `spring-boot-starter-thymeleaf` + 视图解析配置（照搬源 web）。模板不在编译期校验，`mvn compile` 基线不受影响。
- **D-P3-10:** **菜单点未实现业务页 → 接受裸 404** —— 模板全搬但业务 Controller 留 Phase 4，点业务菜单时页面 HTML 能开、数据 XHR 裸 404。本期不做真实 e2e，Phase 4 接通自好。不加"迁移中"兜底拦截（零额外工作、不偏离照搬）。

### 配套 auth 面
- **D-P3-11:** **WebSocket 端点照搬** —— 源 `web` 有 `WebSocketConfig` + `ws/IndexWebSocketServer` / `ws/WebSocketServer`（首页级 WS，可能桥接 spt-push）。搬进 `zgbas-framework`/`zgbas-admin`，首页 WS 连接能建立、行为等价；**推送业务发送方留 Phase 4**（WS 先连上、闲置）。
- **D-P3-12:** **SSO 单点登录入口照搬** —— 源 `UserOpenController` 的 `ssoLogin` / `resSsoLogin` 入口照搬进 `zgbas-admin`，完整 auth 面、行为等价。Realm 的 `reLoginSso` 已随 D-P3-02 照搬进来。`zgBas.secret` 配置走环境变量占位（`${ZGBAS_SECRET:}`，同 Phase 2 密钥外置风格）。

### 验收策略
- **D-P3-13:** **启动验证为主**（同 Phase 2 D-P2-03）—— 启动成功 + Shiro bean 全接线（取消 exclude、Realm/Filter/ShiroFilter 就位）+ `/login` · `/index` · 菜单数据接口可达返回 200 即视为可用。**不强求真实登录通过、不拉起真实 spt-auth**（`IAuthOpenFacade` 是 HTTP bean 启动期懒注入）。真实浏览器登录 e2e 留 Phase 7 行为对齐。

### Claude's Discretion
- **Realm/Controller 落位包名**：照搬源包名 verbatim（`com.spt.bas.web.shiro.*` → Realm 落 framework；`com.spt.bas.web.controller.*` → admin），最小化 import 改动，对齐 D-P2-07"照搬保包名"。
- **登录失败/会话/登出行为**：照搬源（`MyLogoutFilter` 已内联），无需额外设计。
- **登录无验证码**：源 `login.html` + 源码无 kaptcha/captcha（已侦察确认），照搬即无验证码。
- **静态资源路径**：照搬 verbatim，模板内 `/static/...` 引用路径不变。
- **`zgBas.secret` / `mockPassword` 之外的其他 auth 配置项**（如 ShiroPropConfig 其余字段）：照搬源 + 走 Phase 2 profile/占位模式。

</decisions>

<canonical_refs>
## Canonical References

**Downstream agents MUST read these before planning or implementing.**

### 项目规划（仓库内，必读）
- `.planning/ROADMAP.md` §Phase 3 — 阶段目标 / 依赖（Phase 2）/ 需求映射（AUTH-01..04）/ 3 条成功标准 / `UI hint: yes`
- `.planning/REQUIREMENTS.md` — AUTH-01（登录接口照搬 Shiro session+cookie）/ AUTH-02（首页+动态菜单经 auth-sdk 调外部 spt-auth）/ AUTH-03（Shiro 链路迁入 framework）/ AUTH-04（密码 SHA-1+盐 1024 次迭代）
- `.planning/PROJECT.md` — 源架构（Web=80 BFF+UI Thymeleaf+Shiro+WebSocket 不连 DB）、版本基线（Shiro 1.8.0）、认证说明（zgbas 无用户/菜单表，经 auth-sdk HTTP 调外部 spt-auth，保持外部）、已知技术债（mock-password 后门"评估是否保留"）
- `.planning/phases/01-compile-skeleton/01-CONTEXT.md` — **D-01..D-11**（扁平 parent / 模块拓扑 D-08：admin←all / 防环 D-09 / fat-jar-only-admin）
- `.planning/phases/02-infrastructure/02-CONTEXT.md` — **D-P2-01..16**（关键：D-P2-06 framework 留给 Shiro Realm / D-P2-07 照搬保包名 / D-P2-08 pin Shiro 1.8.0 / D-P2-13 密钥环境变量外置 / D-P2-14 dev+prod profile / D-P2-16 端口 8080 root `/`）
- `CLAUDE.md` — 技术栈约束（JDK 1.8 / SpringBoot 2.5.9 / Maven 3.8.6 / zg_settings.xml）、固定 5 模块、外部 spt-auth 保持外部（决策 #7）

### 源项目（搬运参考，**绝对路径，非本仓库内**）
- `/Users/alan/WorkSpace/IDEA/zgbas`（分支 `feat-系统重构v5.0`）— 源微服务 `web` 模块（端口 80，BFF+UI）
  - `web/src/main/java/com/spt/bas/web/shiro/ShiroDbRealm.java` — **核心 Realm**（`super:` 后门 L63/L91、`authOpenFacade.login()` L115、`reLoginSso` SSO 路径、`assertCredentialsMatch` no-op）→ 照搬进 framework
  - `web/src/main/java/com/spt/bas/web/controller/LoginController.java` — 登录页 Controller（GET/POST `/login`）→ 照搬进 admin
  - `web/src/main/java/com/spt/bas/web/controller/IndexController.java` — 首页 + 菜单（调 auth-sdk）→ **stub 移植进 admin**（缺失 Phase-4 client `IPmProcessClient`/`IApproveWaitDealClient` 用 `@Autowired(required=false)` + null 守卫，业务数据裸 404，契合 D-P3-10）；`MyIndexController.java` → **延后 Phase 5**（报表契约级联，见 deferred）
  - `web/src/main/java/com/spt/bas/web/config/ShiroPropConfig.java` — `mockPassword` 配置绑定 → 照搬
  - `web/src/main/java/com/spt/bas/web/open/apply/UserOpenController.java` — SSO 入口 `ssoLogin`/`resSsoLogin`（用 `zgBas.secret`）→ 照搬进 admin
  - `web/src/main/java/com/spt/bas/web/ws/WebSocketConfig.java` / `ws/IndexWebSocketServer.java` / `ws/WebSocketServer.java` — WebSocket 端点 → 照搬进 framework/admin
  - `web/src/main/resources/templates/`（1894 模板：login.html / index.html / main.html / admin/index.html …）+ `web/src/main/resources/static/`（780 JS / 704 CSS）→ **全量照搬**进 `zgbas-admin/resources`
- `/Users/alan/WorkSpace/IDEA/spt-auth` — 外部 spt-auth（RuoYi 改造，认证 + 菜单/用户数据来源；保持外部 HTTP，不合并进单体）

### 当前单体（已就位的内联资产）
- `zgbas-common/src/main/java/com/spt/tools/shiro/**` — 已内联（Phase 2）：`ToolsShiroConfig` / `EnableToolsShiroConfig` / `AbstractShiroDbRealm` / `IShiroService` / `ShiroUtil` / `ShiroChainMetaSource` / `ChainDefinitionSectionMetaSource` / filters（`MyFormAuthenticationFilter`/`MyLogoutFilter`/`MyRolesAuthorizationFilter`/`MyPermissionsAuthorizationFilter`）/ `SsoUsernamePasswordToken`
- `zgbas-admin/src/main/java/com/spt/ZgbasApplication.java:63` — **当前 exclude `ToolsShiroConfig`**（Phase 3 首步取消）
- `zgbas-common/src/main/java/com/spt/tools/config/EnableToolsWebConfig.java` — 已 `@EnableToolsShiroConfig`（conditional on ToolsCore/Http/ShiroConfig）

### 构建 / 工具链（绝对路径）
- `/Users/alan/App/apache-maven-3.8.6` — Maven 可执行
- `/Users/alan/App/apache-maven-3.8.6/zg_settings.xml` — 私服仓库重定向 settings（构建必用）
- `/Users/alan/App/Repository` — 本地仓库（auth-sdk jar：`IAuthOpenFacade`）

</canonical_refs>

<code_context>
## Existing Code Insights

> zgbas-plus 当前为 **Phase 2 完成态**：spt-tools 全量内联（common/com.spt.tools.*）、双 ORM 单 DataSource、外部 SDK 3 bean 注入、nacos 删除、配置收敛 application.yml + dev/prod profile。Shiro 配置源码已内联但被 `ZgbasApplication` exclude（ dormant），Phase 3 把它激活 + 接 Realm。

### Reusable Assets
- **已内联 spt-tools-shiro**（`zgbas-common/com.spt.tools.shiro.*`）：`ToolsShiroConfig`（自动配置，待 un-exclude）、`AbstractShiroDbRealm`（Realm 基类）、`MyFormAuthenticationFilter`（拦截登录 POST）、`MyLogoutFilter`、`ShiroChainMetaSource`/`ChainDefinitionSectionMetaSource`（DB 动态链）、`ShiroUtil`、`SsoUsernamePasswordToken` —— 全部可直接复用，无需重写。
- **外部 `IAuthOpenFacade` bean**（Phase 2 EXT-01 已 `@Bean init(secretKey,appCode,url)` HTTP 注入）：Realm 直接 `@Autowired` 即可调外部 spt-auth（login/findUserByLoginName/findRoleByUserId/findEnterpriseById/findAppByCode）。
- **Phase 2 配置基线**：`application.yml` + `application-dev.yml` + `application-prod.yml`，密钥环境变量外置（`DB_PASSWORD`/`SPT_APP_SECRET`）—— Phase 3 在此加 `mockPassword`/`zgBas.secret`/auth URL 占位。

### Established Patterns
- **照搬保包名**（D-P2-07）：源 `com.spt.bas.web.*` 包名 verbatim，最小化 import 改动。
- **framework vs admin 边界**（D-P2-06 + D-08）：infra/Realm/Filter → `zgbas-framework`；web Controller/模板/静态 → `zgbas-admin`。
- **密钥环境变量外置**（D-P2-13）：敏感项 `${ENV:}` 占位 + 真实值不入 git；dev profile 留默认值，prod 全占位。
- **逐层 compile 绿灯**（Phase 1 gotcha 级联教训）：Shiro 接线后 `mvn compile` 全模块零错再继续。

### Integration Points
- `ZgbasApplication` ← 取消 `ToolsShiroConfig.class` exclude（L63）。
- `zgbas-framework` ← `ShiroDbRealm`（@Component）+ `WebSocketConfig`/`WebSocketServer`（照搬）。
- `zgbas-admin` ← `LoginController` / `IndexController` / `MyIndexController` / `UserOpenController`(SSO) + `templates/` + `static/`（全量照搬）+ Thymeleaf 依赖。
- `application.yml` ← 加 `zgBas.secret` / `mockPassword` / ShiroPropConfig 字段（dev 默认值 + prod 占位）。
- 外部 spt-auth ← Realm 经 `IAuthOpenFacade` HTTP 调用（保持外部，决策 #7）。

</code_context>

<specifics>
## Specific Ideas

- 用户全程选"行为等价优先 / 照搬"：Mock 后门保留可用、前端全量搬运、SSO+WS 照搬、过滤器链照搬 DB 动态链 —— 与项目核心价值"搬运而非重造 + 行为对齐旧系统"完全一致。
- 用户主动指令"搬迁所有静态文件及所有 html"，把 Phase 3 从"login+首页+菜单子集"升级为"web 前端全量 UI 面照搬"，业务页模板提前就位让 Phase 4 只需补 Controller。
- 验收仍保守（启动验证为主，同 Phase 2），真实登录 e2e 留 Phase 7 —— 用户接受"本期不证真实集成"。

</specifics>

<deferred>
## Deferred Ideas

- **业务 Service / Controller / BFF 逻辑** → Phase 4（BIZ-01..03；本期只搬前端模板，业务页后端 Controller 未迁，点业务菜单数据接口裸 404）
- **WebSocket 推送业务发送方** → Phase 4（本期搬 WS 端点骨架，WS 先连上闲置）
- **真实浏览器登录 e2e / 行为对齐** → Phase 7（ALIGN-01/02）
- **Shiro 过滤器链 DB 表存在性** → ✅ 已澄清（pattern-mapping 2026-07-16）：链由静态规则（`/login=authc` / `/logout=logout` / `/**=user`）+ 运行期经 `ShiroService.initMenu()` → `authOpenFacade.findAllMenu()` HTTP 调外部 spt-auth 取菜单构建，**无 DB 表、无需 DDL**，`ddl-auto=none`（D-P2-02）无影响。非 blocker。
- **MyIndexController（`/my/index` 报表仪表盘）** → Phase 5（REPORT-01/02）。pattern-mapping 发现其硬依赖 Phase 5 报表契约级联（`IRptIndexReportClient` + `WorkBenchCache` + ~15 report VO），照搬会启动期 `NoSuchBeanDefinitionException` 破坏 D-P3-13。用户 2026-07-16 确认延后；`IndexController`（`/index` 首页+菜单）保留本期 **stub 移植**（缺失 Phase-4 client 用 `@Autowired(required=false)` + null 守卫，业务数据降级裸 404，契合 D-P3-10）。
- **CR-01 真实轮换已泄漏生产库密码** → 跨阶段安全债（outward-facing），仍 deferred
- **53 套报表 / 64 xxl-job handler / basWx** → Phase 5 / Phase 6 / v2（与本期无关）

### Reviewed Todos (not folded)
- `phase4-inprocess-contract-http-proof.md`（Phase 4 — WR-02 接口契约 HTTP proof）→ Phase 4，非 Phase 3 范围。
- `phase4-resolve-entity-schema-drift.md`（Phase 4 — D-P2-02 ddl-auto=validate 暴露的 239 实体 schema drift）→ Phase 4，非 Phase 3 范围。
- `rotate-leaked-prod-credentials.md`（CR-01 轮换已泄漏生产库密码）→ 跨阶段安全债，非 Phase 3 范围。
  - 3 条均弱匹配（score 0.6，通用关键词），已 reviewed 不折叠。

</deferred>

---

*Phase: 3-认证首页*
*Context gathered: 2026-07-16*
