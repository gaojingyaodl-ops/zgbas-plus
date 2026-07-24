---
phase: 5
plan: 05-02
subsystem: carrier-layer (承托底座 II — util)
tags: [migration, wx, util, verbatim-port]
requires: [05-01]
provides:
  - WX util 工具层(16 类)就位;ResponseUtil/UserContext 源实测替换 stub
  - UserHelper 就位(解除 MessageEnums→UserHelper 前向引用)
affects:
  - 05-03 横切 bean(ServiceAop 等引用 util)
  - 05-04 cache + OcrUtils/OcrHelper(HttpUtils 已就位供 OcrUtils.doPost 调用)
  - 05-05 RequestListener(清理 UserContext ThreadLocal)
tech-stack:
  added: [gui.ava:html2image:0.9 (zgbas-system)]
  patterns: [verbatim-port, stub-overlay, dead-code-verification]
key-files:
  created:
    - "zgbas-system/.../purchase/wx/server/util/ (16 类 verbatim)"
  modified:
    - "zgbas-system/.../util/ResponseUtil.java (stub→source,identical)"
    - "zgbas-system/.../util/UserContext.java (stub→source,identical)"
    - "zgbas-system/pom.xml (+gui.ava:html2image:0.9)"
key-decisions:
  - "HttpUtils 非死码:OcrUtils.doPost 同包调用它(05-04 到位)→ 迁入"
  - "D-P5-14:ResponseUtil/UserContext 源实测替换 P4 stub"
  - "JwtUtil 保留 P4 实测(288L),不覆盖;OcrUtils/OcrHelper 留 05-04"
  - "Rule 2:gui.ava:html2image 加入 zgbas-system(ConvertUtils.html2Img 被 UserInfoService P6 调用)"
requirements-completed: []
requirements-addressed: [WX-BFF-03]
duration: "~10 min"
completed: 2026-07-24
---

# Phase 5 Plan 02: 承托底座 II — util(含 stub 替换 + HttpUtils 死码核实) Summary

承托层第二波:把 basWx 16 个 util 工具类 verbatim 落入包飞地,ResponseUtil/UserContext 以源实测实现替换 P4 stub(D-P5-14),核实 HttpUtils 非死码(OcrUtils 同包调用 → 迁入)。结果:reactor 编译全 GREEN,顺带解除 Wave 1 的 MessageEnums→UserHelper 前向引用。

- **Duration:** ~10 min · **Tasks:** 2 · **Files:** 16 util + 1 pom
- **Commits:** `1ba2295`(util 迁入 + stub 替换 + gui.ava pom)

## Tasks Executed

### Task 1 — HttpUtils 死码核实(先决)
- grep `import ...util.HttpUtils` → 0(同包无需 import)。
- grep `HttpUtils.` 调用点 → **1 命中**:`OcrUtils.java:135 HttpUtils.doPost(...)`(同包,05-04 到位)。
- 结论:**HttpUtils 非死码**(唯一 caller OcrUtils 在 05-04)→ Task 2 迁入。

### Task 2 — verbatim 迁入 util(16 类)+ 替换 ResponseUtil/UserContext stub
- 迁入 16 类:CommonUtil/ConvertUtils/DateUtils/DeptUtils/FreemarkerUtil/HttpUtils/NumUtils/RsaUtil/SignUtil/SM3/SMSUtils/StrUtils/UploadHelper/UserHelper/ResponseUtil/UserContext。
- ResponseUtil / UserContext 以源实测覆盖 P4 stub(D-P5-14),与源字节一致。
- 不迁:JwtUtil(P4 实测 288L 保留)、OcrUtils/OcrHelper(留 05-04 依赖 OcrConfig)。

## Acceptance Criteria Results

| Criterion | Result |
|---|---|
| util/ 含 15-16 目标类(+JwtUtil 保留) | ✅ 17 文件(16 迁入 + JwtUtil) |
| ResponseUtil.java 与源 diff 一致(非 stub) | ✅ IDENTICAL |
| UserContext.java 与源 diff 一致(非 stub) | ✅ IDENTICAL |
| JwtUtil.java 不被覆盖(P4 实测保留) | ✅ 288L 未动 |
| 每文件 `package com.spt.bas.purchase.wx.server.util;` | ✅ ALL MATCH |
| HttpUtils 死码结论记入 SUMMARY | ✅ 非死码 → 迁入 |

## Deviations from Plan

**[Rule 2 — 缺失关键依赖] `gui.ava:html2image:0.9` 加入 zgbas-system/pom.xml**
- Found during: Task 2 编译验证。
- Issue: `ConvertUtils.html2Img()` import `gui.ava.html.image.generator.HtmlImageGenerator`;该 artifact 仅在 `zgbas-admin/pom.xml`(Phase 4 D-P2-08),zgbas-system classpath 缺失。
- 验证非死码:`UserInfoService:608 ConvertUtils.html2Img(html, "png")`(Phase 6 service 实际调用)。
- Fix: 在 zgbas-system/pom.xml 加入同一 artifact/version(gui.ava:html2image:0.9),verbatim 保 ConvertUtils。
- Files modified: `zgbas-system/pom.xml`
- Commit: `1ba2295`。

- Total deviations: 1 auto-fixed(missing-critical dependency)。**Impact:** 无行为影响;ConvertUtils 全功能可用。

## Verification

- util 文件数:`ls .../util/*.java | wc -l` = **17**(16 迁入 + JwtUtil)✅(plan 期望 17)
- reactor 编译:**EXIT=0,0 ERROR** —— GREEN。hsoft.file/push.sdk、auth.sdk、freemarker、spt-tools 均在单体 classpath(#7 外部集成保留),无 classpath 缺口(仅 gui.ava 已补)。
- Wave 1 残留(MessageEnums→UserHelper)已随 UserHelper 迁入解除。

## Self-Check: PASSED

所有 acceptance criteria 通过;HttpUtils 非死码结论落实;1 个 deviation 已 auto-fix;reactor 全 GREEN。

## Next

Ready for **05-03**(灰区 A:4+ 全局横切 bean 逐项最小消歧 + exception/ 3 类,SecurityException 替换 stub)。横切 bean 是启动接线(05-05)与认证链路的前置。
