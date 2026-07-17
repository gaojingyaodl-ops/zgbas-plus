package com.spt.bas.report.server.service;

import com.spt.bas.report.client.vo.RptIndexStatisticsReqVo;
import com.spt.bas.report.client.vo.RptIndexStatisticsVo;

import java.util.List;

public interface IRptIndexStatisticsService {
    
    /**
     * 首页风控统计
     * @param performanceStatus
     * @return
     */
    List<RptIndexStatisticsVo> findIndexRiskStatistics(RptIndexStatisticsReqVo vo);

    /**
     * 首页-财务统计
     * @return
     */
    List<RptIndexStatisticsVo> findIndexFinanceStatistics(RptIndexStatisticsReqVo vo);
    
    /**
     * 首页-到期应收
     * @return
     */
    List<RptIndexStatisticsVo> findSellUnPayFinance();
    
    /**
     * 首页-未收款统计
     * @return
     */
    List<RptIndexStatisticsVo> findUnPayStatistics();
    
    /**
     * 过去10周业务金额统计
     * @return
     */
    List<RptIndexStatisticsVo> findSalesTenWeekData(RptIndexStatisticsReqVo vo);

    /**
     * 过去10周业务金额统计(赊销、代采、自营)合计
     * @param vo
     * @return
     */
    List<RptIndexStatisticsVo> findTotalSalesWeekData(RptIndexStatisticsReqVo vo);
    
    /**
     * 过去10月业务金额统计
     * @return
     */
    List<RptIndexStatisticsVo> findSalesTenMonthData(RptIndexStatisticsReqVo vo);

    /**
     * 过去10月业务金额统计(赊销、代采、自营)合计
     * @param vo
     * @return
     */
    List<RptIndexStatisticsVo> findTotalSalesMonthData(RptIndexStatisticsReqVo vo);
    
}
