---
phase: 04-core-business
plan: 02
subsystem: basClient-contracts
tags: [bulk-copy, basClient, remote-contracts, feign-contract, wave-1]
requires:
  - "Phase 2 D-P2-07 verbatim-package copy convention + inlined entity(239)/dao(229)/vo(293)/constant(20)/cache(3)"
  - "Phase 2 inlined 4 bas remote contracts (IBsDictClient / IBsCompanyOurClient) + spt-tools BaseClient/FeignConfig"
  - "Phase 3 D-P3-10 stub-port (IApproveWaitDealClient / IPmProcessClient stubs in zgbas-system for IndexController compile)"
  - "04-01 BasClientConfig basServerConfig bean + widened @EnableFeignClients + BasFeignPathConfig path stripper (SpEL + proxy prerequisites)"
provides:
  - "238 @FeignClient extends BaseClient<Entity> contracts in zgbas-system/com/spt/bas/client/remote/ (234 net-new + 4 Phase 2/3 already inlined)"
  - "2 stub upgrades: IApproveWaitDealClient + IPmProcessClient now source-real @FeignClient contracts (Phase 3 D-P3-10 stubs replaced)"
  - "14 basClient data carriers: dto(2) + util(6) + common(3) + riskScore(3) — compile prerequisite for remote contracts"
  - "basTrade subpackage (IBasTradeClient) + package-info.java"
  - "BIZ-01 compile prerequisite satisfied: Wave 2 service/api can resolve all I*Client types"
  - "BIZ-03 runtime prerequisite satisfied: 238 Feign proxies generatable (widened @EnableFeignClients from 04-01 + SpEL anchor)"
affects:
  - "Wave 2a/2b service+impl+infra (04-03/04-04) — service layer @Autowired I*Client now type-resolvable"
  - "Wave 3 api @RestController (04-05) — extends BaseApi endpoints become Feign self-loopback targets for these contracts"
  - "Wave 4 BFF controller (04-06) — 252 BFF @Autowired I*Client fields resolve via Feign proxy (D-P4-02 stub-degradation at field layer)"
tech-stack:
  added: []
  patterns:
    - "Verbatim bulk-copy with package preservation (D-P2-07): cp -r source/client/<subpkg>/. target/client/<subpkg>/ — zero rename, zero import edit"
    - "Compile-coupled task reorder (Rule 3): Task 2 data carriers committed before Task 1 remote contracts to resolve bidirectional dto/common dependency"
    - "Stub upgrade preserving caller signatures: Phase 3 stubs matched source real contract method names (getUserWaitDealNum, findAccess) so IndexController needs no fix"
key-files:
  created:
    - path: zgbas-system/src/main/java/com/spt/bas/client/remote/**
      purpose: 238 @FeignClient contracts (234 net-new + basTrade/IBasTradeClient + package-info.java)
    - path: zgbas-system/src/main/java/com/spt/bas/client/dto/**
      purpose: 2 DTOs (CompanyStatusDto, CtrContractDto) — referenced by ICtrContractClient
    - path: zgbas-system/src/main/java/com/spt/bas/client/util/**
      purpose: 6 utilities (ContractCfsUtil, GZipUtil, Md5EncryptUtils, RmbUtil, TreeNode, package-info)
    - path: zgbas-system/src/main/java/com/spt/bas/client/common/**
      purpose: 3 common types (ApiResult, BaseException, Status) — referenced by IRiskApplyClient
    - path: zgbas-system/src/main/java/com/spt/bas/client/riskScore/**
      purpose: 3 risk-score types (RiskModelResult, RiskModelScoreType, RiskQueryVo)
  modified:
    - path: zgbas-system/src/main/java/com/spt/bas/client/remote/IApproveWaitDealClient.java
      change: "Phase 3 stub replaced with source real @FeignClient extends BaseClient<ApproveWaitDeal>; preserves getUserWaitDealNum(ApproveWaitSearchVo) signature used by IndexController"
    - path: zgbas-system/src/main/java/com/spt/bas/client/remote/IPmProcessClient.java
      change: "Phase 3 stub replaced with source real @FeignClient extends BaseClient<PmProcess>; preserves findAccess(PmProcessSearchVo) signature used by IndexController"
decisions:
  - "Commit order reversed to Task 2 → Task 1 (plan permitted: '先跑 Task 2 再回 Task 1 闭环') because Task 1 remote contracts compile-depend on Task 2 dto/common packages (ICtrContractClient→CtrContractDto, IRiskApplyClient→common.*)"
  - "IBsDictClient / IBsCompanyOurClient verified byte-identical to source via diff before copy — plain cp -r safe (no-clobber not needed); both files confirmed untouched by Task 1 commit (last touched Phase 2 commit 4d6bcd7)"
  - "No IndexController fix required: source real contracts preserve the exact method signatures (getUserWaitDealNum, findAccess) the Phase 3 stubs exposed — zgbas-admin sanity compile green"
  - "PM-domain 13 contracts (IPmApplySet/IPmApprove×5/IPmProcess×7) ported verbatim as source-real @FeignClient interfaces; D-P4-02 stub-degradation deferred to BFF field layer in Plan 04-06 (not at contract interface layer)"
metrics:
  duration: ~5 min
  completed: 2026-07-17
  tasks_completed: 2
  files_created: 251
  files_modified: 2
---

# Phase 4 Plan 02: Wave 1 basClient Contracts + Data Carriers Summary

Wave 1 落地 238 个 `@FeignClient extends BaseClient<Entity>` 业务契约（234 净新增 + Phase 2/3 已就位 4 个中的 2 个 stub 升级）与 14 个数据载体（dto/util/common/riskScore），全部保包名 `com.spt.bas.client.*` verbatim 照搬自源 `basCore/basClient`。这构成 Wave 2/3/4 的编译与运行契约前提——后续 service/api 通过 `@Autowired I*Client` 引用这些接口作业务依赖，Feign 自回环 proxy（04-01 已放宽 `@EnableFeignClients` + `basServerConfig` SpEL 锚点）据此生成。

## What Was Built

### Task 2 (committed first — compile prerequisite): 14 data carriers

`zgbas-system/src/main/java/com/spt/bas/client/{dto,util,common,riskScore}/` — verbatim copy of source `basCore/basClient/.../{dto,util,common,riskScore}/`. Breakdown: dto=2 (`CompanyStatusDto`, `CtrContractDto`), util=6 (`ContractCfsUtil`, `GZipUtil`, `Md5EncryptUtils`, `RmbUtil`, `TreeNode`, `package-info`), common=3 (`ApiResult`, `BaseException`, `Status`), riskScore=3 (`RiskModelResult`, `RiskModelScoreType`, `RiskQueryVo`).

Self-contained prerequisite: imports only `com.spt.bas.client.entity.BsCompany` + `com.spt.bas.client.vo.ContractCfs` (Phase 2 inlined) + `com.spt.tools.*` (Phase 2 spt-tools). Zero imports of `remote/` or `server/` — compiles standalone.

### Task 1: 238 remote @FeignClient contracts + 2 stub upgrades

`zgbas-system/src/main/java/com/spt/bas/client/remote/**` — verbatim recursive copy of source `basCore/basClient/.../remote/.` (239 java files: 238 top-level incl `package-info.java` + 1 `basTrade/IBasTradeClient` subdir). 238 `@FeignClient` annotations, 230 `extends BaseClient<Entity>`, 0 `implements I*Client` (contracts are pure interfaces).

**Stub upgrades (Phase 3 D-P3-10 stubs replaced):**
- `IApproveWaitDealClient.java`: Phase 3 had a 1-method stub (`Long getUserWaitDealNum(ApproveWaitSearchVo)`). Source real contract is `@FeignClient extends BaseClient<ApproveWaitDeal>` with 10 methods — critically including `getUserWaitDealNum` (line 49), the exact method IndexController calls.
- `IPmProcessClient.java`: Phase 3 had a 1-method stub (`List<PmProcess> findAccess(PmProcessSearchVo)`). Source real contract is `@FeignClient extends BaseClient<PmProcess>` with 7 methods — including `findAccess` (line 20), the exact method IndexController calls.

Both upgrades preserve caller-compatible signatures, so IndexController's `@Autowired(required=false)` fields + null-guard calls compile unchanged. Verified by zgbas-admin sanity compile (ERROR_COUNT=0).

## Deviations from Plan

### Auto-fixed Issues

**1. [Rule 3 - Blocking] Commit order reversed: Task 2 before Task 1**
- **Found during:** Task 1 initial compile gate
- **Issue:** Task 1's remote contracts compile-depend on Task 2's data carriers. Initial Task 1 compile produced 28 errors: `程序包com.spt.bas.client.dto不存在` (ICtrContractClient→CtrContractDto), `程序包com.spt.bas.client.common不存在` (IRiskApplyClient→common.*).
- **Fix:** Plan 04-02 Task 1 action explicitly anticipated this ("若 Task 1 编译时报 dto/util 缺失，先跑 Task 2 再回 Task 1 闭环"). Executed Task 2 copy first, then re-ran combined compile gate → green. Committed Task 2 (`66b1814`) before Task 1 (`dce83f9`) so each commit is compile-correct in dependency order.
- **Files modified:** none (commit-order change only)
- **Impact on plan:** none — plan permitted this reorder explicitly. Both tasks' `done` criteria met.

### Plan Adherence

Otherwise the plan executed exactly as written — no Rule 1/2/4 deviations. No source logic was edited (pure mechanical copy). No IndexController fix was needed (signatures compatible). No 238-contract `path=` edits (D-P4-01a path-strip handled at Feign RequestInterceptor layer by 04-01's `BasFeignPathConfig`).

## Threat Model Adherence

The plan's `<threat_model>` assigned `accept` dispositions to both threats — no mitigations required of this plan:

| Threat | Disposition | How landed in this plan |
|--------|-------------|-------------------------|
| T-04-02-E (EoP, path SpEL injection) | accept | Contracts use SpEL `#{basServerConfig.url}` reading fixed yml key `spt.bas.server.url` (04-01); no user-controlled input. Verbatim copy introduces no new SpEL surface. |
| T-04-02-I (Info Disclosure, Feign self-loopback url) | accept | Self-loopback target is localhost:8080 (04-01); runtime requests still gated by Phase 3 Shiro chain. This plan only ports interface definitions — no runtime endpoints added. |

No new threat surface introduced. Pure interface port — no new network endpoints, auth paths, file access, or trust-boundary schema changes.

## Verification Evidence

### Combined compile gate (Task 1 + Task 2)

Command:
```
JAVA_HOME=/Users/alan/Library/Java/JavaVirtualMachines/corretto-1.8.0_482/Contents/Home \
/Users/alan/App/apache-maven-3.8.6/bin/mvn -s /Users/alan/App/apache-maven-3.8.6/zg_settings.xml \
-pl zgbas-system -am compile
```

Result: `BUILD SUCCESS`, `ERROR_COUNT=0`, `CANNOT_FIND=0` (locale-independent grep `^\[ERROR]` / `cannot find symbol|找不到符号`). Log: `/tmp/p4-02-t2.log`.

### Downstream sanity (zgbas-admin — IndexController stub-upgrade compatibility)

Command:
```
JAVA_HOME=<Corretto-1.8> mvn -pl zgbas-admin -am compile
```
Result: `BUILD SUCCESS`, `ADMIN_ERROR_COUNT=0`, `ADMIN_CANNOT_FIND=0`. Confirms source real contracts preserve method signatures IndexController uses — no downstream breakage. Log: `/tmp/p4-02-admin.log`.

### Done-criteria metrics

| Criterion | Target | Actual |
|-----------|--------|--------|
| REMOTE_COUNT | ≥ 238 | **239** (238 top-level + 1 basTrade) |
| FEIGNCLIENT_COUNT | ≥ 235 | **238** |
| EXTENDS_BASECLIENT | ≥ 230 | **230** |
| IMPLEMENTS_ICLIENT (in remote/) | — | **0** (contracts are pure interfaces, no premature implements) |
| DTO / UTIL / COMMON / RISKSCORE | 2 / 6 / 3 / 3 | **2 / 6 / 3 / 3** |
| zgbas-system ERROR_COUNT | 0 | **0** |
| zgbas-system CANNOT_FIND | 0 | **0** |
| IBsDictClient / IBsCompanyOurClient protected | unchanged | last touched Phase 2 commit `4d6bcd7` (NOT this plan) |
| Stub upgrades landed | @FeignClient present | both `IApproveWaitDealClient` + `IPmProcessClient` match `@FeignClient` |
| Package names verbatim | com.spt.bas.client.* | confirmed (4 distinct package decls, no rename) |

## Commits

| Hash | Message | Files |
|------|---------|-------|
| `66b1814` | `feat(04-02): port basClient data carriers (dto/util/common/riskScore, 14 files)` | 14 created |
| `dce83f9` | `feat(04-02): port 238 basClient remote @FeignClient contracts to zgbas-system` | 235 created + 2 modified |

Commit order is Task 2 → Task 1 (dependency order: data carriers are the compile prerequisite for remote contracts). See Deviations §1.

## Known Stubs

None introduced by this plan. This plan **removes** two Phase 3 stubs (IApproveWaitDealClient, IPmProcessClient) by replacing them with source-real `@FeignClient` contracts — a stub-elimination, not a stub-addition.

The D-P4-02 stub-degradation for ~5-15 no-impl contracts (ApplyCompanyOnline / ApplyDeposit / ApplyPromoteVip / BsInvestigateInfo / CtrOutInLedger / RiskApply / SignFileApi + PM-domain 13 cluster) is deferred to Plan 04-06 Wave 4 BFF field layer (`@Autowired(required=false)` + null guards), per the plan's action note: "stub 降级在 Plan 04-06 Wave 4 BFF 字段层处理...不在契约接口层 stub——契约接口保留源签名作 Feign 自回环目标".

## Threat Flags

None. No new network endpoints, auth paths, file access patterns, or trust-boundary schema changes introduced. The 238 ported interfaces are type definitions only; runtime endpoint exposure is a Wave 3 (api `@RestController`) concern, and runtime access control remains covered by Phase 3 Shiro chain.

## Self-Check: PASSED

**Files created (sample):**
- `FOUND: zgbas-system/src/main/java/com/spt/bas/client/remote/IApplyBrandClient.java`
- `FOUND: zgbas-system/src/main/java/com/spt/bas/client/remote/package-info.java`
- `FOUND: zgbas-system/src/main/java/com/spt/bas/client/remote/basTrade/IBasTradeClient.java`
- `FOUND: zgbas-system/src/main/java/com/spt/bas/client/dto/CtrContractDto.java`
- `FOUND: zgbas-system/src/main/java/com/spt/bas/client/util/package-info.java`
- `FOUND: zgbas-system/src/main/java/com/spt/bas/client/common/ApiResult.java`
- `FOUND: zgbas-system/src/main/java/com/spt/bas/client/riskScore/RiskModelResult.java`

**Files modified:**
- `FOUND: IApproveWaitDealClient.java` — `@FeignClient` + `extends BaseClient<ApproveWaitDeal>` present (grep confirmed)
- `FOUND: IPmProcessClient.java` — `@FeignClient` + `extends BaseClient<PmProcess>` present (grep confirmed)

**Protected files (Phase 2 assets untouched):**
- `FOUND: IBsDictClient.java` — last commit `4d6bcd7` (Phase 2), NOT this plan
- `FOUND: IBsCompanyOurClient.java` — last commit `4d6bcd7` (Phase 2), NOT this plan

**Commits:**
- `FOUND: 66b1814` (git log)
- `FOUND: dce83f9` (git log)

**Plan done criteria (Task 1):** all 7 met (see Done-criteria metrics table)
**Plan done criteria (Task 2):** all 3 met (counts + verbatim packages + zero-error compile)
