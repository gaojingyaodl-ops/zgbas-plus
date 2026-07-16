package com.spt.bas.server.dao;

import com.spt.bas.client.entity.ApplyInvoice;
import com.spt.bas.client.vo.CtrInvoiceDataVo;
import com.spt.bas.client.vo.CtrLastDateVo;
import com.spt.tools.jpa.dao.BaseDao;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface ApplyInvoiceDao extends BaseDao<ApplyInvoice> {

	@Modifying
	@Query("update ApplyInvoice c set c.fileId =?2 where c.id=?1 ")
	void updateFileId(Long id, String fileId);

	@Query("select max(c.invoiceDate) from ApplyInvoice c where c.contractId = ?1 and c.status = 'D'")
	Date findMaxBillDate(Long contractId);

	@Query("SELECT NEW com.spt.bas.client.vo.CtrLastDateVo(contractId,invoiceDate) FROM ApplyInvoice WHERE contractId in ?1 AND status = 'D'")
	List<CtrLastDateVo> findLastBill(List<Long> contractIds);

	List<ApplyInvoice> findByContractNo(String contractNo);

	@Query("from ApplyInvoice a where a.contractId =?1 and a.status != 'C'")
	List<ApplyInvoice> findByContractId(Long contractId);

	@Query("SELECT NEW com.spt.bas.client.vo.CtrInvoiceDataVo(contractNo,fileId) FROM ApplyInvoice WHERE contractNo in ?1 AND status = 'D' AND fileId is not null")
	List<CtrInvoiceDataVo> findInvoiceFile(List<String> contractNos);

}

