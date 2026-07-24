# Phase 5: 承托层迁入 — Research

**Researched:** 2026-07-23 (inline — GSD subagent layer unavailable in this env; see orchestrator note)
**Status:** RESEARCH COMPLETE
**Requirement:** WX-BFF-03
**Source tree:** `/Users/alan/WorkSpace/IDEA/zgbas/basWx/purchase-server/src/main/java/com/spt/bas/purchase/wx/server/`

> 本文件由 plan-phase orchestrator 直接产出(非 gsd-phase-researcher 子代理),因子代理 spawn 在本环境统一失败(`[1211] 模型不存在`)。所有结论均有 file:line 证据,可独立复核。

---

## 背景与方法

Phase 5 是 **verbatim 嵌入迁移**(方案1),目标:把 basWx purchase-server 的承托类(payload/vo/util/common/config/cache/aop/ewechat/enums/exception/listener/command)按模块边界落 `zgbas-system`,保 `com.spt.bas.purchase.wx.server.*` 包飞地,为 Phase 6 service 与 Phase 7 BFF 提供无孤立 import 的编译底座。CONTEXT.md 的 3 个灰区(D-P5-01..14)已锁定;本研究只解决 CONTEXT 显式交给 researcher 的 7 个未决项 + 探查中新发现的 4 个收口点。

源承托类实测计数:payload 22 / vo 18 / util 19 / common 8 / config 18 / cache 2 / aop 1 / ewechat 1 / enums 1 / exception 3 / listener 2 / command 1。

---

## Q1 — ScheduleConfig 是否与单体重复?→ **SKIP(不迁)**

**证据:**
- 源:`config/ScheduleConfig.java:12-28` — `@EnableScheduling` + `SchedulingConfigurer`,`@Bean Executor taskExecutor() = Executors.newScheduledThreadPool(100)`,含注释掉的 cron 示例。
- 单体:`zgbas-system/.../bas/server/config/ScheduleConfig.java:11-23` — **完全同构**:`@EnableScheduling` + `SchedulingConfigurer` + `@Bean Executor taskExecutor() = newScheduledThreadPool(10)`。
- 单体另有 `@EnableScheduling` 于 `zgbas-framework/.../web/ws/WebSocketServer.java:29` 与 `IndexWebSocketServer.java:25`。

**判定:** 不迁。两份 `ScheduleConfig` 都注册同名 `@Bean taskExecutor` → verbatim 迁入触发 `BeanDefinitionStoreException`(duplicate bean `taskExecutor`)。且承托层无任何 `@Scheduled` 方法(WX 的定时逻辑全在 `task/SignContractTask` 的 `@XxlJob`,已 D-P5-11 延后 v1.3)。单体 pool=10 已服务 quartz+websocket 调度,行为等价。

**→ 新决策 D-P5-16:** `config/ScheduleConfig` 跳过(单体已有同构类,迁入即重名崩溃)。

---

## Q2 — SwaggerConfig 是否迁?→ **SKIP(不迁)**

**证据:**
- 源:`config/SwaggerConfig.java` 全文基于 springfox(`import springfox.documentation.*`、`@EnableSwagger2`、`Docket`、`@ConditionalOnProperty(name="swagger.enable", havingValue="true")`、basePackage 扫 `...wx.server.controller`)。
- 源 pom:`purchase-server/pom.xml:107-110` 声明 `io.springfox:springfox-swagger2` + `swagger-bootstrap-ui` + `io.swagger:swagger-annotations`。
- 单体 pom(zgbas-admin/system/framework/common + 聚合):**无任何 springfox/springdoc**(`grep -iE "springfox|springdoc|swagger"` 在 `*.java` 源仅命中 main.html 更新日志 + easyui icon.css 的 `.swagger` 类,均无关)。
- 承托代码本身已不依赖 swagger:`common/ApiResult.java:5-6` 与 `vo/UserInfoVo.java:4-5` 的 `io.swagger.annotations.*` import **全被注释**。
- SwaggerConfig 扫描的 `...controller` 包要到 Phase 7 才存在。

**判定:** 不迁。springfox 不在单体 classpath → verbatim 迁入即编译失败(unresolved `springfox.*`)。承托代码不引用 swagger 注解;控制器(其扫描目标)Phase 7 才落位;`@ConditionalOnProperty swagger.enable` 默认关闭。API 文档需求若将来出现,应作为独立未来任务(先加 springfox 依赖再迁配置)。

**→ 新决策 D-P5-17:** `config/SwaggerConfig` 跳过(springfox 不在单体 classpath,迁入编译失败;承托层无 swagger 依赖)。

---

## Q3 — JinXinApi 归 Phase 5 还是 Phase 6?→ **Phase 6**(JinXinConfig 留 Phase 5)

**证据:**
- 位置:`service/impl/JinXinApi.java`(物理在 service 层,ROADMAP WX-SERVICE-01 列为 service)。
- 依赖(`service/impl/JinXinApi.java:3-35`):
  - `cfca.etl.common.client.*`(JVerifyRequest/JVerifyWrapper/Base64ImgUtil)+ `cfca.uaclient.UAClient` — **金信 CFCA UA 客户端 SDK**(与单体现有 cfca sign client `EXT-03` 是不同 artifact,需单独核 classpath)。
  - `com.spt.bas.client.remote.IApiRequestHisClient` + `ApiRequestHis`/`SyncData` — 主域 bas Feign 自回环客户端。
  - WX 承托/vo:`JinXinConfig`、`CommonUtil`、`RsaUtil`、`SignUtil`、`JinXinAuthFaceVo`(均 Phase 5 迁)。
  - `@EnableConfigurationProperties` + `extends CommonUtil` + `@Component`。
- **唯一调用方:** `service/impl/UserInfoService.java:120` `private JinXinApi jinXinApi;`(Phase 6 service)。无任何承托类/controller 直接调它。

**判定:** JinXinApi 归 **Phase 6**(随其唯一消费者 UserInfoService)。理由:① 唯一调用方是 Phase 6 service,Phase 5 无消费者;② 物理在 `service/impl/`;③ CFCA `cfca.etl`/`cfca.uaclient` artifact 需在 Phase 6 起编译前确认已上单体 classpath(Phase 6 前置核验项)。**JinXinConfig**(`@ConfigurationProperties(prefix="jinxin")` 纯 POJO,无消费者也可先就位)留 **Phase 5**(config inventory 一部分)。

**→ 修正 D-P5-08 落点:** JinXinConfig → Phase 5;JinXinApi → Phase 6(Phase 6 前置:核 `cfca.etl`/`cfca.uaclient` jar 在 classpath)。

---

## Q4 — EweChat / JinXin / OCR 密钥实际来源与值 → 前缀 + 实测值已查清

**EweChatConfig 绑定机制(关键):**
- `config/EweChatConfig.java` 是纯 `@Data` POJO,**无 `@ConfigurationProperties`、无 `@Component`** —— 与 `JinXinConfig`(prefix `jinxin`)、`OcrConfig`(prefix `aliyun.ocr`)不同。
- 绑定来自 `config/FrameworkConfig.java:78-82`:`@Bean @ConfigurationProperties(prefix = "ewechat.config") public EweChatConfig eweChatConfig()`。所以 yml 前缀 = **`ewechat.config`**,字段 `corpid`/`corpsecret`/`agentid`(EweChatConfig.java:16-26)。
- `EweChatApi.java:67` `MessageFormat.format(ACCESS_TOKEN_URL, eweChatConfig.getCorpid(), eweChatConfig.getCorpsecret())` 确认 corpid/corpsecret 来自该 bean。

**实测值(D-P5-05 明文落 application-dev.yml):**

| 前缀.键 | 实测值 | 来源文件:行 |
|---|---|---|
| `ewechat.config.corpid` | `ww67315c53a72c991f` | application-dev.properties:16 |
| `ewechat.config.corpsecret` | `gn_IIS0Sv-dvJ9MnGwZSH0m_zVZ_FCHk0mEIVMS6iZs` | application-dev.properties:17 |
| `ewechat.config.agentid` | `1000007` | application-dev.properties:18 |
| `ewechat.config.messageUrl` | `http://h5biztest.tosupply.cn/dev-api/initLogin` | application-dev.properties:19 |
| `aliyun.ocr.appcode` | `52e1a1531c5a4e30969d93c16eb1251f` | config.properties:43 |
| `aliyun.ocr.host` | `https://cardpack.market.alicloudapi.com` | config.properties:44 |
| `aliyun.ocr.businessLicenseUrl` | `/rest/160601/ocr/ocr_business_license.json` | config.properties:46 |
| `aliyun.ocr.idCardUrl` | `/rest/160601/ocr/ocr_idcard.json` | config.properties:48 |
| `aliyun.ocr.vehicleUrl` | `/rest/160601/ocr/ocr_vehicle.json` | config.properties:50 |
| `aliyun.ocr.driverLicenseUrl` | `/rest/160601/ocr/ocr_driver_license.json` | config.properties:52 |
| `aliyun.ocr.householdRegisterUrl` | `/api/predict/ocr_household_register` | config.properties:54 |
| `aliyun.ocr.bankCardUrl` | `/rest/160601/ocr/ocr_bank_card.json` | config.properties:56 |
| `aliyun.ocr.passportUrl` | `/rest/160601/ocr/ocr_passport.json` | config.properties:58 |
| `jinxin.host` | `https://210.74.42.15:443` | application.properties:69 |
| `jinxin.livingUrl` | `/UAServer/Authenticate` | application.properties:70 |
| `jinxin.merchNo` | `0000000000036500` | application.properties:72 |
| `jinxin.merchKey` | `LaYeIemv2WEY8Z7h` | application.properties:74 |

> ⚠️ `jinxin.merchRsaPrivateKey`/`keyStorePath`/`keyStorePassword`/`trustStorePath`/`trustStorePassword`/`connectTimeout`/`readTimeout`/`tmpFilePath`(JinXinConfig.java:23-51)未在 head 窗口露出 —— executor 落 yml 时须从 `application.properties:71-85` 区段补全(证书路径类字段,可能为相对路径)。

> 🔒 **安全债:** 以上为真实生产密钥。D-P4 + D-P5-05 用户锁定"明文进 dev yml"(覆盖安全铁律)。`rotate-leaked-prod-credentials` todo 须**新增登记** EweChat corpid/corpsecret、Aliyun OCR appcode、JinXin merchKey 三组。

> 单体现 `application-dev.yml` 仅有 `datasource:`(行2),**未含** ewechat/jinxin/aliyun.ocr/spt.app.* —— Phase 5 须 ADD 上述 WX 专用键。`spt.app.appCode`/`secretKey`/`auth.url`/`push|file.server.url` 由 FrameworkConfig 外部 bean 消费,但那些 bean 按 D-P5-15 全 DROP(单体已自有),故 **Phase 5 只需 ADD ewechat.config.*/aliyun.ocr.*/jinxin.***。

---

## Q5 — 最终 config 清单(D-15b / D-P5-07)— 18 文件对账

| # | 源 config 文件 | 归属 | 依据 |
|---|---|---|---|
| 1 | FastJson2JsonRedisSerializer | ✅ P4 已迁 | enclave 已存在 |
| 2 | JwtAuthenticationFilter | ✅ P4 已迁 | enclave 已存在 |
| 3 | JwtConfig | ✅ P4 已迁 | enclave 已存在 |
| 4 | RedisConfig | ✅ P4 已迁 | enclave 已存在 |
| 5 | WxConfiguration | ✅ P4 已迁 | enclave 已存在 |
| 6 | WxMiniAppConfig | ✅ P4 已迁 | enclave 已存在 |
| 7 | WxSecurityConfig | ✅ P4 新建 | enclave 已存在 |
| 8 | **JinXinConfig** | ✅ **Phase 5 迁** | `@ConfigurationProperties(prefix=jinxin)` POJO,Q3 |
| 9 | **EweChatConfig** | ✅ **Phase 5 迁** | `@Data` POJO,经 FrameworkConfig @Bean 绑定(prefix ewechat.config),Q4 |
| 10 | **OcrConfig** | ✅ **Phase 5 迁** | `@ConfigurationProperties(prefix=aliyun.ocr)` POJO |
| 11 | **FrameworkConfig** | ⚠️ **Phase 5 迁(剥减,D-P5-15)** | 仅保留 `eweChatConfig()` @Bean,余 DROP |
| 12 | **WebAppConfig** | ⚠️ **Phase 5 迁(剥减,D-P5-01)** | 仅贡献 CORS,并入单体 WebAppConfig |
| 13 | ScheduleConfig | ❌ **跳过(D-P5-16)** | 单体已有同构,Q1 |
| 14 | SwaggerConfig | ❌ **跳过(D-P5-17)** | springfox 不在 classpath,Q2 |
| 15 | TransactionConfig | ❌ **跳过(D-P5-04)** | 全注释死码 |
| 16 | WxJobConfig | ❌ **跳过(D-P5-06)** | xxl-job 已删 |
| 17 | CustomConfig | ❌ **跳过(D-07)** | FilterRegistrationBean 已替代;且 FrameworkConfig 剥减后不再 @Bean 它 |
| 18 | IgnoreConfig | ❌ **跳过(D-07)** | 同上,FilterRegistrationBean 已替代 |
| — | BasicErrorController | ➡️ **Phase 7 迁** | ROADMAP Phase 7 |

**Phase 5 实际迁入 config = 5(JinXinConfig/EweChatConfig/OcrConfig/FrameworkConfig[剥减]/WebAppConfig[剥减])**。CONTEXT 估的 ~8–9 偏高;探查后收口为 5(其中 2 个是剥减迁入)。

### 新决策 D-P5-15 — FrameworkConfig 剥减(非 verbatim)

`FrameworkConfig.java` verbatim 迁入会与单体既有 bean **重名崩溃**。证据:单体大量注入 `IAuthOpenFacade`/`FileRemote`/`ThreadPoolTaskExecutor`(20+ controller,如 `IndexController.java:58`、`FileController.java:45`、`LogUtil.java:41`),证明这些 bean **单体已自有**。

FrameworkConfig 6 个 @Bean 处置:
| @Bean | 处置 | 理由 |
|---|---|---|
| `eweChatConfig()` prefix `ewechat.config` | ✅ **保留** | WX 专用,单体无 |
| `authOpenFacade()` (AuthOpenFacade/spt-auth) | ❌ DROP | 单体已自有 IAuthOpenFacade bean |
| `fileRemote()` (FileRemote/spt-file) | ❌ DROP | 单体已自有 |
| `pushClientHttp()` (PushClientHttp/spt-push) | ❌ DROP | 单体已自有(CLAUDE #7 外部 bean 维持原注入) |
| `threadPoolTaskExecutor()` | ❌ DROP | 单体已自有(LogUtil 取它) |
| `dataSourceConfig()` prefix `purchase.datasource` | ❌ DROP | 单体已用 JPA DataSource(D-P2),二数据源冲突 |
| `customConfig()` prefix `custom.config` | ❌ DROP | CustomConfig 不迁(D-07) |

**落地:** 新建一个 **WX-only** `@Configuration`(如 `com.spt.bas.purchase.wx.server.config.WxCarrierConfig`),只含 `@Bean @ConfigurationProperties(prefix="ewechat.config") EweChatConfig eweChatConfig()`。不 verbatim 迁 FrameworkConfig。

---

## Q6 — 承托类逐包 inventory(SC#1)+ 孤立 import 风险

> 标记说明:✅ 纯承托(仅依赖已迁/本批类);⚠️ 依赖未迁代码(P6 service / P7 controller / 外部 jar)→ 编排风险。

### payload/ (22) — 全 ✅ 纯 POJO/请求体
ApplyIouRequest, BaseRequest, BrandRequest, CfcaRequest, CommonRequest, CompanyBaseInfoRequest, CompanyInfoRequest, ContractNoRequest, CustomRequest, DeliveryOutNoRequest, DepositRequest, EntrustRequest, FeedbackRequest, LoginRequest, PayBankInfoRequest, QuotaTestRequest, ServiceOpeningInfoRequest, SupplyInfoRequest, TempSaveRequest, UploadBase64Request, WarehouseRequest, WxLoginRequest

### vo/ (18) — ✅(UserInfoVo 见 D-P5-14 替换 stub)
ApplyIouVo, BaseVo, CfcaVo, CompanyVo, CustomVo, EweChatAccessTokenCallBackDto, IdentityCardVo, JinXinAuthFaceVo, LicenseVo, OcrLicenseVo, QuotaInfoVo, ServiceOpeningInfoVo, SupplyInfoVo, TemplateCardMessage, UploadFileVo, UserChangeVo, UserInfoVo(P4 stub→替换), ZyVehicleTrackReqVo

### util/ (19)
CommonUtil, ConvertUtils, DateUtils, DeptUtils, FreemarkerUtil, **HttpUtils**⚠️(核单体是否已有同名 spt-tools HttpUtils,可能重名), JwtUtil(P4 已迁,跳过), NumUtils, **OcrHelper**⚠️(依赖 OcrConfig+HttpUtils,Phase5 内可解), **OcrUtils**⚠️(同), ResponseUtil(P4 stub→替换), RsaUtil, SignUtil, SM3, SMSUtils, StrUtils, UploadHelper, UserContext(P4 stub→替换), UserHelper

> `HttpUtils` 风险:单体 `com.spt.tools.http.util.HTTPUtility` 已存在(EweChatApi 用它)。源 WX `util/HttpUtils.java` 若迁入需核是否与 spt-tools 重名/重复;若 WX 代码实际用的是 spt-tools HTTPUtility(见 EweChatApi:9),则 WX HttpUtils 可能是死码 → executor 核 callers,无 caller 则不迁。

### common/ (8)
ApiResult(P4 stub→替换), BasConstants, BaseException(P4 stub→替换), CardType, Constant, CustomSetting, InfoStep, Status(enclave 已存在,核 stub vs real)

### cache/ (2) — ✅
BsDictUtil(WX 版,`com.spt.bas.purchase.wx.server.cache.BsDictUtil`,与单体 `bas.server.cache.BsDictUtil` 同名不同包), RedisCache

### aop/ (1) — ServiceAop(D-P5-03 显式注册 bean)
### ewechat/ (1) — EweChatApi ⚠️ 依赖 `IBuyMessageService`(P6 service)+ RedisCache + EweChatConfig。**编译依赖 P6**:EweChatApi 注入 `IBuyMessageService`(EweChatApi.java:32)。
### enums/ (1) — MessageEnums ✅
### exception/ (3) — GlobalExceptionHandler(D-P5-02 限 basePackages), SecurityException(P4 stub→替换), UserNameOrPasswordException ✅

### ⚠️ EweChatApi 编译依赖 Phase 6 — 编排结论
`EweChatApi.java:5,32` `@Autowired IBuyMessageService messageService`。`IBuyMessageService` 是 Phase 6 service interface,**Phase 5 不迁**。因此:
- **方案:** EweChatApi **延迟到 Phase 6** 迁入(随 IBuyMessageService),或 Phase 5 迁入 EweChatApi 但接受其 import `IBuyMessageService` 暂不可解(编译失败)。
- **推荐:** EweChatApi 随 Phase 6 迁(与 JinXinApi 同理:依赖 P6 service)。Phase 5 只迁 EweChatApi 的承托依赖(EweChatConfig/RedisCache/TemplateCardMessage/EweChatAccessTokenCallBackDto),为 Phase 6 EweChatApi 就位铺路。
- **→ 新决策 D-P5-18:** `ewechat/EweChatApi` 延迟 Phase 6(注入 IBuyMessageService,P5 编译不可解)。Phase 5 迁其全部承托依赖。

### listener/ (2) — 见 Q7
### command/ (1) — PurchaseCommand — 见 Q7(→ Phase 6)

---

## Q7 — 启动接线验证(D-P5-09/D-P5-10, D-14a/b)

### ApplicationStartup 合并(D-P5-09)— 非常 critical 的精确化
- 源 WX `listener/ApplicationStartup.java:28-35`:`DictUtil.init(appCode)` + `BsDictUtil.init()`(WX 版)+ `new Thread(executor).start()`。
- 单体 `bas/server/listener/ApplicationStartup.java:42-58`:已含 `DictUtil.init(appCode)` + `ConfigUtil.init()` + `BsDictUtil.init()`(**单体版** `com.spt.bas.server.cache.BsDictUtil`)+ BsCompanyOurUtil/UserCache/DeptCache/PmNodeCache/TemplateContentUtility/ProductTypeUtility/BsCompanyIndustryUtil/FactoryCache/WarehouseCache `.init()` + `new Thread(executor).start()`。

**关键:** 单体已调 `BsDictUtil.init()`,但是**单体自己的** `bas.server.cache.BsDictUtil`(同名不同包)。WX 的 `purchase.wx.server.cache.BsDictUtil.init()` 是**另一个类**,单体没调过。CONTEXT D-P5-09 的"追加 BsDictUtil.init()"需精确为:**追加 WX 版 `com.spt.bas.purchase.wx.server.cache.BsDictUtil.init()`**(全限定 import 消歧)。**不**重复 DictUtil.init / 不重复 `new Thread(executor)`(否则双跑竞态)。

- 直击 Phase 3 登录缺口(memory: authsdk-static-cache-init-gap):`/wx/*` 字典缓存未初始化的根因之一即 WX BsDictUtil 未并入启动链。
- **D-14a 判定:** ApplicationStartup 是 `/wx/*` runtime hard-dep(WX BsDictUtil 提供 WX 字典)。合并是必需的。

### RequestListener(D-P5-10)— 安全 verbatim 迁
`listener/RequestListener.java`:`@Component ServletRequestListener`,`requestDestroyed → UserContext.removeUser()`。单体**无** ServletRequestListener(`grep` 零命中)→ 无重名。依赖 `util/UserContext`(P4 stub→D-P5-14 替换)。**D-14b 判定:** runtime hard-dep(UserContext ThreadLocal 清理防内存泄漏)。verbatim 迁。

### PurchaseCommand(D-P5-13)→ **修正:延迟 Phase 6**
`command/PurchaseCommand.java`:
- `import com.xxl.job.core.context.XxlJobHelper` + `com.xxl.job.core.handler.annotation.XxlJob`(:8-9),`@XxlJob(value="executeCommand")`(:21),`XxlJobHelper.getJobParam()`(:23)。**xxl-job 已删(v1.0 #10)**,classpath 无 `com.xxl.job.*` → verbatim 迁即编译失败。
- 注入 `IUserInfoService` + `ISuccessContractService`(:17-19)—— 均 Phase 6 service。
- `extends ICommand`(spt-tools,单体已有),被 CommandExecutor 消费。

**→ 修正 D-P5-13:** PurchaseCommand **延迟 Phase 6**(依赖 P6 service + xxl-job scrub)。Phase 6 迁入时需 scrub `@XxlJob`/`XxlJobHelper`(改用 commandline 直接参数,因单体用 CommandExecutor 线程驱动非 xxl-job)。Phase 5 的 ApplicationStartup 合并**不需要** PurchaseCommand(单体 CommandExecutor 已在启动线程跑;PurchaseCommand 作为 ICommand bean 在 Phase 6 落位后自动被现有 executor 收集)。

---

## 新增/修正决策汇总(planner 必须落入 plan)

| ID | 内容 | 来源 |
|---|---|---|
| **D-P5-15** | FrameworkConfig 剥减:新建 WX-only config 只含 `eweChatConfig()` @Bean;余 6 bean DROP(单体已自有/冲突) | Q5 探查 |
| **D-P5-16** | ScheduleConfig 跳过(单体同构,重名崩溃) | Q1 |
| **D-P5-17** | SwaggerConfig 跳过(springfox 不在 classpath) | Q2 |
| **D-P5-18** | EweChatApi 延迟 Phase 6(注入 IBuyMessageService) | Q6 |
| **修正 D-P5-08** | JinXinConfig→P5,JinXinApi→P6(P6 前置核 cfca.etl/uaclient jar) | Q3 |
| **修正 D-P5-13** | PurchaseCommand 延迟 Phase 6(P6 service + xxl-job scrub) | Q7 |
| **精确化 D-P5-09** | ApplicationStartup 追加的是 **WX 版** BsDictUtil.init()(全限定消歧),不重复 DictUtil/executor | Q7 |

---

## Validation Architecture(Nyquist Dimension 8)

承托层是纯迁移层,无新业务逻辑,验证以**编译可达 + 启动接线**为主:

1. **编译门(SC#3):** `JAVA_HOME=/path/to/Corretto-1.8 mvn compile -pl zgbas-system` 必须零 `[ERROR]`(locale 无关 grep)。承托层独立编译通过是 Phase 6/7 的前提。
2. **孤立 import 扫描:** 迁入后 grep `import com.spt.bas.purchase.wx.server.*` 在 zgbas-system 内的自洽性 —— 承托类间引用必须闭环(EweChatApi 例外,延 P6)。
3. **stub 替换验证(SC#1 子项):** D-P5-14 六个 stub(ApiResult/BaseException/SecurityException/ResponseUtil/UserContext/UserInfoVo)替换后,字段/方法签名与源实测一致(`diff` 源 vs enclave)。
4. **启动接线(D-P5-09):** ApplicationStartup 含 WX `BsDictUtil.init()` 调用 —— 源断言(grep `purchase.wx.server.cache.BsDictUtil` 在单体 ApplicationStartup)。启动 GREEN 留 Phase 8。
5. **bean 冲突静态保证:** 不存在两个 `taskExecutor`/`eweChatConfig`/外部 SDK @Bean —— grep 单体无重复 `@Bean.*taskExecutor`、无第二个 FrameworkConfig。

**测试粒度:** 承托层无单测价值(纯 POJO/迁移);验证 = 编译 + 静态断言 + 启动(Phase 8)。不强制 80% 覆盖率(迁移层豁免,与 v1.0 Phase 4/5 一致)。

---

## 给 planner 的编排建议(wave 草案)

- **Wave 1(承托底座,无跨切/启动):** payload(22)+ vo(18)+ enums(1)+ common(8,含 D-P5-14 替换 ApiResult/BaseException/Status)+ util(19,含替换 ResponseUtil/UserContext,核 HttpUtils 死码)+ exception(SecurityException 替换 + UserNameOrPasswordException;GlobalExceptionHandler 单独 wave 因 basePackages)。这些类间自洽,可并行迁。
- **Wave 2(横切 bean 收口,灰区 A):** WebAppConfig 剥减 CORS(D-P5-01)+ GlobalExceptionHandler 限 basePackages(D-P5-02)+ ServiceAop 显式注册(D-P5-03)+ FrameworkConfig 剥减 → WxCarrierConfig(D-P5-15)。依赖 Wave 1 的 util/common。
- **Wave 3(外部 wrapper + cache + config 对账,灰区 B):** JinXinConfig/EweChatConfig/OcrConfig 迁入 + WxCarrierConfig `eweChatConfig()` @Bean + cache(BsDictUtil/RedisCache)+ OcrUtils/OcrHelper wrapper + application-dev.yml 加 ewechat/aliyun.ocr/jinxin 明文键(D-P5-05)+ rotate-credentials todo 登记。
- **Wave 4(启动接线,灰区 C):** ApplicationStartup 追加 WX BsDictUtil.init(D-P5-09)+ RequestListener 迁入(D-P5-10)+ D-15a/b inventory checklist 产出(SC#4)。
- **Wave 5(编译门):** `mvn compile -pl zgbas-system` 零错(SC#3)。

**不进 Phase 5(显式排除):** EweChatApi(D-P5-18→P6)、JinXinApi(D-P5-08→P6)、PurchaseCommand(D-P5-13→P6)、ScheduleConfig(D-P5-16)、SwaggerConfig(D-P5-17)、TransactionConfig(D-P5-04)、WxJobConfig(D-P5-06)、CustomConfig/IgnoreConfig(D-07)、BasicErrorController(P7)、SignContractTask/TestJob/DBDocTool(丢弃/延后)。

---

## RESEARCH COMPLETE

7 个 deferred 问题全部定论(5 SKIP/重定向 + 2 精确化),新发现 4 个收口点(D-P5-15/16/17/18)落入决策。承托类 inventory 闭环,EweChatApi/JinXinApi/PurchaseCommand 三处依赖 P6 的类已显式排除出 Phase 5,消除"边搬边猜"。config 清账 18→5(其中 2 剥减)。可进入 planner。
