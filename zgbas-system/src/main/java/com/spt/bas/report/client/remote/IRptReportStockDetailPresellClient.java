package com.spt.bas.report.client.remote;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.spt.bas.report.client.constant.ReportConstant;
import com.spt.bas.report.client.entity.RptStockDetailPresellReport;
import com.spt.bas.report.client.vo.RptStockDetailPresellSearchVo;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.http.feign.FeignConfig;

@FeignClient(name = ReportConstant.SERVER_NAME,path= ReportConstant.SERVER_NAME+"/report/stock/detailPresell",url=ReportConstant.SERVER_URL,configuration=FeignConfig.class)
public interface IRptReportStockDetailPresellClient {
	
	@PostMapping("findApplyPage")
	public PageDown<RptStockDetailPresellReport> findApplyPage(@RequestBody RptStockDetailPresellSearchVo searchVo);
}
