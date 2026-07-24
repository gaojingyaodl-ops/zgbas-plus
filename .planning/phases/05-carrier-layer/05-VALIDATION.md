---
phase: 5
slug: carrier-layer
status: draft
nyquist_compliant: true
wave_0_complete: true
created: 2026-07-23
---

# Phase 5 — Validation Strategy

> 承托层是 verbatim 迁移层(纯 POJO/常量/config/启动接线,无新业务逻辑)。验证以**编译可达 + 静态断言 + 启动接线**为主,不强制单测覆盖率(与 v1.0 Phase 4/5 迁移层一致)。

---

## Test Infrastructure

| Property | Value |
|----------|-------|
| **Framework** | Maven compiler (JDK 1.8) — 承托层无单测价值,验证 = 编译 + 静态 grep 断言 |
| **Config file** | `zgbas-system/pom.xml`(已就位)+ `application-dev.yml` |
| **Quick run command** | `JAVA_HOME=/Library/Java/JavaVirtualMachines/amazon-corretto-8.jdk/Contents/Home mvn compile -pl zgbas-system --settings /Users/alan/App/apache-maven-3.8.6/zg_settings.xml -q 2>&1 \| grep -E "^\[ERROR\]" \| head` |
| **Full suite command** | 同 quick(承托层编译即全套) |
| **Estimated runtime** | ~30-60 秒 |

---

## Sampling Rate

- **After every task commit:** 编译 + grep `[ERROR]`(跨 plan 引用在 phase 完成前可能残留,以最终 plan 05-06 零错为准)
- **After every plan wave:** `mvn compile -pl zgbas-system`
- **Before `/gsd:verify-work`:** 05-06 编译门必须零 `[ERROR]`
- **Max feedback latency:** ~60 秒

---

## Per-Task Verification Map

| Task ID | Plan | Wave | Requirement | Threat Ref | Secure Behavior | Test Type | Automated Command | Status |
|---------|------|------|-------------|------------|-----------------|-----------|-------------------|--------|
| 05-01 | 01 | 1 | WX-BFF-03 | T-05-02 | stub 替换后签名与源一致 | static | `diff` 源 vs enclave(ApiResult/BaseException) | ⬜ |
| 05-02 | 02 | 1 | WX-BFF-03 | — | UserContext/ResponseUtil stub 替换 | static | grep enclave 含源实测字段 | ⬜ |
| 05-03 | 03 | 2 | WX-BFF-03 | T-05-01 | GlobalExceptionHandler 限 basePackages | static | grep `basePackages = "com.spt.bas.purchase.wx.server"` | ⬜ |
| 05-04 | 04 | 3 | WX-BFF-03 | T-05-03 | 明文密钥落 dev yml(D-P5-05) | static | grep ewechat.config.corpid 等 | ⬜ |
| 05-05 | 05 | 4 | WX-BFF-03 | — | ApplicationStartup 含 WX BsDictUtil.init | static | grep 全限定 BsDictUtil 调用 | ⬜ |
| 05-06 | 06 | 5 | WX-BFF-03 | — | 编译零错 | compile | `mvn compile -pl zgbas-system` 零 `[ERROR]` | ⬜ |

---

## Wave 0 Requirements

Existing infrastructure covers all phase requirements. 承托层无新测试桩 — 复用 P4 已就位的 JDK 1.8 + Maven + Spring Boot 2.5.9 编译链。

---

## Manual-Only Verifications

| Behavior | Requirement | Why Manual | Test Instructions |
|----------|-------------|------------|-------------------|
| 启动含 WX beans GREEN | WX-ALIGN-02(Phase 8) | 需完整启动 | 留 Phase 8 ZgbasApplicationTest |
| `/wx/*` 字典缓存初始化 | Phase 3 登录缺口 | 需运行时 HTTP | 留 Phase 8 |

---

## Validation Sign-Off

- [x] All tasks have `<automated>` verify 或静态 grep 断言
- [x] Sampling continuity: 每 plan 有编译/grep 验证
- [x] Wave 0 = 现有基础设施覆盖
- [x] No watch-mode flags
- [x] Feedback latency < 60s
- [x] `nyquist_compliant: true` set in frontmatter

**Approval:** pending(inline research, 2026-07-23)
