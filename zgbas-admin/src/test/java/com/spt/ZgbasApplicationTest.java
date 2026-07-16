package com.spt;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Capstone startup-verification test for Phase 2 (D-P2-03).
 *
 * <p>The {@link SpringBootTest} context load brings up the full monolith context: Druid
 * {@link DataSource} (@Primary, Plan 04), JPA (239 entities validated, Plan 05),
 * mybatis-plus (sample Mapper, Plan 05), 3 external SDK beans (Plan 04), and the cfca sign
 * Feign client. If this context starts without exceptions, all 14 Phase-2 requirements are
 * satisfied at the infrastructure level.
 */
@SpringBootTest
@ActiveProfiles("dev")
class ZgbasApplicationTest {

    @Autowired
    private ApplicationContext context;

    @Test
    void contextLoads() {
        // Primary gate: the full context starts without exceptions.
        // Covers PERSIST-03/04, EXT-01..04, INFRA-04.
    }

    @Test
    void primaryDataSourceIsPresent() {                // PERSIST-03
        assertThat(context.getBean(DataSource.class)).isNotNull();
    }

    @Test
    void jpaTransactionManagerIsPrimary() {            // PERSIST-03 D-P2-03
        // Spring Boot registers the JPA TM as "transactionManager"; @Primary by uniqueness
        // (mybatis-plus starter does not declare its own PlatformTransactionManager).
        assertThat(context.containsBean("transactionManager")).isTrue();
        assertThat(context.getBean(PlatformTransactionManager.class))
            .isInstanceOf(JpaTransactionManager.class);
    }

    @Test
    void sampleMapperBeanRegistered() {                // PERSIST-03 sample Mapper
        assertThat(context.containsBean("sampleMapper")).isTrue();
    }

    @Test
    void externalSdkBeansRegistered() {                // EXT-01..03
        assertThat(context.containsBean("authOpenFacade")).isTrue();
        assertThat(context.containsBean("pushClientHttp")).isTrue();
        assertThat(context.containsBean("fileRemote")).isTrue();
    }
}
