package com.spt.bas.server.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.spt.bas.client.entity.BsCompanyEvaluate;
import com.spt.bas.server.dao.BsCompanyEvaluateDao;
import com.spt.bas.server.service.IBsCompanyEvaluateService;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;

@Component
@Transactional(readOnly = true)
public class BsCompanyEvaluateServiceImpl extends BaseService<BsCompanyEvaluate> implements IBsCompanyEvaluateService {
	@Autowired
	private BsCompanyEvaluateDao bsCompanyEvaluateDao;
	
	@Override
	public BaseDao<BsCompanyEvaluate> getBaseDao() {
		return bsCompanyEvaluateDao;
	}
	
	@Override
	public Class<BsCompanyEvaluate> getEntityClazz() {
		return BsCompanyEvaluate.class;
	}
	
}

