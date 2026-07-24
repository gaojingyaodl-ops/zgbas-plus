# Phase 6: Service 层迁入 - Discussion Log

> **Audit trail only.** Do not use as input to planning, research, or execution agents.
> Decisions are captured in CONTEXT.md — this log preserves the alternatives considered.

**Date:** 2026-07-24
**Phase:** 6-Service 层迁入
**Areas discussed:** BaseService 签名适配落点, PurchaseCommand @XxlJob 处理, Interface/命名 verbatim vs 规范化, 验证深度

---

## ① BaseService/IBaseService 签名适配落点

| Option | Description | Selected |
|--------|-------------|----------|
| 逐类最小适配 | 改 WX service 适配当前内联 base,不动共享 base/IBaseService。主域已用同款 base 零兼容,blast radius 最小,契合 D-12 | ✓ |
| 建 WX 本地 WxBaseService 子类 | 新建 WxBaseService extends BaseService 集中 WX 共享逻辑,不动主域 base。仅当多类共享同一缺失方法时值得 | |
| 扩共享 inlined base | 给 BaseService/IBaseService 加方法,牵主域 533 service,最后手段 | |

**User's choice:** 逐类最小适配
**Notes:** 14 类 `extends BaseService<T>` 用的是主域同款内联 base(`PmProcessServiceImpl` 等已在用),实测兼容风险低。用户明确不动共享 base,避免牵动主域。建 WxBaseService 子类留作 researcher 发现多类共享缺失方法时的阈值选项。

---

## ② PurchaseCommand 的 @XxlJob 处理

| Option | Description | Selected |
|--------|-------------|----------|
| scrub @XxlJob 保 ICommand | 删 @XxlJob + XxlJobHelper import,保 ICommand 队列路径;定时触发延后 v1.3(同 SignContractTask) | ✓ |
| 整类延后 v1.3 | 连 ICommand 一起延后,但 D-P5-09 已并入 ApplicationStartup 注册,延后要回改启动接线 | |
| 现在接 RuoYi quartz | 把 executeCommand 接 sys_job。扩 P6 scope,与 v1.3 gap-closure 重叠 | |

**User's choice:** scrub @XxlJob 保 ICommand
**Notes:** PurchaseCommand 是硬地雷 —— `implements ICommand`(CommandExecutor 队列,services enqueue 依赖)+ `@XxlJob`(xxl-job 已删,类不在 classpath)。与 SignContractTask 同款地雷但不同处理:SignContractTask 整体延后,PurchaseCommand 因被 D-P5-09 启动接线消费必须本阶段保 ICommand 路径。scrub 最小 diff = 只删注解+import,不触 ICommand 业务体。

---

## ③ Interface / 命名 verbatim vs 规范化

| Option | Description | Selected |
|--------|-------------|----------|
| verbatim 保现状 | 保包名/类名/位置全 verbatim,命名混乱是源码既成事实。行为等价优先,compile 级联风险最低,契合 D-10 | ✓ |
| 仅补 Feign 注入必需接口 | Feign 自回环按契约调用,JinXinApi 按类型注入,实测不需补 | |
| 顺手规范化 | 统一 *ServiceImpl 后缀 + 补接口。churn 大、违背 verbatim、级联风险高 | |

**User's choice:** verbatim 保现状
**Notes:** 源码 `service/impl/` 20 类命名混乱(5 *ServiceImpl + 14 *Service,JinXinApi extends CommonUtil 无接口)是既成事实。Feign 自回环按契约接口调用、@Autowired 按类型注入,均不依赖补接口。verbatim 保现状与 D-10 一致。

---

## ④ 验证深度

| Option | Description | Selected |
|--------|-------------|----------|
| 仅编译门 | SC#4:mvn compile -pl zgbas-system 零 [ERROR]。与 P5 一致;runtime/启动留 Phase 8(SC#2/3) | ✓ |
| 编译门 + 启动 bean 装配 smoke | 多一层保险,但 P6 还无 controller,bean 装配验证信号有限 | |
| 编译门 + 全模块 compile | admin/common/framework/quartz/system 全零错,但 controller 在 P7 才迁,admin 可能仍有 WX 缺口 | |

**User's choice:** 仅编译门
**Notes:** 本阶段无 controller(BFF 在 P7),启动 bean 装配 smoke 信号有限;全模块 compile 因 admin 缺 controller 必然有缺口。所有 runtime 验证统一留 Phase 8 对齐验证。

---

## Claude's Discretion

- BaseService 签名不一致的具体方法级清单(researcher 实测每类 IXxxService vs 内联 IBaseService/IDataService 差异)
- 20 service 的编译波次/拓扑序(planner 据 inter-service 依赖图排 wave)
- scrub @XxlJob 的精确删行边界(researcher 标注,只删注解+import)

## Deferred Ideas

- PurchaseCommand 定时触发路径(@XxlJob scheduled trigger)→ v1.3 quartz gap-closure(本阶段仅 scrub 保 ICommand)
- SignContractTask(3 @XxlJob)→ v1.3 quartz gap-closure
- BaseService 共享 base 扩方法 / 建 WxBaseService 子类 → researcher 发现多类共享缺失方法时再评估
- JWT/Shiro 认证统一 → future
- basWx Feign 自回环崩为直注 → future
