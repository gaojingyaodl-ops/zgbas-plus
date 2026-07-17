package com.spt.bas.report.server.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.spt.bas.report.client.entity.RptStockContractReportVo;
import com.spt.bas.report.client.vo.RptStockContractSearchReportVo;
import com.spt.bas.report.server.dao.RptStockContractReportMapper;
import com.spt.bas.report.server.service.IRptStokcContractReportService;
@Component
public class RptStockContractReportServiceImpl implements IRptStokcContractReportService {
	
	@Autowired
	private RptStockContractReportMapper stockContractReportMapper;
	
	@Override
	public Page<RptStockContractReportVo> findStockContractPage(RptStockContractSearchReportVo vo) {
		List<RptStockContractReportVo> list = stockContractReportMapper.findStockContractPage(vo);
		
		Pageable pageable = PageRequest.of(vo.getPage() - 1, vo.getRows());
		Page<RptStockContractReportVo> page = new PageImpl<>(list, pageable, vo.getCount());
		return page;
	}

	@Override
	public Page<RptStockContractReportVo> findPage(RptStockContractSearchReportVo vo) {
		List<RptStockContractReportVo> list = stockContractReportMapper.findPage(vo);
		
		Pageable pageable = PageRequest.of(vo.getPage() - 1, vo.getRows());
		Page<RptStockContractReportVo> page = new PageImpl<>(list, pageable, vo.getCount());
		return page;
	}
}
