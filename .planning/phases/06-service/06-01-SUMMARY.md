---
plan_id: 06-01
phase: 6
wave: 1
title: Service 迁入 I — 11 实体型 CRUD leaf(extends BaseService)+ 11 interface
status: complete
commits: [06-01-task1, 06-01-task2, 06-01-task3]
requirements: [WX-SERVICE-01]
---

# 06-01 Summary

**What was built:** 11 entity-type WX service interfaces + 11 impls verbatim-migrated into the `com.spt.bas.purchase.wx.server.service{,.impl}` enclave in `zgbas-system`. 7 leaf impls reuse P3-migrated WX-package Daos (no re-point); 4 impls (BsCompany/BsDict/CompanyIndustry/Feedback) reuse main-domain Daos and had their Dao imports re-pointed to `com.spt.bas.server.dao` (D-P6-01).

## Tasks Completed

| Task | Result |
|------|--------|
| Task 1 — 11 interfaces → `service/` | ✓ 11 I*Service copied; enclave now has 12 I*Service (+IWxAccessTokenService[P5]); IBsDictService was byte-identical to source — kept, not overwritten. 10 of 11 extend `IBaseService<T>`; IBuyMessageService plain. |
| Task 2 — 7 WX-Dao leaf impls → `service/impl/` | ✓ TempSaveInfoService/UserDetailService/BuyMessageServiceImpl/WxSessionService/WxSmsCheckCodeService/WxUserInfoService/WxUserTextReadServiceImpl. All `extends BaseService<T>` + `getBaseDao()`, import P3 WX-package Daos (verified present). |
| Task 3 — 4 main-domain Dao reuse impls + re-point | ✓ BsCompanyService→`server.dao.BsCompanyDao`; BsDictService→`server.dao.{BsDictTypeDao,BsDictDataDao}`; CompanyIndustryService→`server.dao.BsCompanyIndustryDao` (import + field type `CompanyIndustryDao`→`BsCompanyIndustryDao`); FeedbackService→`server.dao.FeedbackDao`. Zero residual `wx.server.dao` imports; zero standalone `CompanyIndustryDao` identifiers. |

## Key Files

- created: `zgbas-system/.../wx/server/service/{IBsCompanyService,ICompanyIndustryService,IFeedbackService,ITempSaveInfoService,IUserDetailService,IBuyMessageService,IWxSessionService,IWxSmsCheckCodeService,IWxUserInfoService,IWxUserTextReadService}.java`
- created: `zgbas-system/.../wx/server/service/impl/{TempSaveInfoService,UserDetailService,BuyMessageServiceImpl,WxSessionService,WxSmsCheckCodeService,WxUserInfoService,WxUserTextReadServiceImpl,BsCompanyService,BsDictService,CompanyIndustryService,FeedbackService}.java`
- re-pointed: BsCompanyService / BsDictService / CompanyIndustryService / FeedbackService Dao imports → main-domain `com.spt.bas.server.dao`

## Decisions Honored

- D-P6-01: changed WX service to adapt to current Dao location; main-domain Daos and shared `BaseService`/`IBaseService`/`IDataService` untouched.
- D-06 / D-10: verbatim, package names preserved.
- IBsDictService already migrated and identical — not overwritten (avoids needless churn).

## Deviations / Notes

- None. CompanyIndustry `BsCompanyIndustryDao` field type rename verified by word-boundary grep (0 standalone `CompanyIndustryDao`); earlier plain substring grep's 2 hits were `BsCompanyIndustryDao` substrings (import + field type), which is the intended final state.
- Cross-plan references (e.g. `IBuyMessageService` consumed by Wave 2 BuyEnquiry/BuyQuote/EweChatApi) will resolve once 06-03 lands; 06-06 compile gate is authoritative.

## Self-Check: PASSED

- `ls service/impl/*.java | wc -l` ≥ 11 ✓ (now 8 from this plan + WxAccessToken[P5] + 4 re-point = 13)
- 4 re-point impls import `com.spt.bas.server.dao.*`, zero `wx.server.dao` residual ✓
- CompanyIndustry zero standalone `CompanyIndustryDao` ✓
