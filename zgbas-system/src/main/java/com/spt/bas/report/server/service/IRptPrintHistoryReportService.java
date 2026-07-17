package com.spt.bas.report.server.service;

import com.spt.bas.report.client.entity.RptPrintHistoryReport;

/**
 * @author 杨英承
 * @version 1.0.0
 * @date 2022/6/16 10:52
 */

public interface IRptPrintHistoryReportService {
    /**
     * 添加审批打印记录
     * @param printHistoryReport 打印信息
     * @return 打印次数
     */
    Integer addPrintHistory(RptPrintHistoryReport printHistoryReport);
}
