---
phase: 1
slug: compile-skeleton
status: draft
nyquist_compliant: false
wave_0_complete: false
created: 2026-07-16
---

# Phase 1 — Validation Strategy

> Per-phase validation contract for feedback sampling during execution.
> 本期为结构/构建型需求（空 context 骨架），验收以**构建期 + 启动期断言**为主，辅以一个 context-load 冒烟测试固化 BUILD-04。

> **MANDATORY ENV PREFIX（最高优先级 gotcha）：** 本机 Maven 默认跑在 **JDK 21**，违反 BUILD-03 的 JDK 1.8 锁定。所有 mvn 命令必须前缀：
> `export JAVA_HOME=/Users/alan/Library/Java/JavaVirtualMachines/corretto-1.8.0_482/Contents/Home`
> 以下命令记为 `$MVN`（= 该 JAVA_HOME 下的 `/Users/alan/App/apache-maven-3.8.6/bin/mvn`），`$S` = `/Users/alan/App/apache-maven-3.8.6/zg_settings.xml`。

---

## Test Infrastructure

| Property | Value |
|----------|-------|
| **Framework** | JUnit 4（经 `spring-boot-starter-test`，Spring Boot 2.5.9 默认携带）+ Spring Test（`@SpringBootTest`） |
| **Config file** | none — starter-test 自带默认；maven-surefire-plugin 由 spring-boot-starter-parent 管理 |
| **Quick run command** | `$MVN -s "$S" -pl zgbas-admin test` |
| **Full suite command** | `$MVN -s "$S" clean test` |
| **Estimated runtime** | ~10–20 秒（首次 nexus 拉 starter-parent 略长） |

---

## Sampling Rate

- **After every task commit:** `$MVN -s "$S" clean compile` → 断言 `grep -c '^\[ERROR\]'` == 0 且出现 `BUILD SUCCESS`
- **After every plan wave:** `$MVN -s "$S" clean package -DskipTests`（全模块产出，admin fat jar）+ `$MVN -s "$S" -pl zgbas-admin test`（admin context-load 冒烟）
- **Before `/gsd:verify-work`:** compile 零 ERROR + `spring-boot:run` 空 context 启动（`Started ZgbasApplication`）+ package 后仅 admin 有 `.jar.original`，全绿才放行
- **Max feedback latency:** ~30 秒

---

## Per-Task Verification Map

| Task ID | Plan | Wave | Requirement | Threat Ref | Secure Behavior | Test Type | Automated Command | File Exists | Status |
|---------|------|------|-------------|------------|-----------------|-----------|-------------------|-------------|--------|
| 01-01-* | 01 | 1 | BUILD-01 | — | N/A | smoke | `$MVN -s "$S" -q dependency:tree` 核对 5 模块依赖边（D-08） | ❌ W0 脚本 | ⬜ pending |
| 01-01-* | 01 | 1 | BUILD-02 | — | N/A | smoke | `$MVN -s "$S" clean compile` exit 0 | ✅ 命令即验证 | ⬜ pending |
| 01-01-* | 01 | 1 | BUILD-03 | — | N/A | smoke | `mvn --version` 显示 jdk 1.8 + 根 parent `2.5.9` + `java.version=1.8` | ❌ W0 脚本 | ⬜ pending |
| 01-01-* | 01 | 1 | BUILD-04 | — | N/A | context | `$MVN -s "$S" -pl zgbas-admin test`（`@SpringBootTest contextLoads()`） | ❌ W0 | ⬜ pending |
| 01-01-* | 01 | 1 | BUILD-05 | — | N/A | smoke | `package` 后 admin 有 `.jar.original`、common/framework/system/quartz 无 | ❌ W0 脚本 | ⬜ pending |
| 01-01-* | 01 | 1 | ALIGN-03 | — | N/A | smoke | `grep -c '^\[ERROR\]'` == 0 + `BUILD SUCCESS` | ✅ 命令即验证 | ⬜ pending |

*Status: ⬜ pending · ✅ green · ❌ red · ⚠️ flaky*

---

## Wave 0 Requirements

- [ ] `zgbas-admin/src/test/java/com/spt/ZgbasApplicationTest.java` — `@SpringBootTest void contextLoads()` 覆盖 BUILD-04（空 context 加载冒烟）
- [ ] `zgbas-admin/pom.xml` 加 `spring-boot-starter-test`（test scope）— 框架由 starter-parent 管，无需额外安装
- [ ] 可选：验收脚本封装三断言（`grep '^\[ERROR\]'` == 0 / `Started ZgbasApplication` / admin `.jar.original` 存在）

*如不加冒烟测试：BUILD-04 验收退化为 `spring-boot:run` 手动确认，仍可行但非自动化。建议加。*

---

## Manual-Only Verifications

| Behavior | Requirement | Why Manual | Test Instructions |
|----------|-------------|------------|-------------------|
| 5 模块依赖边符合 D-08 拓扑 | BUILD-01 | 依赖图核对（或脚本） | `dependency:tree` 后核对 common←(无)、framework←common、system←{common,framework}、quartz←{common,framework,system}、admin←all |
| 仅 admin 出 fat jar | BUILD-05 | 需检视 4 个 lib 模块无 `.jar.original` | `package` 后 `find . -name '*.jar.original'` 仅命中 admin |

*（均可脚本化；列为 manual 是因可接受人工核对。）*

---

## Validation Sign-Off

- [ ] 所有任务有 `<automated>` verify 或 Wave 0 依赖
- [ ] 采样连续性：无 3 个连续任务缺自动 verify
- [ ] Wave 0 覆盖所有 MISSING 引用
- [ ] 无 watch-mode 标志
- [ ] Feedback latency < 30s
- [ ] `nyquist_compliant: true` 在 verify 前置入 frontmatter

**Approval:** pending
