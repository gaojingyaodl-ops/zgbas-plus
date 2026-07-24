# Phase 6: Service 层迁入 — Technical Research

**Researched:** 2026-07-24 (inline — GSD subagent gateway down, see STATE/memory)
**Question answered:** "What do I need to know to PLAN Phase 6 well?"
**Verdict:** Phase 6 is a **low-risk verbatim port** against a proven inlined base. One hard scrub (PurchaseCommand @XxlJob) and one per-class adaptation pattern (reused main-domain Dao import re-point for 4 services). No dependency-resolution gaps found — every I*Client (main-domain + spt-sign-client) and every Dao resolves in the monolith.

---

## 1. Source Inventory (实测 `/Users/alan/WorkSpace/IDEA/zgbas/basWx/purchase-server/.../server/`)

- `service/` — **19 interfaces** (`I*Service.java`); `service/impl/` — **20 impls** (extra = `JinXinApi`,无接口)
- `command/PurchaseCommand.java` — `implements ICommand` + `@XxlJob` (D-P6-02 scrub 目标)
- `ewechat/EweChatApi.java` — `@Autowired IBuyMessageService`(P5 延入,本阶段编译闭环)

**Already migrated (P3/P5, DO NOT re-port):** `IWxAccessTokenService` + `impl/WxAccessTokenService`。实际本阶段迁 **19 impl + 18 interface**(JinXinApi 无接口,WxAccessToken 已在)。

### Class declarations(三类形态)

| 形态 | 数量 | 类 |
|---|---|---|
| `extends BaseService<T> implements IXxxService` | 14→**13 待迁**(WxAccessToken 已在) | BsCompany, BsDict, BuyEnquiry, BuyMessage, CompanyIndustry, Feedback, TempSaveInfo, UserDetail, **UserService**, WxSession, WxSmsCheckCode, WxUserInfo, WxUserTextRead |
| plain `implements IXxxService`(无实体无 base) | 5 | Apply, BuyQuote, Contract, SuccessContract, UserInfo |
| `extends CommonUtil`(无接口,HTTP wrapper) | 1 | JinXinApi |

### Interface extends-clauses(signature 关键)

- **12 of 13 entity interfaces** `extends IBaseService<T>`(IBsCompany/IBsDict/ICompanyIndustry/IFeedback/ITempSaveInfo/IUserDetail/IUserService/IWxSession/IWxSmsCheckCode/IWxUserInfo/IWxUserTextRead + IWxAccessToken[已迁])
- **`IBuyEnquiryService` / `IBuyMessageService` 为 plain interface(不 extends IBaseService)** —— 其 impl 仍 `extends BaseService<T>`,base 方法由 BaseService 提供、接口自带方法由 impl 实现。编译合法,无冲突。

---

## 2. Dependency Graph(拓扑序输入)

### 2a. WX inter-service dependencies(`I*Service` 本阶段内部)

| Service | 依赖 WX service |
|---|---|
| BuyEnquiryServiceImpl | IBuyMessageService |
| BuyQuoteServiceImpl | IBuyMessageService |
| ContractServiceImpl | IApplyService |
| SuccessContractServiceImpl | IContractService |
| UserInfoService | IApplyService, IContractService, ISuccessContractService, ITempSaveInfoService |
| UserService | IUserInfoService |
| EweChatApi | IBuyMessageService |
| PurchaseCommand | ISuccessContractService, IUserInfoService |

**Leaves(无 WX-svc 依赖,拓扑 Level 0):** BsCompany, BsDict, CompanyIndustry, Feedback, TempSaveInfo, UserDetail, BuyMessage, Apply, JinXinApi, WxSession, WxSmsCheckCode, WxUserInfo, WxUserTextRead。

### 2b. Feign 自回环 + 外部 client(方案1 全部保留不崩为直注)

WX service 经 `PurchaseWxClientConfig` 自回环 localhost:8080 调主域。引用的 `I*Client` 接口 **全部可在单体解析**(实测):

- **主域 `com.spt.bas.client.remote.*`(28 个,单体已有):** IBsCompanyClient, IPmApproveClient, IPmProcessClient, ICtrContractClient, ICtrContractRelaClient, ICtrProductClient, ICtrServiceContractClient, IApplyConfirmReceiptClient, IApplyDeliveryOutClient, IApplyFactorSignClient, ICtrContractOphisClient, IPmApproveContentsClient, ISignFileApiClient, IApiRequestHisClient, IApplyChargeSalesClient, IApplyMatchClient, IBsCompanyAccountClient, IBsCompanyDcsxClient, IBsCompanyOurClient, IBsContractTemplateClient, IBsEntrustClient, IBsKeySequenceClient, IBsSupplyInfoClient, IBsWarehouseAddrClient, IBsWarehouseClient, IMidstreamClient, ISealUsageDCSXClient, ISyncDataClient。
- **电签 `com.spt.sign.client.remote.*`(3 个,spt-sign-client 1.0.0-SNAPSHOT jar 已在 zgbas-system pom + 本地仓库):** ISignContractClient, ISignInfoClient, ICfcaSignClient。**主域已大量使用**(BizSignServiceImpl/ApplyDeliveryOutServiceImpl/SignFileServiceImpl 等),证明 jar 在 classpath、可解析。**无 gap。**

> **依赖解析结论:零 gap。** WX service 的所有 `I*Client` 依赖(主域 28 + 电签 3)均可在单体解析。方案1 保留 Feign 自回环 —— 这些 client 注入由 `PurchaseWxClientConfig`(P3 已迁)提供 Feign proxy,运行期走 localhost:8080 同进程直调(runtime proof 留 Phase 8)。

---

## 3. BaseService / IBaseService 签名适配分析(D-P6-01)

### 结论:签名风险**低**,verbatim port 对内联 base 兼容

- 内联 `BaseService<T extends IdEntity> implements IBaseService<T>`,`IBaseService<T> extends IDataService<T>`(delete/save/findPage/getEntity/findAll/sumPage/saveBatch…)。`getBaseDao()` 为唯一 abstract 方法,各 WX impl override 返回自己的 Dao。
- **主域同款 `extends BaseService` 已大规模在用**(`PmProcessServiceImpl`/`PmApproveContentsServiceImpl` 等)→ 兼容性已证。
- 12 个 entity interface `extends IBaseService<T>` 签名与内联 base 一致(同 IBaseService/IDataService);2 个 plain interface(BuyEnquiry/BuyMessage)自带方法由 impl 实现,不与 base 冲突。
- **无需新建 WxBaseService 子类,无需扩共享 base**(未达 D-P6-01 阈值"多类共享同一缺失方法")。

### 唯一实测适配点:Dao 引用落点(D-P6-01 的 Dao 版本)

13 entity service 的 `getBaseDao()` 返回泛型 `BaseDao<T>`,具体 Dao 由 `@Autowired` 字段注入。分两类:

**A. WX 包内 Dao 已就位(verbatim import 直接解析)— 9 service:**
TempSaveInfo(SaveInfoDao + 复用 UserDetailDao)、UserDetail(UserDetailDao)、BuyMessage(BuyMessageDao)、WxSession、WxSmsCheckCode、WxUserInfo、WxUserTextRead、BuyEnquiry(BuyEnquiryDao + BuyQuoteDao)、**UserService(仅 import BaseDao,无 WX-specific Dao)**。

**B. 复用主域 Dao(源 import WX 路径,实际在 `com.spt.bas.server.dao` → 需 import re-point)— 4 service:**

| Service | 源 import(WX 路径) | 单体实际位置 | 适配动作 |
|---|---|---|---|
| BsCompanyService | `…wx.server.dao.BsCompanyDao` | `com.spt.bas.server.dao.BsCompanyDao` | 改 import 包路径 |
| BsDictService | `…wx.server.dao.BsDictDataDao` + `…BsDictTypeDao` | `com.spt.bas.server.dao.{BsDictDataDao,BsDictTypeDao}` | 改 2 行 import 包路径 |
| CompanyIndustryService | `…wx.server.dao.CompanyIndustryDao` | `com.spt.bas.server.dao.BsCompanyIndustryDao` | 改 import **包路径 + 类名** |
| FeedbackService | `…wx.server.dao.FeedbackDao` | `com.spt.bas.server.dao.FeedbackDao` | 改 import 包路径 |

> D-P6-01 落点确认:逐类改 **WX service 适配当前 Dao 落点**(import 行,CompanyIndustry 含字段类型名 `CompanyIndustryDao`→`BsCompanyIndustryDao` 同步改),不动主域 Dao。blast radius = 这 4 类的 import + CompanyIndustry 字段类型名。SC#4 编译门权威兜底。

---

## 4. PurchaseCommand @XxlJob scrub(D-P6-02 — 硬地雷,精确删行)

源文件 59 行,xxl-job 残留精确位置(`command/PurchaseCommand.java`):

| 行 | 内容 | 动作 |
|---|---|---|
| 8 | `import com.xxl.job.core.context.XxlJobHelper;` | **删** |
| 9 | `import com.xxl.job.core.handler.annotation.XxlJob;` | **删** |
| 21 | `@XxlJob(value = "executeCommand")` | **删** |
| 23-26 | `String jobParam = XxlJobHelper.getJobParam(); if(StringUtils.isNotBlank(jobParam)){ commandline = jobParam; }` | **删**(引用已删的 XxlJobHelper) |

**保留:** `import com.spt.tools.core.cmd.ICommand`、`implements ICommand`、`@Component`、`executeCommand(String commandline)` 方法体(axq/successContract/doSuccessDebtCertificate/cache/doReceiveGood/userInfoService 分支)、`ContractNoRequest`、`ISuccessContractService`、`IUserInfoService`、`LocalCacheManager`、`StringUtils`。

- `ICommand` + `CommandExecutor` 在 `zgbas-common/.../core/cmd/` 已有 → ICommand 队列路径编译闭环。
- **定时触发能力(scheduled trigger)延后 v1.3 quartz gap-closure**(与 SignContractTask 同批,STATE 已列)。本阶段只 scrub 注解保 ICommand。

---

## 5. Compilation Wave Structure(拓扑 → 6 plan / 5 wave)

依赖图导出的拓扑层:

- **Wave 1(Level 0,leaf):** 13 service — BsCompany, BsDict, CompanyIndustry, Feedback, TempSaveInfo, UserDetail, BuyMessage, Apply, JinXinApi, WxSession, WxSmsCheckCode, WxUserInfo, WxUserTextRead
- **Wave 2(Level 1):** BuyEnquiry→BuyMessage, BuyQuote→BuyMessage, Contract→Apply, EweChatApi→BuyMessage
- **Wave 3(Level 2):** SuccessContract→Contract
- **Wave 4(Level 3):** UserInfo→Apply/Contract/SuccessContract/TempSave
- **Wave 5(Level 4):** UserService→UserInfo, PurchaseCommand→SuccessContract+UserInfo

**Plan 分组(平衡 size + 内聚 + 跨 plan 依赖用 wave 隔离):**

| Plan | Wave | Service | iface | depends_on | 关键点 |
|---|---|---|---|---|---|
| 06-01 | 1 | 11 entity CRUD leaf | 11 | — | 含 4 Dao re-point(BsCompany/BsDict/CompanyIndustry/Feedback);WxAccessToken 不重迁 |
| 06-02 | 1 | Apply + JinXinApi | 1 (IApplyService) | — | 逻辑型 leaf + HTTP wrapper;喂给 W2/W4 |
| 06-03 | 2 | BuyEnquiry + BuyQuote + Contract + EweChatApi | 3 | 06-01,06-02 | BuyMessage/Apply 消费者;EweChatApi P5 延入闭环 |
| 06-04 | 3 | SuccessContract + UserInfo | 2 | 06-03 | 同 plan 内 SuccessContract→UserInfo 顺序;UserInfo 25+ client 依赖 |
| 06-05 | 4 | UserService + PurchaseCommand | 1 (IUserService) | 06-04 | PurchaseCommand @XxlJob scrub(D-P6-02) |
| 06-06 | 5 | (编译门) | — | 06-01..06-05 | SC#4 `mvn compile -pl zgbas-system` 零 [ERROR] |

> 内联单 dev 执行下 wave 并行收益有限,但结构保持正确(供 executor 恢复后用)。06-04 把 SuccessContract+UserInfo 合一 plan(plan 内 task 顺序依赖),wave=3 起步(Contract 在 06-03/W2 落地后)。

---

## 6. Validation Architecture(Nyquist / SC#4)

D-P6-04 锁定:**本阶段验收 = 仅编译门**。

**验证维度:**
1. **编译(Dimension: build)** — `[BLOCKING]` `JAVA_HOME=Corretto-1.8 mvn compile -pl zgbas-system` 零 `^\[ERROR\]`(locale 无关 grep,见 memory gotcha)。覆盖 06-06。
2. **源码断言** — `grep -r "com.xxl.job" zgbas-system/.../purchase/wx/` 零命中(PurchaseCommand scrub 干净);`grep -r "spt-tools 残余 import"` 零命中(SC#2)。
3. **结构断言** — 19 impl + 18 iface 落 `com.spt.bas.purchase.wx.server.service{,.impl}`(保包飞地 D-06);`find …/service/impl/*.java | wc -l` ≥ 19(WxAccessToken 已在故 ≥19 即 20 全量)。

**显式不在本阶段验证(runtime/bean 全留 Phase 8):** 启动 bean 装配、`/wx/*` 非 404、WX Feign 自回环 proof、ServiceAop 命中。P6 无 controller,bean 装配验证信号有限(D-P6-04)。

**无新增单测** —— verbatim 行为等价迁移(D-11),不引入新逻辑,无新单测维度。P5 同款(05 仅编译门)。

---

## 7. Risks & Open Questions(交付 planner / executor)

| # | 风险 | 级别 | 缓解 |
|---|---|---|---|
| R1 | CompanyIndustryService Dao **类名+包路径双改**(`CompanyIndustryDao`→`BsCompanyIndustryDao`) | 🟡 低 | 06-01 task 显式标注;read_first 含两个 Dao 文件;SC#4 兜底 |
| R2 | JinXinApi CFCA/金信运行期依赖(P5 延入理由) | 🟢 编译无虞 | 编译只验 IApiRequestHisClient(已解析);runtime 留 P8 |
| R3 | UserService Dao 用法(import 仅 BaseDao,字段提取噪声) | 🟢 低 | executor 读源文件确认;WxAccessToken 同款 simple CRUD |
| R4 | IBuyEnquiryService/IBuyMessageService plain interface 与 BaseService 共存 | 🟢 无 | 编译合法(base 方法由 BaseService 提供);D-P6-01 已分析 |
| R5 | 4 service Dao import re-point 漏改 | 🟡 低 | 06-01 acceptance_criteria 列 4 类具体 import 目标;SC#4 权威 |
| R6 | subagent outage → 全程内联执行 | 🟡 流程 | planner/checker/executor 内联;见 STATE memory |

---

## 8. RESEARCH COMPLETE

Phase 6 = 低风险 verbatim port + 1 硬 scrub(PurchaseCommand)+ 1 适配模式(4 service Dao import re-point)。所有依赖解析无 gap。6 plan / 5 wave / 仅编译门验收,与 P5 同款。可进入 planning。
