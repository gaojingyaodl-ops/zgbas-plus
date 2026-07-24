# Phase 8: 对齐验证 - Context

**Gathered:** 2026-07-24
**Status:** Ready for planning

<domain>
## Phase Boundary

v1.2 收尾验证里程碑 —— **不是**新一轮类迁移。把 P5/P6/P7 全部显式"留到 Phase 8"的 runtime 证明收口为一道四层闸门,使 basWx 正式成为单体的有机组成,v1.2 验收通过。北极星:单进程跑全功能 + 行为等价。只谈 HOW 落地(验证 + 最小修复),不新增能力。

**四层闸门(SC,实测源码 + 测试现状,2026-07-24 探查):**

| SC | 要求 | 实测现状 |
|---|---|---|
| WX-ALIGN-01 | `mvn compile` 全模块零 `[ERROR]` | P7 已证 BUILD SUCCESS(5 模块);本阶段复核全 reactor 即可 |
| WX-ALIGN-02 | `ZgbasApplicationTest` 启动 GREEN(含 WX beans) | `wxInfrastructureBeans_phase4_probe` **已存在且 active**(RedisTemplate/JwtConfig/WxMaService/JwtFilter `/wx/*` 全验) |
| WX-ALIGN-03 | `/wx/*` 等关键端点非 404(业务报错可接受) | **缺口** —— 现有 reachability probe 全是 `/login`/`/index`/`/apply/*`/`/ctr/*`/report,**无任何 `/wx/*`/`/ewechat/*`/`/axq/*` HTTP 探针** |
| WX Feign 自回环 proof | `PurchaseWxClientConfig` 自回环非 404 | **缺口** —— bas/report 各有一个 bean-resolve probe,**WX(purchase)无对应 probe**;yml 注释仍写"v2-deferred 404"(P7 已迁 controller,**注释 stale**) |

**本阶段代码增量(实测,最小):**
- 补 `/wx/*`/`/ewechat/*`/`/axq/*` 三族代表端点的 HTTP reachability probe(对齐既有 `basContractEndpointReachable_*` 模式)→ 满足 SC#3
- 补 WX(purchase)Feign 自回环 bean-resolve probe(对齐 `reportFeignSelfLoopbackWiring_probe`)→ 满足 SC#4
- 独立复跑 `mvn test` 确认全 always-on `@Test` GREEN,修 runtime 暴露的最小问题 → 满足 SC#2
- 全 reactor `mvn compile` 复核 → 满足 SC#1

**不在此阶段:**
- 深层行为等价(端点业务正确性、多步流程、WX 小程序端到端)→ 手动 UAT / v1.3(D-P8-01)
- basWx 微信采购小程序前端(管理页/UI)→ v2(纯 API 服务)
- JWT/Shiro 认证统一 → future(身份模型分离,方案1 不做)
- basWx Feign 自回环崩为直注 → future(方案1 保留)
- xxl-job 残留(PurchaseCommand 定时触发 + SignContractTask 3 job)→ v1.3 quartz gap-closure
- 里程碑归档/tag → `/gsd-complete-milestone` 命令范畴,非 Phase 8 代码 scope

</domain>

<decisions>
## Implementation Decisions

> 沿用上游已锁定决策(STATE.md 方案1 + D-01~D-19):verbatim 嵌入 / 保 `purchase.wx.*` 包飞地 / 双轨认证(Shiro + JWT)不动 / 保留 Feign 自回环不崩为直注 / 外部集成维持 HTTP 边界 / **P5/P6/P7 全部"仅编译门",一切 runtime/启动/自回环 proof 显式留 P8** —— 这正是 Phase 8 存在的理由。下列为 Phase 8 新增决策。

### 灰区 ① — 验收深度 & runtime 修复预算(D-P8-01)

P8 是验证里程碑,但全 reactor 启动几乎必然暴露编译抓不到的 runtime 问题(P7 deferred:`swagger-bootstrap-ui` 1.9.6 入 classpath 的 auto-config 副作用;WX 自回环 yml 注释 stale;5 个 `BasicErrorController` excludeFilter 是否真消解同名 bean;可能的循环依赖/缺 bean/NPE)。修复预算决定 P8 的性格。

- **D-P8-01 最小修复到 GREEN(锁定):** 沿用 P5/P6/P7 编译门迭代回流风格 —— 每个 runtime 失败做最小适配(改注释/扩 excludeFilter/补缺 bean/适配签名/lazy-degradation),不扩 scope、不动业务语义。深层行为等价(端点业务正确性、多步流程、WX 小程序端到端)留手动 UAT / v1.3。
  - 不顺手根治(D-P8-01-rej-A):追根因彻底修业务逻辑会膨胀、撞 dev DB/业务、推迟 milestone,违背北极星 + ① 一致性。
  - 不"验证为主 defer 修复"(D-P8-01-rej-C):P8 若红只文档化不修,则验证里程碑沦为"启动崩溃记录",与定位不符;北极星要求"单进程跑全功能"必须 GREEN。
  - 修复范围界定:仅修"使四层闸门 GREEN 所必需"的 runtime 问题;每处修复须标注根因 + 是否行为等价(对齐 P7-06 `BasicErrorController` timestamp 适配前例)。

### 灰区 ② — SC#2 启动判据 & 防假阳性(D-P8-02)

实测 `${DB_PASSWORD}`/`${SPT_APP_SECRET}` 占位符**仅**在 `application-prod.yml`(D-P2-13);`application-dev.yml`(测试 `@ActiveProfiles("dev")` 所读)已明文 localhost:8080 URL + 明文密钥(D-P4),故 `export` **不再需要**(STATE.md line 83 旧表述 stale,以此实测为准)。但测试仍连真实 dev DB `sptbasdb_pd`(Druid + 53 `sys_job` 种子 + 239 实体 ddl-auto=none)—— **dev DB 可达是硬前置**,非 hermetic。测试分两类:~25 个 always-on `@Test`(bean 装配 + reachability + scheduler 载入)与一批 `@Disabled` 真跑/写类(手动启用约定)。

- **D-P8-02 独立复跑 GREEN(锁定):** SC#2 判据 = 用户本地 `JAVA_HOME=Corretto-1.8 mvn test -pl zgbas-admin -am` **独立复跑**(不靠 executor 自报),always-on `@Test` 全绿(`0 failures/0 errors` + 启动日志无 `FAILED`/`Exception`)。`@Disabled` 真跑/写类按既有手动启用约定,不计入 SC#2 硬闸门。
  - **防假阳性(强制):** P6 executor 自报 GREEN 曾内联泄漏密钥(假绿);本阶段 GREEN **必须独立复跑证实**,executor 自报不作为唯一证据。
  - **dev DB 前置(文档化):** SC#2 前置 = `sptbasdb_pd` 可达 + sys_job 种子(3 RyTask + 50 prod = 53 行)+ 实体表存在;researcher/planner 在 PLAN 里显式列前置,执行者复跑前确认。
  - 不新增 application-test profile + H2(D-P8-02-rej-B):239 实体 + mybatis + Quartz sys_job + Shiro 换 H2 风险极大,与行为等价冲突且可能引入假绿;改动面广、不适合验证里程碑。
  - 不降级为"启动不崩"(D-P8-02-rej-C):contextLoads + WX bean probe 绿即算过会掩盖 reachability/scheduler 等问题,与"对齐验证"定位不符。

### 灰区 ③ — /wx/* 端点验证范围 & 通过判据(D-P8-03)

WX 走 `JwtAuthenticationFilter`(order=1,urlPatterns `/wx/* /ewechat/* /axq/*`)而非 Shiro —— 未认证预期 401/错误信封(**非**主域的 302→/login)。三族端点已在 P7 `07-ROUTE-MATRIX.md` 列全(7 `/wx` + 3 `/ewechat` + 1 `/axq` + 4 `/purchase/*` api + `/error`)。

- **D-P8-03 三族代表抽样 + 非404 即过(锁定):** 三族(`/wx` + `/ewechat` + `/axq`)各挑代表端点 + `/purchase/*` api 代表,新增 HTTP reachability probe(对齐既有 `basContractEndpointReachable_*` / `reportHttpReachability_proof` 模式)。
  - **判据 = HTTP 非 404 即 pass**:`404` = 路由未注册 = 失败;`2xx/3xx/401/403/400/500` = handler 命中 = pass(WX 未认证预期 `401`/JWT 错误信封)。与既有 reachability 测试严格一致(SC#3 字面"业务报错可接受")。
  - 不纠结 reachability 层 500:真实调用正确性(业务语义)由 ④ 自回环 proof + 手动 UAT 保证,reachability 层只证"handler 注册"。
  - 不全端点矩阵扫(D-P8-03-rej-B):~30+ 端点逐个探,多数 JWT-reject/401 信号稀释、收益递减。
  - 不仅典型端点(D-P8-03-rej-C):仅 `/wx/user/login` + 1-2 则 `/ewechat`/`/axq` 三族无证,覆盖不足。
  - researcher 在 PLAN 出三族代表端点清单(每族 1-2 个,覆盖不同 controller + method),参照 07-ROUTE-MATRIX.md §2。

### 灰区 ④ — WX Feign 自回环 proof 形态(D-P8-04)

前例:bas `feignSelfLoopbackWiring_probe` + report `reportFeignSelfLoopbackWiring_probe` 均为 **bean-resolve probe**(断言 PathConfig bean + I*Client proxy 解析 + url 含 localhost:8080,lazy proxy,不证真实 HTTP round-trip)。WX 侧 `PurchaseWxClientConfig`(bean `purchaseWxServerConfig`,urlKey `spt.bas.purchase.wx.url`→`http://localhost:8080`,SERVER_URL `#{purchaseWxServerConfig.url}`)+ 3 契约(`IWxUserClient`/`IWxUserDetailClient`/`ISaveTempClient`,被 16 个 basServer service 调)。**关键变化:P7 迁了 WX controller,真实 HTTP 自回环首次可行** —— 但 ③-A 已在 HTTP 层证明 WX controller 可达(非404),故"非404"在 HTTP 层已被覆盖。

- **D-P8-04 bean-resolve probe(锁定):** 新增 WX(purchase)自回环 probe,对齐 bas/report 前例 —— 断言:①`PurchaseWxClientConfig` bean(`purchaseWxServerConfig`)解析;②1-2 个 WX `I*Client`(如 `IWxUserClient`/`ISaveTempClient`)Feign proxy 解析(证 `@EnableFeignClients` 含 `com.spt.bas.purchase.wx.client.remote` + SpEL `#{purchaseWxServerConfig.url}` 解析);③`purchaseWxServerConfig.url` 含 `localhost:8080`。
  - 组合充分性:③-A 已证 WX controller HTTP 可达(非404),④-A 证自回环 wiring 正确(bean 层)→ SC#4"自回环非404"在 HTTP 层 + wiring 层联合覆盖,无需真实 round-trip。
  - 不扩 scope 做 always-on + @Disabled 双保险(D-P8-04-rej-B):工作量最大,违背 ①-A 最小修复。
  - 不做纯真实 HTTP round-trip(D-P8-04-rej-C):需定位触发路径 + 可能撞 DB/JWT/auth,成本高,边际价值低(③-A 已覆盖 HTTP 层)。
  - **stale 注释清理:** researcher/planner 清理 `application-dev.yml:49-51` 与 `ZgbasApplication.java:131-134` 关于"basWx v2-deferred 404"的 stale 注释(P7 已迁 controller,404 不再成立)—— 属 D-P8-01 最小修复范畴(注释适配,行为等价)。

### Claude's Discretion

- **三族代表端点具体清单** —— researcher 据 07-ROUTE-MATRIX.md §2 实测每族挑 1-2 个覆盖不同 controller + HTTP method 的端点;D-P8-03 给判据(非404 即过)不给枚举。
- **runtime 暴露问题的具体根因 + 最小修复 diff 边界** —— researcher/executor 实测启动日志后逐项定;D-P8-01 给预算(最小适配到 GREEN,标根因 + 行为等价性),不给修复清单。
- **WX bean-resolve probe 选哪 1-2 个 I*Client** —— researcher 实测 3 契约(`IWxUserClient`/`IWxUserDetailClient`/`ISaveTempClient`)哪个最稳解析(优先选被最多 service 调用的);D-P8-04 给结构不给具体类。
- **probe 落位 + 命名** —— 落 `ZgbasApplicationTest.java`,命名延续既有 `*probe` / `*Wiring_probe` 约定(如 `wxPurchaseFeignSelfLoopbackWiring_probe`、`wxEndpointsReachable_proof`),planner 定精确名。
- **dev DB 前置的具体确认步骤** —— researcher/planner 在 PLAN 显式列(sptbasdb_pd 可达 / sys_job 53 行 / 实体表),执行者复跑前核对。

### Folded Todos

(无 —— cross-reference 命中的 2 个 todo(`rotate-leaked-prod-credentials` / `phase4-resolve-entity-schema-drift`)均 v1.0/v1.3 遗留,非 Phase 8 验证 scope,未 fold。与 P5/P6/P7 一致。见 `<deferred>` Reviewed Todos。)

</decisions>

<canonical_refs>
## Canonical References

**Downstream agents MUST read these before planning or implementing.**

### 需求 / Roadmap / 状态
- `.planning/ROADMAP.md` §Phase 8 — Goal + 4 条 Success Criteria(`mvn compile` 全模块零错 / ZgbasApplicationTest 启动 GREEN 含 WX beans / 关键 WX 端点非 404 / WX Feign 自回环 proof)
- `.planning/REQUIREMENTS.md` — **WX-ALIGN-01**(mvn compile 全模块零错)、**WX-ALIGN-02**(ZgbasApplicationTest GREEN 含 WX beans)、**WX-ALIGN-03**(关键 WX 端点非 404)
- `.planning/STATE.md` — 方案1 锁定 + 沿用 D-01~D-19 + 🔴 Blockers(`/wx/contract` runtime 启动 proof 留 P8)+ Deferred(quartz gap-closure / schema drift → v1.3;JWT/Shiro 统一 → future)
- `.planning/phases/07-bff-edge/07-VERIFICATION.md` — **直接前置**。§"Human Verification / Deferred to Phase 8(runtime)" 列 5 项 runtime 留 P8(启动 GREEN 无 AmbiguousMappingException/ConflictingBeanDefinitionException;/wx 非 404;ContractController 透传链自回环;swagger auto-config 副作用;P8 回退预案)
- `.planning/phases/07-bff-edge/07-06-SUMMARY.md` — P7 runtime gotcha ①swagger 注解依赖 ②BasicErrorController excludeFilter ③ErrorResp.setTimestamp LocalDateTime 适配(本阶段 runtime 复核点)
- `.planning/phases/07-bff-edge/07-ROUTE-MATRIX.md` — 三族端点归一化表(§2 inventory = D-P8-03 抽样来源;§3/§4 无 ambiguous mapping 静态证明)
- `.planning/phases/06-service/06-CONTEXT.md` — P6 gotcha:executor 自报 GREEN 假阳性(D-P8-02 防假阳性依据)
- `.planning/phases/05-carrier-layer/05-CONTEXT.md` — D-P5-09 ApplicationStartup 并入单体(BsDictUtil.init,Phase 3 登录缺口同源,启动 GREEN 依赖)

### 关键单体文件(验证/落位/参照)
- `zgbas-admin/src/test/java/com/spt/ZgbasApplicationTest.java` — **本阶段主战场**(1029 行)。已有 `wxInfrastructureBeans_phase4_probe`(SC-1~4,WX bean 装配,active)、`feignSelfLoopbackWiring_probe`/`reportFeignSelfLoopbackWiring_probe`(自回环前例)、`basContractEndpointReachable_*`/`reportHttpReachability_proof`(reachability 前例)、`schedulerLoadAllJobs_proof`(53 sys_job,always-on)。**缺口**:WX 端点 reachability probe + WX 自回环 probe(D-P8-03/04 落点)
- `zgbas-admin/src/main/java/com/spt/ZgbasApplication.java` — boot 类;`@EnableFeignClients` 已含 `com.spt.bas.purchase.wx.client.remote`(L130);5 个 `BasicErrorController` excludeFilter(L99-104,含 WX L103);**stale 注释 L131-134 待清理(D-P8-04)**
- `zgbas-system/src/main/java/com/spt/bas/purchase/wx/client/config/PurchaseWxClientConfig.java` — WX 自回环 config(bean `purchaseWxServerConfig`,urlKey `spt.bas.purchase.wx.url`,D-P8-04 probe 目标)
- `zgbas-system/src/main/java/com/spt/bas/purchase/wx/client/constant/PurchaseWxConstant.java` — `SERVER_BEAN_NAME=purchaseWxServerConfig` / `SERVER_URL=#{purchaseWxServerConfig.url}` / `SERVER_URL_KEY=spt.bas.purchase.wx.url`
- `zgbas-system/src/main/java/com/spt/bas/purchase/wx/client/remote/{IWxUserClient,IWxUserDetailClient,ISaveTempClient}.java` — 3 WX Feign 契约(D-P8-04 proxy 解析目标)
- `zgbas-system/src/main/java/com/spt/bas/client/config/ReportFeignPathConfig.java` + `BasFeignPathConfig`(同包) — 自回环 PathConfig 前例(WX 无独立 PathConfig,WX 端点裸 `/wx/*` 不加前缀)
- `zgbas-admin/src/main/resources/application-dev.yml` — dev 明文 profile;`spt.bas.purchase.wx.url: http://localhost:8080`(L53);**stale 注释 L49-51 待清理(D-P8-04)**
- `zgbas-admin/src/main/resources/application-prod.yml` — `${DB_PASSWORD}`/`${SPT_APP_SECRET}` 占位仅在此(D-P2-13,dev 无占位 → D-P8-02 export 不再需)
- `zgbas-admin/src/main/java/com/spt/bas/purchase/wx/server/controller/` — P7 迁入 11 WX controller(/wx/* + /ewechat/* + /axq/*,D-P8-03 reachability 目标)

### v1.0 验证里程碑参照(行为对齐验证前例)
- v1.0 Phase 7「行为对齐验证」(8 plans,2026-07-20 archived)—— 验证 + 最小修复里程碑的既有范式;本阶段(8-对齐验证)为其在 v1.2 WX 维度的延续。详见 `.planning/milestones/v1.0-ROADMAP.md`(如需对照验收深度)

</canonical_refs>

<code_context>
## Existing Code Insights

### Reusable Assets
- `ZgbasApplicationTest.java` 已有全部验证原语:`@SpringBootTest(RANDOM_PORT)` + `@ActiveProfiles("dev")` + `TestRestTemplate`(HTTP reachability)+ `ApplicationContext`(bean 解析)+ `RequestMappingHandlerMapping` + `MockHttpServletRequest`(路由命中判定)+ `ReflectionTestUtils`(field swap)。本阶段 probe 全部复用这些原语,无需新框架。
- `wxInfrastructureBeans_phase4_probe`(active)—— **SC#2 WX bean 装配已证**(RedisTemplate/JwtConfig key="sgcoding"/WxMaService/JwtFilter `/wx/*`);本阶段 SC#2 主要是"独立复跑确认仍 GREEN"+ 补 reachability/self-loop probe。
- bas/report 自回环 probe + reachability proof —— WX 对应 probe 的 1:1 模板(D-P8-03/04 直接镜像结构,换 WX 类/路径)。
- 11 WX controller + 4 api + BaseController(P7 已落 admin)+ 3 WX Feign 契约(P3 已落 system)+ `PurchaseWxClientConfig`(P3)—— WX 端点 + 自回环 wiring 依赖全就位。

### Established Patterns
- **验证 = 编译门迭代回流 + 最小修复(D-P8-01)** —— P5/P6/P7 全用此式;P8 把它从"编译门"扩到"启动门 + reachability 门",修复风格不变(标根因 + 行为等价)。
- **probe 二分:always-on fail-fast wiring vs @Disabled 手动真跑** —— 既有测试严格遵循(D-P6-06-01 纪律)。本阶段新增的 WX reachability + WX 自回环 probe 均 **always-on**(对齐 bas/report reachability/probe 前例,属 wiring 级,不需 @Disabled)。
- **reachability 判据:404=失败,其余=registered** —— `basContractEndpointReachable_*` / `reportHttpReachability_proof` 一致;WX 延用(D-P8-03)。
- **明文密钥进 dev yml(D-P4)+ dev 无 `${}` 占位** —— D-P8-02 实测确认 export 不再需(修正 STATE.md 旧表述)。
- **excludeFilters 防同名 bean** —— 5 个 `BasicErrorController` 已排除(ZgbasApplication L99-104);P8 启动若再暴露同名 bean 冲突,沿用 assignable-type exclude 模式(D-P8-01 最小修复)。

### Integration Points
- `ZgbasApplication`(`@SpringBootApplication`)—— `@ComponentScan("com.spt")` 自动扫入 WX `@RestController` + `PurchaseWxClientConfig`(@Configuration);`@EnableFeignClients` 已含 WX 契约包。
- `RequestMappingHandlerMapping` —— 启动注册全部 RequestMapping;WX probe 经 `TestRestTemplate` 打 `/wx/*` 证非404;07-ROUTE-MATRIX 静态证明无 ambiguous(P8 runtime 复核)。
- WX Feign 自回环 —— `IWxUserClient` 等经 `PurchaseWxClientConfig` 走 `localhost:8080` 同进程直调(runtime proof = bean-resolve,D-P8-04)。
- dev DB `sptbasdb_pd` —— SC#2 硬前置(Druid + 53 sys_job + 239 实体表);非 hermetic,文档化(D-P8-02)。

</code_context>

<specifics>
## Specific Ideas

- 用户对 4 个灰区全部选**推荐项**,一致延续 P3-P7 决策风格:行为等价优先于 verbatim 字面 / 最低修复级联风险 / 不扩 scope / 沿用既有测试模式(probe + reachability)。
- 用户明确 **P8 = 验证 + 最小修复**,不是"顺手根治"也不是"验证为主 defer 修复" —— 北极星"单进程跑全功能"要求四层闸门必须 GREEN,但深层业务正确性留手动 UAT/v1.3。
- 防假阳性(P6 教训)是用户隐含强约束:SC#2 GREEN **必须独立复跑证实**,executor 自报不作唯一证据(D-P8-02)。
- WX 端点判据延用既有 reachability 模式(非404 即过),不在 reachability 层纠结业务 500 —— 与 bas/report reachability 测试严格一致,信号清晰。

</specifics>

<deferred>
## Deferred Ideas

- 深层行为等价(端点业务正确性 / 多步流程 / WX 小程序端到端)→ 手动 UAT / v1.3(D-P8-01)
- basWx 微信采购小程序前端(管理页/UI)→ v2(纯 API 服务,v1.2 不迁)
- JWT/Shiro 认证统一 → future(身份模型分离,方案1 不做)
- basWx Feign 自回环崩为直注 → future(方案1 保留)
- xxl-job 残留(`PurchaseCommand` 定时触发 + `SignContractTask` 3 job)→ v1.3 quartz gap-closure(D-P5-06/D-P5-11/D-P6-02)
- 真实 HTTP 自回环 round-trip proof(WX controller 首次可行)→ future(③-A + ④-A 组合已充分,D-P8-04-rej-C)
- **v1.2 里程碑归档 + tag** → `/gsd-complete-milestone` 命令范畴(P8 GREEN 后触发,非 Phase 8 代码 scope)

### Reviewed Todos (not folded)
- `rotate-leaked-prod-credentials`(high)—— v1.0 遗留;P5 新增 EweChat/JinXin/OCR 明文密钥后尤需关注。**非 Phase 8 验证 scope**(密钥轮换是运维动作,非四层闸门),但 P8 独立复跑启动时会真实加载这些密钥 —— 若复跑暴露密钥相关问题须登记;轮换本身留 todo 跟进(建议 P8 后、里程碑归档前处理)。
- `phase4-resolve-entity-schema-drift`(medium)—— v1.0/v1.2 遗留(ddl-auto=validate,259 表)。P8 启动用 ddl-auto=none 不触发;schema drift 修复留 v1.3,非验证 scope。

</deferred>

---

*Phase: 8-对齐验证*
*Context gathered: 2026-07-24*
