package com.spt;

import org.apache.shiro.mgt.SecurityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Capstone startup-verification test for Phase 2 (D-P2-03) + Phase 3 (D-P3-13).
 *
 * <p>Uses {@code WebEnvironment.RANDOM_PORT} so the embedded Tomcat starts with a real
 * servlet container — this is required for {@code ServerEndpointExporter} (WebSocket) and
 * lets us verify endpoint reachability via {@link TestRestTemplate}.
 *
 * <p>The {@link SpringBootTest} context load brings up the full monolith context: Druid
 * {@link DataSource}, JPA (239 entities, ddl-auto=none), mybatis-plus (sample Mapper),
 * 3 external SDK beans, cfca sign Feign client, Shiro securityManager/shiroFilter/shiroDbRealm
 * (Phase 3 un-excluded ToolsShiroConfig), ported Login/Index/SSO controllers, and WebSocket
 * endpoints. If this context starts and all assertions pass, D-P3-13 is satisfied.
 *
 * <p>Phase 3 endpoint assertions verify /login, /index, /open/user/ssoLogin return 2xx or 3xx
 * — matching D-P3-13 acceptance ("200 或 302"). Not-logged-in /index is expected to redirect
 * (302) to /login via Shiro {@code user} filter — correct behavior.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dev")
class ZgbasApplicationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private ApplicationContext context;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void contextLoads() {
        // Primary gate: the full context starts without exceptions.
        // Covers PERSIST-03/04, EXT-01..04, INFRA-04, AUTH-01..04.
    }

    @Test
    void primaryDataSourceIsPresent() {                // PERSIST-03
        assertThat(context.getBean(DataSource.class)).isNotNull();
    }

    @Test
    void jpaTransactionManagerIsPrimary() {            // PERSIST-03 D-P2-03
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

    // ---- Phase 3 (D-P3-13): Shiro auth chain bean wiring ----

    @Test
    void shiroSecurityManagerBeanPresent() {           // AUTH-03 D-P3-01
        assertThat(context.containsBean("securityManager")).isTrue();
        assertThat(context.getBean("securityManager"))
            .isInstanceOf(SecurityManager.class);
    }

    @Test
    void shiroFilterBeanPresent() {                    // AUTH-03 D-P3-01
        assertThat(context.containsBean("shiroFilter")).isTrue();
    }

    @Test
    void shiroDbRealmBeanPresent() {                   // AUTH-01/04 D-P3-02
        assertThat(context.containsBean("shiroDbRealm")).isTrue();
    }

    // ---- Phase 3 (D-P3-13): Ported controller endpoints registered ----

    @Test
    void loginControllerRegistered() {                 // AUTH-01 D-P3-03
        assertThat(context.containsBean("loginController")).isTrue();
    }

    @Test
    void indexControllerRegistered() {                 // AUTH-02 D-P3-10
        assertThat(context.containsBean("indexController")).isTrue();
    }

    @Test
    void userOpenControllerRegistered() {              // AUTH-02 D-P3-12 SSO
        assertThat(context.containsBean("userOpenController")).isTrue();
    }

    // ---- Phase 3 (D-P3-13): Endpoint reachability ----

    @Test
    void loginEndpointReachable() {                    // AUTH-01 D-P3-13
        ResponseEntity<String> response = restTemplate.getForEntity("/login", String.class);
        assertThat(response.getStatusCode().is2xxSuccessful()
            || response.getStatusCode().is3xxRedirection()).isTrue();
    }

    @Test
    void indexEndpointReachable() {                    // AUTH-02 D-P3-13
        // Not logged in → Shiro user filter redirects to /login (302) — correct behavior.
        ResponseEntity<String> response = restTemplate.getForEntity("/index", String.class);
        assertThat(response.getStatusCode().is2xxSuccessful()
            || response.getStatusCode().is3xxRedirection()).isTrue();
    }

    @Test
    void ssoLoginEndpointReachable() {                 // AUTH-02 D-P3-12/13 SSO
        // Not logged in → Shiro user filter redirects to /login (302) — correct behavior.
        ResponseEntity<String> response =
            restTemplate.getForEntity("/open/user/ssoLogin", String.class);
        assertThat(response.getStatusCode().is2xxSuccessful()
            || response.getStatusCode().is3xxRedirection()).isTrue();
    }
}
