package com.spt.bas.report.server.service;

import org.springframework.data.domain.Page;

import com.spt.bas.report.client.entity.RptCtrDailyBuyReport;
import com.spt.bas.report.client.entity.RptCtrDailySalesReport;
import com.spt.bas.report.client.entity.RptCtrDailyStockReport;
import com.spt.bas.report.client.entity.RptCtrMatchUserProfitReport;

public interface IRptCtrDailySalesReportService {
	
	public Page<RptCtrDailySalesReport> findDailySales(RptCtrDailySalesReport vo);
	
	public RptCtrDailySalesReport findDailySalesTotal(RptCtrDailySalesReport vo);
	
	public Page<RptCtrDailyBuyReport> findDailyBuy(RptCtrDailyBuyReport vo);
	
	public RptCtrDailyBuyReport findDailyBuyTotal(RptCtrDailyBuyReport vo);
	
	public Page<RptCtrMatchUserProfitReport> findMatchUserProfit(RptCtrMatchUserProfitReport vo);
	
	public RptCtrMatchUserProfitReport findProfitTotal(RptCtrMatchUserProfitReport vo);
	
	public Page<RptCtrDailyStockReport> findDailyStock(RptCtrDailyStockReport vo);
		
	public RptCtrDailyStockReport findDailyStockTotal(RptCtrDailyStockReport vo);
}
