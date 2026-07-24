---
gsd_version: 2.0
milestone: v1.2
milestone_name: basWx 迁入
created: "2026-07-22T00:00:00.000Z"
replanned: "2026-07-23T00:00:00.000Z"
---

# Milestone v1.2 — basWx 迁入 Requirements

> Created: 2026-07-22
> Replanned: 2026-07-23 (方案: 完成 verbatim 嵌入)
> Status: Active — Phase 3/4 done, forward Phases 5–8 pending

## Goal

将 PurchaseWxServer 微信采购小程序完整迁入单体,实现全业务单进程覆盖(含采购小程序 BFF),行为对齐旧系统。

## 嵌入方案(2026-07-23 锁定)

**完成 verbatim 嵌入(方案1):**
- 认证双轨不动(JWT + Shiro `/wx/**=anon` 并存)
- 保留 Feign 自回环调主 bas 域(不崩为直注)
- 保留 `com.spt.bas.purchase.wx.*` 包飞地(verbatim 保包名)
- 迁入剩余 ~150 类,解 `/wx/contract` 路由冲突
- 外部集成(CFCA/金信/EweChat/OCR)维持 HTTP 边界,只迁 wrapper

## Context

**源码位置:** `/Users/alan/WorkSpace/IDEA/zgbas/basWx/`,分支 `feat-系统重构v5.0`(212 Java files:purchase-server 181 + purchase-client 31)

**规模(2026-07-23 探查实测):**
- 11 JPA 实体(6 client + 5 server)+ 7 借用 bas 实体(已在单体)✅ 已迁
- 18 Dao 接口(11 新声明 + 7 复用)✅ 已迁
- 3 Feign 接口(purchase-client remote)+ PurchaseWxClientConfig ✅ 已迁
- ~20 service impl(20 文件)+ 19 interface ⏳ 待迁(Phase 6)
- 11 controller(`/wx/* /ewechat/* /axq/*`)+ 4 API + BasicErrorController ⏳ 待迁(Phase 7)
- 承托类:payload 22 + VO ~17 + util ~16 + common ~5 + config ~11 + cache/aop/ewechat/enums ~4 + GlobalExceptionHandler ⏳ 待迁(Phase 5)

**已落地基础(Phase 3+4,51 文件):** 实体/Dao/Feign + Redis/WxMaService/JWT/Shiro wiring

**关键迁移风险(2026-07-23 探查确认):**
- 🔴 **路由冲突 `/wx/contract`**:报表 `RptCtrContractApi` 与 basWx `ContractController` 同基路径 → Phase 7 必解
- 🟡 basWx 深度耦合主 bas 域:11/20 service 经 Feign 自回环调主域(保留,不重构)
- 🟡 外部集成:CFCA 电签 / 金信 JinXin / 企业微信 EweChat / OCR —— 维持 HTTP 边界
- 🟢 Redis 面很小(仅 RedisCache + RedisConfig,已迁);WxMaService 走静态访问(已迁)

---

## v1 Requirements

### WX-DATA — 实体 & Dao 层 ✅

- [x] **WX-DATA-01**: 迁入 purchase-client 6 JPA 实体(BuyEnquiry/BuyMessage/BuyQuote/CompanyUser/SaveInfo/UserDetail)到 zgbas-system
- [x] **WX-DATA-02**: 迁入 purchase-server 5 JPA 实体(WxAccessToken/WxSession/WxSmsCheckCode/WxUserInfo/WxUserTextRead)到 zgbas-system
- [x] **WX-DATA-03**: 迁入 18 Dao 接口到 zgbas-system(含 7 个引用已有 bas 实体的 Dao)

### WX-CLIENT — Feign 契约内联 ✅

- [x] **WX-CLIENT-01**: 3 Feign 接口(ISaveTempClient/IWxUserClient/IWxUserDetailClient)内联进 zgbas-system,消除 purchase-client 2.0.1-SNAPSHOT 私服 jar 依赖
- [x] **WX-CLIENT-02**: PurchaseWxClientConfig 内联,spt.bas.purchaseWx.url=localhost:8080 自回环

### WX-SERVICE — 业务 Service 层

- [ ] **WX-SERVICE-01**: ~20 service impl 全量迁入 zgbas-system(适配 spt-tools 内联后 BaseService,保留对主域 Feign 自回环)
- [x] **WX-SERVICE-02**: weixin-java-miniapp:3.8.0 SDK 引入 + WxMiniAppConfig + WxConfiguration(WxMaService bean)
- [x] **WX-SERVICE-03**: Redis 引入 + JWT 认证体系(JwtConfig + JwtAuthenticationFilter)与 Shiro 并存

### WX-BFF — Controller & API 层

- [ ] **WX-BFF-01**: 11 Controller 迁入 zgbas-admin(/wx/* + /ewechat/* + /axq/*),含 `/wx/contract` 冲突消歧
- [ ] **WX-BFF-02**: 4 API 类迁入 zgbas-admin(SaveTempApi/WxOpenApi/WxUserApi/WxUserDetailApi)
- [ ] **WX-BFF-03**: 辅助组件迁入:payload 22、VO ~17、util ~16、common ~5、config ~11、AOP(ServiceAop)、cache(RedisCache)、企业微信 EweChatApi、GlobalExceptionHandler

### WX-ALIGN — 对齐验证

- [ ] **WX-ALIGN-01**: `mvn compile` 全模块无错(JDK 1.8 + Spring Boot 2.5.9,JAVA_HOME=Corretto 1.8)
- [ ] **WX-ALIGN-02**: Spring context 含 WX beans 正常启动(ZgbasApplicationTest GREEN,含 WxMaService/Redis/JWT beans)
- [ ] **WX-ALIGN-03**: 关键 WX 端点可达(/wx/user/login 等非 404,自回环 WX-CLIENT-02 proof 绿)

---

## Future Requirements

- JWT 替换为统一认证(长期方向,JWT/Shiro 并存是本次临时方案 —— 探查证两套身份模型 genuinely 分离,本里程碑不做)
- CFCA 电签服务单体化(现保持 HTTP 调用外部)
- 企业微信通知推送配置化(现 hardcode corpid/corpsecret)
- basWx 对主域 Feign 自回环崩为直接注入(方案1 明确不做,留 future)
- 微信小程序推送模板 ID 配置化

## Out of Scope (v1.2)

- **合并 spt-auth** — 认证保持外部 HTTP(决策 #7 不变)
- **JWT/Shiro 认证统一** — 探查证身份模型分离,非本里程碑目标(方案1)
- **Feign 自回环崩为直注** — 方案1 明确保留自回环
- **schema drift 修复(ddl-auto validate)** — Phase 4 遗留,留 v1.3
- **quartz gap-closure(28 handler 路由)** — v1.1 遗留,留 v1.3
- **basWxServer 前端 Web 管理页** — PurchaseWxServer 是纯 API 服务(无 Thymeleaf 管理页),不适用
- **Spring Boot 3 / JDK 17 升级** — 技术栈锁定,本期不做

---

## Traceability

| REQ-ID | Phase | Status |
|--------|-------|--------|
| WX-DATA-01 | Phase 3 | ✅ Complete |
| WX-DATA-02 | Phase 3 | ✅ Complete |
| WX-DATA-03 | Phase 3 | ✅ Complete |
| WX-CLIENT-01 | Phase 3 | ✅ Complete |
| WX-CLIENT-02 | Phase 3 | ✅ Complete |
| WX-SERVICE-01 | Phase 6 | Pending |
| WX-SERVICE-02 | Phase 4 | ✅ Complete |
| WX-SERVICE-03 | Phase 4 | ✅ Complete |
| WX-BFF-01 | Phase 7 | Pending |
| WX-BFF-02 | Phase 7 | Pending |
| WX-BFF-03 | Phase 5 | Pending |
| WX-ALIGN-01 | Phase 8 | Pending |
| WX-ALIGN-02 | Phase 8 | Pending |
| WX-ALIGN-03 | Phase 8 | Pending |
