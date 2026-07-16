package com.spt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Phase 1 skeleton boot class - empty Spring context (no DataSource / no business code).
 * Package com.spt is the ComponentScan base, covering future com.spt.bas.* / com.spt.tools.* business packages.
 * Bare annotation (no auto-config removal): classpath has no db starter, so DataSourceAutoConfiguration is not present to remove.
 */
@SpringBootApplication
public class ZgbasApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZgbasApplication.class, args);
    }
}
