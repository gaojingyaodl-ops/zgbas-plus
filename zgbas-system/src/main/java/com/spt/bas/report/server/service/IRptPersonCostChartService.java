package com.spt.bas.report.server.service;

import com.spt.bas.report.client.entity.RptPersonCostChart;
import com.spt.bas.report.client.vo.RptPersonCostChartSearchVo;

import java.util.List;

public interface IRptPersonCostChartService {

    List<RptPersonCostChart> personCostChartDataList(RptPersonCostChartSearchVo searchVo);
}
