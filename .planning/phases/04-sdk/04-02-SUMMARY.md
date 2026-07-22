---
phase: 4
plan: "04-02"
subsystem: sdk-infra
tags: [redis, fastjson2, serializer, configuration]
dependency_graph:
  requires: [04-01]
  provides: [RedisTemplate<Object,Object> bean, limitScript bean, FastJson2JsonRedisSerializer]
  affects: [zgbas-system/pom.xml, zgbas-system/src/main/java/com/spt/bas/purchase/wx/server/config/]
tech_stack:
  added: [fastjson2:2.0.4]
  patterns: [CachingConfigurerSupport, RedisSerializer<T>, DefaultRedisScript<Long>]
key_files:
  created:
    - zgbas-system/src/main/java/com/spt/bas/purchase/wx/server/config/FastJson2JsonRedisSerializer.java
    - zgbas-system/src/main/java/com/spt/bas/purchase/wx/server/config/RedisConfig.java
  modified:
    - zgbas-system/pom.xml
decisions:
  - "D-04-02-01: fastjson2:2.0.4 显式声明于 zgbas-system/pom.xml（Rule 3 修复）— 源 purchase-server 显式依赖 fastjson2 2.x，项目已有 fastjson 1.x 但 groupId/package 不同，无传递覆盖"
metrics:
  duration: "~3min"
  completed: "2026-07-22T08:15:39Z"
  tasks: 2
  files: 3
---

# Phase 4 Plan 02: Redis 配置类迁入 Summary

**One-liner:** FastJson2JsonRedisSerializer + RedisConfig 从 purchase-server 照搬迁入 zgbas-system，附加 fastjson2:2.0.4 依赖修复，zgbas-system mvn compile 零 ERROR。

## Tasks Completed

| Task | Name | Commit | Files |
|------|------|--------|-------|
| 1 | 创建 FastJson2JsonRedisSerializer.java | 2878cbb | zgbas-system/src/main/java/com/spt/bas/purchase/wx/server/config/FastJson2JsonRedisSerializer.java |
| 2 | 创建 RedisConfig.java + fastjson2 依赖修复 + 编译验证 | 316b28c | zgbas-system/src/main/java/com/spt/bas/purchase/wx/server/config/RedisConfig.java, zgbas-system/pom.xml |

## What Was Built

**Task 1 — FastJson2JsonRedisSerializer.java:**
- package: `com.spt.bas.purchase.wx.server.config`
- 泛型类 `FastJson2JsonRedisSerializer<T> implements RedisSerializer<T>`
- serialize: `JSON.toJSONString(t, JSONWriter.Feature.WriteClassName).getBytes(UTF-8)`
- deserialize: `JSON.parseObject(str, clazz, JSONReader.Feature.SupportAutoType)`
- 照搬无任何逻辑改动

**Task 2 — RedisConfig.java:**
- `@Configuration @EnableCaching`，extends `CachingConfigurerSupport`
- `redisTemplate` bean: key=StringRedisSerializer, value=FastJson2JsonRedisSerializer(Object.class)
- `limitScript` bean: Lua 限流脚本，`DefaultRedisScript<Long>`
- 照搬无任何逻辑改动
- mvn compile -pl zgbas-system 零 [ERROR] 确认

## Verification Results

```
test -f ...FastJson2JsonRedisSerializer.java → EXISTS
test -f ...RedisConfig.java                  → EXISTS
mvn compile -pl zgbas-system                 → 0 [ERROR]
```

## Deviations from Plan

### Auto-fixed Issues

**1. [Rule 3 - Blocking] fastjson2 传递依赖缺失，无法编译**
- **Found during:** Task 2 编译验证
- **Issue:** `FastJson2JsonRedisSerializer.java` 使用 `com.alibaba.fastjson2.*`（fastjson 2.x），但项目仅有 `com.alibaba:fastjson:1.2.75`（1.x），两者 groupId 和 package namespace 均不同，传递依赖无法覆盖
- **Root cause:** 源 purchase-server/pom.xml 显式声明了 `com.alibaba.fastjson2:fastjson2:2.0.4`，计划中"通过 zgbas-common 传递可用"的假设不成立
- **Fix:** 在 `zgbas-system/pom.xml` 添加 `fastjson2:2.0.4` 依赖（本地仓库已有该 jar）
- **Files modified:** zgbas-system/pom.xml
- **Commit:** 316b28c（与 Task 2 合并提交）

## Known Stubs

None — 两个文件为功能完整的配置类，无 placeholder 或 hardcoded empty 值。

## Threat Surface Scan

| Flag | File | Description |
|------|------|-------------|
| T-04-03 accept | FastJson2JsonRedisSerializer.java | JSONReader.Feature.SupportAutoType — 内部服务间通信，已在计划 threat_model 中标注为 accept |
| T-04-04 accept | RedisConfig（runtime） | Redis password=zg123456 明文于 application-dev.yml — Phase 4 明文密钥决策 D-P4 option3，已接受 |

## Self-Check: PASSED

- [x] FastJson2JsonRedisSerializer.java 存在于 zgbas-system/src/main/java/com/spt/bas/purchase/wx/server/config/
- [x] RedisConfig.java 存在于同一包路径
- [x] RedisConfig.java 含 @Configuration @EnableCaching
- [x] RedisConfig.java 含 `public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory)` 方法
- [x] zgbas-system/pom.xml 含 fastjson2:2.0.4
- [x] mvn compile -pl zgbas-system 零 [ERROR]
- [x] commit 2878cbb exists
- [x] commit 316b28c exists
