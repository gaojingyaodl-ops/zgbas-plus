package com.spt.bas.server.dao;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.spt.bas.client.entity.StockDetailRela;
import com.spt.tools.jpa.dao.BaseDao;

public interface StockDetailRelaDao extends BaseDao<StockDetailRela> {

	StockDetailRela findByContractIdAndRelaTypeAndStockDetailId(Long contractId, String relaType, Long stockDetailId);

	@Modifying
	@Query("update StockDetailRela r set r.stockDetailId=?1 where r.stockDetailId=?2 ")
	void updateByStockDetailId(Long stockDetailIdNew, Long stockDetailIdOld);
}
