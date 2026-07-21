# Phase 2: 基础设施 — Learnings & Deferred Debt

**Phase:** 02-infrastructure · **Completed:** 2026-07-16 · **Status:** delivered (14/14 reqs MET, capstone 6/6 GREEN)

> Non-obvious facts future phases MUST know. Read before planning Phase 3 (auth) / Phase 4 (business).

## Load-bearing gotchas resolved this phase

### 1. Dual `FeignConfig` bean-name collision (the capstone stall)
Two `@Configuration` classes share bean name `feignConfig`:
- `com.spt.tools.http.feign.FeignConfig` (inlined into zgbas-common — richer: has `errorDecoder` + `feignOptions`)
- `com.spt.sign.client.config.FeignConfig` (spt-sign-client jar)

Both are **per-client** Feign configs (instantiated in Feign's child context via `@FeignClient(configuration = FeignConfig.class)`), NOT global singletons. The monolith's broad `com.spt` component scan registered BOTH → `ConflictingBeanDefinitionException`. **Fix:** `ZgbasApplication` declares an explicit `@ComponentScan(basePackages="com.spt", excludeFilters=ASSIGNABLE_TYPE on both FeignConfig classes)`. The source `BasServer` avoided this via a narrow `@ComponentScan(basePackages={"com.spt.pm","com.spt.bas.server"})` — do NOT broaden the scan or remove the excludeFilter, or the collision returns.

### 2. `ToolsShiroConfig` excluded (Phase 3 owns Shiro)
`com.spt.tools.shiro.config.ToolsShiroConfig` is excluded from the component scan — its `EhCacheManager` collides with Hibernate's ehcache (same VM CacheManager name) and its security/filter beans need a Realm not present until Phase 3. **Phase 3 must re-enable it** (remove from excludeFilters) when wiring the Shiro Realm (AUTH-01..04).

### 3. `allow-bean-definition-overriding: true`
11 sign-client `@FeignClient` share `name="sign"` → 11 `FeignClientSpecification` beans with the same name. The override flag resolves it (Spring Cloud OpenFeign standard). Documented in `application.yml`.

### 4. `@EntityScan` MUST include `IdEntity`'s package
`basePackageClasses = IdEntity.class` is mandatory — `IdEntity` lives in `com.spt.tools.jpa.vo`, not `com.spt.bas.client.entity`. Without it: `MappingException: Unknown entity` at first query (Pitfall 2). Current `basePackages = {com.spt.bas.client.entity, com.spt.pm.entity}`.

### 5. jjwt 0.7.0 must be `provided` in zgbas-common
spt-tools-http `TokenUtil` uses the old monolithic jjwt 0.7.0 API; at runtime it shadowed jjwt-api 0.11.2 transitively. Declared `provided` in `zgbas-common/pom.xml` so it compiles but doesn't leak.

### 6. Data-layer support surface (Plan 02-05 scope reality)
The 239 entities + 240 Dao import types from 8 sibling packages. To compile, 02-05 copied an extra ~358-file support surface verbatim (pm.inter/pm.entity/pm.vo, client.constant/client.vo/client.cache, server.annotation) + exactly 2 feign contract interfaces. **Verified: zero business-bean annotations** (`@Service`/`@RestController`/`@Controller`/`@Component`) — pure data/contract layer. Phase 4 business logic is NOT present. Phase 4 adds the Service/Controller impls that satisfy these contracts.

## Deferred debt (accepted by user 2026-07-16)

| # | Item | Deferred to | Why |
|---|------|-------------|-----|
| D-02-1 | **ddl-auto=none** (deviates from D-P2-02 `validate`) | Phase 4 | validate surfaced real entity/schema drift across 239 entities (e.g. `api_param`: entity `varchar(255)` vs DB `mediumtext`). The schema fix touches ~259 tables — explicitly out of Phase-2 scope (PROJECT.md). `none` matches source behavior; re-enable `validate` in Phase 4 once drift is resolved. |
| D-02-2 | **InProcessContract proof not over HTTP** (WR-02) | Phase 4 | `InProcessContractTest` proves `@Autowired` resolves to the local impl but uses a plain Java call — no MockMvc, so the interface-`@GetMapping` HTTP mapping is untested. Add a MockMvc assertion in Phase 4 when real impls land (more meaningful against real endpoints). |
| D-02-3 | **Rotate leaked prod credentials** (CR-01 follow-up) | Now (outward-facing) | The prod DB password + auth secretKey were plaintext in early-wave commits (and the source zgbas git history). They are now externalized (`${DB_PASSWORD}`/`${SPT_APP_SECRET}`), but the **values themselves must be rotated** — a deployment/user action, not code. |
| D-02-4 | **TokenUtil deprecated jjwt 0.7.0 API** | Phase 4 | Functional but uses deprecated API. Non-blocking; address during Phase-4 auth/token work. |

## What works (the delivered infra)
- All 10 spt-tools modules source-inline (172 classes, zero spt-tools-* jar declared — only transitive `spt-tools-sdkutil` via the kept `spt-push-sdk` jar).
- Dual-ORM single DataSource: `ZgbasDataSourceConfig.@Primary` Druid (defeats the `ToolsJpaConfig`/`ToolsMybatisConfig` `@Bean("datasource")` race), shared by JPA + mybatis-plus.
- 239 entities + 240 Dao migrated; `IdEntity`→`EntityListener`→`BaseDao` audit chain byte-for-byte intact.
- 3 external SDK beans wired (`authOpenFacade`/`pushClientHttp`/`fileRemote`), secret-log line removed.
- nacos removed (0 in dep graph + yml); Feign narrowed to `com.spt.sign.client.remote`; interface-as-contract mechanism established.
- Config consolidated to 3 YAML profiles; prod = `${VAR}` fail-fast; secrets externalized.
- Capstone `@SpringBootTest` loads the full context: 6/6 tests GREEN (needs `DB_PASSWORD`/`SPT_APP_SECRET` in env).
