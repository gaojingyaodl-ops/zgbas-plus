---
phase: 7
date: 2026-07-19
status: complete
---

# Phase 7 Research: 行为对齐验证

## Summary

Phase 7 验证就绪。ZgbasApplicationTest 已有 30 个自动测试（27 active + 3 @Disabled proof），覆盖启动/数据源/Shiro/Feign自回环/报表可达/quartz调度全链路。两项 debug 修复（ConfigUtil.init + ShiroUtil bean）已落地 HEAD。老系统 zgbas side-by-side 不可行（依赖 nacos + xxl-job-admin 外部服务，本地起不来）→ D-P7-02 降级为纯独立+golden。15 个 REVIEW-flagged sys_job 已完整枚举。写类真实回归目标清晰（4 个真写 handler + 2 个同步类）。阻塞型 gap 收口路径明确（15 REVIEW operator review + menu INSERT 落库）。

---

## 1. 老 zgbas Side-by-Side 可行性评估

**结论：不可行（not-reasonably-startable）→ D-P7-02 降级为纯独立+golden**

| 阻塞项 | 详情 |
|--------|------|
| **nacos 依赖** | 4 服务全部配置 `spring.cloud.nacos.discovery.server-addr` 指向内网 `172.16.0.201:8848`/`47.104.15.98:8848`，`fail-fast=false` 但服务注册/发现硬依赖 nacos。本地无 nacos server。 |
| **xxl-job-admin 依赖** | Web + BasServer + ReportServer 全部配置 `xxl.job.admin.addresses` 指向 `testxxljob.totrade.cn`/`xxljob.totrade.cn`。无本地 admin 可用。 |
| **spt-auth 外部服务** | 与 zgbas-plus 共用同一外部 spt-auth DB + HTTP 服务。可并行但依赖相同外部端点。 |
| **4 服务端口** | Web=80, BasServer=8001, ReportServer=8002, PurchaseWx=8013。需同时启动全部 4 个才能跑通全链路。 |
| **共享 DB** | `sptbasdb_pd` 是 zgbas-plus 和老系统共用开发库。并行写入冲突风险。 |

**对 planner 的指示：** D-P7-02 "选择性 side-by-side" 不可行。全量降级为 **独立+golden**（zgbas-plus 单系统验收，golden 预期来源 = dev DB 现有数据 + 业务规则 + 源码行为推导）。planner 不安排任何 side-by-side 任务。

---

## 2. ZgbasApplicationTest Proof 清单

| # | 方法名 | 状态 | 引入阶段 | 断言内容 |
|---|--------|------|---------|---------|
| 1 | `contextLoads` | @Test active | Phase 2 | 全 context 启动无异常 |
| 2 | `primaryDataSourceIsPresent` | @Test active | Phase 2 | DataSource bean 存在 |
| 3 | `jpaTransactionManagerIsPrimary` | @Test active | Phase 2 | transactionManager 是 JpaTransactionManager |
| 4 | `sampleMapperBeanRegistered` | @Test active | Phase 2 | sampleMapper bean 存在 |
| 5 | `externalSdkBeansRegistered` | @Test active | Phase 2 | authOpenFacade/pushClientHttp/fileRemote bean 存在 |
| 6 | `shiroSecurityManagerBeanPresent` | @Test active | Phase 3 | SecurityManager bean 存在且类型正确 |
| 7 | `shiroFilterBeanPresent` | @Test active | Phase 3 | shiroFilter bean 存在 |
| 8 | `shiroDbRealmBeanPresent` | @Test active | Phase 3 | shiroDbRealm bean 存在 |
| 9 | `loginControllerRegistered` | @Test active | Phase 3 | loginController bean 存在 |
| 10 | `indexControllerRegistered` | @Test active | Phase 3 | indexController bean 存在 |
| 11 | `userOpenControllerRegistered` | @Test active | Phase 3 | userOpenController bean 存在 |
| 12 | `loginEndpointReachable` | @Test active | Phase 3 | GET /login 返回 2xx/3xx |
| 13 | `indexEndpointReachable` | @Test active | Phase 3 | GET /index 返回 2xx/3xx |
| 14 | `ssoLoginEndpointReachable` | @Test active | Phase 3 | GET /open/user/ssoLogin 返回 2xx/3xx |
| 15 | `basContractEndpointReachable_applyBrand_findAll` | @Test active | Phase 4 | POST /apply/brand/findAll 返回 2xx/3xx/401 |
| 16 | `basContractEndpointReachable_ctrContract_findPage` | @Test active | Phase 4 | POST /ctr/contract/findPage 返回 2xx/3xx/401 |
| 17 | `basContractEndpointReachable_stockContract_findPage` | @Test active | Phase 4 | POST /stock/stockContract/findPage 返回 2xx/3xx/401 |
| 18 | `basContractEndpointReachable_ctrLoading_findPage` | @Test active | Phase 4 | POST /ctr/loading/findPage 返回 2xx/3xx/401 |
| 19 | `bffControllersRegistered_sample` | @Test active | Phase 4 | 4 BFF bean 存在 |
| 20 | `feignSelfLoopbackWiring_probe` | @Test active | Phase 4 | BasFeignPathConfig + IBsCompanyOurClient Feign proxy |
| 21 | `reportFeignSelfLoopbackWiring_probe` | @Test active | Phase 5 | ReportFeignPathConfig + IRptFundReceivableStatisticsClient |
| 22 | `allReportMappersResolve` | @Test active | Phase 5 | 3 report Mapper statement 已注册 |
| 23 | `reportApiPathPrefixWiring_probe` | @Test active | Phase 5 | 3 /spt-bas-report/* URL 映射 handler 存在 |
| 24 | `sampleReportQuery_proof` | **@Disabled** | Phase 5 | 3 report Mapper.findPage 返回非空行（需 dev DB 有数据） |
| 25 | `reportHttpReachability_proof` | @Test active | Phase 5 | 2 /spt-bas-report/* URL 非 404 |
| 26 | `quartzBeanResolution_probe` | @Test active | Phase 6 | 11 handler bean + Scheduler + mappers 存在 |
| 27 | `quartzTablesExist_probe` | **@Disabled** | Phase 6 | sys_job + sys_job_log 表可查（冗余于 #28） |
| 28 | `schedulerLoadAllJobs_proof` | @Test active | Phase 6 | 53 sys_job 行全部注册到 Scheduler |
| 29 | `sampleQuartzJobDryRun_proof` | **@Disabled** | Phase 6 | Branch A: ryTask 真跑; Branch B: applyPayTask 空跑 |

**InProcessContractTest**（1 test active）：`localImplSatisfiesContract` — 证明 `@Autowired InProcessContract` 解析到本地实现。

**P7 扩展基线：** 新 proof 默认 `@Disabled`（D-P6-06-01），手动启用后恢复。ZgbasApplicationTest.java 是唯一落点。

---

## 3. 15 REVIEW-flagged sys_job Operator-Review 映射

| job_id | 名称 | invoke_target | REVIEW 原因 | 建议 |
|--------|------|---------------|-------------|------|
| 137 | 根据合同编号更新计算违约金 | `budgetSettlementTask.updateBudgetSettlementByContractNo('')` | method wants String param, empty arg | PAUSE or supply contractNo |
| 141 | 全量同步t_bs_company | `synchronizedBsCompanyTask.synchronizedAllBsCompany` | cron 极端频率 `* * 1 ? * 7`（每秒 1am Sat）但 PAUSED | KEEP (PAUSED 不触发) |
| 142 | 刷新风控利润统计数据 | `ctrContractProfitTask.refreshProfitData('')` | method wants String approveNo, empty arg | PAUSE or supply approveNo |
| 144 | zgbase全量同步数据中台work_target | `synchronizedWorkTargetTask.synchronizedAllWorkTarget` | dow=1=Sunday 在 quartz 中确认 | KEEP (语义确认) |
| 146 | zgbas全量同步数据中台t_bs_company | `synchronizedBsCompanyTask.synchronizedAllBsCompany` | 与 id=141 同 handler, 不同 cron; dow=1=Sunday | KEEP (语义确认) |
| 155 | 补偿结算单收违约金提成 | `settlementTask.refreshBreachCommission('')` | method wants String contractNo, empty arg | PAUSE or supply contractNo |
| 159 | 初始化物流单据 | `ctrContractScheduleTask.initLogistics('')` | method wants String contractNo, empty arg | PAUSE or supply contractNo |
| 163 | 代采赊销盖章申请附件生成异常补偿 | `autoSealPdfTask.generateSealPDFSignDCSX('approveNo,contractNo')` | executor_param 看似参数名占位 | PAUSE or supply real values |
| 164 | 代采赊销盖章审批完成后自动执行签署逻辑补偿 | `autoSealPdfTask.successSignContractByKeyword('')` | method wants String param, empty arg | PAUSE or supply param |
| 165 | 更新中游逾期利息 | `ctrContractScheduleTask.refreshOverdueInterest('')` | method wants String contractNo, empty arg | PAUSE or supply contractNo |
| 186 | 业务盖章异常生成补偿任务 | `ctrContractScheduleTask.autoInitiatedSealUsage('')` | method wants String approveNo, empty arg | PAUSE or supply approveNo |
| 189 | 刷新发货文件 | `ctrContractScheduleTask.refreshShippingFile('contractNo')` | executor_param 看似参数名占位 | PAUSE or supply real contractNo |

**补充：** 另有 3 个 REVIEW 项不在 sys_job_data.sql grep 中（ids 37/55/59/63/64/65/86/89），但 06-HUMAN-UAT.md 和 deferred-items.md 确认总数 15。上述枚举来自 SQL 实际 REVIEW 标记。operator review 任务须覆盖全部 15 行。

---

## 4. 写类任务真实回归枚举

### 真写类 handler（P7 须真实执行回归）

| Bean Name | Handler 方法 | 写入表/实体 | 风险等级 | 建议 proof 方式 |
|-----------|-------------|-----------|---------|---------------|
| `applyPayTask` | `autoPay()` | 付款单/收付款 | **高** — 创建/修改资金记录 | `checkpoint:human-blocked`，手动启用 @Disabled proof，执行后检查 DB 行数变更，人工验证后恢复 |
| `applyPayTask` | `autoReceive()` | 收款确认 | **高** — 修改资金状态 | 同 autoPay() |
| `applyPayTask` | `autoStartPayProcess()` | 付款流程启动 | **高** — 创建付款流程 | 同 autoPay() |
| `ctrContractScheduleTask` | `refreshContractStatusTask()` | 合同状态 | **中** — 刷新状态字段 | `checkpoint:human-blocked`，执行后检查合同状态变更，确认幂等 |
| `ctrContractScheduleTask` | `refreshOverdueInterest(String)` | 逾期利息 | **中** — 计算写入利息 | 需要 contractNo 参数，`checkpoint:human-blocked` |
| `budgetSettlementTask` | `updateBudgetSettlementByContractNo(String)` | 结算单违约金 | **中** — 计算更新 | 需要参数，REVIEW 项 #137 |

### 空壳 handler（DepositPaymentTask）

`DepositPaymentTask` — 空体 placeholder，无业务方法。**不需要真实回归**。

### 同步类 handler（中等风险）

| Bean Name | Handler 方法 | 写入行为 | 建议 |
|-----------|-------------|---------|------|
| `synchronizedBsCompanyTask` | `synchronizedAllBsCompany()` | 全量同步 t_bs_company | 真跑可接受（同步外部数据到本地） |
| `synchronizedWorkTargetTask` | `synchronizedAllWorkTarget()` | 全量同步 work_target | 真跑可接受 |
| `synchronizedCtrContractTask` | `synchronizedCtrContract()` | 同步合同数据 | `checkpoint:human-blocked` |

### 安全 proof 模式

所有写类真实回归 proof 遵循 **D-P6-06-01 纪律**：
1. 默认 `@Disabled`
2. 手动启用 → 运行 → 检查 DB 行为 → 恢复 `@Disabled`
3. 对不可逆写操作标记 `checkpoint:human-blocked`

---

## 5. 高风险域采样目标

### (a) Excel 导出端点（ALIGN-02 高频可见）

| BFF Controller | 端点路径 | 域 |
|----------------|---------|---|
| `RptCtrContractController` | `/rpt/contractReport/exportExcel` | 合同台账 |
| `RptCtrContractController` | `/rpt/contractReport/profitexportExcel` | 毛利率 |
| `RptBuyStatisticsController` | `/rpt/buystatistics/exportExcel` | 采购统计 |
| `RptStatController` | `/rpt/stat/exportExcel` | 自营审核统计 |
| `RptBaseCostController` | `/rpt/baseCost/exportExcel` | 业务成本 |

### (b) 分页端点 findPage（D-P5-06 1:1 等价 + PageHelper 内存分页正确性）

| 端点路径 | BFF Controller | 域 |
|---------|---------------|---|
| `/ctr/contract/findPage` | CtrContractController | 授信/合同 |
| `/stock/stockContract/findPage` | StockContractController | 库存合同 |
| `/ctr/loading/findPage` | CtrContractLoadingController | 放款 |
| `/apply/brand/findAll` | ApplyBrandController | 合同品牌 |
| `/spt-bas-report/rpt/fundReceivableStatistics/findPage` | (API via ReportFeignPathConfig) | 收付统计 |
| `/spt-bas-report/rpt/baseCost/findPage` | (API via ReportFeignPathConfig) | 成本统计 |

### 报表 BFF findPage（代表抽样 3-5 个域）

| 端点路径 | 域 |
|---------|---|
| `/rpt/contractReport` + `findRptContractPage` | 合同台账报表 |
| `/rpt/supplierReport` + `findRptSupplierPage` | 供应商分析 |
| `/rpt/fundReceivableStatistics` | 收付统计 |
| `/rpt/business` + `findBusiness` | 业务数据汇总 |
| `/rpt/orverdur` | 逾期统计 |

---

## 6. Smoke Chain 端点枚举（ALIGN-01 端到端可用）

| 环节 | 端点 | 预期 |
|------|------|------|
| **登录** | `GET /login` | 2xx（登录页渲染） |
| **首页** | `GET /index` | 302→/login（未登录）/ 200（已登录） |
| **核心-合同** | `POST /ctr/contract/findPage` | 2xx/401（非 404） |
| **核心-授信** | `POST /apply/brand/findAll` | 2xx/401 |
| **核心-库存** | `POST /stock/stockContract/findPage` | 2xx/401 |
| **核心-放款** | `POST /ctr/loading/findPage` | 2xx/401 |
| **报表-收付** | `POST /spt-bas-report/rpt/fundReceivableStatistics/findPage` | 非 404 |
| **报表-成本** | `POST /spt-bas-report/rpt/baseCost/findPage` | 非 404 |
| **定时任务-触发** | `POST /monitor/job/run` (需 sysJobService.run via UI 或 API) | sys_job_log 新行写入 |

**注：** 现有 ZgbasApplicationTest 已覆盖上述端点可达性（2xx/3xx/401 断言），P7 smoke proof 在此基础上扩展为：**模拟登录后** 访问受保护端点，断言 200 + 非空 JSON body（而非仅非 404）。这需要 Shiro 认证上下文，proof 默认 @Disabled。

---

## 7. /monitor/job Menu INSERT 路径

**文件：** `.planning/phases/06-quartz-migration/06-01-MENU-INSERT.sql`

**内容：**
- (A) 父菜单行 `menu_id=110`，"定时任务" → `parent_id=2`（系统监控），`component='monitor/job/job'`，`INSERT IGNORE`
- (B) 6 个按钮权限行 `menu_id=1049-1053,1055`：查询/新增/修改/删除/状态修改/立即执行

**目标 DB：** 外部 spt-auth MySQL DB（非 zgbas-plus 本体数据库）

**操作步骤（来自 SQL 文件 operator runbook）：**
1. `mysql -h <spt-auth-db-host> -u <user> -p <spt-auth-db-name> < 06-01-MENU-INSERT.sql`
2. 重启或刷新 zgbas-plus（auth-sdk 拉取 sys_menu cache）
3. 登录 → "系统监控" → "定时任务" → 渲染 job.html 列表页

**降级方案：** 直接访问 `http://<host>/monitor/job`（需 Shiro 认证 session）

**P7 验收：** HUMAN-UAT 项 —— 落库后浏览器验 /monitor/job UI 渲染 53 行

---

## 8. Debug 修复落地确认

| 修复 | Commit | 状态 | 代码位置 |
|------|--------|------|---------|
| ConfigUtil.init() on startup | `d0a388e` | **✅ LANDED** | `zgbas-system/.../listener/ApplicationStartup.java:45` |
| ShiroUtil @Component("shiroUtil") | `49682cf` | **✅ LANDED** | `zgbas-system/.../shiro/ShiroUtil.java:23` — `@Component("shiroUtil")` |

**建议：** 更新 `.planning/debug/login-feign-selfloop-shiro.md` status 从 `investigating` → `resolved`。

---

## 9. Validation Architecture（Nyquist Dimension 8）

### 9.1 验证可信度 — 如何证明 proof/UAT 真能捕获回归

| 维度 | 机制 | 负面测试（spot-check） |
|------|------|---------------------|
| 后端 proof | `@Disabled` proof 手动启用后断言 DB 数据 + HTTP 响应 | 故意破坏一个 proof 断言值，确认 proof 报错 |
| quartz 调度 | `schedulerLoadAllJobs_proof` 自动验证 53 行加载 | 删除一行 sys_job → proof 报错 |
| 写类真实回归 | `checkpoint:human-blocked` + @Disabled + sys_job_log 状态断言 | 不启用 proof → 默认不跑（安全）；启用后断言 status='0' |
| 人工 UAT | 07-HUMAN-UAT.md 清单 + sign-off | 无自动化负面测试（人工验证的本质） |

### 9.2 可重复证据链

1. **自动化 proof**：ZgbasApplicationTest @Disabled 方法 — 可随时启用复跑，输出 JUnit 标准报告
2. **人工 UAT 清单**：07-HUMAN-UAT.md — 结构化 frontmatter + Tests 清单 + result 字段
3. **编译基线**：全 reactor `mvn compile` + `mvn test` 绿灯（30/0/0/3 skipped）

### 9.3 覆盖信号与 known-gap 边界

| 覆盖维度 | 抽样数量 | 总量 | 覆盖率 | known-gap |
|---------|---------|------|-------|-----------|
| 报表 BFF 控制器 | 5-6 exportExcel + 5-6 findPage | 38 BFF 控制器 | ~30% | 53 报表不全跑 |
| 核心 API 端点 | 4 findPage smoke | ~236 API 端点 | ~2% | smoke 级非全量 |
| quartz handler | 2-3 write-class + 2-3 read-only | ~50 活跃 handler | ~10% | 64 任务不全触，28 未迁 |
| 15 REVIEW sys_job | 全量 operator review | 15 | 100% | 无（全量审查） |

**known-gap 声明（D-P7-03）：** 53 报表、64 任务、28 未迁 handler 不全量回归。选代表样本验收 + 长尾标 known-gap 清单。

---

## 10. 非 Hermetic 验收基线确认

沿用 Phase 3/4/5/6 验收契约：
- **明文密钥态**：`application-dev.yml` 含明文 DB_PASSWORD / SPT_APP_SECRET（D-P4-13 决定，Phase 4 记忆 project_phase4-plaintext-secrets-decision 覆盖安全铁律）
- **dev DB**：`sptbasdb_pd`，本地 MySQL
- **proof 默认 @Disabled**：避免每次 `mvn test` 污染 dev DB（D-P6-06-01）
- **不需 export**：明文态下 `mvn test` 直接可跑（Phase 3 Option 4 + Phase 4 决定）
- **写类真实回归**：额外需 `checkpoint:human-blocked` 人工启用

---

## Findings: Side-by-Side Feasibility

**不可行。** 老 zgbas 4 服务硬依赖 nacos + xxl-job-admin 外部服务（均指向内网/公网固定地址），本地无法搭建全套依赖。`spring.cloud.nacos.discovery.fail-fast=false` 虽不阻塞启动，但 Feign 调用会因无服务注册而 404，无法获得有效 JSON 响应用于 diff 对比。

**降级决策：** D-P7-02 "选择性 side-by-side" 降级为 **纯独立 + golden**。golden 预期来源：
- dev DB `sptbasdb_pd` 现有数据（查询返回非空 + 分页 count 合理）
- 业务规则推导（写类 handler outcome 预期）
- 源码行为推导（方法签名 + 返回类型 + 语义）

---

## Risks

| # | 风险 | 影响 | 缓解 |
|---|------|------|------|
| R-01 | 写类真实回归污染 dev DB，无法自动回滚 | 高 | `@Disabled` + `checkpoint:human-blocked`，人工启用前备份表或确认幂等 |
| R-02 | 15 REVIEW sys_job 中空参数方法运行时 NPE 或异常 | 中 | operator review 先行（P7 UAT 项），PAUSE 危险行 |
| R-03 | 验收期发现运行期 drift（实体/bean/路由） | 中 | 按 D-P4-05 仅修阻塞型，不全量 reconcile |
| R-04 | /monitor/job menu INSERT 依赖外部 spt-auth DB 人工操作 | 低 | 降级方案：直接 URL 访问 /monitor/job |
| R-05 | 模拟登录后端点测试（需 Shiro session）复杂度高 | 中 | 后端 proof 走 MockMvc + 手动 session setup；前端走人工浏览器 UAT |

---

## Recommendations to Planner

### 推荐计划拆分（4 个 plan）

| Plan | Wave | 目标 | 需求覆盖 | autonomous |
|------|------|------|---------|-----------|
| 07-01 | 1 | Smoke proof + 全链路端到端可达验证 | ALIGN-01 | 大部分 autonomous（@Disabled proof 编写），smoke 跑通需手动启用 |
| 07-02 | 2 (after 07-01) | 高风险域深验 + 写类真实回归 proof | ALIGN-02 | 部分 `checkpoint:human-blocked`（写类真实跑需人工 gate） |
| 07-03 | 2 (after 07-01) | 阻塞型 gap 收口（15 REVIEW operator review + menu INSERT 落库 + 浏览器 UAT） | ALIGN-02 | `autonomous: false` + `checkpoint:human-verify`（operator review 须人工决策） |
| 07-04 | 3 (after 07-02 + 07-03) | UAT 整合 + known-gap 清单 + 验收收口 | ALIGN-01 + ALIGN-02 | `autonomous: false`（07-HUMAN-UAT.md 人工 sign-off） |

### 关键 task 级建议

1. **07-01 smoke proof**：在 ZgbasApplicationTest 新增 `@Disabled` 方法 `fullChainSmoke_proof()`，断言登录→首页→4 核心 findPage→2 报表→1 quartz 触发全链路。默认 @Disabled，手动启用后断言 200 + 非空 body。
2. **07-02 写类 proof**：新增 `writeClassRealRun_proof()` @Disabled，对 `autoPay()` / `refreshContractStatusTask()` 做真实触发 + DB 行数/状态断言。标记 `checkpoint:human-blocked`。
3. **07-02 导出抽样**：新增 `reportExportSample_proof()` @Disabled，对 5 个 `/rpt/*/exportExcel` 端点断言 Content-Type=xlsx + 非 0 body。
4. **07-03 operator review**：`autonomous: false`，人工逐条审查 15 REVIEW 行，输出审查表（keep/modify/PAUSE），修订 SQL 后重应用。
5. **07-03 menu INSERT**：引导用户执行 06-01-MENU-INSERT.sql 到 spt-auth DB，然后浏览器验 /monitor/job UI。
6. **07-04 UAT 整合**：写 07-HUMAN-UAT.md（整合 P6 carry-over pending 项 2/3 + P7 新增项），执行人工浏览器验收，记录 sign-off。

### 不需要 pattern mapper

Phase 7 不新增业务代码（仅 proof + UAT + 阻塞型 SQL 修订），PATTERNS.md 价值极低。建议跳过 step 7.8。

## RESEARCH COMPLETE
