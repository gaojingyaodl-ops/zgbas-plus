# Phase 5: 承托层迁入 - Discussion Log

> **Audit trail only.** Do not use as input to planning, research, or execution agents.
> Decisions are captured in CONTEXT.md — this log preserves the alternatives considered.

**Date:** 2026-07-23
**Phase:** 5-承托层迁入
**Areas discussed:** 横切 bean 冲突收口 / 外部密钥 & config 对账 / 启动接线 & 模糊包归属

**Carry-forward note:** STATE.md 方案1 + D-01~D-19 已锁定(verbatim 嵌入 / 包飞地 / 承托→zgbas-system / 外部 HTTP 边界 / D-P4 明文密钥 / D-15a·b inventory)。本 phase 不重问已决项,仅讨论探查新发现的 3 个灰区。

---

## 灰区 A — 横切 bean 冲突收口

| Option | Description | Selected |
|--------|-------------|----------|
| 逐 bean 最小消歧 | WebAppConfig 剖到只留 CORS;GlobalExceptionHandler 加 basePackages;ServiceAop 迁入+显式注册 bean;TransactionConfig 跳过(死码) | ✓ |
| verbatim 全迁+撞了再修 | 最贴 verbatim 字面,但 multipartConfigElement @Bean 重名直接启动崩,需事后补丁 | |
| 都不迁复用单体 | 最简,但 WX 控制器失去 ApiResult 错误信封+CORS,行为不等价 | |

**User's choice:** 逐 bean 最小消歧(推荐)
**Notes:** 探查定位 4 个全局横切 bean 无法 verbatim 共存。WX WebAppConfig 与单体 bas.server.WebAppConfig 同名,`multipartConfigElement` @Bean 重名会触发 BeanDefinitionStoreException。GlobalExceptionHandler 是单体首个全局 @ControllerAdvice(catch Exception.class→ApiResult)。ServiceAop pointcut `com.*.*.*.*.*.service..`(7 段)与 spt-tools(4 段+注解)不重叠。TransactionConfig 全文件注释=死码。落地决策 D-P5-01~D-P5-04。

---

## 灰区 B — 外部密钥 & config 对账

| Option | Description | Selected |
|--------|-------------|----------|
| 占位符+延后填实测值 | 新密钥占位符落 dev yml,编译/wiring 就位,实测值延后;不新增真实密钥进 git | |
| 明文实测值直进 dev yml | 与 D-P4 明文策略最一致,但把 EweChat/JinXin/OCR 真实密钥提交进 git | ✓ |
| 新密钥走 env 外置 | 最安全,但与 D-P4(撤销外置改明文)相左 | |

**User's choice:** 明文实测值直进 dev yml(**覆盖 Claude 推荐的占位符**)
**Notes:** 用户明确选与 D-P4 一致性优先,接受新增真实密钥进 git。rotate-credentials(high-priority todo)须显式登记 EweChat/JinXin/OCR 三组新密钥。另定 WxJobConfig 跳过(xxl-job 已删)、config/18→~8-9 对账(D-P5-05~D-P5-08)。EweChat corpid/corpsecret 当前在 EweChatApi URL 模板 `{0}{1}` 由参数传入,researcher 须查清来源后明文化。

---

## 灰区 C — 启动接线 & 模糊包归属

| Option | Description | Selected |
|--------|-------------|----------|
| 并入单体防双跑+分类 | WX 启动逻辑并入单体已有 ApplicationStartup(只追加 BsDictUtil.init);RequestListener 迁入;SignContractTask+WxJobConfig 延后 v1.3;TestJob/DBDocTool 丢弃 | ✓ |
| WX ApplicationStartup verbatim 迁入 | @Component 自注册第二监听器,CommandExecutor 同队列两线程竞态+DictUtil.init 双调 | |
| 启动监听全延后 Phase 8 验 | D-14a/b 原说 Phase 5/8 验,但 BsDictUtil.init 是 /wx/* runtime 硬依赖,延后启动即坏 | |

**User's choice:** 并入单体防双跑+分类(推荐)
**Notes:** 探查发现单体已有 `bas.server.listener.ApplicationStartup`(已 DictUtil.init+CommandExecutor 起线程),WX 同名监听器再迁会双跑(直击 Phase 3 登录缺口同源)。落地决策 D-P5-09(并入,只追加 BsDictUtil.init+PurchaseCommand)/D-P5-10(RequestListener 迁入)/D-P5-11(SignContractTask 延后 v1.3)/D-P5-12(TestJob+DBDocTool 丢弃)/D-P5-13(PurchaseCommand 归 Phase 5)。

---

## Claude's Discretion

- ScheduleConfig 是否与单体已有调度器重复(researcher 查后定并入/新建)
- SwaggerConfig 是否迁(取决于单体是否已有 Swagger)
- JinXinApi 归 Phase 5 vs Phase 6(物理在 service/impl,职责是外部 HTTP wrapper)

## Deferred Ideas

- WxJobConfig + SignContractTask(xxl-job 残留)→ v1.3 quartz gap-closure
- TestJob + DBDocTool(开发工具)→ 丢弃
- JWT/Shiro 认证统一、Feign 自回环崩为直注 → future(方案1 不做)
- rotate-leaked-prod-credentials、phase4-resolve-entity-schema-drift → todo/v1.3(reviewed 不 fold)
