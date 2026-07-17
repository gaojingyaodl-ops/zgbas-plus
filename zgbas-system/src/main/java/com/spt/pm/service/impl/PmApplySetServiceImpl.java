package com.spt.pm.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.spt.pm.dao.PmApplySetDao;
import com.spt.pm.entity.PmApplySet;
import com.spt.pm.service.IPmApplySetService;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;

@Component
@Transactional(readOnly = true)
public class PmApplySetServiceImpl extends BaseService<PmApplySet> implements IPmApplySetService {
	@Autowired
	private PmApplySetDao pmApplySetDao;
	
	@Override
	public BaseDao<PmApplySet> getBaseDao() {
		return pmApplySetDao;
	}
	
	@Override
	public Class<PmApplySet> getEntityClazz() {
		return PmApplySet.class;
	}

	@Override
	public List<PmApplySet> findByProcessId(Long processId) {
		return pmApplySetDao.findByProcessId(processId);
	}
	
}

