# Phase 5: 承托层迁入 - Context

**Gathered:** 2026-07-23
**Status:** Ready for planning

<domain>
## Phase Boundary

把 basWx purchase-server 全部承托类按模块边界落 `zgbas-system`(保留 `com.spt.bas.purchase.wx.server.*` 包飞地,方案1 verbatim 嵌入),为 Phase 6 service 与 Phase 7 BFF 提供稳定、无孤立 import 的编译底座。只谈 HOW 落地,不新增能力。

**交付范围(实测类数,2026-07-23 探查):**
- `payload/` 22 + `vo/` 18(含非 VO 的 Dto) + `util/` 19 + `common/` 8 + `cache/` 2(BsDictUtil/RedisCache) + `aop/` 1(ServiceAop) + `ewechat/` 1(EweChatApi) + `enums/` 1(MessageEnums) + `exception/` 3(GlobalExceptionHandler/SecurityException/UserNameOrPasswordException) ≈ **75 核心承托类**
- `config/` 18 文件,**Phase 5 实际迁入 ~8–9**(见 D-P5-07 对账:P4 已迁 7 / D-07 不迁 2 / Phase 7 迁 1)
- 启动接线:`listener/`(ApplicationStartup 并入 / RequestListener 迁入)+ `command/PurchaseCommand`(迁入)

**不在此阶段:**
- Phase 6:`~20 service impl + interface`(含物理位于 `service/impl/` 的 JinXinApi —— 归属待 researcher 定,见 D-P5-08)
- Phase 7:`controller/` 12 + `api/` 4 + `config/BasicErrorController`
- xxl-job 残留:`task/SignContractTask`(3 @XxlJob)+ `config/WxJobConfig` —— 延后 v1.3 quartz gap-closure(见 D-P5-06/D-P5-11)
- 开发工具:`task/TestJob`、`tools/DBDocTool` —— 丢弃(见 D-P5-12)

</domain>

<decisions>
## Implementation Decisions

> 沿用上游已锁定决策(STATE.md D-01~D-19 + 方案1):verbatim 嵌入 / 保留 `purchase.wx.*` 包飞地 / 承托→zgbas-system / 外部集成维持 HTTP 边界只迁 wrapper(D-16/D-17) / D-P4 明文密钥 / D-15a·b inventory checklist 必做 / 层序 承托(本阶段)→service(P6)→edge(P7)。下列为 Phase 5 新增决策。

### 灰区 A — 全局横切 bean 冲突收口

verbatim 全迁 4 个全局横切 bean 会启动崩溃(`multipartConfigElement` @Bean 重名),必须逐 bean 判定。

- **D-P5-01 WebAppConfig 剥减不 verbatim:** 不整体迁入 `com.spt.bas.purchase.wx.server.config.WebAppConfig`。单体已有 `zgbas-system/src/main/java/com/spt/bas/server/config/WebAppConfig.java`(已含 PageInterceptor / `/`→`/index` / Jackson+Form+ByteArray+String 转换器 / characterEncodingFilter / `multipartConfigElement` @Bean 10MB+100MB)。WX WebAppConfig 与之**唯一差异是 CORS `/**`**(单体无)。落地:把 CORS 配置并入单体已有 WebAppConfig,或新建一个**只含 CORS** 的 WX-only `WebMvcConfigurer`;**不重复** `multipartConfigElement` @Bean(重名触发 BeanDefinitionStoreException)/ PageInterceptor / 转换器。
- **D-P5-02 GlobalExceptionHandler 限定 basePackages:** 迁入但加 `@ControllerAdvice(basePackages = "com.spt.bas.purchase.wx.server")`。单体当前**无任何** @ControllerAdvice;不限 basePackages 会让全局 `Exception.class → WX ApiResult` 改写主域 bas 控制器的错误响应(行为不等价)。限定后仅 WX 控制器走 ApiResult 错误信封,与源系统行为一致。
- **D-P5-03 ServiceAop 迁入 + 显式注册 bean:** 迁入 `.../aop/ServiceAop`。其 pointcut `execution(* com.*.*.*.*.*.service..*.*(..))`(5 段通配 = 7 段路径)自限到 `com.spt.bas.purchase.wx.server.service`,**不命中**主域 `com.spt.bas.server.service`(6 段);与 spt-tools `com.spt.tools.aop.interceptor.ServiceAop`(4 段 + `@ServiceLogAop`/`@ServiceExceptionAop` 注解驱动)pointcut 不重叠 → 无双增强。源类只有 `@Aspect` 无 `@Component`,须显式注册 bean —— 参照 spt-tools `zgbas-common/.../spt/tools/aop/config/ToolsAopConfig.java`(+`EnableToolsAopConfig`)模式新建一个 AopConfig。
- **D-P5-04 TransactionConfig 跳过:** 不迁。源文件 `.../config/TransactionConfig.java` 全注释死码(仅 `package` 声明 + 注释块,无任何 @Bean)。单体已用 `JpaTransactionManager` @Primary(D-P2),无需第二个 TransactionManager。

### 灰区 B — 外部密钥 & config 对账

- **D-P5-05 新增 WX 外部密钥明文落 dev yml:** EweChat `corpid`/`corpsecret`、JinXin key(s)、OCR `host`+`key` 以**明文实测值**直进 `zgbas-admin/src/main/resources/application-dev.yml`(用户决策,与 D-P4 明文策略一致,拒绝外置 env)。⚠️ `rotate-leaked-prod-credentials`(high-priority todo)仍跟进 —— 本决策把新真实密钥写入 git,加重已泄漏密钥债务,须在 todo 中显式登记 EweChat/JinXin/OCR。EweChat corpid/corpsecret 当前在 `EweChatApi` URL 模板 `https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid={0}&corpsecret={1}` 由参数传入 —— researcher 须查清 `{0}{1}` 实际来源(`EweChatConfig` @ConfigurationProperties)后明文化。
- **D-P5-06 WxJobConfig 跳过:** 不迁。`@Component XxlJobSpringExecutor` 读 `xxl.job.*`,单体已删 xxl-job(v1.0 #10 改 RuoYi quartz),无法编译/运行。延后 v1.3 quartz gap-closure。
- **D-P5-07 config/ 对账(18 → Phase 5 ~8–9):** P4 已迁 7(`FastJson2JsonRedisSerializer`/`JwtAuthenticationFilter`/`JwtConfig`/`RedisConfig`/`WxConfiguration`/`WxMiniAppConfig` + 新建 `WxSecurityConfig`);D-07 不迁 2(`CustomConfig`/`IgnoreConfig`,FilterRegistrationBean 已替代);Phase 7 迁 1(`BasicErrorController`→zgbas-admin);Phase 5 实际迁入:① `JinXinConfig` ② `EweChatConfig` ③ `OcrConfig` ④ `FrameworkConfig` ⑤ `WebAppConfig`(剥减,见 D-P5-01) ⑥ `ScheduleConfig`(`@EnableScheduling`+调度线程池,需查单体是否已有调度器防重复) + 待定 `SwaggerConfig`(单体是否已有 Swagger)。`TransactionConfig` 死码跳过(D-P5-04)、`WxJobConfig` 跳过(D-P5-06)。**researcher 须出最终 config 清单写入 inventory(D-15b)。**
- **D-P5-08 外部 wrapper 维持 HTTP 边界:** `EweChatApi` / `OcrUtils`+`OcrHelper`(用 `HttpUtils.doPost`+固定 URL)迁本地 wrapper,远端服务维持 HTTP 边界(D-16/D-17 沿用),保持原调用方式。`JinXinApi` **物理位于 `service/impl/`**(被 ROADMAP WX-SERVICE-01 列为 service)—— researcher 须定 Phase 5(随承托外部 wrapper)vs Phase 6(随 service 层)归属;其职责是外部 HTTP wrapper,倾向 Phase 5。

### 灰区 C — 启动接线 & 模糊包归属

- **D-P5-09 ApplicationStartup 并入单体防双跑:** 单体已有 `zgbas-system/src/main/java/com/spt/bas/server/listener/ApplicationStartup.java`(`ApplicationReadyEvent` 监听,已 `DictUtil.init` + 起 `CommandExecutor` 线程)。WX `ApplicationStartup` **不 verbatim 迁**(否则第二个 `@Component ApplicationReadyEvent` 监听 → 同 `CommandExecutor` 队列起两线程竞态/重复执行 + `DictUtil.init` 双调)。落地:把 WX 独有的 `BsDictUtil.init()` 追加到**单体已有** ApplicationStartup(+ `PurchaseCommand` 注册到现有 CommandExecutor 队列)。直击 Phase 3 登录缺口同源(`/wx/*` 字典缓存初始化)。
- **D-P5-10 RequestListener 迁入:** 迁入 `.../listener/RequestListener`(`@Component ServletRequestListener`,`requestDestroyed → UserContext.removeUser`)。UserContext ThreadLocal 清理必需,防内存泄漏。`@Component` 自注册,researcher 须确认单体无同名 listener 重名。
- **D-P5-11 SignContractTask 延后 v1.3:** `task/SignContractTask`(3 `@XxlJob`:doSuccessContract/doSuccessDebtCertificate/doReceiveGood)延后 v1.3 quartz gap-closure。xxl-job 已删,`@XxlJob` 注解类不在 classpath,verbatim 迁会编译失败。STATE 已列 "quartz gap-closure(28 handler 路由)" 为 v1.3 pending。
- **D-P5-12 TestJob / DBDocTool 丢弃:** `task/TestJob`、`tools/DBDocTool` 为开发/文档工具类,非 runtime 承托,不迁。
- **D-P5-13 PurchaseCommand 归 Phase 5:** `command/PurchaseCommand` 归 Phase 5(被 D-P5-09 的 CommandExecutor 消费,属启动接线承托)。

### 预置 stub 填实(planner 细节,记录在案)

- **D-P5-14 P4 预置 stub 用源实测实现替换:** Phase 4 预置 stub(`SecurityException`/`ResponseUtil`/`UserInfoVo`/`UserContext`/`ApiResult`/`BaseException`,均在 `com.spt.bas.purchase.wx.server.*` 飞地)用源实测实现**替换** stub,不保留 stub 形态。

### Claude's Discretion

- `ScheduleConfig`(`@EnableScheduling`+调度线程池)是否与单体已有调度器重复 —— researcher 查单体现有 `@EnableScheduling`/TaskScheduler 后定并入/新建。
- `SwaggerConfig` 是否迁 —— 取决于单体是否已有 Swagger 配置;若已有则不重复迁。
- `JinXinApi` 归 Phase 5 vs Phase 6 —— researcher 依物理位置(service/impl)与职责(外部 HTTP wrapper)定。

### Folded Todos

(无 —— cross-reference 命中的 2 个 todo 均为 v1.0/v1.3 遗留,非 Phase 5 承托层 scope,未 fold。见 `<deferred>` Reviewed Todos。)

</decisions>

<canonical_refs>
## Canonical References

**Downstream agents MUST read these before planning or implementing.**

### 需求 / Roadmap / 状态
- `.planning/ROADMAP.md` §Phase 5 — Goal + Success Criteria(4 条验收:全量迁入无孤立 import / 外部 wrapper HTTP 边界 / `mvn compile -pl zgbas-system` 零错 / D-15a·b inventory checklist)
- `.planning/REQUIREMENTS.md` — **WX-BFF-03**(辅助组件 payload/VO/util/common/config/AOP/cache/EweChatApi/GlobalExceptionHandler 全量迁入)
- `.planning/STATE.md` — 方案1 锁定 + 沿用 D-01~D-19(尤其 D-04/05/06 模块与包策略、D-15a/b inventory、D-16/17 外部边界、D-18/19 复用 P4 wiring、D-14a/b listener 判定)
- `.planning/phases/04-sdk/04-CONTEXT.md` — Phase 4 决策(Redis/JWT/WxMaService wiring、D-P4 明文密钥、P4 预置 stub 清单、CustomConfig/IgnoreConfig 不迁 D-07)
- `.planning/phases/03-feign/03-CONTEXT.md` — Phase 3 决策(包飞地策略、EntityScan/EnableJpaRepositories 扩包)

### 源码(basWx purchase-server,212 文件)
- `/Users/alan/WorkSpace/IDEA/zgbas/basWx/purchase-server/src/main/java/com/spt/bas/purchase/wx/server/` — 承托包根(payload/vo/util/common/config/cache/aop/ewechat/enums/exception/listener/command)
- 关键源文件(冲突/启动接线):
  - `.../config/WebAppConfig.java` — CORS `/**` 是唯一独有(D-P5-01)
  - `.../exception/GlobalExceptionHandler.java` — `@ControllerAdvice` catch `Exception.class`(D-P5-02 限 basePackages)
  - `.../aop/ServiceAop.java` — pointcut `com.*.*.*.*.*.service..`(D-P5-03)
  - `.../config/TransactionConfig.java` — 全注释死码(D-P5-04)
  - `.../listener/ApplicationStartup.java` — DictUtil.init + BsDictUtil.init + CommandExecutor 起线程(D-P5-09 并入单体)
  - `.../listener/RequestListener.java` — UserContext.removeUser 清理(D-P5-10)
  - `.../ewechat/EweChatApi.java` — corpid/corpsecret URL 模板 `{0}{1}`(D-P5-05)
  - `.../util/OcrUtils.java` + `OcrHelper.java` — `HttpUtils.doPost`+`OcrConfig`(D-P5-08)
  - `.../config/WxJobConfig.java` + `.../task/SignContractTask.java` — xxl-job 残留(D-P5-06/D-P5-11 延后 v1.3)

### 关键单体文件(冲突/合并参照)
- `zgbas-system/src/main/java/com/spt/bas/server/config/WebAppConfig.java` — 已有 WebAppConfig(CORS 合并目标,D-P5-01)
- `zgbas-system/src/main/java/com/spt/bas/server/listener/ApplicationStartup.java` — 已有启动监听器(合并目标,D-P5-09)
- `zgbas-common/src/main/java/com/spt/tools/aop/interceptor/ServiceAop.java` — 已有 aspect(pointcut 不重叠佐证,D-P5-03)
- `zgbas-common/src/main/java/com/spt/tools/aop/config/ToolsAopConfig.java`(+`EnableToolsAopConfig.java`)— aspect bean 注册模式(D-P5-03 新建 AopConfig 参照)
- `zgbas-common/src/main/java/com/spt/tools/core/cmd/CommandExecutor.java` — executor 线程(D-P5-09 防双跑)
- `zgbas-common/src/main/java/com/spt/tools/core/config/ToolsCoreConfig.java` — core wiring
- `zgbas-admin/src/main/java/com/spt/web/config/TemplateHelperConfig.java` — 模板辅助 bean(Phase 3 登录缺口同源)
- `zgbas-admin/src/main/resources/application-dev.yml` — WX 外部密钥明文落点(D-P5-05)

</canonical_refs>

<code_context>
## Existing Code Insights

### Reusable Assets
- 单体 `bas.server.config.WebAppConfig` 已覆盖 multipart/转换器/PageInterceptor/`/`→`/index` —— WX WebAppConfig 仅需贡献 CORS(D-P5-01),其余复用。
- 单体 `bas.server.listener.ApplicationStartup` 已就位 DictUtil.init + CommandExecutor —— WX 启动逻辑只追加 `BsDictUtil.init()`(D-P5-09)。
- P4 预置 stub(`ApiResult`/`UserContext`/`ResponseUtil`/`UserInfoVo`/`SecurityException`/`BaseException`)已在飞地 —— D-P5-14 用源实测实现替换。
- `HttpUtils.doPost`(单体已有,spt-tools http)—— OcrUtils/EweChatApi 外部调用复用同一 HTTP 工具(D-P5-08)。
- spt-tools `ToolsAopConfig`/`EnableToolsAopConfig` —— WX ServiceAop 显式注册 bean 的模式模板(D-P5-03)。

### Established Patterns
- 包飞地:`com.spt.bas.purchase.wx.server.*` verbatim 保包名(方案1 / D-06),承托类与单体 bas 类平级共存于 zgbas-system。
- 明文密钥进 `application-dev.yml`(D-P4)—— D-P5-05 沿用,WX 新密钥明文化。
- `@ControllerAdvice` / `@Aspect` / `WebMvcConfigurer` 全局 bean 须防重名/双注册 —— 本阶段 4 个横切 bean 逐项收口(灰区 A)。

### Integration Points
- `ZgbasApplication`(`@SpringBootApplication`)—— @ComponentScan 自动扫入 WX `@Component`(@Aspect 除外,需显式 @Bean,D-P5-03)。
- D-15a·b inventory checklist(SC#4)五类预映射,researcher/planner 据此出 checklist:
  - **return envelope** → `ApiResult`(D-P5-14 填实);单体另有 `bas.client.common.ApiResult`(WX 控制器用 WX 版,不合并)
  - **exception advice** → `GlobalExceptionHandler`(D-P5-02 限 basePackages);单体当前无 @ControllerAdvice
  - **user-context** → `UserContext`(ThreadLocal<UserInfoVo>)+ `UserInfoVo`(D-P5-14)+ `RequestListener` 清理(D-P5-10);与 Shiro+SysUser 主域身份 genuinely 分离
  - **auth helper** → `UserHelper` + `JwtUtil`(P4 已迁);无其他 pending auth helper
  - **serialization-upload-wrapper** → `FastJson2JsonRedisSerializer`(P4 已迁)+ `UploadHelper` + 单体已有 `MultipartConfigElement` + `configureMessageConverters`

</code_context>

<specifics>
## Specific Ideas

- 用户对灰区 B 明确选**明文实测值直进 dev yml**(覆盖 Claude 推荐的占位符方案)—— 与 D-P4 一致性优先于"避免新增密钥进 git"。rotate-credentials todo 须显式登记 EweChat/JinXin/OCR 三组新密钥。
- 灰区 A/C 用户选推荐项(逐 bean 最小消歧 / 并入单体防双跑)—— 行为等价优先于 verbatim 字面。

</specifics>

<deferred>
## Deferred Ideas

- `config/WxJobConfig` + `task/SignContractTask`(xxl-job 残留)→ v1.3 quartz gap-closure(D-P5-06/D-P5-11)
- `task/TestJob` + `tools/DBDocTool`(开发工具)→ 丢弃(D-P5-12)
- JWT/Shiro 认证统一 → future(身份模型分离,方案1 不做)
- basWx Feign 自回环崩为直注 → future(方案1 保留)
- JinXinApi 归属 Phase 5 vs Phase 6 → researcher 定(D-P5-08)
- ScheduleConfig / SwaggerConfig 是否与单体重复 → researcher 定(Claude's Discretion)

### Reviewed Todos (not folded)
- `rotate-leaked-prod-credentials`(high)—— v1.0 遗留;Phase 5 新增 EweChat/JinXin/OCR 明文密钥后**更需关注**,但密钥轮换本身非承托层 scope,留 todo 跟进。
- `phase4-resolve-entity-schema-drift`(medium)—— v1.0/v1.2 遗留(ddl-auto validate),与承托层迁入无关,留 v1.3。

</deferred>

---

*Phase: 5-承托层迁入*
*Context gathered: 2026-07-23*
