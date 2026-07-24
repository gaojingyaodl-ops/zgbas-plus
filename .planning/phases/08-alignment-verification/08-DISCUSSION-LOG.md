# Phase 8: 对齐验证 - Discussion Log

> **Audit trail only.** Do not use as input to planning, research, or execution agents.
> Decisions are captured in CONTEXT.md — this log preserves the alternatives considered.

**Date:** 2026-07-24
**Phase:** 8-对齐验证
**Areas discussed:** ① 验收深度 & runtime 修复预算, ② 启动测试 hermeticity & DB 依赖, ③ /wx/* 端点验证范围 & 通过判据, ④ WX Feign 自回环 proof 形态

---

## ① 验收深度 & runtime 修复预算

| Option | Description | Selected |
|--------|-------------|----------|
| ①-A 最小修复到 GREEN | 沿用 P5/P6/P7 编译门回流风格:每 runtime 失败最小适配,不扩 scope、不动业务语义;深层行为等价留手动 UAT/v1.3 | ✓ |
| ①-B 顺手根治 | 凡暴露 runtime 问题都追根因彻底修(含业务逻辑),即使非最小 diff;质量最高但可能膨胀、撞 dev DB/业务、推迟 milestone | |
| ①-C 验证为主,修复 defer | P8 只证三道闸;需改业务/数据的修复登记 todo 留 v1.3,启动若红则文档化不修 | |

**User's choice:** ①-A 最小修复到 GREEN
**Notes:** 一致延续 P3-P7"行为等价优先 + 最低级联风险 + 不扩 scope"。北极星"单进程跑全功能"要求 GREEN,故否决 ①-C(纯记录与验证里程碑定位不符);否决 ①-B(膨胀/撞业务/推迟)。

---

## ② 启动测试 hermeticity & DB 依赖

| Option | Description | Selected |
|--------|-------------|----------|
| ②-A 独立复跑 GREEN | 用户本地 `mvn test` 独立复跑(不靠 executor 自报);always-on @Test 全绿;@Disabled 真跑/写类手动启用;dev DB(sptbasdb_pd + 53 sys_job)可达为文档化前置;post-merge 独立复跑防 P6 假阳性 | ✓ |
| ②-B 新 test profile 自包含 | 新增 application-test.yml(plained + H2/embedded)让 mvn test 不依赖外部 dev DB;但 239 实体+mybatis+Quartz sys_job+Shiro 换 H2 风险极大,与行为等价冲突 | |
| ②-C 降级为启动不崩 | SC#2 降为 contextLoads + WX bean probe 绿即过;reachability/self-loop/写类全手动/defer | |

**User's choice:** ②-A 独立复跑 GREEN
**Notes:** **关键实测发现** —— `${DB_PASSWORD}`/`${SPT_APP_SECRET}` 占位符**仅**在 `application-prod.yml`(D-P2-13);`application-dev.yml`(测试 @ActiveProfiles("dev"))已明文(D-P4),故 `export` **不再需要**(修正 STATE.md line 83 旧表述)。dev DB 可达仍是硬前置(非 hermetic)。P6 executor 自报 GREEN 假阳性教训 → GREEN 必须独立复跑证实。

---

## ③ /wx/* 端点验证范围 & 通过判据

| Option | Description | Selected |
|--------|-------------|----------|
| ③-A 三族代表抽样 + 非404 | 三族(/wx + /ewechat + /axq)各挑代表端点 + /purchase/* api 代表;判据 = HTTP 非 404 即过(对齐既有 reachability:404=未注册=失败;2xx/3xx/401/403/400/500=命中=pass,WX 未认证预期 401/错误信封) | ✓ |
| ③-B 全端点矩阵扫 | 用 07-ROUTE-MATRIX 全端点(~30+)逐个探;最全但慢且多数 JWT-reject/401 信号稀释 | |
| ③-C 仅典型端点 | 仅 /wx/user/login + 1-2;契合 SC#3 字面最小但 /ewechat//axq 三族无证、覆盖不足 | |

**User's choice:** ③-A 三族代表抽样 + 非404
**Notes:** WX 走 JwtAuthenticationFilter(order=1)非 Shiro → 未认证预期 401/错误信封(非主域 302→/login)。不在 reachability 层纠结 500 —— 真实业务正确性由 ④ 自回环 proof + 手动 UAT 保证。端点清单 researcher 据 07-ROUTE-MATRIX.md §2 出。

---

## ④ WX Feign 自回环 proof 形态

| Option | Description | Selected |
|--------|-------------|----------|
| ④-A bean-resolve probe | 对齐 bas/report 前例:断言 purchaseWxServerConfig bean + 1-2 WX I*Client proxy 解析 + url 含 localhost:8080;cheap fail-fast wiring。③-A 已证 WX controller HTTP 可达,组合充分且不扩 scope | ✓ |
| ④-B always-on + @Disabled 双保险 | always-on bean-resolve + 一个 @Disabled 真实 round-trip proof(对齐写类真跑约定);最稳但工作量最大 | |
| ④-C 纯真实 HTTP round-trip | 真 触发 basServer→WX I*Client→localhost:8080→WX controller 证响应非404;最强单一证据但需定位触发路径 + 撞 DB/JWT/auth | |

**User's choice:** ④-A bean-resolve probe
**Notes:** **关键变化**:P7 迁了 WX controller,真实 HTTP 自回环首次可行 —— 但 ③-A 已在 HTTP 层覆盖"非404",故 ④ 只需 bean-resolve 证 wiring。另:**stale 注释清理**(application-dev.yml:49-51 + ZgbasApplication.java:131-134 关于"basWx v2-deferred 404"的注释,P7 已迁 controller 不再 404)纳入 D-P8-01 最小修复范畴。

---

## Claude's Discretion

- 三族代表端点具体清单(researcher 据 07-ROUTE-MATRIX §2)
- runtime 暴露问题的具体根因 + 最小修复 diff 边界(executor 实测启动日志后逐项定)
- WX bean-resolve probe 选哪 1-2 个 I*Client(优先被最多 service 调用的)
- probe 落位 + 命名(落 ZgbasApplicationTest.java,延续 `*probe`/`*Wiring_probe` 约定)
- dev DB 前置的具体确认步骤(researcher/planner 在 PLAN 显式列)

## Deferred Ideas

- 深层行为等价 → 手动 UAT / v1.3
- basWx 微信采购小程序前端 → v2
- JWT/Shiro 认证统一 → future
- basWx Feign 自回环崩为直注 → future
- xxl-job 残留(PurchaseCommand 定时触发 + SignContractTask 3 job)→ v1.3 quartz gap-closure
- 真实 HTTP 自回环 round-trip proof → future(③-A + ④-A 组合已充分)
- v1.2 里程碑归档 + tag → `/gsd-complete-milestone` 命令范畴(P8 GREEN 后触发)
- `rotate-leaked-prod-credentials`(high todo)→ P8 后、里程碑归档前处理(非验证 scope)
- `phase4-resolve-entity-schema-drift`(medium todo)→ v1.3(非验证 scope)
