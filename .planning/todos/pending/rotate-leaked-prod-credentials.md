---
title: Rotate leaked production credentials (CR-01 follow-up)
status: open
priority: high
created: 2026-07-16
phase_origin: 02
context: zgbas-plus Phase 2 code review CR-01
---
## What
The production DB password (`sptbasdb_pd` / user `sptbaspduser` @ 47.104.15.98) and the auth `spt.app.secretKey` were committed in plaintext during Phase-02 early waves (02-06 application-dev.yml), and the same values are in the source `zgbas` git history. Phase 2 externalized them to `${DB_PASSWORD}` / `${SPT_APP_SECRET}` (no secret in git as of HEAD), but the **values themselves are still live and must be rotated.**

## Action (outward-facing — user/deployment, not code)
1. Rotate the `sptbasdb_pd` MySQL user password.
2. Rotate the `spt.app.secretKey` (auth-sdk).
3. Update deployments/env to inject the new values via `DB_PASSWORD` / `SPT_APP_SECRET`.
4. Confirm the capstone test still passes: `DB_PASSWORD=… SPT_APP_SECRET=… mvn -pl zgbas-admin -am test -Dtest=ZgbasApplicationTest,InProcessContractTest -DfailIfNoTests=false`.

## Why not code
Credential rotation is a production/deployment action requiring operational access. Tracked here so it surfaces in `/gsd:progress`.
