package com.spt.bas.server.dao;

import com.spt.tools.jpa.dao.BaseDao;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.spt.bas.client.entity.ApplyDiscuss;

public interface ApplyDiscussDao extends BaseDao<ApplyDiscuss> {
	
	@Modifying
	@Query("update ApplyDiscuss a set a.fileId =?2 where a.id=?1 ")
	void updateFileId(Long id, String fileId);
	
	@Query("from ApplyDiscuss s where s.buyContractId = ?1 and status != 'N'")
	List<ApplyDiscuss> findByBuyContractId(Long buyContractId);
}

