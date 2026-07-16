package com.spt;

import com.spt.tools.jpa.vo.IdEntity;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * zgbas-plus monolith boot class.
 *
 * <p>Keeps {@link SpringBootApplication} with NO auto-config exclusions: we WANT
 * DataSource + JPA + mybatis-plus auto-configuration to engage. Three infra annotations
 * scope the data and feign surface:
 *
 * <ul>
 *   <li>{@link EnableFeignClients} narrowed to {@code com.spt.sign.client.remote} ONLY
 *       (D-P2-12). The 295 internal {@code @FeignClient} interfaces become pure contracts
 *       satisfied by local {@code @RestController} impls in Phase 4; narrowing prevents the
 *       double-bean conflict (Pitfall 5).</li>
 *   <li>{@link EntityScan} with {@code basePackageClasses = IdEntity.class} so
 *       {@code com.spt.tools.jpa.vo} (where {@link IdEntity} lives) is entity-scanned
 *       alongside {@code com.spt.bas.client.entity} (239 entities from Plan 05). Missing
 *       the IdEntity package causes {@code MappingException: Unknown entity} (Pitfall 2).</li>
 *   <li>{@link EnableJpaRepositories} targeting {@code com.spt.bas.server.dao} (240 Dao
 *       from Plan 05, each {@code extends BaseDao}).</li>
 * </ul>
 *
 * <p>The mybatis {@code @MapperScan} for {@code com.spt.bas.system.dao} lives in
 * {@code ZgbasMybatisConfig} (single source of truth — WR-01).
 *
 * <p>{@link ComponentScan} is declared explicitly (overrides the default scan inside
 * {@link SpringBootApplication}) to add an {@code excludeFilters}: two {@code @Configuration}
 * classes share the bean name {@code feignConfig} — {@code com.spt.tools.http.feign.FeignConfig}
 * (inlined common) and {@code com.spt.sign.client.config.FeignConfig} (sign-client jar). Both
 * are per-client Feign configs instantiated inside Feign's child context via
 * {@code @FeignClient(configuration = ...)}; neither should be a top-level singleton. The source
 * {@code BasServer} avoided the conflict via a narrow {@code @ComponentScan(basePackages = {"com.spt.pm",
 * "com.spt.bas.server"})}; the monolith's broad {@code com.spt} scan requires the exclude filter instead
 * (Rule 1 fix — ConflictingBeanDefinitionException on context load).
 *
 * <p>Additionally, {@code com.spt.tools.shiro.config.ToolsShiroConfig} is excluded — Shiro
 * authentication is Phase 3 scope (AUTH-01..04, D-P2-06: "Shiro Realm Phase 3 writes"). The
 * inlined config's {@code EhCacheManager} conflicts with Hibernate's ehcache (same VM CacheManager
 * name) and its security/filter beans require a Realm not present until Phase 3. Excluding keeps
 * Shiro dormant without affecting any Phase 2 infrastructure (Rule 1 fix).
 *
 * <p>Dropped vs source {@code BasServer.java}: {@code @PropertySource} (D-P2-14 native
 * profile), {@code @EnableDiscoveryClient} (nacos removed, D-P2-11), {@code @Import} of
 * microservice residue (sign config auto-registers via the {@code com.spt} scan), the bare
 * {@code @ComponentScan} (superseded by the filtered one above).
 */
@SpringBootApplication
@ComponentScan(
    basePackages = "com.spt",
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = { com.spt.tools.http.feign.FeignConfig.class,
                    com.spt.sign.client.config.FeignConfig.class,
                    com.spt.tools.shiro.config.ToolsShiroConfig.class }
    )
)
@EnableFeignClients(basePackages = "com.spt.sign.client.remote")
@EntityScan(basePackageClasses = IdEntity.class,
            basePackages = {"com.spt.bas.client.entity", "com.spt.pm.entity"})
@EnableJpaRepositories(basePackages = {"com.spt.bas.server.dao"})
public class ZgbasApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZgbasApplication.class, args);
    }
}
