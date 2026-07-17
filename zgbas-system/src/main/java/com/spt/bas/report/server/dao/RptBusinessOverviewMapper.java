package com.spt.bas.report.server.dao;

import com.spt.bas.report.client.entity.RptBusinessOverview;
import com.spt.bas.report.client.vo.RptBusinessOverviewSearchVo;
import com.spt.tools.mybatis.annotation.MyBatisDao;

import java.util.List;

@MyBatisDao
public interface RptBusinessOverviewMapper {

	List<RptBusinessOverview> findBusinessOverviewList(RptBusinessOverviewSearchVo vo);
	
}
