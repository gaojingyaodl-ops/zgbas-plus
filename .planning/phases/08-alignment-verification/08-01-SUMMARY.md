---
plan: 08-01
phase: 8
wave: 1
status: complete
sc: [SC#3 code, SC#4 code]
requirements: [WX-ALIGN-03]
decisions: [D-P8-03, D-P8-04, D-P8-01]
committed: 2f7adc9
---

# 08-01: WX 端点 reachability probe + WX 自回环 bean-resolve probe + stale 注释清理

## What was built

Phase 8 第一波(纯加性代码):把 P5/P6/P7 显式留到 P8 的两道 runtime 缺口补成 always-on fail-fast `@Test`,并清理两处"basWx v2-deferred 404"stale 注释(P7 已迁 controller)。两 probe 1:1 镜像既有 `reportHttpReachability_proof` / `reportFeignSelfLoopbackWiring_probe`,无新框架、无新依赖。

| # | 方法 | 镜像源 | 判据 | 决策 |
|---|---|---|---|---|
| 1 | `wxEndpointsReachable_proof` | `reportHttpReachability_proof`(L420-433) | 4 代表端点 POST `!= 404` | D-P8-03 |
| 2 | `wxPurchaseFeignSelfLoopbackWiring_probe` | `reportFeignSelfLoopbackWiring_probe`(L315-344) | 三段:bean + 2 Feign proxy + url localhost:8080 | D-P8-04 |

### Task 1 — 两个 always-on `@Test`(落位于 `wxInfrastructureBeans_phase4_probe` 之后)

**`wxEndpointsReachable_proof`** — 4 代表端点 POST 后逐个 `assertThat(statusCodeValue).isNotEqualTo(404)`,覆盖三族 + api 族 / 4 不同 controller(实测 grep 锁定 + 类级 `@RequestMapping` 二次确认有效路径):

| 族 | 端点 | Controller/Api | 实测映射 |
|---|---|---|---|
| `/wx/*` | `POST /wx/user/login` | WxUserController | `@RequestMapping("/wx/user")` + `@PostMapping("/login")` |
| `/ewechat/*` | `POST /ewechat/buyEnquiry/getProductTree` | BuyEnquiryController | `@RequestMapping("/ewechat/buyEnquiry")` + `@PostMapping("/getProductTree")` |
| `/axq` | `POST /axq/doSuccessContract` | OtherController | `@RequestMapping("/axq")` + `@PostMapping("/doSuccessContract")` |
| `/purchase/*` api | `POST /purchase/user/saveApplyOnLineData` | WxUserApi | `@RequestMapping("purchase/user")` + `@RequestMapping("saveApplyOnLineData")` |

判据 HTTP `!= 404`(WX JwtFilter 未认证预期 401/错误信封,均非 404 = handler 命中)。

**`wxPurchaseFeignSelfLoopbackWiring_probe`** — 三段断言:
1. `context.getBean("purchaseWxServerConfig", LocalServerConfig.class)` 非 null —— 证 `PurchaseWxClientConfig`(`@Bean(SERVER_BEAN_NAME="purchaseWxServerConfig")`)被 `com.spt` ComponentScan 扫入
2. `context.getBean(IWxUserDetailClient.class)` + `context.getBean(ISaveTempClient.class)` 两 Feign proxy 非 null —— 证 `@EnableFeignClients` 含 `com.spt.bas.purchase.wx.client.remote` + SpEL `#{purchaseWxServerConfig.url}` 在 proxy 创建期解析(两 client 为最常调用,~15/~5 service 引用)
3. `purchaseWxServerConfig.getUrl()` contains `localhost:8080` —— 证 `spt.bas.purchaseWx.url` 经 `PurchaseWxClientConfig.setUrlKey` 读入并传播

复用既有注入字段 `context`(ApplicationContext)+ `restTemplate`(TestRestTemplate),**无新 `@Autowired` / 无新 import 框架依赖**;WX `I*Client` 用全限定名(对齐 report probe `IRptFundReceivableStatisticsClient` 全限定名风格)。

### Task 2 — 两处 stale 注释清理(D-P8-01 最小修复,行为等价)

| 文件 | stale 内容(实测) | 修正 |
|---|---|---|
| `application-dev.yml` L47-53 | "basWx itself is v2-deferred ... no @RestController handles them → runtime calls 404 (D-P4-02 lazy-degradation)" | 改注 P7 已迁 WX controller,自回环非 404,URL 不变 |
| `ZgbasApplication.java` L130-134 | `@EnableFeignClients` WX 包注释"basWx ... no @RestController impl exists → runtime calls 404 (D-P4-02 ...)" | 改注 P7 已迁 controller,bean `purchaseWxServerConfig` + SpEL 解析 |

**行为等价锁:** `bas.purchaseWx: url: http://localhost:8080`(yml)与 `@EnableFeignClients(... "com.spt.bas.purchase.wx.client.remote" ...)`(包名)零改动;diff 仅注释行。

## key-files.created
- `zgbas-admin/src/test/java/com/spt/ZgbasApplicationTest.java`(+2 always-on `@Test`)
- `zgbas-admin/src/main/resources/application-dev.yml`(注释适配)
- `zgbas-admin/src/main/java/com/spt/ZgbasApplication.java`(注释适配)

## Decisions applied
- **D-P8-03** — WX 三族端点 reachability probe:4 端点覆盖三族 + api 族 / 4 controller,判据 `!= 404`,命名 `wxEndpointsReachable_proof`
- **D-P8-04** — WX 自回环 bean-resolve probe:三段(bean + 2 proxy + url),命名 `wxPurchaseFeignSelfLoopbackWiring_probe`
- **D-P8-01** — 最小修复范畴:仅注释适配(stale 404 → P7 已迁 controller),标根因 + 行为等价,url/包名零改动

## Self-Check: PASSED(代码落位层)

静态验证(acceptance_criteria 全绿):
- `grep -cE 'void wxEndpointsReachable_proof|void wxPurchaseFeignSelfLoopbackWiring_probe' ZgbasApplicationTest.java` = 2 ✓
- 4 代表端点路径字符串齐全(逐个 `isNotEqualTo(404)`)✓
- 三段自回环断言齐全(purchaseWxServerConfig bean + IWxUserDetailClient + ISaveTempClient + url localhost:8080)✓
- 两新方法均无 `@Disabled` ✓
- 无新 `@Autowired`(复用 context + restTemplate)✓
- stale 404 措辞清零:`grep -cE 'v2-deferred.*404|no @RestController.*404'` = 0/0 ✓
- url `http://localhost:8080` + `@EnableFeignClients` 包名零改动 ✓
- 端点有效路径二次确认(类级 + 方法级 `@RequestMapping` 实测),防 probe 假性真 404

## Notable deviations
无。verbatim 镜像既有 probe + 注释适配,无签名/语义偏离。

## Gaps / Deferred
- probe 实际 GREEN 证据(runtime `mvn test` 中两 probe 非 SKIPPED/非失败)由 **08-02** test-compile(证 probe 编译)+ **08-03** 独立复跑 `mvn test`(证 probe GREEN,SC#3/#4 evidence)产出 —— 本 plan 只证代码落位正确。
- 若 08-03 独立复跑暴露某端点真 404(路径笔误,理论不应 —— 类级/方法级映射已二次确认),按 D-P8-03 Claude's Discretion 替换为同族同 controller 确认端点。

## What this enables
为 08-02 test-compile 提供"probe 编译通过"目标,为 08-03 `mvn test` 复跑提供 SC#3/#4 的可执行断言。stale 注释清理属 D-P8-01 最小修复范畴,收口 P3/P4 写、P7 已失效的注释漂移。
