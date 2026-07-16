package com.spt.bas.server.dao;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.spt.bas.client.entity.ApplyPresell;
import com.spt.tools.jpa.dao.BaseDao;

public interface ApplyPresellDao extends BaseDao<ApplyPresell> {
	
	@Modifying
	@Query("update ApplyPresell c set c.status ='C' where c.contractId=?1 ")
	void updateApplyStatus(Long contractId);
	
	ApplyPresell findByContractId(Long contractId);
	
	@Modifying
	@Query("update ApplyPresell c set c.fileId =?2 where c.id=?1 ")
	void updateFileId(Long id, String fileId);
}

