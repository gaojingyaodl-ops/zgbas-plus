package com.spt.bas.report.server.service;

import com.spt.bas.report.client.entity.RptInvoiceBillStatistics;
import com.spt.bas.report.client.vo.RptInvoiceBillStatisticsVo;

import java.util.List;

public interface IRptInvoiceBillStatisticsService {
    List<RptInvoiceBillStatistics> findInvoiceBillStatistics(RptInvoiceBillStatisticsVo searchVo);
}
