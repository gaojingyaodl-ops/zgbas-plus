package com.spt.bas.server.dao;

import com.spt.bas.client.entity.ApplyPayRefund;
import com.spt.tools.jpa.dao.BaseDao;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ApplyPayRefundDao extends BaseDao<ApplyPayRefund> {
	@Modifying
	@Query("update ApplyPayRefund c set c.fileId =?2 where c.id=?1 ")
	void updateFileId(Long id, String fileId);

	List<ApplyPayRefund> findByContractNo(String contractNo);

	@Query("from ApplyPayRefund where status = 'D' and contractNo in ?1")
	List<ApplyPayRefund> findByContractNoIn(List<String> contractNos);
}

