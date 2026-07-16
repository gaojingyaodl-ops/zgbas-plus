package com.spt.bas.server.dao;

import com.spt.tools.jpa.dao.BaseDao;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.spt.bas.client.entity.ApplyLoss;

public interface ApplyLossDao extends BaseDao<ApplyLoss> {
	
	@Modifying
	@Query("update ApplyLoss a set a.fileId =?2 where a.id=?1 ")
	void updateFileId(Long id, String fileId);
	
	@Query("from ApplyLoss where sellContractId = ?1 and status != 'N'")
	List<ApplyLoss> findBySellContractId(Long sellContractId);
}

