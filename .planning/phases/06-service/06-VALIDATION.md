---
phase: 6
slug: service
status: draft
nyquist_compliant: true
wave_0_complete: true
created: 2026-07-24
---

# Phase 6 — Validation Strategy

> Service 层是 verbatim 迁移层(20 service impl + 19 interface,D-10/D-11 行为等价,无新业务逻辑)。验证 = **编译可达 + 静态 grep 断言**(D-P6-04 锁定仅编译门),不强制单测覆盖率。与 P5 承托层同款。runtime/bean/启动验证全留 Phase 8。

---

## Test Infrastructure

| Property | Value |
|----------|-------|
| **Framework** | Maven compiler (JDK 1.8) — service 迁移层无单测价值,验证 = 编译 + 静态 grep 断言 |
| **Config file** | `zgbas-system/pom.xml`(已就位,含 spt-sign-client 1.0.0-SNAPSHOT) |
| **Quick run command** | `JAVA_HOME=/Library/Java/JavaVirtualMachines/amazon-corretto-8.jdk/Contents/Home mvn compile -pl zgbas-system --settings /Users/alan/App/apache-maven-3.8.6/zg_settings.xml -q 2>&1 \| grep -E "^\[ERROR\]\|cannot find symbol\|找不到符号" \| head` |
| **Full suite command** | 同 quick(service 迁移编译即全套;controller 在 P7 才迁,不做全模块 compile) |
| **Estimated runtime** | ~30-90 秒 |

> **locale gotcha**(见 memory `project_zgbas-javac-locale`):本机 mvn 中文 locale 输出"找不到符号",verify grep 必须 locale 无关 —— 同时匹配 `^\[ERROR\]` / `cannot find symbol` / `找不到符号`。

---

## Sampling Rate

- **After every task commit:** `mvn compile -pl zgbas-system` + grep(跨 plan 引用在 phase 完成前可能残留,以最终 plan 06-06 零错为准)
- **After every plan wave:** `mvn compile -pl zgbas-system`
- **Before `/gsd:verify-work`:** 06-06 编译门必须零 `[ERROR]`
- **Max feedback latency:** ~90 秒

---

## Per-Task Verification Map

| Task ID | Plan | Wave | Requirement | Threat Ref | Secure Behavior | Test Type | Automated Command | Status |
|---------|------|------|-------------|------------|-----------------|-----------|-------------------|--------|
| 06-01-01..11 | 01 | 1 | WX-SERVICE-01 | — | N/A | compile | `mvn compile -pl zgbas-system` | ⬜ pending |
| 06-01-12 | 01 | 1 | D-P6-01 | — | 4 service Dao import re-point 落地 | source-assert | `grep "com.spt.bas.server.dao" …/impl/BsCompanyService.java` | ⬜ pending |
| 06-02-01 | 02 | 1 | WX-SERVICE-01 | — | N/A | compile | `mvn compile -pl zgbas-system` | ⬜ pending |
| 06-03-01..04 | 03 | 2 | WX-SERVICE-01 | — | N/A | compile | `mvn compile -pl zgbas-system` | ⬜ pending |
| 06-04-01..02 | 04 | 3 | WX-SERVICE-01 | — | N/A | compile | `mvn compile -pl zgbas-system` | ⬜ pending |
| 06-05-01 | 05 | 4 | WX-SERVICE-01 | — | N/A | compile | `mvn compile -pl zgbas-system` | ⬜ pending |
| 06-05-02 | 05 | 4 | D-P6-02 | — | xxl-job scrub 干净 | source-assert | `grep -r "com.xxl.job\|XxlJob" …/purchase/wx/` 零命中 | ⬜ pending |
| 06-06-01 | 06 | 5 | WX-SERVICE-01 | — | 零 [ERROR] | compile | `mvn compile -pl zgbas-system` grep `^\[ERROR\]` 空 | ⬜ pending |
| 06-06-02 | 06 | 5 | SC#2 | — | 无 spt-tools 残余 import | source-assert | `grep -rE "import com.spt.tools" …/purchase/wx/server/service/` 空 | ⬜ pending |

*Status: ⬜ pending · ✅ green · ❌ red · ⚠️ flaky*

> **SC#2 说明** —— "无 spt-tools 残余 import" 指 WX service 不应残留对已删 spt-tools jar 的 import。`com.spt.tools.*`(内联在 zgbas-common)是合法引用(BaseService/IBaseService/ICommand/LocalCacheManager 等),**不在排除列**。此条针对 purchase-client 私服 jar 残余。

---

## Wave 0 Requirements

- [x] 编译框架就位 —— `zgbas-system/pom.xml` 已含 spt-sign-client / weixin-java-miniapp / Jedis(P3/P4 落地)
- [x] 内联 base 就位 —— `zgbas-common/.../tools/jpa/service/{BaseService,IBaseService}` + `tools/jpa/dao/BaseDao` + `tools/core/cmd/{ICommand,CommandExecutor}`
- [x] Dao 就位 —— 11 WX 包内 Dao(P3)+ 4 主域复用 Dao(BsCompanyDao/BsDictTypeDao/BsDictDataDao/BsCompanyIndustryDao/FeedbackDao,主域既有)

*Existing infrastructure covers all phase requirements. No Wave 0 install needed.*

---

## Manual-Only Verifications

| Behavior | Requirement | Why Manual | Test Instructions |
|----------|-------------|------------|-------------------|
| WX Feign 自回环 runtime proof | WX-CLIENT-02(runtime) | controller 在 P7 才迁,P6 无 HTTP 端点 | Phase 8 验证(PurchaseWxClientConfig localhost:8080 直调非 404) |
| ServiceAop 命中 WX service | WX-BFF-03(AOP) | bean 装配需启动 context | Phase 8 启动验证 |

*runtime / bean 装配 / 启动验证全留 Phase 8(D-P6-04)。*

---

## Validation Sign-Off

- [x] All tasks have `<automated>` verify or Wave 0 dependencies covered
- [x] Sampling continuity: every plan wave 后 `mvn compile`
- [x] Wave 0 covers all MISSING references(none missing)
- [x] No watch-mode flags
- [x] Feedback latency < 90s
- [x] `nyquist_compliant: true` set in frontmatter

**Approval:** pending(06-06 编译门绿后 approved)
