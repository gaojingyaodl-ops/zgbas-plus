package com.spt.bas.system.dao;

import com.spt.tools.mybatis.annotation.MyBatisDao;

/**
 * Trivial proof: mybatis-plus can execute a query through the same DataSource as JPA.
 * Targets t_api_external_his — the table that backs the ApiExternalHis @Entity
 * (verified present in com.spt.bas.client.entity.ApiExternalHis with
 * @Table(name = "t_api_external_his")). Proves the dual-ORM single-DataSource wiring
 * (PERSIST-03). Picked up by ZgbasMybatisConfig's
 * @MapperScan(basePackages = "com.spt.bas.system.dao", annotationClass = MyBatisDao.class).
 */
@MyBatisDao
public interface SampleMapper {

    long countAll();
}
