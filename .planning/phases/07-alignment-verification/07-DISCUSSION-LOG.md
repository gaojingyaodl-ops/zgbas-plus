# Phase 7: 行为对齐验证 - Discussion Log

> **Audit trail only.** Do not use as input to planning, research, or execution agents.
> Decisions are captured in CONTEXT.md — this log preserves the alternatives considered.

**Date:** 2026-07-19
**Phase:** 7-行为对齐验证
**Areas discussed:** 验证执行形态, 行为等价对照基准, 覆盖深度与抽样, Phase 6 gap 在 P7 的处理

---

## 验证执行形态

| Option | Description | Selected |
|--------|-------------|----------|
| 混合 | 后端关键接口扩 ZgbasApplicationTest proof（沿用 D-P5-08/D-P6-04 @Disabled 手动验收口）+ 前端关键流人工浏览器 UAT 清单。复用现有测试基建，成本/覆盖均衡。 | ✓ |
| 纯人工 UAT 清单 | 只用 UAT 清单走查（浏览器手点 + checklist），不写脚本。最轻，ALIGN-02 缺可重复证据。 | |
| HTTP 脚本化回归 | 扩 ZgbasApplicationTest 全链路 HTTP 脚本，前端只验可达。证据强但前端交互覆盖弱。 | |
| 浏览器 e2e 自动化 | 引入 Playwright/Selenium 跑关键流。最高保真，但项目零基建 + 新增依赖成本高，偏离最小改动。 | |

**User's choice:** 混合
**Notes:** Phase 7 是项目首次全链路真实验收（P3-P6 一直是启动验证 + 抽样 proof）。项目现状无任何浏览器自动化基建。UAT 清单产物落 `07-HUMAN-UAT.md`（沿用 Phase 6 机制）、proof 默认 `@Disabled`（避免 dev 库污染）作 Claude discretion。

---

## 行为等价对照基准

| Option | Description | Selected |
|--------|-------------|----------|
| 混合 | 独立验收 + golden 预期为骨干（dev DB 数据 + 业务规则 + 源码行为推导）+ 选择性 side-by-side JSON diff（仅高风险/低信心接口）。 | ✓ |
| 纯 side-by-side 实时对比 | 起老 zgbas 4 微服务全套逐接口比对。最强证据，但全套依赖重 + 老系统可能起不稳。 | |
| 纯独立验收 + golden | 不起老系统，凭预期值判断。最省，对齐证据较弱。 | |
| 人工目视对照 | 用户凭经验判断"看起来对"。ALIGN-02 欠结构化证据，sign-off 主观。 | |

**User's choice:** 混合
**Notes:** 源系统 zgbas 在本地 `/Users/alan/WorkSpace/IDEA/zgbas`（4 微服务），但需起全套依赖（spt-auth / xxl-job admin / MySQL prod 数据），全套并起成本高且未必稳 → side-by-side 选择性而非全量。哪些接口走 side-by-side（高风险写类如 autoPay / 高频可见如报表导出）交 planning 按风险路由。

---

## 覆盖深度与抽样

| Option | Description | Selected |
|--------|-------------|----------|
| 分级 | smoke 必跑（每环节 1-2）+ 高风险域深验（Excel 导出/分页/关键写类）+ 长尾标 known-gap（53 报表不全跑、64 任务不全触）。 | ✓ |
| 全链路 smoke | 每环节抽 1-2 个证明端到端通。最薄最快，高风险域覆盖不足。 | |
| 风险抽样 | 只打高频可见行为。覆盖中，但跳过 smoke 致 ALIGN-01 证据不全。 | |
| 按模块全面回归 | 合同/授信/库存/放款/53 报表/64 任务逐项。最厚，成本极高，与务实基调冲突。 | |

**User's choice:** 分级
**Notes:** 分级自然吸纳 Phase 6 遗留 gap（28 未迁 handler 等长尾标 known-gap）。高风险域具体清单（写类 autoPay/refreshContractStatus/OrverdurTask/DepositPaymentTask + 导出 + 分页）作 discretion 交 planning 落 UAT 项。

---

## Phase 6 gap 在 P7 的处理

| Option | Description | Selected |
|--------|-------------|----------|
| 混合 | 阻塞型 gap 收口（15 REVIEW sys_job operator review + /monitor/job 菜单 INSERT 前置）in P7 UAT；非阻塞（28 handler / 7 ambiguous / xxl-job admin 退役）defer 不阻塞 sign-off。 | ✓ |
| P7 内全收口 | 补迁 28 handler + 核 7 ambiguous + 收口 15 REVIEW + 菜单 INSERT。交付最完整，但扩范围（28 handler 补迁成本高，偏离 P7 验证定位）。 | |
| 纯验证全 defer | P7 只验已迁部分，全部 gap 继续交 ops。范围最纯，但 15 REVIEW 未核就 sign-off 会掩盖已迁任务参数错配风险。 | |

**User's choice:** 混合
**Notes:** 15 REVIEW sys_job 影响**已迁任务**运行正确性（ALIGN-02 相关）→ P7 UAT 推进 operator review。/monitor/job 菜单 INSERT 是 Phase 6 HUMAN-UAT pending 项 2（浏览器验 UI）的前置。28 handler 源迁移清单外，已路由 `/gsd:plan-phase 06 --gaps`，P7 不补迁不阻塞 sign-off。

---

## Claude's Discretion

- UAT 清单产物落 `07-HUMAN-UAT.md`（沿用 `06-HUMAN-UAT.md` 结构）。
- proof 默认 `@Disabled`（D-P6-06-01 模式，防 dev 库污染）。
- side-by-side 接口选择交 planning 按风险路由（关键写类 outcome + 高频可见导出，3-5 个代表）。
- 高风险域具体清单交 planning 落 UAT 项。
- golden 预期来源（dev DB 数据 + 业务规则 + 源码推导）；需 prod 脱敏样本则 checkpoint:human-blocked。
- 验收非 hermetic 沿用（明文密钥态，不需 export）。
- 运行期 drift 即修（D-P4-05 仅修阻塞型）。
- 起源系统 side-by-side 前先评估可起性，不可起则降级。

## Deferred Ideas

- 浏览器 e2e 自动化（Playwright/Selenium）→ 本期不引入，未来另立基建 task。
- 28 源清单外生产 handler → Phase 6 gap-closure plan 或 v2。
- 7 ambiguous executeCommand → ops 确认。
- xxl-job admin 退役 → ops（P7 sign-off 后）。
- 全量 53 报表 + 64 任务回归 → P7 选代表样本，全量另立 task。
- basWx / purchase 业务 → v2（#14）。
- 报表物理分页性能 / 补审计字段 / 全量 schema drift reconcile → 永久 Out-of-Scope 或 tech debt。
- CR-01 轮换已泄漏生产库密码 → 跨阶段安全债。
- Spring Boot 3 / JDK 17 升级 → Out-of-Scope。
