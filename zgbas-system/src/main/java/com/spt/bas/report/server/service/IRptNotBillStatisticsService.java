package com.spt.bas.report.server.service;

import com.spt.bas.report.client.entity.RptNotBillStatistics;
import com.spt.bas.report.client.vo.RptNotBillStatisticsSearchVo;
import org.springframework.data.domain.Page;


public interface IRptNotBillStatisticsService {
    /**
     * 未收票明细分页查询
     *
     */
    Page<RptNotBillStatistics> findRptNotBillStatisticsPage(RptNotBillStatisticsSearchVo searchVo);
    
    RptNotBillStatistics findRptNotBillStatisticsSum(RptNotBillStatisticsSearchVo searchVo);


}
