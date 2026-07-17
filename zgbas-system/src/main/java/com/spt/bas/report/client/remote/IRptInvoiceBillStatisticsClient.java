package com.spt.bas.report.client.remote;

import com.spt.bas.report.client.constant.ReportConstant;
import com.spt.bas.report.client.entity.RptInvoiceBillStatistics;
import com.spt.bas.report.client.vo.RptInvoiceBillStatisticsVo;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@FeignClient(name = ReportConstant.SERVER_NAME, path = ReportConstant.SERVER_NAME + "/rpt/invoiceBillStatistics", url = ReportConstant.SERVER_URL, configuration = FeignConfig.class)
public interface IRptInvoiceBillStatisticsClient extends BaseClient<RptInvoiceBillStatistics> {
    @RequestMapping("/findInvoiceBillStatistics")
    List<RptInvoiceBillStatistics> findInvoiceBillStatistics(@RequestBody RptInvoiceBillStatisticsVo searchVo);
}
