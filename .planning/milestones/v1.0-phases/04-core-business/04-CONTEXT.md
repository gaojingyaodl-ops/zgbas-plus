# Phase 4: 核心业务迁移 - Context

**Gathered:** 2026-07-17
**Status:** Ready for planning

<domain>
## Phase Boundary

把源 `basServer` 核心供应链业务（合同/授信/库存/放款等）的 **Service + REST 契约实现** 迁入 `zgbas-system`，`web` 的 **BFF controller** 迁入 `zgbas-admin`，业务间原 Feign 调用改为**同进程 bean 直调**（interface-as-contract），使核心业务在单进程内端到端可用。本期交付三件事（BIZ-01/02/03）：

1. **Service 层 + REST 契约实现迁入 `zgbas-system`**（BIZ-01）：源 `basCore/basServer` 的 `service`（241 接口 + 248 impl + 域子包 44）+ `api`（224 个 `@RestController extends BaseApi`，**非 implements I*Client**，经研究修正）+ 业务依赖的 infra（cache/util/enums/annotation/filter/listener/command/event + **rocketmq 22**，筛选照搬）迁入单体。实体（239）+ Dao（229）+ vo/constant/cache + basClient remote 契约**仅 4 个**（`IBsDictClient`/`IBsCompanyOurClient`/`IApproveWaitDealClient`/`IPmProcessClient`）在 Phase 2/3 落位——**剩余 ~234 remote 契约 + dto/util/common/riskScore/config 是本期 Wave 1 实际工作**（经研究修正，非"Phase 2 已全内联 238"）。
2. **BFF controller 迁入 `zgbas-admin`**（BIZ-02）：源 `web` 的 267 个 BFF controller 照搬（保包名 `com.spt.bas.web.*`），其对 `I*Client` 的 `@Autowired` 改解析为同进程本地 `@RestController` bean。
3. **业务间 Feign → 同进程自回环**（BIZ-03）：⚠ 经研究修正（见 D-P4-01）——放宽 Feign 扫 `com.spt.bas.client.remote` + `spt.bas.server.url=http://localhost:8080`，224 个 `extends BaseApi` api 照搬零修改（不加 implements），`@Autowired I*Client` 解析为 Feign proxy 自回环到本进程 api 端点（同进程 HTTP 跳，无跨进程跳；行为等价）。

**不在本期（明确边界）：**
- **xxl-job `task` handler（basServer/task 23 个）** → **Phase 6**（quartz）。本期 infra 筛选照搬时**排除 task 包**。
- **53 套报表 Mapper + XML + report 契约** → **Phase 5**（REPORT-01/02）。无本地 `@RestController` 实现的 report 契约按 D-P4-02 stub 降级。
- **basWx 采购业务** → **v2**（用户决策 #14）。purchase 契约按 D-P4-02 stub 降级。
- **真实业务 CRUD 行为对照 / 真实浏览器 e2e** → **Phase 7**（ALIGN-01/02）。
- **全量实体 schema drift reconcile + 重开 ddl-auto=validate（D-P2-02 原意）** → tech debt 再延（见 D-P4-05）。
- **补 createBy/updateBy/逻辑删除等审计字段（259 表加列）** → 永久 Out-of-Scope（PROJECT.md）。
- **真实轮换已泄漏生产库密码（CR-01）** → 跨阶段安全债，仍 deferred。

</domain>

<decisions>
## Implementation Decisions

### 调用机制 Feign → 同进程自回环 (BIZ-03 核心) — ⚠ D-P4-01 经研究证伪修正
- **D-P4-01: Feign 自回环（方案 A，2026-07-17 用户确认修正原决策）。** 原「`@RestController implements I*Client` 原样照搬 + 解析为本地 bean + 无网络跳」的前提**已被 04-RESEARCH.md §D-P4-01 Critical Finding 证伪**：源 `basServer/api` 224 个 `@RestController` 实测 **0/224 `implements I*Client`**（全部 `extends BaseApi<Entity>` 独立控制器），且 `BaseApi.findPage` 返回 `Page<T>` vs `BaseClient.findPage` 返回 `PageDown<T>`（`PageDown extends PageImpl implements Page`）协变返回方向相反——加 `implements` 后每文件仍需手写 `findPage` 桥接，机械不可行。**修正决策（方案 A）**：放宽 `@EnableFeignClients(basePackages={"com.spt.sign.client.remote","com.spt.bas.client.remote"})` + 配 `spt.bas.server.url=http://localhost:8080`；224 个 `extends BaseApi` api **照搬零修改**（不加 implements）；238 契约的 `@Autowired I*Client` 解析为 **Feign proxy 自回环到本进程 8080 的 api 端点**。行为等价（源即 Feign-over-HTTP，localhost 等价），照搬保真最高。**承认偏离**：D-P4-01 原「无网络跳」措辞放弃——存在同进程 HTTP 跳（无跨进程跳），换取 224 处零修改，与项目「行为等价优先 + 照搬」核心价值一致。弃方案 B（224 手写适配，违反照搬）/ C（FactoryBean，侵入式新代码）。
- **D-P4-01a: path 前缀处理（方案 A 子决策，留 planning 落地）。** 源 basServer `server.servlet.context-path=/spt-bas-server`，238 契约 `path=BasConstants.SERVER_NAME+"/..."` 含 `spt-bas-server` 前缀；单体 D-P2-16 根 `/`，api 照搬后暴露在 `/apply/brand/*`。**优先 Feign path 覆盖**（yml `feign.client.config` 或契约级 `path=` 覆盖去掉前缀，非侵入）——**不设单体 context-path**（会破坏 Phase 3 已验的 Shiro `/login`/`/index` 根路径，AUTH-03 回归风险）。若 Feign path 覆盖不可行（RESEARCH 假设 A3），planner 作 `checkpoint:human-verify` 上报，不擅自改 238 契约。
- **D-P4-02: 无本地实现的契约 stub 降级（Claude discretion，对齐 Phase 3 D-P3-10）。** 238 契约中**无本地 `@RestController` 实现的**（约 14 个，疑为 report→P5 / purchase→v2 / auth 已外部 HTTP）用 `@Autowired(required=false)` + null 守卫 stub，业务降级裸 404，后续 phase 接通。**具体清单留 research/planning 枚举确认**（对照 basClient/remote 238 契约 vs basServer/api 224 实现的差集 + report/purchase/auth 来源）。

### 迁移切片与测序
- **D-P4-03: 层次优先 wave 切片。** 按 compile 依赖分层切 wave，每 wave 后 `mvn compile` 全模块零 `[ERROR]` 再继续（逐层绿灯，Phase 1 gotcha 级联教训 + Phase 2 wave+compile-gate 经验）：
  - **Wave 1** — 补齐 `basClient` 剩余数据载体（dto/util/common/riskScore/constant 等 ~570，service/api/controller 编译依赖的契约数据）。
  - **Wave 2** — `basServer` service（241）+ 筛选后的 infra（util/config/cache/enums/annotation/filter/listener）。
  - **Wave 3** — `basServer/api`（224 个 `@RestController implements I*Client`）。
  - **Wave 4** — `web` BFF controller（267），落 `zgbas-admin`。
  - 具体 wave 文件细分、wave 内依赖排序、是否再拆 sub-wave 留 planning 决定。
- **D-P4-04: infra 筛选照搬（不全量）。** `basServer` infra 包按需筛选：
  - ✅ 照搬：service 依赖的 `cache`（7）/`util`（34）/`enums`（2）/`annotation`（1）/`filter`（3）/`listener`（1）/`command`（2）/`event`（1）。
  - ⚠️ 选择性：`config`（8）去重 Phase 2 已接的 `@Primary` DataSource / `JpaTransactionManager` / 双 ORM 接线，仅保业务必需的 `@Configuration`，避免重复 bean 冲突。
  - ❌ 延后 Phase 6：`task`（23 个 xxl-job handler）。
  - 🔍 评估：`rocketmq`（2）默认延后，除非 service 编译/运行依赖（留 research 确认业务是否用 MQ）。

### schema drift 处理
- **D-P4-05: 保持 ddl-auto=none + 仅修运行阻塞型。** **不主动全量 reconcile** 239 实体 drift（如 `api_param` 实体 `varchar(255)` vs DB `mediumtext`）。保持 Phase 2 的 `ddl-auto=none`（匹配源项目隐式行为 = 行为等价）。业务迁移中**遇到实体运行期映射错误**（查不存在的列、Hibernate 无法映射的类型等）才修，只修阻塞性。**全量 reconcile + 重开 `ddl-auto=validate`（D-P2-02 原意）再延为 tech debt**，`phase4-resolve-entity-schema-drift` todo 降级保留 open 作长期债。⚠️ 与 PROJECT Out-of-Scope「补 createBy/updateBy/审计字段（259 表加列）」是**不同维度**——本期既不补字段也不全量 reconcile annotation，仅修运行阻塞。

### 验收策略
- **D-P4-06: 启动验证为主 + WR-02 契约 HTTP proof。** 同 Phase 2/3 基线（D-P2-03 / D-P3-13）：全 Spring context 启动 + 业务 `@RestController` bean 全解析（无 `NoSuchBeanDefinitionException`，即证 interface-as-contract 接线成功）+ **WR-02 MockMvc HTTP proof 扩到几个真实 `I*Client` 端点 GET 200**（吸收 `phase4-inprocess-contract-http-proof` todo：断言 Spring MVC 经接口级 `@GetMapping` over HTTP 返回 200，而非仅 Java 调用）。非 hermetic 同 D-P3-13（需本地 export `DB_PASSWORD`/`SPT_APP_SECRET`，Option 4 接受）。**真实业务 CRUD 行为对照留 Phase 7（ALIGN-02 回归）。**

### Claude's Discretion
- **Flyway 处理**：`basServer` 仅有 1 个 `db/migration/V20211129_1__Drop_tmp_product.sql`（drop 临时表，无业务价值）。本期移除 Flyway 依赖，纯 `ddl-auto=none` 依赖现有 `sptbasdb_pd` schema（与 D-P4-05 一致）。
- **`basClient` 落位**：dto/vo/constant 等数据载体随 Wave 1 落 `zgbas-system` 的 `com.spt.bas.client.*`（保包名 D-P2-07），与已迁的 entity/remote 同包。
- **运行阻塞 drift 的发现机制**：验收阶段 MockMvc HTTP proof + context 启动会自然暴露运行期映射错误，即修（D-P4-05 + D-P4-06 联动）。
- **事务边界**：沿用 Phase 2 `JpaTransactionManager` @Primary，业务 `@Transactional` 无需额外设计（同进程单 DataSource）。
- **无验证码 / 登录失败等 web 行为**：随 controller 照搬（Phase 3 已确认 web 无 captcha）。

### Folded Todos
- **`phase4-inprocess-contract-http-proof.md`（WR-02）→ 吸收进 D-P4-06（resolved）。** 原问题：`InProcessContractTest` 仅 Java 调用，未断言 Spring MVC 经接口级 `@GetMapping` over HTTP。Phase 4 有 224 个真实 `@RestController implements I*Client`，验收扩 MockMvc `GET I*Client 端点 → 200` 即满足。todo 可在 planning 标记 resolved。
- **`phase4-resolve-entity-schema-drift.md`（D-P2-02 偏离）→ 降级保留 open（D-P4-05）。** 原期望本期全量 reconcile + 重开 validate；决策为保持 none + 仅修运行阻塞，全量 reconcile 延为 tech debt。todo 保留 open 作长期债，非本期 resolved。

</decisions>

<canonical_refs>
## Canonical References

**Downstream agents MUST read these before planning or implementing.**

### 项目规划（仓库内，必读）
- `.planning/ROADMAP.md` §Phase 4 — 阶段目标 / 依赖（Phase 2 + Phase 3）/ 需求映射（BIZ-01/02/03）/ 3 条成功标准
- `.planning/REQUIREMENTS.md` — BIZ-01（核心业务 Service 迁入 system）/ BIZ-02（BFF controller 迁入 admin）/ BIZ-03（Feign→同进程直调 行为等价）+ PERSIST-01（JPA 增删改查主力，基础设施支撑）
- `.planning/PROJECT.md` — 源架构（BasServer=8001 核心业务 JPA+Hibernate+Flyway / Web=80 BFF+UI 不连 DB）、持久层体量（259 实体 + 254 Dao + 53 mybatis 报表）、外部 Bean 注入、Out-of-Scope（补审计字段 259 表加列 / 报表物理分页 / basWx）
- `.planning/phases/01-compile-skeleton/01-CONTEXT.md` — **D-08 模块拓扑**（system←common,framework / admin←all）+ 逐层 compile 绿灯教训
- `.planning/phases/02-infrastructure/02-CONTEXT.md` — **D-P2-06**（framework vs admin 边界）/ **D-P2-07**（照搬保包名）/ **D-P2-10**（interface-as-contract + InProcessContract proof）/ **D-P2-02**（ddl-auto=none 偏离）/ @Primary 双 ORM 接线 / Feign 收窄 cfca
- `.planning/phases/03-auth-homepage/03-CONTEXT.md` — **D-P3-10**（stub-port + `@Autowired(required=false)` + null 守卫，业务降级裸 404）/ **D-P3-13**（启动验证为主 + 非 hermetic，Option 4 本地 export 密钥）
- `CLAUDE.md` — 技术栈约束（JDK 1.8 / SpringBoot 2.5.9 / Maven 3.8.6 + zg_settings.xml）、固定 5 模块、外部 spt-auth 保持外部（#7）、双 ORM（#6）

### 源项目（搬运参考，**绝对路径，非本仓库内**）
- `/Users/alan/WorkSpace/IDEA/zgbas`（分支 `feat-系统重构v5.0`）— 源微服务
  - `basCore/basServer/src/main/java/com/spt/bas/server/service/`（241 Service）→ Wave 2 迁入 `zgbas-system`
  - `basCore/basServer/src/main/java/com/spt/bas/server/api/`（224 `@RestController implements I*Client`，如 `ApplyBrandApi`↔`IApplyBrandClient`）→ Wave 3 迁入 `zgbas-system`
  - `basCore/basServer/src/main/java/com/spt/bas/server/{cache,util,enums,annotation,filter,listener,command,event}/` → Wave 2 筛选照搬
  - `basCore/basServer/src/main/java/com/spt/bas/server/config/`（8 `@Configuration`）→ Wave 2 **选择性去重**
  - `basCore/basServer/src/main/java/com/spt/bas/server/task/`（23 xxl-job handler）→ **不迁（Phase 6）**
  - `basCore/basServer/src/main/resources/db/migration/V20211129_1__Drop_tmp_product.sql`（唯一 Flyway 迁移，移除）
  - `basCore/basClient/src/main/java/com/spt/bas/client/{dto,vo,util,common,constant,riskScore}/`（~570 数据载体）→ Wave 1 迁入 `zgbas-system`
  - `basCore/basClient/src/main/java/com/spt/bas/client/remote/`（238 `@FeignClient` 契约 `I*Client`）→ **Phase 2/3 仅内联 4 个**（IBsDictClient/IBsCompanyOurClient/IApproveWaitDealClient/IPmProcessClient），**剩余 ~234 是本期 Wave 1 内联目标**（经研究修正），作 Feign 自回环契约源（D-P4-01 方案 A）
  - `basCore/basClient/src/main/java/com/spt/bas/client/entity/`（239 实体）→ **Phase 2 已内联**
  - `web/src/main/java/com/spt/bas/web/controller/`（267 BFF controller）→ Wave 4 迁入 `zgbas-admin`

### 当前单体（已就位的 Phase 2/3 资产）
- `zgbas-system/src/main/java/com/spt/bas/server/dao/`（229 Dao）+ `com/spt/bas/client/{entity,remote,vo,cache,constant}/` — Phase 2 已落位
- `zgbas-system/src/main/java/com/spt/bas/web/{shiro,config}/` — Phase 3 Shiro 链路
- `zgbas-admin/src/main/java/com/spt/ZgbasApplication.java` — 单一启动类（已 un-exclude ToolsShiroConfig）
- `zgbas-admin/src/test/.../InProcessContractTest`（Phase 2 D-P2-10 proof）— 本期 WR-02 扩展对象
- `application.yml` + `application-{dev,prod}.yml` — Phase 2 配置基线（密钥环境变量外置）

### 构建 / 工具链（绝对路径）
- `/Users/alan/App/apache-maven-3.8.6` — Maven 可执行
- `/Users/alan/App/apache-maven-3.8.6/zg_settings.xml` — 私服仓库重定向 settings（构建必用）
- `/Users/alan/App/Repository` — 本地仓库（私服 SNAPSHOT jar）

### 待办（已折叠 / 已 review）
- `.planning/todos/pending/phase4-inprocess-contract-http-proof.md` — WR-02，吸收进 D-P4-06
- `.planning/todos/pending/phase4-resolve-entity-schema-drift.md` — D-P2-02，降级保留 open（D-P4-05）

</canonical_refs>

<code_context>
## Existing Code Insights

> zgbas-plus 当前为 **Phase 3 完成态**：骨架 + spt-tools 全量内联 + 双 ORM 单 DataSource + 外部 SDK bean + nacos 删除 + Shiro 登录链路激活 + 全量前端模板/静态资源照搬。zgbas-system 已含 entity(239)/dao(229)/remote 契约(238)/vo 等（Phase 2），Phase 4 补 Service + REST 实现 + BFF controller。

### Reusable Assets
- **`basClient/remote` 契约 Phase 2/3 仅内联 4 个**（`zgbas-system/com.spt.bas.client.remote.*`）——经研究修正，**剩余 ~234 是本期 Wave 1 内联目标**；作 Feign 自回环（D-P4-01 方案 A）的契约侧，被 `@Autowired` 复用解析为 Feign proxy 自回环到本进程 api 端点。
- **Phase 2 `@Primary` Druid DataSource + `JpaTransactionManager`** —— 业务 `@Transactional` 直接生效，无需事务设计。
- **Phase 2 `InProcessContractTest`**（`zgbas-admin`）—— WR-02 扩展对象，本期加 MockMvc HTTP 断言。
- **Phase 3 stub-port 模式**（`@Autowired(required=false)` + null 守卫）—— D-P4-02 无实现契约降级复用此模式。
- **`IAuthOpenFacade` HTTP bean**（Phase 2 EXT-01）—— auth 相关契约（无本地实现）仍走外部 spt-auth，不 stub。

### Established Patterns
- **照搬保包名**（D-P2-07）：`com.spt.bas.server.*` / `com.spt.bas.client.*` / `com.spt.bas.web.*` verbatim，最小化 import 改动。
- **framework vs admin 边界**（D-P2-06 + D-08）：业务 Service + REST 实现 → `zgbas-system`；web BFF controller + 模板 → `zgbas-admin`。
- **逐层 compile 绿灯**（Phase 1 gotcha 级联教训）：每 wave 后 `mvn compile` 全模块零 `[ERROR]`（JDK 1.8.0_482）再继续；grep `^\[ERROR\]` locale 无关。
- **密钥环境变量外置**（D-P2-13）：新增业务配置走 profile + `${ENV:}` 占位。

### Integration Points
- `zgbas-system` ← Wave1 `basClient` 数据载体 / Wave2 service + infra / Wave3 `api` @RestController。
- `zgbas-admin` ← Wave4 web BFF controller（`@Autowired I*Client` 解析为 system 的本地 @RestController bean）。
- `ZgbasApplication` ← 扫描 `com.spt` 全包，业务 bean 自动接线（无需额外 @ComponentScan）。
- `application.yml` ← 业务新增配置项（若 service 依赖新外部 URL/参数）。

</code_context>

<specifics>
## Specific Ideas

- 用户全程选「行为等价优先 + 最小改动 + 照搬」：interface-as-contract 沿用、infra 筛选不全量、schema drift 保持 none、验收与 P2/P3 基线一致 —— 与项目核心价值「搬运而非重造 + 行为对齐旧系统」完全一致。
- 本期是**最大体量阶段**（~1900 文件），但用户认可「层次优先 wave + 逐层 compile 绿灯」可控执行，不追求一步到位的域端到端 proof（真实 e2e 留 P7）。
- 两个 Phase 4 todo（用户在 P2 收尾时自建）的处理方向明确：WR-02 吸收进验收；schema drift 全量 reconcile 不在本期，保持 none 仅修运行阻塞。

</specifics>

<deferred>
## Deferred Ideas

- **xxl-job `task` handler（23 个）** → Phase 6（QUARTZ-03，basServer/task 包本期排除）
- **53 套报表 + report 契约** → Phase 5（REPORT-01/02）
- **basWx 采购业务 + purchase 契约** → v2（WX-01/02）
- **真实业务 CRUD 行为对照 / 浏览器 e2e** → Phase 7（ALIGN-01/02）
- **全量实体 schema drift reconcile + 重开 ddl-auto=validate（D-P2-02 原意）** → tech debt（todo `phase4-resolve-entity-schema-drift` 保留 open）
- **CR-01 真实轮换已泄漏生产库密码** → 跨阶段安全债（outward-facing），仍 deferred

### Reviewed Todos (not folded)
- `rotate-leaked-prod-credentials.md`（CR-01 轮换已泄漏生产库密码）→ 跨阶段安全债，弱匹配（score 0.6，通用关键词），非 Phase 4 范围，review 不折叠。

</deferred>

---

*Phase: 4-核心业务迁移*
*Context gathered: 2026-07-17*
