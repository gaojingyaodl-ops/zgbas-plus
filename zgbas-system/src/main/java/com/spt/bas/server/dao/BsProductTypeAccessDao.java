package com.spt.bas.server.dao;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.spt.bas.client.entity.BsProductTypeAccess;
import com.spt.tools.jpa.dao.BaseDao;

public interface BsProductTypeAccessDao extends BaseDao<BsProductTypeAccess> {

	Long countByProductCdAndEnterpriseId(String productCd, Long enterpriseId);

	@Transactional
	@Modifying
	@Query("delete from BsProductTypeAccess where enterpriseId=?1 ")
	public void deleteAll(Long enterpriseId);

	List<BsProductTypeAccess> findByEnterpriseId(Long enterpriseId);
}

