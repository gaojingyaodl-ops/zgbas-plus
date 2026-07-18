---
quick_id: 260718-blh
slug: phase-5-housekeeping-roadmap-phase-5-com
date: 2026-07-18
mode: quick
status: planned
---

# Quick Task 260718-blh: Phase 5 Housekeeping — mark report migration complete & commit W4–W6

<objective>
Phase 5 (报表迁移) is functionally complete on disk (6/6 plans + summaries, W5/W6 gates green, closeout applied) but the working tree is dirty and ROADMAP.md is stale. This task: (1) marks Phase 5 complete in ROADMAP.md, (2) commits the uncommitted Phase 5 W4+W5+closeout deliverables + 3 outstanding SUMMARYs in two atomic commits (feat + docs). Excludes `.claude/` and `.planning/debug/`.
</objective>

<context>
- STATE.md already reflects Phase 05 COMPLETE (status: ready, 23/23 plans). Not modified by this task beyond the orchestrator's quick-task row in Step 7.
- Repo commit style (from `git log`): `feat(05-04): migrate ...` for code, `docs(05-04): complete wave N ... summary` for docs.
- Phase 4 decision (D-P2-13 reverted): application-dev.yml keeps plaintext secrets; yml diff here only ADDS `weChatWork.webhook` (dev=safe localhost default, prod=env placeholder) — no new secret.
</context>

<tasks>

## Task 1: Mark Phase 5 complete in ROADMAP.md

**files:**
- `.planning/ROADMAP.md`

**action:** Three edits in `.planning/ROADMAP.md`:
1. Phases list (near line 20): `- [ ] **Phase 5: 报表迁移** - 53 套 mybatis 报表迁入，查询行为等价` → `- [x] **Phase 5: 报表迁移** - 53 套 mybatis 报表迁入，查询行为等价 (completed 2026-07-18)`
2. Phase 5 detail section — the 6 wave plan checkboxes (near lines 155, 159, 163, 167, 171, 175): change each `- [ ] 05-0X-PLAN.md` → `- [x] 05-0X-PLAN.md`
3. Bottom Progress table (near line 214): `| 5. 报表迁移 | 0/6 | Not started | - |` → `| 5. 报表迁移 | 6/6 | Complete | 2026-07-18 |`

**verify:** `grep -c '- \[ \] 05-0' ROADMAP.md` returns 0; `grep '5\. 报表迁移' ROADMAP.md` shows `6/6 | Complete`.

**done:** All three locations updated; no other Phase rows touched.

## Task 2: Commit code (feat) — W4 api + W5/W6 probes + closeout hardening

**files (stage exactly these):**
- `zgbas-admin/src/main/java/com/spt/bas/report/` (entire dir, 54 controllers — 05-05 W4)
- `zgbas-admin/src/main/resources/application-dev.yml`
- `zgbas-admin/src/main/resources/application-prod.yml`
- `zgbas-admin/src/test/java/com/spt/ZgbasApplicationTest.java`
- `zgbas-system/src/main/java/com/spt/bas/report/server/dao/RptBaseCostMapper.java`
- `zgbas-system/src/main/java/com/spt/bas/report/server/service/impl/RptBaseCostServiceImpl.java`
- `zgbas-system/src/main/resources/mybatis/mappers/RptBsCompanyMapper.xml`
- `zgbas-system/src/main/resources/mybatis/mappers/RptBusinessAccountMapper.xml`
- `zgbas-system/src/main/resources/mybatis/mappers/RptWxBrandFollowMapper.xml`

**action:** Stage the above explicitly (no `git add -A`/`.`). Commit:
```
feat(05): complete report migration waves 4-6 + closeout hardening

- W4: 54 report @RestController ported to zgbas-admin (ReportFeignPathConfig /spt-bas-report self-loop)
- W5/W6: ZgbasApplicationTest probes (allReportMappersResolve, reportApiPathPrefixWiring, reportHttpReachability_proof, sampleReportQuery_proof @Disabled)
- closeout: drop string @Qualifier coupling in RptBaseCost/RptUserRoi/RptSummaryRoi service impls; weChatWork.webhook config (dev localhost default, prod env placeholder)
- gates: 25/0/0/1 skipped, full reactor compile green, EXACT_REPORT_STUB_COUNT=0
```

**verify:** `git show --stat HEAD` lists exactly the 9 paths above (report/ dir collapsed); `git status --porcelain` no longer shows those paths.

**done:** Single feat commit landed; working tree now clean of code changes.

## Task 3: Commit docs (docs) — ROADMAP + 3 Phase 5 SUMMARYs

**files (stage exactly these):**
- `.planning/ROADMAP.md` (edited in Task 1)
- `.planning/phases/05-report-migration/05-01-SUMMARY.md`
- `.planning/phases/05-report-migration/05-05-SUMMARY.md`
- `.planning/phases/05-report-migration/05-06-SUMMARY.md`

**action:** Stage the above explicitly. Commit:
```
docs(05): mark report migration complete (6/6 plans)

- ROADMAP: Phase 5 → [x] (completed 2026-07-18), 6 wave plans checked, progress table 6/6 Complete
- outstanding summaries: 05-01 (W0 reportClient inline), 05-05 (W4 54 api + probes), 05-06 (W5 BFF stub-port no-op + W6 proof)
```

**verify:** `git show --stat HEAD` lists exactly the 4 paths above.

**done:** Single docs commit landed.

</tasks>

<exclusions>
Do NOT stage or commit (orchestrator handles or out of scope):
- `.claude/` — session config, not a repo artifact
- `.planning/debug/login-feign-selfloop-shiro.md` — active investigation, unrelated to Phase 5
- `.planning/STATE.md` — orchestrator commits in Step 8 (with quick-task row appended in Step 7)
- This task's own `260718-blh-PLAN.md` / `260718-blh-SUMMARY.md` — orchestrator commits in Step 8
</exclusions>

<must_haves>
truths:
  - "ROADMAP.md marks Phase 5 complete (list [x], 6 plan checkboxes, progress table 6/6)"
  - "W4+W5+closeout code committed in one feat commit"
  - "ROADMAP + 3 Phase 5 SUMMARYs committed in one docs commit"
  - ".claude/ and .planning/debug/ are NOT committed"
artifacts:
  - 260718-blh-SUMMARY.md
key_links:
  - .planning/ROADMAP.md
  - zgbas-admin/src/main/java/com/spt/bas/report/
</must_haves>
</content>
</invoke>