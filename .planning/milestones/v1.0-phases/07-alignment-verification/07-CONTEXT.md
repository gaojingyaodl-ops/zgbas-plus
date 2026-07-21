# Phase 7: 行为对齐验证 - Context

**Gathered:** 2026-07-19
**Status:** Ready for planning

<domain>
## Phase Boundary

这是单体化重构的**收尾验收阶段** —— 不写新业务代码（除可能的阻塞型 gap 收口），而是**证明单进程 zgbas-plus 与旧微服务 zgbas 在「登录 → 首页 → 核心业务 → 报表 → 定时任务」全链路上行为等价**。本期交付 2 项需求：

1. **ALIGN-01 — 端到端可用**：单服务启动后，登录 → 首页 → 核心业务 → 报表 → 定时任务 全链路端到端可用（经 Phase 3/4/5/6 已分别交付各环节，本期证明它们串起来能跑通）。
2. **ALIGN-02 — 行为等价回归对照**：关键业务流程与旧系统 zgbas 行为等价（回归对照通过）。

**本期是验证阶段，非实现阶段。** 唯一可能的代码/数据改动是**阻塞型 gap 收口**（D-P7-04：15 REVIEW-flagged sys_job 参数修订 + /monitor/job 菜单 INSERT 落外部 spt-auth DB），不写新业务代码。

**承前（Phase 6 明确 carry-over 到 P7，in-scope for ALIGN-01/02）：**
- **写类真实回归**（D-P6-05 写类空跑的运行版本）—— autoPay / refreshContractStatusTask / OrverdurTask / DepositPaymentTask 等写类任务在 P7 做真实执行回归（非空跑）。
- **浏览器 e2e / 人工 UI 验收** —— Phase 6 HUMAN-UAT pending 项 2（/monitor/job UI 渲染）+ 项 3（15 REVIEW sys_job operator review）在 P7 推进收口。
- **cron 调度 cadence + 任务执行 outcome 行为等价** vs 源 zgbas。

**不在本期（明确边界）：**
- **写新业务代码 / 新功能** → 不做（本期是验证，非实现）。
- **basWx / purchase 业务 + 5 handler** → v2（#14，随业务整体 defer）。
- **28 个源迁移清单外的生产 handler** → Phase 6 gap-closure plan（`/gsd:plan-phase 06 --gaps`）或 v2，**不阻塞 P7 sign-off**（D-P7-04，仅记 known-gap）。
- **报表物理分页性能改造 / 补 createBy-updateBy 审计字段（259 表加列）** → 永久 Out-of-Scope（PROJECT.md）。
- **全量实体 schema drift reconcile + 重开 ddl-auto=validate（D-P2-02 原意）** → tech debt，P7 仅修验收期运行阻塞型 drift。
- **CR-01 真实轮换已泄漏生产库密码** → 跨阶段安全债，仍 deferred。
- **浏览器 e2e 自动化基建（Playwright/Selenium）** → 本期不引入（项目零此类基建 + 最小改动取向；前端验收走人工 UAT）。
- **xxl-job admin 服务退役** → ops 事务，**P7 sign-off 之后**（D-P7-04）。

</domain>

<decisions>
## Implementation Decisions

### 验证执行形态
- **D-P7-01: 混合形态 —— 后端关键接口扩 `ZgbasApplicationTest` proof（脚本化断言）+ 前端关键流人工浏览器 UAT 清单。** 后端：沿用 D-P5-08 / D-P6-04 的 `@Disabled` 手动验收口模式，在 `ZgbasApplicationTest` 加 Phase 7 proof（如 smoke 级接口可达 + 关键写类真实回归 + 高风险域抽样），默认 `@Disabled` 避免 dev 库污染（D-P6-06-01 模式），手动启用跑。前端：关键流（登录 → 首页菜单 → 报表查询 → 任务手动触发）走人工浏览器 UAT，清单落 `07-HUMAN-UAT.md`（沿用 Phase 6 HUMAN-UAT 机制）。复用现有测试基建，成本/覆盖均衡，行为等价有可重复证据。弃"纯人工 UAT"（ALIGN-02 缺可重复证据）/ 弃"纯 HTTP 脚本"（前端交互行为如报表渲染覆盖弱）/ 弃"浏览器 e2e 自动化"（项目零 Playwright/Selenium 基建，JDK1.8+SpringBoot2.5.9+Thymeleaf 栈下新增依赖+学习成本高，偏离最小改动）。

### 行为等价对照基准
- **D-P7-02: 混合基准 —— 独立验收 + golden 预期为骨干 + 选择性 side-by-side JSON diff。** 骨干：zgbas-plus 独立验收，golden 预期来源 = dev DB `sptbasdb_pd` 现有数据 + 业务规则 + 源码行为推导的预期值（断言查询返回非空 / 字段正确 / 分页 count 一致 / 导出行数合理）。**选择性 side-by-side**：仅对**高风险 / 低信心接口**（关键写类如 autoPay 的业务 outcome、高频可见如报表导出）在**老系统能起的范围内**同输入两系统跑、JSON diff 比对响应。⚠ 老系统 zgbas 需起 4 微服务 + 外部依赖（spt-auth / xxl-job admin / MySQL prod 数据），全套并起成本高且未必稳 → side-by-side 是**选择性**而非全量。弃"纯 side-by-side"（全套依赖重 + 老系统起不稳风险）/ 弃"纯独立 + golden"（对齐证据较弱，无老系统呼应）/ 弃"人工目视"（ALIGN-02 欠结构化证据，sign-off 主观）。

### 覆盖深度与抽样
- **D-P7-03: 分级覆盖 —— smoke 必跑 + 高风险域深验 + 长尾标 known-gap。**
  - **smoke 必跑**（关 ALIGN-01 端到端可用）：登录 → 首页 → 核心业务（合同/授信/库存/放款 各 1-2）→ 报表（1-2 套）→ 定时任务（手动触发 1-2 个）全链路串通。
  - **高风险域深验**（关 ALIGN-02 行为等价）：高频可见行为（Excel 导出 / 分页正确性）+ 关键写类真实回归（autoPay / refreshContractStatusTask / OrverdurTask / DepositPaymentTask 等，对齐 D-P6-05 写类 P7 真跑承诺）+ 报表查询语义抽样（合同台账 / 收付款 / 风控 / 业绩 各域选代表）。
  - **长尾标 known-gap**：53 报表不全跑、64 任务不全触、28 未迁 handler 不补 —— 选代表样本验收，余者记 known-gap 清单（D-P7-04 路由）。
  弃"全链路 smoke"（高风险域覆盖不足）/ 弃"风险抽样"（跳过 smoke 致 ALIGN-01 证据不全）/ 弃"按模块全面回归"（成本极高，与项目务实基调冲突）。

### Phase 6 遗留 gap 在 P7 的处理
- **D-P7-04: 混合 —— 阻塞型 gap 收口 in P7 UAT / 非阻塞 defer 不阻塞 sign-off。**
  - **阻塞型（P7 UAT 推进收口）：**
    - **15 REVIEW-flagged sys_job operator review**（参数占位 / 空 args / 注释方法体）—— 影响**已迁任务**运行正确性（ALIGN-02 相关）→ P7 UAT 项，operator 逐条 review（keep / modify args / PAUSE），修订后重应用 SQL。承 Phase 6 HUMAN-UAT pending 项 3。
    - **/monitor/job 菜单 INSERT 落外部 spt-auth DB**（`06-01-MENU-INSERT.sql`）—— Phase 6 HUMAN-UAT pending 项 2（浏览器验 /monitor/job UI 渲染）的**前置**，P7 推进落库 + 浏览器验收。
  - **非阻塞（defer / 路由，不阻塞 P7 sign-off，仅记 known-gap 清单）：**
    - **28 未迁 handler**（含 11 行 GuTu/工商 batch）→ Phase 6 gap-closure plan（`/gsd:plan-phase 06 --gaps`）或 v2，源迁移清单外，P7 不补迁。
    - **7 ambiguous executeCommand entries**（源 prod 从未触发）→ ops 确认 `xxl_job_group → executor_appname` 映射或确认 skip。
    - **xxl-job admin 服务退役** → ops 事务，P7 sign-off **之后**。
  弃"P7 内全收口"（28 handler 补迁成本高，偏离 P7 验证定位）/ 弃"纯验证全 defer"（15 REVIEW 未核就 sign-off 会掩盖已迁任务参数错配风险）。

### Claude's Discretion
- **UAT 清单产物落位**：`07-HUMAN-UAT.md`（沿用 Phase 6 `06-HUMAN-UAT.md` frontmatter + 结构），整合 Phase 6 carry-over pending 项 + Phase 7 新增 smoke/高风险域/写类回归项。
- **proof 默认 `@Disabled`**：对齐 D-P6-06-01，避免 dev 库 `sptbasdb_pd` 污染（写类真实回归尤其重要），手动启用跑后恢复 `@Disabled`。
- **side-by-side 接口选择**：交 planning 按风险路由 —— 优先关键写类 outcome（autoPay 等）+ 高频可见（报表导出），在老系统可起范围内选 3-5 个代表；老系统起不稳则降级为独立 + golden。
- **高风险域具体清单**：交 planning 落 UAT 项 —— 写类（autoPay / refreshContractStatusTask / OrverdurTask / DepositPaymentTask + D-P6-05 标注的写类全集）+ 导出（`/rpt/*/exportExcel` 抽样）+ 分页（`findPage` count + 分页 SQL 正确性，D-P5-06 1:1 等价）。
- **golden 预期来源**：dev DB 现有数据为主 + 业务规则 + 源码行为推导；若需从 prod 导脱敏样本做对照基线，作 `checkpoint:human-blocked` 等用户提供。
- **验收非 hermetic 沿用**：明文密钥态（Phase 4 决定），dev DB `sptbasdb_pd`，启动测试/proof 不需 export `DB_PASSWORD`/`SPT_APP_SECRET`（D-P3-13 / 记忆 project_phase4-plaintext-secrets-decision）。
- **运行期 drift 即修**：验收期 MockMvc/HTTP proof + 浏览器实测暴露的运行期映射错误（实体 drift / bean 缺失 / 路由 404）按 D-P4-05 仅修阻塞型，不全量 reconcile（与 Phase 4/5 一致）。
- **起源系统 side-by-side 的可行性预核**：planning 先快速评估老 zgbas 4 服务能否在本地起来（依赖齐不齐 / DB 连通 / spt-auth 外部可达），不可起则 side-by-side 降级、全量走独立 + golden，避免验收期卡在环境搭建。

### Folded Todos
- 无 todo 折叠进本期（2 个弱匹配均不折叠，见 Reviewed Todos）。

</decisions>

<canonical_refs>
## Canonical References

**Downstream agents MUST read these before planning or implementing.**

### 项目规划（仓库内，必读）
- `.planning/ROADMAP.md` §Phase 7 — 阶段目标（单服务端到端可用 + 行为等价）/ 依赖（Phase 3/4/5/6）/ 需求映射（ALIGN-01/02）/ 2 条成功标准
- `.planning/REQUIREMENTS.md` — ALIGN-01（单服务启动后 登录→首页→核心业务→报表→定时任务 端到端可用）/ ALIGN-02（关键业务流程与旧系统 zgbas 行为等价，回归对照）/ ALIGN-03（编译止血基线，Phase 1 已 Complete）
- `.planning/PROJECT.md` — Core Value（单进程跑全功能 + 行为对齐）、Out-of-Scope（报表物理分页 / 补审计字段 / basWx / Spring Boot 3 升级）、已知风险（mock-password 后门 / 生产库明文密码 / PageHelper 内存分页）
- `.planning/phases/02-infrastructure/02-CONTEXT.md` — **D-P2-02**（ddl-auto=none，P7 仅修运行阻塞 drift）/ **D-P2-07**（照搬保包名）/ **D-P2-09**（mybatis-plus 栈）/ @Primary 双 ORM 接线 / 非 hermetic 验收基线
- `.planning/phases/03-auth-homepage/03-CONTEXT.md` — **D-P3-10**（stub-port 降级，purchase 契约仍维持 v2）/ **D-P3-13**（启动验证为主 + 非 hermetic + 明文密钥不需 export，P7 沿用）
- `.planning/phases/04-core-business/04-CONTEXT.md` — **D-P4-01/01a**（Feign 自回环 + path 前缀剥离）/ **D-P4-05**（ddl-auto=none + 仅修运行阻塞，P7 验收期 drift 处理沿用）/ **D-P4-06**（启动验证 + WR-02 HTTP proof，P7 扩展基线）/ Phase 4 deferred 真实业务 CRUD 对照 → P7（ALIGN-02）
- `.planning/phases/05-report-migration/05-CONTEXT.md` — **D-P5-06**（分页正确性 1:1 等价，P7 高风险域深验项）/ **D-P5-08**（启动 + 抽样 proof 非 hermetic，P7 proof 模式直接复用）/ deferred 真实报表回归 → P7
- `CLAUDE.md` — 技术栈约束（JDK 1.8 / SpringBoot 2.5.9 / Maven 3.8.6 + zg_settings.xml）、固定 5 模块、外部 spt-auth 保持外部（#7）、双 ORM（#6）

### Phase 6 carry-over（P7 直接承接，必读）
- `.planning/phases/06-quartz-migration/06-CONTEXT.md` — **D-P6-04/05/06**（验收策略 + 写类空跑 + 强 Scheduler fail-fast；D-P6-05 写类真实回归 → P7）/ **D-P6-12**（行为等价范围：仅保阻塞+日志，不保重试/告警，P7 对照口径）/ deferred 真实全量任务回归 + 浏览器 e2e → P7（ALIGN-01/02）
- `.planning/phases/06-quartz-migration/06-HUMAN-UAT.md` — **Phase 6 UAT 机制模板 + 2 个 pending 项**（项 2 /monitor/job UI 浏览器验收 + 项 3 15 REVIEW sys_job operator review），P7 UAT 承接 + `07-HUMAN-UAT.md` 沿用此结构
- `.planning/phases/06-quartz-migration/deferred-items.md` — **Phase 6 遗留物全量清单**（28 未迁 handler / 7 ambiguous executeCommand / 15 REVIEW sys_job / 菜单 INSERT / xxl-job admin 退役 / 源 bug preserved 1:1），D-P7-04 gap 分级处理输入
- `.planning/phases/06-quartz-migration/06-VERIFICATION.md` — Phase 6 验证产物（30 tests GREEN / 3 skipped / 5 需求 + 4 成功标准代码层达成），P7 在此基础上扩全链路验收

### 已知运行期缺口（验收期大概率复现或需关注）
- `.planning/debug/login-feign-selfloop-shiro.md` — **登录期 Shiro 自回环 anon 修复**（`/spt-bas-server/**` 加入 anon 列表，方案 A 用户已批准）+ /index 两例 NPE 已修（ConfigUtil.init + ShiroUtil bean，commit d0a388e + 49682cf）。**根模式**：web 侧启动接线未完全并入单体，验收期遇类似 NPE/自回环 404 按此模式排查。⚠ P7 验收前核此修复是否已落代码（debug 文件 status: investigating，planning 须确认实际 commit 状态）。

### 源项目（side-by-side 对照来源，**绝对路径，非本仓库内**）
- `/Users/alan/WorkSpace/IDEA/zgbas`（分支 `feat-系统重构v5.0`）— 源微服务（4 服务：Web=80 / BasServer=8001 / ReportServer=8002 / PurchaseWx=8013）。P7 选择性 side-by-side 对照来源；planning 先评估可起性（D-P7-02 discretion）。
- `/Users/alan/WorkSpace/IDEA/spt-auth` — 外部 spt-auth（RuoYi 改造），/monitor/job 菜单 INSERT 落库目标 DB（D-P7-04 阻塞型 gap）。

### 当前单体（已就位的 Phase 2-6 验证资产）
- `zgbas-admin/src/test/java/com/spt/ZgbasApplicationTest.java` — **P7 proof 落点**（现有 probe/proof + `@Disabled` 模式：`sampleReportQuery_proof` Phase 5 / `sampleQuartzJobDryRun_proof` Phase 6 / `reportHttpReachability_proof` / `feignSelfLoopbackWiring_probe` 等），P7 加 smoke + 写类真实回归 + 高风险域 proof
- `zgbas-admin/src/test/java/com/spt/proof/InProcessContractTest.java` — Phase 2 D-P2-10 proof（已修 webEnvironment=RANDOM_PORT，Phase 6 deferred-items 记录），P7 可扩 MockMvc HTTP 断言
- `zgbas-admin/src/main/java/com/spt/ZgbasApplication.java` — 单一启动类，`@ComponentScan("com.spt")` 全包扫描
- `application.yml` + `application-{dev,prod}.yml` — Phase 2 配置基线（dev 明文密钥，P7 验收非 hermetic 沿用）

### 构建 / 工具链（绝对路径）
- `/Users/alan/App/apache-maven-3.8.6` — Maven 可执行（本机默认 JDK 21，每条 mvn 必须前缀 `JAVA_HOME=` Corretto 1.8，记忆 project_zgbas-plus-jdk8-mvn-prefix）
- `/Users/alan/App/apache-maven-3.8.6/zg_settings.xml` — 私服仓库重定向 settings（构建必用）
- `/Users/alan/App/Repository` — 本地仓库

</canonical_refs>

<code_context>
## Existing Code Insights

> zgbas-plus 当前为 **Phase 6 完成态**：骨架 + spt-tools 全量内联 + 双 ORM 单 DataSource + 外部 SDK bean + nacos 删除 + Shiro 登录 + basServer 核心业务（service/api/BFF）全量迁入 + 53 报表全量迁入 + report 契约接通 + xxl-job 删除 + ~60 handler 迁 RuoYi quartz + sys_job 数据翻译落库 + /monitor/job UI。Phase 7 不新增业务代码，仅在此基线上做全链路验收 + 阻塞型 gap 收口。

### Reusable Assets
- **`ZgbasApplicationTest` probe/proof 模式**（`@Disabled` 默认 off + 手动启用）—— P7 smoke / 写类真实回归 / 高风险域 proof 直接加在此文件，沿用 D-P5-08 / D-P6-04 / D-P6-06-01 模式。
- **`06-HUMAN-UAT.md` 结构**（frontmatter `status/phase/source/started/updated` + Tests 清单 + Summary + Gaps）—— P7 `07-HUMAN-UAT.md` 沿用，整合 Phase 6 carry-over pending 项 + P7 新增项。
- **Phase 5/6 抽样 proof 实例**（`sampleReportQuery_proof` / `sampleQuartzJobDryRun_proof` / `reportHttpReachability_proof`）—— P7 写类真实回归 proof 参照 Branch A/B 模式（read-only 真跑 / write-class 分级）。
- **`InProcessContractTest`**（MockMvc HTTP proof）—— P7 关键接口 HTTP 断言扩展对象。
- **Phase 2 `@Primary` Druid DataSource + `JpaTransactionManager`** —— 验收期业务查询/写操作事务天然可用。

### Established Patterns
- **启动验证为主 + 抽样 proof + 非 hermetic**（D-P3-13 / D-P4-06 / D-P5-08 / D-P6-06）—— 明文密钥态，dev DB `sptbasdb_pd`，不需 export `DB_PASSWORD`/`SPT_APP_SECRET`。
- **`@Disabled` 默认 off 防污染**（D-P6-06-01）—— 写类真实回归 proof 尤其守此纪律，手动跑后恢复。
- **checkpoint:human-verify 机制**（D-P6-02）—— 15 REVIEW sys_job operator review、golden 预期 prod 样本走此机制。
- **照搬保包名 / 行为等价优先于安全加固**（D-P2-07 / plaintext-secrets 决定）—— P7 验收口径。
- **ddl-auto=none + 仅修运行阻塞**（D-P4-05）—— 验收期 drift 处理沿用，不全量 reconcile。
- **逐层 compile 绿灯**（Phase 1 gotcha 教训）—— 阻塞型 gap 收口（SQL 修订）后 `mvn compile` + `mvn test` 全 reactor 绿再 sign-off；grep `^\[ERROR\]` locale 无关。

### Integration Points
- `ZgbasApplicationTest.java` ← P7 新增 proof（smoke 接口可达 / 写类真实回归 / 高风险域抽样），默认 `@Disabled`。
- `07-HUMAN-UAT.md` ← P7 UAT 清单（Phase 6 carry-over pending 项 2/3 + P7 smoke/高风险域/写类回归人工项）。
- `zgbas-quartz/src/main/resources/sql/sys_job_data.sql` ← 15 REVIEW sys_job operator review 后修订 args 重应用（D-P7-04 阻塞型）。
- 外部 spt-auth DB `sys_menu` 表 ← `06-01-MENU-INSERT.sql` 落库（D-P7-04 阻塞型，/monitor/job UI 验收前置）。
- 源 zgbas 4 服务（选择性 side-by-side）← 同输入两系统 JSON diff（D-P7-02，高风险/低信心接口）。
- `.planning/debug/` ← 验收期新发现的运行期缺口（NPE / 自回环 404 / bean 缺失）按 login-feign-selfloop-shiro.md 模式记录。

</code_context>

<specifics>
## Specific Ideas

- 用户在 4 个灰区**全部选「混合 / 分级」务实平衡**，无一选极端（纯人工 / 全 side-by-side / 全面回归 / 全收口）—— 与项目全程「行为等价优先 + 务实成本控制 + 与早期阶段一致」基调完全吻合。Phase 7 定位明确为**纯验证阶段**（非实现），唯一可能改动是阻塞型 gap 收口（SQL 修订 + 菜单 INSERT），非新业务代码。
- 用户认同"分级覆盖 + 长尾标 known-gap"——接受 53 报表不全跑、64 任务不全触，选代表样本验收 + 余者记 known-gap 清单，体现务实等价边界（与 D-P6-12 "任务能调度+执行+记录即核心"同精神）。
- 用户认同"非阻塞 gap 不阻塞 sign-off"——28 未迁 handler 走独立 gap-closure plan、7 ambiguous + xxl-job admin 退役走 ops，P7 仅要求 known-gap 清单透明记录，不一票否决。
- side-by-side 选择性而非全量，体现对"老系统全套并起成本高"的现实认知；planning 须先评估老系统可起性，不可起则降级。

</specifics>

<deferred>
## Deferred Ideas

- **浏览器 e2e 自动化（Playwright/Selenium）** → 本期不引入（项目零此类基建 + JDK1.8/SpringBoot2.5.9/Thymeleaf 栈 + 最小改动取向）；前端验收走人工 UAT。未来若需回归自动化可另立基建 task。
- **28 个源迁移清单外的生产 handler（含 11 行 GuTu/工商 batch）** → Phase 6 gap-closure plan（`/gsd:plan-phase 06 --gaps`）或 v2（D-P7-04，不阻塞 P7 sign-off）。
- **7 ambiguous executeCommand entries** → ops 确认 `xxl_job_group → executor_appname` 映射或确认 skip（源 prod 从未触发）。
- **xxl-job admin 服务退役** → ops 事务，P7 sign-off **之后**。
- **全量 53 报表 + 64 任务回归** → P7 选代表样本 + 长尾 known-gap；全量回归若需，另立 task。
- **basWx / purchase 业务 + 5 handler** → v2（#14）。
- **报表物理分页性能改造 / 补 createBy-updateBy 审计字段** → 永久 Out-of-Scope（PROJECT.md，259 表结构变更）。
- **全量实体 schema drift reconcile + 重开 ddl-auto=validate（D-P2-02 原意）** → tech debt（todo `phase4-resolve-entity-schema-drift` 保留 open；P7 仅修验收期运行阻塞 drift）。
- **CR-01 真实轮换已泄漏生产库密码** → 跨阶段安全债（outward-facing），仍 deferred。
- **Spring Boot 3 / JDK 17 大版本升级** → Out-of-Scope（javax→jakarta 影响 259 实体，与 JDK 1.8 硬约束冲突）。

### Reviewed Todos (not folded)
- `phase4-resolve-entity-schema-drift.md`（D-P2-02 偏离）→ tech debt，Phase 4 降级保留 open；P7 仅修验收期运行阻塞型 drift，不全量 reconcile，不折叠。
- `rotate-leaked-prod-credentials.md`（CR-01 轮换已泄漏生产库密码）→ 跨阶段安全债，弱匹配 Phase 7（通用关键词），review 不折叠。

</deferred>

---

*Phase: 7-行为对齐验证*
*Context gathered: 2026-07-19*
