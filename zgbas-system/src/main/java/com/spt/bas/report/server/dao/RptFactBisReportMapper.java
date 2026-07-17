package com.spt.bas.report.server.dao;

import com.spt.bas.report.client.entity.*;
import com.spt.bas.report.client.vo.RptFactBisBusinessAnalysisSearchVo;
import com.spt.bas.report.client.vo.RptFactBisHumanResourceCostsAnalysisSearchVo;
import com.spt.tools.mybatis.annotation.MyBatisDao;

@MyBatisDao
public interface RptFactBisReportMapper {
	
	RptFactBisModifiedBusinessVo findModifiedBusiness(RptFactBisBusinessAnalysisSearchVo vo);
	
	RptFactBisModifiedBusinessVo findModifiedBusinessCost(RptFactBisBusinessAnalysisSearchVo vo);
	
	RptFactBisModifiedBusinessVo findModifiedBusinessPay(RptFactBisBusinessAnalysisSearchVo vo);

	RptFactBisBusinessAnalysisResultVo findBusinessAnalysis(RptFactBisBusinessAnalysisSearchVo vo);

	RptFactBisBusinessWorkTargetResultVo findWorkTarget(RptFactBisBusinessAnalysisSearchVo vo);
	
	RptFactBisHumanResourceCostsAnalysisVo findHumanResourceCostsAnalysis(RptFactBisHumanResourceCostsAnalysisSearchVo vo);

}
