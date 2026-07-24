# Phase 6: Service 层迁入 - Context

**Gathered:** 2026-07-24
**Status:** Ready for planning

<domain>
## Phase Boundary

把 basWx purchase-server 的 **service 层全量 verbatim 迁入** `zgbas-system`(保 `com.spt.bas.purchase.wx.server.service.*` 包飞地,方案1 verbatim 嵌入),适配单体当前内联 `BaseService/IBaseService` 签名,**保留**对主 bas 域的 Feign 自回环调用(不崩为直注),为 Phase 7 BFF 提供完整业务实现层。只谈 HOW 落地,不新增能力。

**交付范围(实测源码,2026-07-24 探查):**
- `service/impl/` **20 类** + `service/` **19 接口**(`JinXinApi` 无接口)。`WxAccessTokenService`(+`IWxAccessTokenService`)/ `IBsDictService` 接口 **P3/P5 已迁**,本阶段实际迁 **~19 类 impl + ~18 接口**:
  - **14 类 `extends BaseService<T> implements IXxxService`**(实体型,依赖 P3 已迁的 entity + 18 Dao):BsDict/BsCompany/BuyEnquiry/CompanyIndustry/BuyMessage/TempSaveInfo/Feedback/UserDetail/WxUserTextRead/WxSession/User/WxUserInfo/WxSmsCheckCode (+ WxAccessToken 已迁)
  - **5 类纯 `implements IXxxService`**(无实体无 base,纯逻辑):Apply/BuyQuote/Contract/SuccessContract/UserInfo
  - **1 类 `JinXinApi extends CommonUtil`**(外部 HTTP wrapper,P5 延入 P6)
- **P5 延入 P6 的承托/外部 wrapper**(承托依赖已就位):
  - `ewechat/EweChatApi`(`@Autowired IBuyMessageService` —— 本阶段 IBuyMessageService 落地后可编译)
  - `command/PurchaseCommand`(`implements ICommand` + `@XxlJob`,见 D-P6-02)

**不在此阶段:**
- Phase 7:`controller/` 11 + `api/` 4 + `config/BasicErrorController` → zgbas-admin
- Phase 8:全模块编译 + 启动 GREEN + `/wx/*` 非 404 + WX Feign 自回环 proof
- xxl-job 残留:`task/SignContractTask`(3 `@XxlJob`)→ v1.3 quartz gap-closure(D-P5-06/D-P5-11);`PurchaseCommand` 的 **定时触发**路径同步延后 v1.3(D-P6-02),**ICommand 队列路径本阶段保**

</domain>

<decisions>
## Implementation Decisions

> 沿用上游已锁定决策(STATE.md 方案1 + D-01~D-19):verbatim 嵌入 / 保 `purchase.wx.*` 包飞地(D-06)/ service→zgbas-system(D-05)/ **保留 Feign 自回环不崩为直注**(方案1,11·20 service 经 `IBsCompanyClient`/`IPmProcessClient`/`IPmApproveClient` 自回环)/ D-10 verbatim port first / D-11 行为等价 / **D-12 BaseService 最小签名收口,以单体当前内联实现为准** / D-16·17 外部集成维持 HTTP 边界只迁 wrapper / D-18·19 复用 P4 Redis/JWT/WxMaService wiring / 层序 承托(P5 done)→ service(P6)→ edge(P7)。下列为 Phase 6 新增决策。

### 灰区 ① — BaseService/IBaseService 签名适配落点(D-12 核心)

14 类 `extends BaseService<T>` 用的是**主域同款内联 base**(`PmProcessServiceImpl`/`PmApproveContentsServiceImpl` 等主域 service 已在用 → 兼容性已证),故实测编译风险偏低。但源码 `IXxxService`/`IBaseService` 可能声明内联 base 之外的方法或签名微差。

- **D-P6-01 逐类最小适配(锁定):** 签名不一致时**改 WX service 适配当前内联 base**,不动共享 `BaseService`/`IBaseService`/`IDataService`(避免牵动主域 533 service)。blast radius 最小,契合 D-12。仅当**多个** WX service 共享同一缺失方法时,researcher/planner 才评估建 WX 本地 `WxBaseService extends BaseService` 子类集中(不改主域 base);扩共享 base 为最后手段,非必要不动。
  - inlined base 路径:`zgbas-common/src/main/java/com/spt/tools/jpa/service/BaseService.java`(`abstract class BaseService<T extends IdEntity> implements IBaseService<T>`,核心 `abstract BaseDao<T> getBaseDao()`)+ `IBaseService.java`(`extends IDataService<T>`:`delete/findPage/getEntity/loadAll/findAll…`)。

### 灰区 ② — PurchaseCommand 的 @XxlJob 处理(硬地雷)

`PurchaseCommand implements ICommand` + `@XxlJob("executeCommand")` + `XxlJobHelper.getJobParam()`。xxl-job v1.0 #10 已删,**注解类不在 classpath**,verbatim 迁会编译失败 —— 与 `SignContractTask` 同款地雷。但与 SignContractTask 不同:PurchaseCommand 被 D-P5-09 的单体 ApplicationStartup/CommandExecutor 队列消费(services 经 enqueue 依赖 ICommand 路径),**必须本阶段保**。

- **D-P6-02 scrub @XxlJob 保 ICommand(锁定):** 删除 `@XxlJob` 注解 + `XxlJobHelper`/`com.xxl.job.*` import,**保留** `implements ICommand` 及 CommandExecutor 队列消费路径。**定时触发能力(scheduled trigger)延后 v1.3 quartz gap-closure**(与 SignContractTask 同批,STATE 已列 "quartz gap-closure(28 handler 路由)" 为 v1.3 pending)。
  - 不延后整类:D-P5-09 已把 ApplicationStartup 的 `PurchaseCommand` 注册逻辑并入单体,整类延后要回改启动接线,牵连更大。
  - 不现在接 RuoYi quartz sys_job:扩大 P6 scope,与 v1.3 gap-closure 重叠,不做。

### 灰区 ③ — Interface / 命名 verbatim vs 规范化

源码命名混乱是既成事实:`service/impl/` 下仅 5 类用 `*ServiceImpl` 后缀(Apply/BuyEnquiry/BuyMessage/BuyQuote/Contract/SuccessContract/WxUserTextRead 中部分),其余 14 类直接叫 `*Service`;`JinXinApi extends CommonUtil` 无接口。

- **D-P6-03 verbatim 保现状(锁定):** 保包名/类名/文件位置全 verbatim,不统一后缀、不补接口、不重命名。行为等价优先于代码整洁,compile 级联风险最低,契合 D-10。Feign 自回环按契约接口(`IBsCompanyClient` 等)调用、`@Autowired` 按类型注入,实测不依赖补接口;JinXinApi 被按类型注入,无需补 interface。

### 灰区 ④ — 验证深度

- **D-P6-04 仅编译门(锁定):** 本阶段验收 = **SC#4** `JAVA_HOME=Corretto-1.8 mvn compile -pl zgbas-system` 零 `[ERROR]`(与 P5 一致)。不加启动 bean 装配 smoke(P6 还无 controller,bean 装配验证信号有限),不做全模块 compile(controller 在 P7 才迁,admin 可能仍有 WX 缺口)。**所有 runtime/bean 装配/启动验证留 Phase 8**(SC#2 启动 GREEN + SC#3 `/wx/*` 非 404 + WX Feign 自回环 proof)。

### Claude's Discretion

- BaseService 签名不一致的**具体方法级清单** —— researcher 实测每类 `IXxxService` 与内联 `IBaseService`/`IDataService` 差异后定;D-P6-01 给方向(逐类改 WX 适配),建 WxBaseService 子类的阈值(多类共享同一缺失方法)由 researcher/planner 判。
- 20 service 的**编译波次/拓扑序**(inter-service 依赖:EweChatApi→IBuyMessageService、UserInfoService→JinXinApi 等)—— planner 据依赖图排 wave,researcher 先出依赖矩阵。
- `scrub @XxlJob` 的最小 diff 边界(只删注解+import,不触动 ICommand 业务体)—— researcher 标注精确删行。

### Folded Todos

(无 —— cross-reference 命中的 2 个 todo(`phase4-resolve-entity-schema-drift`/`rotate-leaked-prod-credentials`)均 v1.0/v1.3 遗留,非 Phase 6 service scope,未 fold。见 `<deferred>` Reviewed Todos。)

</decisions>

<canonical_refs>
## Canonical References

**Downstream agents MUST read these before planning or implementing.**

### 需求 / Roadmap / 状态
- `.planning/ROADMAP.md` §Phase 6 — Goal + Success Criteria(4 条:20 service 全量迁入 / BaseService 最小签名收口无 spt-tools 残余 import / 保留 Feign 自回环 / `mvn compile -pl zgbas-system` 零错)
- `.planning/REQUIREMENTS.md` — **WX-SERVICE-01**(~20 service impl → zgbas-system)
- `.planning/STATE.md` — 方案1 锁定 + 沿用 D-01~D-19(尤其 D-04/05/06 模块与包、D-10/11/12 verbatim/行为等价/BaseService 最小收口、D-16/17 外部边界、D-18/19 复用 P4 wiring)+ Blockers(BaseService 签名适配 = Phase 6 核心 compile 风险点)
- `.planning/phases/05-carrier-layer/05-CONTEXT.md` — **直接依赖**。P5 承托层决策 + 三项延入 P6(EweChatApi/JinXinApi/PurchaseCommand)+ D-P5-09 ApplicationStartup 并入单体等 PurchaseCommand 注册
- `.planning/phases/04-sdk/04-CONTEXT.md` — P4 wiring(Redis/JWT/WxMaService)+ D-P4 明文密钥 + P4 预置 stub 清单
- `.planning/phases/03-feign/03-CONTEXT.md` — P3 包飞地策略 + EntityScan/EnableJpaRepositories 扩包 + 3 Feign 自回环契约 + 11 实体/18 Dao 已迁落点

### 源码(basWx purchase-server)
- `/Users/alan/WorkSpace/IDEA/zgbas/basWx/purchase-server/src/main/java/com/spt/bas/purchase/wx/server/service/` — 19 接口根(`I*Service.java`)
- `…/service/impl/` — 20 impl(14 `extends BaseService<T>` / 5 纯 implements / JinXinApi extends CommonUtil)
- `…/command/PurchaseCommand.java` — `implements ICommand` + `@XxlJob("executeCommand")` + `XxlJobHelper.getJobParam()`(D-P6-02 scrub 目标,第 8/9/15/21/23 行 xxl-job 残留)
- `…/ewechat/EweChatApi.java` — `@Autowired IBuyMessageService`(P5 延入,本阶段 IBuyMessageService 落地后可编译)

### 关键单体文件(适配/参照/接线)
- `zgbas-common/src/main/java/com/spt/tools/jpa/service/BaseService.java` — 内联 base(`abstract`,`getBaseDao()` 抽象方法,D-P6-01 适配目标)
- `zgbas-common/src/main/java/com/spt/tools/jpa/service/IBaseService.java` — `extends IDataService<T>`(delete/findPage/getEntity/loadAll/findAll 签名基线)
- `zgbas-system/src/main/java/com/spt/pm/service/impl/PmProcessServiceImpl.java`(+ `PmApproveContentsServiceImpl`/`PmProcessAccessServiceImpl`)— 主域同款 `extends BaseService` 参照模式(兼容性已证)
- `zgbas-common/src/main/java/com/spt/tools/core/cmd/CommandExecutor.java` — ICommand 队列 executor(D-P6-02 ICommand 路径保留依据)
- `zgbas-system/src/main/java/com/spt/bas/server/listener/ApplicationStartup.java` — 已并入单体启动监听(D-P5-09,等 PurchaseCommand 注册到 CommandExecutor 队列)
- `zgbas-system/src/main/java/com/spt/bas/purchase/wx/server/service/IBsDictService.java` + `IWxAccessTokenService.java`(+`impl/WxAccessTokenService.java`)— P3/P5 已迁接口/impl(本阶段不重迁,作飞地既有落点参照)

</canonical_refs>

<code_context>
## Existing Code Insights

### Reusable Assets
- 内联 `BaseService<T extends IdEntity>` / `IBaseService<T>` / `IDataService<T>`(`zgbas-common/.../tools/jpa/service/`)—— 14 个实体型 service 直接 `extends`,主域已大规模验证兼容。
- P3 已迁 **11 entity + 18 Dao**(`com.spt.bas.purchase.wx.server.{entity,dao}`)—— 14 实体型 service 的 `getBaseDao()` 返回值与实体泛型实参已就位。
- 3 Feign 自回环契约(`IBsCompanyClient`/`IPmProcessClient`/`IPmApproveClient`)+ `PurchaseWxClientConfig`(P3 已迁)—— 11/20 service 对主域调用经此自回环 localhost:8080,方案1 保留不崩为直注。
- `ICommand` + `CommandExecutor`(spt-tools core,单体已有)—— PurchaseCommand ICommand 队列路径复用,无需新建。
- P4 wiring(Redis/JWT/WxMaService)+ P5 承托(ApiResult/UserContext/ServiceAop/RedisCache/各 Config)—— service 注入依赖已全就位。

### Established Patterns
- 包飞地 `com.spt.bas.purchase.wx.server.*` verbatim 保包名(方案1/D-06)—— service 层与单体 bas service 平级共存于 zgbas-system。
- verbatim port first(D-10)/ 行为等价(D-11)—— D-P6-03 命名混乱保现状、D-P6-01 签名逐类最小适配,均为该模式延续。
- 全局横切 bean 防重名/双注册 —— ServiceAop 已 P5 落地(pointcut 自限 WX,不命中主域 service),本阶段 service 落地后其 AOP 自动生效,无需再处理。

### Integration Points
- `ZgbasApplication`(`@SpringBootApplication`)—— @ComponentScan 自动扫入 WX `@Service`(service 层均为 `@Service` 自注册,无需显式 @Bean)。
- D-P5-09 ApplicationStartup —— PurchaseCommand 经 ICommand 注册到现有 CommandExecutor 队列(本阶段 scrub @XxlJob 后接线闭环)。
- Feign 自回环 —— service 内对主域的 `IBsCompanyClient` 等调用,经 PurchaseWxClientConfig 走 localhost:8080 同进程直调(runtime proof 留 Phase 8)。

</code_context>

<specifics>
## Specific Ideas

- 用户对 4 个灰区全部选**推荐项**,一致延续"行为等价优先于 verbatim 字面 / 最低 compile 级联风险 / 不扩 scope"主线 —— 与 Phase 3/4/5 决策风格一致(verbatim 嵌入方案1)。
- BaseService 适配落点用户明确**不动共享 base**(避免牵主域 533 service),与 D-12「以单体当前内联实现为准」严格一致。
- PurchaseCommand 用户接受**scrub 注解保 ICommand + 定时触发延后 v1.3**,与 SignContractTask 同批处理 —— v1.3 quartz gap-closure 将统一收口 basWx 全部 xxl-job 残留(PurchaseCommand executeCommand + SignContractTask 3 job)。

</specifics>

<deferred>
## Deferred Ideas

- `PurchaseCommand` 的 **定时触发路径**(`@XxlJob("executeCommand")` scheduled trigger)→ v1.3 quartz gap-closure(D-P6-02,本阶段仅 scrub 注解保 ICommand 队列路径)
- `task/SignContractTask`(3 `@XxlJob`)→ v1.3 quartz gap-closure(D-P5-06/D-P5-11)
- JWT/Shiro 认证统一 → future(身份模型分离,方案1 不做)
- basWx Feign 自回环崩为直注 → future(方案1 保留)
- BaseService 共享 base 扩方法 / 建 WxBaseService 子类 → 仅当 researcher 发现多类共享同一缺失方法时再评估(D-P6-01 阈值)

### Reviewed Todos (not folded)
- `phase4-resolve-entity-schema-drift`(medium)—— v1.0/v1.2 遗留(ddl-auto=validate,259 表),与 service 迁入无关,留 v1.3。
- `rotate-leaked-prod-credentials`(high)—— v1.0 遗留,P5 新增 EweChat/JinXin/OCR 明文密钥后尤需关注,但密钥轮换本身非 service 层 scope,留 todo 跟进。

</deferred>

---

*Phase: 6-Service 层迁入*
*Context gathered: 2026-07-24*
