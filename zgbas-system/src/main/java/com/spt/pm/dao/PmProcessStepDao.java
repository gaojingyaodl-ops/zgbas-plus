package com.spt.pm.dao;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.spt.pm.entity.PmProcessStep;
import com.spt.tools.jpa.dao.BaseDao;

public interface PmProcessStepDao extends BaseDao<PmProcessStep> {

	@Query("from PmProcessStep s where s.conditionId = ?1 and s.enableFlg = '1' order by s.dispOrderNo asc")
	List<PmProcessStep> findByConditionId(Long conditionId);


	@Transactional
	@Modifying
	void deleteByConditionId(Long conditionId);

	@Query("from PmProcessStep s where s.processId = ?1 and s.enableFlg = '1' and s.enterpriseId = ?2 order by s.dispOrderNo asc")
	List<PmProcessStep> findByProcessId(Long processId, Long enterpriseId);

	List<PmProcessStep> findByEnterpriseIdAndEnableFlgTrue(Long enterpriseId);

}

