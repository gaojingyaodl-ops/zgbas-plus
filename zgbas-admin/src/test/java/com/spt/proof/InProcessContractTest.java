package com.spt.proof;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Proves D-P2-10: {@code @Autowired InProcessContract} resolves to the local
 * {@link InProcessContractImpl} bean and Spring MVC honors the interface {@code @GetMapping}.
 *
 * <p>This is a {@code @SpringBootTest} (full context) — it executes in Task 2's verify
 * after {@code application-dev.yml} exists, since the context load requires the datasource
 * config.
 *
 * <p><b>Phase 6 06-06 (Rule 3 fix — pre-existing failure blocking full-reactor verify):</b>
 * added {@code webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT}. The original
 * plain {@code @SpringBootTest} uses a mock servlet context with no embedded Tomcat, so
 * {@code com.spt.bas.web.ws.WebSocketConfig}'s {@link org.springframework.web.socket.server.standard.ServerEndpointExporter}
 * bean fails its {@code afterPropertiesSet} assertion ({@code javax.websocket.server.ServerContainer
 * not available}) during context startup. This was a latent Phase 2 defect — Phase 3-5
 * verifications used {@code -Dtest=ZgbasApplicationTest} filtering, masking the failure.
 * Phase 6 06-06 Task 2 requires full-reactor {@code mvn test} BUILD SUCCESS, so this
 * 1-line annotation change (mirroring {@code ZgbasApplicationTest}'s working pattern)
 * unblocks the gate. The actual embedded Tomcat starts in ~1s extra; no behavior change
 * to the test assertion.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dev")
class InProcessContractTest {

    @Autowired
    private InProcessContract contract;

    @Test
    void localImplSatisfiesContract() {
        assertThat(contract.echo("hi")).isEqualTo("echo:hi");
    }
}
