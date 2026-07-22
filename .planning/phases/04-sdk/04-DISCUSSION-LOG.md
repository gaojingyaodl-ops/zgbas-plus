# Phase 4: 基础设施 & SDK 接入 - Discussion Log

> **Audit trail only.** Do not use as input to planning, research, or execution agents.
> Decisions are captured in CONTEXT.md — this log preserves the alternatives considered.

**Date:** 2026-07-22
**Phase:** 4-基础设施 & SDK 接入
**Areas discussed:** Redis 本地环境, jjwt 解析 API 兼容性, JwtFilter 注册范围, WxMaService 访问模式

---

## Redis 本地环境

| Option | Description | Selected |
|--------|-------------|----------|
| localhost:6379 无密码 | 最简单，本地开发够用 | |
| 用户直接提供远程 Redis | host=47.104.15.98, port=6379, password=zg123456 | ✓ |

**User's choice:** 直接告知远程 Redis 连接信息（未通过 AskUserQuestion，用户在消息中直接给出）
**Notes:** 与现有 application-dev.yml 明文密钥策略一致，写入 dev yml 即可。

---

## jjwt 解析 API 兼容性

| Option | Description | Selected |
|--------|-------------|----------|
| 直接复用 TokenUtil.parseJWT | JwtUtil 已改为委托 TokenUtil，单体 jjwt 0.7.0 compile-provided 已支持 | ✓ |
| 切换到新 jjwt API | 需要升级版本，风险高 | |

**User's choice:** 直接复用（确认后无需讨论，Claude 已扫描源码验证）
**Notes:** JwtUtil.createJWT 和 parseJWT 均已注释掉原始 Jwts.builder/parser 直调，改为委托 TokenUtil，与单体已内联的 spt-tools TokenUtil 完全兼容。

---

## JwtFilter 注册范围

| Option | Description | Selected |
|--------|-------------|----------|
| FilterRegistrationBean 限制路径 | /wx/* + /ewechat/* + /axq/*，不影响已有 Shiro 路径 | ✓ |
| @Component + ignores yml 配置 | 照搬源码原样，需额外维护 yml ignores 列表 | |

**User's choice:** FilterRegistrationBean 限制路径，URL patterns = /wx/*, /ewechat/*, /axq/*
**Notes:** CustomConfig/IgnoreConfig 不迁入；JwtAuthenticationFilter 需改造：去掉 @Component、去掉 @Autowired CustomConfig、删 initIgnores/@PostConstruct、checkIgnores 改为直接 return false。

---

## WxMaService 访问模式

| Option | Description | Selected |
|--------|-------------|----------|
| 保留 static 模式 | WxConfiguration.getMaService() 静态调用，Phase 5 service 照搬 | ✓ |
| @Bean 单例 + @Autowired | 改为 Spring 注入，Phase 5 改动量大 | |

**User's choice:** 保留 static 模式
**Notes:** WxConfiguration 有多个 static Map（maServices/routers），保留 static 方式 Phase 5 零改动量。

---

## Claude's Discretion

- WxConfiguration 迁入 `zgbas-system`（与其他 WX config 类平级）
- WxSecurityConfig（新建）用于 FilterRegistrationBean 注册，放 `zgbas-system` config 包
- jwt.config.key=sgcoding，ttl=600000，remember=604800000（沿用源码默认值）

## Deferred Ideas

- CustomConfig / IgnoreConfig 照搬 yml ignores 方案：被 FilterRegistrationBean 替代
- CFCA 电签（jinxin.*）配置：Phase 5 携带
- 企业微信 EweChatApi 配置化：留 Future Requirements
