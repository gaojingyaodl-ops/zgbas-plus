package com.spt.bas.server.dao;

import com.spt.bas.client.entity.CtrServiceContract;
import com.spt.tools.jpa.dao.BaseDao;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface CtrServiceContractDao extends BaseDao<CtrServiceContract> {

	@Modifying
	@Query("update CtrServiceContract c set c.fileId =?2 where c.id=?1 ")
	void updateFileId(Long id, String fileId);

	CtrServiceContract findByCtrContractId(Long ctrContractId);
}

