package com.spt.bas.report.server.service;

import org.springframework.data.domain.Page;

import com.spt.bas.report.client.entity.RptStockContractReportVo;
import com.spt.bas.report.client.vo.RptStockContractSearchReportVo;

public interface IRptStokcContractReportService {
	
	Page<RptStockContractReportVo> findStockContractPage(RptStockContractSearchReportVo vo);
	
	Page<RptStockContractReportVo> findPage(RptStockContractSearchReportVo vo);
	
}
