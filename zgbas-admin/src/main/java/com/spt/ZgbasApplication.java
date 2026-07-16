package com.spt;

import com.spt.tools.jpa.vo.IdEntity;
import com.spt.tools.mybatis.annotation.MyBatisDao;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * zgbas-plus monolith boot class.
 *
 * <p>Keeps {@link SpringBootApplication} with NO auto-config exclusions: we WANT
 * DataSource + JPA + mybatis-plus auto-configuration to engage. Four infra annotations
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
 *   <li>{@link MapperScan} targeting {@code com.spt.bas.system.dao} with the
 *       {@link MyBatisDao} marker (sample Mapper from Plan 05).</li>
 * </ul>
 *
 * <p>Dropped vs source {@code BasServer.java}: {@code @PropertySource} (D-P2-14 native
 * profile), {@code @EnableDiscoveryClient} (nacos removed, D-P2-11), {@code @Import} of
 * microservice residue, {@code @ComponentScan} ({@code com.spt} base package covers all).
 */
@SpringBootApplication
@EnableFeignClients(basePackages = "com.spt.sign.client.remote")
@EntityScan(basePackageClasses = IdEntity.class,
            basePackages = {"com.spt.bas.client.entity"})
@EnableJpaRepositories(basePackages = {"com.spt.bas.server.dao"})
@MapperScan(basePackages = "com.spt.bas.system.dao",
            annotationClass = MyBatisDao.class)
public class ZgbasApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZgbasApplication.class, args);
    }
}
