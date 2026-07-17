package com.spt.pm.dao;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.spt.pm.entity.PmProcessCondition;
import com.spt.tools.jpa.dao.BaseDao;

public interface PmProcessConditionDao extends BaseDao<PmProcessCondition> {
	
//	List<PmProcessCondition> findByProcessId(Long processId);
	
	@Query("from PmProcessCondition c where c.processId=?1 and c.enableFlg=true order by c.dispOrderNo")
	List<PmProcessCondition> findAllEnable(Long processId);
	
	@Transactional
	@Modifying
	void deleteByProcessId(Long processId);
}

