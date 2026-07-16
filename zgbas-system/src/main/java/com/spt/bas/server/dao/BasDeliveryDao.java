package com.spt.bas.server.dao;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.spt.bas.client.entity.BasDelivery;
import com.spt.tools.jpa.dao.BaseDao;

public interface BasDeliveryDao extends BaseDao<BasDelivery> {
	
	BasDelivery findByContractId(Long contractId);
	
	@Transactional
	@Modifying
	@Query("update BasDelivery c set c.fileId =?2 where c.id=?1 ")
	public void updateFileId(Long id, String fileId);
}

