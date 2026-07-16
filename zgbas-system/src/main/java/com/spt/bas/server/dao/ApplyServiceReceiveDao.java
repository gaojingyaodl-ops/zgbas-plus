package com.spt.bas.server.dao;

import com.spt.bas.client.entity.ApplyServiceReceive;
import com.spt.tools.jpa.dao.BaseDao;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ApplyServiceReceiveDao extends BaseDao<ApplyServiceReceive> {
	
	@Modifying
	@Query("update ApplyServiceReceive c set c.fileId =?2 where c.id=?1 ")
	void updateFileId(Long id, String fileId);
	
	List<ApplyServiceReceive> findByContractId(Long contractId);

	List<ApplyServiceReceive> findByServiceContractId(Long serviceContractId);

	List<ApplyServiceReceive> findByServiceContractNo(String contractNo);
	
	@Modifying
	@Query("update ApplyServiceReceive c set c.status ='C' where c.serviceContractId=?1 ")
	void updateApplyStatus(Long serviceContractId);
}

