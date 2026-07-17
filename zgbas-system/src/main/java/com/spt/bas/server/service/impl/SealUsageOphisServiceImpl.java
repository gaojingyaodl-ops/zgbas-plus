package com.spt.bas.server.service.impl;

import com.spt.bas.server.dao.SealUsageOphisDao;
import com.spt.bas.server.service.ISealUsageOphisService;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.spt.bas.client.entity.SealUsageOphis;

@Component
@Transactional(readOnly = true)
public class SealUsageOphisServiceImpl extends BaseService<SealUsageOphis> implements ISealUsageOphisService {
	@Autowired
	private SealUsageOphisDao sealUsageOphisDao;
	
	@Override
	public BaseDao<SealUsageOphis> getBaseDao() {
		return sealUsageOphisDao;
	}
	
	@Override
	public Class<SealUsageOphis> getEntityClazz() {
		return SealUsageOphis.class;
	}
	
}

