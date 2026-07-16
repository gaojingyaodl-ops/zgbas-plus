# Phase 1: 编译止血 + 骨架 - Discussion Log

> **Audit trail only.** Do not use as input to planning, research, or execution agents.
> Decisions are captured in CONTEXT.md — this log preserves the alternatives considered.

**Date:** 2026-07-16
**Phase:** 1-编译止血 + 骨架
**Areas discussed:** 骨架搭建范围+可启动定义, Parent POM 与依赖继承策略, 编译零错+可启动的验收基线, 模块间依赖拓扑确认

---

## 骨架搭建范围 + "可启动"定义

| Option | Description | Selected |
|--------|-------------|----------|
| B 中等：壳+配置骨架 | 5 模块 + 新扁平 parent + admin 启动类 + application.yml 占位；起空 Spring context；不连 DB / 不引外部 SDK / 无业务代码 | ✓ |
| A 薄：纯空壳 | 模块近乎空，起最空 context；最快但"编译止血"几乎没牙 | |
| C 厚：连真实库 | B 基础上加 DataSource(Druid) + 连真实 schema(sptbasdb_pd) + 外部 SDK 引 classpath；会抢 Phase 2 的 PERSIST-03 活，越界 | |

**User's choice:** B
**Notes:** 与"去掉 spt-parent 后 parent 要自建 Spring Boot 2.5.9 dependencyManagement"连锁考虑；B 在不越界 Phase 2 的前提下让"可启动"有意义。

---

## Parent POM 与依赖继承策略

| Option | Description | Selected |
|--------|-------------|----------|
| 新建扁平 parent，消除 spt-parent | 不继承 spt-parent 2.5.3；根 parent 继承 spring-boot-starter-parent:2.5.9；bas-parent + spt-tools-parent BOM import 一并移除；spt-tools\* 内联进 common；保留外部 HTTP SDK jar | ✓ |
| 保留 spt-parent→bas-parent 链 | 版本零漂移、复用全部 dependencyManagement（与用户"消除私服依赖"目标冲突） | |
| 摊平单层 parent + 自管 BOM | 更干净但需自管 Spring Boot 版本，有漂移风险 | |

**User's choice:** 新建扁平 parent，消除 spt-parent
**Notes:** 用户在选项卡前**直接插入指令**锁定此决策："不要在继续使用 spt-parent 尽量消除原私服 nexus 中的 spt-parent依赖，其他 spt-tools\*\*\* 通过复制代码至 common 通过内联方式。" 这是讨论中由用户主动给出的最关键结构性输入，优先级高于"继承现成链以省事"。连带 bas-parent 与 spt-tools-parent BOM import 移除；外部 HTTP SDK（auth/push/file/sign）明确保留 jar 依赖。

---

## 编译零错 + 可启动的验收基线

| Option | Description | Selected |
|--------|-------------|----------|
| mvn compile 零 ERROR + spring-boot:run 空 context 起 + 无 DB | Phase 1 验收基线 | ✓ |
| 加 actuator/health | 启动成功信号端点 | |
| 连真实 MySQL schema | 验证 DB 连通（会越界 Phase 2） | |

**User's choice:** 基线（被方面 1 的 B 选择连带收敛）
**Notes:** 此 area 被方面 1（B）连带解决，未单独再问。actuator 用户未特别要求 → Phase 1 默认不加，Phase 3+ 需要时再加。

---

## 模块间依赖拓扑确认

| Option | Description | Selected |
|--------|-------------|----------|
| 分层 + quartz→system | common←(无); framework←common; system←common,framework; quartz←common,framework,system; admin←all | ✓ |
| quartz 只当引擎壳（不依赖 system） | handler 落 admin/system，quartz 零环 | |
| 照搬 RuoYi（admin→framework→common+system，quartz 平级） | | |

**User's choice:** 分层 + quartz→system（"要"）
**Notes:** `quartz → system` 边保留 —— 因 64 个 job handler 在 quartz 要调 system 业务 Service。防环约束：system 反向触发任务走"common 接口 + quartz 实现"或 Quartz Scheduler API，禁止 system→quartz 编译期依赖。

---

## Claude's Discretion

- actuator/health 端点（Phase 1 不加）
- fat jar 打包插件具体配置（finalName / classifier / mainClass）—— 按 spring-boot-starter-parent 默认
- `.java-version` / JDK 校验、`.idea` 不入 git、`.gitignore` 策略 —— Java/Maven 惯例

## Deferred Ideas

- actuator/health 端点 —— Phase 3+
- spt-tools 内联 / 双 ORM / 外部 Bean / nacos 删除 / Feign 进程内化 —— Phase 2
- 业务 / 报表 / 定时任务搬运 —— Phase 4 / 5 / 6
