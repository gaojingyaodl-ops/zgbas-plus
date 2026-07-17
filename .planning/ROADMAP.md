# Roadmap: zgbas-plus

## Overview

zgbas-plus 是 zgbas（Spring Cloud 微服务 4 服务）→ 单体重构的交付路线图。7 个阶段按 foundation-first 递进：从编译止血 + 骨架搭建开始，经基础设施收敛（spt-tools 内联、双 ORM、外部 Bean、删 nacos、Feign 进程内化），到认证首页端到端，再依次迁移核心业务、53 套报表、64 个定时任务，最终以行为对齐验证收尾。每阶段强依赖前一阶段的成果（parallelization=false），最终交付单进程跑全功能的单体应用。

## Phases

**Phase Numbering:**

- Integer phases (1, 2, 3): Planned milestone work
- Decimal phases (2.1, 2.2): Urgent insertions (marked with INSERTED)

Decimal phases appear between their surrounding integers in numeric order.

- [x] **Phase 1: 编译止血 + 骨架** - 5 模块聚合单体搭建，mvn compile 零错，单进程可启动 (completed 2026-07-16)
- [x] **Phase 2: 基础设施** - spt-tools 内联、双 ORM 共存、外部 Bean 注入、配置收敛、删 nacos、Feign 进程内化 (completed 2026-07-16)
- [x] **Phase 3: 认证首页** - Shiro 登录链路 + 动态菜单首页端到端可用 (completed 2026-07-16)
- [ ] **Phase 4: 核心业务迁移** - 合同/授信/库存/放款 basServer JPA 业务迁入，Controller 迁入 admin
- [ ] **Phase 5: 报表迁移** - 53 套 mybatis 报表迁入，查询行为等价
- [ ] **Phase 6: 定时任务迁移** - xxl-job 删除，64 handler 迁入 RuoYi quartz
- [ ] **Phase 7: 行为对齐验证** - 单服务端到端可用，与旧系统 zgbas 行为等价

## Phase Details

### Phase 1: 编译止血 + 骨架

**Goal**: 5 模块聚合单体结构就位，全模块 mvn compile 零错，单进程可启动 — 为后续所有迁移提供编译通过的骨架基线
**Depends on**: Nothing (first phase)
**Requirements**: BUILD-01, BUILD-02, BUILD-03, BUILD-04, BUILD-05, ALIGN-03
**Success Criteria** (what must be TRUE):

  1. 5 模块聚合单体结构存在（zgbas-admin / common / framework / quartz / system），模块间依赖关系正确
  2. `mvn compile`（apache-maven-3.8.6 + zg_settings.xml）全模块零错误（ALIGN-03 编译止血基线达成）
  3. 单一 `@SpringBootApplication` 启动类存在，`mvn spring-boot:run` 单进程可启动（无需多服务协作）
  4. 仅 zgbas-admin 产出可执行 fat jar，其余 4 模块为普通 jar（弃旧 layout=ZIP 瘦 jar 策略）

**Plans**: TBD

### Phase 2: 基础设施

**Goal**: spt-tools 源码内联进 zgbas-common，双 ORM 单 DataSource 共存，外部服务 Bean 保持原 HTTP 注入，nacos 移除，295 个 FeignClient 进程内化，配置文件收敛 — 为业务迁移提供可用的基础设施层
**Depends on**: Phase 1
**Requirements**: INLINE-01, INLINE-02, INLINE-03, INLINE-04, PERSIST-01, PERSIST-03, PERSIST-04, EXT-01, EXT-02, EXT-03, EXT-04, INFRA-01, INFRA-02, INFRA-04
**Success Criteria** (what must be TRUE):

  1. spt-tools-* 源码内联进 zgbas-common（core 最先），运行时 classpath 不再依赖 spt-tools 私服 jar
  2. 双 ORM 单 DataSource 共存可用：JPA（JpaTransactionManager 设 @Primary）与 mybatis 可同时注入并执行查询，审计字段（createdDate/updatedDate + @EntityListeners）行为保留
  3. 外部服务 Bean（AuthOpenFacade / PushClientHttp / FileRemote / CfcaSignClient）保持原 HTTP/Feign 注入方式，配置项（spt.app.secretKey / appCode / *.url）迁移到位
  4. nacos 依赖与配置完全移除（3 处 nacos.common.utils 工具类引用改 commons），295 个 @FeignClient 改为进程内 bean 直调且编译通过
  5. 4 套配置文件收敛为单 application.yml + profile，数据源前缀统一

**Plans**: 6 plans
Plans:
**Wave 1**

- [x] 02-01-PLAN.md — POM foundation: version pins (D-P2-08) + module dependency declarations

**Wave 2** *(blocked on Wave 1 completion)*

- [x] 02-02-PLAN.md — spt-tools inline layers 1-2 (core + data/http/file) with compile gates

**Wave 3** *(blocked on Wave 2 completion)*

- [x] 02-03-PLAN.md — spt-tools inline layers 3-4 (jpa/web/mybatis/shiro/aop/config) + INLINE-04 verify

**Wave 4** *(blocked on Wave 3 completion)*

- [x] 02-04-PLAN.md — framework dual-ORM wiring (@Primary DataSource + mybatis + 3 external SDK beans)
- [x] 02-05-PLAN.md — entity/Dao bulk-copy (239+240) + trivial sample Mapper (dual-ORM proof)

**Wave 5** *(blocked on Wave 4 completion)*

- [x] 02-06-PLAN.md — boot annotations + config consolidation + Feign proof + startup verification

### Phase 3: 认证首页

**Goal**: Shiro 登录认证 + 动态菜单首页端到端可用 — 用户可登录并看到与旧系统等价的首页菜单
**Depends on**: Phase 2
**Requirements**: AUTH-01, AUTH-02, AUTH-03, AUTH-04
**Success Criteria** (what must be TRUE):

  1. 登录接口照搬旧项目，Shiro session+cookie（非 JWT）认证链路可用，密码校验行为等价（SHA-1 + 盐 1024 次迭代）
  2. 登录成功后首页 + 动态菜单正常加载（经 auth-sdk HTTP 调外部 spt-auth 取菜单/用户数据）
  3. Shiro 链路（Realm / Service / Util / ShiroFilter 配置）迁入 zgbas-framework，过滤器链配置正确

**Plans**: 4 plans
Plans:
**Wave 1**

- [x] 03-01-PLAN.md — Shiro 认证链路迁入 zgbas-framework（Realm/Service/Util + web 工具类 + un-exclude + ruoyi-common）

**Wave 2** *(blocked on Wave 1; 03-02/03-03 逻辑无依赖但须串行执行 — 共享 zgbas-admin/target/)*

- [x] 03-02-PLAN.md — Web 控制器 + Thymeleaf（LoginController/UserOpenController 干净照搬 + IndexController stub-port）
- [x] 03-03-PLAN.md — WebSocket 端点 + 全量前端资源（608 模板 + 742 JS/CSS 批量复制）

**Wave 3** *(blocked on Wave 2 completion)*

- [x] 03-04-PLAN.md — prod 配置外置 + 启动验证（D-P3-13）
**UI hint**: yes

### Phase 4: 核心业务迁移

**Goal**: 合同/授信/库存/放款等核心供应链业务（源 basServer JPA）迁入 zgbas-system，业务 Controller / BFF（源 web）迁入 zgbas-admin，业务间调用改为同进程直调 — 核心业务在单体内端到端可用
**Depends on**: Phase 2, Phase 3
**Requirements**: BIZ-01, BIZ-02, BIZ-03
**Success Criteria** (what must be TRUE):

  1. 合同/授信/库存/放款等核心业务实体与 Service（源 basServer JPA）迁入 zgbas-system，JPA 增删改查可用（PERSIST-01 基础设施支撑实体/Dao 落位）
  2. 业务 Controller / BFF（源 web）迁入 zgbas-admin，核心业务 HTTP 接口可访问
  3. 业务间原 Feign 调用改为同进程 Feign 自回环（D-P4-01 方案 A，2026-07-17 修正锁定），行为等价

**Plans**: 6 plans
Plans:
**Wave 0** *(前置接线，无阻塞)*

- [x] 04-01-PLAN.md — D-P4-01 方案 A Feign 自回环接线 + D-P4-01a path 前缀剥离 + rocketmq pom/yml + WR-02 验收脚手架

**Wave 1** *(blocked on Wave 0)*

- [x] 04-02-PLAN.md — basClient 数据载体照搬（remote 234 契约 + dto/util/common/riskScore 14，合计 ~249 文件）

**Wave 2a** *(blocked on Wave 1)*

- [x] 04-03-PLAN.md — basServer infra 照搬（cache/util/enums/annotation/filter/listener/command/event/rocketmq **14 安全**(8 @XxlJob task→P6)/config 选择性 6）+ dedup FrameworkConfig + exclude BasJobConfig。**⚠ 编译门结构性延迟**：infra↔service↔PM 互联（源单模块），per-wave 绿不可达（320 ERROR），编译门后移至合并单元齐后（决策 A，见 04-03-SUMMARY）

**Wave 2b (合并编译单元)** *(blocked on Wave 2a — 含 04-03 延迟 gate)*

- [x] 04-04-PLAN.md — basServer service + impl + 域子包（ctr/logistics/performance/rt/stock）~533 **+ 吸收 PM 模块**(service/dao/cache/util/annotation ~50，排除 1 xxl-job task 至 P6；entity/vo/constant Phase2 pmClient 已迁待核验) + 级联 pom(wltea/IK·httpmime·QLExpress)。**合并编译门** `mvn -pl zgbas-system -am compile` `^\[ERROR]`=0 在此关闭

**Wave 3** *(blocked on Wave 2b)*

- [ ] 04-05-PLAN.md — basServer api @RestController 照搬 224 **+ PM api 13**（extends BaseApi，零 implements I*Client — D-P4-01 方案 A 关键约束）

**Wave 4** *(blocked on Wave 3)*

- [ ] 04-06-PLAN.md — web BFF controller 照搬 267 文件 + D-P4-02 stub-port（~5-15 无实现契约降级）+ 最终验收（WR-02 HTTP proof + 全 mvn compile + D-P4-01..06 逐项核验）

### Phase 5: 报表迁移

**Goal**: 53 套 mybatis 报表（合同台账/收付款/风控/业绩等）迁入单体，查询行为与旧系统等价
**Depends on**: Phase 2, Phase 4
**Requirements**: REPORT-01, REPORT-02, PERSIST-02
**Success Criteria** (what must be TRUE):

  1. 53 套报表 Mapper + XML 迁入 zgbas-system 报表包，mybatis 报表查询可执行（PERSIST-02 mybatis 复杂报表查询可用）
  2. 报表 Controller 迁入 zgbas-admin，报表查询 HTTP 接口可访问
  3. 报表查询行为与旧系统 zgbas 等价（仅保行为，分页性能另行评估）

**Plans**: TBD

### Phase 6: 定时任务迁移

**Goal**: xxl-job 删除，64 个 handler 迁入 RuoYi quartz（zgbas-quartz 模块），任务可在 quartz 中注册并手动触发
**Depends on**: Phase 2
**Requirements**: QUARTZ-01, QUARTZ-02, QUARTZ-03, QUARTZ-04, INFRA-03
**Success Criteria** (what must be TRUE):

  1. zgbas-quartz 模块就位，RuoYi quartz（整模块复制 spt-auth/auth-quartz + ScheduleConfig）引入，sys_job / sys_job_log 表建好
  2. 64 个 @XxlJob handler 迁移为 quartz bean（XxlJobHelper.log→slf4j、handleSuccess/Fail→return/异常、getJobParam→JobDataMap）
  3. 任务记录初始化（cron / bean / method 翻译为 sys_job 数据），支持手动触发与传参
  4. 至少 1 个迁移后的任务可手动触发 + 传参 dry-run 通过；xxl-job 依赖与 executor 配置完全移除

**Plans**: TBD

### Phase 7: 行为对齐验证

**Goal**: 单服务启动后全链路端到端可用，关键业务流程与旧系统 zgbas 行为等价 — 单体化交付完成
**Depends on**: Phase 3, Phase 4, Phase 5, Phase 6
**Requirements**: ALIGN-01, ALIGN-02
**Success Criteria** (what must be TRUE):

  1. 单服务启动后，登录 → 首页 → 核心业务 → 报表 → 定时任务 全链路端到端可用
  2. 关键业务流程与旧系统 zgbas 行为等价（回归对照通过）

**Plans**: TBD

## Progress

**Execution Order:**
Phases execute in numeric order: 1 → 2 → 3 → 4 → 5 → 6 → 7

| Phase | Plans Complete | Status | Completed |
|-------|----------------|--------|-----------|
| 1. 编译止血 + 骨架 | 1/1 | Complete    | 2026-07-16 |
| 2. 基础设施 | 6/6 | Complete   | 2026-07-16 |
| 3. 认证首页 | 4/4 | Complete   | 2026-07-16 |
| 4. 核心业务迁移 | 4/6 | In Progress|  |
| 5. 报表迁移 | 0/TBD | Not started | - |
| 6. 定时任务迁移 | 0/TBD | Not started | - |
| 7. 行为对齐验证 | 0/TBD | Not started | - |
