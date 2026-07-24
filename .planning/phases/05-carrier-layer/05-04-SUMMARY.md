---
phase: 5
plan: 05-04
subsystem: carrier-layer (灰区 B — 外部 wrapper + cache + 明文密钥)
tags: [migration, wx, cache, ocr, secrets, gray-area]
requires: [05-03]
provides:
  - WX cache 工具(BsDictUtil WX 版 + RedisCache)+ OCR 外部 wrapper(OcrUtils/OcrHelper)
  - WX 外部集成明文配置(ewechat/aliyun.ocr/jinxin)落 dev yml
affects:
  - 05-05(BsDictUtil.init 启动期初始化依赖 cache 就位)
  - Phase 6(EweChatApi 依赖 RedisCache+EweChatConfig;JinXinApi 依赖 JinXinConfig;IBsDictService impl 注入 BsDictUtil)
tech-stack:
  added: []
  patterns: [verbatim-port, plaintext-secrets, missing-interface-migration, http-boundary-preserved]
key-files:
  created:
    - "zgbas-system/.../wx/server/cache/{BsDictUtil,RedisCache}.java"
    - "zgbas-system/.../wx/server/util/{OcrUtils,OcrHelper}.java"
    - "zgbas-system/.../wx/server/service/IBsDictService.java (deviation: iface only, impl→P6)"
  modified:
    - "zgbas-admin/src/main/resources/application-dev.yml (+ewechat/aliyun.ocr/jinxin 明文)"
    - ".planning/todos/pending/rotate-leaked-prod-credentials.md (+Phase 5 三组)"
key-decisions:
  - "D-P5-05:WX 外部集成密钥明文进 dev yml(用户锁定 option3,覆盖安全铁律)"
  - "D-16/D-17:OCR 远端维持 HTTP 边界(HttpUtils.doPost,05-02 已迁)"
  - "Rule 2:迁 IBsDictService 接口(Phase 6 service scope)—— BsDictUtil.getBean 需类型,承托层须在 05-06 编译;impl→Phase 6"
requirements-completed: []
requirements-addressed: [WX-BFF-03]
duration: "~15 min"
completed: 2026-07-24
---

# Phase 5 Plan 04: 外部 wrapper + cache + 明文密钥落 yml(灰区 B) Summary

灰区 B:迁入 cache(BsDictUtil WX 版 + RedisCache)与 OCR 外部 wrapper(OcrUtils/OcrHelper,维持 HTTP 边界),把 WX 专用密钥(ewechat/aliyun.ocr/jinxin)以明文实测值落入 application-dev.yml(D-P5-05),并在 rotate-credentials todo 登记三组新泄漏密钥。

- **Duration:** ~15 min · **Tasks:** 4 · **Files:** 5 新 + 2 改
- **Commits:** d0dcccd(T1 cache+iface)/ 67d6d40(T2 OCR)/ bc901fc(T3 yml)/ 17aec97(T4 todo)

## Tasks Executed

### Task 1 — cache/(BsDictUtil WX 版 + RedisCache)
- verbatim 迁入 `cache/BsDictUtil`(WX 字典缓存,与单体 `bas.server.cache.BsDictUtil` 同名不同包,共存)+ `RedisCache`(依赖 P4 RedisConfig)。
- 包名 `com.spt.bas.purchase.wx.server.cache` 保留。

### Task 2 — OCR 外部 wrapper(util/OcrUtils + OcrHelper)
- verbatim 迁入。远端 OCR 维持 HTTP 边界(D-16/D-17):`OcrUtils` 用 `HttpUtils.doPost`(05-02 已迁,非死码),不内联 OCR 逻辑。

### Task 3 — application-dev.yml 明文配置(D-P5-05)
- 追加三块:`ewechat.config.*`(corpid/corpsecret/agentid/messageUrl)、`aliyun.ocr.*`(appcode/host/8 OCR URL)、`jinxin.*`(host/livingUrl/merchNo/merchKey/merchRsaPrivateKey/keyStore/trustStore/timeouts/tmpFilePath)。
- 键值用源实测值;`merchNo` 加引号防 YAML 把前导零当八进制/整数解析丢精度。
- 不动既有 key(datasource/spring.redis/wx.miniapp/jwt.config 全保留)。Ruby YAML 解析 OK。

### Task 4 — rotate-leaked-prod-credentials todo 登记
- 追加(非覆盖)Phase 5 三组:EweChat(corpsecret)、Aliyun OCR(appcode)、JinXin(merchKey + RSA 私钥 + keystore/truststore 密码)。
- 每组标来源(Phase 5/D-P5-05)+ 落点(application-dev.yml)+ 轮换优先级(JinXin RSA = critical)。Phase 2 原有条目保留。

## Acceptance Criteria Results

| Criterion | Result |
|---|---|
| cache/ 含 BsDictUtil + RedisCache;两类同包共存 | ✅ 单体 bas.server.cache.BsDictUtil 仍在 |
| OcrUtils + OcrHelper 就位;HTTP 边界维持 | ✅ HttpUtils.doPost |
| yml 含 corpid/appcode/merchKey 三 sentinel | ✅ count=3 |
| 不含重复既有 key | ✅ wx/jwt/spring 顶层各 1 |
| YAML 可解析 | ✅ ruby parse OK |
| todo 含 EweChat/Aliyun OCR/JinXin + Phase 5 | ✅ count=9 |
| todo 既有条目未删 | ✅ append-only |
| 每条标 Phase 5 + D-P5-05 + 落点 | ✅ |

## Deviations from Plan

**[Rule 2 — 缺失关键类型] 迁入 WX `IBsDictService` 接口(Phase 6 service scope)**
- Found during: Task 1 编译验证(BsDictUtil 17 个级联编译错误)。
- Issue: `BsDictUtil.init()` 第 45 行 `SpringContextHolder.getBean(IBsDictService.class)` 引用 `com.spt.bas.purchase.wx.server.service.IBsDictService`(WX service 接口,Phase 6 scope),承托层须在 05-06(SC#3)编译,不能跨 phase 残留。
- Fix: verbatim 迁入 **接口**(仅 interface,leaf:`extends IBaseService<BsDictType>`,引用类型全在 classpath)。**impl + bean 留 Phase 6**;runtime `getBean` 是 Phase 8 启动关注点,非本期编译门。
- Files modified: `zgbas-system/.../wx/server/service/IBsDictService.java`
- Verification: reactor EXIT=0(17 级联错误全解)。
- Commit: d0dcccd。Phase 6 计划须知:IBsDictService 接口已在位,勿重复迁。

- Total deviations: 1 auto-fixed(missing-critical interface)。**Impact:** 无行为影响;承托层编译 GREEN。

## Verification

- reactor 编译:**EXIT=0,0 ERROR** —— GREEN。
- 静态自检:`grep -cE "ewechat\.config|aliyun\.ocr|jinxin\." application-dev.yml` ≥ 15 ✅;cache/Ocr 文件就位 ✅。

## Self-Check: PASSED

所有 acceptance criteria 通过;cache + OCR wrapper 就位(HTTP 边界维持);三组明文密钥落 yml(D-P5-05);rotate todo 登记;1 deviation 已 auto-fix;reactor GREEN。

## Next

Ready for **05-05**(灰区 C:启动接线 ApplicationStartup 合并[Phase 3 登录缺口同源修复]+ RequestListener ThreadLocal 清理 + D-15a/b inventory 交付)。BsDictUtil.init 的启动期调用在此 plan 接入。
