package com.spt.bas.report.server.api;

import com.spt.bas.report.client.entity.RptFactBisBusinessAnalysisVo;
import com.spt.bas.report.client.entity.RptFactBisHumanResourceCostsAnalysisVo;
import com.spt.bas.report.client.entity.RptFactBisModifiedBusinessVo;
import com.spt.bas.report.client.vo.RptFactBisBusinessAnalysisSearchVo;
import com.spt.bas.report.client.vo.RptFactBisHumanResourceCostsAnalysisSearchVo;
import com.spt.bas.report.server.service.IRptFactBisReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/fact/bis/report")
public class RptFactBisReportApi {
	@Autowired
	private IRptFactBisReportService factBisReportService;
	
	@PostMapping("findModifiedBusiness")
	public RptFactBisModifiedBusinessVo findModifiedBusiness(@RequestBody RptFactBisBusinessAnalysisSearchVo vo){
		return factBisReportService.findModifiedBusiness(vo);
	}
	
	@PostMapping("findBusinessAnalysis")
	public RptFactBisBusinessAnalysisVo findBusinessAnalysis(@RequestBody RptFactBisBusinessAnalysisSearchVo vo){
		return factBisReportService.findBusinessAnalysis(vo);
	}
	
	@PostMapping("findHumanResourceCostsAnalysis")
	public RptFactBisHumanResourceCostsAnalysisVo findHumanResourceCostsAnalysis(@RequestBody RptFactBisHumanResourceCostsAnalysisSearchVo vo){
		return factBisReportService.findHumanResourceCostsAnalysis(vo);
	}
}
