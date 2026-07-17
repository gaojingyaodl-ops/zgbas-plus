---
phase: 4
slug: core-business
status: draft
nyquist_compliant: false
wave_0_complete: false
created: 2026-07-17
---

# Phase 4 — Validation Strategy

> Per-phase validation contract for feedback sampling during execution.
> Derived from `04-RESEARCH.md` §Validation Architecture (HIGH confidence, source-verified).

---

## Test Infrastructure

| Property | Value |
|----------|-------|
| **Framework** | JUnit 4 + spring-boot-starter-test 2.5.9（Spring Boot 2.5.x 默认带 JUnit Vintage + JUnit 5），沿用 Phase 2/3 `ZgbasApplicationTest` |
| **Config file** | 无独立 `*config` —— 通过 `@SpringBootTest(webEnvironment=RANDOM_PORT)` + `application-dev.yml` profile |
| **Quick run command** (compile gate) | `JAVA_HOME=<Corretto-1.8> mvn -pl <module> -am compile -q -s /Users/alan/App/apache-maven-3.8.6/zg_settings.xml`（grep `^\[ERROR\]` locale 无关） |
| **Startup verify command** | `JAVA_HOME=<Corretto-1.8> mvn -pl zgbas-admin -am test -Dtest=ZgbasApplicationTest -s /Users/alan/App/apache-maven-3.8.6/zg_settings.xml`（前置 `export DB_PASSWORD=... SPT_APP_SECRET=...`，非 hermetic D-P3-13 Option 4） |
| **Full suite command** | `JAVA_HOME=<Corretto-1.8> mvn -s /Users/alan/App/apache-maven-3.8.6/zg_settings.xml clean test`（同前置 export） |
| **Estimated runtime** | compile gate ~15-30s/模块；ZgbasApplicationTest 启动验证 ~30-60s（全 Spring context boot） |

---

## Sampling Rate

- **After every task commit:** 受影响模块 `mvn compile -q`（compile gate，< 30s）—— Phase 1 gotcha 级联教训要求逐层绿灯
- **After every plan wave / sub-wave:** `mvn -am compile` 全模块零 `[ERROR]` + `ZgbasApplicationTest` 启动验证（Wave 2 拆 2a/2b sub-wave 各自一道）
- **Before `/gsd:verify-work`:** 全 `ZgbasApplicationTest`（含 WR-02 新增 HTTP proof）绿 + `mvn compile` 全模块零 ERROR + D-P4-01/02/03/04/05/06 决策逐项核验
- **Max feedback latency:** ~60s（compile gate）/ ~60s（startup verify）

---

## Requirement → Test Map

> 任务级 Task ID 在 planner 产出 PLAN.md 后回填。此处先锁需求级行为→测试映射（源自 RESEARCH §Phase Requirements → Test Map）。

| Req ID | Behavior | Test Type | Automated Command | File Exists? |
|--------|----------|-----------|-------------------|-------------|
| **BIZ-01** | service + impl + 域子包 + infra 迁入 zgbas-system 后 `mvn compile` 全模块零 ERROR | 编译门 | `mvn -pl zgbas-system -am compile -q` per Wave 2a/2b | ✅（Wave 2 任务自验） |
| **BIZ-01** | 全 Spring context 启动（含 489 service + 82 infra + 224 api bean） | 启动验证 | `mvn ... -Dtest=ZgbasApplicationTest#contextLoads` | ✅ 已存在 |
| **BIZ-02** | 267 BFF controller 全部 bean 注册（无 `NoSuchBeanDefinitionException`） | 启动验证 | 扩 `ZgbasApplicationTest` 加 BFF controller `getBean` 抽样 | ❌ Wave 0 加扩展 |
| **BIZ-03 / WR-02** | `I*Client` 经 HTTP 可达（接口级 `@GetMapping`/`@PostMapping` over HTTP 返 200/3xx/401）—— 证明 D-P4-01 方案 A Feign 自回环接线成功 | 集成（启动验证 + HTTP reachability） | 扩 `ZgbasApplicationTest` 加 3-5 真实 `I*Client` 端点（合同/授信/库存/放款各一）`TestRestTemplate` 断言 | ❌ Wave 0 加扩展 |
| **D-P4-02** | stub-port 契约 `@Autowired(required=false)` null 守卫生效（启动期不抛 `NoSuchBeanDefinitionException`） | 启动验证 | `ZgbasApplicationTest` 启动期 required=false 吸收 | ✅ 隐式覆盖 |
| **D-P4-04** | rocketmq starter 懒连接不阻塞 context boot（classpath 有 starter 即可启动） | 启动验证 | `ZgbasApplicationTest#contextLoads`（broker 可达性留 Phase 7） | ✅ 隐式覆盖 |

---

## Per-Task Verification Map

> Planner 产出 PLAN.md 后，每个 task 的 `<automated>` verify 与下表对齐。占位行待回填。

| Task ID | Plan | Wave | Requirement | Threat Ref | Secure Behavior | Test Type | Automated Command | File Exists | Status |
|---------|------|------|-------------|------------|-----------------|-----------|-------------------|-------------|--------|
| 04-01-xx | 01 | 1 | BIZ-01 | — | N/A | compile | `mvn -pl zgbas-system -am compile -q` | ✅ | ⬜ pending |
| 04-02-xx | 02 | 2a/2b | BIZ-01 | — | N/A | compile | `mvn -pl zgbas-system -am compile -q` | ✅ | ⬜ pending |
| 04-03-xx | 03 | 3 | BIZ-01/03 | — | N/A | compile + startup | `mvn compile` + `ZgbasApplicationTest` | ✅ | ⬜ pending |
| 04-04-xx | 04 | 4 | BIZ-02/03 | — | Shiro 链随 controller 照搬 | startup + HTTP | `ZgbasApplicationTest`（含 WR-02 扩展） | ❌ W0 | ⬜ pending |

*Status: ⬜ pending · ✅ green · ❌ red · ⚠️ flaky*

---

## Wave 0 Requirements

> planner 的 Wave 0 / 首个计划必须先落地这些，否则后续 wave 的验收无依据。

- [ ] **D-P4-01 方案 A 决策已锁**（用户 2026-07-17 确认）—— planner 据此切 Wave 1+ 任务，含 D-P4-01a path 前缀处理（优先 Feign path 覆盖，不设 context-path）
- [ ] `zgbas-admin/src/test/java/com/spt/ZgbasApplicationTest.java` 扩展：3-5 个 `I*Client` 端点 HTTP reachability 断言（合同/授信/库存/放款各一，`TestRestTemplate`，断言 200/3xx/401）—— 覆盖 BIZ-03 / WR-02
- [ ] `ZgbasApplicationTest` 扩展：BFF controller bean 存在性抽样（`getBean(MyXxxController.class)`）—— 覆盖 BIZ-02
- [ ] `application-dev.yml` 加 `rocketmq.name-server=47.104.66.178:9876` + `rocketmq.producer.group=contract_producer_group`（来源：源 `basServer/application-dev.properties:22`）
- [ ] `application-prod.yml` 加 rocketmq 占位：`${ROCKETMQ_NAMESERVER}` / `${ROCKETMQ_AK}` / `${ROCKETMQ_SK}`（D-P2-13 外置，源 prod 为明文需外置）
- [ ] pom 加 `rocketmq-spring-boot-starter:2.2.2`（来源：源 `basCore/pom.xml:25`）

---

## Manual-Only Verifications

| Behavior | Requirement | Why Manual | Test Instructions |
|----------|-------------|------------|-------------------|
| 真实业务 CRUD 行为对照旧系统 zgbas | ALIGN-02（Phase 7） | 需双系统回归对照，本期仅保启动 + 端点可达 | 留 Phase 7 |
| RocketMQ broker 真实可达 + 消息送达 | D-P4-04（启动不阻塞） | starter 懒连接，启动验证不依赖；broker 可达性是部署期 | 本期验收不发消息；Phase 7 ALIGN 验 |

*其余本期行为（compile gate / context boot / bean 接线 / HTTP reachability）均有自动化验证。*

---

## Validation Sign-Off

- [ ] All tasks have `<automated>` verify or Wave 0 dependencies
- [ ] Sampling continuity: no 3 consecutive tasks without automated verify（compile gate 覆盖每个 wave）
- [ ] Wave 0 covers all MISSING references（ZgbasApplicationTest WR-02 扩展 + rocketmq yml/pom）
- [ ] No watch-mode flags
- [ ] Feedback latency < 60s
- [ ] `nyquist_compliant: true` set in frontmatter

**Approval:** pending
