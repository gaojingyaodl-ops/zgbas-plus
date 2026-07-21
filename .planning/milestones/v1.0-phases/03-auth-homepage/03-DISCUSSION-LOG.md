# Phase 3: 认证首页 - Discussion Log

> **Audit trail only.** Do not use as input to planning, research, or execution agents.
> Decisions are captured in CONTEXT.md — this log preserves the alternatives considered.

**Date:** 2026-07-16
**Phase:** 3-认证首页
**Areas discussed:** Mock 超级密码后门, mockPassword 存放, 登录+首页前端落地范围, Phase 3 验收策略, Shiro 过滤器链来源, WebSocket 端点, SSO 单点登录入口, 未实现页兜底行为

---

## Mock 超级密码后门

| Option | Description | Selected |
|--------|-------------|----------|
| 保留代码+默认关闭 | 代码 verbatim 照搬，mockPassword 留空/注释，后门默认不生效 | |
| 保留照搬（默认可用） | 代码 + mockPassword 配置值都照搬，后门开箱可用 | ✓ |
| 完全移除 | 删掉 super: 逻辑与 mockPassword 配置 | |

**User's choice:** 保留照搬（默认可用）
**Notes:** 源 `ShiroDbRealm.isMockLogin()` 的 `super:<mockPassword>` 后门（模拟任意用户登录，受 `shiroPropConfig.getMockPassword()` 控制）。用户选行为等价优先于安全。

---

## mockPassword 值存放

| Option | Description | Selected |
|--------|-------------|----------|
| 环境变量外置 | `${ZGBAS_MOCK_PASSWORD:}` 占位，真实值靠环境变量注入，prod 默认空 | |
| 写死在 dev profile | mockPassword 直接写在 application-dev.yml 默认值（明文，跟旧项目） | ✓ |

**User's choice:** 写死在 dev profile
**Notes:** 用户选明文照搬；prod 仍走 `${...}` 占位（沿用 Phase 2 D-P2-13/D-P2-14 prod 模式）。

---

## 登录+首页前端落地范围

| Option | Description | Selected |
|--------|-------------|----------|
| 搬 login+首页+菜单+共享资源 | 仅搬 login/index/菜单渲染依赖的模板+共享 layout/static，不搬 1894 业务模板 | |
| 仅 login 骨架+菜单数据接口 | login 页骨架 + 菜单 JSON 接口，首页渲染留 Phase 4 | |
| 仅后端 API | 不动前端，只让认证链路+菜单数据 API 后端可用 | |

**User's choice:** 搬全部静态文件及所有 html（用户主动指令，超出原三选项）
**Notes:** 用户中断选项，直接指令"搬迁所有静态文件及所有 html" —— 把 Phase 3 升级为 web 前端全量 UI 面照搬（1894 模板 + 780 JS + 704 CSS）→ `zgbas-admin/resources/{templates,static}`，配套 Thymeleaf。业务页模板提前就位，Phase 4 只需补 Controller。副作用：点业务菜单 HTML 能开、数据接口裸 404（预期）。

---

## Phase 3 验收策略（依赖外部 spt-auth）

| Option | Description | Selected |
|--------|-------------|----------|
| 启动验证为主 | 启动成功 + Shiro bean 全接线 + /login·/index·菜单接口可达 200 | ✓ |
| 对真实 spt-auth 浏览器 e2e | 拉起真实 spt-auth + 真实账号跑浏览器登录→首页→菜单 | |
| mock spt-auth 响应跑流程 | mock/stub authOpenFacade 返回造数据，不依赖外部 spt-auth | |

**User's choice:** 启动验证为主
**Notes:** 同 Phase 2 D-P2-03。`IAuthOpenFacade` 是 HTTP bean 启动期懒注入，不强求拉起真实 spt-auth。真实 e2e 留 Phase 7。

---

## Shiro 过滤器链来源

| Option | Description | Selected |
|--------|-------------|----------|
| 照搬 DB 动态链 | ChainDefinitionSectionMetaSource 读表，行为等价、与 spt-auth 同源 | ✓ |
| 改静态 ini 规则 | application.yml 写 anon/login=anon、/**=user，减少 DB 依赖 | |

**User's choice:** 照搬 DB 动态链
**Notes:** 链表是否在 `sptbasdb_pd` 存在 → 留 research/planning 确认；Phase 2 ddl-auto=none 不建表，不存在则记 blocker。

---

## WebSocket 端点

| Option | Description | Selected |
|--------|-------------|----------|
| 搬 WS 端点照搬 | WebSocketConfig + IndexWebSocketServer/WebSocketServer，首页 WS 连接能建立 | ✓ |
| 不搬、stub/defer | 不搬 WS，改首页 JS 跳过 WS 连接 | |

**User's choice:** 搬 WS 端点照搬
**Notes:** 源 web 有 WebSocketConfig + ws/IndexWebSocketServer/WebSocketServer（首页级 WS）。推送业务发送方留 Phase 4（WS 先连上闲置）。

---

## SSO 单点登录入口

| Option | Description | Selected |
|--------|-------------|----------|
| 搬 SSO 入口照搬 | UserOpenController ssoLogin/resSsoLogin + zgBas.secret 占位，完整 auth 面 | ✓ |
| 只搬 Realm，SSO 入口 defer | Realm 含 reLoginSso 代码但不搬 SSO 入口控制器 | |

**User's choice:** 搬 SSO 入口照搬
**Notes:** zgBas.secret 走环境变量占位（${ZGBAS_SECRET:}，同 Phase 2 密钥外置风格）。

---

## 未实现页兜底行为

| Option | Description | Selected |
|--------|-------------|----------|
| 接受裸 404 | 点业务菜单时 HTML 能开、数据 XHR 裸 404，Phase 4 接通自好 | ✓ |
| 加"迁移中"兜底 | 拦截未迁移路由显示提示页 | |

**User's choice:** 接受裸 404
**Notes:** 本期不做真实 e2e，零额外工作、不偏离照搬。

---

## 已澄清为"照搬、无需决策"（侦察确认）

- **会话存储**：源 web + 内联 spt-tools 无 Redis/SessionDAO → Shiro 内存会话，照搬。
- **登录验证码**：源 login.html + 源码无 kaptcha/captcha → 无验证码，照搬。

## Claude's Discretion

- Realm/Controller 落位包名（照搬源 `com.spt.bas.web.*` verbatim）
- 登录失败/会话/登出行为（照搬，MyLogoutFilter 已内联）
- 静态资源路径（照搬 verbatim）
- mockPassword/zgBas.secret 之外的其他 auth 配置项（照搬 + Phase 2 profile/占位模式）

## Deferred Ideas

- 业务 Controller / WebSocket 推送发送方 → Phase 4
- 真实登录 e2e → Phase 7
- 过滤器链 DB 表存在性确认 → Phase 3 research
- CR-01 生产库密码轮换 → 跨阶段安全债
