package com.spt.bas.report.server.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spt.bas.report.client.entity.RptStockDetailPresellReport;
import com.spt.bas.report.client.vo.RptStockDetailPresellSearchVo;
import com.spt.bas.report.server.service.IRptStockDetailPresellService;

@RestController
@RequestMapping(value = "/report/stock/detailPresell")
public class RptReportStockDetailPresellApi {
	@Autowired
	private IRptStockDetailPresellService stockDetailPresellService;
	
	@PostMapping("findApplyPage")
	public Page<RptStockDetailPresellReport> findApplyDeliveryPage(@RequestBody RptStockDetailPresellSearchVo searchVo){
		return stockDetailPresellService.findApplyPage(searchVo);
	}
}
