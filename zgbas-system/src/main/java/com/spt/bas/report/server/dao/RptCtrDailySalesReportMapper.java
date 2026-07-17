package com.spt.bas.report.server.dao;

import java.util.List;

import com.spt.bas.report.client.entity.RptCtrDailyBuyReport;
import com.spt.bas.report.client.entity.RptCtrDailySalesReport;
import com.spt.bas.report.client.entity.RptCtrDailyStockReport;
import com.spt.bas.report.client.entity.RptCtrMatchUserProfitReport;
import com.spt.tools.mybatis.annotation.MyBatisDao;

@MyBatisDao
public interface RptCtrDailySalesReportMapper {
	
	//日销售明细表
	List<RptCtrDailySalesReport> findDailySales(RptCtrDailySalesReport vo);
	
	//日销售合计
	RptCtrDailySalesReport findDailySalesTotal(RptCtrDailySalesReport vo);
	
	//日采购明细表
	List<RptCtrDailyBuyReport> findDailyBuy(RptCtrDailyBuyReport vo);
	
	//日采购明细合计
	RptCtrDailyBuyReport findDailyBuyTotal(RptCtrDailyBuyReport vo);
	
	//业务员毛利明细月报表
	List<RptCtrMatchUserProfitReport> findMatchUserProfit(RptCtrMatchUserProfitReport vo);
	
	//业务员毛利明细月报表合计
	RptCtrMatchUserProfitReport findProfitTotal(RptCtrMatchUserProfitReport vo);
	
	//采购合同库存日明细表
	List<RptCtrDailyStockReport> findDailyStock(RptCtrDailyStockReport vo);
	
	//采购合同库存日明细统计
	RptCtrDailyStockReport findDailyStockTotal(RptCtrDailyStockReport vo);
}
