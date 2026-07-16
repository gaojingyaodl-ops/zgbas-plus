package com.spt.bas.server.dao;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;

import com.spt.bas.client.entity.BsCompanyOphis;
import com.spt.tools.jpa.dao.BaseDao;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BsCompanyOphisDao extends BaseDao<BsCompanyOphis> {
	@Transactional
	@Modifying
	void deleteByCompanyId(Long companyId);
	
	@Query("from BsCompanyOphis bco where bco.companyId=?1 and bco.createUserId <> 0 and bco.createUserId is not null and bco.createUserName is not null ORDER BY bco.createdDate asc")
	List<BsCompanyOphis> findByCompanyId(Long companyId);
}	

