package com.spt.bas.report.client.remote;

import com.spt.bas.report.client.constant.ReportConstant;
import com.spt.bas.report.client.entity.RptPersonCostChart;
import com.spt.bas.report.client.vo.RptPersonCostChartSearchVo;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = ReportConstant.SERVER_NAME,path= ReportConstant.SERVER_NAME+"/person/cost/chart",url=ReportConstant.SERVER_URL,configuration=FeignConfig.class)
public interface IRptPersonCostChartClient {

	@PostMapping("personCostChartDataList")
	public List<RptPersonCostChart> personCostChartDataList(@RequestBody RptPersonCostChartSearchVo searchVo);
}
