package com.spt.bas.server.service.impl;

import com.spt.bas.server.dao.SealBorrowOphisDao;
import com.spt.bas.server.service.ISealBorrowOphisService;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.spt.bas.client.entity.SealBorrowOphis;

@Component
@Transactional(readOnly = true)
public class SealBorrowOphisServiceImpl extends BaseService<SealBorrowOphis> implements ISealBorrowOphisService {
	@Autowired
	private SealBorrowOphisDao sealBorrowOphisDao;
	
	@Override
	public BaseDao<SealBorrowOphis> getBaseDao() {
		return sealBorrowOphisDao;
	}
	
	@Override
	public Class<SealBorrowOphis> getEntityClazz() {
		return SealBorrowOphis.class;
	}
	
}

