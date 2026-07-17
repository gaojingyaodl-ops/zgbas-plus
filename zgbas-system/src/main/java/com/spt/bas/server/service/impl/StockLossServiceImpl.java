package com.spt.bas.server.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.spt.bas.client.entity.StockLoss;
import com.spt.bas.server.dao.StockLossDao;
import com.spt.bas.server.service.IStockLossService;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;

@Component
@Transactional(readOnly = true)
public class StockLossServiceImpl extends BaseService<StockLoss> implements IStockLossService {
	@Autowired
	private StockLossDao stockLossDao;
	
	@Override
	public BaseDao<StockLoss> getBaseDao() {
		return stockLossDao;
	}
	
	@Override
	public Class<StockLoss> getEntityClazz() {
		return StockLoss.class;
	}
	
}

