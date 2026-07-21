# Phase 5: 报表迁移 - Discussion Log

> **Audit trail only.** Do not use as input to planning, research, or execution agents.
> Decisions are captured in CONTEXT.md — this log preserves the alternatives considered.

**Date:** 2026-07-17
**Phase:** 5-报表迁移
**Areas discussed:** Controller 落位, reportClient 载体, Feign 契约接线, 验收深度+mybatis 配置, Excel 导出范围, 53 套迁移测序, 分页正确性保真, @MapperScan/entity 风险验证

---

## Controller 落位

| Option | Description | Selected |
|--------|-------------|----------|
| zgbas-admin | 按 ROADMAP 字面。报表 api 是前端直连 edge 控制器（非 Feign 服务 API）；前端 report 模板/静态资源已在 admin；源 ReportServer 本就是前端面向的报表服务（8002）。admin 已依赖 system（D-08），@Autowired report service 天然可用。 | ✓ |
| zgbas-system | 按 Phase 4 先例（basServer/api→system，与 service+mapper 同模块）。但报表 api 非 Feign 服务 API，Phase 4「impl 与契约同位」理由不完全适用；且会让 admin 依赖反转（模板在 admin、控制器在 system）。 | |

**User's choice:** zgbas-admin（推荐）
**Notes:** 55 个 reportServer/api 是前端直连 @RestController（return Page 给前端），角色区别于 Phase 4 的 basServer/api（Feign 服务 API）。落 admin 与前端模板同模块。路径问题经研究自行消解（前端实测调 /rpt/...，不带 /spt-bas-report 前缀，单体根 / 下端点直接暴露，前端零改）。

---

## reportClient 数据载体

| Option | Description | Selected |
|--------|-------------|----------|
| 内联源码进 system | 对齐 Phase 4 basClient 处理（照搬保包名 D-P2-07）。reportClient entity(53)/vo/remote 契约 verbatim 进 zgbas-system，与已迁 basClient 同构。消除私服 jar 依赖，mybatis type-aliases 指向本地源码包。一致性最高。 | ✓ |
| 保留 jar 依赖 | 报表代码编译期吃 report-client jar（types-only），不内联。少搬 265 文件，但与 Phase 4 basClient（已内联）处理不一致，且留私服 jar 依赖。 | |

**User's choice:** 内联源码进 system（推荐）
**Notes:** 265 java（53 实体+vo+remote 契约）verbatim 内联。子细节（部分实体是否带 @Entity、JPA 扫描误拾）留 research/planning，按 D-P4-05 仅修运行阻塞（并入 D-P5-07）。

---

## Feign 契约接线深度

| Option | Description | Selected |
|--------|-------------|----------|
| 接通 report 契约 | 本期解除 D-P4-02 对 report 契约的 stub 降级，让 web BFF（MyIndexController 等 P3 遗留）的 @Autowired IReport*Client 真正可调。机制留 research 核实（55 report api 路径是否满足契约，否则按 D-P4-01 方案 A 自回环对齐）。关闭 Phase 3 MyIndexController 缺口，完整行为等价。 | ✓ |
| 维持 stub 降级 defer | 本期仅迁报表本体（REPORT-01/02/PERSIST-02 字面）。report Feign 契约留 @Autowired(required=false) 404，与 purchase 一起后续接通。范围最小但 MyIndexController 缺口延续到 P7。 | |

**User's choice:** 接通 report 契约（推荐）
**Notes:** 关闭 Phase 3 因 report 契约缺口 deferred 的 MyIndexController。机制（55 api 是否天然满足契约路径）留 research，不擅自改契约。

---

## 验收深度 + mybatis 配置

| Option | Description | Selected |
|--------|-------------|----------|
| 启动 + 抽样查询 proof | 全 Spring context 启动 + 53 Mapper bean 全解析 + 抽样跑几个报表 Mapper 查询返回非空（证 PERSIST-02 mybatis 复杂查询可用 + XML namespace 正确 + mapper-locations 接通）。对齐 D-P4-06 基线。 | ✓ |
| 仅启动验证 | 只验 53 Mapper bean 解析 + context 启动，查询正确性完全留 P7。最小，但 PERSIST-02「mybatis 报表查询可执行」未实证。 | |
| 全 53 套查询 proof | 每个报表跑一次查询。最彻底但成本高、非 hermetic（需真实库），边际收益低（P7 ALIGN-02 回归已覆盖）。 | |

**User's choice:** 启动 + 抽样查询 proof（推荐）
**Notes:** mybatis 配置合并经研究定为 Claude discretion（report XML 与 SampleMapper 同位、type-aliases 追加 report 包、myBatisConfig.xml 近空丢弃），不占决策。

---

## Excel 导出范围

| Option | Description | Selected |
|--------|-------------|----------|
| 全量保行为等价 | 所有 exportExcel 端点随报表 service 照搬即等价（用已内联 spt-tools Excel util / POI）。导出是高频可见行为，行为等价核心要求保；逻辑随 service 迁入天然可用。特殊依赖（模板文件）留 research。 | ✓ |
| 导出 defer 到 P7 | 本期只迁查询 findPage，exportExcel 暂 stub/404，P7 行为对齐时补。范围更小但导出缺口（用户可见行为缺失）。 | |

**User's choice:** 全量保行为等价（推荐）
**Notes:** 导出逻辑在 report service 里，照搬 service = 导出可用。特殊依赖留 research，运行阻塞才修。

---

## 53 套迁移测序

| Option | Description | Selected |
|--------|-------------|----------|
| 按域分批 wave | 对齐 Phase 4 D-P4-03 wave + 逐层 compile 绿灯教训。按业务域（合同台账/收付款/风控/业绩等）分批，每批 compile + 抽样 proof 绿灯再下一批。报表同质但分批控 mybatis namespace/依赖级联风险。 | ✓ |
| 一次性全量批迁 | 53 套模式统一，机械批迁一次完成，一次 compile + proof。更快但级联风险高（Phase 1 gotcha 教训：一次性 unmask 雪崩）。 | |

**User's choice:** 按域分批 wave（推荐）
**Notes:** 具体域划分留 planning。

---

## 分页正确性保真

| Option | Description | Selected |
|--------|-------------|----------|
| 1:1 照搬等价 | findPage 的 count + 分页 SQL verbatim 照搬，含 count 查询语义。行为等价核心价值。分页性能优化 out-of-scope（不优化），但正确性必须等价。 | ✓ |
| 重 count 报表近似 defer | 默认 1:1 等价，但极重 count 报表（超时型）接受分页近似或 defer。作为 runtime-blocking 逃生阀（对齐 D-P4-05 仅修运行阻塞）。 | |

**User's choice:** 1:1 照搬等价（推荐）
**Notes:** 分页性能 OUT-of-SCOPE，但正确性等价是行为等价一部分。重 count 仅作 runtime-blocking 逃生阀。

---

## @MapperScan / entity 风险验证

| Option | Description | Selected |
|--------|-------------|----------|
| 本期显式验证 | research/planning 显式核实：@MapperScan 覆盖 com.spt.bas.report.server.dao（必要时放宽 basePackages）+ 53 实体无 @Entity 误拾（若有按 D-P4-05 仅修运行阻塞）。启动期 fail-fast 暴露。 | ✓ |
| 靠启动 proof 暴露 | 不单独 research，依赖验收启动 proof 自然暴露问题。省事但可能启动报错时才发现。 | |

**User's choice:** 本期显式验证（推荐）
**Notes:** 并入 D-P5-07（mybatis 配置合并 + 显式验证）。

---

## Claude's Discretion

- **HTTP 路径**：端点暴露在 `/rpt/...`，前端实测不带 `/spt-bas-report` 前缀（源 web 内部转发），单体根 `/` 下前端模板零改、无需 path 剥离器。`/spt-bas-report` context-path 蒸发。
- **mybatis 配置合并**：report XML 与 SampleMapper 同位落盘（现有 mapper-locations 覆盖）；type-aliases-package 追加 report 包；myBatisConfig.xml 近空丢弃。
- **报表 service 事务标注**：随源照搬，沿用 Phase 2 JpaTransactionManager @Primary + 单 DataSource。
- **reportServer infra 筛选**（对齐 D-P4-04）：util/vo/config 按需照搬；task/command 排除（Phase 6）；listener 评估。

## Deferred Ideas

- xxl-job reportServer/task（1）+ command（1）+ listener（评估）→ Phase 6
- 真实报表/业务回归对照 + 浏览器 e2e → Phase 7（ALIGN-01/02）
- 报表物理分页性能改造 → 永久 Out-of-Scope（PROJECT.md）
- basWx 采购业务 + purchase 契约 → v2（purchase 契约维持 D-P4-02 stub 降级）
- 全量实体 schema drift reconcile + 重开 ddl-auto=validate → tech debt（todo 保留 open）
- CR-01 真实轮换已泄漏生产库密码 → 跨阶段安全债
