package com.spt.pm.dao;

import com.spt.pm.entity.PmApprove;
import com.spt.tools.jpa.dao.BaseDao;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

public interface PmApproveDao extends BaseDao<PmApprove> {
	@Transactional
	@Modifying
	@Query("update PmApprove a set a.status=?2 where a.id=?1 ")
	void updateStatus(Long id, String status);

	PmApprove findByApproveNo(String approveNo);

	@Query("from PmApprove a where a.id = ?1")
	PmApprove findApproveNoByApproveId(Long approveId);

	@Query("from PmApprove a where a.contractId = ?1 and a.processId = ?2")
	List<PmApprove> findApproveByContractIdAndProcessId(Long contractId, Long processId);

	@Query("from PmApprove a where a.contractId = ?1 and a.status = ?2")
	List<PmApprove> findApproveByContractIdAndStatus(Long contractId, String status);

	@Query("from PmApprove a where a.contractId = ?1 and a.status in ?2")
	List<PmApprove> findApproveByContractIdAndStatusIn(Long contractId, List<String> status);

	@Transactional
	@Modifying
	@Query("update PmApprove a set a.enableFlg = false where a.id=?1 ")
	void deleteRecord(Long approveId);

	PmApprove findTopByCompanyIdAndStatusOrderByIdDesc(Long companyId,String status);

	PmApprove findTopByIdAndCompanyIdAndStatusOrderByIdDesc(Long id,Long companyId,String status);

	@Transactional
	@Modifying
	@Query("update PmApprove a set a.status=?2,a.enableFlg = true where a.id=?1 ")
	void rollBackStatus(Long id, String status);

	@Query("from PmApprove a where a.status = 'D' and a.enableFlg = true and a.processId in ?1")
	List<PmApprove> findByProcessIdAndEffective(List<Long> processIdList);

	@Query("select count(*) from  PmApprove")
    Integer selectAllCount();

	@Query(nativeQuery = true, value = "SELECT a.* FROM t_pm_approve a WHERE a.auto_sign_limit > 0 AND a.`status` = 'A' AND DATE_ADD(IFNULL(a.last_approve_date,a.updated_date),INTERVAL a.auto_sign_limit MINUTE) <= NOW()")
	List<PmApprove> getAutoSignApproveList();

	@Query("from PmApprove p where p.status = 'D' and p.enableFlg = true and p.contractId in ?1 and p.subject like %?2%")
	List<PmApprove> findCanInvalidList(List<Long> contractIds, String contractNo);

	@Query("from PmApprove p where p.id in ?1")
	List<PmApprove> findByIds(List<Long> ids);

	@Query("from PmApprove p where p.contractId in ?1 and p.subject like %?2%")
    List<PmApprove> findByContractIdsInAndContractNo(List<Long> contractList, String contractNoSuffix);
}

