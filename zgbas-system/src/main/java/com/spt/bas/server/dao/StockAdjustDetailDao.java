package com.spt.bas.server.dao;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;

import com.spt.bas.client.entity.StockAdjustDetail;
import com.spt.tools.jpa.dao.BaseDao;

public interface StockAdjustDetailDao extends BaseDao<StockAdjustDetail> {
	
	@Transactional
	@Modifying
	void deleteByStockAdjustId(Long stockAdjustId);
	
	List<StockAdjustDetail> findByStockAdjustId(Long stockAdjustId);
}

