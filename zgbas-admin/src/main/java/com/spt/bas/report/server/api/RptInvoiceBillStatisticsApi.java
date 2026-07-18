package com.spt.bas.report.server.api;

import com.spt.bas.report.client.entity.RptInvoiceBillStatistics;
import com.spt.bas.report.client.vo.RptInvoiceBillStatisticsVo;
import com.spt.bas.report.server.service.IRptInvoiceBillStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/rpt/invoiceBillStatistics")
public class RptInvoiceBillStatisticsApi {
    @Autowired
    private IRptInvoiceBillStatisticsService rptInvoiceBillStatisticsService;

    @RequestMapping("/findInvoiceBillStatistics")
    List<RptInvoiceBillStatistics> findInvoiceBillStatistics(@RequestBody RptInvoiceBillStatisticsVo searchVo) {
        List<RptInvoiceBillStatistics> list = rptInvoiceBillStatisticsService.findInvoiceBillStatistics(searchVo);
        return list;
    }
}
