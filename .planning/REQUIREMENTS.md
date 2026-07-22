---
gsd_version: 2.0
milestone: v1.2
milestone_name: basWx 迁入
created: "2026-07-22T00:00:00.000Z"
---

# Milestone v1.2 — basWx 迁入 Requirements

> Created: 2026-07-22
> Status: Active

## Goal

将 PurchaseWxServer 微信采购小程序完整迁入单体，实现全业务单进程覆盖（含采购小程序 BFF）。

## Context

**源码位置：** `/Users/alan/WorkSpace/IDEA/zgbas/basWx/`，分支 `feat-系统重构v5.0`

**规模（来自探查）：**
- 2 子模块：`purchase-server`（178 Java files）+ `purchase-client`（31 Java files）= 209 Java files
- 11 JPA 实体（6 in client, 5 in server）+ 7 借用 bas 实体（已在 zgbas-plus）
- 18 Dao 接口
- 3 Feign 接口（purchase-client remote）
- ~20 service impl
- 11 controller + 4 API 类

**关键迁移风险：**
- 🔴 JWT 认证体系（非 Shiro），需与主单体 Shiro session 并存
- 🔴 Redis 依赖（当前单体无 Redis）
- 🟡 新 SDK：`weixin-java-miniapp:3.8.0`、CFCA 电签（cfca.etl）
- 🟡 企业微信（EweChat）独立配置
- 🟡 Phase 4 已扫 com.spt.bas.purchase.wx.client.remote（D-P4-01a），WX Feign 已自回环 localhost:8080 降级 → v1.2 补实现消除 404

---

## v1 Requirements

### WX-DATA — 实体 & Dao 层

- [ ] **WX-DATA-01**: 迁入 purchase-client 6 JPA 实体（BuyEnquiry / BuyMessage / BuyQuote / CompanyUser / SaveInfo / UserDetail）到 zgbas-system
- [ ] **WX-DATA-02**: 迁入 purchase-server 5 JPA 实体（WxAccessToken / WxSession / WxSmsCheckCode / WxUserInfo / WxUserTextRead）到 zgbas-system
- [ ] **WX-DATA-03**: 迁入 18 Dao 接口到 zgbas-system（含 7 个引用已有 bas 实体的 Dao，已有实体无需重复迁）

### WX-CLIENT — Feign 契约内联

- [ ] **WX-CLIENT-01**: 3 Feign 接口（ISaveTempClient / IWxUserClient / IWxUserDetailClient）内联进 zgbas-system，消除 purchase-client 2.0.1-SNAPSHOT 私服 jar 依赖
- [ ] **WX-CLIENT-02**: PurchaseWxClientConfig 内联，spt.bas.purchaseWx.url 配置为 localhost:8080 自回环

### WX-SERVICE — 业务 Service 层

- [ ] **WX-SERVICE-01**: ~20 service impl 全量迁入 zgbas-system（适配 spt-tools 内联后的 BaseService）
- [ ] **WX-SERVICE-02**: weixin-java-miniapp:3.8.0 SDK 引入 pom + WxMiniAppConfig（@ConfigurationProperties）+ WxConfiguration（注册 WxMaService bean）
- [ ] **WX-SERVICE-03**: Redis 引入（spring-boot-starter-data-redis）+ JWT 认证体系（JwtConfig + JwtAuthenticationFilter）与 Shiro 并存

### WX-BFF — Controller & API 层

- [ ] **WX-BFF-01**: 11 Controller 类迁入 zgbas-admin（/wx/* + /ewechat/* + /axq/* 端点）
- [ ] **WX-BFF-02**: 4 API 类迁入 zgbas-admin（SaveTempApi / WxOpenApi / WxUserApi / WxUserDetailApi）
- [ ] **WX-BFF-03**: 辅助组件迁入：payload 21 类、VO 19 类、枚举、util 20 类、AOP（ServiceAop）、缓存（BsDictUtil / RedisCache）、企业微信 EweChatApi

### WX-ALIGN — 对齐验证

- [ ] **WX-ALIGN-01**: `mvn compile` 全模块无错（JDK 1.8 + Spring Boot 2.5.9，JAVA_HOME=Corretto 1.8）
- [ ] **WX-ALIGN-02**: Spring context 含 WX beans 正常启动（ZgbasApplicationTest GREEN，包含 WxMaService / Redis / JWT beans）
- [ ] **WX-ALIGN-03**: 关键 WX 端点可达（/wx/user/login 等非 404，自回环 WX-CLIENT-02 proof 绿）

---

## Future Requirements

- JWT 替换为统一认证（长期方向，JWT/Shiro 并存是本次临时方案）
- CFCA 电签服务单体化（现保持 HTTP 调用外部）
- 企业微信通知推送配置化（现 hardcode corpid/corpsecret）
- 微信小程序推送模板 ID 配置化

## Out of Scope (v1.2)

- **合并 spt-auth** — 认证保持外部 HTTP（决策 #7 不变）
- **schema drift 修复（ddl-auto validate）** — Phase 4 遗留，留 v1.3
- **quartz gap-closure（28 handler 路由）** — v1.1 遗留，留 v1.3
- **basWxServer 前端 Web 管理页** — PurchaseWxServer 是纯 API 服务（无 Thymeleaf 管理页），不适用
- **Spring Boot 3 / JDK 17 升级** — 技术栈锁定，本期不做

---

## Traceability

| REQ-ID | Phase | Status |
|--------|-------|--------|
| WX-DATA-01 | Phase 3 | Pending |
| WX-DATA-02 | Phase 3 | Pending |
| WX-DATA-03 | Phase 3 | Pending |
| WX-CLIENT-01 | Phase 3 | Pending |
| WX-CLIENT-02 | Phase 3 | Pending |
| WX-SERVICE-01 | Phase 5 | Pending |
| WX-SERVICE-02 | Phase 4 | Pending |
| WX-SERVICE-03 | Phase 4 | Pending |
| WX-BFF-01 | Phase 5 | Pending |
| WX-BFF-02 | Phase 5 | Pending |
| WX-BFF-03 | Phase 5 | Pending |
| WX-ALIGN-01 | Phase 6 | Pending |
| WX-ALIGN-02 | Phase 6 | Pending |
| WX-ALIGN-03 | Phase 6 | Pending |
