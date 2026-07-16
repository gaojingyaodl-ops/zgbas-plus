---
title: Phase 4 — resolve entity/schema drift, re-enable ddl-auto=validate (D-P2-02)
status: open
priority: medium
created: 2026-07-16
resolves_phase: 04
phase_origin: 02
context: zgbas-plus Phase 2 ddl-auto deviation
---
## What
Phase 2 runs `spring.jpa.hibernate.ddl-auto=none`, deviating from locked D-P2-02 (`validate`). `validate` surfaced real pre-existing entity/schema drift across the 239 entities (e.g. `api_param`: entity `varchar(255)` vs DB `mediumtext`). `none` matches the source project's implicit behavior and unblocks the bootable-infra goal. Drift fix touches ~259 tables — explicitly out of Phase-2 scope.

## Action (Phase 4)
1. When business logic lands, enumerate the entity↔schema drift (run a one-off `ddl-auto=validate`, collect `SchemaValidationException` outputs).
2. Reconcile entity annotations / `@Column(length=...)` / column types with the live `sptbasdb_pd` schema (or accept deltas deliberately).
3. Re-enable `ddl-auto=validate` (D-P2-02 intent) so future drift surfaces at startup.

Accepted as deferred by user 2026-07-16.
