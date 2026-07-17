package com.spt.bas.report.server.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.spt.bas.report.client.entity.RptApplyDeliveryReport;
import com.spt.bas.report.client.vo.RptApplyDeliverySearchVo;
import com.spt.bas.report.server.dao.RptApplyDeliveryMapper;
import com.spt.bas.report.server.service.IRptApplyDeliveryReportService;
@Component
public class RptApplyDeliveryReportServiceImpl implements IRptApplyDeliveryReportService {
	@Autowired
	private RptApplyDeliveryMapper applyDeliveryMapper;

	@Override
	public Page<RptApplyDeliveryReport> findApplyDeliveryPage(RptApplyDeliverySearchVo searchVo) {
		List<RptApplyDeliveryReport> list = applyDeliveryMapper.findApplyDeliveryPage(searchVo);
		Pageable pageable = PageRequest.of(searchVo.getPage() - 1, searchVo.getRows());
		Page<RptApplyDeliveryReport> page = new PageImpl<>(list, pageable, searchVo.getCount());
		return page;
	}
	

}
