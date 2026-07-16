package com.spt.bas.server.dao;

import com.spt.bas.client.entity.ApplyPay;
import com.spt.bas.client.vo.CtrLastDateVo;
import com.spt.tools.jpa.dao.BaseDao;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface ApplyPayDao extends BaseDao<ApplyPay> {
	
	@Modifying
	@Query("update ApplyPay c set c.fileId =?2 where c.id=?1 ")
	void updateFileId(Long id, String fileId);
	
	@Query("from ApplyPay c where c.contractId=?1 and c.status !='C'")
	List<ApplyPay> findByContractId(Long contractId);
	
	@Modifying
	@Query("update ApplyPay c set c.status ='C' where c.contractId=?1 ")
	void updateApplyStatus(Long contractId);
	
	List<ApplyPay> findByContractNo(String contractNo);
	
	@Query("select max(c.payDate) from ApplyPay c where c.contractId = ?1 and c.status = 'D'")
	Date findLastPay(Long contractId);

	@Query("SELECT NEW com.spt.bas.client.vo.CtrLastDateVo(contractId,payDate) FROM ApplyPay WHERE contractId in ?1 AND status = 'D'")
	List<CtrLastDateVo> findLastPay(List<Long> contractIds);

	ApplyPay findApplyPayByContractNo(String contractNo);

	@Query("select count(p.id) from ApplyPay p where p.contractNo =?1 and p.factorAmount > 0 and p.status in ('A','D')")
	Long getFactorPayCount(String contractNo);

	@Query("from ApplyPay where status = 'D' and contractNo in ?1")
	List<ApplyPay> findByContractNoIn(List<String> contractNos);

	@Query("select sum(p.payAmount) from ApplyPay p left join PmApprove a on p.approveId = a.id where p.contractNo =?1 and a.status in ('A','D')")
	BigDecimal findSumApplyAmountPay(String contractNo);
}

