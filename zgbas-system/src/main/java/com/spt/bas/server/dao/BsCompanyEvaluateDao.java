package com.spt.bas.server.dao;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;

import com.spt.bas.client.entity.BsCompanyEvaluate;
import com.spt.tools.jpa.dao.BaseDao;

public interface BsCompanyEvaluateDao extends BaseDao<BsCompanyEvaluate> {
	@Transactional
	@Modifying
	void deleteByCompanyId(Long companyId);
}

