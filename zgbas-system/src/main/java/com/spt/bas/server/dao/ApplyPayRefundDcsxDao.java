package com.spt.bas.server.dao;

import com.spt.bas.client.entity.ApplyPayRefundDcsx;
import com.spt.tools.jpa.dao.BaseDao;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ApplyPayRefundDcsxDao extends BaseDao<ApplyPayRefundDcsx> {
	@Modifying
	@Query("update ApplyPayRefundDcsx c set c.fileId =?2 where c.id=?1 ")
	void updateFileId(Long id, String fileId);

	List<ApplyPayRefundDcsx> findByContractNo(String contractNo);
}

