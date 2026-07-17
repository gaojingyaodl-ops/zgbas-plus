package com.spt.bas.report.server.dao;

import java.util.List;

import com.spt.bas.report.client.entity.RptStockContractReportVo;
import com.spt.bas.report.client.vo.RptStockContractSearchReportVo;
import com.spt.tools.mybatis.annotation.MyBatisDao;

@MyBatisDao
public interface RptStockContractReportMapper {

	public List<RptStockContractReportVo> findStockContractPage(RptStockContractSearchReportVo searchVo);
	
	public List<RptStockContractReportVo>  findPage(RptStockContractSearchReportVo searchReportVo);
}
