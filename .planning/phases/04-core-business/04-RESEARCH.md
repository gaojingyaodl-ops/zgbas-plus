# Phase 4: 核心业务迁移 - Research

**Researched:** 2026-07-17
**Domain:** zgbas 微服务（basServer JPA 业务 + web BFF）→ zgbas-plus 单体迁移；interface-as-contract 同进程调用机制
**Confidence:** HIGH（基于源码 verbatim 核查，非推断）— 但触发 1 项 **架构级反向发现**（见 §D-P4-01 Critical Finding），需 planner/用户重审

## Summary

Phase 4 是 zgbas-plus 项目体量最大的阶段。CONTEXT.md 的 D-P4-01..06 决策骨架经源码核查**总体成立**，但其中 **D-P4-01 "interface-as-contract 原样照搬" 的核心前提与源码事实不符**，是本期 planner 必须首先处理的反向发现。同时 CONTEXT.md 对 **Wave 1 / Wave 2 文件计数、Phase 2 已内联资产范围、rocketmq 体量、filter 计数** 的描述与实际源码有显著偏差，本 RESEARCH 给出修正后的实测数字。

**两项最关键发现：**

1. **【架构反向发现 / D-P4-01 前提失实】** 源 `basServer/api` 的 224 个 `@RestController` 中**没有任何一个 `implements I*Client`**（实测 0/224）。它们全部是 `extends BaseApi<Entity>` 的独立 REST 控制器，与 238 个 `basClient/remote` 的 `@FeignClient extends BaseClient<Entity>` 契约**仅共享 URL 路径与命名约定，无 `implements` 关系**。更严重：`BaseApi.findPage` 返回 `Page<T>`（Spring Data），`BaseClient.findPage` 返回 `PageDown<T>`，而 `PageDown<T> extends PageImpl<T> implements Page<T>`——**Java 协变返回是反方向**（子类返回更宽的 `Page<T>`，父接口要求更窄的 `PageDown<T>`），**不构成 override**。因此 D-P4-01 字面意义的"`@RestController implements I*Client` 原样照搬 + `@Autowired I*Client` 解析为本地 controller bean"**机械上不可行**，需要每文件加 `findPage` 桥接（224 次手工编辑，违反照搬原则）。详见 §Architecture Patterns §D-P4-01 修正。

2. **【范围反向发现 / Phase 2 实际未内联 238 契约】** CONTEXT.md `code_context` 与 `canonical_refs` 多处声称 "basClient/remote 238 个 `@FeignClient` 契约 Phase 2 已内联"。**实测当前单体 `zgbas-system/com/spt/bas/client/remote/` 仅 4 个文件**（`IBsDictClient`/`IBsCompanyOurClient`，Phase 2 内联；`IApproveWaitDealClient`/`IPmProcessClient`，Phase 3 stub-port 补）。其余 234 个契约**未内联**——它们是 Phase 4 Wave 1 的实际工作。Wave 1 真实净新增 ~250 文件（而非 CONTEXT 暗示的 ~570 减去已内联后的近零），详见 §Wave Composition。

**Primary recommendation:** planner 在 Wave 0 启动前必须先就 **D-P4-01 修正方案**取得用户决策（三选项见下）。在方案锁定前，Wave 1+ 工作无法精确切片。

<user_constraints>
## User Constraints (from CONTEXT.md)

### Locked Decisions

**D-P4-01: interface-as-contract（沿用 D-P2-10）** — `basServer/api` 的 224 个 `@RestController implements I*Client` 原样照搬进 `zgbas-system`；Feign 保持 Phase 2 收窄态（`@EnableFeignClients(basePackages="com.spt.sign.client.remote")`），`basClient/remote` 238 契约不再生成 Feign proxy → `@Autowired I*Client` 解析为本地 controller bean；不拆 controller 契约层、不重写为真·Service 直调。⚠️ **本 RESEARCH 发现此前提与源码不符**（见 §D-P4-01 Critical Finding），需 planner 重新决策。

**D-P4-02: 无本地实现的契约 stub 降级（约 14 个，对齐 D-P3-10）** — 238 契约中无本地 `@RestController` 实现的（疑为 report→P5 / purchase→v2 / auth 外部）用 `@Autowired(required=false)` + null 守卫 stub，业务降级裸 404，后续 phase 接通。具体清单留 research/planning 枚举确认 → **已在本 RESEARCH §D-P4-02 Enumeration 给出 25 项（去命名变体后约 15 项）**。

**D-P4-03: 层次优先 wave 切片** — Wave1 basClient 数据载体 → Wave2 service+infra → Wave3 api `@RestController` → Wave4 web BFF；每 wave 后 `mvn compile` 全模块零 `[ERROR]`。

**D-P4-04: infra 筛选照搬** — ✅照搬 cache(7)/util(34)/enums(2)/annotation(1)/filter/ listener(1)/command(2)/event(1)；⚠️ config(8) 选择性去重 Phase 2 已接的 @Primary DataSource/JpaTransactionManager/双 ORM；❌ task(23)→Phase 6；🔍 rocketmq 评估。→ **本 RESEARCH 给出 rocketmq INCLUDE 结论（22 文件而非 2）+ filter 实测 6（非 3）+ 8 config 分类表**。

**D-P4-05: 保持 ddl-auto=none + 仅修运行阻塞型** — 不主动全量 reconcile 239 实体 drift；运行期映射错误即修，只修阻塞性；全量 reconcile + 重开 validate 延 tech debt。

**D-P4-06: 启动验证为主 + WR-02 契约 HTTP proof** — 全 Spring context 启动 + 业务 `@RestController` bean 全解析 + MockMvc/HTTP 几个真实 `I*Client` 端点 GET 200；非 hermetic 同 D-P3-13（Option 4 本地 export 密钥）。真实 CRUD 行为对照留 Phase 7。

### Claude's Discretion

- **Flyway 处理**：移除（仅 1 个 drop-tmp 迁移无业务价值），纯 `ddl-auto=none`。
- **`basClient` 落位**：dto/vo/constant 等数据载体随 Wave 1 落 `zgbas-system` 的 `com.spt.bas.client.*`（保包名 D-P2-07）。
- **运行阻塞 drift 的发现机制**：验收 MockMvc + context 启动自然暴露。
- **事务边界**：沿用 Phase 2 `JpaTransactionManager` @Primary。
- **web 登录/失败行为**：随 controller 照搬。

### Deferred Ideas (OUT OF SCOPE)

- xxl-job `task` handler（23 个）→ Phase 6（QUARTZ-03，本期排除 task 包）
- 53 套报表 + report 契约 → Phase 5（REPORT-01/02）
- basWx 采购业务 + purchase 契约 → v2（WX-01/02）
- 真实业务 CRUD 行为对照 / 浏览器 e2e → Phase 7（ALIGN-01/02）
- 全量实体 schema drift reconcile + 重开 ddl-auto=validate（D-P2-02 原意）→ tech debt
- CR-01 真实轮换已泄漏生产库密码 → 跨阶段安全债
</user_constraints>

<phase_requirements>
## Phase Requirements

| ID | Description | Research Support |
|----|-------------|------------------|
| **BIZ-01** | 合同/授信/库存/放款等核心供应链业务（源 basServer JPA）迁入 `zgbas-system` | §Wave Composition Wave 2 (service+impl+infra 615 文件) + §D-P4-04 infra 分类表；实体(239)+Dao(229) Phase 2 已落位，本期补 service 业务逻辑层 + 筛选 infra。**前置阻塞：D-P4-01 修正决策（影响 Wave 3 是否能让 api 在单体暴露为 bean）** |
| **BIZ-02** | 业务 Controller / BFF（源 web）迁入 `zgbas-admin` | §Wave Composition Wave 4 (267 BFF controller)；已确认 252 个 BFF 文件引用 `I*Client`、0 个直接引用 service/api 包——BFF 仅依赖契约接口；其 `@Autowired I*Client` 能否解析**直接受 D-P4-01 修正决策影响** |
| **BIZ-03** | 业务间原 Feign 改为同进程直调，行为等价 | §Architecture Patterns §D-P4-01 Critical Finding——"同进程直调"在源码层无 `implements` 关系，需在三种实现路径（Feign 自回环 / 224 手写适配 / 编程式 FactoryBean）中选一；§Validation Architecture WR-02 HTTP proof 验收 |
</phase_requirements>

## Architectural Responsibility Map

| Capability | Primary Tier | Secondary Tier | Rationale |
|------------|-------------|----------------|-----------|
| 业务实体 / 持久化 (Entity/Dao) | zgbas-system | — | Phase 2 已落位（239 entity + 229 dao）；本期不动 |
| 业务 Service 接口 + 实现 | zgbas-system | — | Wave 2 照搬源 `com.spt.bas.server.service.*` + `service/impl/*` + 域子包 ctr/logistics/performance/stock/rt |
| 业务 REST 端点 (`@RestController extends BaseApi`) | zgbas-system | — | Wave 3 照搬源 `com.spt.bas.server.api.*`（路径前缀见 §D-P4-01 修正决策） |
| 业务间契约接口 (`@FeignClient extends BaseClient`) | zgbas-system | — | Wave 1 内联 238 契约到 `com.spt.bas.client.remote.*`（**Phase 2 仅内联 4 个，剩余 234 在本期**） |
| BFF Controller（web 层） | zgbas-admin | — | Wave 4 照搬源 `com.spt.bas.web.controller.*`（267 文件） |
| 前端模板 / 静态资源 | zgbas-admin | — | Phase 3 已全量照搬（608 模板 + 742 资源）；本期不动 |
| 业务 infra（cache/util/enums/annotation/filter/listener/command/event/rocketmq） | zgbas-system | — | Wave 2 随 service 一起照搬，保包名 `com.spt.bas.server.*` |
| Spring `@Configuration` 业务配置 | zgbas-system | — | Wave 2 选择性照搬 6 个（去重 1、剔 P6 1，见 §D-P4-04 表） |
| Shiro / 登录链路 | zgbas-framework + zgbas-admin | — | Phase 3 已就位，本期不动 |
| 外部 spt-auth HTTP 集成 | zgbas-framework (`IAuthOpenFacade` bean) | — | Phase 2 EXT-01 已注入，auth 相关契约（无本地实现）走外部 HTTP 不 stub |

## Standard Stack

### Core（已在 Phase 1/2/3 锁定，本期无新增框架）

| Library | Version | Purpose | Why Standard |
|---------|---------|---------|--------------|
| Spring Boot | 2.5.9 | 全栈基线 | 用户硬约束（CLAUDE.md），`spring-boot-starter-parent:2.5.9` grandparent |
| JDK | 1.8.0_482 (Corretto) | 编译/运行时 | 用户硬约束；mvn 前缀 `JAVA_HOME=Corretto-1.8`（见 ENVIRONMENT.md / MEMORY） |
| Spring Data JPA | 2.5.x (随 Boot) | 主力 ORM | 239 实体 + 229 Dao 已在 Phase 2 落位；本期复用 `JpaTransactionManager @Primary` |
| mybatis-plus | 3.4.x (Phase 2 已 pin) | 报表 ORM | 本期不涉及（→ Phase 5） |
| Shiro | 1.8.0 (Phase 2 已 pin) | 认证 | Phase 3 已激活，本期复用 |
| OpenFeign (spring-cloud-starter-openfeign) | 随 Boot 2.5.9 兼容版 | `@FeignClient` 契约 + cfca 外部调用 | Phase 2 D-P2-11/12 已保留 + 收窄 basePackages；**本期是否再放宽取决于 D-P4-01 修正决策** |

### Supporting（本期新增依赖）

| Library | Version | Purpose | When to Use |
|---------|---------|---------|-------------|
| `rocketmq-spring-boot-starter` | **2.2.2** | RocketMQ producer + 事务 listener | **D-P4-04 INCLUDE 结论**：源 basServer 业务依赖 MQ 推送（合同/工作目标/审批同步），3 个 Wave 2/3 入口文件引用 → 必须随 rocketmq 包一起照搬；详见 §D-P4-04 |

**Installation:**
```bash
# 仅一项新依赖（rocketmq-spring-boot-starter）+ 配置项；其余栈沿用 Phase 2/3
# 在 zgbas-admin/pom.xml 或根 dependencyManagement 加：
<dependency>
    <groupId>org.apache.rocketmq</groupId>
    <artifactId>rocketmq-spring-boot-starter</artifactId>
    <version>2.2.2</version>
</dependency>
# application-{dev,prod}.ml 加：
# rocketmq.name-server=...
# rocketmq.producer.group=contract_producer_group
```

**Version verification:** 2.2.2 来自源 `basCore/pom.xml` `<rocketmq-version>2.2.2</rocketmq-version>`（grep 实测）`[VERIFIED: 源 pom.xml]`。

### Alternatives Considered（rocketmq 三选项）

| Instead of | Could Use | Tradeoff |
|------------|-----------|----------|
| 照搬 rocketmq 全包 + starter | 排除 starter + 自写 no-op `RocketMQTemplate` stub | 违反照搬、需改 22 个文件的 import；行不行要看 WorkTarget/MQ 入口是否真要发消息 |
| 照搬 rocketmq 全包 + starter | 完全延后到 P7（注释掉 3 个入口文件对 MQ 的引用） | 破坏行为等价（WorkTarget CRUD 不再推 MQ）、改 3 处但破坏契约语义 |
| rocketmq-spring-boot-starter 2.2.2 | 升 2.3.x | 违反 D-P2-08 pin 旧版本原则，无必要 |

**本 RESEARCH 推荐照搬 + starter**：最低风险、保行为等价、自包含（无 service 层耦合）；详见 §D-P4-04 verdict。

## Package Legitimacy Audit

> 本期仅引入 1 个新外部依赖：`rocketmq-spring-boot-starter:2.2.2`。其余均为已锁栈。

| Package | Registry | Age | Downloads | Source Repo | slopcheck | Disposition |
|---------|----------|-----|-----------|-------------|-----------|-------------|
| `rocketmq-spring-boot-starter` | Maven Central | 4+ 年（2.2.2 发布于 2021）| Apache 官方维护，广泛使用 | github.com/apache/rocketmq-spring | 未跑（环境无 slopcheck）| Approved — `[ASSUMED]`（来源 = 源项目 pom.xml verbatim，未跨源验证；planner 可加 `checkpoint:human-verify`）|

**Packages removed due to slopcheck [SLOP] verdict:** none
**Packages flagged as suspicious [SUS]:** none

> slopcheck 未安装（Step 1 graceful degradation 路径）。本包版本号直接取自源项目 `basCore/pom.xml:25` `<rocketmq-version>2.2.2</rocketid>`，属"照搬源项目依赖"——`[ASSUMED]` 标签按协议要求保留，但来源可信度等同 `[VERIFIED: 源 pom]`。

**Ecosystem registry verification:**
```bash
# Step 3 — Maven Central 验证（未跑，因环境无网络访问 Maven 元数据；源 pom 已确认）
# planner 执行时跑： mvn dependency:get -Dartifact=org.apache.rocketmq:rocketmq-spring-boot-starter:2.2.2
```

**Step 4 — postinstall scripts check：** Maven 包无 npm 式 postinstall；N/A。

## Architecture Patterns

### System Architecture Diagram

源（微服务）→ 目标（单体）数据流，重点突出 D-P4-01 契约-实现的解耦关系：

```
                          【源：4 个微服务】
   web(80) ──Feign HTTP──>  basServer(8001, /spt-bas-server/*)  ──JPA──> sptbasdb_pd
   BFF(267)                api(224 @RestController)                       (MySQL)
   @Autowired              extends BaseApi<Entity>
   I*Client(238)           NO implements I*Client
                           service(241 接口 + 248 impl + 域子包 44)
                           infra(cache/util/.../rocketmq)
                                  ↑
                                  │ Feign HTTP self-loop (微服务时代)
                                  │
                  【目标：zgbas-plus 单进程】
   ┌─────────────────────────────────────────────────────────────┐
   │ zgbas-admin (8080, /)                                        │
   │  - BFF Controller (267, Wave 4)  @Autowired I*Client (252 处) │
   │  - 模板 + 静态资源（Phase 3 已就位）                          │
   │  - ZgbasApplication @SpringBootApplication 扫 com.spt.*       │
   └─────────────────────────────────────────────────────────────┘
                            ↑ Spring bean DI（单体进程内）
   ┌─────────────────────────────────────────────────────────────┐
   │ zgbas-system                                                 │
   │  - com.spt.bas.client.remote.* (238 契约接口, Wave 1)        │ ← Phase 2 仅 4 个
   │  - com.spt.bas.server.service.* (489 + 域子包, Wave 2)        │
   │  - com.spt.bas.server.api.* (224 @RestController, Wave 3)    │ ← extends BaseApi
   │  - com.spt.bas.server.{infra, rocketmq}.* (Wave 2)           │
   │  - com.spt.bas.client.{entity,dao,vo,constant,cache,...}     │ ← Phase 2 已就位
   └─────────────────────────────────────────────────────────────┘
                            ↑ JPA / mybatis-plus（同 DataSource）
                            ↓
                  sptbasdb_pd (MySQL, ddl-auto=none)

   【D-P4-01 关键问题】
   BFF @Autowired IApplyBrandClient  ───→ ??? 谁满足此契约?
     选项 A：Feign 自回环到 localhost:8080/spt-bas-server/apply/brand（需配 context-path 或改 Feign path）
     选项 B：手写 224 个适配类 implements I*Client + findPage 桥接（违反照搬）
     选项 C：编程式 FactoryBean（@Configuration 产 238 bean，复杂）
     → planner 必须先决策才能切 Wave 3 任务
```

### Recommended Project Structure

源 → 目标包名映射（保包名 verbatim，D-P2-07）：

```
zgbas-system/src/main/java/com/spt/bas/
├── client/                          # basClient 数据载体层
│   ├── remote/    (238 契约)         # Wave 1：内联剩余 234 个（Phase 2 仅 4 个）
│   ├── dto/       (2)                # Wave 1
│   ├── util/      (6)                # Wave 1
│   ├── common/    (3)                # Wave 1
│   ├── riskScore/ (3)                # Wave 1
│   ├── config/    (1: BasClientConfig) # Wave 1（产生 basServerConfig bean）
│   ├── entity/    (239)              # Phase 2 已就位 ✓
│   ├── vo/        (293)              # Phase 2 已就位 ✓
│   ├── constant/  (20)               # Phase 2 已就位 ✓
│   └── cache/     (3)                # Phase 2 已就位 ✓
└── server/                          # basServer 业务层
    ├── service/        (241 接口)    # Wave 2 顶-level
    │   └── impl/       (248 实现)    # Wave 2 子包
    ├── api/            (224)         # Wave 3 @RestController extends BaseApi
    ├── ctr/            (9)           # Wave 2 域子包（合同业务）
    ├── logistics/      (6)           # Wave 2 域子包（物流）
    ├── performance/    (4)           # Wave 2 域子包（业绩）
    ├── stock/          (24)          # Wave 2 域子包（库存）
    ├── rt/             (1)           # Wave 2 域子包（融拓对接）
    ├── cache/          (7)           # Wave 2 infra
    ├── util/           (34)          # Wave 2 infra
    ├── enums/          (2)           # Wave 2 infra
    ├── annotation/     (1)           # Wave 2 infra
    ├── filter/         (6: 3 接口+3 impl)  # Wave 2 infra（CONTEXT 误写 3）
    ├── listener/       (1)           # Wave 2 infra
    ├── command/        (2)           # Wave 2 infra（引用 rocketmq，先于 MQ 包照搬）
    ├── event/          (1)           # Wave 2 infra
    ├── rocketmq/       (22)          # Wave 2 infra（INCLUDE，非延后；见 §D-P4-04）
    ├── config/         (6 of 8)      # Wave 2 infra（去重 FrameworkConfig + 剔 BasJobConfig→P6）
    └── dao/            (229)         # Phase 2 已就位 ✓

zgbas-admin/src/main/java/com/spt/bas/web/
└── controller/        (267)         # Wave 4 BFF（保包名 com.spt.bas.web.controller.*）
                                        # 其中 252 个引用 I*Client 契约

zgbas-admin/src/test/java/com/spt/
├── ZgbasApplicationTest.java         # Phase 2/3 已就位（14 @Test，@SpringBootTest RANDOM_PORT）
└── proof/InProcessContractTest.java  # Phase 2 trivial proof（独立，本期 WR-02 主目标在 ZgbasApplicationTest 扩展）
```

### Pattern 1: interface-as-contract（D-P2-10 原意）

**What:** `@FeignClient extends BaseClient<Entity>` 契约接口保留作契约；目标 `@RestController` `implements` 该接口注册为本地 bean；Spring MVC 认可接口上的 `@RequestMapping`，调用方 `@Autowired` 接口解析为本地 bean（无 Feign 网络跳）。

**When to use:** 源调用方与目标实现在同一进程的单体场景。

**Example（Phase 2 trivial proof，已存在）：**
```java
// Source: zgbas-admin/src/main/java/com/spt/proof/InProcessContract.java（已存在）
public interface InProcessContract {
    @GetMapping("/proof/echo")
    String echo(@RequestParam("msg") String msg);
}

// Source: zgbas-admin/src/main/java/com/spt/proof/InProcessContractImpl.java（已存在）
@RestController
public class InProcessContractImpl implements InProcessContract {
    @Override public String echo(String msg) { return "echo:" + msg; }
}
```

`[VERIFIED: 当前单体 zgbas-admin/src/main/java/com/spt/proof/*]`

### D-P4-01 Critical Finding（架构反向发现）

**源码事实（实测）：**
```
grep -rln "implements.*I[A-Z][a-zA-Z]*Client" basCore/basServer/src/main/java/com/spt/bas/server/api/ --include="*.java"
# → 0 命中
# 224 个 api 文件全部是 "extends BaseApi<Entity>"，无任何 implements I*Client
```
`[VERIFIED: 源码 grep]`

**为什么不能简单加 `implements I*Client`（机械障碍）：**

`spt-tools-data` 中 `BaseApi<T>` 与 `BaseClient<T>` 的 `findPage` 返回类型不兼容（同包内 `com.spt.tools.data.service`）：

| 方法 | BaseClient 签名（契约要求） | BaseApi 签名（源 api 提供） | Java override？ |
|------|----|----|----|
| `delete(Long)` | `void delete(Long)` | `void delete(Long) throws ApplicationException` | ✓（narrowing throws 合法） |
| `findPage(PageSearchVo)` | `PageDown<T> findPage(...)` | **`Page<T>` findPage(...)** | **✗**（反协变） |
| `sumPage(Map)` | `T sumPage(Map)` | `T sumPage(Map)` | ✓ |
| `save(T)` | `T save(T)` | `T save(T) throws ApplicationException` | ✓ |
| `findAll()` | `List<T> findAll()` | `List<T> findAll()` | ✓ |
| `getEntity(Long)` | `T getEntity(Long)` | `T getEntity(Long)` | ✓ |
| `saveBatch(BatchSaveVo)` | `void saveBatch(...)` | `void saveBatch(...) throws ApplicationException` | ✓ |

**根因：** `PageDown<T> extends PageImpl<T>` 且 `PageImpl<T> implements Page<T>`——即 `PageDown` 是 `Page` 的子类型。BaseApi 提供的 `Page<T>` 返回是 BaseClient 要求的 `PageDown<T>` 的**父类型**，**Java 协变返回规则不允许此方向**（子类方法返回必须 ≤ 父类返回，不能 ≥）。`[VERIFIED: spt-tools PageDown.java L23 + BaseApi.java + BaseClient.java]`

**结论：** 即使强行给 224 个 api 加 `implements IXxxClient`，每个 api 仍必须**单独手写** `findPage` override 把 `Page<T>` 包装回 `PageDown<T>`——224 次机械编辑，违反"原样照搬 + 行为等价"原则。

**附加障碍：路径前缀不一致**
- 源 basServer：`server.servlet.context-path=/spt-bas-server`（grep 实测 `application.properties:2`），所以 api 的 `@RequestMapping("apply/brand")` 实际暴露在 `/spt-bas-server/apply/brand/*`
- 单体（Phase 2 D-P2-16）：端口 8080 + 根 `/`，**无 context-path**——api 照搬后会暴露在 `/apply/brand/*`，而 `I*Client` 契约的 Feign `path = BasConstants.SERVER_NAME + "/apply/brand"` = `spt-bas-server/apply/brand`——**路径不一致**
`[VERIFIED: 源 basCore/basServer/src/main/resources/application.properties + 当前 zgbas-admin application.yml]`

**Planner 必须在 Wave 0/1 锁定三选一：**

| 方案 | 工作量 | 行为等价 | 网络跳 | 照搬保真 | 推荐度 |
|------|--------|---------|--------|---------|--------|
| **A. Feign 自回环** — 放宽 `@EnableFeignClients` 扫 `com.spt.bas.client.remote`，配 `spt.bas.server.url=http://localhost:8080`，并解决 path 前缀（加 `server.servlet.context-path=/spt-bas-server` 或改 238 契约的 `path=`） | 极低（1 pom 改 + 1 yml + 1 注解放宽 + path 方案） | ✓ 等价（源即 Feign-over-HTTP） | **有（同进程 HTTP 跳）** | ✓ 最高 | ★★★（与项目"行为等价优先"核心价值最契合） |
| **B. 224 手写适配** — 每个 api 加 `implements IXxxClient` + 单独 `findPage` override（Page→PageDown 包装） | 极高（224×2 编辑，引入新风险） | ✓ | 无 | ✗ 严重违反照搬 | ★ |
| **C. 编程式 FactoryBean** — 单 `@Configuration` 产 238 bean 适配 api → I*Client | 中（1 个大配置类 + 反射代理 + findPage 桥接） | ✓ | 无 | ✗（侵入式新代码） | ★★ |

**RESEARCH 推荐 A**，但**必须由用户在 planning 阶段确认**，因为 D-P4-01 字面写的是"无网络跳"——选 A 即承认偏离此条措辞，换"行为等价 + 照搬保真"。

### Anti-Patterns to Avoid

- **无脑照搬 224 api 后期望 `@Autowired I*Client` 自动解析**：实测会 `NoSuchBeanDefinitionException`（api 不实现契约、Feign 不扫 bas.client.remote 包）——Spring 容器无 bean 可注入。
- **同时启用 `@EnableFeignClients` 扫 bas.client.remote + 让 api `implements I*Client`**：双 bean 冲突（Feign proxy + 本地 controller）。
- **Wave 2 service 抄进来时不带 rocketmq**：`BasCommandExecutor`（command 包）+ `MQApi`/`WorkTargetApi`（api 包）会编译错（grep 实测 3 处 import `com.spt.bas.server.rocketmq.*`）。
- **Wave 2 带全 8 个 config**：`FrameworkConfig` 会与 Phase 2 `framework/ZgbasMybatisConfig` + Druid DataSource 重复 bean；`BasJobConfig` 会引入 `XxlJobSpringExecutor`（违 INFRA-03，应 P6）。

## Don't Hand-Roll

| Problem | Don't Build | Use Instead | Why |
|---------|-------------|-------------|-----|
| Feign → 同进程直调（BIZ-03） | 自写 224 个 `implements IXxxClient` 适配类 | Feign 自回环（方案 A）或编程式 FactoryBean | spt-tools 体系（BaseApi/BaseClient）已锁定，签名不兼容，手写代价极高 |
| RocketMQ 集成 | 自写 no-op `RocketMQTemplate` 替换 | `rocketmq-spring-boot-starter:2.2.2`（源项目版本） | starter 自动配 producer，22 个 rocketmq 包 verbatim 照搬即可 |
| 业务事务边界 | 自写 `PlatformTransactionManager` | 沿用 Phase 2 `JpaTransactionManager @Primary` + `@Transactional` | Phase 2 D-P2-03 已接，业务零事务设计 |
| 实体 schema drift 修复 | 主动全量 reconcile 239 实体 annotation | 保持 `ddl-auto=none` + 遇运行阻塞才修（D-P4-05） | 全量 reconcile 是 259 表改动风险，延 tech debt |

**Key insight：** 本期最大风险不是写代码，而是**让 1900+ 文件机械照搬后能否通过 Spring context 启动验证**。Phase 1 gotcha 级联教训（一处错掩一处）决定了"层次 wave + 逐层 compile 绿灯"的执行纪律必须严格执行。

## Runtime State Inventory

> 本期为 greenfield 照搬迁移，**无运行态 rename/refactor**——源代码包名 verbatim 搬入单体，不改 string。故本表大部分类别为 N/A。

| Category | Items Found | Action Required |
|----------|-------------|------------------|
| Stored data | None — 本期不创建/重命名数据库表（`ddl-auto=none` 保留 `sptbasdb_pd` 现有 schema） | none |
| Live service config | None — 单体未启动到生产；rocketmq/prod 配置在 application-prod.yml 占位 | none（prod 部署见 D-P2-13 外置） |
| OS-registered state | None | none |
| Secrets/env vars | 复用 Phase 2/3 已外置的 `${DB_PASSWORD}` / `${SPT_APP_SECRET}`；**新增** `rocketmq.name-server` + `rocketmq.producer.{access-key,secret-key,group}`（源 application-prod.properties:34-36 已有明文，需走 D-P2-13 外置为 `${ROCKETMQ_NAMESERVER}` / `${ROCKETMQ_AK}` / `${ROCKETMQ_SK}` 占位） | Wave 2 / 验收 yml 加占位；prod profile 全占位 |
| Build artifacts | None — 本期不删现有 jar / 不重打包（仅源码新增） | none |

**The canonical question 答：** 单体进程重启后所有状态由 Spring context + Druid DataSource + sptbasdb_pd 现有 schema 提供。本期新增源码 verbatim 照搬不引入跨进程状态。

## Common Pitfalls

### Pitfall 1: D-P4-01 前提失实导致 Wave 3 完成后 `NoSuchBeanDefinitionException`
**What goes wrong:** 若按 CONTEXT.md 字面照搬 Wave 3 后 BFF 启动，`@Autowired IApplyBrandClient` 在容器中找不到 bean（api 不 implements 契约，Feign 也不扫 bas.client.remote）。
**Why it happens:** CONTEXT 撰写者假设 `@RestController implements I*Client`，但源码不是这样（实测 0/224 implements）。
**How to avoid:** Wave 0/1 必须先锁 D-P4-01 修正方案（A/B/C）；选 A 则 Wave 1 同时放宽 `@EnableFeignClients` basePackages。
**Warning signs:** Wave 3 完成后 `mvn test`（启动验证）抛 `NoSuchBeanDefinitionException: No qualifying bean of type 'com.spt.bas.client.remote.IXxxClient'`。

### Pitfall 2: rocketmq 延后导致 Wave 2/3 编译失败
**What goes wrong:** 按 CONTEXT "rocketmq 评估默认延后" 的暗示，Wave 2 抄 service+command 时漏抄 rocketmq 包，3 个引用文件编译错。
**Why it happens:** CONTEXT 写 "rocketmq（2）"——实际 22 个文件，且 `BasCommandExecutor` 等关键 service-side command 直接 import。
**How to avoid:** 按本 RESEARCH 的 INCLUDE 结论，Wave 2 infra 抄 `cache/util/.../rocketmq/` 一起；pom 加 `rocketmq-spring-boot-starter:2.2.2`。
**Warning signs:** Wave 2 compile gate 抛 `cannot find symbol class TestRocketmqProducer / WorkTargetSendMessage / SynchronizedCtrContractTask`。

### Pitfall 3: filter / config 文件数与 CONTEXT 不符导致漏抄
**What goes wrong:** 按 CONTEXT "filter(3) / config(8 选择性去重)" 切片，实际 filter 6（3 接口+3 impl），漏抄 3 个 impl 后 service 编译缺 BudgetVerify/AutoSealPdfSign/AutoStartPay 过滤器。
**Why it happens:** CONTEXT 计数仅算接口未算 impl。
**How to avoid:** 用本 RESEARCH §D-P4-04 的实测计数表（filter 6 / config 6 保 + 1 dedup + 1 剔 P6）。
**Warning signs:** Wave 2 编译错 `IBudgetVerifyFilter` 找不到 impl。

### Pitfall 4: 路径前缀不一致让 Feign 自回环失败
**What goes wrong:** 选方案 A 后，Feign 调 `http://localhost:8080/spt-bas-server/apply/brand/findAll` → 404（单体无 context-path，api 实际暴露在 `/apply/brand/findAll`）。
**Why it happens:** 源 basServer 用 `server.servlet.context-path=/spt-bas-server` 做前缀；单体 D-P2-16 是根 `/`。
**How to avoid:** 方案 A 子选项二选一：(a) 单体也设 `server.servlet.context-path=/spt-bas-server`（破坏 Phase 3 AUTH-03 的 `/login` `/index` 根路径，需重新验证 Shiro 过滤器链）；(b) 用 Feign 的 `@FeignClient(path=...)` 覆盖机制或 yml `feign.client.config` 把 `spt-bas-server` 前缀去掉——后者更安全。Planner 必须在 Wave 1 锁定。
**Warning signs:** Wave 4 完成后 `TestRestTemplate.getForEntity("/apply/brand/findAll")` 200 但 BFF 调 Feign `IApplyBrandClient.findAll()` 404。

### Pitfall 5: Wave 2 拆分不足导致 gotcha 级联（Phase 1 教训）
**What goes wrong:** Wave 2 一次抄 615+ 文件，编译错误数巨大不可控（Phase 1 教训：gotcha 级联从 40 → 30 → 20 → 0）。
**Why it happens:** service + impl + 域子包 + infra + rocketmq + config 单波太大。
**How to avoid:** **Wave 2 应拆 sub-wave**：(2a) infra 先行（cache/util/enums/.../rocketmq/config，~95 文件）→ compile 绿 → (2b) service + impl + 域子包（~530 文件）→ compile 绿。详见 §Wave Composition。

### Pitfall 6: Phase 2 实际只内联 4 个 remote 契约
**What goes wrong:** 若 planner 信 CONTEXT "Phase 2 已内联 238" 跳过 Wave 1 remote 内联，Wave 4 BFF 启动直接 `ClassNotFoundException: com.spt.bas.client.remote.IApplyBrandClient`。
**Why it happens:** CONTEXT.md `canonical_refs` L88 与 `code_context` L116 错记 Phase 2 范围。
**How to avoid:** Wave 1 必须包含抄全 238 个 `basClient/remote` 契约（净新增 234，已有 4）。
**Warning signs:** Wave 1 完成前 zgbas-system 找不到 `com.spt.bas.client.remote.IXxxClient`。

### Pitfall 7: 启动测试非 hermetic（沿用 Phase 3 D-P3-13）
**What goes wrong:** `mvn test` 直接跑会因 `${DB_PASSWORD}` / `${SPT_APP_SECRET}` 无默认值而 ERROR（`Could not resolve placeholder`）。
**Why it happens:** Phase 3 已确认非 hermetic 契约（dev profile 真连 `47.104.15.98:3306/sptbasdb_pd` + 真外部 spt-auth）。
**How to avoid:** 沿用 Phase 3 Option 4——本地 `export DB_PASSWORD=... SPT_APP_SECRET=...` 后 `mvn test`，验收接受"local-export = passing"。

## Code Examples

### Example 1: Feign 自回环配置（方案 A，推荐）

```yaml
# zgbas-admin/src/main/resources/application-dev.yml 新增（保 Phase 2 D-P2-13 占位模式）
spt:
  bas:
    server:
      url: http://localhost:8080   # 单体自回环；BasClientConfig bean 读此 key 给 @FeignClient url=#{basServerConfig.url}
rocketmq:
  name-server: 47.104.66.178:9876  # 照搬源 application-dev.properties:22
  producer:
    group: contract_producer_group  # 照搬源 application.properties:66
    access-key: ${ROCKETMQ_AK:zgrocketmq}
    secret-key: ${ROCKETMQ_SK:zg12345678}  # ⚠ 源为明文，D-P2-13 应外置占位
```

```java
// ZgbasApplication.java：放宽 basePackages（仅当选方案 A）
@EnableFeignClients(basePackages = {
    "com.spt.sign.client.remote",   // EXT-03 cfca，Phase 2 已就位
    "com.spt.bas.client.remote"     // Phase 4 新增：bas 契约自回环
})
// Source: 当前 ZgbasApplication.java:66 + 推荐扩展
```
`[CITED: 当前单体 ZgbasApplication.java + 源 BasClientConfig.java + 源 application-dev.properties]`

### Example 2: WR-02 HTTP proof 扩展示例（D-P4-06）

```java
// 扩展 ZgbasApplicationTest（已存在，14 @Test）—— 加几条业务端点 HTTP reachability
@Test
void basContractEndpointReachable_applyBrand_findAll() {
    // Wave 3 完成后 ApplyBrandApi 应在 /apply/brand/findAll 暴露（方案 A 自回环后由 Feign 触发，
    // 也可直接 TestRestTemplate 调，断言 200/3xx/甚至 401 都算端点就位）
    // 选 3-5 个代表性 I*Client 端点（合同/授信/库存/放款各一）
    ResponseEntity<String> response = restTemplate.postForEntity(
        "/apply/brand/findAll", null, String.class);
    assertThat(response.getStatusCode().is2xxSuccessful()
        || response.getStatusCode().is3xxRedirection()
        || response.getStatusCodeValue() == 401).isTrue();  // Shiro 未登录可能 401/302
}
// Source: 现有 ZgbasApplicationTest 风格 + D-P4-06 WR-02 要求
```
`[CITED: 当前单体 Zgbas-admin/src/test/java/com/spt/ZgbasApplicationTest.java]`

### Example 3: 真实 api + 契约对照（源码）

```java
// 源 basCore/basServer/src/main/java/com/spt/bas/server/api/ApplyBrandApi.java（Wave 3 照搬目标）
@RestController
@RequestMapping(value = "apply/brand")   // 实际暴露在 /spt-bas-server/apply/brand/*（context-path 加前缀）
public class ApplyBrandApi extends BaseApi<BasBrand> {
    @Override public IDataService<BasBrand> getService() { return workTargetService; }
    // 继承 BaseApi 的 findAll/save/findPage/... 方法
    // ⚠ 注意：未 implements IApplyBrandClient
}

// 源 basCore/basClient/src/main/java/com/spt/bas/client/remote/IApplyBrandClient.java（Wave 1 内联目标）
@FeignClient(qualifier = "applyBrandClient", name = BasConstants.SERVER_NAME,
    path = BasConstants.SERVER_NAME + "/apply/brand",
    url = BasConstants.SERVER_URL,   // "#{basServerConfig.url}"
    configuration = FeignConfig.class)
public interface IApplyBrandClient extends BaseClient<BasBrand> {
    // 继承 BaseClient 的 findAll/save/findPage/... 抽象方法
}
```
`[VERIFIED: 源 basCore 项目 grep]`

## D-P4-02 Stub-Degradation Enumeration（关键交付）

**方法：** 取 238 契约名（去 `I` 前缀 + `Client` 后缀）vs 224 api 名（去 `Api` 后缀）做集合差集。

**结果：25 项契约名无匹配 api**（按命名约定推算）：

```
ApplyCompanyOnline          ApplyDeposit             ApplyPromoteVip
BsInvestigateInfo           CtrOutInLedger           RiskApply
SignFileApi                 (注：实际 api 名 SignFile 缺失，确认 stub)

# 命名变体（可能匹配但命名不一致，需 planner 二次确认）：
BaiduMapClient         ↔ api BaiduMap               (变体匹配)
LogisticsCompanyConfigClient ↔ api LogisticsCompanyConfig (变体匹配)
LogisticsCompanyDetailClient ↔ api LogisticsCompanyDetail (变体匹配)
MQClient               ↔ api MQ                      (变体匹配)
IApplyCtrDcsxClinent   ↔ api ApplyDcsx               (源 typo "Clinent"，变体匹配)
BsInvestigateInfo      ↔ api BsInvestigate            (变体匹配，差 "Info")

# PM 域（13 个一簇，可能跨模块）：
PmApplySet / PmApprove / PmApproveContents / PmApproveHistory / PmApprovePush
PmApproveStep / PmProcess / PmProcessAccess / PmProcessAutoStep
PmProcessCondition / PmProcessNode / PmProcessPush / PmProcessStep

# 已在 Phase 3 stub-port 的（保留）：
IPmProcessClient / IApproveWaitDealClient  → 已在 zgbas-system 作 stub
```
`[VERIFIED: 源 grep + comm 差集]`

**分类与处置：**

| 来源类别 | 数量 | 处置 |
|---------|------|------|
| **真实业务 stub**（ApplyCompanyOnline / ApplyDeposit / ApplyPromoteVip / BsInvestigateInfo / CtrOutInLedger / RiskApply / SignFileApi） | ~7 | D-P4-02 stub 降级：`@Autowired(required=false)` + null 守卫 |
| **PM 域簇**（PmApplySet/PmApprove×5/PmProcess×7） | 13 | **需 planner 二次核查**：PM 实体（com.spt.pm.*）Phase 2 已部分内联（pm.inter/pm.entity/pm.vo），但 api 在源 basServer 找不到对应。可能在 `spt-auth/auth-admin`（grep 未在 zgbas 找到 implements）。**默认按 D-P4-02 stub 处理**，但若确认 PM 服务在 spt-auth 外部则按"外部 HTTP"对待（不 stub，等 Phase 7 接外部） |
| **命名变体匹配**（BaiduMap/LogisticsCompany×2/MQ/ApplyDcsx） | ~5 | 不算 stub，按名变体规则对齐真实 api |
| **Phase 3 已 stub-port**（IPmProcessClient/IApproveWaitDealClient） | 2 | 保留现有 stub，本期 Wave 3 接入真 api `ApproveWaitDealApi`（实测存在）后切换 |

**验证 CONTEXT.md 的 ~14 估计：** 25 原始 - 5 命名变体 - 2 已存在 stub - 13 PM 簇（如全 stub 则合并）= **5-15 个真新 stub**。CONTEXT "~14" 在量级上正确。

## D-P4-04 Infra Filter Confirmation

### Infra 包实测计数 vs CONTEXT.md 对照

| 包 | CONTEXT 估计 | 实测 | 差异 | 处置 |
|----|------|------|------|------|
| cache | 7 | **7** | ✓ | 全照搬 |
| util | 34 | **34** | ✓ | 全照搬 |
| enums | 2 | **2** | ✓ | 全照搬 |
| annotation | 1 | **1** | ✓ | 全照搬 |
| **filter** | **3** | **6** | **CONTEXT 少算 3 impl** | 全照搬（3 接口 + `filter/impl/` 3 impl） |
| listener | 1 | **1** | ✓ | 全照搬 |
| command | 2 | **2** | ✓ | 全照搬（`BasCommandExecutor` 引用 rocketmq） |
| event | 1 | **1** | ✓ | 全照搬 |
| **rocketmq** | **2** | **22** | **CONTEXT 严重低估** | **INCLUDE 照搬 + 加 starter**（见下） |
| config | 8 | **8** | ✓ | 6 保 + 1 dedup + 1 剔 P6（见下） |
| ❌ task | — | 23 | — | 排除 → Phase 6 |
| 附加域子包 | — | ctr 9 + logistics 6 + performance 4 + rt 1 + stock 24 = **44** | CONTEXT 未提 | Wave 2 一起照搬（属业务 service 子域，非 infra） |

### rocketmq INCLUDE 判定（D-P4-04 verdict）

**核查：**
- 源 `basServer/rocketmq/` = 22 文件（producer 1 + customProperties 1 + dto 2 + listener 1 + util 3 + tags 5 + task 8 + send 1）
- **3 个非 rocketmq 文件 import 它**：
  - `api/MQApi.java`（Wave 3）— `import rocketmq.task.*`
  - `api/WorkTargetApi.java`（Wave 3）— `import rocketmq.send.WorkTargetSendMessage`
  - `command/BasCommandExecutor.java`（Wave 2）— `import rocketmq.TestRocketmqProducer`
- pom 依赖：`org.apache.rocketmq:rocketmq-spring-boot-starter:2.2.2`（源 basCore/pom.xml:25）
- 配置：`rocketmq.name-server` dev/prod 均配置（47.104.66.178:9876 / 172.16.0.204:9876）
- Bean 形态：`TestRocketmqProducer` / `WorkTargetSendMessage` / `CtrContractTransactionalListener` 均 `@Component`，autowire `RocketMQTemplate`

**关键事实：** `rocketmq-spring-boot-starter` 2.2.2 的 producer 是**懒连接**——context 启动期不连 broker，仅在首次 send 时连。故 starter 在 classpath 不会阻塞 Spring context 启动（D-P4-06 启动验证可通过），broker 实际可达性是 Phase 7 ALIGN 的问题。

**verdict：INCLUDE**（照搬 22 文件 + 加 starter + yml 配置）。理由：
1. service/api 层有真实编译依赖（3 入口文件），延后必破坏 Wave 2/3 编译。
2. 自包含（仅 3 处外部入口，无 service 层 entanglement）。
3. 与项目"行为等价优先 + 照搬"原则一致。
4. 启动验证不阻塞（懒连接）。

`[VERIFIED: 源 grep + 源 pom + spt-tools PageDown + basServer/rocketmq/* + rocketmq starter 行为]`

### config 8 文件分类（D-P4-04 选择性去重）

| 文件 | 类型 | Phase 4 处置 |
|------|------|--------------|
| `FrameworkConfig.java` | `bas.datasource` DataSource + 3 SDK bean | **DEDUP** — Phase 2 framework 已接 `@Primary` Druid + `JpaTransactionManager` + 3 外部 SDK bean（EXT-01..03）；照搬会重复 bean 冲突 |
| `BasJobConfig.java` | `@Bean XxlJobSpringExecutor` | **EXCLUDE** → Phase 6（INFRA-03，本期照搬会引入 xxl-job 依赖） |
| `WebAppConfig.java` | Servlet filter + multipart | COPY（业务必需 web 配置） |
| `BasicErrorController.java` | Error controller | COPY |
| `RtConfig.java` | `@ConfigurationProperties(prefix="rt.config")` | COPY（被 `rt/RtApi` 引用） |
| `GuTuConfig.java` | `@ConfigurationProperties(prefix="gutu.config")` | COPY |
| `BasPiccConfig.java` | `@ConfigurationProperties(prefix="picc.config")` | COPY |
| `ScheduleConfig.java` | `@EnableScheduling` + `ScheduledTaskRegistrar`（Spring 内置调度，非 xxl-job） | COPY（与 BasJobConfig 不同：Spring `@Scheduled` 而非 xxl-job） |

`[VERIFIED: 源 config/*.java head + grep]`

## D-P4-05 Schema Drift Quick Survey

**方法（轻量）：** 在 239 已内联实体中 grep 显式 drift 关键字（`mediumtext` / `varchar(255)` / `api_param` 等）。

**结果：** **未发现明显运行阻塞型 drift**。CONTEXT.md 举例的 `api_param varchar(255) vs mediumtext` 在实体层 grep 无命中——可能为 DB 列定义层的 drift（D-P4-05 仅作"修运行阻塞型"，启动验证暴露才修）。

**已知风险（保守观察清单，验收阶段 fix-if-hit）：**
- `ApiRequestHis` / `ApiExternalHis` 实体 grep 命中 `api_param` 关键字（字段名引用，非类型冲突）——可能为 SampleMapper 已验证可查的 `t_api_external_his` 表
- 234 个新内联 remote 契约 + 489 service 照搬后，运行期可能暴露更多 entity↔DB column 映射偏差
- `ddl-auto=none` 不主动 reconcile，启动期不校验，**只能在 MockMvc HTTP proof 时通过实际查询错误暴露**

**verdict：** 不做全量 reconcile（D-P4-05）。验收阶段遇到 Hibernate 映射错（如 `Column 'xxx' not found` / `Cannot deserialize`）即修，仅修阻塞性。**全量 reconcile + 重开 validate 延 tech debt**（todo `phase4-resolve-entity-schema-drift` 保留 open）。

`[VERIFIED: 实体 grep]`

## D-P4-06 Verification Assets

### 现有验证资产

| 资产 | 路径 | 当前状态 | 本期用途 |
|------|------|---------|---------|
| `ZgbasApplicationTest` | `zgbas-admin/src/test/java/com/spt/ZgbasApplicationTest.java` | 14 @Test（`@SpringBootTest(RANDOM_PORT)`），覆盖 P2/P3 全 context 启动 + bean 接线 + `/login`/`/index`/`/open/user/ssoLogin` 端点 reachability | **WR-02 主扩展点**：新增 3-5 个真实 `I*Client` 端点的 HTTP reachability 断言（合同/授信/库存/放款各一） |
| `InProcessContractTest` | `zgbas-admin/src/test/java/com/spt/proof/InProcessContractTest.java` | trivial proof（仅测 `/proof/echo`） | 不动；D-P2-10 pattern 的存在性证明 |
| `spring-boot-starter-test` | `zgbas-admin/pom.xml` | 已声明 | 提供 `TestRestTemplate` / `MockMvc`（本期用 `TestRestTemplate` 即可，无需引入 MockMvc） |

### WR-02 HTTP proof 实现路径

`ZgbasApplicationTest` 已用 `TestRestTemplate`（非 MockMvc）做端点 reachability——**本期沿用此风格**，避免引 MockMvc 的额外学习/配置成本。扩展模式见 §Code Examples Example 2。

断言语义：`is2xxSuccessful || is3xxRedirection || 401`（Shiro 未登录可能返回 401/302）——证明**端点已注册 + Spring MVC 经接口级 `@GetMapping`/`@PostMapping` over HTTP 可达**，即 WR-02 满足。

### 非 hermetic 契约（沿用 D-P3-13 Option 4）

- dev profile 真连 `jdbc:mysql://47.104.15.98:3306/sptbasdb_pd`
- `${DB_PASSWORD}` / `${SPT_APP_SECRET}` 在 dev profile 无默认值
- `mvn test` 必须先本地 `export DB_PASSWORD=... SPT_APP_SECRET=...`
- "local-export = passing" 验收语义（同 Phase 3）
- CI hermetic 化（H2 或 test-profile）不在本期

`[VERIFIED: 当前 application-dev.yml:9-11 + Phase 3 03-VERIFICATION.md L14/46/98/102/127/139]`

## Wave Composition（实测文件计数与测序）

> 所有数字均经 `find ... | wc -l` 实测核查，与 CONTEXT.md 对照标差。

### Wave 1: basClient 数据载体（净新增 ~250 文件）

| 子包 | 源文件数 | 已内联（Phase 2/3） | 本期净新增 |
|------|---------|-------------------|-----------|
| `remote`（契约） | 239（238 top-level + 1 basTrade 子包 + 1 package-info） | **4**（IBsDictClient/IBsCompanyOurClient/IApproveWaitDealClient/IPmProcessClient） | **~234** ⚠ CONTEXT 误记 238 全内联 |
| `dto` | 2 | 0 | 2 |
| `util` | 6 | 0 | 6 |
| `common` | 3 | 0 | 3 |
| `riskScore` | 3 | 0 | 3 |
| `config` | 1（BasClientConfig） | 0 | 1 |
| `vo` | 293 | 293 | 0 |
| `constant` | 20 | 20 | 0 |
| `cache` | 3 | 3 | 0 |
| `entity` | 239 | 239 | 0 |
| **合计** | — | — | **~249** |

**Wave 1 排序：** 先抄 `remote`（最大体量）→ compile → 再抄 dto/util/common/riskScore/config → compile。

**Wave 内依赖：** `remote` 与 dto/util 无相互依赖，可并行；但 `config/BasClientConfig` 依赖 spt-tools 的 `LocalServerConfig`（Phase 2 已内联），可独立抄。

### Wave 2: service + 域子包 + infra + rocketmq + config（~615 文件）

**推荐拆 sub-wave**（避 Pitfall 5 gotcha 级联）：

| Sub-wave | 内容 | 文件数 | compile 依赖 |
|----------|------|--------|------------|
| **2a** infra 先行 | cache(7)+util(34)+enums(2)+annotation(1)+filter(6)+listener(1)+command(2)+event(1)+rocketmq(22)+config(6 保) | **~82** | 仅依赖 Phase 2 已内联的 spt-tools + Wave 1 |
| **2b** service + impl + 域子包 | service 顶-level(241)+impl(248)+ctr(9)+logistics(6)+performance(4)+rt(1)+stock(24) | **~533** | 依赖 2a infra + Wave 1 全部 + Phase 2 实体/Dao |

**Wave 2 总计：~615 文件**（CONTEXT "241" 严重低估，仅算顶-level service 接口）。

**Wave 2 排序：** 严格 2a → `mvn compile` 全模块零错 → 2b → `mvn compile`。

### Wave 3: api @RestController（224 文件，与 CONTEXT 一致）

| 子目录 | 文件数 |
|--------|--------|
| api 顶-level | 217 |
| api/basData | 1 |
| api/basTrade | 1 |
| api/fund | 2 |
| api/sign | 3 |
| api/logs | 0 |
| **合计** | **224** ✓ |

**Wave 内依赖：** 所有 api `extends BaseApi<Entity>` → 依赖 Wave 1（dto/vo）+ Wave 2（service/infra）。`MQApi`/`WorkTargetApi` 额外依赖 Wave 2 rocketmq。

**与 D-P4-01 修正方案的耦合：**
- 方案 A（Feign 自回环）：Wave 3 照搬后，BFF 通过 Feign 调到 Wave 3 暴露的端点——需解决 path 前缀（见 Pitfall 4）
- 方案 B（手写适配）：Wave 3 照搬 + 224 个 `implements I*Client` + `findPage` 桥接
- 方案 C（FactoryBean）：Wave 3 照搬 + 编程式适配配置

### Wave 4: web BFF（267 文件，与 CONTEXT 一致）

| 子集 | 文件数 |
|------|--------|
| web/controller 顶-level | 267 |
| 其中引用 `I*Client` 的 | 252 |
| 其中 import `com.spt.bas.client.remote` 的 | 243 |
| 直接引用 `api` 包的 | 0 |
| 直接引用 `service` 包的 | 0 |

**Wave 内依赖：** Wave 4 仅依赖 Wave 1（remote 契约）。`@Autowired I*Client` 在 Wave 3 完成 + D-P4-01 方案落地后可解析。

### Phase 4 总计

- **源侧净新增：~1356 文件**（Wave 1: 249 + Wave 2: 615 + Wave 3: 224 + Wave 4: 267 + Phase 2 已内联作基础）
- CONTEXT "~1900" 为**高估**（可能含 Phase 2 已抄的 entity 239 + Dao 229 + vo 293）

## Validation Architecture

> `workflow.nyquist_validation: true`（.planning/config.json 确认）—— 本节为 orchestrator 生成 04-VALIDATION.md 的依据。

### Test Framework

| Property | Value |
|----------|-------|
| Framework | JUnit 4 + spring-boot-starter-test 2.5.9（Spring Boot 2.5.x 默认带 JUnit Vintage + JUnit 5） |
| Config file | 无独立 `*config`——通过 `@SpringBootTest` + `application-dev.yml` profile |
| Quick run command | `JAVA_HOME=<Corretto-1.8> mvn -pl zgbas-admin -am test -Dtest=ZgbasApplicationTest -s /Users/alan/App/apache-maven-3.8.6/zg_settings.xml`（前置 `export DB_PASSWORD=... SPT_APP_SECRET=...`） |
| Full suite command | `JAVA_HOME=<Corretto-1.8> mvn -s /Users/alan/App/apache-maven-3.8.6/zg_settings.xml clean test`（同前置 export） |
| Compile-gate command | `JAVA_HOME=<Corretto-1.8> mvn -s /Users/alan/App/apache-maven-3.8.6/zg_settings.xml -pl <module> -am compile -q`；grep `^\[ERROR\]` locale 无关 |

### Phase Requirements → Test Map

| Req ID | Behavior | Test Type | Automated Command | File Exists? |
|--------|----------|-----------|-------------------|-------------|
| BIZ-01 | service + infra 迁入 zgbas-system 后 `mvn compile` 全模块零 ERROR | 编译门 | `mvn -pl zgbas-system -am compile -q` per Wave 2a/2b | ✅（Wave 2 任务自验） |
| BIZ-01 | 全 Spring context 启动（含 489 service + 82 infra + 224 api bean） | 启动验证 | `mvn -pl zgbas-admin -am test -Dtest=ZgbasApplicationTest#contextLoads` | ✅ 已存在 |
| BIZ-02 | 267 BFF controller 全部 bean 注册（无 `NoSuchBeanDefinitionException` for BFF controller） | 启动验证 | 扩 ZgbasApplicationTest 加 `getBean(MyXxxController.class)` 抽样 | ❌ Wave 0 加扩展 |
| BIZ-03（WR-02） | `I*Client` 经 HTTP 可达（接口级 `@GetMapping`/`@PostMapping` over HTTP 返 200/3xx/401） | 集成（启动验证 + HTTP reachability） | 扩 `ZgbasApplicationTest` 加 3-5 真实 `I*Client` 端点 GET/POST 断言 | ❌ Wave 0 加扩展 |
| D-P4-02 stub | stub-port 契约 `@Autowired(required=false)` null 守卫生效 | 启动验证 | ZgbasApplicationTest 启动期不抛 `NoSuchBeanDefinitionException`（required=false 吸收） | ✅ 隐式覆盖 |

### Sampling Rate

- **Per task commit：** 仅受影响模块的 `mvn compile -q`（< 30s）+ 关键启动测试
- **Per wave merge：** `mvn -am compile` 全模块零 ERROR（compile-gate 纪律）+ ZgbasApplicationTest 启动验证
- **Phase gate：** 全 ZgbasApplicationTest（含 WR-02 新增 HTTP proof）绿，+ `mvn compile` 全模块零 ERROR，+ D-P4-01/02/03/04/05/06 决策逐项核验

### Wave 0 Gaps

- [ ] `ZgbasApplicationTest` 扩展（3-5 个 `I*Client` 端点 HTTP reachability 断言）—— 覆盖 BIZ-03 / WR-02
- [ ] `ZgbasApplicationTest` 扩展（BFF controller bean 存在性抽样）—— 覆盖 BIZ-02
- [ ] `application-dev.yml` 加 `rocketmq.*` 配置（来源：源 `basServer/src/main/resources/application-dev.properties:22`）
- [ ] `application-prod.yml` 加 `rocketmq.name-server` 等占位（`${ROCKETMQ_NAMESERVER}` / `${ROCKETMQ_AK}` / `${ROCKETMQ_SK}`）
- [ ] **D-P4-01 修正方案决策**（方案 A/B/C 三选一）—— Wave 0 必须先锁

## Environment Availability

| Dependency | Required By | Available | Version | Fallback |
|------------|------------|-----------|---------|----------|
| JDK 1.8 | 编译/运行 | ✓（MEMORY：Corretto 1.8.0_482）| 1.8.0_482 | 无（硬约束） |
| Maven | 构建 | ✓ | apache-maven-3.8.6 | — |
| `zg_settings.xml` | 私服仓库重定向 | ✓ | /Users/alan/App/apache-maven-3.8.6/zg_settings.xml | — |
| 私服 SDK jar（auth/push/file/sign）| EXT-01..04 | ✓ | /Users/alan/App/Repository | — |
| MySQL `sptbasdb_pd`（dev 47.104.15.98:3306）| 启动验证（连真 DB） | ✓（dev profile 已配）| — | 无（非 hermetic，D-P3-13 Option 4） |
| `${DB_PASSWORD}` env | dev/prod profile | ✗（必须本地 export）| — | 无 |
| `${SPT_APP_SECRET}` env | 外部 spt-auth 鉴权 | ✗（必须本地 export）| — | 无 |
| 外部 spt-auth HTTP | 登录/菜单（D-P3-13 已认） | ✓（dev）| — | 无（验收不强求真实登录，留 Phase 7） |
| RocketMQ broker（dev 47.104.66.178:9876） | MQ 发送（启动不阻塞，懒连接） | ✓（dev）| — | 启动不依赖；首次 send 才用，本期验收不发消息 |

**Missing dependencies with no fallback:**
- 本地必须 export 的 `DB_PASSWORD` / `SPT_APP_SECRET`（非 hermetic 契约）

**Missing dependencies with fallback:**
- RocketMQ broker 可达性——本期验收不依赖（懒连接），broker 不可达不影响 context 启动

## Security Domain

> `security_enforcement` 未在 config.json 显式设；按"absent = enabled"，但本期为**纯源码照搬迁移**，无新增安全控制，沿用 Phase 2/3 既定 ASVS 控制。

### Applicable ASVS Categories

| ASVS Category | Applies | Standard Control |
|---------------|---------|------------------|
| V2 Authentication | no（本期不动 auth）| 沿用 Phase 3 Shiro |
| V3 Session Management | no | 沿用 Phase 3 Shiro session |
| V4 Access Control | yes（BFF 端点照搬）| Shiro 过滤器链（Phase 3 已就位），BFF 端点访问控制随 controller 照搬 |
| V5 Input Validation | yes | `@Valid` + BaseApi `@RequestBody` 沿用源实现（照搬） |
| V6 Cryptography | no | 沿用 Phase 2 |

### Known Threat Patterns for stack（Spring MVC + Feign + 照搬）

| Pattern | STRIDE | Standard Mitigation |
|---------|--------|---------------------|
| BFF 端点暴露（业务接口公网可达）| Information disclosure | Shiro 链（Phase 3 静态 `/**=user` + DB 动态链）随 controller 照搬生效 |
| Feign 自回环（方案 A）→ 反射性调用风险 | Tampering | 自回环仅 localhost；不暴露新攻击面；同进程鉴权沿用 Shiro |
| RocketMQ 凭证明文（源 application-prod.properties:35-36）| Info leak | 本期 D-P2-13 占位外置：`${ROCKETMQ_AK}` / `${ROCKETMQ_SK}` |

## State of the Art

| Old Approach | Current Approach | When Changed | Impact |
|--------------|------------------|--------------|--------|
| Spring Cloud 微服务 + Feign 跨进程 | 单体 + 进程内直调（或自回环） | zgbas-plus 项目立项（2026-07-16） | 业务行为等价；省部署复杂度 |
| xxl-job 任务调度 | RuoYi quartz | 本期不涉及 → Phase 6 | 无 |
| ddl-auto=validate（D-P2-02 原意）| ddl-auto=none（D-P4-05 偏离）| Phase 4 决策 | 全量 schema drift reconcile 延 tech debt |

**Deprecated/outdated for 本期：**
- `xxl-job` 相关代码（BasJobConfig + task 包 23 文件）：本期剔除，延 Phase 6
- `nacos` discovery：Phase 2 已删

## Assumptions Log

> 所有 `[ASSUMED]` 标签的 claim 汇总。Planner 与用户需在 Wave 0 前确认。

| # | Claim | Section | Risk if Wrong |
|---|-------|---------|---------------|
| A1 | `rocketmq-spring-boot-starter:2.2.2` 是 Apache 官方维护的安全包（来源 = 源项目 pom）| Standard Stack | 包未验证跨源；planner 可加 `checkpoint:human-verify` |
| A2 | rocketmq-spring-boot-starter 2.2.2 producer 启动期懒连接、不阻塞 context boot | D-P4-04 verdict | 若实际启动连 broker → 启动验证失败，需配 fallback 或 stub producer |
| A3 | 方案 A（Feign 自回环）的 path 前缀问题可用 yml `feign.client.config` 或单设 context-path 解决，不需改 238 契约 | D-P4-01 Pitfall 4 | 若 Feign path 不能去掉 `spt-bas-server` 前缀 → 必须改 238 契约的 `path=`（违反照搬） |
| A4 | PM 域 13 契约（PmApprove*/PmProcess*）按 D-P4-02 stub 降级即可（默认无 spt-auth 外部实现）| D-P4-02 Enumeration | 若 PM 实现在外部 spt-auth → 应按"外部 HTTP"对待而非 stub |
| A5 | ddl-auto=none 下，本期照搬的 489 service + 224 api 启动不会触发严重 schema drift（仅个别 fix-if-hit）| D-P4-05 | 实际可能在 MockMvc HTTP proof 阶段暴露大量映射错，需临时补 schema fix（仍属"修运行阻塞"，不越界） |
| A6 | ZgbasApplicationTest 用 TestRestTemplate 即满足 WR-02（无需引入 MockMvc）| Validation Architecture | 若需更精细的 controller-level 断言 → 引入 MockMvc + `@AutoConfigureMockMvc` |
| A7 | 252 个 BFF 引用 I*Client 的 @Autowired 能在方案 A 下解析（Feign proxy 满足）| D-P4-01 | 若解析失败 → 改方案 B 或 C |

## Open Questions

1. **D-P4-01 修正方案最终选择（A/B/C）**
   - What we know: 源 api 不 implements 契约；签名不兼容；路径前缀不一致
   - What's unclear: 用户是否能接受方案 A 的"同进程 HTTP 跳"（D-P4-01 原文要求无网络跳）
   - Recommendation: **强烈推荐方案 A**——与"行为等价优先 + 照搬"核心价值最契合；用户在 Wave 0 必须明确确认

2. **PM 域 13 契约的真实归属**
   - What we know: zgbas 源码 grep 不到 implements；PM 实体/vo 已 Phase 2 部分内联
   - What's unclear: 是否 spt-auth 外部提供？
   - Recommendation: Wave 1 默认 stub；若验收阶段发现 BFF 调用真依赖 PM 服务，则按外部 HTTP 接入（不 stub）

3. **Phase 2 "已内联 238 契约" 误记是否影响 PROJECT.md/STATE.md 其他记录**
   - What we know: CONTEXT.md `canonical_refs` L88 与 `code_context` L116 误记
   - What's unclear: 是否其他规划文档同样误记 → 影响估算
   - Recommendation: planner 启动 Wave 1 前先 grep `.planning/**` 中"238"修正记录

4. **path 前缀方案（与 Open Q1 耦合）**
   - What we know: 源 context-path=`/spt-bas-server`；单体根 `/`
   - What's unclear: 选 context-path 平移（破坏 Phase 3 Shiro `/login` 根路径）vs 改 Feign path（侵入 238 契约）
   - Recommendation: Wave 0 锁方案 A 时一并决策

5. **是否需拆 sub-wave for Wave 1**
   - What we know: Wave 1 ~249 文件，大部分是 remote 契约（机械活）
   - What's unclear: 一次抄 vs 分批
   - Recommendation: 不需拆——契约接口独立、无相互依赖，单 sub-wave 即可

## Sources

### Primary (HIGH confidence)

- 源码 grep / find / wc（`/Users/alan/WorkSpace/IDEA/zgbas` 分支 `feat-系统重构v5.0`）
  - `basCore/basServer/{api,service,service/impl,ctr,logistics,performance,rt,stock,cache,util,enums,annotation,filter,listener,command,event,rocketmq,config,task,dao}/` 文件计数
  - `basCore/basClient/{remote,dto,util,common,constant,riskScore,cache,config,vo,entity}/` 文件计数
  - `web/controller/` 计数 + import 关系
  - `spt-tools-data` 中 `BaseApi.java` / `BaseClient.java` / `PageDown.java` 签名核查
- 当前单体（`/Users/alan/WorkSpace/IDEA/zgbas-plus`）`find zgbas-system/src/main/java/com/spt/bas/client/remote/` → 仅 4 文件
- 当前单体 `ZgbasApplicationTest.java` + `InProcessContract*.java` 内容核查
- 当前单体 `.planning/phases/02-infrastructure/02-05-SUMMARY.md` 明确"只抄 2 个 feign 接口"的决策
- 源 `basCore/pom.xml:25` rocketmq-version=2.2.2
- 源 `basCore/basServer/src/main/resources/application.properties:2` context-path
- Phase 3 `.planning/phases/03-auth-homepage/03-VERIFICATION.md` 非 hermetic 契约

### Secondary (MEDIUM confidence)

- `.planning/phases/02-infrastructure/02-CONTEXT.md` D-P2-06/07/10/12 + 02-05 SUMMARY
- `.planning/phases/03-auth-homepage/03-CONTEXT.md` D-P3-10/13
- `.planning/REQUIREMENTS.md` / `ROADMAP.md` / `STATE.md` BIZ-01..03

### Tertiary (LOW confidence)

- 无（本期为纯 codebase investigation，无 WebSearch 推断）

## Metadata

**Confidence breakdown:**
- Standard stack: HIGH — 来源 = 源 pom + Phase 2 已锁栈
- Architecture: **HIGH（反向发现）** — D-P4-01 前提失实由源码 verbatim 核查确认（0/224 implements + PageDown 签名 + path 前缀），非推断
- Pitfalls: HIGH — 基于源码 grep + 当前单体实测 + Phase 1/2/3 历史教训
- Wave composition: HIGH — 所有数字经 find+wc 核查
- Verification assets: HIGH — 直接核查 ZgbasApplicationTest + 当前 pom
- D-P4-02 enumeration: MEDIUM — 25 项命名差集，PM 簇归属待二次确认（A4）
- D-P4-05 drift survey: MEDIUM — 轻量 grep，全量未做（按 D-P4-05 设计意图）

**Research date:** 2026-07-17
**Valid until:** 2026-08-16（30 天；本期属源码 verbatim 调查，技术栈与源码绑定，稳定）
