# ROADMAP: zgbas-plus

## Milestones

- ✅ **v1.0 单体化重构交付** — Phases 1–7 (shipped 2026-07-20)
- ✅ **v1.1 quartz 功能完善** — Phases 1–2 (shipped 2026-07-22)
- 🚧 **v1.2 basWx 迁入** — Phases 3–6 (active)

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

### Phases (Summary)

- [x] **Phase 3: 数据层与 Feign 契约** — 11 WX JPA 实体 + 18 Dao + 3 Feign 契约就位 zgbas-system，消除私服 jar 依赖 — completed 2026-07-22
- [ ] **Phase 4: 基础设施 & SDK 接入** — Redis + weixin-java-miniapp SDK + JWT/Shiro 并存机制就绪
- [ ] **Phase 5: Service & BFF 全量迁入** — ~20 service + 11 controller + 4 API + 辅助组件全量落地
- [ ] **Phase 6: 对齐验证** — 编译零错 + 启动 GREEN + WX 端点非 404 三层验证通过

---

## Phase Details

### Phase 3: 数据层与 Feign 契约

**Goal**: 11 WX JPA 实体、18 Dao 接口、3 Feign 契约全量就位于 zgbas-system，消除对 purchase-client 2.0.1-SNAPSHOT 私服 jar 的依赖，为后续 service 层提供完整编译基础。

**Depends on**: v1.1 Phase 2（spt-tools 内联完成，BaseDao 可用，JPA 基础设施已稳定）

**Requirements**: WX-DATA-01, WX-DATA-02, WX-DATA-03, WX-CLIENT-01, WX-CLIENT-02

**Success Criteria** (what must be TRUE):
1. zgbas-system 可独立 `mvn compile`，purchase-client 6 实体（BuyEnquiry/BuyMessage/BuyQuote/CompanyUser/SaveInfo/UserDetail）和 purchase-server 5 实体（WxAccessToken/WxSession/WxSmsCheckCode/WxUserInfo/WxUserTextRead）均带 javax.persistence 注解，无编译 ERROR
2. 18 个 Dao 接口继承内联后的 BaseDao，其中 7 个复用已有 bas 实体的 Dao 无实体重复声明，`mvn compile` 通过
3. ISaveTempClient/IWxUserClient/IWxUserDetailClient 3 个 Feign 接口在 zgbas-system 包路径下就位，pom.xml 中无 purchase-client 2.0.1-SNAPSHOT jar 声明
4. PurchaseWxClientConfig 内联完成，application.yml 含 `spt.bas.purchaseWx.url=http://localhost:8080` 配置项，@ConfigurationProperties 绑定无报错

**Plans**: TBD

---

### Phase 4: 基础设施 & SDK 接入

**Goal**: Redis 依赖注入单体、weixin-java-miniapp SDK 引入、JWT 与 Shiro 并存机制就绪，WxMaService bean 可在 Spring context 中注册，为 WX service 层提供全部运行时依赖。

**Depends on**: Phase 3

**Requirements**: WX-SERVICE-02, WX-SERVICE-03

**Success Criteria** (what must be TRUE):
1. `spring-boot-starter-data-redis` 引入 pom 后 `mvn compile` 零 Redis 相关 ERROR，RedisTemplate bean 在 ApplicationContext 中可注入
2. WxMiniAppConfig（@ConfigurationProperties，绑定 wx.miniapp.*）和 WxConfiguration 就位，WxMaService bean 可在 Spring context 注册，无 NoSuchBeanDefinitionException
3. JwtAuthenticationFilter 注册为 Spring Filter bean 后，/wx/** 路径请求绕过 Shiro AuthenticationFilter（过滤链不抛 UnauthorizedException），已有 Shiro session 登录路径（/login、/index）行为不变
4. Spring context 干启动（通过 ApplicationContextRunner 或 @SpringBootTest）时，Redis bean + WxMaService bean + JwtConfig bean 均可通过 getBean 取到，无 BeanCreationException

**Plans**: 5 plans

- [ ] 04-01-PLAN.md — pom 依赖声明 + application-dev.yml 配置
- [ ] 04-02-PLAN.md — Redis 配置类迁入
- [ ] 04-03-PLAN.md — WxMaService 配置类迁入 + common stubs
- [ ] 04-04-PLAN.md — JWT 认证体系迁入
- [ ] 04-05-PLAN.md — 全量编译验证 + bean 注册验证

---

### Phase 5: Service & BFF 全量迁入

**Goal**: 全部采购小程序业务 service 层和 BFF 控制器迁入单体，辅助组件全量落地，单体覆盖 WX 所有业务路径（/wx/* + /ewechat/* + /axq/*）。

**Depends on**: Phase 4

**Requirements**: WX-SERVICE-01, WX-BFF-01, WX-BFF-02, WX-BFF-03

**Success Criteria** (what must be TRUE):
1. ~20 个 service impl 迁入 zgbas-system，适配内联后 BaseService 签名（无 spt-tools 残余 import），`mvn compile -pl zgbas-system` 无 ERROR
2. 11 个 controller（/wx/* + /ewechat/* + /axq/*）和 4 个 API 类（SaveTempApi/WxOpenApi/WxUserApi/WxUserDetailApi）迁入 zgbas-admin，Spring MVC 扫描到对应 RequestMapping，无重复端点冲突
3. 辅助组件全量迁入（21 payload + 19 VO + 20 util + ServiceAop + BsDictUtil/RedisCache + EweChatApi），无孤立 import 缺失，每个类可独立解析
4. 全模块 `JAVA_HOME=Corretto-1.8 mvn compile` 输出零 `[ERROR]` 行（WX 层全量迁入后首次全量编译通过）

**Plans**: TBD

---

### Phase 6: 对齐验证

**Goal**: 编译 + 启动 + WX 端点可达三层验证全绿，basWx 正式成为单体的有机组成，v1.2 milestone 验收通过。

**Depends on**: Phase 5

**Requirements**: WX-ALIGN-01, WX-ALIGN-02, WX-ALIGN-03

**Success Criteria** (what must be TRUE):
1. `JAVA_HOME=/path/Corretto-1.8 mvn compile` 全模块（admin/common/framework/quartz/system）输出无任何 `[ERROR]` 行（WX-ALIGN-01 编译基线）
2. ZgbasApplicationTest 启动测试 GREEN——ApplicationContext 加载含 WxMaService / RedisTemplate / JwtAuthenticationFilter bean，日志无 `FAILED`，测试报告 0 failures / 0 errors
3. 应用启动后，HTTP GET /wx/user/login 返回非 404 状态码（路由命中，业务逻辑报错可接受，但 Spring DispatcherServlet 必须找到 handler）
4. WX Feign 自回环 proof（WX-CLIENT-02）：通过 PurchaseWxClientConfig 自回环 localhost:8080 发出 WX Feign 调用，响应 HTTP status 非 404（确认 WX controller 已接线，D-P4-01a 扫描范围已覆盖 WX client remote 包）

**Plans**: TBD

---

## Progress

| Phase | Milestone | Plans Complete | Status | Completed |
|-------|-----------|----------------|--------|-----------|
| 1–7 各阶段 | v1.0 | 37/37 | ✅ Complete | 2026-07-20 |
| Phase 1: 前端接口对齐 | v1.1 | 3/3 | ✅ Complete | 2026-07-22 |
| Phase 2: 调度日志 + 辅助 Bean | v1.1 | 3/3 | ✅ Complete | 2026-07-22 |
| Phase 3: 数据层与 Feign 契约 | v1.2 | 3/3 | ✅ Complete | 2026-07-22 |
| Phase 4: 基础设施 & SDK 接入 | v1.2 | 0/TBD | Not started | - |
| Phase 5: Service & BFF 全量迁入 | v1.2 | 0/TBD | Not started | - |
| Phase 6: 对齐验证 | v1.2 | 0/TBD | Not started | - |

---

## Requirement Coverage (v1.2)

| REQ-ID | Description | Phase |
|--------|-------------|-------|
| WX-DATA-01 | 6 JPA 实体 purchase-client → zgbas-system | Phase 3 |
| WX-DATA-02 | 5 JPA 实体 purchase-server → zgbas-system | Phase 3 |
| WX-DATA-03 | 18 Dao 接口 → zgbas-system | Phase 3 |
| WX-CLIENT-01 | 3 Feign 接口内联（ISaveTempClient/IWxUserClient/IWxUserDetailClient） | Phase 3 |
| WX-CLIENT-02 | PurchaseWxClientConfig 内联，自回环 localhost:8080 | Phase 3 |
| WX-SERVICE-01 | ~20 service impl → zgbas-system | Phase 5 |
| WX-SERVICE-02 | weixin-java-miniapp SDK + WxMiniAppConfig + WxMaService bean | Phase 4 |
| WX-SERVICE-03 | Redis + JWT 认证体系与 Shiro 并存 | Phase 4 |
| WX-BFF-01 | 11 Controller → zgbas-admin (/wx/* /ewechat/* /axq/*) | Phase 5 |
| WX-BFF-02 | 4 API 类 → zgbas-admin | Phase 5 |
| WX-BFF-03 | 辅助组件（payload/VO/util/AOP/cache/EweChatApi）全量迁入 | Phase 5 |
| WX-ALIGN-01 | mvn compile 全模块零错 | Phase 6 |
| WX-ALIGN-02 | ZgbasApplicationTest GREEN（含 WX beans） | Phase 6 |
| WX-ALIGN-03 | 关键 WX 端点非 404（路由命中） | Phase 6 |

**Coverage: 14/14 ✓**

---

*Created: 2026-07-16*
*Updated: 2026-07-22 — v1.2 basWx 迁入 roadmap added (Phases 3–6)*
