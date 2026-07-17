package com.spt.bas.server.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.spt.bas.client.entity.BsLog;
import com.spt.bas.server.dao.BsLogDao;
import com.spt.bas.server.service.IBsLogService;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;

@Component
@Transactional(readOnly = true)
public class BsLogServiceImpl extends BaseService<BsLog> implements IBsLogService {
	@Autowired
	private BsLogDao bsLogDao;
	
	@Override
	public BaseDao<BsLog> getBaseDao() {
		return bsLogDao;
	}
	
	@Override
	public Class<BsLog> getEntityClazz() {
		return BsLog.class;
	}
	
}

