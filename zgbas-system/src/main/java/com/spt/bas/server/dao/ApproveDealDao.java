package com.spt.bas.server.dao;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.spt.bas.client.entity.ApproveDeal;
import com.spt.tools.jpa.dao.BaseDao;

public interface ApproveDealDao extends BaseDao<ApproveDeal> {
	ApproveDeal findByRelationId(Long relationId);

	@Modifying
	@Transactional
	@Query("delete from ApproveDeal a where a.relationId=?1")
	void deleteByRelationId(Long relationId);

	@Modifying
	@Transactional
	@Query("delete from ApproveDeal a where a.processCode=?1 and a.remark=?2 and a.relationId=0")
	void deleteByDealType(String processCode, String contractId);

	@Modifying
	@Transactional
	@Query("update ApproveDeal a set a.subject=?3 where a.processCode=?2 and a.remark=?1")
	void updateSubject(String contractId, String processCode, String subject);

	@Modifying
	@Transactional
	@Query("delete from ApproveDeal a where a.remark=?1")
	void deleteByRemark(String remark);

}

