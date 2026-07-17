package com.spt.pm.dao;

import com.spt.pm.entity.PmProcess;
import com.spt.tools.jpa.dao.BaseDao;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PmProcessDao extends BaseDao<PmProcess> {

	PmProcess findByProcessCodeAndEnterpriseId(String processCode, Long enterpriseId);

	PmProcess findByApplyType(String applyType);

	@Query("select p from PmProcess p ,PmProcessAccess a where p.id = a.processId and p.enterpriseId=?1 and a.userId =?2 and p.enableFlg=true order by p.dispOrderNo asc")
	List<PmProcess> findAccess(Long enterpriseId, Long userId);
	@Query("select p from PmProcess p ,PmProcessAccess a where p.id = a.processId and p.enterpriseId=?1 and a.userId =?2 and p.processGroup =?3 and p.enableFlg=true order by p.dispOrderNo asc")
	List<PmProcess> findAccess(Long enterpriseId, Long userId, String processGroup);

	@Query("from PmProcess p where p.enterpriseId=?1 and p.enableFlg=true order by p.dispOrderNo asc")
	List<PmProcess> findByEnterpriseIdAndEnableFlgTrue(Long enterpriseId);

	List<PmProcess> findByProcessCodeInAndEnterpriseId(String[] process, Long enterpriseId);

	@Query(value="SELECT GROUP_CONCAT(id SEPARATOR '|') FROM t_pm_process WHERE enable_flg= true AND enterprise_id = 44 AND process_code IN('APPLY_BUY','APPLY_SELL','APPLY_MATCH','APPLY_MATCH_IOUS','APPLY_SEAL_USAGE','APPLY_SEAL_BORROW','APPLY_MATCH_PALLET','APPLY_CHARGE_SALES','APPLY_OPERATING_BUSINESS_PAY','APPLY_INTERNAL_BUSINESS_PAY','APPLY_VEHICLE_USE','APPLY_BRAND','APPLY_REFUND_BUSINESS_PAY') ",nativeQuery = true)
	String initPmProcessList();

	@Query("select p.id from PmProcess p where p.enterpriseId =?1 and p.enableFlg = true and p.processGroup = 'biz'")
	List<Long> getBusinessProcessId(Long enterpriseId);

	@Query("from PmProcess p where p.enterpriseId=?1 and p.enableFlg=true and p.viewFlg=?2 order by p.dispOrderNo asc")
	List<PmProcess> findByEnterpriseIdAndEnableFlgTrueAndViewFlg(Long enterpriseId, Boolean viewFlg);
}

