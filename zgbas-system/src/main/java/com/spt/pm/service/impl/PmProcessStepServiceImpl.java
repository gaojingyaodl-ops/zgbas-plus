package com.spt.pm.service.impl;

import com.spt.bas.client.constant.BasConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.spt.pm.dao.PmProcessStepDao;
import com.spt.pm.entity.PmProcessStep;
import com.spt.pm.service.IPmProcessStepService;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;

import java.util.List;

@Component
@Transactional(readOnly = true)
public class PmProcessStepServiceImpl extends BaseService<PmProcessStep> implements IPmProcessStepService {
	@Autowired
	private PmProcessStepDao pmProcessStepDao;

	@Override
	public BaseDao<PmProcessStep> getBaseDao() {
		return pmProcessStepDao;
	}

	@Override
	public Class<PmProcessStep> getEntityClazz() {
		return PmProcessStep.class;
	}

	@Override
	public Sort getDefaultSort() {
		Sort sort=Sort.by(Direction.ASC, "dispOrderNo");
		return sort;
	}

	/**
	 * 查询有效数据
	 *
	 * @return
	 */
	@Override
	public List<PmProcessStep> findEnable() {
		return pmProcessStepDao.findByEnterpriseIdAndEnableFlgTrue(BasConstants.ZG_ENTERPRISE_ID);
	}

	@Override
	public List<PmProcessStep> findStepByConditionId(Long conditionId) {
		return pmProcessStepDao.findByConditionId(conditionId);
	}

}

