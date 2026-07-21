# Milestones

## v1.0 — 单体化重构交付

**Shipped:** 2026-07-20
**Phases:** 1–7 | **Plans:** 37 | **Requirements:** 37/37 validated

zgbas-plus v1.0 是供应链核心管理平台 zgbas 从 Spring Cloud 微服务（4 服务）到 Maven 聚合单体（5 模块）的重构交付。单进程启动即可跑通全部供应链业务（登录 → 核心业务 → 报表 → 定时任务），行为对齐旧系统。

**Key accomplishments:**
1. 5 模块 Maven 聚合单体搭建，mvn compile 零错，单进程空 context 启动
2. spt-tools 10 模块全量内联（172 类）+ 双 ORM 单 DataSource 共存 + nacos 删除 + Feign 进程内化
3. Shiro session+cookie 认证 + Thymeleaf 动态菜单首页端到端可用（608 模板 + 2097 静态资源）
4. 核心业务全量迁入（533 service + 238 contract + 224 api + 267 BFF），Feign 自回环同进程直调
5. 53 报表 mybatis 全量迁入 + 64 xxl-job handler → RuoYi quartz（53 sys_job 翻译落库）
6. 行为对齐验证 ALIGN-01/02 sign-off（9/9 UAT PASS + 5 @Disabled proof 全绿）

**Archived:** [v1.0-ROADMAP.md](milestones/v1.0-ROADMAP.md) | [v1.0-REQUIREMENTS.md](milestones/v1.0-REQUIREMENTS.md)

**Known deferred items at close:** 9 (see STATE.md Deferred Items)
