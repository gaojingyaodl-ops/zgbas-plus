package com.spt.bas.report.server.service.impl;

import com.spt.bas.report.client.entity.RptPrintHistoryReport;
import com.spt.bas.report.server.dao.RptPrintHistoryMapper;
import com.spt.bas.report.server.service.IRptPrintHistoryReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author 杨英承
 * @version 1.0.0
 * @date 2022/6/16 10:53
 */

@Service
public class RptPrintHistoryReportServiceImpl implements IRptPrintHistoryReportService {

    @Autowired
    private RptPrintHistoryMapper printHistoryMapper;
    /**
     * 添加审批打印记录
     *
     * @param printHistoryReport 打印信息
     * @return 打印次数
     */
    @Override
    public Integer addPrintHistory(RptPrintHistoryReport printHistoryReport) {
        // 每点击一次插入一次
        printHistoryMapper.addPrintHistory(printHistoryReport);
        return printHistoryMapper.selectPrintCountByApproveNo(printHistoryReport.getApproveNo());
    }
}
