package com.spt.bas.server.dao;

import com.spt.tools.jpa.dao.BaseDao;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.spt.bas.client.entity.ApplyBusinessPay;

public interface ApplyBusinessPayDao extends BaseDao<ApplyBusinessPay> {

	@Modifying
	@Query("update ApplyBusinessPay c set c.fileId =?2 where c.id=?1 ")
	void updateFileId(Long id, String fileId);



	@Query("FROM ApplyBusinessPay p WHERE  p.approveId=?1")
	ApplyBusinessPay findByApproveId(Long id);
}

