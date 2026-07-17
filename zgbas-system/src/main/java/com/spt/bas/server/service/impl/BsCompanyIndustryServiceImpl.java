package com.spt.bas.server.service.impl;

import com.spt.bas.server.dao.BsCompanyIndustryDao;
import com.spt.bas.server.service.IBsCompanyIndustryService;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.spt.bas.client.entity.BsCompanyIndustry;

@Component
@Transactional(readOnly = true)
public class BsCompanyIndustryServiceImpl extends BaseService<BsCompanyIndustry> implements IBsCompanyIndustryService {
	@Autowired
	private BsCompanyIndustryDao bsCompanyIndustryDao;
	
	@Override
	public BaseDao<BsCompanyIndustry> getBaseDao() {
		return bsCompanyIndustryDao;
	}
	
	@Override
	public Class<BsCompanyIndustry> getEntityClazz() {
		return BsCompanyIndustry.class;
	}
	
}

