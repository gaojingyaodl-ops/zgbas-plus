package com.spt.bas.report.client.remote;

import com.spt.bas.report.client.constant.ReportConstant;
import com.spt.bas.report.client.entity.RptFactBisBusinessAnalysisVo;
import com.spt.bas.report.client.entity.RptFactBisHumanResourceCostsAnalysisVo;
import com.spt.bas.report.client.entity.RptFactBisModifiedBusinessVo;
import com.spt.bas.report.client.vo.RptFactBisBusinessAnalysisSearchVo;
import com.spt.bas.report.client.vo.RptFactBisHumanResourceCostsAnalysisSearchVo;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = ReportConstant.SERVER_NAME,path= ReportConstant.SERVER_NAME+"/fact/bis/report",url=ReportConstant.SERVER_URL,configuration=FeignConfig.class)
public interface IRptFactBisReportClient {
	
	@PostMapping("findModifiedBusiness")
	RptFactBisModifiedBusinessVo findModifiedBusiness(@RequestBody RptFactBisBusinessAnalysisSearchVo vo);
	
	@PostMapping("findBusinessAnalysis")
	RptFactBisBusinessAnalysisVo findBusinessAnalysis(@RequestBody RptFactBisBusinessAnalysisSearchVo vo);
	
	@PostMapping("findHumanResourceCostsAnalysis")
    RptFactBisHumanResourceCostsAnalysisVo findHumanResourceCostsAnalysis(@RequestBody RptFactBisHumanResourceCostsAnalysisSearchVo vo);
		
}
