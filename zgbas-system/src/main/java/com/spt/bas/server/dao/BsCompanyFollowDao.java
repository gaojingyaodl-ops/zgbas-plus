package com.spt.bas.server.dao;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;

import com.spt.bas.client.entity.BsCompanyFollow;
import com.spt.tools.jpa.dao.BaseDao;

public interface BsCompanyFollowDao extends BaseDao<BsCompanyFollow> {
	@Transactional
	@Modifying
	void deleteByCompanyId(Long companyId);

	BsCompanyFollow findTopByCompanyIdAndCreateUserIdOrderByCreatedDateDesc(Long companyId,Long createUserId);
}

