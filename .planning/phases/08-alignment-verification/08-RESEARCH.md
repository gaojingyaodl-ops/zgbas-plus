# Phase 8: 对齐验证 — Technical Research

**Researched:** 2026-07-24 (inline — GSD researcher subagent unavailable, GLM gateway outage)
**Answers:** "What do I need to know to PLAN Phase 8 well?"
**Inputs:** 08-CONTEXT.md (4 灰区 D-P8-01~04 locked), 07-ROUTE-MATRIX.md, ZgbasApplicationTest.java(实测 probe 原语), PurchaseWxClientConfig/PurchaseWxConstant(实测), 11 WX controller/api(实测 @RequestMapping)

---

## 0. 一句话结论

Phase 8 是**验证 + 最小修复**里程碑,代码增量极小(2 个新 probe + 2 处 stale 注释清理),主体是**跑通四层闸门并最小修复 runtime 暴露的问题**。所有验证原语已在 `ZgbasApplicationTest.java` 就位(1029 行),新 probe 1:1 镜像既有 `reportFeignSelfLoopbackWiring_probe` / `reportHttpReachability_proof`,无新框架、无新依赖。三族代表端点 + 最常调用的 WX I*Client 已 grep 实测锁定。

---

## 1. 新 probe 1:1 镜像规范(anti-shallow — 精确到方法签名)

### 1.1 WX Feign 自回环 bean-resolve probe(D-P8-04 → SC#4)

**镜像源:** `reportFeignSelfLoopbackWiring_probe`(`ZgbasApplicationTest.java:315-344`,三段断言:PathConfig bean → I*Client proxy → url 含 localhost:8080)。

**WX 侧三段断言(实测对应):**

| # | report 侧(镜像源) | WX 侧(本阶段) | 实测依据 |
|---|---|---|---|
| 1 | `context.getBean(ReportFeignPathConfig.class)` | `context.getBean("purchaseWxServerConfig", LocalServerConfig.class)` 解析 | `PurchaseWxClientConfig.localServerConfig()` `@Bean(SERVER_BEAN_NAME="purchaseWxServerConfig")`;WX **无独立 PathConfig**(裸路径不前缀,D-P7-01) |
| 2 | `context.getBean(IRptFundReceivableStatisticsClient.class)` proxy 解析 | `context.getBean(IWxUserDetailClient.class)` + `context.getBean(ISaveTempClient.class)` 两 proxy 解析 | `@EnableFeignClients` 已含 `com.spt.bas.purchase.wx.client.remote`(`ZgbasApplication.java:130`);`@FeignClient(url=SERVER_URL="#{purchaseWxServerConfig.url}")` SpEL 解析 |
| 3 | `reportServerConfig.getUrl()` contains `localhost:8080` | `purchaseWxServerConfig.getUrl()` contains `localhost:8080` | urlKey `spt.bas.purchaseWx.url`(`PurchaseWxConstant:12`)→ `application-dev.yml` `spt.bas.purchaseWx.url: http://localhost:8080` |

**I*Client 选型(D-P8-04「优先被最多 service 调用」):**
- ✅ `IWxUserDetailClient` — **~15 文件引用**(最常调用,实测 grep)
- ✅ `ISaveTempClient` — **~5 文件引用**(次常)
- ⚪ `IWxUserClient` — ~0(声明存在,直接引用稀少,不选)

**probe 命名(Claude's Discretion 锁定):** `wxPurchaseFeignSelfLoopbackWiring_probe`(延续 `*Wiring_probe` 约定)。
**落位:** `ZgbasApplicationTest.java`(与 `reportFeignSelfLoopbackWiring_probe` 同文件,新区段注释标 D-P8-04)。

### 1.2 WX 三族端点 reachability probe(D-P8-03 → SC#3)

**镜像源:** `reportHttpReachability_proof`(`ZgbasApplicationTest.java:421-433`,`restTemplate.postForEntity` + `assertThat(statusCodeValue).isNotEqualTo(404)`)。

**判据(D-P8-03 锁定):** HTTP `!= 404` 即 pass(`404`=未注册=失败;`2xx/3xx/401/403/400/500`=handler 命中=pass)。WX 走 `JwtAuthenticationFilter`(order=1,`/wx/* /ewechat/* /axq/*`),未认证预期 `401`/错误信封,均非 404 = pass。

**三族 + api 代表端点(grep 实测 `@*Mapping` 锁定,覆盖不同 controller + HTTP method):**

| 族 | 端点(实测) | Controller/Api | 实测映射 |
|---|---|---|---|
| `/wx/*` | `POST /wx/user/login` | WxUserController | class `@RequestMapping("/wx/user")` + `@PostMapping("/login")` |
| `/ewechat/*` | `POST /ewechat/buyEnquiry/getProductTree` | BuyEnquiryController | class `/ewechat/buyEnquiry` + `@PostMapping("/getProductTree")` |
| `/axq` | `POST /axq/doSuccessContract` | OtherController | class `/axq` + `@PostMapping("/doSuccessContract")` |
| `/purchase/*` api | `POST /purchase/user/saveApplyOnLineData` | WxUserApi | class `purchase/user`(无前导斜杠) + `@RequestMapping("saveApplyOnLineData")` |

> 覆盖度核对(D-P8-03「每族 1-2 个覆盖不同 controller + method」):4 端点 = 3 族(/wx + /ewechat + /axq)各 1 + /purchase api 族 1,分属 4 个不同 controller/api 类,满足"不仅典型端点"(否决 ③-C)且"不全端点矩阵"(否决 ③-B)。
> 执行者自由度:若某端点 null body 致 500,仍非 404 = pass(D-P8-03);若实测命中真 404(路径笔误),替换为同族同 controller 的确认端点(Claude's Discretion)。

**probe 命名(锁定):** `wxEndpointsReachable_proof`(延续 `*Reachable_*`/`*Reachability_proof` 约定)。
**落位:** `ZgbasApplicationTest.java` 新区段,标 D-P8-03。

---

## 2. WX 自回环 config 实测机制(D-P8-04 依据)

```
PurchaseWxClientConfig (@Configuration, @DependsOn("propertiesUtil"))
  └─ @Bean("purchaseWxServerConfig") LocalServerConfig{ urlKey = "spt.bas.purchaseWx.url" }

PurchaseWxConstant:
  SERVER_NAME       = "purchase-wx-server"
  SERVER_BEAN_NAME  = "purchaseWxServerConfig"
  SERVER_URL        = "#{purchaseWxServerConfig.url}"   ← SpEL,Feign proxy 创建期解析
  SERVER_URL_KEY    = "spt.bas.purchaseWx.url"

@FeignClient(IWxUserDetailClient / ISaveTempClient):
  name = SERVER_NAME, path = SERVER_NAME+"/purchase/{userDetail|saveTemp}",
  url  = SERVER_URL, configuration = FeignConfig.class

application-dev.yml: spt.bas.purchaseWx.url: http://localhost:8080   ← LocalServerConfig.getUrl() 读此
```

→ bean-resolve probe 证三件事:① bean `purchaseWxServerConfig` 注册;② SpEL `#{purchaseWxServerConfig.url}` 在 Feign proxy 创建期解析(否则 `SpelEvaluationException` fail-fast);③ url 解析为 localhost:8080。与 report 侧 `reportServerConfig` 同构。

> **关键变化(D-P8-04 锁定):** P7 迁了 WX controller,真实 HTTP 自回环首次可行 —— 但 ③-A 已在 HTTP 层证 WX controller 可达(非404),故 ④-A 只需 bean-resolve 证 wiring,无需真 round-trip(否决 ④-B/④-C)。

---

## 3. Stale 注释清理(D-P8-04 / D-P8-01 最小修复范畴)

P7 已迁 11 WX controller + 4 api,`/wx/*`/`/ewechat/*`/`/axq/*` 端点真实可达 —— 但两处注释仍写"basWx v2-deferred 404",**stale**(P3/P4 写,当时 controller 未迁)。

| 文件 | 行 | stale 内容(实测) | 修正方向 |
|---|---|---|---|
| `application-dev.yml` | 49-51 | `# basWx itself is v2-deferred ... proxies self-loop to localhost:8080 where no @RestController handles them → runtime calls 404 (D-P4-02 lazy-degradation)` | 改注:P7 已迁 WX controller,自回环非 404;保留 url 不动 |
| `ZgbasApplication.java` | 131-134 | `@EnableFeignClients` WX 包注释:`// basWx itself is v2-deferred ... no @RestController impl exists → runtime calls 404 (D-P4-02 lazy-degradation for v2 contracts)` | 同上,改注 controller 已落 admin |

**规则(D-P8-01):** 仅改注释文字,行为等价;url `http://localhost:8080` 与 `@EnableFeignClients` 包名**不动**。每处标根因 + 行为等价(对齐 P7-06 `BasicErrorController` timestamp 适配前例)。

---

## 4. 四层闸门执行命令 + 防假阳性(D-P8-02)

### 4.1 全 reactor 编译门(SC#1, WX-ALIGN-01)

```bash
JAVA_HOME=/Library/Java/JavaVirtualMachines/amazon-corretto-1.8.jdk/Contents/Home \
  /Users/alan/App/apache-maven-3.8.6/bin/mvn compile \
  -s /Users/alan/App/apache-maven-3.8.6/zg_settings.xml
```
(全 reactor,无 `-pl`,验证 5 模块 admin/common/framework/quartz/system 全绿)

**关键:** locale 无关 grep — 中文 locale 下 mvn 输出"找不到符号",verify grep 必须 `cannot find symbol|找不到符号`(P5 gotcha,记忆已记)。

### 4.2 启动测试独立复跑(SC#2, WX-ALIGN-02)

```bash
JAVA_HOME=<Corretto-1.8> \
  /Users/alan/App/apache-maven-3.8.6/bin/mvn test \
  -pl zgbas-admin -am \
  -s /Users/alan/App/apache-maven-3.8.6/zg_settings.xml \
  -Dtest=ZgbasApplicationTest
```

**防假阳性(D-P8-02 强制):**
- `application-dev.yml`(测试 `@ActiveProfiles("dev")` 所读)已明文 localhost:8080 URL + 明文密钥(D-P4)→ **`export DB_PASSWORD/SPT_APP_SECRET` 不再需要**(修正 STATE.md line 83 旧表述)。
- 但测试连真实 dev DB `sptbasdb_pd`(Druid + 53 `sys_job` 种子 + 239 实体表 ddl-auto=none)→ **dev DB 可达是硬前置**(非 hermetic,文档化)。
- SC#2 GREEN **必须用户本地独立复跑证实**,executor 自报不作为唯一证据(P6 executor 自报 GREEN 曾内联泄漏密钥假绿)。
- always-on `@Test`(bean 装配 + reachability + scheduler 载入)全绿(`0 failures/0 errors` + 启动日志无 `FAILED`/`Exception`);`@Disabled` 真跑/写类手动启用,不计 SC#2 硬闸门。

**dev DB 前置(PLAN 必列):** `sptbasdb_pd` 可达 + sys_job 53 行(3 RyTask + 50 prod)+ 239 实体表存在。执行者复跑前确认。

### 4.3 reachability + 自回环(SC#3/#4)

由 4.2 的 `mvn test` 同时执行(新 probe `wxEndpointsReachable_proof` + `wxPurchaseFeignSelfLoopbackWiring_probe` 为 always-on `@Test`)。GREEN 即证 SC#3/#4。

---

## 5. runtime 修复预算 + 回流风格(D-P8-01)

**风格:** 沿用 P5/P6/P7 编译门迭代回流,但 P8 把"编译门"扩到"启动门 + reachability 门"。每 runtime 失败做**最小适配**,不扩 scope、不动业务语义。

**预期 runtime 暴露点(P7 deferred,本阶段 watchlist):**
1. **swagger-bootstrap-ui 1.9.6 auto-config 副作用**(07-06 入 classpath)→ 可能触发 auto-config bean 冲突 / NPE
2. **5 个 `BasicErrorController` excludeFilter**(ZgbasApplication L99-104)是否真消解同名 bean → 启动若再暴露 `ConflictingBeanDefinitionException`,沿用 assignable-type exclude
3. **`/wx/contract` ambiguous mapping**(07-ROUTE-MATRIX 静态证已前缀隔离)→ 启动若仍报 `AmbiguousMappingException`,启用 07-ROUTE-MATRIX §6 回退预案(report 侧 sub-path delta)
4. **WX 自回环 SpEL / bean 缺失** → `purchaseWxServerConfig` 未注册或 SpEL 解析失败
5. **循环依赖 / 缺 bean / NPE** → 启动期任意 bean 装配失败

**修复边界(D-P8-01):**
- ✅ 仅修"使四层闸门 GREEN 所必需"的 runtime 问题(改注释 / 扩 excludeFilter / 补缺 bean / 适配签名 / lazy-degradation)
- ✅ 每处修复标注根因 + 是否行为等价(对齐 P7-06 `ErrorResp.setTimestamp` LocalDateTime 适配前例)
- ❌ 不顺手根治(否决 ①-B:膨胀 / 撞 dev DB/业务 / 推迟 milestone)
- ❌ 不"验证为主 defer 修复"(否决 ①-C:验证里程碑沦为"启动崩溃记录")
- 深层行为等价(端点业务正确性 / 多步流程 / WX 小程序端到端)→ 手动 UAT / v1.3

---

## 6. 既有可复用资产(零新框架)

| 资产 | 位置 | 本阶段用途 |
|---|---|---|
| `@SpringBootTest(RANDOM_PORT)` + `@ActiveProfiles("dev")` + `TestRestTemplate` + `ApplicationContext` | ZgbasApplicationTest 头 | 新 probe 直接复用注入字段 `context` / `restTemplate` |
| `wxInfrastructureBeans_phase4_probe`(L935,active) | 同文件 | **SC#2 WX bean 装配已证**(RedisTemplate/JwtConfig key="sgcoding"/WxMaService/JwtFilter `/wx/*`);P8 主要"独立复跑确认仍 GREEN" |
| `reportFeignSelfLoopbackWiring_probe`(L315) | 同文件 | D-P8-04 1:1 镜像源 |
| `reportHttpReachability_proof`(L421) | 同文件 | D-P8-03 1:1 镜像源 |
| 11 WX controller + 4 api + BaseController(P7)+ 3 WX Feign 契约(P3)+ PurchaseWxClientConfig(P3) | zgbas-admin/system | WX 端点 + 自回环 wiring 依赖全就位 |
| `schedulerLoadAllJobs_proof`(L517,always-on,53 sys_job) | 同文件 | SC#2 启动 + scheduler 载入 fail-fast |

---

## 7. PLAN 结构建议(3 plans / 3 waves)

| Plan | Wave | SC | REQ | autonomous | 内容 |
|---|---|---|---|---|---|
| 08-01 | 1 | SC#3/#4 代码 + D-P8-04 注释 | WX-ALIGN-03 | true | 新增 `wxEndpointsReachable_proof` + `wxPurchaseFeignSelfLoopbackWiring_probe`(2 always-on `@Test`)+ stale 注释清理(dev yml L49-51 + ZgbasApplication L131-134) |
| 08-02 | 2 | SC#1 | WX-ALIGN-01 | true | 全 reactor `mvn compile` + `mvn test-compile`(证新 probe 编译)零 `[ERROR]`,depends 08-01 |
| 08-03 | 3 | SC#2 + SC#3/#4 evidence | WX-ALIGN-02 + WX-ALIGN-03 | **false** | 独立 `mvn test -pl zgbas-admin -am` 复跑 + runtime fix-to-GREEN 回流(D-P8-01);**用户本地独立复跑证实**(D-P8-02 防假阳性);depends 08-02 |

**08-03 autonomous=false 理由:** PASS 依赖①dev DB 可达(非 hermetic)②open-ended runtime 修复(Claude's Discretion)③**用户独立复跑**为最终权威(D-P8-02,P6 假阳性教训)→ 不应被 execute-phase 自动盖章,需 checkpoint 让用户独立复跑。

**REQ 覆盖核对:** WX-ALIGN-01→08-02 ✓;WX-ALIGN-02→08-03 ✓;WX-ALIGN-03→08-01 + 08-03 ✓(SC#3「非404」+ SC#4「自回环 proof」均归 WX-ALIGN-03)。3/3 REQ 全覆盖。

---

## 8. Validation Architecture(Nyquist Dimension 8)

本阶段为**验证里程碑**,非功能开发。Validation = 四层闸门本身(编译门 / 启动门 / reachability 门 / 自回环 wiring 门),采样策略 = 三族代表端点 + 最常调用 I*Client(D-P8-03/04 锁定)。无新增业务逻辑需 unit/integration 覆盖;新 probe 即验证资产(always-on fail-fast wiring,对齐 D-P6-06-01 纪律)。VALIDATION.md 不另建(验证策略已固化在 PLAN acceptance_criteria + RESEARCH §1/§4)。

---

## 9. 风险登记

| 风险 | 概率 | 影响 | 缓解 |
|---|---|---|---|
| dev DB 不可达 → SC#2 无法复跑 | 中 | 高(阻塞 SC#2) | PLAN 显式列前置;执行者复跑前确认 sptbasdb_pd + 53 sys_job;不可达则 STOP 报告 |
| runtime 暴露非最小问题(撞业务/DB) | 低 | 高(推迟 milestone) | D-P8-01 边界:仅修四层闸门 GREEN 所需;深层 defer v1.3 |
| executor 自报假绿(P6 教训) | 中 | 高(假阳性) | D-P8-02:GREEN 必须用户独立复跑;08-03 autonomous=false 强制 checkpoint |
| swagger auto-config 启动副作用 | 中 | 中 | watchlist #1;最小适配(排除/skip)记根因 |

---

*Research 以 08-CONTEXT.md(4 灰区锁定)+ 实测源码为准。3 plans / 3 waves 结构对齐 P5/P6/P7 既有范式。*
