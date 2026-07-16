package com.spt.bas.server.dao;


import javax.transaction.Transactional;

import com.spt.bas.client.entity.ApplyMatch;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.spt.bas.client.entity.ApplySell;
import com.spt.tools.jpa.dao.BaseDao;

public interface ApplySellDao extends BaseDao<ApplySell> {
	
	@Transactional
	@Modifying
	@Query("update ApplySell c set c.fileId =?2 where c.id=?1 ")
	public void updateFileId(Long id, String fileId);
	
	@Modifying
	@Query("update ApplySell c set c.status ='C' where c.contractId=?1 ")
	void updateApplyStatus(Long contractId);
	
	ApplySell findByContractId(Long contractId);

	ApplySell findByApproveId(Long approveId);
}

