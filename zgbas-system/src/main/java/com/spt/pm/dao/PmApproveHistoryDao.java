package com.spt.pm.dao;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;

import com.spt.pm.entity.PmApproveHistory;
import com.spt.tools.jpa.dao.BaseDao;

public interface PmApproveHistoryDao extends BaseDao<PmApproveHistory> {
	
	List<PmApproveHistory> findByApproveId(Long approveId);
	
	@Transactional
	@Modifying
	void deleteByApproveId(Long approveId);
	
	List<PmApproveHistory> findByApproveIdAndApproveStepIdOrderByIdDesc(Long approveId, Long approveStepId);
	
}

