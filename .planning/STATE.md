---
gsd_state_version: 1.0
milestone: v1.2
milestone_name: basWx 迁入
status: executing
stopped_at: Phase 5 context gathered — 3 gray areas decided (横切bean逐项收口 / 新密钥明文dev yml / 启动并入单体防双跑)
last_updated: "2026-07-24T01:04:11.634Z"
last_activity: 2026-07-24 -- Phase 5 planning complete
progress:
  total_phases: 6
  completed_phases: 2
  total_plans: 14
  completed_plans: 8
  percent: 33
---

# Project State

## Project Reference

See: .planning/PROJECT.md (updated 2026-07-16)

**Core value:** 单进程启动即可跑通全部供应链业务(登录 → 核心业务 → 报表 → 定时任务 → 微信采购小程序),行为对齐旧系统 zgbas
**Current focus:** Phase 5 — 承托层迁入(v1.2 forward work,待规划)

## Current Position

Phase: 05 (承托层迁入) — PLANNING(未启动)
Status: Ready to execute
Last activity: 2026-07-24 -- Phase 5 planning complete

Progress: [██████░░░░░░░░░░░░░░] 33% (v1.2: 2/6 phases done)

## v1.2 嵌入方案(2026-07-23 锁定)

**完成 verbatim 嵌入(方案1):** 双轨认证不动 / 保留 Feign 自回环 / 保留 `purchase.wx.*` 包飞地 / 迁剩余 ~150 类 / 解 `/wx/contract` 冲突。北极星:单进程跑全功能 + 行为等价。

## v1.2 Phase Map

| Phase | 内容 | 状态 |
|---|---|---|
| 3 数据层与 Feign 契约 | 11 实体/18 Dao/3 Feign/PurchaseWxClientConfig | ✅ done 2026-07-22 |
| 4 基础设施 & SDK | Redis/WxMaService/JWT/Shiro wiring(51 文件基础) | ✅ done 2026-07-22 |
| 5 承托层迁入 | payload/VO/util/common/config/cache/AOP/ewechat → system | ○ 待规划 |
| 6 Service 层迁入 | ~20 service impl + iface + BaseService 适配 → system | ○ 待规划 |
| 7 BFF edge 迁入 | 路由 inventory + /wx/contract 消歧 + 11 controller + 4 API → admin | ○ 待规划 |
| 8 对齐验证 | compile 零错 + 启动 GREEN + /wx 非404 + 自回环 proof | ○ 待规划 |

## Accumulated Context

### Decisions (v1.2 活跃,2026-07-23 探查/锁定)

嵌入方案决策:

- **方案1 verbatim 嵌入**:双轨认证 / Feign 自回环 / 包飞地 全部保留;只迁剩余类 + 解冲突(2026-07-23 用户锁定)
- **认证双轨必然**:主单体 Shiro `IShiroSection.java:20` 已 `/wx/**=anon`;basWx `JwtAuthenticationFilter`(FilterRegistrationBean order=1,限定 `/wx/* /ewechat/* /axq/*`)接管;两套身份模型(Shiro+SysUser vs JWT+手机号小程序用户)genuinely 分离 → 并入 Shiro 非本里程碑目标
- **保留 Feign 自回环**:11/20 service 经 `IBsCompanyClient`/`IPmProcessClient`/`IPmApproveClient` 自回环调主域;崩为直注重构风险高、无行为收益 → 不做
- **保留 `purchase.wx.*` 包飞地**:verbatim 保包名(D-06),最低 compile 级联风险(Phase 3/4 已证)
- 🔴 **`/wx/contract` 冲突已确认**:报表 `RptCtrContractApi` 与 basWx `ContractController` 同基路径 → Phase 7 必解

沿用 Phase 5 既有锁定决策(D-01~D-19,从已删 05-CONTEXT 收割):

- D-01/02/03:路由 1:1 verbatim,仅冲突点最小消歧
- D-04/05/06:service→zgbas-system,controller/API→zgbas-admin,保包名
- D-07/08/09:承托→service→edge 层序
- D-10/11/12:verbatim port first,行为等价,BaseService 最小签名收口
- D-14a/14b:listener 是否 runtime hard-dep 三条判定(ApplicationStartup/RequestListener 待 Phase 5/8 验)
- D-15a/15b:承托类 inventory 五类逐项盘点
- D-16/17:外部集成维持 HTTP 边界,只迁 wrapper
- D-18/19:复用 Phase 4 Redis/JWT/WxMaService wiring

### Pending Todos

- rotate-leaked-prod-credentials (high) — v1.0 期遗留,明文密钥决策后尤需关注
- phase4-resolve-entity-schema-drift (medium) — v1.0/v1.2 遗留,留 v1.3

### Blockers/Concerns

- 🔴 `/wx/contract` 路由冲突(报表 vs basWx)—— Phase 7 启动前必须消歧,否则 Spring ambiguous mapping
- BaseService/IBaseService 签名适配 —— Phase 6 核心 compile 风险点(D-12)
- listener(ApplicationStartup/RequestListener)是否为 `/wx/*` runtime hard-dep —— Phase 5/8 验(D-14a/b)
- 启动测试非 hermetic:`application-dev.yml` 的 `${DB_PASSWORD}`/`${SPT_APP_SECRET}` 无默认值,`mvn test` 需本地 export(明文密钥决策 D-P2-13 撤销后维持)

### Deferred Items

| Category | Item | Status |
|----------|------|--------|
| v2 | basWx 微信采购小程序前端(管理页) | N/A(纯 API 服务) |
| future | JWT/Shiro 认证统一 | deferred(身份模型分离) |
| future | basWx Feign 自回环崩为直注 | deferred(方案1) |
| v1.3 | schema drift 修复(ddl-auto validate) | pending |
| v1.3 | quartz gap-closure(28 handler 路由) | pending |
| debug | login-feign-selfloop-shiro | investigating(v1.0 close deferred) |
| todo | rotate-leaked-prod-credentials | pending (high) |
| todo | phase4-resolve-entity-schema-drift | pending (medium) |

## Session Continuity

Last session: 2026-07-23T09:13:49.343Z
Stopped at: Phase 5 context gathered — 3 gray areas decided (横切bean逐项收口 / 新密钥明文dev yml / 启动并入单体防双跑)
Resume file: .planning/phases/05-carrier-layer/05-CONTEXT.md
Next action: `/gsd-discuss-phase 5` 或 `/gsd-plan-phase 5`(承托层迁入)
