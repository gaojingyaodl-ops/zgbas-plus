---
phase: 4
plan: "04-01"
subsystem: sdk-infra
tags: [pom, redis, weixin-sdk, jwt, configuration]
dependency_graph:
  requires: []
  provides: [spring-boot-starter-data-redis, weixin-java-miniapp:3.8.0, wx.miniapp-config, jwt.config]
  affects: [zgbas-system/pom.xml, application-dev.yml]
tech_stack:
  added: [spring-boot-starter-data-redis, weixin-java-miniapp:3.8.0]
  patterns: [Spring Boot BOM version management, explicit version pin]
key_files:
  created: []
  modified:
    - zgbas-system/pom.xml
    - zgbas-admin/src/main/resources/application-dev.yml
decisions:
  - "D-03: spring-boot-starter-data-redis 无 version（BOM 管理），weixin-java-miniapp 显式 3.8.0"
  - "D-10: wx.miniapp.configs[0].* 落 application-dev.yml（明文，与 Phase 4 明文密钥策略一致）"
  - "msgDataFormat 使用 JSON（非源文件中的 JSONa），符合计划规格"
metrics:
  duration: "~8min"
  completed: "2026-07-22T08:07:41Z"
  tasks: 2
  files: 2
---

# Phase 4 Plan 01: pom 依赖声明 + application-dev.yml 配置 Summary

**One-liner:** Redis starter (BOM) + weixin-java-miniapp:3.8.0 pom 声明，wx.miniapp.configs + jwt.config YAML 块追加，Wave 2 Java 类迁移的前置配置就位。

## Tasks Completed

| Task | Name | Commit | Files |
|------|------|--------|-------|
| 1 | zgbas-system/pom.xml — 追加 Redis 和 weixin-java-miniapp 依赖 | afff6de | zgbas-system/pom.xml |
| 2 | application-dev.yml — 追加 wx.miniapp 和 jwt.config 配置块 | f7a028c | zgbas-admin/src/main/resources/application-dev.yml |

## What Was Built

**Task 1 — pom.xml 依赖：**
- `spring-boot-starter-data-redis`（无 version 标签，由 Spring Boot 2.5.9 BOM 管理，per D-03）
- `weixin-java-miniapp:3.8.0`（显式版本，非 BOM，per D-11）
- `mvn dependency:resolve -pl zgbas-system` 零 [ERROR]，`mvn compile -pl zgbas-system` 零 [ERROR]

**Task 2 — YAML 配置块：**
- `wx.miniapp.configs[0].*`：appid=wxdfdcede72b54ff25 / secret / token / aesKey / msgDataFormat=JSON（per D-10）
- `jwt.config.key=sgcoding / ttl=600000 / remember=604800000`（JwtConfig @ConfigurationProperties prefix=jwt.config）
- `spring.redis.*` 已存在于文件（host/port/password/database），未重复添加（per D-01）

## Verification Results

```
mvn dependency:resolve -pl zgbas-system → 0 [ERROR]
mvn compile -pl zgbas-system          → 0 [ERROR]
grep appid application-dev.yml         → 1 match (wxdfdcede72b54ff25)
grep sgcoding application-dev.yml      → 1 match
grep "host: 47.104.15.98" application-dev.yml → 1 match (no duplication)
```

## Deviations from Plan

None — plan executed exactly as written.

**Note:** 源文件 `application.properties` 中 `msgDataFormat=JSONa`（疑似源码笔误），计划明确规格为 `JSON`，按计划规格执行。

## Threat Surface Scan

| Flag | File | Description |
|------|------|-------------|
| T-04-02 accept | application-dev.yml | wx.miniapp.secret 明文（1a4310766e9f32b90ca55c0af688c17e）已纳入 Phase 4 明文密钥决策，dev 配置，非 prod |

## Self-Check: PASSED

- [x] zgbas-system/pom.xml 含 spring-boot-starter-data-redis（无 version）
- [x] zgbas-system/pom.xml 含 weixin-java-miniapp:3.8.0
- [x] application-dev.yml 含 wx.miniapp.configs[0].appid=wxdfdcede72b54ff25
- [x] application-dev.yml 含 jwt.config.key=sgcoding
- [x] spring.redis.host 仅出现一次（无重复）
- [x] commit afff6de exists
- [x] commit f7a028c exists
