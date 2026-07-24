# Phase 7: BFF edge 迁入 - Discussion Log

> **Audit trail only.** Do not use as input to planning, research, or execution agents.
> Decisions are captured in CONTEXT.md — this log preserves the alternatives considered.

**Date:** 2026-07-24
**Phase:** 7-BFF edge 迁入
**Areas discussed:** /wx/contract 冲突消歧, 验证深度, 承托缺口补齐策略, api/ 4 类落点

---

## 灰区① — /wx/contract 冲突消歧

| Option | Description | Selected |
|--------|-------------|----------|
| 方法级矩阵 + 不删类不改路由 | researcher 产重叠方法矩阵,重叠读方法最小让步(微调路径);WX 独有写操作 confirmReceipt/applyBill/applyPay 与报表独有读 findPageCtrContract 各守原路径。不删类、不改 WX 小程序可见路由、不改报表 web 消费者路由。blast radius 最小,契合 D-01。 | ✓ |
| 允许删冗余重叠读方法 | 若 researcher 证明 basWx 重叠读方法与报表 API 同源等价(都透传同一报表逻辑),删 basWx 侧重叠读,仅留 WX 写操作守 /wx/contract。更干净但触及 basWx 类体、偏离 verbatim。 | |
| 整类换基路径 | 冲突类之一整体换基路径(如 report 改 /rpt/wx/contract),一刀切绕开方法级纠缠。落地最快但触及报表 v1.0 落位或 WX 可见路由,偏离 D-01 最小消歧。 | |

**User's choice:** 方法级矩阵 + 不删类不改路由(推荐)
**Notes:** 🔴 本阶段头号阻塞。实测两端不只同基路径,还重叠约 10 个同名 POST 方法;且 `ContractController` 是透传同一套报表合同逻辑的 Feign(`IRptWxCtrContractClient`)+ 独有 WX 写操作,`RptCtrContractApi`(v1.0 报表迁移已落 admin)是读为主。方法集部分重叠(读碰撞、写仅 ContractController),`AmbiguousMappingException` 真正触发点是重叠读方法。

---

## 灰区② — 验证深度

| Option | Description | Selected |
|--------|-------------|----------|
| 编译门 + 静态路由矩阵,启动留 P8 | 与 P5/P6 一致:`mvn compile -pl zgbas-admin` 零 [ERROR] + SC#1 路由矩阵静态证明 /wx/* /ewechat/* /axq/* 无 ambiguous mapping。实际 Spring 启动 GREEN + /wx 非 404 + 自回环 proof 全留 P8。避免非 hermetic 启动假阳性。 | ✓ |
| 编译门 + 起一次 Spring 验 mapping 注册 | 编译门之外多起一次 Spring context 验 RequestMappingHandlerMapping 无 AmbiguousMappingException,与 P8 SC#2 启动 GREEN 重叠但更早暴露碰撞。需本地 export 启动密钥。 | |
| 仅编译门,矩阵降为副产物 | 严格同 P5/P6,仅 `mvn compile -pl zgbas-admin` 零 [ERROR];路由矩阵降为 researcher 副产物,不强制验收,碰撞由 P8 启动暴露。 | |

**User's choice:** 编译门 + 静态路由矩阵,启动留 P8(推荐)
**Notes:** SC#2 字面要求"启动无 ambiguous mapping",但启动测试非 hermetic(明文密钥态需本地 export,与 P3-P6 同契约)。选静态路由矩阵作强制验收产出,实际启动留 P8,规避假阳性(P6 executor 自报 GREEN 曾假阳性)。

---

## 灰区③ — 承托缺口补齐策略

| Option | Description | Selected |
|--------|-------------|----------|
| 按 P5/P6 惯例 inline 迁入所缺 | 缺啥补啥,承托类落 zgbas-system 飞地,与 P5/P6 一致。FeignHttpsConfig(WX Feign HTTPS wiring)一并迁;StockVirtualWxVo 等迁 vo 包。逐类追踪但保证 controller 真编译过。 | ✓ |
| 先 stub 过编译,实现延后 | 缺失类先最小 stub 过编译门(返回空/默认),真实实现留 P8 或后续。降低 P7 逐类追踪负担,但 stub 会带 runtime 风险且需 P8 填实。 | |
| 严格只迁 controller/API,缺口登记 todo | P7 严格只迁 11 controller+4 API+BasicErrorController;若缺口属 P5/P6 漏迁则登记 todo 回流该阶段,P7 不扩 scope。最严 scope 纪律但可能阻塞 controller 编译。 | |

**User's choice:** 按 P5/P6 惯例 inline 迁入所缺(推荐)
**Notes:** 实测坐实 P6 gotcha:`StockVirtualWxVo`/`FeignHttpsConfig` 单体缺失(`CtrContractVo`/`CtrProductVo`/`RptCtrContractSearch` 已在)。选 inline 迁入拒绝 stub,保证 controller 真编译过且不带 runtime 债。

---

## 灰区④ — api/ 4 类落点

| Option | Description | Selected |
|--------|-------------|----------|
| researcher 定性质后分落 admin/system | 先判 4 类是 @RestController 实控制器还是 Feign 契约接口(purchase/* 无前导斜杠路径疑为契约);控制器落 admin 包飞地,契约接口落 system(同 P3 client 契约)。避免 @FeignClient+@RestController 双注册。 | ✓ |
| 全部当控制器落 admin(D-05 字面) | 严格按 D-05 controller/API→admin,4 类全部落 zgbas-admin 包飞地。若实为契约接口需 researcher 标注防双注册;口径不改 ROADMAP。 | |
| 全部落 system 作 Feign 契约 | purchase/* 路径更像内部契约,4 类落 zgbas-system 作 Feign 契约(同 P3)。但 ROADMAP SC#3 明列 4 API→admin,需同步更新口径与验收。 | |

**User's choice:** researcher 定性质后分落 admin/system(推荐)
**Notes:** `purchase/*` 基路径无前导斜杠是 Spring Cloud Feign 契约接口典型特征(类级 + 方法级 @RequestMapping),与控制器风格不同。不预设,以实测注解/消费者为准,防 @FeignClient+@RestController 同类双注册。

---

## Claude's Discretion

- **冲突矩阵逐方法 owner 判定** —— researcher 实测两端每个 `final path` 的真正消费者(WX 小程序 vs 报表 web 消费者),定 owner 与最小让步侧(D-P7-01 给底线)。
- **4 个 api/ 类性质判定** —— researcher 据注解/实现/被调方定 @RestController vs Feign 契约(D-P7-04 给落点分流)。
- **承托缺口逐类清单** —— 编译驱动,researcher/planner 编译时暴露即 inline 迁入并记 inventory(D-P7-03 给策略不枚举)。
- **`BasicErrorController` 落位/防撞** —— 落 admin,确认 `/error` 路径不与单体默认 error handler 碰撞,必要时限 basePackages 或命名让步(参照 D-P5-02 GlobalExceptionHandler 限域)。
- **11 controller + 4 API + BasicErrorController 编译波次** —— planner 据 inter-controller 依赖与缺口补齐顺序排 wave。

## Deferred Ideas

- 🔴 `/wx/contract` 冲突实际 Spring 启动验证 → Phase 8(本阶段仅静态矩阵)。
- `SignContractTask`(3 @XxlJob)+ `WxJobConfig`(xxl-job 残留)→ v1.3 quartz gap-closure(与 P6 PurchaseCommand 同批)。
- WX 小程序前端(管理页/UI)→ v2(纯 API 服务,v1.2 不迁)。
- JWT/Shiro 认证统一 / basWx Feign 自回环崩为直注 → future(方案1 保留)。
