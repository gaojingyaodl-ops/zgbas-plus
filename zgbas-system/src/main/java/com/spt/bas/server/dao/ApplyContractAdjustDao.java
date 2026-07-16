package com.spt.bas.server.dao;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.spt.bas.client.entity.ApplyContractAdjust;
import com.spt.tools.jpa.dao.BaseDao;

public interface ApplyContractAdjustDao extends BaseDao<ApplyContractAdjust> {

	@Modifying
	@Query("update ApplyContractAdjust c set c.buyContractFileId =?2 where c.id=?1 ")
	void updateBuyFileId(Long id, String fileId);

	@Modifying
	@Query("update ApplyContractAdjust c set c.sellContractFileId =?2 where c.id=?1 ")
	void updateSellFileId(Long id, String fileId);

	@Modifying
	@Query("update ApplyContractAdjust c set c.aboutContractFileId =?2 where c.id=?1 ")
	void updateFileId(Long id, String fileId);
}

