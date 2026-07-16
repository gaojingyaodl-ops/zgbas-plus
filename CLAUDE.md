<!-- GSD:project-start source:PROJECT.md -->
## Project

**zgbas-plus**

zgbas-plus 是供应链核心管理平台 **zgbas** 的单体化重构版。源项目 `zgbas`（分支 `feat-系统重构v5.0`）是 Spring Cloud 微服务，需启动 4 个服务（Web / BasServer / ReportServer / PurchaseWx）才能跑全功能；zgbas-plus 将其收敛为**单进程、单服务启动即可跑通全部业务**的 Maven 聚合单体（5 模块），面向降低部署与运维复杂度，**业务行为与旧系统等价**。

**Core Value:** **一个可独立部署、单进程启动即可跑通全部供应链业务（登录 → 核心业务 → 报表 → 定时任务）的单体应用，行为对齐旧系统。**

当出现取舍时优先保住这条：单体单服务跑全功能 + 行为等价，优先于"技术先进性 / 框架升级"。

### Constraints

- **Tech stack**: JDK 1.8 + Spring Boot 2.5.9（用户硬要求"Spring 尽量不变"，已锁定）— 大版本升级与 javax→jakarta 迁移阻塞，不在本期。
- **Build**: Maven `/Users/alan/App/apache-maven-3.8.6`，settings `/Users/alan/App/apache-maven-3.8.6/zg_settings.xml`（私服仓库重定向）。
- **Module structure**: 固定 5 模块聚合单体 `zgbas-admin / common / framework / quartz / system`（用户指定）。
- **Persistence**: 必须双 ORM（JPA 主力 + mybatis 报表）共存（用户指定 #6）。
- **External integration**: auth/push/file/sign 外部 Bean 保持原 HTTP 注入（用户指定 #7）；spt-auth 保持外部。
- **Removed infra**: nacos 删除（#9）；xxl-job 删除改 RuoYi quartz（#10）。
- **Reference framework**: RuoYi 单体（yudao 因 JDK/版本冲突排除）。
- **Scope**: basWx 第一阶段不迁（#14）。
- **Private repo**: `spt-tools-*`、`auth-sdk`、`spt-push-sdk`、`spt-file-sdk`、`spt-sign-client`、`spt-parent` 均为公司私服 SNAPSHOT，本地仓库在 `/Users/alan/App/Repository`；内联 spt-tools 可消除其 jar 依赖。
<!-- GSD:project-end -->

<!-- GSD:stack-start source:STACK.md -->
## Technology Stack

Technology stack not yet documented. Will populate after codebase mapping or first phase.
<!-- GSD:stack-end -->

<!-- GSD:conventions-start source:CONVENTIONS.md -->
## Conventions

Conventions not yet established. Will populate as patterns emerge during development.
<!-- GSD:conventions-end -->

<!-- GSD:architecture-start source:ARCHITECTURE.md -->
## Architecture

Architecture not yet mapped. Follow existing patterns found in the codebase.
<!-- GSD:architecture-end -->

<!-- GSD:skills-start source:skills/ -->
## Project Skills

No project skills found. Add skills to any of: `.claude/skills/`, `.agents/skills/`, `.cursor/skills/`, `.github/skills/`, or `.codex/skills/` with a `SKILL.md` index file.
<!-- GSD:skills-end -->

<!-- GSD:workflow-start source:GSD defaults -->
## GSD Workflow Enforcement

Before using Edit, Write, or other file-changing tools, start work through a GSD command so planning artifacts and execution context stay in sync.

Use these entry points:
- `/gsd-quick` for small fixes, doc updates, and ad-hoc tasks
- `/gsd-debug` for investigation and bug fixing
- `/gsd-execute-phase` for planned phase work

Do not make direct repo edits outside a GSD workflow unless the user explicitly asks to bypass it.
<!-- GSD:workflow-end -->



<!-- GSD:profile-start -->
## Developer Profile

> Profile not yet configured. Run `/gsd-profile-user` to generate your developer profile.
> This section is managed by `generate-claude-profile` -- do not edit manually.
<!-- GSD:profile-end -->
