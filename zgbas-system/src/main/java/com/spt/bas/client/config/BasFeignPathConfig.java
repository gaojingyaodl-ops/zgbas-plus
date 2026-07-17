package com.spt.bas.client.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * D-P4-01a path-prefix handling for the Feign self-loopback (方案 A).
 *
 * <p><b>Problem.</b> Source {@code basServer} ran with
 * {@code server.servlet.context-path=/spt-bas-server}, so the 224 {@code @RestController}s
 * annotated with e.g. {@code @RequestMapping("apply/brand")} were actually served at
 * {@code /spt-bas-server/apply/brand/*}. The 238 bas {@code @FeignClient} contracts in
 * {@code com.spt.bas.client.remote} hard-code this prefix via
 * {@code path = BasConstants.SERVER_NAME + "/..."} (= {@code "spt-bas-server/..."}).
 *
 * <p>The monolith (Phase 2 D-P2-16) intentionally runs at context-path {@code /} to preserve the
 * Phase 3 AUTH-03 Shiro {@code /login}/{@code /index} root filter chain. Ported controllers
 * therefore expose endpoints at the bare path (e.g. {@code /apply/brand/findAll}), and the Feign
 * self-loopback would 404 without intervention.
 *
 * <p><b>Solution (preferred over re-setting context-path).</b> Register a global
 * {@link RequestInterceptor} that strips the literal {@code "spt-bas-server/"} prefix from the
 * outgoing request URI before it is sent. This keeps:
 * <ul>
 *   <li>Phase 3 AUTH-03 Shiro root-path chain intact (no context-path change);</li>
 *   <li>238 bas contracts verbatim — no {@code path=} rewrite on the interfaces (D-P2-07);</li>
 *   <li>224 ported controllers verbatim — no {@code @RequestMapping} edits (D-P4-01 方案 A).</li>
 * </ul>
 *
 * <p><b>Scope.</b> Applied to all Feign clients (cfca sign clients in
 * {@code com.spt.sign.client.remote} have paths that do NOT contain the literal
 * {@code "spt-bas-server/"}, so they are unaffected). If the literal is absent, the interceptor
 * is a no-op.
 *
 * <p><b>Mechanism.</b> {@link feign.RequestTemplate#path()} returns {@code target + uriTemplate}.
 * The target (e.g. {@code http://localhost:8080}) never contains {@code "spt-bas-server/"}, so a
 * simple substring search is safe. On hit, {@link feign.RequestTemplate#uri(String)} replaces
 * the uriTemplate (preserving target) with the stripped, leading-slash-normalized path.
 *
 * <p>Source rationale: D-P4-01a user decision 2026-07-17; the fallback (servlet URL rewrite
 * filter or re-enabling context-path) is documented in 04-01-SUMMARY.md if Wave 4 acceptance
 * exposes a 404 — the contracts are NOT modified unilaterally.
 */
@Configuration
public class BasFeignPathConfig {

    /**
     * Bean name used by the {@code feignSelfLoopbackWiring_probe} test in {@code ZgbasApplicationTest}
     * to fail-fast verify interceptor registration in Wave 0.
     */
    public static final String PATH_STRIPPER_BEAN_NAME = "basServerPathStripper";

    private static final String PREFIX_LITERAL = "spt-bas-server/";

    @Bean(PATH_STRIPPER_BEAN_NAME)
    public RequestInterceptor basServerPathStripper() {
        return template -> {
            String path = template.path();
            int idx = path.indexOf(PREFIX_LITERAL);
            if (idx < 0) {
                return;
            }
            // Re-compose the relative URI: drop the literal prefix, keep the leading slash.
            String after = path.substring(idx + PREFIX_LITERAL.length());
            String newUri = after.startsWith("/") ? after : "/" + after;
            template.uri(newUri);
        };
    }
}
