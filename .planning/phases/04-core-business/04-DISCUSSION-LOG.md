# Phase 4: 核心业务迁移 - Discussion Log

> **Audit trail only.** Do not use as input to planning, research, or execution agents.
> Decisions are captured in CONTEXT.md — this log preserves the alternatives considered.

**Date:** 2026-07-17
**Phase:** 4-核心业务迁移
**Areas discussed:** 调用机制 Feign→进程内, 迁移切片与测序, schema drift 处理, 验收策略

---

## 调用机制 Feign→进程内 (BIZ-03 核心)

| Option | Description | Selected |
|--------|-------------|----------|
| interface-as-contract (推荐) | 沿用 D-P2-10：@RestController implements I*Client 原样照搬，Feign 旁路（P2 收窄到 cfca），@Autowired I*Client 解析本地 controller bean | ✓ |
| 真·Service 直调 | 拆掉 controller 契约层，调用方直接 @Autowired Service。更干净但重写 238 契约调用点 + web 267 controller 全改，破坏照搬 | |
| 混合 | 对外 HTTP 保契约层，内部高频改 Service 直调。两套并存复杂 | |
| 你决定 | 交 Claude 拍板 | |

**User's choice:** interface-as-contract (推荐)
**Notes:** 锁为 D-P4-01。子决策 D-P4-02（无本地实现的契约 ~14 个 stub 降级，对齐 D-P3-10）随「下一灰区」确认，无异议。

---

## 迁移切片与测序

| Option | Description | Selected |
|--------|-------------|----------|
| 层次优先 (推荐) | Wave1 basClient 补全 → Wave2 service+infra → Wave3 api @RestController → Wave4 web BFF；每 wave compile 绿灯 | ✓ |
| 按业务域端到端 | 合同→授信→库存→放款每域 service+api+controller 一起。跨域共享 dto/util 难切 | |
| 全量一次照搬修级联 | 像 Phase 1 bulk copy 全部 ~1900。级联错误量巨大不可控 | |
| 你决定 | 交 Claude 拍板 | |

**User's choice:** 层次优先 (推荐)
**Notes:** 锁为 D-P4-03。

### 子问题：basServer infra 包处理

| Option | Description | Selected |
|--------|-------------|----------|
| 筛选照搬 (推荐) | service 依赖的照搬；config 选择性去重 P2；task(23 xxl-job)→P6；rocketmq 评估 | ✓ |
| 全量照搬 infra | 含 task/rocketmq/config 全搬。xxl-job 编译错 + config 重复 bean | |
| 仅 service+api | infra 按需零散补。编译反复缺依赖 | |

**User's choice:** 筛选照搬 (推荐)
**Notes:** 锁为 D-P4-04。正确划 task→Phase 6 边界 + 避免 config 重复 bean。

---

## schema drift 处理

| Option | Description | Selected |
|--------|-------------|----------|
| 保持 none+修运行阻塞 (推荐) | 不全量 reconcile；遇运行期映射错误即修；全量 reconcile+validate 再延 tech debt | ✓ |
| 本期全量 reconcile+重开 validate | 枚举 239 drift 逐表对齐。工作量大 + 改 annotation 风险 + 拖慢 | |
| 你决定 | 交 Claude 拍板 | |

**User's choice:** 保持 none+修运行阻塞 (推荐)
**Notes:** 锁为 D-P4-05。phase4-resolve-entity-schema-drift todo 降级保留 open。与「补审计字段」Out-of-Scope 不同维度。

---

## 验收策略

| Option | Description | Selected |
|--------|-------------|----------|
| 启动验证+WR-02 契约 proof (推荐) | 同 P2/P3：全 context 启动 + bean 接线 + MockMvc 真实 I*Client 端点 GET 200；真实 CRUD 留 P7 | ✓ |
| 按域 HTTP CRUD proof | 每核心域 ≥1 接口 MockMvc 200。需测试数据/真实 DB，工作量大 | |
| 真实 DB smoke | 连 sptbasdb_pd 跑真实查询。依赖生产库 + hermetic 问题，越界 P7 | |
| 你决定 | 交 Claude 拍板 | |

**User's choice:** 启动验证+WR-02 契约 proof (推荐)
**Notes:** 锁为 D-P4-06。吸收 phase4-inprocess-contract-http-proof todo。非 hermetic 同 D-P3-13（Option 4 本地 export 密钥）。

---

## Claude's Discretion

- Flyway 处理：移除（仅 1 个 drop-tmp 迁移无业务价值），纯 ddl-auto=none。
- basClient 数据载体落位 zgbas-system（保包名）。
- 运行阻塞 drift 发现机制：验收 MockMvc + context 启动自然暴露。
- 事务边界：沿用 Phase 2 @Primary JpaTransactionManager。
- web 登录/失败行为：随 controller 照搬（Phase 3 已确认无 captcha）。

## Deferred Ideas

- xxl-job task handler（23）→ Phase 6
- 53 套报表 + report 契约 → Phase 5
- basWx 采购业务 → v2
- 真实业务 CRUD e2e → Phase 7
- 全量 schema drift reconcile + 重开 validate → tech debt（todo 保留 open）
- CR-01 轮换泄漏的生产库密码 → 跨阶段安全债（review 不折叠）
