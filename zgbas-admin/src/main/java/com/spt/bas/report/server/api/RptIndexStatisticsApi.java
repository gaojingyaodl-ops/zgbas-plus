package com.spt.bas.report.server.api;

import com.spt.bas.report.client.vo.RptIndexStatisticsReqVo;
import com.spt.bas.report.client.vo.RptIndexStatisticsVo;
import com.spt.bas.report.server.service.IRptIndexStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/home/page/statistics")
public class RptIndexStatisticsApi {
    @Autowired
    private IRptIndexStatisticsService indexStatisticsService;

    /**
     * 首页-风控统计
     * @param performanceStatus
     * @return
     */
    @PostMapping("findIndexRiskStatistics")
    public List<RptIndexStatisticsVo> findIndexRiskStatistics(@RequestBody RptIndexStatisticsReqVo vo) {
        return indexStatisticsService.findIndexRiskStatistics(vo);
    }

    /**
     * 首页-财务统计
     * @return
     */
    @PostMapping("findIndexFinanceStatistics")
    public List<RptIndexStatisticsVo> findIndexFinanceStatistics(@RequestBody RptIndexStatisticsReqVo vo) {
        return indexStatisticsService.findIndexFinanceStatistics(vo);
    }
    
    /**
     * 首页-到期应收      未来7天应收未收的销售货款 已出库，且还未付款
     * @return
     */
    @PostMapping("findSellUnPayFinance")
    public List<RptIndexStatisticsVo> findSellUnPayFinance(){
        return indexStatisticsService.findSellUnPayFinance();
    }

    /**
     * 首页-到期应收      未来7天应收未收的销售货款 已出库，且还未付款
     * @return
     */
    @PostMapping("findUnPayStatistics")
    public List<RptIndexStatisticsVo> findUnPayStatistics(){
        return indexStatisticsService.findUnPayStatistics();
    }
    /**
     * 首页-过去10周业务金额统计
     * @return
     */
    @PostMapping("findSalesTenWeekData")
    public List<RptIndexStatisticsVo> findSalesTenWeekData(@RequestBody RptIndexStatisticsReqVo vo){
        return indexStatisticsService.findSalesTenWeekData(vo);
    }
    /**
     * 过去10周业务金额统计(赊销、代采、自营)合计
     * @return
     */
    @PostMapping("findTotalSalesWeekData")
    public List<RptIndexStatisticsVo> findTotalSalesWeekData(@RequestBody RptIndexStatisticsReqVo vo){
        return indexStatisticsService.findTotalSalesWeekData(vo);
    }
    /**
     * 首页-过去10月业务金额统计
     * @return
     */
    @PostMapping("findSalesTenMonthData")
    public List<RptIndexStatisticsVo> findSalesTenMonthData(@RequestBody RptIndexStatisticsReqVo vo){
        return indexStatisticsService.findSalesTenMonthData(vo);
    }
    /**
     * 过去10月业务金额统计(赊销、代采、自营)合计
     * @return
     */
    @PostMapping("findTotalSalesMonthData")
    public List<RptIndexStatisticsVo> findTotalSalesMonthData(@RequestBody RptIndexStatisticsReqVo vo){
        return indexStatisticsService.findTotalSalesMonthData(vo);
    }

    
    
}
