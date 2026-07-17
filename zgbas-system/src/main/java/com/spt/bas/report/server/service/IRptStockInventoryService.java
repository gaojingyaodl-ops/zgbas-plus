package com.spt.bas.report.server.service;

import com.spt.bas.report.client.entity.*;
import com.spt.bas.report.client.vo.RptStockBookVo;
import org.springframework.data.domain.Page;

import com.spt.bas.report.client.vo.RptWarehouseOutSearchVo;
public interface IRptStockInventoryService {
	
	
	/**
	 * 库存统计报表
	 */
	public Page<RptStockInventoryReport> findPageStockInventory(RptStockInventoryReport vo);
	
	/**
	 * 合计
	 */
	RptStockInventoryReport findTotalStockInventory(RptStockInventoryReport vo);
	/**
	 * 实际出库明细报表
	 */
	public Page<RptDeliveryOutReport> findDeliveryOut(RptDeliveryOutReport vo);
	
	/**
	 * 实际出库明细合计
	 */
	RptDeliveryOutReport findTotalDeliveryOut(RptDeliveryOutReport vo);
	/**
	 * 库存明细表
	 */
	public Page<RptStockDetailReport> findStockDetailPage(RptStockDetailReport vo);
	
	/**
	 * 库存明细合计
	 */
	RptStockDetailReport findStockDetailTotal(RptStockDetailReport vo);
	
	/**
	 * 库存查询
	 */
	public Page<RptStockReport> findStockPage(RptStockReport vo);
	
	/**
	 * 库存查询合计
	 */
	RptStockReport findStockPageTotal(RptStockReport vo);

	/**
	 * 可出库明细查询
	 */
	Page<RptWarehouseOutEntity> findWarehouseOut(RptWarehouseOutSearchVo vo);
	
	/**
	 * 实际库存明细表
	 */
	public Page<RptStockDetailReport> findRealStockDetailPage(RptStockDetailReport vo);
	
	/**
	 * 实际库存明细合计
	 */
	RptStockDetailReport findRealStockDetailTotal(RptStockDetailReport vo);

	Page<RptStockBook> findPageStockBook(RptStockBookVo vo);
}
