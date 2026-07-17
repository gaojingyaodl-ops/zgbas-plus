package com.spt.bas.server.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.spt.bas.client.entity.BsCompanyFollow;
import com.spt.bas.server.dao.BsCompanyFollowDao;
import com.spt.bas.server.service.IBsCompanyFollowService;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;

@Component
@Transactional(readOnly = true)
public class BsCompanyFollowServiceImpl extends BaseService<BsCompanyFollow> implements IBsCompanyFollowService {
	@Autowired
	private BsCompanyFollowDao bsCompanyFollowDao;
	
	@Override
	public BaseDao<BsCompanyFollow> getBaseDao() {
		return bsCompanyFollowDao;
	}
	
	@Override
	public Class<BsCompanyFollow> getEntityClazz() {
		return BsCompanyFollow.class;
	}
}

