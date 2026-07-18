package com.spt.bas.report.server.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spt.bas.report.client.entity.RptCtrDailyBuyReport;
import com.spt.bas.report.client.entity.RptCtrDailySalesReport;
import com.spt.bas.report.client.entity.RptCtrDailyStockReport;
import com.spt.bas.report.client.entity.RptCtrMatchUserProfitReport;
import com.spt.bas.report.server.service.IRptCtrDailySalesReportService;

@RestController
@RequestMapping(value = "/rpt/dailySales")
public class RptCtrDailySalesReportApi {
	@Autowired
	private IRptCtrDailySalesReportService ctrDailySalesReportService;
	
	@PostMapping("findDailySales")
	public Page<RptCtrDailySalesReport> findDailySales(@RequestBody RptCtrDailySalesReport vo){
		Page<RptCtrDailySalesReport> findDailySales = ctrDailySalesReportService.findDailySales(vo);
		return findDailySales;
	}
	@PostMapping("findDailySalesTotal")
	public RptCtrDailySalesReport findDailySalesTotal(@RequestBody RptCtrDailySalesReport vo){
		return ctrDailySalesReportService.findDailySalesTotal(vo);
	}
	
	@PostMapping("findDailyBuy")
	public Page<RptCtrDailyBuyReport> findDailyBuy(@RequestBody RptCtrDailyBuyReport vo){
		Page<RptCtrDailyBuyReport> findDailyBuy = ctrDailySalesReportService.findDailyBuy(vo);
		return findDailyBuy;
	}
	
	@PostMapping("findDailyBuyTotal")
	public RptCtrDailyBuyReport findDailyBuyTotal(@RequestBody RptCtrDailyBuyReport vo){
		return ctrDailySalesReportService.findDailyBuyTotal(vo);
	}
	
	@PostMapping("findMatchUserProfit")
	public Page<RptCtrMatchUserProfitReport> findMatchUserProfit(@RequestBody RptCtrMatchUserProfitReport vo){
		Page<RptCtrMatchUserProfitReport> findMatchUserProfit = ctrDailySalesReportService.findMatchUserProfit(vo);
		return findMatchUserProfit;
	}
	
	@PostMapping("findProfitTotal")
	public RptCtrMatchUserProfitReport findProfitTotal(@RequestBody RptCtrMatchUserProfitReport vo){
		return ctrDailySalesReportService.findProfitTotal(vo);
	}
	
	@PostMapping("findDailyStock")
	public Page<RptCtrDailyStockReport> findDailyStock(@RequestBody RptCtrDailyStockReport vo){
		Page<RptCtrDailyStockReport> findDailyStock = ctrDailySalesReportService.findDailyStock(vo);
		return findDailyStock;
	}
	
	@PostMapping("findDailyStockTotal")
	public RptCtrDailyStockReport findDailyStockTotal(@RequestBody RptCtrDailyStockReport vo){
		return ctrDailySalesReportService.findDailyStockTotal(vo);
	}
}
