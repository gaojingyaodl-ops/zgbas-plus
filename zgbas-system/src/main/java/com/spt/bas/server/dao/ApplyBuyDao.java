package com.spt.bas.server.dao;

import com.spt.bas.client.entity.ApplyBuy;
import com.spt.tools.jpa.dao.BaseDao;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ApplyBuyDao extends BaseDao<ApplyBuy> {
	
	@Modifying
	@Query("update ApplyBuy c set c.fileId =?2 where c.id=?1 ")
	void updateFileId(Long id, String fileId);
	
	@Modifying
	@Query("update ApplyBuy c set c.status ='C' where c.contractId=?1 ")
	void updateApplyStatus(Long contractId);
	
	ApplyBuy findByContractId(Long contractId);
	
	ApplyBuy findByContractNo(String contractNo);

	ApplyBuy findByApproveId(Long approveId);
}

