package com.spt.bas.server.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.spt.bas.client.entity.StockMoveDetail;
import com.spt.bas.server.dao.StockMoveDetailDao;
import com.spt.bas.server.service.IStockMoveDetailService;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;

@Component
@Transactional(readOnly = true)
public class StockMoveDetailServiceImpl extends BaseService<StockMoveDetail> implements IStockMoveDetailService {
	@Autowired
	private StockMoveDetailDao stockMoveDetailDao;
	
	@Override
	public BaseDao<StockMoveDetail> getBaseDao() {
		return stockMoveDetailDao;
	}
	
	@Override
	public Class<StockMoveDetail> getEntityClazz() {
		return StockMoveDetail.class;
	}
	
}

