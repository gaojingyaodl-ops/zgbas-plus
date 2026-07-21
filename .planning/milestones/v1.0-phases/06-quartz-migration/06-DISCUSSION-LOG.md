# Phase 6: 定时任务迁移 - Discussion Log

> **Audit trail only.** Do not use as input to planning, research, or execution agents.
> Decisions are captured in CONTEXT.md — this log preserves the alternatives considered.

**Date:** 2026-07-18
**Phase:** 6-定时任务迁移
**Areas discussed:** cron 数据来源策略, 验收范围 + dry-run 语义, handler 落位 + basWx 5 个取舍, 触发门面 + 告警/阻塞行为等价

---

## cron 数据来源策略

| Option | Description | Selected |
|--------|-------------|----------|
| 导出生产 cron→翻译 sys_job | 从生产 xxl-job admin DB 导出 job 列表（handler 名/cron/参数/路由）→ 翻译为 sys_job INSERT，迁完是"真定时" | ✓ |
| 仅手动触发，cron 留空 | sys_job 只建 handler 名+bean+method，cron 留空，满足成功标准 4 手动 dry-run | |
| 逐个业务方确认 cron | 根据 handler 语义 + 业务方逐个确认，成本高 | |

**User's choice:** 导出生产 cron→翻译 sys_job
**Notes:** 用户承诺提供 xxl-job admin（xxljob.totrade.cn）的 xxl_job_info 表导出。最贴"行为对齐旧系统"核心价值。planner 须作 checkpoint:human-blocked 等待导出物。

| Option | Description | Selected |
|--------|-------------|----------|
| 我翻译 + 你逐项核对 | Claude 翻译为 sys_job INSERT（cron 适配+invoke_target 构造），checkpoint:human-verify 交用户逐条核对 | ✓ |
| 你提供已对齐 sys_job 数据 | 用户前置工作量大（cron 已转 7 字段 + invoke_target 已写 bean.method） | |
| 我翻译直接落库不核对 | 最快但 cron 静默错配风险 | |

**User's choice:** 我翻译 + 你逐项核对（推荐）
**Notes:** 平衡自动化与安全。

| Option | Description | Selected |
|--------|-------------|----------|
| 分级：定时→NORMAL，手动→PAUSED，废弃→跳 | 行为最对齐（旧系统手动型本就不定时） | ✓ |
| 全部 NORMAL 默认 cron 兜底 | 可能让旧手动型变误定时 | |
| 只迁有 cron 的，无 cron 不迁 | 旧手动型丢失 | |

**User's choice:** 分级：定时→NORMAL，手动→PAUSED，废弃→跳
**Notes:** cron 翻译产物不再继续追问（足够清晰），进入下一灰区。

---

## 验收范围 + dry-run 语义

| Option | Description | Selected |
|--------|-------------|----------|
| 全量 bean 解析 + 抽样执行 | 全量 60+ handler 编译过+bean 解析，执行层抽 3-5 个代表性 dry-run | ✓ |
| 全量手动触发执行 | 最全覆盖，成本高+写类污染 dev 库 | |
| 仅 1 个任务 dry-run | 成功标准 4 字面底线，覆盖过薄 | |

**User's choice:** 全量 bean 解析 + 抽样执行
**Notes:** 对齐 D-P5-08 / D-P4-06。

| Option | Description | Selected |
|--------|-------------|----------|
| 分级：只读真跑/写类空跑 | 抽样优先只读/幂等类真跑；写类空跑（mock 短路，只验 bean+参数+日志） | ✓ |
| 全部真实执行 | 接受 dev 库 sptbasdb_pd 被改 | |
| 全部空跑 | 最安全但行为验证最弱 | |

**User's choice:** 分级：只读真跑/写类空跑
**Notes:** 保护 dev 库不被污染。

| Option | Description | Selected |
|--------|-------------|----------|
| 强：Scheduler 全量加载 cron | 启动期加载全量 sys_job，cron 全解析+trigger 可算下次触发+invoke_target bean.method 存在，翻译错 fail-fast | ✓ |
| 弱：仅 Spring bean 解析 | cron 错误后移到运行期/首次触发 | |

**User's choice:** 强：Scheduler 全量加载 cron
**Notes:** 最大化暴露 D-P6-01/02 cron 翻译风险。验收足够清晰，进入下一灰区。

---

## handler 落位 + basWx 5 个取舍

| Option | Description | Selected |
|--------|-------------|----------|
| handler 落 system，quartz 只放基础设施 | quartz 只放 RuoYi 基础设施，handler 随业务 service 落 system | |
| handler 落 zgbas-quartz | handler 与 RuoYi 基础设施同模块，quartz→system 依赖 | ✓ |
| handler 落 admin | handler 非 HTTP 控制器，语义不符 | |

**User's choice:** handler 落 zgbas-quartz
**Notes:** 用户决策：调度相关代码统一归 quartz 模块，便于运维定位。依赖方向 quartz→system 自洽。

| Option | Description | Selected |
|--------|-------------|----------|
| 不迁，随 v2 | basWx 5 个随业务 v2，本期不迁（实际迁 ~60） | ✓ |
| 迁代码+stub，PAUSED | @Autowired(required=false)+null 守卫，运行不跳错但不干活 | |
| 迁全部 65（需 stub） | purchase service 不在单体→需大量 stub | |

**User's choice:** 不迁，随 v2（实际迁~60）
**Notes:** 对齐 #14。实测口径：basServer 58 + reportServer 1 + web 1 = 60。

| Option | Description | Selected |
|--------|-------------|----------|
| 保源包名 照搬（D-P2-07） | 保 com.spt.bas.server.task 等，最小 import 改动 | |
| 改 com.spt.quartz.task | 对齐 RuoYi RyTask 与 zgbas-quartz 模块 | ✓ |

**User's choice:** 改 com.spt.quartz.task
**Notes:** ⚠ 首次偏离 D-P2-07 照搬铁律（仅限 handler 类，因落 quartz 模块）。research 须核 MQApi/BasCommandExecutor 对 handler 类的引用 + import 更新。

| Option | Description | Selected |
|--------|-------------|----------|
| 保 Controller API，不引入页面 | REST API 可手动触发/启停/传参，不引 RuoYi /monitor/job Thymeleaf | |
| 保 Controller + 引入 RuoYi 页面 | 连 Controller 带 RuoYi /monitor/job 模板一起引入，完整可视化管理台 | ✓ |
| 不引入 Controller | 只保 ScheduleConfig+domain/mapper/service/util，手动触发走运维直操 DB | |

**User's choice:** 保 Controller + 引入 RuoYi 页面
**Notes:** ⚠ 引入 RuoYi 前端，偏离照搬核心。菜单接入（zgbas 动态菜单 vs RuoYi 静态）留 research。落位足够清晰。（用户中途拒绝"继续/下一区"meta 提问，要求直接继续——本灰区捕获 4 决策后径直进最后一灰区。）

---

## 触发门面 + 告警/阻塞行为等价

| Option | Description | Selected |
|--------|-------------|----------|
| 保 HTTP 门面，内部改直调 service | MQApi/BasCommandExecutor HTTP 端点照搬，内部改直调 service（不经 quartz 调度），前端零改+同步语义不变 | ✓ |
| 保 HTTP 门面，走 quartz run(jobId) | 内部改走 sysJobService.run(jobId)，与定时同链+统一日志，但 quartz run 是异步线程池破坏同步语义 | |
| 去掉门面，统一 /monitor/job | 所有触发走 RuoYi 手动触发，前端原调用点 404 | |

**User's choice:** 保 HTTP 门面，内部改直调 service
**Notes:** sys_job 是定时调度入口，HTTP 门面是同步触发入口，两者共存。

| Option | Description | Selected |
|--------|-------------|----------|
| 仅保阻塞+日志，不保重试/告警 | 串行型→@DisallowConcurrent 保义，覆盖/丢弃→misfire 默认；失败仅 sys_job_log；重试/告警/路由不要求 | ✓ |
| 保阻塞+重试，不保告警 | RuoYi 用包一层 retry 或 simpleTrigger 实现重试 | |
| 全量等价含告警 | 自接邮件/推送（复用 PushClientHttp） | |

**User's choice:** 仅保阻塞+日志，不保重试/告警
**Notes:** 路由策略单体单节点不适用。全部 4 灰区完成，径直写 CONTEXT（用户拒绝 meta "ready for context" 提问）。

---

## Claude's Discretion

- xxl-job 删除边界（INFRA-03）：删 xxl-job-core jar + XxlJobSpringExecutor + xxl.job.* yml；保留 Spring @EnableScheduling ScheduleConfig（非 xxl-job，给 @Scheduled 用）。
- 写类空跑实现走 test-side mock，不加生产 dry-run 开关。
- 任务参数随 cron 导出一起翻译固化到 sys_job.args。
- cron 格式适配（xxl-job→quartz 字段数）属 D-P6-02 翻译流程一部分。
- web 模块那 1 个 handler 归类同其他。
- RuoYi quartz mapper mybatis namespace 独立不冲突（research 核 @MapperScan 放宽）。
- job_group 划分 / sys_job_log 保留时长留 planning。
- D-P2-07 偏离收口：D-P6-09 仅限 handler 类，其余资产仍守照搬。

## Deferred Ideas

- xxl-job 失败重试/告警邮件/路由等价 → 永久 Out-of-Scope（D-P6-12）
- xxl-job admin 服务退役 / xxl_job_info 表迁入单体 → 运维事务非本期
- basWx 5 handler + purchase 业务 → v2（#14，D-P6-08）
- 真实全量任务回归 + e2e → Phase 7
- 报表物理分页/补审计字段 → 永久 Out-of-Scope
- 全量实体 schema drift reconcile + ddl-auto=validate → tech debt（todo 保留 open）
- CR-01 轮换已泄漏生产库密码 → 跨阶段安全债
