package com.spt.bas.server.dao;

import com.spt.bas.client.entity.ApplyReceiveRefundDcsx;
import com.spt.tools.jpa.dao.BaseDao;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ApplyReceiveRefundDcsxDao extends BaseDao<ApplyReceiveRefundDcsx> {
	@Modifying
	@Query("update ApplyReceiveRefundDcsx c set c.fileId =?2 where c.id=?1 ")
	void updateFileId(Long id, String fileId);

	List<ApplyReceiveRefundDcsx> findByContractNo(String contractNo);

	@Query(value = "from ApplyReceiveRefundDcsx r where r.status = 'D' and r.contractNo in ?1")
	List<ApplyReceiveRefundDcsx> findByContractNoIn(List<String> contractNoList);
}

