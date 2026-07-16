package com.spt.bas.server.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import com.spt.bas.client.entity.StockMoveDetail;
import com.spt.tools.jpa.dao.BaseDao;

public interface StockMoveDetailDao extends BaseDao<StockMoveDetail> {
	
	@Query("from StockMoveDetail t where originalDetailId = ?1")
	public List<StockMoveDetail> findByOriginalId(Long originalDetailId);
	
	@Query("from StockMoveDetail t where originalDetailId in ?1")
	public List<StockMoveDetail> findByOriginalIdList(List<Long> originalDetailIds);
}

