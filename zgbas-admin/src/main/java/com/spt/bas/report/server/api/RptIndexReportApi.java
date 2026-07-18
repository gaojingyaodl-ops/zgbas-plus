package com.spt.bas.report.server.api;

import com.spt.bas.report.client.vo.*;
import com.spt.bas.report.server.service.IRptIndexReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 首页Api
 *
 * @author 杨英承
 * @version 1.0.0
 * @date 2022/6/16 16:04
 */

@RestController
@RequestMapping(value = "/indexReport")
public class RptIndexReportApi {

    @Autowired
    IRptIndexReportService indexReportService;


    /**
     * 待办统计
     *
     * @param query 查询参数
     * @return 结果
     */
    @PostMapping("/backlogStatistics")
    public RptToDoStatisticsVo backlogStatistics(@RequestBody RptIndexReportQuery query){
        return indexReportService.backlogStatistics(query);
    }

    /**
     * 查询待办统计
     *
     * @param query 查询参数
     * @return 待办统计数量
     */
    @PostMapping("/approvalCount")
    public Integer getApprovalCount(@RequestBody RptIndexReportQuery query){
        return indexReportService.getApprovalCount(query);
    }

    /**
     * 业务统计
     *
     * @param query 查询参数
     * @return 结果
     */
    @PostMapping("/businessStatistics")
    public List<RptIndexCommonVo> businessStatistics(@RequestBody RptIndexReportQuery query){
        return indexReportService.businessStatistics(query);
    }
    /**
     * 业务统计
     *
     * @param query 查询参数
     * @return 结果
     */
    @PostMapping("/businessStatisticsByMonth")
    public List<RptIndexCommonVo> businessStatisticsByMonth(@RequestBody RptIndexReportQuery query){
        return indexReportService.businessStatisticsByMonth(query);
    }

    /**
     * 业绩提成
     *
     * @param query 查询参数
     * @return 业绩提成统计
     */
    @PostMapping("/getPerformanceCommission")
    public RptPerformanceVo getPerformanceCommission(@RequestBody RptIndexReportQuery query){
        return indexReportService.getPerformanceCommission(query);
    }


    /**
     * 查询业绩排行
     * @param query 查询参数
     * @return 业绩排行
     */
    @PostMapping("/performanceRanking")
    public List<RptIndexCommonVo> performanceRanking(@RequestBody RptIndexReportQuery query){
        return indexReportService.performanceRanking(query);
    }


    /**
     * 过去10个月的毛利率
     *
     * @return 毛利率
     */
    @PostMapping("/grossProfitMargin")
    public List<RptGrossProfitMarginVo> grossProfitMargin(@RequestBody RptGrossProfitMarginSearchVo searchVo){
        return indexReportService.grossProfitMargin(searchVo);
    }

}
