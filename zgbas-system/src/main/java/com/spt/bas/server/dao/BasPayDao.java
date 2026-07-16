package com.spt.bas.server.dao;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.spt.bas.client.entity.BasPay;
import com.spt.tools.jpa.dao.BaseDao;

public interface BasPayDao extends BaseDao<BasPay> {
	
	BasPay findByContractId(Long contractId);
	
	@Transactional
	@Modifying
	@Query("update BasPay c set c.fileId =?2 where c.id=?1 ")
	public void updateFileId(Long id, String fileId);
}

