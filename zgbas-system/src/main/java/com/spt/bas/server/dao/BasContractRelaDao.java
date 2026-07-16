package com.spt.bas.server.dao;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.spt.bas.client.entity.BasContractRela;
import com.spt.tools.jpa.dao.BaseDao;

public interface BasContractRelaDao extends BaseDao<BasContractRela> {
	@Transactional
	@Modifying
	@Query("update BasContractRela c set c.fileId =?2 where c.id=?1 ")
	void updateFileId(Long id, String fileId);
	
	@Query("select sellContractId from BasContractRela where buyContractId = ?1")
	String findSaleContractIdByBuyId(String buyContractId);
}

