package com.spt.bas.server.dao;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;

import com.spt.bas.client.entity.ApplyCancelDetail;
import com.spt.tools.jpa.dao.BaseDao;

public interface ApplyCancelDetailDao extends BaseDao<ApplyCancelDetail> {

	List<ApplyCancelDetail> findByApplyCancelId(Long id);
	
	@Transactional
	@Modifying
	public void deleteByApplyCancelId(Long id);
}

