package com.spt.bas.report.client.remote;

import com.spt.bas.report.client.constant.ReportConstant;
import com.spt.bas.report.client.entity.RptNotBillStatistics;
import com.spt.bas.report.client.vo.RptNotBillStatisticsSearchVo;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = ReportConstant.SERVER_NAME,path= ReportConstant.SERVER_NAME+"/rpt/notBillStatistics",url=ReportConstant.SERVER_URL,configuration=FeignConfig.class)
public interface IRptNotBillStatisticsClient extends BaseClient<RptNotBillStatistics> {
	
	@PostMapping("findRptNotBillStatisticsPage")
	PageDown<RptNotBillStatistics> findRptNotBillStatisticsPage(@RequestBody RptNotBillStatisticsSearchVo searchVo);
	
	/**
	 * 未收票明细合计查询
	 * @param searchVo
	 * @return
	 */
	@PostMapping("findRptNotBillStatisticsSum")
	RptNotBillStatistics findRptNotBillStatisticsSum(@RequestBody RptNotBillStatisticsSearchVo searchVo);
}
