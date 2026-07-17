package com.spt.bas.report.client.remote;

import com.spt.bas.report.client.constant.ReportConstant;
import com.spt.bas.report.client.vo.*;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author 杨英承
 * @version 1.0.0
 * @date 2022/6/17 14:24
 */
@FeignClient(name = ReportConstant.SERVER_NAME,path= ReportConstant.SERVER_NAME+"/indexReport",url=ReportConstant.SERVER_URL,configuration= FeignConfig.class)
public interface IRptIndexReportClient {

    /**
     * 待办统计
     *
     * @param query 查询参数
     * @return 结果
     */
    @PostMapping("/backlogStatistics")
    RptToDoStatisticsVo backlogStatistics(@RequestBody RptIndexReportQuery query);

    /**
     * 查询待办统计
     *
     * @param query 查询参数
     * @return 待办统计数量
     */
    @PostMapping("/approvalCount")
    Integer getApprovalCount(@RequestBody RptIndexReportQuery query);

    /**
     * 业务统计
     *
     * @param query 查询参数
     * @return 结果
     */
    @PostMapping("/businessStatistics")
    List<RptIndexCommonVo> businessStatistics(@RequestBody RptIndexReportQuery query);

    /**
     * 业务统计 历史月份
     *
     * @param query 查询参数
     * @return 结果
     */
    @PostMapping("/businessStatisticsByMonth")
    List<RptIndexCommonVo> businessStatisticsByMonth(@RequestBody RptIndexReportQuery query);

    /**
     * 业绩提成
     *
     * @param query 查询参数
     * @return 业绩提成统计
     */
    @PostMapping("/getPerformanceCommission")
    RptPerformanceVo getPerformanceCommission(@RequestBody RptIndexReportQuery query);

    /**
     * 查询业绩排行
     * @param query 查询参数
     * @return 业绩排行
     */
    @PostMapping("/performanceRanking")
    List<RptIndexCommonVo> performanceRanking(@RequestBody RptIndexReportQuery query);

    /**
     * 过去10个月的毛利率
     *
     * @return 毛利率
     */
    @PostMapping("/grossProfitMargin")
    public List<RptGrossProfitMarginVo> grossProfitMargin(@RequestBody RptGrossProfitMarginSearchVo searchVo);
}
