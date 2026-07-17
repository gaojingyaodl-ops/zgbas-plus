package com.spt.bas.server.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.spt.bas.client.entity.BsCompanyContacts;
import com.spt.bas.server.dao.BsCompanyContactsDao;
import com.spt.bas.server.service.IBsCompanyContactsService;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;

@Component
@Transactional(readOnly = true)
public class BsCompanyContactsServiceImpl extends BaseService<BsCompanyContacts> implements IBsCompanyContactsService {
	@Autowired
	private BsCompanyContactsDao bsCompanyContactsDao;
	
	@Override
	public BaseDao<BsCompanyContacts> getBaseDao() {
		return bsCompanyContactsDao;
	}
	
	@Override
	public Class<BsCompanyContacts> getEntityClazz() {
		return BsCompanyContacts.class;
	}
	
}

