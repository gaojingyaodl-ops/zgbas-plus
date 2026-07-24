---
plan_id: 06-04
phase: 6
wave: 3
title: Service 迁入 IV — SuccessContract + UserInfo
status: complete
commits: [06-04-task1, 06-04-task2]
requirements: [WX-SERVICE-01]
---

# 06-04 Summary

**What was built:** The two deepest-dependency plain services. SuccessContractServiceImpl (→ IContractService + 电签 ISignContractClient/ISignInfoClient + main-domain Feign self-loop) and UserInfoService (widest dep — IApply/IContract/ISuccessContract/ITempSave + JinXinApi + ~25 main-domain clients + 电签 ICfcaSign/ISignInfo). Task order SuccessContract→UserInfo honored so ISuccessContractService lands first.

## Tasks Completed

| Task | Result |
|------|--------|
| Task 1 — SuccessContract | ✓ plain `implements ISuccessContractService`; sign clients (jar, main-domain-proven) + IContractService + remote.* wildcard. |
| Task 2 — UserInfo | ✓ plain `implements IUserInfoService`; references IContractService + ISuccessContractService + JinXinApi; remote.* wildcard (~25 main-domain clients) + ICfcaSignClient/ISignInfoClient. |

## Key Files

- created: `service/{ISuccessContractService,IUserInfoService}.java`
- created: `service/impl/{SuccessContractServiceImpl,UserInfoService}.java`

## Deviation — purchase-client payload inline (RESOLVED)

Phase 3 inlined `purchase-client` types (entity/config/constant/remote/vo) but NOT `payload/` — nothing compiled needed it then (pom.xml:132-135). Phase 6 JinXinApi (06-02) + UserInfoService (06-04) import `com.spt.bas.purchase.wx.client.payload.AuthFaceRecognition`. Source `purchase-client/payload/` has exactly 1 class (no inter-payload deps). **Inlined verbatim** as a standalone `fix(06-04)` commit — same pattern as Phase 3/5 client-type inlining (D-P3 / D-P5-02). This was a research gap (§2b verified only I*Client resolution, not wx.client.payload).

## Decisions Honored

- 方案1: all main-domain I*Client via Feign self-loop (not direct injection).
- 电签 clients via spt-sign-client jar (main-domain already uses them → classpath proven).

## Self-Check: PASSED
