package com.spt.bas.report.client.remote;

import com.spt.bas.report.client.constant.ReportConstant;
import com.spt.bas.report.client.entity.RptNotInvoiceBillStatistics;
import com.spt.bas.report.client.vo.RptNotInvoiceBillStatisticsSearchVo;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = ReportConstant.SERVER_NAME,path= ReportConstant.SERVER_NAME+"/rpt/notInvoiceBillStatistics",url=ReportConstant.SERVER_URL,configuration=FeignConfig.class)
public interface IRptNotInvoiceBillStatisticsClient extends BaseClient<RptNotInvoiceBillStatistics> {
	
	@PostMapping("findRptNotInvoiceBillStatisticsPage")
	PageDown<RptNotInvoiceBillStatistics> findRptNotInvoiceBillStatisticsPage(@RequestBody RptNotInvoiceBillStatisticsSearchVo searchVo);
	
	/**
	 * 未开票明细合计查询
	 * @param searchVo
	 * @return
	 */
	@PostMapping("findRptNotInvoiceBillStatisticsSum")
	RptNotInvoiceBillStatistics findRptNotInvoiceBillStatisticsSum(@RequestBody RptNotInvoiceBillStatisticsSearchVo searchVo);
}
