package com.spt.bas.report.server.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.spt.bas.report.client.entity.RptStockDetailPresellReport;
import com.spt.bas.report.client.vo.RptStockDetailPresellSearchVo;
import com.spt.bas.report.server.dao.RptStockDetailPresellMapper;
import com.spt.bas.report.server.service.IRptStockDetailPresellService;
@Component
public class RptStockDetailPresellServiceImpl implements IRptStockDetailPresellService {
	@Autowired
	private RptStockDetailPresellMapper stockDetailPresellMapper;

	@Override
	public Page<RptStockDetailPresellReport> findApplyPage(RptStockDetailPresellSearchVo searchVo) {
		List<RptStockDetailPresellReport> list = stockDetailPresellMapper.findApplyPage(searchVo);
		Pageable pageable = PageRequest.of(searchVo.getPage() - 1, searchVo.getRows());
		Page<RptStockDetailPresellReport> page = new PageImpl<>(list, pageable, searchVo.getCount());
		return page;
	}
	

}
