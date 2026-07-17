package com.spt.pm.dao;

import com.spt.pm.entity.PmApproveStep;
import com.spt.tools.jpa.dao.BaseDao;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

public interface PmApproveStepDao extends BaseDao<PmApproveStep> {
	@Query("from PmApproveStep s where s.approveId =?1 order by s.dispOrderNo ")
	List<PmApproveStep> findByApproveId(Long approveId);
	
	@Query("from PmApproveStep s where s.approveId =?1 order by s.dispOrderNo desc")
	List<PmApproveStep> findByApproveIdDesc(Long approveId);
	
	@Transactional
	@Modifying
	void deleteByApproveId(Long approveId);

	@Query("from PmApproveStep s where s.id in ?1 order by s.dispOrderNo desc")
	List<PmApproveStep> findStepByIds(List<Long> stepIdList);
}

