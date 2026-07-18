package com.spt.bas.report.server.api;

import com.spt.bas.report.client.entity.RptNotInvoiceBillStatistics;
import com.spt.bas.report.client.vo.RptNotInvoiceBillStatisticsSearchVo;
import com.spt.bas.report.server.service.IRptNotInvoiceBillStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/rpt/notInvoiceBillStatistics")
public class RptNotInvoiceBillStatisticsApi {

    @Autowired
    private IRptNotInvoiceBillStatisticsService rptNotInvoiceBillStatisticsService;

    /**
     * 未开票明细分页查询
     * @param searchVo
     * @return
     */
    @PostMapping("findRptNotInvoiceBillStatisticsPage")
    public Page<RptNotInvoiceBillStatistics> findRptNotInvoiceBillStatisticsPage(@RequestBody RptNotInvoiceBillStatisticsSearchVo searchVo){
        Page<RptNotInvoiceBillStatistics> page = rptNotInvoiceBillStatisticsService.findRptNotInvoiceBillStatisticsPage(searchVo);
        return page;
    }
    
    /**
     * 未开票明细合计查询
     * @param searchVo
     * @return
     */
    @PostMapping("findRptNotInvoiceBillStatisticsSum")
    public RptNotInvoiceBillStatistics findRptNotInvoiceBillStatisticsSum(@RequestBody RptNotInvoiceBillStatisticsSearchVo searchVo){
        return rptNotInvoiceBillStatisticsService.findRptNotInvoiceBillStatisticsSum(searchVo);
    }


}
