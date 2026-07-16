package com.spt.bas.server.dao;

import com.spt.bas.client.entity.ApplyInterestPay;
import com.spt.tools.jpa.dao.BaseDao;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ApplyInterestPayDao extends BaseDao<ApplyInterestPay> {
	
	@Modifying
	@Query("update ApplyInterestPay c set c.fileId =?2 where c.id=?1 ")
	void updateFileId(Long id, String fileId);
}

