# Phase 3: 数据层与 Feign 契约 - Context

**Gathered:** 2026-07-22
**Status:** Ready for planning

<domain>
## Phase Boundary

Phase 3 将 basWx 的 11 JPA 实体、18 Dao 接口、3 Feign 契约全量就位于 zgbas-system，并同步更新 ZgbasApplication 的 EntityScan/EnableJpaRepositories/EnableFeignClients 注解，消除对 purchase-client 2.0.1-SNAPSHOT 私服 jar 的依赖，为后续 service 层（Phase 5）提供完整编译基础。

**交付范围：**
- 11 WX JPA 实体（6 from purchase-client + 5 from purchase-server）迁入 zgbas-system
- 18 Dao 接口落地（其中 7 个借用已有 bas 实体；5 个单体已有，仅补 BsBrandDao + CompanyIndustryDao 两个）
- 3 Feign 接口（ISaveTempClient/IWxUserClient/IWxUserDetailClient）保持 @FeignClient 声明，改用现有 FeignConfig
- PurchaseWxClientConfig 内联（按 BasClientConfig 模式，LocalServerConfig + @DependsOn propertiesUtil）
- ZgbasApplication 注解扩包（EntityScan + EnableJpaRepositories 精确新增 WX 相关包）
- pom.xml 移除 purchase-client 2.0.1-SNAPSHOT 依赖

**不在此阶段：**
- Phase 4（Redis/JWT/WxMaService），Phase 5（service impl + BFF controller）
- FeignHttpsConfig / SSLFeignClientConfig（不迁，WX Feign 接口改用现有 FeignConfig）
- purchase-client 中 payload/vo 非实体类（随 Phase 5 service 迁移时处理）

</domain>

<decisions>
## Implementation Decisions

### 包路径策略
- **D-01:** 保留原包名，仅改 `package` 语句。实体保留 `com.spt.bas.purchase.wx.client.entity`（6 个 client 实体）和 `com.spt.bas.purchase.wx.server.entity`（5 个 server 实体）；新实体 Dao 保留 `com.spt.bas.purchase.wx.server.dao`（11 个 WX 专属 Dao）。
- **D-02:** 所有 WX 实体和 Dao 迁入 `zgbas-system` 模块（与现有 bas client/server 实体平级）。

### EntityScan / EnableJpaRepositories 扩包
- **D-03:** `@EntityScan` 精确新增两个包：`com.spt.bas.purchase.wx.client.entity` 和 `com.spt.bas.purchase.wx.server.entity`（追加到现有 basePackages，不改原有 basePackageClasses）。
- **D-04:** `@EnableJpaRepositories` 精确新增 `com.spt.bas.purchase.wx.server.dao`（追加到现有 basePackages）。

### Feign 契约内联
- **D-05:** `PurchaseWxClientConfig` 按 `BasClientConfig` 模式迁入（`@Configuration` + `@DependsOn("propertiesUtil")` + `@Bean("purchaseWxServerConfig")` 返回 `LocalServerConfig`，`urlKey = "spt.bas.purchaseWx.url"`）。
- **D-06:** `FeignHttpsConfig` 和 `SSLFeignClientConfig` 不迁。3 个 @FeignClient 接口的 `configuration` 属性改为现有 `FeignConfig.class`（已在单体中就位）。
- **D-07:** `application-dev.yml`（或 application.yml）中确保有 `spt.bas.purchaseWx.url=http://localhost:8080` 配置项。`@EnableFeignClients` 中 `com.spt.bas.purchase.wx.client.remote` 已在 Phase 4 Wave 3 加入，Phase 3 无需重复修改。

### 借用 bas 实体的 7 个 Dao
- **D-08:** BsCompanyDao / BsDictTypeDao / FeedbackDao / BsProductTypeDao / BsDictDataDao 已在单体 `com.spt.bas.server.dao` 就位，**复用已有 bean，不重复声明**。
- **D-09:** BsBrandDao 和 CompanyIndustryDao 尚未迁入，Phase 3 补入 `com.spt.bas.server.dao`（已在 `@EnableJpaRepositories` 扫描范围内，无需额外扩包）。

</decisions>

<canonical_refs>
## Canonical References

**Downstream agents MUST read these before planning or implementing.**

### 需求 / Roadmap
- `.planning/ROADMAP.md` — Phase 3 goal、Success Criteria、Requirements 映射（WX-DATA-01/02/03、WX-CLIENT-01/02）
- `.planning/REQUIREMENTS.md` — v1.2 requirement list，WX-DATA/WX-CLIENT 完整定义

### 源码（basWx）
- `/Users/alan/WorkSpace/IDEA/zgbas/basWx/purchase-client/src/main/java/com/spt/bas/purchase/wx/client/` — 6 实体、3 Feign 接口、PurchaseWxClientConfig、PurchaseWxConstant 原始位置
- `/Users/alan/WorkSpace/IDEA/zgbas/basWx/purchase-server/src/main/java/com/spt/bas/purchase/wx/server/` — 5 实体、18 Dao 原始位置

### 关键单体文件（对齐模式参照）
- `zgbas-admin/src/main/java/com/spt/ZgbasApplication.java` — @EntityScan/@EnableJpaRepositories/@EnableFeignClients 现有注解（D-03/D-04 改动点）
- `zgbas-system/src/main/java/com/spt/bas/client/config/BasClientConfig.java` — PurchaseWxClientConfig 迁入的复制模板（D-05）
- `zgbas-system/src/main/java/com/spt/bas/client/remote/IApplyVipInvoiceClient.java` — 现有 @FeignClient 声明样式参照（D-06）
- `zgbas-common/src/main/java/com/spt/tools/jpa/dao/BaseDao.java` — 所有 Dao 的公共父接口
- `zgbas-admin/src/main/resources/application-dev.yml` — spt.bas.purchaseWx.url 配置落点（D-07）

### 已有 bas.server.dao 中需要复用的 Dao（D-08）
- `zgbas-system/src/main/java/com/spt/bas/server/dao/BsCompanyDao.java`
- `zgbas-system/src/main/java/com/spt/bas/server/dao/BsDictTypeDao.java`
- `zgbas-system/src/main/java/com/spt/bas/server/dao/FeedbackDao.java`
- `zgbas-system/src/main/java/com/spt/bas/server/dao/BsProductTypeDao.java`
- `zgbas-system/src/main/java/com/spt/bas/server/dao/BsDictDataDao.java`

</canonical_refs>

<code_context>
## Existing Code Insights

### Reusable Assets
- `BasClientConfig`：PurchaseWxClientConfig 迁入的直接模板，逻辑完全相同，只换 bean 名和 urlKey。
- `BaseDao<T>`（`zgbas-common`）：所有 WX Dao 直接继承，无需改动。
- `FeignConfig`（已在单体）：WX Feign 接口 configuration 属性改用此 class，替代源码中的 `SSLFeignClientConfig`。
- `BsCompany/BsDictType/Feedback/BsProductType/BsDictData` 等 bas 实体已在 EntityScan 范围内，Dao 也已就位，WX service 层直接注入即可。

### Established Patterns
- 实体包路径：现有 bas 实体住 `com.spt.bas.client.entity`（239 个），WX 实体保留 `com.spt.bas.purchase.wx.*.entity` 不并入。
- JPA EntityScan 采用精确 basePackages 策略（非根包宽扫），避免意外扫入非 JPA 类触发 MappingException。
- @FeignClient 使用 SpEL url（`#{beanName.url}`）+ `LocalServerConfig` bean 实现自回环，不 hardcode localhost。

### Integration Points
- `ZgbasApplication` 三个注解（EntityScan/EnableJpaRepositories/EnableFeignClients）是本 Phase 的核心改动点，均有详细 Javadoc 说明历史决策；改动时保留 Javadoc 并追加本次扩包原因。
- `application-dev.yml` 新增 `spt.bas.purchaseWx.url=http://localhost:8080`，与 `spt.bas.server.url` 同条目风格。

</code_context>

<specifics>
## Specific Ideas

- 7 个借用 bas 实体的 Dao 中，**5 个单体已有，直接复用**；仅 BsBrandDao 和 CompanyIndustryDao 需新建，统一放 `com.spt.bas.server.dao`。
- WX Feign 3 个接口的 `configuration` 属性必须改为 `FeignConfig.class`（删除 SSLFeignClientConfig 引用）；否则编译会找不到类。

</specifics>

<deferred>
## Deferred Ideas

- `FeignHttpsConfig` / `SSLFeignClientConfig` 若后续需 HTTPS 对外调用可在独立阶段补入，本期不做。
- purchase-client payload/vo（非实体）随 Phase 5 service 迁移时携带，Phase 3 不处理。

</deferred>

---

*Phase: 3-数据层与 Feign 契约*
*Context gathered: 2026-07-22*
