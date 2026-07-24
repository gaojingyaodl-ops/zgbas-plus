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

## Phase 5 additions (2026-07-24, plan 05-04, decision D-P5-05)

Phase 5 carrier-layer migration committed three NEW sets of WX external-integration credentials to `zgbas-admin/src/main/resources/application-dev.yml` as plaintext, per the user-locked option3 plaintext policy (D-P4 / D-P5-05, which overrides the secret-externalization rule). Rotation is not carrier-layer scope, but real secrets entering git must be tracked:

| Integration | Keys committed (abbrev.) | Location | Rotate priority |
|---|---|---|---|
| **EweChat (企业微信)** | `corpid=ww67315c53a72c991f`, `corpsecret=gn_IIS0Sv-…`, `agentid=1000007` | `application-dev.yml` → `ewechat.config.*` (bound via `WxCarrierConfig.eweChatConfig`) | high — corpsecret is live |
| **Aliyun OCR (阿里云)** | `appcode=52e1a1531c5a4e30969d93c16eb1251f` | `application-dev.yml` → `aliyun.ocr.*` (bound via `OcrConfig`) | high — billable API key |
| **JinXin (金信 CA)** | `merchKey=LaYeIemv2WEY8Z7h`, `merchRsaPrivateKey=MIIEvQ…` (RSA private key), `merchNo=0000000000036500`, `keyStorePassword=cfca1234`, `trustStorePassword=cfca1234` | `application-dev.yml` → `jinxin.*` (bound via `JinXinConfig`; consumer JinXinApi in Phase 6) | **critical** — RSA private key + store passwords committed |

### Additional rotation actions
5. Rotate the EweChat `corpsecret` (re-issue the 企业微信 app if warranted).
6. Rotate/reissue the Aliyun OCR `appcode`.
7. Rotate the JinXin `merchKey`, regenerate the RSA keypair (`merchRsaPrivateKey`), and change the `cfca1234` keystore/truststore passwords — the committed RSA private key is the most sensitive item in the repo.

Source: Phase 5 plan 05-04 / D-P5-05. Landing: `application-dev.yml`. These are NOT externalized (D-P5-05 locked plaintext); only rotation retires them.
