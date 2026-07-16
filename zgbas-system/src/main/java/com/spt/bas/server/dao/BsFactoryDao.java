package com.spt.bas.server.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import com.spt.bas.client.entity.BsFactory;
import com.spt.tools.jpa.dao.BaseDao;

public interface BsFactoryDao extends BaseDao<BsFactory> {

	@Query("from BsFactory where enterpriseId = ?1 and enableFlg=true")
	List<BsFactory> findByEnterpriseId(Long enterpriseId);

	List<BsFactory> findByFactoryNameAndEnterpriseId(String factoryName, Long enterpriseId);
}

