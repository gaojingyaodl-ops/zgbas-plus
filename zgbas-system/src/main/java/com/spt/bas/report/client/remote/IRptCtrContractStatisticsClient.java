package com.spt.bas.report.client.remote;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.spt.bas.report.client.constant.ReportConstant;
import com.spt.bas.report.client.entity.RptCtrContractStatistics;
import com.spt.bas.report.client.vo.RptStatisticsVo;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.http.feign.FeignConfig;

@FeignClient(name = ReportConstant.SERVER_NAME,path= ReportConstant.SERVER_NAME+"/ctr/statistics",url=ReportConstant.SERVER_URL,configuration=FeignConfig.class)
public interface IRptCtrContractStatisticsClient {
	
	@PostMapping("findBuyCtrContract")
	public PageDown<RptCtrContractStatistics> findBuyCtrContract(@RequestBody RptStatisticsVo vo);
	
	@PostMapping("getContractStatistics")
	public RptCtrContractStatistics getContractStatistics(@RequestBody RptStatisticsVo vo);
	
	@PostMapping("findSaleCtrContract")
	public PageDown<RptCtrContractStatistics> findSaleCtrContract(@RequestBody PageSearchVo searchVo);
	
	@PostMapping("showStatistics")
	public PageDown<RptCtrContractStatistics> showStatistics(@RequestBody RptStatisticsVo vo);
}
