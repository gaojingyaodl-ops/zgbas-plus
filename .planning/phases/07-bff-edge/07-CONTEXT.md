# Phase 7: BFF edge 迁入 - Context

**Gathered:** 2026-07-24
**Status:** Ready for planning

<domain>
## Phase Boundary

把 basWx purchase-server 的 **BFF edge 全量 verbatim 迁入** `zgbas-admin`(保 `com.spt.bas.purchase.wx.server.*` 包飞地,方案1 verbatim 嵌入),先做三族路由(`/wx/*` / `/ewechat/*` / `/axq/*`)inventory 与 🔴 `/wx/contract` 冲突消歧,再把 11 controller + 4 API + BasicErrorController 接到单体 admin 层。只谈 HOW 落地,不新增能力。

**交付范围(实测源码,2026-07-24 探查):**
- `controller/` **11 个带路由 @RestController**(`BaseController` 疑为抽象基类无 `@RequestMapping`,不计数):
  - `/wx/*` 族:`ContractController`(`/wx/contract`)、`WxUserController`(`/wx/user`)、`UserInfoController`(`/wx/userInfo`)、`UserAttentController`(`/wx/userattent`)、`FileController`(`/wx/file`)、`WxTextContentController`(`/wx/content`)、`WxStockVirtualController`(`/wx/stock/virtual`)
  - `/ewechat/*` 族:`BuyEnquiryController`(`/ewechat/buyEnquiry`)、`BuyQuoteController`(`/ewechat/buyQuote`)、`BuyMessageController`(`/ewechat/message`)
  - `/axq/*` 族:`OtherController`(`/axq`)
- `api/` **4 类**(`purchase/*` 基路径**无前导斜杠**,性质待 researcher 定,见 D-P7-04):`WxUserApi`(`purchase/user`)、`WxOpenApi`(`purchase/open`)、`WxUserDetailApi`(`purchase/userDetail`)、`SaveTempApi`(`purchase/saveTemp`)
- `config/BasicErrorController` 1 类 → zgbas-admin
- **路由 inventory(D-03a/D-03a1)**:三族全端点按 `HTTP method + final path` 归一化成表
- **🔴 `/wx/contract` 冲突消歧(D-03b/D-02)**:见灰区①

**不在此阶段:**
- Phase 8:`mvn compile` 全模块零错 + 启动 GREEN(`/wx/*` 非 404 + WX beans 装配)+ WX Feign 自回环 HTTP proof —— **本阶段所有 runtime/启动/自回环验证全留 P8**
- xxl-job 残留:`task/SignContractTask`(3 `@XxlJob`)+ `config/WxJobConfig` —— 延后 v1.3 quartz gap-closure(D-P5-06/D-P5-11,与 P6 PurchaseCommand 同批)
- WX 小程序**前端**(管理页/UI)—— v1.2 不迁(纯 API 服务)

</domain>

<decisions>
## Implementation Decisions

> 沿用上游已锁定决策(STATE.md 方案1 + D-01~D-19):verbatim 嵌入 / 保 `purchase.wx.*` 包飞地(D-06)/ **controller+API → zgbas-admin**(D-05)/ **路由 1:1 verbatim,仅冲突点最小消歧**(D-01)/ **保留 Feign 自回环不崩为直注**(方案1)/ 双轨认证(Shiro + JWT `JwtAuthenticationFilter` order=1 限 `/wx/* /ewechat/* /axq/*`)不动 / 层序 承托(P5)→ service(P6)→ **edge(P7)**。下列为 Phase 7 新增决策。

### 灰区 ① — `/wx/contract` 冲突消歧策略(SC#2 核心阻塞,D-P7-01)

🔴 实测两端碰撞**比预期严重**:不只是同基路径,而是**方法集部分重叠**——
- **报表侧 `RptCtrContractApi`**(已在 admin,`zgbas-admin/src/main/java/com/spt/bas/report/server/api/RptCtrContractApi.java`,`@RequestMapping("/wx/contract")`):**读为主**(`findPageCtrContract` / `getCreditContractDetail` / `getServiceContractDetail` / `getContractOperationList` / `getDeliveryOutDetail` / `getUndeliveryOutDetail` / `getPayDetail` / `getServicePayDetail` / `getBillDetail` / `getServiceBillDetail` / `getConfirmReceiptDetail` …)
- **basWx 侧 `ContractController`**(本阶段迁,`@RequestMapping("/wx/contract")`):**透传报表逻辑**(`import com.spt.bas.report.client.remote.IRptWxCtrContractClient`,经 Feign 自回环调同一套报表合同服务)+ **独有写操作**(`confirmReceipt` / `applyDeliveryOut` / `confirmPay` / `confirmServicePay` / `applyBill`)+ 重叠读方法(`getContractList` 等与报表侧重叠)。
- 两端独有方法互不碰撞(`findPageCtrContract` 仅报表、`confirmReceipt`/`applyBill` 仅 basWx);**重叠读方法**(`getCreditContractDetail` 等)是 `AmbiguousMappingException` 真正触发点。

- **D-P7-01 方法级矩阵 + 不删类 + 不改可见路由(锁定):**
  1. **researcher 产出冲突矩阵**(D-03a1/D-03b):逐方法 `final path (HTTP method + base + sub)` → 两端是否定义 → owner → minimal disambiguation action。`/wx/* /ewechat/* /axq/*` 三族全端点入表(SC#1)。
  2. **消歧底线**:**不删任何类**(verbatim,D-01)/ **不改 WX 小程序可见路由**(WX 前端 out of scope,不可断)/ **不改报表 web 消费者既有路由**(报表 v1.0 已落 admin,不可断)。
  3. **重叠读方法最小让步**:仅对确认碰撞的重叠读方法做**方法级最小路径 delta**(微调 sub-path),谁让步由 researcher 据"谁先 resident + 谁是真正 owner"定;**WX 独有写操作 + 报表独有读方法各守原 `/wx/contract/{method}` 不动**。
  4. 整批不做路由重写(D-01),只对矩阵标注的碰撞点施最小动作。
  - blast radius 最小,契合 D-01「仅冲突点最小消歧」与北极星「行为等价」。

### 灰区 ② — 验证深度(SC#2 验收边界,D-P7-02)

SC#2 字面要求"Spring 启动无 ambiguous mapping",但启动测试非 hermetic(`application-dev.yml` 明文密钥态,需本地 export;与 P3-P6 同契约)。

- **D-P7-02 编译门 + 静态路由矩阵,启动留 P8(锁定):** 本阶段验收 = 两条:
  1. **SC#1 路由矩阵**(强制验收产出物):三族全端点归一化表,静态证明 `/wx/* /ewechat/* /axq/*` **无 ambiguous mapping**(D-P7-01 矩阵即此证明)。
  2. **SC#3/4 编译门**:`JAVA_HOME=Corretto-1.8 mvn compile -pl zgbas-admin -am` 零 `[ERROR]`(⚠️ 必须带 `-am`,见 D-P7-05)。
  - **实际 Spring 启动 GREEN + `/wx/*` 非 404 + WX Feign 自回环 proof 全留 P8**(SC#2/3/4 启动面与 WX-ALIGN-01/02/03),避免非 hermetic 启动假阳性(P6 gotcha:executor 自报 GREEN 曾假阳性)。
  - 与 P5/P6「仅编译门」一致,新增"路由矩阵"为本阶段特有强制项(SC#1 本就是 P7 scope)。

### 灰区 ③ — 承托缺口补齐策略(D-P7-03)

P6 gotcha 已预警 + 本阶段实测坐实:controller 迁入编译将暴露承托缺口 —— **`StockVirtualWxVo` 单体缺失**(`WxStockVirtualController` 依赖)、**`FeignHttpsConfig` 缺失**(WX Feign HTTPS wiring,自回环/外部电签调用用),且 `CtrContractVo`/`CtrProductVo`/`RptCtrContractSearch` 已在(参照系)。

- **D-P7-03 按 P5/P6 惯例 inline 迁入所缺承托(锁定):** controller 编译缺啥补啥,承托类落 `zgbas-system` 包飞地(`com.spt.bas.purchase.wx.server.vo/util/config` 等),保证 controller 真编译过。
  - `StockVirtualWxVo` → vo 包;`FeignHttpsConfig` → config(WX Feign HTTPS wiring,确认其 @Bean 范围防与单体既有 Feign 配置冲突/重复)。
  - **不 stub**(stub 带来 runtime 风险 + P8 填实负担)、**不回流 todo**(缺口属本阶段 controller 直接编译依赖,非 P5/P6 漏迁回流范畴)。
  - researcher/planner 编译时若再暴露更多 VO/config 缺口,沿用本策略逐类 inline 迁入并记入 inventory。

### 灰区 ④ — `api/` 4 类性质与落点(D-P7-04)

`purchase/user`、`purchase/open`、`purchase/userDetail`、`purchase/saveTemp` —— 基路径**无前导斜杠**(疑似 Feign 契约接口风格,非控制器),但带类级 `@RequestMapping`。

- **D-P7-04 researcher 定性质后分落 admin/system(锁定):**
  1. researcher 先判 4 类是 **`@RestController` 实控制器** 还是 **Feign 契约接口**(无前导斜杠 + 类级 `@RequestMapping` + 方法级 `@RequestMapping` 是 Spring Cloud 契约接口典型特征;`@FeignClient` + `@RestController` 同类会双注册,必须防)。
  2. **实控制器** → 落 `zgbas-admin` 包飞地(`com.spt.bas.purchase.wx.server.api`,D-05 字面)。
  3. **Feign 契约接口** → 落 `zgbas-system`(同 P3 client 契约 `IBsCompanyClient`/`IPmProcessClient`/`IPmApproveClient` 落点);若有实现类则 impl 落 system、接口契约供 WX 内部 Feign 自回环。
  4. 若发现 `@FeignClient` 与 `@RestController` 同类双注册,按 P5/P6 逐类最小适配原则拆分(契约接口 vs 控制器实现),不改业务语义。

### 编译 gotcha(D-P7-05,沿用 P6 实证)

- **D-P7-05 `mvn compile -pl zgbas-admin` 必须带 `-am`:** 单体聚合下 `zgbas-admin` 依赖 `zgbas-system`(P5/P6 落点),不带 `-am` 会用 `.m2` 残留旧 SNAPSHOT(system 4.8.3 stale)导致**假性失败**。编译门统一形态:`JAVA_HOME=Corretto-1.8 mvn compile -pl zgbas-admin -am -s /Users/alan/App/apache-maven-3.8.6/zg_settings.xml`。

### Claude's Discretion

- **冲突矩阵逐方法 owner 判定** —— researcher 实测两端每个 `final path` 的真正消费者(WX 小程序 vs 报表 web 消费者),定 owner 与最小让步侧;D-P7-01 给底线(不删/不改可见路由/方法级最小 delta)。
- **4 个 api/ 类的性质判定** —— researcher 据注解/实现/被调方定 @RestController vs Feign 契约;D-P7-04 给落点分流。
- **承托缺口逐类清单** —— 编译驱动,researcher/planner 编译时暴露即 inline 迁入并记 inventory;D-P7-03 给策略不枚举。
- **`BasicErrorController` 落位** —— 落 `zgbas-admin`(D-05),researcher 确认其 `@Controller`/`/error` 路径不与单体既有 error handler(Spring Boot 默认 `BasicErrorController` / 全局错误页)碰撞,必要时限 basePackages 或命名让步(参照 D-P5-02 GlobalExceptionHandler 限域模式)。
- **11 controller + 4 API + BasicErrorController 的编译波次/拓扑序** —— planner 据 inter-controller 依赖与缺口补齐顺序排 wave。

### Folded Todos

(无 —— cross-reference 命中的 todo(`rotate-leaked-prod-credentials` / `phase4-resolve-entity-schema-drift`)均 v1.0/v1.3 遗留,非 Phase 7 BFF scope,未 fold。见 `<deferred>` Reviewed Todos。)

</decisions>

<canonical_refs>
## Canonical References

**Downstream agents MUST read these before planning or implementing.**

### 需求 / Roadmap / 状态
- `.planning/ROADMAP.md` §Phase 7 — Goal + Success Criteria(4 条:三族路由 inventory / 🔴 `/wx/contract` 冲突消歧落地无 ambiguous mapping / 11 controller+4 API+BasicErrorController 迁入无重复端点 / 路径 1:1 仅冲突点最小消歧)
- `.planning/REQUIREMENTS.md` — **WX-BFF-01**(11 Controller → zgbas-admin `/wx/* /ewechat/* /axq/*`)、**WX-BFF-02**(4 API 类 → zgbas-admin)
- `.planning/STATE.md` — 方案1 锁定 + 沿用 D-01~D-19(尤其 D-01 路由 1:1 最小消歧、D-04/05/06 模块与包、D-10/11 verbatim/行为等价、D-14a/b listener 判定、D-16/17 外部边界)+ 🔴 Blockers(`/wx/contract` 路由冲突 = Phase 7 启动前必解)
- `.planning/phases/06-service/06-CONTEXT.md` — **直接前置**。P6 service 层就位(11 controller 注入的 service impl 全部落地);⚠️ P6 gotcha ②「Phase-3 inline 不完整,controller 层可能还要补 CtrContractVo/CtrProductVo/StockVirtualWxVo/FeignHttpsConfig」(本阶段 D-P7-03 验证坐实 StockVirtualWxVo/FeignHttpsConfig 缺失)
- `.planning/phases/05-carrier-layer/05-CONTEXT.md` — 横切 bean 限域防重名/双注册模式(D-P5-02 `@ControllerAdvice(basePackages)` / D-P5-03 ServiceAop pointcut 自限),D-P7-04 防 `@FeignClient`+`@RestController` 双注册同源
- `.planning/phases/03-feign/03-CONTEXT.md` — P3 包飞地策略 + 3 Feign 自回环契约(`IBsCompanyClient`/`IPmProcessClient`/`IPmApproveClient`)+ `PurchaseWxClientConfig` 落点(D-P7-04 Feign 契约接口落 system 参照)

### 源码(basWx purchase-server,11 controller + 4 api)
- `/Users/alan/WorkSpace/IDEA/zgbas/basWx/purchase-server/src/main/java/com/spt/bas/purchase/wx/server/controller/` — 11 带路由 controller + `BaseController`(基类)
- `…/controller/ContractController.java` — `@RequestMapping("/wx/contract")` + `import com.spt.bas.report.client.remote.IRptWxCtrContractClient`(透传 + 独有写操作,D-P7-01 冲突核心)
- `…/api/` — `WxUserApi`/`WxOpenApi`/`WxUserDetailApi`/`SaveTempApi`(`purchase/*` 无前导斜杠,性质待定 D-P7-04)
- `…/config/BasicErrorController.java` — error handler(D-P7-05 落 admin,防与单体默认 error 碰撞)
- **报表冲突对端**:`/Users/alan/WorkSpace/IDEA/zgbas/basReport/reportServer/src/main/java/com/spt/bas/report/server/api/RptCtrContractApi.java`(`@RequestMapping("/wx/contract")`,读为主)

### 关键单体文件(冲突/落位/参照)
- `zgbas-admin/src/main/java/com/spt/bas/report/server/api/RptCtrContractApi.java` — 🔴 冲突对端(**v1.0 Phase 5 报表迁移已落 admin**),D-P7-01 矩阵另一侧
- `zgbas-admin/src/main/java/com/spt/bas/server/api/` — 主域 api 落点(v1.0 已落,WX api 包飞地参照)
- `zgbas-admin/src/main/java/com/spt/bas/web/controller/` — 主域 BFF controller 包(v1.0 已落,WX controller 包飞地平级参照)
- `zgbas-admin/src/main/resources/application-dev.yml` — WX 外部密钥明文落点(D-P5-05,本阶段若 FeignHttpsConfig 引用新密钥沿用)
- `zgbas-system/src/main/java/com/spt/bas/client/vo/CtrContractVo.java`(+ `CtrProductVo`)+ `…/report/client/entity/RptCtrContractSearch.java` — 已迁承托类(本阶段 controller 直接复用,D-P7-03 参照系)
- `zgbas-common`/`zgbas-system` P3-P6 已迁飞地(`com.spt.bas.purchase.wx.server.{entity,dao,service,vo,payload,util,config,cache,aop,ewechat}`)—— 11 controller 注入依赖全就位

</canonical_refs>

<code_context>
## Existing Code Insights

### Reusable Assets
- P3-P6 飞地全就位:11 实体 / 18 Dao / 19 service impl + 18 接口 / EweChatApi / 承托(payload/vo/util/common/config/cache/aop/exception)—— 11 controller 的 `@Autowired` 注入依赖**已全部落地**,本阶段主要是迁控制器本身 + 补编译暴露的少量承托缺口(D-P7-03)。
- 3 Feign 自回环契约(`IBsCompanyClient`/`IPmProcessClient`/`IPmApproveClient`)+ `PurchaseWxClientConfig`(P3)—— controller 对主域调用经此自回环,方案1 保留。`ContractController` 的 `IRptWxCtrContractClient` 同模式(报表域自回环)。
- `JwtAuthenticationFilter`(P4 wiring,order=1 限 `/wx/* /ewechat/* /axq/*`)—— 三族路由的认证接管已就位,controller 迁入即受其保护,无需重接。
- `GlobalExceptionHandler`(P5,`@ControllerAdvice(basePackages="com.spt.bas.purchase.wx.server")`)—— WX controller 异常信封已限域,controller 迁入后自动生效。

### Established Patterns
- 包飞地 `com.spt.bas.purchase.wx.server.*` verbatim 保包名(方案1/D-06)—— controller/api 落 admin 包飞地,与单体 `com.spt.bas.web.controller` / `com.spt.bas.report.server.api` 平级共存。
- verbatim port first(D-10)/ 行为等价(D-11)/ 仅冲突点最小消歧(D-01)—— D-P7-01 方法级矩阵是该模式在路由冲突上的延续。
- 全局横切/路由 bean 防重名/双注册 —— P5 已证(`GlobalExceptionHandler` 限域 / `ServiceAop` pointcut 自限 / `WebAppConfig` 剥减),D-P7-04 防 `@FeignClient`+`@RestController` 双注册同源。

### Integration Points
- `ZgbasApplication`(`@SpringBootApplication`)—— @ComponentScan 自动扫入 WX `@RestController`(@RestController 自注册,无需显式 @Bean)。
- `RequestMappingHandlerMapping` —— 启动时注册全部 RequestMapping;D-P7-01 矩阵 + D-P7-02 静态证明确保**无 AmbiguousMappingException**(`/wx/contract` 重叠读方法是唯一已知风险点)。
- WX Feign 自回环 —— controller 内对主域/报表域的 `I*Client` 调用经 `PurchaseWxClientConfig` 走 localhost:8080 同进程直调(runtime proof 留 Phase 8)。

</code_context>

<specifics>
## Specific Ideas

- 用户对 4 个灰区全部选**推荐项**,一致延续 P3-P6 决策风格:行为等价优先于 verbatim 字面 / 最低 compile 级联风险 / 不扩 scope / 沿用 P5/P6 既定模式(横切 bean 限域、编译门验收、缺啥补啥 inline 迁入)。
- 冲突消歧用户明确底线 **不删类、不改 WX 小程序可见路由、不改报表 web 消费者路由**,方法级最小让步 —— 严格契合 D-01「仅冲突点最小消歧」与北极星「行为等价」,拒绝整类换路径的激进方案。
- 验证深度用户选 **编译门 + 静态路由矩阵**,实际启动 GREEN 留 P8 —— 与 P5/P6「仅编译门」一致,且规避非 hermetic 启动假阳性(P6 executor 自报 GREEN 曾假阳性,post-merge 必须独立复跑)。
- 承托缺口用户选 **按 P5/P6 惯例 inline 迁入**,拒绝 stub —— 保证 controller 真编译过且不带 runtime 债。
- api/ 4 类用户选 **researcher 定性质后分落** —— 不预设,以实测注解/消费者为准,防 `@FeignClient`+`@RestController` 双注册。

</specifics>

<deferred>
## Deferred Ideas

- 🔴 `/wx/contract` 冲突的**实际 Spring 启动验证**(无 AmbiguousMappingException + 两端点 handler 命中)→ **Phase 8**(SC#2 启动 GREEN + `/wx/*` 非 404);本阶段仅静态路由矩阵证明。
- `task/SignContractTask`(3 `@XxlJob`)+ `config/WxJobConfig`(xxl-job 残留)→ v1.3 quartz gap-closure(D-P5-06/D-P5-11,与 P6 PurchaseCommand 同批)。
- WX 小程序前端(管理页/UI)→ v2(纯 API 服务,v1.2 不迁)。
- JWT/Shiro 认证统一 → future(身份模型分离,方案1 不做)。
- basWx Feign 自回环崩为直注 → future(方案1 保留)。

### Reviewed Todos (not folded)
- `rotate-leaked-prod-credentials`(high)—— v1.0 遗留,P5 新增 EweChat/JinXin/OCR 明文密钥后尤需关注;若本阶段 `FeignHttpsConfig` 引用新密钥(D-P7-03)须显式登记,但密钥轮换本身非 BFF scope,留 todo 跟进。
- `phase4-resolve-entity-schema-drift`(medium)—— v1.0/v1.2 遗留(ddl-auto=validate,259 表),与 controller 迁入无关,留 v1.3。

</deferred>

---

*Phase: 7-BFF edge 迁入*
*Context gathered: 2026-07-24*
