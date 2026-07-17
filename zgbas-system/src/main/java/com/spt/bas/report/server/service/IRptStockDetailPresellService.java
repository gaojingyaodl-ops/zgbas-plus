package com.spt.bas.report.server.service;

import org.springframework.data.domain.Page;

import com.spt.bas.report.client.entity.RptStockDetailPresellReport;
import com.spt.bas.report.client.vo.RptStockDetailPresellSearchVo;

public interface IRptStockDetailPresellService {
	
	Page<RptStockDetailPresellReport> findApplyPage(RptStockDetailPresellSearchVo searchVo);
}
