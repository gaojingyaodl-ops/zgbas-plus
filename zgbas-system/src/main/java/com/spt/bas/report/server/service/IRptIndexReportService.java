package com.spt.bas.report.server.service;

import com.spt.bas.report.client.vo.*;

import java.util.List;

/**
 * @author 杨英承
 * @version 1.0.0
 * @date 2022/6/16 16:11
 */

public interface IRptIndexReportService {
    /**
     * 待办统计
     */
    default RptToDoStatisticsVo backlogStatistics(RptIndexReportQuery query) {
        return null;
    }

    /**
     * 业务统计
     *
     * @param query 查询参数
     * @return 结果
     */
    List<RptIndexCommonVo> businessStatistics(RptIndexReportQuery query);

    /**
     * 业务统计查询历史月份数据
     * @param query
     * @return
     */
    List<RptIndexCommonVo> businessStatisticsByMonth(RptIndexReportQuery query);

    /**
     * 业绩提成
     *
     * @return 业绩提成统计
     */
    RptPerformanceVo getPerformanceCommission(RptIndexReportQuery query);

    /**
     * 查询业绩排行
     * @param query 查询参数
     * @return 业绩排行
     */
    List<RptIndexCommonVo> performanceRanking(RptIndexReportQuery query);

    /**
     * 过去10个月的毛利率
     *
     * @return 毛利率
     */
    List<RptGrossProfitMarginVo> grossProfitMargin(RptGrossProfitMarginSearchVo searchVo);

    /**
     * 查询待办统计数量
     *
     * @param query 查询参数
     * @return 待办统计数量
     */
    Integer getApprovalCount(RptIndexReportQuery query);
}
