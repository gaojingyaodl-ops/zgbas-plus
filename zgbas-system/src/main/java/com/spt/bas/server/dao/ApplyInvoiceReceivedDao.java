package com.spt.bas.server.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.spt.bas.client.vo.CtrLastDateVo;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.spt.bas.client.entity.ApplyInvoiceReceived;
import com.spt.tools.jpa.dao.BaseDao;

public interface ApplyInvoiceReceivedDao extends BaseDao<ApplyInvoiceReceived> {
	@Modifying
	@Query("update ApplyInvoiceReceived c set c.fileId =?2 where c.id=?1 ")
	void updateFileId(Long id, String fileId);

	@Query("select max(inInvoiceDate) from ApplyInvoiceReceived c where c.contractId = ?1 and c.status = 'D'")
	Date findLastBill(Long contractId);

	@Query("SELECT NEW com.spt.bas.client.vo.CtrLastDateVo(contractId,inInvoiceDate)  FROM ApplyInvoiceReceived WHERE contractId in ?1 AND status = 'D'")
	List<CtrLastDateVo> findLastBill(List<Long> contractIds);

	@Query("from ApplyInvoiceReceived a where a.contractId = ?1 and a.status != 'C'")
	List<ApplyInvoiceReceived> findByContractId(Long contractId);

	@Query("from ApplyInvoiceReceived a where a.contractNo = ?1 and a.status != 'C'")
	List<ApplyInvoiceReceived> findByContractNo(String contractNo);
}

