package com.spt.bas.report.server.dao;

import java.util.List;

import com.spt.bas.client.entity.BsCompanyDcsx;
import com.spt.bas.client.entity.BsFunder;
import com.spt.bas.report.client.entity.*;
import com.spt.bas.report.client.vo.RptStockBookVo;
import com.spt.bas.report.client.vo.RptWarehouseOutSearchVo;
import com.spt.tools.mybatis.annotation.MyBatisDao;

@MyBatisDao
public interface RptStockInventoryMapper {

	/**
	 * 库存统计报表
	 */
	List<RptStockInventoryReport> findPageStockInventory(RptStockInventoryReport vo);

	/**
	 * 库存统计合计
	 */
	RptStockInventoryReport findTotalStockInventory(RptStockInventoryReport vo);

	/**
	 * 实际出库明细报表
	 */
	List<RptDeliveryOutReport> findDeliveryOut(RptDeliveryOutReport vo);

	/**
	 * 实际出库明细合计
	 */
	RptDeliveryOutReport findTotalDeliveryOut(RptDeliveryOutReport vo);

	/**
	 * 库存明细表
	 */
	List<RptStockDetailReport> findStockDetailPage(RptStockDetailReport vo);

	/**
	 * 库存明细合计
	 */
	RptStockDetailReport findStockDetailTotal(RptStockDetailReport vo);

	/**
	 * 库存查询
	 */
	List<RptStockReport> findStockPage(RptStockReport vo);

	/**
	 * 库存查询合计
	 */
	RptStockReport findStockPageTotal(RptStockReport vo);

	/**
	 * 可出库明细查询
	 */
	List<RptWarehouseOutEntity> findWarehouseOut(RptWarehouseOutSearchVo vo);
	
	/**
	 * 实际库存明细表
	 */
	List<RptStockDetailReport> findRealStockDetailPage(RptStockDetailReport vo);

	/**
	 * 实际库存明细合计
	 */
	RptStockDetailReport findRealStockDetailTotal(RptStockDetailReport vo);

	List<RptStockBook> findPageStockBook(RptStockBookVo vo);

	List<BsCompanyDcsx> findDcsxCompanyList();

	List<BsFunder> finAllBsFunder();
}
