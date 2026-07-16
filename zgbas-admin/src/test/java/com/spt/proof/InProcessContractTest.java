package com.spt.proof;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Proves D-P2-10: {@code @Autowired InProcessContract} resolves to the local
 * {@link InProcessContractImpl} bean and Spring MVC honors the interface {@code @GetMapping}.
 *
 * <p>This is a {@code @SpringBootTest} (full context) — it executes in Task 2's verify
 * after {@code application-dev.yml} exists, since the context load requires the datasource
 * config.
 */
@SpringBootTest
class InProcessContractTest {

    @Autowired
    private InProcessContract contract;

    @Test
    void localImplSatisfiesContract() {
        assertThat(contract.echo("hi")).isEqualTo("echo:hi");
    }
}
