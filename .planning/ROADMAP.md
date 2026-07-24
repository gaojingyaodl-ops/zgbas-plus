# ROADMAP: zgbas-plus

## Milestones

- ✅ **v1.0 单体化重构交付** — Phases 1–7 (shipped 2026-07-20)
- ✅ **v1.1 quartz 功能完善** — Phases 1–2 (shipped 2026-07-22)
- 🚧 **v1.2 basWx 迁入** — Phases 3–8 (active, forward work replanned 2026-07-23)

---

## v1.0 & v1.1 Archived Phases

<details>
<summary>✅ v1.0 单体化重构交付 (Phases 1–7) — SHIPPED 2026-07-20</summary>

- [x] Phase 1: 编译止血 + 骨架 (1/1 plans) — completed 2026-07-16
- [x] Phase 2: 基础设施 (6/6 plans) — completed 2026-07-16
- [x] Phase 3: 认证首页 (4/4 plans) — completed 2026-07-17
- [x] Phase 4: 核心业务迁移 (6/6 plans) — completed 2026-07-17
- [x] Phase 5: 报表迁移 (6/6 plans) — completed 2026-07-18
- [x] Phase 6: quartz 后端迁移 (6/6 plans) — completed 2026-07-20
- [x] Phase 7: 行为对齐验证 (8/8 plans) — completed 2026-07-20

Full details: [milestones/v1.0-ROADMAP.md](milestones/v1.0-ROADMAP.md)

</details>

<details>
<summary>✅ v1.1 quartz 功能完善 (Phases 1–2) — SHIPPED 2026-07-22</summary>

- [x] Phase 1: 前端接口对齐 + 400 修复 (3/3 plans) — completed 2026-07-22
- [x] Phase 2: 调度日志页面 + Thymeleaf 辅助 Bean (3/3 plans) — completed 2026-07-22

Full details: [milestones/v1.1-ROADMAP.md](milestones/v1.1-ROADMAP.md)

</details>

---

## v1.2 basWx 迁入 — Active Phases

> **Replanned 2026-07-23.** 方案 = 完成 verbatim 嵌入(双轨认证不动 / 保留 Feign 自回环 / 保留 `purchase.wx.*` 包飞地 / 迁入剩余 ~150 类 / 解 `/wx/contract` 路由冲突)。北极星:单进程跑全功能 + 行为等价。
>
> **探查结论(驱动本路线图):**
> - 认证双轨是必然 —— 主单体 Shiro 已 `/wx/**=anon`(`IShiroSection.java:20`),basWx 用 `JwtAuthenticationFilter`(order=1,限定 `/wx/* /ewechat/* /axq/*`)接管;两套身份模型(Shiro+SysUser vs JWT+手机号小程序用户) genuinely 分离,并入 Shiro 非本里程碑目标。
> - basWx 深度耦合主 bas 域 —— 11/20 service 经 Feign 自回环(`IBsCompanyClient`/`IPmProcessClient`/`IPmApproveClient`)调主域;保留自回环(直注重构风险高、无行为收益)。
> - 🔴 **已确认路由冲突** `/wx/contract`:报表 `RptCtrContractApi` 与 basWx `ContractController` 同基路径,Phase 7 必须消歧。
> - 外部集成(CFCA 电签/金信 JinXin/企业微信 EweChat/OCR)维持 HTTP 边界,只迁本地 wrapper。

### 已完成基础(Foundation — 已提交验证,51 文件)

| 旧 Phase | 内容 | 状态 |
|---|---|---|
| Phase 3 | 11 JPA 实体 + 18 Dao + 3 Feign 契约 + PurchaseWxClientConfig | ✅ done 2026-07-22 |
| Phase 4 | Redis + WxMaService + JWT/Shiro 并存 wiring(7 config + 3 util + 3 common + 1 vo) | ✅ done 2026-07-22 |

### Forward Phases(2026-07-23 重规划)

- [x] **Phase 5: 承托层迁入** — payload/VO/util/common/config/cache/AOP/ewechat 全量落 zgbas-system,为 service 与 BFF 提供稳定编译底座 (completed 2026-07-24)
- [x] **Phase 6: Service 层迁入** — 19 service impl + 18 interface + EweChatApi + PurchaseCommand(@XxlJob scrubbed) 落 zgbas-system,BaseService 签名未动,Feign 自回环保留 (completed 2026-07-24)
- [x] **Phase 7: BFF edge 迁入** — 路由 inventory + `/wx/contract` 冲突消歧 + 11 controller + 4 API + BasicErrorController 落 zgbas-admin (completed 2026-07-24)
- [ ] **Phase 8: 对齐验证** — compile 零错 + 启动 GREEN + `/wx/*` 非 404 + WX Feign 自回环 proof

---

## Phase Details

### Phase 3: 数据层与 Feign 契约 ✅

**Goal**: 11 WX JPA 实体、18 Dao 接口、3 Feign 契约全量就位于 zgbas-system,消除对 purchase-client 2.0.1-SNAPSHOT 私服 jar 的依赖。

**Status**: ✅ Complete 2026-07-22 (3/3 plans)

---

### Phase 4: 基础设施 & SDK 接入 ✅

**Goal**: Redis 依赖注入单体、weixin-java-miniapp SDK 引入、JWT 与 Shiro 并存机制就绪,WxMaService bean 可注册。

**Status**: ✅ Complete 2026-07-22 (5/5 plans, capstone probe SC-1~4 PASS)

---

### Phase 5: 承托层迁入

**Goal**: basWx 全部承托类(payload/VO/util/common/config/cache/AOP/ewechat/enums/GlobalExceptionHandler)按模块边界落 zgbas-system,消除"边搬边猜",为 Phase 6 service 与 Phase 7 BFF 提供稳定、无孤立 import 的编译底座。

**Depends on**: Phase 4(复用已落地 Redis/JWT/WxMaService wiring,D-18/D-19)

**Requirements**: WX-BFF-03

**Success Criteria** (what must be TRUE):
1. payload(22)+ server VO(~17)+ util(~16)+ common(~5)+ config(~11,含 JinXinConfig/EweChatConfig/FrameworkConfig)+ cache(RedisCache)+ aop(ServiceAop)+ ewechat(EweChatApi)+ enums + GlobalExceptionHandler 全量迁入 zgbas-system,逐类可独立解析,无孤立 import
2. 外部集成 wrapper(JinXinApi/EweChatApi/OCR utils)迁本地 wrapper,远端服务维持 HTTP 边界(D-16/D-17);corpid/corpsecret 等占位不外泄
3. `JAVA_HOME=Corretto-1.8 mvn compile -pl zgbas-system` 零 `[ERROR]`,承托层独立编译通过
4. 承托类 inventory checklist 完成(D-15a/D-15b):return envelope / exception advice / user-context / auth helper / serialization-upload-wrapper 五类逐项 `source → consumer → must-port?` 盘点

**Plans**: 6 plans across 5 waves (planned 2026-07-24)
- Wave 1: 05-01 (payload/vo/enums/common + stub replace) ∥ 05-02 (util + stub replace)
- Wave 2: 05-03 (横切 bean 收口 灰区A: WebAppConfig CORS / GlobalExceptionHandler basePackages / ServiceAop / WxCarrierConfig + config POJOs + exception)
- Wave 3: 05-04 (cache + OcrUtils/OcrHelper wrapper + dev-yml 明文密钥 灰区B + rotate-todo)
- Wave 4: 05-05 (ApplicationStartup 追加 WX BsDictUtil.init + RequestListener + D-15a/b inventory 灰区C)
- Wave 5: 05-06 (编译门 mvn compile -pl zgbas-system 零 [ERROR])

**Research-driven scope adjustments (2026-07-24 RESEARCH, see 05-RESEARCH.md):**
- ⚠️ SC#1 `ewechat(EweChatApi)` 移至 **Phase 6**(D-P5-18):EweChatApi `@Autowired IBuyMessageService`(P6 service),Phase 5 编译不可解。Phase 5 迁其全部承托依赖(EweChatConfig/RedisCache/TemplateCardMessage),为 P6 EweChatApi 铺路。
- JinXinApi → Phase 6(D-P5-08 修正):唯一调用方 UserInfoService(P6)+ CFCA dep;JinXinConfig POJO 仍 Phase 5。
- PurchaseCommand → Phase 6(D-P5-13 修正):依赖 P6 service + xxl-job scrub。
- ScheduleConfig / SwaggerConfig 跳过(D-P5-16/17):单体已有同构 / springfox 不在 classpath。
- FrameworkConfig 剥减为 WxCarrierConfig(仅 eweChatConfig @Bean,D-P5-15)。
- ⚠️ SC#2 "corpid/corpsecret 占位不外泄" 与 D-P5-05 明文策略冲突 —— 用户锁定 D-P5-05(覆盖),rotate-credentials todo 登记 EweChat/Aliyun OCR/JinXin 三组。

---

### Phase 6: Service 层迁入

**Goal**: ~20 个 basWx service impl + interface 全量迁入 zgbas-system,适配内联后 BaseService/IBaseService 签名,保留对主 bas 域的 Feign 自回环调用(不崩为直注),为 Phase 7 BFF 提供完整业务实现层。

**Depends on**: Phase 5(承托类就位)

**Requirements**: WX-SERVICE-01

**Success Criteria** (what must be TRUE):
1. 20 service impl(Apply/BuyEnquiry/BuyMessage/BuyQuote/Contract/SuccessContract/User/UserInfo/UserDetail/WxSession/WxSmsCheckCode/WxUserInfo/WxUserTextRead/BsDict/BsCompany/CompanyIndustry/Feedback/TempSave/JinXinApi/WxAccessToken)+ 对应 interface 全量迁入 zgbas-system
2. BaseService/IBaseService 适配以单体当前内联实现为准,仅最小签名收口,不改业务语义(D-12);无 spt-tools 残余 import
3. 对主 bas 域调用保留 Feign 自回环(`IBsCompanyClient`/`IPmProcessClient`/`IPmApproveClient` 等 localhost:8080),不重构为直接注入(方案1 决策)
4. `JAVA_HOME=Corretto-1.8 mvn compile -pl zgbas-system` 零 `[ERROR]`

**Plans**: 6 plans across 5 waves (planned 2026-07-24)
- Wave 1: 06-01 (11 实体型 CRUD leaf + iface,含 4 Dao import re-point) ∥ 06-02 (Apply + JinXinApi)
- Wave 2: 06-03 (BuyEnquiry + BuyQuote + Contract + EweChatApi —— BuyMessage/Apply 消费者)
- Wave 3: 06-04 (SuccessContract + UserInfo —— plan 内顺序 SuccessContract→UserInfo)
- Wave 4: 06-05 (UserService + PurchaseCommand @XxlJob scrub)
- Wave 5: 06-06 (编译门 mvn compile -pl zgbas-system 零 [ERROR],SC#4 / D-P6-04)

**Research-driven findings (2026-07-24 RESEARCH, see 06-RESEARCH.md):**
- ✓ 零依赖 gap:WX service 的所有 `I*Client`(主域 28 `com.spt.bas.client.remote.*` + 电签 3 `com.spt.sign.client.remote.*` 经 spt-sign-client 1.0.0-SNAPSHOT jar)均在单体解析。
- ✓ BaseService 签名风险低(D-P6-01):12 entity iface extends 内联 IBaseService<T>(主域同款已证),IBuyEnquiry/IBuyMessageService 为 plain;无需 WxBaseService 子类、无需扩共享 base。
- ⚠ 唯一实测适配:4 service(BsCompany/BsDict/CompanyIndustry/Feedback)复用主域 Dao(`com.spt.bas.server.dao`),源 import 需 re-point;CompanyIndustry 含字段重命名(`CompanyIndustryDao`→`BsCompanyIndustryDao`)。
- ⚠ PurchaseCommand @XxlJob scrub(D-P6-02):删 import 行 8/9 + 注解行 21 + XxlJobHelper 块行 23-26,保 ICommand 队列路径;定时触发延后 v1.3 quartz gap-closure。
- ✓ 验收 = 仅编译门(D-P6-04);runtime/bean/启动验证全留 Phase 8。

---

### Phase 7: BFF edge 迁入

**Goal**: 11 controller(`/wx/* + /ewechat/* + /axq/*`)+ 4 API(SaveTempApi/WxOpenApi/WxUserApi/WxUserDetailApi)+ BasicErrorController 迁入 zgbas-admin,先做路由 inventory 与 `/wx/contract` 冲突消歧,再把三族路由真正接到单体 admin 层。

**Depends on**: Phase 6(service 层就位)

**Requirements**: WX-BFF-01, WX-BFF-02

**Success Criteria** (what must be TRUE):
1. 三族路由 inventory 完成(D-03a/D-03a1):`/wx/*`、`/ewechat/*`、`/axq/*` 全端点按 `HTTP method + final path` 归一化成表
2. 🔴 `/wx/contract` 冲突消歧落地:报表 `RptCtrContractApi` vs basWx `ContractController` 同基路径,Spring 启动无 ambiguous mapping,两端点均按预期 handler 命中(冲突矩阵产出 owner phase + minimal disambiguation action,D-03b/D-02)
3. 11 controller + 4 API + BasicErrorController 迁入 zgbas-admin,Spring MVC 扫描到全部 RequestMapping,无重复端点冲突
4. 路径 1:1 保持源码(D-01),仅对确认冲突点做最小消歧,不做整批路由重写

**Plans**: TBD

---

### Phase 8: 对齐验证

**Goal**: 编译 + 启动 + WX 端点可达 + WX Feign 自回环四层验证全绿,basWx 正式成为单体的有机组成,v1.2 milestone 验收通过。

**Depends on**: Phase 7

**Requirements**: WX-ALIGN-01, WX-ALIGN-02, WX-ALIGN-03

**Success Criteria** (what must be TRUE):
1. `JAVA_HOME=Corretto-1.8 mvn compile` 全模块(admin/common/framework/quartz/system)无任何 `[ERROR]` 行(WX-ALIGN-01)
2. ZgbasApplicationTest 启动 GREEN —— ApplicationContext 含 WxMaService/RedisTemplate/JwtAuthenticationFilter bean,日志无 FAILED,0 failures/0 errors(WX-ALIGN-02)
3. HTTP GET/POST `/wx/user/login` 等关键 WX 端点返回非 404(DispatcherServlet 找到 handler,业务报错可接受)(WX-ALIGN-03)
4. WX Feign 自回环 proof:经 PurchaseWxClientConfig 自回环 localhost:8080 发出 WX Feign 调用,响应非 404(确认 WX controller 已接线)

**Plans**: TBD

---

## Progress

| Phase | Milestone | Plans Complete | Status | Completed |
|-------|-----------|----------------|--------|-----------|
| 1–7 各阶段 | v1.0 | 37/37 | ✅ Complete | 2026-07-20 |
| Phase 1: 前端接口对齐 | v1.1 | 3/3 | ✅ Complete | 2026-07-22 |
| Phase 2: 调度日志 + 辅助 Bean | v1.1 | 3/3 | ✅ Complete | 2026-07-22 |
| Phase 3: 数据层与 Feign 契约 | v1.2 | 3/3 | ✅ Complete | 2026-07-22 |
| Phase 4: 基础设施 & SDK 接入 | v1.2 | 5/5 | ✅ Complete | 2026-07-22 |
| Phase 5: 承托层迁入 | v1.2 | 6/6 | ✅ Complete | 2026-07-24 |
| Phase 6: Service 层迁入 | v1.2 | 6/6 | ✅ Complete | 2026-07-24 |
| Phase 7: BFF edge 迁入 | v1.2 | 0/TBD | Not started | - |
| Phase 8: 对齐验证 | v1.2 | 0/TBD | Not started | - |

---

## Requirement Coverage (v1.2)

| REQ-ID | Description | Phase | Status |
|--------|-------------|-------|--------|
| WX-DATA-01 | 6 JPA 实体 purchase-client → zgbas-system | Phase 3 | ✅ Complete |
| WX-DATA-02 | 5 JPA 实体 purchase-server → zgbas-system | Phase 3 | ✅ Complete |
| WX-DATA-03 | 18 Dao 接口 → zgbas-system | Phase 3 | ✅ Complete |
| WX-CLIENT-01 | 3 Feign 接口内联(ISaveTempClient/IWxUserClient/IWxUserDetailClient) | Phase 3 | ✅ Complete |
| WX-CLIENT-02 | PurchaseWxClientConfig 内联,自回环 localhost:8080 | Phase 3 | ✅ Complete |
| WX-SERVICE-01 | ~20 service impl → zgbas-system | Phase 6 | ✅ Complete |
| WX-SERVICE-02 | weixin-java-miniapp SDK + WxMiniAppConfig + WxMaService bean | Phase 4 | ✅ Complete |
| WX-SERVICE-03 | Redis + JWT 认证体系与 Shiro 并存 | Phase 4 | ✅ Complete |
| WX-BFF-01 | 11 Controller → zgbas-admin (/wx/* /ewechat/* /axq/*) | Phase 7 | Pending |
| WX-BFF-02 | 4 API 类 → zgbas-admin | Phase 7 | Pending |
| WX-BFF-03 | 辅助组件(payload/VO/util/AOP/cache/EweChatApi)全量迁入 | Phase 5 | Pending |
| WX-ALIGN-01 | mvn compile 全模块零错 | Phase 8 | Pending |
| WX-ALIGN-02 | ZgbasApplicationTest GREEN(含 WX beans) | Phase 8 | Pending |
| WX-ALIGN-03 | 关键 WX 端点非 404(路由命中) | Phase 8 | Pending |

**Coverage: 14/14 ✓** (6 complete, 8 pending)

---

*Created: 2026-07-16*
*Updated: 2026-07-23 — v1.2 basWx 迁入 forward phases replanned (3/4 done → 5 承托/6 service/7 BFF/8 验证); `/wx/contract` 冲突锁定为 Phase 7 必解项*
