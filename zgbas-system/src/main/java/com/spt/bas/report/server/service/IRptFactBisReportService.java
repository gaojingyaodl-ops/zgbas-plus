package com.spt.bas.report.server.service;

import com.spt.bas.report.client.entity.RptFactBisBusinessAnalysisVo;
import com.spt.bas.report.client.entity.RptFactBisHumanResourceCostsAnalysisVo;
import com.spt.bas.report.client.entity.RptFactBisModifiedBusinessVo;
import com.spt.bas.report.client.vo.RptFactBisBusinessAnalysisSearchVo;
import com.spt.bas.report.client.vo.RptFactBisHumanResourceCostsAnalysisSearchVo;

public interface IRptFactBisReportService {
	
	RptFactBisModifiedBusinessVo findModifiedBusiness(RptFactBisBusinessAnalysisSearchVo vo);
	
	RptFactBisBusinessAnalysisVo findBusinessAnalysis(RptFactBisBusinessAnalysisSearchVo vo);
	
	RptFactBisHumanResourceCostsAnalysisVo findHumanResourceCostsAnalysis(RptFactBisHumanResourceCostsAnalysisSearchVo vo);
}
