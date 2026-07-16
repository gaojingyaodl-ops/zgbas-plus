package com.spt.bas.server.dao;

import com.spt.bas.client.entity.BsCompanyShare;
import com.spt.tools.jpa.dao.BaseDao;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

/**
 * 企业共享记录
 * @author wlddh
 *
 */
public interface BsCompanyShareDao extends BaseDao<BsCompanyShare> {

	BsCompanyShare findByCompanyIdAndSharedUserId(Long companyId, Long sharedUserId);

	List<BsCompanyShare> findByCompanyIdAndCreateUserId(Long companyId, Long createUserId);
	
	List<BsCompanyShare> findBySharedUserId(Long shareUserId);

	@Transactional
	@Modifying
	void deleteByCompanyId(Long companyId);

	List<BsCompanyShare> findByCompanyId(Long id);
	@Query("select s.companyId from BsCompanyShare s where s.sharedUserId = ?1")
	List<Long> findCompanyIdBySharedUserId(Long sharedUserId);

	List<BsCompanyShare> findByCompanyIdIn(List<Long> companyIds);

}

