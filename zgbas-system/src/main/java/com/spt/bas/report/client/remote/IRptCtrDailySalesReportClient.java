package com.spt.bas.report.client.remote;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.spt.bas.report.client.constant.ReportConstant;
import com.spt.bas.report.client.entity.RptCtrDailyBuyReport;
import com.spt.bas.report.client.entity.RptCtrDailySalesReport;
import com.spt.bas.report.client.entity.RptCtrDailyStockReport;
import com.spt.bas.report.client.entity.RptCtrMatchUserProfitReport;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.http.feign.FeignConfig;

@FeignClient(name = ReportConstant.SERVER_NAME,path= ReportConstant.SERVER_NAME+"/rpt/dailySales",url=ReportConstant.SERVER_URL,configuration=FeignConfig.class)
public interface IRptCtrDailySalesReportClient {
	
	@PostMapping("findDailySales")
	public PageDown<RptCtrDailySalesReport> findDailySales(@RequestBody RptCtrDailySalesReport vo);
	
	@PostMapping("findDailySalesTotal")
	public RptCtrDailySalesReport findDailySalesTotal(@RequestBody RptCtrDailySalesReport vo);
	
	@PostMapping("findDailyBuy")
	public PageDown<RptCtrDailyBuyReport> findDailyBuy(@RequestBody RptCtrDailyBuyReport vo);
	
	@PostMapping("findDailyBuyTotal")
	public RptCtrDailyBuyReport findDailyBuyTotal(@RequestBody RptCtrDailyBuyReport vo);
	
	@PostMapping("findMatchUserProfit")
	public PageDown<RptCtrMatchUserProfitReport> findMatchUserProfit(@RequestBody RptCtrMatchUserProfitReport vo);
	
	@PostMapping("findProfitTotal")
	public RptCtrMatchUserProfitReport findProfitTotal(@RequestBody RptCtrMatchUserProfitReport vo);
	
	@PostMapping("findDailyStock")
	public PageDown<RptCtrDailyStockReport> findDailyStock(@RequestBody RptCtrDailyStockReport vo);
	
	@PostMapping("findDailyStockTotal")
	public RptCtrDailyStockReport findDailyStockTotal(@RequestBody RptCtrDailyStockReport vo);
}
