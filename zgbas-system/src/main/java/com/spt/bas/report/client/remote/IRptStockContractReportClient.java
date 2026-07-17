package com.spt.bas.report.client.remote;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.spt.bas.report.client.constant.ReportConstant;
import com.spt.bas.report.client.entity.RptStockContractReportVo;
import com.spt.bas.report.client.vo.RptStockContractSearchReportVo;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.http.feign.FeignConfig;

@FeignClient(name = ReportConstant.SERVER_NAME,path= ReportConstant.SERVER_NAME+"/rpt/stockContract",url=ReportConstant.SERVER_URL,configuration=FeignConfig.class)
public interface IRptStockContractReportClient {
	
	@PostMapping("findStockContractPage")
	public PageDown<RptStockContractReportVo> findStockContractPage(@RequestBody RptStockContractSearchReportVo vo);
	
	@PostMapping("findPage")
	public PageDown<RptStockContractReportVo> findPage(@RequestBody RptStockContractSearchReportVo vo);
}
