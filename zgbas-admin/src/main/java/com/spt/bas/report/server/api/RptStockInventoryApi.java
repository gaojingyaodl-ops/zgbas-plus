package com.spt.bas.report.server.api;

import com.spt.bas.report.client.entity.*;
import com.spt.bas.report.client.vo.RptStockBookVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spt.bas.report.client.vo.RptWarehouseOutSearchVo;
import com.spt.bas.report.server.service.IRptStockInventoryService;

@RestController
@RequestMapping(value = "/rpt/stock")
public class RptStockInventoryApi {
	@Autowired
	private IRptStockInventoryService stockInventoryService;
	
	@PostMapping("findPageStockInventory")
	public Page<RptStockInventoryReport> findPageStockInventory(@RequestBody RptStockInventoryReport vo){
		Page<RptStockInventoryReport> findPageStockInventory = stockInventoryService.findPageStockInventory(vo);
		return findPageStockInventory;
		
	}
	
	@PostMapping("findTotalStockInventory")
	RptStockInventoryReport findTotalStockInventory(@RequestBody RptStockInventoryReport vo){
		RptStockInventoryReport total = stockInventoryService.findTotalStockInventory(vo);
		return total;
	}
	
	@PostMapping("findDeliveryOut")
	public Page<RptDeliveryOutReport> findDeliveryOut(@RequestBody RptDeliveryOutReport vo){
		Page<RptDeliveryOutReport> findDeliveryOut = stockInventoryService.findDeliveryOut(vo);
		return findDeliveryOut;
	}
	
	@PostMapping("findTotalDeliveryOut")
	RptDeliveryOutReport findTotalDeliveryOut(@RequestBody RptDeliveryOutReport vo){
		RptDeliveryOutReport total = stockInventoryService.findTotalDeliveryOut(vo);
		return total;
	}
	
	@PostMapping("findStockDetailPage")
	public Page<RptStockDetailReport> findStockDetailPage(@RequestBody RptStockDetailReport vo){
		Page<RptStockDetailReport> findStockDetailPage = stockInventoryService.findStockDetailPage(vo);
		return findStockDetailPage;
	}
	
	@PostMapping("findStockDetailTotal")
	public RptStockDetailReport findStockDetailTotal(@RequestBody RptStockDetailReport vo){
		RptStockDetailReport total = stockInventoryService.findStockDetailTotal(vo);
		return total;
	}
	
	@PostMapping("findStockPage")
	public Page<RptStockReport> findStockPage(@RequestBody RptStockReport vo){
		Page<RptStockReport> findStockPage = stockInventoryService.findStockPage(vo);
		return findStockPage;
	}
	
	@PostMapping("findWarehouseOut")
	public Page<RptWarehouseOutEntity> findWarehouseOut(@RequestBody RptWarehouseOutSearchVo vo) {
		Page<RptWarehouseOutEntity> findStockPage = stockInventoryService.findWarehouseOut(vo);
		return findStockPage;
	}
	
	@PostMapping("findStockPageTotal")
	public RptStockReport findStockPageTotal(@RequestBody RptStockReport vo){
		RptStockReport total= stockInventoryService.findStockPageTotal(vo);
		return total;
	}
	
	@PostMapping("findRealStockDetailPage")
	public Page<RptStockDetailReport> findRealStockDetailPage(@RequestBody RptStockDetailReport vo){
		Page<RptStockDetailReport> page = stockInventoryService.findRealStockDetailPage(vo);
		return page;
	}
	
	@PostMapping("findRealStockDetailTotal")
	public RptStockDetailReport findRealStockDetailTotal(@RequestBody RptStockDetailReport vo) {
		RptStockDetailReport total = stockInventoryService.findRealStockDetailTotal(vo);
		return total;
	}

	@PostMapping("findPageStockBook")
	public Page<RptStockBook> findPageStockBook(@RequestBody RptStockBookVo vo){
		Page<RptStockBook> findPageStockBook = stockInventoryService.findPageStockBook(vo);
		return findPageStockBook;

	}
}
