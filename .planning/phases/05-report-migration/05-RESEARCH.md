# Phase 5: 报表迁移 - Research

**Researched:** 2026-07-17
**Domain:** MyBatis 报表批量迁移 + Feign 自回环契约接通（沿用 Phase 4 D-P4-01/01a 范式）
**Confidence:** HIGH — 三项 verify-item 全部以源码 + 单体证据 ResolveClosed

## Summary

Phase 5 把源 `basReport`（分支 `feat-系统重构v5.0`）的 **53 套 mybatis 报表**（53 Mapper + 53 XML + 53 service iface + 53 service impl）+ **54 前端直连 `@RestController`** + **reportClient 265 java 数据载体 / Feign 契约**迁入单体，落位 `zgbas-system`（Mapper/XML/service/reportClient）+ `zgbas-admin`（54 api）。**所有源码 verbatim 保包名搬运**（D-P2-07），与 Phase 4 basServer/basClient 处理同构。mybatis 配置 99% 复用 Phase 2 基线（仅追加 type-aliases-package 与 self-loopback url 两个键），`@MapperScan` 放宽一行，`@EnableFeignClients` 已在 Phase 4 04-05 放宽无需再动。

**核心研究结论：D-P5-03 path 契约机制已 conclusive 解答 —— Feign 契约路径（`path = "spt-bas-report" + "/rpt/..."`）与 54 api 路径（`@RequestMapping("/rpt/...")`）DIVERGE `/spt-bas-report/` 前缀，并且单体 admin 已有 **14 个 BFF controller（Phase 4 已迁）**与 report api 路径字面冲突（`/rpt/fundReceivableStatistics`、`/business/manager/workbench`、`/bs/company` 等），**verbatim 照搬必触发 `AmbiguousMappingException`**。**必须**复用 Phase 4 D-P4-01a Wave 4 范式：`WebMvcConfigurer.addPathPrefix("/spt-bas-report", HandlerTypePredicate.forBasePackage("com.spt.bas.report.server.api"))`（在 `zgbas-system` 新建 `ReportFeignPathConfig`，仿 `BasFeignPathConfig`）。加前缀后 Feign 契约路径天然满足，54 api 照搬即接通 9 个 basServer + 多个 BFF controller 的 `IRpt*Client` 真实调用，**无需 checkpoint:human-verify**。

其余两项 verify-item 同样已闭合：`@MapperScan` 当前覆盖 `com.spt.bas.system.dao` 单串 → 改为双串数组 `{"com.spt.bas.system.dao", "com.spt.bas.report.server.dao"}`；53 report 实体 **0 个**带 `javax.persistence.@Entity`（唯一带 `@Table*` 前缀的 `RptWxBrandFollow` 用的是 mybatis-plus `@TableName/@TableId/@TableField`，非 JPA），**无 JPA 误拾风险**，D-P5-07 fail-fast 担忧已解除。

**Primary recommendation:** 5-wave 推进 —— Wave 0 接线（reportClient 内联 + yml 两键 + @MapperScan 放宽 + ReportFeignPathConfig + 移除 report-client jar 依赖）；Wave 1-3 按业务域分批迁 53 套（合同 15 / 资金财务 7 / 业务总览 3 / 风控 3 / 库存 4 / 申请 3 / 业绩人效 4 / 销售区域 3 / 公司供应商 4 / 微信 2 / 发票未开 2 / 其它 6）；Wave 4 迁 54 api + BFF stub-port 升级（D-P5-03 解除 D-P4-02）+ 启动抽样 proof。

## Architectural Responsibility Map

| Capability | Primary Tier | Secondary Tier | Rationale |
|------------|-------------|----------------|-----------|
| 53 mybatis Mapper 接口 + 53 XML 复杂查询 | zgbas-system (`com.spt.bas.report.server.dao` + `resources/mybatis/mappers/`) | — | 持久层，与 Phase 2 SampleMapper 同位（mapper-locations 已覆盖） |
| 53 service iface + 53 impl（业务计算 / 分页包装） | zgbas-system (`com.spt.bas.report.server.service{,.impl}`) | — | 业务逻辑层，`@Autowired` Mapper + cross-service refs |
| reportClient 265 java（entity/vo/remote/constant/payload/utils/config） | zgbas-system (`com.spt.bas.report.client.*`) | — | 数据载体 + Feign 契约；与 Phase 4 basClient 同构（D-P2-07） |
| 54 前端直连 `@RestController`（`/rpt/...` 等多元路径） | zgbas-admin (`com.spt.bas.report.server.api`) | — | edge controller，`@Autowired` system 的 report service，模板同模块（D-P5-01） |
| mybatis 配置（type-aliases / mapper-locations） | zgbas-admin (`application.yml`) | — | 已配 mapper-locations，仅需追加 type-aliases |
| `@MapperScan` 放宽 | zgbas-framework (`ZgbasMybatisConfig`) | — | 单一来源（WR-01） |
| path-prefix `/spt-bas-report` 注入 | zgbas-system (`ReportFeignPathConfig`，仿 `BasFeignPathConfig`) | — | 解 D-P5-03 + 防 14 处 AmbiguousMappingException |
| Feign 契约自回环（9 svc refs + 多 BFF refs） | zgbas-admin (`@EnableFeignClients` 已放宽) | application-dev.yml `spt.bas.report.url` | D-P4-01 方案 A 报表侧落地 |
| `IRpt*Client` 9 个 basServer service impl 调用方 | zgbas-system (`com.spt.bas.server.*`) | — | Phase 4 已迁；本期接通后真实调用替换自回环 404 |
| `IRpt*Client` 多个 BFF controller 调用方（MyIndexController 等） | zgbas-admin (`com.spt.bas.web.controller.*`) | — | Phase 3 因 report 缺口 deferred 的 MyIndexController 关闭 |
| Excel 导出（仅 1 处 `mergeChainExport`） | 随 report service 照搬 | — | CONTEXT.md 关于"大量 exportExcel"的描述与源不符（详见正文） |

<user_constraints>
## User Constraints (from CONTEXT.md)

### Locked Decisions

**架构与落位**
- **D-P5-01**: 报表 Controller 落 `zgbas-admin`（按 ROADMAP 字面；54 api 是前端直连 edge controller，非 Feign 服务 API）。
- **D-P5-02**: reportClient 数据载体（265 java）**verbatim 内联源码**进 `zgbas-system`（保包名 `com.spt.bas.report.client.*`，对齐 Phase 4 basClient Wave 1 + D-P2-07），消除 Phase 4 暂留的 `report-client` 私服 jar 依赖。

**Feign 契约接线**
- **D-P5-03**: report Feign 契约本期接通真实调用（解除 D-P4-02 stub 降级）；机制留 research 核实 —— **本研究已确认：路径 diverge `/spt-bas-report/` 前缀，必须复用 Phase 4 D-P4-01a Wave 4 `addPathPrefix` 范式**（无需 checkpoint）。

**迁移测序**
- **D-P5-04**: 按业务域分批 wave + 逐层 compile + 抽样 proof 绿灯。

**行为保真**
- **D-P5-05**: Excel 导出全量保行为等价（**本研究修正：仅 1 处 `mergeChainExport`，非"大量"**，随 service 照搬即等价）。
- **D-P5-06**: 分页正确性 1:1 照搬等价（性能不优化）。

**mybatis 配置 + 风险验证**
- **D-P5-07**: mybatis 配置合并 + @MapperScan/entity 显式验证 —— **本研究已闭合两项**：`@MapperScan` 需放宽（具体方案见正文），report 实体 0 个 `@Entity` 误拾。
- **D-P5-08**: 启动 + 抽样报表查询 proof（非 hermetic，dev 明文密钥，无需 export DB_PASSWORD/SPT_APP_SECRET）。

### Claude's Discretion

- **HTTP 路径**：`/rpt/...` 等多元路径在单体根暴露，`/spt-bas-report` context-path 蒸发（**但 `addPathPrefix` 必须保留此前缀作为命名空间隔离，防 14 处冲突** —— 路径对前端不可见，但内部必须保留）。
- **报表 service 事务标注**：reports 读为主，沿用 Phase 2 `JpaTransactionManager @Primary` + 单 DataSource，`@Transactional` 标注随源照搬。
- **reportServer infra 筛选**：`util`（2 实际：MyBigDecimalUtils + ReportCalculateUtil）/`config`（6 个，**绝大多数 SKIP，详见正文**）/`task`/`command`/`listener` 排除或评估。
- **报表 Mapper XML 动态 SQL / `${}`**：照搬即等价，不改写。

### Deferred Ideas (OUT OF SCOPE)

- **xxl-job reportServer/task（1）+ command（1）+ listener（1）** → Phase 6（QUARTZ-03）。
- **真实报表/业务回归对照 + 浏览器 e2e** → Phase 7（ALIGN-01/02）。
- **报表物理分页性能改造** → 永久 Out-of-Scope（PROJECT.md，259 表结构变更）。
- **basWx 采购业务 + purchase 契约** → v2（#14，purchase 契约维持 D-P4-02 stub 降级）。
- **全量实体 schema drift reconcile + 重开 ddl-auto=validate** → tech debt。
- **CR-01 真实轮换已泄漏生产库密码** → 跨阶段安全债。
</user_constraints>

<phase_requirements>
## Phase Requirements

| ID | Description | Research Support |
|----|-------------|------------------|
| **REPORT-01** | 53 套报表 Mapper + XML 迁入 `zgbas-system` 报表包 | §Standard Stack（mybatis-plus 2.x 已就位）+ §Architecture Patterns（同位 drop）+ §Code Examples（@MapperScan 放宽 + XML namespace 验证）+ §Validation Architecture（mapper bean 全解析抽样 proof） |
| **REPORT-02** | 报表 Controller 迁入 `zgbas-admin`，查询行为与旧系统等价（分页性能另行评估） | §Architecture Patterns（D-P5-03 path-prefix 范式）+ §Common Pitfalls（14 BFF 路径冲突）+ §Code Examples（ReportFeignPathConfig 模板）+ §Validation Architecture（HTTP reachability proof） |
| **PERSIST-02** | mybatis 复杂报表查询可用，迁 53 Mapper+XML | §Validation Architecture（抽样跑 2-3 个 findPage 返回非空 + Page shape 正确）+ §Common Pitfalls（namespace / typeAliases / cross-mapper refs） |
</phase_requirements>

## Standard Stack

### Core

| Library | Version | Purpose | Why Standard |
|---------|---------|---------|--------------|
| mybatis-plus-boot-starter | (Phase 2 已内联 spt-tools-mybatis) | ORM 报表侧 | `PaginationInterceptor` bean 已在 `ZgbasMybatisConfig` 注册；`@MyBatisDao` 注解扫描机制就位 |
| MyBatis 3.x mapper XML | 3.x（随 spt-tools-mybatis） | 复杂 SQL 报表 | 53 XML `namespace=com.spt.bas.report.server.dao.Rpt*Mapper` 与 dao 接口 verbatim 对齐 |
| Spring Data Commons (Page / Pageable / PageImpl) | Spring Boot 2.5.9 自带 | 分页结果包装 | 源 service impl 已用 `new PageImpl<>(list, pageable, count)` —— 内存包装（非 SQL 分页），与 PROJECT.md「PageHelper 是结果包装器」一致 [VERIFIED: 源码 RptFundReceivableStatisticsServiceImpl.java] |
| Spring Cloud OpenFeign | (Phase 4 已接线) | Feign 自回环契约 | `@FeignClient(name=..., path="spt-bas-report"+..., url="#{reportServerConfig.url}", configuration=FeignConfig.class)` —— Phase 4 04-05 已放宽扫描 [VERIFIED: ZgbasApplication.java] |

### Supporting

| Library | Version | Purpose | When to Use |
|---------|---------|---------|-------------|
| hutool 5.5.9 | (Phase 2 已就位) | 通用工具（service impl 中 `CollUtil` / `DateUtil`） | report service impl 已 import（如 RptCtrContractReportServiceImpl） |
| commons-collections / commons-lang3 | (Phase 2 已就位) | `CollectionUtils` / `StringUtils` | report service impl verbatim 引用 |
| jackson | Spring Boot 自带 | JSON 序列化 / `TypeReference` | report service impl verbatim 引用 |

### Alternatives Considered

| Instead of | Could Use | Tradeoff |
|------------|-----------|----------|
| 自回环 path-prefix（`/spt-bas-report`） | 改 Feign 契约去掉 `SERVER_NAME +` 前缀 | **禁用** —— 改契约会破坏与外部 basWx 等其它消费方的等价性；且 14 处 BFF 路径冲突无解 |
| 自回环 path-prefix | 单体设 `server.servlet.context-path=/spt-bas-report` | **禁用** —— 破坏 Phase 3 AUTH-03 Shiro 根路径链（同 Phase 4 D-P4-01a 否决理由） |
| 一次性全量批迁 53 套 | 按业务域分批 wave | **D-P5-04 锁定后者** —— 控 namespace / 类型依赖级联（Phase 1 gotcha 教训） |

**Installation:** 无新依赖。Phase 4 04-04 Rule 3 cascade pom 已声明 `report-client:2.0.1-SNAPSHOT`（types-only dep in `zgbas-system/pom.xml:139`），**本期 Wave 0 内联 reportClient 源码后必须从 pom 移除此 jar 依赖**（D-P5-02 消除私服依赖原意）。

**Version verification:** 不适用（无新增包）。mybatis-plus 版本由 Phase 2 spt-tools-mybatis 内联锁定。

## Package Legitimacy Audit

> 本期不安装任何外部包 —— reportClient 265 java 是**源码内联**（D-P5-02），非 jar 依赖。Phase 4 已声明的 `report-client:2.0.1-SNAPSHOT` jar 在 Wave 0 被**移除**（替换为内联源码）。

| Package | Registry | Action | Reason |
|---------|----------|--------|--------|
| `report-client:2.0.1-SNAPSHOT` | 公司私服 | **REMOVE** from `zgbas-system/pom.xml:139` | 265 java 内联后此 types-only dep 已无意义（D-P5-02） |

无 slopcheck 调用需要（无私服/公服包新增）。

## Architecture Patterns

### System Architecture Diagram

```
                     Phase 5 — Report Migration Data Flow

  [Browser / Thymeleaf report templates]                         (Phase 3/4 已就位)
              │  POST /rpt/fundReceivableStatistics/findPage
              │  POST /business/overview/api/findBusinessOverviewList
              │  POST /ctr/contractReport/findNotDeliveryInPage
              │  POST /risk/report/getMatchUserList
              │  ... (49 distinct top-level paths across 54 api)
              ▼
  ┌─────────────────────────────────────────────────────────────────┐
  │ zgbas-admin (Phase 5 Wave 4 — 54 @RestController)              │
  │   com.spt.bas.report.server.api.Rpt*Api                        │
  │                                                                │
  │   ⚠ ReportFeignPathConfig (NEW in zgbas-system, Wave 0)        │
  │      addPathPrefix("/spt-bas-report",                          │
  │        forBasePackage("com.spt.bas.report.server.api"))        │
  │      ⇒ all 54 api paths get prefixed                           │
  │      ⇒ /rpt/foo → /spt-bas-report/rpt/foo                      │
  │      ⇒ resolves 14 BFF path collisions + matches Feign         │
  │         contract paths naturally                               │
  └─────────────────────────────────────────────────────────────────┘
              │ @Autowired report service
              ▼
  ┌─────────────────────────────────────────────────────────────────┐
  │ zgbas-system (Phase 5 Wave 1-3 — 53 service iface + 53 impl)  │
  │   com.spt.bas.report.server.service.IRpt*Service               │
  │   com.spt.bas.report.server.service.impl.Rpt*ServiceImpl      │
  │                                                                │
  │   cross-service refs: RptCtrContractReportServiceImpl uses     │
  │   IRptSummaryRoiService + IRptUserRoiService (within-system)   │
  └─────────────────────────────────────────────────────────────────┘
              │ @Autowired Mapper
              ▼
  ┌─────────────────────────────────────────────────────────────────┐
  │ zgbas-system (Phase 5 Wave 1-3 — 53 Mapper iface)              │
  │   com.spt.bas.report.server.dao.Rpt*Mapper @MyBatisDao         │
  │                                                                │
  │   @MapperScan broadened (Wave 0):                              │
  │     basePackages = {                                           │
  │       "com.spt.bas.system.dao",   // Phase 2 SampleMapper      │
  │       "com.spt.bas.report.server.dao"  // ← NEW                │
  │     }, annotationClass = MyBatisDao.class                       │
  └─────────────────────────────────────────────────────────────────┘
              │ mybatis binding
              ▼
  ┌─────────────────────────────────────────────────────────────────┐
  │ zgbas-system/src/main/resources/mybatis/mappers/ (53 XML)      │
  │   Rpt*Mapper.xml  namespace=com.spt.bas.report.server.dao.*    │
  │   resultType → com.spt.bas.report.client.entity.* (83 entity)  │
  │   parameterType → com.spt.bas.report.client.vo.* (119 vo)      │
  │   ↓ also refs Phase 4 assets:                                  │
  │   com.spt.bas.client.entity.{CtrContract, BsCompanyCredit, ...} │
  │   com.spt.bas.client.vo.{ContractShowVo, ...}                  │
  └─────────────────────────────────────────────────────────────────┘
              │ same @Primary DataSource as JPA
              ▼
        MySQL sptbasdb_pd (Phase 2 @Primary Druid)


  Feign Self-Loopback Side Channel (D-P4-01 方案 A 报表侧落地)
  ════════════════════════════════════════════════════════════
  Phase 4 already ported:
    [BFF controllers in zgbas-admin]  ──┐
      MyIndexController (Phase 3 P5-deferred) │
      BusinessOverviewController             │ @Autowired IRpt*Client
      BusinessManagerWorkbenchController     │ (Feign proxy)
      WorkBenchCache                         │
      + 9 basServer service impls in system  │
        (CtrContractUpdateServiceImpl,       │
         BsCompanyApi, WeChatWorkServiceImpl,│
         ApplyDeliveryOutServiceImpl,        │
         RptBaseCostServiceImpl, ...)        │
                                       ▼
  Phase 4 04-05 already widened:
    @EnableFeignClients(... "com.spt.bas.report.client.remote")  ← proxies generated
                                                                  (lazy url resolution)
                                       │
                                       │  url = "#{reportServerConfig.url}"
                                       │      = http://localhost:8080 (Wave 0 adds key)
                                       │  path = "spt-bas-report" + "/rpt/..."
                                       │
                                       ▼
  Phase 5 Wave 4 — 54 @RestController prefixed with /spt-bas-report
    ⇒ proxy calls land on real impl (no more 404)
    ⇒ 9 basServer services + BFF controllers' report calls become real
```

### Recommended Project Structure

```
zgbas-system/
├── src/main/java/com/spt/
│   ├── bas/report/                           # ← NEW (Phase 5 verbatim)
│   │   ├── client/                            # reportClient 265 java inline
│   │   │   ├── config/ReportClientConfig.java          # already component-scanned
│   │   │   ├── constant/{ReportConstant,WorkbenchLabelEnum}.java
│   │   │   ├── entity/      (83 entity POJOs)           # ZERO @Entity (verified)
│   │   │   ├── payload/     (OverdueCompany, FinanceStatics, MatchUser)
│   │   │   ├── remote/      (54 IRpt*Client + 1 package-info)
│   │   │   ├── utils/       (PageHelper, DateUtils)
│   │   │   └── vo/          (119 vo)
│   │   └── server/                           # reportServer selected subdirs
│   │       ├── dao/         (53 Rpt*Mapper + 1 package-info)
│   │       ├── service/     (53 IRpt*Service iface + 1 package-info)
│   │       │   └── impl/    (53 Rpt*ServiceImpl + 1 package-info)
│   │       └── util/        (MyBigDecimalUtils + ReportCalculateUtil, 2 of 3)
│   ├── bas/client/config/
│   │   ├── BasFeignPathConfig.java          # Phase 4 precedent
│   │   └── ReportFeignPathConfig.java       # ← NEW (Wave 0)
│   └── ... (existing Phase 2/3/4 assets)
├── src/main/resources/mybatis/mappers/
│   ├── SampleMapper.xml                     # Phase 2
│   └── Rpt*Mapper.xml                       # ← NEW 53 files (Wave 1-3)
└── pom.xml                                  # remove report-client dep (Wave 0)

zgbas-framework/src/main/java/com/spt/framework/config/
└── ZgbasMybatisConfig.java                  # ← EDIT @MapperScan (Wave 0)

zgbas-admin/
├── src/main/java/com/spt/bas/report/server/api/   # ← NEW 54 @RestController (Wave 4)
└── src/main/resources/
    ├── application.yml                      # ← EDIT type-aliases-package (Wave 0)
    └── application-dev.yml                  # ← EDIT add spt.bas.report.url (Wave 0)
```

### Pattern 1: D-P5-03 Path-Prefix Solution（仿 BasFeignPathConfig）

**What:** Phase 4 D-P4-01a Wave 4 范式：用 `WebMvcConfigurer.configurePathMatch` + `PathMatchConfigurer.addPathPrefix(prefix, HandlerTypePredicate.forBasePackage(...))` 给 api 包的 `@RequestMapping` 加前缀。

**When to use:** 源微服务有 `server.servlet.context-path=/spt-bas-XXX`，单体根 `/` 不能设 context-path（保 Phase 3 Shiro 根），且 Feign 契约 `path = SERVER_NAME + "/..."`。

**Why mandatory here:** 单体 admin 已有 **14 处 BFF controller**路径与 report api 字面冲突（见 §Common Pitfalls #1）—— 不加前缀必 `AmbiguousMappingException`。

**Example:**
```java
// Source: zgbas-system/src/main/java/com/spt/bas/client/config/BasFeignPathConfig.java (Phase 4)
// Target (NEW): zgbas-system/src/main/java/com/spt/bas/client/config/ReportFeignPathConfig.java
package com.spt.bas.client.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerTypePredicate;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * D-P5-03 path-prefix for report Feign self-loopback. Mirrors Phase 4 BasFeignPathConfig
 * verbatim. Adds "/spt-bas-report" prefix to all 54 @RequestMapping paths in
 * com.spt.bas.report.server.api so that:
 *   (1) Feign contract path = "spt-bas-report" + "/rpt/..." resolves to the ported api
 *       (D-P4-01 方案 A self-loopback closes for report contracts).
 *   (2) 14 BFF controllers in zgbas-admin that share literal paths with report api
 *       (e.g. /rpt/fundReceivableStatistics, /business/manager/workbench, /bs/company)
 *       no longer collide — report api is namespaced under /spt-bas-report/*, BFF stays at root.
 *
 * Source ReportServer had server.servlet.context-path=/spt-bas-report; the monolith's root "/"
 * must stay un-prefixed (Phase 3 AUTH-03 Shiro root filter chain).
 *
 * Scope: ONLY @RestController classes whose package starts with
 * com.spt.bas.report.server.api. basServer api (com.spt.bas.server.api) and PM api
 * (com.spt.pm.api) keep their own /spt-bas-server prefix via BasFeignPathConfig.
 */
@Configuration
public class ReportFeignPathConfig implements WebMvcConfigurer {

    private static final String API_PATH_PREFIX = "/spt-bas-report";

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.addPathPrefix(API_PATH_PREFIX,
            HandlerTypePredicate.forBasePackage("com.spt.bas.report.server.api"));
    }
}
```

### Pattern 2: mybatis 配置合并（D-P5-07 part 1）

**What:** 99% 复用 Phase 2 基线，仅追加两个键。

**Example:**
```yaml
# zgbas-admin/src/main/resources/application.yml (Wave 0 edit)
mybatis-plus:
  mapper-locations: classpath:/mybatis/mappers/*Mapper.xml   # unchanged (Phase 2)
  type-aliases-package: >-                                    # ← EDIT (append report packages)
    com.spt.bas.client.entity,
    com.spt.bas.report.client.entity,
    com.spt.bas.report.client.vo

# zgbas-admin/src/main/resources/application-dev.yml (Wave 0 edit)
spt:
  bas:
    server:
      url: http://localhost:8080          # Phase 4 precedent
    report:                                # ← NEW
      url: http://localhost:8080           # self-loopback (D-P4-01 方案 A 报表侧)
```

**Drop:** 源 `reportServer/src/main/resources/mybatis/myBatisConfig.xml`（仅 `logImpl=STDOUT` / `lazyLoadingEnabled=false`，typeAliases 全注释）—— **丢弃**，依赖 Phase 2 mybatis-plus auto-config（D-P2-09）。源 `application.properties` 中的 `server.port/context-path/xxl.job.*/spring.redis.*/nacos.discovery.*` 全部丢弃。

### Pattern 3: @MapperScan 放宽（D-P5-07 part 2）

**What:** 当前 `@MapperScan(basePackages = "com.spt.bas.system.dao", annotationClass = MyBatisDao.class)` 单串；改为数组加 `com.spt.bas.report.server.dao`。

**Why minimal broaden:** 报表 53 Mapper 全部 `@MyBatisDao`（grep 计数 53/53，[VERIFIED: 源码]），与 SampleMapper 同 annotationClass。

**Example:**
```java
// zgbas-framework/src/main/java/com/spt/framework/config/ZgbasMybatisConfig.java
@Configuration
@MapperScan(
    basePackages = {
        "com.spt.bas.system.dao",            // Phase 2 (SampleMapper)
        "com.spt.bas.report.server.dao"      // ← NEW Phase 5 (53 Rpt*Mapper)
    },
    annotationClass = MyBatisDao.class
)
public class ZgbasMybatisConfig { ... }
```

### Pattern 4: reportClient 内联 + 移除 jar 依赖（D-P5-02）

**What:** `reportClient/src/main/java/com/spt/bas/report/client/{entity,vo,remote,config,constant,payload,utils}/` 265 java 全部 verbatim 复制到 `zgbas-system/src/main/java/com/spt/bas/report/client/`（保包名）。同时 `zgbas-system/pom.xml:139` 移除 `<artifactId>report-client</artifactId>` 依赖块。

**Why verbatim:** 对齐 Phase 4 basClient Wave 1（D-P2-07 照搬保包名），与已内联的 basClient 同构。53 XML 的 `resultType`/`parameterType` 全部引用 `com.spt.bas.report.client.{entity,vo}.*` FQN —— 内联后即解析。

### Pattern 5: reportServer infra 筛选照搬（D-P5-04 Claude's Discretion）

| 子目录 | 源计数 | 处置 | 理由 |
|--------|--------|------|------|
| `dao/` | 53 Rpt*Mapper + 1 pkg-info | **全迁** | Phase 5 核心 |
| `service/` iface | 53 IRpt*Service + 1 pkg-info | **全迁** | Phase 5 核心 |
| `service/impl/` | 52 Rpt*ServiceImpl + 1 RptWxBrandFollowService + 1 pkg-info | **全迁** | 注意 1 个命名不跟 ServiceImpl 后缀（RptWxBrandFollowService.java 是 impl 不是 iface，文件位置在 impl/） |
| `api/` | 54 Rpt*Api + 1 pkg-info | **全迁 zgbas-admin** | Phase 5 核心（D-P5-01） |
| `util/` | MyBigDecimalUtils + ReportCalculateUtil + 1 pkg-info | **迁 2** | service impl 中 `ReportCalculateUtil` 被 `RptCtrContractReportServiceImpl` 等引用（[VERIFIED: 源码 import]）—— 运行依赖 |
| `vo/` | 1 pkg-info | **SKIP** | 空目录，仅 package-info |
| `config/WebAppConfig` | 1 | **SKIP** | Phase 4 已迁的 `com.spt.bas.server.config.WebAppConfig` 几乎相同（同作者 wlddh，同 `extends WebMvcConfigurerAdapter`，同 PageInterceptor + `/` → `/index` redirect）—— 重复 bean |
| `config/FrameworkConfig` | 1 | **SKIP** | beans 全部冲突或无用：`authOpenFacade` 重复 Phase 2、`DataSourceConfig(report.datasource)` 前缀单体不存在、`H2KeyGenerator` 不适用、`restTemplateUrl`/`remoteService` 已在 Phase 2 接 |
| `config/TransactionConfig` | 1 | **SKIP** | 整文件**注释掉**（[VERIFIED: 源码头 40 行]） |
| `config/ScheduleConfig` | 1 | **SKIP** | @EnableScheduling + 100-thread 池，但 reportServer **0 个 `@Scheduled` 方法**（xxl-job only，Phase 6） |
| `config/BasReportJobConfig` | 1 | **SKIP → Phase 6** | xxl-job executor bean |
| `config/BasicErrorController` | 1 | **SKIP**（或加 excludeFilter） | 与已 exclude 的 3 个 BasicErrorController（spt-tools/basServer/web）同 simple-name 冲突；建议 `ZgbasApplication.excludeFilters` 加 `com.spt.bas.report.server.config.BasicErrorController.class`（同 Phase 4 Rule 3 范式） |
| `task/` | 1 pkg-info | **SKIP → Phase 6** | 空目录 + xxl-job handler 归 Phase 6 |
| `command/ReportCommandExecutor` | 1 | **SKIP → Phase 6** | `@XxlJob` handler |
| `listener/ApplicationStartup` | 1 | **SKIP → Phase 6** | `@Autowired CommandExecutor executor` 依赖 `ReportCommandExecutor`（Phase 6）—— 若本期迁则 `NoSuchBeanDefinitionException: CommandExecutor`（spt-tools-core 接口，源唯一 impl 在 command/） |

**net：reportServer 迁入 = 53 dao + 53 service iface + 53 service impl + 2 util + 0 config = 161 java（不含 api）；reportClient 迁入 = 265 java；api 迁入 admin = 54 java。XML = 53。**

### Pattern 6: Wave 测序建议（D-P5-04 domain batching）

按业务域 mapper family 切片（依源文件名前缀），每 wave 后 `mvn compile` + 抽样 proof 绿灯：

| Wave | 内容 | Mapper 数 | 备注 |
|------|------|-----------|------|
| **W0** | reportClient 265 java 内联 + yml 2 键 + @MapperScan 放宽 + `ReportFeignPathConfig` + pom 移除 report-client jar | 0 | 接线 wave，无 Mapper；mvn compile 全绿（reportClient 是 POJO/contract，无外部 dep） |
| **W1** | 小批低风险域：Wx/WeChat (2) + Stock (4) + Company/Supplier (4) = 10 mapper + 对应 service + XML | 10 | 几乎无 cross-service dep |
| **W2** | 中批：Business* (3) + Apply* (3) + NotStatistics (2) + Risk (3) = 11 mapper + service + XML | 11 | 内部 cross-service 引用可控 |
| **W3** | 中批：Fund/Finance/Invoice/Budget/BaseCost/Profit (7) + Evaluate/Person/UserRoi/Summary (4) + Sales/Region/Province (3) = 14 mapper | 14 | 必须先迁 Summary/UserRoi（W3 内排序），因 W4 Ctr* 依赖它们 |
| **W4** | 最大域：Ctr* family (15) + Fact/Pm/Index/Print/Credit/WxCtr (~6) = 21 mapper | 21 | 含 `RptCtrContractReportServiceImpl`（依赖 IRptSummaryRoiService + IRptUserRoiService）+ RptCtrContractApi（无自己的 mapper，复用其它 service） |
| **W5** | 54 api controllers 落 zgbas-admin + `BasicErrorController` excludeFilter 追加 | 0 | mvn compile + path-prefix 生效；启动期 54 bean 解析 |
| **W6** | BFF stub-port 升级（D-P5-03 关闭 D-P4-02 report 降级）+ 启动抽样 proof | 0 | D-P5-08 验收 |

**Wave 数可由 planner 调整（合并/拆分），但顺序约束：W0 必先 → W3 必须先于 W4（service 依赖）→ W5 必须最后（api 依赖 service）→ W6 验收。**

### Anti-Patterns to Avoid

- **一次性全量迁 53 套** —— Phase 1 gotcha 级联教训；按域分批（D-P5-04 锁定）。
- **改 Feign 契约去前缀** —— 破坏与其它消费方等价性；14 处 BFF 冲突无解。
- **迁 reportServer/config 整目录** —— 6 个 config 几乎全 SKIP（重复 bean / 已注释 / xxl-job / Spring scheduling 无方法）。
- **迁 listener/ApplicationStartup 但不迁 command** —— 启动期 `NoSuchBeanDefinitionException: CommandExecutor`。
- **设 `server.servlet.context-path=/spt-bas-report`** —— 破坏 Phase 3 Shiro 根路径（同 Phase 4 否决理由）。
- **改 mapper XML 的 `${}` / 动态 SQL** —— 行为等价优先，安全改造非本期。
- **分页性能优化** —— 永久 Out-of-Scope（PROJECT.md），仅保正确性 1:1。

## Don't Hand-Roll

| Problem | Don't Build | Use Instead | Why |
|---------|-------------|-------------|-----|
| 报表 path-prefix 命名空间 | 自写 RequestInterceptor strip/add | `WebMvcConfigurer.configurePathMatch` + `addPathPrefix` + `HandlerTypePredicate.forBasePackage` | Phase 4 BasFeignPathConfig 已实证（D-P4-01a Wave 4 rewrite）；strip-interceptor 旧方案曾导致 AmbiguousMappingException |
| 分页结果包装 | 自写 PageHelper | `new PageImpl<>(list, PageRequest.of(page-1, rows), count)` | 源 service impl verbatim 用法；Spring Data Commons 自带 |
| mybatis 配置 | 自写 SqlSessionFactory @Bean | mybatis-plus-boot-starter auto-config | Phase 2 D-P2-09 已锁定；@Primary DataSource 自动绑定 |
| Excel 导出工具 | 自写 POI 包装 | spt-tools Excel util（Phase 2 已内联） | 仅 1 处 `mergeChainExport` 引用，无新工具 |
| @MapperScan 自写包扫描 | — | `@MapperScan(basePackages={...}, annotationClass=MyBatisDao.class)` | mybatis-spring 标准；放宽一行即可 |
| Feign 自回环 URL 解析 | — | `url = "#{reportServerConfig.url}"` SpEL + `LocalServerConfig` bean（已在 reportClient/config） | Phase 4 范式；ReportClientConfig bean 已 component-scanned |

**Key insight:** Phase 5 是**搬运**而非重造 —— 99% 工作量是 verbatim copy + 3 处配置编辑（yml 2 键 + @MapperScan 数组 + path-prefix class）。与项目核心价值「搬运而非重造 + 行为对齐旧系统」完全一致。

## Common Pitfalls

### Pitfall 1: 14 处 BFF controller 与 report api 路径字面冲突（CRITICAL）

**What goes wrong:** 单体 admin 已有 14 个 Phase 4 迁入的 BFF controller 与本期 report api 字面共享 `@RequestMapping` 顶层路径 → Spring 启动期 `AmbiguousMappingException`（同 Phase 4 D-P4-01a Wave 4 教训）。

**Conflict list [VERIFIED: grep 单体 zgbas-admin + 源 reportServer/api 260 个 BFF path ∩ 49 个 report api path]:**
- `/bs/company`、`/budget/settlementTotal`、`/business/manager/workbench`、`/evaluate/total`、`/person/cost/chart`、`/region/month/sales`
- `/rpt/business`、`/rpt/companyReceivables`、`/rpt/contractFinance`、`/rpt/fundReceivableStatistics`、`/rpt/invoiceBill`、`/rpt/invoiceBillStatistics`、`/rpt/notBillStatistics`、`/rpt/notInvoiceBillStatistics`

**Why it happens:** 源微服务里 web（80）和 ReportServer（8002）是**独立进程**，路径同名不冲突；单体合并后同进程同 URL 空间。

**How to avoid:** `ReportFeignPathConfig`（Pattern 1）—— `addPathPrefix("/spt-bas-report", forBasePackage("com.spt.bas.report.server.api"))` 把 54 api 路径全部前缀化，与 BFF 根路径天然隔离。

**Warning signs:** 启动失败 `AmbiguousMappingException: Cannot map 'rptXxxApi' method ... There is already 'rptXxxController' bean method ... mapped`。

### Pitfall 2: Feign 契约路径 vs api 路径 diverge `/spt-bas-report/` 前缀

**What goes wrong:** 不加 path-prefix → report api 在 `/rpt/foo` 暴露，但 Feign 契约 `path = ReportConstant.SERVER_NAME + "/rpt/foo"` = `"spt-bas-report/rpt/foo"` 发到 `/spt-bas-report/rpt/foo` → 404。

**Evidence [VERIFIED: 源 reportClient/remote/IRpt*Client.java + ReportConstant.java]:**
- `ReportConstant.SERVER_NAME = "spt-bas-report"` （`ReportConstant.java:7`）
- 所有 `IRpt*Client` 用 `path = ReportConstant.SERVER_NAME + "/rpt/..."` 或字面 `"spt-bas-report/..."`
- 例：`IRptFundReceivableStatisticsClient` → `path = "spt-bas-report" + "/rpt/fundReceivableStatistics"`
- 对应 api `RptFundReceivableStatisticsApi` `@RequestMapping("/rpt/fundReceivableStatistics")`

**How to avoid:** 同 Pattern 1 —— path-prefix 让 api 实际暴露在 `/spt-bas-report/rpt/foo`，与 Feign 契约路径天然匹配。

**Warning signs:** 启动 OK 但调用返回 404；或 9 个 basServer service impl + BFF 的 report 调用全部失败。

### Pitfall 3: @MapperScan 不覆盖 `com.spt.bas.report.server.dao`

**What goes wrong:** 启动期 53 Mapper bean 未注册 → `org.apache.ibatis.binding.BindingException: Invalid bound statement (not found): com.spt.bas.report.server.dao.RptXxxMapper.findPage`。

**Why it happens:** 当前 `ZgbasMybatisConfig.@MapperScan(basePackages = "com.spt.bas.system.dao")` 仅覆盖 Phase 2 SampleMapper 包，不含报表包。

**How to avoid:** Pattern 3 —— 放宽为 `basePackages = {"com.spt.bas.system.dao", "com.spt.bas.report.server.dao"}`。

**Warning signs:** 启动 OK 但首个报表查询 500 + `BindingException`。

### Pitfall 4: report service 跨域 cross-service 依赖

**What goes wrong:** Wave 测序错误（如先迁 Ctr* 后迁 Summary/UserRoi）→ 编译期 unresolved import。

**Evidence [VERIFIED: 源 RptCtrContractReportServiceImpl.java]:**
```java
import com.spt.bas.report.server.service.IRptSummaryRoiService;  // ← cross-service
import com.spt.bas.report.server.service.IRptUserRoiService;     // ← cross-service
@Autowired private IRptSummaryRoiService summaryRoiService;
@Autowired private IRptUserRoiService userRoiService;
```

**How to avoid:** Wave 3 必须先于 Wave 4（见 Pattern 6 测序）；或合并 Ctr* + Summary + UserRoi 同 wave。

**Warning signs:** 编译期 `cannot find symbol` / `找不到符号` class `IRptSummaryRoiService`。

### Pitfall 5: `RptBaseCostVo` 同名歧义（mybatis type-alias 风险）

**What goes wrong:** Phase 4 已在 `com.spt.bas.client.vo.RptBaseCostVo` 创建一个 Vo（extends PageSearchVo 的查询条件 Vo），reportClient 源 `com.spt.bas.report.client.vo.RptBaseCostVo` 是另一个（report row 实体）。**不同包 = 编译无冲突**，但 mybatis typeAliases 默认用 simple classname 作 alias → 别名表里出现两个 `RptBaseCostVo` → Spring BeanDefinition 异常或 alias 解析错乱。

**Evidence [VERIFIED: diff 两个文件不同内容]:**
- Phase 4 版：`package com.spt.bas.client.vo; extends PageSearchVo; private List<Long> userList;`
- 源版：`package com.spt.bas.report.client.vo; private Long id;` (report row)

**How to avoid:** 源 XML 的 `resultType` / `parameterType` 全用 FQN（不用 alias）—— grep 验证 [VERIFIED: mapper XML 全用 `resultType="com.spt.bas.report.client.entity.Xxx"` FQN]。**无运行时风险**（typeAliases 仅当 XML 用 simple-name 时才生效）。Planner 可加 checkpoint 验证。

**Warning signs:** mybatis 启动日志 `Type alias 'RptBaseCostVo' collision`（若有）。

### Pitfall 6: `RptWxBrandFollow` 唯一非纯 POJO（mybatis-plus 注解，非 JPA）

**What goes wrong:** D-P5-07 担忧实体带 `@Entity` 误拾 —— 经 grep 验证 0 个 report 实体带 `javax.persistence.@Entity`。`RptWxBrandFollow` 用 `@TableName/@TableId/@TableField` 是**mybatis-plus 注解**（com.baomidou.mybatisplus.annotation），非 JPA。

**Evidence [VERIFIED: grep `javax.persistence|@Entity` 在 reportClient 265 java → 0 命中]:**
```java
// RptWxBrandFollow.java (the only entity with @Table* prefix annotations)
import com.baomidou.mybatisplus.annotation.*;
@TableName("t_wx_brand_follow")
@TableId(value = "id", type = IdType.AUTO)
@TableField(fill = FieldFill.INSERT)
```

**How to avoid:** 无需特殊处理 —— ddl-auto=none 下 Hibernate 不扫 `com.spt.bas.report.client.entity`（Phase 4 `@EntityScan` 已限定 `com.spt.bas.client.entity` + `com.spt.pm.entity`，不含 report）。即使误扫，mybatis-plus `@TableName` 与 JPA `@Table` 是不同注解类，不互译。

**Warning signs:** 无 —— 此 pitfall 已 de-risked。

### Pitfall 7: report-client jar 依赖未移除导致双定义

**What goes wrong:** Wave 0 内联 reportClient 源码后，若 `zgbas-system/pom.xml:139` 的 `report-client:2.0.1-SNAPSHOT` jar 依赖未移除 → classpath 上有两份 `com.spt.bas.report.client.*`（jar + 内联源码）→ 编译期或运行期不可预测行为。

**How to avoid:** Wave 0 同时做两件事 —— 内联源码 + 移除 pom jar 块（D-P5-02 原意）。

**Warning signs:** `mvn dependency:tree` 仍出现 `report-client`。

### Pitfall 8: 启动期 `CommandExecutor` 缺失（listener 误迁）

**What goes wrong:** 若迁 `reportServer/listener/ApplicationStartup` 但不迁 `command/ReportCommandExecutor`（Phase 6）→ 启动期 `NoSuchBeanDefinitionException: CommandExecutor`（spt-tools-core 接口，源唯一 impl 在 command/）。

**How to avoid:** Pattern 5 —— **SKIP listener/ApplicationStartup**（Phase 6 与 command 一起迁）。`@Component` 已 component-scanned 但 Spring 会因依赖缺失而失败 —— 必须 skip。

**Warning signs:** 启动失败 `NoSuchBeanDefinitionException: com.spt.tools.core.cmd.CommandExecutor`。

### Pitfall 9: 9 个 basServer service impl 的 report 调用未解除 stub

**What goes wrong:** Phase 4 已迁的 9 个 basServer service impl（`CtrContractUpdateServiceImpl` / `BsCompanyApi` / `WeChatWorkServiceImpl` / `ApplyDeliveryOutServiceImpl` / `RptBaseCostServiceImpl` / `CtrContractSettlementServiceImpl` / `PushContractServiceImpl` / `BsCompanyServiceImpl` / `PerformanceCommissionUserServiceImpl`）已 `@Autowired IRpt*Client` —— Phase 4 是自回环 404 态（D-P4-02 lazy-degradation）。本期接通后若 path-prefix 错误，仍 404。

**How to avoid:** Wave 6 验收时跑 `IRpt*Client` 调用路径的 HTTP proof（如登录后访问 `/business/overview/api/findBusinessOverviewList` 触发 BFF 调用 report 契约）—— 同 Phase 4 WR-02 范式。

**Warning signs:** HTTP 200 但 report 数据全空；或 500 + Feign 404 stack。

## Code Examples

### Sample source report service impl（含分页包装 + cross-service dep）

```java
// Source: basReport/reportServer/src/main/java/com/spt/bas/report/server/service/impl/RptFundReceivableStatisticsServiceImpl.java
// [VERIFIED: 源码 verbatim] — 标准模式：@Autowired Mapper + 内存 PageImpl 包装
@Service
public class RptFundReceivableStatisticsServiceImpl implements IRptFundReceivableStatisticsService {
    @Autowired
    private RptFundReceivableStatisticsMapper fundReceivableStatisticsMapper;

    @Override
    public Page<RptFundReceivableStatistics> findPage(RptFundReceivableStatisticsVo searchVo) {
        List<RptFundReceivableStatistics> list = fundReceivableStatisticsMapper.findPage(searchVo);
        Pageable pageable = PageRequest.of(searchVo.getPage() - 1, searchVo.getRows());
        return new PageImpl<>(list, pageable, searchVo.getCount());  // ← 内存包装，非 SQL count
    }
}
```

### Sample source mapper XML（namespace + FQN resultType）

```xml
<!-- Source: basReport/reportServer/src/main/resources/mybatis/mappers/RptFundReceivableStatisticsMapper.xml -->
<!-- [VERIFIED: verbatim] — namespace 对齐 dao interface，resultType 全 FQN -->
<mapper namespace="com.spt.bas.report.server.dao.RptFundReceivableStatisticsMapper">
    <select id="findPage" resultType="com.spt.bas.report.client.entity.RptFundReceivableStatistics">
        SELECT ... FROM ...
    </select>
</mapper>
```

### Sample source api controller（54 个之一）

```java
// Source: basReport/reportServer/src/main/java/com/spt/bas/report/server/api/RptFundReceivableStatisticsApi.java
// [VERIFIED: verbatim] — 前端直连 @RestController，@RequestMapping 无 /spt-bas-report 前缀
@RestController
@RequestMapping(value = "/rpt/fundReceivableStatistics")  // ← path-prefix 加 /spt-bas-report 后生效
public class RptFundReceivableStatisticsApi {
    @Autowired
    private IRptFundReceivableStatisticsService service;

    @PostMapping("findPage")
    public Page<RptFundReceivableStatistics> findPage(@RequestBody RptFundReceivableStatisticsVo vo) {
        return service.findPage(vo);
    }
}
```

### Sample source Feign contract（D-P5-03 路径 diverge 证据）

```java
// Source: basReport/reportClient/src/main/java/com/spt/bas/report/client/remote/IRptFundReceivableStatisticsClient.java
// [VERIFIED: verbatim] — path 含 SERVER_NAME 前缀
@FeignClient(
    name = ReportConstant.SERVER_NAME,                                    // "spt-bas-report"
    path = ReportConstant.SERVER_NAME + "/rpt/fundReceivableStatistics",  // "spt-bas-report/rpt/fundReceivableStatistics"
    url  = ReportConstant.SERVER_URL,                                     // "#{reportServerConfig.url}"
    configuration = FeignConfig.class
)
public interface IRptFundReceivableStatisticsClient {
    @PostMapping("findPage")
    RespVo<Page<RptFundReceivableStatistics>> findPage(@RequestBody RptFundReceivableStatisticsVo vo);
}
```

### Source `ReportClientConfig`（已 component-scanned，本期无需迁）

```java
// Source: basReport/reportClient/src/main/java/com/spt/bas/report/client/config/ReportClientConfig.java
// [VERIFIED: Phase 4 04-05 已组件扫描此 bean] — 仅需 application-dev.yml 配 spt.bas.report.url
@Configuration
public class ReportClientConfig {
    @DependsOn({"propertiesUtil"})
    @Bean(ReportConstant.SERVER_BEAN_NAME)  // "reportServerConfig"
    public LocalServerConfig localServerConfig() {
        LocalServerConfig conf = new LocalServerConfig();
        conf.setUrlKey(ReportConstant.SERVER_URL_KEY);  // "spt.bas.report.url"
        return conf;
    }
}
```

## State of the Art

| Old Approach | Current Approach | When Changed | Impact |
|--------------|------------------|--------------|--------|
| 微服务 ReportServer(8002) 独立进程 + `server.servlet.context-path=/spt-bas-report` | 单体合并 + `addPathPrefix` 重建命名空间隔离 | Phase 5 | 前端不可见（前端调 `/rpt/...` 由 web BFF 内部转发或直接调报表服务），但 Feign 契约路径仍带 `/spt-bas-report` 前缀 —— 必须保留 |
| Feign-over-HTTP 跨进程调用 report 契约 | 自回环 localhost:8080 + path-prefix | Phase 4 D-P4-01 方案 A | report 侧本期落地，9 svc refs + 多 BFF refs 接通真实调用 |
| mybatis 独立 SqlSessionFactory | mybatis-plus-boot-starter auto-config 绑 @Primary DataSource | Phase 2 D-P2-09 | 报表 Mapper 复用同 DataSource，零额外配置 |

**Deprecated/outdated:**
- 源 `myBatisConfig.xml`（仅 logImpl/lazyLoading）—— 丢弃，依赖 mybatis-plus auto-config
- 源 `TransactionConfig.java`（整文件注释）—— 丢弃
- 源 `FrameworkConfig.java`（authOpenFacade + DataSourceConfig + RestTemplate）—— 全部重复 Phase 2 已有 bean，丢弃
- 源 `WebAppConfig.java`（PageInterceptor 注册）—— Phase 4 basServer WebAppConfig 已覆盖，丢弃

## Assumptions Log

| # | Claim | Section | Risk if Wrong |
|---|-------|---------|---------------|
| A1 | 14 处 BFF 路径冲突清单基于 grep `@RequestMapping\(value\s*=\s*\"/"` —— 若个别 BFF 用 `@GetMapping`/`@PostMapping` 顶层声明（无 class-level `@RequestMapping`），可能漏计 | Pitfall 1 | 实际冲突更多 → path-prefix 解决方案不变（覆盖更广） |
| A2 | 53 个 mapper XML `resultType` 全用 FQN（不用 alias）—— 基于 5 个样本 + 53 文件 grep `resultType|parameterType|type|ofType` | Pitfall 5 | 若某 XML 用 simple-name alias → typeAliases collision；Wave 0 后 grep 全 XML 验证（planner 加 checkpoint） |
| A3 | report service impl 跨域依赖图按业务域粗粒度切片 —— 仅 sample 几个验证 cross-service import | Pattern 6 | 实际依赖图更复杂 → planner 可按 grep 结果调整 wave 内排序（不影响 path-prefix / MapperScan 等核心方案） |
| A4 | Excel 导出仅 1 处（`mergeChainExport`）—— 基于 grep `[Ee]xport` 在 54 api | D-P5-05 | 若实际更多 → 随 service 照搬即等价，无新工具依赖 |
| A5 | `RptCtrContractApi`（54th api 无自己 mapper）复用 `IRptCtrContractService` —— 基于源码 import `IRptCtrContractService` | Pattern 5 | service 依赖其它 mapper → wave 测序考虑 |

**其它所有 claims 均 [VERIFIED: 源码 / 单体 grep] 或 [CITED: 项目内 CONTEXT.md / REQUIREMENTS.md / PROJECT.md / ROADMAP.md / CLAUDE.md]。**

## Open Questions

1. **Wave 切片粒度由 planner 决定** —— 本研究给 6-wave 建议（W0 接线 / W1-W4 按域 / W5 api / W6 验收）。Planner 可合并 W1+W2 或拆分 W4（Ctr* family 太大 15 mapper）。约束：W0 先 / W3 先于 W4（service dep） / W5 最后 / W6 验收。
   - Recommendation: W1+W2 可合并为单 wave（21 mapper，低 cross-dep）；W4 Ctr* 单独成 wave 保 compile gate 稳。

2. **`spt.bas.report.url` 是否已在 application-prod.yml** —— 本研究未查 prod profile。Planner Wave 0 应同时检查 dev + prod（prod 可能需不同 URL 或占位）。
   - Recommendation: 复用 Phase 4 D-P2-13 prod 占位策略（`#{...}` 或 placeholder）。

3. **Phase 4 9 个 svc refs 调用路径覆盖** —— Wave 6 HTTP proof 应跑哪些 BFF 路由？建议 `BusinessOverviewController.findBusinessOverviewList`（触发 `IRptBusinessOverviewClient`）+ `MyIndexController.homeData`（触发 `IRptIndexReportClient`）+ 任一 `/rpt/findPage` 直连。
   - Recommendation: planner 列具体 3 个 proof 路由 + 期望响应 shape。

## Environment Availability

| Dependency | Required By | Available | Version | Fallback |
|------------|------------|-----------|---------|----------|
| JDK 1.8 | 全模块编译 | ✓ | 1.8.0_482 (Corretto) `/Users/alan/Library/Java/JavaVirtualMachines/corretto-1.8.0_482/Contents/Home` | — |
| Maven 3.8.6 | 构建 | ✓ | apache-maven-3.8.6 + zg_settings.xml | — |
| MySQL `sptbasdb_pd` | 抽样查询 proof（D-P5-08） | ✓（dev profile 明文密钥已就位，47.104.15.98:3306） | — | — |
| 本地私服 jar `report-client:2.0.1-SNAPSHOT` | 编译（仅在 Wave 0 移除前） | ✓（`/Users/alan/App/Repository`） | 2.0.1-SNAPSHOT | Wave 0 移除后不需 |
| `spt-tools-mybatis`（PaginationInterceptor / `@MyBatisDao`） | mapper 扫描 + 分页 | ✓（Phase 2 已内联进 zgbas-common） | — | — |
| Phase 4 basClient 资产（entity/vo/constant） | reportClient 类型 + report XML `resultType` 引用 | ✓ | — | — |

**Missing dependencies with no fallback:** 无。

**Missing dependencies with fallback:** 无。

## Validation Architecture

> workflow.nyquist_validation: true（`.planning/config.json`）—— 启用。

### Test Framework

| Property | Value |
|----------|-------|
| Framework | JUnit 4 + Spring Boot Test 2.5.9（Phase 2/3/4 基线） |
| Config file | `zgbas-admin/src/test/java/com/spt/ZgbasApplicationTest.java`（Phase 4 WR-02 脚手架，含 `@Disabled` 占位） |
| Quick run command | `JAVA_HOME=/Users/alan/Library/Java/JavaVirtualMachines/corretto-1.8.0_482/Contents/Home /Users/alan/App/apache-maven-3.8.6/bin/mvn -s /Users/alan/App/apache-maven-3.8.6/zg_settings.xml -pl zgbas-admin -am test -Dtest=ZgbasApplicationTest` |
| Full suite command | 同上（仅一个 test class） |

### Phase Requirements → Test Map

| Req ID | Behavior | Test Type | Automated Command | File Exists? |
|--------|----------|-----------|-------------------|-------------|
| REPORT-01 + PERSIST-02 | 53 Mapper bean 全解析（无 BindingException）+ namespace 正确 | smoke（startup） | `ZgbasApplicationTest.contextLoads` + `allReportMappersResolve`（NEW Wave 5） | Phase 2 已有 contextLoads；新增 mapper 解析断言 Wave 0/5 |
| REPORT-02 | 54 report api @RestController bean 全解析 + path-prefix 生效 | smoke（startup） | `ZgbasApplicationTest.contextLoads` + `reportApiPathPrefixWiring_probe`（NEW Wave 5） | ❌ Wave 5 新增 |
| PERSIST-02 | 抽样报表查询返回非空 + Page shape 正确 | integration（需真实 DB） | `ZgbasApplicationTest.sampleReportQuery_proof`（NEW Wave 6，`@Disabled` 默认，启动 proof 时激活） | ❌ Wave 6 新增 |
| D-P5-03 | report Feign 自回环接通（9 basServer svc + BFF） | integration（HTTP proof） | `ZgbasApplicationTest.reportFeignSelfLoopbackWiring_probe`（NEW Wave 0 fail-fast，仿 Phase 4 WR-02 同名 probe） | ❌ Wave 0 新增 |

### Sampling Rate

- **Per task commit:** `mvn -pl zgbas-system -am compile -q` grep `^\[ERROR\]`（locale 无关）= 0
- **Per wave merge:** 全模块 `mvn compile` + `ZgbasApplicationTest`（含 Wave 0 新增 `reportFeignSelfLoopbackWiring_probe`）GREEN
- **Phase gate:** Wave 6 抽样 proof —— 启动 → 跑 2-3 个 `findPage` 查询断言非空 → 跑 1 个 BFF HTTP proof（触发 `IRpt*Client` 链路）→ 全绿才能 `/gsd:verify-work`

### Sampling Strategy（D-P5-08 抽样选择）

选 2-3 个**代表性**报表跑 findPage proof，覆盖不同 mapper family + 不同 resultType 路径：

1. **`RptFundReceivableStatisticsMapper.findPage`** —— `resultType=com.spt.bas.report.client.entity.RptFundReceivableStatistics`（简单 entity），单表查询，证明 typeAliases + namespace + DataSource 接通。
2. **`RptCtrContractReportMapper.findRptContractPage`** —— cross-service dep（`IRptSummaryRoiService` + `IRptUserRoiService`），复杂 multi-join，证明 wave 测序正确 + 复杂报表 SQL 可执行。
3. **`RptBusinessOverviewMapper`**（任一 method）—— 结果返回给 BFF `BusinessOverviewController`（触发 `IRptBusinessOverviewClient`），证明 D-P5-03 自回环闭环。

### HTTP Reachability Proof（D-P5-03 完整闭环）

Wave 6 启动后跑：
- `GET/POST /spt-bas-report/rpt/fundReceivableStatistics/findPage`（直接命中 report api，断言 200 + Page shape）
- `POST /business/overview/api/findBusinessOverviewList`（BFF 调用 → `IRptBusinessOverviewClient` 自回环 → `RptBusinessOverviewApi`，断言 200）

前者证明 path-prefix 生效 + api 可达；后者证明 Feign 契约接通（解除 D-P4-02 stub 降级）。

### Bean-Resolution Proof（fail-fast）

Wave 0 加 `reportFeignSelfLoopbackWiring_probe`（仿 Phase 4 WR-02 同名 probe）：
```java
@Test
public void reportFeignSelfLoopbackWiring_probe(ApplicationContext ctx) {
    // D-P5-03 fail-fast: 9 IRpt*Client beans registered + url resolves to localhost:8080
    assertThat(ctx.getBean(IRptFundReceivableStatisticsClient.class)).isNotNull();
    // ReportClientConfig bean exists and reads spt.bas.report.url
    LocalServerConfig cfg = ctx.getBean("reportServerConfig", LocalServerConfig.class);
    assertThat(cfg.getUrl()).contains("localhost:8080");
}
```

Wave 5 加：
```java
@Test
public void allReportMappersResolve(SqlSessionFactory sf) {
    // 53 report mapper namespaces registered
    assertThat(sf.getConfiguration().hasStatement(
        "com.spt.bas.report.server.dao.RptFundReceivableStatisticsMapper.findPage")).isTrue();
}

@Test
public void reportApiPathPrefixWiring_probe(ApplicationContext ctx) throws Exception {
    // path-prefix applied: /spt-bas-report/rpt/fundReceivableStatistics maps to RptFundReceivableStatisticsApi
    RequestMappingHandlerMapping mapping = ctx.getBean(RequestMappingHandlerMapping.class);
    HandlerExecutionChain chain = mapping.getHandler(new MockHttpServletRequest("POST",
        "/spt-bas-report/rpt/fundReceivableStatistics/findPage"));
    assertThat(chain).as("report api path-prefix wiring (D-P5-03)").isNotNull();
}
```

### Wave 0 Gaps

- [ ] `zgbas-admin/src/test/java/com/spt/ZgbasApplicationTest.java` 加 1 个 `reportFeignSelfLoopbackWiring_probe`（D-P5-03 fail-fast）
- [ ] Wave 5 加 2 个 probe（mapper resolve + path-prefix wiring）
- [ ] Wave 6 加 1 个 `@Disabled` 默认的 sample query proof（启动 proof 时手动激活）

现有测试基础设施（Phase 2/3/4）已覆盖运行时栈，仅需追加 report 专项断言。

## Security Domain

> workflow 安全相关：本期无新外部端点暴露（54 api 路径在单体根已通过 BFF / Phase 3 Shiro 链路受保护；path-prefix `/spt-bas-report` 不影响 Shiro 根 filter chain，因 `HandlerTypePredicate.forBasePackage` 仅作用于 `com.spt.bas.report.server.api` 的 `@RestController`）。

### Applicable ASVS Categories

| ASVS Category | Applies | Standard Control |
|---------------|---------|-----------------|
| V2 Authentication | no | Shiro session+cookie（Phase 3 已就位，本期不动） |
| V3 Session Management | no | 同上 |
| V4 Access Control | no | Shiro filter chain（Phase 3 已就位） |
| V5 Input Validation | yes（间接） | 报表查询 Vo 由 `@RequestBody` 反序列化，沿用 Spring Jackson 默认；行为等价优先，**不改写 `${}` / 动态 SQL**（D-P5 Claude's Discretion） |
| V6 Cryptography | no | 无新增 |

### Known Threat Patterns for stack

| Pattern | STRIDE | Standard Mitigation |
|---------|--------|---------------------|
| mybatis `${}` SQL 拼接（动态 SQL） | Tampering | **不改写** —— 行为等价优先（PROJECT.md「搬运而非重造」）；安全改造留后续阶段（同 D-P5 「${} 照搬即等价，不改写」决策） |
| 报表查询越权（用户 A 看用户 B 数据） | Information disclosure | 沿用源 service impl 的过滤逻辑（照搬即等价）；本期不审计 |
| Excel 导出大结果集 OOM | DoS | 仅 1 处 `mergeChainExport`，无新依赖；行为等价优先 |

## Sources

### Primary (HIGH confidence)

- **源码 grep** —— 所有 [VERIFIED: 源码] 标注来自 `/Users/alan/WorkSpace/IDEA/zgbas/basReport/{reportServer,reportClient}/src/main/java/...` 与 `/Users/alan/WorkSpace/IDEA/zgbas/basReport/reportServer/src/main/resources/mybatis/mappers/*.xml` 直接读取
- **单体 grep** —— 所有 [VERIFIED: 单体] 标注来自 `/Users/alan/WorkSpace/IDEA/zgbas-plus/{zgbas-admin,zgbas-framework,zgbas-system}/src/...` 直接读取
- `.planning/phases/05-report-migration/05-CONTEXT.md` — 8 locked decisions + 3 verify-items 契约
- `.planning/REQUIREMENTS.md` — REPORT-01/02 + PERSIST-02 定义
- `.planning/ROADMAP.md` §Phase 5 — Goal / Depends-on / 3 success criteria
- `.planning/PROJECT.md` — 持久层体量 259 实体 + 254 Dao + 53 mybatis 报表；Out-of-Scope 边界
- `CLAUDE.md` — 技术栈约束（JDK 1.8 / SpringBoot 2.5.9 / Maven 3.8.6 + zg_settings.xml / 5 模块 / 双 ORM / 外部 spt-auth）
- `zgbas-system/src/main/java/com/spt/bas/client/config/BasFeignPathConfig.java` — Phase 4 D-P4-01a Wave 4 path-prefix 范式（直接模板）
- `zgbas-framework/src/main/java/com/spt/framework/config/ZgbasMybatisConfig.java` — Phase 2 @MapperScan 现状
- `zgbas-admin/src/main/java/com/spt/ZgbasApplication.java` — Phase 4 @EnableFeignClients 放宽记录

### Secondary (MEDIUM confidence)

- `.planning/STATE.md` — Phase 1-4 完成决策累积（@MapperScan 位置 / WR-02 范式 / D-P4-01 方案 A 落地记录 / 明文密钥决定）
- `.planning/phases/02-infrastructure/02-CONTEXT.md`（via canonical_refs 引用）— D-P2-07 照搬保包名 / D-P2-09 mybatis-plus / D-P2-04 SampleMapper 范围

### Tertiary (LOW confidence)

- 无 —— 所有结论均 [VERIFIED] 或 [CITED]。

## Metadata

**Confidence breakdown:**
- Standard stack: HIGH — 无新增依赖，mybatis-plus 2.x 由 Phase 2 锁定
- Architecture: HIGH — 3 verify-item 全部以源码 + 单体 grep 闭合；path-prefix 方案有 Phase 4 直接模板
- Pitfalls: HIGH — 9 个 pitfall 均以 grep / 源码样本证据支撑
- Wave 测序: MEDIUM — 业务域切片基于文件名前缀启发式（planner 可调整）

**Research date:** 2026-07-17
**Valid until:** 2026-08-16（30 天；但源码静态事实长期有效，主要变量是 planner wave 切片选择）

## RESEARCH COMPLETE

**Phase:** 5 - 报表迁移
**Confidence:** HIGH

### Key Findings

- **D-P5-03 CONCLUSIVE: path diverge `/spt-bas-report/` 前缀 + 14 处 BFF 路径冲突** —— 必须复用 Phase 4 D-P4-01a Wave 4 `addPathPrefix` 范式（新建 `ReportFeignPathConfig` 仿 `BasFeignPathConfig`）。无需 checkpoint:human-verify —— 事实清晰，方案有直接 Phase 4 模板。
- **D-P5-07 CONCLUSIVE: `@MapperScan` 当前 `com.spt.bas.system.dao` 单串 → 放宽为 `{"com.spt.bas.system.dao", "com.spt.bas.report.server.dao"}`；53 report 实体 0 个 `@Entity`（唯一 `RptWxBrandFollow` 用 mybatis-plus `@TableName`，非 JPA）—— 无 JPA 误拾风险。**
- **D-P5-02 CONCLUSIVE: reportClient 实际 265 java 分布为 entity 83 + vo 119 + remote 54 + config 1 + constant 2 + payload 3 + utils 2；与 Phase 4 资产仅 1 处同名（`RptBaseCostVo` 不同包不同内容，无冲突）。**
- **修正 CONTEXT.md「大量 `/rpt/*/exportExcel`」描述：实际仅 1 处 `mergeChainExport`（在 `RptCtrContractApi`），D-P5-05 范围远小于预期，随 service 照搬即等价。**
- **reportServer 6 个 config 几乎全 SKIP**（WebAppConfig/FrameworkConfig 重复 Phase 2/4 bean / TransactionConfig 整文件注释 / ScheduleConfig 无 @Scheduled 方法 / BasReportJobConfig xxl-job → Phase 6 / BasicErrorController 加 excludeFilter）；listener/ApplicationStartup SKIP（依赖 command Phase 6）。

### File Created

`/Users/alan/WorkSpace/IDEA/zgbas-plus/.planning/phases/05-report-migration/05-RESEARCH.md`

### Confidence Assessment

| Area | Level | Reason |
|------|-------|--------|
| Standard Stack | HIGH | 无新增依赖；mybatis-plus 2.x Phase 2 锁定；所有库版本由既有内联决定 |
| Architecture | HIGH | path-prefix 方案有 Phase 4 BasFeignPathConfig 直接模板；@MapperScan 放宽 + yml 追加两键均为 1-line 改动；3 verify-item 全闭合 |
| Pitfalls | HIGH | 9 pitfall 均以源码 grep / 单体 grep / Phase 4 同构经验支撑；14 处冲突清单 + 9 svc refs 清单 + 1 处 Excel export 全部 [VERIFIED] |
| Wave 测序 | MEDIUM | 业务域切片基于文件名启发式，planner 可按实际 service 跨域 grep 调整粒度 |

### Open Questions

1. Wave 切片粒度（W1+W2 合并 / W4 Ctr* 单独）由 planner 拍板（约束：W0 先 / W3 先于 W4 / W5 最后 / W6 验收）。
2. `spt.bas.report.url` 是否需在 application-prod.yml 也添加（planner Wave 0 检查 prod profile）。
3. Wave 6 HTTP proof 具体路由清单（建议 `/business/overview/api/findBusinessOverviewList` 触发 BFF→Feign→report api 全链路）。

### Ready for Planning

Research complete. Planner can now create PLAN.md files for Phase 5 (suggested 6-wave structure: W0 接线 / W1-W4 按业务域 / W5 api / W6 验收). All 3 verify-items resolved conclusively with evidence — no `checkpoint:human-verify` gates needed for the path-prefix decision.
