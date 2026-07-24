---
plan_id: 06-02
phase: 6
wave: 1
title: Service 迁入 II — 逻辑型 leaf(Apply)+ HTTP wrapper(JinXinApi)
status: complete
commits: [06-02-task1, 06-02-task2]
requirements: [WX-SERVICE-01]
---

# 06-02 Summary

**What was built:** Two logic-type leaves migrated into the enclave. `ApplyServiceImpl` (plain `implements IApplyService`, no BaseService) — business orchestration calling the main domain via Feign self-loop (`IPmApproveClient`/`IPmProcessClient`). `JinXinApi` (`extends CommonUtil`, no interface) — 金信 HTTP wrapper, P5-deferred into P6.

## Tasks Completed

| Task | Result |
|------|--------|
| Task 1 — IApplyService + ApplyServiceImpl | ✓ plain `implements IApplyService` (0 `extends BaseService`); `@Autowired IPmApproveClient` + `IPmProcessClient` Feign self-loop preserved (方案1). |
| Task 2 — JinXinApi | ✓ `extends CommonUtil`, no interface added (D-P6-03); references `IApiRequestHisClient` (main-domain, resolved). |

## Key Files

- created: `zgbas-system/.../wx/server/service/IApplyService.java`
- created: `zgbas-system/.../wx/server/service/impl/ApplyServiceImpl.java`
- created: `zgbas-system/.../wx/server/service/impl/JinXinApi.java`

## Decisions Honored

- 方案1: Feign self-loop not collapsed to direct injection.
- D-P6-03: JinXinApi naming/interface kept verbatim — no interface synthesized.
- D-16/17: 金信 external HTTP boundary maintained; CFCA runtime behavior → Phase 8 (R2).

## Deviations / Notes

- None. CFCA classes (`cfca.etl.*`) on classpath jar — compile resolves via IApiRequestHisClient; 06-06 gate authoritative.
- Cross-plan: IApplyService feeds Wave 2 ContractServiceImpl (06-03); JinXinApi feeds Wave 4 UserInfoService (06-04).

## Self-Check: PASSED
