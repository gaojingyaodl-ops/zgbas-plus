package com.spt.bas.server.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.spt.bas.client.entity.BsAreaCost;
import com.spt.bas.server.dao.BsAreaCostDao;
import com.spt.bas.server.service.IBsAreaCostService;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;

@Component
@Transactional(readOnly = true)
public class BsAreaCostServiceImpl extends BaseService<BsAreaCost> implements IBsAreaCostService {
	@Autowired
	private BsAreaCostDao bsAreaCostDao;
	
	@Override
	public BaseDao<BsAreaCost> getBaseDao() {
		return bsAreaCostDao;
	}
	
	@Override
	public Class<BsAreaCost> getEntityClazz() {
		return BsAreaCost.class;
	}

	@Override
	public List<BsAreaCost> findByAreaCode(String areaCode) {
		
		return bsAreaCostDao.findByAreaCode(areaCode);
	}
	
}

