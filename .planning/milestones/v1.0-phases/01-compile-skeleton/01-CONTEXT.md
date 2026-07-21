# Phase 1: 编译止血 + 骨架 - Context

**Gathered:** 2026-07-16
**Status:** Ready for planning

<domain>
## Phase Boundary

在空仓库上搭起 **5 模块 Maven 聚合单体骨架**（`zgbas-admin / common / framework / quartz / system`），模块间依赖拓扑正确；全模块 `mvn compile`（apache-maven-3.8.6 + zg_settings.xml）**零 ERROR**；单一 `@SpringBootApplication` **单进程可启动**（起空 Spring context，无 DB / 无外部 SDK / 无业务代码）；仅 `zgbas-admin` 产出可执行 fat jar。为后续所有迁移提供"编译通过 + 能启动"的骨架基线（ALIGN-03 编译止血基线）。

**不在本期（明确边界）：**
- spt-tools 内联、双 ORM、外部 Bean 注入、nacos 删除、Feign 进程内化 → **Phase 2**
- Shiro 认证 / 首页 → **Phase 3**
- 任何业务实体 / Service / Controller / BFF 搬运 → **Phase 4**
- 报表 Mapper / XML 搬运 → **Phase 5**
- 64 个定时任务 handler 迁移 → **Phase 6**

</domain>

<decisions>
## Implementation Decisions

### Parent POM 与依赖管理（最关键结构性决策）
- **D-01:** 新建扁平 `zgbas-plus` 根 parent POM，**不再继承 `spt-parent 2.5.3-SNAPSHOT`**，尽量消除对私服 nexus 的 spt-parent 依赖。
- **D-02:** 根 parent 改为继承 `spring-boot-starter-parent:2.5.9`（BOM + 插件管理顺 grandparent 流下，符合"Spring 尽量不变"）。继承链：模块 → `zgbas-plus`(根 parent) → `spring-boot-starter-parent:2.5.9`。
- **D-03:** `bas-parent`(`2.0.1-SNAPSHOT`) 与 `spt-tools-parent` BOM `import` **一并移除** —— 新 parent 独立一条，不再挂靠 spt/bas 私服 parent 链。
- **D-04:** `spt-tools-*` 改为**复制源码内联进 `zgbas-common`**（Phase 2 INLINE-01..04 落地；源码 `/Users/alan/WorkSpace/IDEA/tools`，BOM `1.1.1-SNAPSHOT`）。**本期骨架不内联**，仅预留 `common` 作为内联归宿。
- **D-05:** **保留**私服 jar 依赖：外部 HTTP SDK —— `auth-sdk` / `spt-push-sdk` / `spt-file-sdk` / `spt-sign-client`（EXT-01..04 外部集成，**非 spt-tools\***，用户指令不触及）。本期骨架不引，但明确这几个 jar 依赖在后续阶段保留。

### 骨架深度 / "可启动"定义
- **D-06:** 骨架深度 = **中等（B）**。5 模块 + 新扁平 parent + `zgbas-admin` 内 `@SpringBootApplication` + `application.yml`（profile / 端口 / 扫描包 `com.spt` 占位）。
- **D-07:** "可启动" = 起**空 Spring context** 成功（无启动异常退出）。**不连 DB、不引外部 SDK、无业务代码**。

### 模块依赖拓扑（骨架脊柱，定错后续归类全乱）
- **D-08:** 拓扑定稿：
  - `common` ← 无模块内依赖（根基：`IdEntity`/`BaseDao`/异常/常量/工具的归宿）
  - `framework` ← `common`（Shiro/安全/web/数据源 infra）
  - `system` ← `common`, `framework`（业务实体+Service 与报表 mapper，复用 infra）
  - `quartz` ← `common`, `framework`, **`system`**（job handler 在 quartz，调 system 业务 Service）
  - `admin` ← `common`, `framework`, `system`, `quartz`（聚合 + 启动类 + fat jar）
- **D-09:** `quartz → system` 边保留；**防环约束**：`system` 若需反向触发任务，走"接口定义在 `common`、`quartz` 实现"或直接用 Quartz Scheduler API，**禁止 `system → quartz` 编译期依赖**。

### 编译 / 启动验收基线
- **D-10:** Phase 1 验收 = `mvn compile`（apache-maven-3.8.6 + zg_settings.xml）全模块**零 ERROR** + `mvn spring-boot:run` 空上下文成功启动。
- **D-11:** 仅 `zgbas-admin` 产可执行 fat jar（`spring-boot-maven-plugin` repackage），其余 4 模块普通 jar；**弃旧 layout=ZIP 瘦 jar + libs 目录策略**（BUILD-05）。

### Claude's Discretion
- **actuator/health 端点**：Phase 1 默认不加（保持最小启动）；如后续阶段需"启动成功"信号，Phase 3+ 再加 `/actuator/health`。
- **fat jar 打包插件具体配置**（finalName / classifier / mainClass）：按 `spring-boot-starter-parent` 默认 + `zgbas-admin` artifactId，无需自定义 layout。
- **`.java-version`/JDK 1.8 校验、`.idea` 不纳入 git、`.gitignore` 策略**：按 Java/Maven 惯例（`.idea/`、`target/`、`*.iml`、`logs/` 忽略）。

</decisions>

<canonical_refs>
## Canonical References

**Downstream agents MUST read these before planning or implementing.**

### 项目规划（必读，仓库内）
- `.planning/ROADMAP.md` §Phase 1 — 阶段目标 / 依赖 / 需求映射（BUILD-01..05, ALIGN-03）/ 成功标准
- `.planning/REQUIREMENTS.md` — BUILD-01..05 + ALIGN-03（本期需求）；INLINE-01..04（Phase 2 内联范围）；EXT-01..04（外部 SDK 保留范围）
- `.planning/PROJECT.md` — 源架构（4 微服务）、版本基线、spt-tools 内联依赖树（core→data/http/file→jpa/web/mybatis/shiro/aop/config）、外部 Bean 注入模式
- `CLAUDE.md` — 技术栈约束（JDK 1.8 / SpringBoot 2.5.9 / Maven 3.8.6 / zg_settings.xml）、固定 5 模块、私服仓库与本地仓库位置

### 源项目（搬运参考，**非本仓库内路径** —— 绝对路径）
- `/Users/alan/WorkSpace/IDEA/zgbas`（分支 `feat-系统重构v5.0`）— 源微服务
  - `bas-parent/pom.xml` — 现有 parent 链参考（继承 spt-parent 2.5.3、import spt-tools-parent BOM）—— **本期要取代/打破它（D-01..03）**
  - `basCore/{basServer,basClient,pm,pmClient}` — 核心业务源（Phase 4 迁）
  - `basReport` — 报表源（Phase 5 迁）
  - `web` — BFF/UI 源（Phase 4 迁 Controller）
- `/Users/alan/WorkSpace/IDEA/tools` — `spt-tools-*` 源码（Phase 2 内联进 `common` 的来源；BOM `1.1.1-SNAPSHOT`）
- `/Users/alan/WorkSpace/IDEA/spt-auth` — RuoYi 改造参考（单体参考框架；`auth-quartz` + `ScheduleConfig` 供 Phase 6 参考）

### 构建 / 工具链（绝对路径）
- `/Users/alan/App/apache-maven-3.8.6` — Maven 可执行
- `/Users/alan/App/apache-maven-3.8.6/zg_settings.xml` — 私服仓库重定向 settings（构建必用）
- `/Users/alan/App/Repository` — 本地仓库（`spt-tools-*` / `auth-sdk` / `spt-push-sdk` 等 SNAPSHOT jar 在此）

</canonical_refs>

<code_context>
## Existing Code Insights

> zgbas-plus 当前为**空仓库**（仅 `.planning` + `CLAUDE.md` + `.java-version`），无既有代码可复用。下列洞察指向**源项目**结构，用于指导本期从零搭建。

### Reusable Assets
- 无 —— 本期从零搭建 5 模块骨架；不搬运任何源码。

### Established Patterns
- **源 `bas-parent` 的 parent 链**（parent → `spt-parent` + `spt-tools-parent` BOM import）—— 本期**刻意打破**（D-01..03），改扁平 parent + `spring-boot-starter-parent:2.5.9`。
- **Spring Boot 多模块标准模式**：模块 → 自定义根 parent → `spring-boot-starter-parent`（grandparent 的 BOM/插件管理流下）。
- 源模块结构：`basCore` 是聚合 pom（artifactId `spt-core`，含 basServer/basClient/pm/pmClient）；`web`/`basReport`/`basWx` 平级。本期 5 模块是全新映射，不照搬源模块边界。

### Integration Points
- `zgbas-common` 是后续 Phase 2 **spt-tools 内联**的归宿 —— 本期 common 模块结构需为此预留（包名建议对齐 spt-tools 源码包结构 `com.spt.tools.*`，便于 Phase 2 内联落位）。
- `zgbas-admin` 是**唯一启动模块 + fat jar 产出** —— 后续 Controller/BFF（Phase 4）落 admin。
- `application.yml` 的扫描包占位 `com.spt` —— 为后续所有业务代码（`com.spt.bas.*` 等）的 ComponentScan 预留。

</code_context>

<specifics>
## Specific Ideas

- 用户强调 **"Spring 尽量不变"** —— 版本基线 JDK 1.8 + Spring Boot 2.5.9 锁死；新 parent 用 `spring-boot-starter-parent:2.5.9` 而非自管 BOM，最大化复用官方依赖管理。
- 用户对私服依赖的态度是**主动消除**：去 spt-parent + 内联 spt-tools\*；但**接受**外部 HTTP SDK（auth/push/file/sign）暂留 jar 依赖（因它们是外部 HTTP 集成 Bean，非内联目标）。
- 用户在讨论中主动插入关键指令（非走选项卡），明确 parent 策略方向 —— 这是 Phase 1 最重要的输入，优先级高于任何"继承现成链以省事"的考量。

</specifics>

<deferred>
## Deferred Ideas

- **actuator/health 健康端点** —— Phase 3+ 需要启动信号时再加。
- **spt-tools 内联 / 双 ORM / 外部 Bean / nacos 删除 / Feign 进程内化** —— Phase 2（基础设施）。
- **业务实体+Service / Controller+BFF / 报表 / 64 定时任务 handler** 搬运 —— Phase 4 / 5 / 6。

</deferred>

---

*Phase: 1-编译止血 + 骨架*
*Context gathered: 2026-07-16*
