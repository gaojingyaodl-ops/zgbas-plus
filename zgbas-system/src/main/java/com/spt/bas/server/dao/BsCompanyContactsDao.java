package com.spt.bas.server.dao;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;

import com.spt.bas.client.entity.BsCompanyContacts;
import com.spt.tools.jpa.dao.BaseDao;

public interface BsCompanyContactsDao extends BaseDao<BsCompanyContacts> {
	@Transactional
	@Modifying
	void deleteByCompanyId(Long companyId);
}

