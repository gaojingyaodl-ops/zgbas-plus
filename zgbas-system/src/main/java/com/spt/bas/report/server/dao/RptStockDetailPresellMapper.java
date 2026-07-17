package com.spt.bas.report.server.dao;

import java.util.List;

import com.spt.bas.report.client.entity.RptStockDetailPresellReport;
import com.spt.bas.report.client.vo.RptStockDetailPresellSearchVo;
import com.spt.tools.mybatis.annotation.MyBatisDao;

@MyBatisDao
public interface RptStockDetailPresellMapper {
	
	List<RptStockDetailPresellReport> findApplyPage(RptStockDetailPresellSearchVo searchVo);
}
