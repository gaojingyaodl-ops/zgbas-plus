package com.spt.bas.server.dao;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.spt.bas.client.entity.ApplyPay;
import com.spt.bas.client.entity.ApplyServiceContract;
import com.spt.tools.jpa.dao.BaseDao;

public interface ApplyServiceContractDao extends BaseDao<ApplyServiceContract> {

	@Transactional
	@Modifying
	@Query("update ApplyServiceContract c set c.fileId =?2 where c.id=?1 ")
	public void updateFileId(Long id, String fileId);
	
	ApplyServiceContract findByContractId(Long contractId);

	List<ApplyPay> findByLinkContractNo(String contractNo);
}

