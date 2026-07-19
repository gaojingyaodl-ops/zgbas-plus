---
phase: 7
slug: alignment-verification
status: draft
nyquist_compliant: false
wave_0_complete: false
created: 2026-07-19
---

# Phase 7 — Validation Strategy

> Per-phase validation contract for feedback sampling during execution.

---

## Test Infrastructure

| Property | Value |
|----------|-------|
| **Framework** | JUnit 5 + AssertJ + Mockito (Spring Boot 2.5.9 test starter) |
| **Config file** | zgbas-admin/src/test/resources/ — inherits application-dev.yml |
| **Quick run command** | `JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk1.8.0_351.jdk/Contents/Home /Users/alan/App/apache-maven-3.8.6/bin/mvn -s /Users/alan/App/apache-maven-3.8.6/zg_settings.xml test -pl zgbas-admin -Dtest=ZgbasApplicationTest` |
| **Full suite command** | `JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk1.8.0_351.jdk/Contents/Home /Users/alan/App/apache-maven-3.8.6/bin/mvn -s /Users/alan/App/apache-maven-3.8.6/zg_settings.xml test` |
| **Estimated runtime** | ~60s (quick) / ~120s (full reactor) |

---

## Sampling Rate

- **After every task commit:** Run quick command (active @Test methods only)
- **After every plan wave:** Run full suite command
- **Before `/gsd:verify-work`:** Full suite must be green (30/0/0/3 skipped baseline)
- **Max feedback latency:** 120 seconds

---

## Per-Task Verification Map

| Task ID | Plan | Wave | Requirement | Threat Ref | Secure Behavior | Test Type | Automated Command | Status |
|---------|------|------|-------------|------------|-----------------|-----------|-------------------|--------|
| 07-01-01 | 01 | 1 | ALIGN-01 | — | Smoke chain endpoints reachable + non-empty response | proof (@Disabled) | quick + manual enable | ⬜ pending |
| 07-01-02 | 01 | 1 | ALIGN-01 | — | Full reactor compile green | automated | full suite | ⬜ pending |
| 07-02-01 | 02 | 2 | ALIGN-02 | — | Write-class real regression: sys_job_log status + DB row change | proof (@Disabled) | manual enable + checkpoint:human-blocked | ⬜ pending |
| 07-02-02 | 02 | 2 | ALIGN-02 | — | Export endpoints return xlsx Content-Type + non-zero body | proof (@Disabled) | manual enable | ⬜ pending |
| 07-02-03 | 02 | 2 | ALIGN-02 | — | findPage pagination: count + paged SQL correctness | proof (@Disabled) | manual enable | ⬜ pending |
| 07-03-01 | 03 | 2 | ALIGN-02 | — | 15 REVIEW sys_job operator review completed | manual | human verify | ⬜ pending |
| 07-03-02 | 03 | 2 | ALIGN-02 | — | Menu INSERT applied to spt-auth DB + /monitor/job renders | manual | browser UAT | ⬜ pending |
| 07-04-01 | 04 | 3 | ALIGN-01/02 | — | 07-HUMAN-UAT.md sign-off complete | manual | human sign-off | ⬜ pending |

*Status: ⬜ pending · ✅ green · ❌ red · ⚠ flaky*

---

## Wave 0 Requirements

- [ ] ZgbasApplicationTest extends with P7 @Disabled proof stubs
- [ ] 07-HUMAN-UAT.md created with frontmatter + P6 carry-over + P7 items

*Existing infrastructure covers all phase requirements — no new test framework install needed.*

---

## Manual-Only Verifications

| Behavior | Requirement | Why Manual | Test Instructions |
|----------|-------------|------------|-------------------|
| Write-class real execution (autoPay, refreshContractStatusTask) | ALIGN-02 | Irreversible DB mutation — must be human-gated | Enable @Disabled proof → run → check DB → restore @Disabled |
| 15 REVIEW sys_job operator review | ALIGN-02 | Requires business judgment per row | Read each REVIEW row → decide keep/modify/PAUSE → update SQL |
| /monitor/job UI browser rendering | ALIGN-01 | Browser-based visual verification | Login → "系统监控" → "定时任务" → 53 rows visible |
| Excel export file integrity | ALIGN-02 | Binary file format, visual content check | Download .xlsx → open in Excel → verify columns/rows |
| Full smoke chain in browser | ALIGN-01 | End-to-end human walkthrough | Login → navigate menus → run report → trigger job |

---

## Negative Test Spot-Checks

To verify proof tests actually catch regressions:

1. **schedulerLoadAllJobs_proof**: Temporarily comment out one sys_job INSERT → expect test failure
2. **writeClassRealRun_proof**: Change expected sys_job_log status from '0' to '9' → expect assertion failure
3. **reportExportSample_proof**: Change expected Content-Type → expect assertion failure

These spot-checks are one-time validation of the proof quality, not part of the regular test suite.
