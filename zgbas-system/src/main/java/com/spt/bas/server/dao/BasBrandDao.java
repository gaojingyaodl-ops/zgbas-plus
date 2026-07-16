package com.spt.bas.server.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import com.spt.bas.client.entity.BasBrand;
import com.spt.tools.jpa.dao.BaseDao;

public interface BasBrandDao extends BaseDao<BasBrand> {

	@Query("from BasBrand b where b.productCd=?1 and b.enterpriseId =?2 ")
	List<BasBrand> findsBrand(String productCd, Long enterpriseId);

	@Query("from BasBrand b where b.enterpriseId = ?1 and b.safeFlg = true")
	List<BasBrand> findSafeBrand(Long enId);

	@Query("from BasBrand b where b.enterpriseId = ?1")
	List<BasBrand> findBrand(Long enId);

}

