package com.spt.pm.service.impl;

import com.spt.tools.core.exception.ApplicationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.spt.pm.annotation.ServerTransactional;
import com.spt.pm.dao.PmProcessConditionDao;
import com.spt.pm.dao.PmProcessStepDao;
import com.spt.pm.entity.PmProcessCondition;
import com.spt.pm.service.IPmProcessConditionService;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;

import java.util.List;

@Component
@Transactional(readOnly = true)
public class PmProcessConditionServiceImpl extends BaseService<PmProcessCondition> implements IPmProcessConditionService {
	@Autowired
	private PmProcessConditionDao pmProcessConditionDao;
	@Autowired
	private PmProcessStepDao processStepDao;
	
	@Override
	public BaseDao<PmProcessCondition> getBaseDao() {
		return pmProcessConditionDao;
	}
	
	@Override
	public Class<PmProcessCondition> getEntityClazz() {
		return PmProcessCondition.class;
	}

	@Override
	public Sort getDefaultSort() {
		Sort sort=Sort.by(Direction.ASC, "dispOrderNo");
		return sort;
	}
	
	@Override
	@ServerTransactional
	public void delete(Long id) throws ApplicationException {
		processStepDao.deleteByConditionId(id);
		super.delete(id);
	}

	@Override
	public List<PmProcessCondition> findConditionsByProcessId(Long processId) {
		return pmProcessConditionDao.findAllEnable(processId);
	}
}

