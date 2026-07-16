package com.spt.bas.server.dao;

import com.spt.bas.client.entity.ApplyInvoiceReceived;
import com.spt.tools.jpa.dao.BaseDao;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface ApplyInvoiceReceivedDcsxDao extends BaseDao<ApplyInvoiceReceived> {
	@Modifying
	@Query("update ApplyInvoiceReceived c set c.fileId =?2 where c.id=?1 ")
	void updateFileId(Long id, String fileId);

	@Query("from ApplyInvoiceReceived a where a.contractId = ?1 and a.status != 'C'")
	List<ApplyInvoiceReceived> findByContractId(Long contractId);
}

