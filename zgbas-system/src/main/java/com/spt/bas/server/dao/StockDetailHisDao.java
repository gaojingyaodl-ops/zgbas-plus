package com.spt.bas.server.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import com.spt.bas.client.entity.StockDetailHis;
import com.spt.tools.jpa.dao.BaseDao;

public interface StockDetailHisDao extends BaseDao<StockDetailHis> {
	@Query("from StockDetailHis h where h.stockDetailId =?1 and h.applyId =?2 and h.operationType=?3 ")
	public List<StockDetailHis> findDetailHis(Long detailId, Long applyId, String operationType);
}

