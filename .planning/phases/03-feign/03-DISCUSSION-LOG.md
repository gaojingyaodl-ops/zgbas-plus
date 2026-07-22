# Phase 3: 数据层与 Feign 契约 - Discussion Log

> **Audit trail only.** Do not use as input to planning, research, or execution agents.
> Decisions are captured in CONTEXT.md — this log preserves the alternatives considered.

**Date:** 2026-07-22
**Phase:** 3-数据层与 Feign 契约
**Areas discussed:** 包路径策略, EntityScan/JpaRepo 扩包方案, Feign 契约内联方案, purchase-server Dao 的 7 个借用 bas 实体

---

## 包路径策略

### Q1：WX 11 实体和新实体 Dao 放到哪里？

| Option | Description | Selected |
|--------|-------------|----------|
| 保留原包名（最少改动） | 保留 com.spt.bas.purchase.wx.client.entity + server.entity / dao，EntityScan/JpaRepo 新增扫描 | ✓ |
| 并入现有包 | 实体并入 com.spt.bas.client.entity，Dao 并入 com.spt.bas.server.dao，无需扩包但有冲突风险 | |
| 新统一包 com.spt.bas.purchase.wx | 实体+Dao 共用一个根包 | |

**User's choice:** 保留原包名（最少改动）

### Q2：WX 实体和 Dao 放入哪个 Maven 模块？

| Option | Description | Selected |
|--------|-------------|----------|
| 放入 zgbas-system | 外置到 system，和现有实体平级放置 | ✓ |
| 放入 zgbas-admin | 与 BFF 层贴近，但违反层次辨布 | |
| 新建 zgbas-purchase 模块 | 清晰隔离但 pom 改动较大 | |

**User's choice:** 放入 zgbas-system

### Q3：迁移时是否调整包名？

| Option | Description | Selected |
|--------|-------------|----------|
| 仅改 package 语句 | 仅改 package 声明，类名/属性名/@Table/@Column 不动 | ✓ |
| 完整拷贝（包名不改） | 直接拷贝 Java 文件保持原内容，连旧包名一起复制 | |

**User's choice:** 仅改 package 语句

---

## EntityScan / EnableJpaRepositories 扩包方案

### Q1：@EntityScan 如何添加 WX 实体包？

| Option | Description | Selected |
|--------|-------------|----------|
| 精确添加两个包 | 在 basePackages 中新增 com.spt.bas.purchase.wx.client.entity 和 com.spt.bas.purchase.wx.server.entity | ✓ |
| 用共同前缀 com.spt.bas.purchase.wx | 一个前缀覆盖 client+server 实体包，兼容 Phase 5+ 新实体 | |

**User's choice:** 精确添加两个包

### Q2：@EnableJpaRepositories 如何添加 WX Dao 包？

| Option | Description | Selected |
|--------|-------------|----------|
| 精确添加 WX server dao 包 | 在 basePackages 中新增 com.spt.bas.purchase.wx.server.dao | ✓ |
| 用共同前缀 com.spt.bas.purchase.wx | 一个前缀覆盖，兼容 Phase 5+ | |

**User's choice:** 精确添加 WX server dao 包

---

## Feign 契约内联方案

### Q1：PurchaseWxClientConfig 内联方案？

| Option | Description | Selected |
|--------|-------------|----------|
| 丰用 BasClientConfig 模式直接迁入 | 复制 BasClientConfig，改 bean 名（purchaseWxServerConfig）和 urlKey（spt.bas.purchaseWx.url） | ✓ |
| 用 @Value 直接注入 URL | 删除 PurchaseWxClientConfig，用 @Value 在 FeignClient url 中注入 | |

**User's choice:** 丰用 BasClientConfig 模式直接迁入

### Q2：FeignHttpsConfig + SSLFeignClientConfig 是否迁入？

| Option | Description | Selected |
|--------|-------------|----------|
| 不迁 FeignHttps/SSLFeign，用现有 FeignConfig | 3 个 @FeignClient 接口 configuration 属性改为现有 FeignConfig.class | ✓ |
| 连同 SSL 配置一起迁入 | 保持原始 Feign 接口的完整配置不变 | |

**User's choice:** 不迁 FeignHttps/SSLFeign，用现有 FeignConfig

---

## purchase-server Dao 的 7 个借用 bas 实体

### Q1：7 个引用已有 bas 实体的 Dao 如何处理？

| Option | Description | Selected |
|--------|-------------|----------|
| 复用单体已有 Dao，仅补缺的两个 | 5 个已有（BsCompanyDao 等）直接复用；仅补 BsBrandDao + CompanyIndustryDao | ✓ |
| 全部放入 WX server dao 包 | 7 个全部放 com.spt.bas.purchase.wx.server.dao，直接拷贝 purchase-server Dao 无重复类名 | |
| 只迁外参两个到 bas.server.dao | 仅补缺两个，和选项 1 数量简化说法类似 | |

**User's choice:** 复用单体已有 Dao，仅补缺的两个

### Q2：BsBrandDao 和 CompanyIndustryDao 放到哪个包？

| Option | Description | Selected |
|--------|-------------|----------|
| 放 com.spt.bas.server.dao（已在扫描范围内） | 与已有 BsCompanyDao 等同包，无需额外扩 EnableJpaRepositories | ✓ |
| 放 com.spt.bas.purchase.wx.server.dao | 与 WX server Dao 同包，但 @EnableJpaRepositories 新增包已计划 | |

**User's choice:** 放 com.spt.bas.server.dao（已在扫描范围内）

---

## Claude's Discretion

无。所有关键决策均由用户明确选择。

## Deferred Ideas

- FeignHttpsConfig / SSLFeignClientConfig：若后续需要 HTTPS 对外调用可在独立阶段处理
- purchase-client payload/vo（非实体类）随 Phase 5 service 迁移时携带，Phase 3 不涉及
