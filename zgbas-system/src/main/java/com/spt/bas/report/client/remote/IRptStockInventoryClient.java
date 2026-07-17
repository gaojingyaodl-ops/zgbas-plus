package com.spt.bas.report.client.remote;
import com.spt.bas.report.client.entity.*;
import com.spt.bas.report.client.vo.RptStockBookVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.spt.bas.report.client.constant.ReportConstant;
import com.spt.bas.report.client.vo.RptWarehouseOutSearchVo;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.http.feign.FeignConfig;
@FeignClient(name = ReportConstant.SERVER_NAME,path= ReportConstant.SERVER_NAME+"/rpt/stock",url=ReportConstant.SERVER_URL,configuration=FeignConfig.class)

public interface IRptStockInventoryClient {
	
	@PostMapping("findPageStockInventory")
	public PageDown<RptStockInventoryReport> findPageStockInventory(@RequestBody RptStockInventoryReport vo);
	
	@PostMapping("findTotalStockInventory")
	RptStockInventoryReport findTotalStockInventory(@RequestBody RptStockInventoryReport vo);
	
	@PostMapping("findDeliveryOut")
	public PageDown<RptDeliveryOutReport> findDeliveryOut(@RequestBody RptDeliveryOutReport vo);
	
	@PostMapping("findTotalDeliveryOut")
	RptDeliveryOutReport findTotalDeliveryOut(@RequestBody RptDeliveryOutReport vo);
	
	@PostMapping("findStockDetailPage")
	public PageDown<RptStockDetailReport> findStockDetailPage(@RequestBody RptStockDetailReport vo);
	
	@PostMapping("findStockDetailTotal")
	public RptStockDetailReport findStockDetailTotal(@RequestBody RptStockDetailReport vo);
	
	@PostMapping("findStockPage")
	public PageDown<RptStockReport> findStockPage(@RequestBody RptStockReport vo);

	@PostMapping("findWarehouseOut")
	public PageDown<RptWarehouseOutEntity> findWarehouseOut(@RequestBody RptWarehouseOutSearchVo vo);
	
	@PostMapping("findStockPageTotal")
	public RptStockReport findStockPageTotal(@RequestBody RptStockReport vo);
	
	@PostMapping("findRealStockDetailPage")
	public PageDown<RptStockDetailReport> findRealStockDetailPage(@RequestBody RptStockDetailReport vo);
	
	@PostMapping("findRealStockDetailTotal")
	public RptStockDetailReport findRealStockDetailTotal(@RequestBody RptStockDetailReport vo);

	@PostMapping("findPageStockBook")
	public PageDown<RptStockBook> findPageStockBook(@RequestBody RptStockBookVo vo);
}
