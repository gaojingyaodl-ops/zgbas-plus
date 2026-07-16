package com.spt.bas.server.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.spt.bas.client.entity.StockContract;
import com.spt.tools.jpa.dao.BaseDao;

public interface StockContractDao extends BaseDao<StockContract> {

	@Modifying
	public void deleteByBuyProductId(Long buyProductId);

	public StockContract findByBuyProductId(Long buyProductId);

	public List<StockContract> findByBuyContractId(Long buyContractId);
	
	@Query("from StockContract where deliveryInNumber > deliveryOutNumber and DateDiff(NOW(),updatedDate) > ?1")
	public List<StockContract> findWarehouseSchedule(Integer warehouseDays);
}
