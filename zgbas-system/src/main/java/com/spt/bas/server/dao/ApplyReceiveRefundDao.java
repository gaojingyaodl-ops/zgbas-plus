package com.spt.bas.server.dao;

import com.spt.bas.client.entity.ApplyReceiveRefund;
import com.spt.tools.jpa.dao.BaseDao;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ApplyReceiveRefundDao extends BaseDao<ApplyReceiveRefund> {
	@Modifying
	@Query("update ApplyReceiveRefund c set c.fileId =?2 where c.id=?1 ")
	void updateFileId(Long id, String fileId);

	List<ApplyReceiveRefund> findByContractNo(String contractNo);

	@Query(value = "from ApplyReceiveRefund r where r.status = 'D' and r.contractNo in ?1")
	List<ApplyReceiveRefund> findByContractNoIn(List<String> contractNoList);
}

