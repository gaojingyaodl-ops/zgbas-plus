package com.spt.pm.dao;

import java.util.List;

import com.spt.pm.entity.PmProcessAccess;
import com.spt.tools.jpa.dao.BaseDao;
import org.springframework.data.jpa.repository.Query;

public interface PmProcessAccessDao extends BaseDao<PmProcessAccess> {
	
	List<PmProcessAccess> findByProcessId(Long processId);
	
	PmProcessAccess findByProcessIdAndUserId(Long processId, Long userId);
	
	List<PmProcessAccess> findByUserId(Long userId);

	@Query(nativeQuery = true, value = "SELECT a.* FROM t_pm_process_access a LEFT JOIN t_pm_process p ON a.process_id = p.id WHERE p.process_code =?1 AND a.user_id =?2")
	List<PmProcessAccess> findByUserIdAndProcessCode(String processCode, Long userId);
}

