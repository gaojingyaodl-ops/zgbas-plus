package com.spt.bas.report.server.service;

import com.spt.bas.report.client.entity.RptBusinessOverview;
import com.spt.bas.report.client.vo.RptBusinessOverviewSearchVo;

import java.util.List;

public interface IRptBusinessOverviewService {

    List<RptBusinessOverview> findBusinessOverviewList(RptBusinessOverviewSearchVo searchVo);
	
}
