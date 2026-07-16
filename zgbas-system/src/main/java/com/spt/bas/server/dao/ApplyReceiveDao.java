package com.spt.bas.server.dao;

import com.spt.bas.client.entity.ApplyReceive;
import com.spt.bas.client.vo.CtrLastDateVo;
import com.spt.tools.jpa.dao.BaseDao;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ApplyReceiveDao extends BaseDao<ApplyReceive> {

	@Modifying
	@Query("update ApplyReceive c set c.fileId =?2 where c.id=?1 ")
	void updateFileId(Long id, String fileId);

	List<ApplyReceive> findByContractId(Long contractId);

	List<ApplyReceive> findListByContractIdAndStatus(Long contractId,String status);

	@Modifying
	@Query("update ApplyReceive c set c.status ='C' where c.contractId=?1 ")
	void updateApplyStatus(Long contractId);

	List<ApplyReceive> findByContractNo(String contractNo);

	@Query("select max(c.receiveDate) from ApplyReceive c where c.contractId = ?1 and c.status = 'D'")
	Date findLastPay(Long contractId);

	@Query("SELECT NEW com.spt.bas.client.vo.CtrLastDateVo(contractId,receiveDate) FROM ApplyReceive WHERE contractId in ?1 AND status = 'D'")
	List<CtrLastDateVo> findLastPay(List<Long> contractIds);

	@Query(nativeQuery = true, value ="SELECT SUM(receive_amount) receiveAmount,MAX(receive_date) receiveDate,count(1) num FROM t_apply_receive WHERE contract_id = ?1 AND `status` = 'D'")
	Map<String , Object> findReceiveAmountSum(Long contractId);

	@Query(nativeQuery = true, value ="SELECT SUM(receive_amount) receiveAmount,MAX(receive_date) receiveDate,count(1) num FROM t_apply_receive WHERE contract_no = ?1 AND `status` = 'D'")
	Map<String , Object> findReceiveAmountSumByContractNo(String contractNo);

	ApplyReceive findByApplyNo(String applyNo);

	@Query("from ApplyReceive c where c.contractId =?1 and c.status = 'D' and c.receiveType != 'M' and c.receiveType != 'T' order by c.receiveDate asc")
	List<ApplyReceive> findAppReceiveComplete(Long contractId);

	@Query("from ApplyReceive where status = 'D' and receiveType != 'M' and receiveType != 'T' and contractNo in ?1")
	List<ApplyReceive> findByContractNoIn(List<String> contractNos);

	@Query("from ApplyReceive  r where r.parentReceiveId =?1 and r.status != 'C'")
	List<ApplyReceive> findApplyReceiveDetail(Long parentReceiveId);

	@Query("from ApplyReceive  r where r.parentReceiveId =?1")
	List<ApplyReceive> findApplyReceiveDetailAll(Long parentReceiveId);

	@Query("from ApplyReceive where id in ?1")
	List<ApplyReceive> findByIds(List<Long> ids);
}

