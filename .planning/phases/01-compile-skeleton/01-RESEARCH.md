# Phase 1: 编译止血 + 骨架 - Research

**Researched:** 2026-07-16
**Domain:** Maven 多模块聚合单体骨架 / Spring Boot 2.5.9 启动 / 私服构建
**Confidence:** HIGH (全部核心结论均经 `/tmp` 端到端 POC 实测验证)

## Summary

Phase 1 在空仓库上从零搭建一个 5 模块 Maven 聚合单体骨架（`zgbas-admin / common / framework / quartz / system`），目标是 `mvn compile` 全模块零 ERROR + 单一 `@SpringBootApplication` 单进程起空 Spring context。这是一期"结构正确性"工作，**不搬运任何源码、不引任何业务依赖、不连 DB**。

研究通过两个 POC 端到端验证了全部关键结论：(1) 单模块 `parent → spring-boot-starter-parent:2.5.9` 继承链可在本机 nexus + 本地仓库下解析、JDK 8 编译零错、`spring-boot:run` 0.535s 起空 context；(2) 双模块（common→admin）验证了内部模块 `${project.version}` 依赖接线 + `spring-boot-maven-plugin repackage` **仅作用于 admin**（admin 产 17MB fat jar + `.jar.original`，common 仅 2KB 普通 jar 无 repackage）。所有 locked decisions（D-01..D-11）的技术可行性均已确认。

**最重要的发现（必须最先处理）：** 本机 Maven 默认跑在 **JDK 21** 上（`JAVA_HOME=/Users/alan/.jenv/versions/21`），而 BUILD-03 锁定 JDK 1.8。**所有 Maven 命令必须显式 `export JAVA_HOME` 指向 Corretto 8**，否则用 JDK 21 javac 编译 1.8 字节码、`spring-boot:run` 在 JDK 21 上跑 Spring Boot 2.5.9，虽能起空 context 但偏离锁定基线且后续阶段会埋坑。

**Primary recommendation:** 根 parent 直接 `<parent>spring-boot-starter-parent:2.5.9`（grandparent 模式），5 个子模块 `<parent>` 指向根 parent；模块间依赖在根 `<dependencyManagement>` 用 `${project.version}` 统一管版本；只有 `zgbas-admin` 声明 `spring-boot-maven-plugin`（带 `<mainClass>`）+ 引 `spring-boot-starter-web`，其余 4 模块为普通 jar 且不声明该插件。所有构建命令前缀 `JAVA_HOME=<corretto-8>` + `-s zg_settings.xml`。

<user_constraints>
## User Constraints (from CONTEXT.md)

### Locked Decisions
- **D-01:** 新建扁平 `zgbas-plus` 根 parent POM，**不再继承 `spt-parent 2.5.3-SNAPSHOT`**，消除对私服 spt-parent 依赖。
- **D-02:** 根 parent 改为继承 `spring-boot-starter-parent:2.5.9`。继承链：模块 → `zgbas-plus`(根 parent) → `spring-boot-starter-parent:2.5.9`。
- **D-03:** `bas-parent`(2.0.1-SNAPSHOT) 与 `spt-tools-parent` BOM import **一并移除** —— 新 parent 独立一条。
- **D-04:** `spt-tools-*` 改为内联进 `zgbas-common`（**Phase 2 落地**；源码 `/Users/alan/WorkSpace/IDEA/tools`，BOM `1.1.1-SNAPSHOT`）。**本期骨架不内联**，仅预留 `common` 作为内联归宿。
- **D-05:** 保留私服 jar 依赖 auth-sdk / spt-push-sdk / spt-file-sdk / spt-sign-client（**非 spt-tools\***）。本期骨架不引。
- **D-06:** 骨架深度 = 中等（B）。5 模块 + 新扁平 parent + `zgbas-admin` 内 `@SpringBootApplication` + `application.yml`（profile / 端口 / 扫描包 `com.spt` 占位）。
- **D-07:** "可启动" = 起空 Spring context 成功（无启动异常退出）。不连 DB、不引外部 SDK、无业务代码。
- **D-08:** 拓扑：common←无；framework←common；system←common,framework；quartz←common,framework,**system**；admin←common,framework,system,quartz。
- **D-09:** quartz→system 边保留；防环：system 反向触发任务走"接口在 common、quartz 实现"或 Quartz Scheduler API，禁止 system→quartz 编译期依赖。
- **D-10:** 验收 = `mvn compile`（apache-maven-3.8.6 + zg_settings.xml）全模块零 ERROR + `mvn spring-boot:run` 空上下文启动成功。
- **D-11:** 仅 `zgbas-admin` 产可执行 fat jar（spring-boot-maven-plugin repackage），其余 4 模块普通 jar；弃旧 layout=ZIP 瘦 jar 策略。

### Claude's Discretion
- actuator/health 端点：Phase 1 默认不加（保持最小启动）。
- fat jar 打包插件配置（finalName / classifier / mainClass）：按 starter-parent 默认 + admin artifactId + 显式 mainClass，无需自定义 layout。
- `.java-version`/JDK 1.8 校验、`.idea` 不纳入 git、`.gitignore` 策略：按 Java/Maven 惯例（`.idea/`、`target/`、`*.iml`、`logs/` 忽略）。

### Deferred Ideas (OUT OF SCOPE)
- actuator/health 健康端点 — Phase 3+。
- spt-tools 内联 / 双 ORM / 外部 Bean / nacos 删除 / Feign 进程内化 — Phase 2。
- 业务实体+Service / Controller+BFF / 报表 / 64 定时任务 handler — Phase 4/5/6。
</user_constraints>

<phase_requirements>
## Phase Requirements

| ID | Description | Research Support |
|----|-------------|------------------|
| BUILD-01 | 搭建 5 模块聚合单体，模块间依赖关系正确 | "Architecture Patterns §Module Topology → POM Dependencies" 给出经 POC 验证的根 dependencyManagement + 各模块 `<dependency>` 接线；"Code Examples" 提供完整可复制 POM |
| BUILD-02 | 用 apache-maven-3.8.6 + zg_settings.xml 干净构建 | "Environment Availability" 确认工具链可用 + nexus 在线（HTTP 200）；"Acceptance Commands" 给出带 `-s` 与 `JAVA_HOME` 的精确命令 |
| BUILD-03 | 锁定 JDK 1.8 + Spring Boot 2.5.9，全模块一致 | "Common Pitfalls §1" 说明 JAVA_HOME=JDK8 强制 + starter-parent 默认 java.version=1.8；POC 已在 JDK8 实测 |
| BUILD-04 | 单一 @SpringBootApplication 单进程启动加载全部模块 | "Code Examples §Boot Class" + POC 实测 0.535s 起空 context（com.spt 包，ComponentScan 占位） |
| BUILD-05 | 仅 zgbas-admin 产可执行 fat jar，弃 layout=ZIP | "Architecture Patterns §Fat-Jar-Only-On-Admin" + POC package 实测：admin=17MB fat jar(.jar.original)，common=2KB 普通 jar 无 repackage |
| ALIGN-03 | 全模块 mvn compile 零错误（编译止血基线） | "Acceptance Commands §Locale-independent zero-error assertion" — 用 `grep '^\[ERROR\]'` 计数（POC 实测：坏代码 20、干净代码 0） |
</phase_requirements>

## Project Constraints (from CLAUDE.md)

- **Tech stack**: JDK 1.8 + Spring Boot 2.5.9 锁定，不动。
- **Build**: Maven `/Users/alan/App/apache-maven-3.8.6`，settings `/Users/alan/App/apache-maven-3.8.6/zg_settings.xml`（私服 nexus 重定向）。
- **Module structure**: 固定 5 模块 `zgbas-admin / common / framework / quartz / system`。
- **Private repo**: 本地仓库 `/Users/alan/App/Repository`（spt-* SNAPSHOT jar 在此）；本期不引任何 spt-*。
- **Reference framework**: RuoYi 单体（spt-auth 即 RuoYi 改造，作结构参考）。

## Architectural Responsibility Map

| Capability | Primary Tier | Secondary Tier | Rationale |
|------------|-------------|----------------|-----------|
| 启动类 / fat jar 产出 | zgbas-admin (boot) | — | 唯一 `@SpringBootApplication` + 唯一 spring-boot-maven-plugin repackage；聚合所有模块为 classpath |
| 业务实体/Service/Dao 归宿 | zgbas-system | — | (Phase 4 落地；本期仅占位 jar) |
| 基础设施 Shiro/web/数据源 | zgbas-framework | — | (Phase 2/3 落地；本期仅占位 jar) |
| spt-tools 内联归宿 | zgbas-common | — | (Phase 2 落地；本期仅占位 jar，包名对齐 `com.spt.tools.*`) |
| 定时任务 handler | zgbas-quartz | system(被调) | (Phase 6 落地；本期仅占位 jar，依赖 system) |
| 构建版本/插件管理 | 根 parent POM | spring-boot-starter-parent(grandparent) | dependencyManagement 管内部模块版本；grandparent 提供 SB BOM + pluginManagement |
| Maven 工具链/JDK 选择 | 开发机环境 | — | 由 JAVA_HOME 决定（必须显式指向 JDK8） |

## Standard Stack

本期是骨架，**不引入任何第三方业务库**。唯一依赖是 Spring Boot 自身管理的 starter。

### Core
| Library | Version | Purpose | Why Standard |
|---------|---------|---------|--------------|
| spring-boot-starter-parent | 2.5.9 | 根 parent 的 grandparent，提供 BOM + pluginManagement + `java.version=1.8` 默认 | `[VERIFIED: 实测 POC]` 本机 nexus HTTP 200 可下载；`spring-boot-dependencies:2.5.9` 已在本地仓库；grandparent 继承是 Spring Boot 官方推荐多模块模式 |
| spring-boot-starter-web | (managed 2.5.9) | admin 唯一显式依赖：内嵌 Tomcat + Spring MVC，使"端口"配置有意义并验证 web 层可起 | `[VERIFIED: 实测 POC]` admin 引此 starter 后空 context + Tomcat 0.535s 启动成功 |

### Supporting
| Library | Version | Purpose | When to Use |
|---------|---------|---------|-------------|
| spring-boot-starter-test | (managed 2.5.9) | `@SpringBootTest` context-load 冒烟测试（可选，见 Validation Architecture） | Wave 0 加到 admin（test scope），固化"空 context 能起"为自动化断言 |

> **不引入的（明确）：** DataSource/JDBC/JPA/mybatis/Shiro/druid/fastjson/poi/nacos/feign/spt-tools-*/外部 SDK —— 全部 Phase 2+。本期空 context 因 classpath 上**无任何 db starter**，故 **无需** `exclude=DataSourceAutoConfiguration`（详见 Pitfall §3）。

**Installation:**
```bash
# 本期无任何手动安装。所有依赖经 spring-boot-starter-parent:2.5.9 的 BOM 管理。
# 首次构建会从 nexus 下载 spring-boot-starter-parent:2.5.9（本地仓库仅有 spring-boot-dependencies:2.5.9）。
export JAVA_HOME=/Users/alan/Library/Java/JavaVirtualMachines/corretto-1.8.0_482/Contents/Home
/Users/alan/App/apache-maven-3.8.6/bin/mvn -s /Users/alan/App/apache-maven-3.8.6/zg_settings.xml compile
```

**Version verification (已在本会话执行):**
```bash
# spring-boot-starter-parent:2.5.9 pom 在 nexus 可达
curl -s -o /dev/null -w "%{http_code}" http://47.104.66.178:8081/nexus/content/groups/public/org/springframework/boot/spring-boot-starter-parent/2.5.9/spring-boot-starter-parent-2.5.9.pom
# -> 200   [VERIFIED: 实测]
# spring-boot-dependencies:2.5.9 已在本地仓库
ls -d /Users/alan/App/Repository/org/springframework/boot/spring-boot-dependencies/2.5.9   # 存在 [VERIFIED: 实测]
# spring-boot-starter-parent:2.5.9 本地仓库不存在，首次构建会下载 [VERIFIED: 实测]
```

## Package Legitimacy Audit

> 本期**不安装任何第三方独立包**。`spring-boot-starter-web` / `spring-boot-starter-test` 是 Spring Boot 框架自身经 `spring-boot-starter-parent` BOM 管理的官方 starter（框架本体，非第三方库），其合法性由 Spring Boot 官方发布与私服/中央仓库双重背书。无 slopcheck 对象。

| Package | Registry | Age | Downloads | Source Repo | slopcheck | Disposition |
|---------|----------|-----|-----------|-------------|-----------|-------------|
| spring-boot-starter-web | Maven Central / 私服 | SB 2.5.9 (2021-11) | 极高 | github.com/spring-projects/spring-boot (官方) | N/A (框架本体) | Approved via starter-parent |
| spring-boot-starter-test | 同上 | 同上 | 极高 | 同上 | N/A (框架本体) | Approved via starter-parent |

**Packages removed due to slopcheck [SLOP] verdict:** none（无第三方包引入）
**Packages flagged as suspicious [SUS]:** none

*本期无需 checkpoint:human-verify —— 不存在任何非框架本体的第三方包安装步骤。*

## Architecture Patterns

### System Architecture Diagram

骨架的数据/控制流（Phase 1 仅"构建 → 启动"两态，无业务流）：

```
┌─────────────────────────────────────────────────────────────────┐
│  开发机 shell                                                     │
│   export JAVA_HOME=<Corretto-8>   (强制 JDK8，见 Pitfall §1)      │
│        │                                                         │
│        ▼  mvn -s zg_settings.xml compile | spring-boot:run       │
│ ┌─────────────────────────── Maven 3.8.6 ─────────────────────┐ │
│ │  Reactor 解析根 pom.xml (<parent>=spring-boot-starter-parent │ │
│ │   :2.5.9 grandparent)                                        │ │
│ │      │  按 dependencyManagement(${project.version}) 接线      │ │
│ │      ▼                                                       │ │
│ │  common ──► framework ──► system ──► quartz ──► admin         │ │
│ │   (jar)     (jar)        (jar)      (jar)     (jar+boot)     │ │
│ │      │ 拓扑顺序编译 (common 最先, admin 最后)                  │ │
│ │      ▼  javac -source 1.8 -target 1.8 (JDK8)                │ │
│ │  全模块零 [ERROR]  ──► ALIGN-03 达成                          │ │
│ └──────────────────────────────────────────────────────────────┘ │
│        │ (spring-boot:run / package)                             │
│        ▼                                                         │
│ ┌─── zgbas-admin 单进程 (空 Spring context) ──────────────────┐ │
│ │  @SpringBootApplication (com.spt)                            │ │
│ │   └─ ComponentScan base = com.spt (占位, 覆盖后续所有业务包) │ │
│ │  内嵌 Tomcat :8080  ◄── spring-boot-starter-web              │ │
│ │  无 DataSource / 无 JPA / 无 Shiro / 无外部 SDK              │ │
│ │  spring-boot-maven-plugin repackage → fat jar (仅 admin)     │ │
│ └──────────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────┘
        ▲
        │ 首次依赖解析（spring-boot-starter-parent:2.5.9 等）
   ┌────┴─────────────────────────────────────┐
   │ 私服 Nexus (47.104.66.178:8081, HTTP)     │  + 本地仓库 /Users/alan/App/Repository
   │ 镜像拦截 *,!spring-milestones (blocked=false) │   (spring-boot-dependencies:2.5.9 已缓存)
   └──────────────────────────────────────────┘
```

### Recommended Project Structure

```
zgbas-plus/
├── pom.xml                      # 根 parent (packaging=pom, <parent>=spring-boot-starter-parent:2.5.9, <modules>, dependencyManagement 管内部模块)
├── .gitignore                   # .idea/ target/ *.iml logs/ (对齐源项目 .gitignore)
├── .java-version                # 1.8 (已存在, 保留)
├── zgbas-common/                # 普通 jar, 无模块依赖; 包名预留 com.spt.tools.* (Phase 2 内联归宿)
│   └── pom.xml
├── zgbas-framework/             # 普通 jar, dep: common
│   └── pom.xml
├── zgbas-system/                # 普通 jar, dep: common, framework
│   └── pom.xml
├── zgbas-quartz/                # 普通 jar, dep: common, framework, system
│   └── pom.xml
└── zgbas-admin/                 # 普通 jar + boot, dep: common, framework, system, quartz + spring-boot-starter-web; 唯一 spring-boot-maven-plugin
    ├── pom.xml
    └── src/main/
        ├── java/com/spt/ZgbasApplication.java   # 唯一 @SpringBootApplication
        └── resources/application.yml            # 最小配置 (port/profile/name/scan=占位)
```

> 各 library 模块（common/framework/system/quartz）本期**可无 src**（空 jar）或仅放占位类。POC 验证：common 仅一个占位类即可编译产 2KB jar。建议每个模块至少建一个占位包目录或占位类，避免空模块的边角问题（IDE 报红、`mvn` 空模块 warning）。

### Pattern 1: Grandparent 继承（spring-boot-starter-parent 作为祖父）
**What:** 根 parent `<parent>` 直接指向 `spring-boot-starter-parent:2.5.9`；5 个子模块 `<parent>` 指向根 parent。Spring Boot 的 BOM 与 pluginManagement 沿继承链下传到所有子模块。
**When to use:** D-02 锁定，唯一模式。
**Why优于 spt-auth 的 BOM-import 方式：** spt-auth（参考项目）用的是根 parent 自己 import `spring-boot-dependencies` BOM（非继承），还需手动管 pluginManagement。grandparent 继承自动带 `java.version` / `maven-compiler-plugin` / `spring-boot-maven-plugin` 的 pluginManagement，配置量更少、更标准。
**Example:**
```xml
<!-- 根 pom.xml 顶部 -->
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.5.9</version>
    <relativePath/>   <!-- 不从仓库目录继承, 从 nexus/本地仓库取 -->
</parent>
<groupId>com.spt</groupId>
<artifactId>zgbas-plus</artifactId>
<version>1.0.0-SNAPSHOT</version>
<packaging>pom</packaging>
```
（POC 实测：此继承链在 nexus 下解析成功，编译零错。）

### Pattern 2: 内部模块版本统一管理（根 dependencyManagement + ${project.version}）
**What:** 根 parent 在 `<dependencyManagement>` 用 `<version>${project.version}</version>` 声明 4 个内部模块；子模块引用时**省略 `<version>`**。
**Example:**
```xml
<!-- 根 pom.xml -->
<properties>
    <java.version>1.8</java.version>   <!-- starter-parent 默认即 1.8, 显式更安全 -->
</properties>
<dependencyManagement>
    <dependencies>
        <dependency><groupId>com.spt</groupId><artifactId>zgbas-common</artifactId><version>${project.version}</version></dependency>
        <dependency><groupId>com.spt</groupId><artifactId>zgbas-framework</artifactId><version>${project.version}</version></dependency>
        <dependency><groupId>com.spt</groupId><artifactId>zgbas-system</artifactId><version>${project.version}</version></dependency>
        <dependency><groupId>com.spt</groupId><artifactId>zgbas-quartz</artifactId><version>${project.version}</version></dependency>
    </dependencies>
</dependencyManagement>
```
（POC 实测：common→admin 接线成功，admin 引 common 无需写 version。）

### Pattern 3: Fat-Jar-Only-On-Admin（repackage 作用域控制）
**What:** `spring-boot-maven-plugin` 的 repackage 在 starter-parent 中位于 **pluginManagement**（非默认激活）。只有显式在 `<build><plugins>` 声明该插件的模块才会执行 repackage。
**Rule:** **只有 `zgbas-admin` 的 pom 声明 `spring-boot-maven-plugin`**；common/framework/system/quartz 的 pom **不声明**该插件。
**Why:** 防止 library jar 被 repackage 成可执行 jar（会破坏被依赖时的类加载）。POC 实测验证：admin 产 17MB fat jar + `.jar.original`，common 仅 2KB 普通 jar、无 `.jar.original`（未被 repackage）。
**Example (admin):**
```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
            <configuration>
                <mainClass>com.spt.ZgbasApplication</mainClass>
            </configuration>
        </plugin>
    </plugins>
</build>
```
> 不写 `<executions><execution><goals><goal>repackage</goal>` —— starter-parent 的 pluginManagement 已绑定 repackage 到 package 阶段，子模块声明插件即自动继承该执行。POC 实测：admin package 阶段输出 `spring-boot-maven-plugin:2.5.9:repackage ... Replacing main artifact`。
> **刻意不写 `<layout>ZIP</layout>` / `excludeGroupIds` / `maven-dependency-plugin copy-dependencies`** —— 这是旧 bas-parent 的瘦 jar + libs 目录策略，D-11 明确弃用（见 Pitfall §2）。

### Anti-Patterns to Avoid
- **继承 spt-parent / import spt-tools-parent BOM** —— D-01..03 明确打破，本期不挂任何 spt/bas 私服 parent 链。
- **在 library 模块声明 spring-boot-maven-plugin** —— 会把普通 jar 误 repackage，破坏依赖链。
- **复用旧 layout=ZIP + excludeGroupIds + copy-dependencies 瘦 jar 策略** —— 运维复杂，D-11 弃用。
- **把 spt-tools-* / 业务源码在本期搬进来** —— 越界，全是 Phase 2+。
- **用 JDK 21 跑 Maven 而不设 JAVA_HOME** —— 偏离 BUILD-03 锁定的 JDK 1.8 基线（见 Pitfall §1）。

## Don't Hand-Roll

| Problem | Don't Build | Use Instead | Why |
|---------|-------------|-------------|-----|
| Spring Boot 依赖版本管理 | 自己列一堆 `<dependencyManagement>` 版本号 | `<parent>spring-boot-starter-parent:2.5.9` | grandparent 的 BOM 已管好全部 SB 生态版本，且 POC 验证可用 |
| 多模块编译器 JDK 配置 | 每个模块写 maven-compiler-plugin source/target | starter-parent 的 `java.version` 属性 | 设 `<java.version>1.8</java.version>` 即全模块生效（POC 验证） |
| fat jar 打包 | 自己配 maven-assembly/shade | spring-boot-maven-plugin repackage（仅 admin） | POC 验证：17MB 可执行 fat jar，正确 |
| 内部模块版本 | 每个依赖写死版本字符串 | 根 dependencyManagement `${project.version}` | 一处改全模块同步，POC 验证 |
| 启动成功判定 | 解析日志中文 | `grep '^\[ERROR\]'` + `BUILD SUCCESS`/`Started <Class>` | locale 无关（Pitfall §4 实测） |

**Key insight:** 骨架阶段的全部"复杂度"都该被 spring-boot-starter-parent 吃掉。本期理论上**根 parent 只需 java.version + modules + dependencyManagement(内部模块)**，子模块只需 `<parent>` + 业务依赖，admin 多一个插件。POC 已证明这是充分的最小集。

## Runtime State Inventory

> 本期为**全新空仓库 greenfield**（仓库现存仅 `.planning` / `CLAUDE.md` / `.java-version`，无任何代码）。不涉及 rename / refactor / migration，无运行态状态需迁移。SKIPPED。

| Category | Items Found | Action Required |
|----------|-------------|-----------------|
| Stored data | None — 全新空仓库 | — |
| Live service config | None — 无运行服务 | — |
| OS-registered state | None | — |
| Secrets/env vars | None — application.yml 本期无敏感配置 | — |
| Build artifacts | None — 仓库无 target/ | — |

## Common Pitfalls

### Pitfall 1: Maven 默认跑在 JDK 21，不是 JDK 8（最高优先级）
**What goes wrong:** 本机 `JAVA_HOME=/Users/alan/.jenv/versions/21`，`mvn --version` 显示 `Java version: 21.0.10`。直接 `mvn compile` 会用 **JDK 21 的 javac** 编译 1.8 字节码，`mvn spring-boot:run` 会在 **JDK 21 上**跑 Spring Boot 2.5.9。虽空 context 能起（POC 在 JDK21 上未测，但风险在于偏离 BUILD-03 锁定基线，且后续阶段 JDK21 javac 编 1.8 可能误用 JDK21 独有 API）。
**Why it happens:** jenv 把 `JAVA_HOME` 指向 21；Maven 启动器直接读 `JAVA_HOME`（不走 jenv 的 `.java-version` shim）。`java -version`/`javac -version` 在 shell 显示 1.8 是因为 PATH 里 JDK8 的 java 在前，造成"看起来是 8"的假象。
**How to avoid:** **每条 Maven 命令前 `export JAVA_HOME=/Users/alan/Library/Java/JavaVirtualMachines/corretto-1.8.0_482/Contents/Home`**。POC 实测：设此后 `mvn --version` 显示 `Java version: 1.8.0_482, vendor: Amazon`，编译与启动均在 JDK8。
**Warning signs:** `mvn --version` 的 "Java version" 行不是 1.8.x；构建意外下载 JDK21-only 的依赖。
**实施建议（planner 可固化）：** 在 RESEARCH/PLAN 中把该 `export` 作为所有 mvn 命令的前缀；或考虑加 `.mvn/jvm.config`/`JAVA_HOME` 校验脚本（可选）。

### Pitfall 2: 误抄旧 bas-parent 的 layout=ZIP 瘦 jar 策略
**What goes wrong:** 源 `bas-parent/pom.xml` 含大段 `<layout>ZIP</layout>` + `excludeGroupIds`(100+ 组) + `maven-dependency-plugin copy-dependencies`(产 `libs/` 目录) 配置。若照抄会让骨架产"主 jar + libs/ + config"三件套。
**Why it happens:** 它是现成参考，容易 copy-paste。
**How to avoid:** D-11 明确弃用。admin 的 spring-boot-maven-plugin **只写 `<mainClass>`，不写 layout/excludeGroupIds**，不引 maven-dependency-plugin。POC 验证：标准 repackage 产单一可执行 fat jar。

### Pitfall 3: 误加 exclude=DataSourceAutoConfiguration（本期不需要）
**What goes wrong:** 参考 `SptAuthApplication` 用了 `@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})`，照抄到骨架。
**Why it happens:** spt-auth 的 classpath 上有 mysql/mybatis（auth-admin 引了 mysql-connector-java），故需排除数据源自动配置。但 zgbas-plus 本期 **classpath 无任何 jdbc/jpa/db starter**，`DataSourceAutoConfiguration` 根本不在 classpath，**无需排除**。
**How to avoid:** 本期用裸 `@SpringBootApplication`（无 exclude）。POC 实测：无 exclude、无 db 依赖，空 context 正常起。Phase 2 引入数据源后再加 exclude（届时记录到该阶段研究）。

### Pitfall 4: 用中文 locale 误判编译错误（locale 无关断言）
**What goes wrong:** 本机 Maven 默认 `zh_CN`，javac 错误体是中文 `找不到符号` / `程序包不存在`，而非 `cannot find symbol`。若验收脚本 grep 英文错误词会漏报。
**Why it happens:** OS locale 为中文（MEMORY.md 已记录此 gotcha）。
**How to avoid:** **locale 无关信号是 Maven 的 `[ERROR]` 前缀与 `BUILD FAILURE`/`BUILD SUCCESS`**（Maven 日志格式硬编码英文前缀，不随 locale 变）。POC 实测：坏代码 `grep -c '^\[ERROR\]'` = 20，干净代码 = 0；`[ERROR]` 行体仍含中文 `找不到符号`，但前缀固定。**验收用 `grep '^\[ERROR\]'` 计数 + `BUILD SUCCESS`**，绝不 grep 错误消息体。
**Warning signs:** 验收脚本 grep 了 `cannot find symbol`。

### Pitfall 5: spt-tools-* 经由旧 parent 链被意外传递引入
**What goes wrong:** 本期目标是彻底脱离 spt-parent/spt-tools BOM。若根 parent 不小心残留对 spt-parent 的继承或 spt-tools BOM import，会拖入 spt-tools-* jar 及其传递依赖，违背 D-01..03。
**Why it happens:** copy-paste 旧 parent 配置。
**How to avoid:** 根 parent **只继承 spring-boot-starter-parent**，dependencyManagement **只放 4 个内部模块**，不 import 任何 spt/bas BOM。POC 验证：纯 starter-parent 骨架无任何 spt-* 传递依赖。

### Pitfall 6: nexus 必须在线（HTTP 私服）
**What goes wrong:** zg_settings.xml 把所有仓库镜像到 `http://47.104.66.178:8081`（HTTP，blocked=false）。首次构建需从此拉 `spring-boot-starter-parent:2.5.9`（本地仓库未缓存该 parent pom，仅有 spring-boot-dependencies:2.5.9）。离线构建会失败。
**How to avoid:** 构建时确保网络可达 nexus（POC 实测 HTTP 200）。若需离线，先在线跑一次 `mvn compile` 把 starter-parent:2.5.9 及其依赖拉入本地仓库。

## Code Examples

> 以下全部经 `/tmp/zgbas-poc2` 双模块 POC 实测验证（compile + package + boot）。POC 为 2 模块（common→admin），下方示例扩展为完整 5 模块（拓扑按 D-08），模式一致。

### 根 pom.xml（zgbas-plus 父 POM）
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <!-- D-02: grandparent -->
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.5.9</version>
        <relativePath/>
    </parent>
    <groupId>com.spt</groupId>
    <artifactId>zgbas-plus</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <packaging>pom</packaging>

    <modules>
        <module>zgbas-common</module>
        <module>zgbas-framework</module>
        <module>zgbas-system</module>
        <module>zgbas-quartz</module>
        <module>zgbas-admin</module>
    </modules>

    <properties>
        <java.version>1.8</java.version>
    </properties>

    <!-- D-08 拓扑: 内部模块版本统一管理 -->
    <dependencyManagement>
        <dependencies>
            <dependency><groupId>com.spt</groupId><artifactId>zgbas-common</artifactId><version>${project.version}</version></dependency>
            <dependency><groupId>com.spt</groupId><artifactId>zgbas-framework</artifactId><version>${project.version}</version></dependency>
            <dependency><groupId>com.spt</groupId><artifactId>zgbas-system</artifactId><version>${project.version}</version></dependency>
            <dependency><groupId>com.spt</groupId><artifactId>zgbas-quartz</artifactId><version>${project.version}</version></dependency>
        </dependencies>
    </dependencyManagement>
</project>
```

### Library 模块 pom.xml（以 zgbas-framework 为例，演示依赖声明；其余类推）
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>com.spt</groupId>
        <artifactId>zgbas-plus</artifactId>
        <version>1.0.0-SNAPSHOT</version>
        <relativePath>../pom.xml</relativePath>
    </parent>
    <artifactId>zgbas-framework</artifactId>
    <packaging>jar</packaging>
    <!-- D-08: framework ← common. 版本由根 dependencyManagement 管, 省略 version -->
    <dependencies>
        <dependency><groupId>com.spt</groupId><artifactId>zgbas-common</artifactId></dependency>
    </dependencies>
    <!-- 注意: 不声明 spring-boot-maven-plugin (library jar, 不 repackage) -->
</project>
```
**各 library 模块依赖对照表（D-08 → pom `<dependencies>`）：**
| 模块 | `<dependency>` 列表 |
|------|---------------------|
| zgbas-common | （无内部依赖） |
| zgbas-framework | zgbas-common |
| zgbas-system | zgbas-common, zgbas-framework |
| zgbas-quartz | zgbas-common, zgbas-framework, zgbas-system |
| zgbas-admin | zgbas-common, zgbas-framework, zgbas-system, zgbas-quartz + spring-boot-starter-web（+ 可选 spring-boot-starter-test） |

### zgbas-admin/pom.xml（唯一 boot + fat jar 模块）
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>com.spt</groupId>
        <artifactId>zgbas-plus</artifactId>
        <version>1.0.0-SNAPSHOT</version>
        <relativePath>../pom.xml</relativePath>
    </parent>
    <artifactId>zgbas-admin</artifactId>
    <packaging>jar</packaging>

    <dependencies>
        <!-- 内部模块 (聚合全部, 使单进程加载所有模块) -->
        <dependency><groupId>com.spt</groupId><artifactId>zgbas-common</artifactId></dependency>
        <dependency><groupId>com.spt</groupId><artifactId>zgbas-framework</artifactId></dependency>
        <dependency><groupId>com.spt</groupId><artifactId>zgbas-system</artifactId></dependency>
        <dependency><groupId>com.spt</groupId><artifactId>zgbas-quartz</artifactId></dependency>
        <!-- 唯一显式外部依赖: web (内嵌 Tomcat, 使端口配置有意义) -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <!-- 可选: context-load 冒烟测试 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <mainClass>com.spt.ZgbasApplication</mainClass>
                </configuration>
                <!-- 无需 <executions>: starter-parent pluginManagement 已绑 repackage 到 package 阶段 -->
            </plugin>
        </plugins>
    </build>
</project>
```

### Boot Class（admin/src/main/java/com/spt/ZgbasApplication.java）
```java
// Source: POC 实测 (zgbas-poc, 0.535s 启动成功)
package com.spt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Phase 1 骨架启动类 —— 空 Spring context (无 DataSource / 无业务代码)。
 * 包 com.spt 是 ComponentScan base 占位, 覆盖后续 com.spt.bas.* / com.spt.tools.* 等所有业务包。
 * 本期无 exclude: classpath 无 db starter, DataSourceAutoConfiguration 不在 classpath。
 */
@SpringBootApplication
public class ZgbasApplication {
    public static void main(String[] args) {
        SpringApplication.run(ZgbasApplication.class, args);
    }
}
```

### application.yml（admin/src/main/resources/application.yml）
```yaml
# Source: POC 实测最小配置; 参考 spt-auth application.yml 裁剪至空 context 所需
server:
  port: 8080
  servlet:
    context-path: /

spring:
  application:
    name: zgbas-plus
  profiles:
    active: dev

logging:
  level:
    com.spt: info
    org.springframework: warn
```
> 故意不含 datasource / redis / nacos / mybatis / shiro —— 全部 Phase 2+。POC 验证此配置足够起空 context。

### Acceptance Commands（locale-independent 零 ERROR 断言）
```bash
# 前置: 强制 JDK8 (Pitfall §1)
export JAVA_HOME=/Users/alan/Library/Java/JavaVirtualMachines/corretto-1.8.0_482/Contents/Home
MVN=/Users/alan/App/apache-maven-3.8.6/bin/mvn
S=/Users/alan/App/apache-maven-3.8.6/zg_settings.xml

# [ALIGN-03 / BUILD-02] 全模块编译零 ERROR 断言
$MVN -f /Users/alan/WorkSpace/IDEA/zgbas-plus/pom.xml -s "$S" clean compile 2>&1 | tee /tmp/zgbas-build.log
# 断言 (locale 无关, Pitfall §4):
#   1) grep -c '^\[ERROR\]' /tmp/zgbas-build.log  == 0
#   2) grep -q 'BUILD SUCCESS' /tmp/zgbas-build.log
# POC 实测: 干净代码 -> [ERROR] 计数 0 + BUILD SUCCESS

# [BUILD-04] 空上下文单进程启动 (后台+超时杀)
nohup $MVN -f /Users/alan/WorkSpace/IDEA/zgbas-plus/pom.xml -s "$S" spring-boot:run > /tmp/zgbas-boot.log 2>&1 &
# 轮询 /tmp/zgbas-boot.log 出现 "Started ZgbasApplication" 即成功 (POC ~0.5s 出现)
# 成功后 kill 掉该进程; 失败标志: APPLICATION FAILED TO START / BUILD FAILURE

# [BUILD-05] 仅 admin 产 fat jar 断言
$MVN -f /Users/alan/WorkSpace/IDEA/zgbas-plus/pom.xml -s "$S" package -DskipTests
ls zgbas-admin/target/*.jar.original >/dev/null 2>&1 && echo "admin repackaged (OK)"     # 应存在
ls zgbas-common/target/*.jar.original 2>/dev/null && echo "BAD: common repackaged" || echo "common plain jar (OK)"
# POC 实测: admin .jar.original 存在; common .jar.original 不存在
```

## State of the Art

| Old Approach | Current Approach | When Changed | Impact |
|--------------|------------------|--------------|--------|
| 继承 spt-parent 2.5.3-SNAPSHOT + import spt-tools-parent BOM | 继承 spring-boot-starter-parent:2.5.9（grandparent） | 本期 D-01..03 | 脱离 spt/bas 私服 parent 链；SB BOM/pluginManagement 由官方 grandparent 提供 |
| layout=ZIP + excludeGroupIds + libs 目录瘦 jar | 标准 spring-boot-maven-plugin repackage fat jar（仅 admin） | 本期 D-11 | 弃三件套，单一可执行 fat jar |
| 多服务多 jar | 单进程单 fat jar + 4 普通 jar | 本期 | 单 `mvn spring-boot:run` 跑全功能（空 context） |

**Deprecated/outdated (本期弃用):**
- `spt-parent 2.5.3-SNAPSHOT` 继承链 / `spt-tools-parent` BOM import —— D-01..03 移除。
- `<layout>ZIP</layout>` + `maven-dependency-plugin copy-dependencies` —— D-11 移除。

## Assumptions Log

| # | Claim | Section | Risk if Wrong |
|---|-------|---------|---------------|
| A1 | 根 parent `<version>` 用 `1.0.0-SNAPSHOT` | Code Examples | 低 — 版本号可调；非 locked decision，planner 可改（如对齐 `1.0.0`）。`[ASSUMED]` 因用户未指定确切版本号 |
| A2 | admin 端口 8080、profile dev | Code Examples | 极低 — 仅占位，后续阶段可改 |
| A3 | 各 library 模块本期放占位类（非空 src） | Project Structure | 极低 — 空 src 通常也能编，但占位类避免边角问题；planner 可选 |
| A4 | nexus 在线可达（构建时） | Pitfall §6 | 中 — 离线则首次构建失败；POC 实测当前在线 HTTP 200 |

**说明：** 上述均为非 locked 的次要实现细节。所有 locked decisions（D-01..D-11）与核心技术结论（grandparent 继承、JDK8、locale 无关断言、fat jar 仅 admin）均为 `[VERIFIED: 实测 POC]`，非假设。

## Open Questions

1. **根 parent 版本号取什么？**
   - What we know: 源项目用 `2.0.1-SNAPSHOT`(bas-parent) / spt-parent `2.5.3-SNAPSHOT`；新项目是新生命周期。
   - What's unclear: 用户是否希望沿用某版本号体系（如 `1.0.0-SNAPSHOT` vs 对齐旧 `2.x`）。
   - Recommendation: 默认 `1.0.0-SNAPSHOT`（新项目新基线），planner 在 PLAN 中标注，用户可在 review 时改。非阻塞。

2. **library 模块本期是否需要占位 java 文件？**
   - What we know: 空 src 的 jar 模块 Maven 通常能正常构建（POC 未测纯空 src，但测过单占位类）。
   - Recommendation: 每个模块放一个占位类（如 common 放 `com.spt.tools.core` 下占位，对齐 Phase 2 内联包名），避免 IDE/空模块边角问题。

## Environment Availability

| Dependency | Required By | Available | Version | Fallback |
|------------|------------|-----------|---------|----------|
| Maven | BUILD-02 全部构建 | ✓ | 3.8.6 (`/Users/alan/App/apache-maven-3.8.6`) | — |
| Maven settings (zg_settings.xml) | BUILD-02 私服重定向 | ✓ | — | — |
| 本地仓库 | 依赖缓存 | ✓ | `/Users/alan/App/Repository`（spring-boot-dependencies:2.5.9 已缓存） | — |
| 私服 Nexus | 首次拉 spring-boot-starter-parent:2.5.9 | ✓ (在线) | HTTP 47.104.66.178:8081，实测 200 | 无（离线则需先在线拉一次） |
| JDK 1.8 (BUILD-03) | 编译 + 启动 | ✓ | Corretto 1.8.0_482 (`/Users/alan/Library/Java/JavaVirtualMachines/corretto-1.8.0_482/Contents/Home`) | — |
| **JAVA_HOME 指向** | **强制 Maven 用 JDK8** | **需显式设** | 默认是 JDK 21（jenv），必须 export 到 Corretto 8 | 无 —— **必须显式 export**（Pitfall §1） |

**Missing dependencies with no fallback:**
- 无（工具链完整）。

**Missing dependencies with fallback:**
- 无。

> **关键：** 唯一"需人工干预"项是 `JAVA_HOME`（默认 JDK21，必须每条命令前 `export` 到 JDK8）。其余全部就绪。

## Validation Architecture

> `workflow.nyquist_validation: true`（config.json）。本期为结构/构建型需求，验收以构建期断言为主；补一个 context-load 冒烟测试固化 BUILD-04。

### Test Framework
| Property | Value |
|----------|-------|
| Framework | JUnit 4 (经 spring-boot-starter-test, Spring Boot 2.5.9 默认带) + Spring Test (`@SpringBootTest`) |
| Config file | 无（starter-test 自带默认；maven-surefire-plugin 由 starter-parent 管理） |
| Quick run command | `$MVN -s zg_settings.xml -pl zgbas-admin test`（JAVA_HOME=JDK8 前缀） |
| Full suite command | `$MVN -s zg_settings.xml test` |

### Phase Requirements → Test Map
| Req ID | Behavior | Test Type | Automated Command | File Exists? |
|--------|----------|-----------|-------------------|-------------|
| BUILD-01 | 5 模块拓扑正确（依赖边） | smoke | `$MVN -s "$S" -q dependency:tree` 人工/脚本核对边 | ❌ Wave 0（可选脚本断言） |
| BUILD-02 | 干净构建（maven+settings） | smoke | `$MVN -s "$S" clean compile` exit 0 | ✅ 命令本身即验证 |
| BUILD-03 | JDK1.8 + SB2.5.9 一致 | smoke | `mvn --version`(JDK8) + 根 parent version=2.5.9 + java.version=1.8 | ❌ Wave 0（可选断言） |
| BUILD-04 | 单进程空 context 启动 | unit/context | `$MVN -s "$S" -pl zgbas-admin test`（@SpringBootTest context load） | ❌ Wave 0 |
| BUILD-05 | 仅 admin fat jar | smoke | `package` 后断言 admin 有 .jar.original、其余无 | ❌ Wave 0（可选脚本） |
| ALIGN-03 | 全模块零 ERROR | smoke | `grep -c '^\[ERROR\]' == 0` + `BUILD SUCCESS` | ✅ 命令即验证 |

### Sampling Rate
- **Per task commit:** `$MVN -s "$S" clean compile`（JAVA_HOME=JDK8）+ `grep '^\[ERROR\]' == 0`
- **Per wave merge:** `$MVN -s "$S" clean package -DskipTests`（全模块 fat jar 产出）+ admin context-load test
- **Phase gate:** compile 零 ERROR + `spring-boot:run` 起 + package fat jar 仅 admin，全绿才 `/gsd:verify-work`

### Wave 0 Gaps
- [ ] `zgbas-admin/src/test/java/com/spt/ZgbasApplicationTest.java` —— `@SpringBootTest void contextLoads()` 覆盖 BUILD-04（context-load 冒烟）
- [ ] admin/pom.xml 加 `spring-boot-starter-test`（test scope）—— 框架由 starter-parent 管，无需额外安装
- [ ] 可选：验收脚本封装 compile/boot/package 三断言（grep `[ERROR]`/`Started`/`.jar.original`）

*(如不加冒烟测试：BUILD-04 验收退化为 `spring-boot:run` 手动确认，仍可行但非自动化。建议加。)*

## Security Domain

> 本期起空 context、无业务代码、无外部集成、无 DB、无认证。`security_enforcement` 未显式 false，含最小节。Phase 1 几乎无攻击面，重点是不把敏感信息写进 application.yml。

### Applicable ASVS Categories

| ASVS Category | Applies | Standard Control |
|---------------|---------|-----------------|
| V2 Authentication | no | 本期无认证（Phase 3） |
| V3 Session Management | no | 本期无会话（Phase 3） |
| V4 Access Control | no | 本期无端点（Phase 4） |
| V5 Input Validation | no | 本期无输入（Phase 4） |
| V6 Cryptography | no | 本期无加密 |

### Known Threat Patterns for 骨架
| Pattern | STRIDE | Standard Mitigation |
|---------|--------|---------------------|
| 配置泄密（application.yml 写入库密码/密钥） | Information Disclosure | 本期 application.yml **不含任何密码/密钥**（无 DB/Redis）；保持纯净，密码外置到 Phase 2 数据源引入时处理 |
| 私服 HTTP 明文（nexus 走 HTTP） | Spoofing/Tampering | 沿用 zg_settings.xml 现状（`blocked=false` HTTP 私服）—— 本期不改造网络层；属既有基础设施约束，记录不修 |

> **注意（非本期工作，仅提示）：** MEMORY/PROJECT.md 记录旧 `jdbc.properties` 含生产库明文密码进 git。本期 application.yml 不含数据源，天然规避；Phase 2 引数据源时须轮换+外置。

## Sources

### Primary (HIGH confidence — 实测)
- **`/tmp/zgbas-poc` + `/tmp/zgbas-poc2` 端到端 POC**（本会话构建并验证）：grandparent 继承、JDK8 编译零错、空 context 0.535s 启动、内部模块 ${project.version} 接线、fat jar 仅 admin（.jar.original 验证）、locale 无关 `[ERROR]` 计数（坏 20 / 干净 0）。`[VERIFIED: 实测]`
- **`spring-boot-starter-parent:2.5.9` pom**（nexus 抓取）：确认提供 `java.version=1.8` 默认、`maven.compiler.source/target`、maven-compiler/jar/resources-plugin 与 spring-boot-maven-plugin(repackage) 的 pluginManagement。`[VERIFIED: 实测]`
- **`bas-parent/pom.xml`**（源 `/Users/alan/WorkSpace/IDEA/zgbas/bas-parent/pom.xml`）：枚举要打破的旧 parent 链（继承 spt-parent 2.5.3、import spt-tools-parent BOM、layout=ZIP + excludeGroupIds 瘦 jar 策略）。`[VERIFIED: 实测]`
- **`spt-parent 2.5.3-SNAPSHOT` pom**（本地仓库 `/Users/alan/App/Repository/com/spt/spt-parent/2.5.3-SNAPSHOT/`）：确认其提供的 spring-boot/cloud BOM + shiro/druid/mybatis 等版本 pin 在本期骨架**全部不需要**。`[VERIFIED: 实测]`
- **`spt-auth`（RuoYi 参考单体 `/Users/alan/WorkSpace/IDEA/spt-auth`）**：root pom（多模块结构）、`SptAuthApplication.java`（`@SpringBootApplication` + 包 com.spt 模式）、`auth-admin/pom.xml`（boot 模块 = jar + repackage）。`[VERIFIED: 实测]`

### Secondary (MEDIUM confidence)
- **`spt-tools` 源码包结构**（`/Users/alan/WorkSpace/IDEA/tools/spt-tools-core`）：确认内联根包 `com.spt.tools.core.*`，common 模块占位包名对齐此结构（Phase 2 用）。`[CITED: 源码目录]`
- **zg_settings.xml + 本地仓库 + JDK 列表**：确认工具链、nexus 镜像规则、3 个 JDK（8/21/25）、JAVA_HOME 现状（jenv→21）。`[VERIFIED: 实测]`

## Metadata

**Confidence breakdown:**
- Standard stack: HIGH — grandparent + starter-web 经 POC 编译/启动/打包全验证
- Architecture (模块拓扑/接线): HIGH — 2 模块内部依赖 + repackage 作用域经 POC 验证；5 模块为同模式扩展（拓扑边来自 D-08 locked）
- Pitfalls: HIGH — JDK21/8、locale grep、layout=ZIP、DataSource exclude 均 POC 实测确认
- Acceptance commands: HIGH — 零 ERROR 断言经 POC 坏/干净两态验证

**Research date:** 2026-07-16
**Valid until:** 2026-08-16（30 天；稳定型构建配置，除非 Spring Boot 2.5.9 在私服被下架或 JDK 环境变更）
