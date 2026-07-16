---
title: Phase 4 — add MockMvc HTTP proof for interface-as-contract (WR-02)
status: open
priority: medium
created: 2026-07-16
resolves_phase: 04
phase_origin: 02
context: zgbas-plus Phase 2 code review WR-02
---
## What
`InProcessContractTest` (zgbas-admin) proves `@Autowired InProcessContract` resolves to the local `InProcessContractImpl`, but uses a plain Java call — it does NOT assert Spring MVC honors the interface-level `@GetMapping` over HTTP. Phase 4 reuses the D-P2-10 interface-as-contract pattern across ~295 clients.

## Action (Phase 4)
Add a MockMvc / `TestRestTemplate` assertion: `GET /proof/echo?... → 200 "echo:hi"`. More meaningful once real Phase-4 `@RestController implements I*Client` impls exist. Accepted as deferred by user 2026-07-16.
