---
phase: 07-bff-edge
status: passed
verified_by: inline (gsd-verifier subagent unavailable — GLM gateway outage; mirrors P3-P6)
verified_date: 2026-07-24
acceptance_basis: D-P7-02(静态面 SC#1-4 为 P7 权威;runtime 启动 proof 留 Phase 8 WX-ALIGN)
phase_req_ids: [WX-BFF-01, WX-BFF-02]
---

# Phase 7 Verification — BFF edge 迁入

## Phase Goal
把 basWx BFF edge(11 controller + 4 api + BasicErrorController + 2 承托缺口)迁入单体,三族路由(/wx/* + /ewechat/* + /axq + /purchase/*)无 ambiguous mapping,编译零 ERROR。runtime 启动 proof 留 Phase 8。

## Verification Outcome: **PASSED(静态面 SC#1-4 全绿)**

| SC | 要求 | 证据 | 状态 |
|---|---|---|---|
| SC#1 | 三族路由 inventory | `07-ROUTE-MATRIX.md` §2(grep 实测 11 controller + 4 api + /error + resident report /wx/*×2) | ✅ |
| SC#2 | 无 ambiguous mapping | `07-ROUTE-MATRIX.md` §3/§4(唯一字面重复 /wx/contract 经 /spt-bas-report 前缀隔离) | ✅ 静态 |
| SC#3 | 编译门 | `mvn compile -pl zgbas-admin -am` BUILD SUCCESS,[ERROR]=0,symbol errors=0 | ✅ |
| SC#4 | locale 无关 grep 零命中 | `cannot find symbol\|找不到符号` = 0 | ✅ |

## Requirement Traceability
| Req | 交付 | 证据 plan |
|---|---|---|
| WX-BFF-01 | 11 BFF controller(7 /wx/* + 3 /ewechat/* + 1 /axq)+ BasicErrorController | 07-02, 07-04, 07-01 |
| WX-BFF-02 | 4 api(purchase/* CRUD,extends BaseApi) | 07-03 |

## Must-Haves vs Codebase(实测)
- ✅ 11 controller + BaseController 落 admin controller/(12 文件,包名 com.spt.bas.purchase.wx.server.controller 保留)
- ✅ ContractController `@RequestMapping("/wx/contract")` + 16 @PostMapping verbatim,零路由编辑(D-P7-01-RESOLVED)
- ✅ 4 api extends BaseApi<T>,零 @FeignClient(D-P7-04-RESOLVED)
- ✅ BasicErrorController 落 admin config/ + excludeFilter 防同名 bean 冲突(07-04 偏差,plan 预判分支)
- ✅ StockVirtualWxVo + FeignHttpsConfig 落 zgbas-system wx/client/{vo,config}(07-01)
- ✅ 编译门绿(07-06)

## Code Review(inline,2 非 verbatim delta)
> gsd-code-reviewer subagent 不可用(GLM gateway outage);对 verbatim 副本(diff 零差异已证保真)免审,聚焦 2 处真实代码 delta。

| Delta | 文件 | 审查结论 |
|---|---|---|
| excludeFilter + javadoc | ZgbasApplication.java | **PASS** — 延续 Plan04-05/04-06 前例(assignable-type filter 防同名 BasicErrorController bean 冲突),javadoc 完整;无 CRITICAL/HIGH |
| setTimestamp 适配 | BasicErrorController.java | **PASS** — `new Date()`→`LocalDateTime.now()` 对标 active bas.server.config 版,单体 ErrorResp(LocalDateTime) 签名对齐;移除未用 import;行为等价 |
| swagger 依赖 ×2 | zgbas-admin/pom.xml | **PASS** — verbatim 源版本(1.6.6/1.9.6),annotations-only(springfox provided 不传递),无 SwaggerConfig runtime 副作用 |

**Verdict:** 无 CRITICAL/HIGH。2 偏差均为编译必需 + 行为等价,无 scope creep。

## Regression Gate
- 全 reactor 编译绿(07-06 BUILD SUCCESS:common+framework+system+quartz+admin 5 模块)
- P7 仅触及 zgbas-system wx/client(2 承托,additive)+ zgbas-admin(additive + 2 最小修复);**无 prior-phase 模块源码改动** → 无编译回归
- 非 hermetic 启动测试不在 P7 scope(runtime proof 留 P8,D-P7-02)

## Human Verification / Deferred to Phase 8(runtime)
> P7 验收 = 静态面(D-P7-02)。以下 runtime 项显式留 Phase 8(WX-ALIGN-01/02/03):
- [ ] 启动 GREEN(无 AmbiguousMappingException / ConflictingBeanDefinitionException)
- [ ] /wx/* 非 404(WX 小程序可见路由可达)
- [ ] ContractController 透传链自回环(/wx/contract → IRptWxCtrContractClient → /spt-bas-report/wx/contract)
- [ ] swagger-bootstrap-ui 入 classpath 后无 swagger auto-config 副作用(预期无,无 SwaggerConfig bean)
- [ ] P8 回退预案(07-ROUTE-MATRIX §6)若 /wx/contract ambiguous 触发(理论不应)

## Conclusion
Phase 7 静态面 SC#1-4 全绿,目标达成。runtime 启动 proof 为 Phase 8 显式 scope,不阻塞 P7 完成。**建议标记 Phase 7 完成,推进 Phase 8(对齐验证)。**
