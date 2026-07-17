package com.spt.bas.report.client.remote;

import com.spt.bas.report.client.constant.ReportConstant;
import com.spt.bas.report.client.vo.RptIndexStatisticsReqVo;
import com.spt.bas.report.client.vo.RptIndexStatisticsVo;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = ReportConstant.SERVER_NAME,path= ReportConstant.SERVER_NAME+"/home/page/statistics",url=ReportConstant.SERVER_URL,configuration= FeignConfig.class)
public interface IRptIndexStatisticsClient {

    /**
     * 首页-风控统计
     * @param performanceStatus
     * @return
     */
    @PostMapping("findIndexRiskStatistics")
    public List<RptIndexStatisticsVo> findIndexRiskStatistics(@RequestBody RptIndexStatisticsReqVo vo);

    /**
     * 首页-财务统计
     * @return
     */
    @PostMapping("findIndexFinanceStatistics")
    public List<RptIndexStatisticsVo> findIndexFinanceStatistics(@RequestBody RptIndexStatisticsReqVo vo);

    /**
     * 首页-到期应收      未来7天应收未收的销售货款 已出库，且还未付款
     * @return
     */
    @PostMapping("findSellUnPayFinance")
    public List<RptIndexStatisticsVo> findSellUnPayFinance();

    /**
     * 首页-未收款统计
     * @return
     */
    @PostMapping("findUnPayStatistics")
    public List<RptIndexStatisticsVo> findUnPayStatistics();
    
    /**
     * 首页-过去10周业务金额统计
     * @return
     */
    @PostMapping("findSalesTenWeekData")
    public List<RptIndexStatisticsVo> findSalesTenWeekData(@RequestBody RptIndexStatisticsReqVo vo);
   
    /**
     * 过去10周业务金额统计(赊销、代采、自营)合计
     * @return
     */
    @PostMapping("findTotalSalesWeekData")
    public List<RptIndexStatisticsVo> findTotalSalesWeekData(@RequestBody RptIndexStatisticsReqVo vo);

    /**
     * 首页-过去10月业务金额统计
     * @return
     */
    @PostMapping("findSalesTenMonthData")
    public List<RptIndexStatisticsVo> findSalesTenMonthData(@RequestBody RptIndexStatisticsReqVo vo);
    
    /**
     * 过去10月业务金额统计(赊销、代采、自营)合计
     * @return
     */
    @PostMapping("findTotalSalesMonthData")
    public List<RptIndexStatisticsVo> findTotalSalesMonthData(@RequestBody RptIndexStatisticsReqVo vo);
    
}
