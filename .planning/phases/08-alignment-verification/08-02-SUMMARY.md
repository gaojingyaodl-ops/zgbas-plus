---
plan: 08-02
phase: 8
wave: 2
status: complete
sc: [SC#1]
requirements: [WX-ALIGN-01]
decisions: [D-P8-01]
committed: (docs-only, no code change)
---

# 08-02: 全 reactor 编译门 — mvn compile + test-compile 零 [ERROR](SC#1, WX-ALIGN-01)

## Result

| 命令 | 退出 | `^\[ERROR\]` | symbol/package 错误(locale 无关) | 结论 |
|---|---|---|---|---|
| `mvn compile`(全 reactor,JDK8 + zg_settings.xml) | BUILD SUCCESS | **0** | **0** | SC#1 绿 |
| `mvn test-compile -pl zgbas-admin -am` | BUILD SUCCESS | **0** | **0** | 08-01 两 probe 编译通过 |

5 模块全 BUILD SUCCESS(zgbas-common / zgbas-framework / zgbas-system / zgbas-quartz / zgbas-admin)。P7 仅证 zgbas-admin;本阶段复核**全 reactor 5 模块**。

## 编译轮次

**1 轮即绿** —— 零残余问题直接绿,无最小修复回流(D-P8-01 边界未触发)。08-01 两新 probe(`wxEndpointsReachable_proof` + `wxPurchaseFeignSelfLoopbackWiring_probe`)test-compile 通过,证新端点字符串 / `I*Client` 全限定名 / `com.spt.tools.core.bean.LocalServerConfig` 类型解析无误。

## Commands used(acceptance 锁)

```bash
# JDK1.8 实测路径(本机默认 JDK21,每条 mvn 必前缀)
JAVA_HOME=/Users/alan/Library/Java/JavaVirtualMachines/corretto-1.8.0_482/Contents/Home
# 注:PLAN/RESEARCH 写的 /Library/Java/... 路径本机不存在;实测路径在 ~/Library/Java/JavaVirtualMachines/corretto-1.8.0_482

JAVA_HOME=$JAVA8 /Users/alan/App/apache-maven-3.8.6/bin/mvn compile \
  -s /Users/alan/App/apache-maven-3.8.6/zg_settings.xml          # 全 reactor,无 -pl

JAVA_HOME=$JAVA8 /Users/alan/App/apache-maven-3.8.6/bin/mvn test-compile \
  -pl zgbas-admin -am -s /Users/alan/App/apache-maven-3.8.6/zg_settings.xml
```

locale 无关 grep 验证(`cannot find symbol|找不到符号|程序包.*不存在|package .* does not exist|不兼容的类型`)零命中(P5 gotcha:中文 locale 下 mvn 输出"找不到符号")。

## Self-Check: PASSED

- `grep -cE '^\[ERROR\]' /tmp/p8-compile.log` = 0 ✓
- `grep -cE 'cannot find symbol|找不到符号' /tmp/p8-compile.log` = 0 ✓
- 5 模块全 BUILD SUCCESS ✓
- test-compile `-pl zgbas-admin -am` 退出码 0(08-01 probe 编译通过)✓
- 编译命令含 `-s zg_settings.xml` + `JAVA_HOME=Corretto-1.8` ✓
- 零残余问题,无最小修复 inventory(D-P8-01 未触发)

## key-files.created
(无源码变更 —— 编译驱动 plan,首次即绿。`/tmp/p8-compile.log` + `/tmp/p8-testcompile.log` 为复跑日志。)

## Decisions applied
- **D-P8-01** — 最小修复预算:本 plan 未触发(零残余问题直接绿);watchlist(swagger 注解依赖 / BasicErrorController excludeFilter / ErrorResp.setTimestamp)P7 已收口,编译期无残余暴露

## Notable deviations / Gotcha
- **JDK8 路径修正**:PLAN/RESEARCH 与记忆写的 `/Library/Java/JavaVirtualMachines/amazon-corretto-1.8.jdk/Contents/Home` 本机不存在;实测正确路径 = `~/Library/Java/JavaVirtualMachines/corretto-1.8.0_482/Contents/Home`(本机 3 个 JDK:25/21/1.8.0_482,默认 21)。**记忆需更新为实测路径**(下次复跑直接用)。

## What this enables
SC#1(全 reactor 编译门,WX-ALIGN-01)达成,作为 08-03 `mvn test` 独立复跑的编译前置。runtime 启动 + reachability + 自回环 GREEN 由 08-03 产出。
