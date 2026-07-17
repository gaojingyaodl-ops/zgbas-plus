package com.spt.bas.report.server.dao;

import java.util.List;

import com.spt.bas.report.client.entity.RptBusinessAccountReport;
import com.spt.bas.report.client.vo.RptBusinessSearchVo;
import com.spt.tools.mybatis.annotation.MyBatisDao;

@MyBatisDao
public interface RptBusinessAccountMapper {

	List<RptBusinessAccountReport> findPage(RptBusinessSearchVo vo);
	
	RptBusinessAccountReport findPageSum(RptBusinessSearchVo vo);
}
