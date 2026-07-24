---
plan_id: 06-05
phase: 6
wave: 4
title: Service 迁入 V — UserService + PurchaseCommand(@XxlJob scrub)
status: complete
commits: [06-05-task1, 06-05-task2]
requirements: [WX-SERVICE-01]
---

# 06-05 Summary

**What was built:** The final two classes. UserService (extends BaseService<CompanyUser> → IUserInfoService) closes WX user CRUD. PurchaseCommand (implements ICommand) migrated + @XxlJob scrubbed (D-P6-02 hard landmine).

## Tasks Completed

| Task | Result |
|------|--------|
| Task 1 — UserService | ✓ `extends BaseService<CompanyUser> implements IUserService`; IUserService `extends IBaseService<CompanyUser>`; @Autowired IUserInfoService (06-04); getBaseDao()=CompanyUserDao. Copied verbatim. |
| Task 2 — PurchaseCommand + scrub | ✓ zero `XxlJob`/`com.xxl` residue; `implements ICommand` + full `executeCommand(String)` body (6 branches + LocalCacheManager + ContractNoRequest) intact; StringUtils kept. |

## Key Files

- created: `service/IUserService.java`, `service/impl/UserService.java`
- created: `command/PurchaseCommand.java` (new subdir, scrubbed)

## Deviation — WX-package BsCompanyDao migrated (RESOLVED)

Research §3 wrongly claimed UserService had "no WX-specific Dao". In fact UserService calls `bsCompanyDao.findByContactPhoneAndEnableFlgTrue(phone)` — a WX-specific derived-query finder absent from the main-domain `BsCompanyDao` (which only has the 2-param `findByContactPhoneAndEnterpriseIdAndEnableFlgTrue`). Investigation showed **3** migrated services need the WX-package `BsCompanyDao` (4 finders): ApplyServiceImpl (06-02, explicit import), UserInfoService (06-04, wildcard — 3 finders + BaseDao.save/findOne), UserService (06-05, 1 finder). **Migrated the WX-package `BsCompanyDao` verbatim** (`fix(06-05)` commit) — fixes all 3 services with zero edits (wildcard/explicit imports resolve naturally). Per plan R1 guidance: adapt within WX package, do NOT expand shared/main-domain Dao. BsCompanyService (06-01) stays on main-domain Dao (CRUD-only). Two `BsCompanyDao` repos coexist (different interfaces, resolved by import, no Spring bean conflict).

## Decisions Honored

- D-P6-02: scrubbed @XxlJob (annotation + imports + jobParam block), kept ICommand queue path; scheduled trigger deferred to v1.3.
- D-11: PurchaseCommand behavior-equivalent (only the dead xxl-job wiring removed; all 6 command branches intact).

## Self-Check: PASSED
