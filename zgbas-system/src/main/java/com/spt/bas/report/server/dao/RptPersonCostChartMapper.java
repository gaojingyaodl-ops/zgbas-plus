package com.spt.bas.report.server.dao;

import com.spt.bas.report.client.entity.RptPersonCostChart;
import com.spt.bas.report.client.vo.RptPersonCostChartSearchVo;
import com.spt.tools.mybatis.annotation.MyBatisDao;

@MyBatisDao
public interface RptPersonCostChartMapper {

    RptPersonCostChart getPersonCostChartData(RptPersonCostChartSearchVo searchVo);

}
