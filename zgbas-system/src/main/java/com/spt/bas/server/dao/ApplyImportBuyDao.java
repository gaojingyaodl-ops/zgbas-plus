package com.spt.bas.server.dao;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;


import com.spt.bas.client.entity.ApplyImportBuy;
import com.spt.tools.jpa.dao.BaseDao;

public interface ApplyImportBuyDao extends BaseDao<ApplyImportBuy> {

	@Modifying
	@Query("update ApplyImportBuy c set c.fileId =?2 where c.id=?1 ")
	void updateFileId(Long id, String fileId);
	
	ApplyImportBuy findByContractId(Long contractId);
	
	@Modifying
	@Query("update ApplyImportBuy c set c.status = 'C' where c.contractId=?1")
	void updateStatusC(Long id);
}

