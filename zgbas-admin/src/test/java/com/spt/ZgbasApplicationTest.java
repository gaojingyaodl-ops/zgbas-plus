package com.spt;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Context-load smoke test for BUILD-04.
 * Asserts the empty Spring context starts without exceptions.
 */
@SpringBootTest
class ZgbasApplicationTest {

    @Test
    void contextLoads() {
        // Spring Test loads the application context; if it starts without exceptions, the test passes.
    }
}
