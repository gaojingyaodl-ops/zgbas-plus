---
phase: 04-core-business
plan: 03
subsystem: infra
tags: [feign, rocketmq, infra, mechanical-migration, compile-unit]

requires:
  - phase: 04-01
    provides: rocketmq-spring-boot-starter:2.2.2 in zgbas-system pom (Wave 0)
  - phase: 04-02
    provides: 238 basClient @FeignClient contracts + 14 data carriers (Wave 1)
provides:
  - basServer infra 层照搬：cache/util/enums/annotation/filter/listener/command/event/rocketmq + 选择性 config 6（verbatim com.spt.bas.server.* 包名 D-P2-07）
  - 级联 pom 依赖补齐：flying-saucer-pdf:9.1.20 / itextpdf:5.5.13.3 / itext-asian:5.2.0 / spt-sign-client
affects: [04-04 (service), 04-05 (api + PM api), Phase 6 (xxl-job task), PM module]

tech-stack:
  added: [flying-saucer-pdf 9.1.20, itextpdf 5.5.13.3, itext-asian 5.2.0, spt-sign-client]
  patterns: [verbatim package copy (D-P2-07), structural compile-unit merge]

key-files:
  created:
    - zgbas-system/src/main/java/com/spt/bas/server/cache/** (7)
    - zgbas-system/src/main/java/com/spt/bas/server/util/** (34)
    - zgbas-system/src/main/java/com/spt/bas/server/{enums,annotation,filter,listener,command,event}/**
    - zgbas-system/src/main/java/com/spt/bas/server/rocketmq/** (14 safe)
    - zgbas-system/src/main/java/com/spt/bas/server/config/{WebAppConfig,BasicErrorController,RtConfig,GuTuConfig,BasPiccConfig,ScheduleConfig}.java (6)
  modified:
    - zgbas-system/pom.xml (PDF/itext/sign-client deps)

key-decisions:
  - "Rule 4 架构 checkpoint：源 basServer 模块编译拓扑不分 wave——infra↔service↔PM 互联，per-sub-wave 编译绿结构性不可达"
  - "rocketmq 实际安全文件 14（非 RESEARCH 评估的 22）：8 个 rocketmq/task/Synchronized*Task 是纯 @XxlJob handler，与排除的 basServer/task/23 同性质，正确推迟至 Phase 6"
  - "config EXCLUDES 遵守：FrameworkConfig 缺席（Phase 2 @Primary 去重）、BasJobConfig 缺席（xxl-job→P6）、task/ 缺席（P6）"
  - "用户决策 A（2026-07-17）：合并编译单元 + 吸收 PM——infra+service(04-04)+PM(service/dao/cache/util/annotation) 作为单一编译单元，编译门在单元齐后统一跑"

patterns-established:
  - "Structural compile-unit merge: 当源模块编译拓扑与 wave 分割冲突时，以源拓扑为准合并编译单元，编译门后移至单元完整时"

# Dependency graph (continued)
---

## What Was Built

Wave 2a infra 机械照搬（verbatim 包名 D-P2-07），单 commit `3c7cc52`：

| 包 | 文件数 | 说明 |
|----|--------|------|
| cache | 7 | WarehouseCache/FactoryCache/BsDictUtil 等（→ import service，见下） |
| util | 34 | BasBusinessUtil/DeptUtils/CommissionCalculateUtil/SMSUtils 等 |
| enums | 2 | |
| filter (+impl) | 6 | BudgetVerifyFilterImpl 等 |
| listener | 1 | ApplicationStartup |
| command | 2 | |
| event | 1 | |
| rocketmq | 14 | 安全文件（8 个 @XxlJob task 推迟 P6） |
| config | 6 | WebAppConfig/BasicErrorController/RtConfig/GuTuConfig/BasPiccConfig/ScheduleConfig |

EXCLUDES：FrameworkConfig（Phase 2 @Primary 去重）、BasJobConfig（xxl-job→P6）、task/（P6）、rocketmq/task/8（@XxlJob→P6）。

## Structural Checkpoint — Compile-Gate Deferred

`mvn -pl zgbas-system -am compile` = **320 ERROR**（orchestrator 独立复核，非 executor 自报）。这不是 placement 错误，而是**源模块编译拓扑与 wave 分割的结构性冲突**，3 个独立验证的论断：

1. **infra ↔ service 双向依赖（34 处）**：infra 11 文件 import `com.spt.bas.server.service`（FactoryCache/WarehouseCache/BudgetVerifyFilterImpl/BsDictUtil/BsCompanyOurUtil…）。源 basServer 把 infra+service+config 编译为**单一 Maven 模块**，wave 分割切开了不可分的编译单元。
2. **infra → PM 依赖（22 处）**：infra 12 文件 import `com.spt.pm.{annotation,cache,constant,dao,entity,service,util,vo}`。源 `basCore/pm/`（65 文件）Phase 2 仅内联 pmClient（entity/vo/inter/constant），其 service/dao/cache/util/annotation 层**无任何 Phase 4 plan 涵盖**。
3. **PM 是纯下行依赖（修正 executor 过度评估）**：orchestrator 独立验证 PM → basServer {api,task,rocketmq.task,dao} 引用**全为 0**。PM 自包含，仅自身 1 个 xxl-job task（→P6）+ 13 个 api REST 端点。

错误分类：`找不到符号` 108 / `com.spt.bas.server.service 不存在` 24 / `service.impl 不存在` 10（→Wave 2b 04-04）/ `com.spt.pm.* 不存在` 14（→PM）/ `bas.server.task 不存在` 8（→P6）/ wltea(IK Expression) + httpmime 依赖级联。

## Resolution — Decision A (User 2026-07-17)

合并编译单元 + 吸收 PM：
- 继续拷 04-04 service + PM(service/dao/cache/util/annotation ~50，排除 1 xxl-job task 至 P6) + 级联 pom(wltea/httpmime/QLExpress)
- 编译门在 infra+service+PM 齐后**统一跑一次**（Phase 1/2 gotcha-cascade 先例：完整编译单元齐则 320→…→0）
- PM api 13 文件并入 Wave 4（04-05）随 basServer api 一起
- 无源码改动、无 stub，对齐源拓扑 + 项目「行为等价」核心价值

config 6 文件**独立编译 0 错误**（已验证 placement 正确）。320 错误全在 infra 文件，待合并单元齐后消解。

## Deviations

- **Rule 4（架构）**：wave 结构与源编译拓扑冲突 → 决策 A 合并编译单元 + 吸收 PM（用户批准）。不重新规划，执行期吸收，orchestrator 跟踪延迟门。
- **Rule 3（pom）**：flying-saucer-pdf/itextpdf/itext-asian/spt-sign-client verbatim 源 pom。
- rocketmq INCLUDE 14（非 22）：RESEARCH §D-P4-04 高估，8 个 @XxlJob task 正确排除至 P6。

## Self-Check

- [x] infra + config 机械照搬完成，包名 verbatim（D-P2-07）
- [x] EXCLUDES 遵守（FrameworkConfig/BasJobConfig/task/rocketmq-task 缺席）
- [x] config 6 独立编译 0 错误
- [x] 级联 pom 依赖补齐
- [⏳ deferred] zgbas-system 全模块编译门 → 延迟至合并单元（04-04+PM）齐后，由 Wave 3 统一验收。**结构性延迟，非失败**——infra placement 已验证正确，待 service+PM 补齐后 320→0。

## Hand-off to Wave 3 (Merged Unit)

Wave 3 executor 须在 04-04 service + PM 拷贝完成后，跑合并编译门 `mvn -pl zgbas-system -am compile`，确认 `^\[ERROR\]` 归零——此门同时关闭 04-03 的延迟 gate。
