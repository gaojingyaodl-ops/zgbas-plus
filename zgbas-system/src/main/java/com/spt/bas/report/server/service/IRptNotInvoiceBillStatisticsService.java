package com.spt.bas.report.server.service;

import com.spt.bas.report.client.entity.RptNotInvoiceBillStatistics;
import com.spt.bas.report.client.vo.RptNotInvoiceBillStatisticsSearchVo;
import org.springframework.data.domain.Page;


public interface IRptNotInvoiceBillStatisticsService {
    /**
     * 未开票明细分页查询
     *
     */
    Page<RptNotInvoiceBillStatistics> findRptNotInvoiceBillStatisticsPage(RptNotInvoiceBillStatisticsSearchVo searchVo);
    
    RptNotInvoiceBillStatistics findRptNotInvoiceBillStatisticsSum(RptNotInvoiceBillStatisticsSearchVo searchVo);


}
